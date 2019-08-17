package javax.swing.text;

import java.util.Vector;
import sun.awt.AppContext;

public class LayoutQueue {
  private static final Object DEFAULT_QUEUE = new Object();
  
  private Vector<Runnable> tasks = new Vector();
  
  private Thread worker;
  
  public static LayoutQueue getDefaultQueue() {
    AppContext appContext = AppContext.getAppContext();
    synchronized (DEFAULT_QUEUE) {
      LayoutQueue layoutQueue = (LayoutQueue)appContext.get(DEFAULT_QUEUE);
      if (layoutQueue == null) {
        layoutQueue = new LayoutQueue();
        appContext.put(DEFAULT_QUEUE, layoutQueue);
      } 
      return layoutQueue;
    } 
  }
  
  public static void setDefaultQueue(LayoutQueue paramLayoutQueue) {
    synchronized (DEFAULT_QUEUE) {
      AppContext.getAppContext().put(DEFAULT_QUEUE, paramLayoutQueue);
    } 
  }
  
  public void addTask(Runnable paramRunnable) {
    if (this.worker == null) {
      this.worker = new LayoutThread();
      this.worker.start();
    } 
    this.tasks.addElement(paramRunnable);
    notifyAll();
  }
  
  protected Runnable waitForWork() {
    while (this.tasks.size() == 0) {
      try {
        wait();
      } catch (InterruptedException interruptedException) {
        return null;
      } 
    } 
    Runnable runnable = (Runnable)this.tasks.firstElement();
    this.tasks.removeElementAt(0);
    return runnable;
  }
  
  class LayoutThread extends Thread {
    LayoutThread() {
      super("text-layout");
      setPriority(1);
    }
    
    public void run() {
      Runnable runnable;
      do {
        runnable = LayoutQueue.this.waitForWork();
        if (runnable == null)
          continue; 
        runnable.run();
      } while (runnable != null);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\LayoutQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */