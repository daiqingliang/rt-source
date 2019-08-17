package sun.awt.windows;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

final class WPageDialog extends WPrintDialog {
  PageFormat page;
  
  Printable painter;
  
  WPageDialog(Frame paramFrame, PrinterJob paramPrinterJob, PageFormat paramPageFormat, Printable paramPrintable) {
    super(paramFrame, paramPrinterJob);
    this.page = paramPageFormat;
    this.painter = paramPrintable;
  }
  
  WPageDialog(Dialog paramDialog, PrinterJob paramPrinterJob, PageFormat paramPageFormat, Printable paramPrintable) {
    super(paramDialog, paramPrinterJob);
    this.page = paramPageFormat;
    this.painter = paramPrintable;
  }
  
  public void addNotify() {
    synchronized (getTreeLock()) {
      Container container = getParent();
      if (container != null && container.getPeer() == null)
        container.addNotify(); 
      if (getPeer() == null) {
        WPageDialogPeer wPageDialogPeer = ((WToolkit)Toolkit.getDefaultToolkit()).createWPageDialog(this);
        setPeer(wPageDialogPeer);
      } 
      super.addNotify();
    } 
  }
  
  private static native void initIDs();
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPageDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */