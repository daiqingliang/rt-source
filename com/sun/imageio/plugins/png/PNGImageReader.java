package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class PNGImageReader extends ImageReader {
  static final int IHDR_TYPE = 1229472850;
  
  static final int PLTE_TYPE = 1347179589;
  
  static final int IDAT_TYPE = 1229209940;
  
  static final int IEND_TYPE = 1229278788;
  
  static final int bKGD_TYPE = 1649100612;
  
  static final int cHRM_TYPE = 1665684045;
  
  static final int gAMA_TYPE = 1732332865;
  
  static final int hIST_TYPE = 1749635924;
  
  static final int iCCP_TYPE = 1766015824;
  
  static final int iTXt_TYPE = 1767135348;
  
  static final int pHYs_TYPE = 1883789683;
  
  static final int sBIT_TYPE = 1933723988;
  
  static final int sPLT_TYPE = 1934642260;
  
  static final int sRGB_TYPE = 1934772034;
  
  static final int tEXt_TYPE = 1950701684;
  
  static final int tIME_TYPE = 1950960965;
  
  static final int tRNS_TYPE = 1951551059;
  
  static final int zTXt_TYPE = 2052348020;
  
  static final int PNG_COLOR_GRAY = 0;
  
  static final int PNG_COLOR_RGB = 2;
  
  static final int PNG_COLOR_PALETTE = 3;
  
  static final int PNG_COLOR_GRAY_ALPHA = 4;
  
  static final int PNG_COLOR_RGB_ALPHA = 6;
  
  static final int[] inputBandsForColorType = { 1, -1, 3, 1, 2, -1, 4 };
  
  static final int PNG_FILTER_NONE = 0;
  
  static final int PNG_FILTER_SUB = 1;
  
  static final int PNG_FILTER_UP = 2;
  
  static final int PNG_FILTER_AVERAGE = 3;
  
  static final int PNG_FILTER_PAETH = 4;
  
  static final int[] adam7XOffset = { 0, 4, 0, 2, 0, 1, 0 };
  
  static final int[] adam7YOffset = { 0, 0, 4, 0, 2, 0, 1 };
  
  static final int[] adam7XSubsampling = { 8, 8, 4, 4, 2, 2, 1, 1 };
  
  static final int[] adam7YSubsampling = { 8, 8, 8, 4, 4, 2, 2, 1 };
  
  private static final boolean debug = true;
  
  ImageInputStream stream = null;
  
  boolean gotHeader = false;
  
  boolean gotMetadata = false;
  
  ImageReadParam lastParam = null;
  
  long imageStartPosition = -1L;
  
  Rectangle sourceRegion = null;
  
  int sourceXSubsampling = -1;
  
  int sourceYSubsampling = -1;
  
  int sourceMinProgressivePass = 0;
  
  int sourceMaxProgressivePass = 6;
  
  int[] sourceBands = null;
  
  int[] destinationBands = null;
  
  Point destinationOffset = new Point(0, 0);
  
  PNGMetadata metadata = new PNGMetadata();
  
  DataInputStream pixelStream = null;
  
  BufferedImage theImage = null;
  
  int pixelsDone = 0;
  
  int totalPixels;
  
  private static final int[][] bandOffsets = { null, { 0 }, { 0, 1 }, { 0, 1, 2 }, { 0, 1, 2, 3 } };
  
  public PNGImageReader(ImageReaderSpi paramImageReaderSpi) { super(paramImageReaderSpi); }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    this.stream = (ImageInputStream)paramObject;
    resetStreamSettings();
  }
  
  private String readNullTerminatedString(String paramString, int paramInt) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte b = 0;
    int i;
    while (paramInt > b++ && (i = this.stream.read()) != 0) {
      if (i == -1)
        throw new EOFException(); 
      byteArrayOutputStream.write(i);
    } 
    return new String(byteArrayOutputStream.toByteArray(), paramString);
  }
  
  private void readHeader() throws IIOException {
    if (this.gotHeader)
      return; 
    if (this.stream == null)
      throw new IllegalStateException("Input source not set!"); 
    try {
      byte[] arrayOfByte = new byte[8];
      this.stream.readFully(arrayOfByte);
      if (arrayOfByte[0] != -119 || arrayOfByte[1] != 80 || arrayOfByte[2] != 78 || arrayOfByte[3] != 71 || arrayOfByte[4] != 13 || arrayOfByte[5] != 10 || arrayOfByte[6] != 26 || arrayOfByte[7] != 10)
        throw new IIOException("Bad PNG signature!"); 
      int i = this.stream.readInt();
      if (i != 13)
        throw new IIOException("Bad length for IHDR chunk!"); 
      int j = this.stream.readInt();
      if (j != 1229472850)
        throw new IIOException("Bad type for IHDR chunk!"); 
      this.metadata = new PNGMetadata();
      int k = this.stream.readInt();
      int m = this.stream.readInt();
      this.stream.readFully(arrayOfByte, 0, 5);
      byte b1 = arrayOfByte[0] & 0xFF;
      byte b2 = arrayOfByte[1] & 0xFF;
      byte b3 = arrayOfByte[2] & 0xFF;
      byte b4 = arrayOfByte[3] & 0xFF;
      byte b5 = arrayOfByte[4] & 0xFF;
      this.stream.skipBytes(4);
      this.stream.flushBefore(this.stream.getStreamPosition());
      if (k == 0)
        throw new IIOException("Image width == 0!"); 
      if (m == 0)
        throw new IIOException("Image height == 0!"); 
      if (b1 != 1 && b1 != 2 && b1 != 4 && b1 != 8 && b1 != 16)
        throw new IIOException("Bit depth must be 1, 2, 4, 8, or 16!"); 
      if (b2 != 0 && b2 != 2 && b2 != 3 && b2 != 4 && b2 != 6)
        throw new IIOException("Color type must be 0, 2, 3, 4, or 6!"); 
      if (b2 == 3 && b1 == 16)
        throw new IIOException("Bad color type/bit depth combination!"); 
      if ((b2 == 2 || b2 == 6 || b2 == 4) && b1 != 8 && b1 != 16)
        throw new IIOException("Bad color type/bit depth combination!"); 
      if (b3 != 0)
        throw new IIOException("Unknown compression method (not 0)!"); 
      if (b4 != 0)
        throw new IIOException("Unknown filter method (not 0)!"); 
      if (b5 != 0 && b5 != 1)
        throw new IIOException("Unknown interlace method (not 0 or 1)!"); 
      this.metadata.IHDR_present = true;
      this.metadata.IHDR_width = k;
      this.metadata.IHDR_height = m;
      this.metadata.IHDR_bitDepth = b1;
      this.metadata.IHDR_colorType = b2;
      this.metadata.IHDR_compressionMethod = b3;
      this.metadata.IHDR_filterMethod = b4;
      this.metadata.IHDR_interlaceMethod = b5;
      this.gotHeader = true;
    } catch (IOException iOException) {
      throw new IIOException("I/O error reading PNG header!", iOException);
    } 
  }
  
  private void parse_PLTE_chunk(int paramInt) throws IOException {
    byte b1;
    if (this.metadata.PLTE_present) {
      processWarningOccurred("A PNG image may not contain more than one PLTE chunk.\nThe chunk wil be ignored.");
      return;
    } 
    if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
      processWarningOccurred("A PNG gray or gray alpha image cannot have a PLTE chunk.\nThe chunk wil be ignored.");
      return;
    } 
    byte[] arrayOfByte = new byte[paramInt];
    this.stream.readFully(arrayOfByte);
    int i = paramInt / 3;
    if (this.metadata.IHDR_colorType == 3) {
      b1 = 1 << this.metadata.IHDR_bitDepth;
      if (i > b1) {
        processWarningOccurred("PLTE chunk contains too many entries for bit depth, ignoring extras.");
        i = b1;
      } 
      i = Math.min(i, b1);
    } 
    if (i > 16) {
      b1 = 256;
    } else if (i > 4) {
      b1 = 16;
    } else if (i > 2) {
      b1 = 4;
    } else {
      b1 = 2;
    } 
    this.metadata.PLTE_present = true;
    this.metadata.PLTE_red = new byte[b1];
    this.metadata.PLTE_green = new byte[b1];
    this.metadata.PLTE_blue = new byte[b1];
    byte b2 = 0;
    for (byte b3 = 0; b3 < i; b3++) {
      this.metadata.PLTE_red[b3] = arrayOfByte[b2++];
      this.metadata.PLTE_green[b3] = arrayOfByte[b2++];
      this.metadata.PLTE_blue[b3] = arrayOfByte[b2++];
    } 
  }
  
  private void parse_bKGD_chunk() throws IIOException {
    if (this.metadata.IHDR_colorType == 3) {
      this.metadata.bKGD_colorType = 3;
      this.metadata.bKGD_index = this.stream.readUnsignedByte();
    } else if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
      this.metadata.bKGD_colorType = 0;
      this.metadata.bKGD_gray = this.stream.readUnsignedShort();
    } else {
      this.metadata.bKGD_colorType = 2;
      this.metadata.bKGD_red = this.stream.readUnsignedShort();
      this.metadata.bKGD_green = this.stream.readUnsignedShort();
      this.metadata.bKGD_blue = this.stream.readUnsignedShort();
    } 
    this.metadata.bKGD_present = true;
  }
  
  private void parse_cHRM_chunk() throws IIOException {
    this.metadata.cHRM_whitePointX = this.stream.readInt();
    this.metadata.cHRM_whitePointY = this.stream.readInt();
    this.metadata.cHRM_redX = this.stream.readInt();
    this.metadata.cHRM_redY = this.stream.readInt();
    this.metadata.cHRM_greenX = this.stream.readInt();
    this.metadata.cHRM_greenY = this.stream.readInt();
    this.metadata.cHRM_blueX = this.stream.readInt();
    this.metadata.cHRM_blueY = this.stream.readInt();
    this.metadata.cHRM_present = true;
  }
  
  private void parse_gAMA_chunk() throws IIOException {
    int i = this.stream.readInt();
    this.metadata.gAMA_gamma = i;
    this.metadata.gAMA_present = true;
  }
  
  private void parse_hIST_chunk(int paramInt) throws IOException {
    if (!this.metadata.PLTE_present)
      throw new IIOException("hIST chunk without prior PLTE chunk!"); 
    this.metadata.hIST_histogram = new char[paramInt / 2];
    this.stream.readFully(this.metadata.hIST_histogram, 0, this.metadata.hIST_histogram.length);
    this.metadata.hIST_present = true;
  }
  
  private void parse_iCCP_chunk(int paramInt) throws IOException {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.iCCP_profileName = str;
    this.metadata.iCCP_compressionMethod = this.stream.readUnsignedByte();
    byte[] arrayOfByte = new byte[paramInt - str.length() - 2];
    this.stream.readFully(arrayOfByte);
    this.metadata.iCCP_compressedProfile = arrayOfByte;
    this.metadata.iCCP_present = true;
  }
  
  private void parse_iTXt_chunk(int paramInt) throws IOException {
    String str4;
    long l1 = this.stream.getStreamPosition();
    String str1 = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.iTXt_keyword.add(str1);
    int i = this.stream.readUnsignedByte();
    this.metadata.iTXt_compressionFlag.add(Boolean.valueOf((i == 1)));
    int j = this.stream.readUnsignedByte();
    this.metadata.iTXt_compressionMethod.add(Integer.valueOf(j));
    String str2 = readNullTerminatedString("UTF8", 80);
    this.metadata.iTXt_languageTag.add(str2);
    long l2 = this.stream.getStreamPosition();
    int k = (int)(l1 + paramInt - l2);
    String str3 = readNullTerminatedString("UTF8", k);
    this.metadata.iTXt_translatedKeyword.add(str3);
    l2 = this.stream.getStreamPosition();
    byte[] arrayOfByte = new byte[(int)(l1 + paramInt - l2)];
    this.stream.readFully(arrayOfByte);
    if (i == 1) {
      str4 = new String(inflate(arrayOfByte), "UTF8");
    } else {
      str4 = new String(arrayOfByte, "UTF8");
    } 
    this.metadata.iTXt_text.add(str4);
  }
  
  private void parse_pHYs_chunk() throws IIOException {
    this.metadata.pHYs_pixelsPerUnitXAxis = this.stream.readInt();
    this.metadata.pHYs_pixelsPerUnitYAxis = this.stream.readInt();
    this.metadata.pHYs_unitSpecifier = this.stream.readUnsignedByte();
    this.metadata.pHYs_present = true;
  }
  
  private void parse_sBIT_chunk() throws IIOException {
    int i = this.metadata.IHDR_colorType;
    if (i == 0 || i == 4) {
      this.metadata.sBIT_grayBits = this.stream.readUnsignedByte();
    } else if (i == 2 || i == 3 || i == 6) {
      this.metadata.sBIT_redBits = this.stream.readUnsignedByte();
      this.metadata.sBIT_greenBits = this.stream.readUnsignedByte();
      this.metadata.sBIT_blueBits = this.stream.readUnsignedByte();
    } 
    if (i == 4 || i == 6)
      this.metadata.sBIT_alphaBits = this.stream.readUnsignedByte(); 
    this.metadata.sBIT_colorType = i;
    this.metadata.sBIT_present = true;
  }
  
  private void parse_sPLT_chunk(int paramInt) throws IOException {
    this.metadata.sPLT_paletteName = readNullTerminatedString("ISO-8859-1", 80);
    paramInt -= this.metadata.sPLT_paletteName.length() + 1;
    int i = this.stream.readUnsignedByte();
    this.metadata.sPLT_sampleDepth = i;
    int j = paramInt / (4 * i / 8 + 2);
    this.metadata.sPLT_red = new int[j];
    this.metadata.sPLT_green = new int[j];
    this.metadata.sPLT_blue = new int[j];
    this.metadata.sPLT_alpha = new int[j];
    this.metadata.sPLT_frequency = new int[j];
    if (i == 8) {
      for (byte b = 0; b < j; b++) {
        this.metadata.sPLT_red[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_green[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_blue[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_alpha[b] = this.stream.readUnsignedByte();
        this.metadata.sPLT_frequency[b] = this.stream.readUnsignedShort();
      } 
    } else if (i == 16) {
      for (byte b = 0; b < j; b++) {
        this.metadata.sPLT_red[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_green[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_blue[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_alpha[b] = this.stream.readUnsignedShort();
        this.metadata.sPLT_frequency[b] = this.stream.readUnsignedShort();
      } 
    } else {
      throw new IIOException("sPLT sample depth not 8 or 16!");
    } 
    this.metadata.sPLT_present = true;
  }
  
  private void parse_sRGB_chunk() throws IIOException {
    this.metadata.sRGB_renderingIntent = this.stream.readUnsignedByte();
    this.metadata.sRGB_present = true;
  }
  
  private void parse_tEXt_chunk(int paramInt) throws IOException {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.tEXt_keyword.add(str);
    byte[] arrayOfByte = new byte[paramInt - str.length() - 1];
    this.stream.readFully(arrayOfByte);
    this.metadata.tEXt_text.add(new String(arrayOfByte, "ISO-8859-1"));
  }
  
  private void parse_tIME_chunk() throws IIOException {
    this.metadata.tIME_year = this.stream.readUnsignedShort();
    this.metadata.tIME_month = this.stream.readUnsignedByte();
    this.metadata.tIME_day = this.stream.readUnsignedByte();
    this.metadata.tIME_hour = this.stream.readUnsignedByte();
    this.metadata.tIME_minute = this.stream.readUnsignedByte();
    this.metadata.tIME_second = this.stream.readUnsignedByte();
    this.metadata.tIME_present = true;
  }
  
  private void parse_tRNS_chunk(int paramInt) throws IOException {
    int i = this.metadata.IHDR_colorType;
    if (i == 3) {
      if (!this.metadata.PLTE_present) {
        processWarningOccurred("tRNS chunk without prior PLTE chunk, ignoring it.");
        return;
      } 
      int j = this.metadata.PLTE_red.length;
      int k = paramInt;
      if (k > j) {
        processWarningOccurred("tRNS chunk has more entries than prior PLTE chunk, ignoring extras.");
        k = j;
      } 
      this.metadata.tRNS_alpha = new byte[k];
      this.metadata.tRNS_colorType = 3;
      this.stream.read(this.metadata.tRNS_alpha, 0, k);
      this.stream.skipBytes(paramInt - k);
    } else if (i == 0) {
      if (paramInt != 2) {
        processWarningOccurred("tRNS chunk for gray image must have length 2, ignoring chunk.");
        this.stream.skipBytes(paramInt);
        return;
      } 
      this.metadata.tRNS_gray = this.stream.readUnsignedShort();
      this.metadata.tRNS_colorType = 0;
    } else if (i == 2) {
      if (paramInt != 6) {
        processWarningOccurred("tRNS chunk for RGB image must have length 6, ignoring chunk.");
        this.stream.skipBytes(paramInt);
        return;
      } 
      this.metadata.tRNS_red = this.stream.readUnsignedShort();
      this.metadata.tRNS_green = this.stream.readUnsignedShort();
      this.metadata.tRNS_blue = this.stream.readUnsignedShort();
      this.metadata.tRNS_colorType = 2;
    } else {
      processWarningOccurred("Gray+Alpha and RGBS images may not have a tRNS chunk, ignoring it.");
      return;
    } 
    this.metadata.tRNS_present = true;
  }
  
  private static byte[] inflate(byte[] paramArrayOfByte) throws IOException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
    inflaterInputStream = new InflaterInputStream(byteArrayInputStream);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      int i;
      while ((i = inflaterInputStream.read()) != -1)
        byteArrayOutputStream.write(i); 
    } finally {
      inflaterInputStream.close();
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  private void parse_zTXt_chunk(int paramInt) throws IOException {
    String str = readNullTerminatedString("ISO-8859-1", 80);
    this.metadata.zTXt_keyword.add(str);
    int i = this.stream.readUnsignedByte();
    this.metadata.zTXt_compressionMethod.add(new Integer(i));
    byte[] arrayOfByte = new byte[paramInt - str.length() - 2];
    this.stream.readFully(arrayOfByte);
    this.metadata.zTXt_text.add(new String(inflate(arrayOfByte), "ISO-8859-1"));
  }
  
  private void readMetadata() throws IIOException { // Byte code:
    //   0: aload_0
    //   1: getfield gotMetadata : Z
    //   4: ifeq -> 8
    //   7: return
    //   8: aload_0
    //   9: invokespecial readHeader : ()V
    //   12: aload_0
    //   13: getfield metadata : Lcom/sun/imageio/plugins/png/PNGMetadata;
    //   16: getfield IHDR_colorType : I
    //   19: istore_1
    //   20: aload_0
    //   21: getfield ignoreMetadata : Z
    //   24: ifeq -> 123
    //   27: iload_1
    //   28: iconst_3
    //   29: if_icmpeq -> 123
    //   32: aload_0
    //   33: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   36: invokeinterface readInt : ()I
    //   41: istore_2
    //   42: aload_0
    //   43: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   46: invokeinterface readInt : ()I
    //   51: istore_3
    //   52: iload_3
    //   53: ldc 1229209940
    //   55: if_icmpne -> 86
    //   58: aload_0
    //   59: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   62: bipush #-8
    //   64: invokeinterface skipBytes : (I)I
    //   69: pop
    //   70: aload_0
    //   71: aload_0
    //   72: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   75: invokeinterface getStreamPosition : ()J
    //   80: putfield imageStartPosition : J
    //   83: goto -> 102
    //   86: aload_0
    //   87: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   90: iload_2
    //   91: iconst_4
    //   92: iadd
    //   93: invokeinterface skipBytes : (I)I
    //   98: pop
    //   99: goto -> 32
    //   102: goto -> 117
    //   105: astore_2
    //   106: new javax/imageio/IIOException
    //   109: dup
    //   110: ldc 'Error skipping PNG metadata'
    //   112: aload_2
    //   113: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
    //   116: athrow
    //   117: aload_0
    //   118: iconst_1
    //   119: putfield gotMetadata : Z
    //   122: return
    //   123: aload_0
    //   124: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   127: invokeinterface readInt : ()I
    //   132: istore_2
    //   133: aload_0
    //   134: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   137: invokeinterface readInt : ()I
    //   142: istore_3
    //   143: iload_2
    //   144: ifge -> 174
    //   147: new javax/imageio/IIOException
    //   150: dup
    //   151: new java/lang/StringBuilder
    //   154: dup
    //   155: invokespecial <init> : ()V
    //   158: ldc 'Invalid chunk lenght '
    //   160: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   163: iload_2
    //   164: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   167: invokevirtual toString : ()Ljava/lang/String;
    //   170: invokespecial <init> : (Ljava/lang/String;)V
    //   173: athrow
    //   174: aload_0
    //   175: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   178: invokeinterface mark : ()V
    //   183: aload_0
    //   184: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   187: aload_0
    //   188: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   191: invokeinterface getStreamPosition : ()J
    //   196: iload_2
    //   197: i2l
    //   198: ladd
    //   199: invokeinterface seek : (J)V
    //   204: aload_0
    //   205: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   208: invokeinterface readInt : ()I
    //   213: istore #4
    //   215: aload_0
    //   216: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   219: invokeinterface reset : ()V
    //   224: goto -> 256
    //   227: astore #5
    //   229: new javax/imageio/IIOException
    //   232: dup
    //   233: new java/lang/StringBuilder
    //   236: dup
    //   237: invokespecial <init> : ()V
    //   240: ldc 'Invalid chunk length '
    //   242: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   245: iload_2
    //   246: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   249: invokevirtual toString : ()Ljava/lang/String;
    //   252: invokespecial <init> : (Ljava/lang/String;)V
    //   255: athrow
    //   256: iload_3
    //   257: lookupswitch default -> 579, 1229209940 -> 396, 1347179589 -> 424, 1649100612 -> 432, 1665684045 -> 439, 1732332865 -> 446, 1749635924 -> 453, 1766015824 -> 461, 1767135348 -> 469, 1883789683 -> 498, 1933723988 -> 505, 1934642260 -> 512, 1934772034 -> 520, 1950701684 -> 527, 1950960965 -> 535, 1951551059 -> 542, 2052348020 -> 550
    //   396: aload_0
    //   397: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   400: bipush #-8
    //   402: invokeinterface skipBytes : (I)I
    //   407: pop
    //   408: aload_0
    //   409: aload_0
    //   410: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   413: invokeinterface getStreamPosition : ()J
    //   418: putfield imageStartPosition : J
    //   421: goto -> 766
    //   424: aload_0
    //   425: iload_2
    //   426: invokespecial parse_PLTE_chunk : (I)V
    //   429: goto -> 704
    //   432: aload_0
    //   433: invokespecial parse_bKGD_chunk : ()V
    //   436: goto -> 704
    //   439: aload_0
    //   440: invokespecial parse_cHRM_chunk : ()V
    //   443: goto -> 704
    //   446: aload_0
    //   447: invokespecial parse_gAMA_chunk : ()V
    //   450: goto -> 704
    //   453: aload_0
    //   454: iload_2
    //   455: invokespecial parse_hIST_chunk : (I)V
    //   458: goto -> 704
    //   461: aload_0
    //   462: iload_2
    //   463: invokespecial parse_iCCP_chunk : (I)V
    //   466: goto -> 704
    //   469: aload_0
    //   470: getfield ignoreMetadata : Z
    //   473: ifeq -> 490
    //   476: aload_0
    //   477: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   480: iload_2
    //   481: invokeinterface skipBytes : (I)I
    //   486: pop
    //   487: goto -> 704
    //   490: aload_0
    //   491: iload_2
    //   492: invokespecial parse_iTXt_chunk : (I)V
    //   495: goto -> 704
    //   498: aload_0
    //   499: invokespecial parse_pHYs_chunk : ()V
    //   502: goto -> 704
    //   505: aload_0
    //   506: invokespecial parse_sBIT_chunk : ()V
    //   509: goto -> 704
    //   512: aload_0
    //   513: iload_2
    //   514: invokespecial parse_sPLT_chunk : (I)V
    //   517: goto -> 704
    //   520: aload_0
    //   521: invokespecial parse_sRGB_chunk : ()V
    //   524: goto -> 704
    //   527: aload_0
    //   528: iload_2
    //   529: invokespecial parse_tEXt_chunk : (I)V
    //   532: goto -> 704
    //   535: aload_0
    //   536: invokespecial parse_tIME_chunk : ()V
    //   539: goto -> 704
    //   542: aload_0
    //   543: iload_2
    //   544: invokespecial parse_tRNS_chunk : (I)V
    //   547: goto -> 704
    //   550: aload_0
    //   551: getfield ignoreMetadata : Z
    //   554: ifeq -> 571
    //   557: aload_0
    //   558: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   561: iload_2
    //   562: invokeinterface skipBytes : (I)I
    //   567: pop
    //   568: goto -> 704
    //   571: aload_0
    //   572: iload_2
    //   573: invokespecial parse_zTXt_chunk : (I)V
    //   576: goto -> 704
    //   579: iload_2
    //   580: newarray byte
    //   582: astore #5
    //   584: aload_0
    //   585: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   588: aload #5
    //   590: invokeinterface readFully : ([B)V
    //   595: new java/lang/StringBuilder
    //   598: dup
    //   599: iconst_4
    //   600: invokespecial <init> : (I)V
    //   603: astore #6
    //   605: aload #6
    //   607: iload_3
    //   608: bipush #24
    //   610: iushr
    //   611: i2c
    //   612: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   615: pop
    //   616: aload #6
    //   618: iload_3
    //   619: bipush #16
    //   621: ishr
    //   622: sipush #255
    //   625: iand
    //   626: i2c
    //   627: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   630: pop
    //   631: aload #6
    //   633: iload_3
    //   634: bipush #8
    //   636: ishr
    //   637: sipush #255
    //   640: iand
    //   641: i2c
    //   642: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   645: pop
    //   646: aload #6
    //   648: iload_3
    //   649: sipush #255
    //   652: iand
    //   653: i2c
    //   654: invokevirtual append : (C)Ljava/lang/StringBuilder;
    //   657: pop
    //   658: iload_3
    //   659: bipush #28
    //   661: iushr
    //   662: istore #7
    //   664: iload #7
    //   666: ifne -> 675
    //   669: aload_0
    //   670: ldc 'Encountered unknown chunk with critical bit set!'
    //   672: invokevirtual processWarningOccurred : (Ljava/lang/String;)V
    //   675: aload_0
    //   676: getfield metadata : Lcom/sun/imageio/plugins/png/PNGMetadata;
    //   679: getfield unknownChunkType : Ljava/util/ArrayList;
    //   682: aload #6
    //   684: invokevirtual toString : ()Ljava/lang/String;
    //   687: invokevirtual add : (Ljava/lang/Object;)Z
    //   690: pop
    //   691: aload_0
    //   692: getfield metadata : Lcom/sun/imageio/plugins/png/PNGMetadata;
    //   695: getfield unknownChunkData : Ljava/util/ArrayList;
    //   698: aload #5
    //   700: invokevirtual add : (Ljava/lang/Object;)Z
    //   703: pop
    //   704: iload #4
    //   706: aload_0
    //   707: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   710: invokeinterface readInt : ()I
    //   715: if_icmpeq -> 745
    //   718: new javax/imageio/IIOException
    //   721: dup
    //   722: new java/lang/StringBuilder
    //   725: dup
    //   726: invokespecial <init> : ()V
    //   729: ldc 'Failed to read a chunk of type '
    //   731: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   734: iload_3
    //   735: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   738: invokevirtual toString : ()Ljava/lang/String;
    //   741: invokespecial <init> : (Ljava/lang/String;)V
    //   744: athrow
    //   745: aload_0
    //   746: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   749: aload_0
    //   750: getfield stream : Ljavax/imageio/stream/ImageInputStream;
    //   753: invokeinterface getStreamPosition : ()J
    //   758: invokeinterface flushBefore : (J)V
    //   763: goto -> 123
    //   766: goto -> 781
    //   769: astore_2
    //   770: new javax/imageio/IIOException
    //   773: dup
    //   774: ldc 'Error reading PNG metadata'
    //   776: aload_2
    //   777: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
    //   780: athrow
    //   781: aload_0
    //   782: iconst_1
    //   783: putfield gotMetadata : Z
    //   786: return
    // Exception table:
    //   from	to	target	type
    //   32	102	105	java/io/IOException
    //   123	766	769	java/io/IOException
    //   174	224	227	java/io/IOException }
  
  private static void decodeSubFilter(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) {
    for (int i = paramInt3; i < paramInt2; i++) {
      byte b = paramArrayOfByte[i + paramInt1] & 0xFF;
      b += (paramArrayOfByte[i + paramInt1 - paramInt3] & 0xFF);
      paramArrayOfByte[i + paramInt1] = (byte)b;
    } 
  }
  
  private static void decodeUpFilter(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3) {
    for (int i = 0; i < paramInt3; i++) {
      byte b1 = paramArrayOfByte1[i + paramInt1] & 0xFF;
      byte b2 = paramArrayOfByte2[i + paramInt2] & 0xFF;
      paramArrayOfByte1[i + paramInt1] = (byte)(b1 + b2);
    } 
  }
  
  private static void decodeAverageFilter(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4) {
    int i;
    for (i = 0; i < paramInt4; i++) {
      byte b1 = paramArrayOfByte1[i + paramInt1] & 0xFF;
      byte b2 = paramArrayOfByte2[i + paramInt2] & 0xFF;
      paramArrayOfByte1[i + paramInt1] = (byte)(b1 + b2 / 2);
    } 
    for (i = paramInt4; i < paramInt3; i++) {
      byte b1 = paramArrayOfByte1[i + paramInt1] & 0xFF;
      byte b2 = paramArrayOfByte1[i + paramInt1 - paramInt4] & 0xFF;
      byte b3 = paramArrayOfByte2[i + paramInt2] & 0xFF;
      paramArrayOfByte1[i + paramInt1] = (byte)(b1 + (b2 + b3) / 2);
    } 
  }
  
  private static int paethPredictor(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1 + paramInt2 - paramInt3;
    int j = Math.abs(i - paramInt1);
    int k = Math.abs(i - paramInt2);
    int m = Math.abs(i - paramInt3);
    return (j <= k && j <= m) ? paramInt1 : ((k <= m) ? paramInt2 : paramInt3);
  }
  
  private static void decodePaethFilter(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4) {
    int i;
    for (i = 0; i < paramInt4; i++) {
      byte b1 = paramArrayOfByte1[i + paramInt1] & 0xFF;
      byte b2 = paramArrayOfByte2[i + paramInt2] & 0xFF;
      paramArrayOfByte1[i + paramInt1] = (byte)(b1 + b2);
    } 
    for (i = paramInt4; i < paramInt3; i++) {
      byte b1 = paramArrayOfByte1[i + paramInt1] & 0xFF;
      byte b2 = paramArrayOfByte1[i + paramInt1 - paramInt4] & 0xFF;
      byte b3 = paramArrayOfByte2[i + paramInt2] & 0xFF;
      byte b4 = paramArrayOfByte2[i + paramInt2 - paramInt4] & 0xFF;
      paramArrayOfByte1[i + paramInt1] = (byte)(b1 + paethPredictor(b2, b3, b4));
    } 
  }
  
  private WritableRaster createRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    WritableRaster writableRaster = null;
    Point point = new Point(0, 0);
    if (paramInt5 < 8 && paramInt3 == 1) {
      DataBufferByte dataBufferByte = new DataBufferByte(paramInt2 * paramInt4);
      writableRaster = Raster.createPackedRaster(dataBufferByte, paramInt1, paramInt2, paramInt5, point);
    } else if (paramInt5 <= 8) {
      DataBufferByte dataBufferByte = new DataBufferByte(paramInt2 * paramInt4);
      writableRaster = Raster.createInterleavedRaster(dataBufferByte, paramInt1, paramInt2, paramInt4, paramInt3, bandOffsets[paramInt3], point);
    } else {
      DataBufferUShort dataBufferUShort = new DataBufferUShort(paramInt2 * paramInt4);
      writableRaster = Raster.createInterleavedRaster(dataBufferUShort, paramInt1, paramInt2, paramInt4, paramInt3, bandOffsets[paramInt3], point);
    } 
    return writableRaster;
  }
  
  private void skipPass(int paramInt1, int paramInt2) throws IOException, IIOException {
    if (paramInt1 == 0 || paramInt2 == 0)
      return; 
    int i = inputBandsForColorType[this.metadata.IHDR_colorType];
    int j = (i * paramInt1 * this.metadata.IHDR_bitDepth + 7) / 8;
    for (byte b = 0; b < paramInt2; b++) {
      this.pixelStream.skipBytes(1 + j);
      if (abortRequested())
        return; 
    } 
  }
  
  private void updateImageProgress(int paramInt) throws IOException {
    this.pixelsDone += paramInt;
    processImageProgress(100.0F * this.pixelsDone / this.totalPixels);
  }
  
  private void decodePass(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) throws IOException {
    if (paramInt6 == 0 || paramInt7 == 0)
      return; 
    WritableRaster writableRaster1 = this.theImage.getWritableTile(0, 0);
    int i = writableRaster1.getMinX();
    int j = i + writableRaster1.getWidth() - 1;
    int k = writableRaster1.getMinY();
    int m = k + writableRaster1.getHeight() - 1;
    int[] arrayOfInt1 = ReaderUtil.computeUpdatedPixels(this.sourceRegion, this.destinationOffset, i, k, j, m, this.sourceXSubsampling, this.sourceYSubsampling, paramInt2, paramInt3, paramInt6, paramInt7, paramInt4, paramInt5);
    int n = arrayOfInt1[0];
    int i1 = arrayOfInt1[1];
    int i2 = arrayOfInt1[2];
    int i3 = arrayOfInt1[4];
    int i4 = arrayOfInt1[5];
    int i5 = this.metadata.IHDR_bitDepth;
    int i6 = inputBandsForColorType[this.metadata.IHDR_colorType];
    int i7 = (i5 == 16) ? 2 : 1;
    i7 *= i6;
    int i8 = (i6 * paramInt6 * i5 + 7) / 8;
    int i9 = (i5 == 16) ? (i8 / 2) : i8;
    if (i2 == 0) {
      for (byte b1 = 0; b1 < paramInt7; b1++) {
        updateImageProgress(paramInt6);
        this.pixelStream.skipBytes(1 + i8);
      } 
      return;
    } 
    int i10 = (n - this.destinationOffset.x) * this.sourceXSubsampling + this.sourceRegion.x;
    int i11 = (i10 - paramInt2) / paramInt4;
    int i12 = i3 * this.sourceXSubsampling / paramInt4;
    byte[] arrayOfByte1 = null;
    short[] arrayOfShort = null;
    byte[] arrayOfByte2 = new byte[i8];
    byte[] arrayOfByte3 = new byte[i8];
    WritableRaster writableRaster2 = createRaster(paramInt6, 1, i6, i9, i5);
    int[] arrayOfInt2 = writableRaster2.getPixel(0, 0, (int[])null);
    DataBuffer dataBuffer = writableRaster2.getDataBuffer();
    int i13 = dataBuffer.getDataType();
    if (i13 == 0) {
      arrayOfByte1 = ((DataBufferByte)dataBuffer).getData();
    } else {
      arrayOfShort = ((DataBufferUShort)dataBuffer).getData();
    } 
    processPassStarted(this.theImage, paramInt1, this.sourceMinProgressivePass, this.sourceMaxProgressivePass, n, i1, i3, i4, this.destinationBands);
    if (this.sourceBands != null)
      writableRaster2 = writableRaster2.createWritableChild(0, 0, writableRaster2.getWidth(), 1, 0, 0, this.sourceBands); 
    if (this.destinationBands != null)
      writableRaster1 = writableRaster1.createWritableChild(0, 0, writableRaster1.getWidth(), writableRaster1.getHeight(), 0, 0, this.destinationBands); 
    boolean bool1 = false;
    int[] arrayOfInt3 = writableRaster1.getSampleModel().getSampleSize();
    int i14 = arrayOfInt3.length;
    for (byte b = 0; b < i14; b++) {
      if (arrayOfInt3[b] != i5) {
        bool1 = true;
        break;
      } 
    } 
    int[][] arrayOfInt = (int[][])null;
    if (bool1) {
      int i16 = (1 << i5) - 1;
      int i17 = i16 / 2;
      arrayOfInt = new int[i14][];
      for (byte b1 = 0; b1 < i14; b1++) {
        int i18 = (1 << arrayOfInt3[b1]) - 1;
        arrayOfInt[b1] = new int[i16 + 1];
        for (int i19 = 0; i19 <= i16; i19++)
          arrayOfInt[b1][i19] = (i19 * i18 + i17) / i16; 
      } 
    } 
    boolean bool2 = (i12 == 1 && i3 == 1 && !bool1 && writableRaster1 instanceof sun.awt.image.ByteInterleavedRaster) ? 1 : 0;
    if (bool2)
      writableRaster2 = writableRaster2.createWritableChild(i11, 0, i2, 1, 0, 0, null); 
    for (int i15 = 0; i15 < paramInt7; i15++) {
      updateImageProgress(paramInt6);
      int i16 = this.pixelStream.read();
      try {
        byte[] arrayOfByte = arrayOfByte3;
        arrayOfByte3 = arrayOfByte2;
        arrayOfByte2 = arrayOfByte;
        this.pixelStream.readFully(arrayOfByte2, 0, i8);
      } catch (ZipException zipException) {
        throw zipException;
      } 
      switch (i16) {
        case 0:
          break;
        case 1:
          decodeSubFilter(arrayOfByte2, 0, i8, i7);
          break;
        case 2:
          decodeUpFilter(arrayOfByte2, 0, arrayOfByte3, 0, i8);
          break;
        case 3:
          decodeAverageFilter(arrayOfByte2, 0, arrayOfByte3, 0, i8, i7);
          break;
        case 4:
          decodePaethFilter(arrayOfByte2, 0, arrayOfByte3, 0, i8, i7);
          break;
        default:
          throw new IIOException("Unknown row filter type (= " + i16 + ")!");
      } 
      if (i5 < 16) {
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, i8);
      } else {
        boolean bool = false;
        for (byte b1 = 0; b1 < i9; b1++) {
          arrayOfShort[b1] = (short)(arrayOfByte2[bool] << 8 | arrayOfByte2[bool + true] & 0xFF);
          bool += true;
        } 
      } 
      int i17 = i15 * paramInt5 + paramInt3;
      if (i17 >= this.sourceRegion.y && i17 < this.sourceRegion.y + this.sourceRegion.height && (i17 - this.sourceRegion.y) % this.sourceYSubsampling == 0) {
        int i18 = this.destinationOffset.y + (i17 - this.sourceRegion.y) / this.sourceYSubsampling;
        if (i18 >= k) {
          if (i18 > m)
            break; 
          if (bool2) {
            writableRaster1.setRect(n, i18, writableRaster2);
          } else {
            int i19 = i11;
            int i20;
            for (i20 = n; i20 < n + i2; i20 += i3) {
              writableRaster2.getPixel(i19, 0, arrayOfInt2);
              if (bool1)
                for (byte b1 = 0; b1 < i14; b1++)
                  arrayOfInt2[b1] = arrayOfInt[b1][arrayOfInt2[b1]];  
              writableRaster1.setPixel(i20, i18, arrayOfInt2);
              i19 += i12;
            } 
          } 
          processImageUpdate(this.theImage, n, i18, i2, 1, i3, i4, this.destinationBands);
          if (abortRequested())
            return; 
        } 
      } 
    } 
    processPassComplete(this.theImage);
  }
  
  private void decodeImage() throws IIOException {
    int i = this.metadata.IHDR_width;
    int j = this.metadata.IHDR_height;
    this.pixelsDone = 0;
    this.totalPixels = i * j;
    clearAbortRequest();
    if (this.metadata.IHDR_interlaceMethod == 0) {
      decodePass(0, 0, 0, 1, 1, i, j);
    } else {
      for (byte b = 0; b <= this.sourceMaxProgressivePass; b++) {
        int k = adam7XOffset[b];
        int m = adam7YOffset[b];
        int n = adam7XSubsampling[b];
        int i1 = adam7YSubsampling[b];
        int i2 = adam7XSubsampling[b + true] - 1;
        int i3 = adam7YSubsampling[b + true] - 1;
        if (b >= this.sourceMinProgressivePass) {
          decodePass(b, k, m, n, i1, (i + i2) / n, (j + i3) / i1);
        } else {
          skipPass((i + i2) / n, (j + i3) / i1);
        } 
        if (abortRequested())
          return; 
      } 
    } 
  }
  
  private void readImage(ImageReadParam paramImageReadParam) throws IIOException {
    readMetadata();
    int i = this.metadata.IHDR_width;
    int j = this.metadata.IHDR_height;
    this.sourceXSubsampling = 1;
    this.sourceYSubsampling = 1;
    this.sourceMinProgressivePass = 0;
    this.sourceMaxProgressivePass = 6;
    this.sourceBands = null;
    this.destinationBands = null;
    this.destinationOffset = new Point(0, 0);
    if (paramImageReadParam != null) {
      this.sourceXSubsampling = paramImageReadParam.getSourceXSubsampling();
      this.sourceYSubsampling = paramImageReadParam.getSourceYSubsampling();
      this.sourceMinProgressivePass = Math.max(paramImageReadParam.getSourceMinProgressivePass(), 0);
      this.sourceMaxProgressivePass = Math.min(paramImageReadParam.getSourceMaxProgressivePass(), 6);
      this.sourceBands = paramImageReadParam.getSourceBands();
      this.destinationBands = paramImageReadParam.getDestinationBands();
      this.destinationOffset = paramImageReadParam.getDestinationOffset();
    } 
    inflater = null;
    try {
      this.stream.seek(this.imageStartPosition);
      PNGImageDataEnumeration pNGImageDataEnumeration = new PNGImageDataEnumeration(this.stream);
      SequenceInputStream sequenceInputStream = new SequenceInputStream(pNGImageDataEnumeration);
      inflater = new Inflater();
      InflaterInputStream inflaterInputStream = new InflaterInputStream(sequenceInputStream, inflater);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(inflaterInputStream);
      this.pixelStream = new DataInputStream(bufferedInputStream);
      this.theImage = getDestination(paramImageReadParam, getImageTypes(0), i, j);
      Rectangle rectangle = new Rectangle(0, 0, 0, 0);
      this.sourceRegion = new Rectangle(0, 0, 0, 0);
      computeRegions(paramImageReadParam, i, j, this.theImage, this.sourceRegion, rectangle);
      this.destinationOffset.setLocation(rectangle.getLocation());
      int k = this.metadata.IHDR_colorType;
      checkReadParamBandSettings(paramImageReadParam, inputBandsForColorType[k], this.theImage.getSampleModel().getNumBands());
      processImageStarted(0);
      decodeImage();
      if (abortRequested()) {
        processReadAborted();
      } else {
        processImageComplete();
      } 
    } catch (IOException iOException) {
      throw new IIOException("Error reading PNG image data", iOException);
    } finally {
      if (inflater != null)
        inflater.end(); 
    } 
  }
  
  public int getNumImages(boolean paramBoolean) throws IIOException {
    if (this.stream == null)
      throw new IllegalStateException("No input source set!"); 
    if (this.seekForwardOnly && paramBoolean)
      throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!"); 
    return 1;
  }
  
  public int getWidth(int paramInt) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readHeader();
    return this.metadata.IHDR_width;
  }
  
  public int getHeight(int paramInt) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readHeader();
    return this.metadata.IHDR_height;
  }
  
  public Iterator<ImageTypeSpecifier> getImageTypes(int paramInt) throws IIOException {
    byte[] arrayOfByte4;
    byte[] arrayOfByte3;
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    int k;
    byte b;
    int[] arrayOfInt;
    ColorSpace colorSpace2;
    ColorSpace colorSpace1;
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readHeader();
    ArrayList arrayList = new ArrayList(1);
    int i = this.metadata.IHDR_bitDepth;
    int j = this.metadata.IHDR_colorType;
    if (i <= 8) {
      b = 0;
    } else {
      b = 1;
    } 
    switch (j) {
      case 0:
        arrayList.add(ImageTypeSpecifier.createGrayscale(i, b, false));
        break;
      case 2:
        if (i == 8) {
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(5));
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(1));
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(4));
        } 
        colorSpace1 = ColorSpace.getInstance(1000);
        arrayOfInt = new int[3];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 1;
        arrayOfInt[2] = 2;
        arrayList.add(ImageTypeSpecifier.createInterleaved(colorSpace1, arrayOfInt, b, false, false));
        break;
      case 3:
        readMetadata();
        k = 1 << i;
        arrayOfByte1 = this.metadata.PLTE_red;
        arrayOfByte2 = this.metadata.PLTE_green;
        arrayOfByte3 = this.metadata.PLTE_blue;
        if (this.metadata.PLTE_red.length < k) {
          arrayOfByte1 = Arrays.copyOf(this.metadata.PLTE_red, k);
          Arrays.fill(arrayOfByte1, this.metadata.PLTE_red.length, k, this.metadata.PLTE_red[this.metadata.PLTE_red.length - 1]);
          arrayOfByte2 = Arrays.copyOf(this.metadata.PLTE_green, k);
          Arrays.fill(arrayOfByte2, this.metadata.PLTE_green.length, k, this.metadata.PLTE_green[this.metadata.PLTE_green.length - 1]);
          arrayOfByte3 = Arrays.copyOf(this.metadata.PLTE_blue, k);
          Arrays.fill(arrayOfByte3, this.metadata.PLTE_blue.length, k, this.metadata.PLTE_blue[this.metadata.PLTE_blue.length - 1]);
        } 
        arrayOfByte4 = null;
        if (this.metadata.tRNS_present && this.metadata.tRNS_alpha != null)
          if (this.metadata.tRNS_alpha.length == arrayOfByte1.length) {
            arrayOfByte4 = this.metadata.tRNS_alpha;
          } else {
            arrayOfByte4 = Arrays.copyOf(this.metadata.tRNS_alpha, arrayOfByte1.length);
            Arrays.fill(arrayOfByte4, this.metadata.tRNS_alpha.length, arrayOfByte1.length, (byte)-1);
          }  
        arrayList.add(ImageTypeSpecifier.createIndexed(arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4, i, 0));
        break;
      case 4:
        colorSpace2 = ColorSpace.getInstance(1003);
        arrayOfInt = new int[2];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 1;
        arrayList.add(ImageTypeSpecifier.createInterleaved(colorSpace2, arrayOfInt, b, true, false));
        break;
      case 6:
        if (i == 8) {
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(6));
          arrayList.add(ImageTypeSpecifier.createFromBufferedImageType(2));
        } 
        colorSpace1 = ColorSpace.getInstance(1000);
        arrayOfInt = new int[4];
        arrayOfInt[0] = 0;
        arrayOfInt[1] = 1;
        arrayOfInt[2] = 2;
        arrayOfInt[3] = 3;
        arrayList.add(ImageTypeSpecifier.createInterleaved(colorSpace1, arrayOfInt, b, true, false));
        break;
    } 
    return arrayList.iterator();
  }
  
  public ImageTypeSpecifier getRawImageType(int paramInt) throws IOException {
    Iterator iterator = getImageTypes(paramInt);
    ImageTypeSpecifier imageTypeSpecifier = null;
    do {
      imageTypeSpecifier = (ImageTypeSpecifier)iterator.next();
    } while (iterator.hasNext());
    return imageTypeSpecifier;
  }
  
  public ImageReadParam getDefaultReadParam() { return new ImageReadParam(); }
  
  public IIOMetadata getStreamMetadata() throws IIOException { return null; }
  
  public IIOMetadata getImageMetadata(int paramInt) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readMetadata();
    return this.metadata;
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam) throws IIOException {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException("imageIndex != 0!"); 
    readImage(paramImageReadParam);
    return this.theImage;
  }
  
  public void reset() throws IIOException {
    super.reset();
    resetStreamSettings();
  }
  
  private void resetStreamSettings() throws IIOException {
    this.gotHeader = false;
    this.gotMetadata = false;
    this.metadata = null;
    this.pixelStream = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\PNGImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */