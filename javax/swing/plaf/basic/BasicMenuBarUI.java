package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.MenuBarUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicMenuBarUI extends MenuBarUI {
  protected JMenuBar menuBar = null;
  
  protected ContainerListener containerListener;
  
  protected ChangeListener changeListener;
  
  private Handler handler;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicMenuBarUI(); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) { paramLazyActionMap.put(new Actions("takeFocus")); }
  
  public void installUI(JComponent paramJComponent) {
    this.menuBar = (JMenuBar)paramJComponent;
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  protected void installDefaults() {
    if (this.menuBar.getLayout() == null || this.menuBar.getLayout() instanceof javax.swing.plaf.UIResource)
      this.menuBar.setLayout(new DefaultMenuLayout(this.menuBar, 2)); 
    LookAndFeel.installProperty(this.menuBar, "opaque", Boolean.TRUE);
    LookAndFeel.installBorder(this.menuBar, "MenuBar.border");
    LookAndFeel.installColorsAndFont(this.menuBar, "MenuBar.background", "MenuBar.foreground", "MenuBar.font");
  }
  
  protected void installListeners() {
    this.containerListener = createContainerListener();
    this.changeListener = createChangeListener();
    for (byte b = 0; b < this.menuBar.getMenuCount(); b++) {
      JMenu jMenu = this.menuBar.getMenu(b);
      if (jMenu != null)
        jMenu.getModel().addChangeListener(this.changeListener); 
    } 
    this.menuBar.addContainerListener(this.containerListener);
  }
  
  protected void installKeyboardActions() {
    InputMap inputMap = getInputMap(2);
    SwingUtilities.replaceUIInputMap(this.menuBar, 2, inputMap);
    LazyActionMap.installLazyActionMap(this.menuBar, BasicMenuBarUI.class, "MenuBar.actionMap");
  }
  
  InputMap getInputMap(int paramInt) {
    if (paramInt == 2) {
      Object[] arrayOfObject = (Object[])DefaultLookup.get(this.menuBar, this, "MenuBar.windowBindings");
      if (arrayOfObject != null)
        return LookAndFeel.makeComponentInputMap(this.menuBar, arrayOfObject); 
    } 
    return null;
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    this.menuBar = null;
  }
  
  protected void uninstallDefaults() {
    if (this.menuBar != null)
      LookAndFeel.uninstallBorder(this.menuBar); 
  }
  
  protected void uninstallListeners() {
    this.menuBar.removeContainerListener(this.containerListener);
    for (byte b = 0; b < this.menuBar.getMenuCount(); b++) {
      JMenu jMenu = this.menuBar.getMenu(b);
      if (jMenu != null)
        jMenu.getModel().removeChangeListener(this.changeListener); 
    } 
    this.containerListener = null;
    this.changeListener = null;
    this.handler = null;
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIInputMap(this.menuBar, 2, null);
    SwingUtilities.replaceUIActionMap(this.menuBar, null);
  }
  
  protected ContainerListener createContainerListener() { return getHandler(); }
  
  protected ChangeListener createChangeListener() { return getHandler(); }
  
  private Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  public Dimension getMinimumSize(JComponent paramJComponent) { return null; }
  
  public Dimension getMaximumSize(JComponent paramJComponent) { return null; }
  
  private static class Actions extends UIAction {
    private static final String TAKE_FOCUS = "takeFocus";
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JMenuBar jMenuBar = (JMenuBar)param1ActionEvent.getSource();
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      JMenu jMenu = jMenuBar.getMenu(0);
      if (jMenu != null) {
        MenuElement[] arrayOfMenuElement = new MenuElement[3];
        arrayOfMenuElement[0] = jMenuBar;
        arrayOfMenuElement[1] = jMenu;
        arrayOfMenuElement[2] = jMenu.getPopupMenu();
        menuSelectionManager.setSelectedPath(arrayOfMenuElement);
      } 
    }
  }
  
  private class Handler implements ChangeListener, ContainerListener {
    private Handler() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      byte b = 0;
      int i = BasicMenuBarUI.this.menuBar.getMenuCount();
      while (b < i) {
        JMenu jMenu = BasicMenuBarUI.this.menuBar.getMenu(b);
        if (jMenu != null && jMenu.isSelected()) {
          BasicMenuBarUI.this.menuBar.getSelectionModel().setSelectedIndex(b);
          break;
        } 
        b++;
      } 
    }
    
    public void componentAdded(ContainerEvent param1ContainerEvent) {
      Component component = param1ContainerEvent.getChild();
      if (component instanceof JMenu)
        ((JMenu)component).getModel().addChangeListener(BasicMenuBarUI.this.changeListener); 
    }
    
    public void componentRemoved(ContainerEvent param1ContainerEvent) {
      Component component = param1ContainerEvent.getChild();
      if (component instanceof JMenu)
        ((JMenu)component).getModel().removeChangeListener(BasicMenuBarUI.this.changeListener); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicMenuBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */