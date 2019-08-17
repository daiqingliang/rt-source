package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class WindowsSpinnerUI extends BasicSpinnerUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsSpinnerUI(); }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (XPStyle.getXP() != null)
      paintXPBackground(paramGraphics, paramJComponent); 
    super.paint(paramGraphics, paramJComponent);
  }
  
  private TMSchema.State getXPState(JComponent paramJComponent) {
    TMSchema.State state = TMSchema.State.NORMAL;
    if (!paramJComponent.isEnabled())
      state = TMSchema.State.DISABLED; 
    return state;
  }
  
  private void paintXPBackground(Graphics paramGraphics, JComponent paramJComponent) {
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle == null)
      return; 
    XPStyle.Skin skin = xPStyle.getSkin(paramJComponent, TMSchema.Part.EP_EDIT);
    TMSchema.State state = getXPState(paramJComponent);
    skin.paintSkin(paramGraphics, 0, 0, paramJComponent.getWidth(), paramJComponent.getHeight(), state);
  }
  
  protected Component createPreviousButton() {
    if (XPStyle.getXP() != null) {
      XPStyle.GlyphButton glyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_DOWN);
      Dimension dimension = UIManager.getDimension("Spinner.arrowButtonSize");
      glyphButton.setPreferredSize(dimension);
      glyphButton.setRequestFocusEnabled(false);
      installPreviousButtonListeners(glyphButton);
      return glyphButton;
    } 
    return super.createPreviousButton();
  }
  
  protected Component createNextButton() {
    if (XPStyle.getXP() != null) {
      XPStyle.GlyphButton glyphButton = new XPStyle.GlyphButton(this.spinner, TMSchema.Part.SPNP_UP);
      Dimension dimension = UIManager.getDimension("Spinner.arrowButtonSize");
      glyphButton.setPreferredSize(dimension);
      glyphButton.setRequestFocusEnabled(false);
      installNextButtonListeners(glyphButton);
      return glyphButton;
    } 
    return super.createNextButton();
  }
  
  private UIResource getUIResource(Object[] paramArrayOfObject) {
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] instanceof UIResource)
        return (UIResource)paramArrayOfObject[b]; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsSpinnerUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */