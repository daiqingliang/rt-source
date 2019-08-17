package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawParallelogram extends GraphicsPrimitive {
  public static final String methodSignature = "DrawParallelogram(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawParallelogram locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (DrawParallelogram)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected DrawParallelogram(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public DrawParallelogram(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void DrawParallelogram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("DrawParallelogram not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceDrawParallelogram(this); }
  
  private static class TraceDrawParallelogram extends DrawParallelogram {
    DrawParallelogram target;
    
    public TraceDrawParallelogram(DrawParallelogram param1DrawParallelogram) {
      super(param1DrawParallelogram.getSourceType(), param1DrawParallelogram.getCompositeType(), param1DrawParallelogram.getDestType());
      this.target = param1DrawParallelogram;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void DrawParallelogram(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8) {
      tracePrimitive(this.target);
      this.target.DrawParallelogram(param1SunGraphics2D, param1SurfaceData, param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6, param1Double7, param1Double8);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\DrawParallelogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */