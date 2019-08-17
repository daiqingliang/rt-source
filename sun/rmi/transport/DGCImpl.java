package sun.rmi.transport;

import java.net.SocketPermission;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.dgc.DGC;
import java.rmi.dgc.Lease;
import java.rmi.dgc.VMID;
import java.rmi.server.LogStream;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import sun.misc.ObjectInputFilter;
import sun.rmi.runtime.Log;
import sun.rmi.runtime.RuntimeUtil;
import sun.rmi.server.UnicastRef;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.Util;
import sun.security.action.GetLongAction;
import sun.security.action.GetPropertyAction;

final class DGCImpl implements DGC {
  static final Log dgcLog = Log.getLog("sun.rmi.dgc", "dgc", LogStream.parseLevel((String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.dgc.logLevel"))));
  
  private static final long leaseValue = ((Long)AccessController.doPrivileged(new GetLongAction("java.rmi.dgc.leaseValue", 600000L))).longValue();
  
  private static final long leaseCheckInterval = ((Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.checkInterval", leaseValue / 2L))).longValue();
  
  private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged(new RuntimeUtil.GetInstanceAction())).getScheduler();
  
  private static DGCImpl dgc;
  
  private Map<VMID, LeaseInfo> leaseTable = new HashMap();
  
  private Future<?> checker = null;
  
  private static final String DGC_FILTER_PROPNAME = "sun.rmi.transport.dgcFilter";
  
  private static int DGC_MAX_DEPTH = 5;
  
  private static int DGC_MAX_ARRAY_SIZE = 10000;
  
  private static final ObjectInputFilter dgcFilter = (ObjectInputFilter)AccessController.doPrivileged(DGCImpl::initDgcFilter);
  
  static DGCImpl getDGCImpl() { return dgc; }
  
  private static ObjectInputFilter initDgcFilter() {
    ObjectInputFilter objectInputFilter = null;
    String str = System.getProperty("sun.rmi.transport.dgcFilter");
    if (str == null)
      str = Security.getProperty("sun.rmi.transport.dgcFilter"); 
    if (str != null) {
      objectInputFilter = ObjectInputFilter.Config.createFilter(str);
      if (dgcLog.isLoggable(Log.BRIEF))
        dgcLog.log(Log.BRIEF, "dgcFilter = " + objectInputFilter); 
    } 
    return objectInputFilter;
  }
  
  private DGCImpl() {}
  
  public Lease dirty(ObjID[] paramArrayOfObjID, long paramLong, Lease paramLease) {
    VMID vMID = paramLease.getVMID();
    long l = leaseValue;
    if (dgcLog.isLoggable(Log.VERBOSE))
      dgcLog.log(Log.VERBOSE, "vmid = " + vMID); 
    if (vMID == null) {
      vMID = new VMID();
      if (dgcLog.isLoggable(Log.BRIEF)) {
        String str;
        try {
          str = RemoteServer.getClientHost();
        } catch (ServerNotActiveException serverNotActiveException) {
          str = "<unknown host>";
        } 
        dgcLog.log(Log.BRIEF, " assigning vmid " + vMID + " to client " + str);
      } 
    } 
    paramLease = new Lease(vMID, l);
    synchronized (this.leaseTable) {
      LeaseInfo leaseInfo = (LeaseInfo)this.leaseTable.get(vMID);
      if (leaseInfo == null) {
        this.leaseTable.put(vMID, new LeaseInfo(vMID, l));
        if (this.checker == null)
          this.checker = scheduler.scheduleWithFixedDelay(new Runnable() {
                public void run() { DGCImpl.this.checkLeases(); }
              },  leaseCheckInterval, leaseCheckInterval, TimeUnit.MILLISECONDS); 
      } else {
        leaseInfo.renew(l);
      } 
    } 
    for (ObjID objID : paramArrayOfObjID) {
      if (dgcLog.isLoggable(Log.VERBOSE))
        dgcLog.log(Log.VERBOSE, "id = " + objID + ", vmid = " + vMID + ", duration = " + l); 
      ObjectTable.referenced(objID, paramLong, vMID);
    } 
    return paramLease;
  }
  
  public void clean(ObjID[] paramArrayOfObjID, long paramLong, VMID paramVMID, boolean paramBoolean) {
    for (ObjID objID : paramArrayOfObjID) {
      if (dgcLog.isLoggable(Log.VERBOSE))
        dgcLog.log(Log.VERBOSE, "id = " + objID + ", vmid = " + paramVMID + ", strong = " + paramBoolean); 
      ObjectTable.unreferenced(objID, paramLong, paramVMID, paramBoolean);
    } 
  }
  
  void registerTarget(VMID paramVMID, Target paramTarget) {
    synchronized (this.leaseTable) {
      LeaseInfo leaseInfo = (LeaseInfo)this.leaseTable.get(paramVMID);
      if (leaseInfo == null) {
        paramTarget.vmidDead(paramVMID);
      } else {
        leaseInfo.notifySet.add(paramTarget);
      } 
    } 
  }
  
  void unregisterTarget(VMID paramVMID, Target paramTarget) {
    synchronized (this.leaseTable) {
      LeaseInfo leaseInfo = (LeaseInfo)this.leaseTable.get(paramVMID);
      if (leaseInfo != null)
        leaseInfo.notifySet.remove(paramTarget); 
    } 
  }
  
  private void checkLeases() {
    long l = System.currentTimeMillis();
    ArrayList arrayList = new ArrayList();
    synchronized (this.leaseTable) {
      Iterator iterator = this.leaseTable.values().iterator();
      while (iterator.hasNext()) {
        LeaseInfo leaseInfo = (LeaseInfo)iterator.next();
        if (leaseInfo.expired(l)) {
          arrayList.add(leaseInfo);
          iterator.remove();
        } 
      } 
      if (this.leaseTable.isEmpty()) {
        this.checker.cancel(false);
        this.checker = null;
      } 
    } 
    for (LeaseInfo leaseInfo : arrayList) {
      for (Target target : leaseInfo.notifySet)
        target.vmidDead(leaseInfo.vmid); 
    } 
  }
  
  private static ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo paramFilterInfo) {
    if (dgcFilter != null) {
      ObjectInputFilter.Status status = dgcFilter.checkInput(paramFilterInfo);
      if (status != ObjectInputFilter.Status.UNDECIDED)
        return status; 
    } 
    if (paramFilterInfo.depth() > DGC_MAX_DEPTH)
      return ObjectInputFilter.Status.REJECTED; 
    Class clazz = paramFilterInfo.serialClass();
    if (clazz != null) {
      while (clazz.isArray()) {
        if (paramFilterInfo.arrayLength() >= 0L && paramFilterInfo.arrayLength() > DGC_MAX_ARRAY_SIZE)
          return ObjectInputFilter.Status.REJECTED; 
        clazz = clazz.getComponentType();
      } 
      return clazz.isPrimitive() ? ObjectInputFilter.Status.ALLOWED : ((clazz == ObjID.class || clazz == java.rmi.server.UID.class || clazz == VMID.class || clazz == Lease.class) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.REJECTED);
    } 
    return ObjectInputFilter.Status.UNDECIDED;
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            classLoader = Thread.currentThread().getContextClassLoader();
            try {
              Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
              try {
                dgc = new DGCImpl(null);
                final ObjID dgcID = new ObjID(2);
                LiveRef liveRef = new LiveRef(objID, 0);
                final UnicastServerRef disp = new UnicastServerRef(liveRef, param1FilterInfo -> DGCImpl.checkInput(param1FilterInfo));
                final Remote stub = Util.createProxy(DGCImpl.class, new UnicastRef(liveRef), true);
                unicastServerRef.setSkeleton(dgc);
                Permissions permissions = new Permissions();
                permissions.add(new SocketPermission("*", "accept,resolve"));
                ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, permissions) };
                AccessControlContext accessControlContext = new AccessControlContext(arrayOfProtectionDomain);
                Target target = (Target)AccessController.doPrivileged(new PrivilegedAction<Target>() {
                      public Target run() { return new Target(dgc, disp, stub, dgcID, true); }
                    }accessControlContext);
                ObjectTable.putTarget(target);
              } catch (RemoteException remoteException) {
                throw new Error("exception initializing server-side DGC", remoteException);
              } 
            } finally {
              Thread.currentThread().setContextClassLoader(classLoader);
            } 
            return null;
          }
        });
  }
  
  private static class LeaseInfo {
    VMID vmid;
    
    long expiration;
    
    Set<Target> notifySet = new HashSet();
    
    LeaseInfo(VMID param1VMID, long param1Long) {
      this.vmid = param1VMID;
      this.expiration = System.currentTimeMillis() + param1Long;
    }
    
    void renew(long param1Long) {
      long l = System.currentTimeMillis() + param1Long;
      if (l > this.expiration)
        this.expiration = l; 
    }
    
    boolean expired(long param1Long) {
      if (this.expiration < param1Long) {
        if (DGCImpl.dgcLog.isLoggable(Log.BRIEF))
          DGCImpl.dgcLog.log(Log.BRIEF, this.vmid.toString()); 
        return true;
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\DGCImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */