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

final class ToolBarToggleButtonPainter extends AbstractRegionPainter {
  static final int BACKGROUND_ENABLED = 1;
  
  static final int BACKGROUND_FOCUSED = 2;
  
  static final int BACKGROUND_MOUSEOVER = 3;
  
  static final int BACKGROUND_MOUSEOVER_FOCUSED = 4;
  
  static final int BACKGROUND_PRESSED = 5;
  
  static final int BACKGROUND_PRESSED_FOCUSED = 6;
  
  static final int BACKGROUND_SELECTED = 7;
  
  static final int BACKGROUND_SELECTED_FOCUSED = 8;
  
  static final int BACKGROUND_PRESSED_SELECTED = 9;
  
  static final int BACKGROUND_PRESSED_SELECTED_FOCUSED = 10;
  
  static final int BACKGROUND_MOUSEOVER_SELECTED = 11;
  
  static final int BACKGROUND_MOUSEOVER_SELECTED_FOCUSED = 12;
  
  static final int BACKGROUND_DISABLED_SELECTED = 13;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color2 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.06885965F, -0.36862746F, -153);
  
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.020974077F, -0.21960783F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, 0.11169591F, -0.53333336F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10658931F, 0.25098038F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.098526314F, 0.2352941F, 0);
  
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.07333623F, 0.20392156F, 0);
  
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -86);
  
  private Color color10 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.060526315F, -0.3529412F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0F, -0.064372465F, -0.2352941F, 0);
  
  private Color color12 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.0595709F, -0.12941176F, 0);
  
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0F, -0.061075766F, -0.031372547F, 0);
  
  private Color color14 = decodeColor("nimbusBlueGrey", 0.0F, -0.06080256F, -0.035294116F, 0);
  
  private Color color15 = decodeColor("nimbusBlueGrey", 0.0F, -0.06472479F, -0.23137254F, 0);
  
  private Color color16 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.06959064F, -0.0745098F, 0);
  
  private Color color17 = decodeColor("nimbusBlueGrey", 0.0138888955F, -0.06401469F, -0.07058823F, 0);
  
  private Color color18 = decodeColor("nimbusBlueGrey", 0.0F, -0.06530018F, 0.035294116F, 0);
  
  private Color color19 = decodeColor("nimbusBlueGrey", 0.0F, -0.06507177F, 0.031372547F, 0);
  
  private Color color20 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.05338346F, -0.47058824F, 0);
  
  private Color color21 = decodeColor("nimbusBlueGrey", 0.0F, -0.049301825F, -0.36078432F, 0);
  
  private Color color22 = decodeColor("nimbusBlueGrey", -0.018518567F, -0.03909774F, -0.2509804F, 0);
  
  private Color color23 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.040013492F, -0.13333333F, 0);
  
  private Color color24 = decodeColor("nimbusBlueGrey", 0.01010108F, -0.039558575F, -0.1372549F, 0);
  
  private Color color25 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -220);
  
  private Color color26 = decodeColor("nimbusBlueGrey", 0.0F, -0.066408664F, 0.054901958F, 0);
  
  private Color color27 = decodeColor("nimbusBlueGrey", 0.0F, -0.06807348F, 0.086274505F, 0);
  
  private Color color28 = decodeColor("nimbusBlueGrey", 0.0F, -0.06924191F, 0.109803915F, 0);
  
  private Object[] componentColors;
  
  public ToolBarToggleButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 2:
        paintBackgroundFocused(paramGraphics2D);
        break;
      case 3:
        paintBackgroundMouseOver(paramGraphics2D);
        break;
      case 4:
        paintBackgroundMouseOverAndFocused(paramGraphics2D);
        break;
      case 5:
        paintBackgroundPressed(paramGraphics2D);
        break;
      case 6:
        paintBackgroundPressedAndFocused(paramGraphics2D);
        break;
      case 7:
        paintBackgroundSelected(paramGraphics2D);
        break;
      case 8:
        paintBackgroundSelectedAndFocused(paramGraphics2D);
        break;
      case 9:
        paintBackgroundPressedAndSelected(paramGraphics2D);
        break;
      case 10:
        paintBackgroundPressedAndSelectedAndFocused(paramGraphics2D);
        break;
      case 11:
        paintBackgroundMouseOverAndSelected(paramGraphics2D);
        break;
      case 12:
        paintBackgroundMouseOverAndSelectedAndFocused(paramGraphics2D);
        break;
      case 13:
        paintBackgroundDisabledAndSelected(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color2);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundMouseOverAndFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient3(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundPressedAndFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect8();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient3(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundSelected(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundSelectedAndFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect8();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundPressedAndSelected(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient7(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundPressedAndSelectedAndFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect8();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient7(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundMouseOverAndSelected(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient3(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundMouseOverAndSelectedAndFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect8();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient3(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundDisabledAndSelected(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(this.color25);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect6();
    paramGraphics2D.setPaint(decodeGradient9(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect7();
    paramGraphics2D.setPaint(decodeGradient10(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(1.4133738F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(1.9893618F), decodeY(0.120000005F));
    this.path.curveTo(decodeAnchorX(1.9893618F, 3.0F), decodeAnchorY(0.120000005F, 0.0F), decodeAnchorX(2.8857148F, 0.0F), decodeAnchorY(1.0416666F, -3.0F), decodeX(2.8857148F), decodeY(1.0416666F));
    this.path.lineTo(decodeX(2.9F), decodeY(1.9166667F));
    this.path.curveTo(decodeAnchorX(2.9F, 0.0F), decodeAnchorY(1.9166667F, 3.0F), decodeAnchorX(1.9893618F, 3.0F), decodeAnchorY(2.6714287F, 0.0F), decodeX(1.9893618F), decodeY(2.6714287F));
    this.path.lineTo(decodeX(1.0106384F), decodeY(2.6714287F));
    this.path.curveTo(decodeAnchorX(1.0106384F, -3.0F), decodeAnchorY(2.6714287F, 0.0F), decodeAnchorX(0.120000005F, 0.0F), decodeAnchorY(1.9166667F, 3.0F), decodeX(0.120000005F), decodeY(1.9166667F));
    this.path.lineTo(decodeX(0.120000005F), decodeY(1.0446429F));
    this.path.curveTo(decodeAnchorX(0.120000005F, 0.0F), decodeAnchorY(1.0446429F, -3.0F), decodeAnchorX(1.0106384F, -3.0F), decodeAnchorY(0.120000005F, 0.0F), decodeX(1.0106384F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(1.4148936F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(1.4148936F), decodeY(0.4857143F));
    this.path.lineTo(decodeX(1.0106384F), decodeY(0.4857143F));
    this.path.curveTo(decodeAnchorX(1.0106384F, -1.9285715F), decodeAnchorY(0.4857143F, 0.0F), decodeAnchorX(0.47142857F, -0.044279482F), decodeAnchorY(1.0386904F, -2.429218F), decodeX(0.47142857F), decodeY(1.0386904F));
    this.path.lineTo(decodeX(0.47142857F), decodeY(1.9166667F));
    this.path.curveTo(decodeAnchorX(0.47142857F, 0.0F), decodeAnchorY(1.9166667F, 2.2142856F), decodeAnchorX(1.0106384F, -1.7857143F), decodeAnchorY(2.3142858F, 0.0F), decodeX(1.0106384F), decodeY(2.3142858F));
    this.path.lineTo(decodeX(1.9893618F), decodeY(2.3142858F));
    this.path.curveTo(decodeAnchorX(1.9893618F, 2.0714285F), decodeAnchorY(2.3142858F, 0.0F), decodeAnchorX(2.5F, 0.0F), decodeAnchorY(1.9166667F, 2.2142856F), decodeX(2.5F), decodeY(1.9166667F));
    this.path.lineTo(decodeX(2.5142853F), decodeY(1.0416666F));
    this.path.curveTo(decodeAnchorX(2.5142853F, 0.0F), decodeAnchorY(1.0416666F, -2.142857F), decodeAnchorX(1.9901216F, 2.142857F), decodeAnchorY(0.47142857F, 0.0F), decodeX(1.9901216F), decodeY(0.47142857F));
    this.path.lineTo(decodeX(1.4148936F), decodeY(0.4857143F));
    this.path.lineTo(decodeX(1.4133738F), decodeY(0.120000005F));
    this.path.closePath();
    return this.path;
  }
  
  private RoundRectangle2D decodeRoundRect1() {
    this.roundRect.setRoundRect(decodeX(0.4F), decodeY(0.6F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.6F) - decodeY(0.6F)), 12.0D, 12.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2() {
    this.roundRect.setRoundRect(decodeX(0.4F), decodeY(0.4F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.4F) - decodeY(0.4F)), 12.0D, 12.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3() {
    this.roundRect.setRoundRect(decodeX(0.6F), decodeY(0.6F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(2.2F) - decodeY(0.6F)), 9.0D, 9.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4() {
    this.roundRect.setRoundRect(decodeX(0.120000005F), decodeY(0.120000005F), (decodeX(2.8800004F) - decodeX(0.120000005F)), (decodeY(2.6800003F) - decodeY(0.120000005F)), 13.0D, 13.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect5() {
    this.roundRect.setRoundRect(decodeX(0.4F), decodeY(0.6F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.6F) - decodeY(0.6F)), 10.0D, 10.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect6() {
    this.roundRect.setRoundRect(decodeX(0.4F), decodeY(0.4F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.4F) - decodeY(0.4F)), 10.0D, 10.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect7() {
    this.roundRect.setRoundRect(decodeX(0.6F), decodeY(0.6F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(2.2F) - decodeY(0.6F)), 8.0D, 8.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect8() {
    this.roundRect.setRoundRect(decodeX(0.120000005F), decodeY(0.120000005F), (decodeX(2.8800004F) - decodeX(0.120000005F)), (decodeY(2.6799998F) - decodeY(0.120000005F)), 13.0D, 13.0D);
    return this.roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.09F, 0.52F, 0.95F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.0F, 0.03F, 0.06F, 0.33F, 0.6F, 0.65F, 0.7F, 0.825F, 0.95F, 0.975F, 
          1.0F }, new Color[] { 
          this.color5, decodeColor(this.color5, this.color6, 0.5F), this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8, decodeColor(this.color8, this.color8, 0.5F), 
          this.color8 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0041667F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25126263F * f3 + f1, 1.0092592F * f4 + f2, new float[] { 0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F }, new Color[] { this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0041667F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color15, decodeColor(this.color15, this.color16, 0.5F), this.color16 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25126263F * f3 + f1, 1.0092592F * f4 + f2, new float[] { 0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F }, new Color[] { this.color17, decodeColor(this.color17, this.color18, 0.5F), this.color18, decodeColor(this.color18, this.color19, 0.5F), this.color19 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0041667F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25126263F * f3 + f1, 1.0092592F * f4 + f2, new float[] { 0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F }, new Color[] { this.color22, decodeColor(this.color22, this.color23, 0.5F), this.color23, decodeColor(this.color23, this.color24, 0.5F), this.color24 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0041667F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color26, decodeColor(this.color26, this.color27, 0.5F), this.color27 });
  }
  
  private Paint decodeGradient10(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25126263F * f3 + f1, 1.0092592F * f4 + f2, new float[] { 0.0F, 0.06684492F, 0.13368984F, 0.56684494F, 1.0F }, new Color[] { this.color27, decodeColor(this.color27, this.color28, 0.5F), this.color28, decodeColor(this.color28, this.color28, 0.5F), this.color28 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ToolBarToggleButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */