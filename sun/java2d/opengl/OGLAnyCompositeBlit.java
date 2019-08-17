package sun.java2d.opengl;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;

final class OGLAnyCompositeBlit extends Blit {
  private WeakReference<SurfaceData> dstTmp;
  
  private WeakReference<SurfaceData> srcTmp;
  
  private final Blit convertsrc;
  
  private final Blit convertdst;
  
  private final Blit convertresult;
  
  OGLAnyCompositeBlit(SurfaceType paramSurfaceType, Blit paramBlit1, Blit paramBlit2, Blit paramBlit3) {
    super(paramSurfaceType, CompositeType.Any, OGLSurfaceData.OpenGLSurface);
    this.convertsrc = paramBlit1;
    this.convertdst = paramBlit2;
    this.convertresult = paramBlit3;
  }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (this.convertsrc != null) {
      SurfaceData surfaceData = null;
      if (this.srcTmp != null)
        surfaceData = (SurfaceData)this.srcTmp.get(); 
      paramSurfaceData1 = convertFrom(this.convertsrc, paramSurfaceData1, paramInt1, paramInt2, paramInt5, paramInt6, surfaceData, 3);
      if (paramSurfaceData1 != surfaceData)
        this.srcTmp = new WeakReference(paramSurfaceData1); 
    } 
    SurfaceData surfaceData1 = null;
    if (this.dstTmp != null)
      surfaceData1 = (SurfaceData)this.dstTmp.get(); 
    SurfaceData surfaceData2 = convertFrom(this.convertdst, paramSurfaceData2, paramInt3, paramInt4, paramInt5, paramInt6, surfaceData1, 3);
    Region region = (paramRegion == null) ? null : paramRegion.getTranslatedRegion(-paramInt3, -paramInt4);
    Blit blit = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.Any, surfaceData2.getSurfaceType());
    blit.Blit(paramSurfaceData1, surfaceData2, paramComposite, region, paramInt1, paramInt2, 0, 0, paramInt5, paramInt6);
    if (surfaceData2 != surfaceData1)
      this.dstTmp = new WeakReference(surfaceData2); 
    this.convertresult.Blit(surfaceData2, paramSurfaceData2, AlphaComposite.Src, paramRegion, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLAnyCompositeBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */