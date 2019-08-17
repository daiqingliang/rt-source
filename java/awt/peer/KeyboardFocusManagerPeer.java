package java.awt.peer;

import java.awt.Component;
import java.awt.Window;

public interface KeyboardFocusManagerPeer {
  void setCurrentFocusedWindow(Window paramWindow);
  
  Window getCurrentFocusedWindow();
  
  void setCurrentFocusOwner(Component paramComponent);
  
  Component getCurrentFocusOwner();
  
  void clearGlobalFocusOwner(Window paramWindow);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\KeyboardFocusManagerPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */