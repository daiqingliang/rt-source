package java.awt.image;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.java2d.cmm.CMSManager;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.PCMM;

public abstract class ColorModel implements Transparency {
  private long pData;
  
  protected int pixel_bits;
  
  int[] nBits;
  
  int transparency = 3;
  
  boolean supportsAlpha = true;
  
  boolean isAlphaPremultiplied = false;
  
  int numComponents = -1;
  
  int numColorComponents = -1;
  
  ColorSpace colorSpace = ColorSpace.getInstance(1000);
  
  int colorSpaceType = 5;
  
  int maxBits;
  
  boolean is_sRGB = true;
  
  protected int transferType;
  
  private static boolean loaded = false;
  
  private static ColorModel RGBdefault;
  
  static byte[] l8Tos8;
  
  static byte[] s8Tol8;
  
  static byte[] l16Tos8;
  
  static short[] s8Tol16;
  
  static Map<ICC_ColorSpace, byte[]> g8Tos8Map;
  
  static Map<ICC_ColorSpace, byte[]> lg16Toog8Map;
  
  static Map<ICC_ColorSpace, byte[]> g16Tos8Map;
  
  static Map<ICC_ColorSpace, short[]> lg16Toog16Map;
  
  static void loadLibraries() {
    if (!loaded) {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              System.loadLibrary("awt");
              return null;
            }
          });
      loaded = true;
    } 
  }
  
  private static native void initIDs();
  
  public static ColorModel getRGBdefault() {
    if (RGBdefault == null)
      RGBdefault = new DirectColorModel(32, 16711680, 65280, 255, -16777216); 
    return RGBdefault;
  }
  
  public ColorModel(int paramInt) {
    this.pixel_bits = paramInt;
    if (paramInt < 1)
      throw new IllegalArgumentException("Number of bits must be > 0"); 
    this.numComponents = 4;
    this.numColorComponents = 3;
    this.maxBits = paramInt;
    this.transferType = getDefaultTransferType(paramInt);
  }
  
  protected ColorModel(int paramInt1, int[] paramArrayOfInt, ColorSpace paramColorSpace, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3) {
    this.colorSpace = paramColorSpace;
    this.colorSpaceType = paramColorSpace.getType();
    this.numColorComponents = paramColorSpace.getNumComponents();
    this.numComponents = this.numColorComponents + (paramBoolean1 ? 1 : 0);
    this.supportsAlpha = paramBoolean1;
    if (paramArrayOfInt.length < this.numComponents)
      throw new IllegalArgumentException("Number of color/alpha components should be " + this.numComponents + " but length of bits array is " + paramArrayOfInt.length); 
    if (paramInt2 < 1 || paramInt2 > 3)
      throw new IllegalArgumentException("Unknown transparency: " + paramInt2); 
    if (!this.supportsAlpha) {
      this.isAlphaPremultiplied = false;
      this.transparency = 1;
    } else {
      this.isAlphaPremultiplied = paramBoolean2;
      this.transparency = paramInt2;
    } 
    this.nBits = (int[])paramArrayOfInt.clone();
    this.pixel_bits = paramInt1;
    if (paramInt1 <= 0)
      throw new IllegalArgumentException("Number of pixel bits must be > 0"); 
    this.maxBits = 0;
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      if (paramArrayOfInt[b] < 0)
        throw new IllegalArgumentException("Number of bits must be >= 0"); 
      if (this.maxBits < paramArrayOfInt[b])
        this.maxBits = paramArrayOfInt[b]; 
    } 
    if (this.maxBits == 0)
      throw new IllegalArgumentException("There must be at least one component with > 0 pixel bits."); 
    if (paramColorSpace != ColorSpace.getInstance('Ϩ'))
      this.is_sRGB = false; 
    this.transferType = paramInt3;
  }
  
  public final boolean hasAlpha() { return this.supportsAlpha; }
  
  public final boolean isAlphaPremultiplied() { return this.isAlphaPremultiplied; }
  
  public final int getTransferType() { return this.transferType; }
  
  public int getPixelSize() { return this.pixel_bits; }
  
  public int getComponentSize(int paramInt) {
    if (this.nBits == null)
      throw new NullPointerException("Number of bits array is null."); 
    return this.nBits[paramInt];
  }
  
  public int[] getComponentSize() { return (this.nBits != null) ? (int[])this.nBits.clone() : null; }
  
  public int getTransparency() { return this.transparency; }
  
  public int getNumComponents() { return this.numComponents; }
  
  public int getNumColorComponents() { return this.numColorComponents; }
  
  public abstract int getRed(int paramInt);
  
  public abstract int getGreen(int paramInt);
  
  public abstract int getBlue(int paramInt);
  
  public abstract int getAlpha(int paramInt);
  
  public int getRGB(int paramInt) { return getAlpha(paramInt) << 24 | getRed(paramInt) << 16 | getGreen(paramInt) << 8 | getBlue(paramInt) << 0; }
  
  public int getRed(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    int j = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        j = arrayOfByte.length;
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        j = arrayOfShort.length;
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        j = arrayOfInt.length;
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (j == 1)
      return getRed(i); 
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getGreen(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    int j = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        j = arrayOfByte.length;
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        j = arrayOfShort.length;
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        j = arrayOfInt.length;
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (j == 1)
      return getGreen(i); 
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getBlue(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    int j = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        j = arrayOfByte.length;
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        j = arrayOfShort.length;
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        j = arrayOfInt.length;
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (j == 1)
      return getBlue(i); 
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getAlpha(Object paramObject) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = 0;
    int j = 0;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        i = arrayOfByte[0] & 0xFF;
        j = arrayOfByte.length;
        break;
      case 1:
        arrayOfShort = (short[])paramObject;
        i = arrayOfShort[0] & 0xFFFF;
        j = arrayOfShort.length;
        break;
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        j = arrayOfInt.length;
        break;
      default:
        throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
    } 
    if (j == 1)
      return getAlpha(i); 
    throw new UnsupportedOperationException("This method is not supported by this color model");
  }
  
  public int getRGB(Object paramObject) { return getAlpha(paramObject) << 24 | getRed(paramObject) << 16 | getGreen(paramObject) << 8 | getBlue(paramObject) << 0; }
  
  public Object getDataElements(int paramInt, Object paramObject) { throw new UnsupportedOperationException("This method is not supported by this color model."); }
  
  public int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2) { throw new UnsupportedOperationException("This method is not supported by this color model."); }
  
  public int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt) { throw new UnsupportedOperationException("This method is not supported by this color model."); }
  
  public int[] getUnnormalizedComponents(float[] paramArrayOfFloat, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    if (this.colorSpace == null)
      throw new UnsupportedOperationException("This method is not supported by this color model."); 
    if (this.nBits == null)
      throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component."); 
    if (paramArrayOfFloat.length - paramInt1 < this.numComponents)
      throw new IllegalArgumentException("Incorrect number of components.  Expecting " + this.numComponents); 
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt2 + this.numComponents]; 
    if (this.supportsAlpha && this.isAlphaPremultiplied) {
      float f = paramArrayOfFloat[paramInt1 + this.numColorComponents];
      for (int i = 0; i < this.numColorComponents; i++)
        paramArrayOfInt[paramInt2 + i] = (int)(paramArrayOfFloat[paramInt1 + i] * ((1 << this.nBits[i]) - 1) * f + 0.5F); 
      paramArrayOfInt[paramInt2 + this.numColorComponents] = (int)(f * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5F);
    } else {
      for (int i = 0; i < this.numComponents; i++)
        paramArrayOfInt[paramInt2 + i] = (int)(paramArrayOfFloat[paramInt1 + i] * ((1 << this.nBits[i]) - 1) + 0.5F); 
    } 
    return paramArrayOfInt;
  }
  
  public float[] getNormalizedComponents(int[] paramArrayOfInt, int paramInt1, float[] paramArrayOfFloat, int paramInt2) {
    if (this.colorSpace == null)
      throw new UnsupportedOperationException("This method is not supported by this color model."); 
    if (this.nBits == null)
      throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component."); 
    if (paramArrayOfInt.length - paramInt1 < this.numComponents)
      throw new IllegalArgumentException("Incorrect number of components.  Expecting " + this.numComponents); 
    if (paramArrayOfFloat == null)
      paramArrayOfFloat = new float[this.numComponents + paramInt2]; 
    if (this.supportsAlpha && this.isAlphaPremultiplied) {
      float f = paramArrayOfInt[paramInt1 + this.numColorComponents];
      f /= ((1 << this.nBits[this.numColorComponents]) - 1);
      if (f != 0.0F) {
        for (int i = 0; i < this.numColorComponents; i++)
          paramArrayOfFloat[paramInt2 + i] = paramArrayOfInt[paramInt1 + i] / f * ((1 << this.nBits[i]) - 1); 
      } else {
        for (int i = 0; i < this.numColorComponents; i++)
          paramArrayOfFloat[paramInt2 + i] = 0.0F; 
      } 
      paramArrayOfFloat[paramInt2 + this.numColorComponents] = f;
    } else {
      for (int i = 0; i < this.numComponents; i++)
        paramArrayOfFloat[paramInt2 + i] = paramArrayOfInt[paramInt1 + i] / ((1 << this.nBits[i]) - 1); 
    } 
    return paramArrayOfFloat;
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt) { throw new UnsupportedOperationException("This method is not supported by this color model."); }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject) { throw new UnsupportedOperationException("This method has not been implemented for this color model."); }
  
  public int getDataElement(float[] paramArrayOfFloat, int paramInt) {
    int[] arrayOfInt = getUnnormalizedComponents(paramArrayOfFloat, paramInt, null, 0);
    return getDataElement(arrayOfInt, 0);
  }
  
  public Object getDataElements(float[] paramArrayOfFloat, int paramInt, Object paramObject) {
    int[] arrayOfInt = getUnnormalizedComponents(paramArrayOfFloat, paramInt, null, 0);
    return getDataElements(arrayOfInt, 0, paramObject);
  }
  
  public float[] getNormalizedComponents(Object paramObject, float[] paramArrayOfFloat, int paramInt) {
    int[] arrayOfInt = getComponents(paramObject, null, 0);
    return getNormalizedComponents(arrayOfInt, 0, paramArrayOfFloat, paramInt);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ColorModel))
      return false; 
    ColorModel colorModel = (ColorModel)paramObject;
    if (this == colorModel)
      return true; 
    if (this.supportsAlpha != colorModel.hasAlpha() || this.isAlphaPremultiplied != colorModel.isAlphaPremultiplied() || this.pixel_bits != colorModel.getPixelSize() || this.transparency != colorModel.getTransparency() || this.numComponents != colorModel.getNumComponents())
      return false; 
    int[] arrayOfInt = colorModel.getComponentSize();
    if (this.nBits != null && arrayOfInt != null) {
      for (byte b = 0; b < this.numComponents; b++) {
        if (this.nBits[b] != arrayOfInt[b])
          return false; 
      } 
    } else {
      return (this.nBits == null && arrayOfInt == null);
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 0;
    i = (this.supportsAlpha ? 2 : 3) + (this.isAlphaPremultiplied ? 4 : 5) + this.pixel_bits * 6 + this.transparency * 7 + this.numComponents * 8;
    if (this.nBits != null)
      for (byte b = 0; b < this.numComponents; b++)
        i += this.nBits[b] * (b + 9);  
    return i;
  }
  
  public final ColorSpace getColorSpace() { return this.colorSpace; }
  
  public ColorModel coerceData(WritableRaster paramWritableRaster, boolean paramBoolean) { throw new UnsupportedOperationException("This method is not supported by this color model"); }
  
  public boolean isCompatibleRaster(Raster paramRaster) { throw new UnsupportedOperationException("This method has not been implemented for this ColorModel."); }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) { throw new UnsupportedOperationException("This method is not supported by this color model"); }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) { throw new UnsupportedOperationException("This method is not supported by this color model"); }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel) { throw new UnsupportedOperationException("This method is not supported by this color model"); }
  
  public void finalize() {}
  
  public WritableRaster getAlphaRaster(WritableRaster paramWritableRaster) { return null; }
  
  public String toString() { return new String("ColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + this.numComponents + " color space = " + this.colorSpace + " transparency = " + this.transparency + " has alpha = " + this.supportsAlpha + " isAlphaPre = " + this.isAlphaPremultiplied); }
  
  static int getDefaultTransferType(int paramInt) { return (paramInt <= 8) ? 0 : ((paramInt <= 16) ? 1 : ((paramInt <= 32) ? 3 : 32)); }
  
  static boolean isLinearRGBspace(ColorSpace paramColorSpace) { return (paramColorSpace == CMSManager.LINEAR_RGBspace); }
  
  static boolean isLinearGRAYspace(ColorSpace paramColorSpace) { return (paramColorSpace == CMSManager.GRAYspace); }
  
  static byte[] getLinearRGB8TosRGB8LUT() {
    if (l8Tos8 == null) {
      l8Tos8 = new byte[256];
      for (byte b = 0; b <= 'ÿ'; b++) {
        float f2;
        float f1 = b / 255.0F;
        if (f1 <= 0.0031308F) {
          f2 = f1 * 12.92F;
        } else {
          f2 = 1.055F * (float)Math.pow(f1, 0.4166666666666667D) - 0.055F;
        } 
        l8Tos8[b] = (byte)Math.round(f2 * 255.0F);
      } 
    } 
    return l8Tos8;
  }
  
  static byte[] getsRGB8ToLinearRGB8LUT() {
    if (s8Tol8 == null) {
      s8Tol8 = new byte[256];
      for (byte b = 0; b <= 'ÿ'; b++) {
        float f2;
        float f1 = b / 255.0F;
        if (f1 <= 0.04045F) {
          f2 = f1 / 12.92F;
        } else {
          f2 = (float)Math.pow(((f1 + 0.055F) / 1.055F), 2.4D);
        } 
        s8Tol8[b] = (byte)Math.round(f2 * 255.0F);
      } 
    } 
    return s8Tol8;
  }
  
  static byte[] getLinearRGB16TosRGB8LUT() {
    if (l16Tos8 == null) {
      l16Tos8 = new byte[65536];
      for (byte b = 0; b <= '￿'; b++) {
        float f2;
        float f1 = b / 65535.0F;
        if (f1 <= 0.0031308F) {
          f2 = f1 * 12.92F;
        } else {
          f2 = 1.055F * (float)Math.pow(f1, 0.4166666666666667D) - 0.055F;
        } 
        l16Tos8[b] = (byte)Math.round(f2 * 255.0F);
      } 
    } 
    return l16Tos8;
  }
  
  static short[] getsRGB8ToLinearRGB16LUT() {
    if (s8Tol16 == null) {
      s8Tol16 = new short[256];
      for (byte b = 0; b <= 'ÿ'; b++) {
        float f2;
        float f1 = b / 255.0F;
        if (f1 <= 0.04045F) {
          f2 = f1 / 12.92F;
        } else {
          f2 = (float)Math.pow(((f1 + 0.055F) / 1.055F), 2.4D);
        } 
        s8Tol16[b] = (short)Math.round(f2 * 65535.0F);
      } 
    } 
    return s8Tol16;
  }
  
  static byte[] getGray8TosRGB8LUT(ICC_ColorSpace paramICC_ColorSpace) {
    if (isLinearGRAYspace(paramICC_ColorSpace))
      return getLinearRGB8TosRGB8LUT(); 
    if (g8Tos8Map != null) {
      byte[] arrayOfByte = (byte[])g8Tos8Map.get(paramICC_ColorSpace);
      if (arrayOfByte != null)
        return arrayOfByte; 
    } 
    byte[] arrayOfByte1 = new byte[256];
    for (byte b1 = 0; b1 <= 'ÿ'; b1++)
      arrayOfByte1[b1] = (byte)b1; 
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM pCMM = CMSManager.getModule();
    ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
    arrayOfColorTransform[0] = pCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = pCMM.createTransform(iCC_ColorSpace.getProfile(), -1, 2);
    ColorTransform colorTransform = pCMM.createTransform(arrayOfColorTransform);
    byte[] arrayOfByte2 = colorTransform.colorConvert(arrayOfByte1, null);
    byte b2 = 0;
    for (byte b3 = 2; b2 <= 'ÿ'; b3 += 3) {
      arrayOfByte1[b2] = arrayOfByte2[b3];
      b2++;
    } 
    if (g8Tos8Map == null)
      g8Tos8Map = Collections.synchronizedMap(new WeakHashMap(2)); 
    g8Tos8Map.put(paramICC_ColorSpace, arrayOfByte1);
    return arrayOfByte1;
  }
  
  static byte[] getLinearGray16ToOtherGray8LUT(ICC_ColorSpace paramICC_ColorSpace) {
    if (lg16Toog8Map != null) {
      byte[] arrayOfByte1 = (byte[])lg16Toog8Map.get(paramICC_ColorSpace);
      if (arrayOfByte1 != null)
        return arrayOfByte1; 
    } 
    short[] arrayOfShort = new short[65536];
    for (byte b1 = 0; b1 <= '￿'; b1++)
      arrayOfShort[b1] = (short)b1; 
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM pCMM = CMSManager.getModule();
    ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1003);
    arrayOfColorTransform[0] = pCMM.createTransform(iCC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = pCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 2);
    ColorTransform colorTransform = pCMM.createTransform(arrayOfColorTransform);
    arrayOfShort = colorTransform.colorConvert(arrayOfShort, null);
    byte[] arrayOfByte = new byte[65536];
    for (byte b2 = 0; b2 <= '￿'; b2++)
      arrayOfByte[b2] = (byte)(int)((arrayOfShort[b2] & 0xFFFF) * 0.0038910506F + 0.5F); 
    if (lg16Toog8Map == null)
      lg16Toog8Map = Collections.synchronizedMap(new WeakHashMap(2)); 
    lg16Toog8Map.put(paramICC_ColorSpace, arrayOfByte);
    return arrayOfByte;
  }
  
  static byte[] getGray16TosRGB8LUT(ICC_ColorSpace paramICC_ColorSpace) {
    if (isLinearGRAYspace(paramICC_ColorSpace))
      return getLinearRGB16TosRGB8LUT(); 
    if (g16Tos8Map != null) {
      byte[] arrayOfByte1 = (byte[])g16Tos8Map.get(paramICC_ColorSpace);
      if (arrayOfByte1 != null)
        return arrayOfByte1; 
    } 
    short[] arrayOfShort = new short[65536];
    for (byte b1 = 0; b1 <= '￿'; b1++)
      arrayOfShort[b1] = (short)b1; 
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM pCMM = CMSManager.getModule();
    ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1000);
    arrayOfColorTransform[0] = pCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = pCMM.createTransform(iCC_ColorSpace.getProfile(), -1, 2);
    ColorTransform colorTransform = pCMM.createTransform(arrayOfColorTransform);
    arrayOfShort = colorTransform.colorConvert(arrayOfShort, null);
    byte[] arrayOfByte = new byte[65536];
    byte b2 = 0;
    for (byte b3 = 2; b2 <= '￿'; b3 += 3) {
      arrayOfByte[b2] = (byte)(int)((arrayOfShort[b3] & 0xFFFF) * 0.0038910506F + 0.5F);
      b2++;
    } 
    if (g16Tos8Map == null)
      g16Tos8Map = Collections.synchronizedMap(new WeakHashMap(2)); 
    g16Tos8Map.put(paramICC_ColorSpace, arrayOfByte);
    return arrayOfByte;
  }
  
  static short[] getLinearGray16ToOtherGray16LUT(ICC_ColorSpace paramICC_ColorSpace) {
    if (lg16Toog16Map != null) {
      short[] arrayOfShort = (short[])lg16Toog16Map.get(paramICC_ColorSpace);
      if (arrayOfShort != null)
        return arrayOfShort; 
    } 
    short[] arrayOfShort1 = new short[65536];
    for (byte b = 0; b <= '￿'; b++)
      arrayOfShort1[b] = (short)b; 
    ColorTransform[] arrayOfColorTransform = new ColorTransform[2];
    PCMM pCMM = CMSManager.getModule();
    ICC_ColorSpace iCC_ColorSpace = (ICC_ColorSpace)ColorSpace.getInstance(1003);
    arrayOfColorTransform[0] = pCMM.createTransform(iCC_ColorSpace.getProfile(), -1, 1);
    arrayOfColorTransform[1] = pCMM.createTransform(paramICC_ColorSpace.getProfile(), -1, 2);
    ColorTransform colorTransform = pCMM.createTransform(arrayOfColorTransform);
    short[] arrayOfShort2 = colorTransform.colorConvert(arrayOfShort1, null);
    if (lg16Toog16Map == null)
      lg16Toog16Map = Collections.synchronizedMap(new WeakHashMap(2)); 
    lg16Toog16Map.put(paramICC_ColorSpace, arrayOfShort2);
    return arrayOfShort2;
  }
  
  static  {
    loadLibraries();
    initIDs();
    l8Tos8 = null;
    s8Tol8 = null;
    l16Tos8 = null;
    s8Tol16 = null;
    g8Tos8Map = null;
    lg16Toog8Map = null;
    g16Tos8Map = null;
    lg16Toog16Map = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */