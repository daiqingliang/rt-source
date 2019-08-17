package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveNoButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog us;
  
  private int select;
  
  UserSaveNoButtonListener(ToolDialog paramToolDialog, PolicyTool paramPolicyTool, ToolWindow paramToolWindow, int paramInt) {
    this.us = paramToolDialog;
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.select = paramInt;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    this.us.setVisible(false);
    this.us.dispose();
    this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\UserSaveNoButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */