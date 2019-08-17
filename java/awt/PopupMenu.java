package java.awt;

import java.awt.peer.PopupMenuPeer;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import sun.awt.AWTAccessor;

public class PopupMenu extends Menu {
  private static final String base = "popup";
  
  static int nameCounter = 0;
  
  boolean isTrayIconPopup = false;
  
  private static final long serialVersionUID = -4620452533522760060L;
  
  public PopupMenu() throws HeadlessException { this(""); }
  
  public PopupMenu(String paramString) throws HeadlessException { super(paramString); }
  
  public MenuContainer getParent() { return this.isTrayIconPopup ? null : super.getParent(); }
  
  String constructComponentName() {
    synchronized (PopupMenu.class) {
      return "popup" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.parent != null && !(this.parent instanceof Component)) {
        super.addNotify();
      } else {
        if (this.peer == null)
          this.peer = Toolkit.getDefaultToolkit().createPopupMenu(this); 
        int i = getItemCount();
        for (byte b = 0; b < i; b++) {
          MenuItem menuItem = getItem(b);
          menuItem.parent = this;
          menuItem.addNotify();
        } 
      } 
    } 
  }
  
  public void show(Component paramComponent, int paramInt1, int paramInt2) {
    MenuContainer menuContainer = this.parent;
    if (menuContainer == null)
      throw new NullPointerException("parent is null"); 
    if (!(menuContainer instanceof Component))
      throw new IllegalArgumentException("PopupMenus with non-Component parents cannot be shown"); 
    Component component = (Component)menuContainer;
    if (component != paramComponent)
      if (component instanceof Container) {
        if (!((Container)component).isAncestorOf(paramComponent))
          throw new IllegalArgumentException("origin not in parent's hierarchy"); 
      } else {
        throw new IllegalArgumentException("origin not in parent's hierarchy");
      }  
    if (component.getPeer() == null || !component.isShowing())
      throw new RuntimeException("parent not showing on screen"); 
    if (this.peer == null)
      addNotify(); 
    synchronized (getTreeLock()) {
      if (this.peer != null)
        ((PopupMenuPeer)this.peer).show(new Event(paramComponent, 0L, 501, paramInt1, paramInt2, 0, 0)); 
    } 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTPopupMenu(); 
    return this.accessibleContext;
  }
  
  static  {
    AWTAccessor.setPopupMenuAccessor(new AWTAccessor.PopupMenuAccessor() {
          public boolean isTrayIconPopup(PopupMenu param1PopupMenu) { return param1PopupMenu.isTrayIconPopup; }
        });
  }
  
  protected class AccessibleAWTPopupMenu extends Menu.AccessibleAWTMenu {
    private static final long serialVersionUID = -4282044795947239955L;
    
    protected AccessibleAWTPopupMenu() { super(PopupMenu.this); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.POPUP_MENU; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\PopupMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */