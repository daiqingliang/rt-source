package sun.awt;

import java.awt.AWTEvent;
import java.security.AccessController;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import sun.misc.ThreadGroupUtils;
import sun.util.logging.PlatformLogger;

public final class AWTAutoShutdown implements Runnable {
  private static final AWTAutoShutdown theInstance = new AWTAutoShutdown();
  
  private final Object mainLock = new Object();
  
  private final Object activationLock = new Object();
  
  private final Set<Thread> busyThreadSet = new HashSet(7);
  
  private boolean toolkitThreadBusy = false;
  
  private final Map<Object, Object> peerMap = new IdentityHashMap();
  
  private Thread blockerThread = null;
  
  private boolean timeoutPassed = false;
  
  private static final int SAFETY_TIMEOUT = 1000;
  
  public static AWTAutoShutdown getInstance() { return theInstance; }
  
  public static void notifyToolkitThreadBusy() { getInstance().setToolkitBusy(true); }
  
  public static void notifyToolkitThreadFree() { getInstance().setToolkitBusy(false); }
  
  public void notifyThreadBusy(Thread paramThread) {
    if (paramThread == null)
      return; 
    synchronized (this.activationLock) {
      synchronized (this.mainLock) {
        if (this.blockerThread == null) {
          activateBlockerThread();
        } else if (isReadyToShutdown()) {
          this.mainLock.notifyAll();
          this.timeoutPassed = false;
        } 
        this.busyThreadSet.add(paramThread);
      } 
    } 
  }
  
  public void notifyThreadFree(Thread paramThread) {
    if (paramThread == null)
      return; 
    synchronized (this.activationLock) {
      synchronized (this.mainLock) {
        this.busyThreadSet.remove(paramThread);
        if (isReadyToShutdown()) {
          this.mainLock.notifyAll();
          this.timeoutPassed = false;
        } 
      } 
    } 
  }
  
  void notifyPeerMapUpdated() {
    synchronized (this.activationLock) {
      synchronized (this.mainLock) {
        if (!isReadyToShutdown() && this.blockerThread == null) {
          AccessController.doPrivileged(() -> {
                activateBlockerThread();
                return null;
              });
        } else {
          this.mainLock.notifyAll();
          this.timeoutPassed = false;
        } 
      } 
    } 
  }
  
  private boolean isReadyToShutdown() { return (!this.toolkitThreadBusy && this.peerMap.isEmpty() && this.busyThreadSet.isEmpty()); }
  
  private void setToolkitBusy(boolean paramBoolean) {
    if (paramBoolean != this.toolkitThreadBusy)
      synchronized (this.activationLock) {
        synchronized (this.mainLock) {
          if (paramBoolean != this.toolkitThreadBusy)
            if (paramBoolean) {
              if (this.blockerThread == null) {
                activateBlockerThread();
              } else if (isReadyToShutdown()) {
                this.mainLock.notifyAll();
                this.timeoutPassed = false;
              } 
              this.toolkitThreadBusy = paramBoolean;
            } else {
              this.toolkitThreadBusy = paramBoolean;
              if (isReadyToShutdown()) {
                this.mainLock.notifyAll();
                this.timeoutPassed = false;
              } 
            }  
        } 
      }  
  }
  
  public void run() {
    thread = Thread.currentThread();
    boolean bool = false;
    synchronized (this.mainLock) {
      try {
        this.mainLock.notifyAll();
        while (this.blockerThread == thread) {
          this.mainLock.wait();
          this.timeoutPassed = false;
          while (isReadyToShutdown()) {
            if (this.timeoutPassed) {
              this.timeoutPassed = false;
              this.blockerThread = null;
              break;
            } 
            this.timeoutPassed = true;
            this.mainLock.wait(1000L);
          } 
        } 
      } catch (InterruptedException interruptedException) {
        bool = true;
      } finally {
        if (this.blockerThread == thread)
          this.blockerThread = null; 
      } 
    } 
    if (!bool)
      AppContext.stopEventDispatchThreads(); 
  }
  
  static AWTEvent getShutdownEvent() { return new AWTEvent(getInstance(), 0) {
      
      }; }
  
  private void activateBlockerThread() {
    Thread thread = new Thread(ThreadGroupUtils.getRootThreadGroup(), this, "AWT-Shutdown");
    thread.setContextClassLoader(null);
    thread.setDaemon(false);
    this.blockerThread = thread;
    thread.start();
    try {
      this.mainLock.wait();
    } catch (InterruptedException interruptedException) {
      System.err.println("AWT blocker activation interrupted:");
      interruptedException.printStackTrace();
    } 
  }
  
  final void registerPeer(Object paramObject1, Object paramObject2) {
    synchronized (this.activationLock) {
      synchronized (this.mainLock) {
        this.peerMap.put(paramObject1, paramObject2);
        notifyPeerMapUpdated();
      } 
    } 
  }
  
  final void unregisterPeer(Object paramObject1, Object paramObject2) {
    synchronized (this.activationLock) {
      synchronized (this.mainLock) {
        if (this.peerMap.get(paramObject1) == paramObject2) {
          this.peerMap.remove(paramObject1);
          notifyPeerMapUpdated();
        } 
      } 
    } 
  }
  
  final Object getPeer(Object paramObject) {
    synchronized (this.activationLock) {
      synchronized (this.mainLock) {
        return this.peerMap.get(paramObject);
      } 
    } 
  }
  
  final void dumpPeers(PlatformLogger paramPlatformLogger) {
    if (paramPlatformLogger.isLoggable(PlatformLogger.Level.FINE))
      synchronized (this.activationLock) {
        synchronized (this.mainLock) {
          paramPlatformLogger.fine("Mapped peers:");
          for (Object object : this.peerMap.keySet())
            paramPlatformLogger.fine(object + "->" + this.peerMap.get(object)); 
        } 
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\AWTAutoShutdown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */