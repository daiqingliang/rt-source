package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

public final class FontLineMetrics extends LineMetrics implements Cloneable {
  public int numchars;
  
  public final CoreMetrics cm;
  
  public final FontRenderContext frc;
  
  public FontLineMetrics(int paramInt, CoreMetrics paramCoreMetrics, FontRenderContext paramFontRenderContext) {
    this.numchars = paramInt;
    this.cm = paramCoreMetrics;
    this.frc = paramFontRenderContext;
  }
  
  public final int getNumChars() { return this.numchars; }
  
  public final float getAscent() { return this.cm.ascent; }
  
  public final float getDescent() { return this.cm.descent; }
  
  public final float getLeading() { return this.cm.leading; }
  
  public final float getHeight() { return this.cm.height; }
  
  public final int getBaselineIndex() { return this.cm.baselineIndex; }
  
  public final float[] getBaselineOffsets() { return (float[])this.cm.baselineOffsets.clone(); }
  
  public final float getStrikethroughOffset() { return this.cm.strikethroughOffset; }
  
  public final float getStrikethroughThickness() { return this.cm.strikethroughThickness; }
  
  public final float getUnderlineOffset() { return this.cm.underlineOffset; }
  
  public final float getUnderlineThickness() { return this.cm.underlineThickness; }
  
  public final int hashCode() { return this.cm.hashCode(); }
  
  public final boolean equals(Object paramObject) {
    try {
      return this.cm.equals(((FontLineMetrics)paramObject).cm);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public final Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\FontLineMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */