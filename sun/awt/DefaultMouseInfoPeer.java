package sun.awt;

import java.awt.Point;
import java.awt.Window;
import java.awt.peer.MouseInfoPeer;

public class DefaultMouseInfoPeer implements MouseInfoPeer {
  public native int fillPointWithCoords(Point paramPoint);
  
  public native boolean isWindowUnderMouse(Window paramWindow);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\DefaultMouseInfoPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */