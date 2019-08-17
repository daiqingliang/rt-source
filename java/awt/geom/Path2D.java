package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import sun.awt.geom.Curve;

public abstract class Path2D implements Shape, Cloneable {
  public static final int WIND_EVEN_ODD = 0;
  
  public static final int WIND_NON_ZERO = 1;
  
  private static final byte SEG_MOVETO = 0;
  
  private static final byte SEG_LINETO = 1;
  
  private static final byte SEG_QUADTO = 2;
  
  private static final byte SEG_CUBICTO = 3;
  
  private static final byte SEG_CLOSE = 4;
  
  byte[] pointTypes;
  
  int numTypes;
  
  int numCoords;
  
  int windingRule;
  
  static final int INIT_SIZE = 20;
  
  static final int EXPAND_MAX = 500;
  
  static final int EXPAND_MAX_COORDS = 1000;
  
  static final int EXPAND_MIN = 10;
  
  private static final byte SERIAL_STORAGE_FLT_ARRAY = 48;
  
  private static final byte SERIAL_STORAGE_DBL_ARRAY = 49;
  
  private static final byte SERIAL_SEG_FLT_MOVETO = 64;
  
  private static final byte SERIAL_SEG_FLT_LINETO = 65;
  
  private static final byte SERIAL_SEG_FLT_QUADTO = 66;
  
  private static final byte SERIAL_SEG_FLT_CUBICTO = 67;
  
  private static final byte SERIAL_SEG_DBL_MOVETO = 80;
  
  private static final byte SERIAL_SEG_DBL_LINETO = 81;
  
  private static final byte SERIAL_SEG_DBL_QUADTO = 82;
  
  private static final byte SERIAL_SEG_DBL_CUBICTO = 83;
  
  private static final byte SERIAL_SEG_CLOSE = 96;
  
  private static final byte SERIAL_PATH_END = 97;
  
  Path2D() {}
  
  Path2D(int paramInt1, int paramInt2) {
    setWindingRule(paramInt1);
    this.pointTypes = new byte[paramInt2];
  }
  
  abstract float[] cloneCoordsFloat(AffineTransform paramAffineTransform);
  
  abstract double[] cloneCoordsDouble(AffineTransform paramAffineTransform);
  
  abstract void append(float paramFloat1, float paramFloat2);
  
  abstract void append(double paramDouble1, double paramDouble2);
  
  abstract Point2D getPoint(int paramInt);
  
  abstract void needRoom(boolean paramBoolean, int paramInt);
  
  abstract int pointCrossings(double paramDouble1, double paramDouble2);
  
  abstract int rectCrossings(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  static byte[] expandPointTypes(byte[] paramArrayOfByte, int paramInt) {
    int i = paramArrayOfByte.length;
    int j = i + paramInt;
    if (j < i)
      throw new ArrayIndexOutOfBoundsException("pointTypes exceeds maximum capacity !"); 
    int k = i;
    if (k > 500) {
      k = Math.max(500, i >> 3);
    } else if (k < 10) {
      k = 10;
    } 
    assert k > 0;
    int m = i + k;
    if (m < j)
      m = Integer.MAX_VALUE; 
    while (true) {
      try {
        return Arrays.copyOf(paramArrayOfByte, m);
      } catch (OutOfMemoryError outOfMemoryError) {
        if (m == j)
          throw outOfMemoryError; 
        m = j + (m - j) / 2;
      } 
    } 
  }
  
  public abstract void moveTo(double paramDouble1, double paramDouble2);
  
  public abstract void lineTo(double paramDouble1, double paramDouble2);
  
  public abstract void quadTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  public abstract void curveTo(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public final void closePath() {
    if (this.numTypes == 0 || this.pointTypes[this.numTypes - 1] != 4) {
      needRoom(true, 0);
      this.pointTypes[this.numTypes++] = 4;
    } 
  }
  
  public final void append(Shape paramShape, boolean paramBoolean) { append(paramShape.getPathIterator(null), paramBoolean); }
  
  public abstract void append(PathIterator paramPathIterator, boolean paramBoolean);
  
  public final int getWindingRule() { return this.windingRule; }
  
  public final void setWindingRule(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO"); 
    this.windingRule = paramInt;
  }
  
  public final Point2D getCurrentPoint() {
    int i = this.numCoords;
    if (this.numTypes < 1 || i < 1)
      return null; 
    if (this.pointTypes[this.numTypes - 1] == 4)
      for (int j = this.numTypes - 2; j > 0; j--) {
        switch (this.pointTypes[j]) {
          case 0:
            break;
          case 1:
            i -= 2;
            break;
          case 2:
            i -= 4;
            break;
          case 3:
            i -= 6;
            break;
        } 
      }  
    return getPoint(i - 2);
  }
  
  public final void reset() { this.numTypes = this.numCoords = 0; }
  
  public abstract void transform(AffineTransform paramAffineTransform);
  
  public final Shape createTransformedShape(AffineTransform paramAffineTransform) {
    Path2D path2D = (Path2D)clone();
    if (paramAffineTransform != null)
      path2D.transform(paramAffineTransform); 
    return path2D;
  }
  
  public final Rectangle getBounds() { return getBounds2D().getBounds(); }
  
  public static boolean contains(PathIterator paramPathIterator, double paramDouble1, double paramDouble2) {
    if (paramDouble1 * 0.0D + paramDouble2 * 0.0D == 0.0D) {
      int i = (paramPathIterator.getWindingRule() == 1) ? -1 : 1;
      int j = Curve.pointCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2);
      return ((j & i) != 0);
    } 
    return false;
  }
  
  public static boolean contains(PathIterator paramPathIterator, Point2D paramPoint2D) { return contains(paramPathIterator, paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public final boolean contains(double paramDouble1, double paramDouble2) {
    if (paramDouble1 * 0.0D + paramDouble2 * 0.0D == 0.0D) {
      if (this.numTypes < 2)
        return false; 
      int i = (this.windingRule == 1) ? -1 : 1;
      return ((pointCrossings(paramDouble1, paramDouble2) & i) != 0);
    } 
    return false;
  }
  
  public final boolean contains(Point2D paramPoint2D) { return contains(paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public static boolean contains(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    int i = (paramPathIterator.getWindingRule() == 1) ? -1 : 2;
    int j = Curve.rectCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j != Integer.MIN_VALUE && (j & i) != 0);
  }
  
  public static boolean contains(PathIterator paramPathIterator, Rectangle2D paramRectangle2D) { return contains(paramPathIterator, paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public final boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    int i = (this.windingRule == 1) ? -1 : 2;
    int j = rectCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j != Integer.MIN_VALUE && (j & i) != 0);
  }
  
  public final boolean contains(Rectangle2D paramRectangle2D) { return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public static boolean intersects(PathIterator paramPathIterator, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    int i = (paramPathIterator.getWindingRule() == 1) ? -1 : 2;
    int j = Curve.rectCrossingsForPath(paramPathIterator, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j == Integer.MIN_VALUE || (j & i) != 0);
  }
  
  public static boolean intersects(PathIterator paramPathIterator, Rectangle2D paramRectangle2D) { return intersects(paramPathIterator, paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public final boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (Double.isNaN(paramDouble1 + paramDouble3) || Double.isNaN(paramDouble2 + paramDouble4))
      return false; 
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    int i = (this.windingRule == 1) ? -1 : 2;
    int j = rectCrossings(paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (j == Integer.MIN_VALUE || (j & i) != 0);
  }
  
  public final boolean intersects(Rectangle2D paramRectangle2D) { return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public final PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble) { return new FlatteningPathIterator(getPathIterator(paramAffineTransform), paramDouble); }
  
  public abstract Object clone();
  
  final void writeObject(ObjectOutputStream paramObjectOutputStream, boolean paramBoolean) throws IOException {
    Object object;
    float[] arrayOfFloat;
    paramObjectOutputStream.defaultWriteObject();
    if (paramBoolean) {
      object = ((Double)this).doubleCoords;
      arrayOfFloat = null;
    } else {
      arrayOfFloat = ((Float)this).floatCoords;
      object = null;
    } 
    int i = this.numTypes;
    paramObjectOutputStream.writeByte(paramBoolean ? 49 : 48);
    paramObjectOutputStream.writeInt(i);
    paramObjectOutputStream.writeInt(this.numCoords);
    paramObjectOutputStream.writeByte((byte)this.windingRule);
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      byte b4;
      byte b3;
      switch (this.pointTypes[b2]) {
        case 0:
          b3 = 1;
          b4 = paramBoolean ? 80 : 64;
          break;
        case 1:
          b3 = 1;
          b4 = paramBoolean ? 81 : 65;
          break;
        case 2:
          b3 = 2;
          b4 = paramBoolean ? 82 : 66;
          break;
        case 3:
          b3 = 3;
          b4 = paramBoolean ? 83 : 67;
          break;
        case 4:
          b3 = 0;
          b4 = 96;
          break;
        default:
          throw new InternalError("unrecognized path type");
      } 
      paramObjectOutputStream.writeByte(b4);
      while (--b3 >= 0) {
        if (paramBoolean) {
          paramObjectOutputStream.writeDouble(object[b1++]);
          paramObjectOutputStream.writeDouble(object[b1++]);
          continue;
        } 
        paramObjectOutputStream.writeFloat(arrayOfFloat[b1++]);
        paramObjectOutputStream.writeFloat(arrayOfFloat[b1++]);
      } 
    } 
    paramObjectOutputStream.writeByte(97);
  }
  
  final void readObject(ObjectInputStream paramObjectInputStream, boolean paramBoolean) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    paramObjectInputStream.readByte();
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    try {
      setWindingRule(paramObjectInputStream.readByte());
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException(illegalArgumentException.getMessage());
    } 
    this.pointTypes = new byte[(i < 0 || i > 20) ? 20 : i];
    if (j < 0 || j > 40)
      j = 40; 
    if (paramBoolean) {
      ((Double)this).doubleCoords = new double[j];
    } else {
      ((Float)this).floatCoords = new float[j];
    } 
    for (byte b = 0; i < 0 || b < i; b++) {
      byte b2;
      byte b1;
      boolean bool;
      byte b3 = paramObjectInputStream.readByte();
      switch (b3) {
        case 64:
          bool = false;
          b1 = 1;
          b2 = 0;
          break;
        case 65:
          bool = false;
          b1 = 1;
          b2 = 1;
          break;
        case 66:
          bool = false;
          b1 = 2;
          b2 = 2;
          break;
        case 67:
          bool = false;
          b1 = 3;
          b2 = 3;
          break;
        case 80:
          bool = true;
          b1 = 1;
          b2 = 0;
          break;
        case 81:
          bool = true;
          b1 = 1;
          b2 = 1;
          break;
        case 82:
          bool = true;
          b1 = 2;
          b2 = 2;
          break;
        case 83:
          bool = true;
          b1 = 3;
          b2 = 3;
          break;
        case 96:
          bool = false;
          b1 = 0;
          b2 = 4;
          break;
        case 97:
          if (i < 0)
            break; 
          throw new StreamCorruptedException("unexpected PATH_END");
        default:
          throw new StreamCorruptedException("unrecognized path type");
      } 
      needRoom((b2 != 0), b1 * 2);
      if (bool) {
        while (--b1 >= 0)
          append(paramObjectInputStream.readDouble(), paramObjectInputStream.readDouble()); 
      } else {
        while (--b1 >= 0)
          append(paramObjectInputStream.readFloat(), paramObjectInputStream.readFloat()); 
      } 
      this.pointTypes[this.numTypes++] = b2;
    } 
    if (i >= 0 && paramObjectInputStream.readByte() != 97)
      throw new StreamCorruptedException("missing PATH_END"); 
  }
  
  public static class Double extends Path2D implements Serializable {
    double[] doubleCoords;
    
    private static final long serialVersionUID = 1826762518450014216L;
    
    public Double() { this(1, 20); }
    
    public Double(int param1Int) { this(param1Int, 20); }
    
    public Double(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
      this.doubleCoords = new double[param1Int2 * 2];
    }
    
    public Double(Shape param1Shape) { this(param1Shape, null); }
    
    public Double(Shape param1Shape, AffineTransform param1AffineTransform) {
      if (param1Shape instanceof Path2D) {
        Path2D path2D = (Path2D)param1Shape;
        setWindingRule(path2D.windingRule);
        this.numTypes = path2D.numTypes;
        this.pointTypes = Arrays.copyOf(path2D.pointTypes, path2D.numTypes);
        this.numCoords = path2D.numCoords;
        this.doubleCoords = path2D.cloneCoordsDouble(param1AffineTransform);
      } else {
        PathIterator pathIterator = param1Shape.getPathIterator(param1AffineTransform);
        setWindingRule(pathIterator.getWindingRule());
        this.pointTypes = new byte[20];
        this.doubleCoords = new double[40];
        append(pathIterator, false);
      } 
    }
    
    float[] cloneCoordsFloat(AffineTransform param1AffineTransform) {
      float[] arrayOfFloat = new float[this.numCoords];
      if (param1AffineTransform == null) {
        for (byte b = 0; b < this.numCoords; b++)
          arrayOfFloat[b] = (float)this.doubleCoords[b]; 
      } else {
        param1AffineTransform.transform(this.doubleCoords, 0, arrayOfFloat, 0, this.numCoords / 2);
      } 
      return arrayOfFloat;
    }
    
    double[] cloneCoordsDouble(AffineTransform param1AffineTransform) {
      double[] arrayOfDouble;
      if (param1AffineTransform == null) {
        arrayOfDouble = Arrays.copyOf(this.doubleCoords, this.numCoords);
      } else {
        arrayOfDouble = new double[this.numCoords];
        param1AffineTransform.transform(this.doubleCoords, 0, arrayOfDouble, 0, this.numCoords / 2);
      } 
      return arrayOfDouble;
    }
    
    void append(float param1Float1, float param1Float2) {
      this.doubleCoords[this.numCoords++] = param1Float1;
      this.doubleCoords[this.numCoords++] = param1Float2;
    }
    
    void append(double param1Double1, double param1Double2) {
      this.doubleCoords[this.numCoords++] = param1Double1;
      this.doubleCoords[this.numCoords++] = param1Double2;
    }
    
    Point2D getPoint(int param1Int) { return new Point2D.Double(this.doubleCoords[param1Int], this.doubleCoords[param1Int + 1]); }
    
    void needRoom(boolean param1Boolean, int param1Int) {
      if (this.numTypes == 0 && param1Boolean)
        throw new IllegalPathStateException("missing initial moveto in path definition"); 
      if (this.numTypes >= this.pointTypes.length)
        this.pointTypes = expandPointTypes(this.pointTypes, 1); 
      if (this.numCoords > this.doubleCoords.length - param1Int)
        this.doubleCoords = expandCoords(this.doubleCoords, param1Int); 
    }
    
    static double[] expandCoords(double[] param1ArrayOfDouble, int param1Int) {
      int i = param1ArrayOfDouble.length;
      int j = i + param1Int;
      if (j < i)
        throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !"); 
      int k = i;
      if (k > 1000) {
        k = Math.max(1000, i >> 3);
      } else if (k < 10) {
        k = 10;
      } 
      assert k > param1Int;
      int m = i + k;
      if (m < j)
        m = Integer.MAX_VALUE; 
      while (true) {
        try {
          return Arrays.copyOf(param1ArrayOfDouble, m);
        } catch (OutOfMemoryError outOfMemoryError) {
          if (m == j)
            throw outOfMemoryError; 
          m = j + (m - j) / 2;
        } 
      } 
    }
    
    public final void moveTo(double param1Double1, double param1Double2) {
      if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
        this.doubleCoords[this.numCoords - 2] = param1Double1;
        this.doubleCoords[this.numCoords - 1] = param1Double2;
      } else {
        needRoom(false, 2);
        this.pointTypes[this.numTypes++] = 0;
        this.doubleCoords[this.numCoords++] = param1Double1;
        this.doubleCoords[this.numCoords++] = param1Double2;
      } 
    }
    
    public final void lineTo(double param1Double1, double param1Double2) {
      needRoom(true, 2);
      this.pointTypes[this.numTypes++] = 1;
      this.doubleCoords[this.numCoords++] = param1Double1;
      this.doubleCoords[this.numCoords++] = param1Double2;
    }
    
    public final void quadTo(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      needRoom(true, 4);
      this.pointTypes[this.numTypes++] = 2;
      this.doubleCoords[this.numCoords++] = param1Double1;
      this.doubleCoords[this.numCoords++] = param1Double2;
      this.doubleCoords[this.numCoords++] = param1Double3;
      this.doubleCoords[this.numCoords++] = param1Double4;
    }
    
    public final void curveTo(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      needRoom(true, 6);
      this.pointTypes[this.numTypes++] = 3;
      this.doubleCoords[this.numCoords++] = param1Double1;
      this.doubleCoords[this.numCoords++] = param1Double2;
      this.doubleCoords[this.numCoords++] = param1Double3;
      this.doubleCoords[this.numCoords++] = param1Double4;
      this.doubleCoords[this.numCoords++] = param1Double5;
      this.doubleCoords[this.numCoords++] = param1Double6;
    }
    
    int pointCrossings(double param1Double1, double param1Double2) {
      if (this.numTypes == 0)
        return 0; 
      double[] arrayOfDouble = this.doubleCoords;
      double d1 = arrayOfDouble[0];
      double d3 = d1;
      double d2 = arrayOfDouble[1];
      double d4 = d2;
      int i = 0;
      byte b1 = 2;
      for (byte b2 = 1; b2 < this.numTypes; b2++) {
        double d6;
        double d5;
        switch (this.pointTypes[b2]) {
          case 0:
            if (d4 != d2)
              i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
            d1 = d3 = arrayOfDouble[b1++];
            d2 = d4 = arrayOfDouble[b1++];
          case 1:
            i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d5 = arrayOfDouble[b1++], d6 = arrayOfDouble[b1++]);
            d3 = d5;
            d4 = d6;
          case 2:
            i += Curve.pointCrossingsForQuad(param1Double1, param1Double2, d3, d4, arrayOfDouble[b1++], arrayOfDouble[b1++], d5 = arrayOfDouble[b1++], d6 = arrayOfDouble[b1++], 0);
            d3 = d5;
            d4 = d6;
          case 3:
            i += Curve.pointCrossingsForCubic(param1Double1, param1Double2, d3, d4, arrayOfDouble[b1++], arrayOfDouble[b1++], arrayOfDouble[b1++], arrayOfDouble[b1++], d5 = arrayOfDouble[b1++], d6 = arrayOfDouble[b1++], 0);
            d3 = d5;
            d4 = d6;
          case 4:
            if (d4 != d2)
              i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
            d3 = d1;
            d4 = d2;
            break;
        } 
      } 
      if (d4 != d2)
        i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
      return i;
    }
    
    int rectCrossings(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      if (this.numTypes == 0)
        return 0; 
      double[] arrayOfDouble = this.doubleCoords;
      double d3 = arrayOfDouble[0];
      double d1 = d3;
      double d4 = arrayOfDouble[1];
      double d2 = d4;
      int i = 0;
      byte b1 = 2;
      for (byte b2 = 1; i != Integer.MIN_VALUE && b2 < this.numTypes; b2++) {
        double d6;
        double d5;
        switch (this.pointTypes[b2]) {
          case 0:
            if (d1 != d3 || d2 != d4)
              i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
            d3 = d1 = arrayOfDouble[b1++];
            d4 = d2 = arrayOfDouble[b1++];
          case 1:
            d5 = arrayOfDouble[b1++];
            d6 = arrayOfDouble[b1++];
            i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d5, d6);
            d1 = d5;
            d2 = d6;
            break;
          case 2:
            i = Curve.rectCrossingsForQuad(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, arrayOfDouble[b1++], arrayOfDouble[b1++], d5 = arrayOfDouble[b1++], d6 = arrayOfDouble[b1++], 0);
            d1 = d5;
            d2 = d6;
          case 3:
            i = Curve.rectCrossingsForCubic(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, arrayOfDouble[b1++], arrayOfDouble[b1++], arrayOfDouble[b1++], arrayOfDouble[b1++], d5 = arrayOfDouble[b1++], d6 = arrayOfDouble[b1++], 0);
            d1 = d5;
            d2 = d6;
          case 4:
            if (d1 != d3 || d2 != d4)
              i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
            d1 = d3;
            d2 = d4;
            break;
        } 
      } 
      if (i != Integer.MIN_VALUE && (d1 != d3 || d2 != d4))
        i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
      return i;
    }
    
    public final void append(PathIterator param1PathIterator, boolean param1Boolean) {
      double[] arrayOfDouble = new double[6];
      while (!param1PathIterator.isDone()) {
        switch (param1PathIterator.currentSegment(arrayOfDouble)) {
          case 0:
            if (!param1Boolean || this.numTypes < 1 || this.numCoords < 1) {
              moveTo(arrayOfDouble[0], arrayOfDouble[1]);
              break;
            } 
            if (this.pointTypes[this.numTypes - 1] != 4 && this.doubleCoords[this.numCoords - 2] == arrayOfDouble[0] && this.doubleCoords[this.numCoords - 1] == arrayOfDouble[1])
              break; 
            lineTo(arrayOfDouble[0], arrayOfDouble[1]);
            break;
          case 1:
            lineTo(arrayOfDouble[0], arrayOfDouble[1]);
            break;
          case 2:
            quadTo(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3]);
            break;
          case 3:
            curveTo(arrayOfDouble[0], arrayOfDouble[1], arrayOfDouble[2], arrayOfDouble[3], arrayOfDouble[4], arrayOfDouble[5]);
            break;
          case 4:
            closePath();
            break;
        } 
        param1PathIterator.next();
        param1Boolean = false;
      } 
    }
    
    public final void transform(AffineTransform param1AffineTransform) { param1AffineTransform.transform(this.doubleCoords, 0, this.doubleCoords, 0, this.numCoords / 2); }
    
    public final Rectangle2D getBounds2D() {
      double d4;
      double d3;
      double d2;
      double d1;
      int i = this.numCoords;
      if (i > 0) {
        d2 = d4 = this.doubleCoords[--i];
        d1 = d3 = this.doubleCoords[--i];
        while (i > 0) {
          double d5 = this.doubleCoords[--i];
          double d6 = this.doubleCoords[--i];
          if (d6 < d1)
            d1 = d6; 
          if (d5 < d2)
            d2 = d5; 
          if (d6 > d3)
            d3 = d6; 
          if (d5 > d4)
            d4 = d5; 
        } 
      } else {
        d1 = d2 = d3 = d4 = 0.0D;
      } 
      return new Rectangle2D.Double(d1, d2, d3 - d1, d4 - d2);
    }
    
    public final PathIterator getPathIterator(AffineTransform param1AffineTransform) { return (param1AffineTransform == null) ? new CopyIterator(this) : new TxIterator(this, param1AffineTransform); }
    
    public final Object clone() { return new Double(this); }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException { writeObject(param1ObjectOutputStream, true); }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException { readObject(param1ObjectInputStream, true); }
    
    static class CopyIterator extends Path2D.Iterator {
      double[] doubleCoords;
      
      CopyIterator(Path2D.Double param2Double) {
        super(param2Double);
        this.doubleCoords = param2Double.doubleCoords;
      }
      
      public int currentSegment(float[] param2ArrayOfFloat) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          for (int j = 0; j < i; j++)
            param2ArrayOfFloat[j] = (float)this.doubleCoords[this.pointIdx + j];  
        return b;
      }
      
      public int currentSegment(double[] param2ArrayOfDouble) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          System.arraycopy(this.doubleCoords, this.pointIdx, param2ArrayOfDouble, 0, i); 
        return b;
      }
    }
    
    static class TxIterator extends Path2D.Iterator {
      double[] doubleCoords;
      
      AffineTransform affine;
      
      TxIterator(Path2D.Double param2Double, AffineTransform param2AffineTransform) {
        super(param2Double);
        this.doubleCoords = param2Double.doubleCoords;
        this.affine = param2AffineTransform;
      }
      
      public int currentSegment(float[] param2ArrayOfFloat) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          this.affine.transform(this.doubleCoords, this.pointIdx, param2ArrayOfFloat, 0, i / 2); 
        return b;
      }
      
      public int currentSegment(double[] param2ArrayOfDouble) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          this.affine.transform(this.doubleCoords, this.pointIdx, param2ArrayOfDouble, 0, i / 2); 
        return b;
      }
    }
  }
  
  public static class Float extends Path2D implements Serializable {
    float[] floatCoords;
    
    private static final long serialVersionUID = 6990832515060788886L;
    
    public Float() { this(1, 20); }
    
    public Float(int param1Int) { this(param1Int, 20); }
    
    public Float(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
      this.floatCoords = new float[param1Int2 * 2];
    }
    
    public Float(Shape param1Shape) { this(param1Shape, null); }
    
    public Float(Shape param1Shape, AffineTransform param1AffineTransform) {
      if (param1Shape instanceof Path2D) {
        Path2D path2D = (Path2D)param1Shape;
        setWindingRule(path2D.windingRule);
        this.numTypes = path2D.numTypes;
        this.pointTypes = Arrays.copyOf(path2D.pointTypes, path2D.numTypes);
        this.numCoords = path2D.numCoords;
        this.floatCoords = path2D.cloneCoordsFloat(param1AffineTransform);
      } else {
        PathIterator pathIterator = param1Shape.getPathIterator(param1AffineTransform);
        setWindingRule(pathIterator.getWindingRule());
        this.pointTypes = new byte[20];
        this.floatCoords = new float[40];
        append(pathIterator, false);
      } 
    }
    
    float[] cloneCoordsFloat(AffineTransform param1AffineTransform) {
      float[] arrayOfFloat;
      if (param1AffineTransform == null) {
        arrayOfFloat = Arrays.copyOf(this.floatCoords, this.numCoords);
      } else {
        arrayOfFloat = new float[this.numCoords];
        param1AffineTransform.transform(this.floatCoords, 0, arrayOfFloat, 0, this.numCoords / 2);
      } 
      return arrayOfFloat;
    }
    
    double[] cloneCoordsDouble(AffineTransform param1AffineTransform) {
      double[] arrayOfDouble = new double[this.numCoords];
      if (param1AffineTransform == null) {
        for (byte b = 0; b < this.numCoords; b++)
          arrayOfDouble[b] = this.floatCoords[b]; 
      } else {
        param1AffineTransform.transform(this.floatCoords, 0, arrayOfDouble, 0, this.numCoords / 2);
      } 
      return arrayOfDouble;
    }
    
    void append(float param1Float1, float param1Float2) {
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
    }
    
    void append(double param1Double1, double param1Double2) {
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
    }
    
    Point2D getPoint(int param1Int) { return new Point2D.Float(this.floatCoords[param1Int], this.floatCoords[param1Int + 1]); }
    
    void needRoom(boolean param1Boolean, int param1Int) {
      if (this.numTypes == 0 && param1Boolean)
        throw new IllegalPathStateException("missing initial moveto in path definition"); 
      if (this.numTypes >= this.pointTypes.length)
        this.pointTypes = expandPointTypes(this.pointTypes, 1); 
      if (this.numCoords > this.floatCoords.length - param1Int)
        this.floatCoords = expandCoords(this.floatCoords, param1Int); 
    }
    
    static float[] expandCoords(float[] param1ArrayOfFloat, int param1Int) {
      int i = param1ArrayOfFloat.length;
      int j = i + param1Int;
      if (j < i)
        throw new ArrayIndexOutOfBoundsException("coords exceeds maximum capacity !"); 
      int k = i;
      if (k > 1000) {
        k = Math.max(1000, i >> 3);
      } else if (k < 10) {
        k = 10;
      } 
      assert k > param1Int;
      int m = i + k;
      if (m < j)
        m = Integer.MAX_VALUE; 
      while (true) {
        try {
          return Arrays.copyOf(param1ArrayOfFloat, m);
        } catch (OutOfMemoryError outOfMemoryError) {
          if (m == j)
            throw outOfMemoryError; 
          m = j + (m - j) / 2;
        } 
      } 
    }
    
    public final void moveTo(double param1Double1, double param1Double2) {
      if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
        this.floatCoords[this.numCoords - 2] = (float)param1Double1;
        this.floatCoords[this.numCoords - 1] = (float)param1Double2;
      } else {
        needRoom(false, 2);
        this.pointTypes[this.numTypes++] = 0;
        this.floatCoords[this.numCoords++] = (float)param1Double1;
        this.floatCoords[this.numCoords++] = (float)param1Double2;
      } 
    }
    
    public final void moveTo(float param1Float1, float param1Float2) {
      if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
        this.floatCoords[this.numCoords - 2] = param1Float1;
        this.floatCoords[this.numCoords - 1] = param1Float2;
      } else {
        needRoom(false, 2);
        this.pointTypes[this.numTypes++] = 0;
        this.floatCoords[this.numCoords++] = param1Float1;
        this.floatCoords[this.numCoords++] = param1Float2;
      } 
    }
    
    public final void lineTo(double param1Double1, double param1Double2) {
      needRoom(true, 2);
      this.pointTypes[this.numTypes++] = 1;
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
    }
    
    public final void lineTo(float param1Float1, float param1Float2) {
      needRoom(true, 2);
      this.pointTypes[this.numTypes++] = 1;
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
    }
    
    public final void quadTo(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      needRoom(true, 4);
      this.pointTypes[this.numTypes++] = 2;
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
      this.floatCoords[this.numCoords++] = (float)param1Double3;
      this.floatCoords[this.numCoords++] = (float)param1Double4;
    }
    
    public final void quadTo(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      needRoom(true, 4);
      this.pointTypes[this.numTypes++] = 2;
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
      this.floatCoords[this.numCoords++] = param1Float3;
      this.floatCoords[this.numCoords++] = param1Float4;
    }
    
    public final void curveTo(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      needRoom(true, 6);
      this.pointTypes[this.numTypes++] = 3;
      this.floatCoords[this.numCoords++] = (float)param1Double1;
      this.floatCoords[this.numCoords++] = (float)param1Double2;
      this.floatCoords[this.numCoords++] = (float)param1Double3;
      this.floatCoords[this.numCoords++] = (float)param1Double4;
      this.floatCoords[this.numCoords++] = (float)param1Double5;
      this.floatCoords[this.numCoords++] = (float)param1Double6;
    }
    
    public final void curveTo(float param1Float1, float param1Float2, float param1Float3, float param1Float4, float param1Float5, float param1Float6) {
      needRoom(true, 6);
      this.pointTypes[this.numTypes++] = 3;
      this.floatCoords[this.numCoords++] = param1Float1;
      this.floatCoords[this.numCoords++] = param1Float2;
      this.floatCoords[this.numCoords++] = param1Float3;
      this.floatCoords[this.numCoords++] = param1Float4;
      this.floatCoords[this.numCoords++] = param1Float5;
      this.floatCoords[this.numCoords++] = param1Float6;
    }
    
    int pointCrossings(double param1Double1, double param1Double2) {
      if (this.numTypes == 0)
        return 0; 
      float[] arrayOfFloat = this.floatCoords;
      double d1 = arrayOfFloat[0];
      double d3 = d1;
      double d2 = arrayOfFloat[1];
      double d4 = d2;
      int i = 0;
      byte b1 = 2;
      for (byte b2 = 1; b2 < this.numTypes; b2++) {
        double d6;
        double d5;
        switch (this.pointTypes[b2]) {
          case 0:
            if (d4 != d2)
              i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
            d1 = d3 = arrayOfFloat[b1++];
            d2 = d4 = arrayOfFloat[b1++];
          case 1:
            i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++]);
            d3 = d5;
            d4 = d6;
          case 2:
            i += Curve.pointCrossingsForQuad(param1Double1, param1Double2, d3, d4, arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d3 = d5;
            d4 = d6;
          case 3:
            i += Curve.pointCrossingsForCubic(param1Double1, param1Double2, d3, d4, arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d3 = d5;
            d4 = d6;
          case 4:
            if (d4 != d2)
              i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
            d3 = d1;
            d4 = d2;
            break;
        } 
      } 
      if (d4 != d2)
        i += Curve.pointCrossingsForLine(param1Double1, param1Double2, d3, d4, d1, d2); 
      return i;
    }
    
    int rectCrossings(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      if (this.numTypes == 0)
        return 0; 
      float[] arrayOfFloat = this.floatCoords;
      double d3 = arrayOfFloat[0];
      double d1 = d3;
      double d4 = arrayOfFloat[1];
      double d2 = d4;
      int i = 0;
      byte b1 = 2;
      for (byte b2 = 1; i != Integer.MIN_VALUE && b2 < this.numTypes; b2++) {
        double d6;
        double d5;
        switch (this.pointTypes[b2]) {
          case 0:
            if (d1 != d3 || d2 != d4)
              i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
            d3 = d1 = arrayOfFloat[b1++];
            d4 = d2 = arrayOfFloat[b1++];
          case 1:
            i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++]);
            d1 = d5;
            d2 = d6;
          case 2:
            i = Curve.rectCrossingsForQuad(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d1 = d5;
            d2 = d6;
          case 3:
            i = Curve.rectCrossingsForCubic(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], arrayOfFloat[b1++], d5 = arrayOfFloat[b1++], d6 = arrayOfFloat[b1++], 0);
            d1 = d5;
            d2 = d6;
          case 4:
            if (d1 != d3 || d2 != d4)
              i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
            d1 = d3;
            d2 = d4;
            break;
        } 
      } 
      if (i != Integer.MIN_VALUE && (d1 != d3 || d2 != d4))
        i = Curve.rectCrossingsForLine(i, param1Double1, param1Double2, param1Double3, param1Double4, d1, d2, d3, d4); 
      return i;
    }
    
    public final void append(PathIterator param1PathIterator, boolean param1Boolean) {
      float[] arrayOfFloat = new float[6];
      while (!param1PathIterator.isDone()) {
        switch (param1PathIterator.currentSegment(arrayOfFloat)) {
          case 0:
            if (!param1Boolean || this.numTypes < 1 || this.numCoords < 1) {
              moveTo(arrayOfFloat[0], arrayOfFloat[1]);
              break;
            } 
            if (this.pointTypes[this.numTypes - 1] != 4 && this.floatCoords[this.numCoords - 2] == arrayOfFloat[0] && this.floatCoords[this.numCoords - 1] == arrayOfFloat[1])
              break; 
            lineTo(arrayOfFloat[0], arrayOfFloat[1]);
            break;
          case 1:
            lineTo(arrayOfFloat[0], arrayOfFloat[1]);
            break;
          case 2:
            quadTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
            break;
          case 3:
            curveTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
            break;
          case 4:
            closePath();
            break;
        } 
        param1PathIterator.next();
        param1Boolean = false;
      } 
    }
    
    public final void transform(AffineTransform param1AffineTransform) { param1AffineTransform.transform(this.floatCoords, 0, this.floatCoords, 0, this.numCoords / 2); }
    
    public final Rectangle2D getBounds2D() {
      float f4;
      float f3;
      float f2;
      float f1;
      int i = this.numCoords;
      if (i > 0) {
        f2 = f4 = this.floatCoords[--i];
        f1 = f3 = this.floatCoords[--i];
        while (i > 0) {
          float f5 = this.floatCoords[--i];
          float f6 = this.floatCoords[--i];
          if (f6 < f1)
            f1 = f6; 
          if (f5 < f2)
            f2 = f5; 
          if (f6 > f3)
            f3 = f6; 
          if (f5 > f4)
            f4 = f5; 
        } 
      } else {
        f1 = f2 = f3 = f4 = 0.0F;
      } 
      return new Rectangle2D.Float(f1, f2, f3 - f1, f4 - f2);
    }
    
    public final PathIterator getPathIterator(AffineTransform param1AffineTransform) { return (param1AffineTransform == null) ? new CopyIterator(this) : new TxIterator(this, param1AffineTransform); }
    
    public final Object clone() { return (this instanceof GeneralPath) ? new GeneralPath(this) : new Float(this); }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException { writeObject(param1ObjectOutputStream, false); }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws ClassNotFoundException, IOException { readObject(param1ObjectInputStream, false); }
    
    static class CopyIterator extends Path2D.Iterator {
      float[] floatCoords;
      
      CopyIterator(Path2D.Float param2Float) {
        super(param2Float);
        this.floatCoords = param2Float.floatCoords;
      }
      
      public int currentSegment(float[] param2ArrayOfFloat) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          System.arraycopy(this.floatCoords, this.pointIdx, param2ArrayOfFloat, 0, i); 
        return b;
      }
      
      public int currentSegment(double[] param2ArrayOfDouble) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          for (int j = 0; j < i; j++)
            param2ArrayOfDouble[j] = this.floatCoords[this.pointIdx + j];  
        return b;
      }
    }
    
    static class TxIterator extends Path2D.Iterator {
      float[] floatCoords;
      
      AffineTransform affine;
      
      TxIterator(Path2D.Float param2Float, AffineTransform param2AffineTransform) {
        super(param2Float);
        this.floatCoords = param2Float.floatCoords;
        this.affine = param2AffineTransform;
      }
      
      public int currentSegment(float[] param2ArrayOfFloat) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          this.affine.transform(this.floatCoords, this.pointIdx, param2ArrayOfFloat, 0, i / 2); 
        return b;
      }
      
      public int currentSegment(double[] param2ArrayOfDouble) {
        byte b = this.path.pointTypes[this.typeIdx];
        int i = curvecoords[b];
        if (i > 0)
          this.affine.transform(this.floatCoords, this.pointIdx, param2ArrayOfDouble, 0, i / 2); 
        return b;
      }
    }
  }
  
  static abstract class Iterator implements PathIterator {
    int typeIdx;
    
    int pointIdx;
    
    Path2D path;
    
    static final int[] curvecoords = { 2, 2, 4, 6, 0 };
    
    Iterator(Path2D param1Path2D) { this.path = param1Path2D; }
    
    public int getWindingRule() { return this.path.getWindingRule(); }
    
    public boolean isDone() { return (this.typeIdx >= this.path.numTypes); }
    
    public void next() {
      byte b = this.path.pointTypes[this.typeIdx++];
      this.pointIdx += curvecoords[b];
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\Path2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */