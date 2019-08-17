package java.awt;

import java.awt.event.KeyEvent;
import java.awt.peer.MenuPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;

public class Menu extends MenuItem implements MenuContainer, Accessible {
  Vector<MenuComponent> items = new Vector();
  
  boolean tearOff;
  
  boolean isHelpMenu;
  
  private static final String base = "menu";
  
  private static int nameCounter;
  
  private static final long serialVersionUID = -8809584163345499784L;
  
  private int menuSerializedDataVersion = 1;
  
  public Menu() throws HeadlessException { this("", false); }
  
  public Menu(String paramString) throws HeadlessException { this(paramString, false); }
  
  public Menu(String paramString, boolean paramBoolean) throws HeadlessException {
    super(paramString);
    this.tearOff = paramBoolean;
  }
  
  String constructComponentName() {
    synchronized (Menu.class) {
      return "menu" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = Toolkit.getDefaultToolkit().createMenu(this); 
      int i = getItemCount();
      for (byte b = 0; b < i; b++) {
        MenuItem menuItem = getItem(b);
        menuItem.parent = this;
        menuItem.addNotify();
      } 
    } 
  }
  
  public void removeNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      int i = getItemCount();
      for (byte b = 0; b < i; b++)
        getItem(b).removeNotify(); 
      super.removeNotify();
    } 
  }
  
  public boolean isTearOff() { return this.tearOff; }
  
  public int getItemCount() { return countItems(); }
  
  @Deprecated
  public int countItems() { return countItemsImpl(); }
  
  final int countItemsImpl() { return this.items.size(); }
  
  public MenuItem getItem(int paramInt) { return getItemImpl(paramInt); }
  
  final MenuItem getItemImpl(int paramInt) { return (MenuItem)this.items.elementAt(paramInt); }
  
  public MenuItem add(MenuItem paramMenuItem) {
    synchronized (getTreeLock()) {
      if (paramMenuItem.parent != null)
        paramMenuItem.parent.remove(paramMenuItem); 
      this.items.addElement(paramMenuItem);
      paramMenuItem.parent = this;
      MenuPeer menuPeer = (MenuPeer)this.peer;
      if (menuPeer != null) {
        paramMenuItem.addNotify();
        menuPeer.addItem(paramMenuItem);
      } 
      return paramMenuItem;
    } 
  }
  
  public void add(String paramString) throws HeadlessException { add(new MenuItem(paramString)); }
  
  public void insert(MenuItem paramMenuItem, int paramInt) {
    synchronized (getTreeLock()) {
      if (paramInt < 0)
        throw new IllegalArgumentException("index less than zero."); 
      int i = getItemCount();
      Vector vector = new Vector();
      int j;
      for (j = paramInt; j < i; j++) {
        vector.addElement(getItem(paramInt));
        remove(paramInt);
      } 
      add(paramMenuItem);
      for (j = 0; j < vector.size(); j++)
        add((MenuItem)vector.elementAt(j)); 
    } 
  }
  
  public void insert(String paramString, int paramInt) { insert(new MenuItem(paramString), paramInt); }
  
  public void addSeparator() throws HeadlessException { add("-"); }
  
  public void insertSeparator(int paramInt) {
    synchronized (getTreeLock()) {
      if (paramInt < 0)
        throw new IllegalArgumentException("index less than zero."); 
      int i = getItemCount();
      Vector vector = new Vector();
      int j;
      for (j = paramInt; j < i; j++) {
        vector.addElement(getItem(paramInt));
        remove(paramInt);
      } 
      addSeparator();
      for (j = 0; j < vector.size(); j++)
        add((MenuItem)vector.elementAt(j)); 
    } 
  }
  
  public void remove(int paramInt) {
    synchronized (getTreeLock()) {
      MenuItem menuItem = getItem(paramInt);
      this.items.removeElementAt(paramInt);
      MenuPeer menuPeer = (MenuPeer)this.peer;
      if (menuPeer != null) {
        menuPeer.delItem(paramInt);
        menuItem.removeNotify();
        menuItem.parent = null;
      } 
    } 
  }
  
  public void remove(MenuComponent paramMenuComponent) {
    synchronized (getTreeLock()) {
      int i = this.items.indexOf(paramMenuComponent);
      if (i >= 0)
        remove(i); 
    } 
  }
  
  public void removeAll() throws HeadlessException {
    synchronized (getTreeLock()) {
      int i = getItemCount();
      for (int j = i - 1; j >= 0; j--)
        remove(j); 
    } 
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent) {
    int i = getItemCount();
    for (byte b = 0; b < i; b++) {
      MenuItem menuItem = getItem(b);
      if (menuItem.handleShortcut(paramKeyEvent))
        return true; 
    } 
    return false;
  }
  
  MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut) {
    int i = getItemCount();
    for (byte b = 0; b < i; b++) {
      MenuItem menuItem = getItem(b).getShortcutMenuItem(paramMenuShortcut);
      if (menuItem != null)
        return menuItem; 
    } 
    return null;
  }
  
  Enumeration<MenuShortcut> shortcuts() {
    Vector vector = new Vector();
    int i = getItemCount();
    for (byte b = 0; b < i; b++) {
      MenuItem menuItem = getItem(b);
      if (menuItem instanceof Menu) {
        Enumeration enumeration = ((Menu)menuItem).shortcuts();
        while (enumeration.hasMoreElements())
          vector.addElement(enumeration.nextElement()); 
      } else {
        MenuShortcut menuShortcut = menuItem.getShortcut();
        if (menuShortcut != null)
          vector.addElement(menuShortcut); 
      } 
    } 
    return vector.elements();
  }
  
  void deleteShortcut(MenuShortcut paramMenuShortcut) {
    int i = getItemCount();
    for (byte b = 0; b < i; b++)
      getItem(b).deleteShortcut(paramMenuShortcut); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { paramObjectOutputStream.defaultWriteObject(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException, HeadlessException {
    paramObjectInputStream.defaultReadObject();
    for (byte b = 0; b < this.items.size(); b++) {
      MenuItem menuItem = (MenuItem)this.items.elementAt(b);
      menuItem.parent = this;
    } 
  }
  
  public String paramString() {
    String str = ",tearOff=" + this.tearOff + ",isHelpMenu=" + this.isHelpMenu;
    return super.paramString() + str;
  }
  
  private static native void initIDs() throws HeadlessException;
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTMenu(); 
    return this.accessibleContext;
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent) { return this.items.indexOf(paramMenuComponent); }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setMenuAccessor(new AWTAccessor.MenuAccessor() {
          public Vector<MenuComponent> getItems(Menu param1Menu) { return param1Menu.items; }
        });
    nameCounter = 0;
  }
  
  protected class AccessibleAWTMenu extends MenuItem.AccessibleAWTMenuItem {
    private static final long serialVersionUID = 5228160894980069094L;
    
    protected AccessibleAWTMenu() { super(Menu.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.MENU; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Menu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */