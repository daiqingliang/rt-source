package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicRadioButtonUI;
import sun.awt.AppContext;

public class WindowsRadioButtonUI extends BasicRadioButtonUI {
  private static final Object WINDOWS_RADIO_BUTTON_UI_KEY = new Object();
  
  protected int dashedRectGapX;
  
  protected int dashedRectGapY;
  
  protected int dashedRectGapWidth;
  
  protected int dashedRectGapHeight;
  
  protected Color focusColor;
  
  private boolean initialized = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    WindowsRadioButtonUI windowsRadioButtonUI = (WindowsRadioButtonUI)appContext.get(WINDOWS_RADIO_BUTTON_UI_KEY);
    if (windowsRadioButtonUI == null) {
      windowsRadioButtonUI = new WindowsRadioButtonUI();
      appContext.put(WINDOWS_RADIO_BUTTON_UI_KEY, windowsRadioButtonUI);
    } 
    return windowsRadioButtonUI;
  }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.initialized) {
      this.dashedRectGapX = ((Integer)UIManager.get("Button.dashedRectGapX")).intValue();
      this.dashedRectGapY = ((Integer)UIManager.get("Button.dashedRectGapY")).intValue();
      this.dashedRectGapWidth = ((Integer)UIManager.get("Button.dashedRectGapWidth")).intValue();
      this.dashedRectGapHeight = ((Integer)UIManager.get("Button.dashedRectGapHeight")).intValue();
      this.focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
      this.initialized = true;
    } 
    if (XPStyle.getXP() != null)
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE); 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.initialized = false;
  }
  
  protected Color getFocusColor() { return this.focusColor; }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString) { WindowsGraphicsUtils.paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString, getTextShiftOffset()); }
  
  protected void paintFocus(Graphics paramGraphics, Rectangle paramRectangle, Dimension paramDimension) {
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
  }
  
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsRadioButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */