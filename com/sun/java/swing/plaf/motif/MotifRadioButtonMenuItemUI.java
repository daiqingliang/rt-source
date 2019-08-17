package com.sun.java.swing.plaf.motif;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.MenuSelectionManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

public class MotifRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI {
  protected ChangeListener changeListener;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new MotifRadioButtonMenuItemUI(); }
  
  protected void installListeners() {
    super.installListeners();
    this.changeListener = createChangeListener(this.menuItem);
    this.menuItem.addChangeListener(this.changeListener);
  }
  
  protected void uninstallListeners() {
    super.uninstallListeners();
    this.menuItem.removeChangeListener(this.changeListener);
  }
  
  protected ChangeListener createChangeListener(JComponent paramJComponent) { return new ChangeHandler(); }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent) { return new MouseInputHandler(); }
  
  protected class ChangeHandler implements ChangeListener, Serializable {
    public void stateChanged(ChangeEvent param1ChangeEvent) {
      JMenuItem jMenuItem = (JMenuItem)param1ChangeEvent.getSource();
      LookAndFeel.installProperty(jMenuItem, "borderPainted", Boolean.valueOf(jMenuItem.isArmed()));
    }
  }
  
  protected class MouseInputHandler implements MouseInputListener {
    public void mouseClicked(MouseEvent param1MouseEvent) {}
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      menuSelectionManager.setSelectedPath(MotifRadioButtonMenuItemUI.this.getPath());
    }
    
    public void mouseReleased(MouseEvent param1MouseEvent) {
      MenuSelectionManager menuSelectionManager = MenuSelectionManager.defaultManager();
      JMenuItem jMenuItem = (JMenuItem)param1MouseEvent.getComponent();
      Point point = param1MouseEvent.getPoint();
      if (point.x >= 0 && point.x < jMenuItem.getWidth() && point.y >= 0 && point.y < jMenuItem.getHeight()) {
        menuSelectionManager.clearSelectedPath();
        jMenuItem.doClick(0);
      } else {
        menuSelectionManager.processMouseEvent(param1MouseEvent);
      } 
    }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {}
    
    public void mouseExited(MouseEvent param1MouseEvent) {}
    
    public void mouseDragged(MouseEvent param1MouseEvent) { MenuSelectionManager.defaultManager().processMouseEvent(param1MouseEvent); }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifRadioButtonMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */