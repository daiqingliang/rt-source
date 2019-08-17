package sun.java2d.d3d;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.pipe.Region;
import sun.java2d.windows.GDIWindowSurfaceData;

class D3DSurfaceToGDIWindowSurfaceScale extends ScaledBlit {
  D3DSurfaceToGDIWindowSurfaceScale() { super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, GDIWindowSurfaceData.AnyGdi); }
  
  public void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { D3DVolatileSurfaceManager.handleVItoScreenOp(paramSurfaceData1, paramSurfaceData2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DSurfaceToGDIWindowSurfaceScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */