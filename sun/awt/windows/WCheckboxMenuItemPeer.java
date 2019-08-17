package sun.awt.windows;

import java.awt.CheckboxMenuItem;
import java.awt.event.ItemEvent;
import java.awt.peer.CheckboxMenuItemPeer;

final class WCheckboxMenuItemPeer extends WMenuItemPeer implements CheckboxMenuItemPeer {
  public native void setState(boolean paramBoolean);
  
  WCheckboxMenuItemPeer(CheckboxMenuItem paramCheckboxMenuItem) {
    super(paramCheckboxMenuItem, true);
    setState(paramCheckboxMenuItem.getState());
  }
  
  public void handleAction(final boolean state) {
    final CheckboxMenuItem target = (CheckboxMenuItem)this.target;
    WToolkit.executeOnEventHandlerThread(checkboxMenuItem, new Runnable() {
          public void run() {
            target.setState(state);
            WCheckboxMenuItemPeer.this.postEvent(new ItemEvent(target, 701, target.getLabel(), state ? 1 : 2));
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WCheckboxMenuItemPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */