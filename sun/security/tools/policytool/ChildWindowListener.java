package sun.security.tools.policytool;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ChildWindowListener implements WindowListener {
  private ToolDialog td;
  
  ChildWindowListener(ToolDialog paramToolDialog) { this.td = paramToolDialog; }
  
  public void windowOpened(WindowEvent paramWindowEvent) {}
  
  public void windowClosing(WindowEvent paramWindowEvent) {
    this.td.setVisible(false);
    this.td.dispose();
  }
  
  public void windowClosed(WindowEvent paramWindowEvent) {}
  
  public void windowIconified(WindowEvent paramWindowEvent) {}
  
  public void windowDeiconified(WindowEvent paramWindowEvent) {}
  
  public void windowActivated(WindowEvent paramWindowEvent) {}
  
  public void windowDeactivated(WindowEvent paramWindowEvent) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\ChildWindowListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */