package javax.swing.plaf.basic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class BasicFormattedTextFieldUI extends BasicTextFieldUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicFormattedTextFieldUI(); }
  
  protected String getPropertyPrefix() { return "FormattedTextField"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicFormattedTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */