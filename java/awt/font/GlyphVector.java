package java.awt.font;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class GlyphVector implements Cloneable {
  public static final int FLAG_HAS_TRANSFORMS = 1;
  
  public static final int FLAG_HAS_POSITION_ADJUSTMENTS = 2;
  
  public static final int FLAG_RUN_RTL = 4;
  
  public static final int FLAG_COMPLEX_GLYPHS = 8;
  
  public static final int FLAG_MASK = 15;
  
  public abstract Font getFont();
  
  public abstract FontRenderContext getFontRenderContext();
  
  public abstract void performDefaultLayout();
  
  public abstract int getNumGlyphs();
  
  public abstract int getGlyphCode(int paramInt);
  
  public abstract int[] getGlyphCodes(int paramInt1, int paramInt2, int[] paramArrayOfInt);
  
  public int getGlyphCharIndex(int paramInt) { return paramInt; }
  
  public int[] getGlyphCharIndices(int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    if (paramArrayOfInt == null)
      paramArrayOfInt = new int[paramInt2]; 
    byte b = 0;
    for (int i = paramInt1; b < paramInt2; i++) {
      paramArrayOfInt[b] = getGlyphCharIndex(i);
      b++;
    } 
    return paramArrayOfInt;
  }
  
  public abstract Rectangle2D getLogicalBounds();
  
  public abstract Rectangle2D getVisualBounds();
  
  public Rectangle getPixelBounds(FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) {
    Rectangle2D rectangle2D = getVisualBounds();
    int i = (int)Math.floor(rectangle2D.getX() + paramFloat1);
    int j = (int)Math.floor(rectangle2D.getY() + paramFloat2);
    int k = (int)Math.ceil(rectangle2D.getMaxX() + paramFloat1);
    int m = (int)Math.ceil(rectangle2D.getMaxY() + paramFloat2);
    return new Rectangle(i, j, k - i, m - j);
  }
  
  public abstract Shape getOutline();
  
  public abstract Shape getOutline(float paramFloat1, float paramFloat2);
  
  public abstract Shape getGlyphOutline(int paramInt);
  
  public Shape getGlyphOutline(int paramInt, float paramFloat1, float paramFloat2) {
    Shape shape = getGlyphOutline(paramInt);
    AffineTransform affineTransform = AffineTransform.getTranslateInstance(paramFloat1, paramFloat2);
    return affineTransform.createTransformedShape(shape);
  }
  
  public abstract Point2D getGlyphPosition(int paramInt);
  
  public abstract void setGlyphPosition(int paramInt, Point2D paramPoint2D);
  
  public abstract AffineTransform getGlyphTransform(int paramInt);
  
  public abstract void setGlyphTransform(int paramInt, AffineTransform paramAffineTransform);
  
  public int getLayoutFlags() { return 0; }
  
  public abstract float[] getGlyphPositions(int paramInt1, int paramInt2, float[] paramArrayOfFloat);
  
  public abstract Shape getGlyphLogicalBounds(int paramInt);
  
  public abstract Shape getGlyphVisualBounds(int paramInt);
  
  public Rectangle getGlyphPixelBounds(int paramInt, FontRenderContext paramFontRenderContext, float paramFloat1, float paramFloat2) {
    Rectangle2D rectangle2D = getGlyphVisualBounds(paramInt).getBounds2D();
    int i = (int)Math.floor(rectangle2D.getX() + paramFloat1);
    int j = (int)Math.floor(rectangle2D.getY() + paramFloat2);
    int k = (int)Math.ceil(rectangle2D.getMaxX() + paramFloat1);
    int m = (int)Math.ceil(rectangle2D.getMaxY() + paramFloat2);
    return new Rectangle(i, j, k - i, m - j);
  }
  
  public abstract GlyphMetrics getGlyphMetrics(int paramInt);
  
  public abstract GlyphJustificationInfo getGlyphJustificationInfo(int paramInt);
  
  public abstract boolean equals(GlyphVector paramGlyphVector);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\GlyphVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */