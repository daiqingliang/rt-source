package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PrincipalTypeMenuListener implements ItemListener {
  private ToolDialog td;
  
  PrincipalTypeMenuListener(ToolDialog paramToolDialog) { this.td = paramToolDialog; }
  
  public void itemStateChanged(ItemEvent paramItemEvent) {
    if (paramItemEvent.getStateChange() == 2)
      return; 
    JComboBox jComboBox = (JComboBox)this.td.getComponent(1);
    JTextField jTextField1 = (JTextField)this.td.getComponent(2);
    JTextField jTextField2 = (JTextField)this.td.getComponent(4);
    jComboBox.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)paramItemEvent.getItem()));
    if (((String)paramItemEvent.getItem()).equals(ToolDialog.PRIN_TYPE)) {
      if (jTextField1.getText() != null && jTextField1.getText().length() > 0) {
        Prin prin1 = ToolDialog.getPrin(jTextField1.getText(), true);
        jComboBox.setSelectedItem(prin1.CLASS);
      } 
      return;
    } 
    if (jTextField1.getText().indexOf((String)paramItemEvent.getItem()) == -1)
      jTextField2.setText(""); 
    Prin prin = ToolDialog.getPrin((String)paramItemEvent.getItem(), false);
    if (prin != null)
      jTextField1.setText(prin.FULL_CLASS); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\PrincipalTypeMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */