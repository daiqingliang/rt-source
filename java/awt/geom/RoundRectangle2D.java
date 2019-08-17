package java.awt.geom;

import java.io.Serializable;

public abstract class RoundRectangle2D extends RectangularShape {
  public abstract double getArcWidth();
  
  public abstract double getArcHeight();
  
  public abstract void setRoundRect(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6);
  
  public void setRoundRect(RoundRectangle2D paramRoundRectangle2D) { setRoundRect(paramRoundRectangle2D.getX(), paramRoundRectangle2D.getY(), paramRoundRectangle2D.getWidth(), paramRoundRectangle2D.getHeight(), paramRoundRectangle2D.getArcWidth(), paramRoundRectangle2D.getArcHeight()); }
  
  public void setFrame(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { setRoundRect(paramDouble1, paramDouble2, paramDouble3, paramDouble4, getArcWidth(), getArcHeight()); }
  
  public boolean contains(double paramDouble1, double paramDouble2) {
    if (isEmpty())
      return false; 
    double d1 = getX();
    double d2 = getY();
    double d3 = d1 + getWidth();
    double d4 = d2 + getHeight();
    if (paramDouble1 < d1 || paramDouble2 < d2 || paramDouble1 >= d3 || paramDouble2 >= d4)
      return false; 
    double d5 = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0D;
    double d6 = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0D;
    if (paramDouble1 >= d1 += d5 && paramDouble1 < (d1 = d3 - d5))
      return true; 
    if (paramDouble2 >= d2 += d6 && paramDouble2 < (d2 = d4 - d6))
      return true; 
    paramDouble1 = (paramDouble1 - d1) / d5;
    paramDouble2 = (paramDouble2 - d2) / d6;
    return (paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2 <= 1.0D);
  }
  
  private int classify(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { return (paramDouble1 < paramDouble2) ? 0 : ((paramDouble1 < paramDouble2 + paramDouble4) ? 1 : ((paramDouble1 < paramDouble3 - paramDouble4) ? 2 : ((paramDouble1 < paramDouble3) ? 3 : 4))); }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (isEmpty() || paramDouble3 <= 0.0D || paramDouble4 <= 0.0D)
      return false; 
    double d1 = getX();
    double d2 = getY();
    double d3 = d1 + getWidth();
    double d4 = d2 + getHeight();
    if (paramDouble1 + paramDouble3 <= d1 || paramDouble1 >= d3 || paramDouble2 + paramDouble4 <= d2 || paramDouble2 >= d4)
      return false; 
    double d5 = Math.min(getWidth(), Math.abs(getArcWidth())) / 2.0D;
    double d6 = Math.min(getHeight(), Math.abs(getArcHeight())) / 2.0D;
    int i = classify(paramDouble1, d1, d3, d5);
    int j = classify(paramDouble1 + paramDouble3, d1, d3, d5);
    int k = classify(paramDouble2, d2, d4, d6);
    int m = classify(paramDouble2 + paramDouble4, d2, d4, d6);
    if (i == 2 || j == 2 || k == 2 || m == 2)
      return true; 
    if ((i < 2 && j > 2) || (k < 2 && m > 2))
      return true; 
    paramDouble1 = (j == 1) ? (paramDouble1 = paramDouble1 + paramDouble3 - d1 + d5) : (paramDouble1 -= d3 - d5);
    paramDouble2 = (m == 1) ? (paramDouble2 = paramDouble2 + paramDouble4 - d2 + d6) : (paramDouble2 -= d4 - d6);
    paramDouble1 /= d5;
    paramDouble2 /= d6;
    return (paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2 <= 1.0D);
  }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { return (isEmpty() || paramDouble3 <= 0.0D || paramDouble4 <= 0.0D) ? false : ((contains(paramDouble1, paramDouble2) && contains(paramDouble1 + paramDouble3, paramDouble2) && contains(paramDouble1, paramDouble2 + paramDouble4) && contains(paramDouble1 + paramDouble3, paramDouble2 + paramDouble4))); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform) { return new RoundRectIterator(this, paramAffineTransform); }
  
  public int hashCode() {
    long l = Double.doubleToLongBits(getX());
    l += Double.doubleToLongBits(getY()) * 37L;
    l += Double.doubleToLongBits(getWidth()) * 43L;
    l += Double.doubleToLongBits(getHeight()) * 47L;
    l += Double.doubleToLongBits(getArcWidth()) * 53L;
    l += Double.doubleToLongBits(getArcHeight()) * 59L;
    return (int)l ^ (int)(l >> 32);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof RoundRectangle2D) {
      RoundRectangle2D roundRectangle2D = (RoundRectangle2D)paramObject;
      return (getX() == roundRectangle2D.getX() && getY() == roundRectangle2D.getY() && getWidth() == roundRectangle2D.getWidth() && getHeight() == roundRectangle2D.getHeight() && getArcWidth() == roundRectangle2D.getArcWidth() && getArcHeight() == roundRectangle2D.getArcHeight());
    } 
    return false;
  }
  
  public static class Double extends RoundRectangle2D implements Serializable {
    public double x;
    
    public double y;
    
    public double width;
    
    public double height;
    
    public double arcwidth;
    
    public double archeight;
    
    private static final long serialVersionUID = 1048939333485206117L;
    
    public Double() {}
    
    public Double(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) { setRoundRect(param1Double1, param1Double2, param1Double3, param1Double4, param1Double5, param1Double6); }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public double getWidth() { return this.width; }
    
    public double getHeight() { return this.height; }
    
    public double getArcWidth() { return this.arcwidth; }
    
    public double getArcHeight() { return this.archeight; }
    
    public boolean isEmpty() { return (this.width <= 0.0D || this.height <= 0.0D); }
    
    public void setRoundRect(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      this.x = param1Double1;
      this.y = param1Double2;
      this.width = param1Double3;
      this.height = param1Double4;
      this.arcwidth = param1Double5;
      this.archeight = param1Double6;
    }
    
    public void setRoundRect(RoundRectangle2D param1RoundRectangle2D) {
      this.x = param1RoundRectangle2D.getX();
      this.y = param1RoundRectangle2D.getY();
      this.width = param1RoundRectangle2D.getWidth();
      this.height = param1RoundRectangle2D.getHeight();
      this.arcwidth = param1RoundRectangle2D.getArcWidth();
      this.archeight = param1RoundRectangle2D.getArcHeight();
    }
    
    public Rectangle2D getBounds2D() { return new Rectangle2D.Double(this.x, this.y, this.width, this.height); }
  }
  
  public static class Float extends RoundRectangle2D implements Serializable {
    public float x;
    
    public float y;
    
    public float width;
    
    public float height;
    
    public float arcwidth;
    
    public float archeight;
    
    private static final long serialVersionUID = -3423150618393866922L;
    
    public Float() {}
    
    public Float(float param1Float1, float param1Float2, float param1Float3, float param1Float4, float param1Float5, float param1Float6) { setRoundRect(param1Float1, param1Float2, param1Float3, param1Float4, param1Float5, param1Float6); }
    
    public double getX() { return this.x; }
    
    public double getY() { return this.y; }
    
    public double getWidth() { return this.width; }
    
    public double getHeight() { return this.height; }
    
    public double getArcWidth() { return this.arcwidth; }
    
    public double getArcHeight() { return this.archeight; }
    
    public boolean isEmpty() { return (this.width <= 0.0F || this.height <= 0.0F); }
    
    public void setRoundRect(float param1Float1, float param1Float2, float param1Float3, float param1Float4, float param1Float5, float param1Float6) {
      this.x = param1Float1;
      this.y = param1Float2;
      this.width = param1Float3;
      this.height = param1Float4;
      this.arcwidth = param1Float5;
      this.archeight = param1Float6;
    }
    
    public void setRoundRect(double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6) {
      this.x = (float)param1Double1;
      this.y = (float)param1Double2;
      this.width = (float)param1Double3;
      this.height = (float)param1Double4;
      this.arcwidth = (float)param1Double5;
      this.archeight = (float)param1Double6;
    }
    
    public void setRoundRect(RoundRectangle2D param1RoundRectangle2D) {
      this.x = (float)param1RoundRectangle2D.getX();
      this.y = (float)param1RoundRectangle2D.getY();
      this.width = (float)param1RoundRectangle2D.getWidth();
      this.height = (float)param1RoundRectangle2D.getHeight();
      this.arcwidth = (float)param1RoundRectangle2D.getArcWidth();
      this.archeight = (float)param1RoundRectangle2D.getArcHeight();
    }
    
    public Rectangle2D getBounds2D() { return new Rectangle2D.Float(this.x, this.y, this.width, this.height); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\RoundRectangle2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */