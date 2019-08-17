package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JTextField;

class ChangeKeyStoreOKButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog td;
  
  ChangeKeyStoreOKButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.td = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    String str1 = ((JTextField)this.td.getComponent(1)).getText().trim();
    String str2 = ((JTextField)this.td.getComponent(3)).getText().trim();
    String str3 = ((JTextField)this.td.getComponent(5)).getText().trim();
    String str4 = ((JTextField)this.td.getComponent(7)).getText().trim();
    try {
      this.tool.openKeyStore((str1.length() == 0) ? null : str1, (str2.length() == 0) ? null : str2, (str3.length() == 0) ? null : str3, (str4.length() == 0) ? null : str4);
      this.tool.modified = true;
    } catch (Exception exception) {
      MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Unable.to.open.KeyStore.ex.toString."));
      Object[] arrayOfObject = { exception.toString() };
      this.tw.displayErrorDialog(this.td, messageFormat.format(arrayOfObject));
      return;
    } 
    this.td.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ChangeKeyStoreOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */