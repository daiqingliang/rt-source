package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.MenuSelectionManager;

class MotifMenuMouseListener extends MouseAdapter {
  public void mousePressed(MouseEvent paramMouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent); }
  
  public void mouseReleased(MouseEvent paramMouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent); }
  
  public void mouseEntered(MouseEvent paramMouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent); }
  
  public void mouseExited(MouseEvent paramMouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifMenuMouseListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */