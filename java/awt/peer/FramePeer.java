package java.awt.peer;

import java.awt.MenuBar;
import java.awt.Rectangle;

public interface FramePeer extends WindowPeer {
  void setTitle(String paramString);
  
  void setMenuBar(MenuBar paramMenuBar);
  
  void setResizable(boolean paramBoolean);
  
  void setState(int paramInt);
  
  int getState();
  
  void setMaximizedBounds(Rectangle paramRectangle);
  
  void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  Rectangle getBoundsPrivate();
  
  void emulateActivation(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\FramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */