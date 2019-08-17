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

final class InternalFrameTitlePaneMaximizeButtonPainter extends AbstractRegionPainter {
  static final int BACKGROUND_DISABLED_WINDOWMAXIMIZED = 1;
  
  static final int BACKGROUND_ENABLED_WINDOWMAXIMIZED = 2;
  
  static final int BACKGROUND_MOUSEOVER_WINDOWMAXIMIZED = 3;
  
  static final int BACKGROUND_PRESSED_WINDOWMAXIMIZED = 4;
  
  static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 5;
  
  static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 6;
  
  static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED_WINDOWMAXIMIZED = 7;
  
  static final int BACKGROUND_DISABLED = 8;
  
  static final int BACKGROUND_ENABLED = 9;
  
  static final int BACKGROUND_MOUSEOVER = 10;
  
  static final int BACKGROUND_PRESSED = 11;
  
  static final int BACKGROUND_ENABLED_WINDOWNOTFOCUSED = 12;
  
  static final int BACKGROUND_MOUSEOVER_WINDOWNOTFOCUSED = 13;
  
  static final int BACKGROUND_PRESSED_WINDOWNOTFOCUSED = 14;
  
  private int state;
  
  private AbstractRegionPainter.PaintContext ctx;
  
  private Path2D path = new Path2D.Float();
  
  private Rectangle2D rect = new Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
  
  private Ellipse2D ellipse = new Ellipse2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
  
  private Color color1 = decodeColor("nimbusGreen", 0.43362403F, -0.6792196F, 0.054901958F, 0);
  
  private Color color2 = decodeColor("nimbusGreen", 0.44056845F, -0.631913F, -0.039215684F, 0);
  
  private Color color3 = decodeColor("nimbusGreen", 0.44056845F, -0.67475206F, 0.06666666F, 0);
  
  private Color color4 = new Color(255, 200, 0, 255);
  
  private Color color5 = decodeColor("nimbusGreen", 0.4355179F, -0.6581704F, -0.011764705F, 0);
  
  private Color color6 = decodeColor("nimbusGreen", 0.44484192F, -0.644647F, -0.031372547F, 0);
  
  private Color color7 = decodeColor("nimbusGreen", 0.44484192F, -0.6480447F, 0.0F, 0);
  
  private Color color8 = decodeColor("nimbusGreen", 0.4366002F, -0.6368381F, -0.04705882F, 0);
  
  private Color color9 = decodeColor("nimbusGreen", 0.44484192F, -0.6423572F, -0.05098039F, 0);
  
  private Color color10 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.062449392F, 0.07058823F, 0);
  
  private Color color11 = decodeColor("nimbusBlueGrey", -0.008547008F, -0.04174325F, -0.0039215684F, -13);
  
  private Color color12 = decodeColor("nimbusBlueGrey", 0.0F, -0.049920253F, 0.031372547F, 0);
  
  private Color color13 = decodeColor("nimbusBlueGrey", 0.0055555105F, -0.0029994324F, -0.38039216F, -185);
  
  private Color color14 = decodeColor("nimbusGreen", 0.1627907F, 0.2793296F, -0.6431373F, 0);
  
  private Color color15 = decodeColor("nimbusGreen", 0.025363803F, 0.2454313F, -0.2392157F, 0);
  
  private Color color16 = decodeColor("nimbusGreen", 0.02642706F, -0.3456704F, -0.011764705F, 0);
  
  private Color color17 = decodeColor("nimbusGreen", 0.025363803F, 0.2373128F, -0.23529413F, 0);
  
  private Color color18 = decodeColor("nimbusGreen", 0.025363803F, 0.0655365F, -0.13333333F, 0);
  
  private Color color19 = decodeColor("nimbusGreen", -0.0087068975F, -0.009330213F, -0.32156864F, 0);
  
  private Color color20 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -13);
  
  private Color color21 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, -33);
  
  private Color color22 = decodeColor("nimbusGreen", 0.1627907F, 0.2793296F, -0.627451F, 0);
  
  private Color color23 = decodeColor("nimbusGreen", 0.04572721F, 0.2793296F, -0.37254903F, 0);
  
  private Color color24 = decodeColor("nimbusGreen", 0.009822637F, -0.34243205F, 0.054901958F, 0);
  
  private Color color25 = decodeColor("nimbusGreen", 0.010559708F, 0.13167858F, -0.11764705F, 0);
  
  private Color color26 = decodeColor("nimbusGreen", 0.010559708F, 0.12599629F, -0.11372548F, 0);
  
  private Color color27 = decodeColor("nimbusGreen", 0.010559708F, 9.2053413E-4F, -0.011764705F, 0);
  
  private Color color28 = decodeColor("nimbusGreen", 0.015249729F, 0.2793296F, -0.22352943F, -49);
  
  private Color color29 = decodeColor("nimbusGreen", 0.01279068F, 0.2793296F, -0.19215685F, 0);
  
  private Color color30 = decodeColor("nimbusGreen", 0.013319805F, 0.2793296F, -0.20784315F, 0);
  
  private Color color31 = decodeColor("nimbusGreen", 0.009604409F, 0.2793296F, -0.16862744F, 0);
  
  private Color color32 = decodeColor("nimbusGreen", 0.011600211F, 0.2793296F, -0.15294117F, 0);
  
  private Color color33 = decodeColor("nimbusGreen", 0.011939123F, 0.2793296F, -0.16470587F, 0);
  
  private Color color34 = decodeColor("nimbusGreen", 0.009506017F, 0.257901F, -0.15294117F, 0);
  
  private Color color35 = decodeColor("nimbusGreen", -0.17054264F, -0.7206704F, -0.7019608F, 0);
  
  private Color color36 = decodeColor("nimbusGreen", 0.07804492F, 0.2793296F, -0.47058827F, 0);
  
  private Color color37 = decodeColor("nimbusGreen", 0.03592503F, -0.23865601F, -0.15686274F, 0);
  
  private Color color38 = decodeColor("nimbusGreen", 0.035979107F, 0.23766291F, -0.3254902F, 0);
  
  private Color color39 = decodeColor("nimbusGreen", 0.03690417F, 0.2793296F, -0.33333334F, 0);
  
  private Color color40 = decodeColor("nimbusGreen", 0.09681849F, 0.2793296F, -0.5137255F, 0);
  
  private Color color41 = decodeColor("nimbusGreen", 0.06535478F, 0.2793296F, -0.44705883F, 0);
  
  private Color color42 = decodeColor("nimbusGreen", 0.0675526F, 0.2793296F, -0.454902F, 0);
  
  private Color color43 = decodeColor("nimbusGreen", 0.060800627F, 0.2793296F, -0.4392157F, 0);
  
  private Color color44 = decodeColor("nimbusGreen", 0.06419912F, 0.2793296F, -0.42352942F, 0);
  
  private Color color45 = decodeColor("nimbusGreen", 0.06375685F, 0.2793296F, -0.43137255F, 0);
  
  private Color color46 = decodeColor("nimbusGreen", 0.048207358F, 0.2793296F, -0.3882353F, 0);
  
  private Color color47 = decodeColor("nimbusGreen", 0.057156876F, 0.2793296F, -0.42352942F, 0);
  
  private Color color48 = decodeColor("nimbusGreen", 0.44056845F, -0.62133265F, -0.109803915F, 0);
  
  private Color color49 = decodeColor("nimbusGreen", 0.44056845F, -0.5843068F, -0.27058825F, 0);
  
  private Color color50 = decodeColor("nimbusGreen", 0.4294573F, -0.698349F, 0.17647058F, 0);
  
  private Color color51 = decodeColor("nimbusGreen", 0.45066953F, -0.665394F, 0.07843137F, 0);
  
  private Color color52 = decodeColor("nimbusGreen", 0.44056845F, -0.65913194F, 0.062745094F, 0);
  
  private Color color53 = decodeColor("nimbusGreen", 0.44056845F, -0.6609689F, 0.086274505F, 0);
  
  private Color color54 = decodeColor("nimbusGreen", 0.44056845F, -0.6578432F, 0.04705882F, 0);
  
  private Color color55 = decodeColor("nimbusGreen", 0.4355179F, -0.6633787F, 0.05098039F, 0);
  
  private Color color56 = decodeColor("nimbusGreen", 0.4355179F, -0.664548F, 0.06666666F, 0);
  
  private Color color57 = decodeColor("nimbusBlueGrey", 0.0F, -0.029445238F, -0.30980393F, -13);
  
  private Color color58 = decodeColor("nimbusBlueGrey", 0.0F, -0.027957506F, -0.31764707F, -33);
  
  private Color color59 = decodeColor("nimbusGreen", 0.43202144F, -0.64722407F, -0.007843137F, 0);
  
  private Color color60 = decodeColor("nimbusGreen", 0.44056845F, -0.6339652F, -0.02352941F, 0);
  
  private Color color61 = new Color(165, 169, 176, 255);
  
  private Color color62 = decodeColor("nimbusBlueGrey", -0.00505054F, -0.057128258F, 0.062745094F, 0);
  
  private Color color63 = decodeColor("nimbusBlueGrey", -0.003968239F, -0.035257496F, -0.015686274F, 0);
  
  private Color color64 = new Color(64, 88, 0, 255);
  
  private Color color65 = decodeColor("nimbusBlueGrey", 0.0F, -0.110526316F, 0.25490195F, 0);
  
  private Color color66 = decodeColor("nimbusBlueGrey", 0.004830897F, -0.00920473F, 0.14509803F, -101);
  
  private Color color67 = decodeColor("nimbusGreen", 0.009564877F, 0.100521624F, -0.109803915F, 0);
  
  private Color color68 = new Color(113, 125, 0, 255);
  
  private Color color69 = decodeColor("nimbusBlueGrey", 0.0025252104F, -0.0067527294F, 0.086274505F, -65);
  
  private Color color70 = decodeColor("nimbusGreen", 0.03129223F, 0.2793296F, -0.27450982F, 0);
  
  private Color color71 = new Color(19, 48, 0, 255);
  
  private Color color72 = decodeColor("nimbusBlueGrey", 0.0F, -0.029445238F, -0.30980393F, 0);
  
  private Object[] componentColors;
  
  public InternalFrameTitlePaneMaximizeButtonPainter(AbstractRegionPainter.PaintContext paramPaintContext, int paramInt) {
    this.state = paramInt;
    this.ctx = paramPaintContext;
  }
  
  protected void doPaint(Graphics2D paramGraphics2D, JComponent paramJComponent, int paramInt1, int paramInt2, Object[] paramArrayOfObject) {
    this.componentColors = paramArrayOfObject;
    switch (this.state) {
      case 1:
        paintBackgroundDisabledAndWindowMaximized(paramGraphics2D);
        break;
      case 2:
        paintBackgroundEnabledAndWindowMaximized(paramGraphics2D);
        break;
      case 3:
        paintBackgroundMouseOverAndWindowMaximized(paramGraphics2D);
        break;
      case 4:
        paintBackgroundPressedAndWindowMaximized(paramGraphics2D);
        break;
      case 5:
        paintBackgroundEnabledAndWindowNotFocusedAndWindowMaximized(paramGraphics2D);
        break;
      case 6:
        paintBackgroundMouseOverAndWindowNotFocusedAndWindowMaximized(paramGraphics2D);
        break;
      case 7:
        paintBackgroundPressedAndWindowNotFocusedAndWindowMaximized(paramGraphics2D);
        break;
      case 8:
        paintBackgroundDisabled(paramGraphics2D);
        break;
      case 9:
        paintBackgroundEnabled(paramGraphics2D);
        break;
      case 10:
        paintBackgroundMouseOver(paramGraphics2D);
        break;
      case 11:
        paintBackgroundPressed(paramGraphics2D);
        break;
      case 12:
        paintBackgroundEnabledAndWindowNotFocused(paramGraphics2D);
        break;
      case 13:
        paintBackgroundMouseOverAndWindowNotFocused(paramGraphics2D);
        break;
      case 14:
        paintBackgroundPressedAndWindowNotFocused(paramGraphics2D);
        break;
    } 
  }
  
  protected final AbstractRegionPainter.PaintContext getPaintContext() { return this.ctx; }
  
  private void paintBackgroundDisabledAndWindowMaximized(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient1(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient2(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color5);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color6);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color6);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(this.color8);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(this.color9);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color7);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(decodeGradient3(this.path));
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color12);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabledAndWindowMaximized(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect9();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect10();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color19);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color20);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color21);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndWindowMaximized(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient7(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color28);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color29);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color30);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color31);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect9();
    paramGraphics2D.setPaint(this.color32);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(this.color33);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect10();
    paramGraphics2D.setPaint(this.color34);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color31);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color20);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color21);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressedAndWindowMaximized(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient9(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color40);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color41);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color42);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color43);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(this.color44);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(this.color45);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect10();
    paramGraphics2D.setPaint(this.color46);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color47);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color20);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color21);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabledAndWindowNotFocusedAndWindowMaximized(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient10(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient11(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color54);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color55);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color56);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color57);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color58);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndWindowNotFocusedAndWindowMaximized(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient7(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color28);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color29);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color30);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color31);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect9();
    paramGraphics2D.setPaint(this.color32);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(this.color33);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect10();
    paramGraphics2D.setPaint(this.color34);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color31);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color20);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color21);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressedAndWindowNotFocusedAndWindowMaximized(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient9(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect2();
    paramGraphics2D.setPaint(this.color40);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect3();
    paramGraphics2D.setPaint(this.color41);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect4();
    paramGraphics2D.setPaint(this.color42);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect5();
    paramGraphics2D.setPaint(this.color43);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect6();
    paramGraphics2D.setPaint(this.color44);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect7();
    paramGraphics2D.setPaint(this.color45);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect10();
    paramGraphics2D.setPaint(this.color46);
    paramGraphics2D.fill(this.rect);
    this.rect = decodeRect8();
    paramGraphics2D.setPaint(this.color47);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath1();
    paramGraphics2D.setPaint(this.color20);
    paramGraphics2D.fill(this.path);
    this.path = decodePath2();
    paramGraphics2D.setPaint(this.color21);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundDisabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient1(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient12(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color61);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(decodeGradient13(this.path));
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabled(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color13);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient4(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient5(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color64);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color65);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOver(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color66);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient14(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color68);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color65);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressed(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color69);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient15(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color71);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color65);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundEnabledAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient10(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient16(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color72);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundMouseOverAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color66);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient6(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient14(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color68);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color65);
    paramGraphics2D.fill(this.path);
  }
  
  private void paintBackgroundPressedAndWindowNotFocused(Graphics2D paramGraphics2D) {
    this.roundRect = decodeRoundRect3();
    paramGraphics2D.setPaint(this.color69);
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect1();
    paramGraphics2D.setPaint(decodeGradient8(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.roundRect = decodeRoundRect2();
    paramGraphics2D.setPaint(decodeGradient15(this.roundRect));
    paramGraphics2D.fill(this.roundRect);
    this.rect = decodeRect1();
    paramGraphics2D.setPaint(this.color4);
    paramGraphics2D.fill(this.rect);
    this.path = decodePath3();
    paramGraphics2D.setPaint(this.color71);
    paramGraphics2D.fill(this.path);
    this.path = decodePath4();
    paramGraphics2D.setPaint(this.color65);
    paramGraphics2D.fill(this.path);
  }
  
  private RoundRectangle2D decodeRoundRect1() {
    this.roundRect.setRoundRect(decodeX(1.0F), decodeY(1.0F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(1.9444444F) - decodeY(1.0F)), 8.600000381469727D, 8.600000381469727D);
    return this.roundRect;
  }
  
  private RoundRectangle2D decodeRoundRect2() {
    this.roundRect.setRoundRect(decodeX(1.0526316F), decodeY(1.0555556F), (decodeX(1.9473684F) - decodeX(1.0526316F)), (decodeY(1.8888888F) - decodeY(1.0555556F)), 6.75D, 6.75D);
    return this.roundRect;
  }
  
  private Rectangle2D decodeRect1() {
    this.rect.setRect(decodeX(1.0F), decodeY(1.0F), (decodeX(1.0F) - decodeX(1.0F)), (decodeY(1.0F) - decodeY(1.0F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect2() {
    this.rect.setRect(decodeX(1.2165072F), decodeY(1.2790405F), (decodeX(1.6746411F) - decodeX(1.2165072F)), (decodeY(1.3876263F) - decodeY(1.2790405F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect3() {
    this.rect.setRect(decodeX(1.2212919F), decodeY(1.6047981F), (decodeX(1.270335F) - decodeX(1.2212919F)), (decodeY(1.3876263F) - decodeY(1.6047981F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect4() {
    this.rect.setRect(decodeX(1.2643541F), decodeY(1.5542929F), (decodeX(1.6315789F) - decodeX(1.2643541F)), (decodeY(1.5997474F) - decodeY(1.5542929F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect5() {
    this.rect.setRect(decodeX(1.6267943F), decodeY(1.3888888F), (decodeX(1.673445F) - decodeX(1.6267943F)), (decodeY(1.6085858F) - decodeY(1.3888888F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect6() {
    this.rect.setRect(decodeX(1.3684211F), decodeY(1.6111112F), (decodeX(1.4210527F) - decodeX(1.3684211F)), (decodeY(1.7777778F) - decodeY(1.6111112F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect7() {
    this.rect.setRect(decodeX(1.4389952F), decodeY(1.7209597F), (decodeX(1.7882775F) - decodeX(1.4389952F)), (decodeY(1.7765152F) - decodeY(1.7209597F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect8() {
    this.rect.setRect(decodeX(1.5645933F), decodeY(1.4078283F), (decodeX(1.7870812F) - decodeX(1.5645933F)), (decodeY(1.5239899F) - decodeY(1.4078283F)));
    return this.rect;
  }
  
  private Path2D decodePath1() {
    this.path.reset();
    this.path.moveTo(decodeX(1.2105263F), decodeY(1.2222222F));
    this.path.lineTo(decodeX(1.6315789F), decodeY(1.2222222F));
    this.path.lineTo(decodeX(1.6315789F), decodeY(1.5555556F));
    this.path.lineTo(decodeX(1.2105263F), decodeY(1.5555556F));
    this.path.lineTo(decodeX(1.2105263F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.2631578F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.2631578F), decodeY(1.5F));
    this.path.lineTo(decodeX(1.5789473F), decodeY(1.5F));
    this.path.lineTo(decodeX(1.5789473F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.2105263F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.2105263F), decodeY(1.2222222F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath2() {
    this.path.reset();
    this.path.moveTo(decodeX(1.6842105F), decodeY(1.3888888F));
    this.path.lineTo(decodeX(1.6842105F), decodeY(1.5F));
    this.path.lineTo(decodeX(1.7368422F), decodeY(1.5F));
    this.path.lineTo(decodeX(1.7368422F), decodeY(1.6666667F));
    this.path.lineTo(decodeX(1.4210527F), decodeY(1.6666667F));
    this.path.lineTo(decodeX(1.4210527F), decodeY(1.6111112F));
    this.path.lineTo(decodeX(1.3684211F), decodeY(1.6111112F));
    this.path.lineTo(decodeX(1.3684211F), decodeY(1.7222222F));
    this.path.lineTo(decodeX(1.7894738F), decodeY(1.7222222F));
    this.path.lineTo(decodeX(1.7894738F), decodeY(1.3888888F));
    this.path.lineTo(decodeX(1.6842105F), decodeY(1.3888888F));
    this.path.closePath();
    return this.path;
  }
  
  private RoundRectangle2D decodeRoundRect3() {
    this.roundRect.setRoundRect(decodeX(1.0F), decodeY(1.6111112F), (decodeX(2.0F) - decodeX(1.0F)), (decodeY(2.0F) - decodeY(1.6111112F)), 6.0D, 6.0D);
    return this.roundRect;
  }
  
  private Rectangle2D decodeRect9() {
    this.rect.setRect(decodeX(1.3815789F), decodeY(1.6111112F), (decodeX(1.4366028F) - decodeX(1.3815789F)), (decodeY(1.7739899F) - decodeY(1.6111112F)));
    return this.rect;
  }
  
  private Rectangle2D decodeRect10() {
    this.rect.setRect(decodeX(1.7918661F), decodeY(1.7752526F), (decodeX(1.8349283F) - decodeX(1.7918661F)), (decodeY(1.4217172F) - decodeY(1.7752526F)));
    return this.rect;
  }
  
  private Path2D decodePath3() {
    this.path.reset();
    this.path.moveTo(decodeX(1.1913875F), decodeY(1.2916666F));
    this.path.lineTo(decodeX(1.1925838F), decodeY(1.7462121F));
    this.path.lineTo(decodeX(1.8157895F), decodeY(1.7449496F));
    this.path.lineTo(decodeX(1.819378F), decodeY(1.2916666F));
    this.path.lineTo(decodeX(1.722488F), decodeY(1.2916666F));
    this.path.lineTo(decodeX(1.7320573F), decodeY(1.669192F));
    this.path.lineTo(decodeX(1.2799044F), decodeY(1.6565657F));
    this.path.lineTo(decodeX(1.284689F), decodeY(1.3863636F));
    this.path.lineTo(decodeX(1.7260766F), decodeY(1.385101F));
    this.path.lineTo(decodeX(1.722488F), decodeY(1.2904041F));
    this.path.lineTo(decodeX(1.1913875F), decodeY(1.2916666F));
    this.path.closePath();
    return this.path;
  }
  
  private Path2D decodePath4() {
    this.path.reset();
    this.path.moveTo(decodeX(1.2105263F), decodeY(1.2222222F));
    this.path.lineTo(decodeX(1.2105263F), decodeY(1.7222222F));
    this.path.lineTo(decodeX(1.7894738F), decodeY(1.7222222F));
    this.path.lineTo(decodeX(1.7894738F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.7368422F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.7368422F), decodeY(1.6666667F));
    this.path.lineTo(decodeX(1.2631578F), decodeY(1.6666667F));
    this.path.lineTo(decodeX(1.2631578F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.7894738F), decodeY(1.3333334F));
    this.path.lineTo(decodeX(1.7894738F), decodeY(1.2222222F));
    this.path.lineTo(decodeX(1.2105263F), decodeY(1.2222222F));
    this.path.closePath();
    return this.path;
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
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient3(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color10, decodeColor(this.color10, this.color11, 0.5F), this.color11 });
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
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color16, decodeColor(this.color16, this.color15, 0.5F), this.color15, decodeColor(this.color15, this.color17, 0.5F), this.color17, decodeColor(this.color17, this.color18, 0.5F), this.color18 });
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
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color24, decodeColor(this.color24, this.color25, 0.5F), this.color25, decodeColor(this.color25, this.color26, 0.5F), this.color26, decodeColor(this.color26, this.color27, 0.5F), this.color27 });
  }
  
  private Paint decodeGradient8(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color35, decodeColor(this.color35, this.color36, 0.5F), this.color36 });
  }
  
  private Paint decodeGradient9(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color37, decodeColor(this.color37, this.color38, 0.5F), this.color38, decodeColor(this.color38, this.color39, 0.5F), this.color39, decodeColor(this.color39, this.color18, 0.5F), this.color18 });
  }
  
  private Paint decodeGradient10(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.24868421F * f3 + f1, 0.0014705883F * f4 + f2, 0.24868421F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.0F, 0.5F, 1.0F }, new Color[] { this.color48, decodeColor(this.color48, this.color49, 0.5F), this.color49 });
  }
  
  private Paint decodeGradient11(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color50, decodeColor(this.color50, this.color51, 0.5F), this.color51, decodeColor(this.color51, this.color52, 0.5F), this.color52, decodeColor(this.color52, this.color53, 0.5F), this.color53 });
  }
  
  private Paint decodeGradient12(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.6082097F, 0.6766467F, 0.83832335F, 1.0F }, new Color[] { this.color3, decodeColor(this.color3, this.color59, 0.5F), this.color59, decodeColor(this.color59, this.color60, 0.5F), this.color60, decodeColor(this.color60, this.color2, 0.5F), this.color2 });
  }
  
  private Paint decodeGradient13(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.5F * f3 + f1, 0.0F * f4 + f2, 0.5F * f3 + f1, 1.0F * f4 + f2, new float[] { 0.26047903F, 0.6302395F, 1.0F }, new Color[] { this.color62, decodeColor(this.color62, this.color63, 0.5F), this.color63 });
  }
  
  private Paint decodeGradient14(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.5951705F, 0.6505682F, 0.8252841F, 1.0F }, new Color[] { this.color24, decodeColor(this.color24, this.color67, 0.5F), this.color67, decodeColor(this.color67, this.color25, 0.5F), this.color25, decodeColor(this.color25, this.color27, 0.5F), this.color27 });
  }
  
  private Paint decodeGradient15(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.66659296F, 0.79341316F, 0.8967066F, 1.0F }, new Color[] { this.color37, decodeColor(this.color37, this.color38, 0.5F), this.color38, decodeColor(this.color38, this.color39, 0.5F), this.color39, decodeColor(this.color39, this.color70, 0.5F), this.color70 });
  }
  
  private Paint decodeGradient16(Shape paramShape) {
    Rectangle2D rectangle2D = paramShape.getBounds2D();
    float f1 = (float)rectangle2D.getX();
    float f2 = (float)rectangle2D.getY();
    float f3 = (float)rectangle2D.getWidth();
    float f4 = (float)rectangle2D.getHeight();
    return decodeGradient(0.25F * f3 + f1, 0.0F * f4 + f2, 0.25441176F * f3 + f1, 1.0016667F * f4 + f2, new float[] { 0.0F, 0.26988637F, 0.53977275F, 0.6291678F, 0.7185629F, 0.8592814F, 1.0F }, new Color[] { this.color50, decodeColor(this.color50, this.color52, 0.5F), this.color52, decodeColor(this.color52, this.color52, 0.5F), this.color52, decodeColor(this.color52, this.color53, 0.5F), this.color53 });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\nimbus\InternalFrameTitlePaneMaximizeButtonPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */