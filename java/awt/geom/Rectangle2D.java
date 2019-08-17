package java.awt.geom;

import java.io.Serializable;

public abstract class Rectangle2D extends RectangularShape {
  public static final int OUT_LEFT = 1;
  
  public static final int OUT_TOP = 2;
  
  public static final int OUT_RIGHT = 4;
  
  public static final int OUT_BOTTOM = 8;
  
  public abstract void setRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
  
  public void setRect(Rectangle2D paramRectangle2D) { setRect(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public boolean intersectsLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    int j;
    if ((j = outcode(paramDouble3, paramDouble4)) == 0)
      return true; 
    int i;
    while ((i = outcode(paramDouble1, paramDouble2)) != 0) {
      if ((i & j) != 0)
        return false; 
      if ((i & 0x5) != 0) {
        double d1 = getX();
        if ((i & 0x4) != 0)
          d1 += getWidth(); 
        paramDouble2 += (d1 - paramDouble1) * (paramDouble4 - paramDouble2) / (paramDouble3 - paramDouble1);
        paramDouble1 = d1;
        continue;
      } 
      double d = getY();
      if ((i & 0x8) != 0)
        d += getHeight(); 
      paramDouble1 += (d - paramDouble2) * (paramDouble3 - paramDouble1) / (paramDouble4 - paramDouble2);
      paramDouble2 = d;
    } 
    return true;
  }
  
  public boolean intersectsLine(Line2D paramLine2D) { return intersectsLine(paramLine2D.getX1(), paramLine2D.getY1(), paramLine2D.getX2(), paramLine2D.getY2()); }
  
  public abstract int outcode(double paramDouble1, double paramDouble2);
  
  public int outcode(Point2D paramPoint2D) { return outcode(paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public void setFrame(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { setRect(paramDouble1, paramDouble2, paramDouble3, paramDouble4); }
  
  public Rectangle2D getBounds2D() { return (Rectangle2D)clone(); }
  
  public boolean contains(double paramDouble1, double paramDouble2) {
    double d1 = getX();
    double d2 = getY();
    return (paramDouble1 >= d1 && paramDouble2 >= d2 && paramDouble1 < d1 + getWidth() && paramDouble2 < d2 + getHeight());
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (isEmpty() || paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    double d1 = getX();
    double d2 = getY();
    return (paramDouble1 + paramDouble3 > d1 && paramDouble2 + paramDouble4 > d2 && paramDouble1 < d1 + getWidth() && paramDouble2 < d2 + getHeight());
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (isEmpty() || paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    double d1 = getX();
    double d2 = getY();
    return (paramDouble1 >= d1 && paramDouble2 >= d2 && paramDouble1 + paramDouble3 <= d1 + getWidth() && paramDouble2 + paramDouble4 <= d2 + getHeight());
  }
  
  public abstract Rectangle2D createIntersection(Rectangle2D paramRectangle2D);
  
  public static void intersect(Rectangle2D paramRectangle2D1, Rectangle2D paramRectangle2D2, Rectangle2D paramRectangle2D3) {
    double d1 = Math.max(paramRectangle2D1.getMinX(), paramRectangle2D2.getMinX());
    double d2 = Math.max(paramRectangle2D1.getMinY(), paramRectangle2D2.getMinY());
    double d3 = Math.min(paramRectangle2D1.getMaxX(), paramRectangle2D2.getMaxX());
    double d4 = Math.min(paramRectangle2D1.getMaxY(), paramRectangle2D2.getMaxY());
    paramRectangle2D3.setFrame(d1, d2, d3 - d1, d4 - d2);
  }
  
  public abstract Rectangle2D createUnion(Rectangle2D paramRectangle2D);
  
  public static void union(Rectangle2D paramRectangle2D1, Rectangle2D paramRectangle2D2, Rectangle2D paramRectangle2D3) {
    double d1 = Math.min(paramRectangle2D1.getMinX(), paramRectangle2D2.getMinX());
    double d2 = Math.min(paramRectangle2D1.getMinY(), paramRectangle2D2.getMinY());
    double d3 = Math.max(paramRectangle2D1.getMaxX(), paramRectangle2D2.getMaxX());
    double d4 = Math.max(paramRectangle2D1.getMaxY(), paramRectangle2D2.getMaxY());
    paramRectangle2D3.setFrameFromDiagonal(d1, d2, d3, d4);
  }
  
  public void add(double paramDouble1, double paramDouble2) {
    double d1 = Math.min(getMinX(), paramDouble1);
    double d2 = Math.max(getMaxX(), paramDouble1);
    double d3 = Math.min(getMinY(), paramDouble2);
    double d4 = Math.max(getMaxY(), paramDouble2);
    setRect(d1, d3, d2 - d1, d4 - d3);
  }
  
  public void add(Point2D paramPoint2D) { add(paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public void add(Rectangle2D paramRectangle2D) {
    double d1 = Math.min(getMinX(), paramRectangle2D.getMinX());
    double d2 = Math.max(getMaxX(), paramRectangle2D.getMaxX());
    double d3 = Math.min(getMinY(), paramRectangle2D.getMinY());
    double d4 = Math.max(getMaxY(), paramRectangle2D.getMaxY());
    setRect(d1, d3, d2 - d1, d4 - d3);
  }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform) { return new RectIterator(this, paramAffineTransform); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble) { return new RectIterator(this, paramAffineTransform); }
  
  public int hashCode() {
    long l = Double.doubleToLongBits(getX());
    l += Double.doubleToLongBits(getY()) * 37L;
    l += Double.doubleToLongBits(getWidth()) * 43L;
    l += Double.doubleToLongBits(getHeight()) * 47L;
    return (int)l ^ (int)(l >> 32);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof Rectangle2D) {
      Rectangle2D rectangle2D = (Rectangle2D)paramObject;
      return (getX() == rectangle2D.getX() && getY() == rectangle2D.getY() && getWidth() == rectangle2D.getWidth() && getHeight() == rectangle2D.getHeight());
    } 
    return false;
  }
  
  public static class Double extends Rectangle2D implements Serializable {
    public double x;
    
    public double y;
    
    public double width;
    
    public double height;
    
    private static final long serialVersionUID = 7771313791441850493L;
    
    public Double() {}
    
    public Double(double param1Double1, double param1Double2, double param1Double3, double param1Double4) { setRect(param1Double1, param1Double2, param1Double3, param1Double4); }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public double getWidth() { return this.width; }
    
    public double getHeight() { return this.height; }
    
    public boolean isEmpty() { return (this.width <= 0.0D || this.height <= 0.0D); }
    
    public void setRect(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      this.x = param1Double1;
      this.y = param1Double2;
      this.width = param1Double3;
      this.height = param1Double4;
    }
    
    public void setRect(Rectangle2D param1Rectangle2D) {
      this.x = param1Rectangle2D.getX();
      this.y = param1Rectangle2D.getY();
      this.width = param1Rectangle2D.getWidth();
      this.height = param1Rectangle2D.getHeight();
    }
    
    public int outcode(double param1Double1, double param1Double2) {
      byte b = 0;
      if (this.width <= 0.0D) {
        b |= 0x5;
      } else if (param1Double1 < this.x) {
        b |= 0x1;
      } else if (param1Double1 > this.x + this.width) {
        b |= 0x4;
      } 
      if (this.height <= 0.0D) {
        b |= 0xA;
      } else if (param1Double2 < this.y) {
        b |= 0x2;
      } else if (param1Double2 > this.y + this.height) {
        b |= 0x8;
      } 
      return b;
    }
    
    public Rectangle2D getBounds2D() { return new Double(this.x, this.y, this.width, this.height); }
    
    public Rectangle2D createIntersection(Rectangle2D param1Rectangle2D) {
      Double double = new Double();
      Rectangle2D.intersect(this, param1Rectangle2D, double);
      return double;
    }
    
    public Rectangle2D createUnion(Rectangle2D param1Rectangle2D) {
      Double double = new Double();
      Rectangle2D.union(this, param1Rectangle2D, double);
      return double;
    }
    
    public String toString() { return getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",w=" + this.width + ",h=" + this.height + "]"; }
  }
  
  public static class Float extends Rectangle2D implements Serializable {
    public float x;
    
    public float y;
    
    public float width;
    
    public float height;
    
    private static final long serialVersionUID = 3798716824173675777L;
    
    public Float() {}
    
    public Float(float param1Float1, float param1Float2, float param1Float3, float param1Float4) { setRect(param1Float1, param1Float2, param1Float3, param1Float4); }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public double getWidth() { return this.width; }
    
    public double getHeight() { return this.height; }
    
    public boolean isEmpty() { return (this.width <= 0.0F || this.height <= 0.0F); }
    
    public void setRect(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      this.x = param1Float1;
      this.y = param1Float2;
      this.width = param1Float3;
      this.height = param1Float4;
    }
    
    public void setRect(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      this.x = (float)param1Double1;
      this.y = (float)param1Double2;
      this.width = (float)param1Double3;
      this.height = (float)param1Double4;
    }
    
    public void setRect(Rectangle2D param1Rectangle2D) {
      this.x = (float)param1Rectangle2D.getX();
      this.y = (float)param1Rectangle2D.getY();
      this.width = (float)param1Rectangle2D.getWidth();
      this.height = (float)param1Rectangle2D.getHeight();
    }
    
    public int outcode(double param1Double1, double param1Double2) {
      byte b = 0;
      if (this.width <= 0.0F) {
        b |= 0x5;
      } else if (param1Double1 < this.x) {
        b |= 0x1;
      } else if (param1Double1 > this.x + this.width) {
        b |= 0x4;
      } 
      if (this.height <= 0.0F) {
        b |= 0xA;
      } else if (param1Double2 < this.y) {
        b |= 0x2;
      } else if (param1Double2 > this.y + this.height) {
        b |= 0x8;
      } 
      return b;
    }
    
    public Rectangle2D getBounds2D() { return new Float(this.x, this.y, this.width, this.height); }
    
    public Rectangle2D createIntersection(Rectangle2D param1Rectangle2D) {
      Rectangle2D.Double double;
      if (param1Rectangle2D instanceof Float) {
        double = new Float();
      } else {
        double = new Rectangle2D.Double();
      } 
      Rectangle2D.intersect(this, param1Rectangle2D, double);
      return double;
    }
    
    public Rectangle2D createUnion(Rectangle2D param1Rectangle2D) {
      Rectangle2D.Double double;
      if (param1Rectangle2D instanceof Float) {
        double = new Float();
      } else {
        double = new Rectangle2D.Double();
      } 
      Rectangle2D.union(this, param1Rectangle2D, double);
      return double;
    }
    
    public String toString() { return getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",w=" + this.width + ",h=" + this.height + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\Rectangle2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */