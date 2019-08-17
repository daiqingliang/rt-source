package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class RemovePermButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog td;
  
  private boolean edit;
  
  RemovePermButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.td = paramToolDialog;
    this.edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    TaggedList taggedList = (TaggedList)this.td.getComponent(8);
    int i = taggedList.getSelectedIndex();
    if (i < 0) {
      this.tw.displayErrorDialog(this.td, new Exception(PolicyTool.getMessage("No.permission.selected")));
      return;
    } 
    taggedList.removeTaggedItem(i);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\RemovePermButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */