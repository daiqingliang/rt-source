package com.sun.java.swing.plaf.windows;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class WindowsCheckBoxUI extends WindowsRadioButtonUI {
  private static final Object WINDOWS_CHECK_BOX_UI_KEY = new Object();
  
  private static final String propertyPrefix = "CheckBox.";
  
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    WindowsCheckBoxUI windowsCheckBoxUI = (WindowsCheckBoxUI)appContext.get(WINDOWS_CHECK_BOX_UI_KEY);
    if (windowsCheckBoxUI == null) {
      windowsCheckBoxUI = new WindowsCheckBoxUI();
      appContext.put(WINDOWS_CHECK_BOX_UI_KEY, windowsCheckBoxUI);
    } 
    return windowsCheckBoxUI;
  }
  
  public String getPropertyPrefix() { return "CheckBox."; }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.icon = UIManager.getIcon(getPropertyPrefix() + "icon");
      this.defaults_initialized = true;
    } 
  }
  
  public void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsCheckBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */