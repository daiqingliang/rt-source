package sun.rmi.transport;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.net.SocketPermission;
import java.rmi.RemoteException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.ObjID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.misc.GC;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.NewThreadAction;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.Util;
import sun.security.action.GetLongAction;

final class DGCClient {
  private static long nextSequenceNum = Float.MIN_VALUE;
  
  private static VMID vmid = new VMID();
  
  private static final long leaseValue = ((Long)AccessController.doPrivileged(new GetLongAction("java.rmi.dgc.leaseValue", 600000L))).longValue();
  
  private static final long cleanInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.cleanInterval", 180000L))).longValue();
  
  private static final long gcInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.client.gcInterval", 3600000L))).longValue();
  
  private static final int dirtyFailureRetries = 5;
  
  private static final int cleanFailureRetries = 5;
  
  private static final ObjID[] emptyObjIDArray = new ObjID[0];
  
  private static final ObjID dgcID = new ObjID(2);
  
  private static final AccessControlContext SOCKET_ACC;
  
  static void registerRefs(Endpoint paramEndpoint, List<LiveRef> paramList) {
    EndpointEntry endpointEntry;
    do {
      endpointEntry = EndpointEntry.lookup(paramEndpoint);
    } while (!endpointEntry.registerRefs(paramList));
  }
  
  private static long getNextSequenceNum() { return nextSequenceNum++; }
  
  private static long computeRenewTime(long paramLong1, long paramLong2) { return paramLong1 + paramLong2 / 2L; }
  
  static  {
    Permissions permissions = new Permissions();
    permissions.add(new SocketPermission("*", "connect,resolve"));
    ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, permissions) };
    SOCKET_ACC = new AccessControlContext(arrayOfProtectionDomain);
  }
  
  private static class EndpointEntry {
    private Endpoint endpoint;
    
    private DGC dgc;
    
    private Map<LiveRef, RefEntry> refTable = new HashMap(5);
    
    private Set<RefEntry> invalidRefs = new HashSet(5);
    
    private boolean removed = false;
    
    private long renewTime = Float.MAX_VALUE;
    
    private long expirationTime = Float.MIN_VALUE;
    
    private int dirtyFailures = 0;
    
    private long dirtyFailureStartTime;
    
    private long dirtyFailureDuration;
    
    private Thread renewCleanThread;
    
    private boolean interruptible = false;
    
    private ReferenceQueue<LiveRef> refQueue = new ReferenceQueue();
    
    private Set<CleanRequest> pendingCleans = new HashSet(5);
    
    private static Map<Endpoint, EndpointEntry> endpointTable = new HashMap(5);
    
    private static GC.LatencyRequest gcLatencyRequest = null;
    
    public static EndpointEntry lookup(Endpoint param1Endpoint) {
      synchronized (endpointTable) {
        EndpointEntry endpointEntry = (EndpointEntry)endpointTable.get(param1Endpoint);
        if (endpointEntry == null) {
          endpointEntry = new EndpointEntry(param1Endpoint);
          endpointTable.put(param1Endpoint, endpointEntry);
          if (gcLatencyRequest == null)
            gcLatencyRequest = GC.requestLatency(gcInterval); 
        } 
        return endpointEntry;
      } 
    }
    
    private EndpointEntry(Endpoint param1Endpoint) {
      this.endpoint = param1Endpoint;
      try {
        LiveRef liveRef = new LiveRef(dgcID, param1Endpoint, false);
        this.dgc = (DGC)Util.createProxy(DGCImpl.class, new UnicastRef(liveRef), true);
      } catch (RemoteException remoteException) {
        throw new Error("internal error creating DGC stub");
      } 
      this.renewCleanThread = (Thread)AccessController.doPrivileged(new NewThreadAction(new RenewCleanThread(this, null), "RenewClean-" + param1Endpoint, true));
      this.renewCleanThread.start();
    }
    
    public boolean registerRefs(List<LiveRef> param1List) {
      long l;
      assert !Thread.holdsLock(this);
      HashSet hashSet = null;
      synchronized (this) {
        if (this.removed)
          return false; 
        for (LiveRef liveRef : param1List) {
          assert liveRef.getEndpoint().equals(this.endpoint);
          RefEntry refEntry = (RefEntry)this.refTable.get(liveRef);
          if (refEntry == null) {
            LiveRef liveRef1 = (LiveRef)liveRef.clone();
            refEntry = new RefEntry(liveRef1);
            this.refTable.put(liveRef1, refEntry);
            if (hashSet == null)
              hashSet = new HashSet(5); 
            hashSet.add(refEntry);
          } 
          refEntry.addInstanceToRefSet(liveRef);
        } 
        if (hashSet == null)
          return true; 
        hashSet.addAll(this.invalidRefs);
        this.invalidRefs.clear();
        l = DGCClient.getNextSequenceNum();
      } 
      makeDirtyCall(hashSet, l);
      return true;
    }
    
    private void removeRefEntry(RefEntry param1RefEntry) {
      assert Thread.holdsLock(this);
      assert !this.removed;
      assert this.refTable.containsKey(param1RefEntry.getRef());
      this.refTable.remove(param1RefEntry.getRef());
      this.invalidRefs.remove(param1RefEntry);
      if (this.refTable.isEmpty())
        synchronized (endpointTable) {
          endpointTable.remove(this.endpoint);
          Transport transport = this.endpoint.getOutboundTransport();
          transport.free(this.endpoint);
          if (endpointTable.isEmpty()) {
            assert gcLatencyRequest != null;
            gcLatencyRequest.cancel();
            gcLatencyRequest = null;
          } 
          this.removed = true;
        }  
    }
    
    private void makeDirtyCall(Set<RefEntry> param1Set, long param1Long) {
      ObjID[] arrayOfObjID;
      assert !Thread.holdsLock(this);
      if (param1Set != null) {
        arrayOfObjID = createObjIDArray(param1Set);
      } else {
        arrayOfObjID = emptyObjIDArray;
      } 
      long l = System.currentTimeMillis();
      try {
        Lease lease = this.dgc.dirty(arrayOfObjID, param1Long, new Lease(vmid, leaseValue));
        long l1 = lease.getValue();
        long l2 = DGCClient.computeRenewTime(l, l1);
        long l3 = l + l1;
        synchronized (this) {
          this.dirtyFailures = 0;
          setRenewTime(l2);
          this.expirationTime = l3;
        } 
      } catch (Exception exception) {
        long l1 = System.currentTimeMillis();
        synchronized (this) {
          this.dirtyFailures++;
          if (exception instanceof java.rmi.UnmarshalException && exception.getCause() instanceof java.io.InvalidClassException) {
            DGCImpl.dgcLog.log(Log.BRIEF, "InvalidClassException exception in DGC dirty call", exception);
            return;
          } 
          if (this.dirtyFailures == 1) {
            this.dirtyFailureStartTime = l;
            this.dirtyFailureDuration = l1 - l;
            setRenewTime(l1);
          } else {
            int i = this.dirtyFailures - 2;
            if (i == 0)
              this.dirtyFailureDuration = Math.max(this.dirtyFailureDuration + l1 - l >> true, 1000L); 
            long l2 = l1 + (this.dirtyFailureDuration << i);
            if (l2 < this.expirationTime || this.dirtyFailures < 5 || l2 < this.dirtyFailureStartTime + leaseValue) {
              setRenewTime(l2);
            } else {
              setRenewTime(Float.MAX_VALUE);
            } 
          } 
          if (param1Set != null) {
            this.invalidRefs.addAll(param1Set);
            for (RefEntry refEntry : param1Set)
              refEntry.markDirtyFailed(); 
          } 
          if (this.renewTime >= this.expirationTime)
            this.invalidRefs.addAll(this.refTable.values()); 
        } 
      } 
    }
    
    private void setRenewTime(long param1Long) {
      assert Thread.holdsLock(this);
      if (param1Long < this.renewTime) {
        this.renewTime = param1Long;
        if (this.interruptible)
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                  DGCClient.EndpointEntry.this.renewCleanThread.interrupt();
                  return null;
                }
              }); 
      } else {
        this.renewTime = param1Long;
      } 
    }
    
    private void processPhantomRefs(RefEntry.PhantomLiveRef param1PhantomLiveRef) {
      assert Thread.holdsLock(this);
      HashSet hashSet1 = null;
      HashSet hashSet2 = null;
      do {
        RefEntry refEntry = param1PhantomLiveRef.getRefEntry();
        refEntry.removeInstanceFromRefSet(param1PhantomLiveRef);
        if (!refEntry.isRefSetEmpty())
          continue; 
        if (refEntry.hasDirtyFailed()) {
          if (hashSet1 == null)
            hashSet1 = new HashSet(5); 
          hashSet1.add(refEntry);
        } else {
          if (hashSet2 == null)
            hashSet2 = new HashSet(5); 
          hashSet2.add(refEntry);
        } 
        removeRefEntry(refEntry);
      } while ((param1PhantomLiveRef = (RefEntry.PhantomLiveRef)this.refQueue.poll()) != null);
      if (hashSet1 != null)
        this.pendingCleans.add(new CleanRequest(createObjIDArray(hashSet1), DGCClient.getNextSequenceNum(), true)); 
      if (hashSet2 != null)
        this.pendingCleans.add(new CleanRequest(createObjIDArray(hashSet2), DGCClient.getNextSequenceNum(), false)); 
    }
    
    private void makeCleanCalls() {
      assert !Thread.holdsLock(this);
      Iterator iterator = this.pendingCleans.iterator();
      while (iterator.hasNext()) {
        CleanRequest cleanRequest = (CleanRequest)iterator.next();
        try {
          this.dgc.clean(cleanRequest.objIDs, cleanRequest.sequenceNum, vmid, cleanRequest.strong);
          iterator.remove();
        } catch (Exception exception) {
          if (++cleanRequest.failures >= 5)
            iterator.remove(); 
        } 
      } 
    }
    
    private static ObjID[] createObjIDArray(Set<RefEntry> param1Set) {
      ObjID[] arrayOfObjID = new ObjID[param1Set.size()];
      Iterator iterator = param1Set.iterator();
      for (byte b = 0; b < arrayOfObjID.length; b++)
        arrayOfObjID[b] = ((RefEntry)iterator.next()).getRef().getObjID(); 
      return arrayOfObjID;
    }
    
    private static class CleanRequest {
      final ObjID[] objIDs;
      
      final long sequenceNum;
      
      final boolean strong;
      
      int failures = 0;
      
      CleanRequest(ObjID[] param2ArrayOfObjID, long param2Long, boolean param2Boolean) {
        this.objIDs = param2ArrayOfObjID;
        this.sequenceNum = param2Long;
        this.strong = param2Boolean;
      }
    }
    
    private class RefEntry {
      private LiveRef ref;
      
      private Set<PhantomLiveRef> refSet = new HashSet(5);
      
      private boolean dirtyFailed = false;
      
      public RefEntry(LiveRef param2LiveRef) { this.ref = param2LiveRef; }
      
      public LiveRef getRef() { return this.ref; }
      
      public void addInstanceToRefSet(LiveRef param2LiveRef) {
        assert Thread.holdsLock(DGCClient.EndpointEntry.this);
        assert param2LiveRef.equals(this.ref);
        this.refSet.add(new PhantomLiveRef(param2LiveRef));
      }
      
      public void removeInstanceFromRefSet(PhantomLiveRef param2PhantomLiveRef) {
        assert Thread.holdsLock(DGCClient.EndpointEntry.this);
        assert this.refSet.contains(param2PhantomLiveRef);
        this.refSet.remove(param2PhantomLiveRef);
      }
      
      public boolean isRefSetEmpty() {
        assert Thread.holdsLock(DGCClient.EndpointEntry.this);
        return (this.refSet.size() == 0);
      }
      
      public void markDirtyFailed() {
        assert Thread.holdsLock(DGCClient.EndpointEntry.this);
        this.dirtyFailed = true;
      }
      
      public boolean hasDirtyFailed() {
        assert Thread.holdsLock(DGCClient.EndpointEntry.this);
        return this.dirtyFailed;
      }
      
      private class PhantomLiveRef extends PhantomReference<LiveRef> {
        public PhantomLiveRef(LiveRef param3LiveRef) { super(param3LiveRef, this$0.this$0.refQueue); }
        
        public DGCClient.EndpointEntry.RefEntry getRefEntry() { return DGCClient.EndpointEntry.RefEntry.this; }
      }
    }
    
    private class PhantomLiveRef extends PhantomReference<LiveRef> {
      public PhantomLiveRef(LiveRef param2LiveRef) { super(param2LiveRef, this$0.this$0.refQueue); }
      
      public DGCClient.EndpointEntry.RefEntry getRefEntry() { return this.this$1; }
    }
    
    private class RenewCleanThread implements Runnable {
      private RenewCleanThread() {}
      
      public void run() {
        do {
          long l1;
          DGCClient.EndpointEntry.RefEntry.PhantomLiveRef phantomLiveRef = null;
          boolean bool = false;
          Set set1 = null;
          long l2 = Float.MIN_VALUE;
          synchronized (DGCClient.EndpointEntry.this) {
            long l = DGCClient.EndpointEntry.this.renewTime - System.currentTimeMillis();
            l1 = Math.max(l, 1L);
            if (!DGCClient.EndpointEntry.this.pendingCleans.isEmpty())
              l1 = Math.min(l1, cleanInterval); 
            DGCClient.EndpointEntry.this.interruptible = true;
          } 
          try {
            phantomLiveRef = (DGCClient.EndpointEntry.RefEntry.PhantomLiveRef)DGCClient.EndpointEntry.this.refQueue.remove(l1);
          } catch (InterruptedException interruptedException) {}
          synchronized (DGCClient.EndpointEntry.this) {
            DGCClient.EndpointEntry.this.interruptible = false;
            Thread.interrupted();
            if (phantomLiveRef != null)
              DGCClient.EndpointEntry.this.processPhantomRefs(phantomLiveRef); 
            long l = System.currentTimeMillis();
            if (l > DGCClient.EndpointEntry.this.renewTime) {
              bool = true;
              if (!DGCClient.EndpointEntry.this.invalidRefs.isEmpty()) {
                set1 = DGCClient.EndpointEntry.this.invalidRefs;
                DGCClient.EndpointEntry.this.invalidRefs = new HashSet(5);
              } 
              l2 = DGCClient.getNextSequenceNum();
            } 
          } 
          final boolean needRenewal_ = bool;
          final Set refsToDirty_ = set1;
          final long sequenceNum_ = l2;
          AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                  if (needRenewal_)
                    DGCClient.EndpointEntry.RenewCleanThread.this.this$0.makeDirtyCall(refsToDirty_, sequenceNum_); 
                  if (!DGCClient.EndpointEntry.RenewCleanThread.this.this$0.pendingCleans.isEmpty())
                    DGCClient.EndpointEntry.RenewCleanThread.this.this$0.makeCleanCalls(); 
                  return null;
                }
              }SOCKET_ACC);
        } while (!DGCClient.EndpointEntry.this.removed || !DGCClient.EndpointEntry.this.pendingCleans.isEmpty());
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\DGCClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */