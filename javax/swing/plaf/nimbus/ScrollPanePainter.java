package javax.swing.plaf.nimbus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;

final class ScrollPanePainter extends AbstractRegionPainter {
  static final int BACKGROUND_ENABLED = 1;
  
  static final int BORDER_ENABLED_FOCUSED = 2;
  
  static final int BORDER_ENABLED = 3;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color2 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Object[] componentColors;
  
  public ScrollPanePainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 2:
        paintBorderEnabledAndFocused(paramGraphics2D);
        break;
      case 3:
        paintBorderEnabled(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBorderEnabledAndFocused(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color2);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBorderEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(0.6F), decodeY(0.4F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(0.6F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(0.4F), decodeY(0.4F), (decodeX(0.6F) - decodeX(0.4F)), (decodeY(2.6F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(2.4F), decodeY(0.4F), (decodeX(2.6F) - decodeX(2.4F)), (decodeY(2.6F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(0.6F), decodeY(2.4F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(2.6F) - decodeY(2.4F)));
    return this.rect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.4F), decodeY(0.4F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(0.4F));
    this.path.curveTo(decodeAnchorX(2.6F, 0.0F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeX(2.8800004F), decodeY(0.4F));
    this.path.curveTo(decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.0F), decodeAnchorY(2.8799999F, 0.0F), decodeX(2.8800004F), decodeY(2.8799999F));
    this.path.lineTo(decodeX(0.120000005F), decodeY(2.8799999F));
    this.path.lineTo(decodeX(0.120000005F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(2.8800004F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(2.8800004F), decodeY(0.4F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.4F));
    this.path.closePath();
    return this.path;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ScrollPanePainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */