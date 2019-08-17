package sun.java2d.opengl;

import java.awt.Composite;
import java.lang.ref.WeakReference;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderBuffer;

final class OGLSurfaceToSwBlit extends Blit {
  private final int typeval;
  
  private WeakReference<SurfaceData> srcTmp;
  
  OGLSurfaceToSwBlit(SurfaceType paramSurfaceType, int paramInt) {
    super(OGLSurfaceData.OpenGLSurface, CompositeType.SrcNoEa, paramSurfaceType);
    this.typeval = paramInt;
  }
  
  private void complexClipBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    SurfaceData surfaceData = null;
    if (this.srcTmp != null)
      surfaceData = (SurfaceData)this.srcTmp.get(); 
    byte b = (this.typeval == 1) ? 3 : 2;
    paramSurfaceData1 = convertFrom(this, paramSurfaceData1, paramInt1, paramInt2, paramInt5, paramInt6, surfaceData, b);
    Blit blit = Blit.getFromCache(paramSurfaceData1.getSurfaceType(), CompositeType.SrcNoEa, paramSurfaceData2.getSurfaceType());
    blit.Blit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, 0, 0, paramInt3, paramInt4, paramInt5, paramInt6);
    if (paramSurfaceData1 != surfaceData)
      this.srcTmp = new WeakReference(paramSurfaceData1); 
  }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (paramRegion != null) {
      paramRegion = paramRegion.getIntersectionXYWH(paramInt3, paramInt4, paramInt5, paramInt6);
      if (paramRegion.isEmpty())
        return; 
      paramInt1 += paramRegion.getLoX() - paramInt3;
      paramInt2 += paramRegion.getLoY() - paramInt4;
      paramInt3 = paramRegion.getLoX();
      paramInt4 = paramRegion.getLoY();
      paramInt5 = paramRegion.getWidth();
      paramInt6 = paramRegion.getHeight();
      if (!paramRegion.isRectangular()) {
        complexClipBlit(paramSurfaceData1, paramSurfaceData2, paramComposite, paramRegion, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        return;
      } 
    } 
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      oGLRenderQueue.addReference(paramSurfaceData2);
      RenderBuffer renderBuffer = oGLRenderQueue.getBuffer();
      OGLContext.validateContext((OGLSurfaceData)paramSurfaceData1);
      oGLRenderQueue.ensureCapacityAndAlignment(48, 32);
      renderBuffer.putInt(34);
      renderBuffer.putInt(paramInt1).putInt(paramInt2);
      renderBuffer.putInt(paramInt3).putInt(paramInt4);
      renderBuffer.putInt(paramInt5).putInt(paramInt6);
      renderBuffer.putInt(this.typeval);
      renderBuffer.putLong(paramSurfaceData1.getNativeOps());
      renderBuffer.putLong(paramSurfaceData2.getNativeOps());
      oGLRenderQueue.flushNow();
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLSurfaceToSwBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */