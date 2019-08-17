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

final class TableHeaderRendererPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_ENABLED_FOCUSED = 3;
  
  static final int BACKGROUND_MOUSEOVER = 4;
  
  static final int BACKGROUND_PRESSED = 5;
  
  static final int BACKGROUND_ENABLED_SORTED = 6;
  
  static final int BACKGROUND_ENABLED_FOCUSED_SORTED = 7;
  
  static final int BACKGROUND_DISABLED_SORTED = 8;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBorder", -0.013888836F, 5.823001E-4F, -0.12941176F, 0);
  
  private Color color2 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.08625447F, 0.062745094F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", -0.013888836F, -0.028334536F, -0.17254901F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", -0.013888836F, -0.029445238F, -0.16470587F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", -0.02020204F, -0.053531498F, 0.011764705F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10655806F, 0.24313724F, 0);
  
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.08455229F, 0.1607843F, 0);
  
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", 0.0F, -0.07466974F, 0.23921567F, 0);
  
  private Color color10 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
  
  private Color color12 = decodeColor("nimbusBlueGrey", 0.0F, -0.08613607F, 0.21960783F, 0);
  
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
  
  private Color color14 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color15 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.05960039F, 0.10196078F, 0);
  
  private Color color16 = decodeColor("nimbusBlueGrey", 0.0F, -0.017742813F, 0.015686274F, 0);
  
  private Color color17 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
  
  private Color color18 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.020436227F, 0.12549019F, 0);
  
  private Color color19 = decodeColor("nimbusBase", -0.023096085F, -0.62376213F, 0.4352941F, 0);
  
  private Color color20 = decodeColor("nimbusBase", -0.0012707114F, -0.50901747F, 0.31764704F, 0);
  
  private Color color21 = decodeColor("nimbusBase", -0.002461195F, -0.47139505F, 0.2862745F, 0);
  
  private Color color22 = decodeColor("nimbusBase", -0.0051222444F, -0.49103343F, 0.372549F, 0);
  
  private Color color23 = decodeColor("nimbusBase", -8.738637E-4F, -0.49872798F, 0.3098039F, 0);
  
  private Color color24 = decodeColor("nimbusBase", -2.2029877E-4F, -0.4916465F, 0.37647057F, 0);
  
  private Object[] componentColors;
  
  public TableHeaderRendererPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
      case 3:
        paintBackgroundEnabledAndFocused(paramGraphics2D);
        break;
      case 4:
        paintBackgroundMouseOver(paramGraphics2D);
        break;
      case 5:
        paintBackgroundPressed(paramGraphics2D);
        break;
      case 6:
        paintBackgroundEnabledAndSorted(paramGraphics2D);
        break;
      case 7:
        paintBackgroundEnabledAndFocusedAndSorted(paramGraphics2D);
        break;
      case 8:
        paintBackgroundDisabledAndSorted(paramGraphics2D);
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
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabledAndFocused(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.rect));
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient3(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabledAndSorted(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient5(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabledAndFocusedAndSorted(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient6(this.rect));
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundDisabledAndSorted(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(0.0F), decodeY(2.8F), (decodeX(3.0F) - decodeX(0.0F)), (decodeY(3.0F) - decodeY(2.8F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(2.8F), decodeY(0.0F), (decodeX(3.0F) - decodeX(2.8F)), (decodeY(2.8F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(0.0F), decodeY(0.0F), (decodeX(2.8F) - decodeX(0.0F)), (decodeY(2.8F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(3.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(3.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(0.24000001F));
    this.path.lineTo(decodeX(2.7599998F), decodeY(0.24000001F));
    this.path.lineTo(decodeX(2.7599998F), decodeY(2.7599998F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(2.7599998F));
    this.path.lineTo(decodeX(0.24000001F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.14441223F, 0.43703705F, 0.59444445F, 0.75185186F, 0.8759259F, 1.0F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.8518518F, 1.0F }, new Color[] { this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8, decodeColor(this.color8, this.color9, 0.5F), this.color9 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.7919203F, 0.88013697F }, new Color[] { this.color11, decodeColor(this.color11, this.color12, 0.5F), this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.8518518F, 1.0F }, new Color[] { this.color15, decodeColor(this.color15, this.color16, 0.5F), this.color16, decodeColor(this.color16, this.color17, 0.5F), this.color17, decodeColor(this.color17, this.color18, 0.5F), this.color18 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.08049711F, 0.32534248F, 0.56267816F, 0.7037037F, 0.83986557F, 0.97602737F }, new Color[] { this.color19, decodeColor(this.color19, this.color20, 0.5F), this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color22, 0.5F), this.color22 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.07147767F, 0.2888889F, 0.5490909F, 0.7037037F, 0.8518518F, 1.0F }, new Color[] { this.color19, decodeColor(this.color19, this.color23, 0.5F), this.color23, decodeColor(this.color23, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color24, 0.5F), this.color24 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TableHeaderRendererPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */