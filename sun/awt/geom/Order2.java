package sun.awt.geom;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

final class Order2 extends Curve {
  private double x0;
  
  private double y0;
  
  private double cx0;
  
  private double cy0;
  
  private double x1;
  
  private double y1;
  
  private double xmin;
  
  private double xmax;
  
  private double xcoeff0;
  
  private double xcoeff1;
  
  private double xcoeff2;
  
  private double ycoeff0;
  
  private double ycoeff1;
  
  private double ycoeff2;
  
  public static void insert(Vector paramVector, double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt) {
    int i = getHorizontalParams(paramDouble2, paramDouble4, paramDouble6, paramArrayOfDouble);
    if (i == 0) {
      addInstance(paramVector, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramInt);
      return;
    } 
    double d = paramArrayOfDouble[0];
    paramArrayOfDouble[0] = paramDouble1;
    paramArrayOfDouble[1] = paramDouble2;
    paramArrayOfDouble[2] = paramDouble3;
    paramArrayOfDouble[3] = paramDouble4;
    paramArrayOfDouble[4] = paramDouble5;
    paramArrayOfDouble[5] = paramDouble6;
    split(paramArrayOfDouble, 0, d);
    boolean bool = (paramInt == 1) ? 0 : 4;
    byte b = 4 - bool;
    addInstance(paramVector, paramArrayOfDouble[bool], paramArrayOfDouble[bool + true], paramArrayOfDouble[bool + 2], paramArrayOfDouble[bool + 3], paramArrayOfDouble[bool + 4], paramArrayOfDouble[bool + 5], paramInt);
    addInstance(paramVector, paramArrayOfDouble[b], paramArrayOfDouble[b + 1], paramArrayOfDouble[b + 2], paramArrayOfDouble[b + 3], paramArrayOfDouble[b + 4], paramArrayOfDouble[b + 5], paramInt);
  }
  
  public static void addInstance(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt) {
    if (paramDouble2 > paramDouble6) {
      paramVector.add(new Order2(paramDouble5, paramDouble6, paramDouble3, paramDouble4, paramDouble1, paramDouble2, -paramInt));
    } else if (paramDouble6 > paramDouble2) {
      paramVector.add(new Order2(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramInt));
    } 
  }
  
  public static int getHorizontalParams(double paramDouble1, double paramDouble2, double paramDouble3, double[] paramArrayOfDouble) {
    if (paramDouble1 <= paramDouble2 && paramDouble2 <= paramDouble3)
      return 0; 
    paramDouble1 -= paramDouble2;
    paramDouble3 -= paramDouble2;
    double d1 = paramDouble1 + paramDouble3;
    if (d1 == 0.0D)
      return 0; 
    double d2 = paramDouble1 / d1;
    if (d2 <= 0.0D || d2 >= 1.0D)
      return 0; 
    paramArrayOfDouble[0] = d2;
    return 1;
  }
  
  public static void split(double[] paramArrayOfDouble, int paramInt, double paramDouble) {
    double d5 = paramArrayOfDouble[paramInt + 4];
    paramArrayOfDouble[paramInt + 8] = d5;
    double d6 = paramArrayOfDouble[paramInt + 5];
    paramArrayOfDouble[paramInt + 9] = d6;
    double d3 = paramArrayOfDouble[paramInt + 2];
    double d4 = paramArrayOfDouble[paramInt + 3];
    d5 = d3 + (d5 - d3) * paramDouble;
    d6 = d4 + (d6 - d4) * paramDouble;
    double d1 = paramArrayOfDouble[paramInt + 0];
    double d2 = paramArrayOfDouble[paramInt + 1];
    d1 += (d3 - d1) * paramDouble;
    d2 += (d4 - d2) * paramDouble;
    d3 = d1 + (d5 - d1) * paramDouble;
    d4 = d2 + (d6 - d2) * paramDouble;
    paramArrayOfDouble[paramInt + 2] = d1;
    paramArrayOfDouble[paramInt + 3] = d2;
    paramArrayOfDouble[paramInt + 4] = d3;
    paramArrayOfDouble[paramInt + 5] = d4;
    paramArrayOfDouble[paramInt + 6] = d5;
    paramArrayOfDouble[paramInt + 7] = d6;
  }
  
  public Order2(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, int paramInt) {
    super(paramInt);
    if (paramDouble4 < paramDouble2) {
      paramDouble4 = paramDouble2;
    } else if (paramDouble4 > paramDouble6) {
      paramDouble4 = paramDouble6;
    } 
    this.x0 = paramDouble1;
    this.y0 = paramDouble2;
    this.cx0 = paramDouble3;
    this.cy0 = paramDouble4;
    this.x1 = paramDouble5;
    this.y1 = paramDouble6;
    this.xmin = Math.min(Math.min(paramDouble1, paramDouble5), paramDouble3);
    this.xmax = Math.max(Math.max(paramDouble1, paramDouble5), paramDouble3);
    this.xcoeff0 = paramDouble1;
    this.xcoeff1 = paramDouble3 + paramDouble3 - paramDouble1 - paramDouble1;
    this.xcoeff2 = paramDouble1 - paramDouble3 - paramDouble3 + paramDouble5;
    this.ycoeff0 = paramDouble2;
    this.ycoeff1 = paramDouble4 + paramDouble4 - paramDouble2 - paramDouble2;
    this.ycoeff2 = paramDouble2 - paramDouble4 - paramDouble4 + paramDouble6;
  }
  
  public int getOrder() { return 2; }
  
  public double getXTop() { return this.x0; }
  
  public double getYTop() { return this.y0; }
  
  public double getXBot() { return this.x1; }
  
  public double getYBot() { return this.y1; }
  
  public double getXMin() { return this.xmin; }
  
  public double getXMax() { return this.xmax; }
  
  public double getX0() { return (this.direction == 1) ? this.x0 : this.x1; }
  
  public double getY0() { return (this.direction == 1) ? this.y0 : this.y1; }
  
  public double getCX0() { return this.cx0; }
  
  public double getCY0() { return this.cy0; }
  
  public double getX1() { return (this.direction == -1) ? this.x0 : this.x1; }
  
  public double getY1() { return (this.direction == -1) ? this.y0 : this.y1; }
  
  public double XforY(double paramDouble) { return (paramDouble <= this.y0) ? this.x0 : ((paramDouble >= this.y1) ? this.x1 : XforT(TforY(paramDouble))); }
  
  public double TforY(double paramDouble) { return (paramDouble <= this.y0) ? 0.0D : ((paramDouble >= this.y1) ? 1.0D : TforY(paramDouble, this.ycoeff0, this.ycoeff1, this.ycoeff2)); }
  
  public static double TforY(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    paramDouble2 -= paramDouble1;
    if (paramDouble4 == 0.0D) {
      double d = -paramDouble2 / paramDouble3;
      if (d >= 0.0D && d <= 1.0D)
        return d; 
    } else {
      double d = paramDouble3 * paramDouble3 - 4.0D * paramDouble4 * paramDouble2;
      if (d >= 0.0D) {
        d = Math.sqrt(d);
        if (paramDouble3 < 0.0D)
          d = -d; 
        double d3 = (paramDouble3 + d) / -2.0D;
        double d4 = d3 / paramDouble4;
        if (d4 >= 0.0D && d4 <= 1.0D)
          return d4; 
        if (d3 != 0.0D) {
          d4 = paramDouble2 / d3;
          if (d4 >= 0.0D && d4 <= 1.0D)
            return d4; 
        } 
      } 
    } 
    double d1 = paramDouble2;
    double d2 = paramDouble2 + paramDouble3 + paramDouble4;
    return (0.0D < (d1 + d2) / 2.0D) ? 0.0D : 1.0D;
  }
  
  public double XforT(double paramDouble) { return (this.xcoeff2 * paramDouble + this.xcoeff1) * paramDouble + this.xcoeff0; }
  
  public double YforT(double paramDouble) { return (this.ycoeff2 * paramDouble + this.ycoeff1) * paramDouble + this.ycoeff0; }
  
  public double dXforT(double paramDouble, int paramInt) {
    switch (paramInt) {
      case 0:
        return (this.xcoeff2 * paramDouble + this.xcoeff1) * paramDouble + this.xcoeff0;
      case 1:
        return 2.0D * this.xcoeff2 * paramDouble + this.xcoeff1;
      case 2:
        return 2.0D * this.xcoeff2;
    } 
    return 0.0D;
  }
  
  public double dYforT(double paramDouble, int paramInt) {
    switch (paramInt) {
      case 0:
        return (this.ycoeff2 * paramDouble + this.ycoeff1) * paramDouble + this.ycoeff0;
      case 1:
        return 2.0D * this.ycoeff2 * paramDouble + this.ycoeff1;
      case 2:
        return 2.0D * this.ycoeff2;
    } 
    return 0.0D;
  }
  
  public double nextVertical(double paramDouble1, double paramDouble2) {
    double d = -this.xcoeff1 / 2.0D * this.xcoeff2;
    return (d > paramDouble1 && d < paramDouble2) ? d : paramDouble2;
  }
  
  public void enlarge(Rectangle2D paramRectangle2D) {
    paramRectangle2D.add(this.x0, this.y0);
    double d = -this.xcoeff1 / 2.0D * this.xcoeff2;
    if (d > 0.0D && d < 1.0D)
      paramRectangle2D.add(XforT(d), YforT(d)); 
    paramRectangle2D.add(this.x1, this.y1);
  }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt) {
    byte b;
    double d2;
    double d1;
    if (paramDouble1 <= this.y0) {
      if (paramDouble2 >= this.y1)
        return getWithDirection(paramInt); 
      d1 = 0.0D;
    } else {
      d1 = TforY(paramDouble1, this.ycoeff0, this.ycoeff1, this.ycoeff2);
    } 
    if (paramDouble2 >= this.y1) {
      d2 = 1.0D;
    } else {
      d2 = TforY(paramDouble2, this.ycoeff0, this.ycoeff1, this.ycoeff2);
    } 
    double[] arrayOfDouble = new double[10];
    arrayOfDouble[0] = this.x0;
    arrayOfDouble[1] = this.y0;
    arrayOfDouble[2] = this.cx0;
    arrayOfDouble[3] = this.cy0;
    arrayOfDouble[4] = this.x1;
    arrayOfDouble[5] = this.y1;
    if (d2 < 1.0D)
      split(arrayOfDouble, 0, d2); 
    if (d1 <= 0.0D) {
      b = 0;
    } else {
      split(arrayOfDouble, 0, d1 / d2);
      b = 4;
    } 
    return new Order2(arrayOfDouble[b + 0], paramDouble1, arrayOfDouble[b + 2], arrayOfDouble[b + 3], arrayOfDouble[b + 4], paramDouble2, paramInt);
  }
  
  public Curve getReversedCurve() { return new Order2(this.x0, this.y0, this.cx0, this.cy0, this.x1, this.y1, -this.direction); }
  
  public int getSegment(double[] paramArrayOfDouble) {
    paramArrayOfDouble[0] = this.cx0;
    paramArrayOfDouble[1] = this.cy0;
    if (this.direction == 1) {
      paramArrayOfDouble[2] = this.x1;
      paramArrayOfDouble[3] = this.y1;
    } else {
      paramArrayOfDouble[2] = this.x0;
      paramArrayOfDouble[3] = this.y0;
    } 
    return 2;
  }
  
  public String controlPointString() { return "(" + round(this.cx0) + ", " + round(this.cy0) + "), "; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\Order2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */