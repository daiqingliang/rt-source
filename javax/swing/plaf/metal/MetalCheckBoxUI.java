package javax.swing.plaf.metal;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import sun.awt.AppContext;

public class MetalCheckBoxUI extends MetalRadioButtonUI {
  private static final Object METAL_CHECK_BOX_UI_KEY = new Object();
  
  private static final String propertyPrefix = "CheckBox.";
  
  private boolean defaults_initialized = false;
  
  public static ComponentUI createUI(JComponent paramJComponent) {
    AppContext appContext = AppContext.getAppContext();
    MetalCheckBoxUI metalCheckBoxUI = (MetalCheckBoxUI)appContext.get(METAL_CHECK_BOX_UI_KEY);
    if (metalCheckBoxUI == null) {
      metalCheckBoxUI = new MetalCheckBoxUI();
      appContext.put(METAL_CHECK_BOX_UI_KEY, metalCheckBoxUI);
    } 
    return metalCheckBoxUI;
  }
  
  public String getPropertyPrefix() { return "CheckBox."; }
  
  public void installDefaults(AbstractButton paramAbstractButton) {
    super.installDefaults(paramAbstractButton);
    if (!this.defaults_initialized) {
      this.icon = UIManager.getIcon(getPropertyPrefix() + "icon");
      this.defaults_initialized = true;
    } 
  }
  
  protected void uninstallDefaults(AbstractButton paramAbstractButton) {
    super.uninstallDefaults(paramAbstractButton);
    this.defaults_initialized = false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalCheckBoxUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */