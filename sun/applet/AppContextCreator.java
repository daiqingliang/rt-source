package sun.applet;

import sun.awt.AppContext;
import sun.awt.SunToolkit;

class AppContextCreator extends Thread {
  Object syncObject = new Object();
  
  AppContext appContext = null;
  
  AppContextCreator(ThreadGroup paramThreadGroup) { super(paramThreadGroup, "AppContextCreator"); }
  
  public void run() {
    this.appContext = SunToolkit.createNewAppContext();
    this.created = true;
    synchronized (this.syncObject) {
      this.syncObject.notifyAll();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppContextCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */