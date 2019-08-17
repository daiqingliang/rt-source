package com.sun.imageio.plugins.bmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class BMPImageWriter extends ImageWriter implements BMPConstants {
  private ImageOutputStream stream = null;
  
  private ByteArrayOutputStream embedded_stream = null;
  
  private int version;
  
  private int compressionType;
  
  private boolean isTopDown;
  
  private int w;
  
  private int h;
  
  private int compImageSize = 0;
  
  private int[] bitMasks;
  
  private int[] bitPos;
  
  private byte[] bpixels;
  
  private short[] spixels;
  
  private int[] ipixels;
  
  public BMPImageWriter(ImageWriterSpi paramImageWriterSpi) { super(paramImageWriterSpi); }
  
  public void setOutput(Object paramObject) {
    super.setOutput(paramObject);
    if (paramObject != null) {
      if (!(paramObject instanceof ImageOutputStream))
        throw new IllegalArgumentException(I18N.getString("BMPImageWriter0")); 
      this.stream = (ImageOutputStream)paramObject;
      this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    } else {
      this.stream = null;
    } 
  }
  
  public ImageWriteParam getDefaultWriteParam() { return new BMPImageWriteParam(); }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam) { return null; }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    BMPMetadata bMPMetadata = new BMPMetadata();
    bMPMetadata.bmpVersion = "BMP v. 3.x";
    bMPMetadata.compression = getPreferredCompressionType(paramImageTypeSpecifier);
    if (paramImageWriteParam != null && paramImageWriteParam.getCompressionMode() == 2)
      bMPMetadata.compression = BMPCompressionTypes.getType(paramImageWriteParam.getCompressionType()); 
    bMPMetadata.bitsPerPixel = (short)paramImageTypeSpecifier.getColorModel().getPixelSize();
    return bMPMetadata;
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam) { return null; }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) { return null; }
  
  public boolean canWriteRasters() { return true; }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    if (this.stream == null)
      throw new IllegalStateException(I18N.getString("BMPImageWriter7")); 
    if (paramIIOImage == null)
      throw new IllegalArgumentException(I18N.getString("BMPImageWriter8")); 
    clearAbortRequest();
    processImageStarted(0);
    if (paramImageWriteParam == null)
      paramImageWriteParam = getDefaultWriteParam(); 
    BMPImageWriteParam bMPImageWriteParam = (BMPImageWriteParam)paramImageWriteParam;
    int i = 24;
    boolean bool1 = false;
    int j = 0;
    IndexColorModel indexColorModel = null;
    RenderedImage renderedImage = null;
    Raster raster = null;
    boolean bool2 = paramIIOImage.hasRaster();
    Rectangle rectangle1 = paramImageWriteParam.getSourceRegion();
    SampleModel sampleModel = null;
    ColorModel colorModel = null;
    this.compImageSize = 0;
    if (bool2) {
      raster = paramIIOImage.getRaster();
      sampleModel = raster.getSampleModel();
      colorModel = ImageUtil.createColorModel(null, sampleModel);
      if (rectangle1 == null) {
        rectangle1 = raster.getBounds();
      } else {
        rectangle1 = rectangle1.intersection(raster.getBounds());
      } 
    } else {
      renderedImage = paramIIOImage.getRenderedImage();
      sampleModel = renderedImage.getSampleModel();
      colorModel = renderedImage.getColorModel();
      Rectangle rectangle = new Rectangle(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight());
      if (rectangle1 == null) {
        rectangle1 = rectangle;
      } else {
        rectangle1 = rectangle1.intersection(rectangle);
      } 
    } 
    IIOMetadata iIOMetadata = paramIIOImage.getMetadata();
    BMPMetadata bMPMetadata = null;
    if (iIOMetadata != null && iIOMetadata instanceof BMPMetadata) {
      bMPMetadata = (BMPMetadata)iIOMetadata;
    } else {
      ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(colorModel, sampleModel);
      bMPMetadata = (BMPMetadata)getDefaultImageMetadata(imageTypeSpecifier, paramImageWriteParam);
    } 
    if (rectangle1.isEmpty())
      throw new RuntimeException(I18N.getString("BMPImageWrite0")); 
    int k = paramImageWriteParam.getSourceXSubsampling();
    int m = paramImageWriteParam.getSourceYSubsampling();
    int n = paramImageWriteParam.getSubsamplingXOffset();
    int i1 = paramImageWriteParam.getSubsamplingYOffset();
    int i2 = sampleModel.getDataType();
    rectangle1.translate(n, i1);
    rectangle1.width -= n;
    rectangle1.height -= i1;
    int i3 = rectangle1.x / k;
    int i4 = rectangle1.y / m;
    this.w = (rectangle1.width + k - 1) / k;
    this.h = (rectangle1.height + m - 1) / m;
    n = rectangle1.x % k;
    i1 = rectangle1.y % m;
    Rectangle rectangle2 = new Rectangle(i3, i4, this.w, this.h);
    boolean bool3 = rectangle2.equals(rectangle1);
    int[] arrayOfInt1 = paramImageWriteParam.getSourceBands();
    boolean bool4 = true;
    int i5 = sampleModel.getNumBands();
    if (arrayOfInt1 != null) {
      sampleModel = sampleModel.createSubsetSampleModel(arrayOfInt1);
      colorModel = null;
      bool4 = false;
      i5 = sampleModel.getNumBands();
    } else {
      arrayOfInt1 = new int[i5];
      for (byte b = 0; b < i5; b++)
        arrayOfInt1[b] = b; 
    } 
    int[] arrayOfInt2 = null;
    boolean bool5 = true;
    if (sampleModel instanceof ComponentSampleModel) {
      arrayOfInt2 = ((ComponentSampleModel)sampleModel).getBandOffsets();
      if (sampleModel instanceof java.awt.image.BandedSampleModel) {
        bool5 = false;
      } else {
        for (int i16 = 0; i16 < arrayOfInt2.length; i16++)
          bool5 &= ((arrayOfInt2[i16] == arrayOfInt2.length - i16 - 1) ? 1 : 0); 
      } 
    } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
      int[] arrayOfInt = ((SinglePixelPackedSampleModel)sampleModel).getBitOffsets();
      for (byte b = 0; b < arrayOfInt.length - 1; b++)
        bool5 &= ((arrayOfInt[b] > arrayOfInt[b + true]) ? 1 : 0); 
    } 
    if (arrayOfInt2 == null) {
      arrayOfInt2 = new int[i5];
      for (byte b = 0; b < i5; b++)
        arrayOfInt2[b] = b; 
    } 
    bool3 &= bool5;
    int[] arrayOfInt3 = sampleModel.getSampleSize();
    int i6 = this.w * i5;
    switch (bMPImageWriteParam.getCompressionMode()) {
      case 2:
        this.compressionType = BMPCompressionTypes.getType(bMPImageWriteParam.getCompressionType());
        break;
      case 3:
        this.compressionType = bMPMetadata.compression;
        break;
      case 1:
        this.compressionType = getPreferredCompressionType(colorModel, sampleModel);
        break;
      default:
        this.compressionType = 0;
        break;
    } 
    if (!canEncodeImage(this.compressionType, colorModel, sampleModel))
      throw new IOException("Image can not be encoded with compression type " + BMPCompressionTypes.getName(this.compressionType)); 
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    byte[] arrayOfByte3 = null;
    byte[] arrayOfByte4 = null;
    if (this.compressionType == 3) {
      i = DataBuffer.getDataTypeSize(sampleModel.getDataType());
      if (i != 16 && i != 32) {
        i = 32;
        bool3 = false;
      } 
      i6 = this.w * i + 7 >> 3;
      bool1 = true;
      j = 3;
      arrayOfByte1 = new byte[j];
      arrayOfByte2 = new byte[j];
      arrayOfByte3 = new byte[j];
      arrayOfByte4 = new byte[j];
      int i16 = 16711680;
      int i17 = 65280;
      int i18 = 255;
      if (i == 16)
        if (colorModel instanceof DirectColorModel) {
          DirectColorModel directColorModel = (DirectColorModel)colorModel;
          i16 = directColorModel.getRedMask();
          i17 = directColorModel.getGreenMask();
          i18 = directColorModel.getBlueMask();
        } else {
          throw new IOException("Image can not be encoded with compression type " + BMPCompressionTypes.getName(this.compressionType));
        }  
      writeMaskToPalette(i16, 0, arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4);
      writeMaskToPalette(i17, 1, arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4);
      writeMaskToPalette(i18, 2, arrayOfByte1, arrayOfByte2, arrayOfByte3, arrayOfByte4);
      if (!bool3) {
        this.bitMasks = new int[3];
        this.bitMasks[0] = i16;
        this.bitMasks[1] = i17;
        this.bitMasks[2] = i18;
        this.bitPos = new int[3];
        this.bitPos[0] = firstLowBit(i16);
        this.bitPos[1] = firstLowBit(i17);
        this.bitPos[2] = firstLowBit(i18);
      } 
      if (colorModel instanceof IndexColorModel)
        indexColorModel = (IndexColorModel)colorModel; 
    } else if (colorModel instanceof IndexColorModel) {
      bool1 = true;
      indexColorModel = (IndexColorModel)colorModel;
      j = indexColorModel.getMapSize();
      if (j <= 2) {
        i = 1;
        i6 = this.w + 7 >> 3;
      } else if (j <= 16) {
        i = 4;
        i6 = this.w + 1 >> 1;
      } else if (j <= 256) {
        i = 8;
      } else {
        i = 24;
        bool1 = false;
        j = 0;
        i6 = this.w * 3;
      } 
      if (bool1 == true) {
        arrayOfByte1 = new byte[j];
        arrayOfByte2 = new byte[j];
        arrayOfByte3 = new byte[j];
        arrayOfByte4 = new byte[j];
        indexColorModel.getAlphas(arrayOfByte4);
        indexColorModel.getReds(arrayOfByte1);
        indexColorModel.getGreens(arrayOfByte2);
        indexColorModel.getBlues(arrayOfByte3);
      } 
    } else if (i5 == 1) {
      bool1 = true;
      j = 256;
      i = arrayOfInt3[0];
      i6 = this.w * i + 7 >> 3;
      arrayOfByte1 = new byte[256];
      arrayOfByte2 = new byte[256];
      arrayOfByte3 = new byte[256];
      arrayOfByte4 = new byte[256];
      for (byte b = 0; b < 'Ā'; b++) {
        arrayOfByte1[b] = (byte)b;
        arrayOfByte2[b] = (byte)b;
        arrayOfByte3[b] = (byte)b;
        arrayOfByte4[b] = -1;
      } 
    } else if (sampleModel instanceof SinglePixelPackedSampleModel && bool4) {
      int[] arrayOfInt = sampleModel.getSampleSize();
      i = 0;
      for (int i16 : arrayOfInt)
        i += i16; 
      i = roundBpp(i);
      if (i != DataBuffer.getDataTypeSize(sampleModel.getDataType()))
        bool3 = false; 
      i6 = this.w * i + 7 >> 3;
    } 
    int i7 = 0;
    int i8 = 0;
    byte b1 = 0;
    int i9 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    int i10 = j;
    int i11 = i6 % 4;
    if (i11 != 0)
      i11 = 4 - i11; 
    i8 = 54 + j * 4;
    i9 = (i6 + i11) * this.h;
    i7 = i9 + i8;
    b1 = 40;
    long l = this.stream.getStreamPosition();
    writeFileHeader(i7, i8);
    if (this.compressionType == 0 || this.compressionType == 3) {
      this.isTopDown = bMPImageWriteParam.isTopDown();
    } else {
      this.isTopDown = false;
    } 
    writeInfoHeader(b1, i);
    this.stream.writeInt(this.compressionType);
    this.stream.writeInt(i9);
    this.stream.writeInt(b2);
    this.stream.writeInt(b3);
    this.stream.writeInt(b4);
    this.stream.writeInt(i10);
    if (bool1 == true)
      if (this.compressionType == 3) {
        for (byte b = 0; b < 3; b++) {
          byte b6 = (arrayOfByte4[b] & 0xFF) + (arrayOfByte1[b] & 0xFF) * 256 + (arrayOfByte2[b] & 0xFF) * 65536 + (arrayOfByte3[b] & 0xFF) * 16777216;
          this.stream.writeInt(b6);
        } 
      } else {
        for (byte b = 0; b < j; b++) {
          this.stream.writeByte(arrayOfByte3[b]);
          this.stream.writeByte(arrayOfByte2[b]);
          this.stream.writeByte(arrayOfByte1[b]);
          this.stream.writeByte(arrayOfByte4[b]);
        } 
      }  
    int i12 = this.w * i5;
    int[] arrayOfInt4 = new int[i12 * k];
    this.bpixels = new byte[i6];
    if (this.compressionType == 4 || this.compressionType == 5) {
      this.embedded_stream = new ByteArrayOutputStream();
      writeEmbedded(paramIIOImage, bMPImageWriteParam);
      this.embedded_stream.flush();
      i9 = this.embedded_stream.size();
      long l1 = this.stream.getStreamPosition();
      i7 = i8 + i9;
      this.stream.seek(l);
      writeSize(i7, 2);
      this.stream.seek(l);
      writeSize(i9, 34);
      this.stream.seek(l1);
      this.stream.write(this.embedded_stream.toByteArray());
      this.embedded_stream = null;
      if (abortRequested()) {
        processWriteAborted();
      } else {
        processImageComplete();
        this.stream.flushBefore(this.stream.getStreamPosition());
      } 
      return;
    } 
    int i13 = arrayOfInt2[0];
    for (byte b5 = 1; b5 < arrayOfInt2.length; b5++) {
      if (arrayOfInt2[b5] > i13)
        i13 = arrayOfInt2[b5]; 
    } 
    int[] arrayOfInt5 = new int[i13 + 1];
    int i14 = i6;
    if (bool3 && bool4)
      i14 = i6 / (DataBuffer.getDataTypeSize(i2) >> 3); 
    for (int i15 = 0; i15 < this.h && !abortRequested(); i15++) {
      int i16 = i4 + i15;
      if (!this.isTopDown)
        i16 = i4 + this.h - i15 - 1; 
      Raster raster1 = raster;
      Rectangle rectangle = new Rectangle(i3 * k + n, i16 * m + i1, (this.w - 1) * k + 1, 1);
      if (!bool2)
        raster1 = renderedImage.getData(rectangle); 
      if (bool3 && bool4) {
        SampleModel sampleModel1 = raster1.getSampleModel();
        int i17 = 0;
        int i18 = rectangle.x - raster1.getSampleModelTranslateX();
        int i19 = rectangle.y - raster1.getSampleModelTranslateY();
        if (sampleModel1 instanceof ComponentSampleModel) {
          ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel1;
          i17 = componentSampleModel.getOffset(i18, i19, 0);
          for (byte b = 1; b < componentSampleModel.getNumBands(); b++) {
            if (i17 > componentSampleModel.getOffset(i18, i19, b))
              i17 = componentSampleModel.getOffset(i18, i19, b); 
          } 
        } else if (sampleModel1 instanceof MultiPixelPackedSampleModel) {
          MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel1;
          i17 = multiPixelPackedSampleModel.getOffset(i18, i19);
        } else if (sampleModel1 instanceof SinglePixelPackedSampleModel) {
          SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel1;
          i17 = singlePixelPackedSampleModel.getOffset(i18, i19);
        } 
        if (this.compressionType == 0 || this.compressionType == 3) {
          int[] arrayOfInt;
          short[] arrayOfShort2;
          short[] arrayOfShort1;
          byte[] arrayOfByte;
          switch (i2) {
            case 0:
              arrayOfByte = ((DataBufferByte)raster1.getDataBuffer()).getData();
              this.stream.write(arrayOfByte, i17, i14);
              break;
            case 2:
              arrayOfShort1 = ((DataBufferShort)raster1.getDataBuffer()).getData();
              this.stream.writeShorts(arrayOfShort1, i17, i14);
              break;
            case 1:
              arrayOfShort2 = ((DataBufferUShort)raster1.getDataBuffer()).getData();
              this.stream.writeShorts(arrayOfShort2, i17, i14);
              break;
            case 3:
              arrayOfInt = ((DataBufferInt)raster1.getDataBuffer()).getData();
              this.stream.writeInts(arrayOfInt, i17, i14);
              break;
          } 
          for (byte b = 0; b < i11; b++)
            this.stream.writeByte(0); 
        } else if (this.compressionType == 2) {
          if (this.bpixels == null || this.bpixels.length < i12)
            this.bpixels = new byte[i12]; 
          raster1.getPixels(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arrayOfInt4);
          for (byte b = 0; b < i12; b++)
            this.bpixels[b] = (byte)arrayOfInt4[b]; 
          encodeRLE4(this.bpixels, i12);
        } else if (this.compressionType == 1) {
          if (this.bpixels == null || this.bpixels.length < i12)
            this.bpixels = new byte[i12]; 
          raster1.getPixels(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arrayOfInt4);
          for (byte b = 0; b < i12; b++)
            this.bpixels[b] = (byte)arrayOfInt4[b]; 
          encodeRLE8(this.bpixels, i12);
        } 
      } else {
        raster1.getPixels(rectangle.x, rectangle.y, rectangle.width, rectangle.height, arrayOfInt4);
        if (k != 1 || i13 != i5 - 1) {
          byte b = 0;
          int i17 = 0;
          int i18;
          for (i18 = 0; b < this.w; i18 += i5) {
            System.arraycopy(arrayOfInt4, i17, arrayOfInt5, 0, arrayOfInt5.length);
            for (byte b6 = 0; b6 < i5; b6++)
              arrayOfInt4[i18 + b6] = arrayOfInt5[arrayOfInt1[b6]]; 
            b++;
            i17 += k * i5;
          } 
        } 
        writePixels(0, i12, i, arrayOfInt4, i11, i5, indexColorModel);
      } 
      processImageProgress(100.0F * i15 / this.h);
    } 
    if (this.compressionType == 2 || this.compressionType == 1) {
      this.stream.writeByte(0);
      this.stream.writeByte(1);
      incCompImageSize(2);
      i9 = this.compImageSize;
      i7 = this.compImageSize + i8;
      long l1 = this.stream.getStreamPosition();
      this.stream.seek(l);
      writeSize(i7, 2);
      this.stream.seek(l);
      writeSize(i9, 34);
      this.stream.seek(l1);
    } 
    if (abortRequested()) {
      processWriteAborted();
    } else {
      processImageComplete();
      this.stream.flushBefore(this.stream.getStreamPosition());
    } 
  }
  
  private void writePixels(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, int paramInt4, int paramInt5, IndexColorModel paramIndexColorModel) throws IOException {
    byte b3;
    byte[] arrayOfByte3;
    byte[] arrayOfByte2;
    byte[] arrayOfByte1;
    byte b2;
    int j;
    int i = 0;
    byte b1 = 0;
    switch (paramInt3) {
      case 1:
        for (j = 0; j < paramInt2 / 8; j++)
          this.bpixels[b1++] = (byte)(paramArrayOfInt[paramInt1++] << 7 | paramArrayOfInt[paramInt1++] << 6 | paramArrayOfInt[paramInt1++] << 5 | paramArrayOfInt[paramInt1++] << 4 | paramArrayOfInt[paramInt1++] << 3 | paramArrayOfInt[paramInt1++] << 2 | paramArrayOfInt[paramInt1++] << 1 | paramArrayOfInt[paramInt1++]); 
        if (paramInt2 % 8 > 0) {
          i = 0;
          for (j = 0; j < paramInt2 % 8; j++)
            i |= paramArrayOfInt[paramInt1++] << 7 - j; 
          this.bpixels[b1++] = (byte)i;
        } 
        this.stream.write(this.bpixels, 0, (paramInt2 + 7) / 8);
        break;
      case 4:
        if (this.compressionType == 2) {
          byte[] arrayOfByte = new byte[paramInt2];
          for (byte b = 0; b < paramInt2; b++)
            arrayOfByte[b] = (byte)paramArrayOfInt[paramInt1++]; 
          encodeRLE4(arrayOfByte, paramInt2);
          break;
        } 
        for (j = 0; j < paramInt2 / 2; j++) {
          i = paramArrayOfInt[paramInt1++] << 4 | paramArrayOfInt[paramInt1++];
          this.bpixels[b1++] = (byte)i;
        } 
        if (paramInt2 % 2 == 1) {
          i = paramArrayOfInt[paramInt1] << 4;
          this.bpixels[b1++] = (byte)i;
        } 
        this.stream.write(this.bpixels, 0, (paramInt2 + 1) / 2);
        break;
      case 8:
        if (this.compressionType == 1) {
          for (j = 0; j < paramInt2; j++)
            this.bpixels[j] = (byte)paramArrayOfInt[paramInt1++]; 
          encodeRLE8(this.bpixels, paramInt2);
          break;
        } 
        for (j = 0; j < paramInt2; j++)
          this.bpixels[j] = (byte)paramArrayOfInt[paramInt1++]; 
        this.stream.write(this.bpixels, 0, paramInt2);
        break;
      case 16:
        if (this.spixels == null)
          this.spixels = new short[paramInt2 / paramInt5]; 
        j = 0;
        for (b2 = 0; j < paramInt2; b2++) {
          this.spixels[b2] = 0;
          if (this.compressionType == 0) {
            this.spixels[b2] = (short)((0x1F & paramArrayOfInt[j]) << 10 | (0x1F & paramArrayOfInt[j + 1]) << 5 | 0x1F & paramArrayOfInt[j + 2]);
            j += 3;
          } else {
            byte b = 0;
            while (b < paramInt5) {
              this.spixels[b2] = (short)(this.spixels[b2] | paramArrayOfInt[j] << this.bitPos[b] & this.bitMasks[b]);
              b++;
              j++;
            } 
          } 
        } 
        this.stream.writeShorts(this.spixels, 0, this.spixels.length);
        break;
      case 24:
        if (paramInt5 == 3) {
          for (j = 0; j < paramInt2; j += 3) {
            this.bpixels[b1++] = (byte)paramArrayOfInt[paramInt1 + 2];
            this.bpixels[b1++] = (byte)paramArrayOfInt[paramInt1 + 1];
            this.bpixels[b1++] = (byte)paramArrayOfInt[paramInt1];
            paramInt1 += 3;
          } 
          this.stream.write(this.bpixels, 0, paramInt2);
          break;
        } 
        j = paramIndexColorModel.getMapSize();
        arrayOfByte1 = new byte[j];
        arrayOfByte2 = new byte[j];
        arrayOfByte3 = new byte[j];
        paramIndexColorModel.getReds(arrayOfByte1);
        paramIndexColorModel.getGreens(arrayOfByte2);
        paramIndexColorModel.getBlues(arrayOfByte3);
        for (b3 = 0; b3 < paramInt2; b3++) {
          int k = paramArrayOfInt[paramInt1];
          this.bpixels[b1++] = arrayOfByte3[k];
          this.bpixels[b1++] = arrayOfByte2[k];
          this.bpixels[b1++] = arrayOfByte3[k];
          paramInt1++;
        } 
        this.stream.write(this.bpixels, 0, paramInt2 * 3);
        break;
      case 32:
        if (this.ipixels == null)
          this.ipixels = new int[paramInt2 / paramInt5]; 
        if (paramInt5 == 3) {
          j = 0;
          for (byte b = 0; j < paramInt2; b++) {
            this.ipixels[b] = 0;
            if (this.compressionType == 0) {
              this.ipixels[b] = (0xFF & paramArrayOfInt[j + 2]) << 16 | (0xFF & paramArrayOfInt[j + 1]) << 8 | 0xFF & paramArrayOfInt[j];
              j += 3;
            } else {
              byte b4 = 0;
              while (b4 < paramInt5) {
                this.ipixels[b] = this.ipixels[b] | paramArrayOfInt[j] << this.bitPos[b4] & this.bitMasks[b4];
                b4++;
                j++;
              } 
            } 
          } 
        } else {
          for (j = 0; j < paramInt2; j++) {
            if (paramIndexColorModel != null) {
              this.ipixels[j] = paramIndexColorModel.getRGB(paramArrayOfInt[j]);
            } else {
              this.ipixels[j] = paramArrayOfInt[j] << 16 | paramArrayOfInt[j] << 8 | paramArrayOfInt[j];
            } 
          } 
        } 
        this.stream.writeInts(this.ipixels, 0, this.ipixels.length);
        break;
    } 
    if (this.compressionType == 0 || this.compressionType == 3)
      for (b1 = 0; b1 < paramInt4; b1++)
        this.stream.writeByte(0);  
  }
  
  private void encodeRLE8(byte[] paramArrayOfByte, int paramInt) throws IOException {
    byte b = 1;
    byte b1 = -1;
    byte b2 = -1;
    byte b3 = 0;
    byte b4 = 0;
    b3 = paramArrayOfByte[++b2];
    byte[] arrayOfByte = new byte[256];
    while (b2 < paramInt - 1) {
      b4 = paramArrayOfByte[++b2];
      if (b4 == b3) {
        if (b1 >= 3) {
          this.stream.writeByte(0);
          this.stream.writeByte(b1);
          incCompImageSize(2);
          for (byte b5 = 0; b5 < b1; b5++) {
            this.stream.writeByte(arrayOfByte[b5]);
            incCompImageSize(1);
          } 
          if (!isEven(b1)) {
            this.stream.writeByte(0);
            incCompImageSize(1);
          } 
        } else if (b1 > -1) {
          for (byte b5 = 0; b5 < b1; b5++) {
            this.stream.writeByte(1);
            this.stream.writeByte(arrayOfByte[b5]);
            incCompImageSize(2);
          } 
        } 
        b1 = -1;
        if (++b == 'Ā') {
          this.stream.writeByte(b - 1);
          this.stream.writeByte(b3);
          incCompImageSize(2);
          b = 1;
        } 
      } else {
        if (b > 1) {
          this.stream.writeByte(b);
          this.stream.writeByte(b3);
          incCompImageSize(2);
        } else if (b1 < 0) {
          arrayOfByte[++b1] = b3;
          arrayOfByte[++b1] = b4;
        } else if (b1 < 254) {
          arrayOfByte[++b1] = b4;
        } else {
          this.stream.writeByte(0);
          this.stream.writeByte(b1 + 1);
          incCompImageSize(2);
          for (byte b5 = 0; b5 <= b1; b5++) {
            this.stream.writeByte(arrayOfByte[b5]);
            incCompImageSize(1);
          } 
          this.stream.writeByte(0);
          incCompImageSize(1);
          b1 = -1;
        } 
        b3 = b4;
        b = 1;
      } 
      if (b2 == paramInt - 1) {
        if (b1 == -1) {
          this.stream.writeByte(b);
          this.stream.writeByte(b3);
          incCompImageSize(2);
          b = 1;
        } else if (b1 >= 2) {
          this.stream.writeByte(0);
          this.stream.writeByte(b1 + 1);
          incCompImageSize(2);
          for (byte b5 = 0; b5 <= b1; b5++) {
            this.stream.writeByte(arrayOfByte[b5]);
            incCompImageSize(1);
          } 
          if (!isEven(b1 + 1)) {
            this.stream.writeByte(0);
            incCompImageSize(1);
          } 
        } else if (b1 > -1) {
          for (byte b5 = 0; b5 <= b1; b5++) {
            this.stream.writeByte(1);
            this.stream.writeByte(arrayOfByte[b5]);
            incCompImageSize(2);
          } 
        } 
        this.stream.writeByte(0);
        this.stream.writeByte(0);
        incCompImageSize(2);
      } 
    } 
  }
  
  private void encodeRLE4(byte[] paramArrayOfByte, int paramInt) throws IOException {
    byte b1 = 2;
    byte b2 = -1;
    byte b3 = -1;
    byte b4 = 0;
    byte b5 = 0;
    byte b6 = 0;
    byte b7 = 0;
    byte b8 = 0;
    byte b9 = 0;
    byte[] arrayOfByte = new byte[256];
    b6 = paramArrayOfByte[++b3];
    b7 = paramArrayOfByte[++b3];
    while (b3 < paramInt - 2) {
      b8 = paramArrayOfByte[++b3];
      b9 = paramArrayOfByte[++b3];
      if (b8 == b6) {
        if (b2 >= 4) {
          this.stream.writeByte(0);
          this.stream.writeByte(b2 - 1);
          incCompImageSize(2);
          for (boolean bool = false; bool < b2 - 2; bool += true) {
            b4 = arrayOfByte[bool] << 4 | arrayOfByte[bool + true];
            this.stream.writeByte((byte)b4);
            incCompImageSize(1);
          } 
          if (!isEven(b2 - 1)) {
            b5 = arrayOfByte[b2 - 2] << 4 | false;
            this.stream.writeByte(b5);
            incCompImageSize(1);
          } 
          if (!isEven((int)Math.ceil(((b2 - 1) / 2)))) {
            this.stream.writeByte(0);
            incCompImageSize(1);
          } 
        } else if (b2 > -1) {
          this.stream.writeByte(2);
          b4 = arrayOfByte[0] << 4 | arrayOfByte[1];
          this.stream.writeByte(b4);
          incCompImageSize(2);
        } 
        b2 = -1;
        if (b9 == b7) {
          b1 += 2;
          if (b1 == 256) {
            this.stream.writeByte(b1 - 1);
            b4 = b6 << 4 | b7;
            this.stream.writeByte(b4);
            incCompImageSize(2);
            b1 = 2;
            if (b3 < paramInt - 1) {
              b6 = b7;
              b7 = paramArrayOfByte[++b3];
            } else {
              this.stream.writeByte(1);
              byte b = b7 << 4 | false;
              this.stream.writeByte(b);
              incCompImageSize(2);
              b1 = -1;
            } 
          } 
        } else {
          b1++;
          b4 = b6 << 4 | b7;
          this.stream.writeByte(b1);
          this.stream.writeByte(b4);
          incCompImageSize(2);
          b1 = 2;
          b6 = b9;
          if (b3 < paramInt - 1) {
            b7 = paramArrayOfByte[++b3];
          } else {
            this.stream.writeByte(1);
            byte b = b9 << 4 | false;
            this.stream.writeByte(b);
            incCompImageSize(2);
            b1 = -1;
          } 
        } 
      } else {
        if (b1 > 2) {
          b4 = b6 << 4 | b7;
          this.stream.writeByte(b1);
          this.stream.writeByte(b4);
          incCompImageSize(2);
        } else if (b2 < 0) {
          arrayOfByte[++b2] = b6;
          arrayOfByte[++b2] = b7;
          arrayOfByte[++b2] = b8;
          arrayOfByte[++b2] = b9;
        } else if (b2 < 253) {
          arrayOfByte[++b2] = b8;
          arrayOfByte[++b2] = b9;
        } else {
          this.stream.writeByte(0);
          this.stream.writeByte(b2 + 1);
          incCompImageSize(2);
          for (boolean bool = false; bool < b2; bool += true) {
            b4 = arrayOfByte[bool] << 4 | arrayOfByte[bool + true];
            this.stream.writeByte((byte)b4);
            incCompImageSize(1);
          } 
          this.stream.writeByte(0);
          incCompImageSize(1);
          b2 = -1;
        } 
        b6 = b8;
        b7 = b9;
        b1 = 2;
      } 
      if (b3 >= paramInt - 2) {
        if (b2 == -1 && b1 >= 2) {
          if (b3 == paramInt - 2) {
            if (paramArrayOfByte[++b3] == b6) {
              b1++;
              b4 = b6 << 4 | b7;
              this.stream.writeByte(b1);
              this.stream.writeByte(b4);
              incCompImageSize(2);
            } else {
              b4 = b6 << 4 | b7;
              this.stream.writeByte(b1);
              this.stream.writeByte(b4);
              this.stream.writeByte(1);
              b4 = paramArrayOfByte[b3] << 4 | false;
              this.stream.writeByte(b4);
              byte b = paramArrayOfByte[b3] << 4 | false;
              incCompImageSize(4);
            } 
          } else {
            this.stream.writeByte(b1);
            b4 = b6 << 4 | b7;
            this.stream.writeByte(b4);
            incCompImageSize(2);
          } 
        } else if (b2 > -1) {
          if (b3 == paramInt - 2)
            arrayOfByte[++b2] = paramArrayOfByte[++b3]; 
          if (b2 >= 2) {
            this.stream.writeByte(0);
            this.stream.writeByte(b2 + 1);
            incCompImageSize(2);
            for (boolean bool = false; bool < b2; bool += true) {
              b4 = arrayOfByte[bool] << 4 | arrayOfByte[bool + true];
              this.stream.writeByte((byte)b4);
              incCompImageSize(1);
            } 
            if (!isEven(b2 + 1)) {
              b5 = arrayOfByte[b2] << 4 | false;
              this.stream.writeByte(b5);
              incCompImageSize(1);
            } 
            if (!isEven((int)Math.ceil(((b2 + 1) / 2)))) {
              this.stream.writeByte(0);
              incCompImageSize(1);
            } 
          } else {
            byte b;
            switch (b2) {
              case 0:
                this.stream.writeByte(1);
                b = arrayOfByte[0] << 4 | false;
                this.stream.writeByte(b);
                incCompImageSize(2);
                break;
              case 1:
                this.stream.writeByte(2);
                b4 = arrayOfByte[0] << 4 | arrayOfByte[1];
                this.stream.writeByte(b4);
                incCompImageSize(2);
                break;
            } 
          } 
        } 
        this.stream.writeByte(0);
        this.stream.writeByte(0);
        incCompImageSize(2);
      } 
    } 
  }
  
  private void incCompImageSize(int paramInt) { this.compImageSize += paramInt; }
  
  private boolean isEven(int paramInt) { return (paramInt % 2 == 0); }
  
  private void writeFileHeader(int paramInt1, int paramInt2) throws IOException {
    this.stream.writeByte(66);
    this.stream.writeByte(77);
    this.stream.writeInt(paramInt1);
    this.stream.writeInt(0);
    this.stream.writeInt(paramInt2);
  }
  
  private void writeInfoHeader(int paramInt1, int paramInt2) throws IOException {
    this.stream.writeInt(paramInt1);
    this.stream.writeInt(this.w);
    this.stream.writeInt(this.isTopDown ? -this.h : this.h);
    this.stream.writeShort(1);
    this.stream.writeShort(paramInt2);
  }
  
  private void writeSize(int paramInt1, int paramInt2) throws IOException {
    this.stream.skipBytes(paramInt2);
    this.stream.writeInt(paramInt1);
  }
  
  public void reset() {
    super.reset();
    this.stream = null;
  }
  
  private void writeEmbedded(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    String str = (this.compressionType == 4) ? "jpeg" : "png";
    Iterator iterator = ImageIO.getImageWritersByFormatName(str);
    ImageWriter imageWriter = null;
    if (iterator.hasNext())
      imageWriter = (ImageWriter)iterator.next(); 
    if (imageWriter != null) {
      if (this.embedded_stream == null)
        throw new RuntimeException("No stream for writing embedded image!"); 
      imageWriter.addIIOWriteProgressListener(new IIOWriteProgressAdapter() {
            public void imageProgress(ImageWriter param1ImageWriter, float param1Float) { BMPImageWriter.this.processImageProgress(param1Float); }
          });
      imageWriter.addIIOWriteWarningListener(new IIOWriteWarningListener() {
            public void warningOccurred(ImageWriter param1ImageWriter, int param1Int, String param1String) { BMPImageWriter.this.processWarningOccurred(param1Int, param1String); }
          });
      imageWriter.setOutput(ImageIO.createImageOutputStream(this.embedded_stream));
      ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
      imageWriteParam.setDestinationOffset(paramImageWriteParam.getDestinationOffset());
      imageWriteParam.setSourceBands(paramImageWriteParam.getSourceBands());
      imageWriteParam.setSourceRegion(paramImageWriteParam.getSourceRegion());
      imageWriteParam.setSourceSubsampling(paramImageWriteParam.getSourceXSubsampling(), paramImageWriteParam.getSourceYSubsampling(), paramImageWriteParam.getSubsamplingXOffset(), paramImageWriteParam.getSubsamplingYOffset());
      imageWriter.write(null, paramIIOImage, imageWriteParam);
    } else {
      throw new RuntimeException(I18N.getString("BMPImageWrite5") + " " + str);
    } 
  }
  
  private int firstLowBit(int paramInt) {
    byte b = 0;
    while ((paramInt & true) == 0) {
      b++;
      paramInt >>>= 1;
    } 
    return b;
  }
  
  protected int getPreferredCompressionType(ColorModel paramColorModel, SampleModel paramSampleModel) {
    ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(paramColorModel, paramSampleModel);
    return getPreferredCompressionType(imageTypeSpecifier);
  }
  
  protected int getPreferredCompressionType(ImageTypeSpecifier paramImageTypeSpecifier) { return (paramImageTypeSpecifier.getBufferedImageType() == 8) ? 3 : 0; }
  
  protected boolean canEncodeImage(int paramInt, ColorModel paramColorModel, SampleModel paramSampleModel) {
    ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(paramColorModel, paramSampleModel);
    return canEncodeImage(paramInt, imageTypeSpecifier);
  }
  
  protected boolean canEncodeImage(int paramInt, ImageTypeSpecifier paramImageTypeSpecifier) {
    ImageWriterSpi imageWriterSpi = getOriginatingProvider();
    if (!imageWriterSpi.canEncodeImage(paramImageTypeSpecifier))
      return false; 
    int i = paramImageTypeSpecifier.getBufferedImageType();
    int j = paramImageTypeSpecifier.getColorModel().getPixelSize();
    if (this.compressionType == 2 && j != 4)
      return false; 
    if (this.compressionType == 1 && j != 8)
      return false; 
    if (j == 16) {
      boolean bool1 = false;
      boolean bool2 = false;
      SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
      if (sampleModel instanceof SinglePixelPackedSampleModel) {
        int[] arrayOfInt = ((SinglePixelPackedSampleModel)sampleModel).getSampleSize();
        bool1 = true;
        bool2 = true;
        for (byte b = 0; b < arrayOfInt.length; b++) {
          bool1 &= ((arrayOfInt[b] == 5) ? 1 : 0);
          bool2 &= ((arrayOfInt[b] == 5 || (b == 1 && arrayOfInt[b] == 6)) ? 1 : 0);
        } 
      } 
      return ((this.compressionType == 0 && bool1) || (this.compressionType == 3 && bool2));
    } 
    return true;
  }
  
  protected void writeMaskToPalette(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4) {
    paramArrayOfByte3[paramInt2] = (byte)(0xFF & paramInt1 >> 24);
    paramArrayOfByte2[paramInt2] = (byte)(0xFF & paramInt1 >> 16);
    paramArrayOfByte1[paramInt2] = (byte)(0xFF & paramInt1 >> 8);
    paramArrayOfByte4[paramInt2] = (byte)(0xFF & paramInt1);
  }
  
  private int roundBpp(int paramInt) { return (paramInt <= 8) ? 8 : ((paramInt <= 16) ? 16 : ((paramInt <= 24) ? 24 : 32)); }
  
  private class IIOWriteProgressAdapter implements IIOWriteProgressListener {
    private IIOWriteProgressAdapter() {}
    
    public void imageComplete(ImageWriter param1ImageWriter) {}
    
    public void imageProgress(ImageWriter param1ImageWriter, float param1Float) {}
    
    public void imageStarted(ImageWriter param1ImageWriter, int param1Int) {}
    
    public void thumbnailComplete(ImageWriter param1ImageWriter) {}
    
    public void thumbnailProgress(ImageWriter param1ImageWriter, float param1Float) {}
    
    public void thumbnailStarted(ImageWriter param1ImageWriter, int param1Int1, int param1Int2) {}
    
    public void writeAborted(ImageWriter param1ImageWriter) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\bmp\BMPImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */