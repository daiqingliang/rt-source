package sun.awt.datatransfer;

public interface ToolkitThreadBlockedHandler {
  void lock();
  
  void unlock();
  
  void enter();
  
  void exit();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\datatransfer\ToolkitThreadBlockedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */