package sun.java2d.d3d;

import java.awt.geom.Path2D;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.BufferedRenderPipe;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.SpanIterator;

class D3DRenderer extends BufferedRenderPipe {
  D3DRenderer(RenderQueue paramRenderQueue) { super(paramRenderQueue); }
  
  protected void validateContext(SunGraphics2D paramSunGraphics2D) {
    D3DSurfaceData d3DSurfaceData;
    byte b = (paramSunGraphics2D.paint.getTransparency() == 1) ? 1 : 0;
    try {
      d3DSurfaceData = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
    D3DContext.validateContext(d3DSurfaceData, d3DSurfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, null, paramSunGraphics2D.paint, paramSunGraphics2D, b);
  }
  
  protected void validateContextAA(SunGraphics2D paramSunGraphics2D) {
    D3DSurfaceData d3DSurfaceData;
    byte b = 0;
    try {
      d3DSurfaceData = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
    } catch (ClassCastException classCastException) {
      throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
    } 
    D3DContext.validateContext(d3DSurfaceData, d3DSurfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, null, paramSunGraphics2D.paint, paramSunGraphics2D, b);
  }
  
  void copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    this.rq.lock();
    try {
      D3DSurfaceData d3DSurfaceData;
      byte b = (paramSunGraphics2D.surfaceData.getTransparency() == 1) ? 1 : 0;
      try {
        d3DSurfaceData = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
      } catch (ClassCastException classCastException) {
        throw new InvalidPipeException("wrong surface data type: " + paramSunGraphics2D.surfaceData);
      } 
      D3DContext.validateContext(d3DSurfaceData, d3DSurfaceData, paramSunGraphics2D.getCompClip(), paramSunGraphics2D.composite, null, null, null, b);
      this.rq.ensureCapacity(28);
      this.buf.putInt(30);
      this.buf.putInt(paramInt1).putInt(paramInt2).putInt(paramInt3).putInt(paramInt4);
      this.buf.putInt(paramInt5).putInt(paramInt6);
    } finally {
      this.rq.unlock();
    } 
  }
  
  protected native void drawPoly(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3);
  
  D3DRenderer traceWrap() { return new Tracer(this, this); }
  
  private class Tracer extends D3DRenderer {
    private D3DRenderer d3dr;
    
    Tracer(D3DRenderer this$0, D3DRenderer param1D3DRenderer1) {
      super(param1D3DRenderer1.rq);
      this.d3dr = param1D3DRenderer1;
    }
    
    public ParallelogramPipe getAAParallelogramPipe() {
      final ParallelogramPipe realpipe = this.d3dr.getAAParallelogramPipe();
      return new ParallelogramPipe() {
          public void fillParallelogram(SunGraphics2D param2SunGraphics2D, double param2Double1, double param2Double2, double param2Double3, double param2Double4, double param2Double5, double param2Double6, double param2Double7, double param2Double8, double param2Double9, double param2Double10) {
            GraphicsPrimitive.tracePrimitive("D3DFillAAParallelogram");
            realpipe.fillParallelogram(param2SunGraphics2D, param2Double1, param2Double2, param2Double3, param2Double4, param2Double5, param2Double6, param2Double7, param2Double8, param2Double9, param2Double10);
          }
          
          public void drawParallelogram(SunGraphics2D param2SunGraphics2D, double param2Double1, double param2Double2, double param2Double3, double param2Double4, double param2Double5, double param2Double6, double param2Double7, double param2Double8, double param2Double9, double param2Double10, double param2Double11, double param2Double12) {
            GraphicsPrimitive.tracePrimitive("D3DDrawAAParallelogram");
            realpipe.drawParallelogram(param2SunGraphics2D, param2Double1, param2Double2, param2Double3, param2Double4, param2Double5, param2Double6, param2Double7, param2Double8, param2Double9, param2Double10, param2Double11, param2Double12);
          }
        };
    }
    
    protected void validateContext(SunGraphics2D param1SunGraphics2D) { this.d3dr.validateContext(param1SunGraphics2D); }
    
    public void drawLine(SunGraphics2D param1SunGraphics2D, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      GraphicsPrimitive.tracePrimitive("D3DDrawLine");
      this.d3dr.drawLine(param1SunGraphics2D, param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    public void drawRect(SunGraphics2D param1SunGraphics2D, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      GraphicsPrimitive.tracePrimitive("D3DDrawRect");
      this.d3dr.drawRect(param1SunGraphics2D, param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    protected void drawPoly(SunGraphics2D param1SunGraphics2D, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int param1Int, boolean param1Boolean) {
      GraphicsPrimitive.tracePrimitive("D3DDrawPoly");
      this.d3dr.drawPoly(param1SunGraphics2D, param1ArrayOfInt1, param1ArrayOfInt2, param1Int, param1Boolean);
    }
    
    public void fillRect(SunGraphics2D param1SunGraphics2D, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      GraphicsPrimitive.tracePrimitive("D3DFillRect");
      this.d3dr.fillRect(param1SunGraphics2D, param1Int1, param1Int2, param1Int3, param1Int4);
    }
    
    protected void drawPath(SunGraphics2D param1SunGraphics2D, Path2D.Float param1Float, int param1Int1, int param1Int2) {
      GraphicsPrimitive.tracePrimitive("D3DDrawPath");
      this.d3dr.drawPath(param1SunGraphics2D, param1Float, param1Int1, param1Int2);
    }
    
    protected void fillPath(SunGraphics2D param1SunGraphics2D, Path2D.Float param1Float, int param1Int1, int param1Int2) {
      GraphicsPrimitive.tracePrimitive("D3DFillPath");
      this.d3dr.fillPath(param1SunGraphics2D, param1Float, param1Int1, param1Int2);
    }
    
    protected void fillSpans(SunGraphics2D param1SunGraphics2D, SpanIterator param1SpanIterator, int param1Int1, int param1Int2) {
      GraphicsPrimitive.tracePrimitive("D3DFillSpans");
      this.d3dr.fillSpans(param1SunGraphics2D, param1SpanIterator, param1Int1, param1Int2);
    }
    
    public void fillParallelogram(SunGraphics2D param1SunGraphics2D, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8, double param1Double9, double param1Double10) {
      GraphicsPrimitive.tracePrimitive("D3DFillParallelogram");
      this.d3dr.fillParallelogram(param1SunGraphics2D, param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6, param1Double7, param1Double8, param1Double9, param1Double10);
    }
    
    public void drawParallelogram(SunGraphics2D param1SunGraphics2D, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8, double param1Double9, double param1Double10, double param1Double11, double param1Double12) {
      GraphicsPrimitive.tracePrimitive("D3DDrawParallelogram");
      this.d3dr.drawParallelogram(param1SunGraphics2D, param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6, param1Double7, param1Double8, param1Double9, param1Double10, param1Double11, param1Double12);
    }
    
    public void copyArea(SunGraphics2D param1SunGraphics2D, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      GraphicsPrimitive.tracePrimitive("D3DCopyArea");
      this.d3dr.copyArea(param1SunGraphics2D, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */