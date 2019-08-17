package com.sun.imageio.plugins.bmp;

public class BMPCompressionTypes {
  private static final String[] compressionTypeNames = { "BI_RGB", "BI_RLE8", "BI_RLE4", "BI_BITFIELDS", "BI_JPEG", "BI_PNG" };
  
  static int getType(String paramString) {
    for (byte b = 0; b < compressionTypeNames.length; b++) {
      if (compressionTypeNames[b].equals(paramString))
        return b; 
    } 
    return 0;
  }
  
  static String getName(int paramInt) { return compressionTypeNames[paramInt]; }
  
  public static String[] getCompressionTypes() { return (String[])compressionTypeNames.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\bmp\BMPCompressionTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */