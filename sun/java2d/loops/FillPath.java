package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillPath extends GraphicsPrimitive {
  public static final String methodSignature = "FillPath(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static FillPath locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (FillPath)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected FillPath(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public FillPath(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void FillPath(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, Path2D.Float paramFloat);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("FillPath not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceFillPath(this); }
  
  private static class TraceFillPath extends FillPath {
    FillPath target;
    
    public TraceFillPath(FillPath param1FillPath) {
      super(param1FillPath.getSourceType(), param1FillPath.getCompositeType(), param1FillPath.getDestType());
      this.target = param1FillPath;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void FillPath(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, int param1Int1, int param1Int2, Path2D.Float param1Float) {
      tracePrimitive(this.target);
      this.target.FillPath(param1SunGraphics2D, param1SurfaceData, param1Int1, param1Int2, param1Float);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\FillPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */