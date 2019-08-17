package sun.java2d;

import java.awt.GraphicsConfiguration;
import sun.awt.image.BufImgVolatileSurfaceManager;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.d3d.D3DVolatileSurfaceManager;
import sun.java2d.opengl.WGLVolatileSurfaceManager;

public class WindowsSurfaceManagerFactory extends SurfaceManagerFactory {
  public VolatileSurfaceManager createVolatileManager(SunVolatileImage paramSunVolatileImage, Object paramObject) {
    GraphicsConfiguration graphicsConfiguration = paramSunVolatileImage.getGraphicsConfig();
    return (graphicsConfiguration instanceof sun.java2d.d3d.D3DGraphicsConfig) ? new D3DVolatileSurfaceManager(paramSunVolatileImage, paramObject) : ((graphicsConfiguration instanceof sun.java2d.opengl.WGLGraphicsConfig) ? new WGLVolatileSurfaceManager(paramSunVolatileImage, paramObject) : new BufImgVolatileSurfaceManager(paramSunVolatileImage, paramObject));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\WindowsSurfaceManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */