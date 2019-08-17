package com.sun.java.swing.plaf.windows;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class WindowsInternalFrameUI extends BasicInternalFrameUI {
  XPStyle xp = XPStyle.getXP();
  
  public void installDefaults() {
    super.installDefaults();
    if (this.xp != null) {
      this.frame.setBorder(new XPBorder(null));
    } else {
      this.frame.setBorder(UIManager.getBorder("InternalFrame.border"));
    } 
  }
  
  public void installUI(JComponent paramJComponent) {
    super.installUI(paramJComponent);
    LookAndFeel.installProperty(paramJComponent, "opaque", (this.xp == null) ? Boolean.TRUE : Boolean.FALSE);
  }
  
  public void uninstallDefaults() {
    this.frame.setBorder(null);
    super.uninstallDefaults();
  }
  
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsInternalFrameUI((JInternalFrame)paramJComponent); }
  
  public WindowsInternalFrameUI(JInternalFrame paramJInternalFrame) { super(paramJInternalFrame); }
  
  protected DesktopManager createDesktopManager() { return new WindowsDesktopManager(); }
  
  protected JComponent createNorthPane(JInternalFrame paramJInternalFrame) {
    this.titlePane = new WindowsInternalFrameTitlePane(paramJInternalFrame);
    return this.titlePane;
  }
  
  private class XPBorder extends AbstractBorder {
    private XPStyle.Skin leftSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMELEFT);
    
    private XPStyle.Skin rightSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMERIGHT);
    
    private XPStyle.Skin bottomSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMEBOTTOM);
    
    private XPBorder() {}
    
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      TMSchema.State state = ((JInternalFrame)param1Component).isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
      int i = (WindowsInternalFrameUI.this.titlePane != null) ? (this.this$0.titlePane.getSize()).height : 0;
      this.bottomSkin.paintSkin(param1Graphics, 0, param1Int4 - this.bottomSkin.getHeight(), param1Int3, this.bottomSkin.getHeight(), state);
      this.leftSkin.paintSkin(param1Graphics, 0, i - 1, this.leftSkin.getWidth(), param1Int4 - i - this.bottomSkin.getHeight() + 2, state);
      this.rightSkin.paintSkin(param1Graphics, param1Int3 - this.rightSkin.getWidth(), i - 1, this.rightSkin.getWidth(), param1Int4 - i - this.bottomSkin.getHeight() + 2, state);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.top = 4;
      param1Insets.left = this.leftSkin.getWidth();
      param1Insets.right = this.rightSkin.getWidth();
      param1Insets.bottom = this.bottomSkin.getHeight();
      return param1Insets;
    }
    
    public boolean isBorderOpaque() { return true; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsInternalFrameUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */