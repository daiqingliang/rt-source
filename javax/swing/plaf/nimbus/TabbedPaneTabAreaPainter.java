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

final class TabbedPaneTabAreaPainter extends AbstractRegionPainter {
  static final int BACKGROUND_ENABLED = 1;
  
  static final int BACKGROUND_DISABLED = 2;
  
  static final int BACKGROUND_ENABLED_MOUSEOVER = 3;
  
  static final int BACKGROUND_ENABLED_PRESSED = 4;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = new Color(255, 200, 0, 255);
  
  private Color color2 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.4784314F, 0);
  
  private Color color3 = decodeColor("nimbusBase", 5.1498413E-4F, -0.45471883F, 0.31764704F, 0);
  
  private Color color4 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4633005F, 0.3607843F, 0);
  
  private Color color5 = decodeColor("nimbusBase", 0.05468172F, -0.58308274F, 0.19607842F, 0);
  
  private Color color6 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  
  private Color color7 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4690476F, 0.39215684F, 0);
  
  private Color color8 = decodeColor("nimbusBase", 5.1498413E-4F, -0.47635174F, 0.4352941F, 0);
  
  private Color color9 = decodeColor("nimbusBase", 0.0F, -0.05401492F, 0.05098039F, 0);
  
  private Color color10 = decodeColor("nimbusBase", 0.0F, -0.09303135F, 0.09411764F, 0);
  
  private Object[] componentColors;
  
  public TabbedPaneTabAreaPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 1:
        paintBackgroundEnabled(paramGraphics2D);
        break;
      case 2:
        paintBackgroundDisabled(paramGraphics2D);
        break;
      case 3:
        paintBackgroundEnabledAndMouseOver(paramGraphics2D);
        break;
      case 4:
        paintBackgroundEnabledAndPressed(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient2(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabledAndMouseOver(Graphics2D paramGraphics2D) {
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient3(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabledAndPressed(Graphics2D paramGraphics2D) {
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(0.0F), decodeY(1.0F), (decodeX(0.0F) - decodeX(0.0F)), (decodeY(1.0F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(0.0F), decodeY(2.1666667F), (decodeX(3.0F) - decodeX(0.0F)), (decodeY(3.0F) - decodeY(2.1666667F)));
    return this.rect;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F }, new Color[] { this.color5, decodeColor(this.color5, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F }, new Color[] { this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8, decodeColor(this.color8, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.08387097F, 0.09677419F, 0.10967742F, 0.43709677F, 0.7645161F, 0.7758064F, 0.7870968F }, new Color[] { this.color2, decodeColor(this.color2, this.color9, 0.5F), this.color9, decodeColor(this.color9, this.color10, 0.5F), this.color10, decodeColor(this.color10, this.color2, 0.5F), this.color2 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TabbedPaneTabAreaPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */