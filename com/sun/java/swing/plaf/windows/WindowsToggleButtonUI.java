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
import javax.swing.plaf.basic.BasicToggleButtonUI;
import sun.awt.AppContext;

public class WindowsToggleButtonUI extends BasicToggleButtonUI {
  protected int dashedRectGapX;
  
  protected int dashedRectGapY;
  
  protected int dashedRectGapWidth;
  
  protected int dashedRectGapHeight;
  
  protected Color focusColor;
  
  private static final Object WINDOWS_TOGGLE_BUTTON_UI_KEY = new Object();
  
  private boolean defaults_initialized = false;
  
  private Color cachedSelectedColor = null;
  
  private Color cachedBackgroundColor = null;
  
  private Color cachedHighlightColor = null;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    WindowsToggleButtonUI windowsToggleButtonUI = (WindowsToggleButtonUI)appContext.get(WINDOWS_TOGGLE_BUTTON_UI_KEY);
    if (windowsToggleButtonUI == null) {
      windowsToggleButtonUI = new WindowsToggleButtonUI();
      appContext.put(WINDOWS_TOGGLE_BUTTON_UI_KEY, windowsToggleButtonUI);
    } 
    return windowsToggleButtonUI;
  }
  
  protected void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      String str = getPropertyPrefix();
      this.dashedRectGapX = ((Integer)UIManager.get("Button.dashedRectGapX")).intValue();
      this.dashedRectGapY = ((Integer)UIManager.get("Button.dashedRectGapY")).intValue();
      this.dashedRectGapWidth = ((Integer)UIManager.get("Button.dashedRectGapWidth")).intValue();
      this.dashedRectGapHeight = ((Integer)UIManager.get("Button.dashedRectGapHeight")).intValue();
      this.focusColor = UIManager.getColor(str + "focus");
      this.defaults_initialized = true;
    } 
    XPStyle xPStyle = XPStyle.getXP();
    if (xPStyle != null) {
      paramAbstractButton.setBorder(xPStyle.getBorder(paramAbstractButton, WindowsButtonUI.getXPButtonType(paramAbstractButton)));
      LookAndFeel.installProperty(paramAbstractButton, "opaque", Boolean.FALSE);
      LookAndFeel.installProperty(paramAbstractButton, "rolloverEnabled", Boolean.TRUE);
    } 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
  
  protected Color getFocusColor() { return this.focusColor; }
  
  protected void paintButtonPressed(Graphics paramGraphics, AbstractButton paramAbstractButton) {
    if (XPStyle.getXP() == null && paramAbstractButton.isContentAreaFilled()) {
      Color color1 = paramGraphics.getColor();
      Color color2 = paramAbstractButton.getBackground();
      Color color3 = UIManager.getColor("ToggleButton.highlight");
      if (color2 != this.cachedBackgroundColor || color3 != this.cachedHighlightColor) {
        int i = color2.getRed();
        int j = color3.getRed();
        int k = color2.getGreen();
        int m = color3.getGreen();
        int n = color2.getBlue();
        int i1 = color3.getBlue();
        this.cachedSelectedColor = new Color(Math.min(i, j) + Math.abs(i - j) / 2, Math.min(k, m) + Math.abs(k - m) / 2, Math.min(n, i1) + Math.abs(n - i1) / 2);
        this.cachedBackgroundColor = color2;
        this.cachedHighlightColor = color3;
      } 
      paramGraphics.setColor(this.cachedSelectedColor);
      paramGraphics.fillRect(0, 0, paramAbstractButton.getWidth(), paramAbstractButton.getHeight());
      paramGraphics.setColor(color1);
    } 
  }
  
  public void paint(Graphics paramGraphics, JComponent paramJComponent) {
    if (XPStyle.getXP() != null)
      WindowsButtonUI.paintXPButtonBackground(paramGraphics, paramJComponent); 
    super.paint(paramGraphics, paramJComponent);
  }
  
  protected void paintText(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle, String paramString) { WindowsGraphicsUtils.paintText(paramGraphics, paramAbstractButton, paramRectangle, paramString, getTextShiftOffset()); }
  
  protected void paintFocus(Graphics paramGraphics, AbstractButton paramAbstractButton, Rectangle paramRectangle1, Rectangle paramRectangle2, Rectangle paramRectangle3) {
    paramGraphics.setColor(getFocusColor());
    BasicGraphicsUtils.drawDashedRect(paramGraphics, this.dashedRectGapX, this.dashedRectGapY, paramAbstractButton.getWidth() - this.dashedRectGapWidth, paramAbstractButton.getHeight() - this.dashedRectGapHeight);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsToggleButtonUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */