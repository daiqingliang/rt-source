package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Arrays;
import sun.awt.geom.Crossings;

public class Polygon implements Shape, Serializable {
  public int npoints;
  
  public int[] xpoints;
  
  public int[] ypoints;
  
  protected Rectangle bounds;
  
  private static final long serialVersionUID = -6460061437900069969L;
  
  private static final int MIN_LENGTH = 4;
  
  public Polygon() {
    this.xpoints = new int[4];
    this.ypoints = new int[4];
  }
  
  public Polygon(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    if (paramInt > paramArrayOfInt1.length || paramInt > paramArrayOfInt2.length)
      throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length"); 
    if (paramInt < 0)
      throw new NegativeArraySizeException("npoints < 0"); 
    this.npoints = paramInt;
    this.xpoints = Arrays.copyOf(paramArrayOfInt1, paramInt);
    this.ypoints = Arrays.copyOf(paramArrayOfInt2, paramInt);
  }
  
  public void reset() {
    this.npoints = 0;
    this.bounds = null;
  }
  
  public void invalidate() { this.bounds = null; }
  
  public void translate(int paramInt1, int paramInt2) {
    for (byte b = 0; b < this.npoints; b++) {
      this.xpoints[b] = this.xpoints[b] + paramInt1;
      this.ypoints[b] = this.ypoints[b] + paramInt2;
    } 
    if (this.bounds != null)
      this.bounds.translate(paramInt1, paramInt2); 
  }
  
  void calculateBounds(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    int i = Integer.MAX_VALUE;
    int j = Integer.MAX_VALUE;
    int k = Integer.MIN_VALUE;
    int m = Integer.MIN_VALUE;
    for (byte b = 0; b < paramInt; b++) {
      int n = paramArrayOfInt1[b];
      i = Math.min(i, n);
      k = Math.max(k, n);
      int i1 = paramArrayOfInt2[b];
      j = Math.min(j, i1);
      m = Math.max(m, i1);
    } 
    this.bounds = new Rectangle(i, j, k - i, m - j);
  }
  
  void updateBounds(int paramInt1, int paramInt2) {
    if (paramInt1 < this.bounds.x) {
      this.bounds.width += this.bounds.x - paramInt1;
      this.bounds.x = paramInt1;
    } else {
      this.bounds.width = Math.max(this.bounds.width, paramInt1 - this.bounds.x);
    } 
    if (paramInt2 < this.bounds.y) {
      this.bounds.height += this.bounds.y - paramInt2;
      this.bounds.y = paramInt2;
    } else {
      this.bounds.height = Math.max(this.bounds.height, paramInt2 - this.bounds.y);
    } 
  }
  
  public void addPoint(int paramInt1, int paramInt2) {
    if (this.npoints >= this.xpoints.length || this.npoints >= this.ypoints.length) {
      int i = this.npoints * 2;
      if (i < 4) {
        i = 4;
      } else if ((i & i - 1) != 0) {
        i = Integer.highestOneBit(i);
      } 
      this.xpoints = Arrays.copyOf(this.xpoints, i);
      this.ypoints = Arrays.copyOf(this.ypoints, i);
    } 
    this.xpoints[this.npoints] = paramInt1;
    this.ypoints[this.npoints] = paramInt2;
    this.npoints++;
    if (this.bounds != null)
      updateBounds(paramInt1, paramInt2); 
  }
  
  public Rectangle getBounds() { return getBoundingBox(); }
  
  @Deprecated
  public Rectangle getBoundingBox() {
    if (this.npoints == 0)
      return new Rectangle(); 
    if (this.bounds == null)
      calculateBounds(this.xpoints, this.ypoints, this.npoints); 
    return this.bounds.getBounds();
  }
  
  public boolean contains(Point paramPoint) { return contains(paramPoint.x, paramPoint.y); }
  
  public boolean contains(int paramInt1, int paramInt2) { return contains(paramInt1, paramInt2); }
  
  @Deprecated
  public boolean inside(int paramInt1, int paramInt2) { return contains(paramInt1, paramInt2); }
  
  public Rectangle2D getBounds2D() { return getBounds(); }
  
  public boolean contains(double paramDouble1, double paramDouble2) { // Byte code:
    //   0: aload_0
    //   1: getfield npoints : I
    //   4: iconst_2
    //   5: if_icmple -> 20
    //   8: aload_0
    //   9: invokevirtual getBoundingBox : ()Ljava/awt/Rectangle;
    //   12: dload_1
    //   13: dload_3
    //   14: invokevirtual contains : (DD)Z
    //   17: ifne -> 22
    //   20: iconst_0
    //   21: ireturn
    //   22: iconst_0
    //   23: istore #5
    //   25: aload_0
    //   26: getfield xpoints : [I
    //   29: aload_0
    //   30: getfield npoints : I
    //   33: iconst_1
    //   34: isub
    //   35: iaload
    //   36: istore #6
    //   38: aload_0
    //   39: getfield ypoints : [I
    //   42: aload_0
    //   43: getfield npoints : I
    //   46: iconst_1
    //   47: isub
    //   48: iaload
    //   49: istore #7
    //   51: iconst_0
    //   52: istore #10
    //   54: iload #10
    //   56: aload_0
    //   57: getfield npoints : I
    //   60: if_icmpge -> 274
    //   63: aload_0
    //   64: getfield xpoints : [I
    //   67: iload #10
    //   69: iaload
    //   70: istore #8
    //   72: aload_0
    //   73: getfield ypoints : [I
    //   76: iload #10
    //   78: iaload
    //   79: istore #9
    //   81: iload #9
    //   83: iload #7
    //   85: if_icmpne -> 91
    //   88: goto -> 260
    //   91: iload #8
    //   93: iload #6
    //   95: if_icmpge -> 116
    //   98: dload_1
    //   99: iload #6
    //   101: i2d
    //   102: dcmpl
    //   103: iflt -> 109
    //   106: goto -> 260
    //   109: iload #8
    //   111: istore #11
    //   113: goto -> 131
    //   116: dload_1
    //   117: iload #8
    //   119: i2d
    //   120: dcmpl
    //   121: iflt -> 127
    //   124: goto -> 260
    //   127: iload #6
    //   129: istore #11
    //   131: iload #9
    //   133: iload #7
    //   135: if_icmpge -> 188
    //   138: dload_3
    //   139: iload #9
    //   141: i2d
    //   142: dcmpg
    //   143: iflt -> 260
    //   146: dload_3
    //   147: iload #7
    //   149: i2d
    //   150: dcmpl
    //   151: iflt -> 157
    //   154: goto -> 260
    //   157: dload_1
    //   158: iload #11
    //   160: i2d
    //   161: dcmpg
    //   162: ifge -> 171
    //   165: iinc #5, 1
    //   168: goto -> 260
    //   171: dload_1
    //   172: iload #8
    //   174: i2d
    //   175: dsub
    //   176: dstore #12
    //   178: dload_3
    //   179: iload #9
    //   181: i2d
    //   182: dsub
    //   183: dstore #14
    //   185: goto -> 235
    //   188: dload_3
    //   189: iload #7
    //   191: i2d
    //   192: dcmpg
    //   193: iflt -> 260
    //   196: dload_3
    //   197: iload #9
    //   199: i2d
    //   200: dcmpl
    //   201: iflt -> 207
    //   204: goto -> 260
    //   207: dload_1
    //   208: iload #11
    //   210: i2d
    //   211: dcmpg
    //   212: ifge -> 221
    //   215: iinc #5, 1
    //   218: goto -> 260
    //   221: dload_1
    //   222: iload #6
    //   224: i2d
    //   225: dsub
    //   226: dstore #12
    //   228: dload_3
    //   229: iload #7
    //   231: i2d
    //   232: dsub
    //   233: dstore #14
    //   235: dload #12
    //   237: dload #14
    //   239: iload #7
    //   241: iload #9
    //   243: isub
    //   244: i2d
    //   245: ddiv
    //   246: iload #6
    //   248: iload #8
    //   250: isub
    //   251: i2d
    //   252: dmul
    //   253: dcmpg
    //   254: ifge -> 260
    //   257: iinc #5, 1
    //   260: iload #8
    //   262: istore #6
    //   264: iload #9
    //   266: istore #7
    //   268: iinc #10, 1
    //   271: goto -> 54
    //   274: iload #5
    //   276: iconst_1
    //   277: iand
    //   278: ifeq -> 285
    //   281: iconst_1
    //   282: goto -> 286
    //   285: iconst_0
    //   286: ireturn }
  
  private Crossings getCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    Crossings.EvenOdd evenOdd = new Crossings.EvenOdd(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    int i = this.xpoints[this.npoints - 1];
    int j = this.ypoints[this.npoints - 1];
    for (byte b = 0; b < this.npoints; b++) {
      int k = this.xpoints[b];
      int m = this.ypoints[b];
      if (evenOdd.accumulateLine(i, j, k, m))
        return null; 
      i = k;
      j = m;
    } 
    return evenOdd;
  }
  
  public boolean contains(Point2D paramPoint2D) { return contains(paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (this.npoints <= 0 || !getBoundingBox().intersects(paramDouble1, paramDouble2, paramDouble3, paramDouble4))
      return false; 
    Crossings crossings = getCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (crossings == null || !crossings.isEmpty());
  }
  
  public boolean intersects(Rectangle2D paramRectangle2D) { return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (this.npoints <= 0 || !getBoundingBox().intersects(paramDouble1, paramDouble2, paramDouble3, paramDouble4))
      return false; 
    Crossings crossings = getCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (crossings != null && crossings.covers(paramDouble2, paramDouble2 + paramDouble4));
  }
  
  public boolean contains(Rectangle2D paramRectangle2D) { return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform) { return new PolygonPathIterator(this, paramAffineTransform); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble) { return getPathIterator(paramAffineTransform); }
  
  class PolygonPathIterator implements PathIterator {
    Polygon poly;
    
    AffineTransform transform;
    
    int index;
    
    public PolygonPathIterator(Polygon param1Polygon1, AffineTransform param1AffineTransform) {
      this.poly = param1Polygon1;
      this.transform = param1AffineTransform;
      if (param1Polygon1.npoints == 0)
        this.index = 1; 
    }
    
    public int getWindingRule() { return 0; }
    
    public boolean isDone() { return (this.index > this.poly.npoints); }
    
    public void next() { this.index++; }
    
    public int currentSegment(float[] param1ArrayOfFloat) {
      if (this.index >= this.poly.npoints)
        return 4; 
      param1ArrayOfFloat[0] = this.poly.xpoints[this.index];
      param1ArrayOfFloat[1] = this.poly.ypoints[this.index];
      if (this.transform != null)
        this.transform.transform(param1ArrayOfFloat, 0, param1ArrayOfFloat, 0, 1); 
      return (this.index == 0) ? 0 : 1;
    }
    
    public int currentSegment(double[] param1ArrayOfDouble) {
      if (this.index >= this.poly.npoints)
        return 4; 
      param1ArrayOfDouble[0] = this.poly.xpoints[this.index];
      param1ArrayOfDouble[1] = this.poly.ypoints[this.index];
      if (this.transform != null)
        this.transform.transform(param1ArrayOfDouble, 0, param1ArrayOfDouble, 0, 1); 
      return (this.index == 0) ? 0 : 1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Polygon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */