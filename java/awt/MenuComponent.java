package java.awt;

import java.awt.event.FocusListener;
import java.awt.peer.MenuComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Locale;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleSelection;
import javax.accessibility.AccessibleStateSet;
import sun.awt.AWTAccessor;
import sun.awt.AppContext;

public abstract class MenuComponent implements Serializable {
  MenuComponentPeer peer;
  
  MenuContainer parent;
  
  AppContext appContext;
  
  private String name;
  
  private boolean nameExplicitlySet = false;
  
  boolean newEventsOnly = false;
  
  static final String actionListenerK = "actionL";
  
  static final String itemListenerK = "itemL";
  
  private static final long serialVersionUID = -4536902356223894379L;
  
  AccessibleContext accessibleContext = null;
  
  final AccessControlContext getAccessControlContext() {
    if (this.acc == null)
      throw new SecurityException("MenuComponent is missing AccessControlContext"); 
    return this.acc;
  }
  
  public MenuComponent() throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.appContext = AppContext.getAppContext();
  }
  
  String constructComponentName() { return null; }
  
  public String getName() {
    if (this.name == null && !this.nameExplicitlySet)
      synchronized (this) {
        if (this.name == null && !this.nameExplicitlySet)
          this.name = constructComponentName(); 
      }  
    return this.name;
  }
  
  public void setName(String paramString) {
    synchronized (this) {
      this.name = paramString;
      this.nameExplicitlySet = true;
    } 
  }
  
  public MenuContainer getParent() { return getParent_NoClientCode(); }
  
  final MenuContainer getParent_NoClientCode() { return this.parent; }
  
  @Deprecated
  public MenuComponentPeer getPeer() { return this.peer; }
  
  public Font getFont() {
    Font font1 = this.font;
    if (font1 != null)
      return font1; 
    MenuContainer menuContainer = this.parent;
    return (menuContainer != null) ? menuContainer.getFont() : null;
  }
  
  final Font getFont_NoClientCode() {
    Font font1 = this.font;
    if (font1 != null)
      return font1; 
    MenuContainer menuContainer = this.parent;
    if (menuContainer != null)
      if (menuContainer instanceof Component) {
        font1 = ((Component)menuContainer).getFont_NoClientCode();
      } else if (menuContainer instanceof MenuComponent) {
        font1 = ((MenuComponent)menuContainer).getFont_NoClientCode();
      }  
    return font1;
  }
  
  public void setFont(Font paramFont) {
    synchronized (getTreeLock()) {
      this.font = paramFont;
      MenuComponentPeer menuComponentPeer = this.peer;
      if (menuComponentPeer != null)
        menuComponentPeer.setFont(paramFont); 
    } 
  }
  
  public void removeNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      MenuComponentPeer menuComponentPeer = this.peer;
      if (menuComponentPeer != null) {
        Toolkit.getEventQueue().removeSourceEvents(this, true);
        this.peer = null;
        menuComponentPeer.dispose();
      } 
    } 
  }
  
  @Deprecated
  public boolean postEvent(Event paramEvent) {
    MenuContainer menuContainer = this.parent;
    if (menuContainer != null)
      menuContainer.postEvent(paramEvent); 
    return false;
  }
  
  public final void dispatchEvent(AWTEvent paramAWTEvent) { dispatchEventImpl(paramAWTEvent); }
  
  void dispatchEventImpl(AWTEvent paramAWTEvent) {
    EventQueue.setCurrentEventAndMostRecentTime(paramAWTEvent);
    Toolkit.getDefaultToolkit().notifyAWTEventListeners(paramAWTEvent);
    if (this.newEventsOnly || (this.parent != null && this.parent instanceof MenuComponent && ((MenuComponent)this.parent).newEventsOnly)) {
      if (eventEnabled(paramAWTEvent)) {
        processEvent(paramAWTEvent);
      } else if (paramAWTEvent instanceof java.awt.event.ActionEvent && this.parent != null) {
        paramAWTEvent.setSource(this.parent);
        ((MenuComponent)this.parent).dispatchEvent(paramAWTEvent);
      } 
    } else {
      Event event = paramAWTEvent.convertToOld();
      if (event != null)
        postEvent(event); 
    } 
  }
  
  boolean eventEnabled(AWTEvent paramAWTEvent) { return false; }
  
  protected void processEvent(AWTEvent paramAWTEvent) {}
  
  protected String paramString() {
    String str = getName();
    return (str != null) ? str : "";
  }
  
  public String toString() { return getClass().getName() + "[" + paramString() + "]"; }
  
  protected final Object getTreeLock() { return Component.LOCK; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.acc = AccessController.getContext();
    paramObjectInputStream.defaultReadObject();
    this.appContext = AppContext.getAppContext();
  }
  
  private static native void initIDs() throws HeadlessException;
  
  public AccessibleContext getAccessibleContext() { return this.accessibleContext; }
  
  int getAccessibleIndexInParent() {
    MenuContainer menuContainer = this.parent;
    if (!(menuContainer instanceof MenuComponent))
      return -1; 
    MenuComponent menuComponent = (MenuComponent)menuContainer;
    return menuComponent.getAccessibleChildIndex(this);
  }
  
  int getAccessibleChildIndex(MenuComponent paramMenuComponent) { return -1; }
  
  AccessibleStateSet getAccessibleStateSet() { return new AccessibleStateSet(); }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    AWTAccessor.setMenuComponentAccessor(new AWTAccessor.MenuComponentAccessor() {
          public AppContext getAppContext(MenuComponent param1MenuComponent) { return param1MenuComponent.appContext; }
          
          public void setAppContext(MenuComponent param1MenuComponent, AppContext param1AppContext) { param1MenuComponent.appContext = param1AppContext; }
          
          public MenuContainer getParent(MenuComponent param1MenuComponent) { return param1MenuComponent.parent; }
          
          public Font getFont_NoClientCode(MenuComponent param1MenuComponent) { return param1MenuComponent.getFont_NoClientCode(); }
          
          public <T extends MenuComponentPeer> T getPeer(MenuComponent param1MenuComponent) { return (T)param1MenuComponent.peer; }
        });
  }
  
  protected abstract class AccessibleAWTMenuComponent extends AccessibleContext implements Serializable, AccessibleComponent, AccessibleSelection {
    private static final long serialVersionUID = -4269533416223798698L;
    
    public AccessibleSelection getAccessibleSelection() { return this; }
    
    public String getAccessibleName() { return this.accessibleName; }
    
    public String getAccessibleDescription() { return this.accessibleDescription; }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.AWT_COMPONENT; }
    
    public AccessibleStateSet getAccessibleStateSet() { return MenuComponent.this.getAccessibleStateSet(); }
    
    public Accessible getAccessibleParent() {
      if (this.accessibleParent != null)
        return this.accessibleParent; 
      MenuContainer menuContainer = MenuComponent.this.getParent();
      return (menuContainer instanceof Accessible) ? (Accessible)menuContainer : null;
    }
    
    public int getAccessibleIndexInParent() { return MenuComponent.this.getAccessibleIndexInParent(); }
    
    public int getAccessibleChildrenCount() { return 0; }
    
    public Accessible getAccessibleChild(int param1Int) { return null; }
    
    public Locale getLocale() {
      MenuContainer menuContainer = MenuComponent.this.getParent();
      return (menuContainer instanceof Component) ? ((Component)menuContainer).getLocale() : Locale.getDefault();
    }
    
    public AccessibleComponent getAccessibleComponent() { return this; }
    
    public Color getBackground() { return null; }
    
    public void setBackground(Color param1Color) {}
    
    public Color getForeground() { return null; }
    
    public void setForeground(Color param1Color) {}
    
    public Cursor getCursor() { return null; }
    
    public void setCursor(Cursor param1Cursor) {}
    
    public Font getFont() { return MenuComponent.this.getFont(); }
    
    public void setFont(Font param1Font) { MenuComponent.this.setFont(param1Font); }
    
    public FontMetrics getFontMetrics(Font param1Font) { return null; }
    
    public boolean isEnabled() { return true; }
    
    public void setEnabled(boolean param1Boolean) {}
    
    public boolean isVisible() { return true; }
    
    public void setVisible(boolean param1Boolean) {}
    
    public boolean isShowing() { return true; }
    
    public boolean contains(Point param1Point) { return false; }
    
    public Point getLocationOnScreen() { return null; }
    
    public Point getLocation() { return null; }
    
    public void setLocation(Point param1Point) {}
    
    public Rectangle getBounds() { return null; }
    
    public void setBounds(Rectangle param1Rectangle) {}
    
    public Dimension getSize() { return null; }
    
    public void setSize(Dimension param1Dimension) {}
    
    public Accessible getAccessibleAt(Point param1Point) { return null; }
    
    public boolean isFocusTraversable() { return true; }
    
    public void requestFocus() throws HeadlessException {}
    
    public void addFocusListener(FocusListener param1FocusListener) {}
    
    public void removeFocusListener(FocusListener param1FocusListener) {}
    
    public int getAccessibleSelectionCount() { return 0; }
    
    public Accessible getAccessibleSelection(int param1Int) { return null; }
    
    public boolean isAccessibleChildSelected(int param1Int) { return false; }
    
    public void addAccessibleSelection(int param1Int) {}
    
    public void removeAccessibleSelection(int param1Int) {}
    
    public void clearAccessibleSelection() throws HeadlessException {}
    
    public void selectAllAccessibleSelection() throws HeadlessException {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\MenuComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */