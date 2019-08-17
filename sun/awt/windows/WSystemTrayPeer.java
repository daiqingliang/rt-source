package sun.awt.windows;

import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.peer.SystemTrayPeer;

final class WSystemTrayPeer extends WObjectPeer implements SystemTrayPeer {
  WSystemTrayPeer(SystemTray paramSystemTray) { this.target = paramSystemTray; }
  
  public Dimension getTrayIconSize() { return new Dimension(16, 16); }
  
  public boolean isSupported() { return ((WToolkit)Toolkit.getDefaultToolkit()).isTraySupported(); }
  
  protected void disposeImpl() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WSystemTrayPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */