package java.awt.peer;

import java.awt.Point;
import java.awt.Window;

public interface MouseInfoPeer {
  int fillPointWithCoords(Point paramPoint);
  
  boolean isWindowUnderMouse(Window paramWindow);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\MouseInfoPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */