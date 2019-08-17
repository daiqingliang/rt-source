package sun.java2d.opengl;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

class OGLUtilities {
  public static final int UNDEFINED = 0;
  
  public static final int WINDOW = 1;
  
  public static final int PBUFFER = 2;
  
  public static final int TEXTURE = 3;
  
  public static final int FLIP_BACKBUFFER = 4;
  
  public static final int FBOBJECT = 5;
  
  public static boolean isQueueFlusherThread() { return OGLRenderQueue.isQueueFlusherThread(); }
  
  public static boolean invokeWithOGLContextCurrent(Graphics paramGraphics, Runnable paramRunnable) {
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      if (paramGraphics != null) {
        if (!(paramGraphics instanceof SunGraphics2D))
          return false; 
        SurfaceData surfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
        if (!(surfaceData instanceof OGLSurfaceData))
          return false; 
        OGLContext.validateContext((OGLSurfaceData)surfaceData);
      } 
      oGLRenderQueue.flushAndInvokeNow(paramRunnable);
      OGLContext.invalidateCurrentContext();
    } finally {
      oGLRenderQueue.unlock();
    } 
    return true;
  }
  
  public static boolean invokeWithOGLSharedContextCurrent(GraphicsConfiguration paramGraphicsConfiguration, Runnable paramRunnable) {
    if (!(paramGraphicsConfiguration instanceof OGLGraphicsConfig))
      return false; 
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      OGLContext.setScratchSurface((OGLGraphicsConfig)paramGraphicsConfiguration);
      oGLRenderQueue.flushAndInvokeNow(paramRunnable);
      OGLContext.invalidateCurrentContext();
    } finally {
      oGLRenderQueue.unlock();
    } 
    return true;
  }
  
  public static Rectangle getOGLViewport(Graphics paramGraphics, int paramInt1, int paramInt2) {
    if (!(paramGraphics instanceof SunGraphics2D))
      return null; 
    SunGraphics2D sunGraphics2D = (SunGraphics2D)paramGraphics;
    SurfaceData surfaceData = sunGraphics2D.surfaceData;
    int i = sunGraphics2D.transX;
    int j = sunGraphics2D.transY;
    Rectangle rectangle = surfaceData.getBounds();
    int k = i;
    int m = rectangle.height - j + paramInt2;
    return new Rectangle(k, m, paramInt1, paramInt2);
  }
  
  public static Rectangle getOGLScissorBox(Graphics paramGraphics) {
    if (!(paramGraphics instanceof SunGraphics2D))
      return null; 
    SunGraphics2D sunGraphics2D = (SunGraphics2D)paramGraphics;
    SurfaceData surfaceData = sunGraphics2D.surfaceData;
    Region region = sunGraphics2D.getCompClip();
    if (!region.isRectangular())
      return null; 
    int i = region.getLoX();
    int j = region.getLoY();
    int k = region.getWidth();
    int m = region.getHeight();
    Rectangle rectangle = surfaceData.getBounds();
    int n = i;
    int i1 = rectangle.height - j + m;
    return new Rectangle(n, i1, k, m);
  }
  
  public static Object getOGLSurfaceIdentifier(Graphics paramGraphics) { return !(paramGraphics instanceof SunGraphics2D) ? null : ((SunGraphics2D)paramGraphics).surfaceData; }
  
  public static int getOGLSurfaceType(Graphics paramGraphics) {
    if (!(paramGraphics instanceof SunGraphics2D))
      return 0; 
    SurfaceData surfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
    return !(surfaceData instanceof OGLSurfaceData) ? 0 : ((OGLSurfaceData)surfaceData).getType();
  }
  
  public static int getOGLTextureType(Graphics paramGraphics) {
    if (!(paramGraphics instanceof SunGraphics2D))
      return 0; 
    SurfaceData surfaceData = ((SunGraphics2D)paramGraphics).surfaceData;
    return !(surfaceData instanceof OGLSurfaceData) ? 0 : ((OGLSurfaceData)surfaceData).getTextureTarget();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */