package sun.misc;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.SortedSet;
import java.util.TreeSet;

public class GC {
  private static final long NO_TARGET = 9223372036854775807L;
  
  private static long latencyTarget = Float.MAX_VALUE;
  
  private static Thread daemon = null;
  
  private static Object lock = new LatencyLock(null);
  
  public static native long maxObjectInspectionAge();
  
  private static void setLatencyTarget(long paramLong) {
    latencyTarget = paramLong;
    if (daemon == null) {
      Daemon.create();
    } else {
      lock.notify();
    } 
  }
  
  public static LatencyRequest requestLatency(long paramLong) { return new LatencyRequest(paramLong, null); }
  
  public static long currentLatencyTarget() {
    long l = latencyTarget;
    return (l == Float.MAX_VALUE) ? 0L : l;
  }
  
  private static class Daemon extends Thread {
    public void run() {
      while (true) {
        synchronized (lock) {
          long l1 = latencyTarget;
          if (l1 == Float.MAX_VALUE) {
            daemon = null;
            return;
          } 
          long l2 = GC.maxObjectInspectionAge();
          if (l2 >= l1) {
            System.gc();
            l2 = 0L;
          } 
          try {
            lock.wait(l1 - l2);
          } catch (InterruptedException interruptedException) {}
        } 
      } 
    }
    
    private Daemon(ThreadGroup param1ThreadGroup) { super(param1ThreadGroup, "GC Daemon"); }
    
    public static void create() {
      PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
          public Void run() {
            ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
            for (ThreadGroup threadGroup2 = threadGroup1; threadGroup2 != null; threadGroup2 = threadGroup1.getParent())
              threadGroup1 = threadGroup2; 
            GC.Daemon daemon = new GC.Daemon(threadGroup1, null);
            daemon.setDaemon(true);
            daemon.setPriority(2);
            daemon.start();
            daemon = daemon;
            return null;
          }
        };
      AccessController.doPrivileged(privilegedAction);
    }
  }
  
  private static class LatencyLock {
    private LatencyLock() {}
  }
  
  public static class LatencyRequest extends Object implements Comparable<LatencyRequest> {
    private static long counter = 0L;
    
    private static SortedSet<LatencyRequest> requests = null;
    
    private long latency;
    
    private long id;
    
    private static void adjustLatencyIfNeeded() {
      if (requests == null || requests.isEmpty()) {
        if (latencyTarget != Float.MAX_VALUE)
          GC.setLatencyTarget(Float.MAX_VALUE); 
      } else {
        LatencyRequest latencyRequest = (LatencyRequest)requests.first();
        if (latencyRequest.latency != latencyTarget)
          GC.setLatencyTarget(latencyRequest.latency); 
      } 
    }
    
    private LatencyRequest(long param1Long) {
      if (param1Long <= 0L)
        throw new IllegalArgumentException("Non-positive latency: " + param1Long); 
      this.latency = param1Long;
      synchronized (lock) {
        this.id = ++counter;
        if (requests == null)
          requests = new TreeSet(); 
        requests.add(this);
        adjustLatencyIfNeeded();
      } 
    }
    
    public void cancel() {
      synchronized (lock) {
        if (this.latency == Float.MAX_VALUE)
          throw new IllegalStateException("Request already cancelled"); 
        if (!requests.remove(this))
          throw new InternalError("Latency request " + this + " not found"); 
        if (requests.isEmpty())
          requests = null; 
        this.latency = Float.MAX_VALUE;
        adjustLatencyIfNeeded();
      } 
    }
    
    public int compareTo(LatencyRequest param1LatencyRequest) {
      long l = this.latency - param1LatencyRequest.latency;
      if (l == 0L)
        l = this.id - param1LatencyRequest.id; 
      return (l < 0L) ? -1 : ((l > 0L) ? 1 : 0);
    }
    
    public String toString() { return LatencyRequest.class.getName() + "[" + this.latency + "," + this.id + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\GC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */