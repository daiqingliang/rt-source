package javax.swing.plaf.metal;

import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;
import sun.awt.AppContext;
import sun.swing.SwingUtilities2;

public class MetalLabelUI extends BasicLabelUI {
  protected static MetalLabelUI metalLabelUI = new MetalLabelUI();
  
  private static final Object METAL_LABEL_UI_KEY = new Object();
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    if (System.getSecurityManager() != null) {
      AppContext appContext = AppContext.getAppContext();
      MetalLabelUI metalLabelUI1 = (MetalLabelUI)appContext.get(METAL_LABEL_UI_KEY);
      if (metalLabelUI1 == null) {
        metalLabelUI1 = new MetalLabelUI();
        appContext.put(METAL_LABEL_UI_KEY, metalLabelUI1);
      } 
      return metalLabelUI1;
    } 
    return metalLabelUI;
  }
  
  protected void paintDisabledText(JLabel paramJLabel, Graphics paramGraphics, String paramString, int paramInt1, int paramInt2) {
    int i = paramJLabel.getDisplayedMnemonicIndex();
    paramGraphics.setColor(UIManager.getColor("Label.disabledForeground"));
    SwingUtilities2.drawStringUnderlineCharAt(paramJLabel, paramGraphics, paramString, i, paramInt1, paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalLabelUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */