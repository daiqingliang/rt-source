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

final class TreePainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_ENABLED = 2;
  
  static final int BACKGROUND_ENABLED_SELECTED = 3;
  
  static final int LEAFICON_ENABLED = 4;
  
  static final int CLOSEDICON_ENABLED = 5;
  
  static final int OPENICON_ENABLED = 6;
  
  static final int COLLAPSEDICON_ENABLED = 7;
  
  static final int COLLAPSEDICON_ENABLED_SELECTED = 8;
  
  static final int EXPANDEDICON_ENABLED = 9;
  
  static final int EXPANDEDICON_ENABLED_SELECTED = 10;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", 0.007936537F, -0.065654516F, -0.13333333F, 0);
  
  private Color color2 = new Color(97, 98, 102, 255);
  
  private Color color3 = decodeColor("nimbusBlueGrey", -0.032679737F, -0.043332636F, 0.24705881F, 0);
  
  private Color color4 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color5 = decodeColor("nimbusBase", 0.0077680945F, -0.51781034F, 0.3490196F, 0);
  
  private Color color6 = decodeColor("nimbusBase", 0.013940871F, -0.599277F, 0.41960782F, 0);
  
  private Color color7 = decodeColor("nimbusBase", 0.004681647F, -0.4198052F, 0.14117646F, 0);
  
  private Color color8 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -127);
  
  private Color color9 = decodeColor("nimbusBlueGrey", 0.0F, 0.0F, -0.21F, -99);
  
  private Color color10 = decodeColor("nimbusBase", 2.9569864E-4F, -0.45978838F, 0.2980392F, 0);
  
  private Color color11 = decodeColor("nimbusBase", 0.0015952587F, -0.34848025F, 0.18823528F, 0);
  
  private Color color12 = decodeColor("nimbusBase", 0.0015952587F, -0.30844158F, 0.09803921F, 0);
  
  private Color color13 = decodeColor("nimbusBase", 0.0015952587F, -0.27329817F, 0.035294116F, 0);
  
  private Color color14 = decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
  
  private Color color15 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -125);
  
  private Color color16 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -50);
  
  private Color color17 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, -100);
  
  private Color color18 = decodeColor("nimbusBase", 0.0012094378F, -0.23571429F, -0.0784314F, 0);
  
  private Color color19 = decodeColor("nimbusBase", 2.9569864E-4F, -0.115166366F, -0.2627451F, 0);
  
  private Color color20 = decodeColor("nimbusBase", 0.0027436614F, -0.335015F, 0.011764705F, 0);
  
  private Color color21 = decodeColor("nimbusBase", 0.0024294257F, -0.3857143F, 0.031372547F, 0);
  
  private Color color22 = decodeColor("nimbusBase", 0.0018081069F, -0.3595238F, -0.13725492F, 0);
  
  private Color color23 = new Color(255, 200, 0, 255);
  
  private Color color24 = decodeColor("nimbusBase", 0.004681647F, -0.33496243F, -0.027450979F, 0);
  
  private Color color25 = decodeColor("nimbusBase", 0.0019934773F, -0.361378F, -0.10588238F, 0);
  
  private Color color26 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.34509805F, 0);
  
  private Object[] componentColors;
  
  public TreePainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 4:
        paintleafIconEnabled(paramGraphics2D);
        break;
      case 5:
        paintclosedIconEnabled(paramGraphics2D);
        break;
      case 6:
        paintopenIconEnabled(paramGraphics2D);
        break;
      case 7:
        paintcollapsedIconEnabled(paramGraphics2D);
        break;
      case 8:
        paintcollapsedIconEnabledAndSelected(paramGraphics2D);
        break;
      case 9:
        paintexpandedIconEnabled(paramGraphics2D);
        break;
      case 10:
        paintexpandedIconEnabledAndSelected(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintleafIconEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color2);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient1(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient2(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(this.color8);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintclosedIconEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath6();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.path);
    this.path = decodePath7();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath8();
    paramGraphics2D.setPaint(decodeGradient4(this.path));
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color15);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color16);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color17);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath9();
    paramGraphics2D.setPaint(decodeGradient5(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath10();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath11();
    paramGraphics2D.setPaint(this.color23);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintopenIconEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath6();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.path);
    this.path = decodePath12();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath13();
    paramGraphics2D.setPaint(decodeGradient4(this.path));
    paramGraphics2D.fill(this.path);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color15);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color16);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color17);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath14();
    paramGraphics2D.setPaint(decodeGradient5(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath15();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath11();
    paramGraphics2D.setPaint(this.color23);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintcollapsedIconEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath16();
    paramGraphics2D.setPaint(this.color26);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintcollapsedIconEnabledAndSelected(Graphics2D paramGraphics2D) {
    this.path = decodePath16();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintexpandedIconEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath17();
    paramGraphics2D.setPaint(this.color26);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintexpandedIconEnabledAndSelected(Graphics2D paramGraphics2D) {
    this.path = decodePath17();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.path);
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.2F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.2F), decodeY(3.0F));
    this.path.lineTo(decodeX(0.4F), decodeY(3.0F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.2F));
    this.path.lineTo(decodeX(1.9197531F), decodeY(0.2F));
    this.path.lineTo(decodeX(2.6F), decodeY(0.9F));
    this.path.lineTo(decodeX(2.6F), decodeY(3.0F));
    this.path.lineTo(decodeX(2.8F), decodeY(3.0F));
    this.path.lineTo(decodeX(2.8F), decodeY(0.88888896F));
    this.path.lineTo(decodeX(1.9537036F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(0.4F), decodeY(2.8F), (decodeX(2.6F) - decodeX(0.4F)), (decodeY(3.0F) - decodeY(2.8F)));
    return this.rect;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(1.8333333F), decodeY(0.2F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(0.2F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(1.8333333F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.8F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.8F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(0.2F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath4() {
    this.path.reset();
    this.path.moveTo(decodeX(1.8333333F), decodeY(0.2F));
    this.path.lineTo(decodeX(1.6234567F), decodeY(0.2F));
    this.path.lineTo(decodeX(1.6296296F), decodeY(1.2037038F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.2006173F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(1.0F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(0.2F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath5() {
    this.path.reset();
    this.path.moveTo(decodeX(1.8333333F), decodeY(0.4F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.8F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.8F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.4F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.4F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6F), decodeY(0.4F));
    this.path.lineTo(decodeX(1.8333333F), decodeY(0.4F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath6() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(2.4F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(3.0F));
    this.path.lineTo(decodeX(2.6F), decodeY(3.0F));
    this.path.lineTo(decodeX(2.8F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.8F), decodeY(2.4F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.4F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath7() {
    this.path.reset();
    this.path.moveTo(decodeX(0.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6037037F), decodeY(1.8425925F));
    this.path.lineTo(decodeX(0.8F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.8F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.8F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6F), decodeY(2.6F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath8() {
    this.path.reset();
    this.path.moveTo(decodeX(0.2F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.40833336F), decodeY(1.8645833F));
    this.path.lineTo(decodeX(0.79583335F), decodeY(0.8F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.8F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.5F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.4F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.6F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.6F), decodeY(0.4F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.6F));
    this.path.closePath();
    return this.path;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(0.2F), decodeY(0.6F), (decodeX(0.4F) - decodeX(0.2F)), (decodeY(0.8F) - decodeY(0.6F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(0.6F), decodeY(0.2F), (decodeX(1.3333334F) - decodeX(0.6F)), (decodeY(0.4F) - decodeY(0.2F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(1.5F), decodeY(0.6F), (decodeX(2.4F) - decodeX(1.5F)), (decodeY(0.8F) - decodeY(0.6F)));
    return this.rect;
  }
  
  private Path2D decodePath9() {
    this.path.reset();
    this.path.moveTo(decodeX(3.0F), decodeY(0.8F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.4F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.5F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.4F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.5888889F), decodeY(0.20370372F));
    this.path.lineTo(decodeX(0.5962963F), decodeY(0.34814817F));
    this.path.lineTo(decodeX(0.34814817F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.774074F), decodeY(1.1604939F));
    this.path.lineTo(decodeX(2.8F), decodeY(1.0F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.8925927F), decodeY(1.1882716F));
    this.path.lineTo(decodeX(2.8F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.8F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.8F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.8F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.65185183F));
    this.path.lineTo(decodeX(0.63703704F), decodeY(0.0F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.0F));
    this.path.lineTo(decodeX(1.5925925F), decodeY(0.4F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.4F));
    this.path.lineTo(decodeX(2.6F), decodeY(0.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(0.8F));
    this.path.lineTo(decodeX(3.0F), decodeY(0.8F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath10() {
    this.path.reset();
    this.path.moveTo(decodeX(2.4F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.8F));
    this.path.lineTo(decodeX(0.74814814F), decodeY(0.8F));
    this.path.lineTo(decodeX(0.4037037F), decodeY(1.8425925F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.5925926F), decodeY(2.225926F));
    this.path.lineTo(decodeX(0.916F), decodeY(0.996F));
    this.path.lineTo(decodeX(2.4F), decodeY(1.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath11() {
    this.path.reset();
    this.path.moveTo(decodeX(2.2F), decodeY(2.2F));
    this.path.lineTo(decodeX(2.2F), decodeY(2.2F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath12() {
    this.path.reset();
    this.path.moveTo(decodeX(0.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6F), decodeY(2.2F));
    this.path.lineTo(decodeX(0.8F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.8F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.8F), decodeY(1.6666667F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6F), decodeY(2.6F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath13() {
    this.path.reset();
    this.path.moveTo(decodeX(0.2F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.0F));
    this.path.lineTo(decodeX(0.8F), decodeY(1.1666666F));
    this.path.lineTo(decodeX(2.4F), decodeY(1.1666666F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.5F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.4F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.6F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.6F), decodeY(0.4F));
    this.path.lineTo(decodeX(0.4F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.6F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath14() {
    this.path.reset();
    this.path.moveTo(decodeX(3.0F), decodeY(1.1666666F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.4F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.5F), decodeY(0.6F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.4F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.2F));
    this.path.lineTo(decodeX(0.5888889F), decodeY(0.20370372F));
    this.path.lineTo(decodeX(0.5962963F), decodeY(0.34814817F));
    this.path.lineTo(decodeX(0.34814817F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(0.6F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.0F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.8333333F));
    this.path.lineTo(decodeX(2.916F), decodeY(1.3533334F));
    this.path.lineTo(decodeX(2.98F), decodeY(1.3766667F));
    this.path.lineTo(decodeX(2.8F), decodeY(1.8333333F));
    this.path.lineTo(decodeX(2.8F), decodeY(2.0F));
    this.path.lineTo(decodeX(2.8F), decodeY(2.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(2.8F));
    this.path.lineTo(decodeX(0.2F), decodeY(2.8F));
    this.path.lineTo(decodeX(0.0F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.65185183F));
    this.path.lineTo(decodeX(0.63703704F), decodeY(0.0F));
    this.path.lineTo(decodeX(1.3333334F), decodeY(0.0F));
    this.path.lineTo(decodeX(1.5925925F), decodeY(0.4F));
    this.path.lineTo(decodeX(2.4F), decodeY(0.4F));
    this.path.lineTo(decodeX(2.6F), decodeY(0.6F));
    this.path.lineTo(decodeX(2.6F), decodeY(1.1666666F));
    this.path.lineTo(decodeX(3.0F), decodeY(1.1666666F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath15() {
    this.path.reset();
    this.path.moveTo(decodeX(2.4F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.4F), decodeY(1.1666666F));
    this.path.lineTo(decodeX(0.74F), decodeY(1.1666666F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.0F));
    this.path.lineTo(decodeX(0.4F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.6F), decodeY(2.6F));
    this.path.lineTo(decodeX(0.5925926F), decodeY(2.225926F));
    this.path.lineTo(decodeX(0.8F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(2.4F), decodeY(1.3333334F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath16() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(1.2397541F), decodeY(0.70163935F));
    this.path.lineTo(decodeX(0.0F), decodeY(3.0F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath17() {
    this.path.reset();
    this.path.moveTo(decodeX(0.0F), decodeY(0.0F));
    this.path.lineTo(decodeX(1.25F), decodeY(0.0F));
    this.path.lineTo(decodeX(0.70819676F), decodeY(2.9901638F));
    this.path.lineTo(decodeX(0.0F), decodeY(0.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Paint decodeGradient1(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.046296295F * f3 + f1, 0.9675926F * f4 + f2, 0.4861111F * f3 + f1, 0.5324074F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color4, 0.5F), this.color4 });
  }
  
  private Paint decodeGradient2(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color5, decodeColor(this.color5, this.color6, 0.5F), this.color6 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.04191617F, 0.10329342F, 0.16467066F, 0.24550897F, 0.3263473F, 0.6631737F, 1.0F }, new Color[] { this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11, decodeColor(this.color11, this.color12, 0.5F), this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color5, decodeColor(this.color5, this.color14, 0.5F), this.color14 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color18, decodeColor(this.color18, this.color19, 0.5F), this.color19 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.12724552F, 0.25449103F, 0.62724555F, 1.0F }, new Color[] { this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21, decodeColor(this.color21, this.color22, 0.5F), this.color22 });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color24, decodeColor(this.color24, this.color25, 0.5F), this.color25 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\TreePainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */