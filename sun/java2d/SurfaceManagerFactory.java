package sun.java2d;

import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;

public abstract class SurfaceManagerFactory {
  private static SurfaceManagerFactory instance;
  
  public static SurfaceManagerFactory getInstance() {
    if (instance == null)
      throw new IllegalStateException("No SurfaceManagerFactory set."); 
    return instance;
  }
  
  public static void setInstance(SurfaceManagerFactory paramSurfaceManagerFactory) {
    if (paramSurfaceManagerFactory == null)
      throw new IllegalArgumentException("factory must be non-null"); 
    if (instance != null)
      throw new IllegalStateException("The surface manager factory is already initialized"); 
    instance = paramSurfaceManagerFactory;
  }
  
  public abstract VolatileSurfaceManager createVolatileManager(SunVolatileImage paramSunVolatileImage, Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\SurfaceManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */