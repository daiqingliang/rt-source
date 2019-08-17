package sun.font;

import java.awt.FontFormatException;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class PhysicalFont extends Font2D {
  protected String platName;
  
  protected Object nativeNames;
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject.getClass() == getClass() && ((Font2D)paramObject).fullName.equals(this.fullName)); }
  
  public int hashCode() { return this.fullName.hashCode(); }
  
  PhysicalFont(String paramString, Object paramObject) throws FontFormatException {
    this.platName = paramString;
    this.nativeNames = paramObject;
  }
  
  protected PhysicalFont() {}
  
  Point2D.Float getGlyphPoint(long paramLong, int paramInt1, int paramInt2) { return new Point2D.Float(); }
  
  abstract StrikeMetrics getFontMetrics(long paramLong);
  
  abstract float getGlyphAdvance(long paramLong, int paramInt);
  
  abstract void getGlyphMetrics(long paramLong, int paramInt, Point2D.Float paramFloat);
  
  abstract long getGlyphImage(long paramLong, int paramInt);
  
  abstract Rectangle2D.Float getGlyphOutlineBounds(long paramLong, int paramInt);
  
  abstract GeneralPath getGlyphOutline(long paramLong, int paramInt, float paramFloat1, float paramFloat2);
  
  abstract GeneralPath getGlyphVectorOutline(long paramLong, int[] paramArrayOfInt, int paramInt, float paramFloat1, float paramFloat2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\PhysicalFont.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */