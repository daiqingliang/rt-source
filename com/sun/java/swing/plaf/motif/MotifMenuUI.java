package com.sun.java.swing.plaf.motif;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class MotifMenuUI extends BasicMenuUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifMenuUI(); }
  
  protected ChangeListener createChangeListener(JComponent paramJComponent) { return new MotifChangeHandler((JMenu)paramJComponent, this); }
  
  private boolean popupIsOpen(JMenu paramJMenu, MenuElement[] paramArrayOfMenuElement) {
    JPopupMenu jPopupMenu = paramJMenu.getPopupMenu();
    for (int i = paramArrayOfMenuElement.length - 1; i >= 0; i--) {
      if (paramArrayOfMenuElement[i].getComponent() == jPopupMenu)
        return true; 
    } 
    return false;
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent) { return new MouseInputHandler(); }
  
  public class MotifChangeHandler extends BasicMenuUI.ChangeHandler {
    public MotifChangeHandler(JMenu param1JMenu, MotifMenuUI param1MotifMenuUI1) { super(MotifMenuUI.this, param1JMenu, param1MotifMenuUI1); }
    
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      JMenuItem jMenuItem = (JMenuItem)param1ChangeEvent.getSource();
      if (jMenuItem.isArmed() || jMenuItem.isSelected()) {
        jMenuItem.setBorderPainted(true);
      } else {
        jMenuItem.setBorderPainted(false);
      } 
      super.stateChanged(param1ChangeEvent);
    }
  }
  
  protected class MouseInputHandler implements MouseInputListener {
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      JMenu jMenu = (JMenu)param1MouseEvent.getComponent();
      if (jMenu.isEnabled()) {
        if (jMenu.isTopLevelMenu())
          if (jMenu.isSelected()) {
            menuSelectionManager.clearSelectedPath();
          } else {
            Container container = jMenu.getParent();
            if (container != null && container instanceof javax.swing.JMenuBar) {
              MenuElement[] arrayOfMenuElement1 = new MenuElement[2];
              arrayOfMenuElement1[0] = (MenuElement)container;
              arrayOfMenuElement1[1] = jMenu;
              menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
            } 
          }  
        MenuElement[] arrayOfMenuElement = MotifMenuUI.this.getPath();
        if (arrayOfMenuElement.length > 0) {
          MenuElement[] arrayOfMenuElement1 = new MenuElement[arrayOfMenuElement.length + 1];
          System.arraycopy(arrayOfMenuElement, 0, arrayOfMenuElement1, 0, arrayOfMenuElement.length);
          arrayOfMenuElement1[arrayOfMenuElement.length] = jMenu.getPopupMenu();
          menuSelectionManager.setSelectedPath(arrayOfMenuElement1);
        } 
      } 
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      JMenuItem jMenuItem = (JMenuItem)param1MouseEvent.getComponent();
      Point point = param1MouseEvent.getPoint();
      if (point.x < 0 || point.x >= jMenuItem.getWidth() || point.y < 0 || point.y >= jMenuItem.getHeight())
        menuSelectionManager.processMouseEvent(param1MouseEvent); 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mouseDragged(MouseEvent param1MouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */