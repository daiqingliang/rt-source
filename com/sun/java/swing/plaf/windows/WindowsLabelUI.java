package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class WindowsLabelUI extends BasicLabelUI {
  private static final Object WINDOWS_LABEL_UI_KEY = new Object();
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    WindowsLabelUI windowsLabelUI = (WindowsLabelUI)appContext.get(WINDOWS_LABEL_UI_KEY);
    if (windowsLabelUI == null) {
      windowsLabelUI = new WindowsLabelUI();
      appContext.put(WINDOWS_LABEL_UI_KEY, windowsLabelUI);
    } 
    return windowsLabelUI;
  }
  
  protected void paintEnabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2) {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    if (WindowsLookAndFeel.isMnemonicHidden() == true)
      i = -1; 
    paramGraphics.setColor(paramJLabel.getForeground());
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
  }
  
  protected void paintDisabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2) {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    if (WindowsLookAndFeel.isMnemonicHidden() == true)
      i = -1; 
    if (UIManager.getColor("Label.disabledForeground") instanceof Color && UIManager.getColor("Label.disabledShadow") instanceof Color) {
      paramGraphics.setColor(UIManager.getColor("Label.disabledShadow"));
      SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1 + 1, paramInt2 + 1);
      paramGraphics.setColor(UIManager.getColor("Label.disabledForeground"));
      SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
    } else {
      Color color = paramJLabel.getBackground();
      paramGraphics.setColor(color.brighter());
      SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1 + 1, paramInt2 + 1);
      paramGraphics.setColor(color.darker());
      SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */