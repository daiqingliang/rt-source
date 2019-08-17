package java.awt.font;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public final class ShapeGraphicAttribute extends GraphicAttribute {
  private Shape fShape;
  
  private boolean fStroke;
  
  public static final boolean STROKE = true;
  
  public static final boolean FILL = false;
  
  private Rectangle2D fShapeBounds;
  
  public ShapeGraphicAttribute(Shape paramShape, int paramInt, boolean paramBoolean) {
    super(paramInt);
    this.fShape = paramShape;
    this.fStroke = paramBoolean;
    this.fShapeBounds = this.fShape.getBounds2D();
  }
  
  public float getAscent() { return (float)Math.max(0.0D, -this.fShapeBounds.getMinY()); }
  
  public float getDescent() { return (float)Math.max(0.0D, this.fShapeBounds.getMaxY()); }
  
  public float getAdvance() { return (float)Math.max(0.0D, this.fShapeBounds.getMaxX()); }
  
  public void draw(Graphics2D paramGraphics2D, float paramFloat1, float paramFloat2) {
    paramGraphics2D.translate((int)paramFloat1, (int)paramFloat2);
    try {
      if (this.fStroke == true) {
        paramGraphics2D.draw(this.fShape);
      } else {
        paramGraphics2D.fill(this.fShape);
      } 
    } finally {
      paramGraphics2D.translate(-((int)paramFloat1), -((int)paramFloat2));
    } 
  }
  
  public Rectangle2D getBounds() {
    Rectangle2D.Float float = new Rectangle2D.Float();
    float.setRect(this.fShapeBounds);
    if (this.fStroke == true) {
      float.width++;
      float.height++;
    } 
    return float;
  }
  
  public Shape getOutline(AffineTransform paramAffineTransform) { return (paramAffineTransform == null) ? this.fShape : paramAffineTransform.createTransformedShape(this.fShape); }
  
  public int hashCode() { return this.fShape.hashCode(); }
  
  public boolean equals(Object paramObject) {
    try {
      return equals((ShapeGraphicAttribute)paramObject);
    } catch (ClassCastException classCastException) {
      return false;
    } 
  }
  
  public boolean equals(ShapeGraphicAttribute paramShapeGraphicAttribute) { return (paramShapeGraphicAttribute == null) ? false : ((this == paramShapeGraphicAttribute) ? true : ((this.fStroke != paramShapeGraphicAttribute.fStroke) ? false : ((getAlignment() != paramShapeGraphicAttribute.getAlignment()) ? false : (!!this.fShape.equals(paramShapeGraphicAttribute.fShape))))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\ShapeGraphicAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */