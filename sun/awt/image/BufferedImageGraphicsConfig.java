package sun.awt.image;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class BufferedImageGraphicsConfig extends GraphicsConfiguration {
  private static final int numconfigs = 12;
  
  private static BufferedImageGraphicsConfig[] configs = new BufferedImageGraphicsConfig[12];
  
  GraphicsDevice gd;
  
  ColorModel model;
  
  Raster raster;
  
  int width;
  
  int height;
  
  public static BufferedImageGraphicsConfig getConfig(BufferedImage paramBufferedImage) {
    int i = paramBufferedImage.getType();
    if (i > 0 && i < 12) {
      BufferedImageGraphicsConfig bufferedImageGraphicsConfig1 = configs[i];
      if (bufferedImageGraphicsConfig1 != null)
        return bufferedImageGraphicsConfig1; 
    } 
    BufferedImageGraphicsConfig bufferedImageGraphicsConfig = new BufferedImageGraphicsConfig(paramBufferedImage, null);
    if (i > 0 && i < 12)
      configs[i] = bufferedImageGraphicsConfig; 
    return bufferedImageGraphicsConfig;
  }
  
  public BufferedImageGraphicsConfig(BufferedImage paramBufferedImage, Component paramComponent) {
    if (paramComponent == null) {
      this.gd = new BufferedImageDevice(this);
    } else {
      Graphics2D graphics2D = (Graphics2D)paramComponent.getGraphics();
      this.gd = graphics2D.getDeviceConfiguration().getDevice();
    } 
    this.model = paramBufferedImage.getColorModel();
    this.raster = paramBufferedImage.getRaster().createCompatibleWritableRaster(1, 1);
    this.width = paramBufferedImage.getWidth();
    this.height = paramBufferedImage.getHeight();
  }
  
  public GraphicsDevice getDevice() { return this.gd; }
  
  public BufferedImage createCompatibleImage(int paramInt1, int paramInt2) {
    WritableRaster writableRaster = this.raster.createCompatibleWritableRaster(paramInt1, paramInt2);
    return new BufferedImage(this.model, writableRaster, this.model.isAlphaPremultiplied(), null);
  }
  
  public ColorModel getColorModel() { return this.model; }
  
  public ColorModel getColorModel(int paramInt) {
    if (this.model.getTransparency() == paramInt)
      return this.model; 
    switch (paramInt) {
      case 1:
        return new DirectColorModel(24, 16711680, 65280, 255);
      case 2:
        return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
        return ColorModel.getRGBdefault();
    } 
    return null;
  }
  
  public AffineTransform getDefaultTransform() { return new AffineTransform(); }
  
  public AffineTransform getNormalizingTransform() { return new AffineTransform(); }
  
  public Rectangle getBounds() { return new Rectangle(0, 0, this.width, this.height); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\BufferedImageGraphicsConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */