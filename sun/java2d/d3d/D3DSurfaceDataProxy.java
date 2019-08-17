package sun.java2d.d3d;

import java.awt.Color;
import sun.java2d.InvalidPipeException;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;

public class D3DSurfaceDataProxy extends SurfaceDataProxy {
  D3DGraphicsConfig d3dgc;
  
  int transparency;
  
  public static SurfaceDataProxy createProxy(SurfaceData paramSurfaceData, D3DGraphicsConfig paramD3DGraphicsConfig) { return (paramSurfaceData instanceof D3DSurfaceData) ? UNCACHED : new D3DSurfaceDataProxy(paramD3DGraphicsConfig, paramSurfaceData.getTransparency()); }
  
  public D3DSurfaceDataProxy(D3DGraphicsConfig paramD3DGraphicsConfig, int paramInt) {
    this.d3dgc = paramD3DGraphicsConfig;
    this.transparency = paramInt;
    activateDisplayListener();
  }
  
  public SurfaceData validateSurfaceData(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2) {
    if (paramSurfaceData2 == null || paramSurfaceData2.isSurfaceLost())
      try {
        paramSurfaceData2 = this.d3dgc.createManagedSurface(paramInt1, paramInt2, this.transparency);
      } catch (InvalidPipeException invalidPipeException) {
        if (!this.d3dgc.getD3DDevice().isD3DAvailable()) {
          invalidate();
          flush();
          return null;
        } 
      }  
    return paramSurfaceData2;
  }
  
  public boolean isSupportedOperation(SurfaceData paramSurfaceData, int paramInt, CompositeType paramCompositeType, Color paramColor) { return (paramColor == null || this.transparency == 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DSurfaceDataProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */