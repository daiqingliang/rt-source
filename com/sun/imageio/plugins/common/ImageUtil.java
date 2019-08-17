package com.sun.imageio.plugins.common;

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
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
import java.awt.image.WritableRaster;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

public class ImageUtil {
  public static final ColorModel createColorModel(SampleModel paramSampleModel) {
    if (paramSampleModel == null)
      throw new IllegalArgumentException("sampleModel == null!"); 
    int i = paramSampleModel.getDataType();
    switch (i) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
        break;
      default:
        return null;
    } 
    IndexColorModel indexColorModel = null;
    int[] arrayOfInt = paramSampleModel.getSampleSize();
    if (paramSampleModel instanceof ComponentSampleModel) {
      int j = paramSampleModel.getNumBands();
      ColorSpace colorSpace = null;
      if (j <= 2) {
        colorSpace = ColorSpace.getInstance(1003);
      } else if (j <= 4) {
        colorSpace = ColorSpace.getInstance(1000);
      } else {
        colorSpace = new BogusColorSpace(j);
      } 
      boolean bool1 = (j == 2 || j == 4);
      boolean bool2 = false;
      byte b = bool1 ? 3 : 1;
      indexColorModel = new ComponentColorModel(colorSpace, arrayOfInt, bool1, bool2, b, i);
    } else {
      if (paramSampleModel.getNumBands() <= 4 && paramSampleModel instanceof SinglePixelPackedSampleModel) {
        SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
        int[] arrayOfInt1 = singlePixelPackedSampleModel.getBitMasks();
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = arrayOfInt1.length;
        if (i1 <= 2) {
          j = k = m = arrayOfInt1[0];
          if (i1 == 2)
            n = arrayOfInt1[1]; 
        } else {
          j = arrayOfInt1[0];
          k = arrayOfInt1[1];
          m = arrayOfInt1[2];
          if (i1 == 4)
            n = arrayOfInt1[3]; 
        } 
        int i2 = 0;
        for (byte b = 0; b < arrayOfInt.length; b++)
          i2 += arrayOfInt[b]; 
        return new DirectColorModel(i2, j, k, m, n);
      } 
      if (paramSampleModel instanceof MultiPixelPackedSampleModel) {
        int j = arrayOfInt[0];
        int k = 1 << j;
        byte[] arrayOfByte = new byte[k];
        for (byte b = 0; b < k; b++)
          arrayOfByte[b] = (byte)(b * 'ÿ' / (k - 1)); 
        indexColorModel = new IndexColorModel(j, k, arrayOfByte, arrayOfByte, arrayOfByte);
      } 
    } 
    return indexColorModel;
  }
  
  public static byte[] getPackedBinaryData(Raster paramRaster, Rectangle paramRectangle) {
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (!isBinary(sampleModel))
      throw new IllegalArgumentException(I18N.getString("ImageUtil0")); 
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    DataBuffer dataBuffer = paramRaster.getDataBuffer();
    int n = i - paramRaster.getSampleModelTranslateX();
    int i1 = j - paramRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
    int i2 = multiPixelPackedSampleModel.getScanlineStride();
    int i3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = multiPixelPackedSampleModel.getBitOffset(n);
    int i5 = (k + 7) / 8;
    if (dataBuffer instanceof DataBufferByte && i3 == 0 && i4 == 0 && i5 == i2 && ((DataBufferByte)dataBuffer).getData().length == i5 * m)
      return ((DataBufferByte)dataBuffer).getData(); 
    byte[] arrayOfByte = new byte[i5 * m];
    byte b = 0;
    if (i4 == 0) {
      if (dataBuffer instanceof DataBufferByte) {
        byte[] arrayOfByte1 = ((DataBufferByte)dataBuffer).getData();
        int i6 = i5;
        int i7 = 0;
        for (byte b1 = 0; b1 < m; b1++) {
          System.arraycopy(arrayOfByte1, i3, arrayOfByte, i7, i6);
          i7 += i6;
          i3 += i2;
        } 
      } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
        short[] arrayOfShort = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
        for (byte b1 = 0; b1 < m; b1++) {
          int i6 = k;
          int i7 = i3;
          while (i6 > 8) {
            short s = arrayOfShort[i7++];
            arrayOfByte[b++] = (byte)(s >>> 8 & 0xFF);
            arrayOfByte[b++] = (byte)(s & 0xFF);
            i6 -= 16;
          } 
          if (i6 > 0)
            arrayOfByte[b++] = (byte)(arrayOfShort[i7] >>> 8 & 0xFF); 
          i3 += i2;
        } 
      } else if (dataBuffer instanceof DataBufferInt) {
        int[] arrayOfInt = ((DataBufferInt)dataBuffer).getData();
        for (byte b1 = 0; b1 < m; b1++) {
          int i6 = k;
          int i7 = i3;
          while (i6 > 24) {
            int i9 = arrayOfInt[i7++];
            arrayOfByte[b++] = (byte)(i9 >>> 24 & 0xFF);
            arrayOfByte[b++] = (byte)(i9 >>> 16 & 0xFF);
            arrayOfByte[b++] = (byte)(i9 >>> 8 & 0xFF);
            arrayOfByte[b++] = (byte)(i9 & 0xFF);
            i6 -= 32;
          } 
          int i8 = 24;
          while (i6 > 0) {
            arrayOfByte[b++] = (byte)(arrayOfInt[i7] >>> i8 & 0xFF);
            i8 -= 8;
            i6 -= 8;
          } 
          i3 += i2;
        } 
      } 
    } else if (dataBuffer instanceof DataBufferByte) {
      byte[] arrayOfByte1 = ((DataBufferByte)dataBuffer).getData();
      if ((i4 & 0x7) == 0) {
        int i6 = i5;
        int i7 = 0;
        for (byte b1 = 0; b1 < m; b1++) {
          System.arraycopy(arrayOfByte1, i3, arrayOfByte, i7, i6);
          i7 += i6;
          i3 += i2;
        } 
      } else {
        int i6 = i4 & 0x7;
        int i7 = 8 - i6;
        for (byte b1 = 0; b1 < m; b1++) {
          int i8 = i3;
          for (int i9 = k; i9 > 0; i9 -= 8) {
            if (i9 > i7) {
              arrayOfByte[b++] = (byte)((arrayOfByte1[i8++] & 0xFF) << i6 | (arrayOfByte1[i8] & 0xFF) >>> i7);
            } else {
              arrayOfByte[b++] = (byte)((arrayOfByte1[i8] & 0xFF) << i6);
            } 
          } 
          i3 += i2;
        } 
      } 
    } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
      short[] arrayOfShort = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
      for (byte b1 = 0; b1 < m; b1++) {
        int i6 = i4;
        boolean bool = false;
        while (bool < k) {
          int i7 = i3 + i6 / 16;
          int i8 = i6 % 16;
          short s = arrayOfShort[i7] & 0xFFFF;
          if (i8 <= 8) {
            arrayOfByte[b++] = (byte)(s >>> 8 - i8);
          } else {
            int i9 = i8 - 8;
            short s1 = arrayOfShort[i7 + 1] & 0xFFFF;
            arrayOfByte[b++] = (byte)(s << i9 | s1 >>> 16 - i9);
          } 
          bool += true;
          i6 += 8;
        } 
        i3 += i2;
      } 
    } else if (dataBuffer instanceof DataBufferInt) {
      int[] arrayOfInt = ((DataBufferInt)dataBuffer).getData();
      for (byte b1 = 0; b1 < m; b1++) {
        int i6 = i4;
        boolean bool = false;
        while (bool < k) {
          int i7 = i3 + i6 / 32;
          int i8 = i6 % 32;
          int i9 = arrayOfInt[i7];
          if (i8 <= 24) {
            arrayOfByte[b++] = (byte)(i9 >>> 24 - i8);
          } else {
            int i10 = i8 - 24;
            int i11 = arrayOfInt[i7 + 1];
            arrayOfByte[b++] = (byte)(i9 << i10 | i11 >>> 32 - i10);
          } 
          bool += true;
          i6 += 8;
        } 
        i3 += i2;
      } 
    } 
    return arrayOfByte;
  }
  
  public static byte[] getUnpackedBinaryData(Raster paramRaster, Rectangle paramRectangle) {
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (!isBinary(sampleModel))
      throw new IllegalArgumentException(I18N.getString("ImageUtil0")); 
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    DataBuffer dataBuffer = paramRaster.getDataBuffer();
    int n = i - paramRaster.getSampleModelTranslateX();
    int i1 = j - paramRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
    int i2 = multiPixelPackedSampleModel.getScanlineStride();
    int i3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = multiPixelPackedSampleModel.getBitOffset(n);
    byte[] arrayOfByte = new byte[k * m];
    int i5 = j + m;
    int i6 = i + k;
    byte b = 0;
    if (dataBuffer instanceof DataBufferByte) {
      byte[] arrayOfByte1 = ((DataBufferByte)dataBuffer).getData();
      for (int i7 = j; i7 < i5; i7++) {
        int i8 = i3 * 8 + i4;
        for (int i9 = i; i9 < i6; i9++) {
          byte b1 = arrayOfByte1[i8 / 8];
          arrayOfByte[b++] = (byte)(b1 >>> (7 - i8 & 0x7) & true);
          i8++;
        } 
        i3 += i2;
      } 
    } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
      short[] arrayOfShort = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
      for (int i7 = j; i7 < i5; i7++) {
        int i8 = i3 * 16 + i4;
        for (int i9 = i; i9 < i6; i9++) {
          short s = arrayOfShort[i8 / 16];
          arrayOfByte[b++] = (byte)(s >>> 15 - i8 % 16 & true);
          i8++;
        } 
        i3 += i2;
      } 
    } else if (dataBuffer instanceof DataBufferInt) {
      int[] arrayOfInt = ((DataBufferInt)dataBuffer).getData();
      for (int i7 = j; i7 < i5; i7++) {
        int i8 = i3 * 32 + i4;
        for (int i9 = i; i9 < i6; i9++) {
          int i10 = arrayOfInt[i8 / 32];
          arrayOfByte[b++] = (byte)(i10 >>> 31 - i8 % 32 & true);
          i8++;
        } 
        i3 += i2;
      } 
    } 
    return arrayOfByte;
  }
  
  public static void setPackedBinaryData(byte[] paramArrayOfByte, WritableRaster paramWritableRaster, Rectangle paramRectangle) {
    SampleModel sampleModel = paramWritableRaster.getSampleModel();
    if (!isBinary(sampleModel))
      throw new IllegalArgumentException(I18N.getString("ImageUtil0")); 
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    DataBuffer dataBuffer = paramWritableRaster.getDataBuffer();
    int n = i - paramWritableRaster.getSampleModelTranslateX();
    int i1 = j - paramWritableRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
    int i2 = multiPixelPackedSampleModel.getScanlineStride();
    int i3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = multiPixelPackedSampleModel.getBitOffset(n);
    byte b = 0;
    if (i4 == 0) {
      if (dataBuffer instanceof DataBufferByte) {
        byte[] arrayOfByte = ((DataBufferByte)dataBuffer).getData();
        if (arrayOfByte == paramArrayOfByte)
          return; 
        int i5 = (k + 7) / 8;
        int i6 = 0;
        for (byte b1 = 0; b1 < m; b1++) {
          System.arraycopy(paramArrayOfByte, i6, arrayOfByte, i3, i5);
          i6 += i5;
          i3 += i2;
        } 
      } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
        short[] arrayOfShort = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
        for (byte b1 = 0; b1 < m; b1++) {
          int i5 = k;
          int i6 = i3;
          while (i5 > 8) {
            arrayOfShort[i6++] = (short)((paramArrayOfByte[b++] & 0xFF) << 8 | paramArrayOfByte[b++] & 0xFF);
            i5 -= 16;
          } 
          if (i5 > 0)
            arrayOfShort[i6++] = (short)((paramArrayOfByte[b++] & 0xFF) << 8); 
          i3 += i2;
        } 
      } else if (dataBuffer instanceof DataBufferInt) {
        int[] arrayOfInt = ((DataBufferInt)dataBuffer).getData();
        for (byte b1 = 0; b1 < m; b1++) {
          int i5 = k;
          int i6 = i3;
          while (i5 > 24) {
            arrayOfInt[i6++] = (paramArrayOfByte[b++] & 0xFF) << 24 | (paramArrayOfByte[b++] & 0xFF) << 16 | (paramArrayOfByte[b++] & 0xFF) << 8 | paramArrayOfByte[b++] & 0xFF;
            i5 -= 32;
          } 
          byte b2 = 24;
          while (i5 > 0) {
            arrayOfInt[i6] = arrayOfInt[i6] | (paramArrayOfByte[b++] & 0xFF) << b2;
            b2 -= 8;
            i5 -= 8;
          } 
          i3 += i2;
        } 
      } 
    } else {
      int i5 = (k + 7) / 8;
      int i6 = 0;
      if (dataBuffer instanceof DataBufferByte) {
        byte[] arrayOfByte = ((DataBufferByte)dataBuffer).getData();
        if ((i4 & 0x7) == 0) {
          for (byte b1 = 0; b1 < m; b1++) {
            System.arraycopy(paramArrayOfByte, i6, arrayOfByte, i3, i5);
            i6 += i5;
            i3 += i2;
          } 
        } else {
          int i7 = i4 & 0x7;
          int i8 = 8 - i7;
          int i9 = 8 + i8;
          byte b1 = (byte)(255 << i8);
          byte b2 = (byte)(b1 ^ 0xFFFFFFFF);
          for (byte b3 = 0; b3 < m; b3++) {
            int i10 = i3;
            for (int i11 = k; i11 > 0; i11 -= 8) {
              byte b4 = paramArrayOfByte[b++];
              if (i11 > i9) {
                arrayOfByte[i10] = (byte)(arrayOfByte[i10] & b1 | (b4 & 0xFF) >>> i7);
                arrayOfByte[++i10] = (byte)((b4 & 0xFF) << i8);
              } else if (i11 > i8) {
                arrayOfByte[i10] = (byte)(arrayOfByte[i10] & b1 | (b4 & 0xFF) >>> i7);
                arrayOfByte[++i10] = (byte)(arrayOfByte[i10] & b2 | (b4 & 0xFF) << i8);
              } else {
                int i12 = (1 << i8 - i11) - 1;
                arrayOfByte[i10] = (byte)(arrayOfByte[i10] & (b1 | i12) | (b4 & 0xFF) >>> i7 & (i12 ^ 0xFFFFFFFF));
              } 
            } 
            i3 += i2;
          } 
        } 
      } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
        short[] arrayOfShort = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
        int i7 = i4 & 0x7;
        int i8 = 8 - i7;
        int i9 = 16 + i8;
        short s1 = (short)(255 << i8 ^ 0xFFFFFFFF);
        short s2 = (short)(65535 << i8);
        short s3 = (short)(s2 ^ 0xFFFFFFFF);
        for (byte b1 = 0; b1 < m; b1++) {
          int i10 = i4;
          int i11 = k;
          boolean bool = false;
          while (bool < k) {
            int i12 = i3 + (i10 >> 4);
            int i13 = i10 & 0xF;
            byte b2 = paramArrayOfByte[b++] & 0xFF;
            if (i13 <= 8) {
              if (i11 < 8)
                b2 &= 255 << 8 - i11; 
              arrayOfShort[i12] = (short)(arrayOfShort[i12] & s1 | b2 << i8);
            } else if (i11 > i9) {
              arrayOfShort[i12] = (short)(arrayOfShort[i12] & s2 | b2 >>> i7 & 0xFFFF);
              arrayOfShort[++i12] = (short)(b2 << i8 & 0xFFFF);
            } else if (i11 > i8) {
              arrayOfShort[i12] = (short)(arrayOfShort[i12] & s2 | b2 >>> i7 & 0xFFFF);
              arrayOfShort[++i12] = (short)(arrayOfShort[i12] & s3 | b2 << i8 & 0xFFFF);
            } else {
              int i14 = (1 << i8 - i11) - 1;
              arrayOfShort[i12] = (short)(arrayOfShort[i12] & (s2 | i14) | b2 >>> i7 & 0xFFFF & (i14 ^ 0xFFFFFFFF));
            } 
            bool += true;
            i10 += 8;
            i11 -= 8;
          } 
          i3 += i2;
        } 
      } else if (dataBuffer instanceof DataBufferInt) {
        int[] arrayOfInt = ((DataBufferInt)dataBuffer).getData();
        int i7 = i4 & 0x7;
        int i8 = 8 - i7;
        int i9 = 32 + i8;
        int i10 = -1 << i8;
        int i11 = i10 ^ 0xFFFFFFFF;
        for (byte b1 = 0; b1 < m; b1++) {
          int i12 = i4;
          int i13 = k;
          boolean bool = false;
          while (bool < k) {
            int i14 = i3 + (i12 >> 5);
            int i15 = i12 & 0x1F;
            byte b2 = paramArrayOfByte[b++] & 0xFF;
            if (i15 <= 24) {
              int i16 = 24 - i15;
              if (i13 < 8)
                b2 &= 255 << 8 - i13; 
              arrayOfInt[i14] = arrayOfInt[i14] & (255 << i16 ^ 0xFFFFFFFF) | b2 << i16;
            } else if (i13 > i9) {
              arrayOfInt[i14] = arrayOfInt[i14] & i10 | b2 >>> i7;
              arrayOfInt[++i14] = b2 << i8;
            } else if (i13 > i8) {
              arrayOfInt[i14] = arrayOfInt[i14] & i10 | b2 >>> i7;
              arrayOfInt[++i14] = arrayOfInt[i14] & i11 | b2 << i8;
            } else {
              int i16 = (1 << i8 - i13) - 1;
              arrayOfInt[i14] = arrayOfInt[i14] & (i10 | i16) | b2 >>> i7 & (i16 ^ 0xFFFFFFFF);
            } 
            bool += true;
            i12 += 8;
            i13 -= 8;
          } 
          i3 += i2;
        } 
      } 
    } 
  }
  
  public static void setUnpackedBinaryData(byte[] paramArrayOfByte, WritableRaster paramWritableRaster, Rectangle paramRectangle) {
    SampleModel sampleModel = paramWritableRaster.getSampleModel();
    if (!isBinary(sampleModel))
      throw new IllegalArgumentException(I18N.getString("ImageUtil0")); 
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    DataBuffer dataBuffer = paramWritableRaster.getDataBuffer();
    int n = i - paramWritableRaster.getSampleModelTranslateX();
    int i1 = j - paramWritableRaster.getSampleModelTranslateY();
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
    int i2 = multiPixelPackedSampleModel.getScanlineStride();
    int i3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, i1);
    int i4 = multiPixelPackedSampleModel.getBitOffset(n);
    byte b = 0;
    if (dataBuffer instanceof DataBufferByte) {
      byte[] arrayOfByte = ((DataBufferByte)dataBuffer).getData();
      for (byte b1 = 0; b1 < m; b1++) {
        int i5 = i3 * 8 + i4;
        for (byte b2 = 0; b2 < k; b2++) {
          if (paramArrayOfByte[b++] != 0)
            arrayOfByte[i5 / 8] = (byte)(arrayOfByte[i5 / 8] | (byte)(1 << (7 - i5 & 0x7))); 
          i5++;
        } 
        i3 += i2;
      } 
    } else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
      short[] arrayOfShort = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
      for (byte b1 = 0; b1 < m; b1++) {
        int i5 = i3 * 16 + i4;
        for (byte b2 = 0; b2 < k; b2++) {
          if (paramArrayOfByte[b++] != 0)
            arrayOfShort[i5 / 16] = (short)(arrayOfShort[i5 / 16] | (short)(1 << 15 - i5 % 16)); 
          i5++;
        } 
        i3 += i2;
      } 
    } else if (dataBuffer instanceof DataBufferInt) {
      int[] arrayOfInt = ((DataBufferInt)dataBuffer).getData();
      for (byte b1 = 0; b1 < m; b1++) {
        int i5 = i3 * 32 + i4;
        for (byte b2 = 0; b2 < k; b2++) {
          if (paramArrayOfByte[b++] != 0)
            arrayOfInt[i5 / 32] = arrayOfInt[i5 / 32] | 1 << 31 - i5 % 32; 
          i5++;
        } 
        i3 += i2;
      } 
    } 
  }
  
  public static boolean isBinary(SampleModel paramSampleModel) { return (paramSampleModel instanceof MultiPixelPackedSampleModel && ((MultiPixelPackedSampleModel)paramSampleModel).getPixelBitStride() == 1 && paramSampleModel.getNumBands() == 1); }
  
  public static ColorModel createColorModel(ColorSpace paramColorSpace, SampleModel paramSampleModel) {
    IndexColorModel indexColorModel = null;
    if (paramSampleModel == null)
      throw new IllegalArgumentException(I18N.getString("ImageUtil1")); 
    int i = paramSampleModel.getNumBands();
    if (i < 1 || i > 4)
      return null; 
    int j = paramSampleModel.getDataType();
    if (paramSampleModel instanceof ComponentSampleModel) {
      if (j < 0 || j > 5)
        return null; 
      if (paramColorSpace == null)
        paramColorSpace = (i <= 2) ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000); 
      boolean bool1 = (i == 2 || i == 4);
      byte b1 = bool1 ? 3 : 1;
      boolean bool2 = false;
      int k = DataBuffer.getDataTypeSize(j);
      int[] arrayOfInt = new int[i];
      for (byte b2 = 0; b2 < i; b2++)
        arrayOfInt[b2] = k; 
      indexColorModel = new ComponentColorModel(paramColorSpace, arrayOfInt, bool1, bool2, b1, j);
    } else if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      int[] arrayOfInt1 = singlePixelPackedSampleModel.getBitMasks();
      int k = 0;
      int m = 0;
      int n = 0;
      int i1 = 0;
      i = arrayOfInt1.length;
      if (i <= 2) {
        k = m = n = arrayOfInt1[0];
        if (i == 2)
          i1 = arrayOfInt1[1]; 
      } else {
        k = arrayOfInt1[0];
        m = arrayOfInt1[1];
        n = arrayOfInt1[2];
        if (i == 4)
          i1 = arrayOfInt1[3]; 
      } 
      int[] arrayOfInt2 = singlePixelPackedSampleModel.getSampleSize();
      int i2 = 0;
      for (byte b = 0; b < arrayOfInt2.length; b++)
        i2 += arrayOfInt2[b]; 
      if (paramColorSpace == null)
        paramColorSpace = ColorSpace.getInstance(1000); 
      DirectColorModel directColorModel = new DirectColorModel(paramColorSpace, i2, k, m, n, i1, false, paramSampleModel.getDataType());
    } else if (paramSampleModel instanceof MultiPixelPackedSampleModel) {
      int k = ((MultiPixelPackedSampleModel)paramSampleModel).getPixelBitStride();
      int m = 1 << k;
      byte[] arrayOfByte = new byte[m];
      for (byte b = 0; b < m; b++)
        arrayOfByte[b] = (byte)('ÿ' * b / (m - 1)); 
      indexColorModel = new IndexColorModel(k, m, arrayOfByte, arrayOfByte, arrayOfByte);
    } 
    return indexColorModel;
  }
  
  public static int getElementSize(SampleModel paramSampleModel) {
    int i = DataBuffer.getDataTypeSize(paramSampleModel.getDataType());
    if (paramSampleModel instanceof MultiPixelPackedSampleModel) {
      MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)paramSampleModel;
      return multiPixelPackedSampleModel.getSampleSize(0) * multiPixelPackedSampleModel.getNumBands();
    } 
    return (paramSampleModel instanceof ComponentSampleModel) ? (paramSampleModel.getNumBands() * i) : ((paramSampleModel instanceof SinglePixelPackedSampleModel) ? i : (i * paramSampleModel.getNumBands()));
  }
  
  public static long getTileSize(SampleModel paramSampleModel) {
    int i = DataBuffer.getDataTypeSize(paramSampleModel.getDataType());
    if (paramSampleModel instanceof MultiPixelPackedSampleModel) {
      MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)paramSampleModel;
      return ((multiPixelPackedSampleModel.getScanlineStride() * multiPixelPackedSampleModel.getHeight() + (multiPixelPackedSampleModel.getDataBitOffset() + i - 1) / i) * (i + 7) / 8);
    } 
    if (paramSampleModel instanceof ComponentSampleModel) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramSampleModel;
      int[] arrayOfInt1 = componentSampleModel.getBandOffsets();
      int j = arrayOfInt1[0];
      for (byte b1 = 1; b1 < arrayOfInt1.length; b1++)
        j = Math.max(j, arrayOfInt1[b1]); 
      long l = 0L;
      int k = componentSampleModel.getPixelStride();
      int m = componentSampleModel.getScanlineStride();
      if (j >= 0)
        l += (j + 1); 
      if (k > 0)
        l += (k * (paramSampleModel.getWidth() - 1)); 
      if (m > 0)
        l += (m * (paramSampleModel.getHeight() - 1)); 
      int[] arrayOfInt2 = componentSampleModel.getBankIndices();
      j = arrayOfInt2[0];
      for (byte b2 = 1; b2 < arrayOfInt2.length; b2++)
        j = Math.max(j, arrayOfInt2[b2]); 
      return l * (j + 1) * ((i + 7) / 8);
    } 
    if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      long l = (singlePixelPackedSampleModel.getScanlineStride() * (singlePixelPackedSampleModel.getHeight() - 1) + singlePixelPackedSampleModel.getWidth());
      return l * ((i + 7) / 8);
    } 
    return 0L;
  }
  
  public static long getBandSize(SampleModel paramSampleModel) {
    int i = DataBuffer.getDataTypeSize(paramSampleModel.getDataType());
    if (paramSampleModel instanceof ComponentSampleModel) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)paramSampleModel;
      int j = componentSampleModel.getPixelStride();
      int k = componentSampleModel.getScanlineStride();
      long l = Math.min(j, k);
      if (j > 0)
        l += (j * (paramSampleModel.getWidth() - 1)); 
      if (k > 0)
        l += (k * (paramSampleModel.getHeight() - 1)); 
      return l * ((i + 7) / 8);
    } 
    return getTileSize(paramSampleModel);
  }
  
  public static boolean isIndicesForGrayscale(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    if (paramArrayOfByte1.length != paramArrayOfByte2.length || paramArrayOfByte1.length != paramArrayOfByte3.length)
      return false; 
    int i = paramArrayOfByte1.length;
    if (i != 256)
      return false; 
    for (byte b = 0; b < i; b++) {
      byte b1 = (byte)b;
      if (paramArrayOfByte1[b] != b1 || paramArrayOfByte2[b] != b1 || paramArrayOfByte3[b] != b1)
        return false; 
    } 
    return true;
  }
  
  public static String convertObjectToString(Object paramObject) {
    if (paramObject == null)
      return ""; 
    String str = "";
    if (paramObject instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject;
      for (byte b = 0; b < arrayOfByte.length; b++)
        str = str + arrayOfByte[b] + " "; 
      return str;
    } 
    if (paramObject instanceof int[]) {
      int[] arrayOfInt = (int[])paramObject;
      for (byte b = 0; b < arrayOfInt.length; b++)
        str = str + arrayOfInt[b] + " "; 
      return str;
    } 
    if (paramObject instanceof short[]) {
      short[] arrayOfShort = (short[])paramObject;
      for (byte b = 0; b < arrayOfShort.length; b++)
        str = str + arrayOfShort[b] + " "; 
      return str;
    } 
    return paramObject.toString();
  }
  
  public static final void canEncodeImage(ImageWriter paramImageWriter, ImageTypeSpecifier paramImageTypeSpecifier) throws IIOException {
    ImageWriterSpi imageWriterSpi = paramImageWriter.getOriginatingProvider();
    if (paramImageTypeSpecifier != null && imageWriterSpi != null && !imageWriterSpi.canEncodeImage(paramImageTypeSpecifier))
      throw new IIOException(I18N.getString("ImageUtil2") + " " + paramImageWriter.getClass().getName()); 
  }
  
  public static final void canEncodeImage(ImageWriter paramImageWriter, ColorModel paramColorModel, SampleModel paramSampleModel) throws IIOException {
    ImageTypeSpecifier imageTypeSpecifier = null;
    if (paramColorModel != null && paramSampleModel != null)
      imageTypeSpecifier = new ImageTypeSpecifier(paramColorModel, paramSampleModel); 
    canEncodeImage(paramImageWriter, imageTypeSpecifier);
  }
  
  public static final boolean imageIsContiguous(RenderedImage paramRenderedImage) {
    SampleModel sampleModel;
    if (paramRenderedImage instanceof BufferedImage) {
      WritableRaster writableRaster = ((BufferedImage)paramRenderedImage).getRaster();
      sampleModel = writableRaster.getSampleModel();
    } else {
      sampleModel = paramRenderedImage.getSampleModel();
    } 
    if (sampleModel instanceof ComponentSampleModel) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
      if (componentSampleModel.getPixelStride() != componentSampleModel.getNumBands())
        return false; 
      int[] arrayOfInt1 = componentSampleModel.getBandOffsets();
      for (byte b1 = 0; b1 < arrayOfInt1.length; b1++) {
        if (arrayOfInt1[b1] != b1)
          return false; 
      } 
      int[] arrayOfInt2 = componentSampleModel.getBankIndices();
      for (byte b2 = 0; b2 < arrayOfInt1.length; b2++) {
        if (arrayOfInt2[b2] != 0)
          return false; 
      } 
      return true;
    } 
    return isBinary(sampleModel);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\common\ImageUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */