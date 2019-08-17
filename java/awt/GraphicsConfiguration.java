package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;
import sun.awt.image.SunVolatileImage;

public abstract class GraphicsConfiguration {
  private static BufferCapabilities defaultBufferCaps;
  
  private static ImageCapabilities defaultImageCaps;
  
  public abstract GraphicsDevice getDevice();
  
  public BufferedImage createCompatibleImage(int paramInt1, int paramInt2) {
    ColorModel colorModel = getColorModel();
    WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
    return new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
  }
  
  public BufferedImage createCompatibleImage(int paramInt1, int paramInt2, int paramInt3) {
    if (getColorModel().getTransparency() == paramInt3)
      return createCompatibleImage(paramInt1, paramInt2); 
    ColorModel colorModel = getColorModel(paramInt3);
    if (colorModel == null)
      throw new IllegalArgumentException("Unknown transparency: " + paramInt3); 
    WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(paramInt1, paramInt2);
    return new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2) {
    VolatileImage volatileImage = null;
    try {
      volatileImage = createCompatibleVolatileImage(paramInt1, paramInt2, null, 1);
    } catch (AWTException aWTException) {
      assert false;
    } 
    return volatileImage;
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3) {
    VolatileImage volatileImage = null;
    try {
      volatileImage = createCompatibleVolatileImage(paramInt1, paramInt2, null, paramInt3);
    } catch (AWTException aWTException) {
      assert false;
    } 
    return volatileImage;
  }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities) throws AWTException { return createCompatibleVolatileImage(paramInt1, paramInt2, paramImageCapabilities, 1); }
  
  public VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, ImageCapabilities paramImageCapabilities, int paramInt3) throws AWTException {
    SunVolatileImage sunVolatileImage = new SunVolatileImage(this, paramInt1, paramInt2, paramInt3, paramImageCapabilities);
    if (paramImageCapabilities != null && paramImageCapabilities.isAccelerated() && !sunVolatileImage.getCapabilities().isAccelerated())
      throw new AWTException("Supplied image capabilities could not be met by this graphics configuration."); 
    return sunVolatileImage;
  }
  
  public abstract ColorModel getColorModel();
  
  public abstract ColorModel getColorModel(int paramInt);
  
  public abstract AffineTransform getDefaultTransform();
  
  public abstract AffineTransform getNormalizingTransform();
  
  public abstract Rectangle getBounds();
  
  public BufferCapabilities getBufferCapabilities() {
    if (defaultBufferCaps == null)
      defaultBufferCaps = new DefaultBufferCapabilities(getImageCapabilities()); 
    return defaultBufferCaps;
  }
  
  public ImageCapabilities getImageCapabilities() {
    if (defaultImageCaps == null)
      defaultImageCaps = new ImageCapabilities(false); 
    return defaultImageCaps;
  }
  
  public boolean isTranslucencyCapable() { return false; }
  
  private static class DefaultBufferCapabilities extends BufferCapabilities {
    public DefaultBufferCapabilities(ImageCapabilities param1ImageCapabilities) { super(param1ImageCapabilities, param1ImageCapabilities, null); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\GraphicsConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */