package sun.awt.geom;

import java.awt.geom.PathIterator;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Crossings {
  public static final boolean debug = false;
  
  int limit = 0;
  
  double[] yranges = new double[10];
  
  double xlo;
  
  double ylo;
  
  double xhi;
  
  double yhi;
  
  private Vector tmp = new Vector();
  
  public Crossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    this.xlo = paramDouble1;
    this.ylo = paramDouble2;
    this.xhi = paramDouble3;
    this.yhi = paramDouble4;
  }
  
  public final double getXLo() { return this.xlo; }
  
  public final double getYLo() { return this.ylo; }
  
  public final double getXHi() { return this.xhi; }
  
  public final double getYHi() { return this.yhi; }
  
  public abstract void record(double paramDouble1, double paramDouble2, int paramInt);
  
  public void print() {
    System.out.println("Crossings [");
    System.out.println("  bounds = [" + this.ylo + ", " + this.yhi + "]");
    for (boolean bool = false; bool < this.limit; bool += true)
      System.out.println("  [" + this.yranges[bool] + ", " + this.yranges[bool + true] + "]"); 
    System.out.println("]");
  }
  
  public final boolean isEmpty() { return (this.limit == 0); }
  
  public abstract boolean covers(double paramDouble1, double paramDouble2);
  
  public static Crossings findCrossings(Vector paramVector, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    EvenOdd evenOdd = new EvenOdd(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements()) {
      Curve curve = (Curve)enumeration.nextElement();
      if (curve.accumulateCrossings(evenOdd))
        return null; 
    } 
    return evenOdd;
  }
  
  public static Crossings findCrossings(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    NonZero nonZero;
    paramPathIterator;
    if (paramPathIterator.getWindingRule() == 0) {
      nonZero = new EvenOdd(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    } else {
      nonZero = new NonZero(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    } 
    double[] arrayOfDouble = new double[23];
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    while (!paramPathIterator.isDone()) {
      double d6;
      double d5;
      int i = paramPathIterator.currentSegment(arrayOfDouble);
      switch (i) {
        case 0:
          if (d2 != d4 && nonZero.accumulateLine(d3, d4, d1, d2))
            return null; 
          d1 = d3 = arrayOfDouble[0];
          d2 = d4 = arrayOfDouble[1];
        case 1:
          d5 = arrayOfDouble[0];
          d6 = arrayOfDouble[1];
          if (nonZero.accumulateLine(d3, d4, d5, d6))
            return null; 
          d3 = d5;
          d4 = d6;
          break;
        case 2:
          d5 = arrayOfDouble[2];
          d6 = arrayOfDouble[3];
          if (nonZero.accumulateQuad(d3, d4, arrayOfDouble))
            return null; 
          d3 = d5;
          d4 = d6;
          break;
        case 3:
          d5 = arrayOfDouble[4];
          d6 = arrayOfDouble[5];
          if (nonZero.accumulateCubic(d3, d4, arrayOfDouble))
            return null; 
          d3 = d5;
          d4 = d6;
          break;
        case 4:
          if (d2 != d4 && nonZero.accumulateLine(d3, d4, d1, d2))
            return null; 
          d3 = d1;
          d4 = d2;
          break;
      } 
      paramPathIterator.next();
    } 
    return (d2 != d4 && nonZero.accumulateLine(d3, d4, d1, d2)) ? null : nonZero;
  }
  
  public boolean accumulateLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { return (paramDouble2 <= paramDouble4) ? accumulateLine(paramDouble1, paramDouble2, paramDouble3, paramDouble4, 1) : accumulateLine(paramDouble3, paramDouble4, paramDouble1, paramDouble2, -1); }
  
  public boolean accumulateLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt) {
    double d4;
    double d3;
    double d2;
    double d1;
    if (this.yhi <= paramDouble2 || this.ylo >= paramDouble4)
      return false; 
    if (paramDouble1 >= this.xhi && paramDouble3 >= this.xhi)
      return false; 
    if (paramDouble2 == paramDouble4)
      return (paramDouble1 >= this.xlo || paramDouble3 >= this.xlo); 
    double d5 = paramDouble3 - paramDouble1;
    double d6 = paramDouble4 - paramDouble2;
    if (paramDouble2 < this.ylo) {
      d1 = paramDouble1 + (this.ylo - paramDouble2) * d5 / d6;
      d2 = this.ylo;
    } else {
      d1 = paramDouble1;
      d2 = paramDouble2;
    } 
    if (this.yhi < paramDouble4) {
      d3 = paramDouble1 + (this.yhi - paramDouble2) * d5 / d6;
      d4 = this.yhi;
    } else {
      d3 = paramDouble3;
      d4 = paramDouble4;
    } 
    if (d1 >= this.xhi && d3 >= this.xhi)
      return false; 
    if (d1 > this.xlo || d3 > this.xlo)
      return true; 
    record(d2, d4, paramInt);
    return false;
  }
  
  public boolean accumulateQuad(double paramDouble1, double paramDouble2, double[] paramArrayOfDouble) {
    if (paramDouble2 < this.ylo && paramArrayOfDouble[1] < this.ylo && paramArrayOfDouble[3] < this.ylo)
      return false; 
    if (paramDouble2 > this.yhi && paramArrayOfDouble[1] > this.yhi && paramArrayOfDouble[3] > this.yhi)
      return false; 
    if (paramDouble1 > this.xhi && paramArrayOfDouble[0] > this.xhi && paramArrayOfDouble[2] > this.xhi)
      return false; 
    if (paramDouble1 < this.xlo && paramArrayOfDouble[0] < this.xlo && paramArrayOfDouble[2] < this.xlo) {
      if (paramDouble2 < paramArrayOfDouble[3]) {
        record(Math.max(paramDouble2, this.ylo), Math.min(paramArrayOfDouble[3], this.yhi), 1);
      } else if (paramDouble2 > paramArrayOfDouble[3]) {
        record(Math.max(paramArrayOfDouble[3], this.ylo), Math.min(paramDouble2, this.yhi), -1);
      } 
      return false;
    } 
    Curve.insertQuad(this.tmp, paramDouble1, paramDouble2, paramArrayOfDouble);
    Enumeration enumeration = this.tmp.elements();
    while (enumeration.hasMoreElements()) {
      Curve curve = (Curve)enumeration.nextElement();
      if (curve.accumulateCrossings(this))
        return true; 
    } 
    this.tmp.clear();
    return false;
  }
  
  public boolean accumulateCubic(double paramDouble1, double paramDouble2, double[] paramArrayOfDouble) {
    if (paramDouble2 < this.ylo && paramArrayOfDouble[1] < this.ylo && paramArrayOfDouble[3] < this.ylo && paramArrayOfDouble[5] < this.ylo)
      return false; 
    if (paramDouble2 > this.yhi && paramArrayOfDouble[1] > this.yhi && paramArrayOfDouble[3] > this.yhi && paramArrayOfDouble[5] > this.yhi)
      return false; 
    if (paramDouble1 > this.xhi && paramArrayOfDouble[0] > this.xhi && paramArrayOfDouble[2] > this.xhi && paramArrayOfDouble[4] > this.xhi)
      return false; 
    if (paramDouble1 < this.xlo && paramArrayOfDouble[0] < this.xlo && paramArrayOfDouble[2] < this.xlo && paramArrayOfDouble[4] < this.xlo) {
      if (paramDouble2 <= paramArrayOfDouble[5]) {
        record(Math.max(paramDouble2, this.ylo), Math.min(paramArrayOfDouble[5], this.yhi), 1);
      } else {
        record(Math.max(paramArrayOfDouble[5], this.ylo), Math.min(paramDouble2, this.yhi), -1);
      } 
      return false;
    } 
    Curve.insertCubic(this.tmp, paramDouble1, paramDouble2, paramArrayOfDouble);
    Enumeration enumeration = this.tmp.elements();
    while (enumeration.hasMoreElements()) {
      Curve curve = (Curve)enumeration.nextElement();
      if (curve.accumulateCrossings(this))
        return true; 
    } 
    this.tmp.clear();
    return false;
  }
  
  public static final class EvenOdd extends Crossings {
    public EvenOdd(double param1Double1, double param1Double2, double param1Double3, double param1Double4) { super(param1Double1, param1Double2, param1Double3, param1Double4); }
    
    public final boolean covers(double param1Double1, double param1Double2) { return (this.limit == 2 && this.yranges[0] <= param1Double1 && this.yranges[1] >= param1Double2); }
    
    public void record(double param1Double1, double param1Double2, int param1Int) {
      if (param1Double1 >= param1Double2)
        return; 
      int i;
      for (i = 0; i < this.limit && param1Double1 > this.yranges[i + true]; i += true);
      int j = i;
      while (i < this.limit) {
        double d6;
        double d5;
        double d4;
        double d3;
        double d1 = this.yranges[i++];
        double d2 = this.yranges[i++];
        if (param1Double2 < d1) {
          this.yranges[j++] = param1Double1;
          this.yranges[j++] = param1Double2;
          param1Double1 = d1;
          param1Double2 = d2;
          continue;
        } 
        if (param1Double1 < d1) {
          d3 = param1Double1;
          d4 = d1;
        } else {
          d3 = d1;
          d4 = param1Double1;
        } 
        if (param1Double2 < d2) {
          d5 = param1Double2;
          d6 = d2;
        } else {
          d5 = d2;
          d6 = param1Double2;
        } 
        if (d4 == d5) {
          param1Double1 = d3;
          param1Double2 = d6;
        } else {
          if (d4 > d5) {
            param1Double1 = d5;
            d5 = d4;
            d4 = param1Double1;
          } 
          if (d3 != d4) {
            this.yranges[j++] = d3;
            this.yranges[j++] = d4;
          } 
          param1Double1 = d5;
          param1Double2 = d6;
        } 
        if (param1Double1 >= param1Double2)
          break; 
      } 
      if (j < i && i < this.limit)
        System.arraycopy(this.yranges, i, this.yranges, j, this.limit - i); 
      j += this.limit - i;
      if (param1Double1 < param1Double2) {
        if (j >= this.yranges.length) {
          double[] arrayOfDouble = new double[j + 10];
          System.arraycopy(this.yranges, 0, arrayOfDouble, 0, j);
          this.yranges = arrayOfDouble;
        } 
        this.yranges[j++] = param1Double1;
        this.yranges[j++] = param1Double2;
      } 
      this.limit = j;
    }
  }
  
  public static final class NonZero extends Crossings {
    private int[] crosscounts = new int[this.yranges.length / 2];
    
    public NonZero(double param1Double1, double param1Double2, double param1Double3, double param1Double4) { super(param1Double1, param1Double2, param1Double3, param1Double4); }
    
    public final boolean covers(double param1Double1, double param1Double2) {
      byte b = 0;
      while (b < this.limit) {
        double d1 = this.yranges[b++];
        double d2 = this.yranges[b++];
        if (param1Double1 >= d2)
          continue; 
        if (param1Double1 < d1)
          return false; 
        if (param1Double2 <= d2)
          return true; 
        param1Double1 = d2;
      } 
      return (param1Double1 >= param1Double2);
    }
    
    public void remove(int param1Int) {
      this.limit -= 2;
      int i = this.limit - param1Int;
      if (i > 0) {
        System.arraycopy(this.yranges, param1Int + 2, this.yranges, param1Int, i);
        System.arraycopy(this.crosscounts, param1Int / 2 + 1, this.crosscounts, param1Int / 2, i / 2);
      } 
    }
    
    public void insert(int param1Int1, double param1Double1, double param1Double2, int param1Int2) {
      int i = this.limit - param1Int1;
      double[] arrayOfDouble = this.yranges;
      int[] arrayOfInt = this.crosscounts;
      if (this.limit >= this.yranges.length) {
        this.yranges = new double[this.limit + 10];
        System.arraycopy(arrayOfDouble, 0, this.yranges, 0, param1Int1);
        this.crosscounts = new int[(this.limit + 10) / 2];
        System.arraycopy(arrayOfInt, 0, this.crosscounts, 0, param1Int1 / 2);
      } 
      if (i > 0) {
        System.arraycopy(arrayOfDouble, param1Int1, this.yranges, param1Int1 + 2, i);
        System.arraycopy(arrayOfInt, param1Int1 / 2, this.crosscounts, param1Int1 / 2 + 1, i / 2);
      } 
      this.yranges[param1Int1 + 0] = param1Double1;
      this.yranges[param1Int1 + 1] = param1Double2;
      this.crosscounts[param1Int1 / 2] = param1Int2;
      this.limit += 2;
    }
    
    public void record(double param1Double1, double param1Double2, int param1Int) {
      if (param1Double1 >= param1Double2)
        return; 
      byte b;
      for (b = 0; b < this.limit && param1Double1 > this.yranges[b + true]; b += true);
      if (b < this.limit) {
        int i = this.crosscounts[b / 2];
        double d1 = this.yranges[b + false];
        double d2 = this.yranges[b + true];
        if (d2 == param1Double1 && i == param1Int) {
          if (b + 2 == this.limit) {
            this.yranges[b + true] = param1Double2;
            return;
          } 
          remove(b);
          param1Double1 = d1;
          i = this.crosscounts[b / 2];
          d1 = this.yranges[b + 0];
          d2 = this.yranges[b + 1];
        } 
        if (param1Double2 < d1) {
          insert(b, param1Double1, param1Double2, param1Int);
          return;
        } 
        if (param1Double2 == d1 && i == param1Int) {
          this.yranges[b] = param1Double1;
          return;
        } 
        if (param1Double1 < d1) {
          insert(b, param1Double1, d1, param1Int);
          b += 2;
          param1Double1 = d1;
        } else if (d1 < param1Double1) {
          insert(b, d1, param1Double1, i);
          b += 2;
          d1 = param1Double1;
        } 
        int j = i + param1Int;
        double d3 = Math.min(param1Double2, d2);
        if (j == 0) {
          remove(b);
        } else {
          this.crosscounts[b / 2] = j;
          this.yranges[b++] = param1Double1;
          this.yranges[b++] = d3;
        } 
        param1Double1 = d1 = d3;
        if (d1 < d2)
          insert(b, d1, d2, i); 
      } 
      if (param1Double1 < param1Double2)
        insert(b, param1Double1, param1Double2, param1Int); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\Crossings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */