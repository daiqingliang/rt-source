package sun.java2d.opengl;

import java.awt.Color;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;

public class OGLSurfaceDataProxy extends SurfaceDataProxy {
  OGLGraphicsConfig oglgc;
  
  int transparency;
  
  public static SurfaceDataProxy createProxy(SurfaceData paramSurfaceData, OGLGraphicsConfig paramOGLGraphicsConfig) { return (paramSurfaceData instanceof OGLSurfaceData) ? UNCACHED : new OGLSurfaceDataProxy(paramOGLGraphicsConfig, paramSurfaceData.getTransparency()); }
  
  public OGLSurfaceDataProxy(OGLGraphicsConfig paramOGLGraphicsConfig, int paramInt) {
    this.oglgc = paramOGLGraphicsConfig;
    this.transparency = paramInt;
  }
  
  public SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2) {
    if (paramSurfaceData2 == null)
      try {
        paramSurfaceData2 = this.oglgc.createManagedSurface(paramInt1, paramInt2, this.transparency);
      } catch (OutOfMemoryError outOfMemoryError) {
        return null;
      }  
    return paramSurfaceData2;
  }
  
  public boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor) { return (paramCompositeType.isDerivedFrom(CompositeType.AnyAlpha) && (paramColor == null || this.transparency == 1)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLSurfaceDataProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */