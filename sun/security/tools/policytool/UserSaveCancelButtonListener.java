package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveCancelButtonListener implements ActionListener {
  private ToolDialog us;
  
  UserSaveCancelButtonListener(ToolDialog paramToolDialog) { this.us = paramToolDialog; }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    this.us.setVisible(false);
    this.us.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\UserSaveCancelButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */