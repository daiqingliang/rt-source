package sun.awt.windows;

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.peer.PopupMenuPeer;
import sun.awt.AWTAccessor;

final class WPopupMenuPeer extends WMenuPeer implements PopupMenuPeer {
  WPopupMenuPeer(PopupMenu paramPopupMenu) {
    this.target = paramPopupMenu;
    MenuContainer menuContainer = null;
    boolean bool = AWTAccessor.getPopupMenuAccessor().isTrayIconPopup(paramPopupMenu);
    if (bool) {
      menuContainer = AWTAccessor.getMenuComponentAccessor().getParent(paramPopupMenu);
    } else {
      menuContainer = paramPopupMenu.getParent();
    } 
    if (menuContainer instanceof Component) {
      WComponentPeer wComponentPeer = (WComponentPeer)WToolkit.targetToPeer(menuContainer);
      if (wComponentPeer == null) {
        menuContainer = WToolkit.getNativeContainer((Component)menuContainer);
        wComponentPeer = (WComponentPeer)WToolkit.targetToPeer(menuContainer);
      } 
      wComponentPeer.addChildPeer(this);
      createMenu(wComponentPeer);
      checkMenuCreation();
    } else {
      throw new IllegalArgumentException("illegal popup menu container class");
    } 
  }
  
  private native void createMenu(WComponentPeer paramWComponentPeer);
  
  public void show(Event paramEvent) {
    Component component = (Component)paramEvent.target;
    WComponentPeer wComponentPeer = (WComponentPeer)WToolkit.targetToPeer(component);
    if (wComponentPeer == null) {
      Container container = WToolkit.getNativeContainer(component);
      paramEvent.target = container;
      for (Component component1 = component; component1 != container; component1 = component1.getParent()) {
        Point point = component1.getLocation();
        paramEvent.x += point.x;
        paramEvent.y += point.y;
      } 
    } 
    _show(paramEvent);
  }
  
  void show(Component paramComponent, Point paramPoint) {
    WComponentPeer wComponentPeer = (WComponentPeer)WToolkit.targetToPeer(paramComponent);
    Event event = new Event(paramComponent, 0L, 501, paramPoint.x, paramPoint.y, 0, 0);
    if (wComponentPeer == null) {
      Container container = WToolkit.getNativeContainer(paramComponent);
      event.target = container;
    } 
    event.x = paramPoint.x;
    event.y = paramPoint.y;
    _show(event);
  }
  
  private native void _show(Event paramEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPopupMenuPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */