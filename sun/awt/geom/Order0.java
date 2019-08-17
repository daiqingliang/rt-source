package sun.awt.geom;

import java.awt.geom.Rectangle2D;

final class Order0 extends Curve {
  private double x;
  
  private double y;
  
  public Order0(double paramDouble1, double paramDouble2) {
    super(1);
    this.x = paramDouble1;
    this.y = paramDouble2;
  }
  
  public int getOrder() { return 0; }
  
  public double getXTop() { return this.x; }
  
  public double getYTop() { return this.y; }
  
  public double getXBot() { return this.x; }
  
  public double getYBot() { return this.y; }
  
  public double getXMin() { return this.x; }
  
  public double getXMax() { return this.x; }
  
  public double getX0() { return this.x; }
  
  public double getY0() { return this.y; }
  
  public double getX1() { return this.x; }
  
  public double getY1() { return this.y; }
  
  public double XforY(double paramDouble) { return paramDouble; }
  
  public double TforY(double paramDouble) { return 0.0D; }
  
  public double XforT(double paramDouble) { return this.x; }
  
  public double YforT(double paramDouble) { return this.y; }
  
  public double dXforT(double paramDouble, int paramInt) { return 0.0D; }
  
  public double dYforT(double paramDouble, int paramInt) { return 0.0D; }
  
  public double nextVertical(double paramDouble1, double paramDouble2) { return paramDouble2; }
  
  public int crossingsFor(double paramDouble1, double paramDouble2) { return 0; }
  
  public boolean accumulateCrossings(Crossings paramCrossings) { return (this.x > paramCrossings.getXLo() && this.x < paramCrossings.getXHi() && this.y > paramCrossings.getYLo() && this.y < paramCrossings.getYHi()); }
  
  public void enlarge(Rectangle2D paramRectangle2D) { paramRectangle2D.add(this.x, this.y); }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt) { return this; }
  
  public Curve getReversedCurve() { return this; }
  
  public int getSegment(double[] paramArrayOfDouble) {
    paramArrayOfDouble[0] = this.x;
    paramArrayOfDouble[1] = this.y;
    return 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\Order0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */