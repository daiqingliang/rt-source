package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionActionsMenuListener implements ItemListener {
  private ToolDialog td;
  
  PermissionActionsMenuListener(ToolDialog paramToolDialog) { this.td = paramToolDialog; }
  
  public void itemStateChanged(ItemEvent paramItemEvent) {
    if (paramItemEvent.getStateChange() == 2)
      return; 
    JComboBox jComboBox = (JComboBox)this.td.getComponent(5);
    jComboBox.getAccessibleContext().setAccessibleName((String)paramItemEvent.getItem());
    if (((String)paramItemEvent.getItem()).indexOf(ToolDialog.PERM_ACTIONS) != -1)
      return; 
    JTextField jTextField = (JTextField)this.td.getComponent(6);
    if (jTextField.getText() == null || jTextField.getText().equals("")) {
      jTextField.setText((String)paramItemEvent.getItem());
    } else if (jTextField.getText().indexOf((String)paramItemEvent.getItem()) == -1) {
      jTextField.setText(jTextField.getText() + ", " + (String)paramItemEvent.getItem());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\PermissionActionsMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */