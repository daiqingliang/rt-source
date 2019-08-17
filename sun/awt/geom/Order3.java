package sun.awt.geom;

import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

final class Order3 extends Curve {
  private double x0;
  
  private double y0;
  
  private double cx0;
  
  private double cy0;
  
  private double cx1;
  
  private double cy1;
  
  private double x1;
  
  private double y1;
  
  private double xmin;
  
  private double xmax;
  
  private double xcoeff0;
  
  private double xcoeff1;
  
  private double xcoeff2;
  
  private double xcoeff3;
  
  private double ycoeff0;
  
  private double ycoeff1;
  
  private double ycoeff2;
  
  private double ycoeff3;
  
  private double TforY1;
  
  private double YforT1;
  
  private double TforY2;
  
  private double YforT2;
  
  private double TforY3;
  
  private double YforT3;
  
  public static void insert(Vector paramVector, double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, int paramInt) {
    int i = getHorizontalParams(paramDouble2, paramDouble4, paramDouble6, paramDouble8, paramArrayOfDouble);
    if (i == 0) {
      addInstance(paramVector, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramInt);
      return;
    } 
    paramArrayOfDouble[3] = paramDouble1;
    paramArrayOfDouble[4] = paramDouble2;
    paramArrayOfDouble[5] = paramDouble3;
    paramArrayOfDouble[6] = paramDouble4;
    paramArrayOfDouble[7] = paramDouble5;
    paramArrayOfDouble[8] = paramDouble6;
    paramArrayOfDouble[9] = paramDouble7;
    paramArrayOfDouble[10] = paramDouble8;
    double d = paramArrayOfDouble[0];
    if (i > 1 && d > paramArrayOfDouble[1]) {
      paramArrayOfDouble[0] = paramArrayOfDouble[1];
      paramArrayOfDouble[1] = d;
      d = paramArrayOfDouble[0];
    } 
    split(paramArrayOfDouble, 3, d);
    if (i > 1) {
      d = (paramArrayOfDouble[1] - d) / (1.0D - d);
      split(paramArrayOfDouble, 9, d);
    } 
    int j = 3;
    if (paramInt == -1)
      j += i * 6; 
    while (i >= 0) {
      addInstance(paramVector, paramArrayOfDouble[j + 0], paramArrayOfDouble[j + 1], paramArrayOfDouble[j + 2], paramArrayOfDouble[j + 3], paramArrayOfDouble[j + 4], paramArrayOfDouble[j + 5], paramArrayOfDouble[j + 6], paramArrayOfDouble[j + 7], paramInt);
      i--;
      if (paramInt == 1) {
        j += 6;
        continue;
      } 
      j -= 6;
    } 
  }
  
  public static void addInstance(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, int paramInt) {
    if (paramDouble2 > paramDouble8) {
      paramVector.add(new Order3(paramDouble7, paramDouble8, paramDouble5, paramDouble6, paramDouble3, paramDouble4, paramDouble1, paramDouble2, -paramInt));
    } else if (paramDouble8 > paramDouble2) {
      paramVector.add(new Order3(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramInt));
    } 
  }
  
  public static int getHorizontalParams(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double[] paramArrayOfDouble) {
    if (paramDouble1 <= paramDouble2 && paramDouble2 <= paramDouble3 && paramDouble3 <= paramDouble4)
      return 0; 
    paramDouble4 -= paramDouble3;
    paramDouble3 -= paramDouble2;
    paramDouble2 -= paramDouble1;
    paramArrayOfDouble[0] = paramDouble2;
    paramArrayOfDouble[1] = (paramDouble3 - paramDouble2) * 2.0D;
    paramArrayOfDouble[2] = paramDouble4 - paramDouble3 - paramDouble3 + paramDouble2;
    int i = QuadCurve2D.solveQuadratic(paramArrayOfDouble, paramArrayOfDouble);
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      double d = paramArrayOfDouble[b2];
      if (d > 0.0D && d < 1.0D) {
        if (b1 < b2)
          paramArrayOfDouble[b1] = d; 
        b1++;
      } 
    } 
    return b1;
  }
  
  public static void split(double[] paramArrayOfDouble, int paramInt, double paramDouble) {
    double d7 = paramArrayOfDouble[paramInt + 6];
    paramArrayOfDouble[paramInt + 12] = d7;
    double d8 = paramArrayOfDouble[paramInt + 7];
    paramArrayOfDouble[paramInt + 13] = d8;
    double d5 = paramArrayOfDouble[paramInt + 4];
    double d6 = paramArrayOfDouble[paramInt + 5];
    d7 = d5 + (d7 - d5) * paramDouble;
    d8 = d6 + (d8 - d6) * paramDouble;
    double d1 = paramArrayOfDouble[paramInt + 0];
    double d2 = paramArrayOfDouble[paramInt + 1];
    double d3 = paramArrayOfDouble[paramInt + 2];
    double d4 = paramArrayOfDouble[paramInt + 3];
    d1 += (d3 - d1) * paramDouble;
    d2 += (d4 - d2) * paramDouble;
    d3 += (d5 - d3) * paramDouble;
    d4 += (d6 - d4) * paramDouble;
    d5 = d3 + (d7 - d3) * paramDouble;
    d6 = d4 + (d8 - d4) * paramDouble;
    d3 = d1 + (d3 - d1) * paramDouble;
    d4 = d2 + (d4 - d2) * paramDouble;
    paramArrayOfDouble[paramInt + 2] = d1;
    paramArrayOfDouble[paramInt + 3] = d2;
    paramArrayOfDouble[paramInt + 4] = d3;
    paramArrayOfDouble[paramInt + 5] = d4;
    paramArrayOfDouble[paramInt + 6] = d3 + (d5 - d3) * paramDouble;
    paramArrayOfDouble[paramInt + 7] = d4 + (d6 - d4) * paramDouble;
    paramArrayOfDouble[paramInt + 8] = d5;
    paramArrayOfDouble[paramInt + 9] = d6;
    paramArrayOfDouble[paramInt + 10] = d7;
    paramArrayOfDouble[paramInt + 11] = d8;
  }
  
  public Order3(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, int paramInt) {
    super(paramInt);
    if (paramDouble4 < paramDouble2)
      paramDouble4 = paramDouble2; 
    if (paramDouble6 > paramDouble8)
      paramDouble6 = paramDouble8; 
    this.x0 = paramDouble1;
    this.y0 = paramDouble2;
    this.cx0 = paramDouble3;
    this.cy0 = paramDouble4;
    this.cx1 = paramDouble5;
    this.cy1 = paramDouble6;
    this.x1 = paramDouble7;
    this.y1 = paramDouble8;
    this.xmin = Math.min(Math.min(paramDouble1, paramDouble7), Math.min(paramDouble3, paramDouble5));
    this.xmax = Math.max(Math.max(paramDouble1, paramDouble7), Math.max(paramDouble3, paramDouble5));
    this.xcoeff0 = paramDouble1;
    this.xcoeff1 = (paramDouble3 - paramDouble1) * 3.0D;
    this.xcoeff2 = (paramDouble5 - paramDouble3 - paramDouble3 + paramDouble1) * 3.0D;
    this.xcoeff3 = paramDouble7 - (paramDouble5 - paramDouble3) * 3.0D - paramDouble1;
    this.ycoeff0 = paramDouble2;
    this.ycoeff1 = (paramDouble4 - paramDouble2) * 3.0D;
    this.ycoeff2 = (paramDouble6 - paramDouble4 - paramDouble4 + paramDouble2) * 3.0D;
    this.ycoeff3 = paramDouble8 - (paramDouble6 - paramDouble4) * 3.0D - paramDouble2;
    this.YforT1 = this.YforT2 = this.YforT3 = paramDouble2;
  }
  
  public int getOrder() { return 3; }
  
  public double getXTop() { return this.x0; }
  
  public double getYTop() { return this.y0; }
  
  public double getXBot() { return this.x1; }
  
  public double getYBot() { return this.y1; }
  
  public double getXMin() { return this.xmin; }
  
  public double getXMax() { return this.xmax; }
  
  public double getX0() { return (this.direction == 1) ? this.x0 : this.x1; }
  
  public double getY0() { return (this.direction == 1) ? this.y0 : this.y1; }
  
  public double getCX0() { return (this.direction == 1) ? this.cx0 : this.cx1; }
  
  public double getCY0() { return (this.direction == 1) ? this.cy0 : this.cy1; }
  
  public double getCX1() { return (this.direction == -1) ? this.cx0 : this.cx1; }
  
  public double getCY1() { return (this.direction == -1) ? this.cy0 : this.cy1; }
  
  public double getX1() { return (this.direction == -1) ? this.x0 : this.x1; }
  
  public double getY1() { return (this.direction == -1) ? this.y0 : this.y1; }
  
  public double TforY(double paramDouble) {
    double d9;
    if (paramDouble <= this.y0)
      return 0.0D; 
    if (paramDouble >= this.y1)
      return 1.0D; 
    if (paramDouble == this.YforT1)
      return this.TforY1; 
    if (paramDouble == this.YforT2)
      return this.TforY2; 
    if (paramDouble == this.YforT3)
      return this.TforY3; 
    if (this.ycoeff3 == 0.0D)
      return Order2.TforY(paramDouble, this.ycoeff0, this.ycoeff1, this.ycoeff2); 
    double d1 = this.ycoeff2 / this.ycoeff3;
    double d2 = this.ycoeff1 / this.ycoeff3;
    double d3 = (this.ycoeff0 - paramDouble) / this.ycoeff3;
    boolean bool = false;
    double d4 = (d1 * d1 - 3.0D * d2) / 9.0D;
    double d5 = (2.0D * d1 * d1 * d1 - 9.0D * d1 * d2 + 27.0D * d3) / 54.0D;
    double d6 = d5 * d5;
    double d7 = d4 * d4 * d4;
    double d8 = d1 / 3.0D;
    if (d6 < d7) {
      double d = Math.acos(d5 / Math.sqrt(d7));
      d4 = -2.0D * Math.sqrt(d4);
      d9 = refine(d1, d2, d3, paramDouble, d4 * Math.cos(d / 3.0D) - d8);
      if (d9 < 0.0D)
        d9 = refine(d1, d2, d3, paramDouble, d4 * Math.cos((d + 6.283185307179586D) / 3.0D) - d8); 
      if (d9 < 0.0D)
        d9 = refine(d1, d2, d3, paramDouble, d4 * Math.cos((d - 6.283185307179586D) / 3.0D) - d8); 
    } else {
      boolean bool1 = (d5 < 0.0D) ? 1 : 0;
      double d10 = Math.sqrt(d6 - d7);
      if (bool1)
        d5 = -d5; 
      double d11 = Math.pow(d5 + d10, 0.3333333333333333D);
      if (!bool1)
        d11 = -d11; 
      double d12 = (d11 == 0.0D) ? 0.0D : (d4 / d11);
      d9 = refine(d1, d2, d3, paramDouble, d11 + d12 - d8);
    } 
    if (d9 < 0.0D) {
      double d10 = 0.0D;
      double d11 = 1.0D;
      while (true) {
        d9 = (d10 + d11) / 2.0D;
        if (d9 == d10 || d9 == d11)
          break; 
        double d = YforT(d9);
        if (d < paramDouble) {
          d10 = d9;
          continue;
        } 
        if (d > paramDouble) {
          d11 = d9;
          continue;
        } 
        break;
      } 
    } 
    if (d9 >= 0.0D) {
      this.TforY3 = this.TforY2;
      this.YforT3 = this.YforT2;
      this.TforY2 = this.TforY1;
      this.YforT2 = this.YforT1;
      this.TforY1 = d9;
      this.YforT1 = paramDouble;
    } 
    return d9;
  }
  
  public double refine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5) {
    double d3;
    double d2;
    if (paramDouble5 < -0.1D || paramDouble5 > 1.1D)
      return -1.0D; 
    double d1 = YforT(paramDouble5);
    if (d1 < paramDouble4) {
      d2 = paramDouble5;
      d3 = 1.0D;
    } else {
      d2 = 0.0D;
      d3 = paramDouble5;
    } 
    double d4 = paramDouble5;
    double d5 = d1;
    boolean bool1 = true;
    while (d1 != paramDouble4) {
      if (!bool1) {
        double d = (d2 + d3) / 2.0D;
        if (d == d2 || d == d3)
          break; 
        paramDouble5 = d;
      } else {
        double d6 = dYforT(paramDouble5, 1);
        if (d6 == 0.0D) {
          bool1 = false;
          continue;
        } 
        double d7 = paramDouble5 + (paramDouble4 - d1) / d6;
        if (d7 == paramDouble5 || d7 <= d2 || d7 >= d3) {
          bool1 = false;
          continue;
        } 
        paramDouble5 = d7;
      } 
      d1 = YforT(paramDouble5);
      if (d1 < paramDouble4) {
        d2 = paramDouble5;
        continue;
      } 
      if (d1 > paramDouble4)
        d3 = paramDouble5; 
    } 
    boolean bool2 = false;
    return (paramDouble5 > 1.0D) ? -1.0D : paramDouble5;
  }
  
  public double XforY(double paramDouble) { return (paramDouble <= this.y0) ? this.x0 : ((paramDouble >= this.y1) ? this.x1 : XforT(TforY(paramDouble))); }
  
  public double XforT(double paramDouble) { return ((this.xcoeff3 * paramDouble + this.xcoeff2) * paramDouble + this.xcoeff1) * paramDouble + this.xcoeff0; }
  
  public double YforT(double paramDouble) { return ((this.ycoeff3 * paramDouble + this.ycoeff2) * paramDouble + this.ycoeff1) * paramDouble + this.ycoeff0; }
  
  public double dXforT(double paramDouble, int paramInt) {
    switch (paramInt) {
      case 0:
        return ((this.xcoeff3 * paramDouble + this.xcoeff2) * paramDouble + this.xcoeff1) * paramDouble + this.xcoeff0;
      case 1:
        return (3.0D * this.xcoeff3 * paramDouble + 2.0D * this.xcoeff2) * paramDouble + this.xcoeff1;
      case 2:
        return 6.0D * this.xcoeff3 * paramDouble + 2.0D * this.xcoeff2;
      case 3:
        return 6.0D * this.xcoeff3;
    } 
    return 0.0D;
  }
  
  public double dYforT(double paramDouble, int paramInt) {
    switch (paramInt) {
      case 0:
        return ((this.ycoeff3 * paramDouble + this.ycoeff2) * paramDouble + this.ycoeff1) * paramDouble + this.ycoeff0;
      case 1:
        return (3.0D * this.ycoeff3 * paramDouble + 2.0D * this.ycoeff2) * paramDouble + this.ycoeff1;
      case 2:
        return 6.0D * this.ycoeff3 * paramDouble + 2.0D * this.ycoeff2;
      case 3:
        return 6.0D * this.ycoeff3;
    } 
    return 0.0D;
  }
  
  public double nextVertical(double paramDouble1, double paramDouble2) {
    double[] arrayOfDouble = { this.xcoeff1, 2.0D * this.xcoeff2, 3.0D * this.xcoeff3 };
    int i = QuadCurve2D.solveQuadratic(arrayOfDouble, arrayOfDouble);
    for (byte b = 0; b < i; b++) {
      if (arrayOfDouble[b] > paramDouble1 && arrayOfDouble[b] < paramDouble2)
        paramDouble2 = arrayOfDouble[b]; 
    } 
    return paramDouble2;
  }
  
  public void enlarge(Rectangle2D paramRectangle2D) {
    paramRectangle2D.add(this.x0, this.y0);
    double[] arrayOfDouble = { this.xcoeff1, 2.0D * this.xcoeff2, 3.0D * this.xcoeff3 };
    int i = QuadCurve2D.solveQuadratic(arrayOfDouble, arrayOfDouble);
    for (byte b = 0; b < i; b++) {
      double d = arrayOfDouble[b];
      if (d > 0.0D && d < 1.0D)
        paramRectangle2D.add(XforT(d), YforT(d)); 
    } 
    paramRectangle2D.add(this.x1, this.y1);
  }
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt) {
    byte b;
    if (paramDouble1 <= this.y0 && paramDouble2 >= this.y1)
      return getWithDirection(paramInt); 
    double[] arrayOfDouble = new double[14];
    double d1 = TforY(paramDouble1);
    double d2 = TforY(paramDouble2);
    arrayOfDouble[0] = this.x0;
    arrayOfDouble[1] = this.y0;
    arrayOfDouble[2] = this.cx0;
    arrayOfDouble[3] = this.cy0;
    arrayOfDouble[4] = this.cx1;
    arrayOfDouble[5] = this.cy1;
    arrayOfDouble[6] = this.x1;
    arrayOfDouble[7] = this.y1;
    if (d1 > d2) {
      b = d1;
      d1 = d2;
      d2 = b;
    } 
    if (d2 < 1.0D)
      split(arrayOfDouble, 0, d2); 
    if (d1 <= 0.0D) {
      b = 0;
    } else {
      split(arrayOfDouble, 0, d1 / d2);
      b = 6;
    } 
    return new Order3(arrayOfDouble[b + 0], paramDouble1, arrayOfDouble[b + 2], arrayOfDouble[b + 3], arrayOfDouble[b + 4], arrayOfDouble[b + 5], arrayOfDouble[b + 6], paramDouble2, paramInt);
  }
  
  public Curve getReversedCurve() { return new Order3(this.x0, this.y0, this.cx0, this.cy0, this.cx1, this.cy1, this.x1, this.y1, -this.direction); }
  
  public int getSegment(double[] paramArrayOfDouble) {
    if (this.direction == 1) {
      paramArrayOfDouble[0] = this.cx0;
      paramArrayOfDouble[1] = this.cy0;
      paramArrayOfDouble[2] = this.cx1;
      paramArrayOfDouble[3] = this.cy1;
      paramArrayOfDouble[4] = this.x1;
      paramArrayOfDouble[5] = this.y1;
    } else {
      paramArrayOfDouble[0] = this.cx1;
      paramArrayOfDouble[1] = this.cy1;
      paramArrayOfDouble[2] = this.cx0;
      paramArrayOfDouble[3] = this.cy0;
      paramArrayOfDouble[4] = this.x0;
      paramArrayOfDouble[5] = this.y0;
    } 
    return 3;
  }
  
  public String controlPointString() { return "(" + round(getCX0()) + ", " + round(getCY0()) + "), " + "(" + round(getCX1()) + ", " + round(getCY1()) + "), "; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\Order3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */