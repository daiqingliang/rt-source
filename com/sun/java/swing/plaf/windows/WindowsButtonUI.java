package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import sun.awt.AppContext;

public class WindowsButtonUI extends BasicButtonUI {
  protected int dashedRectGapX;
  
  protected int dashedRectGapY;
  
  protected int dashedRectGapWidth;
  
  protected int dashedRectGapHeight;
  
  protected Color focusColor;
  
  private boolean defaults_initialized = false;
  
  private static final Object WINDOWS_BUTTON_UI_KEY = new Object();
  
  private Rectangle viewRect = new Rectangle();
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    WindowsButtonUI windowsButtonUI = (WindowsButtonUI)appContext.get(WINDOWS_BUTTON_UI_KEY);
    if (windowsButtonUI == null) {
      windowsButtonUI = new WindowsButtonUI();
      appContext.put(WINDOWS_BUTTON_UI_KEY, windowsButtonUI);
    } 
    return windowsButtonUI;
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      String str = getPropertyPrefix();
      this.dashedRectGapX = UIManager.getInt(str + "dashedRectGapX");
      this.dashedRectGapY = UIManager.getInt(str + "dashedRectGapY");
      this.dashedRectGapWidth = UIManager.getInt(str + "dashedRectGapWidth");
      this.dashedRectGapHeight = UIManager.getInt(str + "dashedRectGapHeight");
      this.focusColor = UIManager.getColor(str + "focus");
      this.defaults_initialized = true;
    } 
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      paramAbstractButton.setBorder(xPStyle.getBorder(paramAbstractButton, getXPButtonType(paramAbstractButton)));
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE);
    } 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getFocusColor() { return this.focusColor; }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString) { WindowsGraphicsUtils.paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString, getTextShiftOffset()); }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) {
    int i = paramAbstractButton.getWidth();
    int j = paramAbstractButton.getHeight();
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, this.dashedRectGapX, this.dashedRectGapY, i - this.dashedRectGapWidth, j - this.dashedRectGapHeight);
  }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) { setTextShiftOffset(); }
  
  public Dimension getPreferredSize(JComponent paramJComponent) {
    Dimension dimension = super.getPreferredSize(paramJComponent);
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    if (dimension != null && abstractButton.isFocusPainted()) {
      if (dimension.width % 2 == 0)
        dimension.width++; 
      if (dimension.height % 2 == 0)
        dimension.height++; 
    } 
    return dimension;
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (XPStyle.getXP() != null)
      paintXPButtonBackground(paramGraphics, paramJComponent); 
    super.paint(paramGraphics, paramJComponent);
  }
  
  static TMSchema.Part getXPButtonType(AbstractButton paramAbstractButton) {
    if (paramAbstractButton instanceof javax.swing.JCheckBox)
      return TMSchema.Part.BP_CHECKBOX; 
    if (paramAbstractButton instanceof javax.swing.JRadioButton)
      return TMSchema.Part.BP_RADIOBUTTON; 
    boolean bool = paramAbstractButton.getParent() instanceof javax.swing.JToolBar;
    return bool ? TMSchema.Part.TP_BUTTON : TMSchema.Part.BP_PUSHBUTTON;
  }
  
  static TMSchema.State getXPButtonState(AbstractButton paramAbstractButton) {
    boolean bool;
    TMSchema.Part part = getXPButtonType(paramAbstractButton);
    ButtonModel buttonModel = paramAbstractButton.getModel();
    null = TMSchema.State.NORMAL;
    switch (part) {
      case BP_RADIOBUTTON:
      case BP_CHECKBOX:
        if (!buttonModel.isEnabled()) {
          null = buttonModel.isSelected() ? TMSchema.State.CHECKEDDISABLED : TMSchema.State.UNCHECKEDDISABLED;
        } else if (buttonModel.isPressed() && buttonModel.isArmed()) {
          null = buttonModel.isSelected() ? TMSchema.State.CHECKEDPRESSED : TMSchema.State.UNCHECKEDPRESSED;
        } else if (buttonModel.isRollover()) {
          null = buttonModel.isSelected() ? TMSchema.State.CHECKEDHOT : TMSchema.State.UNCHECKEDHOT;
        } else {
          null = buttonModel.isSelected() ? TMSchema.State.CHECKEDNORMAL : TMSchema.State.UNCHECKEDNORMAL;
        } 
        return null;
      case BP_PUSHBUTTON:
      case TP_BUTTON:
        bool = paramAbstractButton.getParent() instanceof javax.swing.JToolBar;
        if (bool) {
          if (buttonModel.isArmed() && buttonModel.isPressed()) {
            null = TMSchema.State.PRESSED;
          } else if (!buttonModel.isEnabled()) {
            null = TMSchema.State.DISABLED;
          } else if (buttonModel.isSelected() && buttonModel.isRollover()) {
            null = TMSchema.State.HOTCHECKED;
          } else if (buttonModel.isSelected()) {
            null = TMSchema.State.CHECKED;
          } else if (buttonModel.isRollover()) {
            null = TMSchema.State.HOT;
          } else if (paramAbstractButton.hasFocus()) {
            null = TMSchema.State.HOT;
          } 
        } else if ((buttonModel.isArmed() && buttonModel.isPressed()) || buttonModel.isSelected()) {
          null = TMSchema.State.PRESSED;
        } else if (!buttonModel.isEnabled()) {
          null = TMSchema.State.DISABLED;
        } else if (buttonModel.isRollover() || buttonModel.isPressed()) {
          null = TMSchema.State.HOT;
        } else if (paramAbstractButton instanceof JButton && ((JButton)paramAbstractButton).isDefaultButton()) {
          null = TMSchema.State.DEFAULTED;
        } else if (paramAbstractButton.hasFocus()) {
          null = TMSchema.State.HOT;
        } 
        return null;
    } 
    return TMSchema.State.NORMAL;
  }
  
  static void paintXPButtonBackground(Graphics paramGraphics, JComponent paramJComponent) {
    AbstractButton abstractButton = (AbstractButton)paramJComponent;
    XPStyle xPStyle = XPStyle.getXP();
    TMSchema.Part part = getXPButtonType(abstractButton);
    if (abstractButton.isContentAreaFilled() && xPStyle != null) {
      Insets insets;
      XPStyle.Skin skin = xPStyle.getSkin(abstractButton, part);
      TMSchema.State state = getXPButtonState(abstractButton);
      Dimension dimension = paramJComponent.getSize();
      int i = 0;
      int j = 0;
      int k = dimension.width;
      int m = dimension.height;
      Border border = paramJComponent.getBorder();
      if (border != null) {
        insets = getOpaqueInsets(border, paramJComponent);
      } else {
        insets = paramJComponent.getInsets();
      } 
      if (insets != null) {
        i += insets.left;
        j += insets.top;
        k -= insets.left + insets.right;
        m -= insets.top + insets.bottom;
      } 
      skin.paintSkin(paramGraphics, i, j, k, m, state);
    } 
  }
  
  private static Insets getOpaqueInsets(Border paramBorder, Component paramComponent) {
    if (paramBorder == null)
      return null; 
    if (paramBorder.isBorderOpaque())
      return paramBorder.getBorderInsets(paramComponent); 
    if (paramBorder instanceof CompoundBorder) {
      CompoundBorder compoundBorder = (CompoundBorder)paramBorder;
      Insets insets = getOpaqueInsets(compoundBorder.getOutsideBorder(), paramComponent);
      if (insets != null && insets.equals(compoundBorder.getOutsideBorder().getBorderInsets(paramComponent))) {
        Insets insets1 = getOpaqueInsets(compoundBorder.getInsideBorder(), paramComponent);
        return (insets1 == null) ? insets : new Insets(insets.top + insets1.top, insets.left + insets1.left, insets.bottom + insets1.bottom, insets.right + insets1.right);
      } 
      return insets;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */