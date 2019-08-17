package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JTextField;

class UserSaveYesButtonListener implements ActionListener {
  private ToolDialog us;
  
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private int select;
  
  UserSaveYesButtonListener(ToolDialog paramToolDialog, PolicyTool paramPolicyTool, ToolWindow paramToolWindow, int paramInt) {
    this.us = paramToolDialog;
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.select = paramInt;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    this.us.setVisible(false);
    this.us.dispose();
    try {
      String str = ((JTextField)this.tw.getComponent(1)).getText();
      if (str == null || str.equals("")) {
        this.us.displaySaveAsDialog(this.select);
      } else {
        this.tool.savePolicy(str);
        MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
        Object[] arrayOfObject = { str };
        this.tw.displayStatusDialog(null, messageFormat.format(arrayOfObject));
        this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
      } 
    } catch (Exception exception) {
      this.tw.displayErrorDialog(null, exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\UserSaveYesButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */