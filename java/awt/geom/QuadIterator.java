package java.awt.geom;

import java.util.NoSuchElementException;

class QuadIterator implements PathIterator {
  QuadCurve2D quad;
  
  AffineTransform affine;
  
  int index;
  
  QuadIterator(QuadCurve2D paramQuadCurve2D, AffineTransform paramAffineTransform) {
    this.quad = paramQuadCurve2D;
    this.affine = paramAffineTransform;
  }
  
  public int getWindingRule() { return 1; }
  
  public boolean isDone() { return (this.index > 1); }
  
  public void next() { this.index++; }
  
  public int currentSegment(float[] paramArrayOfFloat) {
    byte b;
    if (isDone())
      throw new NoSuchElementException("quad iterator iterator out of bounds"); 
    if (this.index == 0) {
      paramArrayOfFloat[0] = (float)this.quad.getX1();
      paramArrayOfFloat[1] = (float)this.quad.getY1();
      b = 0;
    } else {
      paramArrayOfFloat[0] = (float)this.quad.getCtrlX();
      paramArrayOfFloat[1] = (float)this.quad.getCtrlY();
      paramArrayOfFloat[2] = (float)this.quad.getX2();
      paramArrayOfFloat[3] = (float)this.quad.getY2();
      b = 2;
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, (this.index == 0) ? 1 : 2); 
    return b;
  }
  
  public int currentSegment(double[] paramArrayOfDouble) {
    byte b;
    if (isDone())
      throw new NoSuchElementException("quad iterator iterator out of bounds"); 
    if (this.index == 0) {
      paramArrayOfDouble[0] = this.quad.getX1();
      paramArrayOfDouble[1] = this.quad.getY1();
      b = 0;
    } else {
      paramArrayOfDouble[0] = this.quad.getCtrlX();
      paramArrayOfDouble[1] = this.quad.getCtrlY();
      paramArrayOfDouble[2] = this.quad.getX2();
      paramArrayOfDouble[3] = this.quad.getY2();
      b = 2;
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, (this.index == 0) ? 1 : 2); 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\QuadIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */