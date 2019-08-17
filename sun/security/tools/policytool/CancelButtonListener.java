package sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CancelButtonListener implements ActionListener {
  private ToolDialog td;
  
  CancelButtonListener(ToolDialog paramToolDialog) { this.td = paramToolDialog; }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    this.td.setVisible(false);
    this.td.dispose();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\CancelButtonListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */