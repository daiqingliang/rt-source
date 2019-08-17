package sun.awt.geom;

import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

public abstract class Curve {
  public static final int INCREASING = 1;
  
  public static final int DECREASING = -1;
  
  protected int direction;
  
  public static final int RECT_INTERSECTS = -2147483648;
  
  public static final double TMIN = 0.001D;
  
  public static void insertMove(Vector paramVector, double paramDouble1, double paramDouble2) { paramVector.add(new Order0(paramDouble1, paramDouble2)); }
  
  public static void insertLine(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramDouble2 < paramDouble4) {
      paramVector.add(new Order1(paramDouble1, paramDouble2, paramDouble3, paramDouble4, 1));
    } else if (paramDouble2 > paramDouble4) {
      paramVector.add(new Order1(paramDouble3, paramDouble4, paramDouble1, paramDouble2, -1));
    } 
  }
  
  public static void insertQuad(Vector paramVector, double paramDouble1, double paramDouble2, double[] paramArrayOfDouble) {
    double d = paramArrayOfDouble[3];
    if (paramDouble2 > d) {
      Order2.insert(paramVector, paramArrayOfDouble, paramArrayOfDouble[2], d, paramArrayOfDouble[0], paramArrayOfDouble[1], paramDouble1, paramDouble2, -1);
    } else {
      if (paramDouble2 == d && paramDouble2 == paramArrayOfDouble[1])
        return; 
      Order2.insert(paramVector, paramArrayOfDouble, paramDouble1, paramDouble2, paramArrayOfDouble[0], paramArrayOfDouble[1], paramArrayOfDouble[2], d, 1);
    } 
  }
  
  public static void insertCubic(Vector paramVector, double paramDouble1, double paramDouble2, double[] paramArrayOfDouble) {
    double d = paramArrayOfDouble[5];
    if (paramDouble2 > d) {
      Order3.insert(paramVector, paramArrayOfDouble, paramArrayOfDouble[4], d, paramArrayOfDouble[2], paramArrayOfDouble[3], paramArrayOfDouble[0], paramArrayOfDouble[1], paramDouble1, paramDouble2, -1);
    } else {
      if (paramDouble2 == d && paramDouble2 == paramArrayOfDouble[1] && paramDouble2 == paramArrayOfDouble[3])
        return; 
      Order3.insert(paramVector, paramArrayOfDouble, paramDouble1, paramDouble2, paramArrayOfDouble[0], paramArrayOfDouble[1], paramArrayOfDouble[2], paramArrayOfDouble[3], paramArrayOfDouble[4], d, 1);
    } 
  }
  
  public static int pointCrossingsForPath(PathIterator paramPathIterator, double paramDouble1, double paramDouble2) {
    if (paramPathIterator.isDone())
      return 0; 
    double[] arrayOfDouble = new double[6];
    if (paramPathIterator.currentSegment(arrayOfDouble) != 0)
      throw new IllegalPathStateException("missing initial moveto in path definition"); 
    paramPathIterator.next();
    double d1 = arrayOfDouble[0];
    double d2 = arrayOfDouble[1];
    double d3 = d1;
    double d4 = d2;
    int i = 0;
    while (!paramPathIterator.isDone()) {
      double d6;
      double d5;
      switch (paramPathIterator.currentSegment(arrayOfDouble)) {
        case 0:
          if (d4 != d2)
            i += pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2); 
          d1 = d3 = arrayOfDouble[0];
          d2 = d4 = arrayOfDouble[1];
        case 1:
          d5 = arrayOfDouble[0];
          d6 = arrayOfDouble[1];
          i += pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d5, d6);
          d3 = d5;
          d4 = d6;
          break;
        case 2:
          d5 = arrayOfDouble[2];
          d6 = arrayOfDouble[3];
          i += pointCrossingsForQuad(paramDouble1, paramDouble2, d3, d4, arrayOfDouble[0], arrayOfDouble[1], d5, d6, 0);
          d3 = d5;
          d4 = d6;
          break;
        case 3:
          d5 = arrayOfDouble[4];
          d6 = arrayOfDouble[5];
          i += pointCrossingsForCubic(paramDouble1, paramDouble2, d3, d4, arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3], d5, d6, 0);
          d3 = d5;
          d4 = d6;
          break;
        case 4:
          if (d4 != d2)
            i += pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2); 
          d3 = d1;
          d4 = d2;
          break;
      } 
      paramPathIterator.next();
    } 
    if (d4 != d2)
      i += pointCrossingsForLine(paramDouble1, paramDouble2, d3, d4, d1, d2); 
    return i;
  }
  
  public static int pointCrossingsForLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6) {
    if (paramDouble2 < paramDouble4 && paramDouble2 < paramDouble6)
      return 0; 
    if (paramDouble2 >= paramDouble4 && paramDouble2 >= paramDouble6)
      return 0; 
    if (paramDouble1 >= paramDouble3 && paramDouble1 >= paramDouble5)
      return 0; 
    if (paramDouble1 < paramDouble3 && paramDouble1 < paramDouble5)
      return (paramDouble4 < paramDouble6) ? 1 : -1; 
    double d = paramDouble3 + (paramDouble2 - paramDouble4) * (paramDouble5 - paramDouble3) / (paramDouble6 - paramDouble4);
    return (paramDouble1 >= d) ? 0 : ((paramDouble4 < paramDouble6) ? 1 : -1);
  }
  
  public static int pointCrossingsForQuad(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, int paramInt) {
    if (paramDouble2 < paramDouble4 && paramDouble2 < paramDouble6 && paramDouble2 < paramDouble8)
      return 0; 
    if (paramDouble2 >= paramDouble4 && paramDouble2 >= paramDouble6 && paramDouble2 >= paramDouble8)
      return 0; 
    if (paramDouble1 >= paramDouble3 && paramDouble1 >= paramDouble5 && paramDouble1 >= paramDouble7)
      return 0; 
    if (paramDouble1 < paramDouble3 && paramDouble1 < paramDouble5 && paramDouble1 < paramDouble7) {
      if (paramDouble2 >= paramDouble4) {
        if (paramDouble2 < paramDouble8)
          return 1; 
      } else if (paramDouble2 >= paramDouble8) {
        return -1;
      } 
      return 0;
    } 
    if (paramInt > 52)
      return pointCrossingsForLine(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble7, paramDouble8); 
    double d1 = (paramDouble3 + paramDouble5) / 2.0D;
    double d2 = (paramDouble4 + paramDouble6) / 2.0D;
    double d3 = (paramDouble5 + paramDouble7) / 2.0D;
    double d4 = (paramDouble6 + paramDouble8) / 2.0D;
    paramDouble5 = (d1 + d3) / 2.0D;
    paramDouble6 = (d2 + d4) / 2.0D;
    return (Double.isNaN(paramDouble5) || Double.isNaN(paramDouble6)) ? 0 : (pointCrossingsForQuad(paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, paramDouble5, paramDouble6, paramInt + 1) + pointCrossingsForQuad(paramDouble1, paramDouble2, paramDouble5, paramDouble6, d3, d4, paramDouble7, paramDouble8, paramInt + 1));
  }
  
  public static int pointCrossingsForCubic(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, int paramInt) {
    if (paramDouble2 < paramDouble4 && paramDouble2 < paramDouble6 && paramDouble2 < paramDouble8 && paramDouble2 < paramDouble10)
      return 0; 
    if (paramDouble2 >= paramDouble4 && paramDouble2 >= paramDouble6 && paramDouble2 >= paramDouble8 && paramDouble2 >= paramDouble10)
      return 0; 
    if (paramDouble1 >= paramDouble3 && paramDouble1 >= paramDouble5 && paramDouble1 >= paramDouble7 && paramDouble1 >= paramDouble9)
      return 0; 
    if (paramDouble1 < paramDouble3 && paramDouble1 < paramDouble5 && paramDouble1 < paramDouble7 && paramDouble1 < paramDouble9) {
      if (paramDouble2 >= paramDouble4) {
        if (paramDouble2 < paramDouble10)
          return 1; 
      } else if (paramDouble2 >= paramDouble10) {
        return -1;
      } 
      return 0;
    } 
    if (paramInt > 52)
      return pointCrossingsForLine(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble9, paramDouble10); 
    double d1 = (paramDouble5 + paramDouble7) / 2.0D;
    double d2 = (paramDouble6 + paramDouble8) / 2.0D;
    paramDouble5 = (paramDouble3 + paramDouble5) / 2.0D;
    paramDouble6 = (paramDouble4 + paramDouble6) / 2.0D;
    paramDouble7 = (paramDouble7 + paramDouble9) / 2.0D;
    paramDouble8 = (paramDouble8 + paramDouble10) / 2.0D;
    double d3 = (paramDouble5 + d1) / 2.0D;
    double d4 = (paramDouble6 + d2) / 2.0D;
    double d5 = (d1 + paramDouble7) / 2.0D;
    double d6 = (d2 + paramDouble8) / 2.0D;
    d1 = (d3 + d5) / 2.0D;
    d2 = (d4 + d6) / 2.0D;
    return (Double.isNaN(d1) || Double.isNaN(d2)) ? 0 : (pointCrossingsForCubic(paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, d3, d4, d1, d2, paramInt + 1) + pointCrossingsForCubic(paramDouble1, paramDouble2, d1, d2, d5, d6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramInt + 1));
  }
  
  public static int rectCrossingsForPath(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramDouble3 <= paramDouble1 || paramDouble4 <= paramDouble2)
      return 0; 
    if (paramPathIterator.isDone())
      return 0; 
    double[] arrayOfDouble = new double[6];
    if (paramPathIterator.currentSegment(arrayOfDouble) != 0)
      throw new IllegalPathStateException("missing initial moveto in path definition"); 
    paramPathIterator.next();
    double d3 = arrayOfDouble[0];
    double d1 = d3;
    double d4 = arrayOfDouble[1];
    double d2 = d4;
    int i = 0;
    while (i != Integer.MIN_VALUE && !paramPathIterator.isDone()) {
      double d6;
      double d5;
      switch (paramPathIterator.currentSegment(arrayOfDouble)) {
        case 0:
          if (d1 != d3 || d2 != d4)
            i = rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4); 
          d3 = d1 = arrayOfDouble[0];
          d4 = d2 = arrayOfDouble[1];
        case 1:
          d5 = arrayOfDouble[0];
          d6 = arrayOfDouble[1];
          i = rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d5, d6);
          d1 = d5;
          d2 = d6;
          break;
        case 2:
          d5 = arrayOfDouble[2];
          d6 = arrayOfDouble[3];
          i = rectCrossingsForQuad(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, arrayOfDouble[0], arrayOfDouble[1], d5, d6, 0);
          d1 = d5;
          d2 = d6;
          break;
        case 3:
          d5 = arrayOfDouble[4];
          d6 = arrayOfDouble[5];
          i = rectCrossingsForCubic(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3], d5, d6, 0);
          d1 = d5;
          d2 = d6;
          break;
        case 4:
          if (d1 != d3 || d2 != d4)
            i = rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4); 
          d1 = d3;
          d2 = d4;
          break;
      } 
      paramPathIterator.next();
    } 
    if (i != Integer.MIN_VALUE && (d1 != d3 || d2 != d4))
      i = rectCrossingsForLine(i, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4); 
    return i;
  }
  
  public static int rectCrossingsForLine(int paramInt, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8) {
    if (paramDouble6 >= paramDouble4 && paramDouble8 >= paramDouble4)
      return paramInt; 
    if (paramDouble6 <= paramDouble2 && paramDouble8 <= paramDouble2)
      return paramInt; 
    if (paramDouble5 <= paramDouble1 && paramDouble7 <= paramDouble1)
      return paramInt; 
    if (paramDouble5 >= paramDouble3 && paramDouble7 >= paramDouble3) {
      if (paramDouble6 < paramDouble8) {
        if (paramDouble6 <= paramDouble2)
          paramInt++; 
        if (paramDouble8 >= paramDouble4)
          paramInt++; 
      } else if (paramDouble8 < paramDouble6) {
        if (paramDouble8 <= paramDouble2)
          paramInt--; 
        if (paramDouble6 >= paramDouble4)
          paramInt--; 
      } 
      return paramInt;
    } 
    if ((paramDouble5 > paramDouble1 && paramDouble5 < paramDouble3 && paramDouble6 > paramDouble2 && paramDouble6 < paramDouble4) || (paramDouble7 > paramDouble1 && paramDouble7 < paramDouble3 && paramDouble8 > paramDouble2 && paramDouble8 < paramDouble4))
      return Integer.MIN_VALUE; 
    double d1 = paramDouble5;
    if (paramDouble6 < paramDouble2) {
      d1 += (paramDouble2 - paramDouble6) * (paramDouble7 - paramDouble5) / (paramDouble8 - paramDouble6);
    } else if (paramDouble6 > paramDouble4) {
      d1 += (paramDouble4 - paramDouble6) * (paramDouble7 - paramDouble5) / (paramDouble8 - paramDouble6);
    } 
    double d2 = paramDouble7;
    if (paramDouble8 < paramDouble2) {
      d2 += (paramDouble2 - paramDouble8) * (paramDouble5 - paramDouble7) / (paramDouble6 - paramDouble8);
    } else if (paramDouble8 > paramDouble4) {
      d2 += (paramDouble4 - paramDouble8) * (paramDouble5 - paramDouble7) / (paramDouble6 - paramDouble8);
    } 
    if (d1 <= paramDouble1 && d2 <= paramDouble1)
      return paramInt; 
    if (d1 >= paramDouble3 && d2 >= paramDouble3) {
      if (paramDouble6 < paramDouble8) {
        if (paramDouble6 <= paramDouble2)
          paramInt++; 
        if (paramDouble8 >= paramDouble4)
          paramInt++; 
      } else if (paramDouble8 < paramDouble6) {
        if (paramDouble8 <= paramDouble2)
          paramInt--; 
        if (paramDouble6 >= paramDouble4)
          paramInt--; 
      } 
      return paramInt;
    } 
    return Integer.MIN_VALUE;
  }
  
  public static int rectCrossingsForQuad(int paramInt1, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, int paramInt2) {
    if (paramDouble6 >= paramDouble4 && paramDouble8 >= paramDouble4 && paramDouble10 >= paramDouble4)
      return paramInt1; 
    if (paramDouble6 <= paramDouble2 && paramDouble8 <= paramDouble2 && paramDouble10 <= paramDouble2)
      return paramInt1; 
    if (paramDouble5 <= paramDouble1 && paramDouble7 <= paramDouble1 && paramDouble9 <= paramDouble1)
      return paramInt1; 
    if (paramDouble5 >= paramDouble3 && paramDouble7 >= paramDouble3 && paramDouble9 >= paramDouble3) {
      if (paramDouble6 < paramDouble10) {
        if (paramDouble6 <= paramDouble2 && paramDouble10 > paramDouble2)
          paramInt1++; 
        if (paramDouble6 < paramDouble4 && paramDouble10 >= paramDouble4)
          paramInt1++; 
      } else if (paramDouble10 < paramDouble6) {
        if (paramDouble10 <= paramDouble2 && paramDouble6 > paramDouble2)
          paramInt1--; 
        if (paramDouble10 < paramDouble4 && paramDouble6 >= paramDouble4)
          paramInt1--; 
      } 
      return paramInt1;
    } 
    if ((paramDouble5 < paramDouble3 && paramDouble5 > paramDouble1 && paramDouble6 < paramDouble4 && paramDouble6 > paramDouble2) || (paramDouble9 < paramDouble3 && paramDouble9 > paramDouble1 && paramDouble10 < paramDouble4 && paramDouble10 > paramDouble2))
      return Integer.MIN_VALUE; 
    if (paramInt2 > 52)
      return rectCrossingsForLine(paramInt1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble9, paramDouble10); 
    double d1 = (paramDouble5 + paramDouble7) / 2.0D;
    double d2 = (paramDouble6 + paramDouble8) / 2.0D;
    double d3 = (paramDouble7 + paramDouble9) / 2.0D;
    double d4 = (paramDouble8 + paramDouble10) / 2.0D;
    paramDouble7 = (d1 + d3) / 2.0D;
    paramDouble8 = (d2 + d4) / 2.0D;
    if (Double.isNaN(paramDouble7) || Double.isNaN(paramDouble8))
      return 0; 
    paramInt1 = rectCrossingsForQuad(paramInt1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, d1, d2, paramDouble7, paramDouble8, paramInt2 + 1);
    if (paramInt1 != Integer.MIN_VALUE)
      paramInt1 = rectCrossingsForQuad(paramInt1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble7, paramDouble8, d3, d4, paramDouble9, paramDouble10, paramInt2 + 1); 
    return paramInt1;
  }
  
  public static int rectCrossingsForCubic(int paramInt1, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12, int paramInt2) {
    if (paramDouble6 >= paramDouble4 && paramDouble8 >= paramDouble4 && paramDouble10 >= paramDouble4 && paramDouble12 >= paramDouble4)
      return paramInt1; 
    if (paramDouble6 <= paramDouble2 && paramDouble8 <= paramDouble2 && paramDouble10 <= paramDouble2 && paramDouble12 <= paramDouble2)
      return paramInt1; 
    if (paramDouble5 <= paramDouble1 && paramDouble7 <= paramDouble1 && paramDouble9 <= paramDouble1 && paramDouble11 <= paramDouble1)
      return paramInt1; 
    if (paramDouble5 >= paramDouble3 && paramDouble7 >= paramDouble3 && paramDouble9 >= paramDouble3 && paramDouble11 >= paramDouble3) {
      if (paramDouble6 < paramDouble12) {
        if (paramDouble6 <= paramDouble2 && paramDouble12 > paramDouble2)
          paramInt1++; 
        if (paramDouble6 < paramDouble4 && paramDouble12 >= paramDouble4)
          paramInt1++; 
      } else if (paramDouble12 < paramDouble6) {
        if (paramDouble12 <= paramDouble2 && paramDouble6 > paramDouble2)
          paramInt1--; 
        if (paramDouble12 < paramDouble4 && paramDouble6 >= paramDouble4)
          paramInt1--; 
      } 
      return paramInt1;
    } 
    if ((paramDouble5 > paramDouble1 && paramDouble5 < paramDouble3 && paramDouble6 > paramDouble2 && paramDouble6 < paramDouble4) || (paramDouble11 > paramDouble1 && paramDouble11 < paramDouble3 && paramDouble12 > paramDouble2 && paramDouble12 < paramDouble4))
      return Integer.MIN_VALUE; 
    if (paramInt2 > 52)
      return rectCrossingsForLine(paramInt1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble11, paramDouble12); 
    double d1 = (paramDouble7 + paramDouble9) / 2.0D;
    double d2 = (paramDouble8 + paramDouble10) / 2.0D;
    paramDouble7 = (paramDouble5 + paramDouble7) / 2.0D;
    paramDouble8 = (paramDouble6 + paramDouble8) / 2.0D;
    paramDouble9 = (paramDouble9 + paramDouble11) / 2.0D;
    paramDouble10 = (paramDouble10 + paramDouble12) / 2.0D;
    double d3 = (paramDouble7 + d1) / 2.0D;
    double d4 = (paramDouble8 + d2) / 2.0D;
    double d5 = (d1 + paramDouble9) / 2.0D;
    double d6 = (d2 + paramDouble10) / 2.0D;
    d1 = (d3 + d5) / 2.0D;
    d2 = (d4 + d6) / 2.0D;
    if (Double.isNaN(d1) || Double.isNaN(d2))
      return 0; 
    paramInt1 = rectCrossingsForCubic(paramInt1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, d3, d4, d1, d2, paramInt2 + 1);
    if (paramInt1 != Integer.MIN_VALUE)
      paramInt1 = rectCrossingsForCubic(paramInt1, paramDouble1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d5, d6, paramDouble9, paramDouble10, paramDouble11, paramDouble12, paramInt2 + 1); 
    return paramInt1;
  }
  
  public Curve(int paramInt) { this.direction = paramInt; }
  
  public final int getDirection() { return this.direction; }
  
  public final Curve getWithDirection(int paramInt) { return (this.direction == paramInt) ? this : getReversedCurve(); }
  
  public static double round(double paramDouble) { return paramDouble; }
  
  public static int orderof(double paramDouble1, double paramDouble2) { return (paramDouble1 < paramDouble2) ? -1 : ((paramDouble1 > paramDouble2) ? 1 : 0); }
  
  public static long signeddiffbits(double paramDouble1, double paramDouble2) { return Double.doubleToLongBits(paramDouble1) - Double.doubleToLongBits(paramDouble2); }
  
  public static long diffbits(double paramDouble1, double paramDouble2) { return Math.abs(Double.doubleToLongBits(paramDouble1) - Double.doubleToLongBits(paramDouble2)); }
  
  public static double prev(double paramDouble) { return Double.longBitsToDouble(Double.doubleToLongBits(paramDouble) - 1L); }
  
  public static double next(double paramDouble) { return Double.longBitsToDouble(Double.doubleToLongBits(paramDouble) + 1L); }
  
  public String toString() { return "Curve[" + getOrder() + ", " + "(" + round(getX0()) + ", " + round(getY0()) + "), " + controlPointString() + "(" + round(getX1()) + ", " + round(getY1()) + "), " + ((this.direction == 1) ? "D" : "U") + "]"; }
  
  public String controlPointString() { return ""; }
  
  public abstract int getOrder();
  
  public abstract double getXTop();
  
  public abstract double getYTop();
  
  public abstract double getXBot();
  
  public abstract double getYBot();
  
  public abstract double getXMin();
  
  public abstract double getXMax();
  
  public abstract double getX0();
  
  public abstract double getY0();
  
  public abstract double getX1();
  
  public abstract double getY1();
  
  public abstract double XforY(double paramDouble);
  
  public abstract double TforY(double paramDouble);
  
  public abstract double XforT(double paramDouble);
  
  public abstract double YforT(double paramDouble);
  
  public abstract double dXforT(double paramDouble, int paramInt);
  
  public abstract double dYforT(double paramDouble, int paramInt);
  
  public abstract double nextVertical(double paramDouble1, double paramDouble2);
  
  public int crossingsFor(double paramDouble1, double paramDouble2) { return (paramDouble2 >= getYTop() && paramDouble2 < getYBot() && paramDouble1 < getXMax() && (paramDouble1 < getXMin() || paramDouble1 < XforY(paramDouble2))) ? 1 : 0; }
  
  public boolean accumulateCrossings(Crossings paramCrossings) {
    double d10;
    double d9;
    double d8;
    double d7;
    double d1 = paramCrossings.getXHi();
    if (getXMin() >= d1)
      return false; 
    double d2 = paramCrossings.getXLo();
    double d3 = paramCrossings.getYLo();
    double d4 = paramCrossings.getYHi();
    double d5 = getYTop();
    double d6 = getYBot();
    if (d5 < d3) {
      if (d6 <= d3)
        return false; 
      d8 = d3;
      d7 = TforY(d3);
    } else {
      if (d5 >= d4)
        return false; 
      d8 = d5;
      d7 = 0.0D;
    } 
    if (d6 > d4) {
      d10 = d4;
      d9 = TforY(d4);
    } else {
      d10 = d6;
      d9 = 1.0D;
    } 
    boolean bool1 = false;
    boolean bool2 = false;
    while (true) {
      double d = XforT(d7);
      if (d < d1) {
        if (bool2 || d > d2)
          return true; 
        bool1 = true;
      } else {
        if (bool1)
          return true; 
        bool2 = true;
      } 
      if (d7 >= d9)
        break; 
      d7 = nextVertical(d7, d9);
    } 
    if (bool1)
      paramCrossings.record(d8, d10, this.direction); 
    return false;
  }
  
  public abstract void enlarge(Rectangle2D paramRectangle2D);
  
  public Curve getSubCurve(double paramDouble1, double paramDouble2) { return getSubCurve(paramDouble1, paramDouble2, this.direction); }
  
  public abstract Curve getReversedCurve();
  
  public abstract Curve getSubCurve(double paramDouble1, double paramDouble2, int paramInt);
  
  public int compareTo(Curve paramCurve, double[] paramArrayOfDouble) {
    double d1 = paramArrayOfDouble[0];
    double d2 = paramArrayOfDouble[1];
    d2 = Math.min(Math.min(d2, getYBot()), paramCurve.getYBot());
    if (d2 <= paramArrayOfDouble[0]) {
      System.err.println("this == " + this);
      System.err.println("that == " + paramCurve);
      System.out.println("target range = " + paramArrayOfDouble[0] + "=>" + paramArrayOfDouble[1]);
      throw new InternalError("backstepping from " + paramArrayOfDouble[0] + " to " + d2);
    } 
    paramArrayOfDouble[1] = d2;
    if (getXMax() <= paramCurve.getXMin())
      return (getXMin() == paramCurve.getXMax()) ? 0 : -1; 
    if (getXMin() >= paramCurve.getXMax())
      return 1; 
    double d3 = TforY(d1);
    double d4 = YforT(d3);
    if (d4 < d1) {
      d3 = refineTforY(d3, d4, d1);
      d4 = YforT(d3);
    } 
    double d5 = TforY(d2);
    if (YforT(d5) < d1)
      d5 = refineTforY(d5, YforT(d5), d1); 
    double d6 = paramCurve.TforY(d1);
    double d7 = paramCurve.YforT(d6);
    if (d7 < d1) {
      d6 = paramCurve.refineTforY(d6, d7, d1);
      d7 = paramCurve.YforT(d6);
    } 
    double d8 = paramCurve.TforY(d2);
    if (paramCurve.YforT(d8) < d1)
      d8 = paramCurve.refineTforY(d8, paramCurve.YforT(d8), d1); 
    double d9 = XforT(d3);
    double d10 = paramCurve.XforT(d6);
    double d11 = Math.max(Math.abs(d1), Math.abs(d2));
    double d12 = Math.max(d11 * 1.0E-14D, 1.0E-300D);
    if (fairlyClose(d9, d10)) {
      double d14 = d12;
      double d15 = Math.min(d12 * 1.0E13D, (d2 - d1) * 0.1D);
      double d16;
      for (d16 = d1 + d14; d16 <= d2; d16 += d14) {
        if (fairlyClose(XforY(d16), paramCurve.XforY(d16))) {
          if (d14 *= 2.0D > d15)
            d14 = d15; 
        } else {
          d16 -= d14;
          while (true) {
            d14 /= 2.0D;
            double d = d16 + d14;
            if (d <= d16)
              break; 
            if (fairlyClose(XforY(d), paramCurve.XforY(d)))
              d16 = d; 
          } 
          break;
        } 
      } 
      if (d16 > d1) {
        if (d16 < d2)
          paramArrayOfDouble[1] = d16; 
        return 0;
      } 
    } 
    if (d12 <= 0.0D)
      System.out.println("ymin = " + d12); 
    while (d3 < d5 && d6 < d8) {
      double d14 = nextVertical(d3, d5);
      double d15 = XforT(d14);
      double d16 = YforT(d14);
      double d17 = paramCurve.nextVertical(d6, d8);
      double d18 = paramCurve.XforT(d17);
      double d19 = paramCurve.YforT(d17);
      try {
        if (findIntersect(paramCurve, paramArrayOfDouble, d12, 0, 0, d3, d9, d4, d14, d15, d16, d6, d10, d7, d17, d18, d19))
          break; 
      } catch (Throwable throwable) {
        System.err.println("Error: " + throwable);
        System.err.println("y range was " + paramArrayOfDouble[0] + "=>" + paramArrayOfDouble[1]);
        System.err.println("s y range is " + d4 + "=>" + d16);
        System.err.println("t y range is " + d7 + "=>" + d19);
        System.err.println("ymin is " + d12);
        return 0;
      } 
      if (d16 < d19) {
        if (d16 > paramArrayOfDouble[0]) {
          if (d16 < paramArrayOfDouble[1])
            paramArrayOfDouble[1] = d16; 
          break;
        } 
        d3 = d14;
        d9 = d15;
        d4 = d16;
        continue;
      } 
      if (d19 > paramArrayOfDouble[0]) {
        if (d19 < paramArrayOfDouble[1])
          paramArrayOfDouble[1] = d19; 
        break;
      } 
      d6 = d17;
      d10 = d18;
      d7 = d19;
    } 
    double d13 = (paramArrayOfDouble[0] + paramArrayOfDouble[1]) / 2.0D;
    return orderof(XforY(d13), paramCurve.XforY(d13));
  }
  
  public boolean findIntersect(Curve paramCurve, double[] paramArrayOfDouble, double paramDouble1, int paramInt1, int paramInt2, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10, double paramDouble11, double paramDouble12, double paramDouble13) {
    if (paramDouble4 > paramDouble13 || paramDouble10 > paramDouble7)
      return false; 
    if (Math.min(paramDouble3, paramDouble6) > Math.max(paramDouble9, paramDouble12) || Math.max(paramDouble3, paramDouble6) < Math.min(paramDouble9, paramDouble12))
      return false; 
    if (paramDouble5 - paramDouble2 > 0.001D) {
      double d1 = (paramDouble2 + paramDouble5) / 2.0D;
      double d2 = XforT(d1);
      double d3 = YforT(d1);
      if (d1 == paramDouble2 || d1 == paramDouble5) {
        System.out.println("s0 = " + paramDouble2);
        System.out.println("s1 = " + paramDouble5);
        throw new InternalError("no s progress!");
      } 
      if (paramDouble11 - paramDouble8 > 0.001D) {
        double d4 = (paramDouble8 + paramDouble11) / 2.0D;
        double d5 = paramCurve.XforT(d4);
        double d6 = paramCurve.YforT(d4);
        if (d4 == paramDouble8 || d4 == paramDouble11) {
          System.out.println("t0 = " + paramDouble8);
          System.out.println("t1 = " + paramDouble11);
          throw new InternalError("no t progress!");
        } 
        if (d3 >= paramDouble10 && d6 >= paramDouble4 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1 + 1, paramInt2 + 1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, paramDouble8, paramDouble9, paramDouble10, d4, d5, d6))
          return true; 
        if (d3 >= d6 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1 + 1, paramInt2 + 1, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, d4, d5, d6, paramDouble11, paramDouble12, paramDouble13))
          return true; 
        if (d6 >= d3 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1 + 1, paramInt2 + 1, d1, d2, d3, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, d4, d5, d6))
          return true; 
        if (paramDouble7 >= d6 && paramDouble13 >= d3 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1 + 1, paramInt2 + 1, d1, d2, d3, paramDouble5, paramDouble6, paramDouble7, d4, d5, d6, paramDouble11, paramDouble12, paramDouble13))
          return true; 
      } else {
        if (d3 >= paramDouble10 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1 + 1, paramInt2, paramDouble2, paramDouble3, paramDouble4, d1, d2, d3, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12, paramDouble13))
          return true; 
        if (paramDouble13 >= d3 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1 + 1, paramInt2, d1, d2, d3, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, paramDouble11, paramDouble12, paramDouble13))
          return true; 
      } 
    } else if (paramDouble11 - paramDouble8 > 0.001D) {
      double d1 = (paramDouble8 + paramDouble11) / 2.0D;
      double d2 = paramCurve.XforT(d1);
      double d3 = paramCurve.YforT(d1);
      if (d1 == paramDouble8 || d1 == paramDouble11) {
        System.out.println("t0 = " + paramDouble8);
        System.out.println("t1 = " + paramDouble11);
        throw new InternalError("no t progress!");
      } 
      if (d3 >= paramDouble4 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1, paramInt2 + 1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8, paramDouble9, paramDouble10, d1, d2, d3))
        return true; 
      if (paramDouble7 >= d3 && findIntersect(paramCurve, paramArrayOfDouble, paramDouble1, paramInt1, paramInt2 + 1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, d1, d2, d3, paramDouble11, paramDouble12, paramDouble13))
        return true; 
    } else {
      double d1 = paramDouble6 - paramDouble3;
      double d2 = paramDouble7 - paramDouble4;
      double d3 = paramDouble12 - paramDouble9;
      double d4 = paramDouble13 - paramDouble10;
      double d5 = paramDouble9 - paramDouble3;
      double d6 = paramDouble10 - paramDouble4;
      double d7 = d3 * d2 - d4 * d1;
      if (d7 != 0.0D) {
        double d8 = 1.0D / d7;
        double d9 = (d3 * d6 - d4 * d5) * d8;
        double d10 = (d1 * d6 - d2 * d5) * d8;
        if (d9 >= 0.0D && d9 <= 1.0D && d10 >= 0.0D && d10 <= 1.0D) {
          d9 = paramDouble2 + d9 * (paramDouble5 - paramDouble2);
          d10 = paramDouble8 + d10 * (paramDouble11 - paramDouble8);
          if (d9 < 0.0D || d9 > 1.0D || d10 < 0.0D || d10 > 1.0D)
            System.out.println("Uh oh!"); 
          double d = (YforT(d9) + paramCurve.YforT(d10)) / 2.0D;
          if (d <= paramArrayOfDouble[1] && d > paramArrayOfDouble[0]) {
            paramArrayOfDouble[1] = d;
            return true;
          } 
        } 
      } 
    } 
    return false;
  }
  
  public double refineTforY(double paramDouble1, double paramDouble2, double paramDouble3) {
    double d = 1.0D;
    while (true) {
      double d1 = (paramDouble1 + d) / 2.0D;
      if (d1 == paramDouble1 || d1 == d)
        return d; 
      double d2 = YforT(d1);
      if (d2 < paramDouble3) {
        paramDouble1 = d1;
        paramDouble2 = d2;
        continue;
      } 
      if (d2 > paramDouble3) {
        d = d1;
        continue;
      } 
      break;
    } 
    return d;
  }
  
  public boolean fairlyClose(double paramDouble1, double paramDouble2) { return (Math.abs(paramDouble1 - paramDouble2) < Math.max(Math.abs(paramDouble1), Math.abs(paramDouble2)) * 1.0E-10D); }
  
  public abstract int getSegment(double[] paramArrayOfDouble);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\Curve.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */