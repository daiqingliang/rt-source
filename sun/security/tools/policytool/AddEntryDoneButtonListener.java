package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;
import java.text.MessageFormat;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import sun.security.provider.PolicyParser;

class AddEntryDoneButtonListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  private ToolDialog td;
  
  private boolean edit;
  
  AddEntryDoneButtonListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow, ToolDialog paramToolDialog, boolean paramBoolean) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
    this.td = paramToolDialog;
    this.edit = paramBoolean;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    try {
      PolicyEntry policyEntry = this.td.getPolicyEntryFromDialog();
      PolicyParser.GrantEntry grantEntry = policyEntry.getGrantEntry();
      if (grantEntry.signedBy != null) {
        String[] arrayOfString = this.tool.parseSigners(grantEntry.signedBy);
        for (byte b = 0; b < arrayOfString.length; b++) {
          PublicKey publicKey = this.tool.getPublicKeyAlias(arrayOfString[b]);
          if (publicKey == null) {
            MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
            Object[] arrayOfObject = { arrayOfString[b] };
            this.tool.warnings.addElement(messageFormat.format(arrayOfObject));
            this.tw.displayStatusDialog(this.td, messageFormat.format(arrayOfObject));
          } 
        } 
      } 
      JList jList = (JList)this.tw.getComponent(3);
      if (this.edit) {
        int i = jList.getSelectedIndex();
        this.tool.addEntry(policyEntry, i);
        String str = policyEntry.headerToString();
        if (PolicyTool.collator.compare(str, jList.getModel().getElementAt(i)) != 0)
          this.tool.modified = true; 
        ((DefaultListModel)jList.getModel()).set(i, str);
      } else {
        this.tool.addEntry(policyEntry, -1);
        ((DefaultListModel)jList.getModel()).addElement(policyEntry.headerToString());
        this.tool.modified = true;
      } 
      this.td.setVisible(false);
      this.td.dispose();
    } catch (Exception exception) {
      this.tw.displayErrorDialog(this.td, exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\AddEntryDoneButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */