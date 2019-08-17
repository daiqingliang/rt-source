package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionNameMenuListener implements ItemListener {
  private ToolDialog td;
  
  PermissionNameMenuListener(ToolDialog paramToolDialog) { this.td = paramToolDialog; }
  
  public void itemStateChanged(ItemEvent paramItemEvent) {
    if (paramItemEvent.getStateChange() == 2)
      return; 
    JComboBox jComboBox = (JComboBox)this.td.getComponent(3);
    jComboBox.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)paramItemEvent.getItem()));
    if (((String)paramItemEvent.getItem()).indexOf(ToolDialog.PERM_NAME) != -1)
      return; 
    JTextField jTextField = (JTextField)this.td.getComponent(4);
    jTextField.setText((String)paramItemEvent.getItem());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\PermissionNameMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */