package javax.swing.plaf.basic;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JList;

public interface ComboPopup {
  void show();
  
  void hide();
  
  boolean isVisible();
  
  JList getList();
  
  MouseListener getMouseListener();
  
  MouseMotionListener getMouseMotionListener();
  
  KeyListener getKeyListener();
  
  void uninstallingUI();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\ComboPopup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */