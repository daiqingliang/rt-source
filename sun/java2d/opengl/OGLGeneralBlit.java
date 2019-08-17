package sun.java2d.opengl;

import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

class OGLGeneralBlit extends Blit {
  private final Blit performop;
  
  private WeakReference srcTmp;
  
  OGLGeneralBlit(SurfaceType paramSurfaceType, CompositeType paramCompositeType, Blit paramBlit) {
    super(SurfaceType.Any, paramCompositeType, paramSurfaceType);
    this.performop = paramBlit;
  }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    Blit blit = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
    SurfaceData surfaceData = null;
    if (this.srcTmp != null)
      surfaceData = (SurfaceData)this.srcTmp.get(); 
    paramSurfaceData1 = convertFrom(blit, paramSurfaceData1, paramInt1, paramInt2, paramInt5, paramInt6, surfaceData, 3);
    this.performop.Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
    if (paramSurfaceData1 != surfaceData)
      this.srcTmp = new WeakReference(paramSurfaceData1); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLGeneralBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */