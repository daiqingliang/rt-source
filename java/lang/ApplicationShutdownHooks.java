package java.lang;

import java.util.IdentityHashMap;
import java.util.Set;

class ApplicationShutdownHooks {
  private static IdentityHashMap<Thread, Thread> hooks;
  
  static void add(Thread paramThread) {
    if (hooks == null)
      throw new IllegalStateException("Shutdown in progress"); 
    if (paramThread.isAlive())
      throw new IllegalArgumentException("Hook already running"); 
    if (hooks.containsKey(paramThread))
      throw new IllegalArgumentException("Hook previously registered"); 
    hooks.put(paramThread, paramThread);
  }
  
  static boolean remove(Thread paramThread) {
    if (hooks == null)
      throw new IllegalStateException("Shutdown in progress"); 
    if (paramThread == null)
      throw new NullPointerException(); 
    return (hooks.remove(paramThread) != null);
  }
  
  static void runHooks() {
    Set set;
    synchronized (ApplicationShutdownHooks.class) {
      set = hooks.keySet();
      hooks = null;
    } 
    for (Thread thread : set)
      thread.start(); 
    label23: for (Thread thread : set) {
      while (true) {
        try {
          thread.join();
          continue label23;
        } catch (InterruptedException interruptedException) {}
      } 
    } 
  }
  
  static  {
    try {
      Shutdown.add(1, false, new Runnable() {
            public void run() { ApplicationShutdownHooks.runHooks(); }
          });
      hooks = new IdentityHashMap();
    } catch (IllegalStateException illegalStateException) {
      hooks = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ApplicationShutdownHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */