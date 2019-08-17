package java.awt.image;

import java.util.Hashtable;

public interface ImageConsumer {
  public static final int RANDOMPIXELORDER = 1;
  
  public static final int TOPDOWNLEFTRIGHT = 2;
  
  public static final int COMPLETESCANLINES = 4;
  
  public static final int SINGLEPASS = 8;
  
  public static final int SINGLEFRAME = 16;
  
  public static final int IMAGEERROR = 1;
  
  public static final int SINGLEFRAMEDONE = 2;
  
  public static final int STATICIMAGEDONE = 3;
  
  public static final int IMAGEABORTED = 4;
  
  void setDimensions(int paramInt1, int paramInt2);
  
  void setProperties(Hashtable<?, ?> paramHashtable);
  
  void setColorModel(ColorModel paramColorModel);
  
  void setHints(int paramInt);
  
  void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, byte[] paramArrayOfByte, int paramInt5, int paramInt6);
  
  void setPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ColorModel paramColorModel, int[] paramArrayOfInt, int paramInt5, int paramInt6);
  
  void imageComplete(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ImageConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */