package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawRect extends GraphicsPrimitive {
  public static final String methodSignature = "DrawRect(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawRect locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (DrawRect)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected DrawRect(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public DrawRect(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void DrawRect(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("DrawRect not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceDrawRect(this); }
  
  private static class TraceDrawRect extends DrawRect {
    DrawRect target;
    
    public TraceDrawRect(DrawRect param1DrawRect) {
      super(param1DrawRect.getSourceType(), param1DrawRect.getCompositeType(), param1DrawRect.getDestType());
      this.target = param1DrawRect;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void DrawRect(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      tracePrimitive(this.target);
      this.target.DrawRect(param1SunGraphics2D, param1SurfaceData, param1Int1, param1Int2, param1Int3, param1Int4);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\DrawRect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */