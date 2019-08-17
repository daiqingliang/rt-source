package javax.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.ActionMapUIResource;
import sun.swing.DefaultLookup;
import sun.swing.SwingUtilities2;

public class BasicInternalFrameTitlePane extends JComponent {
  protected JMenuBar menuBar;
  
  protected JButton iconButton;
  
  protected JButton maxButton;
  
  protected JButton closeButton;
  
  protected JMenu windowMenu;
  
  protected JInternalFrame frame;
  
  protected Color selectedTitleColor;
  
  protected Color selectedTextColor;
  
  protected Color notSelectedTitleColor;
  
  protected Color notSelectedTextColor;
  
  protected Icon maxIcon;
  
  protected Icon minIcon;
  
  protected Icon iconIcon;
  
  protected Icon closeIcon;
  
  protected PropertyChangeListener propertyChangeListener;
  
  protected Action closeAction;
  
  protected Action maximizeAction;
  
  protected Action iconifyAction;
  
  protected Action restoreAction;
  
  protected Action moveAction;
  
  protected Action sizeAction;
  
  protected static final String CLOSE_CMD = UIManager.getString("InternalFrameTitlePane.closeButtonText");
  
  protected static final String ICONIFY_CMD = UIManager.getString("InternalFrameTitlePane.minimizeButtonText");
  
  protected static final String RESTORE_CMD = UIManager.getString("InternalFrameTitlePane.restoreButtonText");
  
  protected static final String MAXIMIZE_CMD = UIManager.getString("InternalFrameTitlePane.maximizeButtonText");
  
  protected static final String MOVE_CMD = UIManager.getString("InternalFrameTitlePane.moveButtonText");
  
  protected static final String SIZE_CMD = UIManager.getString("InternalFrameTitlePane.sizeButtonText");
  
  private String closeButtonToolTip;
  
  private String iconButtonToolTip;
  
  private String restoreButtonToolTip;
  
  private String maxButtonToolTip;
  
  private Handler handler;
  
  public BasicInternalFrameTitlePane(JInternalFrame paramJInternalFrame) {
    this.frame = paramJInternalFrame;
    installTitlePane();
  }
  
  protected void installTitlePane() {
    installDefaults();
    installListeners();
    createActions();
    enableActions();
    createActionMap();
    setLayout(createLayout());
    assembleSystemMenu();
    createButtons();
    addSubComponents();
    updateProperties();
  }
  
  private void updateProperties() {
    Object object = this.frame.getClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
    putClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY, object);
  }
  
  protected void addSubComponents() {
    add(this.menuBar);
    add(this.iconButton);
    add(this.maxButton);
    add(this.closeButton);
  }
  
  protected void createActions() {
    this.maximizeAction = new MaximizeAction();
    this.iconifyAction = new IconifyAction();
    this.closeAction = new CloseAction();
    this.restoreAction = new RestoreAction();
    this.moveAction = new MoveAction();
    this.sizeAction = new SizeAction();
  }
  
  ActionMap createActionMap() {
    ActionMapUIResource actionMapUIResource = new ActionMapUIResource();
    actionMapUIResource.put("showSystemMenu", new ShowSystemMenuAction(true));
    actionMapUIResource.put("hideSystemMenu", new ShowSystemMenuAction(false));
    return actionMapUIResource;
  }
  
  protected void installListeners() {
    if (this.propertyChangeListener == null)
      this.propertyChangeListener = createPropertyChangeListener(); 
    this.frame.addPropertyChangeListener(this.propertyChangeListener);
  }
  
  protected void uninstallListeners() {
    this.frame.removePropertyChangeListener(this.propertyChangeListener);
    this.handler = null;
  }
  
  protected void installDefaults() {
    this.maxIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
    this.minIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
    this.iconIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
    this.closeIcon = UIManager.getIcon("InternalFrame.closeIcon");
    this.selectedTitleColor = UIManager.getColor("InternalFrame.activeTitleBackground");
    this.selectedTextColor = UIManager.getColor("InternalFrame.activeTitleForeground");
    this.notSelectedTitleColor = UIManager.getColor("InternalFrame.inactiveTitleBackground");
    this.notSelectedTextColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
    setFont(UIManager.getFont("InternalFrame.titleFont"));
    this.closeButtonToolTip = UIManager.getString("InternalFrame.closeButtonToolTip");
    this.iconButtonToolTip = UIManager.getString("InternalFrame.iconButtonToolTip");
    this.restoreButtonToolTip = UIManager.getString("InternalFrame.restoreButtonToolTip");
    this.maxButtonToolTip = UIManager.getString("InternalFrame.maxButtonToolTip");
  }
  
  protected void uninstallDefaults() {}
  
  protected void createButtons() {
    this.iconButton = new NoFocusButton("InternalFrameTitlePane.iconifyButtonAccessibleName", "InternalFrameTitlePane.iconifyButtonOpacity");
    this.iconButton.addActionListener(this.iconifyAction);
    if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0)
      this.iconButton.setToolTipText(this.iconButtonToolTip); 
    this.maxButton = new NoFocusButton("InternalFrameTitlePane.maximizeButtonAccessibleName", "InternalFrameTitlePane.maximizeButtonOpacity");
    this.maxButton.addActionListener(this.maximizeAction);
    this.closeButton = new NoFocusButton("InternalFrameTitlePane.closeButtonAccessibleName", "InternalFrameTitlePane.closeButtonOpacity");
    this.closeButton.addActionListener(this.closeAction);
    if (this.closeButtonToolTip != null && this.closeButtonToolTip.length() != 0)
      this.closeButton.setToolTipText(this.closeButtonToolTip); 
    setButtonIcons();
  }
  
  protected void setButtonIcons() {
    if (this.frame.isIcon()) {
      if (this.minIcon != null)
        this.iconButton.setIcon(this.minIcon); 
      if (this.restoreButtonToolTip != null && this.restoreButtonToolTip.length() != 0)
        this.iconButton.setToolTipText(this.restoreButtonToolTip); 
      if (this.maxIcon != null)
        this.maxButton.setIcon(this.maxIcon); 
      if (this.maxButtonToolTip != null && this.maxButtonToolTip.length() != 0)
        this.maxButton.setToolTipText(this.maxButtonToolTip); 
    } else if (this.frame.isMaximum()) {
      if (this.iconIcon != null)
        this.iconButton.setIcon(this.iconIcon); 
      if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0)
        this.iconButton.setToolTipText(this.iconButtonToolTip); 
      if (this.minIcon != null)
        this.maxButton.setIcon(this.minIcon); 
      if (this.restoreButtonToolTip != null && this.restoreButtonToolTip.length() != 0)
        this.maxButton.setToolTipText(this.restoreButtonToolTip); 
    } else {
      if (this.iconIcon != null)
        this.iconButton.setIcon(this.iconIcon); 
      if (this.iconButtonToolTip != null && this.iconButtonToolTip.length() != 0)
        this.iconButton.setToolTipText(this.iconButtonToolTip); 
      if (this.maxIcon != null)
        this.maxButton.setIcon(this.maxIcon); 
      if (this.maxButtonToolTip != null && this.maxButtonToolTip.length() != 0)
        this.maxButton.setToolTipText(this.maxButtonToolTip); 
    } 
    if (this.closeIcon != null)
      this.closeButton.setIcon(this.closeIcon); 
  }
  
  protected void assembleSystemMenu() {
    this.menuBar = createSystemMenuBar();
    this.windowMenu = createSystemMenu();
    this.menuBar.add(this.windowMenu);
    addSystemMenuItems(this.windowMenu);
    enableActions();
  }
  
  protected void addSystemMenuItems(JMenu paramJMenu) {
    JMenuItem jMenuItem = paramJMenu.add(this.restoreAction);
    jMenuItem.setMnemonic(getButtonMnemonic("restore"));
    jMenuItem = paramJMenu.add(this.moveAction);
    jMenuItem.setMnemonic(getButtonMnemonic("move"));
    jMenuItem = paramJMenu.add(this.sizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("size"));
    jMenuItem = paramJMenu.add(this.iconifyAction);
    jMenuItem.setMnemonic(getButtonMnemonic("minimize"));
    jMenuItem = paramJMenu.add(this.maximizeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("maximize"));
    paramJMenu.add(new JSeparator());
    jMenuItem = paramJMenu.add(this.closeAction);
    jMenuItem.setMnemonic(getButtonMnemonic("close"));
  }
  
  private static int getButtonMnemonic(String paramString) {
    try {
      return Integer.parseInt(UIManager.getString("InternalFrameTitlePane." + paramString + "Button.mnemonic"));
    } catch (NumberFormatException numberFormatException) {
      return -1;
    } 
  }
  
  protected JMenu createSystemMenu() { return new JMenu("    "); }
  
  protected JMenuBar createSystemMenuBar() {
    this.menuBar = new SystemMenuBar();
    this.menuBar.setBorderPainted(false);
    return this.menuBar;
  }
  
  protected void showSystemMenu() { this.windowMenu.doClick(); }
  
  public void paintComponent(Graphics paramGraphics) {
    paintTitleBackground(paramGraphics);
    if (this.frame.getTitle() != null) {
      int j;
      boolean bool = this.frame.isSelected();
      Font font = paramGraphics.getFont();
      paramGraphics.setFont(getFont());
      if (bool) {
        paramGraphics.setColor(this.selectedTextColor);
      } else {
        paramGraphics.setColor(this.notSelectedTextColor);
      } 
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.frame, paramGraphics);
      int i = (getHeight() + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
      Rectangle rectangle = new Rectangle(0, 0, 0, 0);
      if (this.frame.isIconifiable()) {
        rectangle = this.iconButton.getBounds();
      } else if (this.frame.isMaximizable()) {
        rectangle = this.maxButton.getBounds();
      } else if (this.frame.isClosable()) {
        rectangle = this.closeButton.getBounds();
      } 
      String str = this.frame.getTitle();
      if (BasicGraphicsUtils.isLeftToRight(this.frame)) {
        if (rectangle.x == 0)
          rectangle.x = this.frame.getWidth() - (this.frame.getInsets()).right; 
        j = this.menuBar.getX() + this.menuBar.getWidth() + 2;
        int k = rectangle.x - j - 3;
        str = getTitle(this.frame.getTitle(), fontMetrics, k);
      } else {
        j = this.menuBar.getX() - 2 - SwingUtilities2.stringWidth(this.frame, fontMetrics, str);
      } 
      SwingUtilities2.drawString(this.frame, paramGraphics, str, j, i);
      paramGraphics.setFont(font);
    } 
  }
  
  protected void paintTitleBackground(Graphics paramGraphics) {
    boolean bool = this.frame.isSelected();
    if (bool) {
      paramGraphics.setColor(this.selectedTitleColor);
    } else {
      paramGraphics.setColor(this.notSelectedTitleColor);
    } 
    paramGraphics.fillRect(0, 0, getWidth(), getHeight());
  }
  
  protected String getTitle(String paramString, FontMetrics paramFontMetrics, int paramInt) { return SwingUtilities2.clipStringIfNecessary(this.frame, paramFontMetrics, paramString, paramInt); }
  
  protected void postClosingEvent(JInternalFrame paramJInternalFrame) {
    InternalFrameEvent internalFrameEvent = new InternalFrameEvent(paramJInternalFrame, 25550);
    try {
      Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(internalFrameEvent);
    } catch (SecurityException securityException) {
      paramJInternalFrame.dispatchEvent(internalFrameEvent);
    } 
  }
  
  protected void enableActions() {
    this.restoreAction.setEnabled((this.frame.isMaximum() || this.frame.isIcon()));
    this.maximizeAction.setEnabled(((this.frame.isMaximizable() && !this.frame.isMaximum() && !this.frame.isIcon()) || (this.frame.isMaximizable() && this.frame.isIcon())));
    this.iconifyAction.setEnabled((this.frame.isIconifiable() && !this.frame.isIcon()));
    this.closeAction.setEnabled(this.frame.isClosable());
    this.sizeAction.setEnabled(false);
    this.moveAction.setEnabled(false);
  }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected PropertyChangeListener createPropertyChangeListener() { return getHandler(); }
  
  protected LayoutManager createLayout() { return getHandler(); }
  
  public class CloseAction extends AbstractAction {
    public CloseAction() { super(UIManager.getString("InternalFrameTitlePane.closeButtonText")); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicInternalFrameTitlePane.this.frame.isClosable())
        BasicInternalFrameTitlePane.this.frame.doDefaultCloseAction(); 
    }
  }
  
  private class Handler implements LayoutManager, PropertyChangeListener {
    private Handler() {}
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      String str = param1PropertyChangeEvent.getPropertyName();
      if (str == "selected") {
        BasicInternalFrameTitlePane.this.repaint();
        return;
      } 
      if (str == "icon" || str == "maximum") {
        BasicInternalFrameTitlePane.this.setButtonIcons();
        BasicInternalFrameTitlePane.this.enableActions();
        return;
      } 
      if ("closable" == str) {
        if (param1PropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.closeButton);
        } else {
          BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.closeButton);
        } 
      } else if ("maximizable" == str) {
        if (param1PropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.maxButton);
        } else {
          BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.maxButton);
        } 
      } else if ("iconable" == str) {
        if (param1PropertyChangeEvent.getNewValue() == Boolean.TRUE) {
          BasicInternalFrameTitlePane.this.add(BasicInternalFrameTitlePane.this.iconButton);
        } else {
          BasicInternalFrameTitlePane.this.remove(BasicInternalFrameTitlePane.this.iconButton);
        } 
      } 
      BasicInternalFrameTitlePane.this.enableActions();
      BasicInternalFrameTitlePane.this.revalidate();
      BasicInternalFrameTitlePane.this.repaint();
    }
    
    public void addLayoutComponent(String param1String, Component param1Component) {}
    
    public void removeLayoutComponent(Component param1Component) {}
    
    public Dimension preferredLayoutSize(Container param1Container) { return minimumLayoutSize(param1Container); }
    
    public Dimension minimumLayoutSize(Container param1Container) {
      int i = 22;
      if (BasicInternalFrameTitlePane.this.frame.isClosable())
        i += 19; 
      if (BasicInternalFrameTitlePane.this.frame.isMaximizable())
        i += 19; 
      if (BasicInternalFrameTitlePane.this.frame.isIconifiable())
        i += 19; 
      FontMetrics fontMetrics = BasicInternalFrameTitlePane.this.frame.getFontMetrics(BasicInternalFrameTitlePane.this.getFont());
      String str = BasicInternalFrameTitlePane.this.frame.getTitle();
      int j = (str != null) ? SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, fontMetrics, str) : 0;
      int k = (str != null) ? str.length() : 0;
      if (k > 3) {
        int i2 = SwingUtilities2.stringWidth(BasicInternalFrameTitlePane.this.frame, fontMetrics, str.substring(0, 3) + "...");
        i += ((j < i2) ? j : i2);
      } else {
        i += j;
      } 
      Icon icon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
      int m = fontMetrics.getHeight();
      m += 2;
      int n = 0;
      if (icon != null)
        n = Math.min(icon.getIconHeight(), 16); 
      n += 2;
      int i1 = Math.max(m, n);
      Dimension dimension = new Dimension(i, i1);
      if (BasicInternalFrameTitlePane.this.getBorder() != null) {
        Insets insets = BasicInternalFrameTitlePane.this.getBorder().getBorderInsets(param1Container);
        dimension.height += insets.top + insets.bottom;
        dimension.width += insets.left + insets.right;
      } 
      return dimension;
    }
    
    public void layoutContainer(Container param1Container) {
      boolean bool = BasicGraphicsUtils.isLeftToRight(BasicInternalFrameTitlePane.this.frame);
      int i = BasicInternalFrameTitlePane.this.getWidth();
      int j = BasicInternalFrameTitlePane.this.getHeight();
      int m = BasicInternalFrameTitlePane.this.closeButton.getIcon().getIconHeight();
      Icon icon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
      int n = 0;
      if (icon != null)
        n = icon.getIconHeight(); 
      int k = bool ? 2 : (i - 16 - 2);
      BasicInternalFrameTitlePane.this.menuBar.setBounds(k, (j - n) / 2, 16, 16);
      k = bool ? (i - 16 - 2) : 2;
      if (BasicInternalFrameTitlePane.this.frame.isClosable()) {
        BasicInternalFrameTitlePane.this.closeButton.setBounds(k, (j - m) / 2, 16, 14);
        k += (bool ? -18 : 18);
      } 
      if (BasicInternalFrameTitlePane.this.frame.isMaximizable()) {
        BasicInternalFrameTitlePane.this.maxButton.setBounds(k, (j - m) / 2, 16, 14);
        k += (bool ? -18 : 18);
      } 
      if (BasicInternalFrameTitlePane.this.frame.isIconifiable())
        BasicInternalFrameTitlePane.this.iconButton.setBounds(k, (j - m) / 2, 16, 14); 
    }
  }
  
  public class IconifyAction extends AbstractAction {
    public IconifyAction() { super(UIManager.getString("InternalFrameTitlePane.minimizeButtonText")); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicInternalFrameTitlePane.this.frame.isIconifiable())
        if (!BasicInternalFrameTitlePane.this.frame.isIcon()) {
          try {
            BasicInternalFrameTitlePane.this.frame.setIcon(true);
          } catch (PropertyVetoException propertyVetoException) {}
        } else {
          try {
            BasicInternalFrameTitlePane.this.frame.setIcon(false);
          } catch (PropertyVetoException propertyVetoException) {}
        }  
    }
  }
  
  public class MaximizeAction extends AbstractAction {
    public MaximizeAction() { super(UIManager.getString("InternalFrameTitlePane.maximizeButtonText")); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicInternalFrameTitlePane.this.frame.isMaximizable())
        if (BasicInternalFrameTitlePane.this.frame.isMaximum() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
          try {
            BasicInternalFrameTitlePane.this.frame.setIcon(false);
          } catch (PropertyVetoException propertyVetoException) {}
        } else if (!BasicInternalFrameTitlePane.this.frame.isMaximum()) {
          try {
            BasicInternalFrameTitlePane.this.frame.setMaximum(true);
          } catch (PropertyVetoException propertyVetoException) {}
        } else {
          try {
            BasicInternalFrameTitlePane.this.frame.setMaximum(false);
          } catch (PropertyVetoException propertyVetoException) {}
        }  
    }
  }
  
  public class MoveAction extends AbstractAction {
    public MoveAction() { super(UIManager.getString("InternalFrameTitlePane.moveButtonText")); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {}
  }
  
  private class NoFocusButton extends JButton {
    private String uiKey;
    
    public NoFocusButton(String param1String1, String param1String2) {
      setFocusPainted(false);
      setMargin(new Insets(0, 0, 0, 0));
      this.uiKey = param1String1;
      Object object = UIManager.get(param1String2);
      if (object instanceof Boolean)
        setOpaque(((Boolean)object).booleanValue()); 
    }
    
    public boolean isFocusTraversable() { return false; }
    
    public void requestFocus() {}
    
    public AccessibleContext getAccessibleContext() {
      AccessibleContext accessibleContext = super.getAccessibleContext();
      if (this.uiKey != null) {
        accessibleContext.setAccessibleName(UIManager.getString(this.uiKey));
        this.uiKey = null;
      } 
      return accessibleContext;
    }
  }
  
  public class PropertyChangeHandler implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) { BasicInternalFrameTitlePane.this.getHandler().propertyChange(param1PropertyChangeEvent); }
  }
  
  public class RestoreAction extends AbstractAction {
    public RestoreAction() { super(UIManager.getString("InternalFrameTitlePane.restoreButtonText")); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (BasicInternalFrameTitlePane.this.frame.isMaximizable() && BasicInternalFrameTitlePane.this.frame.isMaximum() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
        try {
          BasicInternalFrameTitlePane.this.frame.setIcon(false);
        } catch (PropertyVetoException propertyVetoException) {}
      } else if (BasicInternalFrameTitlePane.this.frame.isMaximizable() && BasicInternalFrameTitlePane.this.frame.isMaximum()) {
        try {
          BasicInternalFrameTitlePane.this.frame.setMaximum(false);
        } catch (PropertyVetoException propertyVetoException) {}
      } else if (BasicInternalFrameTitlePane.this.frame.isIconifiable() && BasicInternalFrameTitlePane.this.frame.isIcon()) {
        try {
          BasicInternalFrameTitlePane.this.frame.setIcon(false);
        } catch (PropertyVetoException propertyVetoException) {}
      } 
    }
  }
  
  private class ShowSystemMenuAction extends AbstractAction {
    private boolean show;
    
    public ShowSystemMenuAction(boolean param1Boolean) { this.show = param1Boolean; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (this.show) {
        BasicInternalFrameTitlePane.this.windowMenu.doClick();
      } else {
        BasicInternalFrameTitlePane.this.windowMenu.setVisible(false);
      } 
    }
  }
  
  public class SizeAction extends AbstractAction {
    public SizeAction() { super(UIManager.getString("InternalFrameTitlePane.sizeButtonText")); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {}
  }
  
  public class SystemMenuBar extends JMenuBar {
    public boolean isFocusTraversable() { return false; }
    
    public void requestFocus() {}
    
    public void paint(Graphics param1Graphics) {
      Icon icon = BasicInternalFrameTitlePane.this.frame.getFrameIcon();
      if (icon == null)
        icon = (Icon)DefaultLookup.get(BasicInternalFrameTitlePane.this.frame, BasicInternalFrameTitlePane.this.frame.getUI(), "InternalFrame.icon"); 
      if (icon != null) {
        if (icon instanceof ImageIcon && (icon.getIconWidth() > 16 || icon.getIconHeight() > 16)) {
          Image image = ((ImageIcon)icon).getImage();
          ((ImageIcon)icon).setImage(image.getScaledInstance(16, 16, 4));
        } 
        icon.paintIcon(this, param1Graphics, 0, 0);
      } 
    }
    
    public boolean isOpaque() { return true; }
  }
  
  public class TitlePaneLayout implements LayoutManager {
    public void addLayoutComponent(String param1String, Component param1Component) { BasicInternalFrameTitlePane.this.getHandler().addLayoutComponent(param1String, param1Component); }
    
    public void removeLayoutComponent(Component param1Component) { BasicInternalFrameTitlePane.this.getHandler().removeLayoutComponent(param1Component); }
    
    public Dimension preferredLayoutSize(Container param1Container) { return BasicInternalFrameTitlePane.this.getHandler().preferredLayoutSize(param1Container); }
    
    public Dimension minimumLayoutSize(Container param1Container) { return BasicInternalFrameTitlePane.this.getHandler().minimumLayoutSize(param1Container); }
    
    public void layoutContainer(Container param1Container) { BasicInternalFrameTitlePane.this.getHandler().layoutContainer(param1Container); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicInternalFrameTitlePane.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */