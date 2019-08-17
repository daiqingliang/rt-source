package java.awt.geom;

import java.util.NoSuchElementException;

class LineIterator implements PathIterator {
  Line2D line;
  
  AffineTransform affine;
  
  int index;
  
  LineIterator(Line2D paramLine2D, AffineTransform paramAffineTransform) {
    this.line = paramLine2D;
    this.affine = paramAffineTransform;
  }
  
  public int getWindingRule() { return 1; }
  
  public boolean isDone() { return (this.index > 1); }
  
  public void next() { this.index++; }
  
  public int currentSegment(float[] paramArrayOfFloat) {
    byte b;
    if (isDone())
      throw new NoSuchElementException("line iterator out of bounds"); 
    if (this.index == 0) {
      paramArrayOfFloat[0] = (float)this.line.getX1();
      paramArrayOfFloat[1] = (float)this.line.getY1();
      b = 0;
    } else {
      paramArrayOfFloat[0] = (float)this.line.getX2();
      paramArrayOfFloat[1] = (float)this.line.getY2();
      b = 1;
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfFloat, 0, paramArrayOfFloat, 0, 1); 
    return b;
  }
  
  public int currentSegment(double[] paramArrayOfDouble) {
    byte b;
    if (isDone())
      throw new NoSuchElementException("line iterator out of bounds"); 
    if (this.index == 0) {
      paramArrayOfDouble[0] = this.line.getX1();
      paramArrayOfDouble[1] = this.line.getY1();
      b = 0;
    } else {
      paramArrayOfDouble[0] = this.line.getX2();
      paramArrayOfDouble[1] = this.line.getY2();
      b = 1;
    } 
    if (this.affine != null)
      this.affine.transform(paramArrayOfDouble, 0, paramArrayOfDouble, 0, 1); 
    return b;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\LineIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */