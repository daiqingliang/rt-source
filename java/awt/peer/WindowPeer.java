package java.awt.peer;

import java.awt.Dialog;

public interface WindowPeer extends ContainerPeer {
  void toFront();
  
  void toBack();
  
  void updateAlwaysOnTopState();
  
  void updateFocusableWindowState();
  
  void setModalBlocked(Dialog paramDialog, boolean paramBoolean);
  
  void updateMinimumSize();
  
  void updateIconImages();
  
  void setOpacity(float paramFloat);
  
  void setOpaque(boolean paramBoolean);
  
  void updateWindow();
  
  void repositionSecurityWarning();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\WindowPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */