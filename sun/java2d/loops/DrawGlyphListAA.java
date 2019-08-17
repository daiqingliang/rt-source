package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.Region;

public class DrawGlyphListAA extends GraphicsPrimitive {
  public static final String methodSignature = "DrawGlyphListAA(...)".toString();
  
  public static final int primTypeID = makePrimTypeID();
  
  public static DrawGlyphListAA locate(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return (DrawGlyphListAA)GraphicsPrimitiveMgr.locate(primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  protected DrawGlyphListAA(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public DrawGlyphListAA(long paramLong, SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { super(paramLong, methodSignature, primTypeID, paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public native void DrawGlyphListAA(SunGraphics2D paramSunGraphics2D, SurfaceData paramSurfaceData, GlyphList paramGlyphList);
  
  public GraphicsPrimitive makePrimitive(SurfaceType paramSurfaceType1, CompositeType paramCompositeType, SurfaceType paramSurfaceType2) { return new General(paramSurfaceType1, paramCompositeType, paramSurfaceType2); }
  
  public GraphicsPrimitive traceWrap() { return new TraceDrawGlyphListAA(this); }
  
  static  {
    GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListAA(null, null, null));
  }
  
  public static class General extends DrawGlyphListAA {
    MaskFill maskop;
    
    public General(SurfaceType param1SurfaceType1, CompositeType param1CompositeType, SurfaceType param1SurfaceType2) {
      super(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
      this.maskop = MaskFill.locate(param1SurfaceType1, param1CompositeType, param1SurfaceType2);
    }
    
    public void DrawGlyphListAA(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, GlyphList param1GlyphList) {
      param1GlyphList.getBounds();
      int i = param1GlyphList.getNumGlyphs();
      Region region = param1SunGraphics2D.getCompClip();
      int j = region.getLoX();
      int k = region.getLoY();
      int m = region.getHiX();
      int n = region.getHiY();
      for (byte b = 0; b < i; b++) {
        param1GlyphList.setGlyphIndex(b);
        int[] arrayOfInt = param1GlyphList.getMetrics();
        int i1 = arrayOfInt[0];
        int i2 = arrayOfInt[1];
        int i3 = arrayOfInt[2];
        int i4 = i1 + i3;
        int i5 = i2 + arrayOfInt[3];
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
  
  private static class TraceDrawGlyphListAA extends DrawGlyphListAA {
    DrawGlyphListAA target;
    
    public TraceDrawGlyphListAA(DrawGlyphListAA param1DrawGlyphListAA) {
      super(param1DrawGlyphListAA.getSourceType(), param1DrawGlyphListAA.getCompositeType(), param1DrawGlyphListAA.getDestType());
      this.target = param1DrawGlyphListAA;
    }
    
    public GraphicsPrimitive traceWrap() { return this; }
    
    public void DrawGlyphListAA(SunGraphics2D param1SunGraphics2D, SurfaceData param1SurfaceData, GlyphList param1GlyphList) {
      tracePrimitive(this.target);
      this.target.DrawGlyphListAA(param1SunGraphics2D, param1SurfaceData, param1GlyphList);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\DrawGlyphListAA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */