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

final class SpinnerPanelSpinnerFormattedTextFieldPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_FOCUSED = 3;
  
  static final int BACKGROUND_SELECTED = 4;
  
  static final int BACKGROUND_SELECTED_FOCUSED = 5;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, -237);
  
  private Color color2 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.07187897F, 0.06666666F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07703349F, 0.0745098F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07968931F, 0.14509803F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07856284F, 0.11372548F, 0);
  
  private Color color6 = decodeColor("nimbusBase", 0.040395975F, -0.60315615F, 0.29411763F, 0);
  
  private Color color7 = decodeColor("nimbusBase", 0.016586483F, -0.6051466F, 0.3490196F, 0);
  
  private Color color8 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.0965403F, -0.18431371F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.1048766F, -0.08F, 0);
  
  private Color color10 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.105624355F, 0.054901958F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color12 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.105344966F, 0.011764705F, 0);
  
  private Color color13 = decodeColor("nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color14 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color15 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.1048766F, -0.05098039F, 0);
  
  private Object[] componentColors;
  
  public SpinnerPanelSpinnerFormattedTextFieldPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
        paintBackgroundSelected(paramGraphics2D);
        break;
      case 5:
        paintBackgroundSelectedAndFocused(paramGraphics2D);
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
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color6);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient3(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D) {
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(this.color14);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient5(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundSelected(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient3(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundSelectedAndFocused(Graphics2D paramGraphics2D) {
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(this.color14);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient5(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(2.3333333F), (decodeX(3.0F) - decodeX(0.6666667F)), (decodeY(2.6666667F) - decodeY(2.3333333F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(0.4F), (decodeX(3.0F) - decodeX(0.6666667F)), (decodeY(1.0F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(1.0F), decodeY(0.6F), (decodeX(3.0F) - decodeX(1.0F)), (decodeY(1.0F) - decodeY(0.6F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(1.0F), (decodeX(3.0F) - decodeX(0.6666667F)), (decodeY(2.3333333F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect5() {
    this.rect.setRect(decodeX(1.0F), decodeY(1.0F), (decodeX(3.0F) - decodeX(1.0F)), (decodeY(2.0F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect6() {
    this.rect.setRect(decodeX(0.22222222F), decodeY(0.13333334F), (decodeX(2.916668F) - decodeX(0.22222222F)), (decodeY(2.75F) - decodeY(0.13333334F)));
    return this.rect;
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
    return decodeGradient(0.5F * f3 + f1, 1.0F * f4 + f2, 0.5F * f3 + f1, 0.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.49573863F, 0.99147725F }, new Color[] { this.color8, decodeColor(this.color8, this.color9, 0.5F), this.color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.1684492F, 1.0F }, new Color[] { this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.49573863F, 0.99147725F }, new Color[] { this.color8, decodeColor(this.color8, this.color15, 0.5F), this.color15 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\SpinnerPanelSpinnerFormattedTextFieldPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */