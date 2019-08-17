package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

class NullFontScaler extends FontScaler {
  NullFontScaler() {}
  
  public NullFontScaler(Font2D paramFont2D, int paramInt1, boolean paramBoolean, int paramInt2) {}
  
  StrikeMetrics getFontMetrics(long paramLong) { return new StrikeMetrics(240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F, 240.0F); }
  
  float getGlyphAdvance(long paramLong, int paramInt) { return 0.0F; }
  
  void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) {
    paramFloat.x = 0.0F;
    paramFloat.y = 0.0F;
  }
  
  Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) { return new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F); }
  
  GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) { return new GeneralPath(); }
  
  GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2) { return new GeneralPath(); }
  
  long getLayoutTableCache() { return 0L; }
  
  long createScalerContext(double[] paramArrayOfDouble, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, boolean paramBoolean) { return getNullScalerContext(); }
  
  void invalidateScalerContext(long paramLong) {}
  
  int getNumGlyphs() throws FontScalerException { return 1; }
  
  int getMissingGlyphCode() throws FontScalerException { return 0; }
  
  int getGlyphCode(char paramChar) throws FontScalerException { return 0; }
  
  long getUnitsPerEm() { return 2048L; }
  
  Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2) { return null; }
  
  static native long getNullScalerContext();
  
  native long getGlyphImage(long paramLong, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\NullFontScaler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */