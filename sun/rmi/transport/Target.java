package sun.rmi.transport;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.rmi.server.Unreferenced;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.server.Dispatcher;

public final class Target {
  private final ObjID id;
  
  private final boolean permanent;
  
  private final WeakRef weakImpl;
  
  private final Remote stub;
  
  private final Vector<VMID> refSet = new Vector();
  
  private final Hashtable<VMID, SequenceEntry> sequenceTable = new Hashtable(5);
  
  private final AccessControlContext acc;
  
  private final ClassLoader ccl;
  
  private int callCount = 0;
  
  private boolean removed = false;
  
  private static int nextThreadNum = 0;
  
  public Target(Remote paramRemote1, Dispatcher paramDispatcher, Remote paramRemote2, ObjID paramObjID, boolean paramBoolean) {
    this.weakImpl = new WeakRef(paramRemote1, ObjectTable.reapQueue);
    this.disp = paramDispatcher;
    this.stub = paramRemote2;
    this.id = paramObjID;
    this.acc = AccessController.getContext();
    ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
    ClassLoader classLoader2 = paramRemote1.getClass().getClassLoader();
    if (checkLoaderAncestry(classLoader1, classLoader2)) {
      this.ccl = classLoader1;
    } else {
      this.ccl = classLoader2;
    } 
    this.permanent = paramBoolean;
    if (paramBoolean)
      pinImpl(); 
  }
  
  private static boolean checkLoaderAncestry(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) {
    if (paramClassLoader2 == null)
      return true; 
    if (paramClassLoader1 == null)
      return false; 
    for (ClassLoader classLoader = paramClassLoader1; classLoader != null; classLoader = classLoader.getParent()) {
      if (classLoader == paramClassLoader2)
        return true; 
    } 
    return false;
  }
  
  public Remote getStub() { return this.stub; }
  
  ObjectEndpoint getObjectEndpoint() { return new ObjectEndpoint(this.id, this.exportedTransport); }
  
  WeakRef getWeakImpl() { return this.weakImpl; }
  
  Dispatcher getDispatcher() { return this.disp; }
  
  AccessControlContext getAccessControlContext() { return this.acc; }
  
  ClassLoader getContextClassLoader() { return this.ccl; }
  
  Remote getImpl() { return (Remote)this.weakImpl.get(); }
  
  boolean isPermanent() { return this.permanent; }
  
  void pinImpl() { this.weakImpl.pin(); }
  
  void unpinImpl() {
    if (!this.permanent && this.refSet.isEmpty())
      this.weakImpl.unpin(); 
  }
  
  void setExportedTransport(Transport paramTransport) {
    if (this.exportedTransport == null)
      this.exportedTransport = paramTransport; 
  }
  
  void referenced(long paramLong, VMID paramVMID) {
    SequenceEntry sequenceEntry = (SequenceEntry)this.sequenceTable.get(paramVMID);
    if (sequenceEntry == null) {
      this.sequenceTable.put(paramVMID, new SequenceEntry(paramLong));
    } else if (sequenceEntry.sequenceNum < paramLong) {
      sequenceEntry.update(paramLong);
    } else {
      return;
    } 
    if (!this.refSet.contains(paramVMID)) {
      pinImpl();
      if (getImpl() == null)
        return; 
      if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE))
        DGCImpl.dgcLog.log(Log.VERBOSE, "add to dirty set: " + paramVMID); 
      this.refSet.addElement(paramVMID);
      DGCImpl.getDGCImpl().registerTarget(paramVMID, this);
    } 
  }
  
  void unreferenced(long paramLong, VMID paramVMID, boolean paramBoolean) {
    SequenceEntry sequenceEntry = (SequenceEntry)this.sequenceTable.get(paramVMID);
    if (sequenceEntry == null || sequenceEntry.sequenceNum > paramLong)
      return; 
    if (paramBoolean) {
      sequenceEntry.retain(paramLong);
    } else if (!sequenceEntry.keep) {
      this.sequenceTable.remove(paramVMID);
    } 
    if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE))
      DGCImpl.dgcLog.log(Log.VERBOSE, "remove from dirty set: " + paramVMID); 
    refSetRemove(paramVMID);
  }
  
  private void refSetRemove(VMID paramVMID) {
    DGCImpl.getDGCImpl().unregisterTarget(paramVMID, this);
    if (this.refSet.removeElement(paramVMID) && this.refSet.isEmpty()) {
      if (DGCImpl.dgcLog.isLoggable(Log.VERBOSE))
        DGCImpl.dgcLog.log(Log.VERBOSE, "reference set is empty: target = " + this); 
      Remote remote = getImpl();
      if (remote instanceof Unreferenced) {
        Unreferenced unreferenced = (Unreferenced)remote;
        ((Thread)AccessController.doPrivileged(new NewThreadAction(() -> {
                Thread.currentThread().setContextClassLoader(this.ccl);
                AccessController.doPrivileged((), this.acc);
              }"Unreferenced-" + nextThreadNum++, false, true))).start();
      } 
      unpinImpl();
    } 
  }
  
  boolean unexport(boolean paramBoolean) {
    if (paramBoolean == true || this.callCount == 0 || this.disp == null) {
      this.disp = null;
      unpinImpl();
      DGCImpl dGCImpl = DGCImpl.getDGCImpl();
      Enumeration enumeration = this.refSet.elements();
      while (enumeration.hasMoreElements()) {
        VMID vMID = (VMID)enumeration.nextElement();
        dGCImpl.unregisterTarget(vMID, this);
      } 
      return true;
    } 
    return false;
  }
  
  void markRemoved() {
    if (this.removed)
      throw new AssertionError(); 
    this.removed = true;
    if (!this.permanent && this.callCount == 0)
      ObjectTable.decrementKeepAliveCount(); 
    if (this.exportedTransport != null)
      this.exportedTransport.targetUnexported(); 
  }
  
  void incrementCallCount() {
    if (this.disp != null) {
      this.callCount++;
    } else {
      throw new NoSuchObjectException("object not accepting new calls");
    } 
  }
  
  void decrementCallCount() {
    if (--this.callCount < 0)
      throw new Error("internal error: call count less than zero"); 
    if (!this.permanent && this.removed && this.callCount == 0)
      ObjectTable.decrementKeepAliveCount(); 
  }
  
  boolean isEmpty() { return this.refSet.isEmpty(); }
  
  public void vmidDead(VMID paramVMID) {
    if (DGCImpl.dgcLog.isLoggable(Log.BRIEF))
      DGCImpl.dgcLog.log(Log.BRIEF, "removing endpoint " + paramVMID + " from reference set"); 
    this.sequenceTable.remove(paramVMID);
    refSetRemove(paramVMID);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\Target.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */