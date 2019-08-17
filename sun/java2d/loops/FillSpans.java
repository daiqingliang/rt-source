package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.SpanIterator;

public class FillSpans extends GraphicsPrimitive {
  public static final String methodSignature = "FillSpans(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static FillSpans locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (FillSpans)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected FillSpans(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public FillSpans(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  private native void FillSpans(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt, long paramLong, SpanIterator paramSpanIterator);
  
  public void FillSpans(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, SpanIterator paramSpanIterator) { FillSpans(paramSunGraphics2D, paramSurfaceData, paramSunGraphics2D.pixel, paramSpanIterator.getNativeIterator(), paramSpanIterator); }
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("FillSpans not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceFillSpans(this); }
  
  private static class TraceFillSpans extends FillSpans {
    FillSpans target;
    
    public TraceFillSpans(FillSpans param1FillSpans) {
      super(param1FillSpans.getSourceType(), param1FillSpans.getCompositeType(), param1FillSpans.getDestType());
      this.target = param1FillSpans;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void FillSpans(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, SpanIterator param1SpanIterator) {
      tracePrimitive(this.target);
      this.target.FillSpans(param1SunGraphics2D, param1SurfaceData, param1SpanIterator);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\FillSpans.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */