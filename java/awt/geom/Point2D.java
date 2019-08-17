package java.awt.geom;

import java.io.Serializable;

public abstract class Point2D implements Cloneable {
  public abstract double getX();
  
  public abstract double getY();
  
  public abstract void setLocation(double paramDouble1, double paramDouble2);
  
  public void setLocation(Point2D paramPoint2D) { setLocation(paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public static double distanceSq(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    paramDouble1 -= paramDouble3;
    paramDouble2 -= paramDouble4;
    return paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2;
  }
  
  public static double distance(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    paramDouble1 -= paramDouble3;
    paramDouble2 -= paramDouble4;
    return Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2);
  }
  
  public double distanceSq(double paramDouble1, double paramDouble2) {
    paramDouble1 -= getX();
    paramDouble2 -= getY();
    return paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2;
  }
  
  public double distanceSq(Point2D paramPoint2D) {
    double d1 = paramPoint2D.getX() - getX();
    double d2 = paramPoint2D.getY() - getY();
    return d1 * d1 + d2 * d2;
  }
  
  public double distance(double paramDouble1, double paramDouble2) {
    paramDouble1 -= getX();
    paramDouble2 -= getY();
    return Math.sqrt(paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2);
  }
  
  public double distance(Point2D paramPoint2D) {
    double d1 = paramPoint2D.getX() - getX();
    double d2 = paramPoint2D.getY() - getY();
    return Math.sqrt(d1 * d1 + d2 * d2);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public int hashCode() {
    long l = Double.doubleToLongBits(getX());
    l ^= Double.doubleToLongBits(getY()) * 31L;
    return (int)l ^ (int)(l >> 32);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Point2D) {
      Point2D point2D = (Point2D)paramObject;
      return (getX() == point2D.getX() && getY() == point2D.getY());
    } 
    return super.equals(paramObject);
  }
  
  public static class Double extends Point2D implements Serializable {
    public double x;
    
    public double y;
    
    private static final long serialVersionUID = 6150783262733311327L;
    
    public Double() {}
    
    public Double(double param1Double1, double param1Double2) {
      this.x = param1Double1;
      this.y = param1Double2;
    }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public void setLocation(double param1Double1, double param1Double2) {
      this.x = param1Double1;
      this.y = param1Double2;
    }
    
    public String toString() { return "Point2D.Double[" + this.x + ", " + this.y + "]"; }
  }
  
  public static class Float extends Point2D implements Serializable {
    public float x;
    
    public float y;
    
    private static final long serialVersionUID = -2870572449815403710L;
    
    public Float() {}
    
    public Float(float param1Float1, float param1Float2) {
      this.x = param1Float1;
      this.y = param1Float2;
    }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public void setLocation(double param1Double1, double param1Double2) {
      this.x = (float)param1Double1;
      this.y = (float)param1Double2;
    }
    
    public void setLocation(float param1Float1, float param1Float2) {
      this.x = param1Float1;
      this.y = param1Float2;
    }
    
    public String toString() { return "Point2D.Float[" + this.x + ", " + this.y + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\Point2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */