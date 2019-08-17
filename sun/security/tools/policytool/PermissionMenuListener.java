package sun.security.tools.policytool;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionMenuListener implements ItemListener {
  private ToolDialog td;
  
  PermissionMenuListener(ToolDialog paramToolDialog) { this.td = paramToolDialog; }
  
  public void itemStateChanged(ItemEvent paramItemEvent) {
    if (paramItemEvent.getStateChange() == 2)
      return; 
    JComboBox jComboBox1 = (JComboBox)this.td.getComponent(1);
    JComboBox jComboBox2 = (JComboBox)this.td.getComponent(3);
    JComboBox jComboBox3 = (JComboBox)this.td.getComponent(5);
    JTextField jTextField1 = (JTextField)this.td.getComponent(4);
    JTextField jTextField2 = (JTextField)this.td.getComponent(6);
    JTextField jTextField3 = (JTextField)this.td.getComponent(2);
    JTextField jTextField4 = (JTextField)this.td.getComponent(8);
    jComboBox1.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)paramItemEvent.getItem()));
    if (PolicyTool.collator.compare((String)paramItemEvent.getItem(), ToolDialog.PERM) == 0) {
      if (jTextField3.getText() != null && jTextField3.getText().length() > 0) {
        Perm perm1 = ToolDialog.getPerm(jTextField3.getText(), true);
        if (perm1 != null)
          jComboBox1.setSelectedItem(perm1.CLASS); 
      } 
      return;
    } 
    if (jTextField3.getText().indexOf((String)paramItemEvent.getItem()) == -1) {
      jTextField1.setText("");
      jTextField2.setText("");
      jTextField4.setText("");
    } 
    Perm perm = ToolDialog.getPerm((String)paramItemEvent.getItem(), false);
    if (perm == null) {
      jTextField3.setText("");
    } else {
      jTextField3.setText(perm.FULL_CLASS);
    } 
    this.td.setPermissionNames(perm, jComboBox2, jTextField1);
    this.td.setPermissionActions(perm, jComboBox3, jTextField2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\PermissionMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */