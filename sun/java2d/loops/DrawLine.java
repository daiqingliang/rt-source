package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawLine extends GraphicsPrimitive {
  public static final String methodSignature = "DrawLine(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawLine locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (DrawLine)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected DrawLine(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public DrawLine(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void DrawLine(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("DrawLine not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceDrawLine(this); }
  
  private static class TraceDrawLine extends DrawLine {
    DrawLine target;
    
    public TraceDrawLine(DrawLine param1DrawLine) {
      super(param1DrawLine.getSourceType(), param1DrawLine.getCompositeType(), param1DrawLine.getDestType());
      this.target = param1DrawLine;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void DrawLine(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      tracePrimitive(this.target);
      this.target.DrawLine(param1SunGraphics2D, param1SurfaceData, param1Int1, param1Int2, param1Int3, param1Int4);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\DrawLine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */