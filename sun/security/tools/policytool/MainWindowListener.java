package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;

class MainWindowListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  MainWindowListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Add.Policy.Entry") == 0) {
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
      toolDialog.displayPolicyEntryDialog(false);
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Remove.Policy.Entry") == 0) {
      JList jList = (JList)this.tw.getComponent(3);
      int i = jList.getSelectedIndex();
      if (i < 0) {
        this.tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
        return;
      } 
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Remove.Policy.Entry"), this.tool, this.tw, true);
      toolDialog.displayConfirmRemovePolicyEntry();
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Edit.Policy.Entry") == 0) {
      JList jList = (JList)this.tw.getComponent(3);
      int i = jList.getSelectedIndex();
      if (i < 0) {
        this.tw.displayErrorDialog(null, new Exception(PolicyTool.getMessage("No.Policy.Entry.selected")));
        return;
      } 
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
      toolDialog.displayPolicyEntryDialog(true);
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Edit") == 0) {
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("KeyStore"), this.tool, this.tw, true);
      toolDialog.keyStoreDialog(0);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\MainWindowListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */