package com.sun.java.swing.plaf.motif;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.MenuSelectionManager;

class MotifMenuMouseMotionListener implements MouseMotionListener {
  public void mouseDragged(MouseEvent paramMouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent); }
  
  public void mouseMoved(MouseEvent paramMouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(paramMouseEvent); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifMenuMouseMotionListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */