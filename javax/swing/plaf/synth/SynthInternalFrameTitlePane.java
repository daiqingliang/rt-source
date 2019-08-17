package javax.swing.plaf.synth;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import sun.swing.SwingUtilities2;

class SynthInternalFrameTitlePane extends BasicInternalFrameTitlePane implements SynthUI, PropertyChangeListener {
  protected JPopupMenu systemPopupMenu;
  
  protected JButton menuButton;
  
  private SynthStyle style;
  
  private int titleSpacing;
  
  private int buttonSpacing;
  
  private int titleAlignment;
  
  public SynthInternalFrameTitlePane(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  public String getUIClassID() { return "InternalFrameTitlePaneUI"; }
  
  public SynthContext getContext(JComponent paramJComponent) { return getContext(paramJComponent, getComponentState(paramJComponent)); }
  
  public SynthContext getContext(JComponent paramJComponent, int paramInt) { return SynthContext.getContext(paramJComponent, this.style, paramInt); }
  
  private Region getRegion(JComponent paramJComponent) { return SynthLookAndFeel.getRegion(paramJComponent); }
  
  private int getComponentState(JComponent paramJComponent) { return (this.frame != null && this.frame.isSelected()) ? 512 : SynthLookAndFeel.getComponentState(paramJComponent); }
  
  protected void addSubComponents() {
    this.menuButton.setName("InternalFrameTitlePane.menuButton");
    this.iconButton.setName("InternalFrameTitlePane.iconifyButton");
    this.maxButton.setName("InternalFrameTitlePane.maximizeButton");
    this.closeButton.setName("InternalFrameTitlePane.closeButton");
    add(this.menuButton);
    add(this.iconButton);
    add(this.maxButton);
    add(this.closeButton);
  }
  
  protected void installListeners() {
    super.installListeners();
    this.frame.addPropertyChangeListener(this);
    addPropertyChangeListener(this);
  }
  
  protected void uninstallListeners() {
    this.frame.removePropertyChangeListener(this);
    removePropertyChangeListener(this);
    super.uninstallListeners();
  }
  
  private void updateStyle(JComponent paramJComponent) {
    SynthContext synthContext = getContext(this, 1);
    SynthStyle synthStyle = this.style;
    this.style = SynthLookAndFeel.updateStyle(synthContext, this);
    if (this.style != synthStyle) {
      this.maxIcon = this.style.getIcon(synthContext, "InternalFrameTitlePane.maximizeIcon");
      this.minIcon = this.style.getIcon(synthContext, "InternalFrameTitlePane.minimizeIcon");
      this.iconIcon = this.style.getIcon(synthContext, "InternalFrameTitlePane.iconifyIcon");
      this.closeIcon = this.style.getIcon(synthContext, "InternalFrameTitlePane.closeIcon");
      this.titleSpacing = this.style.getInt(synthContext, "InternalFrameTitlePane.titleSpacing", 2);
      this.buttonSpacing = this.style.getInt(synthContext, "InternalFrameTitlePane.buttonSpacing", 2);
      String str = (String)this.style.get(synthContext, "InternalFrameTitlePane.titleAlignment");
      this.titleAlignment = 10;
      if (str != null) {
        str = str.toUpperCase();
        if (str.equals("TRAILING")) {
          this.titleAlignment = 11;
        } else if (str.equals("CENTER")) {
          this.titleAlignment = 0;
        } 
      } 
    } 
    synthContext.dispose();
  }
  
  protected void installDefaults() {
    super.installDefaults();
    updateStyle(this);
  }
  
  protected void uninstallDefaults() {
    SynthContext synthContext = getContext(this, 1);
    this.style.uninstallDefaults(synthContext);
    synthContext.dispose();
    this.style = null;
    JInternalFrame.JDesktopIcon jDesktopIcon = this.frame.getDesktopIcon();
    if (jDesktopIcon != null && jDesktopIcon.getComponentPopupMenu() == this.systemPopupMenu)
      jDesktopIcon.setComponentPopupMenu(null); 
    super.uninstallDefaults();
  }
  
  protected void assembleSystemMenu() {
    this.systemPopupMenu = new JPopupMenuUIResource(null);
    addSystemMenuItems(this.systemPopupMenu);
    enableActions();
    this.menuButton = createNoFocusButton();
    updateMenuIcon();
    this.menuButton.addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent param1MouseEvent) {
            try {
              SynthInternalFrameTitlePane.this.frame.setSelected(true);
            } catch (PropertyVetoException propertyVetoException) {}
            SynthInternalFrameTitlePane.this.showSystemMenu();
          }
        });
    JPopupMenu jPopupMenu = this.frame.getComponentPopupMenu();
    if (jPopupMenu == null || jPopupMenu instanceof UIResource)
      this.frame.setComponentPopupMenu(this.systemPopupMenu); 
    if (this.frame.getDesktopIcon() != null) {
      jPopupMenu = this.frame.getDesktopIcon().getComponentPopupMenu();
      if (jPopupMenu == null || jPopupMenu instanceof UIResource)
        this.frame.getDesktopIcon().setComponentPopupMenu(this.systemPopupMenu); 
    } 
    setInheritsPopupMenu(true);
  }
  
  protected void addSystemMenuItems(JPopupMenu paramJPopupMenu) {
    JMenuItem jMenuItem = paramJPopupMenu.add(this.restoreAction);
    jMenuItem.setMnemonic(getButtonMnemonic("restore"));
    jMenuItem = paramJPopupMenu.add(this.moveAction);
    jMenuItem.setMnemonic(getButtonMnemonic("move"));
    jMenuItem = paramJPopupMenu.add(this.sizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("size"));
    jMenuItem = paramJPopupMenu.add(this.iconifyAction);
    jMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    jMenuItem = paramJPopupMenu.add(this.maximizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    paramJPopupMenu.add(new JSeparator());
    jMenuItem = paramJPopupMenu.add(this.closeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("close"));
  }
  
  private static int getButtonMnemonic(String paramString) {
    try {
      return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + paramString + "Button.mnemonic"));
    } catch (NumberFormatException numberFormatException) {
      return -1;
    } 
  }
  
  protected void showSystemMenu() {
    Insets insets = this.frame.getInsets();
    if (!this.frame.isIcon()) {
      this.systemPopupMenu.show(this.frame, this.menuButton.getX(), getY() + getHeight());
    } else {
      this.systemPopupMenu.show(this.menuButton, getX() - insets.left - insets.right, getY() - (this.systemPopupMenu.getPreferredSize()).height - insets.bottom - insets.top);
    } 
  }
  
  public void paintComponent(Graphics paramGraphics) {
    SynthContext synthContext = getContext(this);
    SynthLookAndFeel.update(synthContext, paramGraphics);
    synthContext.getPainter().paintInternalFrameTitlePaneBackground(synthContext, paramGraphics, 0, 0, getWidth(), getHeight());
    paint(synthContext, paramGraphics);
    synthContext.dispose();
  }
  
  protected void paint(SynthContext paramSynthContext, Graphics paramGraphics) {
    String str = this.frame.getTitle();
    if (str != null) {
      int k;
      int j;
      SynthStyle synthStyle = paramSynthContext.getStyle();
      paramGraphics.setColor(synthStyle.getColor(paramSynthContext, ColorType.TEXT_FOREGROUND));
      paramGraphics.setFont(synthStyle.getFont(paramSynthContext));
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, paramGraphics);
      int i = (getHeight() + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
      JButton jButton = null;
      if (this.frame.isIconifiable()) {
        jButton = this.iconButton;
      } else if (this.frame.isMaximizable()) {
        jButton = this.maxButton;
      } else if (this.frame.isClosable()) {
        jButton = this.closeButton;
      } 
      boolean bool = SynthLookAndFeel.isLeftToRight(this.frame);
      int m = this.titleAlignment;
      if (bool) {
        if (jButton != null) {
          j = jButton.getX() - this.titleSpacing;
        } else {
          j = this.frame.getWidth() - (this.frame.getInsets()).right - this.titleSpacing;
        } 
        k = this.menuButton.getX() + this.menuButton.getWidth() + this.titleSpacing;
      } else {
        if (jButton != null) {
          k = jButton.getX() + jButton.getWidth() + this.titleSpacing;
        } else {
          k = (this.frame.getInsets()).left + this.titleSpacing;
        } 
        j = this.menuButton.getX() - this.titleSpacing;
        if (m == 10) {
          m = 11;
        } else if (m == 11) {
          m = 10;
        } 
      } 
      String str1 = getTitle(str, fontMetrics, j - k);
      if (str1 == str)
        if (m == 11) {
          k = j - synthStyle.getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, paramGraphics.getFont(), fontMetrics, str);
        } else if (m == 0) {
          int n = synthStyle.getGraphicsUtils(paramSynthContext).computeStringWidth(paramSynthContext, paramGraphics.getFont(), fontMetrics, str);
          k = Math.max(k, (getWidth() - n) / 2);
          k = Math.min(j - n, k);
        }  
      synthStyle.getGraphicsUtils(paramSynthContext).paintText(paramSynthContext, paramGraphics, str1, k, i - fontMetrics.getAscent(), -1);
    } 
  }
  
  public void paintBorder(SynthContext paramSynthContext, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramSynthContext.getPainter().paintInternalFrameTitlePaneBorder(paramSynthContext, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  protected LayoutManager createLayout() {
    SynthContext synthContext = getContext(this);
    LayoutManager layoutManager = (LayoutManager)this.style.get(synthContext, "InternalFrameTitlePane.titlePaneLayout");
    synthContext.dispose();
    return (layoutManager != null) ? layoutManager : new SynthTitlePaneLayout();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    if (paramPropertyChangeEvent.getSource() == this) {
      if (SynthLookAndFeel.shouldUpdateStyle(paramPropertyChangeEvent))
        updateStyle(this); 
    } else if (paramPropertyChangeEvent.getPropertyName() == "frameIcon") {
      updateMenuIcon();
    } 
  }
  
  private void updateMenuIcon() {
    Icon icon = this.frame.getFrameIcon();
    SynthContext synthContext = getContext(this);
    if (icon != null) {
      Dimension dimension = (Dimension)synthContext.getStyle().get(synthContext, "InternalFrameTitlePane.maxFrameIconSize");
      int i = 16;
      int j = 16;
      if (dimension != null) {
        i = dimension.width;
        j = dimension.height;
      } 
      if ((icon.getIconWidth() > i || icon.getIconHeight() > j) && icon instanceof ImageIcon)
        icon = new ImageIcon(((ImageIcon)icon).getImage().getScaledInstance(i, j, 4)); 
    } 
    synthContext.dispose();
    this.menuButton.setIcon(icon);
  }
  
  private JButton createNoFocusButton() {
    JButton jButton = new JButton();
    jButton.setFocusable(false);
    jButton.setMargin(new Insets(0, 0, 0, 0));
    return jButton;
  }
  
  private static class JPopupMenuUIResource extends JPopupMenu implements UIResource {
    private JPopupMenuUIResource() {}
  }
  
  class SynthTitlePaneLayout implements LayoutManager {
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) { return minimumLayoutSize(param1Container); }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      SynthContext synthContext = SynthInternalFrameTitlePane.this.getContext(SynthInternalFrameTitlePane.this);
      int i = 0;
      int j = 0;
      byte b = 0;
      if (SynthInternalFrameTitlePane.this.frame.isClosable()) {
        Dimension dimension1 = SynthInternalFrameTitlePane.this.closeButton.getPreferredSize();
        i += dimension1.width;
        j = Math.max(dimension1.height, j);
        b++;
      } 
      if (SynthInternalFrameTitlePane.this.frame.isMaximizable()) {
        Dimension dimension1 = SynthInternalFrameTitlePane.this.maxButton.getPreferredSize();
        i += dimension1.width;
        j = Math.max(dimension1.height, j);
        b++;
      } 
      if (SynthInternalFrameTitlePane.this.frame.isIconifiable()) {
        Dimension dimension1 = SynthInternalFrameTitlePane.this.iconButton.getPreferredSize();
        i += dimension1.width;
        j = Math.max(dimension1.height, j);
        b++;
      } 
      Dimension dimension = SynthInternalFrameTitlePane.this.menuButton.getPreferredSize();
      i += dimension.width;
      j = Math.max(dimension.height, j);
      i += Math.max(0, (b - 1) * SynthInternalFrameTitlePane.this.buttonSpacing);
      FontMetrics fontMetrics = SynthInternalFrameTitlePane.this.getFontMetrics(SynthInternalFrameTitlePane.this.getFont());
      SynthGraphicsUtils synthGraphicsUtils = synthContext.getStyle().getGraphicsUtils(synthContext);
      String str = SynthInternalFrameTitlePane.this.frame.getTitle();
      int k = (str != null) ? synthGraphicsUtils.computeStringWidth(synthContext, fontMetrics.getFont(), fontMetrics, str) : 0;
      int m = (str != null) ? str.length() : 0;
      if (m > 3) {
        int n = synthGraphicsUtils.computeStringWidth(synthContext, fontMetrics.getFont(), fontMetrics, str.substring(0, 3) + "...");
        i += ((k < n) ? k : n);
      } else {
        i += k;
      } 
      j = Math.max(fontMetrics.getHeight() + 2, j);
      i += SynthInternalFrameTitlePane.this.titleSpacing + SynthInternalFrameTitlePane.this.titleSpacing;
      Insets insets = SynthInternalFrameTitlePane.this.getInsets();
      j += insets.top + insets.bottom;
      i += insets.left + insets.right;
      synthContext.dispose();
      return new Dimension(i, j);
    }
    
    private int center(Component param1Component, Insets param1Insets, int param1Int, boolean param1Boolean) {
      Dimension dimension = param1Component.getPreferredSize();
      if (param1Boolean)
        param1Int -= dimension.width; 
      param1Component.setBounds(param1Int, param1Insets.top + (SynthInternalFrameTitlePane.this.getHeight() - param1Insets.top - param1Insets.bottom - dimension.height) / 2, dimension.width, dimension.height);
      return (dimension.width > 0) ? (param1Boolean ? (param1Int - SynthInternalFrameTitlePane.this.buttonSpacing) : (param1Int + dimension.width + SynthInternalFrameTitlePane.this.buttonSpacing)) : param1Int;
    }
    
    public void layoutContainer(Container param1Container) {
      Insets insets = param1Container.getInsets();
      if (SynthLookAndFeel.isLeftToRight(SynthInternalFrameTitlePane.this.frame)) {
        center(SynthInternalFrameTitlePane.this.menuButton, insets, insets.left, false);
        int i = SynthInternalFrameTitlePane.this.getWidth() - insets.right;
        if (SynthInternalFrameTitlePane.this.frame.isClosable())
          i = center(SynthInternalFrameTitlePane.this.closeButton, insets, i, true); 
        if (SynthInternalFrameTitlePane.this.frame.isMaximizable())
          i = center(SynthInternalFrameTitlePane.this.maxButton, insets, i, true); 
        if (SynthInternalFrameTitlePane.this.frame.isIconifiable())
          i = center(SynthInternalFrameTitlePane.this.iconButton, insets, i, true); 
      } else {
        center(SynthInternalFrameTitlePane.this.menuButton, insets, SynthInternalFrameTitlePane.this.getWidth() - insets.right, true);
        int i = insets.left;
        if (SynthInternalFrameTitlePane.this.frame.isClosable())
          i = center(SynthInternalFrameTitlePane.this.closeButton, insets, i, false); 
        if (SynthInternalFrameTitlePane.this.frame.isMaximizable())
          i = center(SynthInternalFrameTitlePane.this.maxButton, insets, i, false); 
        if (SynthInternalFrameTitlePane.this.frame.isIconifiable())
          i = center(SynthInternalFrameTitlePane.this.iconButton, insets, i, false); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */