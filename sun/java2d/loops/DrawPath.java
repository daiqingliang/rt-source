package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawPath extends GraphicsPrimitive {
  public static final String methodSignature = "DrawPath(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawPath locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (DrawPath)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected DrawPath(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public DrawPath(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void DrawPath(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, Path2D.Float paramFloat);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("DrawPath not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceDrawPath(this); }
  
  private static class TraceDrawPath extends DrawPath {
    DrawPath target;
    
    public TraceDrawPath(DrawPath param1DrawPath) {
      super(param1DrawPath.getSourceType(), param1DrawPath.getCompositeType(), param1DrawPath.getDestType());
      this.target = param1DrawPath;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void DrawPath(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, int param1Int1, int param1Int2, Path2D.Float param1Float) {
      tracePrimitive(this.target);
      this.target.DrawPath(param1SunGraphics2D, param1SurfaceData, param1Int1, param1Int2, param1Float);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\DrawPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */