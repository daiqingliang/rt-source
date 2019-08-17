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

final class RadioButtonPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int ICON_DISABLED = 3;
  
  static final int ICON_ENABLED = 4;
  
  static final int ICON_FOCUSED = 5;
  
  static final int ICON_MOUSEOVER = 6;
  
  static final int ICON_MOUSEOVER_FOCUSED = 7;
  
  static final int ICON_PRESSED = 8;
  
  static final int ICON_PRESSED_FOCUSED = 9;
  
  static final int ICON_SELECTED = 10;
  
  static final int ICON_SELECTED_FOCUSED = 11;
  
  static final int ICON_PRESSED_SELECTED = 12;
  
  static final int ICON_PRESSED_SELECTED_FOCUSED = 13;
  
  static final int ICON_MOUSEOVER_SELECTED = 14;
  
  static final int ICON_MOUSEOVER_SELECTED_FOCUSED = 15;
  
  static final int ICON_DISABLED_SELECTED = 16;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.06766917F, 0.07843137F, 0);
  
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0F, -0.06413457F, 0.015686274F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.08466425F, 0.16470587F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0F, -0.070703305F, 0.14117646F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.07052632F, 0.1372549F, 0);
  
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, 0.0F, 0.0F, -112);
  
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.053201474F, -0.12941176F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", 0.0F, 0.006356798F, -0.44313726F, 0);
  
  private Color color10 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10654225F, 0.23921567F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0F, -0.07206477F, 0.17254901F, 0);
  
  private Color color12 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color13 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.027819552F, -0.2235294F, 0);
  
  private Color color14 = decodeColor("nimbusBlueGrey", 0.0F, 0.24241486F, -0.6117647F, 0);
  
  private Color color15 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.10655806F, 0.24313724F, 0);
  
  private Color color16 = decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
  
  private Color color17 = decodeColor("nimbusBlueGrey", 0.08585858F, -0.067389056F, 0.25490195F, 0);
  
  private Color color18 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.10628903F, 0.18039215F, 0);
  
  private Color color19 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color20 = decodeColor("nimbusBlueGrey", 0.055555582F, 0.23947367F, -0.6666667F, 0);
  
  private Color color21 = decodeColor("nimbusBlueGrey", -0.0777778F, -0.06815343F, -0.28235295F, 0);
  
  private Color color22 = decodeColor("nimbusBlueGrey", 0.0F, -0.06866585F, 0.09803921F, 0);
  
  private Color color23 = decodeColor("nimbusBlueGrey", -0.0027777553F, -0.0018306673F, -0.02352941F, 0);
  
  private Color color24 = decodeColor("nimbusBlueGrey", 0.002924025F, -0.02047892F, 0.082352936F, 0);
  
  private Color color25 = decodeColor("nimbusBase", 2.9569864E-4F, -0.36035198F, -0.007843137F, 0);
  
  private Color color26 = decodeColor("nimbusBase", 2.9569864E-4F, 0.019458115F, -0.32156867F, 0);
  
  private Color color27 = decodeColor("nimbusBase", 0.004681647F, -0.6195853F, 0.4235294F, 0);
  
  private Color color28 = decodeColor("nimbusBase", 0.004681647F, -0.56704473F, 0.36470586F, 0);
  
  private Color color29 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  
  private Color color30 = decodeColor("nimbusBase", 5.1498413E-4F, -0.44879842F, 0.29019606F, 0);
  
  private Color color31 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.07243107F, -0.33333334F, 0);
  
  private Color color32 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, 0);
  
  private Color color33 = decodeColor("nimbusBlueGrey", -0.027777791F, 0.07129187F, -0.6156863F, 0);
  
  private Color color34 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.49803925F, 0);
  
  private Color color35 = decodeColor("nimbusBase", 0.0030477047F, -0.1257143F, -0.15686277F, 0);
  
  private Color color36 = decodeColor("nimbusBase", -0.0017285943F, -0.4367347F, 0.21960783F, 0);
  
  private Color color37 = decodeColor("nimbusBase", -0.0010654926F, -0.31349206F, 0.15686274F, 0);
  
  private Color color38 = decodeColor("nimbusBase", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color39 = decodeColor("nimbusBase", 8.05676E-4F, -0.12380952F, 0.109803915F, 0);
  
  private Color color40 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.080223285F, -0.4862745F, 0);
  
  private Color color41 = decodeColor("nimbusBase", -6.374717E-4F, -0.20452163F, -0.12156865F, 0);
  
  private Color color42 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.5058824F, 0);
  
  private Color color43 = decodeColor("nimbusBase", -0.011985004F, -0.6157143F, 0.43137252F, 0);
  
  private Color color44 = decodeColor("nimbusBase", 0.004681647F, -0.56932425F, 0.3960784F, 0);
  
  private Color color45 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  
  private Color color46 = decodeColor("nimbusBase", 5.1498413E-4F, -0.46550155F, 0.372549F, 0);
  
  private Color color47 = decodeColor("nimbusBase", 0.0024294257F, -0.47271872F, 0.34117645F, 0);
  
  private Color color48 = decodeColor("nimbusBase", 0.010237217F, -0.56289876F, 0.2588235F, 0);
  
  private Color color49 = decodeColor("nimbusBase", 0.016586483F, -0.5620301F, 0.19607842F, 0);
  
  private Color color50 = decodeColor("nimbusBase", 0.027408898F, -0.5878882F, 0.35294116F, 0);
  
  private Color color51 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  
  private Color color52 = decodeColor("nimbusBase", 0.021348298F, -0.567841F, 0.31764704F, 0);
  
  private Color color53 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.058170296F, 0.0039215684F, 0);
  
  private Color color54 = decodeColor("nimbusBlueGrey", -0.013888836F, -0.04195489F, -0.058823526F, 0);
  
  private Color color55 = decodeColor("nimbusBlueGrey", 0.009259284F, -0.0147816315F, -0.007843137F, 0);
  
  private Object[] componentColors;
  
  public RadioButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 3:
        painticonDisabled(paramGraphics2D);
        break;
      case 4:
        painticonEnabled(paramGraphics2D);
        break;
      case 5:
        painticonFocused(paramGraphics2D);
        break;
      case 6:
        painticonMouseOver(paramGraphics2D);
        break;
      case 7:
        painticonMouseOverAndFocused(paramGraphics2D);
        break;
      case 8:
        painticonPressed(paramGraphics2D);
        break;
      case 9:
        painticonPressedAndFocused(paramGraphics2D);
        break;
      case 10:
        painticonSelected(paramGraphics2D);
        break;
      case 11:
        painticonSelectedAndFocused(paramGraphics2D);
        break;
      case 12:
        painticonPressedAndSelected(paramGraphics2D);
        break;
      case 13:
        painticonPressedAndSelectedAndFocused(paramGraphics2D);
        break;
      case 14:
        painticonMouseOverAndSelected(paramGraphics2D);
        break;
      case 15:
        painticonMouseOverAndSelectedAndFocused(paramGraphics2D);
        break;
      case 16:
        painticonDisabledAndSelected(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void painticonDisabled(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient1(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient2(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonEnabled(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient3(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient4(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonFocused(Graphics2D paramGraphics2D) {
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
  
  private void painticonMouseOver(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient5(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient6(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonMouseOverAndFocused(Graphics2D paramGraphics2D) {
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
  
  private void painticonPressed(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient7(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient8(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonPressedAndFocused(Graphics2D paramGraphics2D) {
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
  
  private void painticonSelected(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient9(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient10(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonSelectedAndFocused(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient9(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient10(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonPressedAndSelected(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient12(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient13(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient14(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonPressedAndSelectedAndFocused(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient12(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient13(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient14(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonMouseOverAndSelected(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse3();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient15(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient16(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonMouseOverAndSelectedAndFocused(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient15(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient16(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient11(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
  }
  
  private void painticonDisabledAndSelected(Graphics2D paramGraphics2D) {
    this.ellipse = decodeEllipse1();
    paramGraphics2D.setPaint(decodeGradient17(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse2();
    paramGraphics2D.setPaint(decodeGradient18(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
    this.ellipse = decodeEllipse5();
    paramGraphics2D.setPaint(decodeGradient19(this.ellipse));
    paramGraphics2D.fill(this.ellipse);
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
  
  private Ellipse2D decodeEllipse5() {
    this.ellipse.setFrame(decodeX(1.125F), decodeY(1.125F), (decodeX(1.875F) - decodeX(1.125F)), (decodeY(1.875F) - decodeY(1.125F)));
    return this.ellipse;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color1, decodeColor(this.color1, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.21601209F, 0.36858007F, 0.54833835F, 0.72809666F, 0.77492446F, 0.82175225F, 0.91087615F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5, decodeColor(this.color5, this.color6, 0.5F), this.color6 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color8, decodeColor(this.color8, this.color9, 0.5F), this.color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.25009555F, 0.43674698F, 0.48042166F, 0.52409637F, 0.70481926F, 0.88554215F }, new Color[] { this.color10, decodeColor(this.color10, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color11, 0.5F), this.color11 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.21601209F, 0.36858007F, 0.54833835F, 0.72809666F, 0.77492446F, 0.82175225F, 0.91087615F, 1.0F }, new Color[] { this.color15, decodeColor(this.color15, this.color16, 0.5F), this.color16, decodeColor(this.color16, this.color16, 0.5F), this.color16, decodeColor(this.color16, this.color17, 0.5F), this.color17, decodeColor(this.color17, this.color18, 0.5F), this.color18 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.06344411F, 0.20792687F, 0.35240963F, 0.45030123F, 0.5481928F, 0.748494F, 0.9487952F }, new Color[] { this.color22, decodeColor(this.color22, this.color23, 0.5F), this.color23, decodeColor(this.color23, this.color23, 0.5F), this.color23, decodeColor(this.color23, this.color24, 0.5F), this.color24 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color25, decodeColor(this.color25, this.color26, 0.5F), this.color26 });
  }
  
  private Paint decodeGradient10(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0813253F, 0.100903615F, 0.12048193F, 0.28915662F, 0.45783132F, 0.6159638F, 0.77409637F, 0.82981926F, 0.88554215F }, new Color[] { this.color27, decodeColor(this.color27, this.color28, 0.5F), this.color28, decodeColor(this.color28, this.color29, 0.5F), this.color29, decodeColor(this.color29, this.color29, 0.5F), this.color29, decodeColor(this.color29, this.color30, 0.5F), this.color30 });
  }
  
  private Paint decodeGradient11(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.50490195F * f3 + f1, 0.0F * f4 + f2, 0.49509802F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23192771F, 0.46385542F, 0.73192775F, 1.0F }, new Color[] { this.color31, decodeColor(this.color31, this.color32, 0.5F), this.color32, decodeColor(this.color32, this.color33, 0.5F), this.color33 });
  }
  
  private Paint decodeGradient12(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color34, decodeColor(this.color34, this.color26, 0.5F), this.color26 });
  }
  
  private Paint decodeGradient13(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.039156627F, 0.07831325F, 0.11746988F, 0.2876506F, 0.45783132F, 0.56174695F, 0.66566265F, 0.7756024F, 0.88554215F }, new Color[] { this.color36, decodeColor(this.color36, this.color37, 0.5F), this.color37, decodeColor(this.color37, this.color38, 0.5F), this.color38, decodeColor(this.color38, this.color38, 0.5F), this.color38, decodeColor(this.color38, this.color39, 0.5F), this.color39 });
  }
  
  private Paint decodeGradient14(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.50490195F * f3 + f1, 0.0F * f4 + f2, 0.49509802F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23192771F, 0.46385542F, 0.73192775F, 1.0F }, new Color[] { this.color40, decodeColor(this.color40, this.color32, 0.5F), this.color32, decodeColor(this.color32, this.color33, 0.5F), this.color33 });
  }
  
  private Paint decodeGradient15(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color41, decodeColor(this.color41, this.color42, 0.5F), this.color42 });
  }
  
  private Paint decodeGradient16(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.0813253F, 0.100903615F, 0.12048193F, 0.20180723F, 0.28313252F, 0.49246985F, 0.7018072F, 0.7560241F, 0.810241F, 0.84789157F, 
          0.88554215F }, new Color[] { 
          this.color43, decodeColor(this.color43, this.color44, 0.5F), this.color44, decodeColor(this.color44, this.color45, 0.5F), this.color45, decodeColor(this.color45, this.color45, 0.5F), this.color45, decodeColor(this.color45, this.color46, 0.5F), this.color46, decodeColor(this.color46, this.color47, 0.5F), 
          this.color47 });
  }
  
  private Paint decodeGradient17(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49789914F * f3 + f1, -0.004201681F * f4 + f2, 0.5F * f3 + f1, 0.9978992F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color48, decodeColor(this.color48, this.color49, 0.5F), this.color49 });
  }
  
  private Paint decodeGradient18(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.49754903F * f3 + f1, 0.004901961F * f4 + f2, 0.50735295F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0813253F, 0.2695783F, 0.45783132F, 0.67168677F, 0.88554215F }, new Color[] { this.color50, decodeColor(this.color50, this.color51, 0.5F), this.color51, decodeColor(this.color51, this.color52, 0.5F), this.color52 });
  }
  
  private Paint decodeGradient19(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.50490195F * f3 + f1, 0.0F * f4 + f2, 0.49509802F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.23192771F, 0.46385542F, 0.73192775F, 1.0F }, new Color[] { this.color53, decodeColor(this.color53, this.color54, 0.5F), this.color54, decodeColor(this.color54, this.color55, 0.5F), this.color55 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\RadioButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */