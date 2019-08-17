package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class PopupMenuPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.39607844F, 0);
  
  private Color color2 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  
  private Color color3 = decodeColor("nimbusBase", 0.021348298F, -0.6150531F, 0.39999998F, 0);
  
  private Object[] componentColors;
  
  public PopupMenuPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 1:
        paintBackgroundDisabled(paramGraphics2D);
        break;
      case 2:
        paintBackgroundEnabled(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(1.0F), decodeY(0.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(1.0045455F), decodeY(0.11111111F), (decodeX(1.9954545F) - decodeX(1.0045455F)), (decodeY(2.909091F) - decodeY(0.11111111F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(0.0F), decodeY(0.0F), (decodeX(3.0F) - decodeX(0.0F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(0.5F), decodeY(0.09090909F), (decodeX(2.5F) - decodeX(0.5F)), (decodeY(2.909091F) - decodeY(0.09090909F)));
    return this.rect;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.003F, 0.02F, 0.5F, 0.98F, 0.996F, 1.0F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color2, 0.5F), this.color2 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\PopupMenuPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */