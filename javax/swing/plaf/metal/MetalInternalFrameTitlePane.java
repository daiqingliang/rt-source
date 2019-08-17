package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.swing.SwingUtilities2;

public class MetalInternalFrameTitlePane extends BasicInternalFrameTitlePane {
  protected boolean isPalette = false;
  
  protected Icon paletteCloseIcon;
  
  protected int paletteTitleHeight;
  
  private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
  
  private String selectedBackgroundKey;
  
  private String selectedForegroundKey;
  
  private String selectedShadowKey;
  
  private boolean wasClosable;
  
  int buttonsWidth = 0;
  
  MetalBumps activeBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlDarkShadow(), (UIManager.get("InternalFrame.activeTitleGradient") != null) ? null : MetalLookAndFeel.getPrimaryControl());
  
  MetalBumps inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), (UIManager.get("InternalFrame.inactiveTitleGradient") != null) ? null : MetalLookAndFeel.getControl());
  
  MetalBumps paletteBumps;
  
  private Color activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
  
  private Color activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
  
  public MetalInternalFrameTitlePane(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  public void addNotify() {
    super.addNotify();
    updateOptionPaneState();
  }
  
  protected void installDefaults() {
    super.installDefaults();
    setFont(UIManager.getFont("InternalFrame.titleFont"));
    this.paletteTitleHeight = UIManager.getInt("InternalFrame.paletteTitleHeight");
    this.paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");
    this.wasClosable = this.frame.isClosable();
    this.selectedForegroundKey = this.selectedBackgroundKey = null;
    if (MetalLookAndFeel.usingOcean())
      setOpaque(true); 
  }
  
  protected void uninstallDefaults() {
    super.uninstallDefaults();
    if (this.wasClosable != this.frame.isClosable())
      this.frame.setClosable(this.wasClosable); 
  }
  
  protected void createButtons() {
    super.createButtons();
    Boolean bool = this.frame.isSelected() ? Boolean.TRUE : Boolean.FALSE;
    this.iconButton.putClientProperty("paintActive", bool);
    this.iconButton.setBorder(handyEmptyBorder);
    this.maxButton.putClientProperty("paintActive", bool);
    this.maxButton.setBorder(handyEmptyBorder);
    this.closeButton.putClientProperty("paintActive", bool);
    this.closeButton.setBorder(handyEmptyBorder);
    this.closeButton.setBackground(MetalLookAndFeel.getPrimaryControlShadow());
    if (MetalLookAndFeel.usingOcean()) {
      this.iconButton.setContentAreaFilled(false);
      this.maxButton.setContentAreaFilled(false);
      this.closeButton.setContentAreaFilled(false);
    } 
  }
  
  protected void assembleSystemMenu() {}
  
  protected void addSystemMenuItems(JMenu paramJMenu) {}
  
  protected void showSystemMenu() {}
  
  protected void addSubComponents() {
    add(this.iconButton);
    add(this.maxButton);
    add(this.closeButton);
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return new MetalPropertyChangeHandler(); }
  
  protected LayoutManager createLayout() { return new MetalTitlePaneLayout(); }
  
  public void paintPalette(Graphics paramGraphics) {
    boolean bool = MetalUtils.isLeftToRight(this.frame);
    int i = getWidth();
    int j = getHeight();
    if (this.paletteBumps == null)
      this.paletteBumps = new MetalBumps(0, 0, MetalLookAndFeel.getPrimaryControlHighlight(), MetalLookAndFeel.getPrimaryControlInfo(), MetalLookAndFeel.getPrimaryControlShadow()); 
    ColorUIResource colorUIResource1 = MetalLookAndFeel.getPrimaryControlShadow();
    ColorUIResource colorUIResource2 = MetalLookAndFeel.getPrimaryControlDarkShadow();
    paramGraphics.setColor(colorUIResource1);
    paramGraphics.fillRect(0, 0, i, j);
    paramGraphics.setColor(colorUIResource2);
    paramGraphics.drawLine(0, j - 1, i, j - 1);
    byte b = bool ? 4 : (this.buttonsWidth + 4);
    int k = i - this.buttonsWidth - 8;
    int m = getHeight() - 4;
    this.paletteBumps.setBumpArea(k, m);
    this.paletteBumps.paintIcon(this, paramGraphics, b, 2);
  }
  
  public void paintComponent(Graphics paramGraphics) {
    int n;
    int m;
    String str1;
    MetalBumps metalBumps;
    if (this.isPalette) {
      paintPalette(paramGraphics);
      return;
    } 
    boolean bool1 = MetalUtils.isLeftToRight(this.frame);
    boolean bool2 = this.frame.isSelected();
    int i = getWidth();
    int j = getHeight();
    Color color1 = null;
    Color color2 = null;
    Color color3 = null;
    if (bool2) {
      if (!MetalLookAndFeel.usingOcean()) {
        this.closeButton.setContentAreaFilled(true);
        this.maxButton.setContentAreaFilled(true);
        this.iconButton.setContentAreaFilled(true);
      } 
      if (this.selectedBackgroundKey != null)
        color1 = UIManager.getColor(this.selectedBackgroundKey); 
      if (color1 == null)
        color1 = MetalLookAndFeel.getWindowTitleBackground(); 
      if (this.selectedForegroundKey != null)
        color2 = UIManager.getColor(this.selectedForegroundKey); 
      if (this.selectedShadowKey != null)
        color3 = UIManager.getColor(this.selectedShadowKey); 
      if (color3 == null)
        color3 = MetalLookAndFeel.getPrimaryControlDarkShadow(); 
      if (color2 == null)
        color2 = MetalLookAndFeel.getWindowTitleForeground(); 
      this.activeBumps.setBumpColors(this.activeBumpsHighlight, this.activeBumpsShadow, (UIManager.get("InternalFrame.activeTitleGradient") != null) ? null : color1);
      metalBumps = this.activeBumps;
      str1 = "InternalFrame.activeTitleGradient";
    } else {
      if (!MetalLookAndFeel.usingOcean()) {
        this.closeButton.setContentAreaFilled(false);
        this.maxButton.setContentAreaFilled(false);
        this.iconButton.setContentAreaFilled(false);
      } 
      color1 = MetalLookAndFeel.getWindowTitleInactiveBackground();
      color2 = MetalLookAndFeel.getWindowTitleInactiveForeground();
      color3 = MetalLookAndFeel.getControlDarkShadow();
      metalBumps = this.inactiveBumps;
      str1 = "InternalFrame.inactiveTitleGradient";
    } 
    if (!MetalUtils.drawGradient(this, paramGraphics, str1, 0, 0, i, j, true)) {
      paramGraphics.setColor(color1);
      paramGraphics.fillRect(0, 0, i, j);
    } 
    paramGraphics.setColor(color3);
    paramGraphics.drawLine(0, j - 1, i, j - 1);
    paramGraphics.drawLine(0, 0, 0, 0);
    paramGraphics.drawLine(i - 1, 0, i - 1, 0);
    int k = bool1 ? 5 : (i - 5);
    String str2 = this.frame.getTitle();
    Icon icon = this.frame.getFrameIcon();
    if (icon != null) {
      if (!bool1)
        k -= icon.getIconWidth(); 
      m = j / 2 - icon.getIconHeight() / 2;
      icon.paintIcon(this.frame, paramGraphics, k, m);
      k += (bool1 ? (icon.getIconWidth() + 5) : -5);
    } 
    if (str2 != null) {
      Font font = getFont();
      paramGraphics.setFont(font);
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, paramGraphics, font);
      int i3 = fontMetrics.getHeight();
      paramGraphics.setColor(color2);
      int i4 = (j - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
      Rectangle rectangle = new Rectangle(0, 0, 0, 0);
      if (this.frame.isIconifiable()) {
        rectangle = this.iconButton.getBounds();
      } else if (this.frame.isMaximizable()) {
        rectangle = this.maxButton.getBounds();
      } else if (this.frame.isClosable()) {
        rectangle = this.closeButton.getBounds();
      } 
      if (bool1) {
        if (rectangle.x == 0)
          rectangle.x = this.frame.getWidth() - (this.frame.getInsets()).right - 2; 
        int i5 = rectangle.x - k - 4;
        str2 = getTitle(str2, fontMetrics, i5);
      } else {
        int i5 = k - rectangle.x - rectangle.width - 4;
        str2 = getTitle(str2, fontMetrics, i5);
        k -= SwingUtilities2.stringWidth(this.frame, fontMetrics, str2);
      } 
      int i2 = SwingUtilities2.stringWidth(this.frame, fontMetrics, str2);
      SwingUtilities2.drawString(this.frame, paramGraphics, str2, k, i4);
      k += (bool1 ? (i2 + 5) : -5);
    } 
    if (bool1) {
      n = i - this.buttonsWidth - k - 5;
      m = k;
    } else {
      n = k - this.buttonsWidth - 5;
      m = this.buttonsWidth + 5;
    } 
    byte b = 3;
    int i1 = getHeight() - 2 * b;
    metalBumps.setBumpArea(n, i1);
    metalBumps.paintIcon(this, paramGraphics, m, b);
  }
  
  public void setPalette(boolean paramBoolean) {
    this.isPalette = paramBoolean;
    if (this.isPalette) {
      this.closeButton.setIcon(this.paletteCloseIcon);
      if (this.frame.isMaximizable())
        remove(this.maxButton); 
      if (this.frame.isIconifiable())
        remove(this.iconButton); 
    } else {
      this.closeButton.setIcon(this.closeIcon);
      if (this.frame.isMaximizable())
        add(this.maxButton); 
      if (this.frame.isIconifiable())
        add(this.iconButton); 
    } 
    revalidate();
    repaint();
  }
  
  private void updateOptionPaneState() {
    int i = -2;
    boolean bool = this.wasClosable;
    Object object = this.frame.getClientProperty("JInternalFrame.messageType");
    if (object == null)
      return; 
    if (object instanceof Integer)
      i = ((Integer)object).intValue(); 
    switch (i) {
      case 0:
        this.selectedBackgroundKey = "OptionPane.errorDialog.titlePane.background";
        this.selectedForegroundKey = "OptionPane.errorDialog.titlePane.foreground";
        this.selectedShadowKey = "OptionPane.errorDialog.titlePane.shadow";
        bool = false;
        break;
      case 3:
        this.selectedBackgroundKey = "OptionPane.questionDialog.titlePane.background";
        this.selectedForegroundKey = "OptionPane.questionDialog.titlePane.foreground";
        this.selectedShadowKey = "OptionPane.questionDialog.titlePane.shadow";
        bool = false;
        break;
      case 2:
        this.selectedBackgroundKey = "OptionPane.warningDialog.titlePane.background";
        this.selectedForegroundKey = "OptionPane.warningDialog.titlePane.foreground";
        this.selectedShadowKey = "OptionPane.warningDialog.titlePane.shadow";
        bool = false;
        break;
      case -1:
      case 1:
        this.selectedBackgroundKey = this.selectedForegroundKey = this.selectedShadowKey = null;
        bool = false;
        break;
      default:
        this.selectedBackgroundKey = this.selectedForegroundKey = this.selectedShadowKey = null;
        break;
    } 
    if (bool != this.frame.isClosable())
      this.frame.setClosable(bool); 
  }
  
  class MetalPropertyChangeHandler extends BasicInternalFrameTitlePane.PropertyChangeHandler {
    MetalPropertyChangeHandler() { super(MetalInternalFrameTitlePane.this); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str.equals("selected")) {
        Boolean bool = (Boolean)param1PropertyChangeEvent.getNewValue();
        MetalInternalFrameTitlePane.this.iconButton.putClientProperty("paintActive", bool);
        MetalInternalFrameTitlePane.this.closeButton.putClientProperty("paintActive", bool);
        MetalInternalFrameTitlePane.this.maxButton.putClientProperty("paintActive", bool);
      } else if ("JInternalFrame.messageType".equals(str)) {
        MetalInternalFrameTitlePane.this.updateOptionPaneState();
        MetalInternalFrameTitlePane.this.frame.repaint();
      } 
      super.propertyChange(param1PropertyChangeEvent);
    }
  }
  
  class MetalTitlePaneLayout extends BasicInternalFrameTitlePane.TitlePaneLayout {
    MetalTitlePaneLayout() { super(MetalInternalFrameTitlePane.this); }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) { return minimumLayoutSize(param1Container); }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      int m;
      int i = 30;
      if (MetalInternalFrameTitlePane.this.frame.isClosable())
        i += 21; 
      if (MetalInternalFrameTitlePane.this.frame.isMaximizable())
        i += 16 + (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4); 
      if (MetalInternalFrameTitlePane.this.frame.isIconifiable())
        i += 16 + (MetalInternalFrameTitlePane.this.frame.isMaximizable() ? 2 : (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4)); 
      FontMetrics fontMetrics = MetalInternalFrameTitlePane.this.frame.getFontMetrics(MetalInternalFrameTitlePane.this.getFont());
      String str = MetalInternalFrameTitlePane.this.frame.getTitle();
      int j = (str != null) ? SwingUtilities2.stringWidth(MetalInternalFrameTitlePane.this.frame, fontMetrics, str) : 0;
      int k = (str != null) ? str.length() : 0;
      if (k > 2) {
        m = SwingUtilities2.stringWidth(MetalInternalFrameTitlePane.this.frame, fontMetrics, MetalInternalFrameTitlePane.this.frame.getTitle().substring(0, 2) + "...");
        i += ((j < m) ? j : m);
      } else {
        i += j;
      } 
      if (MetalInternalFrameTitlePane.this.isPalette) {
        m = MetalInternalFrameTitlePane.this.paletteTitleHeight;
      } else {
        int n = fontMetrics.getHeight();
        n += 7;
        Icon icon = MetalInternalFrameTitlePane.this.frame.getFrameIcon();
        int i1 = 0;
        if (icon != null)
          i1 = Math.min(icon.getIconHeight(), 16); 
        i1 += 5;
        m = Math.max(n, i1);
      } 
      return new Dimension(i, m);
    }
    
    public void layoutContainer(Container param1Container) {
      boolean bool = MetalUtils.isLeftToRight(MetalInternalFrameTitlePane.this.frame);
      int i = MetalInternalFrameTitlePane.this.getWidth();
      int j = bool ? i : 0;
      byte b = 2;
      int k = MetalInternalFrameTitlePane.this.closeButton.getIcon().getIconHeight();
      int m = MetalInternalFrameTitlePane.this.closeButton.getIcon().getIconWidth();
      if (MetalInternalFrameTitlePane.this.frame.isClosable())
        if (MetalInternalFrameTitlePane.this.isPalette) {
          byte b1 = 3;
          j += (bool ? (-b1 - m + 2) : b1);
          MetalInternalFrameTitlePane.this.closeButton.setBounds(j, b, m + 2, MetalInternalFrameTitlePane.this.getHeight() - 4);
          if (!bool)
            j += m + 2; 
        } else {
          byte b1 = 4;
          j += (bool ? (-b1 - m) : b1);
          MetalInternalFrameTitlePane.this.closeButton.setBounds(j, b, m, k);
          if (!bool)
            j += m; 
        }  
      if (MetalInternalFrameTitlePane.this.frame.isMaximizable() && !MetalInternalFrameTitlePane.this.isPalette) {
        byte b1 = MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4;
        j += (bool ? (-b1 - m) : b1);
        MetalInternalFrameTitlePane.this.maxButton.setBounds(j, b, m, k);
        if (!bool)
          j += m; 
      } 
      if (MetalInternalFrameTitlePane.this.frame.isIconifiable() && !MetalInternalFrameTitlePane.this.isPalette) {
        byte b1 = MetalInternalFrameTitlePane.this.frame.isMaximizable() ? 2 : (MetalInternalFrameTitlePane.this.frame.isClosable() ? 10 : 4);
        j += (bool ? (-b1 - m) : b1);
        MetalInternalFrameTitlePane.this.iconButton.setBounds(j, b, m, k);
        if (!bool)
          j += m; 
      } 
      MetalInternalFrameTitlePane.this.buttonsWidth = bool ? (i - j) : j;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */