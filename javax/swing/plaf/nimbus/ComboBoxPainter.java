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

final class ComboBoxPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED = 1;
  
  static final int BACKGROUND_DISABLED_PRESSED = 2;
  
  static final int BACKGROUND_ENABLED = 3;
  
  static final int BACKGROUND_FOCUSED = 4;
  
  static final int BACKGROUND_MOUSEOVER_FOCUSED = 5;
  
  static final int BACKGROUND_MOUSEOVER = 6;
  
  static final int BACKGROUND_PRESSED_FOCUSED = 7;
  
  static final int BACKGROUND_PRESSED = 8;
  
  static final int BACKGROUND_ENABLED_SELECTED = 9;
  
  static final int BACKGROUND_DISABLED_EDITABLE = 10;
  
  static final int BACKGROUND_ENABLED_EDITABLE = 11;
  
  static final int BACKGROUND_FOCUSED_EDITABLE = 12;
  
  static final int BACKGROUND_MOUSEOVER_EDITABLE = 13;
  
  static final int BACKGROUND_PRESSED_EDITABLE = 14;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusBlueGrey", -0.6111111F, -0.110526316F, -0.74509805F, -247);
  
  private Color color2 = decodeColor("nimbusBase", 0.032459438F, -0.5928571F, 0.2745098F, 0);
  
  private Color color3 = decodeColor("nimbusBase", 0.032459438F, -0.590029F, 0.2235294F, 0);
  
  private Color color4 = decodeColor("nimbusBase", 0.032459438F, -0.60996324F, 0.36470586F, 0);
  
  private Color color5 = decodeColor("nimbusBase", 0.040395975F, -0.60474086F, 0.33725488F, 0);
  
  private Color color6 = decodeColor("nimbusBase", 0.032459438F, -0.5953556F, 0.32549018F, 0);
  
  private Color color7 = decodeColor("nimbusBase", 0.032459438F, -0.5957143F, 0.3333333F, 0);
  
  private Color color8 = decodeColor("nimbusBase", 0.021348298F, -0.56289876F, 0.2588235F, 0);
  
  private Color color9 = decodeColor("nimbusBase", 0.010237217F, -0.55799407F, 0.20784312F, 0);
  
  private Color color10 = decodeColor("nimbusBase", 0.021348298F, -0.59223604F, 0.35294116F, 0);
  
  private Color color11 = decodeColor("nimbusBase", 0.02391243F, -0.5774183F, 0.32549018F, 0);
  
  private Color color12 = decodeColor("nimbusBase", 0.021348298F, -0.56722116F, 0.3098039F, 0);
  
  private Color color13 = decodeColor("nimbusBase", 0.021348298F, -0.567841F, 0.31764704F, 0);
  
  private Color color14 = decodeColor("nimbusBlueGrey", 0.0F, 0.0F, -0.22F, -176);
  
  private Color color15 = decodeColor("nimbusBase", 0.032459438F, -0.5787523F, 0.07058823F, 0);
  
  private Color color16 = decodeColor("nimbusBase", 0.032459438F, -0.5399696F, -0.18039218F, 0);
  
  private Color color17 = decodeColor("nimbusBase", 0.08801502F, -0.63174605F, 0.43921566F, 0);
  
  private Color color18 = decodeColor("nimbusBase", 0.040395975F, -0.6054113F, 0.35686272F, 0);
  
  private Color color19 = decodeColor("nimbusBase", 0.032459438F, -0.5998577F, 0.4352941F, 0);
  
  private Color color20 = decodeColor("nimbusBase", 5.1498413E-4F, -0.34585923F, -0.007843137F, 0);
  
  private Color color21 = decodeColor("nimbusBase", 5.1498413E-4F, -0.095173776F, -0.25882354F, 0);
  
  private Color color22 = decodeColor("nimbusBase", 0.004681647F, -0.6197143F, 0.43137252F, 0);
  
  private Color color23 = decodeColor("nimbusBase", -0.0028941035F, -0.4800539F, 0.28235292F, 0);
  
  private Color color24 = decodeColor("nimbusBase", 5.1498413E-4F, -0.43866998F, 0.24705881F, 0);
  
  private Color color25 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4625541F, 0.35686272F, 0);
  
  private Color color26 = decodeColor("nimbusFocus", 0.0F, 0.0F, 0.0F, 0);
  
  private Color color27 = decodeColor("nimbusBase", 0.032459438F, -0.54616207F, -0.02352941F, 0);
  
  private Color color28 = decodeColor("nimbusBase", 0.032459438F, -0.41349208F, -0.33725494F, 0);
  
  private Color color29 = decodeColor("nimbusBase", 0.08801502F, -0.6317773F, 0.4470588F, 0);
  
  private Color color30 = decodeColor("nimbusBase", 0.032459438F, -0.6113241F, 0.41568625F, 0);
  
  private Color color31 = decodeColor("nimbusBase", 0.032459438F, -0.5985242F, 0.39999998F, 0);
  
  private Color color32 = decodeColor("nimbusBase", 0.0F, -0.6357143F, 0.45098037F, 0);
  
  private Color color33 = decodeColor("nimbusBase", 0.0013483167F, -0.1769987F, -0.12156865F, 0);
  
  private Color color34 = decodeColor("nimbusBase", 0.059279382F, 0.3642857F, -0.43529415F, 0);
  
  private Color color35 = decodeColor("nimbusBase", 0.004681647F, -0.6198413F, 0.43921566F, 0);
  
  private Color color36 = decodeColor("nimbusBase", -8.738637E-4F, -0.50527954F, 0.35294116F, 0);
  
  private Color color37 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4555341F, 0.3215686F, 0);
  
  private Color color38 = decodeColor("nimbusBase", 5.1498413E-4F, -0.4757143F, 0.43137252F, 0);
  
  private Color color39 = decodeColor("nimbusBase", 0.08801502F, 0.3642857F, -0.52156866F, 0);
  
  private Color color40 = decodeColor("nimbusBase", 0.032459438F, -0.5246032F, -0.12549022F, 0);
  
  private Color color41 = decodeColor("nimbusBase", 0.027408898F, -0.5847884F, 0.2980392F, 0);
  
  private Color color42 = decodeColor("nimbusBase", 0.026611507F, -0.53623784F, 0.19999999F, 0);
  
  private Color color43 = decodeColor("nimbusBase", 0.029681683F, -0.52701867F, 0.17254901F, 0);
  
  private Color color44 = decodeColor("nimbusBase", 0.03801495F, -0.5456242F, 0.3215686F, 0);
  
  private Color color45 = decodeColor("nimbusBase", -0.57865167F, -0.6357143F, -0.54901963F, 0);
  
  private Color color46 = decodeColor("nimbusBase", -3.528595E-5F, 0.018606722F, -0.23137257F, 0);
  
  private Color color47 = decodeColor("nimbusBase", -4.2033195E-4F, -0.38050595F, 0.20392156F, 0);
  
  private Color color48 = decodeColor("nimbusBase", 4.081726E-4F, -0.12922078F, 0.054901958F, 0);
  
  private Color color49 = decodeColor("nimbusBase", 0.0F, -0.00895375F, 0.007843137F, 0);
  
  private Color color50 = decodeColor("nimbusBase", -0.0015907288F, -0.1436508F, 0.19215685F, 0);
  
  private Color color51 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -83);
  
  private Color color52 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -88);
  
  private Color color53 = decodeColor("nimbusBlueGrey", 0.0F, -0.005263157F, -0.52156866F, -191);
  
  private Object[] componentColors;
  
  public ComboBoxPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
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
        paintBackgroundDisabledAndPressed(paramGraphics2D);
        break;
      case 3:
        paintBackgroundEnabled(paramGraphics2D);
        break;
      case 4:
        paintBackgroundFocused(paramGraphics2D);
        break;
      case 5:
        paintBackgroundMouseOverAndFocused(paramGraphics2D);
        break;
      case 6:
        paintBackgroundMouseOver(paramGraphics2D);
        break;
      case 7:
        paintBackgroundPressedAndFocused(paramGraphics2D);
        break;
      case 8:
        paintBackgroundPressed(paramGraphics2D);
        break;
      case 9:
        paintBackgroundEnabledAndSelected(paramGraphics2D);
        break;
      case 10:
        paintBackgroundDisabledAndEditable(paramGraphics2D);
        break;
      case 11:
        paintBackgroundEnabledAndEditable(paramGraphics2D);
        break;
      case 12:
        paintBackgroundFocusedAndEditable(paramGraphics2D);
        break;
      case 13:
        paintBackgroundMouseOverAndEditable(paramGraphics2D);
        break;
      case 14:
        paintBackgroundPressedAndEditable(paramGraphics2D);
        break;
    } 
  }
  
  protected Object[] getExtendedCacheKeys(JComponent paramJComponent) {
    Object[] arrayOfObject = null;
    switch (this.state) {
      case 3:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color17, -0.63174605F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", this.color18, -0.6054113F, 0.35686272F, 0), getComponentColor(paramJComponent, "background", this.color6, -0.5953556F, 0.32549018F, 0), getComponentColor(paramJComponent, "background", this.color19, -0.5998577F, 0.4352941F, 0), getComponentColor(paramJComponent, "background", this.color22, -0.6197143F, 0.43137252F, 0), getComponentColor(paramJComponent, "background", this.color23, -0.4800539F, 0.28235292F, 0), getComponentColor(paramJComponent, "background", this.color24, -0.43866998F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", this.color25, -0.4625541F, 0.35686272F, 0) };
        break;
      case 4:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color17, -0.63174605F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", this.color18, -0.6054113F, 0.35686272F, 0), getComponentColor(paramJComponent, "background", this.color6, -0.5953556F, 0.32549018F, 0), getComponentColor(paramJComponent, "background", this.color19, -0.5998577F, 0.4352941F, 0), getComponentColor(paramJComponent, "background", this.color22, -0.6197143F, 0.43137252F, 0), getComponentColor(paramJComponent, "background", this.color23, -0.4800539F, 0.28235292F, 0), getComponentColor(paramJComponent, "background", this.color24, -0.43866998F, 0.24705881F, 0), getComponentColor(paramJComponent, "background", this.color25, -0.4625541F, 0.35686272F, 0) };
        break;
      case 5:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color29, -0.6317773F, 0.4470588F, 0), getComponentColor(paramJComponent, "background", this.color30, -0.6113241F, 0.41568625F, 0), getComponentColor(paramJComponent, "background", this.color31, -0.5985242F, 0.39999998F, 0), getComponentColor(paramJComponent, "background", this.color32, -0.6357143F, 0.45098037F, 0), getComponentColor(paramJComponent, "background", this.color35, -0.6198413F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", this.color36, -0.50527954F, 0.35294116F, 0), getComponentColor(paramJComponent, "background", this.color37, -0.4555341F, 0.3215686F, 0), getComponentColor(paramJComponent, "background", this.color25, -0.4625541F, 0.35686272F, 0), getComponentColor(paramJComponent, "background", this.color38, -0.4757143F, 0.43137252F, 0) };
        break;
      case 6:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color29, -0.6317773F, 0.4470588F, 0), getComponentColor(paramJComponent, "background", this.color30, -0.6113241F, 0.41568625F, 0), getComponentColor(paramJComponent, "background", this.color31, -0.5985242F, 0.39999998F, 0), getComponentColor(paramJComponent, "background", this.color32, -0.6357143F, 0.45098037F, 0), getComponentColor(paramJComponent, "background", this.color35, -0.6198413F, 0.43921566F, 0), getComponentColor(paramJComponent, "background", this.color36, -0.50527954F, 0.35294116F, 0), getComponentColor(paramJComponent, "background", this.color37, -0.4555341F, 0.3215686F, 0), getComponentColor(paramJComponent, "background", this.color25, -0.4625541F, 0.35686272F, 0), getComponentColor(paramJComponent, "background", this.color38, -0.4757143F, 0.43137252F, 0) };
        break;
      case 7:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color41, -0.5847884F, 0.2980392F, 0), getComponentColor(paramJComponent, "background", this.color42, -0.53623784F, 0.19999999F, 0), getComponentColor(paramJComponent, "background", this.color43, -0.52701867F, 0.17254901F, 0), getComponentColor(paramJComponent, "background", this.color44, -0.5456242F, 0.3215686F, 0), getComponentColor(paramJComponent, "background", this.color47, -0.38050595F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", this.color48, -0.12922078F, 0.054901958F, 0), getComponentColor(paramJComponent, "background", this.color49, -0.00895375F, 0.007843137F, 0), getComponentColor(paramJComponent, "background", this.color50, -0.1436508F, 0.19215685F, 0) };
        break;
      case 8:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color41, -0.5847884F, 0.2980392F, 0), getComponentColor(paramJComponent, "background", this.color42, -0.53623784F, 0.19999999F, 0), getComponentColor(paramJComponent, "background", this.color43, -0.52701867F, 0.17254901F, 0), getComponentColor(paramJComponent, "background", this.color44, -0.5456242F, 0.3215686F, 0), getComponentColor(paramJComponent, "background", this.color47, -0.38050595F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", this.color48, -0.12922078F, 0.054901958F, 0), getComponentColor(paramJComponent, "background", this.color49, -0.00895375F, 0.007843137F, 0), getComponentColor(paramJComponent, "background", this.color50, -0.1436508F, 0.19215685F, 0) };
        break;
      case 9:
        arrayOfObject = new Object[] { getComponentColor(paramJComponent, "background", this.color41, -0.5847884F, 0.2980392F, 0), getComponentColor(paramJComponent, "background", this.color42, -0.53623784F, 0.19999999F, 0), getComponentColor(paramJComponent, "background", this.color43, -0.52701867F, 0.17254901F, 0), getComponentColor(paramJComponent, "background", this.color44, -0.5456242F, 0.3215686F, 0), getComponentColor(paramJComponent, "background", this.color47, -0.38050595F, 0.20392156F, 0), getComponentColor(paramJComponent, "background", this.color48, -0.12922078F, 0.054901958F, 0), getComponentColor(paramJComponent, "background", this.color49, -0.00895375F, 0.007843137F, 0), getComponentColor(paramJComponent, "background", this.color50, -0.1436508F, 0.19215685F, 0) };
        break;
    } 
    return arrayOfObject;
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient1(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient2(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient4(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundDisabledAndPressed(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color1);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient1(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient2(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient4(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color14);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient5(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color26);
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient5(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient7(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color26);
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient10(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color14);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient9(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient10(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressedAndFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(this.color26);
    paramGraphics2D.fill(this.roundRect);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient11(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient12(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color51);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient11(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient12(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabledAndSelected(Graphics2D paramGraphics2D) {
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color52);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(decodeGradient11(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath3();
    paramGraphics2D.setPaint(decodeGradient6(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient12(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath5();
    paramGraphics2D.setPaint(decodeGradient8(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundDisabledAndEditable(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color53);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundEnabledAndEditable(Graphics2D paramGraphics2D) {
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color53);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundFocusedAndEditable(Graphics2D paramGraphics2D) {
    this.path = decodePath6();
    paramGraphics2D.setPaint(this.color26);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndEditable(Graphics2D paramGraphics2D) {
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color53);
    paramGraphics2D.fill(this.rect);
  }
  
  private void paintBackgroundPressedAndEditable(Graphics2D paramGraphics2D) {
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color53);
    paramGraphics2D.fill(this.rect);
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(0.22222222F), decodeY(2.0F));
    this.path.lineTo(decodeX(0.22222222F), decodeY(2.25F));
    this.path.curveTo(decodeAnchorX(0.22222222F, 0.0F), decodeAnchorY(2.25F, 3.0F), decodeAnchorX(0.7777778F, -3.0F), decodeAnchorY(2.875F, 0.0F), decodeX(0.7777778F), decodeY(2.875F));
    this.path.lineTo(decodeX(2.631579F), decodeY(2.875F));
    this.path.curveTo(decodeAnchorX(2.631579F, 3.0F), decodeAnchorY(2.875F, 0.0F), decodeAnchorX(2.8947368F, 0.0F), decodeAnchorY(2.25F, 3.0F), decodeX(2.8947368F), decodeY(2.25F));
    this.path.lineTo(decodeX(2.8947368F), decodeY(2.0F));
    this.path.lineTo(decodeX(0.22222222F), decodeY(2.0F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(0.22222222F), decodeY(0.875F));
    this.path.lineTo(decodeX(0.22222222F), decodeY(2.125F));
    this.path.curveTo(decodeAnchorX(0.22222222F, 0.0F), decodeAnchorY(2.125F, 3.0F), decodeAnchorX(0.7777778F, -3.0F), decodeAnchorY(2.75F, 0.0F), decodeX(0.7777778F), decodeY(2.75F));
    this.path.lineTo(decodeX(2.0F), decodeY(2.75F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.25F));
    this.path.lineTo(decodeX(0.7777778F), decodeY(0.25F));
    this.path.curveTo(decodeAnchorX(0.7777778F, -3.0F), decodeAnchorY(0.25F, 0.0F), decodeAnchorX(0.22222222F, 0.0F), decodeAnchorY(0.875F, -3.0F), decodeX(0.22222222F), decodeY(0.875F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(0.8888889F), decodeY(0.375F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.375F));
    this.path.lineTo(decodeX(2.0F), decodeY(2.625F));
    this.path.lineTo(decodeX(0.8888889F), decodeY(2.625F));
    this.path.curveTo(decodeAnchorX(0.8888889F, -4.0F), decodeAnchorY(2.625F, 0.0F), decodeAnchorX(0.33333334F, 0.0F), decodeAnchorY(2.0F, 4.0F), decodeX(0.33333334F), decodeY(2.0F));
    this.path.lineTo(decodeX(0.33333334F), decodeY(0.875F));
    this.path.curveTo(decodeAnchorX(0.33333334F, 0.0F), decodeAnchorY(0.875F, -3.0F), decodeAnchorX(0.8888889F, -4.0F), decodeAnchorY(0.375F, 0.0F), decodeX(0.8888889F), decodeY(0.375F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath4() {
    this.path.reset();
    this.path.moveTo(decodeX(2.0F), decodeY(0.25F));
    this.path.lineTo(decodeX(2.631579F), decodeY(0.25F));
    this.path.curveTo(decodeAnchorX(2.631579F, 3.0F), decodeAnchorY(0.25F, 0.0F), decodeAnchorX(2.8947368F, 0.0F), decodeAnchorY(0.875F, -3.0F), decodeX(2.8947368F), decodeY(0.875F));
    this.path.lineTo(decodeX(2.8947368F), decodeY(2.125F));
    this.path.curveTo(decodeAnchorX(2.8947368F, 0.0F), decodeAnchorY(2.125F, 3.0F), decodeAnchorX(2.631579F, 3.0F), decodeAnchorY(2.75F, 0.0F), decodeX(2.631579F), decodeY(2.75F));
    this.path.lineTo(decodeX(2.0F), decodeY(2.75F));
    this.path.lineTo(decodeX(2.0F), decodeY(0.25F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath5() {
    this.path.reset();
    this.path.moveTo(decodeX(2.0131578F), decodeY(0.375F));
    this.path.lineTo(decodeX(2.5789473F), decodeY(0.375F));
    this.path.curveTo(decodeAnchorX(2.5789473F, 4.0F), decodeAnchorY(0.375F, 0.0F), decodeAnchorX(2.8421054F, 0.0F), decodeAnchorY(1.0F, -4.0F), decodeX(2.8421054F), decodeY(1.0F));
    this.path.lineTo(decodeX(2.8421054F), decodeY(2.0F));
    this.path.curveTo(decodeAnchorX(2.8421054F, 0.0F), decodeAnchorY(2.0F, 4.0F), decodeAnchorX(2.5789473F, 4.0F), decodeAnchorY(2.625F, 0.0F), decodeX(2.5789473F), decodeY(2.625F));
    this.path.lineTo(decodeX(2.0131578F), decodeY(2.625F));
    this.path.lineTo(decodeX(2.0131578F), decodeY(0.375F));
    this.path.closePath();
    return this.path;
  }
  
  private RoundRectangle2D decodeRoundRect1() {
    this.roundRect.setRoundRect(decodeX(0.06666667F), decodeY(0.075F), (decodeX(2.9684212F) - decodeX(0.06666667F)), (decodeY(2.925F) - decodeY(0.075F)), 13.0D, 13.0D);
    return this.roundRect;
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(1.4385965F), decodeY(1.4444444F), (decodeX(1.4385965F) - decodeX(1.4385965F)), (decodeY(1.4444444F) - decodeY(1.4444444F)));
    return this.rect;
  }
  
  private Path2D decodePath6() {
    this.path.reset();
    this.path.moveTo(decodeX(0.120000005F), decodeY(0.120000005F));
    this.path.lineTo(decodeX(1.9954545F), decodeY(0.120000005F));
    this.path.curveTo(decodeAnchorX(1.9954545F, 3.0F), decodeAnchorY(0.120000005F, 0.0F), decodeAnchorX(2.8799987F, 0.0F), decodeAnchorY(1.0941176F, -3.0F), decodeX(2.8799987F), decodeY(1.0941176F));
    this.path.lineTo(decodeX(2.8799987F), decodeY(1.964706F));
    this.path.curveTo(decodeAnchorX(2.8799987F, 0.0F), decodeAnchorY(1.964706F, 3.0F), decodeAnchorX(1.9954545F, 3.0F), decodeAnchorY(2.8799999F, 0.0F), decodeX(1.9954545F), decodeY(2.8799999F));
    this.path.lineTo(decodeX(0.120000005F), decodeY(2.8799999F));
    this.path.lineTo(decodeX(0.120000005F), decodeY(0.120000005F));
    this.path.closePath();
    return this.path;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(1.4385965F), decodeY(1.5F), (decodeX(1.4385965F) - decodeX(1.4385965F)), (decodeY(1.5F) - decodeY(1.5F)));
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
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.2002841F, 0.4005682F, 0.5326705F, 0.66477275F, 0.8323864F, 1.0F }, new Color[] { this.color4, decodeColor(this.color4, this.color5, 0.5F), this.color5, decodeColor(this.color5, this.color6, 0.5F), this.color6, decodeColor(this.color6, this.color7, 0.5F), this.color7 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color8, decodeColor(this.color8, this.color9, 0.5F), this.color9 });
  }
  
  private Paint decodeGradient4(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.171875F, 0.34375F, 0.4815341F, 0.6193182F, 0.8096591F, 1.0F }, new Color[] { this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11, decodeColor(this.color11, this.color12, 0.5F), this.color12, decodeColor(this.color12, this.color13, 0.5F), this.color13 });
  }
  
  private Paint decodeGradient5(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color15, decodeColor(this.color15, this.color16, 0.5F), this.color16 });
  }
  
  private Paint decodeGradient6(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.2002841F, 0.4005682F, 0.5326705F, 0.66477275F, 0.8323864F, 1.0F }, new Color[] { (Color)this.componentColors[0], decodeColor((Color)this.componentColors[0], (Color)this.componentColors[1], 0.5F), (Color)this.componentColors[1], decodeColor((Color)this.componentColors[1], (Color)this.componentColors[2], 0.5F), (Color)this.componentColors[2], decodeColor((Color)this.componentColors[2], (Color)this.componentColors[3], 0.5F), (Color)this.componentColors[3] });
  }
  
  private Paint decodeGradient7(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color20, decodeColor(this.color20, this.color21, 0.5F), this.color21 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.171875F, 0.34375F, 0.4815341F, 0.6193182F, 0.8096591F, 1.0F }, new Color[] { (Color)this.componentColors[4], decodeColor((Color)this.componentColors[4], (Color)this.componentColors[5], 0.5F), (Color)this.componentColors[5], decodeColor((Color)this.componentColors[5], (Color)this.componentColors[6], 0.5F), (Color)this.componentColors[6], decodeColor((Color)this.componentColors[6], (Color)this.componentColors[7], 0.5F), (Color)this.componentColors[7] });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color27, decodeColor(this.color27, this.color28, 0.5F), this.color28 });
  }
  
  private Paint decodeGradient10(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color33, decodeColor(this.color33, this.color34, 0.5F), this.color34 });
  }
  
  private Paint decodeGradient11(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color39, decodeColor(this.color39, this.color40, 0.5F), this.color40 });
  }
  
  private Paint decodeGradient12(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color45, decodeColor(this.color45, this.color46, 0.5F), this.color46 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\ComboBoxPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */