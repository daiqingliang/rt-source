package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class DrawGlyphList extends GraphicsPrimitive {
  public static final String methodSignature = "DrawGlyphList(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawGlyphList locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (DrawGlyphList)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected DrawGlyphList(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public DrawGlyphList(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void DrawGlyphList(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public GraphicsPrimitive traceWrap() { return new TraceDrawGlyphList(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphList(null, null, null));
  }
  
  private static class General extends DrawGlyphList {
    MaskFill maskop;
    
    public General(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) {
      super(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
      this.maskop = MaskFill.locate(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
    }
    
    public void DrawGlyphList(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, GlyphList param1GlyphList) {
      int[] arrayOfInt = param1GlyphList.getBounds();
      int i = param1GlyphList.getNumGlyphs();
      Region region = param1SunGraphics2D.getCompClip();
      int j = region.getLoX();
      int k = region.getLoY();
      int m = region.getHiX();
      int n = region.getHiY();
      for (byte b = 0; b < i; b++) {
        param1GlyphList.setGlyphIndex(b);
        int[] arrayOfInt1 = param1GlyphList.getMetrics();
        int i1 = arrayOfInt1[0];
        int i2 = arrayOfInt1[1];
        int i3 = arrayOfInt1[2];
        int i4 = i1 + i3;
        int i5 = i2 + arrayOfInt1[3];
        int i6 = 0;
        if (i1 < j) {
          i6 = j - i1;
          i1 = j;
        } 
        if (i2 < k) {
          i6 += (k - i2) * i3;
          i2 = k;
        } 
        if (i4 > m)
          i4 = m; 
        if (i5 > n)
          i5 = n; 
        if (i4 > i1 && i5 > i2) {
          byte[] arrayOfByte = param1GlyphList.getGrayBits();
          this.maskop.MaskFill(param1SunGraphics2D, param1SurfaceData, param1SunGraphics2D.composite, i1, i2, i4 - i1, i5 - i2, arrayOfByte, i6, i3);
        } 
      } 
    }
  }
  
  private static class TraceDrawGlyphList extends DrawGlyphList {
    DrawGlyphList target;
    
    public TraceDrawGlyphList(DrawGlyphList param1DrawGlyphList) {
      super(param1DrawGlyphList.getSourceType(), param1DrawGlyphList.getCompositeType(), param1DrawGlyphList.getDestType());
      this.target = param1DrawGlyphList;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void DrawGlyphList(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, GlyphList param1GlyphList) {
      tracePrimitive(this.target);
      this.target.DrawGlyphList(param1SunGraphics2D, param1SurfaceData, param1GlyphList);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\DrawGlyphList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */