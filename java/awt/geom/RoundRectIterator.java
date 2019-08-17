package java.awt.geom;

import java.util.NoSuchElementException;

class RoundRectIterator implements PathIterator {
  double x;
  
  double y;
  
  double w;
  
  double h;
  
  double aw;
  
  double ah;
  
  AffineTransform affine;
  
  int index;
  
  private static final double angle = 0.7853981633974483D;
  
  private static final double a = 1.0D - Math.cos(0.7853981633974483D);
  
  private static final double b = Math.tan(0.7853981633974483D);
  
  private static final double c = Math.sqrt(1.0D + b * b) - 1.0D + a;
  
  private static final double cv = 1.3333333333333333D * a * b / c;
  
  private static final double acv = (1.0D - cv) / 2.0D;
  
  private static double[][] ctrlpts = { { 0.0D, 0.0D, 0.0D, 0.5D }, { 0.0D, 0.0D, 1.0D, -0.5D }, { 
        0.0D, 0.0D, 1.0D, -acv, 0.0D, acv, 1.0D, 0.0D, 0.0D, 0.5D, 
        1.0D, 0.0D }, { 1.0D, -0.5D, 1.0D, 0.0D }, { 
        1.0D, -acv, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, -acv, 1.0D, 0.0D, 
        1.0D, -0.5D }, { 1.0D, 0.0D, 0.0D, 0.5D }, { 
        1.0D, 0.0D, 0.0D, acv, 1.0D, -acv, 0.0D, 0.0D, 1.0D, -0.5D, 
        0.0D, 0.0D }, { 0.0D, 0.5D, 0.0D, 0.0D }, { 
        0.0D, acv, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, acv, 0.0D, 0.0D, 
        0.0D, 0.5D }, {} };
  
  private static int[] types = { 0, 1, 3, 1, 3, 1, 3, 1, 3, 4 };
  
  RoundRectIterator(RoundRectangle2D paramRoundRectangle2D, AffineTransform paramAffineTransform) {
    this.x = paramRoundRectangle2D.getX();
    this.y = paramRoundRectangle2D.getY();
    this.w = paramRoundRectangle2D.getWidth();
    this.h = paramRoundRectangle2D.getHeight();
    this.aw = Math.min(this.w, Math.abs(paramRoundRectangle2D.getArcWidth()));
    this.ah = Math.min(this.h, Math.abs(paramRoundRectangle2D.getArcHeight()));
    this.affine = paramAffineTransform;
    if (this.aw < 0.0D || this.ah < 0.0D)
      this.index = ctrlpts.length; 
  }
  
  public int getWindingRule() { return 1; }
  
  public boolean isDone() { return (this.index >= ctrlpts.length); }
  
  public void next() { this.index++; }
  
  public int currentSegment(float[] paramArrayOfFloat) {
    if (isDone())
      throw new NoSuchElementException("roundrect iterator out of bounds"); 
    double[] arrayOfDouble = ctrlpts[this.index];
    byte b1 = 0;
    for (boolean bool = false; bool < arrayOfDouble.length; bool += true) {
      paramArrayOfFloat[b1++] = (float)(this.x + arrayOfDouble[bool + false] * this.w + arrayOfDouble[bool + true] * this.aw);
      paramArrayOfFloat[b1++] = (float)(this.y + arrayOfDouble[bool + 2] * this.h + arrayOfDouble[bool + 3] * this.ah);
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, b1 / 2); 
    return types[this.index];
  }
  
  public int currentSegment(double[] paramArrayOfDouble) {
    if (isDone())
      throw new NoSuchElementException("roundrect iterator out of bounds"); 
    double[] arrayOfDouble = ctrlpts[this.index];
    byte b1 = 0;
    for (boolean bool = false; bool < arrayOfDouble.length; bool += true) {
      paramArrayOfDouble[b1++] = this.x + arrayOfDouble[bool + false] * this.w + arrayOfDouble[bool + true] * this.aw;
      paramArrayOfDouble[b1++] = this.y + arrayOfDouble[bool + 2] * this.h + arrayOfDouble[bool + 3] * this.ah;
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, b1 / 2); 
    return types[this.index];
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\RoundRectIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */