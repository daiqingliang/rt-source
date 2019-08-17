package com.sun.media.sound;

public final class ModelStandardTransform implements ModelTransform {
  public static final boolean DIRECTION_MIN2MAX = false;
  
  public static final boolean DIRECTION_MAX2MIN = true;
  
  public static final boolean POLARITY_UNIPOLAR = false;
  
  public static final boolean POLARITY_BIPOLAR = true;
  
  public static final int TRANSFORM_LINEAR = 0;
  
  public static final int TRANSFORM_CONCAVE = 1;
  
  public static final int TRANSFORM_CONVEX = 2;
  
  public static final int TRANSFORM_SWITCH = 3;
  
  public static final int TRANSFORM_ABSOLUTE = 4;
  
  private boolean direction = false;
  
  private boolean polarity = false;
  
  private int transform = 0;
  
  public ModelStandardTransform() {}
  
  public ModelStandardTransform(boolean paramBoolean) { this.direction = paramBoolean; }
  
  public ModelStandardTransform(boolean paramBoolean1, boolean paramBoolean2) {
    this.direction = paramBoolean1;
    this.polarity = paramBoolean2;
  }
  
  public ModelStandardTransform(boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
    this.direction = paramBoolean1;
    this.polarity = paramBoolean2;
    this.transform = paramInt;
  }
  
  public double transform(double paramDouble) {
    double d2;
    double d1;
    if (this.direction == true)
      paramDouble = 1.0D - paramDouble; 
    if (this.polarity == true)
      paramDouble = paramDouble * 2.0D - 1.0D; 
    switch (this.transform) {
      case 1:
        d1 = Math.signum(paramDouble);
        d2 = Math.abs(paramDouble);
        d2 = -(0.4166666666666667D / Math.log(10.0D)) * Math.log(1.0D - d2);
        if (d2 < 0.0D) {
          d2 = 0.0D;
        } else if (d2 > 1.0D) {
          d2 = 1.0D;
        } 
        return d1 * d2;
      case 2:
        d1 = Math.signum(paramDouble);
        d2 = Math.abs(paramDouble);
        d2 = 1.0D + 0.4166666666666667D / Math.log(10.0D) * Math.log(d2);
        if (d2 < 0.0D) {
          d2 = 0.0D;
        } else if (d2 > 1.0D) {
          d2 = 1.0D;
        } 
        return d1 * d2;
      case 3:
        return (this.polarity == true) ? ((paramDouble > 0.0D) ? 1.0D : -1.0D) : ((paramDouble > 0.5D) ? 1.0D : 0.0D);
      case 4:
        return Math.abs(paramDouble);
    } 
    return paramDouble;
  }
  
  public boolean getDirection() { return this.direction; }
  
  public void setDirection(boolean paramBoolean) { this.direction = paramBoolean; }
  
  public boolean getPolarity() { return this.polarity; }
  
  public void setPolarity(boolean paramBoolean) { this.polarity = paramBoolean; }
  
  public int getTransform() { return this.transform; }
  
  public void setTransform(int paramInt) { this.transform = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\ModelStandardTransform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */