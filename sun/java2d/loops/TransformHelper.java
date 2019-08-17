package sun.java2d.loops;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class TransformHelper extends GraphicsPrimitive {
  public static final String methodSignature = "TransformHelper(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache helpercache = new RenderCache(10);
  
  public static TransformHelper locate(SurfaceType paramSurfaceType) { return (TransformHelper)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType, CompositeType.SrcNoEa, SurfaceType.IntArgbPre); }
  
  public static TransformHelper getFromCache(SurfaceType paramSurfaceType) {
    Object object = helpercache.get(paramSurfaceType, null, null);
    if (object != null)
      return (TransformHelper)object; 
    TransformHelper transformHelper = locate(paramSurfaceType);
    if (transformHelper != null)
      helpercache.put(paramSurfaceType, null, null, transformHelper); 
    return transformHelper;
  }
  
  protected TransformHelper(SurfaceType paramSurfaceType) { super(methodSignature, primTypeID, paramSurfaceType, CompositeType.SrcNoEa, SurfaceType.IntArgbPre); }
  
  public TransformHelper(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void Transform(MaskBlit paramMaskBlit, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int[] paramArrayOfInt, int paramInt10, int paramInt11);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return null; }
  
  public GraphicsPrimitive traceWrap() { return new TraceTransformHelper(this); }
  
  private static class TraceTransformHelper extends TransformHelper {
    TransformHelper target;
    
    public TraceTransformHelper(TransformHelper param1TransformHelper) {
      super(param1TransformHelper.getSourceType());
      this.target = param1TransformHelper;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void Transform(MaskBlit param1MaskBlit, SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, AffineTransform param1AffineTransform, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6, int param1Int7, int param1Int8, int param1Int9, int[] param1ArrayOfInt, int param1Int10, int param1Int11) {
      tracePrimitive(this.target);
      this.target.Transform(param1MaskBlit, param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1AffineTransform, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6, param1Int7, param1Int8, param1Int9, param1ArrayOfInt, param1Int10, param1Int11);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\TransformHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */