package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class FillRect extends GraphicsPrimitive {
  public static final String methodSignature = "FillRect(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static FillRect locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (FillRect)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected FillRect(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public FillRect(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void FillRect(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public GraphicsPrimitive traceWrap() { return new TraceFillRect(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new FillRect(null, null, null));
  }
  
  public static class General extends FillRect {
    public MaskFill fillop;
    
    public General(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) {
      super(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
      this.fillop = MaskFill.locate(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
    }
    
    public void FillRect(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { this.fillop.MaskFill(param1SunGraphics2D, param1SurfaceData, param1SunGraphics2D.composite, param1Int1, param1Int2, param1Int3, param1Int4, null, 0, 0); }
  }
  
  private static class TraceFillRect extends FillRect {
    FillRect target;
    
    public TraceFillRect(FillRect param1FillRect) {
      super(param1FillRect.getSourceType(), param1FillRect.getCompositeType(), param1FillRect.getDestType());
      this.target = param1FillRect;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void FillRect(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      tracePrimitive(this.target);
      this.target.FillRect(param1SunGraphics2D, param1SurfaceData, param1Int1, param1Int2, param1Int3, param1Int4);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\FillRect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */