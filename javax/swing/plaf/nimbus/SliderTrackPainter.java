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

final class SliderTrackPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -245);
  
  private Color color2 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.061265234F, 0.05098039F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", 0.01010108F, -0.059835073F, 0.10588235F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", -0.01111114F, -0.061982628F, 0.062745094F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.058639523F, 0.086274505F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -111);
  
  private Color color7 = decodeColor("nimbusBlueGrey", 0.0F, -0.034093194F, -0.12941176F, 0);
  
  private Color color8 = decodeColor("nimbusBlueGrey", 0.01111114F, -0.023821115F, -0.06666666F, 0);
  
  private Color color9 = decodeColor("nimbusBlueGrey", -0.008547008F, -0.03314536F, -0.086274505F, 0);
  
  private Color color10 = decodeColor("nimbusBlueGrey", 0.004273474F, -0.040256046F, -0.019607842F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.0F, -0.03626889F, 0.04705882F, 0);
  
  private Object[] componentColors;
  
  public SliderTrackPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient1(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(decodeGradient2(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect4();
    paramGraphics2D.setPaint(this.color6);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient3(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect5();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
  }
  
  private RoundRectangle2D decodeRoundRect1() {
    this.roundRect.setRoundRect(decodeX(0.2F), decodeY(1.6F), (decodeX(2.8F) - decodeX(0.2F)), (decodeY(2.8333333F) - decodeY(1.6F)), 8.70588207244873D, 8.70588207244873D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2() {
    this.roundRect.setRoundRect(decodeX(0.0F), decodeY(1.0F), (decodeX(3.0F) - decodeX(0.0F)), (decodeY(2.0F) - decodeY(1.0F)), 4.941176414489746D, 4.941176414489746D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect3() {
    this.roundRect.setRoundRect(decodeX(0.29411763F), decodeY(1.2F), (decodeX(2.7058823F) - decodeX(0.29411763F)), (decodeY(2.0F) - decodeY(1.2F)), 4.0D, 4.0D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect4() {
    this.roundRect.setRoundRect(decodeX(0.2F), decodeY(1.6F), (decodeX(2.8F) - decodeX(0.2F)), (decodeY(2.1666667F) - decodeY(1.6F)), 8.70588207244873D, 8.70588207244873D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect5() {
    this.roundRect.setRoundRect(decodeX(0.28823528F), decodeY(1.2F), (decodeX(2.7F) - decodeX(0.28823528F)), (decodeY(2.0F) - decodeY(1.2F)), 4.0D, 4.0D);
    return this.roundRect;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.07647059F * f4 + f2, 0.25F * f3 + f1, 0.9117647F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color2, decodeColor(this.color2, this.color3, 0.5F), this.color3 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.13770053F, 0.27540106F, 0.63770056F, 1.0F }, new Color[] { this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5, decodeColor(this.color5, this.color3, 0.5F), this.color3 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.07647059F * f4 + f2, 0.25F * f3 + f1, 0.9117647F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.13770053F, 0.27540106F, 0.4906417F, 0.7058824F }, new Color[] { this.color9, decodeColor(this.color9, this.color10, 0.5F), this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\SliderTrackPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */