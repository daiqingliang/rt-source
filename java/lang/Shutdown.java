package java.lang;

class Shutdown {
  private static final int RUNNING = 0;
  
  private static final int HOOKS = 1;
  
  private static final int FINALIZERS = 2;
  
  private static int state = 0;
  
  private static boolean runFinalizersOnExit = false;
  
  private static final int MAX_SYSTEM_HOOKS = 10;
  
  private static final Runnable[] hooks = new Runnable[10];
  
  private static int currentRunningHook = 0;
  
  private static Object lock = new Lock(null);
  
  private static Object haltLock = new Lock(null);
  
  static void setRunFinalizersOnExit(boolean paramBoolean) {
    synchronized (lock) {
      runFinalizersOnExit = paramBoolean;
    } 
  }
  
  static void add(int paramInt, boolean paramBoolean, Runnable paramRunnable) {
    synchronized (lock) {
      if (hooks[paramInt] != null)
        throw new InternalError("Shutdown hook at slot " + paramInt + " already registered"); 
      if (!paramBoolean) {
        if (state > 0)
          throw new IllegalStateException("Shutdown in progress"); 
      } else if (state > 1 || (state == 1 && paramInt <= currentRunningHook)) {
        throw new IllegalStateException("Shutdown in progress");
      } 
      hooks[paramInt] = paramRunnable;
    } 
  }
  
  private static void runHooks() {
    for (byte b = 0; b < 10; b++) {
      try {
        Runnable runnable;
        synchronized (lock) {
          currentRunningHook = b;
          runnable = hooks[b];
        } 
        if (runnable != null)
          runnable.run(); 
      } catch (Throwable throwable) {
        if (throwable instanceof ThreadDeath) {
          ThreadDeath threadDeath = (ThreadDeath)throwable;
          throw threadDeath;
        } 
      } 
    } 
  }
  
  static void halt(int paramInt) {
    synchronized (haltLock) {
      halt0(paramInt);
    } 
  }
  
  static native void halt0(int paramInt);
  
  private static native void runAllFinalizers();
  
  private static void sequence() {
    boolean bool;
    synchronized (lock) {
      if (state != 1)
        return; 
    } 
    runHooks();
    synchronized (lock) {
      state = 2;
      bool = runFinalizersOnExit;
    } 
    if (bool)
      runAllFinalizers(); 
  }
  
  static void exit(int paramInt) {
    boolean bool = false;
    synchronized (lock) {
      if (paramInt != 0)
        runFinalizersOnExit = false; 
      switch (state) {
        case 0:
          state = 1;
          break;
        case 2:
          if (paramInt != 0) {
            halt(paramInt);
            break;
          } 
          bool = runFinalizersOnExit;
          break;
      } 
    } 
    if (bool) {
      runAllFinalizers();
      halt(paramInt);
    } 
    synchronized (Shutdown.class) {
      sequence();
      halt(paramInt);
    } 
  }
  
  static void shutdown() {
    synchronized (lock) {
      switch (state) {
        case 0:
          state = 1;
          break;
      } 
    } 
    synchronized (Shutdown.class) {
      sequence();
    } 
  }
  
  private static class Lock {
    private Lock() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Shutdown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */