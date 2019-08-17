package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class MotifIconFactory implements Serializable {
  private static Icon checkBoxIcon;
  
  private static Icon radioButtonIcon;
  
  private static Icon menuItemCheckIcon;
  
  private static Icon menuItemArrowIcon;
  
  private static Icon menuArrowIcon;
  
  public static Icon getMenuItemCheckIcon() { return null; }
  
  public static Icon getMenuItemArrowIcon() {
    if (menuItemArrowIcon == null)
      menuItemArrowIcon = new MenuItemArrowIcon(null); 
    return menuItemArrowIcon;
  }
  
  public static Icon getMenuArrowIcon() {
    if (menuArrowIcon == null)
      menuArrowIcon = new MenuArrowIcon(null); 
    return menuArrowIcon;
  }
  
  public static Icon getCheckBoxIcon() {
    if (checkBoxIcon == null)
      checkBoxIcon = new CheckBoxIcon(null); 
    return checkBoxIcon;
  }
  
  public static Icon getRadioButtonIcon() {
    if (radioButtonIcon == null)
      radioButtonIcon = new RadioButtonIcon(null); 
    return radioButtonIcon;
  }
  
  private static class CheckBoxIcon implements Icon, UIResource, Serializable {
    static final int csize = 13;
    
    private Color control = UIManager.getColor("control");
    
    private Color foreground = UIManager.getColor("CheckBox.foreground");
    
    private Color shadow = UIManager.getColor("controlShadow");
    
    private Color highlight = UIManager.getColor("controlHighlight");
    
    private Color lightShadow = UIManager.getColor("controlLightShadow");
    
    private CheckBoxIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      boolean bool1 = false;
      if (abstractButton instanceof JCheckBox)
        bool1 = ((JCheckBox)abstractButton).isBorderPaintedFlat(); 
      boolean bool2 = buttonModel.isPressed();
      boolean bool3 = buttonModel.isArmed();
      boolean bool4 = buttonModel.isEnabled();
      boolean bool5 = buttonModel.isSelected();
      boolean bool6 = ((bool2 && !bool3 && bool5) || (bool2 && bool3 && !bool5)) ? 1 : 0;
      boolean bool7 = ((bool2 && !bool3 && !bool5) || (bool2 && bool3 && bool5)) ? 1 : 0;
      boolean bool8 = ((!bool2 && bool3 && bool5) || (!bool2 && !bool3 && bool5)) ? 1 : 0;
      if (bool1) {
        param1Graphics.setColor(this.shadow);
        param1Graphics.drawRect(param1Int1 + 2, param1Int2, 12, 12);
        if (bool7 || bool6) {
          param1Graphics.setColor(this.control);
          param1Graphics.fillRect(param1Int1 + 3, param1Int2 + 1, 11, 11);
        } 
      } 
      if (bool6) {
        drawCheckBezel(param1Graphics, param1Int1, param1Int2, 13, true, false, false, bool1);
      } else if (bool7) {
        drawCheckBezel(param1Graphics, param1Int1, param1Int2, 13, true, true, false, bool1);
      } else if (bool8) {
        drawCheckBezel(param1Graphics, param1Int1, param1Int2, 13, false, false, true, bool1);
      } else if (!bool1) {
        drawCheckBezelOut(param1Graphics, param1Int1, param1Int2, 13);
      } 
    }
    
    public int getIconWidth() { return 13; }
    
    public int getIconHeight() { return 13; }
    
    public void drawCheckBezelOut(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3) {
      Color color1 = UIManager.getColor("controlShadow");
      int i = param1Int3;
      int j = param1Int3;
      Color color2 = param1Graphics.getColor();
      param1Graphics.translate(param1Int1, param1Int2);
      param1Graphics.setColor(this.highlight);
      param1Graphics.drawLine(0, 0, 0, j - 1);
      param1Graphics.drawLine(1, 0, i - 1, 0);
      param1Graphics.setColor(this.shadow);
      param1Graphics.drawLine(1, j - 1, i - 1, j - 1);
      param1Graphics.drawLine(i - 1, j - 1, i - 1, 1);
      param1Graphics.translate(-param1Int1, -param1Int2);
      param1Graphics.setColor(color2);
    }
    
    public void drawCheckBezel(Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3, boolean param1Boolean4) {
      Color color = param1Graphics.getColor();
      param1Graphics.translate(param1Int1, param1Int2);
      if (!param1Boolean4) {
        if (param1Boolean2) {
          param1Graphics.setColor(this.control);
          param1Graphics.fillRect(1, 1, param1Int3 - 2, param1Int3 - 2);
          param1Graphics.setColor(this.shadow);
        } else {
          param1Graphics.setColor(this.lightShadow);
          param1Graphics.fillRect(0, 0, param1Int3, param1Int3);
          param1Graphics.setColor(this.highlight);
        } 
        param1Graphics.drawLine(1, param1Int3 - 1, param1Int3 - 2, param1Int3 - 1);
        if (param1Boolean1) {
          param1Graphics.drawLine(2, param1Int3 - 2, param1Int3 - 3, param1Int3 - 2);
          param1Graphics.drawLine(param1Int3 - 2, 2, param1Int3 - 2, param1Int3 - 1);
          if (param1Boolean2) {
            param1Graphics.setColor(this.highlight);
          } else {
            param1Graphics.setColor(this.shadow);
          } 
          param1Graphics.drawLine(1, 2, 1, param1Int3 - 2);
          param1Graphics.drawLine(1, 1, param1Int3 - 3, 1);
          if (param1Boolean2) {
            param1Graphics.setColor(this.shadow);
          } else {
            param1Graphics.setColor(this.highlight);
          } 
        } 
        param1Graphics.drawLine(param1Int3 - 1, 1, param1Int3 - 1, param1Int3 - 1);
        if (param1Boolean2) {
          param1Graphics.setColor(this.highlight);
        } else {
          param1Graphics.setColor(this.shadow);
        } 
        param1Graphics.drawLine(0, 1, 0, param1Int3 - 1);
        param1Graphics.drawLine(0, 0, param1Int3 - 1, 0);
      } 
      if (param1Boolean3) {
        param1Graphics.setColor(this.foreground);
        param1Graphics.drawLine(param1Int3 - 2, 1, param1Int3 - 2, 2);
        param1Graphics.drawLine(param1Int3 - 3, 2, param1Int3 - 3, 3);
        param1Graphics.drawLine(param1Int3 - 4, 3, param1Int3 - 4, 4);
        param1Graphics.drawLine(param1Int3 - 5, 4, param1Int3 - 5, 6);
        param1Graphics.drawLine(param1Int3 - 6, 5, param1Int3 - 6, 8);
        param1Graphics.drawLine(param1Int3 - 7, 6, param1Int3 - 7, 10);
        param1Graphics.drawLine(param1Int3 - 8, 7, param1Int3 - 8, 10);
        param1Graphics.drawLine(param1Int3 - 9, 6, param1Int3 - 9, 9);
        param1Graphics.drawLine(param1Int3 - 10, 5, param1Int3 - 10, 8);
        param1Graphics.drawLine(param1Int3 - 11, 5, param1Int3 - 11, 7);
        param1Graphics.drawLine(param1Int3 - 12, 6, param1Int3 - 12, 6);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
      param1Graphics.setColor(color);
    }
  }
  
  private static class MenuArrowIcon implements Icon, UIResource, Serializable {
    private Color focus = UIManager.getColor("windowBorder");
    
    private Color shadow = UIManager.getColor("controlShadow");
    
    private Color highlight = UIManager.getColor("controlHighlight");
    
    private MenuArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      int i = getIconWidth();
      int j = getIconHeight();
      Color color = param1Graphics.getColor();
      if (buttonModel.isSelected()) {
        if (MotifGraphicsUtils.isLeftToRight(param1Component)) {
          param1Graphics.setColor(this.shadow);
          param1Graphics.fillRect(param1Int1 + 1, param1Int2 + 1, 2, j);
          param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 2, param1Int1 + 4, param1Int2 + 2);
          param1Graphics.drawLine(param1Int1 + 6, param1Int2 + 3, param1Int1 + 6, param1Int2 + 3);
          param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 4, param1Int1 + 8, param1Int2 + 5);
          param1Graphics.setColor(this.focus);
          param1Graphics.fillRect(param1Int1 + 2, param1Int2 + 2, 2, j - 2);
          param1Graphics.fillRect(param1Int1 + 4, param1Int2 + 3, 2, j - 4);
          param1Graphics.fillRect(param1Int1 + 6, param1Int2 + 4, 2, j - 6);
          param1Graphics.setColor(this.highlight);
          param1Graphics.drawLine(param1Int1 + 2, param1Int2 + j, param1Int1 + 2, param1Int2 + j);
          param1Graphics.drawLine(param1Int1 + 4, param1Int2 + j - 1, param1Int1 + 4, param1Int2 + j - 1);
          param1Graphics.drawLine(param1Int1 + 6, param1Int2 + j - 2, param1Int1 + 6, param1Int2 + j - 2);
          param1Graphics.drawLine(param1Int1 + 8, param1Int2 + j - 4, param1Int1 + 8, param1Int2 + j - 3);
        } else {
          param1Graphics.setColor(this.highlight);
          param1Graphics.fillRect(param1Int1 + 7, param1Int2 + 1, 2, 10);
          param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 9, param1Int1 + 5, param1Int2 + 9);
          param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 8, param1Int1 + 3, param1Int2 + 8);
          param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 6, param1Int1 + 1, param1Int2 + 7);
          param1Graphics.setColor(this.focus);
          param1Graphics.fillRect(param1Int1 + 6, param1Int2 + 2, 2, 8);
          param1Graphics.fillRect(param1Int1 + 4, param1Int2 + 3, 2, 6);
          param1Graphics.fillRect(param1Int1 + 2, param1Int2 + 4, 2, 4);
          param1Graphics.setColor(this.shadow);
          param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 4, param1Int1 + 1, param1Int2 + 5);
          param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 3, param1Int1 + 3, param1Int2 + 3);
          param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 2, param1Int1 + 5, param1Int2 + 2);
          param1Graphics.drawLine(param1Int1 + 7, param1Int2 + 1, param1Int1 + 7, param1Int2 + 1);
        } 
      } else if (MotifGraphicsUtils.isLeftToRight(param1Component)) {
        param1Graphics.setColor(this.highlight);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 1, param1Int1 + 1, param1Int2 + j);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 1, param1Int1 + 2, param1Int2 + j - 2);
        param1Graphics.fillRect(param1Int1 + 3, param1Int2 + 2, 2, 2);
        param1Graphics.fillRect(param1Int1 + 5, param1Int2 + 3, 2, 2);
        param1Graphics.fillRect(param1Int1 + 7, param1Int2 + 4, 2, 2);
        param1Graphics.setColor(this.shadow);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + j - 1, param1Int1 + 2, param1Int2 + j);
        param1Graphics.fillRect(param1Int1 + 3, param1Int2 + j - 2, 2, 2);
        param1Graphics.fillRect(param1Int1 + 5, param1Int2 + j - 3, 2, 2);
        param1Graphics.fillRect(param1Int1 + 7, param1Int2 + j - 4, 2, 2);
        param1Graphics.setColor(color);
      } else {
        param1Graphics.setColor(this.highlight);
        param1Graphics.fillRect(param1Int1 + 1, param1Int2 + 4, 2, 2);
        param1Graphics.fillRect(param1Int1 + 3, param1Int2 + 3, 2, 2);
        param1Graphics.fillRect(param1Int1 + 5, param1Int2 + 2, 2, 2);
        param1Graphics.drawLine(param1Int1 + 7, param1Int2 + 1, param1Int1 + 7, param1Int2 + 2);
        param1Graphics.setColor(this.shadow);
        param1Graphics.fillRect(param1Int1 + 1, param1Int2 + j - 4, 2, 2);
        param1Graphics.fillRect(param1Int1 + 3, param1Int2 + j - 3, 2, 2);
        param1Graphics.fillRect(param1Int1 + 5, param1Int2 + j - 2, 2, 2);
        param1Graphics.drawLine(param1Int1 + 7, param1Int2 + 3, param1Int1 + 7, param1Int2 + j);
        param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 1, param1Int1 + 8, param1Int2 + j);
        param1Graphics.setColor(color);
      } 
    }
    
    public int getIconWidth() { return 10; }
    
    public int getIconHeight() { return 10; }
  }
  
  private static class MenuItemArrowIcon implements Icon, UIResource, Serializable {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 0; }
    
    public int getIconHeight() { return 0; }
  }
  
  private static class MenuItemCheckIcon implements Icon, UIResource, Serializable {
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 0; }
    
    public int getIconHeight() { return 0; }
  }
  
  private static class RadioButtonIcon implements Icon, UIResource, Serializable {
    private Color dot = UIManager.getColor("activeCaptionBorder");
    
    private Color highlight = UIManager.getColor("controlHighlight");
    
    private Color shadow = UIManager.getColor("controlShadow");
    
    private RadioButtonIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      int i = getIconWidth();
      int j = getIconHeight();
      boolean bool1 = buttonModel.isPressed();
      boolean bool2 = buttonModel.isArmed();
      boolean bool3 = buttonModel.isEnabled();
      boolean bool4 = buttonModel.isSelected();
      boolean bool = ((bool1 && !bool2 && bool4) || (bool1 && bool2 && !bool4) || (!bool1 && bool2 && bool4) || (!bool1 && !bool2 && bool4)) ? 1 : 0;
      if (bool) {
        param1Graphics.setColor(this.shadow);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 0, param1Int1 + 8, param1Int2 + 0);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 1, param1Int1 + 4, param1Int2 + 1);
        param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 1, param1Int1 + 9, param1Int2 + 1);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 2, param1Int1 + 2, param1Int2 + 2);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 3, param1Int1 + 1, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1, param1Int2 + 4, param1Int1, param1Int2 + 9);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 10, param1Int1 + 1, param1Int2 + 10);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 11, param1Int1 + 2, param1Int2 + 11);
        param1Graphics.setColor(this.highlight);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 12, param1Int1 + 4, param1Int2 + 12);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 13, param1Int1 + 8, param1Int2 + 13);
        param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 12, param1Int1 + 10, param1Int2 + 12);
        param1Graphics.drawLine(param1Int1 + 11, param1Int2 + 11, param1Int1 + 11, param1Int2 + 11);
        param1Graphics.drawLine(param1Int1 + 12, param1Int2 + 10, param1Int1 + 12, param1Int2 + 10);
        param1Graphics.drawLine(param1Int1 + 13, param1Int2 + 9, param1Int1 + 13, param1Int2 + 4);
        param1Graphics.drawLine(param1Int1 + 12, param1Int2 + 3, param1Int1 + 12, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1 + 11, param1Int2 + 2, param1Int1 + 11, param1Int2 + 2);
        param1Graphics.drawLine(param1Int1 + 10, param1Int2 + 1, param1Int1 + 10, param1Int2 + 1);
        param1Graphics.setColor(this.dot);
        param1Graphics.fillRect(param1Int1 + 4, param1Int2 + 5, 6, 4);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 4, param1Int1 + 8, param1Int2 + 4);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 9, param1Int1 + 8, param1Int2 + 9);
      } else {
        param1Graphics.setColor(this.highlight);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 0, param1Int1 + 8, param1Int2 + 0);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 1, param1Int1 + 4, param1Int2 + 1);
        param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 1, param1Int1 + 9, param1Int2 + 1);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 2, param1Int1 + 2, param1Int2 + 2);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 3, param1Int1 + 1, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1, param1Int2 + 4, param1Int1, param1Int2 + 9);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 10, param1Int1 + 1, param1Int2 + 10);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 11, param1Int1 + 2, param1Int2 + 11);
        param1Graphics.setColor(this.shadow);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 12, param1Int1 + 4, param1Int2 + 12);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 13, param1Int1 + 8, param1Int2 + 13);
        param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 12, param1Int1 + 10, param1Int2 + 12);
        param1Graphics.drawLine(param1Int1 + 11, param1Int2 + 11, param1Int1 + 11, param1Int2 + 11);
        param1Graphics.drawLine(param1Int1 + 12, param1Int2 + 10, param1Int1 + 12, param1Int2 + 10);
        param1Graphics.drawLine(param1Int1 + 13, param1Int2 + 9, param1Int1 + 13, param1Int2 + 4);
        param1Graphics.drawLine(param1Int1 + 12, param1Int2 + 3, param1Int1 + 12, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1 + 11, param1Int2 + 2, param1Int1 + 11, param1Int2 + 2);
        param1Graphics.drawLine(param1Int1 + 10, param1Int2 + 1, param1Int1 + 10, param1Int2 + 1);
      } 
    }
    
    public int getIconWidth() { return 14; }
    
    public int getIconHeight() { return 14; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */