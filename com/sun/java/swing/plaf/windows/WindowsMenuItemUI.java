package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import sun.swing.SwingUtilities2;

public class WindowsMenuItemUI extends BasicMenuItemUI {
  final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {
      public JMenuItem getMenuItem() { return WindowsMenuItemUI.this.menuItem; }
      
      public TMSchema.State getState(JMenuItem param1JMenuItem) { return WindowsMenuItemUI.getState(this, param1JMenuItem); }
      
      public TMSchema.Part getPart(JMenuItem param1JMenuItem) { return WindowsMenuItemUI.getPart(this, param1JMenuItem); }
    };
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsMenuItemUI(); }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString) {
    if (isVistaPainting()) {
      paintText(this.accessor, paramGraphics, paramJMenuItem, paramRectangle, paramString);
      return;
    } 
    ButtonModel buttonModel = paramJMenuItem.getModel();
    Color color = paramGraphics.getColor();
    if (buttonModel.isEnabled() && (buttonModel.isArmed() || (paramJMenuItem instanceof javax.swing.JMenu && buttonModel.isSelected())))
      paramGraphics.setColor(this.selectionForeground); 
    WindowsGraphicsUtils.paintText(paramGraphics, paramJMenuItem, paramRectangle, paramString, 0);
    paramGraphics.setColor(color);
  }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor) {
    if (isVistaPainting()) {
      paintBackground(this.accessor, paramGraphics, paramJMenuItem, paramColor);
      return;
    } 
    super.paintBackground(paramGraphics, paramJMenuItem, paramColor);
  }
  
  static void paintBackground(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor) {
    XPStyle xPStyle = XPStyle.getXP();
    assert isVistaPainting(xPStyle);
    if (isVistaPainting(xPStyle)) {
      int i = paramJMenuItem.getWidth();
      int j = paramJMenuItem.getHeight();
      if (paramJMenuItem.isOpaque()) {
        Color color = paramGraphics.getColor();
        paramGraphics.setColor(paramJMenuItem.getBackground());
        paramGraphics.fillRect(0, 0, i, j);
        paramGraphics.setColor(color);
      } 
      TMSchema.Part part = paramWindowsMenuItemUIAccessor.getPart(paramJMenuItem);
      XPStyle.Skin skin = xPStyle.getSkin(paramJMenuItem, part);
      skin.paintSkin(paramGraphics, 0, 0, i, j, paramWindowsMenuItemUIAccessor.getState(paramJMenuItem));
    } 
  }
  
  static void paintText(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString) {
    assert isVistaPainting();
    if (isVistaPainting()) {
      TMSchema.State state = paramWindowsMenuItemUIAccessor.getState(paramJMenuItem);
      FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(paramJMenuItem, paramGraphics);
      int i = paramJMenuItem.getDisplayedMnemonicIndex();
      if (WindowsLookAndFeel.isMnemonicHidden() == true)
        i = -1; 
      WindowsGraphicsUtils.paintXPText(paramJMenuItem, paramWindowsMenuItemUIAccessor.getPart(paramJMenuItem), state, paramGraphics, paramRectangle.x, paramRectangle.y + fontMetrics.getAscent(), paramString, i);
    } 
  }
  
  static TMSchema.State getState(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, JMenuItem paramJMenuItem) {
    TMSchema.State state;
    ButtonModel buttonModel = paramJMenuItem.getModel();
    if (buttonModel.isArmed()) {
      state = buttonModel.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT;
    } else {
      state = buttonModel.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
    } 
    return state;
  }
  
  static TMSchema.Part getPart(WindowsMenuItemUIAccessor paramWindowsMenuItemUIAccessor, JMenuItem paramJMenuItem) { return TMSchema.Part.MP_POPUPITEM; }
  
  static boolean isVistaPainting(XPStyle paramXPStyle) { return (paramXPStyle != null && paramXPStyle.isSkinDefined(null, TMSchema.Part.MP_POPUPITEM)); }
  
  static boolean isVistaPainting() { return isVistaPainting(XPStyle.getXP()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsMenuItemUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */