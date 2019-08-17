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

final class ComboBoxArrowButtonPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_ENABLED_MOUSEOVER = 3;
  
  static final int BACKGROUND_ENABLED_PRESSED = 4;
  
  static final int BACKGROUND_DISABLED_EDITABLE = 5;
  
  static final int BACKGROUND_ENABLED_EDITABLE = 6;
  
  static final int BACKGROUND_MOUSEOVER_EDITABLE = 7;
  
  static final int BACKGROUND_PRESSED_EDITABLE = 8;
  
  static final int BACKGROUND_SELECTED_EDITABLE = 9;
  
  static final int FOREGROUND_ENABLED = 10;
  
  static final int FOREGROUND_MOUSEOVER = 11;
  
  static final int FOREGROUND_DISABLED = 12;
  
  static final int FOREGROUND_PRESSED = 13;
  
  static final int FOREGROUND_SELECTED = 14;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, -247);
  
  private Color color2 = decodeColor("nimbusBase", 0.021348298F, -0.56289876F, 0.2588235F, 0);
  
  private Color color3 = decodeColor("nimbusBase", 0.010237217F, -0.55799407F, 0.20784312F, 0);
  
  private Color color4 = new Color(255, 200, 0, 255);
  
  private Color color5 = decodeColor("nimbusBase", 0.021348298F, -0.59223604F, 0.35294116F, 0);
  
  private Color color6 = decodeColor("nimbusBase", 0.02391243F, -0.5774183F, 0.32549018F, 0);
  
  private Color color7 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  
  private Color color8 = decodeColor("nimbusBase", 0.021348298F, -0.567841F, 0.31764704F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, -191);
  
  private Color color10 = decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
  
  private Color color11 = decodeColor("nimbusBase", 5.1498413E-4F, -0.095173776F, -0.25882354F, 0);
  
  private Color color12 = decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
  
  private Color color13 = decodeColor("nimbusBase", 0.0023007393F, -0.46825016F, 0.27058822F, 0);
  
  private Color color14 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  
  private Color color15 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
  
  private Color color16 = decodeColor("nimbusBase", 0.0013483167F, -0.1769987F, -0.12156865F, 0);
  
  private Color color17 = decodeColor("nimbusBase", 0.059279382F, 0.3642857F, -0.43529415F, 0);
  
  private Color color18 = decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
  
  private Color color19 = decodeColor("nimbusBase", 0.0023007393F, -0.48084703F, 0.33725488F, 0);
  
  private Color color20 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  
  private Color color21 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4757143F, 0.43137252F, 0);
  
  private Color color22 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  
  private Color color23 = decodeColor("nimbusBase", -3.528595E-5F, 0.018606722F, -0.23137257F, 0);
  
  private Color color24 = decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
  
  private Color color25 = decodeColor("nimbusBase", 7.13408E-4F, -0.064285696F, 0.027450979F, 0);
  
  private Color color26 = decodeColor("nimbusBase", 0.0F, -0.00895375F, 0.007843137F, 0);
  
  private Color color27 = decodeColor("nimbusBase", 8.9377165E-4F, -0.13853917F, 0.14509803F, 0);
  
  private Color color28 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.37254906F, 0);
  
  private Color color29 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.5254902F, 0);
  
  private Color color30 = decodeColor("nimbusBase", 0.027408898F, -0.57391655F, 0.1490196F, 0);
  
  private Color color31 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  
  private Object[] componentColors;
  
  public ComboBoxArrowButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 5:
        paintBackgroundDisabledAndEditable(paramGraphics2D);
        break;
      case 6:
        paintBackgroundEnabledAndEditable(paramGraphics2D);
        break;
      case 7:
        paintBackgroundMouseOverAndEditable(paramGraphics2D);
        break;
      case 8:
        paintBackgroundPressedAndEditable(paramGraphics2D);
        break;
      case 9:
        paintBackgroundSelectedAndEditable(paramGraphics2D);
        break;
      case 10:
        paintForegroundEnabled(paramGraphics2D);
        break;
      case 11:
        paintForegroundMouseOver(paramGraphics2D);
        break;
      case 12:
        paintForegroundDisabled(paramGraphics2D);
        break;
      case 13:
        paintForegroundPressed(paramGraphics2D);
        break;
      case 14:
        paintForegroundSelected(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabledAndEditable(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient1(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient2(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabledAndEditable(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient4(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndEditable(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient5(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressedAndEditable(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundSelectedAndEditable(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintForegroundEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient9(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintForegroundMouseOver(Graphics2D paramGraphics2D) {
    this.path = decodePath6();
    paramGraphics2D.setPaint(decodeGradient9(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintForegroundDisabled(Graphics2D paramGraphics2D) {
    this.path = decodePath7();
    paramGraphics2D.setPaint(this.color30);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintForegroundPressed(Graphics2D paramGraphics2D) {
    this.path = decodePath8();
    paramGraphics2D.setPaint(this.color31);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintForegroundSelected(Graphics2D paramGraphics2D) {
    this.path = decodePath7();
    paramGraphics2D.setPaint(this.color31);
    paramGraphics2D.fill(this.path);
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(2.0F));
    this.path.lineTo(decodeX(2.75F), decodeY(2.0F));
    this.path.lineTo(decodeX(2.75F), decodeY(2.25F));
    this.path.curveTo(decodeAnchorX(2.75F, 0.0F), decodeAnchorY(2.25F, 4.0F), decodeAnchorX(2.125F, 3.0F), decodeAnchorY(2.875F, 0.0F), decodeX(2.125F), decodeY(2.875F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.875F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(0.25F));
    this.path.lineTo(decodeX(2.125F), decodeY(0.25F));
    this.path.curveTo(decodeAnchorX(2.125F, 3.0F), decodeAnchorY(0.25F, 0.0F), decodeAnchorX(2.75F, 0.0F), decodeAnchorY(0.875F, -3.0F), decodeX(2.75F), decodeY(0.875F));
    this.path.lineTo(decodeX(2.75F), decodeY(2.125F));
    this.path.curveTo(decodeAnchorX(2.75F, 0.0F), decodeAnchorY(2.125F, 3.0F), decodeAnchorX(2.125F, 3.0F), decodeAnchorY(2.75F, 0.0F), decodeX(2.125F), decodeY(2.75F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.75F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.25F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(0.85294116F), decodeY(2.639706F));
    this.path.lineTo(decodeX(0.85294116F), decodeY(2.639706F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath4() {
    this.path.reset();
    this.path.moveTo(decodeX(1.0F), decodeY(0.375F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.375F));
    this.path.curveTo(decodeAnchorX(2.0F, 4.0F), decodeAnchorY(0.375F, 0.0F), decodeAnchorX(2.625F, 0.0F), decodeAnchorY(1.0F, -4.0F), decodeX(2.625F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.625F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(2.625F, 0.0F), decodeAnchorY(2.0F, 4.0F), decodeAnchorX(2.0F, 4.0F), decodeAnchorY(2.625F, 0.0F), decodeX(2.0F), decodeY(2.625F));
    this.path.lineTo(decodeX(1.0F), decodeY(2.625F));
    this.path.lineTo(decodeX(1.0F), decodeY(0.375F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath5() {
    this.path.reset();
    this.path.moveTo(decodeX(0.9995915F), decodeY(1.3616071F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.8333333F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.8571429F));
    this.path.lineTo(decodeX(0.9995915F), decodeY(1.3616071F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath6() {
    this.path.reset();
    this.path.moveTo(decodeX(1.00625F), decodeY(1.3526785F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.8333333F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.8571429F));
    this.path.lineTo(decodeX(1.00625F), decodeY(1.3526785F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath7() {
    this.path.reset();
    this.path.moveTo(decodeX(1.0117648F), decodeY(1.3616071F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.8333333F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.8571429F));
    this.path.lineTo(decodeX(1.0117648F), decodeY(1.3616071F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath8() {
    this.path.reset();
    this.path.moveTo(decodeX(1.0242647F), decodeY(1.3526785F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.8333333F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.8571429F));
    this.path.lineTo(decodeX(1.0242647F), decodeY(1.3526785F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.171875F, 0.34375F, 0.4815341F, 0.6193182F, 0.8096591F, 1.0F }, new Color[] { this.color5, decodeColor(this.color5, this.color6, 0.5F), this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.12299465F, 0.44652405F, 0.5441176F, 0.64171124F, 0.8208556F, 1.0F }, new Color[] { this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14, decodeColor(this.color14, this.color15, 0.5F), this.color15 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color16, decodeColor(this.color16, this.color17, 0.5F), this.color17 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.12299465F, 0.44652405F, 0.5441176F, 0.64171124F, 0.81283426F, 0.98395723F }, new Color[] { this.color18, decodeColor(this.color18, this.color19, 0.5F), this.color19, decodeColor(this.color19, this.color20, 0.5F), this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color22, decodeColor(this.color22, this.color23, 0.5F), this.color23 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.12299465F, 0.44652405F, 0.5441176F, 0.64171124F, 0.8208556F, 1.0F }, new Color[] { this.color24, decodeColor(this.color24, this.color25, 0.5F), this.color25, decodeColor(this.color25, this.color26, 0.5F), this.color26, decodeColor(this.color26, this.color27, 0.5F), this.color27 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(1.0F * f3 + f1, 0.5F * f4 + f2, 0.0F * f3 + f1, 0.5F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color28, decodeColor(this.color28, this.color29, 0.5F), this.color29 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ComboBoxArrowButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */