package sun.java2d.d3d;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderBuffer;

class D3DSurfaceToSwBlit extends Blit {
  private int typeval;
  
  D3DSurfaceToSwBlit(SurfaceType paramSurfaceType, int paramInt) {
    super(D3DSurfaceData.D3DSurface, CompositeType.SrcNoEa, paramSurfaceType);
    this.typeval = paramInt;
  }
  
  public void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.addReference(paramSurfaceData2);
      RenderBuffer renderBuffer = d3DRenderQueue.getBuffer();
      D3DContext.setScratchSurface(((D3DSurfaceData)paramSurfaceData1).getContext());
      d3DRenderQueue.ensureCapacityAndAlignment(48, 32);
      renderBuffer.putInt(34);
      renderBuffer.putInt(paramInt1).putInt(paramInt2);
      renderBuffer.putInt(paramInt3).putInt(paramInt4);
      renderBuffer.putInt(paramInt5).putInt(paramInt6);
      renderBuffer.putInt(this.typeval);
      renderBuffer.putLong(paramSurfaceData1.getNativeOps());
      renderBuffer.putLong(paramSurfaceData2.getNativeOps());
      d3DRenderQueue.flushNow();
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DSurfaceToSwBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */