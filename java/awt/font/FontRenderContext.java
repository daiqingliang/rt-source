package java.awt.font;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class FontRenderContext {
  private AffineTransform tx;
  
  private Object aaHintValue;
  
  private Object fmHintValue;
  
  private boolean defaulting;
  
  protected FontRenderContext() {
    this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
    this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;
    this.defaulting = true;
  }
  
  public FontRenderContext(AffineTransform paramAffineTransform, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramAffineTransform != null && !paramAffineTransform.isIdentity())
      this.tx = new AffineTransform(paramAffineTransform); 
    if (paramBoolean1) {
      this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
    } else {
      this.aaHintValue = RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
    } 
    if (paramBoolean2) {
      this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_ON;
    } else {
      this.fmHintValue = RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
    } 
  }
  
  public FontRenderContext(AffineTransform paramAffineTransform, Object paramObject1, Object paramObject2) {
    if (paramAffineTransform != null && !paramAffineTransform.isIdentity())
      this.tx = new AffineTransform(paramAffineTransform); 
    try {
      if (RenderingHints.KEY_TEXT_ANTIALIASING.isCompatibleValue(paramObject1)) {
        this.aaHintValue = paramObject1;
      } else {
        throw new IllegalArgumentException("AA hint:" + paramObject1);
      } 
    } catch (Exception exception) {
      throw new IllegalArgumentException("AA hint:" + paramObject1);
    } 
    try {
      if (RenderingHints.KEY_FRACTIONALMETRICS.isCompatibleValue(paramObject2)) {
        this.fmHintValue = paramObject2;
      } else {
        throw new IllegalArgumentException("FM hint:" + paramObject2);
      } 
    } catch (Exception exception) {
      throw new IllegalArgumentException("FM hint:" + paramObject2);
    } 
  }
  
  public boolean isTransformed() { return !this.defaulting ? ((this.tx != null)) : (!getTransform().isIdentity()); }
  
  public int getTransformType() { return !this.defaulting ? ((this.tx == null) ? 0 : this.tx.getType()) : getTransform().getType(); }
  
  public AffineTransform getTransform() { return (this.tx == null) ? new AffineTransform() : new AffineTransform(this.tx); }
  
  public boolean isAntiAliased() { return (this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF && this.aaHintValue != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT); }
  
  public boolean usesFractionalMetrics() { return (this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_OFF && this.fmHintValue != RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT); }
  
  public Object getAntiAliasingHint() { return this.defaulting ? (isAntiAliased() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) : this.aaHintValue; }
  
  public Object getFractionalMetricsHint() { return this.defaulting ? (usesFractionalMetrics() ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF) : this.fmHintValue; }
  
  public boolean equals(Object paramObject) {
    try {
      return equals((FontRenderContext)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public boolean equals(FontRenderContext paramFontRenderContext) { return (this == paramFontRenderContext) ? true : ((paramFontRenderContext == null) ? false : ((!paramFontRenderContext.defaulting && !this.defaulting) ? ((paramFontRenderContext.aaHintValue == this.aaHintValue && paramFontRenderContext.fmHintValue == this.fmHintValue) ? ((this.tx == null) ? ((paramFontRenderContext.tx == null)) : this.tx.equals(paramFontRenderContext.tx)) : false) : ((paramFontRenderContext.getAntiAliasingHint() == getAntiAliasingHint() && paramFontRenderContext.getFractionalMetricsHint() == getFractionalMetricsHint() && paramFontRenderContext.getTransform().equals(getTransform()))))); }
  
  public int hashCode() {
    int i = (this.tx == null) ? 0 : this.tx.hashCode();
    if (this.defaulting) {
      i += getAntiAliasingHint().hashCode();
      i += getFractionalMetricsHint().hashCode();
    } else {
      i += this.aaHintValue.hashCode();
      i += this.fmHintValue.hashCode();
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\FontRenderContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */