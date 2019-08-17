package javax.swing.plaf.basic;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.ComponentUI;

public class BasicCheckBoxMenuItemUI extends BasicMenuItemUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicCheckBoxMenuItemUI(); }
  
  protected String getPropertyPrefix() { return "CheckBoxMenuItem"; }
  
  public void processMouseEvent(JMenuItem paramJMenuItem, MouseEvent paramMouseEvent, MenuElement[] paramArrayOfMenuElement, MenuSelectionManager paramMenuSelectionManager) {
    Point point = paramMouseEvent.getPoint();
    if (point.x >= 0 && point.x < paramJMenuItem.getWidth() && point.y >= 0 && point.y < paramJMenuItem.getHeight()) {
      if (paramMouseEvent.getID() == 502) {
        paramMenuSelectionManager.clearSelectedPath();
        paramJMenuItem.doClick(0);
      } else {
        paramMenuSelectionManager.setSelectedPath(paramArrayOfMenuElement);
      } 
    } else if (paramJMenuItem.getModel().isArmed()) {
      MenuElement[] arrayOfMenuElement = new MenuElement[paramArrayOfMenuElement.length - 1];
      byte b = 0;
      int i = paramArrayOfMenuElement.length - 1;
      while (b < i) {
        arrayOfMenuElement[b] = paramArrayOfMenuElement[b];
        b++;
      } 
      paramMenuSelectionManager.setSelectedPath(arrayOfMenuElement);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicCheckBoxMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */