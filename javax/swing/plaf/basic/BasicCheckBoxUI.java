package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class BasicCheckBoxUI extends BasicRadioButtonUI {
  private static final Object BASIC_CHECK_BOX_UI_KEY = new Object();
  
  private static final String propertyPrefix = "CheckBox.";
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    BasicCheckBoxUI basicCheckBoxUI = (BasicCheckBoxUI)appContext.get(BASIC_CHECK_BOX_UI_KEY);
    if (basicCheckBoxUI == null) {
      basicCheckBoxUI = new BasicCheckBoxUI();
      appContext.put(BASIC_CHECK_BOX_UI_KEY, basicCheckBoxUI);
    } 
    return basicCheckBoxUI;
  }
  
  public String getPropertyPrefix() { return "CheckBox."; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicCheckBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */