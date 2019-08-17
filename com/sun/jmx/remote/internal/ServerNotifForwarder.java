package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.security.NotificationAccessController;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanPermission;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;
import javax.security.auth.Subject;

public class ServerNotifForwarder {
  private final NotifForwarderBufferFilter bufferFilter = new NotifForwarderBufferFilter();
  
  private MBeanServer mbeanServer;
  
  private final String connectionId;
  
  private final long connectionTimeout;
  
  private static int listenerCounter = 0;
  
  private static final int[] listenerCounterLock = new int[0];
  
  private NotificationBuffer notifBuffer;
  
  private final Map<ObjectName, Set<IdAndFilter>> listenerMap = new HashMap();
  
  private boolean terminated = false;
  
  private final int[] terminationLock = new int[0];
  
  static final String broadcasterClass = javax.management.NotificationBroadcaster.class.getName();
  
  private final boolean checkNotificationEmission;
  
  private final NotificationAccessController notificationAccessController;
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ServerNotifForwarder");
  
  public ServerNotifForwarder(MBeanServer paramMBeanServer, Map<String, ?> paramMap, NotificationBuffer paramNotificationBuffer, String paramString) {
    this.mbeanServer = paramMBeanServer;
    this.notifBuffer = paramNotificationBuffer;
    this.connectionId = paramString;
    this.connectionTimeout = EnvHelp.getServerConnectionTimeout(paramMap);
    String str = (String)paramMap.get("jmx.remote.x.check.notification.emission");
    this.checkNotificationEmission = EnvHelp.computeBooleanFromString(str);
    this.notificationAccessController = EnvHelp.getNotificationAccessController(paramMap);
  }
  
  public Integer addNotificationListener(final ObjectName name, NotificationFilter paramNotificationFilter) throws InstanceNotFoundException, IOException {
    if (logger.traceOn())
      logger.trace("addNotificationListener", "Add a listener at " + paramObjectName); 
    checkState();
    checkMBeanPermission(paramObjectName, "addNotificationListener");
    if (this.notificationAccessController != null)
      this.notificationAccessController.addNotificationListener(this.connectionId, paramObjectName, getSubject()); 
    try {
      boolean bool = ((Boolean)AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
            public Boolean run() throws InstanceNotFoundException { return Boolean.valueOf(ServerNotifForwarder.this.mbeanServer.isInstanceOf(name, ServerNotifForwarder.broadcasterClass)); }
          })).booleanValue();
      if (!bool)
        throw new IllegalArgumentException("The specified MBean [" + paramObjectName + "] is not a NotificationBroadcaster object."); 
    } catch (PrivilegedActionException privilegedActionException) {
      throw (InstanceNotFoundException)extractException(privilegedActionException);
    } 
    Integer integer = getListenerID();
    ObjectName objectName = paramObjectName;
    if (paramObjectName.getDomain() == null || paramObjectName.getDomain().equals(""))
      try {
        objectName = ObjectName.getInstance(this.mbeanServer.getDefaultDomain(), paramObjectName.getKeyPropertyList());
      } catch (MalformedObjectNameException malformedObjectNameException) {
        IOException iOException = new IOException(malformedObjectNameException.getMessage());
        iOException.initCause(malformedObjectNameException);
        throw iOException;
      }  
    synchronized (this.listenerMap) {
      IdAndFilter idAndFilter = new IdAndFilter(integer, paramNotificationFilter);
      Set set = (Set)this.listenerMap.get(objectName);
      if (set == null) {
        set = Collections.singleton(idAndFilter);
      } else {
        if (set.size() == 1)
          set = new HashSet(set); 
        set.add(idAndFilter);
      } 
      this.listenerMap.put(objectName, set);
    } 
    return integer;
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, Integer[] paramArrayOfInteger) throws Exception {
    if (logger.traceOn())
      logger.trace("removeNotificationListener", "Remove some listeners from " + paramObjectName); 
    checkState();
    checkMBeanPermission(paramObjectName, "removeNotificationListener");
    if (this.notificationAccessController != null)
      this.notificationAccessController.removeNotificationListener(this.connectionId, paramObjectName, getSubject()); 
    Exception exception = null;
    for (byte b = 0; b < paramArrayOfInteger.length; b++) {
      try {
        removeNotificationListener(paramObjectName, paramArrayOfInteger[b]);
      } catch (Exception exception1) {
        if (exception != null)
          exception = exception1; 
      } 
    } 
    if (exception != null)
      throw exception; 
  }
  
  public void removeNotificationListener(ObjectName paramObjectName, Integer paramInteger) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
    if (logger.traceOn())
      logger.trace("removeNotificationListener", "Remove the listener " + paramInteger + " from " + paramObjectName); 
    checkState();
    if (paramObjectName != null && !paramObjectName.isPattern() && !this.mbeanServer.isRegistered(paramObjectName))
      throw new InstanceNotFoundException("The MBean " + paramObjectName + " is not registered."); 
    synchronized (this.listenerMap) {
      Set set = (Set)this.listenerMap.get(paramObjectName);
      IdAndFilter idAndFilter = new IdAndFilter(paramInteger, null);
      if (set == null || !set.contains(idAndFilter))
        throw new ListenerNotFoundException("Listener not found"); 
      if (set.size() == 1) {
        this.listenerMap.remove(paramObjectName);
      } else {
        set.remove(idAndFilter);
      } 
    } 
  }
  
  public NotificationResult fetchNotifs(long paramLong1, long paramLong2, int paramInt) {
    NotificationResult notificationResult;
    if (logger.traceOn())
      logger.trace("fetchNotifs", "Fetching notifications, the startSequenceNumber is " + paramLong1 + ", the timeout is " + paramLong2 + ", the maxNotifications is " + paramInt); 
    long l = Math.min(this.connectionTimeout, paramLong2);
    try {
      notificationResult = this.notifBuffer.fetchNotifications(this.bufferFilter, paramLong1, l, paramInt);
      snoopOnUnregister(notificationResult);
    } catch (InterruptedException interruptedException) {
      notificationResult = new NotificationResult(0L, 0L, new TargetedNotification[0]);
    } 
    if (logger.traceOn())
      logger.trace("fetchNotifs", "Forwarding the notifs: " + notificationResult); 
    return notificationResult;
  }
  
  private void snoopOnUnregister(NotificationResult paramNotificationResult) {
    ArrayList arrayList = null;
    synchronized (this.listenerMap) {
      Set set = (Set)this.listenerMap.get(MBeanServerDelegate.DELEGATE_NAME);
      if (set == null || set.isEmpty())
        return; 
      arrayList = new ArrayList(set);
    } 
    for (TargetedNotification targetedNotification : paramNotificationResult.getTargetedNotifications()) {
      Integer integer = targetedNotification.getListenerID();
      Iterator iterator = arrayList.iterator();
      while (iterator.hasNext()) {
        IdAndFilter idAndFilter;
        if (idAndFilter.id == integer) {
          Notification notification = targetedNotification.getNotification();
          if (notification instanceof MBeanServerNotification && notification.getType().equals("JMX.mbean.unregistered")) {
            MBeanServerNotification mBeanServerNotification = (MBeanServerNotification)notification;
            ObjectName objectName = mBeanServerNotification.getMBeanName();
            synchronized (this.listenerMap) {
              this.listenerMap.remove(objectName);
            } 
          } 
        } 
      } 
    } 
  }
  
  public void terminate() {
    if (logger.traceOn())
      logger.trace("terminate", "Be called."); 
    synchronized (this.terminationLock) {
      if (this.terminated)
        return; 
      this.terminated = true;
      synchronized (this.listenerMap) {
        this.listenerMap.clear();
      } 
    } 
    if (logger.traceOn())
      logger.trace("terminate", "Terminated."); 
  }
  
  private Subject getSubject() { return Subject.getSubject(AccessController.getContext()); }
  
  private void checkState() {
    synchronized (this.terminationLock) {
      if (this.terminated)
        throw new IOException("The connection has been terminated."); 
    } 
  }
  
  private Integer getListenerID() {
    synchronized (listenerCounterLock) {
      return Integer.valueOf(listenerCounter++);
    } 
  }
  
  public final void checkMBeanPermission(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException, SecurityException { checkMBeanPermission(this.mbeanServer, paramObjectName, paramString); }
  
  static void checkMBeanPermission(final MBeanServer mbs, final ObjectName name, String paramString) throws InstanceNotFoundException, SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      ObjectInstance objectInstance;
      AccessControlContext accessControlContext = AccessController.getContext();
      try {
        objectInstance = (ObjectInstance)AccessController.doPrivileged(new PrivilegedExceptionAction<ObjectInstance>() {
              public ObjectInstance run() throws InstanceNotFoundException { return mbs.getObjectInstance(name); }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (InstanceNotFoundException)extractException(privilegedActionException);
      } 
      String str = objectInstance.getClassName();
      MBeanPermission mBeanPermission = new MBeanPermission(str, null, paramObjectName, paramString);
      securityManager.checkPermission(mBeanPermission, accessControlContext);
    } 
  }
  
  private boolean allowNotificationEmission(ObjectName paramObjectName, TargetedNotification paramTargetedNotification) {
    try {
      if (this.checkNotificationEmission)
        checkMBeanPermission(paramObjectName, "addNotificationListener"); 
      if (this.notificationAccessController != null)
        this.notificationAccessController.fetchNotification(this.connectionId, paramObjectName, paramTargetedNotification.getNotification(), getSubject()); 
      return true;
    } catch (SecurityException securityException) {
      if (logger.debugOn())
        logger.debug("fetchNotifs", "Notification " + paramTargetedNotification.getNotification() + " not forwarded: the caller didn't have the required access rights"); 
      return false;
    } catch (Exception exception) {
      if (logger.debugOn())
        logger.debug("fetchNotifs", "Notification " + paramTargetedNotification.getNotification() + " not forwarded: got an unexpected exception: " + exception); 
      return false;
    } 
  }
  
  private static Exception extractException(Exception paramException) {
    while (paramException instanceof PrivilegedActionException)
      paramException = ((PrivilegedActionException)paramException).getException(); 
    return paramException;
  }
  
  private static class IdAndFilter {
    private Integer id;
    
    private NotificationFilter filter;
    
    IdAndFilter(Integer param1Integer, NotificationFilter param1NotificationFilter) {
      this.id = param1Integer;
      this.filter = param1NotificationFilter;
    }
    
    Integer getId() { return this.id; }
    
    NotificationFilter getFilter() { return this.filter; }
    
    public int hashCode() { return this.id.hashCode(); }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof IdAndFilter && ((IdAndFilter)param1Object).getId().equals(getId())); }
  }
  
  final class NotifForwarderBufferFilter implements NotificationBufferFilter {
    public void apply(List<TargetedNotification> param1List, ObjectName param1ObjectName, Notification param1Notification) {
      ServerNotifForwarder.IdAndFilter[] arrayOfIdAndFilter;
      synchronized (ServerNotifForwarder.this.listenerMap) {
        Set set = (Set)ServerNotifForwarder.this.listenerMap.get(param1ObjectName);
        if (set == null) {
          logger.debug("bufferFilter", "no listeners for this name");
          return;
        } 
        arrayOfIdAndFilter = new ServerNotifForwarder.IdAndFilter[set.size()];
        set.toArray(arrayOfIdAndFilter);
      } 
      for (ServerNotifForwarder.IdAndFilter idAndFilter : arrayOfIdAndFilter) {
        NotificationFilter notificationFilter = idAndFilter.getFilter();
        if (notificationFilter == null || notificationFilter.isNotificationEnabled(param1Notification)) {
          logger.debug("bufferFilter", "filter matches");
          TargetedNotification targetedNotification = new TargetedNotification(param1Notification, idAndFilter.getId());
          if (ServerNotifForwarder.this.allowNotificationEmission(param1ObjectName, targetedNotification))
            param1List.add(targetedNotification); 
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\ServerNotifForwarder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */