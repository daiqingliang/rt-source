package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

public class WindowsRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI {
  final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {
      public JMenuItem getMenuItem() { return WindowsRadioButtonMenuItemUI.this.menuItem; }
      
      public TMSchema.State getState(JMenuItem param1JMenuItem) { return WindowsMenuItemUI.getState(this, param1JMenuItem); }
      
      public TMSchema.Part getPart(JMenuItem param1JMenuItem) { return WindowsMenuItemUI.getPart(this, param1JMenuItem); }
    };
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsRadioButtonMenuItemUI(); }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor) {
    if (WindowsMenuItemUI.isVistaPainting()) {
      WindowsMenuItemUI.paintBackground(this.accessor, paramGraphics, paramJMenuItem, paramColor);
      return;
    } 
    super.paintBackground(paramGraphics, paramJMenuItem, paramColor);
  }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString) {
    if (WindowsMenuItemUI.isVistaPainting()) {
      WindowsMenuItemUI.paintText(this.accessor, paramGraphics, paramJMenuItem, paramRectangle, paramString);
      return;
    } 
    ButtonModel buttonModel = paramJMenuItem.getModel();
    Color color = paramGraphics.getColor();
    if (buttonModel.isEnabled() && buttonModel.isArmed())
      paramGraphics.setColor(this.selectionForeground); 
    WindowsGraphicsUtils.paintText(paramGraphics, paramJMenuItem, paramRectangle, paramString, 0);
    paramGraphics.setColor(color);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsRadioButtonMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */