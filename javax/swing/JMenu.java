package javax.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.PopupMenuUI;

public class JMenu extends JMenuItem implements Accessible, MenuElement {
  private static final String uiClassID = "MenuUI";
  
  private JPopupMenu popupMenu;
  
  private ChangeListener menuChangeListener = null;
  
  private MenuEvent menuEvent = null;
  
  private int delay;
  
  private Point customMenuLocation = null;
  
  private static final boolean TRACE = false;
  
  private static final boolean VERBOSE = false;
  
  private static final boolean DEBUG = false;
  
  protected WinListener popupListener;
  
  public JMenu() { this(""); }
  
  public JMenu(String paramString) { super(paramString); }
  
  public JMenu(Action paramAction) {
    this();
    setAction(paramAction);
  }
  
  public JMenu(String paramString, boolean paramBoolean) { this(paramString); }
  
  void initFocusability() {}
  
  public void updateUI() {
    setUI((MenuItemUI)UIManager.getUI(this));
    if (this.popupMenu != null)
      this.popupMenu.setUI((PopupMenuUI)UIManager.getUI(this.popupMenu)); 
  }
  
  public String getUIClassID() { return "MenuUI"; }
  
  public void setModel(ButtonModel paramButtonModel) {
    ButtonModel buttonModel = getModel();
    super.setModel(paramButtonModel);
    if (buttonModel != null && this.menuChangeListener != null) {
      buttonModel.removeChangeListener(this.menuChangeListener);
      this.menuChangeListener = null;
    } 
    this.model = paramButtonModel;
    if (paramButtonModel != null) {
      this.menuChangeListener = createMenuChangeListener();
      paramButtonModel.addChangeListener(this.menuChangeListener);
    } 
  }
  
  public boolean isSelected() { return getModel().isSelected(); }
  
  public void setSelected(boolean paramBoolean) {
    ButtonModel buttonModel = getModel();
    boolean bool = buttonModel.isSelected();
    if (paramBoolean != buttonModel.isSelected())
      getModel().setSelected(paramBoolean); 
  }
  
  public boolean isPopupMenuVisible() {
    ensurePopupMenuCreated();
    return this.popupMenu.isVisible();
  }
  
  public void setPopupMenuVisible(boolean paramBoolean) {
    boolean bool = isPopupMenuVisible();
    if (paramBoolean != bool && (isEnabled() || !paramBoolean)) {
      ensurePopupMenuCreated();
      if (paramBoolean == true && isShowing()) {
        Point point = getCustomMenuLocation();
        if (point == null)
          point = getPopupMenuOrigin(); 
        getPopupMenu().show(this, point.x, point.y);
      } else {
        getPopupMenu().setVisible(false);
      } 
    } 
  }
  
  protected Point getPopupMenuOrigin() {
    int j;
    int i;
    JPopupMenu jPopupMenu = getPopupMenu();
    Dimension dimension1 = getSize();
    Dimension dimension2 = jPopupMenu.getSize();
    if (dimension2.width == 0)
      dimension2 = jPopupMenu.getPreferredSize(); 
    Point point = getLocationOnScreen();
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    GraphicsConfiguration graphicsConfiguration = getGraphicsConfiguration();
    Rectangle rectangle = new Rectangle(toolkit.getScreenSize());
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice = graphicsEnvironment.getScreenDevices();
    for (byte b = 0; b < arrayOfGraphicsDevice.length; b++) {
      if (arrayOfGraphicsDevice[b].getType() == 0) {
        GraphicsConfiguration graphicsConfiguration1 = arrayOfGraphicsDevice[b].getDefaultConfiguration();
        if (graphicsConfiguration1.getBounds().contains(point)) {
          graphicsConfiguration = graphicsConfiguration1;
          break;
        } 
      } 
    } 
    if (graphicsConfiguration != null) {
      rectangle = graphicsConfiguration.getBounds();
      Insets insets = toolkit.getScreenInsets(graphicsConfiguration);
      rectangle.width -= Math.abs(insets.left + insets.right);
      rectangle.height -= Math.abs(insets.top + insets.bottom);
      point.x -= Math.abs(insets.left);
      point.y -= Math.abs(insets.top);
    } 
    Container container = getParent();
    if (container instanceof JPopupMenu) {
      int k = UIManager.getInt("Menu.submenuPopupOffsetX");
      int m = UIManager.getInt("Menu.submenuPopupOffsetY");
      if (SwingUtilities.isLeftToRight(this)) {
        i = dimension1.width + k;
        if (point.x + i + dimension2.width >= rectangle.width + rectangle.x && rectangle.width - dimension1.width < 2 * (point.x - rectangle.x))
          i = 0 - k - dimension2.width; 
      } else {
        i = 0 - k - dimension2.width;
        if (point.x + i < rectangle.x && rectangle.width - dimension1.width > 2 * (point.x - rectangle.x))
          i = dimension1.width + k; 
      } 
      j = m;
      if (point.y + j + dimension2.height >= rectangle.height + rectangle.y && rectangle.height - dimension1.height < 2 * (point.y - rectangle.y))
        j = dimension1.height - m - dimension2.height; 
    } else {
      int k = UIManager.getInt("Menu.menuPopupOffsetX");
      int m = UIManager.getInt("Menu.menuPopupOffsetY");
      if (SwingUtilities.isLeftToRight(this)) {
        i = k;
        if (point.x + i + dimension2.width >= rectangle.width + rectangle.x && rectangle.width - dimension1.width < 2 * (point.x - rectangle.x))
          i = dimension1.width - k - dimension2.width; 
      } else {
        i = dimension1.width - k - dimension2.width;
        if (point.x + i < rectangle.x && rectangle.width - dimension1.width > 2 * (point.x - rectangle.x))
          i = k; 
      } 
      j = dimension1.height + m;
      if (point.y + j + dimension2.height >= rectangle.height + rectangle.y && rectangle.height - dimension1.height < 2 * (point.y - rectangle.y))
        j = 0 - m - dimension2.height; 
    } 
    return new Point(i, j);
  }
  
  public int getDelay() { return this.delay; }
  
  public void setDelay(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Delay must be a positive integer"); 
    this.delay = paramInt;
  }
  
  private void ensurePopupMenuCreated() {
    if (this.popupMenu == null) {
      JMenu jMenu = this;
      this.popupMenu = new JPopupMenu();
      this.popupMenu.setInvoker(this);
      this.popupListener = createWinListener(this.popupMenu);
    } 
  }
  
  private Point getCustomMenuLocation() { return this.customMenuLocation; }
  
  public void setMenuLocation(int paramInt1, int paramInt2) {
    this.customMenuLocation = new Point(paramInt1, paramInt2);
    if (this.popupMenu != null)
      this.popupMenu.setLocation(paramInt1, paramInt2); 
  }
  
  public JMenuItem add(JMenuItem paramJMenuItem) {
    ensurePopupMenuCreated();
    return this.popupMenu.add(paramJMenuItem);
  }
  
  public Component add(Component paramComponent) {
    ensurePopupMenuCreated();
    this.popupMenu.add(paramComponent);
    return paramComponent;
  }
  
  public Component add(Component paramComponent, int paramInt) {
    ensurePopupMenuCreated();
    this.popupMenu.add(paramComponent, paramInt);
    return paramComponent;
  }
  
  public JMenuItem add(String paramString) { return add(new JMenuItem(paramString)); }
  
  public JMenuItem add(Action paramAction) {
    JMenuItem jMenuItem = createActionComponent(paramAction);
    jMenuItem.setAction(paramAction);
    add(jMenuItem);
    return jMenuItem;
  }
  
  protected JMenuItem createActionComponent(Action paramAction) {
    JMenuItem jMenuItem = new JMenuItem() {
        protected PropertyChangeListener createActionPropertyChangeListener(Action param1Action) {
          PropertyChangeListener propertyChangeListener = JMenu.this.createActionChangeListener(this);
          if (propertyChangeListener == null)
            propertyChangeListener = super.createActionPropertyChangeListener(param1Action); 
          return propertyChangeListener;
        }
      };
    jMenuItem.setHorizontalTextPosition(11);
    jMenuItem.setVerticalTextPosition(0);
    return jMenuItem;
  }
  
  protected PropertyChangeListener createActionChangeListener(JMenuItem paramJMenuItem) { return paramJMenuItem.createActionPropertyChangeListener0(paramJMenuItem.getAction()); }
  
  public void addSeparator() {
    ensurePopupMenuCreated();
    this.popupMenu.addSeparator();
  }
  
  public void insert(String paramString, int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    ensurePopupMenuCreated();
    this.popupMenu.insert(new JMenuItem(paramString), paramInt);
  }
  
  public JMenuItem insert(JMenuItem paramJMenuItem, int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    ensurePopupMenuCreated();
    this.popupMenu.insert(paramJMenuItem, paramInt);
    return paramJMenuItem;
  }
  
  public JMenuItem insert(Action paramAction, int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    ensurePopupMenuCreated();
    JMenuItem jMenuItem = new JMenuItem(paramAction);
    jMenuItem.setHorizontalTextPosition(11);
    jMenuItem.setVerticalTextPosition(0);
    this.popupMenu.insert(jMenuItem, paramInt);
    return jMenuItem;
  }
  
  public void insertSeparator(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    ensurePopupMenuCreated();
    this.popupMenu.insert(new JPopupMenu.Separator(), paramInt);
  }
  
  public JMenuItem getItem(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    Component component = getMenuComponent(paramInt);
    return (component instanceof JMenuItem) ? (JMenuItem)component : null;
  }
  
  public int getItemCount() { return getMenuComponentCount(); }
  
  public boolean isTearOff() { throw new Error("boolean isTearOff() {} not yet implemented"); }
  
  public void remove(JMenuItem paramJMenuItem) {
    if (this.popupMenu != null)
      this.popupMenu.remove(paramJMenuItem); 
  }
  
  public void remove(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("index less than zero."); 
    if (paramInt > getItemCount())
      throw new IllegalArgumentException("index greater than the number of items."); 
    if (this.popupMenu != null)
      this.popupMenu.remove(paramInt); 
  }
  
  public void remove(Component paramComponent) {
    if (this.popupMenu != null)
      this.popupMenu.remove(paramComponent); 
  }
  
  public void removeAll() {
    if (this.popupMenu != null)
      this.popupMenu.removeAll(); 
  }
  
  public int getMenuComponentCount() {
    int i = 0;
    if (this.popupMenu != null)
      i = this.popupMenu.getComponentCount(); 
    return i;
  }
  
  public Component getMenuComponent(int paramInt) { return (this.popupMenu != null) ? this.popupMenu.getComponent(paramInt) : null; }
  
  public Component[] getMenuComponents() { return (this.popupMenu != null) ? this.popupMenu.getComponents() : new Component[0]; }
  
  public boolean isTopLevelMenu() { return getParent() instanceof JMenuBar; }
  
  public boolean isMenuComponent(Component paramComponent) {
    if (paramComponent == this)
      return true; 
    if (paramComponent instanceof JPopupMenu) {
      JPopupMenu jPopupMenu = (JPopupMenu)paramComponent;
      if (jPopupMenu == getPopupMenu())
        return true; 
    } 
    int i = getMenuComponentCount();
    Component[] arrayOfComponent = getMenuComponents();
    for (byte b = 0; b < i; b++) {
      Component component = arrayOfComponent[b];
      if (component == paramComponent)
        return true; 
      if (component instanceof JMenu) {
        JMenu jMenu = (JMenu)component;
        if (jMenu.isMenuComponent(paramComponent))
          return true; 
      } 
    } 
    return false;
  }
  
  private Point translateToPopupMenu(Point paramPoint) { return translateToPopupMenu(paramPoint.x, paramPoint.y); }
  
  private Point translateToPopupMenu(int paramInt1, int paramInt2) {
    int j;
    int i;
    if (getParent() instanceof JPopupMenu) {
      i = paramInt1 - (getSize()).width;
      j = paramInt2;
    } else {
      i = paramInt1;
      j = paramInt2 - (getSize()).height;
    } 
    return new Point(i, j);
  }
  
  public JPopupMenu getPopupMenu() {
    ensurePopupMenuCreated();
    return this.popupMenu;
  }
  
  public void addMenuListener(MenuListener paramMenuListener) { this.listenerList.add(MenuListener.class, paramMenuListener); }
  
  public void removeMenuListener(MenuListener paramMenuListener) { this.listenerList.remove(MenuListener.class, paramMenuListener); }
  
  public MenuListener[] getMenuListeners() { return (MenuListener[])this.listenerList.getListeners(MenuListener.class); }
  
  protected void fireMenuSelected() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuListener.class) {
        if (arrayOfObject[i + true] == null)
          throw new Error(getText() + " has a NULL Listener!! " + i); 
        if (this.menuEvent == null)
          this.menuEvent = new MenuEvent(this); 
        ((MenuListener)arrayOfObject[i + 1]).menuSelected(this.menuEvent);
      } 
    } 
  }
  
  protected void fireMenuDeselected() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuListener.class) {
        if (arrayOfObject[i + true] == null)
          throw new Error(getText() + " has a NULL Listener!! " + i); 
        if (this.menuEvent == null)
          this.menuEvent = new MenuEvent(this); 
        ((MenuListener)arrayOfObject[i + 1]).menuDeselected(this.menuEvent);
      } 
    } 
  }
  
  protected void fireMenuCanceled() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == MenuListener.class) {
        if (arrayOfObject[i + true] == null)
          throw new Error(getText() + " has a NULL Listener!! " + i); 
        if (this.menuEvent == null)
          this.menuEvent = new MenuEvent(this); 
        ((MenuListener)arrayOfObject[i + 1]).menuCanceled(this.menuEvent);
      } 
    } 
  }
  
  void configureAcceleratorFromAction(Action paramAction) {}
  
  private ChangeListener createMenuChangeListener() { return new MenuChangeListener(); }
  
  protected WinListener createWinListener(JPopupMenu paramJPopupMenu) { return new WinListener(paramJPopupMenu); }
  
  public void menuSelectionChanged(boolean paramBoolean) { setSelected(paramBoolean); }
  
  public MenuElement[] getSubElements() {
    if (this.popupMenu == null)
      return new MenuElement[0]; 
    MenuElement[] arrayOfMenuElement = new MenuElement[1];
    arrayOfMenuElement[0] = this.popupMenu;
    return arrayOfMenuElement;
  }
  
  public Component getComponent() { return this; }
  
  public void applyComponentOrientation(ComponentOrientation paramComponentOrientation) {
    super.applyComponentOrientation(paramComponentOrientation);
    if (this.popupMenu != null) {
      int i = getMenuComponentCount();
      for (byte b = 0; b < i; b++)
        getMenuComponent(b).applyComponentOrientation(paramComponentOrientation); 
      this.popupMenu.setComponentOrientation(paramComponentOrientation);
    } 
  }
  
  public void setComponentOrientation(ComponentOrientation paramComponentOrientation) {
    super.setComponentOrientation(paramComponentOrientation);
    if (this.popupMenu != null)
      this.popupMenu.setComponentOrientation(paramComponentOrientation); 
  }
  
  public void setAccelerator(KeyStroke paramKeyStroke) { throw new Error("setAccelerator() is not defined for JMenu.  Use setMnemonic() instead."); }
  
  protected void processKeyEvent(KeyEvent paramKeyEvent) {
    MenuSelectionManager.defaultManager().processKeyEvent(paramKeyEvent);
    if (paramKeyEvent.isConsumed())
      return; 
    super.processKeyEvent(paramKeyEvent);
  }
  
  public void doClick(int paramInt) {
    MenuElement[] arrayOfMenuElement = buildMenuElementArray(this);
    MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
  }
  
  private MenuElement[] buildMenuElementArray(JMenu paramJMenu) {
    Vector vector = new Vector();
    Component component = paramJMenu.getPopupMenu();
    while (true) {
      while (component instanceof JPopupMenu) {
        JPopupMenu jPopupMenu = (JPopupMenu)component;
        vector.insertElementAt(jPopupMenu, 0);
        component = jPopupMenu.getInvoker();
      } 
      if (component instanceof JMenu) {
        JMenu jMenu = (JMenu)component;
        vector.insertElementAt(jMenu, 0);
        component = jMenu.getParent();
        continue;
      } 
      if (component instanceof JMenuBar)
        break; 
    } 
    JMenuBar jMenuBar = (JMenuBar)component;
    vector.insertElementAt(jMenuBar, 0);
    MenuElement[] arrayOfMenuElement = new MenuElement[vector.size()];
    vector.copyInto(arrayOfMenuElement);
    return arrayOfMenuElement;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("MenuUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() { return super.paramString(); }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJMenu(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJMenu extends JMenuItem.AccessibleJMenuItem implements AccessibleSelection {
    protected AccessibleJMenu() { super(JMenu.this); }
    
    public int getAccessibleChildrenCount() {
      Component[] arrayOfComponent = JMenu.this.getMenuComponents();
      byte b = 0;
      for (Component component : arrayOfComponent) {
        if (component instanceof Accessible)
          b++; 
      } 
      return b;
    }
    
    public Accessible getAccessibleChild(int param1Int) {
      Component[] arrayOfComponent = JMenu.this.getMenuComponents();
      byte b = 0;
      for (Component component : arrayOfComponent) {
        if (component instanceof Accessible) {
          if (b == param1Int) {
            if (component instanceof JComponent) {
              AccessibleContext accessibleContext = component.getAccessibleContext();
              accessibleContext.setAccessibleParent(JMenu.this);
            } 
            return (Accessible)component;
          } 
          b++;
        } 
      } 
      return null;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.MENU; }
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public int getAccessibleSelectionCount() {
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement != null)
        for (byte b = 0; b < arrayOfMenuElement.length; b++) {
          if (arrayOfMenuElement[b] == JMenu.this && b + true < arrayOfMenuElement.length)
            return 1; 
        }  
      return 0;
    }
    
    public Accessible getAccessibleSelection(int param1Int) {
      if (param1Int < 0 || param1Int >= JMenu.this.getItemCount())
        return null; 
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement != null)
        for (byte b = 0; b < arrayOfMenuElement.length; b++) {
          if (arrayOfMenuElement[b] == JMenu.this)
            while (++b < arrayOfMenuElement.length) {
              if (arrayOfMenuElement[b] instanceof JMenuItem)
                return (Accessible)arrayOfMenuElement[b]; 
            }  
        }  
      return null;
    }
    
    public boolean isAccessibleChildSelected(int param1Int) {
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement != null) {
        JMenuItem jMenuItem = JMenu.this.getItem(param1Int);
        for (byte b = 0; b < arrayOfMenuElement.length; b++) {
          if (arrayOfMenuElement[b] == jMenuItem)
            return true; 
        } 
      } 
      return false;
    }
    
    public void addAccessibleSelection(int param1Int) {
      if (param1Int < 0 || param1Int >= JMenu.this.getItemCount())
        return; 
      JMenuItem jMenuItem = JMenu.this.getItem(param1Int);
      if (jMenuItem != null)
        if (jMenuItem instanceof JMenu) {
          MenuElement[] arrayOfMenuElement = JMenu.this.buildMenuElementArray((JMenu)jMenuItem);
          MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement);
        } else {
          MenuSelectionManager.defaultManager().setSelectedPath(null);
        }  
    }
    
    public void removeAccessibleSelection(int param1Int) {
      if (param1Int < 0 || param1Int >= JMenu.this.getItemCount())
        return; 
      JMenuItem jMenuItem = JMenu.this.getItem(param1Int);
      if (jMenuItem != null && jMenuItem instanceof JMenu && jMenuItem.isSelected()) {
        MenuElement[] arrayOfMenuElement1 = MenuSelectionManager.defaultManager().getSelectedPath();
        MenuElement[] arrayOfMenuElement2 = new MenuElement[arrayOfMenuElement1.length - 2];
        for (byte b = 0; b < arrayOfMenuElement1.length - 2; b++)
          arrayOfMenuElement2[b] = arrayOfMenuElement1[b]; 
        MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement2);
      } 
    }
    
    public void clearAccessibleSelection() {
      MenuElement[] arrayOfMenuElement = MenuSelectionManager.defaultManager().getSelectedPath();
      if (arrayOfMenuElement != null)
        for (byte b = 0; b < arrayOfMenuElement.length; b++) {
          if (arrayOfMenuElement[b] == JMenu.this) {
            MenuElement[] arrayOfMenuElement1 = new MenuElement[b + true];
            System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement1, 0, b);
            arrayOfMenuElement1[b] = JMenu.this.getPopupMenu();
            MenuSelectionManager.defaultManager().setSelectedPath(arrayOfMenuElement1);
          } 
        }  
    }
    
    public void selectAllAccessibleSelection() {}
  }
  
  class MenuChangeListener implements ChangeListener, Serializable {
    boolean isSelected = false;
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      ButtonModel buttonModel = (ButtonModel)param1ChangeEvent.getSource();
      boolean bool = buttonModel.isSelected();
      if (bool != this.isSelected) {
        if (bool == true) {
          JMenu.this.fireMenuSelected();
        } else {
          JMenu.this.fireMenuDeselected();
        } 
        this.isSelected = bool;
      } 
    }
  }
  
  protected class WinListener extends WindowAdapter implements Serializable {
    JPopupMenu popupMenu;
    
    public WinListener(JPopupMenu param1JPopupMenu) { this.popupMenu = param1JPopupMenu; }
    
    public void windowClosing(WindowEvent param1WindowEvent) { JMenu.this.setSelected(false); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */