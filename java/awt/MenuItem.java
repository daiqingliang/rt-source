package java.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.peer.MenuItemPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleValue;
import sun.awt.AWTAccessor;

public class MenuItem extends MenuComponent implements Accessible {
  boolean enabled = true;
  
  String label;
  
  String actionCommand;
  
  long eventMask;
  
  ActionListener actionListener;
  
  private MenuShortcut shortcut = null;
  
  private static final String base = "menuitem";
  
  private static int nameCounter;
  
  private static final long serialVersionUID = -21757335363267194L;
  
  private int menuItemSerializedDataVersion = 1;
  
  public MenuItem() throws HeadlessException { this("", null); }
  
  public MenuItem(String paramString) throws HeadlessException { this(paramString, null); }
  
  public MenuItem(String paramString, MenuShortcut paramMenuShortcut) throws HeadlessException {
    this.label = paramString;
    this.shortcut = paramMenuShortcut;
  }
  
  String constructComponentName() {
    synchronized (MenuItem.class) {
      return "menuitem" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = Toolkit.getDefaultToolkit().createMenuItem(this); 
    } 
  }
  
  public String getLabel() { return this.label; }
  
  public void setLabel(String paramString) throws HeadlessException {
    this.label = paramString;
    MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
    if (menuItemPeer != null)
      menuItemPeer.setLabel(paramString); 
  }
  
  public boolean isEnabled() { return this.enabled; }
  
  public void setEnabled(boolean paramBoolean) { enable(paramBoolean); }
  
  @Deprecated
  public void enable() throws HeadlessException {
    this.enabled = true;
    MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
    if (menuItemPeer != null)
      menuItemPeer.setEnabled(true); 
  }
  
  @Deprecated
  public void enable(boolean paramBoolean) {
    if (paramBoolean) {
      enable();
    } else {
      disable();
    } 
  }
  
  @Deprecated
  public void disable() throws HeadlessException {
    this.enabled = false;
    MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
    if (menuItemPeer != null)
      menuItemPeer.setEnabled(false); 
  }
  
  public MenuShortcut getShortcut() { return this.shortcut; }
  
  public void setShortcut(MenuShortcut paramMenuShortcut) {
    this.shortcut = paramMenuShortcut;
    MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
    if (menuItemPeer != null)
      menuItemPeer.setLabel(this.label); 
  }
  
  public void deleteShortcut() throws HeadlessException {
    this.shortcut = null;
    MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
    if (menuItemPeer != null)
      menuItemPeer.setLabel(this.label); 
  }
  
  void deleteShortcut(MenuShortcut paramMenuShortcut) {
    if (paramMenuShortcut.equals(this.shortcut)) {
      this.shortcut = null;
      MenuItemPeer menuItemPeer = (MenuItemPeer)this.peer;
      if (menuItemPeer != null)
        menuItemPeer.setLabel(this.label); 
    } 
  }
  
  void doMenuEvent(long paramLong, int paramInt) { Toolkit.getEventQueue().postEvent(new ActionEvent(this, 1001, getActionCommand(), paramLong, paramInt)); }
  
  private final boolean isItemEnabled() {
    if (!isEnabled())
      return false; 
    MenuContainer menuContainer = getParent_NoClientCode();
    do {
      if (!(menuContainer instanceof Menu))
        return true; 
      Menu menu = (Menu)menuContainer;
      if (!menu.isEnabled())
        return false; 
      menuContainer = menu.getParent_NoClientCode();
    } while (menuContainer != null);
    return true;
  }
  
  boolean handleShortcut(KeyEvent paramKeyEvent) {
    MenuShortcut menuShortcut1 = new MenuShortcut(paramKeyEvent.getKeyCode(), ((paramKeyEvent.getModifiers() & true) > 0));
    MenuShortcut menuShortcut2 = new MenuShortcut(paramKeyEvent.getExtendedKeyCode(), ((paramKeyEvent.getModifiers() & true) > 0));
    if ((menuShortcut1.equals(this.shortcut) || menuShortcut2.equals(this.shortcut)) && isItemEnabled()) {
      if (paramKeyEvent.getID() == 401)
        doMenuEvent(paramKeyEvent.getWhen(), paramKeyEvent.getModifiers()); 
      return true;
    } 
    return false;
  }
  
  MenuItem getShortcutMenuItem(MenuShortcut paramMenuShortcut) { return paramMenuShortcut.equals(this.shortcut) ? this : null; }
  
  protected final void enableEvents(long paramLong) {
    this.eventMask |= paramLong;
    this.newEventsOnly = true;
  }
  
  protected final void disableEvents(long paramLong) { this.eventMask &= (paramLong ^ 0xFFFFFFFFFFFFFFFFL); }
  
  public void setActionCommand(String paramString) throws HeadlessException { this.actionCommand = paramString; }
  
  public String getActionCommand() { return getActionCommandImpl(); }
  
  final String getActionCommandImpl() { return (this.actionCommand == null) ? this.label : this.actionCommand; }
  
  public void addActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.add(this.actionListener, paramActionListener);
    this.newEventsOnly = true;
  }
  
  public void removeActionListener(ActionListener paramActionListener) {
    if (paramActionListener == null)
      return; 
    this.actionListener = AWTEventMulticaster.remove(this.actionListener, paramActionListener);
  }
  
  public ActionListener[] getActionListeners() { return (ActionListener[])getListeners(ActionListener.class); }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) {
    ActionListener actionListener1 = null;
    if (paramClass == ActionListener.class)
      actionListener1 = this.actionListener; 
    return (T[])AWTEventMulticaster.getListeners(actionListener1, paramClass);
  }
  
  protected void processEvent(AWTEvent paramAWTEvent) {
    if (paramAWTEvent instanceof ActionEvent)
      processActionEvent((ActionEvent)paramAWTEvent); 
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return (paramAWTEvent.id == 1001) ? (((this.eventMask & 0x80L) != 0L || this.actionListener != null)) : super.eventEnabled(paramAWTEvent); }
  
  protected void processActionEvent(ActionEvent paramActionEvent) {
    ActionListener actionListener1 = this.actionListener;
    if (actionListener1 != null)
      actionListener1.actionPerformed(paramActionEvent); 
  }
  
  public String paramString() {
    String str = ",label=" + this.label;
    if (this.shortcut != null)
      str = str + ",shortcut=" + this.shortcut; 
    return super.paramString() + str;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    AWTEventMulticaster.save(paramObjectOutputStream, "actionL", this.actionListener);
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    paramObjectInputStream.defaultReadObject();
    Object object;
    while (null != (object = paramObjectInputStream.readObject())) {
      String str = ((String)object).intern();
      if ("actionL" == str) {
        addActionListener((ActionListener)paramObjectInputStream.readObject());
        continue;
      } 
      paramObjectInputStream.readObject();
    } 
  }
  
  private static native void initIDs() throws HeadlessException;
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTMenuItem(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setMenuItemAccessor(new AWTAccessor.MenuItemAccessor() {
          public boolean isEnabled(MenuItem param1MenuItem) { return param1MenuItem.enabled; }
          
          public String getLabel(MenuItem param1MenuItem) { return param1MenuItem.label; }
          
          public MenuShortcut getShortcut(MenuItem param1MenuItem) { return param1MenuItem.shortcut; }
          
          public String getActionCommandImpl(MenuItem param1MenuItem) { return param1MenuItem.getActionCommandImpl(); }
          
          public boolean isItemEnabled(MenuItem param1MenuItem) { return param1MenuItem.isItemEnabled(); }
        });
    nameCounter = 0;
  }
  
  protected class AccessibleAWTMenuItem extends MenuComponent.AccessibleAWTMenuComponent implements AccessibleAction, AccessibleValue {
    private static final long serialVersionUID = -217847831945965825L;
    
    protected AccessibleAWTMenuItem() { super(MenuItem.this); }
    
    public String getAccessibleName() { return (this.accessibleName != null) ? this.accessibleName : ((MenuItem.this.getLabel() == null) ? super.getAccessibleName() : MenuItem.this.getLabel()); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.MENU_ITEM; }
    
    public AccessibleAction getAccessibleAction() { return this; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public int getAccessibleActionCount() { return 1; }
    
    public String getAccessibleActionDescription(int param1Int) { return (param1Int == 0) ? "click" : null; }
    
    public boolean doAccessibleAction(int param1Int) {
      if (param1Int == 0) {
        Toolkit.getEventQueue().postEvent(new ActionEvent(MenuItem.this, 1001, MenuItem.this.getActionCommand(), EventQueue.getMostRecentEventTime(), 0));
        return true;
      } 
      return false;
    }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(0); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) { return false; }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(0); }
    
    public Number getMaximumAccessibleValue() { return Integer.valueOf(0); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MenuItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */