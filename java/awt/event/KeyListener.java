package java.awt.event;

import java.util.EventListener;

public interface KeyListener extends EventListener {
  void keyTyped(KeyEvent paramKeyEvent);
  
  void keyPressed(KeyEvent paramKeyEvent);
  
  void keyReleased(KeyEvent paramKeyEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\event\KeyListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */