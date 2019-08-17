package sun.awt.windows;

import sun.awt.Mutex;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

final class WToolkitThreadBlockedHandler extends Mutex implements ToolkitThreadBlockedHandler {
  public void enter() {
    if (!isOwned())
      throw new IllegalMonitorStateException(); 
    unlock();
    startSecondaryEventLoop();
    lock();
  }
  
  public void exit() {
    if (!isOwned())
      throw new IllegalMonitorStateException(); 
    WToolkit.quitSecondaryEventLoop();
  }
  
  private native void startSecondaryEventLoop();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WToolkitThreadBlockedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */