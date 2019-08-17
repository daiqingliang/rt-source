package java.awt.geom;

import java.util.NoSuchElementException;

class CubicIterator implements PathIterator {
  CubicCurve2D cubic;
  
  AffineTransform affine;
  
  int index;
  
  CubicIterator(CubicCurve2D paramCubicCurve2D, AffineTransform paramAffineTransform) {
    this.cubic = paramCubicCurve2D;
    this.affine = paramAffineTransform;
  }
  
  public int getWindingRule() { return 1; }
  
  public boolean isDone() { return (this.index > 1); }
  
  public void next() { this.index++; }
  
  public int currentSegment(float[] paramArrayOfFloat) {
    byte b;
    if (isDone())
      throw new NoSuchElementException("cubic iterator iterator out of bounds"); 
    if (this.index == 0) {
      paramArrayOfFloat[0] = (float)this.cubic.getX1();
      paramArrayOfFloat[1] = (float)this.cubic.getY1();
      b = 0;
    } else {
      paramArrayOfFloat[0] = (float)this.cubic.getCtrlX1();
      paramArrayOfFloat[1] = (float)this.cubic.getCtrlY1();
      paramArrayOfFloat[2] = (float)this.cubic.getCtrlX2();
      paramArrayOfFloat[3] = (float)this.cubic.getCtrlY2();
      paramArrayOfFloat[4] = (float)this.cubic.getX2();
      paramArrayOfFloat[5] = (float)this.cubic.getY2();
      b = 3;
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, (this.index == 0) ? 1 : 3); 
    return b;
  }
  
  public int currentSegment(double[] paramArrayOfDouble) {
    byte b;
    if (isDone())
      throw new NoSuchElementException("cubic iterator iterator out of bounds"); 
    if (this.index == 0) {
      paramArrayOfDouble[0] = this.cubic.getX1();
      paramArrayOfDouble[1] = this.cubic.getY1();
      b = 0;
    } else {
      paramArrayOfDouble[0] = this.cubic.getCtrlX1();
      paramArrayOfDouble[1] = this.cubic.getCtrlY1();
      paramArrayOfDouble[2] = this.cubic.getCtrlX2();
      paramArrayOfDouble[3] = this.cubic.getCtrlY2();
      paramArrayOfDouble[4] = this.cubic.getX2();
      paramArrayOfDouble[5] = this.cubic.getY2();
      b = 3;
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, (this.index == 0) ? 1 : 3); 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\CubicIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */