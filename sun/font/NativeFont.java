package sun.font;

import java.awt.FontFormatException;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class NativeFont extends PhysicalFont {
  public NativeFont(String paramString, boolean paramBoolean) throws FontFormatException { throw new FontFormatException("NativeFont not used on Windows"); }
  
  static boolean hasExternalBitmaps(String paramString) { return false; }
  
  public CharToGlyphMapper getMapper() { return null; }
  
  PhysicalFont getDelegateFont() { return null; }
  
  FontStrike createStrike(FontStrikeDesc paramFontStrikeDesc) { return null; }
  
  public Rectangle2D getMaxCharBounds(FontRenderContext paramFontRenderContext) { return null; }
  
  StrikeMetrics getFontMetrics(long paramLong) { return null; }
  
  public GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2) { return null; }
  
  public GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2) { return null; }
  
  long getGlyphImage(long paramLong, int paramInt) { return 0L; }
  
  void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat) {}
  
  float getGlyphAdvance(long paramLong, int paramInt) { return 0.0F; }
  
  Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt) { return new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\NativeFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */