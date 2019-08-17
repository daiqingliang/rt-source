package java.awt.geom;

import java.util.NoSuchElementException;

class EllipseIterator implements PathIterator {
  double x;
  
  double y;
  
  double w;
  
  double h;
  
  AffineTransform affine;
  
  int index;
  
  public static final double CtrlVal = 0.5522847498307933D;
  
  private static final double pcv = 0.7761423749153966D;
  
  private static final double ncv = 0.22385762508460333D;
  
  private static double[][] ctrlpts = { { 1.0D, 0.7761423749153966D, 0.7761423749153966D, 1.0D, 0.5D, 1.0D }, { 0.22385762508460333D, 1.0D, 0.0D, 0.7761423749153966D, 0.0D, 0.5D }, { 0.0D, 0.22385762508460333D, 0.22385762508460333D, 0.0D, 0.5D, 0.0D }, { 0.7761423749153966D, 0.0D, 1.0D, 0.22385762508460333D, 1.0D, 0.5D } };
  
  EllipseIterator(Ellipse2D paramEllipse2D, AffineTransform paramAffineTransform) {
    this.x = paramEllipse2D.getX();
    this.y = paramEllipse2D.getY();
    this.w = paramEllipse2D.getWidth();
    this.h = paramEllipse2D.getHeight();
    this.affine = paramAffineTransform;
    if (this.w < 0.0D || this.h < 0.0D)
      this.index = 6; 
  }
  
  public int getWindingRule() { return 1; }
  
  public boolean isDone() { return (this.index > 5); }
  
  public void next() { this.index++; }
  
  public int currentSegment(float[] paramArrayOfFloat) {
    if (isDone())
      throw new NoSuchElementException("ellipse iterator out of bounds"); 
    if (this.index == 5)
      return 4; 
    if (this.index == 0) {
      double[] arrayOfDouble1 = ctrlpts[3];
      paramArrayOfFloat[0] = (float)(this.x + arrayOfDouble1[4] * this.w);
      paramArrayOfFloat[1] = (float)(this.y + arrayOfDouble1[5] * this.h);
      if (this.affine != null)
        this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1); 
      return 0;
    } 
    double[] arrayOfDouble = ctrlpts[this.index - 1];
    paramArrayOfFloat[0] = (float)(this.x + arrayOfDouble[0] * this.w);
    paramArrayOfFloat[1] = (float)(this.y + arrayOfDouble[1] * this.h);
    paramArrayOfFloat[2] = (float)(this.x + arrayOfDouble[2] * this.w);
    paramArrayOfFloat[3] = (float)(this.y + arrayOfDouble[3] * this.h);
    paramArrayOfFloat[4] = (float)(this.x + arrayOfDouble[4] * this.w);
    paramArrayOfFloat[5] = (float)(this.y + arrayOfDouble[5] * this.h);
    if (this.affine != null)
      this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 3); 
    return 3;
  }
  
  public int currentSegment(double[] paramArrayOfDouble) {
    if (isDone())
      throw new NoSuchElementException("ellipse iterator out of bounds"); 
    if (this.index == 5)
      return 4; 
    if (this.index == 0) {
      double[] arrayOfDouble1 = ctrlpts[3];
      paramArrayOfDouble[0] = this.x + arrayOfDouble1[4] * this.w;
      paramArrayOfDouble[1] = this.y + arrayOfDouble1[5] * this.h;
      if (this.affine != null)
        this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1); 
      return 0;
    } 
    double[] arrayOfDouble = ctrlpts[this.index - 1];
    paramArrayOfDouble[0] = this.x + arrayOfDouble[0] * this.w;
    paramArrayOfDouble[1] = this.y + arrayOfDouble[1] * this.h;
    paramArrayOfDouble[2] = this.x + arrayOfDouble[2] * this.w;
    paramArrayOfDouble[3] = this.y + arrayOfDouble[3] * this.h;
    paramArrayOfDouble[4] = this.x + arrayOfDouble[4] * this.w;
    paramArrayOfDouble[5] = this.y + arrayOfDouble[5] * this.h;
    if (this.affine != null)
      this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 3); 
    return 3;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\EllipseIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */