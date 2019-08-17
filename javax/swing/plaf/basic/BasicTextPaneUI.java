package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicTextPaneUI extends BasicEditorPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicTextPaneUI(); }
  
  protected String getPropertyPrefix() { return "TextPane"; }
  
  public void installUI(JComponent paramJComponent) { super.installUI(paramJComponent); }
  
  protected void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { super.propertyChange(paramPropertyChangeEvent); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTextPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */