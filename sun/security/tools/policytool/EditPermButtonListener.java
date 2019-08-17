package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

class EditPermButtonListener extends MouseAdapter implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog td;
  
  private boolean editPolicyEntry;
  
  EditPermButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.td = paramToolDialog;
    this.editPolicyEntry = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    JList jList = (JList)this.td.getComponent(8);
    int i = jList.getSelectedIndex();
    if (i < 0) {
      this.tw.displayErrorDialog(this.td, new Exception(PolicyTool.getMessage("No.permission.selected")));
      return;
    } 
    this.td.displayPermissionDialog(this.editPolicyEntry, true);
  }
  
  public void mouseClicked(MouseEvent paramMouseEvent) {
    if (paramMouseEvent.getClickCount() == 2)
      actionPerformed(null); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\EditPermButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */