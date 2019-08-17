package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ErrorOKButtonListener implements ActionListener {
  private ToolDialog ed;
  
  ErrorOKButtonListener(ToolDialog paramToolDialog) { this.ed = paramToolDialog; }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    this.ed.setVisible(false);
    this.ed.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ErrorOKButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */