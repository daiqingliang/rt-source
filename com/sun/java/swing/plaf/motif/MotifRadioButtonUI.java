package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import sun.awt.AppContext;

public class MotifRadioButtonUI extends BasicRadioButtonUI {
  private static final Object MOTIF_RADIO_BUTTON_UI_KEY = new Object();
  
  protected Color focusColor;
  
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MotifRadioButtonUI motifRadioButtonUI = (MotifRadioButtonUI)appContext.get(MOTIF_RADIO_BUTTON_UI_KEY);
    if (motifRadioButtonUI == null) {
      motifRadioButtonUI = new MotifRadioButtonUI();
      appContext.put(MOTIF_RADIO_BUTTON_UI_KEY, motifRadioButtonUI);
    } 
    return motifRadioButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
      this.defaults_initialized = true;
    } 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getFocusColor() { return this.focusColor; }
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension) {
    paramGraphics.setColor(getFocusColor());
    paramGraphics.drawRect(0, 0, paramDimension.width - 1, paramDimension.height - 1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */