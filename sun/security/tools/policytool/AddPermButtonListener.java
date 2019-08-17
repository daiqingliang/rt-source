package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AddPermButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog td;
  
  private boolean editPolicyEntry;
  
  AddPermButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.td = paramToolDialog;
    this.editPolicyEntry = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) { this.td.displayPermissionDialog(this.editPolicyEntry, false); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\AddPermButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */