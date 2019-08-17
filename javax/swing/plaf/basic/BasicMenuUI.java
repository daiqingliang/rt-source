package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

public class BasicMenuUI extends BasicMenuItemUI {
  protected ChangeListener changeListener;
  
  protected MenuListener menuListener;
  
  private int lastMnemonic = 0;
  
  private InputMap selectedWindowInputMap;
  
  private static final boolean TRACE = false;
  
  private static final boolean VERBOSE = false;
  
  private static final boolean DEBUG = false;
  
  private static boolean crossMenuMnemonic = true;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicMenuUI(); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    BasicMenuItemUI.loadActionMap(paramLazyActionMap);
    paramLazyActionMap.put(new Actions("selectMenu", null, true));
  }
  
  protected void installDefaults() {
    super.installDefaults();
    updateDefaultBackgroundColor();
    ((JMenu)this.menuItem).setDelay(200);
    crossMenuMnemonic = UIManager.getBoolean("Menu.crossMenuMnemonic");
  }
  
  protected String getPropertyPrefix() { return "Menu"; }
  
  protected void installListeners() {
    super.installListeners();
    if (this.changeListener == null)
      this.changeListener = createChangeListener(this.menuItem); 
    if (this.changeListener != null)
      this.menuItem.addChangeListener(this.changeListener); 
    if (this.menuListener == null)
      this.menuListener = createMenuListener(this.menuItem); 
    if (this.menuListener != null)
      ((JMenu)this.menuItem).addMenuListener(this.menuListener); 
  }
  
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    updateMnemonicBinding();
  }
  
  void installLazyActionMap() { LazyActionMap.installLazyActionMap(this.menuItem, BasicMenuUI.class, getPropertyPrefix() + ".actionMap"); }
  
  void updateMnemonicBinding() {
    int i = this.menuItem.getModel().getMnemonic();
    int[] arrayOfInt = (int[])DefaultLookup.get(this.menuItem, this, "Menu.shortcutKeys");
    if (arrayOfInt == null)
      arrayOfInt = new int[] { 8 }; 
    if (i == this.lastMnemonic)
      return; 
    InputMap inputMap = SwingUtilities.getUIInputMap(this.menuItem, 2);
    if (this.lastMnemonic != 0 && inputMap != null)
      for (int j : arrayOfInt)
        inputMap.remove(KeyStroke.getKeyStroke(this.lastMnemonic, j, false));  
    if (i != 0) {
      if (inputMap == null) {
        inputMap = createInputMap(2);
        SwingUtilities.replaceUIInputMap(this.menuItem, 2, inputMap);
      } 
      for (int j : arrayOfInt)
        inputMap.put(KeyStroke.getKeyStroke(i, j, false), "selectMenu"); 
    } 
    this.lastMnemonic = i;
  }
  
  protected void uninstallKeyboardActions() {
    super.uninstallKeyboardActions();
    this.lastMnemonic = 0;
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent) { return getHandler(); }
  
  protected MenuListener createMenuListener(JComponent paramJComponent) { return null; }
  
  protected ChangeListener createChangeListener(JComponent paramJComponent) { return null; }
  
  protected PropertyChangeListener createPropertyChangeListener(JComponent paramJComponent) { return getHandler(); }
  
  BasicMenuItemUI.Handler getHandler() {
    if (this.handler == null)
      this.handler = new Handler(null); 
    return this.handler;
  }
  
  protected void uninstallDefaults() {
    this.menuItem.setArmed(false);
    this.menuItem.setSelected(false);
    this.menuItem.resetKeyboardActions();
    super.uninstallDefaults();
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    if (this.changeListener != null)
      this.menuItem.removeChangeListener(this.changeListener); 
    if (this.menuListener != null)
      ((JMenu)this.menuItem).removeMenuListener(this.menuListener); 
    this.changeListener = null;
    this.menuListener = null;
    this.handler = null;
  }
  
  protected MenuDragMouseListener createMenuDragMouseListener(JComponent paramJComponent) { return getHandler(); }
  
  protected MenuKeyListener createMenuKeyListener(JComponent paramJComponent) { return (MenuKeyListener)getHandler(); }
  
  public Dimension getMaximumSize(JComponent paramJComponent) {
    if (((JMenu)this.menuItem).isTopLevelMenu() == true) {
      Dimension dimension = paramJComponent.getPreferredSize();
      return new Dimension(dimension.width, 32767);
    } 
    return null;
  }
  
  protected void setupPostTimer(JMenu paramJMenu) {
    Timer timer = new Timer(paramJMenu.getDelay(), new Actions("selectMenu", paramJMenu, false));
    timer.setRepeats(false);
    timer.start();
  }
  
  private static void appendPath(MenuElement[] paramArrayOfMenuElement, MenuElement paramMenuElement) {
    MenuElement[] arrayOfMenuElement = new MenuElement[paramArrayOfMenuElement.length + 1];
    System.arraycopy(paramArrayOfMenuElement, 0, arrayOfMenuElement, 0, paramArrayOfMenuElement.length);
    arrayOfMenuElement[paramArrayOfMenuElement.length] = paramMenuElement;
    MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
  }
  
  private void updateDefaultBackgroundColor() {
    if (!UIManager.getBoolean("Menu.useMenuBarBackgroundForTopLevel"))
      return; 
    JMenu jMenu = (JMenu)this.menuItem;
    if (jMenu.getBackground() instanceof javax.swing.plaf.UIResource)
      if (jMenu.isTopLevelMenu()) {
        jMenu.setBackground(UIManager.getColor("MenuBar.background"));
      } else {
        jMenu.setBackground(UIManager.getColor(getPropertyPrefix() + ".background"));
      }  
  }
  
  private static class Actions extends UIAction {
    private static final String SELECT = "selectMenu";
    
    private JMenu menu;
    
    private boolean force = false;
    
    Actions(String param1String, JMenu param1JMenu, boolean param1Boolean) {
      super(param1String);
      this.menu = param1JMenu;
      this.force = param1Boolean;
    }
    
    private JMenu getMenu(ActionEvent param1ActionEvent) { return (param1ActionEvent.getSource() instanceof JMenu) ? (JMenu)param1ActionEvent.getSource() : this.menu; }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JMenu jMenu = getMenu(param1ActionEvent);
      if (!crossMenuMnemonic) {
        JPopupMenu jPopupMenu = BasicPopupMenuUI.getLastPopup();
        if (jPopupMenu != null && jPopupMenu != jMenu.getParent())
          return; 
      } 
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      if (this.force) {
        Container container = jMenu.getParent();
        if (container != null && container instanceof javax.swing.JMenuBar) {
          MenuElement[] arrayOfMenuElement1;
          MenuElement[] arrayOfMenuElement2 = jMenu.getPopupMenu().getSubElements();
          if (arrayOfMenuElement2.length > 0) {
            arrayOfMenuElement1 = new MenuElement[4];
            arrayOfMenuElement1[0] = (MenuElement)container;
            arrayOfMenuElement1[1] = jMenu;
            arrayOfMenuElement1[2] = jMenu.getPopupMenu();
            arrayOfMenuElement1[3] = arrayOfMenuElement2[0];
          } else {
            arrayOfMenuElement1 = new MenuElement[3];
            arrayOfMenuElement1[0] = (MenuElement)container;
            arrayOfMenuElement1[1] = jMenu;
            arrayOfMenuElement1[2] = jMenu.getPopupMenu();
          } 
          menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
        } 
      } else {
        MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
        if (arrayOfMenuElement.length > 0 && arrayOfMenuElement[arrayOfMenuElement.length - true] == jMenu)
          BasicMenuUI.appendPath(arrayOfMenuElement, jMenu.getPopupMenu()); 
      } 
    }
    
    public boolean isEnabled(Object param1Object) { return (param1Object instanceof JMenu) ? ((JMenu)param1Object).isEnabled() : 1; }
  }
  
  public class ChangeHandler implements ChangeListener {
    public JMenu menu;
    
    public BasicMenuUI ui;
    
    public boolean isSelected = false;
    
    public Component wasFocused;
    
    public ChangeHandler(JMenu param1JMenu, BasicMenuUI param1BasicMenuUI1) {
      this.menu = param1JMenu;
      this.ui = param1BasicMenuUI1;
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {}
  }
  
  private class Handler extends BasicMenuItemUI.Handler implements MenuKeyListener {
    private Handler() { super(BasicMenuUI.this); }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      if (param1PropertyChangeEvent.getPropertyName() == "mnemonic") {
        BasicMenuUI.this.updateMnemonicBinding();
      } else {
        if (param1PropertyChangeEvent.getPropertyName().equals("ancestor"))
          BasicMenuUI.this.updateDefaultBackgroundColor(); 
        super.propertyChange(param1PropertyChangeEvent);
      } 
    }
    
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      JMenu jMenu = (JMenu)BasicMenuUI.this.menuItem;
      if (!jMenu.isEnabled())
        return; 
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      if (jMenu.isTopLevelMenu())
        if (jMenu.isSelected() && jMenu.getPopupMenu().isShowing()) {
          menuSelectionManager.clearSelectedPath();
        } else {
          Container container = jMenu.getParent();
          if (container != null && container instanceof javax.swing.JMenuBar) {
            MenuElement[] arrayOfMenuElement1 = new MenuElement[2];
            arrayOfMenuElement1[0] = (MenuElement)container;
            arrayOfMenuElement1[1] = jMenu;
            menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
          } 
        }  
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      if (arrayOfMenuElement.length > 0 && arrayOfMenuElement[arrayOfMenuElement.length - true] != jMenu.getPopupMenu())
        if (jMenu.isTopLevelMenu() || jMenu.getDelay() == 0) {
          BasicMenuUI.appendPath(arrayOfMenuElement, jMenu.getPopupMenu());
        } else {
          BasicMenuUI.this.setupPostTimer(jMenu);
        }  
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      JMenu jMenu = (JMenu)BasicMenuUI.this.menuItem;
      if (!jMenu.isEnabled())
        return; 
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      menuSelectionManager.processMouseEvent(param1MouseEvent);
      if (!param1MouseEvent.isConsumed())
        menuSelectionManager.clearSelectedPath(); 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      JMenu jMenu = (JMenu)BasicMenuUI.this.menuItem;
      if (!jMenu.isEnabled() && !UIManager.getBoolean("MenuItem.disabledAreNavigable"))
        return; 
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      if (!jMenu.isTopLevelMenu()) {
        if (arrayOfMenuElement.length <= 0 || arrayOfMenuElement[arrayOfMenuElement.length - true] != jMenu.getPopupMenu())
          if (jMenu.getDelay() == 0) {
            BasicMenuUI.appendPath(BasicMenuUI.this.getPath(), jMenu.getPopupMenu());
          } else {
            menuSelectionManager.setSelectedPath(BasicMenuUI.this.getPath());
            BasicMenuUI.this.setupPostTimer(jMenu);
          }  
      } else if (arrayOfMenuElement.length > 0 && arrayOfMenuElement[false] == jMenu.getParent()) {
        MenuElement[] arrayOfMenuElement1 = new MenuElement[3];
        arrayOfMenuElement1[0] = (MenuElement)jMenu.getParent();
        arrayOfMenuElement1[1] = jMenu;
        if (BasicPopupMenuUI.getLastPopup() != null)
          arrayOfMenuElement1[2] = jMenu.getPopupMenu(); 
        menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
      } 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mouseDragged(MouseEvent param1MouseEvent) {
      JMenu jMenu = (JMenu)BasicMenuUI.this.menuItem;
      if (!jMenu.isEnabled())
        return; 
      MenuSelectionManager.defaultManager().processMouseEvent(param1MouseEvent);
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
    
    public void menuDragMouseEntered(MenuDragMouseEvent param1MenuDragMouseEvent) {}
    
    public void menuDragMouseDragged(MenuDragMouseEvent param1MenuDragMouseEvent) {
      if (!BasicMenuUI.this.menuItem.isEnabled())
        return; 
      MenuSelectionManager menuSelectionManager = param1MenuDragMouseEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement = param1MenuDragMouseEvent.getPath();
      Point point = param1MenuDragMouseEvent.getPoint();
      if (point.x >= 0 && point.x < BasicMenuUI.this.menuItem.getWidth() && point.y >= 0 && point.y < BasicMenuUI.this.menuItem.getHeight()) {
        JMenu jMenu = (JMenu)BasicMenuUI.this.menuItem;
        MenuElement[] arrayOfMenuElement1 = menuSelectionManager.getSelectedPath();
        if (arrayOfMenuElement1.length <= 0 || arrayOfMenuElement1[arrayOfMenuElement1.length - true] != jMenu.getPopupMenu())
          if (jMenu.isTopLevelMenu() || jMenu.getDelay() == 0 || param1MenuDragMouseEvent.getID() == 506) {
            BasicMenuUI.appendPath(arrayOfMenuElement, jMenu.getPopupMenu());
          } else {
            menuSelectionManager.setSelectedPath(arrayOfMenuElement);
            BasicMenuUI.this.setupPostTimer(jMenu);
          }  
      } else if (param1MenuDragMouseEvent.getID() == 502) {
        Component component = menuSelectionManager.componentForPoint(param1MenuDragMouseEvent.getComponent(), param1MenuDragMouseEvent.getPoint());
        if (component == null)
          menuSelectionManager.clearSelectedPath(); 
      } 
    }
    
    public void menuDragMouseExited(MenuDragMouseEvent param1MenuDragMouseEvent) {}
    
    public void menuDragMouseReleased(MenuDragMouseEvent param1MenuDragMouseEvent) {}
    
    public void menuKeyTyped(MenuKeyEvent param1MenuKeyEvent) {
      if (!crossMenuMnemonic && BasicPopupMenuUI.getLastPopup() != null)
        return; 
      if (BasicPopupMenuUI.getPopups().size() != 0)
        return; 
      char c = Character.toLowerCase((char)BasicMenuUI.this.menuItem.getMnemonic());
      MenuElement[] arrayOfMenuElement = param1MenuKeyEvent.getPath();
      if (c == Character.toLowerCase(param1MenuKeyEvent.getKeyChar())) {
        JPopupMenu jPopupMenu = ((JMenu)BasicMenuUI.this.menuItem).getPopupMenu();
        ArrayList arrayList = new ArrayList(Arrays.asList(arrayOfMenuElement));
        arrayList.add(jPopupMenu);
        MenuElement[] arrayOfMenuElement1 = jPopupMenu.getSubElements();
        MenuElement menuElement = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement1, -1, true);
        if (menuElement != null)
          arrayList.add(menuElement); 
        MenuSelectionManager menuSelectionManager = param1MenuKeyEvent.getMenuSelectionManager();
        MenuElement[] arrayOfMenuElement2 = new MenuElement[0];
        arrayOfMenuElement2 = (MenuElement[])arrayList.toArray(arrayOfMenuElement2);
        menuSelectionManager.setSelectedPath(arrayOfMenuElement2);
        param1MenuKeyEvent.consume();
      } 
    }
    
    public void menuKeyPressed(MenuKeyEvent param1MenuKeyEvent) {}
    
    public void menuKeyReleased(MenuKeyEvent param1MenuKeyEvent) {}
  }
  
  protected class MouseInputHandler implements MouseInputListener {
    public void mouseClicked(MouseEvent param1MouseEvent) { BasicMenuUI.this.getHandler().mouseClicked(param1MouseEvent); }
    
    public void mousePressed(MouseEvent param1MouseEvent) { BasicMenuUI.this.getHandler().mousePressed(param1MouseEvent); }
    
    public void mouseReleased(MouseEvent param1MouseEvent) { BasicMenuUI.this.getHandler().mouseReleased(param1MouseEvent); }
    
    public void mouseEntered(MouseEvent param1MouseEvent) { BasicMenuUI.this.getHandler().mouseEntered(param1MouseEvent); }
    
    public void mouseExited(MouseEvent param1MouseEvent) { BasicMenuUI.this.getHandler().mouseExited(param1MouseEvent); }
    
    public void mouseDragged(MouseEvent param1MouseEvent) { BasicMenuUI.this.getHandler().mouseDragged(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) { BasicMenuUI.this.getHandler().mouseMoved(param1MouseEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */