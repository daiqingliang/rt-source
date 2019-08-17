package java.awt.geom;

import java.io.Serializable;

public abstract class Ellipse2D extends RectangularShape {
  public boolean contains(double paramDouble1, double paramDouble2) {
    double d1 = getWidth();
    if (d1 <= 0.0D)
      return false; 
    double d2 = (paramDouble1 - getX()) / d1 - 0.5D;
    double d3 = getHeight();
    if (d3 <= 0.0D)
      return false; 
    double d4 = (paramDouble2 - getY()) / d3 - 0.5D;
    return (d2 * d2 + d4 * d4 < 0.25D);
  }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    double d8;
    double d7;
    if (paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    double d1 = getWidth();
    if (d1 <= 0.0D)
      return false; 
    double d2 = (paramDouble1 - getX()) / d1 - 0.5D;
    double d3 = d2 + paramDouble3 / d1;
    double d4 = getHeight();
    if (d4 <= 0.0D)
      return false; 
    double d5 = (paramDouble2 - getY()) / d4 - 0.5D;
    double d6 = d5 + paramDouble4 / d4;
    if (d2 > 0.0D) {
      d7 = d2;
    } else if (d3 < 0.0D) {
      d7 = d3;
    } else {
      d7 = 0.0D;
    } 
    if (d5 > 0.0D) {
      d8 = d5;
    } else if (d6 < 0.0D) {
      d8 = d6;
    } else {
      d8 = 0.0D;
    } 
    return (d7 * d7 + d8 * d8 < 0.25D);
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { return (contains(paramDouble1, paramDouble2) && contains(paramDouble1 + paramDouble3, paramDouble2) && contains(paramDouble1, paramDouble2 + paramDouble4) && contains(paramDouble1 + paramDouble3, paramDouble2 + paramDouble4)); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform) { return new EllipseIterator(this, paramAffineTransform); }
  
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
    if (paramObject instanceof Ellipse2D) {
      Ellipse2D ellipse2D = (Ellipse2D)paramObject;
      return (getX() == ellipse2D.getX() && getY() == ellipse2D.getY() && getWidth() == ellipse2D.getWidth() && getHeight() == ellipse2D.getHeight());
    } 
    return false;
  }
  
  public static class Double extends Ellipse2D implements Serializable {
    public double x;
    
    public double y;
    
    public double width;
    
    public double height;
    
    private static final long serialVersionUID = 5555464816372320683L;
    
    public Double() {}
    
    public Double(double param1Double1, double param1Double2, double param1Double3, double param1Double4) { setFrame(param1Double1, param1Double2, param1Double3, param1Double4); }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public double getWidth() { return this.width; }
    
    public double getHeight() { return this.height; }
    
    public boolean isEmpty() { return (this.width <= 0.0D || this.height <= 0.0D); }
    
    public void setFrame(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      this.x = param1Double1;
      this.y = param1Double2;
      this.width = param1Double3;
      this.height = param1Double4;
    }
    
    public Rectangle2D getBounds2D() { return new Rectangle2D.Double(this.x, this.y, this.width, this.height); }
  }
  
  public static class Float extends Ellipse2D implements Serializable {
    public float x;
    
    public float y;
    
    public float width;
    
    public float height;
    
    private static final long serialVersionUID = -6633761252372475977L;
    
    public Float() {}
    
    public Float(float param1Float1, float param1Float2, float param1Float3, float param1Float4) { setFrame(param1Float1, param1Float2, param1Float3, param1Float4); }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public double getWidth() { return this.width; }
    
    public double getHeight() { return this.height; }
    
    public boolean isEmpty() { return (this.width <= 0.0D || this.height <= 0.0D); }
    
    public void setFrame(float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      this.x = param1Float1;
      this.y = param1Float2;
      this.width = param1Float3;
      this.height = param1Float4;
    }
    
    public void setFrame(double param1Double1, double param1Double2, double param1Double3, double param1Double4) {
      this.x = (float)param1Double1;
      this.y = (float)param1Double2;
      this.width = (float)param1Double3;
      this.height = (float)param1Double4;
    }
    
    public Rectangle2D getBounds2D() { return new Rectangle2D.Float(this.x, this.y, this.width, this.height); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\Ellipse2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */