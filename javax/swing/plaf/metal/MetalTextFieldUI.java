package javax.swing.plaf.metal;

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

public class MetalTextFieldUI extends BasicTextFieldUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new MetalTextFieldUI(); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { super.propertyChange(paramPropertyChangeEvent); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */