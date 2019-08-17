package java.awt;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import sun.java2d.pipe.RenderingEngine;

public class BasicStroke implements Stroke {
  public static final int JOIN_MITER = 0;
  
  public static final int JOIN_ROUND = 1;
  
  public static final int JOIN_BEVEL = 2;
  
  public static final int CAP_BUTT = 0;
  
  public static final int CAP_ROUND = 1;
  
  public static final int CAP_SQUARE = 2;
  
  float width;
  
  int join;
  
  int cap;
  
  float miterlimit;
  
  float[] dash;
  
  float dash_phase;
  
  @ConstructorProperties({"lineWidth", "endCap", "lineJoin", "miterLimit", "dashArray", "dashPhase"})
  public BasicStroke(float paramFloat1, int paramInt1, int paramInt2, float paramFloat2, float[] paramArrayOfFloat, float paramFloat3) {
    if (paramFloat1 < 0.0F)
      throw new IllegalArgumentException("negative width"); 
    if (paramInt1 != 0 && paramInt1 != 1 && paramInt1 != 2)
      throw new IllegalArgumentException("illegal end cap value"); 
    if (paramInt2 == 0) {
      if (paramFloat2 < 1.0F)
        throw new IllegalArgumentException("miter limit < 1"); 
    } else if (paramInt2 != 1 && paramInt2 != 2) {
      throw new IllegalArgumentException("illegal line join value");
    } 
    if (paramArrayOfFloat != null) {
      if (paramFloat3 < 0.0F)
        throw new IllegalArgumentException("negative dash phase"); 
      boolean bool = true;
      for (byte b = 0; b < paramArrayOfFloat.length; b++) {
        float f = paramArrayOfFloat[b];
        if (f > 0.0D) {
          bool = false;
        } else if (f < 0.0D) {
          throw new IllegalArgumentException("negative dash length");
        } 
      } 
      if (bool)
        throw new IllegalArgumentException("dash lengths all zero"); 
    } 
    this.width = paramFloat1;
    this.cap = paramInt1;
    this.join = paramInt2;
    this.miterlimit = paramFloat2;
    if (paramArrayOfFloat != null)
      this.dash = (float[])paramArrayOfFloat.clone(); 
    this.dash_phase = paramFloat3;
  }
  
  public BasicStroke(float paramFloat1, int paramInt1, int paramInt2, float paramFloat2) { this(paramFloat1, paramInt1, paramInt2, paramFloat2, null, 0.0F); }
  
  public BasicStroke(float paramFloat, int paramInt1, int paramInt2) { this(paramFloat, paramInt1, paramInt2, 10.0F, null, 0.0F); }
  
  public BasicStroke(float paramFloat) { this(paramFloat, 2, 0, 10.0F, null, 0.0F); }
  
  public BasicStroke() { this(1.0F, 2, 0, 10.0F, null, 0.0F); }
  
  public Shape createStrokedShape(Shape paramShape) {
    RenderingEngine renderingEngine = RenderingEngine.getInstance();
    return renderingEngine.createStrokedShape(paramShape, this.width, this.cap, this.join, this.miterlimit, this.dash, this.dash_phase);
  }
  
  public float getLineWidth() { return this.width; }
  
  public int getEndCap() { return this.cap; }
  
  public int getLineJoin() { return this.join; }
  
  public float getMiterLimit() { return this.miterlimit; }
  
  public float[] getDashArray() { return (this.dash == null) ? null : (float[])this.dash.clone(); }
  
  public float getDashPhase() { return this.dash_phase; }
  
  public int hashCode() {
    int i = Float.floatToIntBits(this.width);
    i = i * 31 + this.join;
    i = i * 31 + this.cap;
    i = i * 31 + Float.floatToIntBits(this.miterlimit);
    if (this.dash != null) {
      i = i * 31 + Float.floatToIntBits(this.dash_phase);
      for (byte b = 0; b < this.dash.length; b++)
        i = i * 31 + Float.floatToIntBits(this.dash[b]); 
    } 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof BasicStroke))
      return false; 
    BasicStroke basicStroke = (BasicStroke)paramObject;
    if (this.width != basicStroke.width)
      return false; 
    if (this.join != basicStroke.join)
      return false; 
    if (this.cap != basicStroke.cap)
      return false; 
    if (this.miterlimit != basicStroke.miterlimit)
      return false; 
    if (this.dash != null) {
      if (this.dash_phase != basicStroke.dash_phase)
        return false; 
      if (!Arrays.equals(this.dash, basicStroke.dash))
        return false; 
    } else if (basicStroke.dash != null) {
      return false;
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\BasicStroke.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */