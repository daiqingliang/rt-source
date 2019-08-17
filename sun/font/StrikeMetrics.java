package sun.font;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public final class StrikeMetrics {
  public float ascentX = this.ascentY = 2.14748365E9F;
  
  public float ascentY;
  
  public float descentX;
  
  public float descentY;
  
  public float baselineX;
  
  public float baselineY;
  
  public float leadingX;
  
  public float leadingY;
  
  public float maxAdvanceX;
  
  public float maxAdvanceY;
  
  StrikeMetrics() {
    this.descentX = this.descentY = this.leadingX = this.leadingY = -2.14748365E9F;
    this.baselineX = this.maxAdvanceX = this.maxAdvanceY = -2.14748365E9F;
  }
  
  StrikeMetrics(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, float paramFloat9, float paramFloat10) {
    this.ascentY = paramFloat2;
    this.descentX = paramFloat3;
    this.descentY = paramFloat4;
    this.baselineX = paramFloat5;
    this.baselineY = paramFloat6;
    this.leadingX = paramFloat7;
    this.leadingY = paramFloat8;
    this.maxAdvanceX = paramFloat9;
    this.maxAdvanceY = paramFloat10;
  }
  
  public float getAscent() { return -this.ascentY; }
  
  public float getDescent() { return this.descentY; }
  
  public float getLeading() { return this.leadingY; }
  
  public float getMaxAdvance() { return this.maxAdvanceX; }
  
  void merge(StrikeMetrics paramStrikeMetrics) {
    if (paramStrikeMetrics == null)
      return; 
    if (paramStrikeMetrics.ascentX < this.ascentX)
      this.ascentX = paramStrikeMetrics.ascentX; 
    if (paramStrikeMetrics.ascentY < this.ascentY)
      this.ascentY = paramStrikeMetrics.ascentY; 
    if (paramStrikeMetrics.descentX > this.descentX)
      this.descentX = paramStrikeMetrics.descentX; 
    if (paramStrikeMetrics.descentY > this.descentY)
      this.descentY = paramStrikeMetrics.descentY; 
    if (paramStrikeMetrics.baselineX > this.baselineX)
      this.baselineX = paramStrikeMetrics.baselineX; 
    if (paramStrikeMetrics.baselineY > this.baselineY)
      this.baselineY = paramStrikeMetrics.baselineY; 
    if (paramStrikeMetrics.leadingX > this.leadingX)
      this.leadingX = paramStrikeMetrics.leadingX; 
    if (paramStrikeMetrics.leadingY > this.leadingY)
      this.leadingY = paramStrikeMetrics.leadingY; 
    if (paramStrikeMetrics.maxAdvanceX > this.maxAdvanceX)
      this.maxAdvanceX = paramStrikeMetrics.maxAdvanceX; 
    if (paramStrikeMetrics.maxAdvanceY > this.maxAdvanceY)
      this.maxAdvanceY = paramStrikeMetrics.maxAdvanceY; 
  }
  
  void convertToUserSpace(AffineTransform paramAffineTransform) {
    Point2D.Float float = new Point2D.Float();
    float.x = this.ascentX;
    float.y = this.ascentY;
    paramAffineTransform.deltaTransform(float, float);
    this.ascentX = float.x;
    this.ascentY = float.y;
    float.x = this.descentX;
    float.y = this.descentY;
    paramAffineTransform.deltaTransform(float, float);
    this.descentX = float.x;
    this.descentY = float.y;
    float.x = this.baselineX;
    float.y = this.baselineY;
    paramAffineTransform.deltaTransform(float, float);
    this.baselineX = float.x;
    this.baselineY = float.y;
    float.x = this.leadingX;
    float.y = this.leadingY;
    paramAffineTransform.deltaTransform(float, float);
    this.leadingX = float.x;
    this.leadingY = float.y;
    float.x = this.maxAdvanceX;
    float.y = this.maxAdvanceY;
    paramAffineTransform.deltaTransform(float, float);
    this.maxAdvanceX = float.x;
    this.maxAdvanceY = float.y;
  }
  
  public String toString() { return "ascent:x=" + this.ascentX + " y=" + this.ascentY + " descent:x=" + this.descentX + " y=" + this.descentY + " baseline:x=" + this.baselineX + " y=" + this.baselineY + " leading:x=" + this.leadingX + " y=" + this.leadingY + " maxAdvance:x=" + this.maxAdvanceX + " y=" + this.maxAdvanceY; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\StrikeMetrics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */