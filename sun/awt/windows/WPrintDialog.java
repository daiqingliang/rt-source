package sun.awt.windows;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.peer.ComponentPeer;
import java.awt.print.PrinterJob;
import sun.awt.AWTAccessor;

class WPrintDialog extends Dialog {
  protected PrintJob job;
  
  protected PrinterJob pjob;
  
  private boolean retval = false;
  
  WPrintDialog(Frame paramFrame, PrinterJob paramPrinterJob) {
    super(paramFrame, true);
    this.pjob = paramPrinterJob;
    setLayout(null);
  }
  
  WPrintDialog(Dialog paramDialog, PrinterJob paramPrinterJob) {
    super(paramDialog, "", true);
    this.pjob = paramPrinterJob;
    setLayout(null);
  }
  
  final void setPeer(ComponentPeer paramComponentPeer) { AWTAccessor.getComponentAccessor().setPeer(this, paramComponentPeer); }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      Container container = getParent();
      if (container != null && container.getPeer() == null)
        container.addNotify(); 
      if (getPeer() == null) {
        WPrintDialogPeer wPrintDialogPeer = ((WToolkit)Toolkit.getDefaultToolkit()).createWPrintDialog(this);
        setPeer(wPrintDialogPeer);
      } 
      super.addNotify();
    } 
  }
  
  final void setRetVal(boolean paramBoolean) { this.retval = paramBoolean; }
  
  final boolean getRetVal() { return this.retval; }
  
  private static native void initIDs();
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPrintDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */