package sun.java2d.d3d;

import java.awt.Composite;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.BufferedTextPipe;
import sun.java2d.pipe.RenderQueue;

class D3DTextRenderer extends BufferedTextPipe {
  D3DTextRenderer(RenderQueue paramRenderQueue) { super(paramRenderQueue); }
  
  protected native void drawGlyphList(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, float paramFloat1, float paramFloat2, long[] paramArrayOfLong, float[] paramArrayOfFloat);
  
  protected void validateContext(SunGraphics2D paramSunGraphics2D, Composite paramComposite) {
    D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)paramSunGraphics2D.surfaceData;
    D3DContext.validateContext(d3DSurfaceData, d3DSurfaceData, paramSunGraphics2D.getCompClip(), paramComposite, null, paramSunGraphics2D.paint, paramSunGraphics2D, 0);
  }
  
  D3DTextRenderer traceWrap() { return new Tracer(this); }
  
  private static class Tracer extends D3DTextRenderer {
    Tracer(D3DTextRenderer param1D3DTextRenderer) { super(param1D3DTextRenderer.rq); }
    
    protected void drawGlyphList(SunGraphics2D param1SunGraphics2D, GlyphList param1GlyphList) {
      GraphicsPrimitive.tracePrimitive("D3DDrawGlyphs");
      super.drawGlyphList(param1SunGraphics2D, param1GlyphList);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DTextRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */