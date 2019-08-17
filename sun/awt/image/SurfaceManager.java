package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;

public abstract class SurfaceManager {
  private static ImageAccessor imgaccessor;
  
  private ConcurrentHashMap<Object, Object> cacheMap;
  
  public static void setImageAccessor(ImageAccessor paramImageAccessor) {
    if (imgaccessor != null)
      throw new InternalError("Attempt to set ImageAccessor twice"); 
    imgaccessor = paramImageAccessor;
  }
  
  public static SurfaceManager getManager(Image paramImage) {
    SurfaceManager surfaceManager = imgaccessor.getSurfaceManager(paramImage);
    if (surfaceManager == null)
      try {
        BufferedImage bufferedImage = (BufferedImage)paramImage;
        (surfaceManager = new BufImgSurfaceManager(bufferedImage)).setManager(bufferedImage, surfaceManager);
      } catch (ClassCastException classCastException) {
        throw new IllegalArgumentException("Invalid Image variant");
      }  
    return surfaceManager;
  }
  
  public static void setManager(Image paramImage, SurfaceManager paramSurfaceManager) { imgaccessor.setSurfaceManager(paramImage, paramSurfaceManager); }
  
  public Object getCacheData(Object paramObject) { return (this.cacheMap == null) ? null : this.cacheMap.get(paramObject); }
  
  public void setCacheData(Object paramObject1, Object paramObject2) {
    if (this.cacheMap == null)
      synchronized (this) {
        if (this.cacheMap == null)
          this.cacheMap = new ConcurrentHashMap(2); 
      }  
    this.cacheMap.put(paramObject1, paramObject2);
  }
  
  public abstract SurfaceData getPrimarySurfaceData();
  
  public abstract SurfaceData restoreContents();
  
  public void acceleratedSurfaceLost() {}
  
  public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration) { return new ImageCapabilitiesGc(paramGraphicsConfiguration); }
  
  public void flush() { flush(false); }
  
  void flush(boolean paramBoolean) {
    if (this.cacheMap != null) {
      Iterator iterator = this.cacheMap.values().iterator();
      while (iterator.hasNext()) {
        Object object = iterator.next();
        if (object instanceof FlushableCacheData && ((FlushableCacheData)object).flush(paramBoolean))
          iterator.remove(); 
      } 
    } 
  }
  
  public void setAccelerationPriority(float paramFloat) {
    if (paramFloat == 0.0F)
      flush(true); 
  }
  
  public static int getImageScale(Image paramImage) {
    if (!(paramImage instanceof java.awt.image.VolatileImage))
      return 1; 
    SurfaceManager surfaceManager = getManager(paramImage);
    return surfaceManager.getPrimarySurfaceData().getDefaultScale();
  }
  
  public static interface FlushableCacheData {
    boolean flush(boolean param1Boolean);
  }
  
  public static abstract class ImageAccessor {
    public abstract SurfaceManager getSurfaceManager(Image param1Image);
    
    public abstract void setSurfaceManager(Image param1Image, SurfaceManager param1SurfaceManager);
  }
  
  class ImageCapabilitiesGc extends ImageCapabilities {
    GraphicsConfiguration gc;
    
    public ImageCapabilitiesGc(GraphicsConfiguration param1GraphicsConfiguration) {
      super(false);
      this.gc = param1GraphicsConfiguration;
    }
    
    public boolean isAccelerated() {
      GraphicsConfiguration graphicsConfiguration = this.gc;
      if (graphicsConfiguration == null)
        graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); 
      if (graphicsConfiguration instanceof SurfaceManager.ProxiedGraphicsConfig) {
        Object object = ((SurfaceManager.ProxiedGraphicsConfig)graphicsConfiguration).getProxyKey();
        if (object != null) {
          SurfaceDataProxy surfaceDataProxy = (SurfaceDataProxy)SurfaceManager.this.getCacheData(object);
          return (surfaceDataProxy != null && surfaceDataProxy.isAccelerated());
        } 
      } 
      return false;
    }
  }
  
  public static interface ProxiedGraphicsConfig {
    Object getProxyKey();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\SurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */