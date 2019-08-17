package javax.swing.plaf.basic;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PopupMenuUI;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.swing.UIAction;

public class BasicPopupMenuUI extends PopupMenuUI {
  static final StringBuilder MOUSE_GRABBER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MouseGrabber");
  
  static final StringBuilder MENU_KEYBOARD_HELPER_KEY = new StringBuilder("javax.swing.plaf.basic.BasicPopupMenuUI.MenuKeyboardHelper");
  
  protected JPopupMenu popupMenu = null;
  
  private PopupMenuListener popupMenuListener = null;
  
  private MenuKeyListener menuKeyListener = null;
  
  private static boolean checkedUnpostPopup;
  
  private static boolean unpostPopup;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicPopupMenuUI(); }
  
  public BasicPopupMenuUI() {
    BasicLookAndFeel.needsEventHelper = true;
    LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
    if (lookAndFeel instanceof BasicLookAndFeel)
      ((BasicLookAndFeel)lookAndFeel).installAWTEventListener(); 
  }
  
  public void installUI(JComponent paramJComponent) {
    this.popupMenu = (JPopupMenu)paramJComponent;
    installDefaults();
    installListeners();
    installKeyboardActions();
  }
  
  public void installDefaults() {
    if (this.popupMenu.getLayout() == null || this.popupMenu.getLayout() instanceof javax.swing.plaf.UIResource)
      this.popupMenu.setLayout(new DefaultMenuLayout(this.popupMenu, 1)); 
    LookAndFeel.installProperty(this.popupMenu, "opaque", Boolean.TRUE);
    LookAndFeel.installBorder(this.popupMenu, "PopupMenu.border");
    LookAndFeel.installColorsAndFont(this.popupMenu, "PopupMenu.background", "PopupMenu.foreground", "PopupMenu.font");
  }
  
  protected void installListeners() {
    if (this.popupMenuListener == null)
      this.popupMenuListener = new BasicPopupMenuListener(null); 
    this.popupMenu.addPopupMenuListener(this.popupMenuListener);
    if (this.menuKeyListener == null)
      this.menuKeyListener = new BasicMenuKeyListener(null); 
    this.popupMenu.addMenuKeyListener(this.menuKeyListener);
    AppContext appContext = AppContext.getAppContext();
    synchronized (MOUSE_GRABBER_KEY) {
      MouseGrabber mouseGrabber = (MouseGrabber)appContext.get(MOUSE_GRABBER_KEY);
      if (mouseGrabber == null) {
        mouseGrabber = new MouseGrabber();
        appContext.put(MOUSE_GRABBER_KEY, mouseGrabber);
      } 
    } 
    synchronized (MENU_KEYBOARD_HELPER_KEY) {
      MenuKeyboardHelper menuKeyboardHelper = (MenuKeyboardHelper)appContext.get(MENU_KEYBOARD_HELPER_KEY);
      if (menuKeyboardHelper == null) {
        menuKeyboardHelper = new MenuKeyboardHelper();
        appContext.put(MENU_KEYBOARD_HELPER_KEY, menuKeyboardHelper);
        MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
        menuSelectionManager.addChangeListener(menuKeyboardHelper);
      } 
    } 
  }
  
  protected void installKeyboardActions() {}
  
  static InputMap getInputMap(JPopupMenu paramJPopupMenu, JComponent paramJComponent) {
    ComponentInputMap componentInputMap = null;
    Object[] arrayOfObject = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings");
    if (arrayOfObject != null) {
      componentInputMap = LookAndFeel.makeComponentInputMap(paramJComponent, arrayOfObject);
      if (!paramJPopupMenu.getComponentOrientation().isLeftToRight()) {
        Object[] arrayOfObject1 = (Object[])UIManager.get("PopupMenu.selectedWindowInputMapBindings.RightToLeft");
        if (arrayOfObject1 != null) {
          ComponentInputMap componentInputMap1 = LookAndFeel.makeComponentInputMap(paramJComponent, arrayOfObject1);
          componentInputMap1.setParent(componentInputMap);
          componentInputMap = componentInputMap1;
        } 
      } 
    } 
    return componentInputMap;
  }
  
  static ActionMap getActionMap() { return LazyActionMap.getActionMap(BasicPopupMenuUI.class, "PopupMenu.actionMap"); }
  
  static void loadActionMap(LazyActionMap paramLazyActionMap) {
    paramLazyActionMap.put(new Actions("cancel"));
    paramLazyActionMap.put(new Actions("selectNext"));
    paramLazyActionMap.put(new Actions("selectPrevious"));
    paramLazyActionMap.put(new Actions("selectParent"));
    paramLazyActionMap.put(new Actions("selectChild"));
    paramLazyActionMap.put(new Actions("return"));
    BasicLookAndFeel.installAudioActionMap(paramLazyActionMap);
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    uninstallDefaults();
    uninstallListeners();
    uninstallKeyboardActions();
    this.popupMenu = null;
  }
  
  protected void uninstallDefaults() { LookAndFeel.uninstallBorder(this.popupMenu); }
  
  protected void uninstallListeners() {
    if (this.popupMenuListener != null)
      this.popupMenu.removePopupMenuListener(this.popupMenuListener); 
    if (this.menuKeyListener != null)
      this.popupMenu.removeMenuKeyListener(this.menuKeyListener); 
  }
  
  protected void uninstallKeyboardActions() {
    SwingUtilities.replaceUIActionMap(this.popupMenu, null);
    SwingUtilities.replaceUIInputMap(this.popupMenu, 2, null);
  }
  
  static MenuElement getFirstPopup() {
    MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
    MenuElement menuElement = null;
    for (byte b = 0; menuElement == null && b < arrayOfMenuElement.length; b++) {
      if (arrayOfMenuElement[b] instanceof JPopupMenu)
        menuElement = arrayOfMenuElement[b]; 
    } 
    return menuElement;
  }
  
  static JPopupMenu getLastPopup() {
    MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
    JPopupMenu jPopupMenu = null;
    for (int i = arrayOfMenuElement.length - 1; jPopupMenu == null && i >= 0; i--) {
      if (arrayOfMenuElement[i] instanceof JPopupMenu)
        jPopupMenu = (JPopupMenu)arrayOfMenuElement[i]; 
    } 
    return jPopupMenu;
  }
  
  static List<JPopupMenu> getPopups() {
    MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
    MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
    ArrayList arrayList = new ArrayList(arrayOfMenuElement.length);
    for (MenuElement menuElement : arrayOfMenuElement) {
      if (menuElement instanceof JPopupMenu)
        arrayList.add((JPopupMenu)menuElement); 
    } 
    return arrayList;
  }
  
  public boolean isPopupTrigger(MouseEvent paramMouseEvent) { return (paramMouseEvent.getID() == 502 && (paramMouseEvent.getModifiers() & 0x4) != 0); }
  
  private static boolean checkInvokerEqual(MenuElement paramMenuElement1, MenuElement paramMenuElement2) {
    Component component1 = paramMenuElement1.getComponent();
    Component component2 = paramMenuElement2.getComponent();
    if (component1 instanceof JPopupMenu)
      component1 = ((JPopupMenu)component1).getInvoker(); 
    if (component2 instanceof JPopupMenu)
      component2 = ((JPopupMenu)component2).getInvoker(); 
    return (component1 == component2);
  }
  
  private static MenuElement nextEnabledChild(MenuElement[] paramArrayOfMenuElement, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i <= paramInt2; i++) {
      if (paramArrayOfMenuElement[i] != null) {
        Component component = paramArrayOfMenuElement[i].getComponent();
        if (component != null && (component.isEnabled() || UIManager.getBoolean("MenuItem.disabledAreNavigable")) && component.isVisible())
          return paramArrayOfMenuElement[i]; 
      } 
    } 
    return null;
  }
  
  private static MenuElement previousEnabledChild(MenuElement[] paramArrayOfMenuElement, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i >= paramInt2; i--) {
      if (paramArrayOfMenuElement[i] != null) {
        Component component = paramArrayOfMenuElement[i].getComponent();
        if (component != null && (component.isEnabled() || UIManager.getBoolean("MenuItem.disabledAreNavigable")) && component.isVisible())
          return paramArrayOfMenuElement[i]; 
      } 
    } 
    return null;
  }
  
  static MenuElement findEnabledChild(MenuElement[] paramArrayOfMenuElement, int paramInt, boolean paramBoolean) {
    MenuElement menuElement;
    if (paramBoolean) {
      menuElement = nextEnabledChild(paramArrayOfMenuElement, paramInt + 1, paramArrayOfMenuElement.length - 1);
      if (menuElement == null)
        menuElement = nextEnabledChild(paramArrayOfMenuElement, 0, paramInt - 1); 
    } else {
      menuElement = previousEnabledChild(paramArrayOfMenuElement, paramInt - 1, 0);
      if (menuElement == null)
        menuElement = previousEnabledChild(paramArrayOfMenuElement, paramArrayOfMenuElement.length - 1, paramInt + 1); 
    } 
    return menuElement;
  }
  
  static MenuElement findEnabledChild(MenuElement[] paramArrayOfMenuElement, MenuElement paramMenuElement, boolean paramBoolean) {
    for (byte b = 0; b < paramArrayOfMenuElement.length; b++) {
      if (paramArrayOfMenuElement[b] == paramMenuElement)
        return findEnabledChild(paramArrayOfMenuElement, b, paramBoolean); 
    } 
    return null;
  }
  
  private static class Actions extends UIAction {
    private static final String CANCEL = "cancel";
    
    private static final String SELECT_NEXT = "selectNext";
    
    private static final String SELECT_PREVIOUS = "selectPrevious";
    
    private static final String SELECT_PARENT = "selectParent";
    
    private static final String SELECT_CHILD = "selectChild";
    
    private static final String RETURN = "return";
    
    private static final boolean FORWARD = true;
    
    private static final boolean BACKWARD = false;
    
    private static final boolean PARENT = false;
    
    private static final boolean CHILD = true;
    
    Actions(String param1String) { super(param1String); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      String str = getName();
      if (str == "cancel") {
        cancel();
      } else if (str == "selectNext") {
        selectItem(true);
      } else if (str == "selectPrevious") {
        selectItem(false);
      } else if (str == "selectParent") {
        selectParentChild(false);
      } else if (str == "selectChild") {
        selectParentChild(true);
      } else if (str == "return") {
        doReturn();
      } 
    }
    
    private void doReturn() {
      KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      Component component = keyboardFocusManager.getFocusOwner();
      if (component != null && !(component instanceof JRootPane))
        return; 
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      if (arrayOfMenuElement.length > 0) {
        MenuElement menuElement = arrayOfMenuElement[arrayOfMenuElement.length - 1];
        if (menuElement instanceof JMenu) {
          MenuElement[] arrayOfMenuElement1 = new MenuElement[arrayOfMenuElement.length + 1];
          System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement1, 0, arrayOfMenuElement.length);
          arrayOfMenuElement1[arrayOfMenuElement.length] = ((JMenu)menuElement).getPopupMenu();
          menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
        } else if (menuElement instanceof JMenuItem) {
          JMenuItem jMenuItem = (JMenuItem)menuElement;
          if (jMenuItem.getUI() instanceof BasicMenuItemUI) {
            ((BasicMenuItemUI)jMenuItem.getUI()).doClick(menuSelectionManager);
          } else {
            menuSelectionManager.clearSelectedPath();
            jMenuItem.doClick(0);
          } 
        } 
      } 
    }
    
    private void selectParentChild(boolean param1Boolean) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      int i = arrayOfMenuElement.length;
      if (!param1Boolean) {
        int j = i - 1;
        if (i > 2 && (arrayOfMenuElement[j] instanceof JPopupMenu || arrayOfMenuElement[--j] instanceof JPopupMenu) && !((JMenu)arrayOfMenuElement[j - 1]).isTopLevelMenu()) {
          MenuElement[] arrayOfMenuElement1 = new MenuElement[j];
          System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement1, 0, j);
          menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
          return;
        } 
      } else if (i > 0 && arrayOfMenuElement[i - 1] instanceof JMenu && !((JMenu)arrayOfMenuElement[i - 1]).isTopLevelMenu()) {
        MenuElement[] arrayOfMenuElement2;
        JMenu jMenu = (JMenu)arrayOfMenuElement[i - 1];
        JPopupMenu jPopupMenu = jMenu.getPopupMenu();
        MenuElement[] arrayOfMenuElement1 = jPopupMenu.getSubElements();
        MenuElement menuElement = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement1, -1, true);
        if (menuElement == null) {
          arrayOfMenuElement2 = new MenuElement[i + 1];
        } else {
          arrayOfMenuElement2 = new MenuElement[i + 2];
          arrayOfMenuElement2[i + 1] = menuElement;
        } 
        System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement2, 0, i);
        arrayOfMenuElement2[i] = jPopupMenu;
        menuSelectionManager.setSelectedPath(arrayOfMenuElement2);
        return;
      } 
      if (i > 1 && arrayOfMenuElement[0] instanceof javax.swing.JMenuBar) {
        MenuElement menuElement1 = arrayOfMenuElement[1];
        MenuElement menuElement2 = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement[0].getSubElements(), menuElement1, param1Boolean);
        if (menuElement2 != null && menuElement2 != menuElement1) {
          MenuElement[] arrayOfMenuElement1;
          if (i == 2) {
            arrayOfMenuElement1 = new MenuElement[2];
            arrayOfMenuElement1[0] = arrayOfMenuElement[0];
            arrayOfMenuElement1[1] = menuElement2;
          } else {
            arrayOfMenuElement1 = new MenuElement[3];
            arrayOfMenuElement1[0] = arrayOfMenuElement[0];
            arrayOfMenuElement1[1] = menuElement2;
            arrayOfMenuElement1[2] = ((JMenu)menuElement2).getPopupMenu();
          } 
          menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
        } 
      } 
    }
    
    private void selectItem(boolean param1Boolean) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      if (arrayOfMenuElement.length == 0)
        return; 
      int i = arrayOfMenuElement.length;
      if (i == 1 && arrayOfMenuElement[0] instanceof JPopupMenu) {
        JPopupMenu jPopupMenu = (JPopupMenu)arrayOfMenuElement[0];
        MenuElement[] arrayOfMenuElement1 = new MenuElement[2];
        arrayOfMenuElement1[0] = jPopupMenu;
        arrayOfMenuElement1[1] = BasicPopupMenuUI.findEnabledChild(jPopupMenu.getSubElements(), -1, param1Boolean);
        menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
      } else if (i == 2 && arrayOfMenuElement[0] instanceof javax.swing.JMenuBar && arrayOfMenuElement[1] instanceof JMenu) {
        MenuElement[] arrayOfMenuElement1;
        JPopupMenu jPopupMenu = ((JMenu)arrayOfMenuElement[1]).getPopupMenu();
        MenuElement menuElement = BasicPopupMenuUI.findEnabledChild(jPopupMenu.getSubElements(), -1, true);
        if (menuElement != null) {
          arrayOfMenuElement1 = new MenuElement[4];
          arrayOfMenuElement1[3] = menuElement;
        } else {
          arrayOfMenuElement1 = new MenuElement[3];
        } 
        System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement1, 0, 2);
        arrayOfMenuElement1[2] = jPopupMenu;
        menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
      } else if (arrayOfMenuElement[i - 1] instanceof JPopupMenu && arrayOfMenuElement[i - 2] instanceof JMenu) {
        JMenu jMenu = (JMenu)arrayOfMenuElement[i - 2];
        JPopupMenu jPopupMenu = jMenu.getPopupMenu();
        MenuElement menuElement = BasicPopupMenuUI.findEnabledChild(jPopupMenu.getSubElements(), -1, param1Boolean);
        if (menuElement != null) {
          MenuElement[] arrayOfMenuElement1 = new MenuElement[i + 1];
          System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement1, 0, i);
          arrayOfMenuElement1[i] = menuElement;
          menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
        } else if (i > 2 && arrayOfMenuElement[i - 3] instanceof JPopupMenu) {
          jPopupMenu = (JPopupMenu)arrayOfMenuElement[i - 3];
          menuElement = BasicPopupMenuUI.findEnabledChild(jPopupMenu.getSubElements(), jMenu, param1Boolean);
          if (menuElement != null && menuElement != jMenu) {
            MenuElement[] arrayOfMenuElement1 = new MenuElement[i - 1];
            System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement1, 0, i - 2);
            arrayOfMenuElement1[i - 2] = menuElement;
            menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
          } 
        } 
      } else {
        MenuElement[] arrayOfMenuElement1 = arrayOfMenuElement[i - 2].getSubElements();
        MenuElement menuElement = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement1, arrayOfMenuElement[i - 1], param1Boolean);
        if (menuElement == null)
          menuElement = BasicPopupMenuUI.findEnabledChild(arrayOfMenuElement1, -1, param1Boolean); 
        if (menuElement != null) {
          arrayOfMenuElement[i - 1] = menuElement;
          menuSelectionManager.setSelectedPath(arrayOfMenuElement);
        } 
      } 
    }
    
    private void cancel() {
      JPopupMenu jPopupMenu = BasicPopupMenuUI.getLastPopup();
      if (jPopupMenu != null)
        jPopupMenu.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE); 
      String str = UIManager.getString("Menu.cancelMode");
      if ("hideMenuTree".equals(str)) {
        MenuSelectionManager.defaultManager().clearSelectedPath();
      } else {
        shortenSelectedPath();
      } 
    }
    
    private void shortenSelectedPath() {
      MenuElement[] arrayOfMenuElement1 = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement1.length <= 2) {
        MenuSelectionManager.defaultManager().clearSelectedPath();
        return;
      } 
      int i = 2;
      MenuElement menuElement = arrayOfMenuElement1[arrayOfMenuElement1.length - 1];
      JPopupMenu jPopupMenu = BasicPopupMenuUI.getLastPopup();
      if (menuElement == jPopupMenu) {
        MenuElement menuElement1 = arrayOfMenuElement1[arrayOfMenuElement1.length - 2];
        if (menuElement1 instanceof JMenu) {
          JMenu jMenu = (JMenu)menuElement1;
          if (jMenu.isEnabled() && jPopupMenu.getComponentCount() > 0) {
            i = 1;
          } else {
            i = 3;
          } 
        } 
      } 
      if (arrayOfMenuElement1.length - i <= 2 && !UIManager.getBoolean("Menu.preserveTopLevelSelection"))
        i = arrayOfMenuElement1.length; 
      MenuElement[] arrayOfMenuElement2 = new MenuElement[arrayOfMenuElement1.length - i];
      System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement2, 0, arrayOfMenuElement1.length - i);
      MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement2);
    }
  }
  
  private class BasicMenuKeyListener implements MenuKeyListener {
    MenuElement menuToOpen = null;
    
    private BasicMenuKeyListener() {}
    
    public void menuKeyTyped(MenuKeyEvent param1MenuKeyEvent) {
      if (this.menuToOpen != null) {
        JPopupMenu jPopupMenu = ((JMenu)this.menuToOpen).getPopupMenu();
        MenuElement menuElement = BasicPopupMenuUI.findEnabledChild(jPopupMenu.getSubElements(), -1, true);
        ArrayList arrayList = new ArrayList(Arrays.asList(param1MenuKeyEvent.getPath()));
        arrayList.add(this.menuToOpen);
        arrayList.add(jPopupMenu);
        if (menuElement != null)
          arrayList.add(menuElement); 
        MenuElement[] arrayOfMenuElement = new MenuElement[0];
        arrayOfMenuElement = (MenuElement[])arrayList.toArray(arrayOfMenuElement);
        MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
        param1MenuKeyEvent.consume();
      } 
      this.menuToOpen = null;
    }
    
    public void menuKeyPressed(MenuKeyEvent param1MenuKeyEvent) {
      char c = param1MenuKeyEvent.getKeyChar();
      if (!Character.isLetterOrDigit(c))
        return; 
      MenuSelectionManager menuSelectionManager = param1MenuKeyEvent.getMenuSelectionManager();
      MenuElement[] arrayOfMenuElement1 = param1MenuKeyEvent.getPath();
      MenuElement[] arrayOfMenuElement2 = BasicPopupMenuUI.this.popupMenu.getSubElements();
      byte b1 = -1;
      byte b2 = 0;
      byte b3 = -1;
      int[] arrayOfInt = null;
      for (byte b4 = 0; b4 < arrayOfMenuElement2.length; b4++) {
        if (arrayOfMenuElement2[b4] instanceof JMenuItem) {
          JMenuItem jMenuItem = (JMenuItem)arrayOfMenuElement2[b4];
          int i = jMenuItem.getMnemonic();
          if (jMenuItem.isEnabled() && jMenuItem.isVisible() && lower(c) == lower(i))
            if (!b2) {
              b3 = b4;
              b2++;
            } else {
              if (arrayOfInt == null) {
                arrayOfInt = new int[arrayOfMenuElement2.length];
                arrayOfInt[0] = b3;
              } 
              arrayOfInt[b2++] = b4;
            }  
          if (jMenuItem.isArmed() || jMenuItem.isSelected())
            b1 = b2 - 1; 
        } 
      } 
      if (b2 != 0)
        if (b2 == 1) {
          JMenuItem jMenuItem = (JMenuItem)arrayOfMenuElement2[b3];
          if (jMenuItem instanceof JMenu) {
            this.menuToOpen = jMenuItem;
          } else if (jMenuItem.isEnabled()) {
            menuSelectionManager.clearSelectedPath();
            jMenuItem.doClick();
          } 
          param1MenuKeyEvent.consume();
        } else {
          MenuElement menuElement = arrayOfMenuElement2[arrayOfInt[(b1 + 1) % b2]];
          MenuElement[] arrayOfMenuElement = new MenuElement[arrayOfMenuElement1.length + 1];
          System.arraycopy(arrayOfMenuElement1, 0, arrayOfMenuElement, 0, arrayOfMenuElement1.length);
          arrayOfMenuElement[arrayOfMenuElement1.length] = menuElement;
          menuSelectionManager.setSelectedPath(arrayOfMenuElement);
          param1MenuKeyEvent.consume();
        }  
    }
    
    public void menuKeyReleased(MenuKeyEvent param1MenuKeyEvent) {}
    
    private char lower(char param1Char) { return Character.toLowerCase(param1Char); }
    
    private char lower(int param1Int) { return Character.toLowerCase((char)param1Int); }
  }
  
  private class BasicPopupMenuListener implements PopupMenuListener {
    private BasicPopupMenuListener() {}
    
    public void popupMenuCanceled(PopupMenuEvent param1PopupMenuEvent) {}
    
    public void popupMenuWillBecomeInvisible(PopupMenuEvent param1PopupMenuEvent) {}
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent param1PopupMenuEvent) { BasicLookAndFeel.playSound((JPopupMenu)param1PopupMenuEvent.getSource(), "PopupMenu.popupSound"); }
  }
  
  static class MenuKeyboardHelper implements ChangeListener, KeyListener {
    private Component lastFocused = null;
    
    private MenuElement[] lastPathSelected = new MenuElement[0];
    
    private JPopupMenu lastPopup;
    
    private JRootPane invokerRootPane;
    
    private ActionMap menuActionMap = BasicPopupMenuUI.getActionMap();
    
    private InputMap menuInputMap;
    
    private boolean focusTraversalKeysEnabled;
    
    private boolean receivedKeyPressed = false;
    
    private FocusListener rootPaneFocusListener = new FocusAdapter() {
        public void focusGained(FocusEvent param2FocusEvent) {
          Component component = param2FocusEvent.getOppositeComponent();
          if (component != null)
            BasicPopupMenuUI.MenuKeyboardHelper.this.lastFocused = component; 
          param2FocusEvent.getComponent().removeFocusListener(this);
        }
      };
    
    void removeItems() {
      if (this.lastFocused != null) {
        if (!this.lastFocused.requestFocusInWindow()) {
          Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
          if (window != null && "###focusableSwingPopup###".equals(window.getName()))
            this.lastFocused.requestFocus(); 
        } 
        this.lastFocused = null;
      } 
      if (this.invokerRootPane != null) {
        this.invokerRootPane.removeKeyListener(this);
        this.invokerRootPane.setFocusTraversalKeysEnabled(this.focusTraversalKeysEnabled);
        removeUIInputMap(this.invokerRootPane, this.menuInputMap);
        removeUIActionMap(this.invokerRootPane, this.menuActionMap);
        this.invokerRootPane = null;
      } 
      this.receivedKeyPressed = false;
    }
    
    JPopupMenu getActivePopup(MenuElement[] param1ArrayOfMenuElement) {
      for (int i = param1ArrayOfMenuElement.length - 1; i >= 0; i--) {
        MenuElement menuElement = param1ArrayOfMenuElement[i];
        if (menuElement instanceof JPopupMenu)
          return (JPopupMenu)menuElement; 
      } 
      return null;
    }
    
    void addUIInputMap(JComponent param1JComponent, InputMap param1InputMap) {
      InputMap inputMap1 = null;
      InputMap inputMap2;
      for (inputMap2 = param1JComponent.getInputMap(2); inputMap2 != null && !(inputMap2 instanceof javax.swing.plaf.UIResource); inputMap2 = inputMap2.getParent())
        inputMap1 = inputMap2; 
      if (inputMap1 == null) {
        param1JComponent.setInputMap(2, param1InputMap);
      } else {
        inputMap1.setParent(param1InputMap);
      } 
      param1InputMap.setParent(inputMap2);
    }
    
    void addUIActionMap(JComponent param1JComponent, ActionMap param1ActionMap) {
      ActionMap actionMap1 = null;
      ActionMap actionMap2;
      for (actionMap2 = param1JComponent.getActionMap(); actionMap2 != null && !(actionMap2 instanceof javax.swing.plaf.UIResource); actionMap2 = actionMap2.getParent())
        actionMap1 = actionMap2; 
      if (actionMap1 == null) {
        param1JComponent.setActionMap(param1ActionMap);
      } else {
        actionMap1.setParent(param1ActionMap);
      } 
      param1ActionMap.setParent(actionMap2);
    }
    
    void removeUIInputMap(JComponent param1JComponent, InputMap param1InputMap) {
      InputMap inputMap1 = null;
      for (InputMap inputMap2 = param1JComponent.getInputMap(2); inputMap2 != null; inputMap2 = inputMap2.getParent()) {
        if (inputMap2 == param1InputMap) {
          if (inputMap1 == null) {
            param1JComponent.setInputMap(2, param1InputMap.getParent());
            break;
          } 
          inputMap1.setParent(param1InputMap.getParent());
          break;
        } 
        inputMap1 = inputMap2;
      } 
    }
    
    void removeUIActionMap(JComponent param1JComponent, ActionMap param1ActionMap) {
      ActionMap actionMap1 = null;
      for (ActionMap actionMap2 = param1JComponent.getActionMap(); actionMap2 != null; actionMap2 = actionMap2.getParent()) {
        if (actionMap2 == param1ActionMap) {
          if (actionMap1 == null) {
            param1JComponent.setActionMap(param1ActionMap.getParent());
            break;
          } 
          actionMap1.setParent(param1ActionMap.getParent());
          break;
        } 
        actionMap1 = actionMap2;
      } 
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      if (!(UIManager.getLookAndFeel() instanceof BasicLookAndFeel)) {
        uninstall();
        return;
      } 
      MenuSelectionManager menuSelectionManager = (MenuSelectionManager)param1ChangeEvent.getSource();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      JPopupMenu jPopupMenu = getActivePopup(arrayOfMenuElement);
      if (jPopupMenu != null && !jPopupMenu.isFocusable())
        return; 
      if (this.lastPathSelected.length != 0 && arrayOfMenuElement.length != 0 && !BasicPopupMenuUI.checkInvokerEqual(arrayOfMenuElement[0], this.lastPathSelected[0])) {
        removeItems();
        this.lastPathSelected = new MenuElement[0];
      } 
      if (this.lastPathSelected.length == 0 && arrayOfMenuElement.length > 0) {
        JComponent jComponent;
        if (jPopupMenu == null) {
          if (arrayOfMenuElement.length == 2 && arrayOfMenuElement[0] instanceof javax.swing.JMenuBar && arrayOfMenuElement[1] instanceof JMenu) {
            jComponent = (JComponent)arrayOfMenuElement[1];
            jPopupMenu = ((JMenu)jComponent).getPopupMenu();
          } else {
            return;
          } 
        } else {
          Component component = jPopupMenu.getInvoker();
          if (component instanceof JFrame) {
            jComponent = ((JFrame)component).getRootPane();
          } else if (component instanceof JDialog) {
            jComponent = ((JDialog)component).getRootPane();
          } else if (component instanceof JApplet) {
            jComponent = ((JApplet)component).getRootPane();
          } else {
            while (!(component instanceof JComponent)) {
              if (component == null)
                return; 
              component = component.getParent();
            } 
            jComponent = (JComponent)component;
          } 
        } 
        this.lastFocused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        this.invokerRootPane = SwingUtilities.getRootPane(jComponent);
        if (this.invokerRootPane != null) {
          this.invokerRootPane.addFocusListener(this.rootPaneFocusListener);
          this.invokerRootPane.requestFocus(true);
          this.invokerRootPane.addKeyListener(this);
          this.focusTraversalKeysEnabled = this.invokerRootPane.getFocusTraversalKeysEnabled();
          this.invokerRootPane.setFocusTraversalKeysEnabled(false);
          this.menuInputMap = BasicPopupMenuUI.getInputMap(jPopupMenu, this.invokerRootPane);
          addUIInputMap(this.invokerRootPane, this.menuInputMap);
          addUIActionMap(this.invokerRootPane, this.menuActionMap);
        } 
      } else if (this.lastPathSelected.length != 0 && arrayOfMenuElement.length == 0) {
        removeItems();
      } else if (jPopupMenu != this.lastPopup) {
        this.receivedKeyPressed = false;
      } 
      this.lastPathSelected = arrayOfMenuElement;
      this.lastPopup = jPopupMenu;
    }
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      this.receivedKeyPressed = true;
      MenuSelectionManager.defaultManager().processKeyEvent(param1KeyEvent);
    }
    
    public void keyReleased(KeyEvent param1KeyEvent) {
      if (this.receivedKeyPressed) {
        this.receivedKeyPressed = false;
        MenuSelectionManager.defaultManager().processKeyEvent(param1KeyEvent);
      } 
    }
    
    public void keyTyped(KeyEvent param1KeyEvent) {
      if (this.receivedKeyPressed)
        MenuSelectionManager.defaultManager().processKeyEvent(param1KeyEvent); 
    }
    
    void uninstall() {
      synchronized (BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY) {
        MenuSelectionManager.defaultManager().removeChangeListener(this);
        AppContext.getAppContext().remove(BasicPopupMenuUI.MENU_KEYBOARD_HELPER_KEY);
      } 
    }
  }
  
  static class MouseGrabber implements ChangeListener, AWTEventListener, ComponentListener, WindowListener {
    Window grabbedWindow;
    
    MenuElement[] lastPathSelected;
    
    public MouseGrabber() {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      menuSelectionManager.addChangeListener(this);
      this.lastPathSelected = menuSelectionManager.getSelectedPath();
      if (this.lastPathSelected.length != 0)
        grabWindow(this.lastPathSelected); 
    }
    
    void uninstall() {
      synchronized (BasicPopupMenuUI.MOUSE_GRABBER_KEY) {
        MenuSelectionManager.defaultManager().removeChangeListener(this);
        ungrabWindow();
        AppContext.getAppContext().remove(BasicPopupMenuUI.MOUSE_GRABBER_KEY);
      } 
    }
    
    void grabWindow(MenuElement[] param1ArrayOfMenuElement) {
      final Toolkit tk = Toolkit.getDefaultToolkit();
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
              tk.addAWTEventListener(BasicPopupMenuUI.MouseGrabber.this, -2147352464L);
              return null;
            }
          });
      Component component = param1ArrayOfMenuElement[0].getComponent();
      if (component instanceof JPopupMenu)
        component = ((JPopupMenu)component).getInvoker(); 
      this.grabbedWindow = (component instanceof Window) ? (Window)component : SwingUtilities.getWindowAncestor(component);
      if (this.grabbedWindow != null)
        if (toolkit instanceof SunToolkit) {
          ((SunToolkit)toolkit).grab(this.grabbedWindow);
        } else {
          this.grabbedWindow.addComponentListener(this);
          this.grabbedWindow.addWindowListener(this);
        }  
    }
    
    void ungrabWindow() {
      final Toolkit tk = Toolkit.getDefaultToolkit();
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
              tk.removeAWTEventListener(BasicPopupMenuUI.MouseGrabber.this);
              return null;
            }
          });
      realUngrabWindow();
    }
    
    void realUngrabWindow() {
      Toolkit toolkit = Toolkit.getDefaultToolkit();
      if (this.grabbedWindow != null) {
        if (toolkit instanceof SunToolkit) {
          ((SunToolkit)toolkit).ungrab(this.grabbedWindow);
        } else {
          this.grabbedWindow.removeComponentListener(this);
          this.grabbedWindow.removeWindowListener(this);
        } 
        this.grabbedWindow = null;
      } 
    }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      MenuElement[] arrayOfMenuElement = menuSelectionManager.getSelectedPath();
      if (this.lastPathSelected.length == 0 && arrayOfMenuElement.length != 0)
        grabWindow(arrayOfMenuElement); 
      if (this.lastPathSelected.length != 0 && arrayOfMenuElement.length == 0)
        ungrabWindow(); 
      this.lastPathSelected = arrayOfMenuElement;
    }
    
    public void eventDispatched(AWTEvent param1AWTEvent) {
      if (param1AWTEvent instanceof sun.awt.UngrabEvent) {
        cancelPopupMenu();
        return;
      } 
      if (!(param1AWTEvent instanceof MouseEvent))
        return; 
      MouseEvent mouseEvent = (MouseEvent)param1AWTEvent;
      Component component = mouseEvent.getComponent();
      switch (mouseEvent.getID()) {
        case 501:
          if (isInPopup(component) || (component instanceof JMenu && ((JMenu)component).isSelected()))
            return; 
          if (!(component instanceof JComponent) || ((JComponent)component).getClientProperty("doNotCancelPopup") != BasicComboBoxUI.HIDE_POPUP_KEY) {
            cancelPopupMenu();
            boolean bool = UIManager.getBoolean("PopupMenu.consumeEventOnClose");
            if (bool && !(component instanceof MenuElement))
              mouseEvent.consume(); 
          } 
          break;
        case 502:
          if ((component instanceof MenuElement || !isInPopup(component)) && (component instanceof JMenu || !(component instanceof JMenuItem)))
            MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent); 
          break;
        case 506:
          if (!(component instanceof MenuElement) && isInPopup(component))
            break; 
          MenuSelectionManager.defaultManager().processMouseEvent(mouseEvent);
          break;
        case 507:
          if (isInPopup(component) || (component instanceof JComboBox && ((JComboBox)component).isPopupVisible()))
            return; 
          cancelPopupMenu();
          break;
      } 
    }
    
    boolean isInPopup(Component param1Component) {
      for (Component component = param1Component; component != null && !(component instanceof java.applet.Applet) && !(component instanceof Window); component = component.getParent()) {
        if (component instanceof JPopupMenu)
          return true; 
      } 
      return false;
    }
    
    void cancelPopupMenu() {
      try {
        List list = BasicPopupMenuUI.getPopups();
        for (JPopupMenu jPopupMenu : list)
          jPopupMenu.putClientProperty("JPopupMenu.firePopupMenuCanceled", Boolean.TRUE); 
        MenuSelectionManager.defaultManager().clearSelectedPath();
      } catch (RuntimeException runtimeException) {
        realUngrabWindow();
        throw runtimeException;
      } catch (Error error) {
        realUngrabWindow();
        throw error;
      } 
    }
    
    public void componentResized(ComponentEvent param1ComponentEvent) { cancelPopupMenu(); }
    
    public void componentMoved(ComponentEvent param1ComponentEvent) { cancelPopupMenu(); }
    
    public void componentShown(ComponentEvent param1ComponentEvent) { cancelPopupMenu(); }
    
    public void componentHidden(ComponentEvent param1ComponentEvent) { cancelPopupMenu(); }
    
    public void windowClosing(WindowEvent param1WindowEvent) { cancelPopupMenu(); }
    
    public void windowClosed(WindowEvent param1WindowEvent) { cancelPopupMenu(); }
    
    public void windowIconified(WindowEvent param1WindowEvent) { cancelPopupMenu(); }
    
    public void windowDeactivated(WindowEvent param1WindowEvent) { cancelPopupMenu(); }
    
    public void windowOpened(WindowEvent param1WindowEvent) {}
    
    public void windowDeiconified(WindowEvent param1WindowEvent) {}
    
    public void windowActivated(WindowEvent param1WindowEvent) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicPopupMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */