package java.awt;

import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import sun.awt.image.SurfaceManager;

public abstract class Image {
  private static ImageCapabilities defaultImageCaps = new ImageCapabilities(false);
  
  protected float accelerationPriority = 0.5F;
  
  public static final Object UndefinedProperty = new Object();
  
  public static final int SCALE_DEFAULT = 1;
  
  public static final int SCALE_FAST = 2;
  
  public static final int SCALE_SMOOTH = 4;
  
  public static final int SCALE_REPLICATE = 8;
  
  public static final int SCALE_AREA_AVERAGING = 16;
  
  SurfaceManager surfaceManager;
  
  public abstract int getWidth(ImageObserver paramImageObserver);
  
  public abstract int getHeight(ImageObserver paramImageObserver);
  
  public abstract ImageProducer getSource();
  
  public abstract Graphics getGraphics();
  
  public abstract Object getProperty(String paramString, ImageObserver paramImageObserver);
  
  public Image getScaledInstance(int paramInt1, int paramInt2, int paramInt3) {
    ReplicateScaleFilter replicateScaleFilter;
    if ((paramInt3 & 0x14) != 0) {
      replicateScaleFilter = new AreaAveragingScaleFilter(paramInt1, paramInt2);
    } else {
      replicateScaleFilter = new ReplicateScaleFilter(paramInt1, paramInt2);
    } 
    FilteredImageSource filteredImageSource = new FilteredImageSource(getSource(), replicateScaleFilter);
    return Toolkit.getDefaultToolkit().createImage(filteredImageSource);
  }
  
  public void flush() {
    if (this.surfaceManager != null)
      this.surfaceManager.flush(); 
  }
  
  public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration) { return (this.surfaceManager != null) ? this.surfaceManager.getCapabilities(paramGraphicsConfiguration) : defaultImageCaps; }
  
  public void setAccelerationPriority(float paramFloat) {
    if (paramFloat < 0.0F || paramFloat > 1.0F)
      throw new IllegalArgumentException("Priority must be a value between 0 and 1, inclusive"); 
    this.accelerationPriority = paramFloat;
    if (this.surfaceManager != null)
      this.surfaceManager.setAccelerationPriority(this.accelerationPriority); 
  }
  
  public float getAccelerationPriority() { return this.accelerationPriority; }
  
  static  {
    SurfaceManager.setImageAccessor(new SurfaceManager.ImageAccessor() {
          public SurfaceManager getSurfaceManager(Image param1Image) { return param1Image.surfaceManager; }
          
          public void setSurfaceManager(Image param1Image, SurfaceManager param1SurfaceManager) { param1Image.surfaceManager = param1SurfaceManager; }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Image.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */