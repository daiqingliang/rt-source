package java.awt.image;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.util.Arrays;

public class ComponentColorModel extends ColorModel {
  private boolean signed;
  
  private boolean is_sRGB_stdScale;
  
  private boolean is_LinearRGB_stdScale;
  
  private boolean is_LinearGray_stdScale;
  
  private boolean is_ICCGray_stdScale;
  
  private byte[] tosRGB8LUT;
  
  private byte[] fromsRGB8LUT8;
  
  private short[] fromsRGB8LUT16;
  
  private byte[] fromLinearGray16ToOtherGray8LUT;
  
  private short[] fromLinearGray16ToOtherGray16LUT;
  
  private boolean needScaleInit;
  
  private boolean noUnnorm;
  
  private boolean nonStdScale;
  
  private float[] min;
  
  private float[] diffMinMax;
  
  private float[] compOffset;
  
  private float[] compScale;
  
  public ComponentColorModel(ColorSpace paramColorSpace, int[] paramArrayOfInt, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) {
    super(bitsHelper(paramInt2, paramColorSpace, paramBoolean1), bitsArrayHelper(paramArrayOfInt, paramInt2, paramColorSpace, paramBoolean1), paramColorSpace, paramBoolean1, paramBoolean2, paramInt1, paramInt2);
    switch (paramInt2) {
      case 0:
      case 1:
      case 3:
        this.signed = false;
        this.needScaleInit = true;
        break;
      case 2:
        this.signed = true;
        this.needScaleInit = true;
        break;
      case 4:
      case 5:
        this.signed = true;
        this.needScaleInit = false;
        this.noUnnorm = true;
        this.nonStdScale = false;
        break;
      default:
        throw new IllegalArgumentException("This constructor is not compatible with transferType " + paramInt2);
    } 
    setupLUTs();
  }
  
  public ComponentColorModel(ColorSpace paramColorSpace, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2) { this(paramColorSpace, null, paramBoolean1, paramBoolean2, paramInt1, paramInt2); }
  
  private static int bitsHelper(int paramInt, ColorSpace paramColorSpace, boolean paramBoolean) {
    int i = DataBuffer.getDataTypeSize(paramInt);
    int j = paramColorSpace.getNumComponents();
    if (paramBoolean)
      j++; 
    return i * j;
  }
  
  private static int[] bitsArrayHelper(int[] paramArrayOfInt, int paramInt, ColorSpace paramColorSpace, boolean paramBoolean) {
    switch (paramInt) {
      case 0:
      case 1:
      case 3:
        if (paramArrayOfInt != null)
          return paramArrayOfInt; 
        break;
    } 
    int i = DataBuffer.getDataTypeSize(paramInt);
    int j = paramColorSpace.getNumComponents();
    if (paramBoolean)
      j++; 
    int[] arrayOfInt = new int[j];
    for (byte b = 0; b < j; b++)
      arrayOfInt[b] = i; 
    return arrayOfInt;
  }
  
  private void setupLUTs() {
    if (this.is_sRGB) {
      this.is_sRGB_stdScale = true;
      this.nonStdScale = false;
    } else if (ColorModel.isLinearRGBspace(this.colorSpace)) {
      this.is_LinearRGB_stdScale = true;
      this.nonStdScale = false;
      if (this.transferType == 0) {
        this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
        this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
      } else {
        this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
        this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      } 
    } else if (this.colorSpaceType == 6 && this.colorSpace instanceof ICC_ColorSpace && this.colorSpace.getMinValue(0) == 0.0F && this.colorSpace.getMaxValue(0) == 1.0F) {
      ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)this.colorSpace;
      this.is_ICCGray_stdScale = true;
      this.nonStdScale = false;
      this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
      if (ColorModel.isLinearGRAYspace(iCC_ColorSpace)) {
        this.is_LinearGray_stdScale = true;
        if (this.transferType == 0) {
          this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(iCC_ColorSpace);
        } else {
          this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(iCC_ColorSpace);
        } 
      } else if (this.transferType == 0) {
        this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(iCC_ColorSpace);
        this.fromLinearGray16ToOtherGray8LUT = ColorModel.getLinearGray16ToOtherGray8LUT(iCC_ColorSpace);
      } else {
        this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(iCC_ColorSpace);
        this.fromLinearGray16ToOtherGray16LUT = ColorModel.getLinearGray16ToOtherGray16LUT(iCC_ColorSpace);
      } 
    } else if (this.needScaleInit) {
      this.nonStdScale = false;
      byte b;
      for (b = 0; b < this.numColorComponents; b++) {
        if (this.colorSpace.getMinValue(b) != 0.0F || this.colorSpace.getMaxValue(b) != 1.0F) {
          this.nonStdScale = true;
          break;
        } 
      } 
      if (this.nonStdScale) {
        this.min = new float[this.numColorComponents];
        this.diffMinMax = new float[this.numColorComponents];
        for (b = 0; b < this.numColorComponents; b++) {
          this.min[b] = this.colorSpace.getMinValue(b);
          this.diffMinMax[b] = this.colorSpace.getMaxValue(b) - this.min[b];
        } 
      } 
    } 
  }
  
  private void initScale() {
    byte b2;
    byte[] arrayOfByte;
    short[] arrayOfShort2;
    int[] arrayOfInt;
    short[] arrayOfShort1;
    float[] arrayOfFloat2;
    float[] arrayOfFloat1;
    this.needScaleInit = false;
    if (this.nonStdScale || this.signed) {
      this.noUnnorm = true;
    } else {
      this.noUnnorm = false;
    } 
    switch (this.transferType) {
      case 0:
        arrayOfByte = new byte[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfByte[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfByte[this.numColorComponents] = (byte)((1 << this.nBits[this.numColorComponents]) - 1); 
        arrayOfFloat1 = getNormalizedComponents(arrayOfByte, null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfByte[b2] = (byte)((1 << this.nBits[b2]) - 1); 
        arrayOfFloat2 = getNormalizedComponents(arrayOfByte, null, 0);
        break;
      case 1:
        arrayOfShort2 = new short[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort2[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfShort2[this.numColorComponents] = (short)((1 << this.nBits[this.numColorComponents]) - 1); 
        arrayOfFloat1 = getNormalizedComponents(arrayOfShort2, null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort2[b2] = (short)((1 << this.nBits[b2]) - 1); 
        arrayOfFloat2 = getNormalizedComponents(arrayOfShort2, null, 0);
        break;
      case 3:
        arrayOfInt = new int[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfInt[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfInt[this.numColorComponents] = (1 << this.nBits[this.numColorComponents]) - 1; 
        arrayOfFloat1 = getNormalizedComponents(arrayOfInt, null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfInt[b2] = (1 << this.nBits[b2]) - 1; 
        arrayOfFloat2 = getNormalizedComponents(arrayOfInt, null, 0);
        break;
      case 2:
        arrayOfShort1 = new short[this.numComponents];
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort1[b2] = 0; 
        if (this.supportsAlpha)
          arrayOfShort1[this.numColorComponents] = Short.MAX_VALUE; 
        arrayOfFloat1 = getNormalizedComponents(arrayOfShort1, null, 0);
        for (b2 = 0; b2 < this.numColorComponents; b2++)
          arrayOfShort1[b2] = Short.MAX_VALUE; 
        arrayOfFloat2 = getNormalizedComponents(arrayOfShort1, null, 0);
        break;
      default:
        arrayOfFloat1 = arrayOfFloat2 = null;
        break;
    } 
    this.nonStdScale = false;
    byte b1;
    for (b1 = 0; b1 < this.numColorComponents; b1++) {
      if (arrayOfFloat1[b1] != 0.0F || arrayOfFloat2[b1] != 1.0F) {
        this.nonStdScale = true;
        break;
      } 
    } 
    if (this.nonStdScale) {
      this.noUnnorm = true;
      this.is_sRGB_stdScale = false;
      this.is_LinearRGB_stdScale = false;
      this.is_LinearGray_stdScale = false;
      this.is_ICCGray_stdScale = false;
      this.compOffset = new float[this.numColorComponents];
      this.compScale = new float[this.numColorComponents];
      for (b1 = 0; b1 < this.numColorComponents; b1++) {
        this.compOffset[b1] = arrayOfFloat1[b1];
        this.compScale[b1] = 1.0F / (arrayOfFloat2[b1] - arrayOfFloat1[b1]);
      } 
    } 
  }
  
  private int getRGBComponent(int paramInt1, int paramInt2) {
    short[] arrayOfShort;
    int[] arrayOfInt;
    byte[] arrayOfByte;
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    if (this.needScaleInit)
      initScale(); 
    Object object = null;
    switch (this.transferType) {
      case 0:
        arrayOfByte = new byte[] { (byte)paramInt1 };
        object = arrayOfByte;
        break;
      case 1:
        arrayOfShort = new short[] { (short)paramInt1 };
        object = arrayOfShort;
        break;
      case 3:
        arrayOfInt = new int[] { paramInt1 };
        object = arrayOfInt;
        break;
    } 
    float[] arrayOfFloat1 = getNormalizedComponents(object, null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return (int)(arrayOfFloat2[paramInt2] * 255.0F + 0.5F);
  }
  
  public int getRed(int paramInt) { return getRGBComponent(paramInt, 0); }
  
  public int getGreen(int paramInt) { return getRGBComponent(paramInt, 1); }
  
  public int getBlue(int paramInt) { return getRGBComponent(paramInt, 2); }
  
  public int getAlpha(int paramInt) {
    if (!this.supportsAlpha)
      return 255; 
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    return (int)(paramInt / ((1 << this.nBits[0]) - 1) * 255.0F + 0.5F);
  }
  
  public int getRGB(int paramInt) {
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    return getAlpha(paramInt) << 24 | getRed(paramInt) << 16 | getGreen(paramInt) << 8 | getBlue(paramInt) << 0;
  }
  
  private int extractComponent(Object paramObject, int paramInt1, int paramInt2) {
    int[] arrayOfInt;
    double d;
    float f;
    short[] arrayOfShort2;
    double[] arrayOfDouble;
    byte[] arrayOfByte;
    short[] arrayOfShort1;
    float[] arrayOfFloat;
    short s;
    int j;
    byte b;
    boolean bool = (this.supportsAlpha && this.isAlphaPremultiplied) ? 1 : 0;
    int i = 0;
    int k = (1 << this.nBits[paramInt1]) - 1;
    switch (this.transferType) {
      case 2:
        arrayOfShort1 = (short[])paramObject;
        f = ((1 << paramInt2) - 1);
        if (bool) {
          short s1 = arrayOfShort1[this.numColorComponents];
          return (s1 != 0) ? (int)(arrayOfShort1[paramInt1] / s1 * f + 0.5F) : 0;
        } 
        return (int)(arrayOfShort1[paramInt1] / 32767.0F * f + 0.5F);
      case 4:
        arrayOfFloat = (float[])paramObject;
        f = ((1 << paramInt2) - 1);
        if (bool) {
          float f1 = arrayOfFloat[this.numColorComponents];
          return (f1 != 0.0F) ? (int)(arrayOfFloat[paramInt1] / f1 * f + 0.5F) : 0;
        } 
        return (int)(arrayOfFloat[paramInt1] * f + 0.5F);
      case 5:
        arrayOfDouble = (double[])paramObject;
        d = ((1 << paramInt2) - 1);
        if (bool) {
          double d1 = arrayOfDouble[this.numColorComponents];
          return (d1 != 0.0D) ? (int)(arrayOfDouble[paramInt1] / d1 * d + 0.5D) : 0;
        } 
        return (int)(arrayOfDouble[paramInt1] * d + 0.5D);
      case 0:
        arrayOfByte = (byte[])paramObject;
        b = arrayOfByte[paramInt1] & k;
        paramInt2 = 8;
        if (bool)
          i = arrayOfByte[this.numColorComponents] & k; 
        break;
      case 1:
        arrayOfShort2 = (short[])paramObject;
        s = arrayOfShort2[paramInt1] & k;
        if (bool)
          i = arrayOfShort2[this.numColorComponents] & k; 
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        j = arrayOfInt[paramInt1];
        if (bool)
          i = arrayOfInt[this.numColorComponents]; 
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (bool) {
      if (i != 0) {
        float f1 = ((1 << paramInt2) - 1);
        float f2 = j / k;
        float f3 = ((1 << this.nBits[this.numColorComponents]) - 1) / i;
        return (int)(f2 * f3 * f1 + 0.5F);
      } 
      return 0;
    } 
    if (this.nBits[paramInt1] != paramInt2) {
      float f1 = ((1 << paramInt2) - 1);
      float f2 = j / k;
      return (int)(f2 * f1 + 0.5F);
    } 
    return j;
  }
  
  private int getRGBComponent(Object paramObject, int paramInt) {
    if (this.needScaleInit)
      initScale(); 
    if (this.is_sRGB_stdScale)
      return extractComponent(paramObject, paramInt, 8); 
    if (this.is_LinearRGB_stdScale) {
      int i = extractComponent(paramObject, paramInt, 16);
      return this.tosRGB8LUT[i] & 0xFF;
    } 
    if (this.is_ICCGray_stdScale) {
      int i = extractComponent(paramObject, 0, 16);
      return this.tosRGB8LUT[i] & 0xFF;
    } 
    float[] arrayOfFloat1 = getNormalizedComponents(paramObject, null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return (int)(arrayOfFloat2[paramInt] * 255.0F + 0.5F);
  }
  
  public int getRed(Object paramObject) { return getRGBComponent(paramObject, 0); }
  
  public int getGreen(Object paramObject) { return getRGBComponent(paramObject, 1); }
  
  public int getBlue(Object paramObject) { return getRGBComponent(paramObject, 2); }
  
  public int getAlpha(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort2;
    byte[] arrayOfByte;
    double[] arrayOfDouble;
    float[] arrayOfFloat;
    short[] arrayOfShort1;
    int i;
    short s;
    byte b;
    if (!this.supportsAlpha)
      return 255; 
    null = 0;
    int j = this.numColorComponents;
    int k = (1 << this.nBits[j]) - 1;
    switch (this.transferType) {
      case 2:
        arrayOfShort1 = (short[])paramObject;
        return (int)(arrayOfShort1[j] / 32767.0F * 255.0F + 0.5F);
      case 4:
        arrayOfFloat = (float[])paramObject;
        return (int)(arrayOfFloat[j] * 255.0F + 0.5F);
      case 5:
        arrayOfDouble = (double[])paramObject;
        return (int)(arrayOfDouble[j] * 255.0D + 0.5D);
      case 0:
        arrayOfByte = (byte[])paramObject;
        b = arrayOfByte[j] & k;
        break;
      case 1:
        arrayOfShort2 = (short[])paramObject;
        s = arrayOfShort2[j] & k;
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[j];
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    return (this.nBits[j] == 8) ? i : (int)(i / ((1 << this.nBits[j]) - 1) * 255.0F + 0.5F);
  }
  
  public int getRGB(Object paramObject) {
    if (this.needScaleInit)
      initScale(); 
    if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale)
      return getAlpha(paramObject) << 24 | getRed(paramObject) << 16 | getGreen(paramObject) << 8 | getBlue(paramObject); 
    if (this.colorSpaceType == 6) {
      int i = getRed(paramObject);
      return getAlpha(paramObject) << 24 | i << 16 | i << 8 | i;
    } 
    float[] arrayOfFloat1 = getNormalizedComponents(paramObject, null, 0);
    float[] arrayOfFloat2 = this.colorSpace.toRGB(arrayOfFloat1);
    return getAlpha(paramObject) << 24 | (int)(arrayOfFloat2[0] * 255.0F + 0.5F) << 16 | (int)(arrayOfFloat2[1] * 255.0F + 0.5F) << 8 | (int)(arrayOfFloat2[2] * 255.0F + 0.5F) << 0;
  }
  
  public Object getDataElements(int paramInt, Object paramObject) {
    byte b;
    short[] arrayOfShort;
    int[] arrayOfInt;
    int i = paramInt >> 16 & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt & 0xFF;
    if (this.needScaleInit)
      initScale(); 
    if (this.signed)
      switch (this.transferType) {
        case 2:
          if (paramObject == null) {
            arrayOfInt = new short[this.numComponents];
          } else {
            arrayOfInt = (short[])paramObject;
          } 
          if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            float f = 128.49803F;
            if (this.is_LinearRGB_stdScale) {
              i = this.fromsRGB8LUT16[i] & 0xFFFF;
              j = this.fromsRGB8LUT16[j] & 0xFFFF;
              k = this.fromsRGB8LUT16[k] & 0xFFFF;
              f = 0.49999237F;
            } 
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[3] = (short)(int)(m * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f = m * f * 0.003921569F; 
            } 
            arrayOfInt[0] = (short)(int)(i * f + 0.5F);
            arrayOfInt[1] = (short)(int)(j * f + 0.5F);
            arrayOfInt[2] = (short)(int)(k * f + 0.5F);
          } else if (this.is_LinearGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            float f2 = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
            float f1 = 32767.0F;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[1] = (short)(int)(m * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f1 = m * f1 * 0.003921569F; 
            } 
            arrayOfInt[0] = (short)(int)(f2 * f1 + 0.5F);
          } else if (this.is_ICCGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
            m = this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF;
            float f = 0.49999237F;
            if (this.supportsAlpha) {
              int n = paramInt >> 24 & 0xFF;
              arrayOfInt[1] = (short)(int)(n * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f = n * f * 0.003921569F; 
            } 
            arrayOfInt[0] = (short)(int)(m * f + 0.5F);
          } else {
            float f = 0.003921569F;
            float[] arrayOfFloat = new float[3];
            arrayOfFloat[0] = i * f;
            arrayOfFloat[1] = j * f;
            arrayOfFloat[2] = k * f;
            arrayOfFloat = this.colorSpace.fromRGB(arrayOfFloat);
            if (this.nonStdScale)
              for (byte b2 = 0; b2 < this.numColorComponents; b2++) {
                arrayOfFloat[b2] = (arrayOfFloat[b2] - this.compOffset[b2]) * this.compScale[b2];
                if (arrayOfFloat[b2] < 0.0F)
                  arrayOfFloat[b2] = 0.0F; 
                if (arrayOfFloat[b2] > 1.0F)
                  arrayOfFloat[b2] = 1.0F; 
              }  
            f = 32767.0F;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[this.numColorComponents] = (short)(int)(m * 128.49803F + 0.5F);
              if (this.isAlphaPremultiplied)
                f *= m * 0.003921569F; 
            } 
            for (byte b1 = 0; b1 < this.numColorComponents; b1++)
              arrayOfInt[b1] = (short)(int)(arrayOfFloat[b1] * f + 0.5F); 
          } 
          return arrayOfInt;
        case 4:
          if (paramObject == null) {
            float[] arrayOfFloat = new float[this.numComponents];
          } else {
            arrayOfInt = (float[])paramObject;
          } 
          if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            float f;
            if (this.is_LinearRGB_stdScale) {
              i = this.fromsRGB8LUT16[i] & 0xFFFF;
              j = this.fromsRGB8LUT16[j] & 0xFFFF;
              k = this.fromsRGB8LUT16[k] & 0xFFFF;
              f = 1.5259022E-5F;
            } else {
              f = 0.003921569F;
            } 
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[3] = m * 0.003921569F;
              if (this.isAlphaPremultiplied)
                f *= arrayOfInt[3]; 
            } 
            arrayOfInt[0] = i * f;
            arrayOfInt[1] = j * f;
            arrayOfInt[2] = k * f;
          } else if (this.is_LinearGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            arrayOfInt[0] = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[1] = m * 0.003921569F;
              if (this.isAlphaPremultiplied)
                arrayOfInt[0] = arrayOfInt[0] * arrayOfInt[1]; 
            } 
          } else if (this.is_ICCGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
            arrayOfInt[0] = (this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF) / 65535.0F;
            if (this.supportsAlpha) {
              int n = paramInt >> 24 & 0xFF;
              arrayOfInt[1] = n * 0.003921569F;
              if (this.isAlphaPremultiplied)
                arrayOfInt[0] = arrayOfInt[0] * arrayOfInt[1]; 
            } 
          } else {
            float[] arrayOfFloat = new float[3];
            float f = 0.003921569F;
            arrayOfFloat[0] = i * f;
            arrayOfFloat[1] = j * f;
            arrayOfFloat[2] = k * f;
            arrayOfFloat = this.colorSpace.fromRGB(arrayOfFloat);
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[this.numColorComponents] = m * f;
              if (this.isAlphaPremultiplied) {
                f *= m;
                for (byte b2 = 0; b2 < this.numColorComponents; b2++)
                  arrayOfFloat[b2] = arrayOfFloat[b2] * f; 
              } 
            } 
            for (byte b1 = 0; b1 < this.numColorComponents; b1++)
              arrayOfInt[b1] = arrayOfFloat[b1]; 
          } 
          return arrayOfInt;
        case 5:
          if (paramObject == null) {
            double[] arrayOfDouble = new double[this.numComponents];
          } else {
            arrayOfInt = (double[])paramObject;
          } 
          if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            double d;
            if (this.is_LinearRGB_stdScale) {
              i = this.fromsRGB8LUT16[i] & 0xFFFF;
              j = this.fromsRGB8LUT16[j] & 0xFFFF;
              k = this.fromsRGB8LUT16[k] & 0xFFFF;
              d = 1.5259021896696422E-5D;
            } else {
              d = 0.00392156862745098D;
            } 
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[3] = m * 0.00392156862745098D;
              if (this.isAlphaPremultiplied)
                d *= arrayOfInt[3]; 
            } 
            arrayOfInt[0] = i * d;
            arrayOfInt[1] = j * d;
            arrayOfInt[2] = k * d;
          } else if (this.is_LinearGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            arrayOfInt[0] = (0.2125D * i + 0.7154D * j + 0.0721D * k) / 65535.0D;
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[1] = m * 0.00392156862745098D;
              if (this.isAlphaPremultiplied)
                arrayOfInt[0] = arrayOfInt[0] * arrayOfInt[1]; 
            } 
          } else if (this.is_ICCGray_stdScale) {
            i = this.fromsRGB8LUT16[i] & 0xFFFF;
            j = this.fromsRGB8LUT16[j] & 0xFFFF;
            k = this.fromsRGB8LUT16[k] & 0xFFFF;
            int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
            arrayOfInt[0] = (this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF) / 65535.0D;
            if (this.supportsAlpha) {
              int n = paramInt >> 24 & 0xFF;
              arrayOfInt[1] = n * 0.00392156862745098D;
              if (this.isAlphaPremultiplied)
                arrayOfInt[0] = arrayOfInt[0] * arrayOfInt[1]; 
            } 
          } else {
            float f = 0.003921569F;
            float[] arrayOfFloat = new float[3];
            arrayOfFloat[0] = i * f;
            arrayOfFloat[1] = j * f;
            arrayOfFloat[2] = k * f;
            arrayOfFloat = this.colorSpace.fromRGB(arrayOfFloat);
            if (this.supportsAlpha) {
              int m = paramInt >> 24 & 0xFF;
              arrayOfInt[this.numColorComponents] = m * 0.00392156862745098D;
              if (this.isAlphaPremultiplied) {
                f *= m;
                for (byte b2 = 0; b2 < this.numColorComponents; b2++)
                  arrayOfFloat[b2] = arrayOfFloat[b2] * f; 
              } 
            } 
            for (byte b1 = 0; b1 < this.numColorComponents; b1++)
              arrayOfInt[b1] = arrayOfFloat[b1]; 
          } 
          return arrayOfInt;
      }  
    if (this.transferType == 3 && paramObject != null) {
      arrayOfInt = (int[])paramObject;
    } else {
      arrayOfInt = new int[this.numComponents];
    } 
    if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
      float f;
      byte b1;
      if (this.is_LinearRGB_stdScale) {
        if (this.transferType == 0) {
          i = this.fromsRGB8LUT8[i] & 0xFF;
          j = this.fromsRGB8LUT8[j] & 0xFF;
          k = this.fromsRGB8LUT8[k] & 0xFF;
          b1 = 8;
          f = 0.003921569F;
        } else {
          i = this.fromsRGB8LUT16[i] & 0xFFFF;
          j = this.fromsRGB8LUT16[j] & 0xFFFF;
          k = this.fromsRGB8LUT16[k] & 0xFFFF;
          b1 = 16;
          f = 1.5259022E-5F;
        } 
      } else {
        b1 = 8;
        f = 0.003921569F;
      } 
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.nBits[3] == 8) {
          arrayOfInt[3] = m;
        } else {
          arrayOfInt[3] = (int)(m * 0.003921569F * ((1 << this.nBits[3]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied) {
          f *= m * 0.003921569F;
          b1 = -1;
        } 
      } 
      if (this.nBits[0] == b1) {
        arrayOfInt[0] = i;
      } else {
        arrayOfInt[0] = (int)(i * f * ((1 << this.nBits[0]) - 1) + 0.5F);
      } 
      if (this.nBits[1] == b1) {
        arrayOfInt[1] = j;
      } else {
        arrayOfInt[1] = (int)(j * f * ((1 << this.nBits[1]) - 1) + 0.5F);
      } 
      if (this.nBits[2] == b1) {
        arrayOfInt[2] = k;
      } else {
        arrayOfInt[2] = (int)(k * f * ((1 << this.nBits[2]) - 1) + 0.5F);
      } 
    } else if (this.is_LinearGray_stdScale) {
      i = this.fromsRGB8LUT16[i] & 0xFFFF;
      j = this.fromsRGB8LUT16[j] & 0xFFFF;
      k = this.fromsRGB8LUT16[k] & 0xFFFF;
      float f = (0.2125F * i + 0.7154F * j + 0.0721F * k) / 65535.0F;
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.nBits[1] == 8) {
          arrayOfInt[1] = m;
        } else {
          arrayOfInt[1] = (int)(m * 0.003921569F * ((1 << this.nBits[1]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied)
          f *= m * 0.003921569F; 
      } 
      arrayOfInt[0] = (int)(f * ((1 << this.nBits[0]) - 1) + 0.5F);
    } else if (this.is_ICCGray_stdScale) {
      i = this.fromsRGB8LUT16[i] & 0xFFFF;
      j = this.fromsRGB8LUT16[j] & 0xFFFF;
      k = this.fromsRGB8LUT16[k] & 0xFFFF;
      int m = (int)(0.2125F * i + 0.7154F * j + 0.0721F * k + 0.5F);
      float f = (this.fromLinearGray16ToOtherGray16LUT[m] & 0xFFFF) / 65535.0F;
      if (this.supportsAlpha) {
        int n = paramInt >> 24 & 0xFF;
        if (this.nBits[1] == 8) {
          arrayOfInt[1] = n;
        } else {
          arrayOfInt[1] = (int)(n * 0.003921569F * ((1 << this.nBits[1]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied)
          f *= n * 0.003921569F; 
      } 
      arrayOfInt[0] = (int)(f * ((1 << this.nBits[0]) - 1) + 0.5F);
    } else {
      arrayOfShort = new float[3];
      float f = 0.003921569F;
      arrayOfShort[0] = i * f;
      arrayOfShort[1] = j * f;
      arrayOfShort[2] = k * f;
      arrayOfShort = this.colorSpace.fromRGB(arrayOfShort);
      if (this.nonStdScale)
        for (byte b2 = 0; b2 < this.numColorComponents; b2++) {
          arrayOfShort[b2] = (arrayOfShort[b2] - this.compOffset[b2]) * this.compScale[b2];
          if (arrayOfShort[b2] < 0.0F)
            arrayOfShort[b2] = 0.0F; 
          if (arrayOfShort[b2] > 1.0F)
            arrayOfShort[b2] = 1.0F; 
        }  
      if (this.supportsAlpha) {
        int m = paramInt >> 24 & 0xFF;
        if (this.nBits[this.numColorComponents] == 8) {
          arrayOfInt[this.numColorComponents] = m;
        } else {
          arrayOfInt[this.numColorComponents] = (int)(m * f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } 
        if (this.isAlphaPremultiplied) {
          f *= m;
          for (byte b2 = 0; b2 < this.numColorComponents; b2++)
            arrayOfShort[b2] = arrayOfShort[b2] * f; 
        } 
      } 
      for (byte b1 = 0; b1 < this.numColorComponents; b1++)
        arrayOfInt[b1] = (int)(arrayOfShort[b1] * ((1 << this.nBits[b1]) - 1) + 0.5F); 
    } 
    switch (this.transferType) {
      case 0:
        if (paramObject == null) {
          arrayOfShort = new byte[this.numComponents];
        } else {
          arrayOfShort = (byte[])paramObject;
        } 
        for (b = 0; b < this.numComponents; b++)
          arrayOfShort[b] = (byte)(0xFF & arrayOfInt[b]); 
        return arrayOfShort;
      case 1:
        if (paramObject == null) {
          short[] arrayOfShort1 = new short[this.numComponents];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        for (b = 0; b < this.numComponents; b++)
          arrayOfShort[b] = (short)(arrayOfInt[b] & 0xFFFF); 
        return arrayOfShort;
      case 3:
        if (this.maxBits > 23)
          for (byte b1 = 0; b1 < this.numComponents; b1++) {
            if (arrayOfInt[b1] > (1 << this.nBits[b1]) - 1)
              arrayOfInt[b1] = (1 << this.nBits[b1]) - 1; 
          }  
        return arrayOfInt;
    } 
    throw new IllegalArgumentException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt2 + 1]; 
    paramArrayOfInt[paramInt2 + 0] = paramInt1 & (1 << this.nBits[0]) - 1;
    return paramArrayOfInt;
  }
  
  public int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt) {
    int[] arrayOfInt;
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    if (paramObject instanceof int[]) {
      arrayOfInt = (int[])paramObject;
    } else {
      arrayOfInt = DataBuffer.toIntArray(paramObject);
      if (arrayOfInt == null)
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType); 
    } 
    if (arrayOfInt.length < this.numComponents)
      throw new IllegalArgumentException("Length of pixel array < number of components in model"); 
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[paramInt + this.numComponents];
    } else if (paramArrayOfInt.length - paramInt < this.numComponents) {
      throw new IllegalArgumentException("Length of components array < number of components in model");
    } 
    System.arraycopy(arrayOfInt, 0, paramArrayOfInt, paramInt, this.numComponents);
    return paramArrayOfInt;
  }
  
  public int[] getUnnormalizedComponents(float[] paramArrayOfFloat, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    return super.getUnnormalizedComponents(paramArrayOfFloat, paramInt1, paramArrayOfInt, paramInt2);
  }
  
  public float[] getNormalizedComponents(int[] paramArrayOfInt, int paramInt1, float[] paramArrayOfFloat, int paramInt2) {
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    return super.getNormalizedComponents(paramArrayOfInt, paramInt1, paramArrayOfFloat, paramInt2);
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt) {
    if (this.needScaleInit)
      initScale(); 
    if (this.numComponents == 1) {
      if (this.noUnnorm)
        throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
      return paramArrayOfInt[paramInt + 0];
    } 
    throw new IllegalArgumentException("This model returns " + this.numComponents + " elements in the pixel array.");
  }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject) {
    int i;
    short[] arrayOfShort;
    if (this.needScaleInit)
      initScale(); 
    if (this.noUnnorm)
      throw new IllegalArgumentException("This ColorModel does not support the unnormalized form"); 
    if (paramArrayOfInt.length - paramInt < this.numComponents)
      throw new IllegalArgumentException("Component array too small (should be " + this.numComponents); 
    switch (this.transferType) {
      case 3:
        if (paramObject == null) {
          arrayOfShort = new int[this.numComponents];
        } else {
          arrayOfShort = (int[])paramObject;
        } 
        System.arraycopy(paramArrayOfInt, paramInt, arrayOfShort, 0, this.numComponents);
        return arrayOfShort;
      case 0:
        if (paramObject == null) {
          byte[] arrayOfByte = new byte[this.numComponents];
        } else {
          arrayOfShort = (byte[])paramObject;
        } 
        for (i = 0; i < this.numComponents; i++)
          arrayOfShort[i] = (byte)(paramArrayOfInt[paramInt + i] & 0xFF); 
        return arrayOfShort;
      case 1:
        if (paramObject == null) {
          short[] arrayOfShort1 = new short[this.numComponents];
        } else {
          arrayOfShort = (short[])paramObject;
        } 
        for (i = 0; i < this.numComponents; i++)
          arrayOfShort[i] = (short)(paramArrayOfInt[paramInt + i] & 0xFFFF); 
        return arrayOfShort;
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getDataElement(float[] paramArrayOfFloat, int paramInt) {
    byte[] arrayOfByte;
    short[] arrayOfShort;
    int[] arrayOfInt;
    if (this.numComponents > 1)
      throw new IllegalArgumentException("More than one component per pixel"); 
    if (this.signed)
      throw new IllegalArgumentException("Component value is signed"); 
    if (this.needScaleInit)
      initScale(); 
    Object object = getDataElements(paramArrayOfFloat, paramInt, null);
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])object;
        return arrayOfByte[0] & 0xFF;
      case 1:
        arrayOfShort = (short[])object;
        return arrayOfShort[0] & 0xFFFF;
      case 3:
        arrayOfInt = (int[])object;
        return arrayOfInt[0];
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public Object getDataElements(float[] paramArrayOfFloat, int paramInt, Object paramObject) {
    double[] arrayOfDouble;
    float[] arrayOfFloat2;
    short[] arrayOfShort2;
    int[] arrayOfInt;
    short[] arrayOfShort1;
    byte[] arrayOfByte;
    float[] arrayOfFloat1;
    boolean bool = (this.supportsAlpha && this.isAlphaPremultiplied) ? 1 : 0;
    if (this.needScaleInit)
      initScale(); 
    if (this.nonStdScale) {
      arrayOfFloat1 = new float[this.numComponents];
      byte b = 0;
      for (int i = paramInt; b < this.numColorComponents; i++) {
        arrayOfFloat1[b] = (paramArrayOfFloat[i] - this.compOffset[b]) * this.compScale[b];
        if (arrayOfFloat1[b] < 0.0F)
          arrayOfFloat1[b] = 0.0F; 
        if (arrayOfFloat1[b] > 1.0F)
          arrayOfFloat1[b] = 1.0F; 
        b++;
      } 
      if (this.supportsAlpha)
        arrayOfFloat1[this.numColorComponents] = paramArrayOfFloat[this.numColorComponents + paramInt]; 
      paramInt = 0;
    } else {
      arrayOfFloat1 = paramArrayOfFloat;
    } 
    switch (this.transferType) {
      case 0:
        if (paramObject == null) {
          arrayOfByte = new byte[this.numComponents];
        } else {
          arrayOfByte = (byte[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b = 0;
          for (int i = paramInt; b < this.numColorComponents; i++) {
            arrayOfByte[b] = (byte)(int)(arrayOfFloat1[i] * f * ((1 << this.nBits[b]) - 1) + 0.5F);
            b++;
          } 
          arrayOfByte[this.numColorComponents] = (byte)(int)(f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } else {
          byte b = 0;
          for (int i = paramInt; b < this.numComponents; i++) {
            arrayOfByte[b] = (byte)(int)(arrayOfFloat1[i] * ((1 << this.nBits[b]) - 1) + 0.5F);
            b++;
          } 
        } 
        return arrayOfByte;
      case 1:
        if (paramObject == null) {
          arrayOfShort1 = new short[this.numComponents];
        } else {
          arrayOfShort1 = (short[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b = 0;
          for (int i = paramInt; b < this.numColorComponents; i++) {
            arrayOfShort1[b] = (short)(int)(arrayOfFloat1[i] * f * ((1 << this.nBits[b]) - 1) + 0.5F);
            b++;
          } 
          arrayOfShort1[this.numColorComponents] = (short)(int)(f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } else {
          byte b = 0;
          for (int i = paramInt; b < this.numComponents; i++) {
            arrayOfShort1[b] = (short)(int)(arrayOfFloat1[i] * ((1 << this.nBits[b]) - 1) + 0.5F);
            b++;
          } 
        } 
        return arrayOfShort1;
      case 3:
        if (paramObject == null) {
          arrayOfInt = new int[this.numComponents];
        } else {
          arrayOfInt = (int[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b = 0;
          for (int i = paramInt; b < this.numColorComponents; i++) {
            arrayOfInt[b] = (int)(arrayOfFloat1[i] * f * ((1 << this.nBits[b]) - 1) + 0.5F);
            b++;
          } 
          arrayOfInt[this.numColorComponents] = (int)(f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
        } else {
          byte b = 0;
          for (int i = paramInt; b < this.numComponents; i++) {
            arrayOfInt[b] = (int)(arrayOfFloat1[i] * ((1 << this.nBits[b]) - 1) + 0.5F);
            b++;
          } 
        } 
        return arrayOfInt;
      case 2:
        if (paramObject == null) {
          arrayOfShort2 = new short[this.numComponents];
        } else {
          arrayOfShort2 = (short[])paramObject;
        } 
        if (bool) {
          float f = arrayOfFloat1[this.numColorComponents + paramInt];
          byte b = 0;
          for (int i = paramInt; b < this.numColorComponents; i++) {
            arrayOfShort2[b] = (short)(int)(arrayOfFloat1[i] * f * 32767.0F + 0.5F);
            b++;
          } 
          arrayOfShort2[this.numColorComponents] = (short)(int)(f * 32767.0F + 0.5F);
        } else {
          byte b = 0;
          for (int i = paramInt; b < this.numComponents; i++) {
            arrayOfShort2[b] = (short)(int)(arrayOfFloat1[i] * 32767.0F + 0.5F);
            b++;
          } 
        } 
        return arrayOfShort2;
      case 4:
        if (paramObject == null) {
          arrayOfFloat2 = new float[this.numComponents];
        } else {
          arrayOfFloat2 = (float[])paramObject;
        } 
        if (bool) {
          float f = paramArrayOfFloat[this.numColorComponents + paramInt];
          byte b = 0;
          for (int i = paramInt; b < this.numColorComponents; i++) {
            arrayOfFloat2[b] = paramArrayOfFloat[i] * f;
            b++;
          } 
          arrayOfFloat2[this.numColorComponents] = f;
        } else {
          byte b = 0;
          for (int i = paramInt; b < this.numComponents; i++) {
            arrayOfFloat2[b] = paramArrayOfFloat[i];
            b++;
          } 
        } 
        return arrayOfFloat2;
      case 5:
        if (paramObject == null) {
          arrayOfDouble = new double[this.numComponents];
        } else {
          arrayOfDouble = (double[])paramObject;
        } 
        if (bool) {
          double d = paramArrayOfFloat[this.numColorComponents + paramInt];
          byte b = 0;
          for (int i = paramInt; b < this.numColorComponents; i++) {
            arrayOfDouble[b] = paramArrayOfFloat[i] * d;
            b++;
          } 
          arrayOfDouble[this.numColorComponents] = d;
        } else {
          byte b = 0;
          for (int i = paramInt; b < this.numComponents; i++) {
            arrayOfDouble[b] = paramArrayOfFloat[i];
            b++;
          } 
        } 
        return arrayOfDouble;
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public float[] getNormalizedComponents(Object paramObject, float[] paramArrayOfFloat, int paramInt) {
    int i1;
    int n;
    int m;
    double[] arrayOfDouble;
    float[] arrayOfFloat;
    int k;
    int j;
    short[] arrayOfShort2;
    int i;
    int[] arrayOfInt;
    byte b;
    short[] arrayOfShort1;
    byte[] arrayOfByte;
    if (paramArrayOfFloat == null)
      paramArrayOfFloat = new float[this.numComponents + paramInt]; 
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        b = 0;
        for (i = paramInt; b < this.numComponents; i++) {
          paramArrayOfFloat[i] = (arrayOfByte[b] & 0xFF) / ((1 << this.nBits[b]) - 1);
          b++;
        } 
        break;
      case 1:
        arrayOfShort1 = (short[])paramObject;
        i = 0;
        for (j = paramInt; i < this.numComponents; j++) {
          paramArrayOfFloat[j] = (arrayOfShort1[i] & 0xFFFF) / ((1 << this.nBits[i]) - 1);
          i++;
        } 
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        j = 0;
        for (k = paramInt; j < this.numComponents; k++) {
          paramArrayOfFloat[k] = arrayOfInt[j] / ((1 << this.nBits[j]) - 1);
          j++;
        } 
        break;
      case 2:
        arrayOfShort2 = (short[])paramObject;
        k = 0;
        for (m = paramInt; k < this.numComponents; m++) {
          paramArrayOfFloat[m] = arrayOfShort2[k] / 32767.0F;
          k++;
        } 
        break;
      case 4:
        arrayOfFloat = (float[])paramObject;
        m = 0;
        for (n = paramInt; m < this.numComponents; n++) {
          paramArrayOfFloat[n] = arrayOfFloat[m];
          m++;
        } 
        break;
      case 5:
        arrayOfDouble = (double[])paramObject;
        n = 0;
        for (i1 = paramInt; n < this.numComponents; i1++) {
          paramArrayOfFloat[i1] = (float)arrayOfDouble[n];
          n++;
        } 
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (this.supportsAlpha && this.isAlphaPremultiplied) {
      float f = paramArrayOfFloat[this.numColorComponents + paramInt];
      if (f != 0.0F) {
        float f1 = 1.0F / f;
        for (int i2 = paramInt; i2 < this.numColorComponents + paramInt; i2++)
          paramArrayOfFloat[i2] = paramArrayOfFloat[i2] * f1; 
      } 
    } 
    if (this.min != null)
      for (int i2 = 0; i2 < this.numColorComponents; i2++)
        paramArrayOfFloat[i2 + paramInt] = this.min[i2] + this.diffMinMax[i2] * paramArrayOfFloat[i2 + paramInt];  
    return paramArrayOfFloat;
  }
  
  public ColorModel coerceData(WritableRaster paramWritableRaster, boolean paramBoolean) {
    if (!this.supportsAlpha || this.isAlphaPremultiplied == paramBoolean)
      return this; 
    int i = paramWritableRaster.getWidth();
    int j = paramWritableRaster.getHeight();
    int k = paramWritableRaster.getNumBands() - 1;
    int m = paramWritableRaster.getMinX();
    int n = paramWritableRaster.getMinY();
    if (paramBoolean) {
      byte b2;
      byte b1;
      float f;
      double[] arrayOfDouble2;
      double[] arrayOfDouble1;
      switch (this.transferType) {
        case 0:
          arrayOfDouble1 = null;
          arrayOfDouble2 = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble1 = (byte[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble1);
              float f1 = (arrayOfDouble1[k] & 0xFF) * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble1[b3] = (byte)(int)((arrayOfDouble1[b3] & 0xFF) * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble1);
              } else {
                if (arrayOfDouble2 == null) {
                  arrayOfDouble2 = new byte[this.numComponents];
                  Arrays.fill(arrayOfDouble2, (byte)0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble2);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 1:
          arrayOfDouble1 = null;
          arrayOfDouble2 = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble1 = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble1);
              float f1 = (arrayOfDouble1[k] & 0xFFFF) * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble1[b3] = (short)(int)((arrayOfDouble1[b3] & 0xFFFF) * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble1);
              } else {
                if (arrayOfDouble2 == null) {
                  arrayOfDouble2 = new short[this.numComponents];
                  Arrays.fill(arrayOfDouble2, (short)0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble2);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 3:
          arrayOfDouble1 = null;
          arrayOfDouble2 = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble1 = (int[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble1);
              float f1 = arrayOfDouble1[k] * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble1[b3] = (int)(arrayOfDouble1[b3] * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble1);
              } else {
                if (arrayOfDouble2 == null) {
                  arrayOfDouble2 = new int[this.numComponents];
                  Arrays.fill(arrayOfDouble2, 0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble2);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 2:
          arrayOfDouble1 = null;
          arrayOfDouble2 = null;
          f = 3.051851E-5F;
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble1 = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble1);
              float f1 = arrayOfDouble1[k] * f;
              if (f1 != 0.0F) {
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble1[b3] = (short)(int)(arrayOfDouble1[b3] * f1 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble1);
              } else {
                if (arrayOfDouble2 == null) {
                  arrayOfDouble2 = new short[this.numComponents];
                  Arrays.fill(arrayOfDouble2, (short)0);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble2);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 4:
          arrayOfDouble1 = null;
          arrayOfDouble2 = null;
          b1 = 0;
          while (b1 < j) {
            int i1 = m;
            b2 = 0;
            while (b2 < i) {
              arrayOfDouble1 = (float[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble1);
              float f1 = arrayOfDouble1[k];
              if (f1 != 0.0F) {
                for (byte b = 0; b < k; b++)
                  arrayOfDouble1[b] = arrayOfDouble1[b] * f1; 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble1);
              } else {
                if (arrayOfDouble2 == null) {
                  arrayOfDouble2 = new float[this.numComponents];
                  Arrays.fill(arrayOfDouble2, 0.0F);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble2);
              } 
              b2++;
              i1++;
            } 
            b1++;
            n++;
          } 
          break;
        case 5:
          arrayOfDouble1 = null;
          arrayOfDouble2 = null;
          b1 = 0;
          while (b1 < j) {
            int i1 = m;
            b2 = 0;
            while (b2 < i) {
              arrayOfDouble1 = (double[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble1);
              double d = arrayOfDouble1[k];
              if (d != 0.0D) {
                for (byte b = 0; b < k; b++)
                  arrayOfDouble1[b] = arrayOfDouble1[b] * d; 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble1);
              } else {
                if (arrayOfDouble2 == null) {
                  arrayOfDouble2 = new double[this.numComponents];
                  Arrays.fill(arrayOfDouble2, 0.0D);
                } 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble2);
              } 
              b2++;
              i1++;
            } 
            b1++;
            n++;
          } 
          break;
        default:
          throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      } 
    } else {
      byte b2;
      float f;
      byte b1;
      double[] arrayOfDouble;
      switch (this.transferType) {
        case 0:
          arrayOfDouble = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble = (byte[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble);
              float f1 = (arrayOfDouble[k] & 0xFF) * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble[b3] = (byte)(int)((arrayOfDouble[b3] & 0xFF) * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 1:
          arrayOfDouble = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble);
              float f1 = (arrayOfDouble[k] & 0xFFFF) * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble[b3] = (short)(int)((arrayOfDouble[b3] & 0xFFFF) * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 3:
          arrayOfDouble = null;
          f = 1.0F / ((1 << this.nBits[k]) - 1);
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble = (int[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble);
              float f1 = arrayOfDouble[k] * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble[b3] = (int)(arrayOfDouble[b3] * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 2:
          arrayOfDouble = null;
          f = 3.051851E-5F;
          b2 = 0;
          while (b2 < j) {
            int i1 = m;
            byte b = 0;
            while (b < i) {
              arrayOfDouble = (short[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble);
              float f1 = arrayOfDouble[k] * f;
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b3 = 0; b3 < k; b3++)
                  arrayOfDouble[b3] = (short)(int)(arrayOfDouble[b3] * f2 + 0.5F); 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } 
              b++;
              i1++;
            } 
            b2++;
            n++;
          } 
          break;
        case 4:
          arrayOfDouble = null;
          b1 = 0;
          while (b1 < j) {
            int i1 = m;
            b2 = 0;
            while (b2 < i) {
              arrayOfDouble = (float[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble);
              float f1 = arrayOfDouble[k];
              if (f1 != 0.0F) {
                float f2 = 1.0F / f1;
                for (byte b = 0; b < k; b++)
                  arrayOfDouble[b] = arrayOfDouble[b] * f2; 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } 
              b2++;
              i1++;
            } 
            b1++;
            n++;
          } 
          break;
        case 5:
          arrayOfDouble = null;
          b1 = 0;
          while (b1 < j) {
            int i1 = m;
            b2 = 0;
            while (b2 < i) {
              arrayOfDouble = (double[])paramWritableRaster.getDataElements(i1, n, arrayOfDouble);
              double d = arrayOfDouble[k];
              if (d != 0.0D) {
                double d1 = 1.0D / d;
                for (byte b = 0; b < k; b++)
                  arrayOfDouble[b] = arrayOfDouble[b] * d1; 
                paramWritableRaster.setDataElements(i1, n, arrayOfDouble);
              } 
              b2++;
              i1++;
            } 
            b1++;
            n++;
          } 
          break;
        default:
          throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
      } 
    } 
    return !this.signed ? new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, paramBoolean, this.transparency, this.transferType) : new ComponentColorModel(this.colorSpace, this.supportsAlpha, paramBoolean, this.transparency, this.transferType);
  }
  
  public boolean isCompatibleRaster(Raster paramRaster) {
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (sampleModel instanceof ComponentSampleModel) {
      if (sampleModel.getNumBands() != getNumComponents())
        return false; 
      for (byte b = 0; b < this.nBits.length; b++) {
        if (sampleModel.getSampleSize(b) < this.nBits[b])
          return false; 
      } 
      return (paramRaster.getTransferType() == this.transferType);
    } 
    return false;
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    int i = paramInt1 * paramInt2 * this.numComponents;
    null = null;
    switch (this.transferType) {
      case 0:
      case 1:
        return Raster.createInterleavedRaster(this.transferType, paramInt1, paramInt2, this.numComponents, null);
    } 
    SampleModel sampleModel = createCompatibleSampleModel(paramInt1, paramInt2);
    DataBuffer dataBuffer = sampleModel.createDataBuffer();
    return Raster.createWritableRaster(sampleModel, dataBuffer, null);
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[this.numComponents];
    for (byte b = 0; b < this.numComponents; b++)
      arrayOfInt[b] = b; 
    switch (this.transferType) {
      case 0:
      case 1:
        return new PixelInterleavedSampleModel(this.transferType, paramInt1, paramInt2, this.numComponents, paramInt1 * this.numComponents, arrayOfInt);
    } 
    return new ComponentSampleModel(this.transferType, paramInt1, paramInt2, this.numComponents, paramInt1 * this.numComponents, arrayOfInt);
  }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel) { return !(paramSampleModel instanceof ComponentSampleModel) ? false : ((this.numComponents != paramSampleModel.getNumBands()) ? false : (!(paramSampleModel.getTransferType() != this.transferType))); }
  
  public WritableRaster getAlphaRaster(WritableRaster paramWritableRaster) {
    if (!hasAlpha())
      return null; 
    int i = paramWritableRaster.getMinX();
    int j = paramWritableRaster.getMinY();
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = paramWritableRaster.getNumBands() - 1;
    return paramWritableRaster.createWritableChild(i, j, paramWritableRaster.getWidth(), paramWritableRaster.getHeight(), i, j, arrayOfInt);
  }
  
  public boolean equals(Object paramObject) { return !super.equals(paramObject) ? false : (!(paramObject.getClass() != getClass())); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ComponentColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */