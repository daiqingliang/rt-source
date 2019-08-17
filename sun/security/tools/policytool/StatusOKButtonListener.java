package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatusOKButtonListener implements ActionListener {
  private ToolDialog sd;
  
  StatusOKButtonListener(ToolDialog paramToolDialog) { this.sd = paramToolDialog; }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    this.sd.setVisible(false);
    this.sd.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\StatusOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */