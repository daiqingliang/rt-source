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

final class DesktopPanePainter extends AbstractRegionPainter {
  static final int BACKGROUND_ENABLED = 1;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBase", -0.004577577F, -0.12867206F, 0.007843137F, 0);
  
  private Color color2 = decodeColor("nimbusBase", -0.0063245893F, -0.08363098F, -0.17254904F, 0);
  
  private Color color3 = decodeColor("nimbusBase", -3.6883354E-4F, -0.056766927F, -0.10196081F, 0);
  
  private Color color4 = decodeColor("nimbusBase", -0.008954704F, -0.12645501F, -0.12549022F, 0);
  
  private Color color5 = new Color(255, 200, 0, 6);
  
  private Color color6 = decodeColor("nimbusBase", -8.028746E-5F, -0.084533215F, -0.05098042F, 0);
  
  private Color color7 = decodeColor("nimbusBase", -0.0052053332F, -0.12267083F, -0.09803924F, 0);
  
  private Color color8 = decodeColor("nimbusBase", -0.012559712F, -0.13136649F, -0.09803924F, 0);
  
  private Color color9 = decodeColor("nimbusBase", -0.009207249F, -0.13984653F, -0.07450983F, 0);
  
  private Color color10 = decodeColor("nimbusBase", -0.010750473F, -0.13571429F, -0.12549022F, 0);
  
  private Color color11 = decodeColor("nimbusBase", -0.008476257F, -0.1267857F, -0.109803945F, 0);
  
  private Color color12 = decodeColor("nimbusBase", -0.0034883022F, -0.042691052F, -0.21176472F, 0);
  
  private Color color13 = decodeColor("nimbusBase", -0.012613952F, -0.11610645F, -0.14901963F, 0);
  
  private Color color14 = decodeColor("nimbusBase", -0.0038217902F, -0.05238098F, -0.21960786F, 0);
  
  private Object[] componentColors;
  
  public DesktopPanePainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 1:
        paintBackgroundEnabled(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient1(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient2(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient4(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath6();
    paramGraphics2D.setPaint(decodeGradient5(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath7();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath8();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath9();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(1.2716666F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.2716666F, 0.0F), decodeAnchorY(2.0F, 0.5F), decodeAnchorX(1.1283333F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeX(1.1283333F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.3516667F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.5866666F), decodeY(1.5754311F));
    this.path.lineTo(decodeX(1.5416667F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.5416667F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(1.2716666F, 0.0F), decodeAnchorY(2.0F, -0.5F), decodeX(1.2716666F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(1.7883334F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.7883334F, 0.0F), decodeAnchorY(2.0F, 0.5F), decodeAnchorX(1.6533333F, 0.0F), decodeAnchorY(1.7737069F, 0.0F), decodeX(1.6533333F), decodeY(1.7737069F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.1465517F));
    this.path.curveTo(decodeAnchorX(2.0F, 0.0F), decodeAnchorY(1.1465517F, 0.0F), decodeAnchorX(2.0F, 0.0F), decodeAnchorY(2.0F, -0.5F), decodeX(2.0F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(2.0F, 0.5F), decodeAnchorY(2.0F, 0.5F), decodeAnchorX(1.7883334F, 0.0F), decodeAnchorY(2.0F, -0.5F), decodeX(1.7883334F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(1.5666666F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.5666666F), decodeY(1.5689654F));
    this.path.lineTo(decodeX(1.675F), decodeY(1.7715517F));
    this.path.curveTo(decodeAnchorX(1.675F, 0.0F), decodeAnchorY(1.7715517F, 0.0F), decodeAnchorX(1.8116667F, -23.5F), decodeAnchorY(1.4978448F, 33.5F), decodeX(1.8116667F), decodeY(1.4978448F));
    this.path.curveTo(decodeAnchorX(1.8116667F, 23.5F), decodeAnchorY(1.4978448F, -33.5F), decodeAnchorX(2.0F, 0.0F), decodeAnchorY(1.200431F, 0.0F), decodeX(2.0F), decodeY(1.200431F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.5666666F), decodeY(1.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath4() {
    this.path.reset();
    this.path.moveTo(decodeX(1.3383334F), decodeY(1.0F));
    this.path.curveTo(decodeAnchorX(1.3383334F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeAnchorX(1.4416666F, -21.0F), decodeAnchorY(1.3103448F, -37.5F), decodeX(1.4416666F), decodeY(1.3103448F));
    this.path.curveTo(decodeAnchorX(1.4416666F, 21.0F), decodeAnchorY(1.3103448F, 37.5F), decodeAnchorX(1.5733333F, 0.0F), decodeAnchorY(1.5840517F, 0.0F), decodeX(1.5733333F), decodeY(1.5840517F));
    this.path.curveTo(decodeAnchorX(1.5733333F, 0.0F), decodeAnchorY(1.5840517F, 0.0F), decodeAnchorX(1.6066667F, 1.5F), decodeAnchorY(1.2413793F, 29.5F), decodeX(1.6066667F), decodeY(1.2413793F));
    this.path.curveTo(decodeAnchorX(1.6066667F, -1.5F), decodeAnchorY(1.2413793F, -29.5F), decodeAnchorX(1.605F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeX(1.605F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.3383334F), decodeY(1.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath5() {
    this.path.reset();
    this.path.moveTo(decodeX(1.5683334F), decodeY(1.5797414F));
    this.path.curveTo(decodeAnchorX(1.5683334F, 0.0F), decodeAnchorY(1.5797414F, 0.0F), decodeAnchorX(1.575F, 0.0F), decodeAnchorY(1.2392242F, 33.0F), decodeX(1.575F), decodeY(1.2392242F));
    this.path.curveTo(decodeAnchorX(1.575F, 0.0F), decodeAnchorY(1.2392242F, -33.0F), decodeAnchorX(1.5616667F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeX(1.5616667F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.0F), decodeY(1.1982758F));
    this.path.curveTo(decodeAnchorX(2.0F, 0.0F), decodeAnchorY(1.1982758F, 0.0F), decodeAnchorX(1.8066666F, 27.5F), decodeAnchorY(1.5043104F, -38.5F), decodeX(1.8066666F), decodeY(1.5043104F));
    this.path.curveTo(decodeAnchorX(1.8066666F, -27.5F), decodeAnchorY(1.5043104F, 38.5F), decodeAnchorX(1.6766667F, 0.0F), decodeAnchorY(1.7780173F, 0.0F), decodeX(1.6766667F), decodeY(1.7780173F));
    this.path.lineTo(decodeX(1.5683334F), decodeY(1.5797414F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath6() {
    this.path.reset();
    this.path.moveTo(decodeX(1.5216666F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.5216666F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(1.5550001F, -2.0F), decodeAnchorY(1.7780173F, 22.5F), decodeX(1.5550001F), decodeY(1.7780173F));
    this.path.curveTo(decodeAnchorX(1.5550001F, 2.0F), decodeAnchorY(1.7780173F, -22.5F), decodeAnchorX(1.5683334F, 0.0F), decodeAnchorY(1.5765086F, 0.0F), decodeX(1.5683334F), decodeY(1.5765086F));
    this.path.lineTo(decodeX(1.6775F), decodeY(1.7747846F));
    this.path.curveTo(decodeAnchorX(1.6775F, 0.0F), decodeAnchorY(1.7747846F, 0.0F), decodeAnchorX(1.6508334F, 6.0F), decodeAnchorY(1.8922414F, -14.0F), decodeX(1.6508334F), decodeY(1.8922414F));
    this.path.curveTo(decodeAnchorX(1.6508334F, -6.0F), decodeAnchorY(1.8922414F, 14.0F), decodeAnchorX(1.6083333F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(1.6083333F), decodeY(2.0F));
    this.path.lineTo(decodeX(1.5216666F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath7() {
    this.path.reset();
    this.path.moveTo(decodeX(1.6066667F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.6066667F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(1.64F, -7.0F), decodeAnchorY(1.8814654F, 17.0F), decodeX(1.64F), decodeY(1.8814654F));
    this.path.curveTo(decodeAnchorX(1.64F, 7.0F), decodeAnchorY(1.8814654F, -17.0F), decodeAnchorX(1.6775F, 0.0F), decodeAnchorY(1.7747846F, 0.0F), decodeX(1.6775F), decodeY(1.7747846F));
    this.path.curveTo(decodeAnchorX(1.6775F, 0.0F), decodeAnchorY(1.7747846F, 0.0F), decodeAnchorX(1.7416667F, -11.0F), decodeAnchorY(1.8836207F, -15.0F), decodeX(1.7416667F), decodeY(1.8836207F));
    this.path.curveTo(decodeAnchorX(1.7416667F, 11.0F), decodeAnchorY(1.8836207F, 15.0F), decodeAnchorX(1.8133333F, 0.0F), decodeAnchorY(2.0F, -0.5F), decodeX(1.8133333F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.8133333F, 0.0F), decodeAnchorY(2.0F, 0.5F), decodeAnchorX(1.6066667F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(1.6066667F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath8() {
    this.path.reset();
    this.path.moveTo(decodeX(1.2733333F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.2733333F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeAnchorX(1.2633333F, 5.0F), decodeAnchorY(1.6594827F, 37.0F), decodeX(1.2633333F), decodeY(1.6594827F));
    this.path.curveTo(decodeAnchorX(1.2633333F, -5.0F), decodeAnchorY(1.6594827F, -37.0F), decodeAnchorX(1.1933334F, 9.0F), decodeAnchorY(1.2241379F, 33.5F), decodeX(1.1933334F), decodeY(1.2241379F));
    this.path.curveTo(decodeAnchorX(1.1933334F, -9.0F), decodeAnchorY(1.2241379F, -33.5F), decodeAnchorX(1.1333333F, 0.0F), decodeAnchorY(1.0F, 0.0F), decodeX(1.1333333F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.0F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.0F), decodeY(1.6120689F));
    this.path.curveTo(decodeAnchorX(1.0F, 0.0F), decodeAnchorY(1.6120689F, 0.0F), decodeAnchorX(1.15F, 0.0F), decodeAnchorY(2.0F, -0.5F), decodeX(1.15F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.15F, 0.0F), decodeAnchorY(2.0F, 0.5F), decodeAnchorX(1.2733333F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(1.2733333F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath9() {
    this.path.reset();
    this.path.moveTo(decodeX(1.0F), decodeY(2.0F));
    this.path.lineTo(decodeX(1.0F), decodeY(1.5969827F));
    this.path.curveTo(decodeAnchorX(1.0F, 0.0F), decodeAnchorY(1.5969827F, 0.0F), decodeAnchorX(1.0733334F, -10.0F), decodeAnchorY(1.7974138F, -19.5F), decodeX(1.0733334F), decodeY(1.7974138F));
    this.path.curveTo(decodeAnchorX(1.0733334F, 10.0F), decodeAnchorY(1.7974138F, 19.5F), decodeAnchorX(1.1666666F, 0.0F), decodeAnchorY(2.0F, -0.5F), decodeX(1.1666666F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(1.1666666F, 0.0F), decodeAnchorY(2.0F, 0.5F), decodeAnchorX(1.0F, 0.0F), decodeAnchorY(2.0F, 0.0F), decodeX(1.0F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.75F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color1, decodeColor(this.color1, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.9567308F * f3 + f1, 0.06835443F * f4 + f2, 0.75F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.83536583F * f3 + f1, 0.9522059F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.8659696F * f3 + f1, 0.011049724F * f4 + f2, 0.24809887F * f3 + f1, 0.95027626F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color6, decodeColor(this.color6, this.color8, 0.5F), this.color8 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.3511236F * f3 + f1, 0.09326425F * f4 + f2, 0.33426967F * f3 + f1, 0.9846154F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color9, decodeColor(this.color9, this.color10, 0.5F), this.color10 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.3548387F * f3 + f1, 0.114285715F * f4 + f2, 0.48387095F * f3 + f1, 0.9809524F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color11, decodeColor(this.color11, this.color4, 0.5F), this.color4 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.75F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color1, decodeColor(this.color1, this.color12, 0.5F), this.color12 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.75F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color13, decodeColor(this.color13, this.color14, 0.5F), this.color14 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\DesktopPanePainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */