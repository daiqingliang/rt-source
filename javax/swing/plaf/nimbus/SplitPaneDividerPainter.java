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

final class SplitPaneDividerPainter extends AbstractRegionPainter {
  static final int BACKGROUND_ENABLED = 1;
  
  static final int BACKGROUND_FOCUSED = 2;
  
  static final int FOREGROUND_ENABLED = 3;
  
  static final int FOREGROUND_ENABLED_VERTICAL = 4;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.017358616F, -0.11372548F, 0);
  
  private Color color2 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.102396235F, 0.21960783F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", 0.0F, -0.07016757F, 0.12941176F, 0);
  
  private Color color4 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.048026316F, 0.007843137F, 0);
  
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.06970999F, 0.21568626F, 0);
  
  private Color color8 = decodeColor("nimbusBlueGrey", 0.0F, -0.06704806F, 0.06666666F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", 0.0F, -0.019617222F, -0.09803921F, 0);
  
  private Color color10 = decodeColor("nimbusBlueGrey", 0.004273474F, -0.03790062F, -0.043137252F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", -0.111111104F, -0.106573746F, 0.24705881F, 0);
  
  private Color color12 = decodeColor("nimbusBlueGrey", 0.0F, -0.049301825F, 0.02352941F, 0);
  
  private Color color13 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.07399663F, 0.11372548F, 0);
  
  private Color color14 = decodeColor("nimbusBlueGrey", -0.018518567F, -0.06998578F, 0.12549019F, 0);
  
  private Color color15 = decodeColor("nimbusBlueGrey", 0.0F, -0.050526317F, 0.039215684F, 0);
  
  private Object[] componentColors;
  
  public SplitPaneDividerPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
        paintBackgroundFocused(paramGraphics2D);
        break;
      case 3:
        paintForegroundEnabled(paramGraphics2D);
        break;
      case 4:
        paintForegroundEnabledAndVertical(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(decodeGradient1(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(decodeGradient2(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintForegroundEnabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient3(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintForegroundEnabledAndVertical(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(decodeGradient6(this.rect));
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(1.0F), decodeY(0.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(3.0F) - decodeY(0.0F)));
    return this.rect;
  }
  
  private RoundRectangle2D decodeRoundRect1() {
    this.roundRect.setRoundRect(decodeX(1.05F), decodeY(1.3F), (decodeX(1.95F) - decodeX(1.05F)), (decodeY(1.8F) - decodeY(1.3F)), 3.6666667461395264D, 3.6666667461395264D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2() {
    this.roundRect.setRoundRect(decodeX(1.1F), decodeY(1.4F), (decodeX(1.9F) - decodeX(1.1F)), (decodeY(1.7F) - decodeY(1.4F)), 4.0D, 4.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3() {
    this.roundRect.setRoundRect(decodeX(1.3F), decodeY(1.1428572F), (decodeX(1.7F) - decodeX(1.3F)), (decodeY(1.8214285F) - decodeY(1.1428572F)), 4.0D, 4.0D);
    return this.roundRect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(1.4F), decodeY(1.1785715F), (decodeX(1.6F) - decodeX(1.4F)), (decodeY(1.7678571F) - decodeY(1.1785715F)));
    return this.rect;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.058064517F, 0.08064516F, 0.103225805F, 0.116129026F, 0.12903225F, 0.43387097F, 0.7387097F, 0.77903223F, 0.81935483F, 0.85806453F, 
          0.8967742F }, new Color[] { 
          this.color1, decodeColor(this.color1, this.color2, 0.5F), this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color2, 0.5F), this.color2, decodeColor(this.color2, this.color1, 0.5F), 
          this.color1 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 
          0.058064517F, 0.08064516F, 0.103225805F, 0.1166129F, 0.13F, 0.43F, 0.73F, 0.7746774F, 0.81935483F, 0.85806453F, 
          0.8967742F }, new Color[] { 
          this.color1, decodeColor(this.color1, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color3, 0.5F), this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4, decodeColor(this.color4, this.color1, 0.5F), 
          this.color1 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.20645161F, 0.5F, 0.7935484F }, new Color[] { this.color1, decodeColor(this.color1, this.color5, 0.5F), this.color5 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.090322584F, 0.2951613F, 0.5F, 0.5822581F, 0.66451615F }, new Color[] { this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.75F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.42096773F, 0.84193546F, 0.8951613F, 0.9483871F }, new Color[] { this.color9, decodeColor(this.color9, this.color10, 0.5F), this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.08064516F, 0.16129032F, 0.5129032F, 0.86451614F, 0.88548386F, 0.90645164F }, new Color[] { this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14, decodeColor(this.color14, this.color15, 0.5F), this.color15 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\SplitPaneDividerPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */