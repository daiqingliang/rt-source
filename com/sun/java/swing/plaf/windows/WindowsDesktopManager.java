package com.sun.java.swing.plaf.windows;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;
import javax.swing.plaf.UIResource;

public class WindowsDesktopManager extends DefaultDesktopManager implements Serializable, UIResource {
  private WeakReference<JInternalFrame> currentFrameRef;
  
  public void activateFrame(JInternalFrame paramJInternalFrame) {
    JInternalFrame jInternalFrame = (this.currentFrameRef != null) ? (JInternalFrame)this.currentFrameRef.get() : null;
    try {
      super.activateFrame(paramJInternalFrame);
      if (jInternalFrame != null && paramJInternalFrame != jInternalFrame) {
        if (jInternalFrame.isMaximum() && paramJInternalFrame.getClientProperty("JInternalFrame.frameType") != "optionDialog" && !jInternalFrame.isIcon()) {
          jInternalFrame.setMaximum(false);
          if (paramJInternalFrame.isMaximizable())
            if (!paramJInternalFrame.isMaximum()) {
              paramJInternalFrame.setMaximum(true);
            } else if (paramJInternalFrame.isMaximum() && paramJInternalFrame.isIcon()) {
              paramJInternalFrame.setIcon(false);
            } else {
              paramJInternalFrame.setMaximum(false);
            }  
        } 
        if (jInternalFrame.isSelected())
          jInternalFrame.setSelected(false); 
      } 
      if (!paramJInternalFrame.isSelected())
        paramJInternalFrame.setSelected(true); 
    } catch (PropertyVetoException propertyVetoException) {}
    if (paramJInternalFrame != jInternalFrame)
      this.currentFrameRef = new WeakReference(paramJInternalFrame); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsDesktopManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */