package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class WindowsMenuUI extends BasicMenuUI {
  protected Integer menuBarHeight;
  
  protected boolean hotTrackingOn;
  
  final WindowsMenuItemUIAccessor accessor = new WindowsMenuItemUIAccessor() {
      public JMenuItem getMenuItem() { return WindowsMenuUI.this.menuItem; }
      
      public TMSchema.State getState(JMenuItem param1JMenuItem) {
        TMSchema.State state = param1JMenuItem.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
        ButtonModel buttonModel = param1JMenuItem.getModel();
        if (buttonModel.isArmed() || buttonModel.isSelected()) {
          state = param1JMenuItem.isEnabled() ? TMSchema.State.PUSHED : TMSchema.State.DISABLEDPUSHED;
        } else if (buttonModel.isRollover() && ((JMenu)param1JMenuItem).isTopLevelMenu()) {
          TMSchema.State state1 = state;
          state = param1JMenuItem.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT;
          for (MenuElement menuElement : ((JMenuBar)param1JMenuItem.getParent()).getSubElements()) {
            if (((JMenuItem)menuElement).isSelected()) {
              state = state1;
              break;
            } 
          } 
        } 
        if (!((JMenu)param1JMenuItem).isTopLevelMenu())
          if (state == TMSchema.State.PUSHED) {
            state = TMSchema.State.HOT;
          } else if (state == TMSchema.State.DISABLEDPUSHED) {
            state = TMSchema.State.DISABLEDHOT;
          }  
        if (((JMenu)param1JMenuItem).isTopLevelMenu() && WindowsMenuItemUI.isVistaPainting() && !WindowsMenuBarUI.isActive(param1JMenuItem))
          state = TMSchema.State.DISABLED; 
        return state;
      }
      
      public TMSchema.Part getPart(JMenuItem param1JMenuItem) { return ((JMenu)param1JMenuItem).isTopLevelMenu() ? TMSchema.Part.MP_BARITEM : TMSchema.Part.MP_POPUPITEM; }
    };
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsMenuUI(); }
  
  protected void installDefaults() {
    super.installDefaults();
    if (!WindowsLookAndFeel.isClassicWindows())
      this.menuItem.setRolloverEnabled(true); 
    this.menuBarHeight = Integer.valueOf(UIManager.getInt("MenuBar.height"));
    Object object = UIManager.get("MenuBar.rolloverEnabled");
    this.hotTrackingOn = (object instanceof Boolean) ? ((Boolean)object).booleanValue() : 1;
  }
  
  protected void paintBackground(Graphics paramGraphics, JMenuItem paramJMenuItem, Color paramColor) {
    if (WindowsMenuItemUI.isVistaPainting()) {
      WindowsMenuItemUI.paintBackground(this.accessor, paramGraphics, paramJMenuItem, paramColor);
      return;
    } 
    JMenu jMenu = (JMenu)paramJMenuItem;
    ButtonModel buttonModel = jMenu.getModel();
    if (WindowsLookAndFeel.isClassicWindows() || !jMenu.isTopLevelMenu() || (XPStyle.getXP() != null && (buttonModel.isArmed() || buttonModel.isSelected()))) {
      super.paintBackground(paramGraphics, jMenu, paramColor);
      return;
    } 
    Color color1 = paramGraphics.getColor();
    int i = jMenu.getWidth();
    int j = jMenu.getHeight();
    UIDefaults uIDefaults = UIManager.getLookAndFeelDefaults();
    Color color2 = uIDefaults.getColor("controlLtHighlight");
    Color color3 = uIDefaults.getColor("controlShadow");
    paramGraphics.setColor(jMenu.getBackground());
    paramGraphics.fillRect(0, 0, i, j);
    if (jMenu.isOpaque())
      if (buttonModel.isArmed() || buttonModel.isSelected()) {
        paramGraphics.setColor(color3);
        paramGraphics.drawLine(0, 0, i - 1, 0);
        paramGraphics.drawLine(0, 0, 0, j - 2);
        paramGraphics.setColor(color2);
        paramGraphics.drawLine(i - 1, 0, i - 1, j - 2);
        paramGraphics.drawLine(0, j - 2, i - 1, j - 2);
      } else if (buttonModel.isRollover() && buttonModel.isEnabled()) {
        boolean bool = false;
        MenuElement[] arrayOfMenuElement = ((JMenuBar)jMenu.getParent()).getSubElements();
        for (byte b = 0; b < arrayOfMenuElement.length; b++) {
          if (((JMenuItem)arrayOfMenuElement[b]).isSelected()) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          if (XPStyle.getXP() != null) {
            paramGraphics.setColor(this.selectionBackground);
            paramGraphics.fillRect(0, 0, i, j);
          } else {
            paramGraphics.setColor(color2);
            paramGraphics.drawLine(0, 0, i - 1, 0);
            paramGraphics.drawLine(0, 0, 0, j - 2);
            paramGraphics.setColor(color3);
            paramGraphics.drawLine(i - 1, 0, i - 1, j - 2);
            paramGraphics.drawLine(0, j - 2, i - 1, j - 2);
          }  
      }  
    paramGraphics.setColor(color1);
  }
  
  protected void paintText(Graphics paramGraphics, JMenuItem paramJMenuItem, Rectangle paramRectangle, String paramString) {
    if (WindowsMenuItemUI.isVistaPainting()) {
      WindowsMenuItemUI.paintText(this.accessor, paramGraphics, paramJMenuItem, paramRectangle, paramString);
      return;
    } 
    JMenu jMenu = (JMenu)paramJMenuItem;
    ButtonModel buttonModel = paramJMenuItem.getModel();
    Color color = paramGraphics.getColor();
    boolean bool = buttonModel.isRollover();
    if (bool && jMenu.isTopLevelMenu()) {
      MenuElement[] arrayOfMenuElement = ((JMenuBar)jMenu.getParent()).getSubElements();
      for (byte b = 0; b < arrayOfMenuElement.length; b++) {
        if (((JMenuItem)arrayOfMenuElement[b]).isSelected()) {
          bool = false;
          break;
        } 
      } 
    } 
    if ((buttonModel.isSelected() && (WindowsLookAndFeel.isClassicWindows() || !jMenu.isTopLevelMenu())) || (XPStyle.getXP() != null && (bool || buttonModel.isArmed() || buttonModel.isSelected())))
      paramGraphics.setColor(this.selectionForeground); 
    WindowsGraphicsUtils.paintText(paramGraphics, paramJMenuItem, paramRectangle, paramString, 0);
    paramGraphics.setColor(color);
  }
  
  protected MouseInputListener createMouseInputListener(JComponent paramJComponent) { return new WindowsMouseInputHandler(); }
  
  protected Dimension getPreferredMenuItemSize(JComponent paramJComponent, Icon paramIcon1, Icon paramIcon2, int paramInt) {
    Dimension dimension = super.getPreferredMenuItemSize(paramJComponent, paramIcon1, paramIcon2, paramInt);
    if (paramJComponent instanceof JMenu && ((JMenu)paramJComponent).isTopLevelMenu() && this.menuBarHeight != null && dimension.height < this.menuBarHeight.intValue())
      dimension.height = this.menuBarHeight.intValue(); 
    return dimension;
  }
  
  protected class WindowsMouseInputHandler extends BasicMenuUI.MouseInputHandler {
    protected WindowsMouseInputHandler() { super(WindowsMenuUI.this); }
    
    public void mouseEntered(MouseEvent param1MouseEvent) {
      super.mouseEntered(param1MouseEvent);
      JMenu jMenu = (JMenu)param1MouseEvent.getSource();
      if (WindowsMenuUI.this.hotTrackingOn && jMenu.isTopLevelMenu() && jMenu.isRolloverEnabled()) {
        jMenu.getModel().setRollover(true);
        WindowsMenuUI.this.menuItem.repaint();
      } 
    }
    
    public void mouseExited(MouseEvent param1MouseEvent) {
      super.mouseExited(param1MouseEvent);
      JMenu jMenu = (JMenu)param1MouseEvent.getSource();
      ButtonModel buttonModel = jMenu.getModel();
      if (jMenu.isRolloverEnabled()) {
        buttonModel.setRollover(false);
        WindowsMenuUI.this.menuItem.repaint();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsMenuUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */