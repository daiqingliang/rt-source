package sun.awt.windows;

import java.awt.Menu;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.peer.MenuPeer;

class WMenuPeer extends WMenuItemPeer implements MenuPeer {
  public native void addSeparator();
  
  public void addItem(MenuItem paramMenuItem) { WMenuItemPeer wMenuItemPeer = (WMenuItemPeer)WToolkit.targetToPeer(paramMenuItem); }
  
  public native void delItem(int paramInt);
  
  WMenuPeer() {}
  
  WMenuPeer(Menu paramMenu) {
    this.target = paramMenu;
    MenuContainer menuContainer = paramMenu.getParent();
    if (menuContainer instanceof java.awt.MenuBar) {
      WMenuBarPeer wMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(menuContainer);
      this.parent = wMenuBarPeer;
      wMenuBarPeer.addChildPeer(this);
      createMenu(wMenuBarPeer);
    } else if (menuContainer instanceof Menu) {
      this.parent = (WMenuPeer)WToolkit.targetToPeer(menuContainer);
      this.parent.addChildPeer(this);
      createSubMenu(this.parent);
    } else {
      throw new IllegalArgumentException("unknown menu container class");
    } 
    checkMenuCreation();
  }
  
  native void createMenu(WMenuBarPeer paramWMenuBarPeer);
  
  native void createSubMenu(WMenuPeer paramWMenuPeer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WMenuPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */