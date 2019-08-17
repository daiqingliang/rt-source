package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import sun.security.provider.PolicyParser;

class NewPolicyPrinOKButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog listDialog;
  
  private ToolDialog infoDialog;
  
  private boolean edit;
  
  NewPolicyPrinOKButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog1, ToolDialog paramToolDialog2, boolean paramBoolean) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.listDialog = paramToolDialog1;
    this.infoDialog = paramToolDialog2;
    this.edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    try {
      PolicyParser.PrincipalEntry principalEntry = this.infoDialog.getPrinFromDialog();
      if (principalEntry != null) {
        try {
          this.tool.verifyPrincipal(principalEntry.getPrincipalClass(), principalEntry.getPrincipalName());
        } catch (ClassNotFoundException classNotFoundException) {
          MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.Class.not.found.class"));
          Object[] arrayOfObject = { principalEntry.getPrincipalClass() };
          this.tool.warnings.addElement(messageFormat.format(arrayOfObject));
          this.tw.displayStatusDialog(this.infoDialog, messageFormat.format(arrayOfObject));
        } 
        TaggedList taggedList = (TaggedList)this.listDialog.getComponent(6);
        String str = ToolDialog.PrincipalEntryToUserFriendlyString(principalEntry);
        if (this.edit) {
          int i = taggedList.getSelectedIndex();
          taggedList.replaceTaggedItem(str, principalEntry, i);
        } else {
          taggedList.addTaggedItem(str, principalEntry);
        } 
      } 
      this.infoDialog.dispose();
    } catch (Exception exception) {
      this.tw.displayErrorDialog(this.infoDialog, exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\NewPolicyPrinOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */