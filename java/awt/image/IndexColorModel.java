package java.awt.image;

import java.awt.color.ColorSpace;
import java.math.BigInteger;
import java.util.Arrays;
import sun.awt.image.BufImgSurfaceData;

public class IndexColorModel extends ColorModel {
  private int[] rgb;
  
  private int map_size;
  
  private int pixel_mask;
  
  private int transparent_index = -1;
  
  private boolean allgrayopaque;
  
  private BigInteger validBits;
  
  private BufImgSurfaceData.ICMColorData colorData = null;
  
  private static int[] opaqueBits = { 8, 8, 8 };
  
  private static int[] alphaBits = { 8, 8, 8, 8 };
  
  private static final int CACHESIZE = 40;
  
  private int[] lookupcache = new int[40];
  
  private static native void initIDs();
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(paramInt1));
    if (paramInt1 < 1 || paramInt1 > 16)
      throw new IllegalArgumentException("Number of bits must be between 1 and 16."); 
    setRGBs(paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, null);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt3) {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(paramInt1));
    if (paramInt1 < 1 || paramInt1 > 16)
      throw new IllegalArgumentException("Number of bits must be between 1 and 16."); 
    setRGBs(paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, null);
    setTransparentPixel(paramInt3);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4) {
    super(paramInt1, alphaBits, ColorSpace.getInstance(1000), true, false, 3, ColorModel.getDefaultTransferType(paramInt1));
    if (paramInt1 < 1 || paramInt1 > 16)
      throw new IllegalArgumentException("Number of bits must be between 1 and 16."); 
    setRGBs(paramInt2, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, paramArrayOfByte4);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, boolean paramBoolean) {
    this(paramInt1, paramInt2, paramArrayOfByte, paramInt3, paramBoolean, -1);
    if (paramInt1 < 1 || paramInt1 > 16)
      throw new IllegalArgumentException("Number of bits must be between 1 and 16."); 
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3, boolean paramBoolean, int paramInt4) {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(paramInt1));
    if (paramInt1 < 1 || paramInt1 > 16)
      throw new IllegalArgumentException("Number of bits must be between 1 and 16."); 
    if (paramInt2 < 1)
      throw new IllegalArgumentException("Map size (" + paramInt2 + ") must be >= 1"); 
    this.map_size = paramInt2;
    this.rgb = new int[calcRealMapSize(paramInt1, paramInt2)];
    int i = paramInt3;
    short s = 255;
    boolean bool = true;
    byte b1 = 1;
    for (byte b2 = 0; b2 < paramInt2; b2++) {
      byte b3 = paramArrayOfByte[i++] & 0xFF;
      byte b4 = paramArrayOfByte[i++] & 0xFF;
      byte b5 = paramArrayOfByte[i++] & 0xFF;
      bool = (bool && b3 == b4 && b4 == b5) ? 1 : 0;
      if (paramBoolean) {
        s = paramArrayOfByte[i++] & 0xFF;
        if (s != 255) {
          if (s == 0) {
            if (b1 == 1)
              b1 = 2; 
            if (this.transparent_index < 0)
              this.transparent_index = b2; 
          } else {
            b1 = 3;
          } 
          bool = false;
        } 
      } 
      this.rgb[b2] = s << 24 | b3 << 16 | b4 << 8 | b5;
    } 
    this.allgrayopaque = bool;
    setTransparency(b1);
    setTransparentPixel(paramInt4);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, boolean paramBoolean, int paramInt4, int paramInt5) {
    super(paramInt1, opaqueBits, ColorSpace.getInstance(1000), false, false, 1, paramInt5);
    if (paramInt1 < 1 || paramInt1 > 16)
      throw new IllegalArgumentException("Number of bits must be between 1 and 16."); 
    if (paramInt2 < 1)
      throw new IllegalArgumentException("Map size (" + paramInt2 + ") must be >= 1"); 
    if (paramInt5 != 0 && paramInt5 != 1)
      throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT"); 
    setRGBs(paramInt2, paramArrayOfInt, paramInt3, paramBoolean);
    setTransparentPixel(paramInt4);
    calculatePixelMask();
  }
  
  public IndexColorModel(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, BigInteger paramBigInteger) {
    super(paramInt1, alphaBits, ColorSpace.getInstance(1000), true, false, 3, paramInt4);
    if (paramInt1 < 1 || paramInt1 > 16)
      throw new IllegalArgumentException("Number of bits must be between 1 and 16."); 
    if (paramInt2 < 1)
      throw new IllegalArgumentException("Map size (" + paramInt2 + ") must be >= 1"); 
    if (paramInt4 != 0 && paramInt4 != 1)
      throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT"); 
    if (paramBigInteger != null)
      for (byte b = 0; b < paramInt2; b++) {
        if (!paramBigInteger.testBit(b)) {
          this.validBits = paramBigInteger;
          break;
        } 
      }  
    setRGBs(paramInt2, paramArrayOfInt, paramInt3, true);
    calculatePixelMask();
  }
  
  private void setRGBs(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4) {
    if (paramInt < 1)
      throw new IllegalArgumentException("Map size (" + paramInt + ") must be >= 1"); 
    this.map_size = paramInt;
    this.rgb = new int[calcRealMapSize(this.pixel_bits, paramInt)];
    short s = 255;
    byte b1 = 1;
    boolean bool = true;
    for (byte b2 = 0; b2 < paramInt; b2++) {
      byte b3 = paramArrayOfByte1[b2] & 0xFF;
      byte b4 = paramArrayOfByte2[b2] & 0xFF;
      byte b5 = paramArrayOfByte3[b2] & 0xFF;
      bool = (bool && b3 == b4 && b4 == b5) ? 1 : 0;
      if (paramArrayOfByte4 != null) {
        s = paramArrayOfByte4[b2] & 0xFF;
        if (s != 255) {
          if (s == 0) {
            if (b1 == 1)
              b1 = 2; 
            if (this.transparent_index < 0)
              this.transparent_index = b2; 
          } else {
            b1 = 3;
          } 
          bool = false;
        } 
      } 
      this.rgb[b2] = s << 24 | b3 << 16 | b4 << 8 | b5;
    } 
    this.allgrayopaque = bool;
    setTransparency(b1);
  }
  
  private void setRGBs(int paramInt1, int[] paramArrayOfInt, int paramInt2, boolean paramBoolean) {
    this.map_size = paramInt1;
    this.rgb = new int[calcRealMapSize(this.pixel_bits, paramInt1)];
    int i = paramInt2;
    byte b1 = 1;
    boolean bool = true;
    BigInteger bigInteger = this.validBits;
    byte b2 = 0;
    while (b2 < paramInt1) {
      if (bigInteger == null || bigInteger.testBit(b2)) {
        int j = paramArrayOfInt[i];
        int k = j >> 16 & 0xFF;
        int m = j >> 8 & 0xFF;
        int n = j & 0xFF;
        bool = (bool && k == m && m == n) ? 1 : 0;
        if (paramBoolean) {
          int i1 = j >>> 24;
          if (i1 != 255) {
            if (i1 == 0) {
              if (b1 == 1)
                b1 = 2; 
              if (this.transparent_index < 0)
                this.transparent_index = b2; 
            } else {
              b1 = 3;
            } 
            bool = false;
          } 
        } else {
          j |= 0xFF000000;
        } 
        this.rgb[b2] = j;
      } 
      b2++;
      i++;
    } 
    this.allgrayopaque = bool;
    setTransparency(b1);
  }
  
  private int calcRealMapSize(int paramInt1, int paramInt2) {
    int i = Math.max(1 << paramInt1, paramInt2);
    return Math.max(i, 256);
  }
  
  private BigInteger getAllValid() {
    int i = (this.map_size + 7) / 8;
    byte[] arrayOfByte = new byte[i];
    Arrays.fill(arrayOfByte, (byte)-1);
    arrayOfByte[0] = (byte)(255 >>> i * 8 - this.map_size);
    return new BigInteger(1, arrayOfByte);
  }
  
  public int getTransparency() { return this.transparency; }
  
  public int[] getComponentSize() {
    if (this.nBits == null) {
      if (this.supportsAlpha) {
        this.nBits = new int[4];
        this.nBits[3] = 8;
      } else {
        this.nBits = new int[3];
      } 
      this.nBits[2] = 8;
      this.nBits[1] = 8;
      this.nBits[0] = 8;
    } 
    return (int[])this.nBits.clone();
  }
  
  public final int getMapSize() { return this.map_size; }
  
  public final int getTransparentPixel() { return this.transparent_index; }
  
  public final void getReds(byte[] paramArrayOfByte) {
    for (byte b = 0; b < this.map_size; b++)
      paramArrayOfByte[b] = (byte)(this.rgb[b] >> 16); 
  }
  
  public final void getGreens(byte[] paramArrayOfByte) {
    for (byte b = 0; b < this.map_size; b++)
      paramArrayOfByte[b] = (byte)(this.rgb[b] >> 8); 
  }
  
  public final void getBlues(byte[] paramArrayOfByte) {
    for (byte b = 0; b < this.map_size; b++)
      paramArrayOfByte[b] = (byte)this.rgb[b]; 
  }
  
  public final void getAlphas(byte[] paramArrayOfByte) {
    for (byte b = 0; b < this.map_size; b++)
      paramArrayOfByte[b] = (byte)(this.rgb[b] >> 24); 
  }
  
  public final void getRGBs(int[] paramArrayOfInt) { System.arraycopy(this.rgb, 0, paramArrayOfInt, 0, this.map_size); }
  
  private void setTransparentPixel(int paramInt) {
    if (paramInt >= 0 && paramInt < this.map_size) {
      this.rgb[paramInt] = this.rgb[paramInt] & 0xFFFFFF;
      this.transparent_index = paramInt;
      this.allgrayopaque = false;
      if (this.transparency == 1)
        setTransparency(2); 
    } 
  }
  
  private void setTransparency(int paramInt) {
    if (this.transparency != paramInt) {
      this.transparency = paramInt;
      if (paramInt == 1) {
        this.supportsAlpha = false;
        this.numComponents = 3;
        this.nBits = opaqueBits;
      } else {
        this.supportsAlpha = true;
        this.numComponents = 4;
        this.nBits = alphaBits;
      } 
    } 
  }
  
  private final void calculatePixelMask() {
    int i = this.pixel_bits;
    if (i == 3) {
      i = 4;
    } else if (i > 4 && i < 8) {
      i = 8;
    } 
    this.pixel_mask = (1 << i) - 1;
  }
  
  public final int getRed(int paramInt) { return this.rgb[paramInt & this.pixel_mask] >> 16 & 0xFF; }
  
  public final int getGreen(int paramInt) { return this.rgb[paramInt & this.pixel_mask] >> 8 & 0xFF; }
  
  public final int getBlue(int paramInt) { return this.rgb[paramInt & this.pixel_mask] & 0xFF; }
  
  public final int getAlpha(int paramInt) { return this.rgb[paramInt & this.pixel_mask] >> 24 & 0xFF; }
  
  public final int getRGB(int paramInt) { return this.rgb[paramInt & this.pixel_mask]; }
  
  public Object getDataElements(int paramInt, Object paramObject) {
    int i = paramInt >> 16 & 0xFF;
    int j = paramInt >> 8 & 0xFF;
    int k = paramInt & 0xFF;
    int m = paramInt >>> 24;
    int n = 0;
    int i1;
    for (i1 = 38; i1 >= 0 && (n = this.lookupcache[i1]) != 0; i1 -= 2) {
      if (paramInt == this.lookupcache[i1 + 1])
        return installpixel(paramObject, n ^ 0xFFFFFFFF); 
    } 
    if (this.allgrayopaque) {
      i1 = 256;
      int i2 = (i * 77 + j * 150 + k * 29 + 128) / 256;
      for (int i3 = 0; i3 < this.map_size; i3++) {
        if (this.rgb[i3] != 0) {
          int i4 = (this.rgb[i3] & 0xFF) - i2;
          if (i4 < 0)
            i4 = -i4; 
          if (i4 < i1) {
            n = i3;
            if (i4 == 0)
              break; 
            i1 = i4;
          } 
        } 
      } 
    } else if (this.transparency == 1) {
      i1 = Integer.MAX_VALUE;
      int[] arrayOfInt = this.rgb;
      int i2;
      for (i2 = 0; i2 < this.map_size; i2++) {
        int i3 = arrayOfInt[i2];
        if (i3 == paramInt && i3 != 0) {
          n = i2;
          i1 = 0;
          break;
        } 
      } 
      if (i1 != 0)
        for (i2 = 0; i2 < this.map_size; i2++) {
          int i3 = arrayOfInt[i2];
          if (i3 != 0) {
            int i4 = (i3 >> 16 & 0xFF) - i;
            int i5 = i4 * i4;
            if (i5 < i1) {
              i4 = (i3 >> 8 & 0xFF) - j;
              i5 += i4 * i4;
              if (i5 < i1) {
                i4 = (i3 & 0xFF) - k;
                i5 += i4 * i4;
                if (i5 < i1) {
                  n = i2;
                  i1 = i5;
                } 
              } 
            } 
          } 
        }  
    } else if (m == 0 && this.transparent_index >= 0) {
      n = this.transparent_index;
    } else {
      i1 = Integer.MAX_VALUE;
      int[] arrayOfInt = this.rgb;
      for (int i2 = 0; i2 < this.map_size; i2++) {
        int i3 = arrayOfInt[i2];
        if (i3 == paramInt) {
          if (this.validBits == null || this.validBits.testBit(i2)) {
            n = i2;
            break;
          } 
        } else {
          int i4 = (i3 >> 16 & 0xFF) - i;
          int i5 = i4 * i4;
          if (i5 < i1) {
            i4 = (i3 >> 8 & 0xFF) - j;
            i5 += i4 * i4;
            if (i5 < i1) {
              i4 = (i3 & 0xFF) - k;
              i5 += i4 * i4;
              if (i5 < i1) {
                i4 = (i3 >>> 24) - m;
                i5 += i4 * i4;
                if (i5 < i1 && (this.validBits == null || this.validBits.testBit(i2))) {
                  n = i2;
                  i1 = i5;
                } 
              } 
            } 
          } 
        } 
      } 
    } 
    System.arraycopy(this.lookupcache, 2, this.lookupcache, 0, 38);
    this.lookupcache[39] = paramInt;
    this.lookupcache[38] = n ^ 0xFFFFFFFF;
    return installpixel(paramObject, n);
  }
  
  private Object installpixel(Object paramObject, int paramInt) {
    short[] arrayOfShort2;
    byte[] arrayOfByte;
    int[] arrayOfInt;
    short[] arrayOfShort1;
    switch (this.transferType) {
      case 3:
        if (paramObject == null) {
          arrayOfShort1 = arrayOfInt = new int[1];
        } else {
          arrayOfInt = (int[])arrayOfShort1;
        } 
        arrayOfInt[0] = paramInt;
        return arrayOfShort1;
      case 0:
        if (arrayOfShort1 == null) {
          arrayOfShort1 = arrayOfByte = new byte[1];
        } else {
          arrayOfByte = (byte[])arrayOfShort1;
        } 
        arrayOfByte[0] = (byte)paramInt;
        return arrayOfShort1;
      case 1:
        if (arrayOfShort1 == null) {
          arrayOfShort1 = arrayOfShort2 = new short[1];
        } else {
          arrayOfShort2 = (short[])arrayOfShort1;
        } 
        arrayOfShort2[0] = (short)paramInt;
        return arrayOfShort1;
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int[] getComponents(int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt2 + this.numComponents]; 
    paramArrayOfInt[paramInt2 + 0] = getRed(paramInt1);
    paramArrayOfInt[paramInt2 + 1] = getGreen(paramInt1);
    paramArrayOfInt[paramInt2 + 2] = getBlue(paramInt1);
    if (this.supportsAlpha && paramArrayOfInt.length - paramInt2 > 3)
      paramArrayOfInt[paramInt2 + 3] = getAlpha(paramInt1); 
    return paramArrayOfInt;
  }
  
  public int[] getComponents(Object paramObject, int[] paramArrayOfInt, int paramInt) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i;
    short s;
    byte b;
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])paramObject;
        b = arrayOfByte[0] & 0xFF;
        return getComponents(b, paramArrayOfInt, paramInt);
      case 1:
        arrayOfShort = (short[])paramObject;
        s = arrayOfShort[0] & 0xFFFF;
        return getComponents(s, paramArrayOfInt, paramInt);
      case 3:
        arrayOfInt = (int[])paramObject;
        i = arrayOfInt[0];
        return getComponents(i, paramArrayOfInt, paramInt);
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public int getDataElement(int[] paramArrayOfInt, int paramInt) {
    int[] arrayOfInt;
    short[] arrayOfShort;
    byte[] arrayOfByte;
    int i = paramArrayOfInt[paramInt + 0] << 16 | paramArrayOfInt[paramInt + 1] << 8 | paramArrayOfInt[paramInt + 2];
    if (this.supportsAlpha) {
      i |= paramArrayOfInt[paramInt + 3] << 24;
    } else {
      i |= 0xFF000000;
    } 
    Object object = getDataElements(i, null);
    switch (this.transferType) {
      case 0:
        arrayOfByte = (byte[])object;
        return arrayOfByte[0] & 0xFF;
      case 1:
        arrayOfShort = (short[])object;
        return arrayOfShort[0];
      case 3:
        arrayOfInt = (int[])object;
        return arrayOfInt[0];
    } 
    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
  }
  
  public Object getDataElements(int[] paramArrayOfInt, int paramInt, Object paramObject) {
    int i = paramArrayOfInt[paramInt + 0] << 16 | paramArrayOfInt[paramInt + 1] << 8 | paramArrayOfInt[paramInt + 2];
    if (this.supportsAlpha) {
      i |= paramArrayOfInt[paramInt + 3] << 24;
    } else {
      i &= 0xFF000000;
    } 
    return getDataElements(i, paramObject);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    WritableRaster writableRaster;
    if (this.pixel_bits == 1 || this.pixel_bits == 2 || this.pixel_bits == 4) {
      writableRaster = Raster.createPackedRaster(0, paramInt1, paramInt2, 1, this.pixel_bits, null);
    } else if (this.pixel_bits <= 8) {
      writableRaster = Raster.createInterleavedRaster(0, paramInt1, paramInt2, 1, null);
    } else if (this.pixel_bits <= 16) {
      writableRaster = Raster.createInterleavedRaster(1, paramInt1, paramInt2, 1, null);
    } else {
      throw new UnsupportedOperationException("This method is not supported  for pixel bits > 16.");
    } 
    return writableRaster;
  }
  
  public boolean isCompatibleRaster(Raster paramRaster) {
    int i = paramRaster.getSampleModel().getSampleSize(0);
    return (paramRaster.getTransferType() == this.transferType && paramRaster.getNumBands() == 1 && 1 << i >= this.map_size);
  }
  
  public SampleModel createCompatibleSampleModel(int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 0;
    return (this.pixel_bits == 1 || this.pixel_bits == 2 || this.pixel_bits == 4) ? new MultiPixelPackedSampleModel(this.transferType, paramInt1, paramInt2, this.pixel_bits) : new ComponentSampleModel(this.transferType, paramInt1, paramInt2, 1, paramInt1, arrayOfInt);
  }
  
  public boolean isCompatibleSampleModel(SampleModel paramSampleModel) { return (!(paramSampleModel instanceof ComponentSampleModel) && !(paramSampleModel instanceof MultiPixelPackedSampleModel)) ? false : ((paramSampleModel.getTransferType() != this.transferType) ? false : (!(paramSampleModel.getNumBands() != 1))); }
  
  public BufferedImage convertToIntDiscrete(Raster paramRaster, boolean paramBoolean) {
    DirectColorModel directColorModel;
    if (!isCompatibleRaster(paramRaster))
      throw new IllegalArgumentException("This raster is not compatiblewith this IndexColorModel."); 
    if (paramBoolean || this.transparency == 3) {
      directColorModel = ColorModel.getRGBdefault();
    } else if (this.transparency == 2) {
      directColorModel = new DirectColorModel(25, 16711680, 65280, 255, 16777216);
    } else {
      directColorModel = new DirectColorModel(24, 16711680, 65280, 255);
    } 
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    WritableRaster writableRaster = directColorModel.createCompatibleWritableRaster(i, j);
    Object object = null;
    int[] arrayOfInt = null;
    int k = paramRaster.getMinX();
    int m = paramRaster.getMinY();
    byte b = 0;
    while (b < j) {
      object = paramRaster.getDataElements(k, m, i, 1, object);
      if (object instanceof int[]) {
        arrayOfInt = (int[])object;
      } else {
        arrayOfInt = DataBuffer.toIntArray(object);
      } 
      for (byte b1 = 0; b1 < i; b1++)
        arrayOfInt[b1] = this.rgb[arrayOfInt[b1] & this.pixel_mask]; 
      writableRaster.setDataElements(0, b, i, 1, arrayOfInt);
      b++;
      m++;
    } 
    return new BufferedImage(directColorModel, writableRaster, false, null);
  }
  
  public boolean isValid(int paramInt) { return (paramInt >= 0 && paramInt < this.map_size && (this.validBits == null || this.validBits.testBit(paramInt))); }
  
  public boolean isValid() { return (this.validBits == null); }
  
  public BigInteger getValidPixels() { return (this.validBits == null) ? getAllValid() : this.validBits; }
  
  public void finalize() {}
  
  public String toString() { return new String("IndexColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + this.numComponents + " color space = " + this.colorSpace + " transparency = " + this.transparency + " transIndex   = " + this.transparent_index + " has alpha = " + this.supportsAlpha + " isAlphaPre = " + this.isAlphaPremultiplied); }
  
  static  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\IndexColorModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */