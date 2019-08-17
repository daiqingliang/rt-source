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

final class TextFieldPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_SELECTED = 3;
  
  static final int BORDER_DISABLED = 4;
  
  static final int BORDER_FOCUSED = 5;
  
  static final int BORDER_ENABLED = 6;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", -0.015872955F, -0.07995863F, 0.15294117F, 0);
  
  private Color color2 = decodeColor("nimbusLightBackground", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color3 = decodeColor("nimbusBlueGrey", -0.006944418F, -0.07187897F, 0.06666666F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07826825F, 0.10588235F, 0);
  
  private Color color5 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07856284F, 0.11372548F, 0);
  
  private Color color6 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.07796818F, 0.09803921F, 0);
  
  private Color color7 = decodeColor("nimbusBlueGrey", -0.027777791F, -0.0965403F, -0.18431371F, 0);
  
  private Color color8 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.1048766F, -0.05098039F, 0);
  
  private Color color9 = decodeColor("nimbusLightBackground", 0.6666667F, 0.004901961F, -0.19999999F, 0);
  
  private Color color10 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.10512091F, -0.019607842F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", 0.055555582F, -0.105344966F, 0.011764705F, 0);
  
  private Color color12 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Object[] componentColors;
  
  public TextFieldPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
        paintBackgroundSelected(paramGraphics2D);
        break;
      case 4:
        paintBorderDisabled(paramGraphics2D);
        break;
      case 5:
        paintBorderFocused(paramGraphics2D);
        break;
      case 6:
        paintBorderEnabled(paramGraphics2D);
        break;
    } 
  }
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent) {
    Object[] arrayOfObject = null;
    switch (this.state) {
      case 2:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color2, 0.0F, 0.0F, 0) };
        break;
      case 5:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color9, 0.004901961F, -0.19999999F, 0), getComponentColor(paramJComponent, "background", this.color2, 0.0F, 0.0F, 0) };
        break;
      case 6:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color9, 0.004901961F, -0.19999999F, 0), getComponentColor(paramJComponent, "background", this.color2, 0.0F, 0.0F, 0) };
        break;
    } 
    return arrayOfObject;
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint((Color)this.componentColors[0]);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundSelected(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color2);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBorderDisabled(Graphics2D paramGraphics2D) {
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
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBorderFocused(Graphics2D paramGraphics2D) {
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(decodeGradient3(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect9();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect10();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect11();
    paramGraphics2D.setPaint(this.color11);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBorderEnabled(Graphics2D paramGraphics2D) {
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(decodeGradient5(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(decodeGradient4(this.rect));
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect9();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect10();
    paramGraphics2D.setPaint(this.color10);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect11();
    paramGraphics2D.setPaint(this.color11);
    paramGraphics2D.fill(this.rect);
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(0.4F), decodeY(0.4F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(2.6F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(0.4F), (decodeX(2.3333333F) - decodeX(0.6666667F)), (decodeY(1.0F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(1.0F), decodeY(0.6F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(1.0F) - decodeY(0.6F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(1.0F), (decodeX(1.0F) - decodeX(0.6666667F)), (decodeY(2.0F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect5() {
    this.rect.setRect(decodeX(0.6666667F), decodeY(2.3333333F), (decodeX(2.3333333F) - decodeX(0.6666667F)), (decodeY(2.0F) - decodeY(2.3333333F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect6() {
    this.rect.setRect(decodeX(2.0F), decodeY(1.0F), (decodeX(2.3333333F) - decodeX(2.0F)), (decodeY(2.0F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect7() {
    this.rect.setRect(decodeX(0.4F), decodeY(0.4F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(1.0F) - decodeY(0.4F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect8() {
    this.rect.setRect(decodeX(0.6F), decodeY(0.6F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(1.0F) - decodeY(0.6F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect9() {
    this.rect.setRect(decodeX(0.4F), decodeY(1.0F), (decodeX(0.6F) - decodeX(0.4F)), (decodeY(2.6F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect10() {
    this.rect.setRect(decodeX(2.4F), decodeY(1.0F), (decodeX(2.6F) - decodeX(2.4F)), (decodeY(2.6F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect11() {
    this.rect.setRect(decodeX(0.6F), decodeY(2.4F), (decodeX(2.4F) - decodeX(0.6F)), (decodeY(2.6F) - decodeY(2.4F)));
    return this.rect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.4F), decodeY(0.4F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(0.4F));
    this.path.curveTo(decodeAnchorX(2.6F, 0.0F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeX(2.8800004F), decodeY(0.4F));
    this.path.curveTo(decodeAnchorX(2.8800004F, 0.1F), decodeAnchorY(0.4F, 0.0F), decodeAnchorX(2.8800004F, 0.0F), decodeAnchorY(2.8799999F, 0.0F), decodeX(2.8800004F), decodeY(2.8799999F));
    this.path.lineTo(decodeX(0.120000005F), decodeY(2.8799999F));
    this.path.lineTo(decodeX(0.120000005F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(2.8800004F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(2.8800004F), decodeY(0.4F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.4F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color5, decodeColor(this.color5, this.color1, 0.5F), this.color1 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25F * f3 + f1, 0.1625F * f4 + f2, new float[] { 0.1F, 0.49999997F, 0.9F }, new Color[] { this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.1F, 0.49999997F, 0.9F }, new Color[] { (Color)this.componentColors[0], decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5F), (Color)this.componentColors[1] });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.1F, 0.49999997F, 0.9F }, new Color[] { this.color7, decodeColor(this.color7, this.color8, 0.5F), this.color8 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TextFieldPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */