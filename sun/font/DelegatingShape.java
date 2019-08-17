package sun.font;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public final class DelegatingShape implements Shape {
  Shape delegate;
  
  public DelegatingShape(Shape paramShape) { this.delegate = paramShape; }
  
  public Rectangle getBounds() { return this.delegate.getBounds(); }
  
  public Rectangle2D getBounds2D() { return this.delegate.getBounds2D(); }
  
  public boolean contains(double paramDouble1, double paramDouble2) { return this.delegate.contains(paramDouble1, paramDouble2); }
  
  public boolean contains(Point2D paramPoint2D) { return this.delegate.contains(paramPoint2D); }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { return this.delegate.intersects(paramDouble1, paramDouble2, paramDouble3, paramDouble4); }
  
  public boolean intersects(Rectangle2D paramRectangle2D) { return this.delegate.intersects(paramRectangle2D); }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) { return this.delegate.contains(paramDouble1, paramDouble2, paramDouble3, paramDouble4); }
  
  public boolean contains(Rectangle2D paramRectangle2D) { return this.delegate.contains(paramRectangle2D); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform) { return this.delegate.getPathIterator(paramAffineTransform); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble) { return this.delegate.getPathIterator(paramAffineTransform, paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\font\DelegatingShape.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */