package java.awt.color;

import java.io.Serializable;
import sun.java2d.cmm.CMSManager;

public abstract class ColorSpace implements Serializable {
  static final long serialVersionUID = -409452704308689724L;
  
  private int type;
  
  private int numComponents;
  
  private String[] compName = null;
  
  private static ColorSpace sRGBspace;
  
  private static ColorSpace XYZspace;
  
  private static ColorSpace PYCCspace;
  
  private static ColorSpace GRAYspace;
  
  private static ColorSpace LINEAR_RGBspace;
  
  public static final int TYPE_XYZ = 0;
  
  public static final int TYPE_Lab = 1;
  
  public static final int TYPE_Luv = 2;
  
  public static final int TYPE_YCbCr = 3;
  
  public static final int TYPE_Yxy = 4;
  
  public static final int TYPE_RGB = 5;
  
  public static final int TYPE_GRAY = 6;
  
  public static final int TYPE_HSV = 7;
  
  public static final int TYPE_HLS = 8;
  
  public static final int TYPE_CMYK = 9;
  
  public static final int TYPE_CMY = 11;
  
  public static final int TYPE_2CLR = 12;
  
  public static final int TYPE_3CLR = 13;
  
  public static final int TYPE_4CLR = 14;
  
  public static final int TYPE_5CLR = 15;
  
  public static final int TYPE_6CLR = 16;
  
  public static final int TYPE_7CLR = 17;
  
  public static final int TYPE_8CLR = 18;
  
  public static final int TYPE_9CLR = 19;
  
  public static final int TYPE_ACLR = 20;
  
  public static final int TYPE_BCLR = 21;
  
  public static final int TYPE_CCLR = 22;
  
  public static final int TYPE_DCLR = 23;
  
  public static final int TYPE_ECLR = 24;
  
  public static final int TYPE_FCLR = 25;
  
  public static final int CS_sRGB = 1000;
  
  public static final int CS_LINEAR_RGB = 1004;
  
  public static final int CS_CIEXYZ = 1001;
  
  public static final int CS_PYCC = 1002;
  
  public static final int CS_GRAY = 1003;
  
  protected ColorSpace(int paramInt1, int paramInt2) {
    this.type = paramInt1;
    this.numComponents = paramInt2;
  }
  
  public static ColorSpace getInstance(int paramInt) {
    ColorSpace colorSpace;
    switch (paramInt) {
      case 1000:
        synchronized (ColorSpace.class) {
          if (sRGBspace == null) {
            ICC_Profile iCC_Profile = ICC_Profile.getInstance(1000);
            sRGBspace = new ICC_ColorSpace(iCC_Profile);
          } 
          colorSpace = sRGBspace;
        } 
        return colorSpace;
      case 1001:
        synchronized (ColorSpace.class) {
          if (XYZspace == null) {
            ICC_Profile iCC_Profile = ICC_Profile.getInstance(1001);
            XYZspace = new ICC_ColorSpace(iCC_Profile);
          } 
          colorSpace = XYZspace;
        } 
        return colorSpace;
      case 1002:
        synchronized (ColorSpace.class) {
          if (PYCCspace == null) {
            ICC_Profile iCC_Profile = ICC_Profile.getInstance(1002);
            PYCCspace = new ICC_ColorSpace(iCC_Profile);
          } 
          colorSpace = PYCCspace;
        } 
        return colorSpace;
      case 1003:
        synchronized (ColorSpace.class) {
          if (GRAYspace == null) {
            ICC_Profile iCC_Profile = ICC_Profile.getInstance(1003);
            GRAYspace = new ICC_ColorSpace(iCC_Profile);
            CMSManager.GRAYspace = GRAYspace;
          } 
          colorSpace = GRAYspace;
        } 
        return colorSpace;
      case 1004:
        synchronized (ColorSpace.class) {
          if (LINEAR_RGBspace == null) {
            ICC_Profile iCC_Profile = ICC_Profile.getInstance(1004);
            LINEAR_RGBspace = new ICC_ColorSpace(iCC_Profile);
            CMSManager.LINEAR_RGBspace = LINEAR_RGBspace;
          } 
          colorSpace = LINEAR_RGBspace;
        } 
        return colorSpace;
    } 
    throw new IllegalArgumentException("Unknown color space");
  }
  
  public boolean isCS_sRGB() { return (this == sRGBspace); }
  
  public abstract float[] toRGB(float[] paramArrayOfFloat);
  
  public abstract float[] fromRGB(float[] paramArrayOfFloat);
  
  public abstract float[] toCIEXYZ(float[] paramArrayOfFloat);
  
  public abstract float[] fromCIEXYZ(float[] paramArrayOfFloat);
  
  public int getType() { return this.type; }
  
  public int getNumComponents() { return this.numComponents; }
  
  public String getName(int paramInt) {
    if (paramInt < 0 || paramInt > this.numComponents - 1)
      throw new IllegalArgumentException("Component index out of range: " + paramInt); 
    if (this.compName == null) {
      switch (this.type) {
        case 0:
          this.compName = new String[] { "X", "Y", "Z" };
          return this.compName[paramInt];
        case 1:
          this.compName = new String[] { "L", "a", "b" };
          return this.compName[paramInt];
        case 2:
          this.compName = new String[] { "L", "u", "v" };
          return this.compName[paramInt];
        case 3:
          this.compName = new String[] { "Y", "Cb", "Cr" };
          return this.compName[paramInt];
        case 4:
          this.compName = new String[] { "Y", "x", "y" };
          return this.compName[paramInt];
        case 5:
          this.compName = new String[] { "Red", "Green", "Blue" };
          return this.compName[paramInt];
        case 6:
          this.compName = new String[] { "Gray" };
          return this.compName[paramInt];
        case 7:
          this.compName = new String[] { "Hue", "Saturation", "Value" };
          return this.compName[paramInt];
        case 8:
          this.compName = new String[] { "Hue", "Lightness", "Saturation" };
          return this.compName[paramInt];
        case 9:
          this.compName = new String[] { "Cyan", "Magenta", "Yellow", "Black" };
          return this.compName[paramInt];
        case 11:
          this.compName = new String[] { "Cyan", "Magenta", "Yellow" };
          return this.compName[paramInt];
      } 
      String[] arrayOfString = new String[this.numComponents];
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfString[b] = "Unnamed color component(" + b + ")"; 
      this.compName = arrayOfString;
    } 
    return this.compName[paramInt];
  }
  
  public float getMinValue(int paramInt) {
    if (paramInt < 0 || paramInt > this.numComponents - 1)
      throw new IllegalArgumentException("Component index out of range: " + paramInt); 
    return 0.0F;
  }
  
  public float getMaxValue(int paramInt) {
    if (paramInt < 0 || paramInt > this.numComponents - 1)
      throw new IllegalArgumentException("Component index out of range: " + paramInt); 
    return 1.0F;
  }
  
  static boolean isCS_CIEXYZ(ColorSpace paramColorSpace) { return (paramColorSpace == XYZspace); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\color\ColorSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */