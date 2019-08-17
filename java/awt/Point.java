package java.awt;

import java.awt.geom.Point2D;
import java.beans.Transient;
import java.io.Serializable;

public class Point extends Point2D implements Serializable {
  public int x;
  
  public int y;
  
  private static final long serialVersionUID = -5276940640259749850L;
  
  public Point() { this(0, 0); }
  
  public Point(Point paramPoint) { this(paramPoint.x, paramPoint.y); }
  
  public Point(int paramInt1, int paramInt2) {
    this.x = paramInt1;
    this.y = paramInt2;
  }
  
  public double getX() { return this.x; }
  
  public double getY() { return this.y; }
  
  @Transient
  public Point getLocation() { return new Point(this.x, this.y); }
  
  public void setLocation(Point paramPoint) { setLocation(paramPoint.x, paramPoint.y); }
  
  public void setLocation(int paramInt1, int paramInt2) { move(paramInt1, paramInt2); }
  
  public void setLocation(double paramDouble1, double paramDouble2) {
    this.x = (int)Math.floor(paramDouble1 + 0.5D);
    this.y = (int)Math.floor(paramDouble2 + 0.5D);
  }
  
  public void move(int paramInt1, int paramInt2) {
    this.x = paramInt1;
    this.y = paramInt2;
  }
  
  public void translate(int paramInt1, int paramInt2) {
    this.x += paramInt1;
    this.y += paramInt2;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Point) {
      Point point = (Point)paramObject;
      return (this.x == point.x && this.y == point.y);
    } 
    return super.equals(paramObject);
  }
  
  public String toString() { return getClass().getName() + "[x=" + this.x + ",y=" + this.y + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Point.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */