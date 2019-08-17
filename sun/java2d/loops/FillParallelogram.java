package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillParallelogram extends GraphicsPrimitive {
  public static final String methodSignature = "FillParallelogram(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static FillParallelogram locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (FillParallelogram)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected FillParallelogram(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public FillParallelogram(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void FillParallelogram(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("FillParallelogram not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceFillParallelogram(this); }
  
  private static class TraceFillParallelogram extends FillParallelogram {
    FillParallelogram target;
    
    public TraceFillParallelogram(FillParallelogram param1FillParallelogram) {
      super(param1FillParallelogram.getSourceType(), param1FillParallelogram.getCompositeType(), param1FillParallelogram.getDestType());
      this.target = param1FillParallelogram;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void FillParallelogram(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      tracePrimitive(this.target);
      this.target.FillParallelogram(param1SunGraphics2D, param1SurfaceData, param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\FillParallelogram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */