package sun.font;

import java.awt.font.LineMetrics;

public final class CoreMetrics {
  public final float ascent;
  
  public final float descent;
  
  public final float leading;
  
  public final float height;
  
  public final int baselineIndex;
  
  public final float[] baselineOffsets;
  
  public final float strikethroughOffset;
  
  public final float strikethroughThickness;
  
  public final float underlineOffset;
  
  public final float underlineThickness;
  
  public final float ssOffset;
  
  public final float italicAngle;
  
  public CoreMetrics(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt, float[] paramArrayOfFloat, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10) {
    this.ascent = paramFloat1;
    this.descent = paramFloat2;
    this.leading = paramFloat3;
    this.height = paramFloat4;
    this.baselineIndex = paramInt;
    this.baselineOffsets = paramArrayOfFloat;
    this.strikethroughOffset = paramFloat5;
    this.strikethroughThickness = paramFloat6;
    this.underlineOffset = paramFloat7;
    this.underlineThickness = paramFloat8;
    this.ssOffset = paramFloat9;
    this.italicAngle = paramFloat10;
  }
  
  public static CoreMetrics get(LineMetrics paramLineMetrics) { return ((FontLineMetrics)paramLineMetrics).cm; }
  
  public final int hashCode() { return Float.floatToIntBits(this.ascent + this.ssOffset); }
  
  public final boolean equals(Object paramObject) {
    try {
      return equals((CoreMetrics)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public final boolean equals(CoreMetrics paramCoreMetrics) { return (paramCoreMetrics != null) ? ((this == paramCoreMetrics) ? true : ((this.ascent == paramCoreMetrics.ascent && this.descent == paramCoreMetrics.descent && this.leading == paramCoreMetrics.leading && this.baselineIndex == paramCoreMetrics.baselineIndex && this.baselineOffsets[0] == paramCoreMetrics.baselineOffsets[0] && this.baselineOffsets[1] == paramCoreMetrics.baselineOffsets[1] && this.baselineOffsets[2] == paramCoreMetrics.baselineOffsets[2] && this.strikethroughOffset == paramCoreMetrics.strikethroughOffset && this.strikethroughThickness == paramCoreMetrics.strikethroughThickness && this.underlineOffset == paramCoreMetrics.underlineOffset && this.underlineThickness == paramCoreMetrics.underlineThickness && this.ssOffset == paramCoreMetrics.ssOffset && this.italicAngle == paramCoreMetrics.italicAngle))) : false; }
  
  public final float effectiveBaselineOffset(float[] paramArrayOfFloat) {
    switch (this.baselineIndex) {
      case -1:
        return paramArrayOfFloat[4] + this.ascent;
      case -2:
        return paramArrayOfFloat[3] - this.descent;
    } 
    return paramArrayOfFloat[this.baselineIndex];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\CoreMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */