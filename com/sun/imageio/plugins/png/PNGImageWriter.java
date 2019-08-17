package com.sun.imageio.plugins.png;

import java.awt.Rectangle;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.DeflaterOutputStream;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class PNGImageWriter extends ImageWriter {
  ImageOutputStream stream = null;
  
  PNGMetadata metadata = null;
  
  int sourceXOffset = 0;
  
  int sourceYOffset = 0;
  
  int sourceWidth = 0;
  
  int sourceHeight = 0;
  
  int[] sourceBands = null;
  
  int periodX = 1;
  
  int periodY = 1;
  
  int numBands;
  
  int bpp;
  
  RowFilter rowFilter = new RowFilter();
  
  byte[] prevRow = null;
  
  byte[] currRow = null;
  
  byte[][] filteredRows = (byte[][])null;
  
  int[] sampleSize = null;
  
  int scalingBitDepth = -1;
  
  byte[][] scale = (byte[][])null;
  
  byte[] scale0 = null;
  
  byte[][] scaleh = (byte[][])null;
  
  byte[][] scalel = (byte[][])null;
  
  int totalPixels;
  
  int pixelsDone;
  
  private static int[] allowedProgressivePasses = { 1, 7 };
  
  public PNGImageWriter(ImageWriterSpi paramImageWriterSpi) { super(paramImageWriterSpi); }
  
  public void setOutput(Object paramObject) {
    super.setOutput(paramObject);
    if (paramObject != null) {
      if (!(paramObject instanceof ImageOutputStream))
        throw new IllegalArgumentException("output not an ImageOutputStream!"); 
      this.stream = (ImageOutputStream)paramObject;
    } else {
      this.stream = null;
    } 
  }
  
  public ImageWriteParam getDefaultWriteParam() { return new PNGImageWriteParam(getLocale()); }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam) { return null; }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    PNGMetadata pNGMetadata = new PNGMetadata();
    pNGMetadata.initialize(paramImageTypeSpecifier, paramImageTypeSpecifier.getSampleModel().getNumBands());
    return pNGMetadata;
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam) { return null; }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) { return (paramIIOMetadata instanceof PNGMetadata) ? (PNGMetadata)((PNGMetadata)paramIIOMetadata).clone() : new PNGMetadata(paramIIOMetadata); }
  
  private void write_magic() throws IOException {
    byte[] arrayOfByte = { -119, 80, 78, 71, 13, 10, 26, 10 };
    this.stream.write(arrayOfByte);
  }
  
  private void write_IHDR() throws IOException {
    ChunkStream chunkStream = new ChunkStream(1229472850, this.stream);
    chunkStream.writeInt(this.metadata.IHDR_width);
    chunkStream.writeInt(this.metadata.IHDR_height);
    chunkStream.writeByte(this.metadata.IHDR_bitDepth);
    chunkStream.writeByte(this.metadata.IHDR_colorType);
    if (this.metadata.IHDR_compressionMethod != 0)
      throw new IIOException("Only compression method 0 is defined in PNG 1.1"); 
    chunkStream.writeByte(this.metadata.IHDR_compressionMethod);
    if (this.metadata.IHDR_filterMethod != 0)
      throw new IIOException("Only filter method 0 is defined in PNG 1.1"); 
    chunkStream.writeByte(this.metadata.IHDR_filterMethod);
    if (this.metadata.IHDR_interlaceMethod < 0 || this.metadata.IHDR_interlaceMethod > 1)
      throw new IIOException("Only interlace methods 0 (node) and 1 (adam7) are defined in PNG 1.1"); 
    chunkStream.writeByte(this.metadata.IHDR_interlaceMethod);
    chunkStream.finish();
  }
  
  private void write_cHRM() throws IOException {
    if (this.metadata.cHRM_present) {
      ChunkStream chunkStream = new ChunkStream(1665684045, this.stream);
      chunkStream.writeInt(this.metadata.cHRM_whitePointX);
      chunkStream.writeInt(this.metadata.cHRM_whitePointY);
      chunkStream.writeInt(this.metadata.cHRM_redX);
      chunkStream.writeInt(this.metadata.cHRM_redY);
      chunkStream.writeInt(this.metadata.cHRM_greenX);
      chunkStream.writeInt(this.metadata.cHRM_greenY);
      chunkStream.writeInt(this.metadata.cHRM_blueX);
      chunkStream.writeInt(this.metadata.cHRM_blueY);
      chunkStream.finish();
    } 
  }
  
  private void write_gAMA() throws IOException {
    if (this.metadata.gAMA_present) {
      ChunkStream chunkStream = new ChunkStream(1732332865, this.stream);
      chunkStream.writeInt(this.metadata.gAMA_gamma);
      chunkStream.finish();
    } 
  }
  
  private void write_iCCP() throws IOException {
    if (this.metadata.iCCP_present) {
      ChunkStream chunkStream = new ChunkStream(1766015824, this.stream);
      chunkStream.writeBytes(this.metadata.iCCP_profileName);
      chunkStream.writeByte(0);
      chunkStream.writeByte(this.metadata.iCCP_compressionMethod);
      chunkStream.write(this.metadata.iCCP_compressedProfile);
      chunkStream.finish();
    } 
  }
  
  private void write_sBIT() throws IOException {
    if (this.metadata.sBIT_present) {
      ChunkStream chunkStream = new ChunkStream(1933723988, this.stream);
      int i = this.metadata.IHDR_colorType;
      if (this.metadata.sBIT_colorType != i) {
        processWarningOccurred(0, "sBIT metadata has wrong color type.\nThe chunk will not be written.");
        return;
      } 
      if (i == 0 || i == 4) {
        chunkStream.writeByte(this.metadata.sBIT_grayBits);
      } else if (i == 2 || i == 3 || i == 6) {
        chunkStream.writeByte(this.metadata.sBIT_redBits);
        chunkStream.writeByte(this.metadata.sBIT_greenBits);
        chunkStream.writeByte(this.metadata.sBIT_blueBits);
      } 
      if (i == 4 || i == 6)
        chunkStream.writeByte(this.metadata.sBIT_alphaBits); 
      chunkStream.finish();
    } 
  }
  
  private void write_sRGB() throws IOException {
    if (this.metadata.sRGB_present) {
      ChunkStream chunkStream = new ChunkStream(1934772034, this.stream);
      chunkStream.writeByte(this.metadata.sRGB_renderingIntent);
      chunkStream.finish();
    } 
  }
  
  private void write_PLTE() throws IOException {
    if (this.metadata.PLTE_present) {
      if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
        processWarningOccurred(0, "A PLTE chunk may not appear in a gray or gray alpha image.\nThe chunk will not be written");
        return;
      } 
      ChunkStream chunkStream = new ChunkStream(1347179589, this.stream);
      int i = this.metadata.PLTE_red.length;
      byte[] arrayOfByte = new byte[i * 3];
      byte b1 = 0;
      for (byte b2 = 0; b2 < i; b2++) {
        arrayOfByte[b1++] = this.metadata.PLTE_red[b2];
        arrayOfByte[b1++] = this.metadata.PLTE_green[b2];
        arrayOfByte[b1++] = this.metadata.PLTE_blue[b2];
      } 
      chunkStream.write(arrayOfByte);
      chunkStream.finish();
    } 
  }
  
  private void write_hIST() throws IOException {
    if (this.metadata.hIST_present) {
      ChunkStream chunkStream = new ChunkStream(1749635924, this.stream);
      if (!this.metadata.PLTE_present)
        throw new IIOException("hIST chunk without PLTE chunk!"); 
      chunkStream.writeChars(this.metadata.hIST_histogram, 0, this.metadata.hIST_histogram.length);
      chunkStream.finish();
    } 
  }
  
  private void write_tRNS() throws IOException {
    if (this.metadata.tRNS_present) {
      ChunkStream chunkStream = new ChunkStream(1951551059, this.stream);
      int i = this.metadata.IHDR_colorType;
      int j = this.metadata.tRNS_colorType;
      int k = this.metadata.tRNS_red;
      int m = this.metadata.tRNS_green;
      int n = this.metadata.tRNS_blue;
      if (i == 2 && j == 0) {
        j = i;
        k = m = n = this.metadata.tRNS_gray;
      } 
      if (j != i) {
        processWarningOccurred(0, "tRNS metadata has incompatible color type.\nThe chunk will not be written.");
        return;
      } 
      if (i == 3) {
        if (!this.metadata.PLTE_present)
          throw new IIOException("tRNS chunk without PLTE chunk!"); 
        chunkStream.write(this.metadata.tRNS_alpha);
      } else if (i == 0) {
        chunkStream.writeShort(this.metadata.tRNS_gray);
      } else if (i == 2) {
        chunkStream.writeShort(k);
        chunkStream.writeShort(m);
        chunkStream.writeShort(n);
      } else {
        throw new IIOException("tRNS chunk for color type 4 or 6!");
      } 
      chunkStream.finish();
    } 
  }
  
  private void write_bKGD() throws IOException {
    if (this.metadata.bKGD_present) {
      ChunkStream chunkStream = new ChunkStream(1649100612, this.stream);
      int i = this.metadata.IHDR_colorType & 0x3;
      int j = this.metadata.bKGD_colorType;
      int k = this.metadata.bKGD_red;
      int m = this.metadata.bKGD_red;
      int n = this.metadata.bKGD_red;
      if (i == 2 && j == 0) {
        j = i;
        k = m = n = this.metadata.bKGD_gray;
      } 
      if (j != i) {
        processWarningOccurred(0, "bKGD metadata has incompatible color type.\nThe chunk will not be written.");
        return;
      } 
      if (i == 3) {
        chunkStream.writeByte(this.metadata.bKGD_index);
      } else if (i == 0 || i == 4) {
        chunkStream.writeShort(this.metadata.bKGD_gray);
      } else {
        chunkStream.writeShort(k);
        chunkStream.writeShort(m);
        chunkStream.writeShort(n);
      } 
      chunkStream.finish();
    } 
  }
  
  private void write_pHYs() throws IOException {
    if (this.metadata.pHYs_present) {
      ChunkStream chunkStream = new ChunkStream(1883789683, this.stream);
      chunkStream.writeInt(this.metadata.pHYs_pixelsPerUnitXAxis);
      chunkStream.writeInt(this.metadata.pHYs_pixelsPerUnitYAxis);
      chunkStream.writeByte(this.metadata.pHYs_unitSpecifier);
      chunkStream.finish();
    } 
  }
  
  private void write_sPLT() throws IOException {
    if (this.metadata.sPLT_present) {
      ChunkStream chunkStream = new ChunkStream(1934642260, this.stream);
      chunkStream.writeBytes(this.metadata.sPLT_paletteName);
      chunkStream.writeByte(0);
      chunkStream.writeByte(this.metadata.sPLT_sampleDepth);
      int i = this.metadata.sPLT_red.length;
      if (this.metadata.sPLT_sampleDepth == 8) {
        for (byte b = 0; b < i; b++) {
          chunkStream.writeByte(this.metadata.sPLT_red[b]);
          chunkStream.writeByte(this.metadata.sPLT_green[b]);
          chunkStream.writeByte(this.metadata.sPLT_blue[b]);
          chunkStream.writeByte(this.metadata.sPLT_alpha[b]);
          chunkStream.writeShort(this.metadata.sPLT_frequency[b]);
        } 
      } else {
        for (byte b = 0; b < i; b++) {
          chunkStream.writeShort(this.metadata.sPLT_red[b]);
          chunkStream.writeShort(this.metadata.sPLT_green[b]);
          chunkStream.writeShort(this.metadata.sPLT_blue[b]);
          chunkStream.writeShort(this.metadata.sPLT_alpha[b]);
          chunkStream.writeShort(this.metadata.sPLT_frequency[b]);
        } 
      } 
      chunkStream.finish();
    } 
  }
  
  private void write_tIME() throws IOException {
    if (this.metadata.tIME_present) {
      ChunkStream chunkStream = new ChunkStream(1950960965, this.stream);
      chunkStream.writeShort(this.metadata.tIME_year);
      chunkStream.writeByte(this.metadata.tIME_month);
      chunkStream.writeByte(this.metadata.tIME_day);
      chunkStream.writeByte(this.metadata.tIME_hour);
      chunkStream.writeByte(this.metadata.tIME_minute);
      chunkStream.writeByte(this.metadata.tIME_second);
      chunkStream.finish();
    } 
  }
  
  private void write_tEXt() throws IOException {
    Iterator iterator1 = this.metadata.tEXt_keyword.iterator();
    Iterator iterator2 = this.metadata.tEXt_text.iterator();
    while (iterator1.hasNext()) {
      ChunkStream chunkStream = new ChunkStream(1950701684, this.stream);
      String str1 = (String)iterator1.next();
      chunkStream.writeBytes(str1);
      chunkStream.writeByte(0);
      String str2 = (String)iterator2.next();
      chunkStream.writeBytes(str2);
      chunkStream.finish();
    } 
  }
  
  private byte[] deflate(byte[] paramArrayOfByte) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream);
    deflaterOutputStream.write(paramArrayOfByte);
    deflaterOutputStream.close();
    return byteArrayOutputStream.toByteArray();
  }
  
  private void write_iTXt() throws IOException {
    Iterator iterator1 = this.metadata.iTXt_keyword.iterator();
    Iterator iterator2 = this.metadata.iTXt_compressionFlag.iterator();
    Iterator iterator3 = this.metadata.iTXt_compressionMethod.iterator();
    Iterator iterator4 = this.metadata.iTXt_languageTag.iterator();
    Iterator iterator5 = this.metadata.iTXt_translatedKeyword.iterator();
    Iterator iterator6 = this.metadata.iTXt_text.iterator();
    while (iterator1.hasNext()) {
      ChunkStream chunkStream = new ChunkStream(1767135348, this.stream);
      chunkStream.writeBytes((String)iterator1.next());
      chunkStream.writeByte(0);
      Boolean bool = (Boolean)iterator2.next();
      chunkStream.writeByte(bool.booleanValue() ? 1 : 0);
      chunkStream.writeByte(((Integer)iterator3.next()).intValue());
      chunkStream.writeBytes((String)iterator4.next());
      chunkStream.writeByte(0);
      chunkStream.write(((String)iterator5.next()).getBytes("UTF8"));
      chunkStream.writeByte(0);
      String str = (String)iterator6.next();
      if (bool.booleanValue()) {
        chunkStream.write(deflate(str.getBytes("UTF8")));
      } else {
        chunkStream.write(str.getBytes("UTF8"));
      } 
      chunkStream.finish();
    } 
  }
  
  private void write_zTXt() throws IOException {
    Iterator iterator1 = this.metadata.zTXt_keyword.iterator();
    Iterator iterator2 = this.metadata.zTXt_compressionMethod.iterator();
    Iterator iterator3 = this.metadata.zTXt_text.iterator();
    while (iterator1.hasNext()) {
      ChunkStream chunkStream = new ChunkStream(2052348020, this.stream);
      String str1 = (String)iterator1.next();
      chunkStream.writeBytes(str1);
      chunkStream.writeByte(0);
      int i = ((Integer)iterator2.next()).intValue();
      chunkStream.writeByte(i);
      String str2 = (String)iterator3.next();
      chunkStream.write(deflate(str2.getBytes("ISO-8859-1")));
      chunkStream.finish();
    } 
  }
  
  private void writeUnknownChunks() throws IOException {
    Iterator iterator1 = this.metadata.unknownChunkType.iterator();
    Iterator iterator2 = this.metadata.unknownChunkData.iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      String str = (String)iterator1.next();
      ChunkStream chunkStream = new ChunkStream(chunkType(str), this.stream);
      byte[] arrayOfByte = (byte[])iterator2.next();
      chunkStream.write(arrayOfByte);
      chunkStream.finish();
    } 
  }
  
  private static int chunkType(String paramString) {
    char c1 = paramString.charAt(0);
    char c2 = paramString.charAt(1);
    char c3 = paramString.charAt(2);
    char c4 = paramString.charAt(3);
    return c1 << '\030' | c2 << '\020' | c3 << '\b' | c4;
  }
  
  private void encodePass(ImageOutputStream paramImageOutputStream, RenderedImage paramRenderedImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IOException {
    int i = this.sourceXOffset;
    int j = this.sourceYOffset;
    int k = this.sourceWidth;
    int m = this.sourceHeight;
    paramInt1 *= this.periodX;
    paramInt3 *= this.periodX;
    paramInt2 *= this.periodY;
    paramInt4 *= this.periodY;
    int n = (k - paramInt1 + paramInt3 - 1) / paramInt3;
    int i1 = (m - paramInt2 + paramInt4 - 1) / paramInt4;
    if (n == 0 || i1 == 0)
      return; 
    paramInt1 *= this.numBands;
    paramInt3 *= this.numBands;
    int i2 = 8 / this.metadata.IHDR_bitDepth;
    int i3 = k * this.numBands;
    int[] arrayOfInt = new int[i3];
    int i4 = n * this.numBands;
    if (this.metadata.IHDR_bitDepth < 8) {
      i4 = (i4 + i2 - 1) / i2;
    } else if (this.metadata.IHDR_bitDepth == 16) {
      i4 *= 2;
    } 
    IndexColorModel indexColorModel = null;
    if (this.metadata.IHDR_colorType == 4 && paramRenderedImage.getColorModel() instanceof IndexColorModel) {
      i4 *= 2;
      indexColorModel = (IndexColorModel)paramRenderedImage.getColorModel();
    } 
    this.currRow = new byte[i4 + this.bpp];
    this.prevRow = new byte[i4 + this.bpp];
    this.filteredRows = new byte[5][i4 + this.bpp];
    int i5 = this.metadata.IHDR_bitDepth;
    int i6;
    for (i6 = j + paramInt2; i6 < j + m; i6 += paramInt4) {
      int i11;
      Rectangle rectangle = new Rectangle(i, i6, k, 1);
      Raster raster = paramRenderedImage.getData(rectangle);
      if (this.sourceBands != null)
        raster = raster.createChild(i, i6, k, 1, i, i6, this.sourceBands); 
      raster.getPixels(i, i6, k, 1, arrayOfInt);
      if (paramRenderedImage.getColorModel().isAlphaPremultiplied()) {
        WritableRaster writableRaster = raster.createCompatibleWritableRaster();
        writableRaster.setPixels(writableRaster.getMinX(), writableRaster.getMinY(), writableRaster.getWidth(), writableRaster.getHeight(), arrayOfInt);
        paramRenderedImage.getColorModel().coerceData(writableRaster, false);
        writableRaster.getPixels(writableRaster.getMinX(), writableRaster.getMinY(), writableRaster.getWidth(), writableRaster.getHeight(), arrayOfInt);
      } 
      int[] arrayOfInt1 = this.metadata.PLTE_order;
      if (arrayOfInt1 != null)
        for (byte b = 0; b < i3; b++)
          arrayOfInt[b] = arrayOfInt1[arrayOfInt[b]];  
      int i7 = this.bpp;
      int i8 = 0;
      int i9 = 0;
      switch (i5) {
        case 1:
        case 2:
        case 4:
          i10 = i2 - 1;
          for (i11 = paramInt1; i11 < i3; i11 += paramInt3) {
            byte b = this.scale0[arrayOfInt[i11]];
            i9 = i9 << i5 | b;
            if ((i8++ & i10) == i10) {
              this.currRow[i7++] = (byte)i9;
              i9 = 0;
              i8 = 0;
            } 
          } 
          if ((i8 & i10) != 0) {
            i9 <<= (8 / i5 - i8) * i5;
            this.currRow[i7++] = (byte)i9;
          } 
          break;
        case 8:
          if (this.numBands == 1) {
            for (i11 = paramInt1; i11 < i3; i11 += paramInt3) {
              this.currRow[i7++] = this.scale0[arrayOfInt[i11]];
              if (indexColorModel != null)
                this.currRow[i7++] = this.scale0[indexColorModel.getAlpha(0xFF & arrayOfInt[i11])]; 
            } 
            break;
          } 
          for (i11 = paramInt1; i11 < i3; i11 += paramInt3) {
            for (int i12 = 0; i12 < this.numBands; i12++)
              this.currRow[i7++] = this.scale[i12][arrayOfInt[i11 + i12]]; 
          } 
          break;
        case 16:
          for (i11 = paramInt1; i11 < i3; i11 += paramInt3) {
            for (int i12 = 0; i12 < this.numBands; i12++) {
              this.currRow[i7++] = this.scaleh[i12][arrayOfInt[i11 + i12]];
              this.currRow[i7++] = this.scalel[i12][arrayOfInt[i11 + i12]];
            } 
          } 
          break;
      } 
      int i10 = this.rowFilter.filterRow(this.metadata.IHDR_colorType, this.currRow, this.prevRow, this.filteredRows, i4, this.bpp);
      paramImageOutputStream.write(i10);
      paramImageOutputStream.write(this.filteredRows[i10], this.bpp, i4);
      byte[] arrayOfByte = this.currRow;
      this.currRow = this.prevRow;
      this.prevRow = arrayOfByte;
      this.pixelsDone += n;
      processImageProgress(100.0F * this.pixelsDone / this.totalPixels);
      if (abortRequested())
        return; 
    } 
  }
  
  private void write_IDAT(RenderedImage paramRenderedImage) throws IOException {
    iDATOutputStream = new IDATOutputStream(this.stream, 32768);
    try {
      if (this.metadata.IHDR_interlaceMethod == 1) {
        for (byte b = 0; b < 7; b++) {
          encodePass(iDATOutputStream, paramRenderedImage, PNGImageReader.adam7XOffset[b], PNGImageReader.adam7YOffset[b], PNGImageReader.adam7XSubsampling[b], PNGImageReader.adam7YSubsampling[b]);
          if (abortRequested())
            break; 
        } 
      } else {
        encodePass(iDATOutputStream, paramRenderedImage, 0, 0, 1, 1);
      } 
    } finally {
      iDATOutputStream.finish();
    } 
  }
  
  private void writeIEND() throws IOException {
    ChunkStream chunkStream = new ChunkStream(1229278788, this.stream);
    chunkStream.finish();
  }
  
  private boolean equals(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if (paramArrayOfInt1 == null || paramArrayOfInt2 == null)
      return false; 
    if (paramArrayOfInt1.length != paramArrayOfInt2.length)
      return false; 
    for (byte b = 0; b < paramArrayOfInt1.length; b++) {
      if (paramArrayOfInt1[b] != paramArrayOfInt2[b])
        return false; 
    } 
    return true;
  }
  
  private void initializeScaleTables(int[] paramArrayOfInt) {
    int i = this.metadata.IHDR_bitDepth;
    if (i == this.scalingBitDepth && equals(paramArrayOfInt, this.sampleSize))
      return; 
    this.sampleSize = paramArrayOfInt;
    this.scalingBitDepth = i;
    int j = (1 << i) - 1;
    if (i <= 8) {
      this.scale = new byte[this.numBands][];
      for (byte b = 0; b < this.numBands; b++) {
        int k = (1 << paramArrayOfInt[b]) - 1;
        int m = k / 2;
        this.scale[b] = new byte[k + 1];
        for (int n = 0; n <= k; n++)
          this.scale[b][n] = (byte)((n * j + m) / k); 
      } 
      this.scale0 = this.scale[0];
      this.scaleh = this.scalel = (byte[][])null;
    } else {
      this.scaleh = new byte[this.numBands][];
      this.scalel = new byte[this.numBands][];
      for (byte b = 0; b < this.numBands; b++) {
        int k = (1 << paramArrayOfInt[b]) - 1;
        int m = k / 2;
        this.scaleh[b] = new byte[k + 1];
        this.scalel[b] = new byte[k + 1];
        for (int n = 0; n <= k; n++) {
          int i1 = (n * j + m) / k;
          this.scaleh[b][n] = (byte)(i1 >> 8);
          this.scalel[b][n] = (byte)(i1 & 0xFF);
        } 
      } 
      this.scale = (byte[][])null;
      this.scale0 = null;
    } 
  }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IIOException {
    if (this.stream == null)
      throw new IllegalStateException("output == null!"); 
    if (paramIIOImage == null)
      throw new IllegalArgumentException("image == null!"); 
    if (paramIIOImage.hasRaster())
      throw new UnsupportedOperationException("image has a Raster!"); 
    RenderedImage renderedImage = paramIIOImage.getRenderedImage();
    SampleModel sampleModel = renderedImage.getSampleModel();
    this.numBands = sampleModel.getNumBands();
    this.sourceXOffset = renderedImage.getMinX();
    this.sourceYOffset = renderedImage.getMinY();
    this.sourceWidth = renderedImage.getWidth();
    this.sourceHeight = renderedImage.getHeight();
    this.sourceBands = null;
    this.periodX = 1;
    this.periodY = 1;
    if (paramImageWriteParam != null) {
      Rectangle rectangle = paramImageWriteParam.getSourceRegion();
      if (rectangle != null) {
        Rectangle rectangle1 = new Rectangle(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight());
        rectangle = rectangle.intersection(rectangle1);
        this.sourceXOffset = rectangle.x;
        this.sourceYOffset = rectangle.y;
        this.sourceWidth = rectangle.width;
        this.sourceHeight = rectangle.height;
      } 
      int k = paramImageWriteParam.getSubsamplingXOffset();
      int m = paramImageWriteParam.getSubsamplingYOffset();
      this.sourceXOffset += k;
      this.sourceYOffset += m;
      this.sourceWidth -= k;
      this.sourceHeight -= m;
      this.periodX = paramImageWriteParam.getSourceXSubsampling();
      this.periodY = paramImageWriteParam.getSourceYSubsampling();
      int[] arrayOfInt = paramImageWriteParam.getSourceBands();
      if (arrayOfInt != null) {
        this.sourceBands = arrayOfInt;
        this.numBands = this.sourceBands.length;
      } 
    } 
    int i = (this.sourceWidth + this.periodX - 1) / this.periodX;
    int j = (this.sourceHeight + this.periodY - 1) / this.periodY;
    if (i <= 0 || j <= 0)
      throw new IllegalArgumentException("Empty source region!"); 
    this.totalPixels = i * j;
    this.pixelsDone = 0;
    IIOMetadata iIOMetadata = paramIIOImage.getMetadata();
    if (iIOMetadata != null) {
      this.metadata = (PNGMetadata)convertImageMetadata(iIOMetadata, ImageTypeSpecifier.createFromRenderedImage(renderedImage), null);
    } else {
      this.metadata = new PNGMetadata();
    } 
    if (paramImageWriteParam != null)
      switch (paramImageWriteParam.getProgressiveMode()) {
        case 1:
          this.metadata.IHDR_interlaceMethod = 1;
          break;
        case 0:
          this.metadata.IHDR_interlaceMethod = 0;
          break;
      }  
    this.metadata.initialize(new ImageTypeSpecifier(renderedImage), this.numBands);
    this.metadata.IHDR_width = i;
    this.metadata.IHDR_height = j;
    this.bpp = this.numBands * ((this.metadata.IHDR_bitDepth == 16) ? 2 : 1);
    initializeScaleTables(sampleModel.getSampleSize());
    clearAbortRequest();
    processImageStarted(0);
    try {
      write_magic();
      write_IHDR();
      write_cHRM();
      write_gAMA();
      write_iCCP();
      write_sBIT();
      write_sRGB();
      write_PLTE();
      write_hIST();
      write_tRNS();
      write_bKGD();
      write_pHYs();
      write_sPLT();
      write_tIME();
      write_tEXt();
      write_iTXt();
      write_zTXt();
      writeUnknownChunks();
      write_IDAT(renderedImage);
      if (abortRequested()) {
        processWriteAborted();
      } else {
        writeIEND();
        processImageComplete();
      } 
    } catch (IOException iOException) {
      throw new IIOException("I/O error writing PNG file!", iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\png\PNGImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */