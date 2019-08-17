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

final class InternalFrameTitlePaneCloseButtonPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_MOUSEOVER = 3;
  
  static final int BACKGROUND_PRESSED = 4;
  
  static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED = 5;
  
  static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED = 6;
  
  static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED = 7;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusRed", 0.5893519F, -0.75736576F, 0.09411764F, 0);
  
  private Color color2 = decodeColor("nimbusRed", 0.5962963F, -0.71005917F, 0.0F, 0);
  
  private Color color3 = decodeColor("nimbusRed", 0.6005698F, -0.7200287F, -0.015686274F, -122);
  
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.062449392F, 0.07058823F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -185);
  
  private Color color6 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.4431373F, 0);
  
  private Color color7 = decodeColor("nimbusRed", -2.7342606E-4F, 0.13829035F, -0.039215684F, 0);
  
  private Color color8 = decodeColor("nimbusRed", 6.890595E-4F, -0.36665577F, 0.11764705F, 0);
  
  private Color color9 = decodeColor("nimbusRed", -0.001021713F, 0.101804554F, -0.031372547F, 0);
  
  private Color color10 = decodeColor("nimbusRed", -2.7342606E-4F, 0.13243341F, -0.035294116F, 0);
  
  private Color color11 = decodeColor("nimbusRed", -2.7342606E-4F, 0.002258718F, 0.06666666F, 0);
  
  private Color color12 = decodeColor("nimbusRed", 0.0056530247F, 0.0040003657F, -0.38431373F, -122);
  
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color14 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.3882353F, 0);
  
  private Color color15 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.13333333F, 0);
  
  private Color color16 = decodeColor("nimbusRed", 6.890595E-4F, -0.38929275F, 0.1607843F, 0);
  
  private Color color17 = decodeColor("nimbusRed", 2.537202E-5F, 0.012294531F, 0.043137252F, 0);
  
  private Color color18 = decodeColor("nimbusRed", -2.7342606E-4F, 0.033585668F, 0.039215684F, 0);
  
  private Color color19 = decodeColor("nimbusRed", -2.7342606E-4F, -0.07198727F, 0.14117646F, 0);
  
  private Color color20 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, 0.0039215684F, -122);
  
  private Color color21 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -140);
  
  private Color color22 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.49411768F, 0);
  
  private Color color23 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.20392159F, 0);
  
  private Color color24 = decodeColor("nimbusRed", -0.014814814F, -0.21260965F, 0.019607842F, 0);
  
  private Color color25 = decodeColor("nimbusRed", -0.014814814F, 0.17340565F, -0.09803921F, 0);
  
  private Color color26 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.10588235F, 0);
  
  private Color color27 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.04705882F, 0);
  
  private Color color28 = decodeColor("nimbusRed", -0.014814814F, 0.20118344F, -0.31764707F, -122);
  
  private Color color29 = decodeColor("nimbusRed", 0.5962963F, -0.6994788F, -0.07058823F, 0);
  
  private Color color30 = decodeColor("nimbusRed", 0.5962963F, -0.66245294F, -0.23137257F, 0);
  
  private Color color31 = decodeColor("nimbusRed", 0.58518517F, -0.77649516F, 0.21568626F, 0);
  
  private Color color32 = decodeColor("nimbusRed", 0.5962963F, -0.7372781F, 0.10196078F, 0);
  
  private Color color33 = decodeColor("nimbusRed", 0.5962963F, -0.73911506F, 0.12549019F, 0);
  
  private Color color34 = decodeColor("nimbusBlueGrey", 0.0F, -0.027957506F, -0.31764707F, 0);
  
  private Object[] componentColors;
  
  public InternalFrameTitlePaneCloseButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
        paintBackgroundMouseOver(paramGraphics2D);
        break;
      case 4:
        paintBackgroundPressed(paramGraphics2D);
        break;
      case 5:
        paintBackgroundEnabledAndWindowNotFocused(paramGraphics2D);
        break;
      case 6:
        paintBackgroundMouseOverAndWindowNotFocused(paramGraphics2D);
        break;
      case 7:
        paintBackgroundPressedAndWindowNotFocused(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient1(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color3);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient2(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient3(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color20);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(this.color21);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient7(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color28);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabledAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient9(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color34);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color20);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressedAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(this.color21);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient7(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color28);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.path);
  }
  
  private RoundRectangle2D decodeRoundRect1() {
    this.roundRect.setRoundRect(decodeX(1.0F), decodeY(1.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(1.9444444F) - decodeY(1.0F)), 8.600000381469727D, 8.600000381469727D);
    return this.roundRect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(1.25F), decodeY(1.7373737F));
    this.path.lineTo(decodeX(1.3002392F), decodeY(1.794192F));
    this.path.lineTo(decodeX(1.5047847F), decodeY(1.5909091F));
    this.path.lineTo(decodeX(1.6842105F), decodeY(1.7954545F));
    this.path.lineTo(decodeX(1.7595694F), decodeY(1.719697F));
    this.path.lineTo(decodeX(1.5956938F), decodeY(1.5239899F));
    this.path.lineTo(decodeX(1.7535884F), decodeY(1.3409091F));
    this.path.lineTo(decodeX(1.6830144F), decodeY(1.2537879F));
    this.path.lineTo(decodeX(1.5083733F), decodeY(1.4406565F));
    this.path.lineTo(decodeX(1.3301436F), decodeY(1.2563131F));
    this.path.lineTo(decodeX(1.257177F), decodeY(1.3320707F));
    this.path.lineTo(decodeX(1.4270334F), decodeY(1.5252526F));
    this.path.lineTo(decodeX(1.25F), decodeY(1.7373737F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(1.257177F), decodeY(1.2828283F));
    this.path.lineTo(decodeX(1.3217703F), decodeY(1.2133838F));
    this.path.lineTo(decodeX(1.5F), decodeY(1.4040405F));
    this.path.lineTo(decodeX(1.673445F), decodeY(1.2108586F));
    this.path.lineTo(decodeX(1.7440192F), decodeY(1.2853535F));
    this.path.lineTo(decodeX(1.5669856F), decodeY(1.4709597F));
    this.path.lineTo(decodeX(1.7488039F), decodeY(1.6527778F));
    this.path.lineTo(decodeX(1.673445F), decodeY(1.7398989F));
    this.path.lineTo(decodeX(1.4988039F), decodeY(1.5416667F));
    this.path.lineTo(decodeX(1.3313397F), decodeY(1.7424242F));
    this.path.lineTo(decodeX(1.2523923F), decodeY(1.6565657F));
    this.path.lineTo(decodeX(1.4366028F), decodeY(1.4722222F));
    this.path.lineTo(decodeX(1.257177F), decodeY(1.2828283F));
    this.path.closePath();
    return this.path;
  }
  
  private RoundRectangle2D decodeRoundRect2() {
    this.roundRect.setRoundRect(decodeX(1.0F), decodeY(1.6111112F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(2.0F) - decodeY(1.6111112F)), 6.0D, 6.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3() {
    this.roundRect.setRoundRect(decodeX(1.0526316F), decodeY(1.0530303F), (decodeX(1.9473684F) - decodeX(1.0526316F)), (decodeY(1.8863636F) - decodeY(1.0530303F)), 6.75D, 6.75D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4() {
    this.roundRect.setRoundRect(decodeX(1.0526316F), decodeY(1.0517677F), (decodeX(1.9473684F) - decodeX(1.0526316F)), (decodeY(1.8851011F) - decodeY(1.0517677F)), 6.75D, 6.75D);
    return this.roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color1, decodeColor(this.color1, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color8, decodeColor(this.color8, this.color9, 0.5F), this.color9, decodeColor(this.color9, this.color10, 0.5F), this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color14, decodeColor(this.color14, this.color15, 0.5F), this.color15 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.81480503F, 0.97904193F }, new Color[] { this.color16, decodeColor(this.color16, this.color17, 0.5F), this.color17, decodeColor(this.color17, this.color18, 0.5F), this.color18, decodeColor(this.color18, this.color19, 0.5F), this.color19 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color22, decodeColor(this.color22, this.color23, 0.5F), this.color23 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.81630206F, 0.98203593F }, new Color[] { this.color24, decodeColor(this.color24, this.color25, 0.5F), this.color25, decodeColor(this.color25, this.color26, 0.5F), this.color26, decodeColor(this.color26, this.color27, 0.5F), this.color27 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color29, decodeColor(this.color29, this.color30, 0.5F), this.color30 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.24101797F, 0.48203593F, 0.5838324F, 0.6856288F, 0.8428144F, 1.0F }, new Color[] { this.color31, decodeColor(this.color31, this.color32, 0.5F), this.color32, decodeColor(this.color32, this.color32, 0.5F), this.color32, decodeColor(this.color32, this.color33, 0.5F), this.color33 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\InternalFrameTitlePaneCloseButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */