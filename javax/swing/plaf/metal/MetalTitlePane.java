package javax.swing.plaf.metal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import sun.awt.SunToolkit;
import sun.swing.SwingUtilities2;

class MetalTitlePane extends JComponent {
  private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
  
  private static final int IMAGE_HEIGHT = 16;
  
  private static final int IMAGE_WIDTH = 16;
  
  private PropertyChangeListener propertyChangeListener;
  
  private JMenuBar menuBar;
  
  private Action closeAction;
  
  private Action iconifyAction;
  
  private Action restoreAction;
  
  private Action maximizeAction;
  
  private JButton toggleButton;
  
  private JButton iconifyButton;
  
  private JButton closeButton;
  
  private Icon maximizeIcon;
  
  private Icon minimizeIcon;
  
  private Image systemIcon;
  
  private WindowListener windowListener;
  
  private Window window;
  
  private JRootPane rootPane;
  
  private int buttonsWidth;
  
  private int state;
  
  private MetalRootPaneUI rootPaneUI;
  
  private Color inactiveBackground = UIManager.getColor("inactiveCaption");
  
  private Color inactiveForeground = UIManager.getColor("inactiveCaptionText");
  
  private Color inactiveShadow = UIManager.getColor("inactiveCaptionBorder");
  
  private Color activeBumpsHighlight = MetalLookAndFeel.getPrimaryControlHighlight();
  
  private Color activeBumpsShadow = MetalLookAndFeel.getPrimaryControlDarkShadow();
  
  private Color activeBackground = null;
  
  private Color activeForeground = null;
  
  private Color activeShadow = null;
  
  private MetalBumps activeBumps = new MetalBumps(0, 0, this.activeBumpsHighlight, this.activeBumpsShadow, MetalLookAndFeel.getPrimaryControl());
  
  private MetalBumps inactiveBumps = new MetalBumps(0, 0, MetalLookAndFeel.getControlHighlight(), MetalLookAndFeel.getControlDarkShadow(), MetalLookAndFeel.getControl());
  
  public MetalTitlePane(JRootPane paramJRootPane, MetalRootPaneUI paramMetalRootPaneUI) {
    this.rootPane = paramJRootPane;
    this.rootPaneUI = paramMetalRootPaneUI;
    this.state = -1;
    installSubcomponents();
    determineColors();
    installDefaults();
    setLayout(createLayout());
  }
  
  private void uninstall() {
    uninstallListeners();
    this.window = null;
    removeAll();
  }
  
  private void installListeners() {
    if (this.window != null) {
      this.windowListener = createWindowListener();
      this.window.addWindowListener(this.windowListener);
      this.propertyChangeListener = createWindowPropertyChangeListener();
      this.window.addPropertyChangeListener(this.propertyChangeListener);
    } 
  }
  
  private void uninstallListeners() {
    if (this.window != null) {
      this.window.removeWindowListener(this.windowListener);
      this.window.removePropertyChangeListener(this.propertyChangeListener);
    } 
  }
  
  private WindowListener createWindowListener() { return new WindowHandler(null); }
  
  private PropertyChangeListener createWindowPropertyChangeListener() { return new PropertyChangeHandler(null); }
  
  public JRootPane getRootPane() { return this.rootPane; }
  
  private int getWindowDecorationStyle() { return getRootPane().getWindowDecorationStyle(); }
  
  public void addNotify() {
    super.addNotify();
    uninstallListeners();
    this.window = SwingUtilities.getWindowAncestor(this);
    if (this.window != null) {
      if (this.window instanceof Frame) {
        setState(((Frame)this.window).getExtendedState());
      } else {
        setState(0);
      } 
      setActive(this.window.isActive());
      installListeners();
      updateSystemIcon();
    } 
  }
  
  public void removeNotify() {
    super.removeNotify();
    uninstallListeners();
    this.window = null;
  }
  
  private void installSubcomponents() {
    int i = getWindowDecorationStyle();
    if (i == 1) {
      createActions();
      this.menuBar = createMenuBar();
      add(this.menuBar);
      createButtons();
      add(this.iconifyButton);
      add(this.toggleButton);
      add(this.closeButton);
    } else if (i == 2 || i == 3 || i == 4 || i == 5 || i == 6 || i == 7 || i == 8) {
      createActions();
      createButtons();
      add(this.closeButton);
    } 
  }
  
  private void determineColors() {
    switch (getWindowDecorationStyle()) {
      case 1:
        this.activeBackground = UIManager.getColor("activeCaption");
        this.activeForeground = UIManager.getColor("activeCaptionText");
        this.activeShadow = UIManager.getColor("activeCaptionBorder");
        break;
      case 4:
        this.activeBackground = UIManager.getColor("OptionPane.errorDialog.titlePane.background");
        this.activeForeground = UIManager.getColor("OptionPane.errorDialog.titlePane.foreground");
        this.activeShadow = UIManager.getColor("OptionPane.errorDialog.titlePane.shadow");
        break;
      case 5:
      case 6:
      case 7:
        this.activeBackground = UIManager.getColor("OptionPane.questionDialog.titlePane.background");
        this.activeForeground = UIManager.getColor("OptionPane.questionDialog.titlePane.foreground");
        this.activeShadow = UIManager.getColor("OptionPane.questionDialog.titlePane.shadow");
        break;
      case 8:
        this.activeBackground = UIManager.getColor("OptionPane.warningDialog.titlePane.background");
        this.activeForeground = UIManager.getColor("OptionPane.warningDialog.titlePane.foreground");
        this.activeShadow = UIManager.getColor("OptionPane.warningDialog.titlePane.shadow");
        break;
      default:
        this.activeBackground = UIManager.getColor("activeCaption");
        this.activeForeground = UIManager.getColor("activeCaptionText");
        this.activeShadow = UIManager.getColor("activeCaptionBorder");
        break;
    } 
    this.activeBumps.setBumpColors(this.activeBumpsHighlight, this.activeBumpsShadow, this.activeBackground);
  }
  
  private void installDefaults() { setFont(UIManager.getFont("InternalFrame.titleFont", getLocale())); }
  
  private void uninstallDefaults() {}
  
  protected JMenuBar createMenuBar() {
    this.menuBar = new SystemMenuBar(null);
    this.menuBar.setFocusable(false);
    this.menuBar.setBorderPainted(true);
    this.menuBar.add(createMenu());
    return this.menuBar;
  }
  
  private void close() {
    Window window1 = getWindow();
    if (window1 != null)
      window1.dispatchEvent(new WindowEvent(window1, 201)); 
  }
  
  private void iconify() {
    Frame frame = getFrame();
    if (frame != null)
      frame.setExtendedState(this.state | true); 
  }
  
  private void maximize() {
    Frame frame = getFrame();
    if (frame != null)
      frame.setExtendedState(this.state | 0x6); 
  }
  
  private void restore() {
    Frame frame = getFrame();
    if (frame == null)
      return; 
    if ((this.state & true) != 0) {
      frame.setExtendedState(this.state & 0xFFFFFFFE);
    } else {
      frame.setExtendedState(this.state & 0xFFFFFFF9);
    } 
  }
  
  private void createActions() {
    this.closeAction = new CloseAction();
    if (getWindowDecorationStyle() == 1) {
      this.iconifyAction = new IconifyAction();
      this.restoreAction = new RestoreAction();
      this.maximizeAction = new MaximizeAction();
    } 
  }
  
  private JMenu createMenu() {
    JMenu jMenu = new JMenu("");
    if (getWindowDecorationStyle() == 1)
      addMenuItems(jMenu); 
    return jMenu;
  }
  
  private void addMenuItems(JMenu paramJMenu) {
    Locale locale = getRootPane().getLocale();
    JMenuItem jMenuItem = paramJMenu.add(this.restoreAction);
    int i = MetalUtils.getInt("MetalTitlePane.restoreMnemonic", -1);
    if (i != -1)
      jMenuItem.setMnemonic(i); 
    jMenuItem = paramJMenu.add(this.iconifyAction);
    i = MetalUtils.getInt("MetalTitlePane.iconifyMnemonic", -1);
    if (i != -1)
      jMenuItem.setMnemonic(i); 
    if (Toolkit.getDefaultToolkit().isFrameStateSupported(6)) {
      jMenuItem = paramJMenu.add(this.maximizeAction);
      i = MetalUtils.getInt("MetalTitlePane.maximizeMnemonic", -1);
      if (i != -1)
        jMenuItem.setMnemonic(i); 
    } 
    paramJMenu.add(new JSeparator());
    jMenuItem = paramJMenu.add(this.closeAction);
    i = MetalUtils.getInt("MetalTitlePane.closeMnemonic", -1);
    if (i != -1)
      jMenuItem.setMnemonic(i); 
  }
  
  private JButton createTitleButton() {
    JButton jButton = new JButton();
    jButton.setFocusPainted(false);
    jButton.setFocusable(false);
    jButton.setOpaque(true);
    return jButton;
  }
  
  private void createButtons() {
    this.closeButton = createTitleButton();
    this.closeButton.setAction(this.closeAction);
    this.closeButton.setText(null);
    this.closeButton.putClientProperty("paintActive", Boolean.TRUE);
    this.closeButton.setBorder(handyEmptyBorder);
    this.closeButton.putClientProperty("AccessibleName", "Close");
    this.closeButton.setIcon(UIManager.getIcon("InternalFrame.closeIcon"));
    if (getWindowDecorationStyle() == 1) {
      this.maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
      this.minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
      this.iconifyButton = createTitleButton();
      this.iconifyButton.setAction(this.iconifyAction);
      this.iconifyButton.setText(null);
      this.iconifyButton.putClientProperty("paintActive", Boolean.TRUE);
      this.iconifyButton.setBorder(handyEmptyBorder);
      this.iconifyButton.putClientProperty("AccessibleName", "Iconify");
      this.iconifyButton.setIcon(UIManager.getIcon("InternalFrame.iconifyIcon"));
      this.toggleButton = createTitleButton();
      this.toggleButton.setAction(this.restoreAction);
      this.toggleButton.putClientProperty("paintActive", Boolean.TRUE);
      this.toggleButton.setBorder(handyEmptyBorder);
      this.toggleButton.putClientProperty("AccessibleName", "Maximize");
      this.toggleButton.setIcon(this.maximizeIcon);
    } 
  }
  
  private LayoutManager createLayout() { return new TitlePaneLayout(null); }
  
  private void setActive(boolean paramBoolean) {
    Boolean bool = paramBoolean ? Boolean.TRUE : Boolean.FALSE;
    this.closeButton.putClientProperty("paintActive", bool);
    if (getWindowDecorationStyle() == 1) {
      this.iconifyButton.putClientProperty("paintActive", bool);
      this.toggleButton.putClientProperty("paintActive", bool);
    } 
    getRootPane().repaint();
  }
  
  private void setState(int paramInt) { setState(paramInt, false); }
  
  private void setState(int paramInt, boolean paramBoolean) {
    Window window1 = getWindow();
    if (window1 != null && getWindowDecorationStyle() == 1) {
      if (this.state == paramInt && !paramBoolean)
        return; 
      Frame frame = getFrame();
      if (frame != null) {
        JRootPane jRootPane = getRootPane();
        if ((paramInt & 0x6) != 0 && (jRootPane.getBorder() == null || jRootPane.getBorder() instanceof javax.swing.plaf.UIResource) && frame.isShowing()) {
          jRootPane.setBorder(null);
        } else if ((paramInt & 0x6) == 0) {
          this.rootPaneUI.installBorder(jRootPane);
        } 
        if (frame.isResizable()) {
          if ((paramInt & 0x6) != 0) {
            updateToggleButton(this.restoreAction, this.minimizeIcon);
            this.maximizeAction.setEnabled(false);
            this.restoreAction.setEnabled(true);
          } else {
            updateToggleButton(this.maximizeAction, this.maximizeIcon);
            this.maximizeAction.setEnabled(true);
            this.restoreAction.setEnabled(false);
          } 
          if (this.toggleButton.getParent() == null || this.iconifyButton.getParent() == null) {
            add(this.toggleButton);
            add(this.iconifyButton);
            revalidate();
            repaint();
          } 
          this.toggleButton.setText(null);
        } else {
          this.maximizeAction.setEnabled(false);
          this.restoreAction.setEnabled(false);
          if (this.toggleButton.getParent() != null) {
            remove(this.toggleButton);
            revalidate();
            repaint();
          } 
        } 
      } else {
        this.maximizeAction.setEnabled(false);
        this.restoreAction.setEnabled(false);
        this.iconifyAction.setEnabled(false);
        remove(this.toggleButton);
        remove(this.iconifyButton);
        revalidate();
        repaint();
      } 
      this.closeAction.setEnabled(true);
      this.state = paramInt;
    } 
  }
  
  private void updateToggleButton(Action paramAction, Icon paramIcon) {
    this.toggleButton.setAction(paramAction);
    this.toggleButton.setIcon(paramIcon);
    this.toggleButton.setText(null);
  }
  
  private Frame getFrame() {
    Window window1 = getWindow();
    return (window1 instanceof Frame) ? (Frame)window1 : null;
  }
  
  private Window getWindow() { return this.window; }
  
  private String getTitle() {
    Window window1 = getWindow();
    return (window1 instanceof Frame) ? ((Frame)window1).getTitle() : ((window1 instanceof Dialog) ? ((Dialog)window1).getTitle() : null);
  }
  
  public void paintComponent(Graphics paramGraphics) {
    int n;
    int m;
    MetalBumps metalBumps;
    Color color3;
    Color color2;
    Color color1;
    if (getFrame() != null)
      setState(getFrame().getExtendedState()); 
    JRootPane jRootPane = getRootPane();
    Window window1 = getWindow();
    boolean bool = (window1 == null) ? jRootPane.getComponentOrientation().isLeftToRight() : window1.getComponentOrientation().isLeftToRight();
    boolean bool1 = (window1 == null) ? 1 : window1.isActive();
    int i = getWidth();
    int j = getHeight();
    if (bool1) {
      color1 = this.activeBackground;
      color2 = this.activeForeground;
      color3 = this.activeShadow;
      metalBumps = this.activeBumps;
    } else {
      color1 = this.inactiveBackground;
      color2 = this.inactiveForeground;
      color3 = this.inactiveShadow;
      metalBumps = this.inactiveBumps;
    } 
    paramGraphics.setColor(color1);
    paramGraphics.fillRect(0, 0, i, j);
    paramGraphics.setColor(color3);
    paramGraphics.drawLine(0, j - 1, i, j - 1);
    paramGraphics.drawLine(0, 0, 0, 0);
    paramGraphics.drawLine(i - 1, 0, i - 1, 0);
    int k = bool ? 5 : (i - 5);
    if (getWindowDecorationStyle() == 1)
      k += (bool ? 21 : -21); 
    String str = getTitle();
    if (str != null) {
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(jRootPane, paramGraphics);
      paramGraphics.setColor(color2);
      n = (j - fontMetrics.getHeight()) / 2 + fontMetrics.getAscent();
      Rectangle rectangle = new Rectangle(0, 0, 0, 0);
      if (this.iconifyButton != null && this.iconifyButton.getParent() != null)
        rectangle = this.iconifyButton.getBounds(); 
      if (bool) {
        if (rectangle.x == 0)
          rectangle.x = window1.getWidth() - (window1.getInsets()).right - 2; 
        int i3 = rectangle.x - k - 4;
        str = SwingUtilities2.clipStringIfNecessary(jRootPane, fontMetrics, str, i3);
      } else {
        int i3 = k - rectangle.x - rectangle.width - 4;
        str = SwingUtilities2.clipStringIfNecessary(jRootPane, fontMetrics, str, i3);
        k -= SwingUtilities2.stringWidth(jRootPane, fontMetrics, str);
      } 
      int i2 = SwingUtilities2.stringWidth(jRootPane, fontMetrics, str);
      SwingUtilities2.drawString(jRootPane, paramGraphics, str, k, n);
      k += (bool ? (i2 + 5) : -5);
    } 
    if (bool) {
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
  
  private void updateSystemIcon() {
    Window window1 = getWindow();
    if (window1 == null) {
      this.systemIcon = null;
      return;
    } 
    List list = window1.getIconImages();
    assert list != null;
    if (list.size() == 0) {
      this.systemIcon = null;
    } else if (list.size() == 1) {
      this.systemIcon = (Image)list.get(0);
    } else {
      this.systemIcon = SunToolkit.getScaledIconImage(list, 16, 16);
    } 
  }
  
  private class CloseAction extends AbstractAction {
    public CloseAction() { super(UIManager.getString("MetalTitlePane.closeTitle", this$0.getLocale())); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { MetalTitlePane.this.close(); }
  }
  
  private class IconifyAction extends AbstractAction {
    public IconifyAction() { super(UIManager.getString("MetalTitlePane.iconifyTitle", this$0.getLocale())); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { MetalTitlePane.this.iconify(); }
  }
  
  private class MaximizeAction extends AbstractAction {
    public MaximizeAction() { super(UIManager.getString("MetalTitlePane.maximizeTitle", this$0.getLocale())); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { MetalTitlePane.this.maximize(); }
  }
  
  private class PropertyChangeHandler implements PropertyChangeListener {
    private PropertyChangeHandler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if ("resizable".equals(str) || "state".equals(str)) {
        Frame frame = MetalTitlePane.this.getFrame();
        if (frame != null)
          MetalTitlePane.this.setState(frame.getExtendedState(), true); 
        if ("resizable".equals(str))
          MetalTitlePane.this.getRootPane().repaint(); 
      } else if ("title".equals(str)) {
        MetalTitlePane.this.repaint();
      } else if ("componentOrientation" == str) {
        MetalTitlePane.this.revalidate();
        MetalTitlePane.this.repaint();
      } else if ("iconImage" == str) {
        MetalTitlePane.this.updateSystemIcon();
        MetalTitlePane.this.revalidate();
        MetalTitlePane.this.repaint();
      } 
    }
  }
  
  private class RestoreAction extends AbstractAction {
    public RestoreAction() { super(UIManager.getString("MetalTitlePane.restoreTitle", this$0.getLocale())); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) { MetalTitlePane.this.restore(); }
  }
  
  private class SystemMenuBar extends JMenuBar {
    private SystemMenuBar() {}
    
    public void paint(Graphics param1Graphics) {
      if (isOpaque()) {
        param1Graphics.setColor(getBackground());
        param1Graphics.fillRect(0, 0, getWidth(), getHeight());
      } 
      if (MetalTitlePane.this.systemIcon != null) {
        param1Graphics.drawImage(MetalTitlePane.this.systemIcon, 0, 0, 16, 16, null);
      } else {
        Icon icon = UIManager.getIcon("InternalFrame.icon");
        if (icon != null)
          icon.paintIcon(this, param1Graphics, 0, 0); 
      } 
    }
    
    public Dimension getMinimumSize() { return getPreferredSize(); }
    
    public Dimension getPreferredSize() {
      Dimension dimension = super.getPreferredSize();
      return new Dimension(Math.max(16, dimension.width), Math.max(dimension.height, 16));
    }
  }
  
  private class TitlePaneLayout implements LayoutManager {
    private TitlePaneLayout() {}
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) {
      int i = computeHeight();
      return new Dimension(i, i);
    }
    
    public Dimension minimumLayoutSize(Container param1Container) { return preferredLayoutSize(param1Container); }
    
    private int computeHeight() {
      FontMetrics fontMetrics = MetalTitlePane.this.rootPane.getFontMetrics(MetalTitlePane.this.getFont());
      int i = fontMetrics.getHeight();
      i += 7;
      byte b = 0;
      if (MetalTitlePane.this.getWindowDecorationStyle() == 1)
        b = 16; 
      return Math.max(i, b);
    }
    
    public void layoutContainer(Container param1Container) {
      int m;
      byte b2;
      boolean bool = (MetalTitlePane.this.window == null) ? MetalTitlePane.this.getRootPane().getComponentOrientation().isLeftToRight() : MetalTitlePane.this.window.getComponentOrientation().isLeftToRight();
      int i = MetalTitlePane.this.getWidth();
      byte b1 = 3;
      if (MetalTitlePane.this.closeButton != null && MetalTitlePane.this.closeButton.getIcon() != null) {
        b2 = MetalTitlePane.this.closeButton.getIcon().getIconHeight();
        m = MetalTitlePane.this.closeButton.getIcon().getIconWidth();
      } else {
        b2 = 16;
        m = 16;
      } 
      int j = bool ? i : 0;
      int k = 5;
      j = bool ? k : (i - m - k);
      if (MetalTitlePane.this.menuBar != null)
        MetalTitlePane.this.menuBar.setBounds(j, b1, m, b2); 
      j = bool ? i : 0;
      k = 4;
      j += (bool ? (-k - m) : k);
      if (MetalTitlePane.this.closeButton != null)
        MetalTitlePane.this.closeButton.setBounds(j, b1, m, b2); 
      if (!bool)
        j += m; 
      if (MetalTitlePane.this.getWindowDecorationStyle() == 1) {
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(6) && MetalTitlePane.this.toggleButton.getParent() != null) {
          k = 10;
          j += (bool ? (-k - m) : k);
          MetalTitlePane.this.toggleButton.setBounds(j, b1, m, b2);
          if (!bool)
            j += m; 
        } 
        if (MetalTitlePane.this.iconifyButton != null && MetalTitlePane.this.iconifyButton.getParent() != null) {
          k = 2;
          j += (bool ? (-k - m) : k);
          MetalTitlePane.this.iconifyButton.setBounds(j, b1, m, b2);
          if (!bool)
            j += m; 
        } 
      } 
      MetalTitlePane.this.buttonsWidth = bool ? (i - j) : j;
    }
  }
  
  private class WindowHandler extends WindowAdapter {
    private WindowHandler() {}
    
    public void windowActivated(WindowEvent param1WindowEvent) { MetalTitlePane.this.setActive(true); }
    
    public void windowDeactivated(WindowEvent param1WindowEvent) { MetalTitlePane.this.setActive(false); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */