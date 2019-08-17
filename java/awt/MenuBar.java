package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuBarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;

public class MenuBar extends MenuComponent implements MenuContainer, Accessible {
  Vector<Menu> menus = new Vector();
  
  Menu helpMenu;
  
  private static final String base = "menubar";
  
  private static int nameCounter;
  
  private static final long serialVersionUID = -4930327919388951260L;
  
  private int menuBarSerializedDataVersion = 1;
  
  String constructComponentName() {
    synchronized (MenuBar.class) {
      return "menubar" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = Toolkit.getDefaultToolkit().createMenuBar(this); 
      int i = getMenuCount();
      for (byte b = 0; b < i; b++)
        getMenu(b).addNotify(); 
    } 
  }
  
  public void removeNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      int i = getMenuCount();
      for (byte b = 0; b < i; b++)
        getMenu(b).removeNotify(); 
      super.removeNotify();
    } 
  }
  
  public Menu getHelpMenu() { return this.helpMenu; }
  
  public void setHelpMenu(Menu paramMenu) {
    synchronized (getTreeLock()) {
      if (this.helpMenu == paramMenu)
        return; 
      if (this.helpMenu != null)
        remove(this.helpMenu); 
      this.helpMenu = paramMenu;
      if (paramMenu != null) {
        if (paramMenu.parent != this)
          add(paramMenu); 
        paramMenu.isHelpMenu = true;
        paramMenu.parent = this;
        MenuBarPeer menuBarPeer = (MenuBarPeer)this.peer;
        if (menuBarPeer != null) {
          if (paramMenu.peer == null)
            paramMenu.addNotify(); 
          menuBarPeer.addHelpMenu(paramMenu);
        } 
      } 
    } 
  }
  
  public Menu add(Menu paramMenu) {
    synchronized (getTreeLock()) {
      if (paramMenu.parent != null)
        paramMenu.parent.remove(paramMenu); 
      paramMenu.parent = this;
      MenuBarPeer menuBarPeer = (MenuBarPeer)this.peer;
      if (menuBarPeer != null) {
        if (paramMenu.peer == null)
          paramMenu.addNotify(); 
        this.menus.addElement(paramMenu);
        menuBarPeer.addMenu(paramMenu);
      } else {
        this.menus.addElement(paramMenu);
      } 
      return paramMenu;
    } 
  }
  
  public void remove(int paramInt) {
    synchronized (getTreeLock()) {
      Menu menu = getMenu(paramInt);
      this.menus.removeElementAt(paramInt);
      MenuBarPeer menuBarPeer = (MenuBarPeer)this.peer;
      if (menuBarPeer != null) {
        menuBarPeer.delMenu(paramInt);
        menu.removeNotify();
        menu.parent = null;
      } 
      if (this.helpMenu == menu) {
        this.helpMenu = null;
        menu.isHelpMenu = false;
      } 
    } 
  }
  
  public void remove(MenuComponent paramMenuComponent) {
    synchronized (getTreeLock()) {
      int i = this.menus.indexOf(paramMenuComponent);
      if (i >= 0)
        remove(i); 
    } 
  }
  
  public int getMenuCount() { return countMenus(); }
  
  @Deprecated
  public int countMenus() { return getMenuCountImpl(); }
  
  final int getMenuCountImpl() { return this.menus.size(); }
  
  public Menu getMenu(int paramInt) { return getMenuImpl(paramInt); }
  
  final Menu getMenuImpl(int paramInt) { return (Menu)this.menus.elementAt(paramInt); }
  
  public Enumeration<MenuShortcut> shortcuts() {
    Vector vector = new Vector();
    int i = getMenuCount();
    for (byte b = 0; b < i; b++) {
      Enumeration enumeration = getMenu(b).shortcuts();
      while (enumeration.hasMoreElements())
        vector.addElement(enumeration.nextElement()); 
    } 
    return vector.elements();
  }
  
  public MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut) {
    int i = getMenuCount();
    for (byte b = 0; b < i; b++) {
      MenuItem menuItem = getMenu(b).getShortcutMenuItem(paramMenuShortcut);
      if (menuItem != null)
        return menuItem; 
    } 
    return null;
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent) {
    int i = paramKeyEvent.getID();
    if (i != 401 && i != 402)
      return false; 
    int j = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    if ((paramKeyEvent.getModifiers() & j) == 0)
      return false; 
    int k = getMenuCount();
    for (byte b = 0; b < k; b++) {
      Menu menu = getMenu(b);
      if (menu.handleShortcut(paramKeyEvent))
        return true; 
    } 
    return false;
  }
  
  public void deleteShortcut(MenuShortcut paramMenuShortcut) {
    int i = getMenuCount();
    for (byte b = 0; b < i; b++)
      getMenu(b).deleteShortcut(paramMenuShortcut); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws ClassNotFoundException, IOException { paramObjectOutputStream.defaultWriteObject(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    paramObjectInputStream.defaultReadObject();
    for (byte b = 0; b < this.menus.size(); b++) {
      Menu menu = (Menu)this.menus.elementAt(b);
      menu.parent = this;
    } 
  }
  
  private static native void initIDs() throws HeadlessException;
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTMenuBar(); 
    return this.accessibleContext;
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent) { return this.menus.indexOf(paramMenuComponent); }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setMenuBarAccessor(new AWTAccessor.MenuBarAccessor() {
          public Menu getHelpMenu(MenuBar param1MenuBar) { return param1MenuBar.helpMenu; }
          
          public Vector<Menu> getMenus(MenuBar param1MenuBar) { return param1MenuBar.menus; }
        });
    nameCounter = 0;
  }
  
  protected class AccessibleAWTMenuBar extends MenuComponent.AccessibleAWTMenuComponent {
    private static final long serialVersionUID = -8577604491830083815L;
    
    protected AccessibleAWTMenuBar() { super(MenuBar.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.MENU_BAR; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MenuBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */