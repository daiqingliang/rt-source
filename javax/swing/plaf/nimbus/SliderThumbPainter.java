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

final class SliderThumbPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_FOCUSED = 3;
  
  static final int BACKGROUND_FOCUSED_MOUSEOVER = 4;
  
  static final int BACKGROUND_FOCUSED_PRESSED = 5;
  
  static final int BACKGROUND_MOUSEOVER = 6;
  
  static final int BACKGROUND_PRESSED = 7;
  
  static final int BACKGROUND_ENABLED_ARROWSHAPE = 8;
  
  static final int BACKGROUND_DISABLED_ARROWSHAPE = 9;
  
  static final int BACKGROUND_MOUSEOVER_ARROWSHAPE = 10;
  
  static final int BACKGROUND_PRESSED_ARROWSHAPE = 11;
  
  static final int BACKGROUND_FOCUSED_ARROWSHAPE = 12;
  
  static final int BACKGROUND_FOCUSED_MOUSEOVER_ARROWSHAPE = 13;
  
  static final int BACKGROUND_FOCUSED_PRESSED_ARROWSHAPE = 14;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBase", 0.021348298F, -0.5625436F, 0.25490195F, 0);
  
  private Color color2 = decodeColor("nimbusBase", 0.015098333F, -0.55105823F, 0.19215685F, 0);
  
  private Color color3 = decodeColor("nimbusBase", 0.021348298F, -0.5924243F, 0.35686272F, 0);
  
  private Color color4 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  
  private Color color5 = decodeColor("nimbusBase", 0.021348298F, -0.56844974F, 0.32549018F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", -0.003968239F, 0.0014736876F, -0.25490198F, -156);
  
  private Color color7 = decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
  
  private Color color8 = decodeColor("nimbusBase", -0.0017285943F, -0.11571431F, -0.25490198F, 0);
  
  private Color color9 = decodeColor("nimbusBase", -0.023096085F, -0.6238095F, 0.43921566F, 0);
  
  private Color color10 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  
  private Color color11 = decodeColor("nimbusBase", 5.1498413E-4F, -0.45714286F, 0.32941175F, 0);
  
  private Color color12 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color13 = decodeColor("nimbusBase", -0.0038217902F, -0.15532213F, -0.14901963F, 0);
  
  private Color color14 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54509807F, 0);
  
  private Color color15 = decodeColor("nimbusBase", 0.004681647F, -0.62780917F, 0.44313723F, 0);
  
  private Color color16 = decodeColor("nimbusBase", 2.9569864E-4F, -0.4653107F, 0.32549018F, 0);
  
  private Color color17 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4563421F, 0.32549018F, 0);
  
  private Color color18 = decodeColor("nimbusBase", -0.0017285943F, -0.4732143F, 0.39215684F, 0);
  
  private Color color19 = decodeColor("nimbusBase", 0.0015952587F, -0.04875779F, -0.18823531F, 0);
  
  private Color color20 = decodeColor("nimbusBase", 2.9569864E-4F, -0.44943976F, 0.25098038F, 0);
  
  private Color color21 = decodeColor("nimbusBase", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color22 = decodeColor("nimbusBase", 8.9377165E-4F, -0.121094406F, 0.12156862F, 0);
  
  private Color color23 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -121);
  
  private Color color24 = new Color(150, 156, 168, 146);
  
  private Color color25 = decodeColor("nimbusBase", -0.0033828616F, -0.40608466F, -0.019607842F, 0);
  
  private Color color26 = decodeColor("nimbusBase", 5.1498413E-4F, -0.17594418F, -0.20784315F, 0);
  
  private Color color27 = decodeColor("nimbusBase", 0.0023007393F, -0.11332625F, -0.28627452F, 0);
  
  private Color color28 = decodeColor("nimbusBase", -0.023096085F, -0.62376213F, 0.4352941F, 0);
  
  private Color color29 = decodeColor("nimbusBase", 0.004681647F, -0.594392F, 0.39999998F, 0);
  
  private Color color30 = decodeColor("nimbusBase", -0.0017285943F, -0.4454704F, 0.25490195F, 0);
  
  private Color color31 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
  
  private Color color32 = decodeColor("nimbusBase", 5.1498413E-4F, -0.47442397F, 0.4235294F, 0);
  
  private Object[] componentColors;
  
  public SliderThumbPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
        paintBackgroundFocused(paramGraphics2D);
        break;
      case 4:
        paintBackgroundFocusedAndMouseOver(paramGraphics2D);
        break;
      case 5:
        paintBackgroundFocusedAndPressed(paramGraphics2D);
        break;
      case 6:
        paintBackgroundMouseOver(paramGraphics2D);
        break;
      case 7:
        paintBackgroundPressed(paramGraphics2D);
        break;
      case 8:
        paintBackgroundEnabledAndArrowShape(paramGraphics2D);
        break;
      case 9:
        paintBackgroundDisabledAndArrowShape(paramGraphics2D);
        break;
      case 10:
        paintBackgroundMouseOverAndArrowShape(paramGraphics2D);
        break;
      case 11:
        paintBackgroundPressedAndArrowShape(paramGraphics2D);
        break;
      case 12:
        paintBackgroundFocusedAndArrowShape(paramGraphics2D);
        break;
      case 13:
        paintBackgroundFocusedAndMouseOverAndArrowShape(paramGraphics2D);
        break;
      case 14:
        paintBackgroundFocusedAndPressedAndArrowShape(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient1(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient2(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color6);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient3(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient4(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient3(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient4(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void paintBackgroundFocusedAndMouseOver(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient5(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient6(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void paintBackgroundFocusedAndPressed(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient7(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient8(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color6);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient5(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient6(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color23);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient7(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient8(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void paintBackgroundEnabledAndArrowShape(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color24);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient10(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundDisabledAndArrowShape(Graphics2D paramGraphics2D) {
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient11(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient12(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndArrowShape(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color24);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient13(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient14(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressedAndArrowShape(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color24);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient15(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient16(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundFocusedAndArrowShape(Graphics2D paramGraphics2D) {
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient17(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundFocusedAndMouseOverAndArrowShape(Graphics2D paramGraphics2D) {
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient13(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient14(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundFocusedAndPressedAndArrowShape(Graphics2D paramGraphics2D) {
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient15(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient16(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private Ellipse2D decodeEllipse1() {
    this.ellipse.setFrame(decodeX(0.4F), decodeY(0.4F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.6F) - decodeY(0.4F)));
    return this.ellipse;
  }
  
  private Ellipse2D decodeEllipse2() {
    this.ellipse.setFrame(decodeX(0.6F), decodeY(0.6F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(2.4F) - decodeY(0.6F)));
    return this.ellipse;
  }
  
  private Ellipse2D decodeEllipse3() {
    this.ellipse.setFrame(decodeX(0.4F), decodeY(0.6F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.8F) - decodeY(0.6F)));
    return this.ellipse;
  }
  
  private Ellipse2D decodeEllipse4() {
    this.ellipse.setFrame(decodeX(0.120000005F), decodeY(0.120000005F), (decodeX(2.8799999F) - decodeX(0.120000005F)), (decodeY(2.8799999F) - decodeY(0.120000005F)));
    return this.ellipse;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.8166667F), decodeY(0.5007576F));
    this.path.curveTo(decodeAnchorX(0.8166667F, 1.5643269F), decodeAnchorY(0.5007576F, -0.3097513F), decodeAnchorX(2.7925456F, 0.058173586F), decodeAnchorY(1.6116884F, -0.4647635F), decodeX(2.7925456F), decodeY(1.6116884F));
    this.path.curveTo(decodeAnchorX(2.7925456F, -0.34086856F), decodeAnchorY(1.6116884F, 2.7232852F), decodeAnchorX(0.7006364F, 4.568128F), decodeAnchorY(2.7693636F, -0.006014915F), decodeX(0.7006364F), decodeY(2.7693636F));
    this.path.curveTo(decodeAnchorX(0.7006364F, -3.5233955F), decodeAnchorY(2.7693636F, 0.004639302F), decodeAnchorX(0.8166667F, -1.8635255F), decodeAnchorY(0.5007576F, 0.36899543F), decodeX(0.8166667F), decodeY(0.5007576F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(0.6155303F), decodeY(2.5954547F));
    this.path.curveTo(decodeAnchorX(0.6155303F, 0.90980893F), decodeAnchorY(2.5954547F, 1.3154242F), decodeAnchorX(2.6151516F, 0.014588808F), decodeAnchorY(1.6112013F, 0.9295521F), decodeX(2.6151516F), decodeY(1.6112013F));
    this.path.curveTo(decodeAnchorX(2.6151516F, -0.01365518F), decodeAnchorY(1.6112013F, -0.8700643F), decodeAnchorX(0.60923916F, 0.9729935F), decodeAnchorY(0.40716404F, -1.4248644F), decodeX(0.60923916F), decodeY(0.40716404F));
    this.path.curveTo(decodeAnchorX(0.60923916F, -0.7485209F), decodeAnchorY(0.40716404F, 1.0961438F), decodeAnchorX(0.6155303F, -0.74998796F), decodeAnchorY(2.5954547F, -1.0843511F), decodeX(0.6155303F), decodeY(2.5954547F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(0.8055606F), decodeY(0.6009697F));
    this.path.curveTo(decodeAnchorX(0.8055606F, 0.50820893F), decodeAnchorY(0.6009697F, -0.8490881F), decodeAnchorX(2.3692727F, 0.0031846066F), decodeAnchorY(1.613117F, -0.60668826F), decodeX(2.3692727F), decodeY(1.613117F));
    this.path.curveTo(decodeAnchorX(2.3692727F, -0.003890196F), decodeAnchorY(1.613117F, 0.74110764F), decodeAnchorX(0.7945455F, 0.3870974F), decodeAnchorY(2.3932729F, 1.240782F), decodeX(0.7945455F), decodeY(2.3932729F));
    this.path.curveTo(decodeAnchorX(0.7945455F, -0.38636583F), decodeAnchorY(2.3932729F, -1.2384372F), decodeAnchorX(0.8055606F, -0.995154F), decodeAnchorY(0.6009697F, 1.6626496F), decodeX(0.8055606F), decodeY(0.6009697F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath4() {
    this.path.reset();
    this.path.moveTo(decodeX(0.60059524F), decodeY(0.11727543F));
    this.path.curveTo(decodeAnchorX(0.60059524F, 1.5643269F), decodeAnchorY(0.11727543F, -0.3097513F), decodeAnchorX(2.7925456F, 0.004405844F), decodeAnchorY(1.6116884F, -1.1881162F), decodeX(2.7925456F), decodeY(1.6116884F));
    this.path.curveTo(decodeAnchorX(2.7925456F, -0.007364541F), decodeAnchorY(1.6116884F, 1.9859827F), decodeAnchorX(0.7006364F, 2.7716863F), decodeAnchorY(2.8693638F, -0.008974582F), decodeX(0.7006364F), decodeY(2.8693638F));
    this.path.curveTo(decodeAnchorX(0.7006364F, -3.754899F), decodeAnchorY(2.8693638F, 0.012158176F), decodeAnchorX(0.60059524F, -1.8635255F), decodeAnchorY(0.11727543F, 0.36899543F), decodeX(0.60059524F), decodeY(0.11727543F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5106101F * f3 + f1, -4.553649E-18F * f4 + f2, 0.49933687F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color1, decodeColor(this.color1, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5023511F * f3 + f1, 0.0015673981F * f4 + f2, 0.5023511F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.71256685F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.51F * f3 + f1, -4.553649E-18F * f4 + f2, 0.51F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0015673981F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F }, new Color[] { this.color9, decodeColor(this.color9, this.color10, 0.5F), this.color10, decodeColor(this.color10, this.color10, 0.5F), this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5106101F * f3 + f1, -4.553649E-18F * f4 + f2, 0.49933687F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5023511F * f3 + f1, 0.0015673981F * f4 + f2, 0.5023511F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F }, new Color[] { this.color15, decodeColor(this.color15, this.color16, 0.5F), this.color16, decodeColor(this.color16, this.color17, 0.5F), this.color17, decodeColor(this.color17, this.color18, 0.5F), this.color18 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5106101F * f3 + f1, -4.553649E-18F * f4 + f2, 0.49933687F * f3 + f1, 1.0039787F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color14, decodeColor(this.color14, this.color19, 0.5F), this.color19 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5023511F * f3 + f1, 0.0015673981F * f4 + f2, 0.5023511F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23796791F, 0.47593582F, 0.5360962F, 0.5962567F, 0.79812837F, 1.0F }, new Color[] { this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color22, 0.5F), this.color22 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.24032257F, 0.48064515F, 0.7403226F, 1.0F }, new Color[] { this.color25, decodeColor(this.color25, this.color26, 0.5F), this.color26, decodeColor(this.color26, this.color27, 0.5F), this.color27 });
  }
  
  private Paint decodeGradient10(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.061290324F, 0.1016129F, 0.14193548F, 0.3016129F, 0.46129033F, 0.5983871F, 0.7354839F, 0.7935484F, 0.8516129F }, new Color[] { this.color28, decodeColor(this.color28, this.color29, 0.5F), this.color29, decodeColor(this.color29, this.color30, 0.5F), this.color30, decodeColor(this.color30, this.color31, 0.5F), this.color31, decodeColor(this.color31, this.color32, 0.5F), this.color32 });
  }
  
  private Paint decodeGradient11(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color1, decodeColor(this.color1, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient12(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.71256685F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5 });
  }
  
  private Paint decodeGradient13(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14 });
  }
  
  private Paint decodeGradient14(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.21256684F, 0.42513368F, 0.56149733F, 0.69786096F, 0.8489305F, 1.0F }, new Color[] { this.color15, decodeColor(this.color15, this.color16, 0.5F), this.color16, decodeColor(this.color16, this.color17, 0.5F), this.color17, decodeColor(this.color17, this.color18, 0.5F), this.color18 });
  }
  
  private Paint decodeGradient15(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color14, decodeColor(this.color14, this.color19, 0.5F), this.color19 });
  }
  
  private Paint decodeGradient16(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23796791F, 0.47593582F, 0.5360962F, 0.5962567F, 0.79812837F, 1.0F }, new Color[] { this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color22, 0.5F), this.color22 });
  }
  
  private Paint decodeGradient17(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.4925773F * f3 + f1, 0.082019866F * f4 + f2, 0.4925773F * f3 + f1, 0.91798013F * f4 + f2, new float[] { 0.061290324F, 0.1016129F, 0.14193548F, 0.3016129F, 0.46129033F, 0.5983871F, 0.7354839F, 0.7935484F, 0.8516129F }, new Color[] { this.color28, decodeColor(this.color28, this.color29, 0.5F), this.color29, decodeColor(this.color29, this.color30, 0.5F), this.color30, decodeColor(this.color30, this.color31, 0.5F), this.color31, decodeColor(this.color31, this.color32, 0.5F), this.color32 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\SliderThumbPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */