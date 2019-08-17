package sun.java2d.loops;

import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class ScaledBlit extends GraphicsPrimitive {
  public static final String methodSignature = "ScaledBlit(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  private static RenderCache blitcache = new RenderCache(20);
  
  public static ScaledBlit locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (ScaledBlit)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public static ScaledBlit getFromCache(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) {
    Object object = blitcache.get(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (object != null)
      return (ScaledBlit)object; 
    ScaledBlit scaledBlit = locate(paramSurfaceType1, paramCompositeType, paramSurfaceType2);
    if (scaledBlit != null)
      blitcache.put(paramSurfaceType1, paramCompositeType, paramSurfaceType2, scaledBlit); 
    return scaledBlit;
  }
  
  protected ScaledBlit(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public ScaledBlit(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void Scale(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return null; }
  
  public GraphicsPrimitive traceWrap() { return new TraceScaledBlit(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new ScaledBlit(null, null, null));
  }
  
  private static class TraceScaledBlit extends ScaledBlit {
    ScaledBlit target;
    
    public TraceScaledBlit(ScaledBlit param1ScaledBlit) {
      super(param1ScaledBlit.getSourceType(), param1ScaledBlit.getCompositeType(), param1ScaledBlit.getDestType());
      this.target = param1ScaledBlit;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void Scale(SurfaceData param1SurfaceData1, SurfaceData param1SurfaceData2, Composite param1Composite, Region param1Region, int param1Int1, int param1Int2, int param1Int3, int param1Int4, double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      tracePrimitive(this.target);
      this.target.Scale(param1SurfaceData1, param1SurfaceData2, param1Composite, param1Region, param1Int1, param1Int2, param1Int3, param1Int4, param1Double1, param1Double2, param1Double3, param1Double4);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\ScaledBlit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */