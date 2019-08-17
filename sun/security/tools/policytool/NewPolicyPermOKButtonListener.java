package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import sun.security.provider.PolicyParser;

class NewPolicyPermOKButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog listDialog;
  
  private ToolDialog infoDialog;
  
  private boolean edit;
  
  NewPolicyPermOKButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog1, ToolDialog paramToolDialog2, boolean paramBoolean) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.listDialog = paramToolDialog1;
    this.infoDialog = paramToolDialog2;
    this.edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    try {
      PolicyParser.PermissionEntry permissionEntry = this.infoDialog.getPermFromDialog();
      try {
        this.tool.verifyPermission(permissionEntry.permission, permissionEntry.name, permissionEntry.action);
      } catch (ClassNotFoundException classNotFoundException) {
        MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.Class.not.found.class"));
        Object[] arrayOfObject = { permissionEntry.permission };
        this.tool.warnings.addElement(messageFormat.format(arrayOfObject));
        this.tw.displayStatusDialog(this.infoDialog, messageFormat.format(arrayOfObject));
      } 
      TaggedList taggedList = (TaggedList)this.listDialog.getComponent(8);
      String str = ToolDialog.PermissionEntryToUserFriendlyString(permissionEntry);
      if (this.edit) {
        int i = taggedList.getSelectedIndex();
        taggedList.replaceTaggedItem(str, permissionEntry, i);
      } else {
        taggedList.addTaggedItem(str, permissionEntry);
      } 
      this.infoDialog.dispose();
    } catch (InvocationTargetException invocationTargetException) {
      this.tw.displayErrorDialog(this.infoDialog, invocationTargetException.getTargetException());
    } catch (Exception exception) {
      this.tw.displayErrorDialog(this.infoDialog, exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\NewPolicyPermOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */