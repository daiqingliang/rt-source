package sun.java2d.opengl;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class OGLSwToSurfaceScale extends ScaledBlit {
  private int typeval;
  
  OGLSwToSurfaceScale(SurfaceType paramSurfaceType, int paramInt) {
    super(paramSurfaceType, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
    this.typeval = paramInt;
  }
  
  public void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { OGLBlitLoops.Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, null, 1, paramInt1, paramInt2, paramInt3, paramInt4, paramDouble1, paramDouble2, paramDouble3, paramDouble4, this.typeval, false); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLSwToSurfaceScale.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */