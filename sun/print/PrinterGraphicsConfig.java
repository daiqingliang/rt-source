package sun.print;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;

public class PrinterGraphicsConfig extends GraphicsConfiguration {
  static ColorModel theModel;
  
  GraphicsDevice gd;
  
  int pageWidth;
  
  int pageHeight;
  
  AffineTransform deviceTransform;
  
  public PrinterGraphicsConfig(String paramString, AffineTransform paramAffineTransform, int paramInt1, int paramInt2) {
    this.pageWidth = paramInt1;
    this.pageHeight = paramInt2;
    this.deviceTransform = paramAffineTransform;
    this.gd = new PrinterGraphicsDevice(this, paramString);
  }
  
  public GraphicsDevice getDevice() { return this.gd; }
  
  public ColorModel getColorModel() {
    if (theModel == null) {
      BufferedImage bufferedImage = new BufferedImage(1, 1, 5);
      theModel = bufferedImage.getColorModel();
    } 
    return theModel;
  }
  
  public ColorModel getColorModel(int paramInt) {
    switch (paramInt) {
      case 1:
        return getColorModel();
      case 2:
        return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
        return ColorModel.getRGBdefault();
    } 
    return null;
  }
  
  public AffineTransform getDefaultTransform() { return new AffineTransform(this.deviceTransform); }
  
  public AffineTransform getNormalizingTransform() { return new AffineTransform(); }
  
  public Rectangle getBounds() { return new Rectangle(0, 0, this.pageWidth, this.pageHeight); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PrinterGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */