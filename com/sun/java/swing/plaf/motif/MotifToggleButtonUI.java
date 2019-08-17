package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;

public class MotifToggleButtonUI extends BasicToggleButtonUI {
  private static final Object MOTIF_TOGGLE_BUTTON_UI_KEY = new Object();
  
  protected Color selectColor;
  
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MotifToggleButtonUI motifToggleButtonUI = (MotifToggleButtonUI)appContext.get(MOTIF_TOGGLE_BUTTON_UI_KEY);
    if (motifToggleButtonUI == null) {
      motifToggleButtonUI = new MotifToggleButtonUI();
      appContext.put(MOTIF_TOGGLE_BUTTON_UI_KEY, motifToggleButtonUI);
    } 
    return motifToggleButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.selectColor = UIManager.getColor(getPropertyPrefix() + "select");
      this.defaults_initialized = true;
    } 
    LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getSelectColor() { return this.selectColor; }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) {
    if (paramAbstractButton.isContentAreaFilled()) {
      Color color = paramGraphics.getColor();
      Dimension dimension = paramAbstractButton.getSize();
      Insets insets1 = paramAbstractButton.getInsets();
      Insets insets2 = paramAbstractButton.getMargin();
      if (paramAbstractButton.getBackground() instanceof javax.swing.plaf.UIResource)
        paramGraphics.setColor(getSelectColor()); 
      paramGraphics.fillRect(insets1.left - insets2.left, insets1.top - insets2.top, dimension.width - insets1.left - insets2.left - insets1.right - insets2.right, dimension.height - insets1.top - insets2.top - insets1.bottom - insets2.bottom);
      paramGraphics.setColor(color);
    } 
  }
  
  public Insets getInsets(JComponent paramJComponent) {
    Border border = paramJComponent.getBorder();
    return (border != null) ? border.getBorderInsets(paramJComponent) : new Insets(0, 0, 0, 0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */