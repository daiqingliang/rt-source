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

final class ProgressBarPainter extends AbstractRegionPainter {
  static final int BACKGROUND_ENABLED = 1;
  
  static final int BACKGROUND_DISABLED = 2;
  
  static final int FOREGROUND_ENABLED = 3;
  
  static final int FOREGROUND_ENABLED_FINISHED = 4;
  
  static final int FOREGROUND_ENABLED_INDETERMINATE = 5;
  
  static final int FOREGROUND_DISABLED = 6;
  
  static final int FOREGROUND_DISABLED_FINISHED = 7;
  
  static final int FOREGROUND_DISABLED_INDETERMINATE = 8;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.04845735F, -0.17647058F, 0);
  
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0F, -0.061345987F, -0.027450979F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, -0.097921275F, 0.18823528F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0138888955F, -0.0925083F, 0.12549019F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.08222443F, 0.086274505F, 0);
  
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.08477524F, 0.16862744F, 0);
  
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.086996906F, 0.25490195F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", 0.0F, -0.061613273F, -0.02352941F, 0);
  
  private Color color10 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.061265234F, 0.05098039F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0138888955F, -0.09378991F, 0.19215685F, 0);
  
  private Color color12 = decodeColor("nimbusBlueGrey", 0.0F, -0.08455229F, 0.1607843F, 0);
  
  private Color color13 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.08362049F, 0.12941176F, 0);
  
  private Color color14 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07826825F, 0.10588235F, 0);
  
  private Color color15 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07982456F, 0.1490196F, 0);
  
  private Color color16 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.08099045F, 0.18431371F, 0);
  
  private Color color17 = decodeColor("nimbusOrange", 0.0F, 0.0F, 0.0F, -156);
  
  private Color color18 = decodeColor("nimbusOrange", -0.015796512F, 0.02094239F, -0.15294117F, 0);
  
  private Color color19 = decodeColor("nimbusOrange", -0.004321605F, 0.02094239F, -0.0745098F, 0);
  
  private Color color20 = decodeColor("nimbusOrange", -0.008021399F, 0.02094239F, -0.10196078F, 0);
  
  private Color color21 = decodeColor("nimbusOrange", -0.011706904F, -0.1790576F, -0.02352941F, 0);
  
  private Color color22 = decodeColor("nimbusOrange", -0.048691254F, 0.02094239F, -0.3019608F, 0);
  
  private Color color23 = decodeColor("nimbusOrange", 0.003940329F, -0.7375322F, 0.17647058F, 0);
  
  private Color color24 = decodeColor("nimbusOrange", 0.005506739F, -0.46764207F, 0.109803915F, 0);
  
  private Color color25 = decodeColor("nimbusOrange", 0.0042127445F, -0.18595415F, 0.04705882F, 0);
  
  private Color color26 = decodeColor("nimbusOrange", 0.0047626942F, 0.02094239F, 0.0039215684F, 0);
  
  private Color color27 = decodeColor("nimbusOrange", 0.0047626942F, -0.15147138F, 0.1607843F, 0);
  
  private Color color28 = decodeColor("nimbusOrange", 0.010665476F, -0.27317524F, 0.25098038F, 0);
  
  private Color color29 = decodeColor("nimbusBlueGrey", -0.54444444F, -0.08748484F, 0.10588235F, 0);
  
  private Color color30 = decodeColor("nimbusOrange", 0.0047626942F, -0.21715283F, 0.23921567F, 0);
  
  private Color color31 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -173);
  
  private Color color32 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -170);
  
  private Color color33 = decodeColor("nimbusOrange", 0.024554357F, -0.8873145F, 0.10588235F, -156);
  
  private Color color34 = decodeColor("nimbusOrange", -0.023593787F, -0.7963165F, 0.02352941F, 0);
  
  private Color color35 = decodeColor("nimbusOrange", -0.010608241F, -0.7760873F, 0.043137252F, 0);
  
  private Color color36 = decodeColor("nimbusOrange", -0.015402906F, -0.7840576F, 0.035294116F, 0);
  
  private Color color37 = decodeColor("nimbusOrange", -0.017112307F, -0.8091547F, 0.058823526F, 0);
  
  private Color color38 = decodeColor("nimbusOrange", -0.07044564F, -0.844649F, -0.019607842F, 0);
  
  private Color color39 = decodeColor("nimbusOrange", -0.009704903F, -0.9381485F, 0.11372548F, 0);
  
  private Color color40 = decodeColor("nimbusOrange", -4.4563413E-4F, -0.86742973F, 0.09411764F, 0);
  
  private Color color41 = decodeColor("nimbusOrange", -4.4563413E-4F, -0.79896283F, 0.07843137F, 0);
  
  private Color color42 = decodeColor("nimbusOrange", 0.0013274103F, -0.7530961F, 0.06666666F, 0);
  
  private Color color43 = decodeColor("nimbusOrange", 0.0013274103F, -0.7644457F, 0.109803915F, 0);
  
  private Color color44 = decodeColor("nimbusOrange", 0.009244293F, -0.78794646F, 0.13333333F, 0);
  
  private Color color45 = decodeColor("nimbusBlueGrey", -0.015872955F, -0.0803539F, 0.16470587F, 0);
  
  private Color color46 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07968931F, 0.14509803F, 0);
  
  private Color color47 = decodeColor("nimbusBlueGrey", 0.02222228F, -0.08779904F, 0.11764705F, 0);
  
  private Color color48 = decodeColor("nimbusBlueGrey", 0.0138888955F, -0.075128086F, 0.14117646F, 0);
  
  private Color color49 = decodeColor("nimbusBlueGrey", 0.0138888955F, -0.07604356F, 0.16470587F, 0);
  
  private Color color50 = decodeColor("nimbusOrange", 0.0014062226F, -0.77816474F, 0.12941176F, 0);
  
  private Object[] componentColors;
  
  public ProgressBarPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
        paintForegroundEnabled(paramGraphics2D);
        break;
      case 4:
        paintForegroundEnabledAndFinished(paramGraphics2D);
        break;
      case 5:
        paintForegroundEnabledAndIndeterminate(paramGraphics2D);
        break;
      case 6:
        paintForegroundDisabled(paramGraphics2D);
        break;
      case 7:
        paintForegroundDisabledAndFinished(paramGraphics2D);
        break;
      case 8:
        paintForegroundDisabledAndIndeterminate(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient2(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(decodeGradient3(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintForegroundEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color17);
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient5(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(decodeGradient6(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintForegroundEnabledAndFinished(Graphics2D paramGraphics2D) {
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color17);
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(decodeGradient5(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(decodeGradient6(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintForegroundEnabledAndIndeterminate(Graphics2D paramGraphics2D) {
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(decodeGradient7(this.rect));
    paramGraphics2D.fill(this.rect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color31);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect9();
    paramGraphics2D.setPaint(this.color32);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintForegroundDisabled(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color33);
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient9(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(decodeGradient10(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintForegroundDisabledAndFinished(Graphics2D paramGraphics2D) {
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color33);
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(decodeGradient9(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(decodeGradient10(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintForegroundDisabledAndIndeterminate(Graphics2D paramGraphics2D) {
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(decodeGradient11(this.rect));
    paramGraphics2D.fill(this.rect);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient12(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(0.4F), decodeY(0.4F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.6F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(0.6F), decodeY(0.6F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(2.4F) - decodeY(0.6F)));
    return this.rect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(1.0F), decodeY(0.21111111F));
    this.path.curveTo(decodeAnchorX(1.0F, -2.0F), decodeAnchorY(0.21111111F, 0.0F), decodeAnchorX(0.21111111F, 0.0F), decodeAnchorY(1.0F, -2.0F), decodeX(0.21111111F), decodeY(1.0F));
    this.path.curveTo(decodeAnchorX(0.21111111F, 0.0F), decodeAnchorY(1.0F, 2.0F), decodeAnchorX(0.21111111F, 0.0F), decodeAnchorY(2.0F, -2.0F), decodeX(0.21111111F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(0.21111111F, 0.0F), decodeAnchorY(2.0F, 2.0F), decodeAnchorX(1.0F, -2.0F), decodeAnchorY(2.8222225F, 0.0F), decodeX(1.0F), decodeY(2.8222225F));
    this.path.curveTo(decodeAnchorX(1.0F, 2.0F), decodeAnchorY(2.8222225F, 0.0F), decodeAnchorX(3.0F, 0.0F), decodeAnchorY(2.8222225F, 0.0F), decodeX(3.0F), decodeY(2.8222225F));
    this.path.lineTo(decodeX(3.0F), decodeY(2.3333333F));
    this.path.lineTo(decodeX(0.6666667F), decodeY(2.3333333F));
    this.path.lineTo(decodeX(0.6666667F), decodeY(0.6666667F));
    this.path.lineTo(decodeX(3.0F), decodeY(0.6666667F));
    this.path.lineTo(decodeX(3.0F), decodeY(0.2F));
    this.path.curveTo(decodeAnchorX(3.0F, 0.0F), decodeAnchorY(0.2F, 0.0F), decodeAnchorX(1.0F, 2.0F), decodeAnchorY(0.21111111F, 0.0F), decodeX(1.0F), decodeY(0.21111111F));
    this.path.closePath();
    return this.path;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(0.6666667F), (decodeX(3.0F) - decodeX(0.6666667F)), (decodeY(2.3333333F) - decodeY(0.6666667F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(1.0F), decodeY(1.0F), (decodeX(2.6666667F) - decodeX(1.0F)), (decodeY(2.0F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(0.9111111F), decodeY(0.21111111F));
    this.path.curveTo(decodeAnchorX(0.9111111F, -2.0F), decodeAnchorY(0.21111111F, 0.0F), decodeAnchorX(0.2F, 0.0F), decodeAnchorY(1.0025641F, -2.0F), decodeX(0.2F), decodeY(1.0025641F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.0444443F));
    this.path.curveTo(decodeAnchorX(0.2F, 0.0F), decodeAnchorY(2.0444443F, 2.0F), decodeAnchorX(0.9666667F, -2.0F), decodeAnchorY(2.8F, 0.0F), decodeX(0.9666667F), decodeY(2.8F));
    this.path.lineTo(decodeX(2.0F), decodeY(2.788889F));
    this.path.curveTo(decodeAnchorX(2.0F, 1.9709293F), decodeAnchorY(2.788889F, 0.01985704F), decodeAnchorX(2.777778F, -0.033333335F), decodeAnchorY(2.0555553F, 1.9333333F), decodeX(2.777778F), decodeY(2.0555553F));
    this.path.lineTo(decodeX(2.788889F), decodeY(1.8051281F));
    this.path.lineTo(decodeX(2.777778F), decodeY(1.2794871F));
    this.path.lineTo(decodeX(2.777778F), decodeY(1.0025641F));
    this.path.curveTo(decodeAnchorX(2.777778F, 0.0042173304F), decodeAnchorY(1.0025641F, -1.9503378F), decodeAnchorX(2.0999997F, 1.9659461F), decodeAnchorY(0.22222222F, 0.017122267F), decodeX(2.0999997F), decodeY(0.22222222F));
    this.path.lineTo(decodeX(0.9111111F), decodeY(0.21111111F));
    this.path.closePath();
    return this.path;
  }
  
  private Rectangle2D decodeRect5() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(0.6666667F), (decodeX(2.3333333F) - decodeX(0.6666667F)), (decodeY(2.3333333F) - decodeY(0.6666667F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect6() {
    this.rect.setRect(decodeX(1.0F), decodeY(1.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(2.0F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect7() {
    this.rect.setRect(decodeX(0.0F), decodeY(0.0F), (decodeX(3.0F) - decodeX(0.0F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(1.4285715F));
    this.path.curveTo(decodeAnchorX(0.0F, 2.6785715F), decodeAnchorY(1.4285715F, 8.881784E-16F), decodeAnchorX(1.3898809F, -6.214286F), decodeAnchorY(0.3452381F, -0.035714287F), decodeX(1.3898809F), decodeY(0.3452381F));
    this.path.lineTo(decodeX(1.5535715F), decodeY(0.3452381F));
    this.path.curveTo(decodeAnchorX(1.5535715F, 8.32967F), decodeAnchorY(0.3452381F, 0.0027472528F), decodeAnchorX(2.3333333F, -5.285714F), decodeAnchorY(1.4285715F, 0.035714287F), decodeX(2.3333333F), decodeY(1.4285715F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.4285715F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.5714285F));
    this.path.lineTo(decodeX(2.3333333F), decodeY(1.5714285F));
    this.path.curveTo(decodeAnchorX(2.3333333F, -5.321429F), decodeAnchorY(1.5714285F, 0.035714287F), decodeAnchorX(1.5535715F, 8.983517F), decodeAnchorY(2.6666667F, 0.03846154F), decodeX(1.5535715F), decodeY(2.6666667F));
    this.path.lineTo(decodeX(1.4077381F), decodeY(2.6666667F));
    this.path.curveTo(decodeAnchorX(1.4077381F, -6.714286F), decodeAnchorY(2.6666667F, 0.0F), decodeAnchorX(0.0F, 2.607143F), decodeAnchorY(1.5714285F, 0.035714287F), decodeX(0.0F), decodeY(1.5714285F));
    this.path.lineTo(decodeX(0.0F), decodeY(1.4285715F));
    this.path.closePath();
    return this.path;
  }
  
  private Rectangle2D decodeRect8() {
    this.rect.setRect(decodeX(1.2916666F), decodeY(0.0F), (decodeX(1.3333334F) - decodeX(1.2916666F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect9() {
    this.rect.setRect(decodeX(1.7083333F), decodeY(0.0F), (decodeX(1.75F) - decodeX(1.7083333F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private Path2D decodePath4() {
    this.path.reset();
    this.path.moveTo(decodeX(0.9888889F), decodeY(0.2F));
    this.path.curveTo(decodeAnchorX(0.9888889F, -2.0F), decodeAnchorY(0.2F, 0.0F), decodeAnchorX(0.2F, 0.0F), decodeAnchorY(0.9888889F, -2.0F), decodeX(0.2F), decodeY(0.9888889F));
    this.path.curveTo(decodeAnchorX(0.2F, 0.0F), decodeAnchorY(0.9888889F, 2.0F), decodeAnchorX(0.2F, 0.0F), decodeAnchorY(1.9974358F, -2.0F), decodeX(0.2F), decodeY(1.9974358F));
    this.path.curveTo(decodeAnchorX(0.2F, 0.0F), decodeAnchorY(1.9974358F, 2.0F), decodeAnchorX(0.9888889F, -2.0F), decodeAnchorY(2.8111107F, 0.0F), decodeX(0.9888889F), decodeY(2.8111107F));
    this.path.curveTo(decodeAnchorX(0.9888889F, 2.0F), decodeAnchorY(2.8111107F, 0.0F), decodeAnchorX(2.5F, 0.0F), decodeAnchorY(2.8F, 0.0F), decodeX(2.5F), decodeY(2.8F));
    this.path.lineTo(decodeX(2.7444446F), decodeY(2.488889F));
    this.path.lineTo(decodeX(2.7555554F), decodeY(1.5794872F));
    this.path.lineTo(decodeX(2.7666664F), decodeY(1.4358975F));
    this.path.lineTo(decodeX(2.7666664F), decodeY(0.62222224F));
    this.path.lineTo(decodeX(2.5999997F), decodeY(0.22222222F));
    this.path.curveTo(decodeAnchorX(2.5999997F, 0.0F), decodeAnchorY(0.22222222F, 0.0F), decodeAnchorX(0.9888889F, 2.0F), decodeAnchorY(0.2F, 0.0F), decodeX(0.9888889F), decodeY(0.2F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath5() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(1.4285715F));
    this.path.curveTo(decodeAnchorX(0.0F, 2.6785715F), decodeAnchorY(1.4285715F, 8.881784E-16F), decodeAnchorX(1.3898809F, -6.357143F), decodeAnchorY(0.3452381F, -0.035714287F), decodeX(1.3898809F), decodeY(0.3452381F));
    this.path.lineTo(decodeX(1.5535715F), decodeY(0.3452381F));
    this.path.curveTo(decodeAnchorX(1.5535715F, 4.0F), decodeAnchorY(0.3452381F, 0.0F), decodeAnchorX(2.3333333F, -5.285714F), decodeAnchorY(1.4285715F, 0.035714287F), decodeX(2.3333333F), decodeY(1.4285715F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.4285715F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.5714285F));
    this.path.lineTo(decodeX(2.3333333F), decodeY(1.5714285F));
    this.path.curveTo(decodeAnchorX(2.3333333F, -5.321429F), decodeAnchorY(1.5714285F, 0.035714287F), decodeAnchorX(1.5535715F, 4.0F), decodeAnchorY(2.6666667F, 0.0F), decodeX(1.5535715F), decodeY(2.6666667F));
    this.path.lineTo(decodeX(1.4077381F), decodeY(2.6666667F));
    this.path.curveTo(decodeAnchorX(1.4077381F, -6.571429F), decodeAnchorY(2.6666667F, -0.035714287F), decodeAnchorX(0.0F, 2.607143F), decodeAnchorY(1.5714285F, 0.035714287F), decodeX(0.0F), decodeY(1.5714285F));
    this.path.lineTo(decodeX(0.0F), decodeY(1.4285715F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color1, decodeColor(this.color1, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.038709678F, 0.05967742F, 0.08064516F, 0.23709677F, 0.3935484F, 0.41612905F, 0.43870968F, 0.67419356F, 0.90967745F, 0.91451615F, 
          0.91935486F }, new Color[] { 
          this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5, decodeColor(this.color5, this.color6, 0.5F), this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color8, 0.5F), 
          this.color8 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.05483871F, 0.5032258F, 0.9516129F }, new Color[] { this.color9, decodeColor(this.color9, this.color10, 0.5F), this.color10 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.038709678F, 0.05967742F, 0.08064516F, 0.23709677F, 0.3935484F, 0.41612905F, 0.43870968F, 0.67419356F, 0.90967745F, 0.91612905F, 
          0.92258066F }, new Color[] { 
          this.color11, decodeColor(this.color11, this.color12, 0.5F), this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14, decodeColor(this.color14, this.color15, 0.5F), this.color15, decodeColor(this.color15, this.color16, 0.5F), 
          this.color16 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.038709678F, 0.05483871F, 0.07096774F, 0.28064516F, 0.4903226F, 0.6967742F, 0.9032258F, 0.9241935F, 0.9451613F }, new Color[] { this.color18, decodeColor(this.color18, this.color19, 0.5F), this.color19, decodeColor(this.color19, this.color20, 0.5F), this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color22, 0.5F), this.color22 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.038709678F, 0.061290324F, 0.08387097F, 0.27258065F, 0.46129033F, 0.4903226F, 0.5193548F, 0.71774197F, 0.91612905F, 0.92419356F, 
          0.93225807F }, new Color[] { 
          this.color23, decodeColor(this.color23, this.color24, 0.5F), this.color24, decodeColor(this.color24, this.color25, 0.5F), this.color25, decodeColor(this.color25, this.color26, 0.5F), this.color26, decodeColor(this.color26, this.color27, 0.5F), this.color27, decodeColor(this.color27, this.color28, 0.5F), 
          this.color28 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.051612902F, 0.06612903F, 0.08064516F, 0.2935484F, 0.5064516F, 0.6903226F, 0.87419355F, 0.88870966F, 0.9032258F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color29, 0.5F), this.color29, decodeColor(this.color29, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.20645161F, 0.41290322F, 0.44193548F, 0.47096774F, 0.7354839F, 1.0F }, new Color[] { this.color24, decodeColor(this.color24, this.color25, 0.5F), this.color25, decodeColor(this.color25, this.color26, 0.5F), this.color26, decodeColor(this.color26, this.color30, 0.5F), this.color30 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.038709678F, 0.05483871F, 0.07096774F, 0.28064516F, 0.4903226F, 0.6967742F, 0.9032258F, 0.9241935F, 0.9451613F }, new Color[] { this.color34, decodeColor(this.color34, this.color35, 0.5F), this.color35, decodeColor(this.color35, this.color36, 0.5F), this.color36, decodeColor(this.color36, this.color37, 0.5F), this.color37, decodeColor(this.color37, this.color38, 0.5F), this.color38 });
  }
  
  private Paint decodeGradient10(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.038709678F, 0.061290324F, 0.08387097F, 0.27258065F, 0.46129033F, 0.4903226F, 0.5193548F, 0.71774197F, 0.91612905F, 0.92419356F, 
          0.93225807F }, new Color[] { 
          this.color39, decodeColor(this.color39, this.color40, 0.5F), this.color40, decodeColor(this.color40, this.color41, 0.5F), this.color41, decodeColor(this.color41, this.color42, 0.5F), this.color42, decodeColor(this.color42, this.color43, 0.5F), this.color43, decodeColor(this.color43, this.color44, 0.5F), 
          this.color44 });
  }
  
  private Paint decodeGradient11(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.051612902F, 0.06612903F, 0.08064516F, 0.2935484F, 0.5064516F, 0.6903226F, 0.87419355F, 0.88870966F, 0.9032258F }, new Color[] { this.color45, decodeColor(this.color45, this.color46, 0.5F), this.color46, decodeColor(this.color46, this.color47, 0.5F), this.color47, decodeColor(this.color47, this.color48, 0.5F), this.color48, decodeColor(this.color48, this.color49, 0.5F), this.color49 });
  }
  
  private Paint decodeGradient12(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.20645161F, 0.41290322F, 0.44193548F, 0.47096774F, 0.7354839F, 1.0F }, new Color[] { this.color40, decodeColor(this.color40, this.color41, 0.5F), this.color41, decodeColor(this.color41, this.color42, 0.5F), this.color42, decodeColor(this.color42, this.color50, 0.5F), this.color50 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ProgressBarPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */