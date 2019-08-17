package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import javax.swing.JTextField;

class FileMenuListener implements ActionListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  FileMenuListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Exit") == 0) {
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
      toolDialog.displayUserSave(1);
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "New") == 0) {
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
      toolDialog.displayUserSave(2);
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Open") == 0) {
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
      toolDialog.displayUserSave(3);
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Save") == 0) {
      String str = ((JTextField)this.tw.getComponent(1)).getText();
      if (str == null || str.length() == 0) {
        ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Save.As"), this.tool, this.tw, true);
        toolDialog.displaySaveAsDialog(0);
      } else {
        try {
          this.tool.savePolicy(str);
          MessageFormat messageFormat = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
          Object[] arrayOfObject = { str };
          this.tw.displayStatusDialog(null, messageFormat.format(arrayOfObject));
        } catch (FileNotFoundException fileNotFoundException) {
          if (str == null || str.equals("")) {
            this.tw.displayErrorDialog(null, new FileNotFoundException(PolicyTool.getMessage("null.filename")));
          } else {
            this.tw.displayErrorDialog(null, fileNotFoundException);
          } 
        } catch (Exception exception) {
          this.tw.displayErrorDialog(null, exception);
        } 
      } 
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "Save.As") == 0) {
      ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Save.As"), this.tool, this.tw, true);
      toolDialog.displaySaveAsDialog(0);
    } else if (PolicyTool.collator.compare(paramActionEvent.getActionCommand(), "View.Warning.Log") == 0) {
      this.tw.displayWarningLog(null);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\FileMenuListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */