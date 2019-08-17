package javax.swing.plaf.metal;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToolTipUI;
import javax.swing.text.View;
import sun.swing.SwingUtilities2;

public class MetalToolTipUI extends BasicToolTipUI {
  static MetalToolTipUI sharedInstance = new MetalToolTipUI();
  
  private Font smallFont;
  
  private JToolTip tip;
  
  public static final int padSpaceBetweenStrings = 12;
  
  private String acceleratorDelimiter;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return sharedInstance; }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    this.tip = (JToolTip)paramJComponent;
    Font font = paramJComponent.getFont();
    this.smallFont = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
    this.acceleratorDelimiter = UIManager.getString("MenuItem.acceleratorDelimiter");
    if (this.acceleratorDelimiter == null)
      this.acceleratorDelimiter = "-"; 
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    super.uninstallUI(paramJComponent);
    this.tip = null;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    int i;
    JToolTip jToolTip = (JToolTip)paramJComponent;
    Font font = paramJComponent.getFont();
    FontMetrics fontMetrics1 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, font);
    Dimension dimension = paramJComponent.getSize();
    paramGraphics.setColor(paramJComponent.getForeground());
    String str1 = jToolTip.getTipText();
    if (str1 == null)
      str1 = ""; 
    String str2 = getAcceleratorString(jToolTip);
    FontMetrics fontMetrics2 = SwingUtilities2.getFontMetrics(paramJComponent, paramGraphics, this.smallFont);
    int j = calcAccelSpacing(paramJComponent, fontMetrics2, str2);
    Insets insets = jToolTip.getInsets();
    Rectangle rectangle = new Rectangle(insets.left + 3, insets.top, dimension.width - insets.left + insets.right - 6 - j, dimension.height - insets.top + insets.bottom);
    View view = (View)paramJComponent.getClientProperty("html");
    if (view != null) {
      view.paint(paramGraphics, rectangle);
      i = BasicHTML.getHTMLBaseline(view, rectangle.width, rectangle.height);
    } else {
      paramGraphics.setFont(font);
      SwingUtilities2.drawString(jToolTip, paramGraphics, str1, rectangle.x, rectangle.y + fontMetrics1.getAscent());
      i = fontMetrics1.getAscent();
    } 
    if (!str2.equals("")) {
      paramGraphics.setFont(this.smallFont);
      paramGraphics.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
      SwingUtilities2.drawString(jToolTip, paramGraphics, str2, jToolTip.getWidth() - 1 - insets.right - j + 12 - 3, rectangle.y + i);
    } 
  }
  
  private int calcAccelSpacing(JComponent paramJComponent, FontMetrics paramFontMetrics, String paramString) { return paramString.equals("") ? 0 : (12 + SwingUtilities2.stringWidth(paramJComponent, paramFontMetrics, paramString)); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension = super.getPreferredSize(paramJComponent);
    String str = getAcceleratorString((JToolTip)paramJComponent);
    if (!str.equals(""))
      dimension.width += calcAccelSpacing(paramJComponent, paramJComponent.getFontMetrics(this.smallFont), str); 
    return dimension;
  }
  
  protected boolean isAcceleratorHidden() {
    Boolean bool = (Boolean)UIManager.get("ToolTip.hideAccelerator");
    return (bool != null && bool.booleanValue());
  }
  
  private String getAcceleratorString(JToolTip paramJToolTip) {
    this.tip = paramJToolTip;
    String str = getAcceleratorString();
    this.tip = null;
    return str;
  }
  
  public String getAcceleratorString() {
    if (this.tip == null || isAcceleratorHidden())
      return ""; 
    JComponent jComponent = this.tip.getComponent();
    if (!(jComponent instanceof javax.swing.AbstractButton))
      return ""; 
    KeyStroke[] arrayOfKeyStroke = jComponent.getInputMap(2).keys();
    if (arrayOfKeyStroke == null)
      return ""; 
    String str = "";
    boolean bool = false;
    if (bool < arrayOfKeyStroke.length) {
      int i = arrayOfKeyStroke[bool].getModifiers();
      str = KeyEvent.getKeyModifiersText(i) + this.acceleratorDelimiter + KeyEvent.getKeyText(arrayOfKeyStroke[bool].getKeyCode());
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalToolTipUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */