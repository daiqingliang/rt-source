package sun.java2d.loops;

import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DrawPolygons extends GraphicsPrimitive {
  public static final String methodSignature = "DrawPolygons(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawPolygons locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (DrawPolygons)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected DrawPolygons(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public DrawPolygons(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void DrawPolygons(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { throw new InternalError("DrawPolygons not implemented for " + paramSurfaceType1 + " with " + paramCompositeType); }
  
  public GraphicsPrimitive traceWrap() { return new TraceDrawPolygons(this); }
  
  private static class TraceDrawPolygons extends DrawPolygons {
    DrawPolygons target;
    
    public TraceDrawPolygons(DrawPolygons param1DrawPolygons) {
      super(param1DrawPolygons.getSourceType(), param1DrawPolygons.getCompositeType(), param1DrawPolygons.getDestType());
      this.target = param1DrawPolygons;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void DrawPolygons(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2, int[] param1ArrayOfInt3, int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      tracePrimitive(this.target);
      this.target.DrawPolygons(param1SunGraphics2D, param1SurfaceData, param1ArrayOfInt1, param1ArrayOfInt2, param1ArrayOfInt3, param1Int1, param1Int2, param1Int3, param1Boolean);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\DrawPolygons.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */