package sun.awt.image;

import sun.java2d.SurfaceData;

public class BufImgVolatileSurfaceManager extends VolatileSurfaceManager {
  public BufImgVolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject) { super(paramSunVolatileImage, paramObject); }
  
  protected boolean isAccelerationEnabled() { return false; }
  
  protected SurfaceData initAcceleratedSurface() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\BufImgVolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */