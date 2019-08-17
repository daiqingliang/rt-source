package sun.security.tools.policytool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ToolWindowListener implements WindowListener {
  private PolicyTool tool;
  
  private ToolWindow tw;
  
  ToolWindowListener(PolicyTool paramPolicyTool, ToolWindow paramToolWindow) {
    this.tool = paramPolicyTool;
    this.tw = paramToolWindow;
  }
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent) {
    ToolDialog toolDialog = new ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
    toolDialog.displayUserSave(1);
  }
  
  public void windowClosed(WindowEvent paramWindowEvent) { System.exit(0); }
  
  public void windowIconified(WindowEvent paramWindowEvent) {}
  
  public void windowDeiconified(WindowEvent paramWindowEvent) {}
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ToolWindowListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */