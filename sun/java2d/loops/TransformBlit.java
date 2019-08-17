package sun.java2d.loops;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class TransformBlit extends GraphicsPrimitive {
  public static final String methodSignature = "TransformBlit(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache blitcache = new RenderCache(10);
  
  public static TransformBlit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (TransformBlit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public static TransformBlit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (TransformBlit)object; 
    TransformBlit transformBlit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (transformBlit != null)
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, transformBlit); 
    return transformBlit;
  }
  
  protected TransformBlit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public TransformBlit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void Transform(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return null; }
  
  public GraphicsPrimitive traceWrap() { return new TraceTransformBlit(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new TransformBlit(null, null, null));
  }
  
  private static class TraceTransformBlit extends TransformBlit {
    TransformBlit target;
    
    public TraceTransformBlit(TransformBlit param1TransformBlit) {
      super(param1TransformBlit.getSourceType(), param1TransformBlit.getCompositeType(), param1TransformBlit.getDestType());
      this.target = param1TransformBlit;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void Transform(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, AffineTransform param1AffineTransform, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7) {
      tracePrimitive(this.target);
      this.target.Transform(param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1AffineTransform, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\TransformBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */