package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;

public class WindowsToolBarUI extends BasicToolBarUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsToolBarUI(); }
  
  protected void installDefaults() {
    if (XPStyle.getXP() != null)
      setRolloverBorders(true); 
    super.installDefaults();
  }
  
  protected Border createRolloverBorder() { return (XPStyle.getXP() != null) ? new EmptyBorder(3, 3, 3, 3) : super.createRolloverBorder(); }
  
  protected Border createNonRolloverBorder() { return (XPStyle.getXP() != null) ? new EmptyBorder(3, 3, 3, 3) : super.createNonRolloverBorder(); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      xPStyle.getSkin(paramJComponent, TMSchema.Part.TP_TOOLBAR).paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), null, true);
    } else {
      super.paint(paramGraphics, paramJComponent);
    } 
  }
  
  protected Border getRolloverBorder(AbstractButton paramAbstractButton) {
    XPStyle xPStyle = XPStyle.getXP();
    return (xPStyle != null) ? xPStyle.getBorder(paramAbstractButton, WindowsButtonUI.getXPButtonType(paramAbstractButton)) : super.getRolloverBorder(paramAbstractButton);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsToolBarUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */