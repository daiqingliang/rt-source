package java.awt.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Enumeration;
import java.util.Vector;
import sun.awt.geom.AreaOp;
import sun.awt.geom.Crossings;
import sun.awt.geom.Curve;

public class Area implements Shape, Cloneable {
  private static Vector EmptyCurves = new Vector();
  
  private Vector curves;
  
  private Rectangle2D cachedBounds;
  
  public Area() { this.curves = EmptyCurves; }
  
  public Area(Shape paramShape) {
    if (paramShape instanceof Area) {
      this.curves = ((Area)paramShape).curves;
    } else {
      this.curves = pathToCurves(paramShape.getPathIterator(null));
    } 
  }
  
  private static Vector pathToCurves(PathIterator paramPathIterator) {
    AreaOp.NZWindOp nZWindOp;
    Vector vector = new Vector();
    int i = paramPathIterator.getWindingRule();
    double[] arrayOfDouble = new double[23];
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    while (!paramPathIterator.isDone()) {
      double d6;
      double d5;
      switch (paramPathIterator.currentSegment(arrayOfDouble)) {
        case 0:
          Curve.insertLine(vector, d3, d4, d1, d2);
          d3 = d1 = arrayOfDouble[0];
          d4 = d2 = arrayOfDouble[1];
          Curve.insertMove(vector, d1, d2);
        case 1:
          d5 = arrayOfDouble[0];
          d6 = arrayOfDouble[1];
          Curve.insertLine(vector, d3, d4, d5, d6);
          d3 = d5;
          d4 = d6;
          break;
        case 2:
          d5 = arrayOfDouble[2];
          d6 = arrayOfDouble[3];
          Curve.insertQuad(vector, d3, d4, arrayOfDouble);
          d3 = d5;
          d4 = d6;
          break;
        case 3:
          d5 = arrayOfDouble[4];
          d6 = arrayOfDouble[5];
          Curve.insertCubic(vector, d3, d4, arrayOfDouble);
          d3 = d5;
          d4 = d6;
          break;
        case 4:
          Curve.insertLine(vector, d3, d4, d1, d2);
          d3 = d1;
          d4 = d2;
          break;
      } 
      paramPathIterator.next();
    } 
    Curve.insertLine(vector, d3, d4, d1, d2);
    if (i == 0) {
      nZWindOp = new AreaOp.EOWindOp();
    } else {
      nZWindOp = new AreaOp.NZWindOp();
    } 
    return nZWindOp.calculate(vector, EmptyCurves);
  }
  
  public void add(Area paramArea) {
    this.curves = (new AreaOp.AddOp()).calculate(this.curves, paramArea.curves);
    invalidateBounds();
  }
  
  public void subtract(Area paramArea) {
    this.curves = (new AreaOp.SubOp()).calculate(this.curves, paramArea.curves);
    invalidateBounds();
  }
  
  public void intersect(Area paramArea) {
    this.curves = (new AreaOp.IntOp()).calculate(this.curves, paramArea.curves);
    invalidateBounds();
  }
  
  public void exclusiveOr(Area paramArea) {
    this.curves = (new AreaOp.XorOp()).calculate(this.curves, paramArea.curves);
    invalidateBounds();
  }
  
  public void reset() {
    this.curves = new Vector();
    invalidateBounds();
  }
  
  public boolean isEmpty() { return (this.curves.size() == 0); }
  
  public boolean isPolygonal() {
    Enumeration enumeration = this.curves.elements();
    while (enumeration.hasMoreElements()) {
      if (((Curve)enumeration.nextElement()).getOrder() > 1)
        return false; 
    } 
    return true;
  }
  
  public boolean isRectangular() {
    int i = this.curves.size();
    if (i == 0)
      return true; 
    if (i > 3)
      return false; 
    Curve curve1 = (Curve)this.curves.get(1);
    Curve curve2 = (Curve)this.curves.get(2);
    return (curve1.getOrder() != 1 || curve2.getOrder() != 1) ? false : ((curve1.getXTop() != curve1.getXBot() || curve2.getXTop() != curve2.getXBot()) ? false : (!(curve1.getYTop() != curve2.getYTop() || curve1.getYBot() != curve2.getYBot())));
  }
  
  public boolean isSingular() {
    if (this.curves.size() < 3)
      return true; 
    Enumeration enumeration = this.curves.elements();
    enumeration.nextElement();
    while (enumeration.hasMoreElements()) {
      if (((Curve)enumeration.nextElement()).getOrder() == 0)
        return false; 
    } 
    return true;
  }
  
  private void invalidateBounds() { this.cachedBounds = null; }
  
  private Rectangle2D getCachedBounds() {
    if (this.cachedBounds != null)
      return this.cachedBounds; 
    Rectangle2D.Double double = new Rectangle2D.Double();
    if (this.curves.size() > 0) {
      Curve curve = (Curve)this.curves.get(0);
      double.setRect(curve.getX0(), curve.getY0(), 0.0D, 0.0D);
      for (byte b = 1; b < this.curves.size(); b++)
        ((Curve)this.curves.get(b)).enlarge(double); 
    } 
    return this.cachedBounds = double;
  }
  
  public Rectangle2D getBounds2D() { return getCachedBounds().getBounds2D(); }
  
  public Rectangle getBounds() { return getCachedBounds().getBounds(); }
  
  public Object clone() { return new Area(this); }
  
  public boolean equals(Area paramArea) {
    if (paramArea == this)
      return true; 
    if (paramArea == null)
      return false; 
    Vector vector = (new AreaOp.XorOp()).calculate(this.curves, paramArea.curves);
    return vector.isEmpty();
  }
  
  public void transform(AffineTransform paramAffineTransform) {
    if (paramAffineTransform == null)
      throw new NullPointerException("transform must not be null"); 
    this.curves = pathToCurves(getPathIterator(paramAffineTransform));
    invalidateBounds();
  }
  
  public Area createTransformedArea(AffineTransform paramAffineTransform) {
    Area area = new Area(this);
    area.transform(paramAffineTransform);
    return area;
  }
  
  public boolean contains(double paramDouble1, double paramDouble2) {
    if (!getCachedBounds().contains(paramDouble1, paramDouble2))
      return false; 
    Enumeration enumeration = this.curves.elements();
    int i;
    for (i = 0; enumeration.hasMoreElements(); i += curve.crossingsFor(paramDouble1, paramDouble2))
      Curve curve = (Curve)enumeration.nextElement(); 
    return ((i & true) == 1);
  }
  
  public boolean contains(Point2D paramPoint2D) { return contains(paramPoint2D.getX(), paramPoint2D.getY()); }
  
  public boolean contains(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramDouble3 < 0.0D || paramDouble4 < 0.0D)
      return false; 
    if (!getCachedBounds().contains(paramDouble1, paramDouble2, paramDouble3, paramDouble4))
      return false; 
    Crossings crossings = Crossings.findCrossings(this.curves, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (crossings != null && crossings.covers(paramDouble2, paramDouble2 + paramDouble4));
  }
  
  public boolean contains(Rectangle2D paramRectangle2D) { return contains(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public boolean intersects(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    if (paramDouble3 < 0.0D || paramDouble4 < 0.0D)
      return false; 
    if (!getCachedBounds().intersects(paramDouble1, paramDouble2, paramDouble3, paramDouble4))
      return false; 
    Crossings crossings = Crossings.findCrossings(this.curves, paramDouble1, paramDouble2, paramDouble1 + paramDouble3, paramDouble2 + paramDouble4);
    return (crossings == null || !crossings.isEmpty());
  }
  
  public boolean intersects(Rectangle2D paramRectangle2D) { return intersects(paramRectangle2D.getX(), paramRectangle2D.getY(), paramRectangle2D.getWidth(), paramRectangle2D.getHeight()); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform) { return new AreaIterator(this.curves, paramAffineTransform); }
  
  public PathIterator getPathIterator(AffineTransform paramAffineTransform, double paramDouble) { return new FlatteningPathIterator(getPathIterator(paramAffineTransform), paramDouble); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\geom\Area.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */