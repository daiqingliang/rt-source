package sun.awt;

import java.awt.SecondaryLoop;

public interface FwDispatcher {
  boolean isDispatchThread();
  
  void scheduleDispatch(Runnable paramRunnable);
  
  SecondaryLoop createSecondaryLoop();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\FwDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */