package javax.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface MenuElement {
  void processMouseEvent(MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager);
  
  void processKeyEvent(KeyEvent paramKeyEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager);
  
  void menuSelectionChanged(boolean paramBoolean);
  
  MenuElement[] getSubElements();
  
  Component getComponent();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\MenuElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */