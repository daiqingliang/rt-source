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

final class ToolBarPainter extends AbstractRegionPainter {
  static final int BORDER_NORTH = 1;
  
  static final int BORDER_SOUTH = 2;
  
  static final int BORDER_EAST = 3;
  
  static final int BORDER_WEST = 4;
  
  static final int HANDLEICON_ENABLED = 5;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBorder", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.07399663F, 0.11372548F, 0);
  
  private Color color4 = decodeColor("nimbusBorder", 0.0F, -0.029675633F, 0.109803915F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", -0.008547008F, -0.03494492F, -0.07058823F, 0);
  
  private Object[] componentColors;
  
  public ToolBarPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 1:
        paintBorderNorth(paramGraphics2D);
        break;
      case 2:
        paintBorderSouth(paramGraphics2D);
        break;
      case 3:
        paintBorderEast(paramGraphics2D);
        break;
      case 4:
        paintBorderWest(paramGraphics2D);
        break;
      case 5:
        painthandleIconEnabled(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBorderNorth(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBorderSouth(Graphics2D paramGraphics2D) {
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBorderEast(Graphics2D paramGraphics2D) {
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBorderWest(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
  }
  
  private void painthandleIconEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.path);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(1.0F), decodeY(2.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(3.0F) - decodeY(2.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(1.0F), decodeY(0.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(1.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(0.0F), decodeY(0.0F), (decodeX(2.8F) - decodeX(0.0F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(2.8F), decodeY(0.0F), (decodeX(3.0F) - decodeX(2.8F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.4F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.4F), decodeY(3.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(3.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.0F * f3 + f1, 0.5F * f4 + f2, 1.0F * f3 + f1, 0.5F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ToolBarPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */