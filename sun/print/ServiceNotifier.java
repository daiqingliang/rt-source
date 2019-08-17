package sun.print;

import java.util.Vector;
import javax.print.PrintService;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeEvent;
import javax.print.event.PrintServiceAttributeListener;

class ServiceNotifier extends Thread {
  private PrintService service;
  
  private Vector listeners;
  
  private boolean stop = false;
  
  private PrintServiceAttributeSet lastSet;
  
  ServiceNotifier(PrintService paramPrintService) {
    super(paramPrintService.getName() + " notifier");
    this.service = paramPrintService;
    this.listeners = new Vector();
    try {
      setPriority(4);
      setDaemon(true);
      start();
    } catch (SecurityException securityException) {}
  }
  
  void addListener(PrintServiceAttributeListener paramPrintServiceAttributeListener) {
    synchronized (this) {
      if (paramPrintServiceAttributeListener == null || this.listeners == null)
        return; 
      this.listeners.add(paramPrintServiceAttributeListener);
    } 
  }
  
  void removeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener) {
    synchronized (this) {
      if (paramPrintServiceAttributeListener == null || this.listeners == null)
        return; 
      this.listeners.remove(paramPrintServiceAttributeListener);
    } 
  }
  
  boolean isEmpty() { return (this.listeners == null || this.listeners.isEmpty()); }
  
  void stopNotifier() { this.stop = true; }
  
  void wake() {
    try {
      interrupt();
    } catch (SecurityException securityException) {}
  }
  
  public void run() {
    long l1 = 15000L;
    long l2 = 2000L;
    while (!this.stop) {
      try {
        Thread.sleep(l2);
      } catch (InterruptedException interruptedException) {}
      synchronized (this) {
        if (this.listeners == null)
          continue; 
        long l = System.currentTimeMillis();
        if (this.listeners != null) {
          PrintServiceAttributeSet printServiceAttributeSet;
          if (this.service instanceof AttributeUpdater) {
            printServiceAttributeSet = ((AttributeUpdater)this.service).getUpdatedAttributes();
          } else {
            printServiceAttributeSet = this.service.getAttributes();
          } 
          if (printServiceAttributeSet != null && !printServiceAttributeSet.isEmpty())
            for (byte b = 0; b < this.listeners.size(); b++) {
              PrintServiceAttributeListener printServiceAttributeListener = (PrintServiceAttributeListener)this.listeners.elementAt(b);
              HashPrintServiceAttributeSet hashPrintServiceAttributeSet = new HashPrintServiceAttributeSet(printServiceAttributeSet);
              PrintServiceAttributeEvent printServiceAttributeEvent = new PrintServiceAttributeEvent(this.service, hashPrintServiceAttributeSet);
              printServiceAttributeListener.attributeUpdate(printServiceAttributeEvent);
            }  
        } 
        l2 = (System.currentTimeMillis() - l) * 10L;
        if (l2 < l1)
          l2 = l1; 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\ServiceNotifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */