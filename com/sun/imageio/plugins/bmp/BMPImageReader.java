package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class BMPImageReader extends ImageReader implements BMPConstants {
  private static final int VERSION_2_1_BIT = 0;
  
  private static final int VERSION_2_4_BIT = 1;
  
  private static final int VERSION_2_8_BIT = 2;
  
  private static final int VERSION_2_24_BIT = 3;
  
  private static final int VERSION_3_1_BIT = 4;
  
  private static final int VERSION_3_4_BIT = 5;
  
  private static final int VERSION_3_8_BIT = 6;
  
  private static final int VERSION_3_24_BIT = 7;
  
  private static final int VERSION_3_NT_16_BIT = 8;
  
  private static final int VERSION_3_NT_32_BIT = 9;
  
  private static final int VERSION_4_1_BIT = 10;
  
  private static final int VERSION_4_4_BIT = 11;
  
  private static final int VERSION_4_8_BIT = 12;
  
  private static final int VERSION_4_16_BIT = 13;
  
  private static final int VERSION_4_24_BIT = 14;
  
  private static final int VERSION_4_32_BIT = 15;
  
  private static final int VERSION_3_XP_EMBEDDED = 16;
  
  private static final int VERSION_4_XP_EMBEDDED = 17;
  
  private static final int VERSION_5_XP_EMBEDDED = 18;
  
  private long bitmapFileSize;
  
  private long bitmapOffset;
  
  private long compression;
  
  private long imageSize;
  
  private byte[] palette;
  
  private int imageType;
  
  private int numBands;
  
  private boolean isBottomUp;
  
  private int bitsPerPixel;
  
  private int redMask;
  
  private int greenMask;
  
  private int blueMask;
  
  private int alphaMask;
  
  private SampleModel sampleModel;
  
  private SampleModel originalSampleModel;
  
  private ColorModel colorModel;
  
  private ColorModel originalColorModel;
  
  private ImageInputStream iis = null;
  
  private boolean gotHeader = false;
  
  private int width;
  
  private int height;
  
  private Rectangle destinationRegion;
  
  private Rectangle sourceRegion;
  
  private BMPMetadata metadata;
  
  private BufferedImage bi;
  
  private boolean noTransform = true;
  
  private boolean seleBand = false;
  
  private int scaleX;
  
  private int scaleY;
  
  private int[] sourceBands;
  
  private int[] destBands;
  
  private static Boolean isLinkedProfileDisabled = null;
  
  private static Boolean isWindowsPlatform = null;
  
  public BMPImageReader(ImageReaderSpi paramImageReaderSpi) { super(paramImageReaderSpi); }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    this.iis = (ImageInputStream)paramObject;
    if (this.iis != null)
      this.iis.setByteOrder(ByteOrder.LITTLE_ENDIAN); 
    resetHeaderInfo();
  }
  
  public int getNumImages(boolean paramBoolean) throws IOException {
    if (this.iis == null)
      throw new IllegalStateException(I18N.getString("GetNumImages0")); 
    if (this.seekForwardOnly && paramBoolean)
      throw new IllegalStateException(I18N.getString("GetNumImages1")); 
    return 1;
  }
  
  public int getWidth(int paramInt) throws IOException {
    checkIndex(paramInt);
    try {
      readHeader();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IIOException(I18N.getString("BMPImageReader6"), illegalArgumentException);
    } 
    return this.width;
  }
  
  public int getHeight(int paramInt) throws IOException {
    checkIndex(paramInt);
    try {
      readHeader();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IIOException(I18N.getString("BMPImageReader6"), illegalArgumentException);
    } 
    return this.height;
  }
  
  private void checkIndex(int paramInt) {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException(I18N.getString("BMPImageReader0")); 
  }
  
  protected void readHeader() throws IOException, IllegalArgumentException {
    if (this.gotHeader)
      return; 
    if (this.iis == null)
      throw new IllegalStateException("Input source not set!"); 
    int i = 0;
    int j = 0;
    this.metadata = new BMPMetadata();
    this.iis.mark();
    byte[] arrayOfByte = new byte[2];
    this.iis.read(arrayOfByte);
    if (arrayOfByte[0] != 66 || arrayOfByte[1] != 77)
      throw new IllegalArgumentException(I18N.getString("BMPImageReader1")); 
    this.bitmapFileSize = this.iis.readUnsignedInt();
    this.iis.skipBytes(4);
    this.bitmapOffset = this.iis.readUnsignedInt();
    long l = this.iis.readUnsignedInt();
    if (l == 12L) {
      this.width = this.iis.readShort();
      this.height = this.iis.readShort();
    } else {
      this.width = this.iis.readInt();
      this.height = this.iis.readInt();
    } 
    this.metadata.width = this.width;
    this.metadata.height = this.height;
    int k = this.iis.readUnsignedShort();
    this.bitsPerPixel = this.iis.readUnsignedShort();
    this.metadata.bitsPerPixel = (short)this.bitsPerPixel;
    this.numBands = 3;
    if (l == 12L) {
      this.metadata.bmpVersion = "BMP v. 2.x";
      if (this.bitsPerPixel == 1) {
        this.imageType = 0;
      } else if (this.bitsPerPixel == 4) {
        this.imageType = 1;
      } else if (this.bitsPerPixel == 8) {
        this.imageType = 2;
      } else if (this.bitsPerPixel == 24) {
        this.imageType = 3;
      } 
      int m = (int)((this.bitmapOffset - 14L - l) / 3L);
      int n = m * 3;
      this.palette = new byte[n];
      this.iis.readFully(this.palette, 0, n);
      this.metadata.palette = this.palette;
      this.metadata.paletteSize = m;
    } else {
      this.compression = this.iis.readUnsignedInt();
      this.imageSize = this.iis.readUnsignedInt();
      long l1 = this.iis.readInt();
      long l2 = this.iis.readInt();
      long l3 = this.iis.readUnsignedInt();
      long l4 = this.iis.readUnsignedInt();
      this.metadata.compression = (int)this.compression;
      this.metadata.xPixelsPerMeter = (int)l1;
      this.metadata.yPixelsPerMeter = (int)l2;
      this.metadata.colorsUsed = (int)l3;
      this.metadata.colorsImportant = (int)l4;
      if (l == 40L) {
        int n;
        int m;
        switch ((int)this.compression) {
          case 4:
          case 5:
            this.metadata.bmpVersion = "BMP v. 3.x";
            this.imageType = 16;
            break;
          case 0:
          case 1:
          case 2:
            if (this.bitmapOffset < l + 14L)
              throw new IIOException(I18N.getString("BMPImageReader7")); 
            m = (int)((this.bitmapOffset - 14L - l) / 4L);
            n = m * 4;
            this.palette = new byte[n];
            this.iis.readFully(this.palette, 0, n);
            this.metadata.palette = this.palette;
            this.metadata.paletteSize = m;
            if (this.bitsPerPixel == 1) {
              this.imageType = 4;
            } else if (this.bitsPerPixel == 4) {
              this.imageType = 5;
            } else if (this.bitsPerPixel == 8) {
              this.imageType = 6;
            } else if (this.bitsPerPixel == 24) {
              this.imageType = 7;
            } else if (this.bitsPerPixel == 16) {
              this.imageType = 8;
              this.redMask = 31744;
              this.greenMask = 992;
              this.blueMask = 31;
              this.metadata.redMask = this.redMask;
              this.metadata.greenMask = this.greenMask;
              this.metadata.blueMask = this.blueMask;
            } else if (this.bitsPerPixel == 32) {
              this.imageType = 9;
              this.redMask = 16711680;
              this.greenMask = 65280;
              this.blueMask = 255;
              this.metadata.redMask = this.redMask;
              this.metadata.greenMask = this.greenMask;
              this.metadata.blueMask = this.blueMask;
            } 
            this.metadata.bmpVersion = "BMP v. 3.x";
            break;
          case 3:
            if (this.bitsPerPixel == 16) {
              this.imageType = 8;
            } else if (this.bitsPerPixel == 32) {
              this.imageType = 9;
            } 
            this.redMask = (int)this.iis.readUnsignedInt();
            this.greenMask = (int)this.iis.readUnsignedInt();
            this.blueMask = (int)this.iis.readUnsignedInt();
            this.metadata.redMask = this.redMask;
            this.metadata.greenMask = this.greenMask;
            this.metadata.blueMask = this.blueMask;
            if (l3 != 0L) {
              n = (int)l3 * 4;
              this.palette = new byte[n];
              this.iis.readFully(this.palette, 0, n);
              this.metadata.palette = this.palette;
              this.metadata.paletteSize = (int)l3;
            } 
            this.metadata.bmpVersion = "BMP v. 3.x NT";
            break;
          default:
            throw new IIOException(I18N.getString("BMPImageReader2"));
        } 
      } else if (l == 108L || l == 124L) {
        if (l == 108L) {
          this.metadata.bmpVersion = "BMP v. 4.x";
        } else if (l == 124L) {
          this.metadata.bmpVersion = "BMP v. 5.x";
        } 
        this.redMask = (int)this.iis.readUnsignedInt();
        this.greenMask = (int)this.iis.readUnsignedInt();
        this.blueMask = (int)this.iis.readUnsignedInt();
        this.alphaMask = (int)this.iis.readUnsignedInt();
        long l5 = this.iis.readUnsignedInt();
        int m = this.iis.readInt();
        int n = this.iis.readInt();
        int i1 = this.iis.readInt();
        int i2 = this.iis.readInt();
        int i3 = this.iis.readInt();
        int i4 = this.iis.readInt();
        int i5 = this.iis.readInt();
        int i6 = this.iis.readInt();
        int i7 = this.iis.readInt();
        long l6 = this.iis.readUnsignedInt();
        long l7 = this.iis.readUnsignedInt();
        long l8 = this.iis.readUnsignedInt();
        if (l == 124L) {
          this.metadata.intent = this.iis.readInt();
          i = this.iis.readInt();
          j = this.iis.readInt();
          this.iis.skipBytes(4);
        } 
        this.metadata.colorSpace = (int)l5;
        if (l5 == 0L) {
          this.metadata.redX = m;
          this.metadata.redY = n;
          this.metadata.redZ = i1;
          this.metadata.greenX = i2;
          this.metadata.greenY = i3;
          this.metadata.greenZ = i4;
          this.metadata.blueX = i5;
          this.metadata.blueY = i6;
          this.metadata.blueZ = i7;
          this.metadata.gammaRed = (int)l6;
          this.metadata.gammaGreen = (int)l7;
          this.metadata.gammaBlue = (int)l8;
        } 
        int i8 = (int)((this.bitmapOffset - 14L - l) / 4L);
        int i9 = i8 * 4;
        this.palette = new byte[i9];
        this.iis.readFully(this.palette, 0, i9);
        this.metadata.palette = this.palette;
        this.metadata.paletteSize = i8;
        switch ((int)this.compression) {
          case 4:
          case 5:
            if (l == 108L) {
              this.imageType = 17;
              break;
            } 
            if (l == 124L)
              this.imageType = 18; 
            break;
          default:
            if (this.bitsPerPixel == 1) {
              this.imageType = 10;
            } else if (this.bitsPerPixel == 4) {
              this.imageType = 11;
            } else if (this.bitsPerPixel == 8) {
              this.imageType = 12;
            } else if (this.bitsPerPixel == 16) {
              this.imageType = 13;
              if ((int)this.compression == 0) {
                this.redMask = 31744;
                this.greenMask = 992;
                this.blueMask = 31;
              } 
            } else if (this.bitsPerPixel == 24) {
              this.imageType = 14;
            } else if (this.bitsPerPixel == 32) {
              this.imageType = 15;
              if ((int)this.compression == 0) {
                this.redMask = 16711680;
                this.greenMask = 65280;
                this.blueMask = 255;
              } 
            } 
            this.metadata.redMask = this.redMask;
            this.metadata.greenMask = this.greenMask;
            this.metadata.blueMask = this.blueMask;
            this.metadata.alphaMask = this.alphaMask;
            break;
        } 
      } else {
        throw new IIOException(I18N.getString("BMPImageReader3"));
      } 
    } 
    if (this.height > 0) {
      this.isBottomUp = true;
    } else {
      this.isBottomUp = false;
      this.height = Math.abs(this.height);
    } 
    ColorSpace colorSpace = ColorSpace.getInstance(1000);
    if (this.metadata.colorSpace == 3 || this.metadata.colorSpace == 4) {
      this.iis.mark();
      this.iis.skipBytes(i - l);
      byte[] arrayOfByte1 = new byte[j];
      this.iis.readFully(arrayOfByte1, 0, j);
      this.iis.reset();
      try {
        if (this.metadata.colorSpace == 3 && isLinkedProfileAllowed() && !isUncOrDevicePath(arrayOfByte1)) {
          String str = new String(arrayOfByte1, "windows-1252");
          colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(str));
        } else {
          colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(arrayOfByte1));
        } 
      } catch (Exception exception) {
        colorSpace = ColorSpace.getInstance(1000);
      } 
    } 
    if (this.bitsPerPixel == 0 || this.compression == 4L || this.compression == 5L) {
      this.colorModel = null;
      this.sampleModel = null;
    } else if (this.bitsPerPixel == 1 || this.bitsPerPixel == 4 || this.bitsPerPixel == 8) {
      byte[] arrayOfByte3;
      byte[] arrayOfByte2;
      byte[] arrayOfByte1;
      this.numBands = 1;
      if (this.bitsPerPixel == 8) {
        arrayOfByte1 = new int[this.numBands];
        for (int m = 0; m < this.numBands; m++)
          arrayOfByte1[m] = this.numBands - 1 - m; 
        this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, arrayOfByte1);
      } else {
        this.sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, this.bitsPerPixel);
      } 
      if (this.imageType == 0 || this.imageType == 1 || this.imageType == 2) {
        l = (this.palette.length / 3);
        if (l > 256L)
          l = 256L; 
        arrayOfByte1 = new byte[(int)l];
        arrayOfByte2 = new byte[(int)l];
        arrayOfByte3 = new byte[(int)l];
        for (byte b = 0; b < (int)l; b++) {
          byte b1 = 3 * b;
          arrayOfByte3[b] = this.palette[b1];
          arrayOfByte2[b] = this.palette[b1 + 1];
          arrayOfByte1[b] = this.palette[b1 + 2];
        } 
      } else {
        l = (this.palette.length / 4);
        if (l > 256L)
          l = 256L; 
        arrayOfByte1 = new byte[(int)l];
        arrayOfByte2 = new byte[(int)l];
        arrayOfByte3 = new byte[(int)l];
        for (byte b = 0; b < l; b++) {
          byte b1 = 4 * b;
          arrayOfByte3[b] = this.palette[b1];
          arrayOfByte2[b] = this.palette[b1 + 1];
          arrayOfByte1[b] = this.palette[b1 + 2];
        } 
      } 
      if (ImageUtil.isIndicesForGrayscale(arrayOfByte1, arrayOfByte2, arrayOfByte3)) {
        this.colorModel = ImageUtil.createColorModel(null, this.sampleModel);
      } else {
        this.colorModel = new IndexColorModel(this.bitsPerPixel, (int)l, arrayOfByte1, arrayOfByte2, arrayOfByte3);
      } 
    } else if (this.bitsPerPixel == 16) {
      this.numBands = 3;
      this.sampleModel = new SinglePixelPackedSampleModel(1, this.width, this.height, new int[] { this.redMask, this.greenMask, this.blueMask });
      this.colorModel = new DirectColorModel(colorSpace, 16, this.redMask, this.greenMask, this.blueMask, 0, false, 1);
    } else if (this.bitsPerPixel == 32) {
      this.numBands = (this.alphaMask == 0) ? 3 : 4;
      new int[3][0] = this.redMask;
      new int[3][1] = this.greenMask;
      new int[3][2] = this.blueMask;
      new int[4][0] = this.redMask;
      new int[4][1] = this.greenMask;
      new int[4][2] = this.blueMask;
      new int[4][3] = this.alphaMask;
      int[] arrayOfInt = (this.numBands == 3) ? new int[3] : new int[4];
      this.sampleModel = new SinglePixelPackedSampleModel(3, this.width, this.height, arrayOfInt);
      this.colorModel = new DirectColorModel(colorSpace, 32, this.redMask, this.greenMask, this.blueMask, this.alphaMask, false, 3);
    } else {
      this.numBands = 3;
      int[] arrayOfInt = new int[this.numBands];
      for (int m = 0; m < this.numBands; m++)
        arrayOfInt[m] = this.numBands - 1 - m; 
      this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, arrayOfInt);
      this.colorModel = ImageUtil.createColorModel(colorSpace, this.sampleModel);
    } 
    this.originalSampleModel = this.sampleModel;
    this.originalColorModel = this.colorModel;
    this.iis.reset();
    this.iis.skipBytes(this.bitmapOffset);
    this.gotHeader = true;
  }
  
  public Iterator getImageTypes(int paramInt) throws IOException {
    checkIndex(paramInt);
    try {
      readHeader();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IIOException(I18N.getString("BMPImageReader6"), illegalArgumentException);
    } 
    ArrayList arrayList = new ArrayList(1);
    arrayList.add(new ImageTypeSpecifier(this.originalColorModel, this.originalSampleModel));
    return arrayList.iterator();
  }
  
  public ImageReadParam getDefaultReadParam() { return new ImageReadParam(); }
  
  public IIOMetadata getImageMetadata(int paramInt) throws IOException {
    checkIndex(paramInt);
    if (this.metadata == null)
      try {
        readHeader();
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new IIOException(I18N.getString("BMPImageReader6"), illegalArgumentException);
      }  
    return this.metadata;
  }
  
  public IIOMetadata getStreamMetadata() throws IOException { return null; }
  
  public boolean isRandomAccessEasy(int paramInt) throws IOException {
    checkIndex(paramInt);
    try {
      readHeader();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IIOException(I18N.getString("BMPImageReader6"), illegalArgumentException);
    } 
    return (this.metadata.compression == 0);
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam) throws IOException {
    if (this.iis == null)
      throw new IllegalStateException(I18N.getString("BMPImageReader5")); 
    checkIndex(paramInt);
    clearAbortRequest();
    processImageStarted(paramInt);
    if (paramImageReadParam == null)
      paramImageReadParam = getDefaultReadParam(); 
    try {
      readHeader();
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IIOException(I18N.getString("BMPImageReader6"), illegalArgumentException);
    } 
    this.sourceRegion = new Rectangle(0, 0, 0, 0);
    this.destinationRegion = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, this.width, this.height, paramImageReadParam.getDestination(), this.sourceRegion, this.destinationRegion);
    this.scaleX = paramImageReadParam.getSourceXSubsampling();
    this.scaleY = paramImageReadParam.getSourceYSubsampling();
    this.sourceBands = paramImageReadParam.getSourceBands();
    this.destBands = paramImageReadParam.getDestinationBands();
    this.seleBand = (this.sourceBands != null && this.destBands != null);
    this.noTransform = (this.destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || this.seleBand);
    if (!this.seleBand) {
      this.sourceBands = new int[this.numBands];
      this.destBands = new int[this.numBands];
      for (byte b = 0; b < this.numBands; b++) {
        this.sourceBands[b] = b;
        this.destBands[b] = b;
      } 
    } 
    this.bi = paramImageReadParam.getDestination();
    WritableRaster writableRaster = null;
    if (this.bi == null) {
      if (this.sampleModel != null && this.colorModel != null) {
        this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
        if (this.seleBand)
          this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands); 
        writableRaster = Raster.createWritableRaster(this.sampleModel, new Point());
        this.bi = new BufferedImage(this.colorModel, writableRaster, false, null);
      } 
    } else {
      writableRaster = this.bi.getWritableTile(0, 0);
      this.sampleModel = this.bi.getSampleModel();
      this.colorModel = this.bi.getColorModel();
      this.noTransform &= this.destinationRegion.equals(writableRaster.getBounds());
    } 
    byte[] arrayOfByte = null;
    short[] arrayOfShort = null;
    int[] arrayOfInt = null;
    if (this.sampleModel != null)
      if (this.sampleModel.getDataType() == 0) {
        arrayOfByte = (byte[])((DataBufferByte)writableRaster.getDataBuffer()).getData();
      } else if (this.sampleModel.getDataType() == 1) {
        arrayOfShort = (short[])((DataBufferUShort)writableRaster.getDataBuffer()).getData();
      } else if (this.sampleModel.getDataType() == 3) {
        arrayOfInt = (int[])((DataBufferInt)writableRaster.getDataBuffer()).getData();
      }  
    switch (this.imageType) {
      case 0:
        read1Bit(arrayOfByte);
        break;
      case 1:
        read4Bit(arrayOfByte);
        break;
      case 2:
        read8Bit(arrayOfByte);
        break;
      case 3:
        read24Bit(arrayOfByte);
        break;
      case 4:
        read1Bit(arrayOfByte);
        break;
      case 5:
        switch ((int)this.compression) {
          case 0:
            read4Bit(arrayOfByte);
            break;
          case 2:
            readRLE4(arrayOfByte);
            break;
        } 
        throw new IIOException(I18N.getString("BMPImageReader1"));
      case 6:
        switch ((int)this.compression) {
          case 0:
            read8Bit(arrayOfByte);
            break;
          case 1:
            readRLE8(arrayOfByte);
            break;
        } 
        throw new IIOException(I18N.getString("BMPImageReader1"));
      case 7:
        read24Bit(arrayOfByte);
        break;
      case 8:
        read16Bit(arrayOfShort);
        break;
      case 9:
        read32Bit(arrayOfInt);
        break;
      case 16:
      case 17:
      case 18:
        this.bi = readEmbedded((int)this.compression, this.bi, paramImageReadParam);
        break;
      case 10:
        read1Bit(arrayOfByte);
        break;
      case 11:
        switch ((int)this.compression) {
          case 0:
            read4Bit(arrayOfByte);
            break;
          case 2:
            readRLE4(arrayOfByte);
            break;
          default:
            throw new IIOException(I18N.getString("BMPImageReader1"));
        } 
      case 12:
        switch ((int)this.compression) {
          case 0:
            read8Bit(arrayOfByte);
            break;
          case 1:
            readRLE8(arrayOfByte);
            break;
        } 
        throw new IIOException(I18N.getString("BMPImageReader1"));
      case 13:
        read16Bit(arrayOfShort);
        break;
      case 14:
        read24Bit(arrayOfByte);
        break;
      case 15:
        read32Bit(arrayOfInt);
        break;
    } 
    if (abortRequested()) {
      processReadAborted();
    } else {
      processImageComplete();
    } 
    return this.bi;
  }
  
  public boolean canReadRaster() { return true; }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam) throws IOException {
    BufferedImage bufferedImage = read(paramInt, paramImageReadParam);
    return bufferedImage.getData();
  }
  
  private void resetHeaderInfo() throws IOException, IllegalArgumentException {
    this.gotHeader = false;
    this.bi = null;
    this.sampleModel = this.originalSampleModel = null;
    this.colorModel = this.originalColorModel = null;
  }
  
  public void reset() throws IOException, IllegalArgumentException {
    super.reset();
    this.iis = null;
    resetHeaderInfo();
  }
  
  private void read1Bit(byte[] paramArrayOfByte) throws IOException {
    int i = (this.width + 7) / 8;
    int j = i % 4;
    if (j != 0)
      j = 4 - j; 
    int k = i + j;
    if (this.noTransform) {
      int m = this.isBottomUp ? ((this.height - 1) * i) : 0;
      for (byte b = 0; b < this.height && !abortRequested(); b++) {
        this.iis.readFully(paramArrayOfByte, m, i);
        this.iis.skipBytes(j);
        m += (this.isBottomUp ? -i : i);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
      } 
    } else {
      byte[] arrayOfByte = new byte[k];
      int m = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
      if (this.isBottomUp) {
        int i4 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
        this.iis.skipBytes(k * (this.height - 1 - i4));
      } else {
        this.iis.skipBytes(k * this.sourceRegion.y);
      } 
      int n = k * (this.scaleY - 1);
      int[] arrayOfInt1 = new int[this.destinationRegion.width];
      int[] arrayOfInt2 = new int[this.destinationRegion.width];
      int[] arrayOfInt3 = new int[this.destinationRegion.width];
      int[] arrayOfInt4 = new int[this.destinationRegion.width];
      int i1 = this.destinationRegion.x;
      int i2 = this.sourceRegion.x;
      int i3 = 0;
      while (i1 < this.destinationRegion.x + this.destinationRegion.width) {
        arrayOfInt3[i3] = i2 >> 3;
        arrayOfInt1[i3] = 7 - (i2 & 0x7);
        arrayOfInt4[i3] = i1 >> 3;
        arrayOfInt2[i3] = 7 - (i1 & 0x7);
        i1++;
        i3++;
        i2 += this.scaleX;
      } 
      i1 = this.destinationRegion.y * m;
      if (this.isBottomUp)
        i1 += (this.destinationRegion.height - 1) * m; 
      i2 = 0;
      for (i3 = this.sourceRegion.y; i2 < this.destinationRegion.height && !abortRequested(); i3 += this.scaleY) {
        this.iis.read(arrayOfByte, 0, k);
        for (byte b = 0; b < this.destinationRegion.width; b++) {
          byte b1 = arrayOfByte[arrayOfInt3[b]] >> arrayOfInt1[b] & true;
          paramArrayOfByte[i1 + arrayOfInt4[b]] = (byte)(paramArrayOfByte[i1 + arrayOfInt4[b]] | b1 << arrayOfInt2[b]);
        } 
        i1 += (this.isBottomUp ? -m : m);
        this.iis.skipBytes(n);
        processImageUpdate(this.bi, 0, i2, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * i2 / this.destinationRegion.height);
        i2++;
      } 
    } 
  }
  
  private void read4Bit(byte[] paramArrayOfByte) throws IOException {
    int i = (this.width + 1) / 2;
    int j = i % 4;
    if (j != 0)
      j = 4 - j; 
    int k = i + j;
    if (this.noTransform) {
      int m = this.isBottomUp ? ((this.height - 1) * i) : 0;
      for (byte b = 0; b < this.height && !abortRequested(); b++) {
        this.iis.readFully(paramArrayOfByte, m, i);
        this.iis.skipBytes(j);
        m += (this.isBottomUp ? -i : i);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
      } 
    } else {
      byte[] arrayOfByte = new byte[k];
      int m = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
      if (this.isBottomUp) {
        int i4 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
        this.iis.skipBytes(k * (this.height - 1 - i4));
      } else {
        this.iis.skipBytes(k * this.sourceRegion.y);
      } 
      int n = k * (this.scaleY - 1);
      int[] arrayOfInt1 = new int[this.destinationRegion.width];
      int[] arrayOfInt2 = new int[this.destinationRegion.width];
      int[] arrayOfInt3 = new int[this.destinationRegion.width];
      int[] arrayOfInt4 = new int[this.destinationRegion.width];
      int i1 = this.destinationRegion.x;
      int i2 = this.sourceRegion.x;
      int i3 = 0;
      while (i1 < this.destinationRegion.x + this.destinationRegion.width) {
        arrayOfInt3[i3] = i2 >> 1;
        arrayOfInt1[i3] = 1 - (i2 & true) << 2;
        arrayOfInt4[i3] = i1 >> 1;
        arrayOfInt2[i3] = 1 - (i1 & true) << 2;
        i1++;
        i3++;
        i2 += this.scaleX;
      } 
      i1 = this.destinationRegion.y * m;
      if (this.isBottomUp)
        i1 += (this.destinationRegion.height - 1) * m; 
      i2 = 0;
      for (i3 = this.sourceRegion.y; i2 < this.destinationRegion.height && !abortRequested(); i3 += this.scaleY) {
        this.iis.read(arrayOfByte, 0, k);
        for (byte b = 0; b < this.destinationRegion.width; b++) {
          byte b1 = arrayOfByte[arrayOfInt3[b]] >> arrayOfInt1[b] & 0xF;
          paramArrayOfByte[i1 + arrayOfInt4[b]] = (byte)(paramArrayOfByte[i1 + arrayOfInt4[b]] | b1 << arrayOfInt2[b]);
        } 
        i1 += (this.isBottomUp ? -m : m);
        this.iis.skipBytes(n);
        processImageUpdate(this.bi, 0, i2, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * i2 / this.destinationRegion.height);
        i2++;
      } 
    } 
  }
  
  private void read8Bit(byte[] paramArrayOfByte) throws IOException {
    int i = this.width % 4;
    if (i != 0)
      i = 4 - i; 
    int j = this.width + i;
    if (this.noTransform) {
      int k = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
      for (byte b = 0; b < this.height && !abortRequested(); b++) {
        this.iis.readFully(paramArrayOfByte, k, this.width);
        this.iis.skipBytes(i);
        k += (this.isBottomUp ? -this.width : this.width);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
      } 
    } else {
      byte[] arrayOfByte = new byte[j];
      int k = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
      if (this.isBottomUp) {
        int i2 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
        this.iis.skipBytes(j * (this.height - 1 - i2));
      } else {
        this.iis.skipBytes(j * this.sourceRegion.y);
      } 
      int m = j * (this.scaleY - 1);
      int n = this.destinationRegion.y * k;
      if (this.isBottomUp)
        n += (this.destinationRegion.height - 1) * k; 
      n += this.destinationRegion.x;
      byte b = 0;
      int i1;
      for (i1 = this.sourceRegion.y; b < this.destinationRegion.height && !abortRequested(); i1 += this.scaleY) {
        this.iis.read(arrayOfByte, 0, j);
        int i2 = 0;
        int i3;
        for (i3 = this.sourceRegion.x; i2 < this.destinationRegion.width; i3 += this.scaleX) {
          paramArrayOfByte[n + i2] = arrayOfByte[i3];
          i2++;
        } 
        n += (this.isBottomUp ? -k : k);
        this.iis.skipBytes(m);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
        b++;
      } 
    } 
  }
  
  private void read24Bit(byte[] paramArrayOfByte) throws IOException {
    int i = this.width * 3 % 4;
    if (i != 0)
      i = 4 - i; 
    int j = this.width * 3;
    int k = j + i;
    if (this.noTransform) {
      int m = this.isBottomUp ? ((this.height - 1) * this.width * 3) : 0;
      for (byte b = 0; b < this.height && !abortRequested(); b++) {
        this.iis.readFully(paramArrayOfByte, m, j);
        this.iis.skipBytes(i);
        m += (this.isBottomUp ? -j : j);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
      } 
    } else {
      byte[] arrayOfByte = new byte[k];
      j = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
      if (this.isBottomUp) {
        int i2 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
        this.iis.skipBytes(k * (this.height - 1 - i2));
      } else {
        this.iis.skipBytes(k * this.sourceRegion.y);
      } 
      int m = k * (this.scaleY - 1);
      int n = this.destinationRegion.y * j;
      if (this.isBottomUp)
        n += (this.destinationRegion.height - 1) * j; 
      n += this.destinationRegion.x * 3;
      byte b = 0;
      int i1;
      for (i1 = this.sourceRegion.y; b < this.destinationRegion.height && !abortRequested(); i1 += this.scaleY) {
        this.iis.read(arrayOfByte, 0, k);
        byte b1 = 0;
        int i2;
        for (i2 = 3 * this.sourceRegion.x; b1 < this.destinationRegion.width; i2 += 3 * this.scaleX) {
          int i3 = 3 * b1 + n;
          for (byte b2 = 0; b2 < this.destBands.length; b2++)
            paramArrayOfByte[i3 + this.destBands[b2]] = arrayOfByte[i2 + this.sourceBands[b2]]; 
          b1++;
        } 
        n += (this.isBottomUp ? -j : j);
        this.iis.skipBytes(m);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
        b++;
      } 
    } 
  }
  
  private void read16Bit(short[] paramArrayOfShort) throws IOException {
    int i = this.width * 2 % 4;
    if (i != 0)
      i = 4 - i; 
    int j = this.width + i / 2;
    if (this.noTransform) {
      int k = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
      for (byte b = 0; b < this.height && !abortRequested(); b++) {
        this.iis.readFully(paramArrayOfShort, k, this.width);
        this.iis.skipBytes(i);
        k += (this.isBottomUp ? -this.width : this.width);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
      } 
    } else {
      short[] arrayOfShort = new short[j];
      int k = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
      if (this.isBottomUp) {
        int i2 = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
        this.iis.skipBytes(j * (this.height - 1 - i2) << 1);
      } else {
        this.iis.skipBytes(j * this.sourceRegion.y << 1);
      } 
      int m = j * (this.scaleY - 1) << 1;
      int n = this.destinationRegion.y * k;
      if (this.isBottomUp)
        n += (this.destinationRegion.height - 1) * k; 
      n += this.destinationRegion.x;
      byte b = 0;
      int i1;
      for (i1 = this.sourceRegion.y; b < this.destinationRegion.height && !abortRequested(); i1 += this.scaleY) {
        this.iis.readFully(arrayOfShort, 0, j);
        int i2 = 0;
        int i3;
        for (i3 = this.sourceRegion.x; i2 < this.destinationRegion.width; i3 += this.scaleX) {
          paramArrayOfShort[n + i2] = arrayOfShort[i3];
          i2++;
        } 
        n += (this.isBottomUp ? -k : k);
        this.iis.skipBytes(m);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
        b++;
      } 
    } 
  }
  
  private void read32Bit(int[] paramArrayOfInt) throws IOException {
    if (this.noTransform) {
      int i = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
      for (byte b = 0; b < this.height && !abortRequested(); b++) {
        this.iis.readFully(paramArrayOfInt, i, this.width);
        i += (this.isBottomUp ? -this.width : this.width);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
      } 
    } else {
      int[] arrayOfInt = new int[this.width];
      int i = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
      if (this.isBottomUp) {
        int n = this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY;
        this.iis.skipBytes(this.width * (this.height - 1 - n) << 2);
      } else {
        this.iis.skipBytes(this.width * this.sourceRegion.y << 2);
      } 
      int j = this.width * (this.scaleY - 1) << 2;
      int k = this.destinationRegion.y * i;
      if (this.isBottomUp)
        k += (this.destinationRegion.height - 1) * i; 
      k += this.destinationRegion.x;
      byte b = 0;
      int m;
      for (m = this.sourceRegion.y; b < this.destinationRegion.height && !abortRequested(); m += this.scaleY) {
        this.iis.readFully(arrayOfInt, 0, this.width);
        int n = 0;
        int i1;
        for (i1 = this.sourceRegion.x; n < this.destinationRegion.width; i1 += this.scaleX) {
          paramArrayOfInt[k + n] = arrayOfInt[i1];
          n++;
        } 
        k += (this.isBottomUp ? -i : i);
        this.iis.skipBytes(j);
        processImageUpdate(this.bi, 0, b, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * b / this.destinationRegion.height);
        b++;
      } 
    } 
  }
  
  private void readRLE8(byte[] paramArrayOfByte) throws IOException {
    int i = (int)this.imageSize;
    if (i == 0)
      i = (int)(this.bitmapFileSize - this.bitmapOffset); 
    int j = 0;
    int k = this.width % 4;
    if (k != 0)
      j = 4 - k; 
    byte[] arrayOfByte = new byte[i];
    boolean bool = false;
    this.iis.readFully(arrayOfByte, 0, i);
    decodeRLE8(i, j, arrayOfByte, paramArrayOfByte);
  }
  
  private void decodeRLE8(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws IOException {
    byte[] arrayOfByte = new byte[this.width * this.height];
    byte b1 = 0;
    byte b = 0;
    boolean bool = false;
    int i = this.isBottomUp ? (this.height - 1) : 0;
    int j = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
    byte b2 = 0;
    while (b1 != paramInt1) {
      byte b3 = paramArrayOfByte1[b1++] & 0xFF;
      if (b3 == 0) {
        byte b7;
        byte b6;
        byte b5;
        byte b4;
        switch (paramArrayOfByte1[b1++] & 0xFF) {
          case 0:
            if (i >= this.sourceRegion.y && i < this.sourceRegion.y + this.sourceRegion.height)
              if (this.noTransform) {
                int k = i * this.width;
                for (byte b8 = 0; b8 < this.width; b8++)
                  paramArrayOfByte2[k++] = arrayOfByte[b8]; 
                processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                b2++;
              } else if ((i - this.sourceRegion.y) % this.scaleY == 0) {
                int k = (i - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                int m = k * j;
                m += this.destinationRegion.x;
                int n;
                for (n = this.sourceRegion.x; n < this.sourceRegion.x + this.sourceRegion.width; n += this.scaleX)
                  paramArrayOfByte2[m++] = arrayOfByte[n]; 
                processImageUpdate(this.bi, 0, k, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                b2++;
              }  
            processImageProgress(100.0F * b2 / this.destinationRegion.height);
            i += (this.isBottomUp ? -1 : 1);
            b = 0;
            if (abortRequested())
              bool = true; 
            break;
          case 1:
            bool = true;
            break;
          case 2:
            b4 = paramArrayOfByte1[b1++] & 0xFF;
            b5 = paramArrayOfByte1[b1] & 0xFF;
            b += b4 + b5 * this.width;
            break;
          default:
            b6 = paramArrayOfByte1[b1 - 1] & 0xFF;
            for (b7 = 0; b7 < b6; b7++)
              arrayOfByte[b++] = (byte)(paramArrayOfByte1[b1++] & 0xFF); 
            if ((b6 & true) == 1)
              b1++; 
            break;
        } 
      } else {
        for (byte b4 = 0; b4 < b3; b4++)
          arrayOfByte[b++] = (byte)(paramArrayOfByte1[b1] & 0xFF); 
        b1++;
      } 
      if (bool)
        break; 
    } 
  }
  
  private void readRLE4(byte[] paramArrayOfByte) throws IOException {
    int i = (int)this.imageSize;
    if (i == 0)
      i = (int)(this.bitmapFileSize - this.bitmapOffset); 
    int j = 0;
    int k = this.width % 4;
    if (k != 0)
      j = 4 - k; 
    byte[] arrayOfByte = new byte[i];
    this.iis.readFully(arrayOfByte, 0, i);
    decodeRLE4(i, j, arrayOfByte, paramArrayOfByte);
  }
  
  private void decodeRLE4(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws IOException {
    byte[] arrayOfByte = new byte[this.width];
    byte b1 = 0;
    byte b = 0;
    boolean bool = false;
    int i = this.isBottomUp ? (this.height - 1) : 0;
    int j = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
    byte b2 = 0;
    while (b1 != paramInt1) {
      byte b3 = paramArrayOfByte1[b1++] & 0xFF;
      if (b3 == 0) {
        byte b7;
        byte b6;
        byte b5;
        byte b4;
        switch (paramArrayOfByte1[b1++] & 0xFF) {
          case 0:
            if (i >= this.sourceRegion.y && i < this.sourceRegion.y + this.sourceRegion.height)
              if (this.noTransform) {
                int k = i * (this.width + 1 >> 1);
                byte b8 = 0;
                byte b9 = 0;
                while (b8 < this.width >> 1) {
                  paramArrayOfByte2[k++] = (byte)(arrayOfByte[b9++] << 4 | arrayOfByte[b9++]);
                  b8++;
                } 
                if ((this.width & true) == 1)
                  paramArrayOfByte2[k] = (byte)(paramArrayOfByte2[k] | arrayOfByte[this.width - 1] << 4); 
                processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                b2++;
              } else if ((i - this.sourceRegion.y) % this.scaleY == 0) {
                int k = (i - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                int m = k * j;
                m += (this.destinationRegion.x >> 1);
                int n = 1 - (this.destinationRegion.x & true) << 2;
                int i1;
                for (i1 = this.sourceRegion.x; i1 < this.sourceRegion.x + this.sourceRegion.width; i1 += this.scaleX) {
                  paramArrayOfByte2[m] = (byte)(paramArrayOfByte2[m] | arrayOfByte[i1] << n);
                  n += 4;
                  if (n == 4)
                    m++; 
                  n &= 0x7;
                } 
                processImageUpdate(this.bi, 0, k, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                b2++;
              }  
            processImageProgress(100.0F * b2 / this.destinationRegion.height);
            i += (this.isBottomUp ? -1 : 1);
            b = 0;
            if (abortRequested())
              bool = true; 
            break;
          case 1:
            bool = true;
            break;
          case 2:
            b4 = paramArrayOfByte1[b1++] & 0xFF;
            b5 = paramArrayOfByte1[b1] & 0xFF;
            b += b4 + b5 * this.width;
            break;
          default:
            b6 = paramArrayOfByte1[b1 - 1] & 0xFF;
            for (b7 = 0; b7 < b6; b7++)
              arrayOfByte[b++] = (byte)(!(b7 & true) ? ((paramArrayOfByte1[b1] & 0xF0) >> 4) : (paramArrayOfByte1[b1++] & 0xF)); 
            if ((b6 & true) == 1)
              b1++; 
            if (((int)Math.ceil((b6 / 2)) & true) == 1)
              b1++; 
            break;
        } 
      } else {
        int[] arrayOfInt = { (paramArrayOfByte1[b1] & 0xF0) >> 4, paramArrayOfByte1[b1] & 0xF };
        for (byte b4 = 0; b4 < b3 && b < this.width; b4++)
          arrayOfByte[b++] = (byte)arrayOfInt[b4 & true]; 
        b1++;
      } 
      if (bool)
        break; 
    } 
  }
  
  private BufferedImage readEmbedded(int paramInt, BufferedImage paramBufferedImage, ImageReadParam paramImageReadParam) throws IOException {
    String str;
    switch (paramInt) {
      case 4:
        str = "JPEG";
        break;
      case 5:
        str = "PNG";
        break;
      default:
        throw new IOException("Unexpected compression type: " + paramInt);
    } 
    ImageReader imageReader = (ImageReader)ImageIO.getImageReadersByFormatName(str).next();
    if (imageReader == null)
      throw new RuntimeException(I18N.getString("BMPImageReader4") + " " + str); 
    byte[] arrayOfByte = new byte[(int)this.imageSize];
    this.iis.read(arrayOfByte);
    imageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(arrayOfByte)));
    if (paramBufferedImage == null) {
      ImageTypeSpecifier imageTypeSpecifier = (ImageTypeSpecifier)imageReader.getImageTypes(0).next();
      paramBufferedImage = imageTypeSpecifier.createBufferedImage(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
    } 
    imageReader.addIIOReadProgressListener(new EmbeddedProgressAdapter() {
          public void imageProgress(ImageReader param1ImageReader, float param1Float) { BMPImageReader.this.processImageProgress(param1Float); }
        });
    imageReader.addIIOReadUpdateListener(new IIOReadUpdateListener() {
          public void imageUpdate(ImageReader param1ImageReader, BufferedImage param1BufferedImage, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int[] param1ArrayOfInt) { BMPImageReader.this.processImageUpdate(param1BufferedImage, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1ArrayOfInt); }
          
          public void passComplete(ImageReader param1ImageReader, BufferedImage param1BufferedImage) { BMPImageReader.this.processPassComplete(param1BufferedImage); }
          
          public void passStarted(ImageReader param1ImageReader, BufferedImage param1BufferedImage, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int[] param1ArrayOfInt) { BMPImageReader.this.processPassStarted(param1BufferedImage, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7, param1ArrayOfInt); }
          
          public void thumbnailPassComplete(ImageReader param1ImageReader, BufferedImage param1BufferedImage) {}
          
          public void thumbnailPassStarted(ImageReader param1ImageReader, BufferedImage param1BufferedImage, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int[] param1ArrayOfInt) {}
          
          public void thumbnailUpdate(ImageReader param1ImageReader, BufferedImage param1BufferedImage, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int[] param1ArrayOfInt) {}
        });
    imageReader.addIIOReadWarningListener(new IIOReadWarningListener() {
          public void warningOccurred(ImageReader param1ImageReader, String param1String) { BMPImageReader.this.processWarningOccurred(param1String); }
        });
    ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
    imageReadParam.setDestination(paramBufferedImage);
    imageReadParam.setDestinationBands(paramImageReadParam.getDestinationBands());
    imageReadParam.setDestinationOffset(paramImageReadParam.getDestinationOffset());
    imageReadParam.setSourceBands(paramImageReadParam.getSourceBands());
    imageReadParam.setSourceRegion(paramImageReadParam.getSourceRegion());
    imageReadParam.setSourceSubsampling(paramImageReadParam.getSourceXSubsampling(), paramImageReadParam.getSourceYSubsampling(), paramImageReadParam.getSubsamplingXOffset(), paramImageReadParam.getSubsamplingYOffset());
    imageReader.read(0, imageReadParam);
    return paramBufferedImage;
  }
  
  private static boolean isLinkedProfileAllowed() {
    if (isLinkedProfileDisabled == null) {
      PrivilegedAction<Boolean> privilegedAction = new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("sun.imageio.plugins.bmp.disableLinkedProfiles")); }
        };
      isLinkedProfileDisabled = (Boolean)AccessController.doPrivileged(privilegedAction);
    } 
    return !isLinkedProfileDisabled.booleanValue();
  }
  
  private static boolean isUncOrDevicePath(byte[] paramArrayOfByte) {
    if (isWindowsPlatform == null) {
      PrivilegedAction<Boolean> privilegedAction = new PrivilegedAction<Boolean>() {
          public Boolean run() {
            String str = System.getProperty("os.name");
            return Boolean.valueOf((str != null && str.toLowerCase().startsWith("win")));
          }
        };
      isWindowsPlatform = (Boolean)AccessController.doPrivileged(privilegedAction);
    } 
    if (!isWindowsPlatform.booleanValue())
      return false; 
    if (paramArrayOfByte[0] == 47)
      paramArrayOfByte[0] = 92; 
    if (paramArrayOfByte[1] == 47)
      paramArrayOfByte[1] = 92; 
    if (paramArrayOfByte[3] == 47)
      paramArrayOfByte[3] = 92; 
    return (paramArrayOfByte[0] == 92 && paramArrayOfByte[1] == 92) ? ((paramArrayOfByte[2] == 63 && paramArrayOfByte[3] == 92) ? (((paramArrayOfByte[4] == 85 || paramArrayOfByte[4] == 117) && (paramArrayOfByte[5] == 78 || paramArrayOfByte[5] == 110) && (paramArrayOfByte[6] == 67 || paramArrayOfByte[6] == 99))) : true) : false;
  }
  
  private class EmbeddedProgressAdapter implements IIOReadProgressListener {
    private EmbeddedProgressAdapter() {}
    
    public void imageComplete(ImageReader param1ImageReader) {}
    
    public void imageProgress(ImageReader param1ImageReader, float param1Float) {}
    
    public void imageStarted(ImageReader param1ImageReader, int param1Int) {}
    
    public void thumbnailComplete(ImageReader param1ImageReader) {}
    
    public void thumbnailProgress(ImageReader param1ImageReader, float param1Float) {}
    
    public void thumbnailStarted(ImageReader param1ImageReader, int param1Int1, int param1Int2) {}
    
    public void sequenceComplete(ImageReader param1ImageReader) {}
    
    public void sequenceStarted(ImageReader param1ImageReader, int param1Int) {}
    
    public void readAborted(ImageReader param1ImageReader) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\bmp\BMPImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */