package sun.font;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class NativeStrike extends PhysicalStrike {
  NativeFont nativeFont;
  
  NativeStrike(NativeFont paramNativeFont, FontStrikeDesc paramFontStrikeDesc) {
    super(paramNativeFont, paramFontStrikeDesc);
    throw new RuntimeException("NativeFont not used on Windows");
  }
  
  NativeStrike(NativeFont paramNativeFont, FontStrikeDesc paramFontStrikeDesc, boolean paramBoolean) {
    super(paramNativeFont, paramFontStrikeDesc);
    throw new RuntimeException("NativeFont not used on Windows");
  }
  
  void getGlyphImagePtrs(int[] paramArrayOfInt, long[] paramArrayOfLong, int paramInt) {}
  
  long getGlyphImagePtr(int paramInt) { return 0L; }
  
  long getGlyphImagePtrNoCache(int paramInt) { return 0L; }
  
  void getGlyphImageBounds(int paramInt, Point2D.Float paramFloat, Rectangle paramRectangle) {}
  
  Point2D.Float getGlyphMetrics(int paramInt) { return null; }
  
  float getGlyphAdvance(int paramInt) { return 0.0F; }
  
  Rectangle2D.Float getGlyphOutlineBounds(int paramInt) { return null; }
  
  GeneralPath getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) { return null; }
  
  GeneralPath getGlyphVectorOutline(int[] paramArrayOfInt, float paramFloat1, float paramFloat2) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\NativeStrike.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */