package com.sun.java.swing.plaf.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class WindowsDesktopIconUI extends BasicDesktopIconUI {
  private int width;
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsDesktopIconUI(); }
  
  public void installDefaults() {
    super.installDefaults();
    this.width = UIManager.getInt("DesktopIcon.width");
  }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    paramJComponent.setOpaque((XPStyle.getXP() == null));
  }
  
  public void uninstallUI(JComponent paramJComponent) {
    WindowsInternalFrameTitlePane windowsInternalFrameTitlePane = (WindowsInternalFrameTitlePane)this.iconPane;
    super.uninstallUI(paramJComponent);
    windowsInternalFrameTitlePane.uninstallListeners();
  }
  
  protected void installComponents() {
    this.iconPane = new WindowsInternalFrameTitlePane(this.frame);
    this.desktopIcon.setLayout(new BorderLayout());
    this.desktopIcon.add(this.iconPane, "Center");
    if (XPStyle.getXP() != null)
      this.desktopIcon.setBorder(null); 
  }
  
  public Dimension getPreferredSize(JComponent paramJComponent) { return getMinimumSize(paramJComponent); }
  
  public Dimension getMinimumSize(JComponent paramJComponent) {
    Dimension dimension = super.getMinimumSize(paramJComponent);
    dimension.width = this.width;
    return dimension;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsDesktopIconUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */