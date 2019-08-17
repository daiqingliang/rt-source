package sun.rmi.transport;

import java.lang.ref.ReferenceQueue;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.dgc.VMID;
import java.rmi.server.ExportException;
import java.rmi.server.ObjID;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import sun.misc.GC;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.security.action.GetLongAction;

public final class ObjectTable {
  private static final long gcInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.server.gcInterval", 3600000L))).longValue();
  
  private static final Object tableLock = new Object();
  
  private static final Map<ObjectEndpoint, Target> objTable = new HashMap();
  
  private static final Map<WeakRef, Target> implTable = new HashMap();
  
  private static final Object keepAliveLock = new Object();
  
  private static int keepAliveCount = 0;
  
  private static Thread reaper = null;
  
  static final ReferenceQueue<Object> reapQueue = new ReferenceQueue();
  
  private static GC.LatencyRequest gcLatencyRequest = null;
  
  static Target getTarget(ObjectEndpoint paramObjectEndpoint) {
    synchronized (tableLock) {
      return (Target)objTable.get(paramObjectEndpoint);
    } 
  }
  
  public static Target getTarget(Remote paramRemote) {
    synchronized (tableLock) {
      return (Target)implTable.get(new WeakRef(paramRemote));
    } 
  }
  
  public static Remote getStub(Remote paramRemote) throws NoSuchObjectException {
    Target target = getTarget(paramRemote);
    if (target == null)
      throw new NoSuchObjectException("object not exported"); 
    return target.getStub();
  }
  
  public static boolean unexportObject(Remote paramRemote, boolean paramBoolean) throws NoSuchObjectException {
    synchronized (tableLock) {
      Target target = getTarget(paramRemote);
      if (target == null)
        throw new NoSuchObjectException("object not exported"); 
      if (target.unexport(paramBoolean)) {
        removeTarget(target);
        return true;
      } 
      return false;
    } 
  }
  
  static void putTarget(Target paramTarget) throws ExportException {
    ObjectEndpoint objectEndpoint = paramTarget.getObjectEndpoint();
    WeakRef weakRef = paramTarget.getWeakImpl();
    if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE))
      DGCImpl.dgcLog.log(Log.VERBOSE, "add object " + objectEndpoint); 
    synchronized (tableLock) {
      if (paramTarget.getImpl() != null) {
        if (objTable.containsKey(objectEndpoint))
          throw new ExportException("internal error: ObjID already in use"); 
        if (implTable.containsKey(weakRef))
          throw new ExportException("object already exported"); 
        objTable.put(objectEndpoint, paramTarget);
        implTable.put(weakRef, paramTarget);
        if (!paramTarget.isPermanent())
          incrementKeepAliveCount(); 
      } 
    } 
  }
  
  private static void removeTarget(Target paramTarget) throws ExportException {
    ObjectEndpoint objectEndpoint = paramTarget.getObjectEndpoint();
    WeakRef weakRef = paramTarget.getWeakImpl();
    if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE))
      DGCImpl.dgcLog.log(Log.VERBOSE, "remove object " + objectEndpoint); 
    objTable.remove(objectEndpoint);
    implTable.remove(weakRef);
    paramTarget.markRemoved();
  }
  
  static void referenced(ObjID paramObjID, long paramLong, VMID paramVMID) {
    synchronized (tableLock) {
      ObjectEndpoint objectEndpoint = new ObjectEndpoint(paramObjID, Transport.currentTransport());
      Target target = (Target)objTable.get(objectEndpoint);
      if (target != null)
        target.referenced(paramLong, paramVMID); 
    } 
  }
  
  static void unreferenced(ObjID paramObjID, long paramLong, VMID paramVMID, boolean paramBoolean) {
    synchronized (tableLock) {
      ObjectEndpoint objectEndpoint = new ObjectEndpoint(paramObjID, Transport.currentTransport());
      Target target = (Target)objTable.get(objectEndpoint);
      if (target != null)
        target.unreferenced(paramLong, paramVMID, paramBoolean); 
    } 
  }
  
  static void incrementKeepAliveCount() {
    synchronized (keepAliveLock) {
      keepAliveCount++;
      if (reaper == null) {
        reaper = (Thread)AccessController.doPrivileged(new NewThreadAction(new Reaper(null), "Reaper", false));
        reaper.start();
      } 
      if (gcLatencyRequest == null)
        gcLatencyRequest = GC.requestLatency(gcInterval); 
    } 
  }
  
  static void decrementKeepAliveCount() {
    synchronized (keepAliveLock) {
      keepAliveCount--;
      if (keepAliveCount == 0) {
        if (reaper == null)
          throw new AssertionError(); 
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                reaper.interrupt();
                return null;
              }
            });
        reaper = null;
        gcLatencyRequest.cancel();
        gcLatencyRequest = null;
      } 
    } 
  }
  
  private static class Reaper implements Runnable {
    private Reaper() {}
    
    public void run() {
      try {
        do {
          WeakRef weakRef = (WeakRef)ObjectTable.reapQueue.remove();
          synchronized (tableLock) {
            Target target = (Target)implTable.get(weakRef);
            if (target != null) {
              if (!target.isEmpty())
                throw new Error("object with known references collected"); 
              if (target.isPermanent())
                throw new Error("permanent object collected"); 
              ObjectTable.removeTarget(target);
            } 
          } 
        } while (!Thread.interrupted());
      } catch (InterruptedException interruptedException) {}
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\ObjectTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */