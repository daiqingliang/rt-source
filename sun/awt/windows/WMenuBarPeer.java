package sun.awt.windows;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.peer.MenuBarPeer;

final class WMenuBarPeer extends WMenuPeer implements MenuBarPeer {
  final WFramePeer framePeer;
  
  public native void addMenu(Menu paramMenu);
  
  public native void delMenu(int paramInt);
  
  public void addHelpMenu(Menu paramMenu) { addMenu(paramMenu); }
  
  WMenuBarPeer(MenuBar paramMenuBar) {
    this.target = paramMenuBar;
    this.framePeer = (WFramePeer)WToolkit.targetToPeer(paramMenuBar.getParent());
    if (this.framePeer != null)
      this.framePeer.addChildPeer(this); 
    create(this.framePeer);
    checkMenuCreation();
  }
  
  native void create(WFramePeer paramWFramePeer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WMenuBarPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */