package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Serializable;
import java.util.Arrays;
import sun.awt.geom.Curve;

public abstract class CubicCurve2D implements Shape, Cloneable {
  public abstract double getX1();
  
  public abstract double getY1();
  
  public abstract Point2D getP1();
  
  public abstract double getCtrlX1();
  
  public abstract double getCtrlY1();
  
  public abstract Point2D getCtrlP1();
  
  public abstract double getCtrlX2();
  
  public abstract double getCtrlY2();
  
  public abstract Point2D getCtrlP2();
  
  public abstract double getX2();
  
  public abstract double getY2();
  
  public abstract Point2D getP2();
  
  public abstract void setCurve(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8);
  
  public void setCurve(double[] paramArrayOfDouble, int paramInt) { setCurve(paramArrayOfDouble[paramInt + 0], paramArrayOfDouble[paramInt + 1], paramArrayOfDouble[paramInt + 2], paramArrayOfDouble[paramInt + 3], paramArrayOfDouble[paramInt + 4], paramArrayOfDouble[paramInt + 5], paramArrayOfDouble[paramInt + 6], paramArrayOfDouble[paramInt + 7]); }
  
  public void setCurve(Point2D paramPoint2D1, Point2D paramPoint2D2, Point2D paramPoint2D3, Point2D paramPoint2D4) { setCurve(paramPoint2D1.getX(), paramPoint2D1.getY(), paramPoint2D2.getX(), paramPoint2D2.getY(), paramPoint2D3.getX(), paramPoint2D3.getY(), paramPoint2D4.getX(), paramPoint2D4.getY()); }
  
  public void setCurve(Point2D[] paramArrayOfPoint2D, int paramInt) { setCurve(paramArrayOfPoint2D[paramInt + 0].getX(), paramArrayOfPoint2D[paramInt + 0].getY(), paramArrayOfPoint2D[paramInt + 1].getX(), paramArrayOfPoint2D[paramInt + 1].getY(), paramArrayOfPoint2D[paramInt + 2].getX(), paramArrayOfPoint2D[paramInt + 2].getY(), paramArrayOfPoint2D[paramInt + 3].getX(), paramArrayOfPoint2D[paramInt + 3].getY()); }
  
  public void setCurve(CubicCurve2D paramCubicCurve2D) { setCurve(paramCubicCurve2D.getX1(), paramCubicCurve2D.getY1(), paramCubicCurve2D.getCtrlX1(), paramCubicCurve2D.getCtrlY1(), paramCubicCurve2D.getCtrlX2(), paramCubicCurve2D.getCtrlY2(), paramCubicCurve2D.getX2(), paramCubicCurve2D.getY2()); }
  
  public static double getFlatnessSq(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8) { return Math.max(Line2D.ptSegDistSq(paramDouble1, paramDouble2, paramDouble7, paramDouble8, paramDouble3, paramDouble4), Line2D.ptSegDistSq(paramDouble1, paramDouble2, paramDouble7, paramDouble8, paramDouble5, paramDouble6)); }
  
  public static double getFlatness(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8) { return Math.sqrt(getFlatnessSq(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8)); }
  
  public static double getFlatnessSq(double[] paramArrayOfDouble, int paramInt) { return getFlatnessSq(paramArrayOfDouble[paramInt + 0], paramArrayOfDouble[paramInt + 1], paramArrayOfDouble[paramInt + 2], paramArrayOfDouble[paramInt + 3], paramArrayOfDouble[paramInt + 4], paramArrayOfDouble[paramInt + 5], paramArrayOfDouble[paramInt + 6], paramArrayOfDouble[paramInt + 7]); }
  
  public static double getFlatness(double[] paramArrayOfDouble, int paramInt) { return getFlatness(paramArrayOfDouble[paramInt + 0], paramArrayOfDouble[paramInt + 1], paramArrayOfDouble[paramInt + 2], paramArrayOfDouble[paramInt + 3], paramArrayOfDouble[paramInt + 4], paramArrayOfDouble[paramInt + 5], paramArrayOfDouble[paramInt + 6], paramArrayOfDouble[paramInt + 7]); }
  
  public double getFlatnessSq() { return getFlatnessSq(getX1(), getY1(), getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), getX2(), getY2()); }
  
  public double getFlatness() { return getFlatness(getX1(), getY1(), getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), getX2(), getY2()); }
  
  public void subdivide(CubicCurve2D paramCubicCurve2D1, CubicCurve2D paramCubicCurve2D2) { subdivide(this, paramCubicCurve2D1, paramCubicCurve2D2); }
  
  public static void subdivide(CubicCurve2D paramCubicCurve2D1, CubicCurve2D paramCubicCurve2D2, CubicCurve2D paramCubicCurve2D3) {
    double d1 = paramCubicCurve2D1.getX1();
    double d2 = paramCubicCurve2D1.getY1();
    double d3 = paramCubicCurve2D1.getCtrlX1();
    double d4 = paramCubicCurve2D1.getCtrlY1();
    double d5 = paramCubicCurve2D1.getCtrlX2();
    double d6 = paramCubicCurve2D1.getCtrlY2();
    double d7 = paramCubicCurve2D1.getX2();
    double d8 = paramCubicCurve2D1.getY2();
    double d9 = (d3 + d5) / 2.0D;
    double d10 = (d4 + d6) / 2.0D;
    d3 = (d1 + d3) / 2.0D;
    d4 = (d2 + d4) / 2.0D;
    d5 = (d7 + d5) / 2.0D;
    d6 = (d8 + d6) / 2.0D;
    double d11 = (d3 + d9) / 2.0D;
    double d12 = (d4 + d10) / 2.0D;
    double d13 = (d5 + d9) / 2.0D;
    double d14 = (d6 + d10) / 2.0D;
    d9 = (d11 + d13) / 2.0D;
    d10 = (d12 + d14) / 2.0D;
    if (paramCubicCurve2D2 != null)
      paramCubicCurve2D2.setCurve(d1, d2, d3, d4, d11, d12, d9, d10); 
    if (paramCubicCurve2D3 != null)
      paramCubicCurve2D3.setCurve(d9, d10, d13, d14, d5, d6, d7, d8); 
  }
  
  public static void subdivide(double[] paramArrayOfDouble1, int paramInt1, double[] paramArrayOfDouble2, int paramInt2, double[] paramArrayOfDouble3, int paramInt3) {
    double d1 = paramArrayOfDouble1[paramInt1 + 0];
    double d2 = paramArrayOfDouble1[paramInt1 + 1];
    double d3 = paramArrayOfDouble1[paramInt1 + 2];
    double d4 = paramArrayOfDouble1[paramInt1 + 3];
    double d5 = paramArrayOfDouble1[paramInt1 + 4];
    double d6 = paramArrayOfDouble1[paramInt1 + 5];
    double d7 = paramArrayOfDouble1[paramInt1 + 6];
    double d8 = paramArrayOfDouble1[paramInt1 + 7];
    if (paramArrayOfDouble2 != null) {
      paramArrayOfDouble2[paramInt2 + 0] = d1;
      paramArrayOfDouble2[paramInt2 + 1] = d2;
    } 
    if (paramArrayOfDouble3 != null) {
      paramArrayOfDouble3[paramInt3 + 6] = d7;
      paramArrayOfDouble3[paramInt3 + 7] = d8;
    } 
    d1 = (d1 + d3) / 2.0D;
    d2 = (d2 + d4) / 2.0D;
    d7 = (d7 + d5) / 2.0D;
    d8 = (d8 + d6) / 2.0D;
    double d9 = (d3 + d5) / 2.0D;
    double d10 = (d4 + d6) / 2.0D;
    d3 = (d1 + d9) / 2.0D;
    d4 = (d2 + d10) / 2.0D;
    d5 = (d7 + d9) / 2.0D;
    d6 = (d8 + d10) / 2.0D;
    d9 = (d3 + d5) / 2.0D;
    d10 = (d4 + d6) / 2.0D;
    if (paramArrayOfDouble2 != null) {
      paramArrayOfDouble2[paramInt2 + 2] = d1;
      paramArrayOfDouble2[paramInt2 + 3] = d2;
      paramArrayOfDouble2[paramInt2 + 4] = d3;
      paramArrayOfDouble2[paramInt2 + 5] = d4;
      paramArrayOfDouble2[paramInt2 + 6] = d9;
      paramArrayOfDouble2[paramInt2 + 7] = d10;
    } 
    if (paramArrayOfDouble3 != null) {
      paramArrayOfDouble3[paramInt3 + 0] = d9;
      paramArrayOfDouble3[paramInt3 + 1] = d10;
      paramArrayOfDouble3[paramInt3 + 2] = d5;
      paramArrayOfDouble3[paramInt3 + 3] = d6;
      paramArrayOfDouble3[paramInt3 + 4] = d7;
      paramArrayOfDouble3[paramInt3 + 5] = d8;
    } 
  }
  
  public static int solveCubic(double[] paramArrayOfDouble) { return solveCubic(paramArrayOfDouble, paramArrayOfDouble); }
  
  public static int solveCubic(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2) {
    int i;
    double d1 = paramArrayOfDouble1[3];
    if (d1 == 0.0D)
      return QuadCurve2D.solveQuadratic(paramArrayOfDouble1, paramArrayOfDouble2); 
    double d2 = paramArrayOfDouble1[2] / d1;
    double d3 = paramArrayOfDouble1[1] / d1;
    double d4 = paramArrayOfDouble1[0] / d1;
    double d5 = d2 * d2;
    double d6 = 0.3333333333333333D * (-0.3333333333333333D * d5 + d3);
    double d7 = 0.5D * (0.07407407407407407D * d2 * d5 - 0.3333333333333333D * d2 * d3 + d4);
    double d8 = d6 * d6 * d6;
    double d9 = d7 * d7 + d8;
    double d10 = 0.3333333333333333D * d2;
    if (d9 < 0.0D) {
      double d11 = 0.3333333333333333D * Math.acos(-d7 / Math.sqrt(-d8));
      double d12 = 2.0D * Math.sqrt(-d6);
      if (paramArrayOfDouble2 == paramArrayOfDouble1)
        paramArrayOfDouble1 = Arrays.copyOf(paramArrayOfDouble1, 4); 
      paramArrayOfDouble2[0] = d12 * Math.cos(d11);
      paramArrayOfDouble2[1] = -d12 * Math.cos(d11 + 1.0471975511965976D);
      paramArrayOfDouble2[2] = -d12 * Math.cos(d11 - 1.0471975511965976D);
      i = 3;
      for (byte b = 0; b < i; b++)
        paramArrayOfDouble2[b] = paramArrayOfDouble2[b] - d10; 
    } else {
      double d11 = Math.sqrt(d9);
      double d12 = Math.cbrt(d11 - d7);
      double d13 = -Math.cbrt(d11 + d7);
      double d14 = d12 + d13;
      i = 1;
      double d15 = 1.2E9D * Math.ulp(Math.abs(d14) + Math.abs(d10));
      if (iszero(d9, d15) || within(d12, d13, d15)) {
        if (paramArrayOfDouble2 == paramArrayOfDouble1)
          paramArrayOfDouble1 = Arrays.copyOf(paramArrayOfDouble1, 4); 
        paramArrayOfDouble2[1] = -(d14 / 2.0D) - d10;
        i = 2;
      } 
      paramArrayOfDouble2[0] = d14 - d10;
    } 
    if (i > 1)
      i = fixRoots(paramArrayOfDouble1, paramArrayOfDouble2, i); 
    if (i > 2 && (paramArrayOfDouble2[2] == paramArrayOfDouble2[1] || paramArrayOfDouble2[2] == paramArrayOfDouble2[0]))
      i--; 
    if (i > 1 && paramArrayOfDouble2[1] == paramArrayOfDouble2[0])
      paramArrayOfDouble2[1] = paramArrayOfDouble2[--i]; 
    return i;
  }
  
  private static int fixRoots(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt) {
    double[] arrayOfDouble = { paramArrayOfDouble1[1], 2.0D * paramArrayOfDouble1[2], 3.0D * paramArrayOfDouble1[3] };
    int i = QuadCurve2D.solveQuadratic(arrayOfDouble, arrayOfDouble);
    if (i == 2 && arrayOfDouble[0] == arrayOfDouble[1])
      i--; 
    if (i == 2 && arrayOfDouble[0] > arrayOfDouble[1]) {
      double d = arrayOfDouble[0];
      arrayOfDouble[0] = arrayOfDouble[1];
      arrayOfDouble[1] = d;
    } 
    if (paramInt == 3) {
      double d1 = getRootUpperBound(paramArrayOfDouble1);
      double d2 = -d1;
      Arrays.sort(paramArrayOfDouble2, 0, paramInt);
      if (i == 2) {
        paramArrayOfDouble2[0] = refineRootWithHint(paramArrayOfDouble1, d2, arrayOfDouble[0], paramArrayOfDouble2[0]);
        paramArrayOfDouble2[1] = refineRootWithHint(paramArrayOfDouble1, arrayOfDouble[0], arrayOfDouble[1], paramArrayOfDouble2[1]);
        paramArrayOfDouble2[2] = refineRootWithHint(paramArrayOfDouble1, arrayOfDouble[1], d1, paramArrayOfDouble2[2]);
        return 3;
      } 
      if (i == 1) {
        double d3 = paramArrayOfDouble1[3];
        double d4 = -d3;
        double d5 = arrayOfDouble[0];
        double d6 = solveEqn(paramArrayOfDouble1, 3, d5);
        if (oppositeSigns(d4, d6)) {
          paramArrayOfDouble2[0] = bisectRootWithHint(paramArrayOfDouble1, d2, d5, paramArrayOfDouble2[0]);
        } else if (oppositeSigns(d6, d3)) {
          paramArrayOfDouble2[0] = bisectRootWithHint(paramArrayOfDouble1, d5, d1, paramArrayOfDouble2[2]);
        } else {
          paramArrayOfDouble2[0] = d5;
        } 
      } else if (i == 0) {
        paramArrayOfDouble2[0] = bisectRootWithHint(paramArrayOfDouble1, d2, d1, paramArrayOfDouble2[1]);
      } 
    } else if (paramInt == 2 && i == 2) {
      double d1 = paramArrayOfDouble2[0];
      double d2 = paramArrayOfDouble2[1];
      double d3 = arrayOfDouble[0];
      double d4 = arrayOfDouble[1];
      double d5 = (Math.abs(d3 - d1) > Math.abs(d4 - d1)) ? d3 : d4;
      double d6 = solveEqn(paramArrayOfDouble1, 3, d5);
      if (iszero(d6, 1.0E7D * Math.ulp(d5))) {
        double d = solveEqn(paramArrayOfDouble1, 3, d2);
        paramArrayOfDouble2[1] = (Math.abs(d) < Math.abs(d6)) ? d2 : d5;
        return 2;
      } 
    } 
    return 1;
  }
  
  private static double refineRootWithHint(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3) {
    if (!inInterval(paramDouble3, paramDouble1, paramDouble2))
      return paramDouble3; 
    double[] arrayOfDouble = { paramArrayOfDouble[1], 2.0D * paramArrayOfDouble[2], 3.0D * paramArrayOfDouble[3] };
    double d = paramDouble3;
    for (byte b = 0; b < 3; b++) {
      double d1 = solveEqn(arrayOfDouble, 2, paramDouble3);
      double d2 = solveEqn(paramArrayOfDouble, 3, paramDouble3);
      double d3 = -(d2 / d1);
      double d4 = paramDouble3 + d3;
      if (d1 == 0.0D || d2 == 0.0D || paramDouble3 == d4)
        break; 
      paramDouble3 = d4;
    } 
    return (within(paramDouble3, d, 1000.0D * Math.ulp(d)) && inInterval(paramDouble3, paramDouble1, paramDouble2)) ? paramDouble3 : d;
  }
  
  private static double bisectRootWithHint(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2, double paramDouble3) {
    double d1 = Math.min(Math.abs(paramDouble3 - paramDouble1) / 64.0D, 0.0625D);
    double d2 = Math.min(Math.abs(paramDouble3 - paramDouble2) / 64.0D, 0.0625D);
    double d3 = paramDouble3 - d1;
    double d4 = paramDouble3 + d2;
    double d5 = solveEqn(paramArrayOfDouble, 3, d3);
    double d6;
    for (d6 = solveEqn(paramArrayOfDouble, 3, d4); oppositeSigns(d5, d6); d6 = solveEqn(paramArrayOfDouble, 3, d4)) {
      if (d3 >= d4)
        return d3; 
      paramDouble1 = d3;
      paramDouble2 = d4;
      d1 /= 64.0D;
      d2 /= 64.0D;
      d3 = paramDouble3 - d1;
      d4 = paramDouble3 + d2;
      d5 = solveEqn(paramArrayOfDouble, 3, d3);
    } 
    return (d5 == 0.0D) ? d3 : ((d6 == 0.0D) ? d4 : bisectRoot(paramArrayOfDouble, paramDouble1, paramDouble2));
  }
  
  private static double bisectRoot(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2) {
    double d1 = solveEqn(paramArrayOfDouble, 3, paramDouble1);
    double d2;
    for (d2 = paramDouble1 + (paramDouble2 - paramDouble1) / 2.0D; d2 != paramDouble1 && d2 != paramDouble2; d2 = paramDouble1 + (paramDouble2 - paramDouble1) / 2.0D) {
      double d = solveEqn(paramArrayOfDouble, 3, d2);
      if (d == 0.0D)
        return d2; 
      if (oppositeSigns(d1, d)) {
        paramDouble2 = d2;
      } else {
        d1 = d;
        paramDouble1 = d2;
      } 
    } 
    return d2;
  }
  
  private static boolean inInterval(double paramDouble1, double paramDouble2, double paramDouble3) { return (paramDouble2 <= paramDouble1 && paramDouble1 <= paramDouble3); }
  
  private static boolean within(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d = paramDouble2 - paramDouble1;
    return (d <= paramDouble3 && d >= -paramDouble3);
  }
  
  private static boolean iszero(double paramDouble1, double paramDouble2) { return within(paramDouble1, 0.0D, paramDouble2); }
  
  private static boolean oppositeSigns(double paramDouble1, double paramDouble2) { return ((paramDouble1 < 0.0D && paramDouble2 > 0.0D) || (paramDouble1 > 0.0D && paramDouble2 < 0.0D)); }
  
  private static double solveEqn(double[] paramArrayOfDouble, int paramInt, double paramDouble) {
    double d;
    for (d = paramArrayOfDouble[paramInt]; --paramInt >= 0; d = d * paramDouble + paramArrayOfDouble[paramInt]);
    return d;
  }
  
  private static double getRootUpperBound(double[] paramArrayOfDouble) {
    double d1 = paramArrayOfDouble[3];
    double d2 = paramArrayOfDouble[2];
    double d3 = paramArrayOfDouble[1];
    double d4 = paramArrayOfDouble[0];
    null = 1.0D + Math.max(Math.max(Math.abs(d2), Math.abs(d3)), Math.abs(d4)) / Math.abs(d1);
    return Math.ulp(null) + 1.0D;
  }
  
  public boolean contains(double paramDouble1, double paramDouble2) {
    if (paramDouble1 * 0.0D + paramDouble2 * 0.0D != 0.0D)
      return false; 
    double d1 = getX1();
    double d2 = getY1();
    double d3 = getX2();
    double d4 = getY2();
    int i = Curve.pointCrossingsForLine(paramDouble1, paramDouble2, d1, d2, d3, d4) + Curve.pointCrossingsForCubic(paramDouble1, paramDouble2, d1, d2, getCtrlX1(), getCtrlY1(), getCtrlX2(), getCtrlY2(), d3, d4, 0);
    return ((i & true) == 1);
  }
  
  public boolean contains(Point2D paramPoint2D) { return contains(paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    int i = rectCrossings(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    return (i != 0);
  }
  
  public boolean intersects(Rectangle2D paramRectangle2D) { return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    int i = rectCrossings(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    return (i != 0 && i != Integer.MIN_VALUE);
  }
  
  private int rectCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    int i = 0;
    if (getX1() != getX2() || getY1() != getY2()) {
      i = Curve.rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4, getX1(), getY1(), getX2(), getY2());
      if (i == Integer.MIN_VALUE)
        return i; 
    } 
    return Curve.rectCrossingsForCubic(i, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4, getX2(), getY2(), getCtrlX2(), getCtrlY2(), getCtrlX1(), getCtrlY1(), getX1(), getY1(), 0);
  }
  
  public boolean contains(Rectangle2D paramRectangle2D) { return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public Rectangle getBounds() { return getBounds2D().getBounds(); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform) { return new CubicIterator(this, paramAffineTransform); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble) { return new FlatteningPathIterator(getPathIterator(paramAffineTransform), paramDouble); }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public static class Double extends CubicCurve2D implements Serializable {
    public double x1;
    
    public double y1;
    
    public double ctrlx1;
    
    public double ctrly1;
    
    public double ctrlx2;
    
    public double ctrly2;
    
    public double x2;
    
    public double y2;
    
    private static final long serialVersionUID = -4202960122839707295L;
    
    public Double() {}
    
    public Double(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8) { setCurve(param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6, param1Double7, param1Double8); }
    
    public double getX1() { return this.x1; }
    
    public double getY1() { return this.y1; }
    
    public Point2D getP1() { return new Point2D.Double(this.x1, this.y1); }
    
    public double getCtrlX1() { return this.ctrlx1; }
    
    public double getCtrlY1() { return this.ctrly1; }
    
    public Point2D getCtrlP1() { return new Point2D.Double(this.ctrlx1, this.ctrly1); }
    
    public double getCtrlX2() { return this.ctrlx2; }
    
    public double getCtrlY2() { return this.ctrly2; }
    
    public Point2D getCtrlP2() { return new Point2D.Double(this.ctrlx2, this.ctrly2); }
    
    public double getX2() { return this.x2; }
    
    public double getY2() { return this.y2; }
    
    public Point2D getP2() { return new Point2D.Double(this.x2, this.y2); }
    
    public void setCurve(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8) {
      this.x1 = param1Double1;
      this.y1 = param1Double2;
      this.ctrlx1 = param1Double3;
      this.ctrly1 = param1Double4;
      this.ctrlx2 = param1Double5;
      this.ctrly2 = param1Double6;
      this.x2 = param1Double7;
      this.y2 = param1Double8;
    }
    
    public Rectangle2D getBounds2D() {
      double d1 = Math.min(Math.min(this.x1, this.x2), Math.min(this.ctrlx1, this.ctrlx2));
      double d2 = Math.min(Math.min(this.y1, this.y2), Math.min(this.ctrly1, this.ctrly2));
      double d3 = Math.max(Math.max(this.x1, this.x2), Math.max(this.ctrlx1, this.ctrlx2));
      double d4 = Math.max(Math.max(this.y1, this.y2), Math.max(this.ctrly1, this.ctrly2));
      return new Rectangle2D.Double(d1, d2, d3 - d1, d4 - d2);
    }
  }
  
  public static class Float extends CubicCurve2D implements Serializable {
    public float x1;
    
    public float y1;
    
    public float ctrlx1;
    
    public float ctrly1;
    
    public float ctrlx2;
    
    public float ctrly2;
    
    public float x2;
    
    public float y2;
    
    private static final long serialVersionUID = -1272015596714244385L;
    
    public Float() {}
    
    public Float(float param1Float1, float param1Float2, float param1Float3, float param1Float4, float param1Float5, float param1Float6, float param1Float7, float param1Float8) { setCurve(param1Float1, param1Float2, param1Float3, param1Float4, param1Float5, param1Float6, param1Float7, param1Float8); }
    
    public double getX1() { return this.x1; }
    
    public double getY1() { return this.y1; }
    
    public Point2D getP1() { return new Point2D.Float(this.x1, this.y1); }
    
    public double getCtrlX1() { return this.ctrlx1; }
    
    public double getCtrlY1() { return this.ctrly1; }
    
    public Point2D getCtrlP1() { return new Point2D.Float(this.ctrlx1, this.ctrly1); }
    
    public double getCtrlX2() { return this.ctrlx2; }
    
    public double getCtrlY2() { return this.ctrly2; }
    
    public Point2D getCtrlP2() { return new Point2D.Float(this.ctrlx2, this.ctrly2); }
    
    public double getX2() { return this.x2; }
    
    public double getY2() { return this.y2; }
    
    public Point2D getP2() { return new Point2D.Float(this.x2, this.y2); }
    
    public void setCurve(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8) {
      this.x1 = (float)param1Double1;
      this.y1 = (float)param1Double2;
      this.ctrlx1 = (float)param1Double3;
      this.ctrly1 = (float)param1Double4;
      this.ctrlx2 = (float)param1Double5;
      this.ctrly2 = (float)param1Double6;
      this.x2 = (float)param1Double7;
      this.y2 = (float)param1Double8;
    }
    
    public void setCurve(float param1Float1, float param1Float2, float param1Float3, float param1Float4, float param1Float5, float param1Float6, float param1Float7, float param1Float8) {
      this.x1 = param1Float1;
      this.y1 = param1Float2;
      this.ctrlx1 = param1Float3;
      this.ctrly1 = param1Float4;
      this.ctrlx2 = param1Float5;
      this.ctrly2 = param1Float6;
      this.x2 = param1Float7;
      this.y2 = param1Float8;
    }
    
    public Rectangle2D getBounds2D() {
      float f1 = Math.min(Math.min(this.x1, this.x2), Math.min(this.ctrlx1, this.ctrlx2));
      float f2 = Math.min(Math.min(this.y1, this.y2), Math.min(this.ctrly1, this.ctrly2));
      float f3 = Math.max(Math.max(this.x1, this.x2), Math.max(this.ctrlx1, this.ctrlx2));
      float f4 = Math.max(Math.max(this.y1, this.y2), Math.max(this.ctrly1, this.ctrly2));
      return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\CubicCurve2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */