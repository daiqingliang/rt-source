package sun.awt.image;

import java.awt.image.ColorModel;

public class PixelConverter {
  public static final PixelConverter instance = new PixelConverter();
  
  protected int alphaMask = 0;
  
  public int rgbToPixel(int paramInt, ColorModel paramColorModel) {
    short[] arrayOfShort;
    byte[] arrayOfByte;
    Object object = paramColorModel.getDataElements(paramInt, null);
    switch (paramColorModel.getTransferType()) {
      case 0:
        arrayOfByte = (byte[])object;
        null = 0;
        switch (arrayOfByte.length) {
          default:
            null = arrayOfByte[3] << 24;
          case 3:
            null |= (arrayOfByte[2] & 0xFF) << 16;
          case 2:
            null |= (arrayOfByte[1] & 0xFF) << 8;
            break;
          case 1:
            break;
        } 
        return arrayOfByte[0] & 0xFF;
      case 1:
      case 2:
        arrayOfShort = (short[])object;
        return ((arrayOfShort.length > 1) ? (arrayOfShort[1] << 16) : 0) | arrayOfShort[0] & 0xFFFF;
      case 3:
        return (int[])object[0];
    } 
    return paramInt;
  }
  
  public int pixelToRgb(int paramInt, ColorModel paramColorModel) { return paramInt; }
  
  public final int getAlphaMask() { return this.alphaMask; }
  
  public static class Argb extends PixelConverter {
    public static final PixelConverter instance = new Argb();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return param1Int; }
  }
  
  public static class ArgbBm extends PixelConverter {
    public static final PixelConverter instance = new ArgbBm();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int | param1Int >> 31 << 24; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return param1Int << 7 >> 7; }
  }
  
  public static class ArgbPre extends PixelConverter {
    public static final PixelConverter instance = new ArgbPre();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) {
      if (param1Int >> 24 == -1)
        return param1Int; 
      int i = param1Int >>> 24;
      int j = param1Int >> 16 & 0xFF;
      int k = param1Int >> 8 & 0xFF;
      int m = param1Int & 0xFF;
      int n = i + (i >> 7);
      j = j * n >> 8;
      k = k * n >> 8;
      m = m * n >> 8;
      return i << 24 | j << 16 | k << 8 | m;
    }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int >>> 24;
      if (i == 255 || i == 0)
        return param1Int; 
      int j = param1Int >> 16 & 0xFF;
      int k = param1Int >> 8 & 0xFF;
      int m = param1Int & 0xFF;
      j = ((j << 8) - j) / i;
      k = ((k << 8) - k) / i;
      m = ((m << 8) - m) / i;
      return i << 24 | j << 16 | k << 8 | m;
    }
  }
  
  public static class Bgrx extends PixelConverter {
    public static final PixelConverter instance = new Bgrx();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int << 24 | (param1Int & 0xFF00) << 8 | param1Int >> 8 & 0xFF00; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return 0xFF000000 | (param1Int & 0xFF00) << 8 | param1Int >> 8 & 0xFF00 | param1Int >>> 24; }
  }
  
  public static class ByteGray extends PixelConverter {
    static final double RED_MULT = 0.299D;
    
    static final double GRN_MULT = 0.587D;
    
    static final double BLU_MULT = 0.114D;
    
    public static final PixelConverter instance = new ByteGray();
    
    private ByteGray() {}
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int >> 16 & 0xFF;
      int j = param1Int >> 8 & 0xFF;
      int k = param1Int & 0xFF;
      return (int)(i * 0.299D + j * 0.587D + k * 0.114D + 0.5D);
    }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return ((0xFF00 | param1Int) << 8 | param1Int) << 8 | param1Int; }
  }
  
  public static class Rgba extends PixelConverter {
    public static final PixelConverter instance = new Rgba();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int << 8 | param1Int >>> 24; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return param1Int << 24 | param1Int >>> 8; }
  }
  
  public static class RgbaPre extends PixelConverter {
    public static final PixelConverter instance = new RgbaPre();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) {
      if (param1Int >> 24 == -1)
        return param1Int << 8 | param1Int >>> 24; 
      int i = param1Int >>> 24;
      int j = param1Int >> 16 & 0xFF;
      int k = param1Int >> 8 & 0xFF;
      int m = param1Int & 0xFF;
      int n = i + (i >> 7);
      j = j * n >> 8;
      k = k * n >> 8;
      m = m * n >> 8;
      return j << 24 | k << 16 | m << 8 | i;
    }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int & 0xFF;
      if (i == 255 || i == 0)
        return param1Int >>> 8 | param1Int << 24; 
      int j = param1Int >>> 24;
      int k = param1Int >> 16 & 0xFF;
      int m = param1Int >> 8 & 0xFF;
      j = ((j << 8) - j) / i;
      k = ((k << 8) - k) / i;
      m = ((m << 8) - m) / i;
      return j << 24 | k << 16 | m << 8 | i;
    }
  }
  
  public static class Rgbx extends PixelConverter {
    public static final PixelConverter instance = new Rgbx();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int << 8; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return 0xFF000000 | param1Int >> 8; }
  }
  
  public static class Ushort4444Argb extends PixelConverter {
    public static final PixelConverter instance = new Ushort4444Argb();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int >> 16 & 0xF000;
      int j = param1Int >> 12 & 0xF00;
      int k = param1Int >> 8 & 0xF0;
      int m = param1Int >> 4 & 0xF;
      return i | j | k | m;
    }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int & 0xF000;
      i = (param1Int << 16 | param1Int << 12) & 0xFF000000;
      int j = param1Int & 0xF00;
      j = (param1Int << 12 | param1Int << 8) & 0xFF0000;
      int k = param1Int & 0xF0;
      k = (param1Int << 8 | param1Int << 4) & 0xFF00;
      int m = param1Int & 0xF;
      m = (param1Int << 4 | param1Int << 0) & 0xFF;
      return i | j | k | m;
    }
  }
  
  public static class Ushort555Rgb extends PixelConverter {
    public static final PixelConverter instance = new Ushort555Rgb();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int >> 9 & 0x7C00 | param1Int >> 6 & 0x3E0 | param1Int >> 3 & 0x1F; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int >> 10 & 0x1F;
      i = i << 3 | i >> 2;
      int j = param1Int >> 5 & 0x1F;
      j = j << 3 | j >> 2;
      int k = param1Int & 0x1F;
      k = k << 3 | k >> 2;
      return 0xFF000000 | i << 16 | j << 8 | k;
    }
  }
  
  public static class Ushort555Rgbx extends PixelConverter {
    public static final PixelConverter instance = new Ushort555Rgbx();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int >> 8 & 0xF800 | param1Int >> 5 & 0x7C0 | param1Int >> 2 & 0x3E; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int >> 11 & 0x1F;
      i = i << 3 | i >> 2;
      int j = param1Int >> 6 & 0x1F;
      j = j << 3 | j >> 2;
      int k = param1Int >> 1 & 0x1F;
      k = k << 3 | k >> 2;
      return 0xFF000000 | i << 16 | j << 8 | k;
    }
  }
  
  public static class Ushort565Rgb extends PixelConverter {
    public static final PixelConverter instance = new Ushort565Rgb();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int >> 8 & 0xF800 | param1Int >> 5 & 0x7E0 | param1Int >> 3 & 0x1F; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int >> 11 & 0x1F;
      i = i << 3 | i >> 2;
      int j = param1Int >> 5 & 0x3F;
      j = j << 2 | j >> 4;
      int k = param1Int & 0x1F;
      k = k << 3 | k >> 2;
      return 0xFF000000 | i << 16 | j << 8 | k;
    }
  }
  
  public static class UshortGray extends ByteGray {
    static final double SHORT_MULT = 257.0D;
    
    static final double USHORT_RED_MULT = 76.843D;
    
    static final double USHORT_GRN_MULT = 150.85899999999998D;
    
    static final double USHORT_BLU_MULT = 29.298000000000002D;
    
    public static final PixelConverter instance = new UshortGray();
    
    private UshortGray() { super(null); }
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) {
      int i = param1Int >> 16 & 0xFF;
      int j = param1Int >> 8 & 0xFF;
      int k = param1Int & 0xFF;
      return (int)(i * 76.843D + j * 150.85899999999998D + k * 29.298000000000002D + 0.5D);
    }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) {
      param1Int >>= 8;
      return ((0xFF00 | param1Int) << 8 | param1Int) << 8 | param1Int;
    }
  }
  
  public static class Xbgr extends PixelConverter {
    public static final PixelConverter instance = new Xbgr();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return (param1Int & 0xFF) << 16 | param1Int & 0xFF00 | param1Int >> 16 & 0xFF; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return 0xFF000000 | (param1Int & 0xFF) << 16 | param1Int & 0xFF00 | param1Int >> 16 & 0xFF; }
  }
  
  public static class Xrgb extends PixelConverter {
    public static final PixelConverter instance = new Xrgb();
    
    public int rgbToPixel(int param1Int, ColorModel param1ColorModel) { return param1Int; }
    
    public int pixelToRgb(int param1Int, ColorModel param1ColorModel) { return 0xFF000000 | param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\PixelConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */