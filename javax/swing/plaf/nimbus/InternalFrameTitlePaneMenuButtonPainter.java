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

final class InternalFrameTitlePaneMenuButtonPainter extends AbstractRegionPainter {
  static final int ICON_ENABLED = 1;
  
  static final int ICON_DISABLED = 2;
  
  static final int ICON_MOUSEOVER = 3;
  
  static final int ICON_PRESSED = 4;
  
  static final int ICON_ENABLED_WINDOWNOTFOCUSED = 5;
  
  static final int ICON_MOUSEOVER_WINDOWNOTFOCUSED = 6;
  
  static final int ICON_PRESSED_WINDOWNOTFOCUSED = 7;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -185);
  
  private Color color2 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.5019608F, 0);
  
  private Color color3 = decodeColor("nimbusBase", 0.030543745F, -0.3835404F, -0.09803924F, 0);
  
  private Color color4 = decodeColor("nimbusBase", 0.029191494F, -0.53801316F, 0.13333333F, 0);
  
  private Color color5 = decodeColor("nimbusBase", 0.030543745F, -0.3857143F, -0.09411767F, 0);
  
  private Color color6 = decodeColor("nimbusBase", 0.030543745F, -0.43148893F, 0.007843137F, 0);
  
  private Color color7 = decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, -132);
  
  private Color color8 = decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, 0);
  
  private Color color9 = decodeColor("nimbusBase", 0.029191494F, -0.24935067F, -0.20392159F, -123);
  
  private Color color10 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -208);
  
  private Color color12 = decodeColor("nimbusBase", 0.02551502F, -0.5942635F, 0.20784312F, 0);
  
  private Color color13 = decodeColor("nimbusBase", 0.032459438F, -0.5490091F, 0.12941176F, 0);
  
  private Color color14 = decodeColor("nimbusBase", 0.032459438F, -0.5469569F, 0.11372548F, 0);
  
  private Color color15 = decodeColor("nimbusBase", 0.032459438F, -0.5760128F, 0.23921567F, 0);
  
  private Color color16 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.4901961F, 0);
  
  private Color color17 = decodeColor("nimbusBase", 0.032459438F, -0.1857143F, -0.23529413F, 0);
  
  private Color color18 = decodeColor("nimbusBase", 0.029191494F, -0.5438224F, 0.17647058F, 0);
  
  private Color color19 = decodeColor("nimbusBase", 0.030543745F, -0.41929638F, -0.02352941F, 0);
  
  private Color color20 = decodeColor("nimbusBase", 0.030543745F, -0.45559007F, 0.082352936F, 0);
  
  private Color color21 = decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, -132);
  
  private Color color22 = decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, 0);
  
  private Color color23 = decodeColor("nimbusBase", 0.03409344F, -0.329408F, -0.11372551F, -123);
  
  private Color color24 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  
  private Color color25 = decodeColor("nimbusBase", 0.031104386F, 0.12354499F, -0.33725494F, 0);
  
  private Color color26 = decodeColor("nimbusBase", 0.032459438F, -0.4592437F, -0.015686274F, 0);
  
  private Color color27 = decodeColor("nimbusBase", 0.029191494F, -0.2579365F, -0.19607845F, 0);
  
  private Color color28 = decodeColor("nimbusBase", 0.03409344F, -0.3149596F, -0.13333336F, 0);
  
  private Color color29 = decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, -132);
  
  private Color color30 = decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, 0);
  
  private Color color31 = decodeColor("nimbusBase", 0.029681683F, 0.07857144F, -0.3294118F, -123);
  
  private Color color32 = decodeColor("nimbusBase", 0.032459438F, -0.53637654F, 0.043137252F, 0);
  
  private Color color33 = decodeColor("nimbusBase", 0.032459438F, -0.49935067F, -0.11764708F, 0);
  
  private Color color34 = decodeColor("nimbusBase", 0.021348298F, -0.6133929F, 0.32941175F, 0);
  
  private Color color35 = decodeColor("nimbusBase", 0.042560518F, -0.5804379F, 0.23137254F, 0);
  
  private Color color36 = decodeColor("nimbusBase", 0.032459438F, -0.57417583F, 0.21568626F, 0);
  
  private Color color37 = decodeColor("nimbusBase", 0.027408898F, -0.5784226F, 0.20392156F, -132);
  
  private Color color38 = decodeColor("nimbusBase", 0.042560518F, -0.5665319F, 0.0745098F, 0);
  
  private Color color39 = decodeColor("nimbusBase", 0.036732912F, -0.5642857F, 0.16470587F, -123);
  
  private Color color40 = decodeColor("nimbusBase", 0.021348298F, -0.54480517F, -0.11764708F, 0);
  
  private Object[] componentColors;
  
  public InternalFrameTitlePaneMenuButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 1:
        painticonEnabled(paramGraphics2D);
        break;
      case 2:
        painticonDisabled(paramGraphics2D);
        break;
      case 3:
        painticonMouseOver(paramGraphics2D);
        break;
      case 4:
        painticonPressed(paramGraphics2D);
        break;
      case 5:
        painticonEnabledAndWindowNotFocused(paramGraphics2D);
        break;
      case 6:
        painticonMouseOverAndWindowNotFocused(paramGraphics2D);
        break;
      case 7:
        painticonPressedAndWindowNotFocused(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void painticonEnabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.path);
  }
  
  private void painticonDisabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color11);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color15);
    paramGraphics2D.fill(this.path);
  }
  
  private void painticonMouseOver(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.path);
  }
  
  private void painticonPressed(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient9(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient10(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.path);
  }
  
  private void painticonEnabledAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient11(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient12(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient13(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color40);
    paramGraphics2D.fill(this.path);
  }
  
  private void painticonMouseOverAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.path);
  }
  
  private void painticonPressedAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient9(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient10(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.path);
  }
  
  private RoundRectangle2D decodeRoundRect1() {
    this.roundRect.setRoundRect(decodeX(1.0F), decodeY(1.6111112F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(2.0F) - decodeY(1.6111112F)), 6.0D, 6.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2() {
    this.roundRect.setRoundRect(decodeX(1.0F), decodeY(1.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(1.9444444F) - decodeY(1.0F)), 8.600000381469727D, 8.600000381469727D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3() {
    this.roundRect.setRoundRect(decodeX(1.0526316F), decodeY(1.0555556F), (decodeX(1.9473684F) - decodeX(1.0526316F)), (decodeY(1.8888888F) - decodeY(1.0555556F)), 6.75D, 6.75D);
    return this.roundRect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(1.3157895F), decodeY(1.4444444F));
    this.path.lineTo(decodeX(1.6842105F), decodeY(1.4444444F));
    this.path.lineTo(decodeX(1.5013158F), decodeY(1.7208333F));
    this.path.lineTo(decodeX(1.3157895F), decodeY(1.4444444F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(1.3157895F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.6842105F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.5F), decodeY(1.6083333F));
    this.path.lineTo(decodeX(1.3157895F), decodeY(1.3333334F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(1.3157895F), decodeY(1.3888888F));
    this.path.lineTo(decodeX(1.6842105F), decodeY(1.3888888F));
    this.path.lineTo(decodeX(1.4952153F), decodeY(1.655303F));
    this.path.lineTo(decodeX(1.3157895F), decodeY(1.3888888F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5, decodeColor(this.color5, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color6, 0.5F), this.color6 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8, decodeColor(this.color8, this.color9, 0.5F), this.color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.31107953F, 0.62215906F, 0.8110795F, 1.0F }, new Color[] { this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color16, decodeColor(this.color16, this.color17, 0.5F), this.color17 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color18, decodeColor(this.color18, this.color19, 0.5F), this.color19, decodeColor(this.color19, this.color19, 0.5F), this.color19, decodeColor(this.color19, this.color20, 0.5F), this.color20 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { this.color21, decodeColor(this.color21, this.color22, 0.5F), this.color22, decodeColor(this.color22, this.color23, 0.5F), this.color23 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color24, decodeColor(this.color24, this.color25, 0.5F), this.color25 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color26, decodeColor(this.color26, this.color27, 0.5F), this.color27, decodeColor(this.color27, this.color27, 0.5F), this.color27, decodeColor(this.color27, this.color28, 0.5F), this.color28 });
  }
  
  private Paint decodeGradient10(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { this.color29, decodeColor(this.color29, this.color30, 0.5F), this.color30, decodeColor(this.color30, this.color31, 0.5F), this.color31 });
  }
  
  private Paint decodeGradient11(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color32, decodeColor(this.color32, this.color33, 0.5F), this.color33 });
  }
  
  private Paint decodeGradient12(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color34, decodeColor(this.color34, this.color35, 0.5F), this.color35, decodeColor(this.color35, this.color36, 0.5F), this.color36, decodeColor(this.color36, this.color15, 0.5F), this.color15 });
  }
  
  private Paint decodeGradient13(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.50714284F * f3 + f1, 0.095F * f4 + f2, 0.49285713F * f3 + f1, 0.91F * f4 + f2, new float[] { 0.0F, 0.24289773F, 0.48579547F, 0.74289775F, 1.0F }, new Color[] { this.color37, decodeColor(this.color37, this.color38, 0.5F), this.color38, decodeColor(this.color38, this.color39, 0.5F), this.color39 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\InternalFrameTitlePaneMenuButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */