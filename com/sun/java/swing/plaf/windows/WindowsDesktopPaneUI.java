package com.sun.java.swing.plaf.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class WindowsDesktopPaneUI extends BasicDesktopPaneUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsDesktopPaneUI(); }
  
  protected void installDesktopManager() {
    this.desktopManager = this.desktop.getDesktopManager();
    if (this.desktopManager == null) {
      this.desktopManager = new WindowsDesktopManager();
      this.desktop.setDesktopManager(this.desktopManager);
    } 
  }
  
  protected void installDefaults() { super.installDefaults(); }
  
  protected void installKeyboardActions() {
    super.installKeyboardActions();
    if (!this.desktop.requestDefaultFocus())
      this.desktop.requestFocus(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsDesktopPaneUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */