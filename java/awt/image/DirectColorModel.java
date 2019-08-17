package java.awt.image;

import java.awt.color.ColorSpace;
import java.util.Arrays;

public class DirectColorModel extends PackedColorModel {
  private int red_mask;
  
  private int green_mask;
  
  private int blue_mask;
  
  private int alpha_mask;
  
  private int red_offset;
  
  private int green_offset;
  
  private int blue_offset;
  
  private int alpha_offset;
  
  private int red_scale;
  
  private int green_scale;
  
  private int blue_scale;
  
  private int alpha_scale;
  
  private boolean is_LinearRGB;
  
  private int lRGBprecision;
  
  private byte[] tosRGB8LUT;
  
  private byte[] fromsRGB8LUT8;
  
  private short[] fromsRGB8LUT16;
  
  public DirectColorModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(paramInt1, paramInt2, paramInt3, paramInt4, 0); }
  
  public DirectColorModel(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(ColorSpace.getInstance(1000), paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, false, (paramInt5 == 0) ? 1 : 3, ColorModel.getDefaultTransferType(paramInt1));
    setFields();
  }
  
  public DirectColorModel(ColorSpace paramColorSpace, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6) {
    super(paramColorSpace, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean, (paramInt5 == 0) ? 1 : 3, paramInt6);
    if (ColorModel.isLinearRGBspace(this.colorSpace)) {
      this.is_LinearRGB = true;
      if (this.maxBits <= 8) {
        this.lRGBprecision = 8;
        this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
        this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
      } else {
        this.lRGBprecision = 16;
        this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
        this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      } 
    } else if (!this.is_sRGB) {
      for (byte b = 0; b < 3; b++) {
        if (paramColorSpace.getMinValue(b) != 0.0F || paramColorSpace.getMaxValue(b) != 1.0F)
          throw new IllegalArgumentException("Illegal min/max RGB component value"); 
      } 
    } 
    setFields();
  }
  
  public final int getRedMask() { return this.maskArray[0]; }
  
  public final int getGreenMask() { return this.maskArray[1]; }
  
  public final int getBlueMask() { return this.maskArray[2]; }
  
  public final int getAlphaMask() { return this.supportsAlpha ? this.maskArray[3] : 0; }
  
  private float[] getDefaultRGBComponents(int paramInt) {
    int[] arrayOfInt = getComponents(paramInt, null, 0);
    float[] arrayOfFloat = getNormalizedComponents(arrayOfInt, 0, null, 0);
    return this.colorSpace.toRGB(arrayOfFloat);
  }
  
  private int getsRGBComponentFromsRGB(int paramInt1, int paramInt2) {
    int i = (paramInt1 & this.maskArray[paramInt2]) >>> this.maskOffsets[paramInt2];
    if (this.isAlphaPremultiplied) {
      int j = (paramInt1 & this.maskArray[3]) >>> this.maskOffsets[3];
      i = (j == 0) ? 0 : (int)(i * this.scaleFactors[paramInt2] * 255.0F / j * this.scaleFactors[3] + 0.5F);
    } else if (this.scaleFactors[paramInt2] != 1.0F) {
      i = (int)(i * this.scaleFactors[paramInt2] + 0.5F);
    } 
    return i;
  }
  
  private int getsRGBComponentFromLinearRGB(int paramInt1, int paramInt2) {
    int i = (paramInt1 & this.maskArray[paramInt2]) >>> this.maskOffsets[paramInt2];
    if (this.isAlphaPremultiplied) {
      float f = ((1 << this.lRGBprecision) - 1);
      int j = (paramInt1 & this.maskArray[3]) >>> this.maskOffsets[3];
      i = (j == 0) ? 0 : (int)(i * this.scaleFactors[paramInt2] * f / j * this.scaleFactors[3] + 0.5F);
    } else if (this.nBits[paramInt2] != this.lRGBprecision) {
      if (this.lRGBprecision == 16) {
        i = (int)(i * this.scaleFactors[paramInt2] * 257.0F + 0.5F);
      } else {
        i = (int)(i * this.scaleFactors[paramInt2] + 0.5F);
      } 
    } 
    return this.tosRGB8LUT[i] & 0xFF;
  }
  
  public final int getRed(int paramInt) {
    if (this.is_sRGB)
      return getsRGBComponentFromsRGB(paramInt, 0); 
    if (this.is_LinearRGB)
      return getsRGBComponentFromLinearRGB(paramInt, 0); 
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return (int)(arrayOfFloat[0] * 255.0F + 0.5F);
  }
  
  public final int getGreen(int paramInt) {
    if (this.is_sRGB)
      return getsRGBComponentFromsRGB(paramInt, 1); 
    if (this.is_LinearRGB)
      return getsRGBComponentFromLinearRGB(paramInt, 1); 
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return (int)(arrayOfFloat[1] * 255.0F + 0.5F);
  }
  
  public final int getBlue(int paramInt) {
    if (this.is_sRGB)
      return getsRGBComponentFromsRGB(paramInt, 2); 
    if (this.is_LinearRGB)
      return getsRGBComponentFromLinearRGB(paramInt, 2); 
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return (int)(arrayOfFloat[2] * 255.0F + 0.5F);
  }
  
  public final int getAlpha(int paramInt) {
    if (!this.supportsAlpha)
      return 255; 
    int i = (paramInt & this.maskArray[3]) >>> this.maskOffsets[3];
    if (this.scaleFactors[3] != 1.0F)
      i = (int)(i * this.scaleFactors[3] + 0.5F); 
    return i;
  }
  
  public final int getRGB(int paramInt) {
    if (this.is_sRGB || this.is_LinearRGB)
      return getAlpha(paramInt) << 24 | getRed(paramInt) << 16 | getGreen(paramInt) << 8 | getBlue(paramInt) << 0; 
    float[] arrayOfFloat = getDefaultRGBComponents(paramInt);
    return getAlpha(paramInt) << 24 | (int)(arrayOfFloat[0] * 255.0F + 0.5F) << 16 | (int)(arrayOfFloat[1] * 255.0F + 0.5F) << 8 | (int)(arrayOfFloat[2] * 255.0F + 0.5F) << 0;
  }
  
  public int getRed(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        return getRed(i);
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        return getRed(i);
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        return getRed(i);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getGreen(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        return getGreen(i);
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        return getGreen(i);
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        return getGreen(i);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getBlue(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        return getBlue(i);
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        return getBlue(i);
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        return getBlue(i);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getAlpha(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        return getAlpha(i);
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        return getAlpha(i);
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        return getAlpha(i);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getRGB(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        return getRGB(i);
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        return getRGB(i);
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        return getRGB(i);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public Object getDataElements(int paramInt, Object paramObject) {
    short[] arrayOfShort;
    int[] arrayOfInt = null;
    if (this.transferType == 3 && paramObject != null) {
      arrayOfInt = (int[])paramObject;
      arrayOfInt[0] = 0;
    } else {
      arrayOfInt = new int[1];
    } 
    ColorModel colorModel = ColorModel.getRGBdefault();
    if (this == colorModel || equals(colorModel)) {
      arrayOfInt[0] = paramInt;
      return arrayOfInt;
    } 
    int i = paramInt >> 16 & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt & 0xFF;
    if (this.is_sRGB || this.is_LinearRGB) {
      float f;
      byte b;
      if (this.is_LinearRGB) {
        if (this.lRGBprecision == 8) {
          i = this.fromsRGB8LUT8[i] & 0xFF;
          j = this.fromsRGB8LUT8[j] & 0xFF;
          k = this.fromsRGB8LUT8[k] & 0xFF;
          b = 8;
          f = 0.003921569F;
        } else {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          b = 16;
          f = 1.5259022E-5F;
        } 
      } else {
        b = 8;
        f = 0.003921569F;
      } 
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.isAlphaPremultiplied) {
          f *= m * 0.003921569F;
          b = -1;
        } 
        if (this.nBits[3] != 8) {
          m = (int)(m * 0.003921569F * ((1 << this.nBits[3]) - 1) + 0.5F);
          if (m > (1 << this.nBits[3]) - 1)
            m = (1 << this.nBits[3]) - 1; 
        } 
        arrayOfInt[0] = m << this.maskOffsets[3];
      } 
      if (this.nBits[0] != b)
        i = (int)(i * f * ((1 << this.nBits[0]) - 1) + 0.5F); 
      if (this.nBits[1] != b)
        j = (int)(j * f * ((1 << this.nBits[1]) - 1) + 0.5F); 
      if (this.nBits[2] != b)
        k = (int)(k * f * ((1 << this.nBits[2]) - 1) + 0.5F); 
    } else {
      arrayOfShort = new float[3];
      float f = 0.003921569F;
      arrayOfShort[0] = i * f;
      arrayOfShort[1] = j * f;
      arrayOfShort[2] = k * f;
      arrayOfShort = this.colorSpace.fromRGB(arrayOfShort);
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.isAlphaPremultiplied) {
          f *= m;
          for (byte b = 0; b < 3; b++)
            arrayOfShort[b] = arrayOfShort[b] * f; 
        } 
        if (this.nBits[3] != 8) {
          m = (int)(m * 0.003921569F * ((1 << this.nBits[3]) - 1) + 0.5F);
          if (m > (1 << this.nBits[3]) - 1)
            m = (1 << this.nBits[3]) - 1; 
        } 
        arrayOfInt[0] = m << this.maskOffsets[3];
      } 
      i = (int)(arrayOfShort[0] * ((1 << this.nBits[0]) - 1) + 0.5F);
      j = (int)(arrayOfShort[1] * ((1 << this.nBits[1]) - 1) + 0.5F);
      k = (int)(arrayOfShort[2] * ((1 << this.nBits[2]) - 1) + 0.5F);
    } 
    if (this.maxBits > 23) {
      if (i > (1 << this.nBits[0]) - 1)
        i = (1 << this.nBits[0]) - 1; 
      if (j > (1 << this.nBits[1]) - 1)
        j = (1 << this.nBits[1]) - 1; 
      if (k > (1 << this.nBits[2]) - 1)
        k = (1 << this.nBits[2]) - 1; 
    } 
    arrayOfInt[0] = arrayOfInt[0] | i << this.maskOffsets[0] | j << this.maskOffsets[1] | k << this.maskOffsets[2];
    switch (this.transferType) {
      case 0:
        if (paramObject == null) {
          arrayOfShort = new byte[1];
        } else {
          arrayOfShort = (byte[])paramObject;
        } 
        arrayOfShort[0] = (byte)(0xFF & arrayOfInt[0]);
        return arrayOfShort;
      case 1:
        if (paramObject == null) {
          short[] arrayOfShort1 = new short[1];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        arrayOfShort[0] = (short)(arrayOfInt[0] & 0xFFFF);
        return arrayOfShort;
      case 3:
        return arrayOfInt;
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public final int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt2 + this.numComponents]; 
    for (int i = 0; i < this.numComponents; i++)
      paramArrayOfInt[paramInt2 + i] = (paramInt1 & this.maskArray[i]) >>> this.maskOffsets[i]; 
    return paramArrayOfInt;
  }
  
  public final int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        return getComponents(i, paramArrayOfInt, paramInt);
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        return getComponents(i, paramArrayOfInt, paramInt);
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        return getComponents(i, paramArrayOfInt, paramInt);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public final WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    int[] arrayOfInt;
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new IllegalArgumentException("Width (" + paramInt1 + ") and height (" + paramInt2 + ") cannot be <= 0"); 
    if (this.supportsAlpha) {
      arrayOfInt = new int[4];
      arrayOfInt[3] = this.alpha_mask;
    } else {
      arrayOfInt = new int[3];
    } 
    arrayOfInt[0] = this.red_mask;
    arrayOfInt[1] = this.green_mask;
    arrayOfInt[2] = this.blue_mask;
    return (this.pixel_bits > 16) ? Raster.createPackedRaster(3, paramInt1, paramInt2, arrayOfInt, null) : ((this.pixel_bits > 8) ? Raster.createPackedRaster(1, paramInt1, paramInt2, arrayOfInt, null) : Raster.createPackedRaster(0, paramInt1, paramInt2, arrayOfInt, null));
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt) {
    int i = 0;
    for (int j = 0; j < this.numComponents; j++)
      i |= paramArrayOfInt[paramInt + j] << this.maskOffsets[j] & this.maskArray[j]; 
    return i;
  }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject) {
    int i = 0;
    for (int j = 0; j < this.numComponents; j++)
      i |= paramArrayOfInt[paramInt + j] << this.maskOffsets[j] & this.maskArray[j]; 
    switch (this.transferType) {
      case 0:
        if (paramObject instanceof byte[]) {
          byte[] arrayOfByte = (byte[])paramObject;
          arrayOfByte[0] = (byte)(i & 0xFF);
          return arrayOfByte;
        } 
        return new byte[] { (byte)(i & 0xFF) };
      case 1:
        if (paramObject instanceof short[]) {
          short[] arrayOfShort = (short[])paramObject;
          arrayOfShort[0] = (short)(i & 0xFFFF);
          return arrayOfShort;
        } 
        return new short[] { (short)(i & 0xFFFF) };
      case 3:
        if (paramObject instanceof int[]) {
          int[] arrayOfInt = (int[])paramObject;
          arrayOfInt[0] = i;
          return arrayOfInt;
        } 
        return new int[] { i };
    } 
    throw new ClassCastException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public final ColorModel coerceData(WritableRaster paramWritableRaster, boolean paramBoolean) {
    byte b;
    if (!this.supportsAlpha || isAlphaPremultiplied() == paramBoolean)
      return this; 
    int i = paramWritableRaster.getWidth();
    int j = paramWritableRaster.getHeight();
    int k = this.numColorComponents;
    float f = 1.0F / ((1 << this.nBits[k]) - 1);
    int m = paramWritableRaster.getMinX();
    int n = paramWritableRaster.getMinY();
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = null;
    if (paramBoolean) {
      byte b1;
      switch (this.transferType) {
        case 0:
          b1 = 0;
          while (b1 < j) {
            int i1 = m;
            byte b2 = 0;
            while (b2 < i) {
              arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
              float f1 = arrayOfInt1[k] * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfInt1[b3] = (int)(arrayOfInt1[b3] * f1 + 0.5F); 
                paramWritableRaster.setPixel(i1, n, arrayOfInt1);
              } else {
                if (arrayOfInt2 == null) {
                  arrayOfInt2 = new int[this.numComponents];
                  Arrays.fill(arrayOfInt2, 0);
                } 
                paramWritableRaster.setPixel(i1, n, arrayOfInt2);
              } 
              b2++;
              i1++;
            } 
            b1++;
            n++;
          } 
          return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], paramBoolean, this.transferType);
        case 1:
          b1 = 0;
          while (b1 < j) {
            int i1 = m;
            byte b2 = 0;
            while (b2 < i) {
              arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
              float f1 = arrayOfInt1[k] * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfInt1[b3] = (int)(arrayOfInt1[b3] * f1 + 0.5F); 
                paramWritableRaster.setPixel(i1, n, arrayOfInt1);
              } else {
                if (arrayOfInt2 == null) {
                  arrayOfInt2 = new int[this.numComponents];
                  Arrays.fill(arrayOfInt2, 0);
                } 
                paramWritableRaster.setPixel(i1, n, arrayOfInt2);
              } 
              b2++;
              i1++;
            } 
            b1++;
            n++;
          } 
          return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], paramBoolean, this.transferType);
        case 3:
          b1 = 0;
          while (b1 < j) {
            int i1 = m;
            byte b2 = 0;
            while (b2 < i) {
              arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
              float f1 = arrayOfInt1[k] * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfInt1[b3] = (int)(arrayOfInt1[b3] * f1 + 0.5F); 
                paramWritableRaster.setPixel(i1, n, arrayOfInt1);
              } else {
                if (arrayOfInt2 == null) {
                  arrayOfInt2 = new int[this.numComponents];
                  Arrays.fill(arrayOfInt2, 0);
                } 
                paramWritableRaster.setPixel(i1, n, arrayOfInt2);
              } 
              b2++;
              i1++;
            } 
            b1++;
            n++;
          } 
          return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], paramBoolean, this.transferType);
      } 
      throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    switch (this.transferType) {
      case 0:
        b = 0;
        while (b < j) {
          int i1 = m;
          byte b1 = 0;
          while (b1 < i) {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            float f1 = arrayOfInt1[k] * f;
            if (f1 != 0.0F) {
              float f2 = 1.0F / f1;
              for (byte b2 = 0; b2 < k; b2++)
                arrayOfInt1[b2] = (int)(arrayOfInt1[b2] * f2 + 0.5F); 
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            } 
            b1++;
            i1++;
          } 
          b++;
          n++;
        } 
        return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], paramBoolean, this.transferType);
      case 1:
        b = 0;
        while (b < j) {
          int i1 = m;
          byte b1 = 0;
          while (b1 < i) {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            float f1 = arrayOfInt1[k] * f;
            if (f1 != 0.0F) {
              float f2 = 1.0F / f1;
              for (byte b2 = 0; b2 < k; b2++)
                arrayOfInt1[b2] = (int)(arrayOfInt1[b2] * f2 + 0.5F); 
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            } 
            b1++;
            i1++;
          } 
          b++;
          n++;
        } 
        return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], paramBoolean, this.transferType);
      case 3:
        b = 0;
        while (b < j) {
          int i1 = m;
          byte b1 = 0;
          while (b1 < i) {
            arrayOfInt1 = paramWritableRaster.getPixel(i1, n, arrayOfInt1);
            float f1 = arrayOfInt1[k] * f;
            if (f1 != 0.0F) {
              float f2 = 1.0F / f1;
              for (byte b2 = 0; b2 < k; b2++)
                arrayOfInt1[b2] = (int)(arrayOfInt1[b2] * f2 + 0.5F); 
              paramWritableRaster.setPixel(i1, n, arrayOfInt1);
            } 
            b1++;
            i1++;
          } 
          b++;
          n++;
        } 
        return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], paramBoolean, this.transferType);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public boolean isCompatibleRaster(Raster paramRaster) {
    SinglePixelPackedSampleModel singlePixelPackedSampleModel;
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (sampleModel instanceof SinglePixelPackedSampleModel) {
      singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
    } else {
      return false;
    } 
    if (singlePixelPackedSampleModel.getNumBands() != getNumComponents())
      return false; 
    int[] arrayOfInt = singlePixelPackedSampleModel.getBitMasks();
    for (byte b = 0; b < this.numComponents; b++) {
      if (arrayOfInt[b] != this.maskArray[b])
        return false; 
    } 
    return (paramRaster.getTransferType() == this.transferType);
  }
  
  private void setFields() {
    this.red_mask = this.maskArray[0];
    this.red_offset = this.maskOffsets[0];
    this.green_mask = this.maskArray[1];
    this.green_offset = this.maskOffsets[1];
    this.blue_mask = this.maskArray[2];
    this.blue_offset = this.maskOffsets[2];
    if (this.nBits[0] < 8)
      this.red_scale = (1 << this.nBits[0]) - 1; 
    if (this.nBits[1] < 8)
      this.green_scale = (1 << this.nBits[1]) - 1; 
    if (this.nBits[2] < 8)
      this.blue_scale = (1 << this.nBits[2]) - 1; 
    if (this.supportsAlpha) {
      this.alpha_mask = this.maskArray[3];
      this.alpha_offset = this.maskOffsets[3];
      if (this.nBits[3] < 8)
        this.alpha_scale = (1 << this.nBits[3]) - 1; 
    } 
  }
  
  public String toString() { return new String("DirectColorModel: rmask=" + Integer.toHexString(this.red_mask) + " gmask=" + Integer.toHexString(this.green_mask) + " bmask=" + Integer.toHexString(this.blue_mask) + " amask=" + Integer.toHexString(this.alpha_mask)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\DirectColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */