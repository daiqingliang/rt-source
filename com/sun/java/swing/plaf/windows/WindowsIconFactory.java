package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.UIResource;
import sun.swing.MenuItemCheckIconFactory;

public class WindowsIconFactory implements Serializable {
  private static Icon frame_closeIcon;
  
  private static Icon frame_iconifyIcon;
  
  private static Icon frame_maxIcon;
  
  private static Icon frame_minIcon;
  
  private static Icon frame_resizeIcon;
  
  private static Icon checkBoxIcon;
  
  private static Icon radioButtonIcon;
  
  private static Icon checkBoxMenuItemIcon;
  
  private static Icon radioButtonMenuItemIcon;
  
  private static Icon menuItemCheckIcon;
  
  private static Icon menuItemArrowIcon;
  
  private static Icon menuArrowIcon;
  
  private static VistaMenuItemCheckIconFactory menuItemCheckIconFactory;
  
  public static Icon getMenuItemCheckIcon() {
    if (menuItemCheckIcon == null)
      menuItemCheckIcon = new MenuItemCheckIcon(null); 
    return menuItemCheckIcon;
  }
  
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
  
  public static Icon getCheckBoxMenuItemIcon() {
    if (checkBoxMenuItemIcon == null)
      checkBoxMenuItemIcon = new CheckBoxMenuItemIcon(null); 
    return checkBoxMenuItemIcon;
  }
  
  public static Icon getRadioButtonMenuItemIcon() {
    if (radioButtonMenuItemIcon == null)
      radioButtonMenuItemIcon = new RadioButtonMenuItemIcon(null); 
    return radioButtonMenuItemIcon;
  }
  
  static VistaMenuItemCheckIconFactory getMenuItemCheckIconFactory() {
    if (menuItemCheckIconFactory == null)
      menuItemCheckIconFactory = new VistaMenuItemCheckIconFactory(); 
    return menuItemCheckIconFactory;
  }
  
  public static Icon createFrameCloseIcon() {
    if (frame_closeIcon == null)
      frame_closeIcon = new FrameButtonIcon(TMSchema.Part.WP_CLOSEBUTTON, null); 
    return frame_closeIcon;
  }
  
  public static Icon createFrameIconifyIcon() {
    if (frame_iconifyIcon == null)
      frame_iconifyIcon = new FrameButtonIcon(TMSchema.Part.WP_MINBUTTON, null); 
    return frame_iconifyIcon;
  }
  
  public static Icon createFrameMaximizeIcon() {
    if (frame_maxIcon == null)
      frame_maxIcon = new FrameButtonIcon(TMSchema.Part.WP_MAXBUTTON, null); 
    return frame_maxIcon;
  }
  
  public static Icon createFrameMinimizeIcon() {
    if (frame_minIcon == null)
      frame_minIcon = new FrameButtonIcon(TMSchema.Part.WP_RESTOREBUTTON, null); 
    return frame_minIcon;
  }
  
  public static Icon createFrameResizeIcon() {
    if (frame_resizeIcon == null)
      frame_resizeIcon = new ResizeIcon(null); 
    return frame_resizeIcon;
  }
  
  private static class CheckBoxIcon implements Icon, Serializable {
    static final int csize = 13;
    
    private CheckBoxIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      JCheckBox jCheckBox = (JCheckBox)param1Component;
      ButtonModel buttonModel = jCheckBox.getModel();
      XPStyle xPStyle = XPStyle.getXP();
      if (xPStyle != null) {
        TMSchema.State state;
        if (buttonModel.isSelected()) {
          state = TMSchema.State.CHECKEDNORMAL;
          if (!buttonModel.isEnabled()) {
            state = TMSchema.State.CHECKEDDISABLED;
          } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
            state = TMSchema.State.CHECKEDPRESSED;
          } else if (buttonModel.isRollover()) {
            state = TMSchema.State.CHECKEDHOT;
          } 
        } else {
          state = TMSchema.State.UNCHECKEDNORMAL;
          if (!buttonModel.isEnabled()) {
            state = TMSchema.State.UNCHECKEDDISABLED;
          } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
            state = TMSchema.State.UNCHECKEDPRESSED;
          } else if (buttonModel.isRollover()) {
            state = TMSchema.State.UNCHECKEDHOT;
          } 
        } 
        TMSchema.Part part = TMSchema.Part.BP_CHECKBOX;
        xPStyle.getSkin(param1Component, part).paintSkin(param1Graphics, param1Int1, param1Int2, state);
      } else {
        if (!jCheckBox.isBorderPaintedFlat()) {
          param1Graphics.setColor(UIManager.getColor("CheckBox.shadow"));
          param1Graphics.drawLine(param1Int1, param1Int2, param1Int1 + 11, param1Int2);
          param1Graphics.drawLine(param1Int1, param1Int2 + 1, param1Int1, param1Int2 + 11);
          param1Graphics.setColor(UIManager.getColor("CheckBox.highlight"));
          param1Graphics.drawLine(param1Int1 + 12, param1Int2, param1Int1 + 12, param1Int2 + 12);
          param1Graphics.drawLine(param1Int1, param1Int2 + 12, param1Int1 + 11, param1Int2 + 12);
          param1Graphics.setColor(UIManager.getColor("CheckBox.darkShadow"));
          param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 1, param1Int1 + 10, param1Int2 + 1);
          param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 2, param1Int1 + 1, param1Int2 + 10);
          param1Graphics.setColor(UIManager.getColor("CheckBox.light"));
          param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 11, param1Int1 + 11, param1Int2 + 11);
          param1Graphics.drawLine(param1Int1 + 11, param1Int2 + 1, param1Int1 + 11, param1Int2 + 10);
          if ((buttonModel.isPressed() && buttonModel.isArmed()) || !buttonModel.isEnabled()) {
            param1Graphics.setColor(UIManager.getColor("CheckBox.background"));
          } else {
            param1Graphics.setColor(UIManager.getColor("CheckBox.interiorBackground"));
          } 
          param1Graphics.fillRect(param1Int1 + 2, param1Int2 + 2, 9, 9);
        } else {
          param1Graphics.setColor(UIManager.getColor("CheckBox.shadow"));
          param1Graphics.drawRect(param1Int1 + 1, param1Int2 + 1, 10, 10);
          if ((buttonModel.isPressed() && buttonModel.isArmed()) || !buttonModel.isEnabled()) {
            param1Graphics.setColor(UIManager.getColor("CheckBox.background"));
          } else {
            param1Graphics.setColor(UIManager.getColor("CheckBox.interiorBackground"));
          } 
          param1Graphics.fillRect(param1Int1 + 2, param1Int2 + 2, 9, 9);
        } 
        if (buttonModel.isEnabled()) {
          param1Graphics.setColor(UIManager.getColor("CheckBox.foreground"));
        } else {
          param1Graphics.setColor(UIManager.getColor("CheckBox.shadow"));
        } 
        if (buttonModel.isSelected()) {
          param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 3, param1Int1 + 9, param1Int2 + 3);
          param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 4, param1Int1 + 9, param1Int2 + 4);
          param1Graphics.drawLine(param1Int1 + 7, param1Int2 + 5, param1Int1 + 9, param1Int2 + 5);
          param1Graphics.drawLine(param1Int1 + 6, param1Int2 + 6, param1Int1 + 8, param1Int2 + 6);
          param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 7, param1Int1 + 7, param1Int2 + 7);
          param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 8, param1Int1 + 6, param1Int2 + 8);
          param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 9, param1Int1 + 5, param1Int2 + 9);
          param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 5, param1Int1 + 3, param1Int2 + 5);
          param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 6, param1Int1 + 4, param1Int2 + 6);
        } 
      } 
    }
    
    public int getIconWidth() {
      XPStyle xPStyle = XPStyle.getXP();
      return (xPStyle != null) ? xPStyle.getSkin(null, TMSchema.Part.BP_CHECKBOX).getWidth() : 13;
    }
    
    public int getIconHeight() {
      XPStyle xPStyle = XPStyle.getXP();
      return (xPStyle != null) ? xPStyle.getSkin(null, TMSchema.Part.BP_CHECKBOX).getHeight() : 13;
    }
  }
  
  private static class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable {
    private CheckBoxMenuItemIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      boolean bool = buttonModel.isSelected();
      if (bool) {
        param1Int2 -= getIconHeight() / 2;
        param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 3, param1Int1 + 9, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 4, param1Int1 + 9, param1Int2 + 4);
        param1Graphics.drawLine(param1Int1 + 7, param1Int2 + 5, param1Int1 + 9, param1Int2 + 5);
        param1Graphics.drawLine(param1Int1 + 6, param1Int2 + 6, param1Int1 + 8, param1Int2 + 6);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 7, param1Int1 + 7, param1Int2 + 7);
        param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 8, param1Int1 + 6, param1Int2 + 8);
        param1Graphics.drawLine(param1Int1 + 5, param1Int2 + 9, param1Int1 + 5, param1Int2 + 9);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 5, param1Int1 + 3, param1Int2 + 5);
        param1Graphics.drawLine(param1Int1 + 3, param1Int2 + 6, param1Int1 + 4, param1Int2 + 6);
      } 
    }
    
    public int getIconWidth() { return 9; }
    
    public int getIconHeight() { return 9; }
  }
  
  private static class FrameButtonIcon implements Icon, Serializable {
    private TMSchema.Part part;
    
    private FrameButtonIcon(TMSchema.Part param1Part) { this.part = param1Part; }
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      int i = getIconWidth();
      int j = getIconHeight();
      XPStyle xPStyle = XPStyle.getXP();
      if (xPStyle != null) {
        TMSchema.State state;
        XPStyle.Skin skin = xPStyle.getSkin(param1Component, this.part);
        AbstractButton abstractButton = (AbstractButton)param1Component;
        ButtonModel buttonModel = abstractButton.getModel();
        JInternalFrame jInternalFrame = (JInternalFrame)SwingUtilities.getAncestorOfClass(JInternalFrame.class, abstractButton);
        boolean bool = (jInternalFrame != null && jInternalFrame.isSelected()) ? 1 : 0;
        if (bool) {
          if (!buttonModel.isEnabled()) {
            state = TMSchema.State.DISABLED;
          } else if (buttonModel.isArmed() && buttonModel.isPressed()) {
            state = TMSchema.State.PUSHED;
          } else if (buttonModel.isRollover()) {
            state = TMSchema.State.HOT;
          } else {
            state = TMSchema.State.NORMAL;
          } 
        } else if (!buttonModel.isEnabled()) {
          state = TMSchema.State.INACTIVEDISABLED;
        } else if (buttonModel.isArmed() && buttonModel.isPressed()) {
          state = TMSchema.State.INACTIVEPUSHED;
        } else if (buttonModel.isRollover()) {
          state = TMSchema.State.INACTIVEHOT;
        } else {
          state = TMSchema.State.INACTIVENORMAL;
        } 
        skin.paintSkin(param1Graphics, 0, 0, i, j, state);
      } else {
        param1Graphics.setColor(Color.black);
        int k = i / 12 + 2;
        int m = j / 5;
        int n = j - m * 2 - 1;
        int i1 = i * 3 / 4 - 3;
        int i2 = Math.max(j / 8, 2);
        int i3 = Math.max(i / 15, 1);
        if (this.part == TMSchema.Part.WP_CLOSEBUTTON) {
          boolean bool;
          if (i > 47) {
            bool = true;
          } else if (i > 37) {
            bool = true;
          } else if (i > 26) {
            bool = true;
          } else if (i > 16) {
            bool = true;
          } else if (i > 12) {
            bool = true;
          } else {
            bool = true;
          } 
          m = j / 12 + 2;
          if (bool == true) {
            if (i1 % 2 == 1) {
              k++;
              i1++;
            } 
            param1Graphics.drawLine(k, m, k + i1 - 2, m + i1 - 2);
            param1Graphics.drawLine(k + i1 - 2, m, k, m + i1 - 2);
          } else if (bool == 2) {
            if (i1 > 6) {
              k++;
              i1--;
            } 
            param1Graphics.drawLine(k, m, k + i1 - 2, m + i1 - 2);
            param1Graphics.drawLine(k + i1 - 2, m, k, m + i1 - 2);
            param1Graphics.drawLine(k + 1, m, k + i1 - 1, m + i1 - 2);
            param1Graphics.drawLine(k + i1 - 1, m, k + 1, m + i1 - 2);
          } else {
            k += 2;
            m++;
            i1 -= 2;
            param1Graphics.drawLine(k, m, k + i1 - 1, m + i1 - 1);
            param1Graphics.drawLine(k + i1 - 1, m, k, m + i1 - 1);
            param1Graphics.drawLine(k + 1, m, k + i1 - 1, m + i1 - 2);
            param1Graphics.drawLine(k + i1 - 2, m, k, m + i1 - 2);
            param1Graphics.drawLine(k, m + 1, k + i1 - 2, m + i1 - 1);
            param1Graphics.drawLine(k + i1 - 1, m + 1, k + 1, m + i1 - 1);
            for (int i4 = 4; i4 <= bool; i4++) {
              param1Graphics.drawLine(k + i4 - 2, m, k + i1 - 1, m + i1 - i4 + 1);
              param1Graphics.drawLine(k, m + i4 - 2, k + i1 - i4 + 1, m + i1 - 1);
              param1Graphics.drawLine(k + i1 - i4 + 1, m, k, m + i1 - i4 + 1);
              param1Graphics.drawLine(k + i1 - 1, m + i4 - 2, k + i4 - 2, m + i1 - 1);
            } 
          } 
        } else if (this.part == TMSchema.Part.WP_MINBUTTON) {
          param1Graphics.fillRect(k, m + n - i2, i1 - i1 / 3, i2);
        } else if (this.part == TMSchema.Part.WP_MAXBUTTON) {
          param1Graphics.fillRect(k, m, i1, i2);
          param1Graphics.fillRect(k, m, i3, n);
          param1Graphics.fillRect(k + i1 - i3, m, i3, n);
          param1Graphics.fillRect(k, m + n - i3, i1, i3);
        } else if (this.part == TMSchema.Part.WP_RESTOREBUTTON) {
          param1Graphics.fillRect(k + i1 / 3, m, i1 - i1 / 3, i2);
          param1Graphics.fillRect(k + i1 / 3, m, i3, n / 3);
          param1Graphics.fillRect(k + i1 - i3, m, i3, n - n / 3);
          param1Graphics.fillRect(k + i1 - i1 / 3, m + n - n / 3 - i3, i1 / 3, i3);
          param1Graphics.fillRect(k, m + n / 3, i1 - i1 / 3, i2);
          param1Graphics.fillRect(k, m + n / 3, i3, n - n / 3);
          param1Graphics.fillRect(k + i1 - i1 / 3 - i3, m + n / 3, i3, n - n / 3);
          param1Graphics.fillRect(k, m + n - i3, i1 - i1 / 3, i3);
        } 
      } 
    }
    
    public int getIconWidth() {
      int i;
      if (XPStyle.getXP() != null) {
        i = UIManager.getInt("InternalFrame.titleButtonHeight") - 2;
        Dimension dimension = XPStyle.getPartSize(TMSchema.Part.WP_CLOSEBUTTON, TMSchema.State.NORMAL);
        if (dimension != null && dimension.width != 0 && dimension.height != 0)
          i = (int)(i * dimension.width / dimension.height); 
      } else {
        i = UIManager.getInt("InternalFrame.titleButtonWidth") - 2;
      } 
      if (XPStyle.getXP() != null)
        i -= 2; 
      return i;
    }
    
    public int getIconHeight() { return UIManager.getInt("InternalFrame.titleButtonHeight") - 4; }
  }
  
  private static class MenuArrowIcon implements Icon, UIResource, Serializable {
    private MenuArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      XPStyle xPStyle = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(xPStyle)) {
        TMSchema.State state = TMSchema.State.NORMAL;
        if (param1Component instanceof JMenuItem)
          state = ((JMenuItem)param1Component).getModel().isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED; 
        XPStyle.Skin skin = xPStyle.getSkin(param1Component, TMSchema.Part.MP_POPUPSUBMENU);
        if (WindowsGraphicsUtils.isLeftToRight(param1Component)) {
          skin.paintSkin(param1Graphics, param1Int1, param1Int2, state);
        } else {
          Graphics2D graphics2D = (Graphics2D)param1Graphics.create();
          graphics2D.translate(param1Int1 + skin.getWidth(), param1Int2);
          graphics2D.scale(-1.0D, 1.0D);
          skin.paintSkin(graphics2D, 0, 0, state);
          graphics2D.dispose();
        } 
      } else {
        param1Graphics.translate(param1Int1, param1Int2);
        if (WindowsGraphicsUtils.isLeftToRight(param1Component)) {
          param1Graphics.drawLine(0, 0, 0, 7);
          param1Graphics.drawLine(1, 1, 1, 6);
          param1Graphics.drawLine(2, 2, 2, 5);
          param1Graphics.drawLine(3, 3, 3, 4);
        } else {
          param1Graphics.drawLine(4, 0, 4, 7);
          param1Graphics.drawLine(3, 1, 3, 6);
          param1Graphics.drawLine(2, 2, 2, 5);
          param1Graphics.drawLine(1, 3, 1, 4);
        } 
        param1Graphics.translate(-param1Int1, -param1Int2);
      } 
    }
    
    public int getIconWidth() {
      XPStyle xPStyle = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(xPStyle)) {
        XPStyle.Skin skin = xPStyle.getSkin(null, TMSchema.Part.MP_POPUPSUBMENU);
        return skin.getWidth();
      } 
      return 4;
    }
    
    public int getIconHeight() {
      XPStyle xPStyle = XPStyle.getXP();
      if (WindowsMenuItemUI.isVistaPainting(xPStyle)) {
        XPStyle.Skin skin = xPStyle.getSkin(null, TMSchema.Part.MP_POPUPSUBMENU);
        return skin.getHeight();
      } 
      return 8;
    }
  }
  
  private static class MenuItemArrowIcon implements Icon, UIResource, Serializable {
    private MenuItemArrowIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 4; }
    
    public int getIconHeight() { return 8; }
  }
  
  private static class MenuItemCheckIcon implements Icon, UIResource, Serializable {
    private MenuItemCheckIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {}
    
    public int getIconWidth() { return 9; }
    
    public int getIconHeight() { return 9; }
  }
  
  private static class RadioButtonIcon implements Icon, UIResource, Serializable {
    private RadioButtonIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      XPStyle xPStyle = XPStyle.getXP();
      if (xPStyle != null) {
        TMSchema.State state;
        TMSchema.Part part = TMSchema.Part.BP_RADIOBUTTON;
        XPStyle.Skin skin = xPStyle.getSkin(abstractButton, part);
        boolean bool = false;
        if (buttonModel.isSelected()) {
          state = TMSchema.State.CHECKEDNORMAL;
          if (!buttonModel.isEnabled()) {
            state = TMSchema.State.CHECKEDDISABLED;
          } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
            state = TMSchema.State.CHECKEDPRESSED;
          } else if (buttonModel.isRollover()) {
            state = TMSchema.State.CHECKEDHOT;
          } 
        } else {
          state = TMSchema.State.UNCHECKEDNORMAL;
          if (!buttonModel.isEnabled()) {
            state = TMSchema.State.UNCHECKEDDISABLED;
          } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
            state = TMSchema.State.UNCHECKEDPRESSED;
          } else if (buttonModel.isRollover()) {
            state = TMSchema.State.UNCHECKEDHOT;
          } 
        } 
        skin.paintSkin(param1Graphics, param1Int1, param1Int2, state);
      } else {
        if ((buttonModel.isPressed() && buttonModel.isArmed()) || !buttonModel.isEnabled()) {
          param1Graphics.setColor(UIManager.getColor("RadioButton.background"));
        } else {
          param1Graphics.setColor(UIManager.getColor("RadioButton.interiorBackground"));
        } 
        param1Graphics.fillRect(param1Int1 + 2, param1Int2 + 2, 8, 8);
        param1Graphics.setColor(UIManager.getColor("RadioButton.shadow"));
        param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 0, param1Int1 + 7, param1Int2 + 0);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 1, param1Int1 + 3, param1Int2 + 1);
        param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 1, param1Int1 + 9, param1Int2 + 1);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 2, param1Int1 + 1, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1 + 0, param1Int2 + 4, param1Int1 + 0, param1Int2 + 7);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 8, param1Int1 + 1, param1Int2 + 9);
        param1Graphics.setColor(UIManager.getColor("RadioButton.highlight"));
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 10, param1Int1 + 3, param1Int2 + 10);
        param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 11, param1Int1 + 7, param1Int2 + 11);
        param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 10, param1Int1 + 9, param1Int2 + 10);
        param1Graphics.drawLine(param1Int1 + 10, param1Int2 + 9, param1Int1 + 10, param1Int2 + 8);
        param1Graphics.drawLine(param1Int1 + 11, param1Int2 + 7, param1Int1 + 11, param1Int2 + 4);
        param1Graphics.drawLine(param1Int1 + 10, param1Int2 + 3, param1Int1 + 10, param1Int2 + 2);
        param1Graphics.setColor(UIManager.getColor("RadioButton.darkShadow"));
        param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 1, param1Int1 + 7, param1Int2 + 1);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 2, param1Int1 + 3, param1Int2 + 2);
        param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 2, param1Int1 + 9, param1Int2 + 2);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 3, param1Int1 + 2, param1Int2 + 3);
        param1Graphics.drawLine(param1Int1 + 1, param1Int2 + 4, param1Int1 + 1, param1Int2 + 7);
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 8, param1Int1 + 2, param1Int2 + 8);
        param1Graphics.setColor(UIManager.getColor("RadioButton.light"));
        param1Graphics.drawLine(param1Int1 + 2, param1Int2 + 9, param1Int1 + 3, param1Int2 + 9);
        param1Graphics.drawLine(param1Int1 + 4, param1Int2 + 10, param1Int1 + 7, param1Int2 + 10);
        param1Graphics.drawLine(param1Int1 + 8, param1Int2 + 9, param1Int1 + 9, param1Int2 + 9);
        param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 8, param1Int1 + 9, param1Int2 + 8);
        param1Graphics.drawLine(param1Int1 + 10, param1Int2 + 7, param1Int1 + 10, param1Int2 + 4);
        param1Graphics.drawLine(param1Int1 + 9, param1Int2 + 3, param1Int1 + 9, param1Int2 + 3);
        if (buttonModel.isSelected()) {
          if (buttonModel.isEnabled()) {
            param1Graphics.setColor(UIManager.getColor("RadioButton.foreground"));
          } else {
            param1Graphics.setColor(UIManager.getColor("RadioButton.shadow"));
          } 
          param1Graphics.fillRect(param1Int1 + 4, param1Int2 + 5, 4, 2);
          param1Graphics.fillRect(param1Int1 + 5, param1Int2 + 4, 2, 4);
        } 
      } 
    }
    
    public int getIconWidth() {
      XPStyle xPStyle = XPStyle.getXP();
      return (xPStyle != null) ? xPStyle.getSkin(null, TMSchema.Part.BP_RADIOBUTTON).getWidth() : 13;
    }
    
    public int getIconHeight() {
      XPStyle xPStyle = XPStyle.getXP();
      return (xPStyle != null) ? xPStyle.getSkin(null, TMSchema.Part.BP_RADIOBUTTON).getHeight() : 13;
    }
  }
  
  private static class RadioButtonMenuItemIcon implements Icon, UIResource, Serializable {
    private RadioButtonMenuItemIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      AbstractButton abstractButton = (AbstractButton)param1Component;
      ButtonModel buttonModel = abstractButton.getModel();
      if (abstractButton.isSelected() == true)
        param1Graphics.fillRoundRect(param1Int1 + 3, param1Int2 + 3, getIconWidth() - 6, getIconHeight() - 6, 4, 4); 
    }
    
    public int getIconWidth() { return 12; }
    
    public int getIconHeight() { return 12; }
  }
  
  private static class ResizeIcon implements Icon, Serializable {
    private ResizeIcon() {}
    
    public void paintIcon(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2) {
      param1Graphics.setColor(UIManager.getColor("InternalFrame.resizeIconHighlight"));
      param1Graphics.drawLine(0, 11, 11, 0);
      param1Graphics.drawLine(4, 11, 11, 4);
      param1Graphics.drawLine(8, 11, 11, 8);
      param1Graphics.setColor(UIManager.getColor("InternalFrame.resizeIconShadow"));
      param1Graphics.drawLine(1, 11, 11, 1);
      param1Graphics.drawLine(2, 11, 11, 2);
      param1Graphics.drawLine(5, 11, 11, 5);
      param1Graphics.drawLine(6, 11, 11, 6);
      param1Graphics.drawLine(9, 11, 11, 9);
      param1Graphics.drawLine(10, 11, 11, 10);
    }
    
    public int getIconWidth() { return 13; }
    
    public int getIconHeight() { return 13; }
  }
  
  static class VistaMenuItemCheckIconFactory implements MenuItemCheckIconFactory {
    private static final int OFFSET = 3;
    
    public Icon getIcon(JMenuItem param1JMenuItem) { return new VistaMenuItemCheckIcon(param1JMenuItem); }
    
    public boolean isCompatible(Object param1Object, String param1String) { return (param1Object instanceof VistaMenuItemCheckIcon && ((VistaMenuItemCheckIcon)param1Object).type == getType(param1String)); }
    
    public Icon getIcon(String param1String) { return new VistaMenuItemCheckIcon(param1String); }
    
    static int getIconWidth() {
      XPStyle xPStyle = XPStyle.getXP();
      return ((xPStyle != null) ? xPStyle.getSkin(null, TMSchema.Part.MP_POPUPCHECK).getWidth() : 16) + 6;
    }
    
    private static Class<? extends JMenuItem> getType(Component param1Component) {
      Class clazz = null;
      if (param1Component instanceof javax.swing.JCheckBoxMenuItem) {
        clazz = javax.swing.JCheckBoxMenuItem.class;
      } else if (param1Component instanceof javax.swing.JRadioButtonMenuItem) {
        clazz = javax.swing.JRadioButtonMenuItem.class;
      } else if (param1Component instanceof javax.swing.JMenu) {
        clazz = javax.swing.JMenu.class;
      } else if (param1Component instanceof JMenuItem) {
        clazz = JMenuItem.class;
      } 
      return clazz;
    }
    
    private static Class<? extends JMenuItem> getType(String param1String) {
      Class clazz = null;
      if (param1String == "CheckBoxMenuItem") {
        clazz = javax.swing.JCheckBoxMenuItem.class;
      } else if (param1String == "RadioButtonMenuItem") {
        clazz = javax.swing.JRadioButtonMenuItem.class;
      } else if (param1String == "Menu") {
        clazz = javax.swing.JMenu.class;
      } else if (param1String == "MenuItem") {
        clazz = JMenuItem.class;
      } else {
        clazz = JMenuItem.class;
      } 
      return clazz;
    }
    
    private static class VistaMenuItemCheckIcon implements Icon, UIResource, Serializable {
      private final JMenuItem menuItem;
      
      private final Class<? extends JMenuItem> type;
      
      VistaMenuItemCheckIcon(JMenuItem param2JMenuItem) {
        this.type = WindowsIconFactory.VistaMenuItemCheckIconFactory.getType(param2JMenuItem);
        this.menuItem = param2JMenuItem;
      }
      
      VistaMenuItemCheckIcon(String param2String) {
        this.type = WindowsIconFactory.VistaMenuItemCheckIconFactory.getType(param2String);
        this.menuItem = null;
      }
      
      public int getIconHeight() {
        Icon icon1 = getLaFIcon();
        if (icon1 != null)
          return icon1.getIconHeight(); 
        Icon icon2 = getIcon();
        null = 0;
        if (icon2 != null) {
          null = icon2.getIconHeight();
        } else {
          XPStyle xPStyle = XPStyle.getXP();
          if (xPStyle != null) {
            XPStyle.Skin skin = xPStyle.getSkin(null, TMSchema.Part.MP_POPUPCHECK);
            null = skin.getHeight();
          } else {
            null = 16;
          } 
        } 
        return 6;
      }
      
      public int getIconWidth() {
        Icon icon1 = getLaFIcon();
        if (icon1 != null)
          return icon1.getIconWidth(); 
        Icon icon2 = getIcon();
        int i = 0;
        if (icon2 != null) {
          i = icon2.getIconWidth() + 6;
        } else {
          i = WindowsIconFactory.VistaMenuItemCheckIconFactory.getIconWidth();
        } 
        return i;
      }
      
      public void paintIcon(Component param2Component, Graphics param2Graphics, int param2Int1, int param2Int2) {
        Icon icon1 = getLaFIcon();
        if (icon1 != null) {
          icon1.paintIcon(param2Component, param2Graphics, param2Int1, param2Int2);
          return;
        } 
        assert this.menuItem == null || param2Component == this.menuItem;
        Icon icon2 = getIcon();
        if (this.type == javax.swing.JCheckBoxMenuItem.class || this.type == javax.swing.JRadioButtonMenuItem.class) {
          AbstractButton abstractButton = (AbstractButton)param2Component;
          if (abstractButton.isSelected()) {
            TMSchema.State state2;
            TMSchema.State state1;
            TMSchema.Part part1 = TMSchema.Part.MP_POPUPCHECKBACKGROUND;
            TMSchema.Part part2 = TMSchema.Part.MP_POPUPCHECK;
            if (isEnabled(param2Component, null)) {
              state1 = (icon2 != null) ? TMSchema.State.BITMAP : TMSchema.State.NORMAL;
              state2 = (this.type == javax.swing.JRadioButtonMenuItem.class) ? TMSchema.State.BULLETNORMAL : TMSchema.State.CHECKMARKNORMAL;
            } else {
              state1 = TMSchema.State.DISABLEDPUSHED;
              state2 = (this.type == javax.swing.JRadioButtonMenuItem.class) ? TMSchema.State.BULLETDISABLED : TMSchema.State.CHECKMARKDISABLED;
            } 
            XPStyle xPStyle = XPStyle.getXP();
            if (xPStyle != null) {
              XPStyle.Skin skin = xPStyle.getSkin(param2Component, part1);
              skin.paintSkin(param2Graphics, param2Int1, param2Int2, getIconWidth(), getIconHeight(), state1);
              if (icon2 == null) {
                skin = xPStyle.getSkin(param2Component, part2);
                skin.paintSkin(param2Graphics, param2Int1 + 3, param2Int2 + 3, state2);
              } 
            } 
          } 
        } 
        if (icon2 != null)
          icon2.paintIcon(param2Component, param2Graphics, param2Int1 + 3, param2Int2 + 3); 
      }
      
      private static WindowsMenuItemUIAccessor getAccessor(JMenuItem param2JMenuItem) {
        WindowsMenuItemUIAccessor windowsMenuItemUIAccessor = null;
        ButtonUI buttonUI = (param2JMenuItem != null) ? param2JMenuItem.getUI() : null;
        if (buttonUI instanceof WindowsMenuItemUI) {
          windowsMenuItemUIAccessor = ((WindowsMenuItemUI)buttonUI).accessor;
        } else if (buttonUI instanceof WindowsMenuUI) {
          windowsMenuItemUIAccessor = ((WindowsMenuUI)buttonUI).accessor;
        } else if (buttonUI instanceof WindowsCheckBoxMenuItemUI) {
          windowsMenuItemUIAccessor = ((WindowsCheckBoxMenuItemUI)buttonUI).accessor;
        } else if (buttonUI instanceof WindowsRadioButtonMenuItemUI) {
          windowsMenuItemUIAccessor = ((WindowsRadioButtonMenuItemUI)buttonUI).accessor;
        } 
        return windowsMenuItemUIAccessor;
      }
      
      private static boolean isEnabled(Component param2Component, TMSchema.State param2State) {
        if (param2State == null && param2Component instanceof JMenuItem) {
          WindowsMenuItemUIAccessor windowsMenuItemUIAccessor = getAccessor((JMenuItem)param2Component);
          if (windowsMenuItemUIAccessor != null)
            param2State = windowsMenuItemUIAccessor.getState((JMenuItem)param2Component); 
        } 
        return (param2State == null) ? ((param2Component != null) ? param2Component.isEnabled() : 1) : ((param2State != TMSchema.State.DISABLED && param2State != TMSchema.State.DISABLEDHOT && param2State != TMSchema.State.DISABLEDPUSHED) ? 1 : 0);
      }
      
      private Icon getIcon() {
        Icon icon = null;
        if (this.menuItem == null)
          return icon; 
        WindowsMenuItemUIAccessor windowsMenuItemUIAccessor = getAccessor(this.menuItem);
        TMSchema.State state = (windowsMenuItemUIAccessor != null) ? windowsMenuItemUIAccessor.getState(this.menuItem) : null;
        if (isEnabled(this.menuItem, null)) {
          if (state == TMSchema.State.PUSHED) {
            icon = this.menuItem.getPressedIcon();
          } else {
            icon = this.menuItem.getIcon();
          } 
        } else {
          icon = this.menuItem.getDisabledIcon();
        } 
        return icon;
      }
      
      private Icon getLaFIcon() {
        Icon icon = (Icon)UIManager.getDefaults().get(typeToString(this.type));
        if (icon instanceof VistaMenuItemCheckIcon && ((VistaMenuItemCheckIcon)icon).type == this.type)
          icon = null; 
        return icon;
      }
      
      private static String typeToString(Class<? extends JMenuItem> param2Class) {
        assert param2Class == JMenuItem.class || param2Class == javax.swing.JMenu.class || param2Class == javax.swing.JCheckBoxMenuItem.class || param2Class == javax.swing.JRadioButtonMenuItem.class;
        StringBuilder stringBuilder = new StringBuilder(param2Class.getName());
        stringBuilder.delete(0, stringBuilder.lastIndexOf("J") + 1);
        stringBuilder.append(".checkIcon");
        return stringBuilder.toString();
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsIconFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */