package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;

class ConfirmRemovePolicyEntryOKButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog us;
  
  ConfirmRemovePolicyEntryOKButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.us = paramToolDialog;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    JList jList = (JList)this.tw.getComponent(3);
    int i = jList.getSelectedIndex();
    PolicyEntry[] arrayOfPolicyEntry = this.tool.getEntry();
    this.tool.removeEntry(arrayOfPolicyEntry[i]);
    DefaultListModel defaultListModel = new DefaultListModel();
    jList = new JList(defaultListModel);
    jList.setVisibleRowCount(15);
    jList.setSelectionMode(0);
    jList.addMouseListener(new PolicyListListener(this.tool, this.tw));
    arrayOfPolicyEntry = this.tool.getEntry();
    if (arrayOfPolicyEntry != null)
      for (byte b = 0; b < arrayOfPolicyEntry.length; b++)
        defaultListModel.addElement(arrayOfPolicyEntry[b].headerToString());  
    this.tw.replacePolicyList(jList);
    this.us.setVisible(false);
    this.us.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ConfirmRemovePolicyEntryOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */