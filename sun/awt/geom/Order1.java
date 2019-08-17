package sun.awt.geom;

import java.awt.geom.Rectangle2D;

final class Order1 extends Curve {
  private double x0;
  
  private double y0;
  
  private double x1;
  
  private double y1;
  
  private double xmin;
  
  private double xmax;
  
  public Order1(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt) {
    super(paramInt);
    this.x0 = paramDouble1;
    this.y0 = paramDouble2;
    this.x1 = paramDouble3;
    this.y1 = paramDouble4;
    if (paramDouble1 < paramDouble3) {
      this.xmin = paramDouble1;
      this.xmax = paramDouble3;
    } else {
      this.xmin = paramDouble3;
      this.xmax = paramDouble1;
    } 
  }
  
  public int getOrder() { return 1; }
  
  public double getXTop() { return this.x0; }
  
  public double getYTop() { return this.y0; }
  
  public double getXBot() { return this.x1; }
  
  public double getYBot() { return this.y1; }
  
  public double getXMin() { return this.xmin; }
  
  public double getXMax() { return this.xmax; }
  
  public double getX0() { return (this.direction == 1) ? this.x0 : this.x1; }
  
  public double getY0() { return (this.direction == 1) ? this.y0 : this.y1; }
  
  public double getX1() { return (this.direction == -1) ? this.x0 : this.x1; }
  
  public double getY1() { return (this.direction == -1) ? this.y0 : this.y1; }
  
  public double XforY(double paramDouble) { return (this.x0 == this.x1 || paramDouble <= this.y0) ? this.x0 : ((paramDouble >= this.y1) ? this.x1 : (this.x0 + (paramDouble - this.y0) * (this.x1 - this.x0) / (this.y1 - this.y0))); }
  
  public double TforY(double paramDouble) { return (paramDouble <= this.y0) ? 0.0D : ((paramDouble >= this.y1) ? 1.0D : ((paramDouble - this.y0) / (this.y1 - this.y0))); }
  
  public double XforT(double paramDouble) { return this.x0 + paramDouble * (this.x1 - this.x0); }
  
  public double YforT(double paramDouble) { return this.y0 + paramDouble * (this.y1 - this.y0); }
  
  public double dXforT(double paramDouble, int paramInt) {
    switch (paramInt) {
      case 0:
        return this.x0 + paramDouble * (this.x1 - this.x0);
      case 1:
        return this.x1 - this.x0;
    } 
    return 0.0D;
  }
  
  public double dYforT(double paramDouble, int paramInt) {
    switch (paramInt) {
      case 0:
        return this.y0 + paramDouble * (this.y1 - this.y0);
      case 1:
        return this.y1 - this.y0;
    } 
    return 0.0D;
  }
  
  public double nextVertical(double paramDouble1, double paramDouble2) { return paramDouble2; }
  
  public boolean accumulateCrossings(Crossings paramCrossings) {
    double d8;
    double d7;
    double d6;
    double d5;
    double d1 = paramCrossings.getXLo();
    double d2 = paramCrossings.getYLo();
    double d3 = paramCrossings.getXHi();
    double d4 = paramCrossings.getYHi();
    if (this.xmin >= d3)
      return false; 
    if (this.y0 < d2) {
      if (this.y1 <= d2)
        return false; 
      d6 = d2;
      d5 = XforY(d2);
    } else {
      if (this.y0 >= d4)
        return false; 
      d6 = this.y0;
      d5 = this.x0;
    } 
    if (this.y1 > d4) {
      d8 = d4;
      d7 = XforY(d4);
    } else {
      d8 = this.y1;
      d7 = this.x1;
    } 
    if (d5 >= d3 && d7 >= d3)
      return false; 
    if (d5 > d1 || d7 > d1)
      return true; 
    paramCrossings.record(d6, d8, this.direction);
    return false;
  }
  
  public void enlarge(Rectangle2D paramRectangle2D) {
    paramRectangle2D.add(this.x0, this.y0);
    paramRectangle2D.add(this.x1, this.y1);
  }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt) {
    if (paramDouble1 == this.y0 && paramDouble2 == this.y1)
      return getWithDirection(paramInt); 
    if (this.x0 == this.x1)
      return new Order1(this.x0, paramDouble1, this.x1, paramDouble2, paramInt); 
    double d1 = this.x0 - this.x1;
    double d2 = this.y0 - this.y1;
    double d3 = this.x0 + (paramDouble1 - this.y0) * d1 / d2;
    double d4 = this.x0 + (paramDouble2 - this.y0) * d1 / d2;
    return new Order1(d3, paramDouble1, d4, paramDouble2, paramInt);
  }
  
  public Curve getReversedCurve() { return new Order1(this.x0, this.y0, this.x1, this.y1, -this.direction); }
  
  public int compareTo(Curve paramCurve, double[] paramArrayOfDouble) {
    double d6;
    if (!(paramCurve instanceof Order1))
      return super.compareTo(paramCurve, paramArrayOfDouble); 
    Order1 order1 = (Order1)paramCurve;
    if (paramArrayOfDouble[1] <= paramArrayOfDouble[0])
      throw new InternalError("yrange already screwed up..."); 
    paramArrayOfDouble[1] = Math.min(Math.min(paramArrayOfDouble[1], this.y1), order1.y1);
    if (paramArrayOfDouble[1] <= paramArrayOfDouble[0])
      throw new InternalError("backstepping from " + paramArrayOfDouble[0] + " to " + paramArrayOfDouble[1]); 
    if (this.xmax <= order1.xmin)
      return (this.xmin == order1.xmax) ? 0 : -1; 
    if (this.xmin >= order1.xmax)
      return 1; 
    double d1 = this.x1 - this.x0;
    double d2 = this.y1 - this.y0;
    double d3 = order1.x1 - order1.x0;
    double d4 = order1.y1 - order1.y0;
    double d5 = d3 * d2 - d1 * d4;
    if (d5 != 0.0D) {
      double d = (this.x0 - order1.x0) * d2 * d4 - this.y0 * d1 * d4 + order1.y0 * d3 * d2;
      d6 = d / d5;
      if (d6 <= paramArrayOfDouble[0]) {
        d6 = Math.min(this.y1, order1.y1);
      } else {
        if (d6 < paramArrayOfDouble[1])
          paramArrayOfDouble[1] = d6; 
        d6 = Math.max(this.y0, order1.y0);
      } 
    } else {
      d6 = Math.max(this.y0, order1.y0);
    } 
    return orderof(XforY(d6), order1.XforY(d6));
  }
  
  public int getSegment(double[] paramArrayOfDouble) {
    if (this.direction == 1) {
      paramArrayOfDouble[0] = this.x1;
      paramArrayOfDouble[1] = this.y1;
    } else {
      paramArrayOfDouble[0] = this.x0;
      paramArrayOfDouble[1] = this.y0;
    } 
    return 1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\Order1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */