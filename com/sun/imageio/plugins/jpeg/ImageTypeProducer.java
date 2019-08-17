package com.sun.imageio.plugins.jpeg;

import javax.imageio.ImageTypeSpecifier;

class ImageTypeProducer {
  private ImageTypeSpecifier type = null;
  
  boolean failed = false;
  
  private int csCode;
  
  private static final ImageTypeProducer[] defaultTypes = new ImageTypeProducer[12];
  
  public ImageTypeProducer(int paramInt) { this.csCode = paramInt; }
  
  public ImageTypeProducer() { this.csCode = -1; }
  
  public ImageTypeSpecifier getType() {
    if (!this.failed && this.type == null)
      try {
        this.type = produce();
      } catch (Throwable throwable) {
        this.failed = true;
      }  
    return this.type;
  }
  
  public static ImageTypeProducer getTypeProducer(int paramInt) {
    if (paramInt < 0 || paramInt >= 12)
      return null; 
    if (defaultTypes[paramInt] == null)
      defaultTypes[paramInt] = new ImageTypeProducer(paramInt); 
    return defaultTypes[paramInt];
  }
  
  protected ImageTypeSpecifier produce() {
    switch (this.csCode) {
      case 1:
        return ImageTypeSpecifier.createFromBufferedImageType(10);
      case 2:
        return ImageTypeSpecifier.createInterleaved(JPEG.JCS.sRGB, JPEG.bOffsRGB, 0, false, false);
      case 6:
        return ImageTypeSpecifier.createPacked(JPEG.JCS.sRGB, -16777216, 16711680, 65280, 255, 3, false);
      case 5:
        return (JPEG.JCS.getYCC() != null) ? ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[2], 0, false, false) : null;
      case 10:
        return (JPEG.JCS.getYCC() != null) ? ImageTypeSpecifier.createInterleaved(JPEG.JCS.getYCC(), JPEG.bandOffsets[3], 0, true, false) : null;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\ImageTypeProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */