package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.QueryEval;
import javax.management.QueryExp;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;

public class ArrayNotificationBuffer implements NotificationBuffer {
  private boolean disposed = false;
  
  private static final Object globalLock = new Object();
  
  private static final HashMap<MBeanServer, ArrayNotificationBuffer> mbsToBuffer = new HashMap(1);
  
  private final Collection<ShareBuffer> sharers = new HashSet(1);
  
  private final NotificationListener bufferListener = new BufferListener(null);
  
  private static final QueryExp broadcasterQuery = new BroadcasterQuery(null);
  
  private static final NotificationFilter creationFilter;
  
  private final NotificationListener creationListener = new NotificationListener() {
      public void handleNotification(Notification param1Notification, Object param1Object) {
        logger.debug("creationListener", "handleNotification called");
        ArrayNotificationBuffer.this.createdNotification((MBeanServerNotification)param1Notification);
      }
    };
  
  private static final ClassLogger logger;
  
  private final MBeanServer mBeanServer;
  
  private final ArrayQueue<NamedNotification> queue;
  
  private int queueSize;
  
  private long earliestSequenceNumber;
  
  private long nextSequenceNumber;
  
  private Set<ObjectName> createdDuringQuery;
  
  static final String broadcasterClass;
  
  public static NotificationBuffer getNotificationBuffer(MBeanServer paramMBeanServer, Map<String, ?> paramMap) {
    ShareBuffer shareBuffer;
    boolean bool;
    ArrayNotificationBuffer arrayNotificationBuffer;
    if (paramMap == null)
      paramMap = Collections.emptyMap(); 
    int i = EnvHelp.getNotifBufferSize(paramMap);
    synchronized (globalLock) {
      arrayNotificationBuffer = (ArrayNotificationBuffer)mbsToBuffer.get(paramMBeanServer);
      bool = (arrayNotificationBuffer == null) ? 1 : 0;
      if (bool) {
        arrayNotificationBuffer = new ArrayNotificationBuffer(paramMBeanServer, i);
        mbsToBuffer.put(paramMBeanServer, arrayNotificationBuffer);
      } 
      arrayNotificationBuffer.getClass();
      shareBuffer = new ShareBuffer(i);
    } 
    if (bool)
      arrayNotificationBuffer.createListeners(); 
    return shareBuffer;
  }
  
  static void removeNotificationBuffer(MBeanServer paramMBeanServer) {
    synchronized (globalLock) {
      mbsToBuffer.remove(paramMBeanServer);
    } 
  }
  
  void addSharer(ShareBuffer paramShareBuffer) {
    synchronized (globalLock) {
      synchronized (this) {
        if (paramShareBuffer.getSize() > this.queueSize)
          resize(paramShareBuffer.getSize()); 
      } 
      this.sharers.add(paramShareBuffer);
    } 
  }
  
  private void removeSharer(ShareBuffer paramShareBuffer) {
    boolean bool;
    synchronized (globalLock) {
      this.sharers.remove(paramShareBuffer);
      bool = this.sharers.isEmpty();
      if (bool) {
        removeNotificationBuffer(this.mBeanServer);
      } else {
        int i = 0;
        for (ShareBuffer shareBuffer : this.sharers) {
          int j = shareBuffer.getSize();
          if (j > i)
            i = j; 
        } 
        if (i < this.queueSize)
          resize(i); 
      } 
    } 
    if (bool) {
      synchronized (this) {
        this.disposed = true;
        notifyAll();
      } 
      destroyListeners();
    } 
  }
  
  private void resize(int paramInt) {
    if (paramInt == this.queueSize)
      return; 
    while (this.queue.size() > paramInt)
      dropNotification(); 
    this.queue.resize(paramInt);
    this.queueSize = paramInt;
  }
  
  private ArrayNotificationBuffer(MBeanServer paramMBeanServer, int paramInt) {
    if (logger.traceOn())
      logger.trace("Constructor", "queueSize=" + paramInt); 
    if (paramMBeanServer == null || paramInt < 1)
      throw new IllegalArgumentException("Bad args"); 
    this.mBeanServer = paramMBeanServer;
    this.queueSize = paramInt;
    this.queue = new ArrayQueue(paramInt);
    this.earliestSequenceNumber = System.currentTimeMillis();
    this.nextSequenceNumber = this.earliestSequenceNumber;
    logger.trace("Constructor", "ends");
  }
  
  private boolean isDisposed() { return this.disposed; }
  
  public void dispose() { throw new UnsupportedOperationException(); }
  
  public NotificationResult fetchNotifications(NotificationBufferFilter paramNotificationBufferFilter, long paramLong1, long paramLong2, int paramInt) throws InterruptedException {
    logger.trace("fetchNotifications", "starts");
    if (paramLong1 < 0L || isDisposed())
      synchronized (this) {
        return new NotificationResult(earliestSequenceNumber(), nextSequenceNumber(), new TargetedNotification[0]);
      }  
    if (paramNotificationBufferFilter == null || paramLong1 < 0L || paramLong2 < 0L || paramInt < 0) {
      logger.trace("fetchNotifications", "Bad args");
      throw new IllegalArgumentException("Bad args to fetch");
    } 
    if (logger.debugOn())
      logger.trace("fetchNotifications", "filter=" + paramNotificationBufferFilter + "; startSeq=" + paramLong1 + "; timeout=" + paramLong2 + "; max=" + paramInt); 
    if (paramLong1 > nextSequenceNumber()) {
      String str = "Start sequence number too big: " + paramLong1 + " > " + nextSequenceNumber();
      logger.trace("fetchNotifications", str);
      throw new IllegalArgumentException(str);
    } 
    long l1 = System.currentTimeMillis() + paramLong2;
    if (l1 < 0L)
      l1 = Float.MAX_VALUE; 
    if (logger.debugOn())
      logger.debug("fetchNotifications", "endTime=" + l1); 
    long l2 = -1L;
    long l3 = paramLong1;
    ArrayList arrayList = new ArrayList();
    while (true) {
      NamedNotification namedNotification;
      logger.debug("fetchNotifications", "main loop starts");
      synchronized (this) {
        if (l2 < 0L) {
          l2 = earliestSequenceNumber();
          if (logger.debugOn())
            logger.debug("fetchNotifications", "earliestSeq=" + l2); 
          if (l3 < l2) {
            l3 = l2;
            logger.debug("fetchNotifications", "nextSeq=earliestSeq");
          } 
        } else {
          l2 = earliestSequenceNumber();
        } 
        if (l3 < l2) {
          logger.trace("fetchNotifications", "nextSeq=" + l3 + " < earliestSeq=" + l2 + " so may have lost notifs");
          break;
        } 
        if (l3 < nextSequenceNumber()) {
          namedNotification = notificationAt(l3);
          if (!(paramNotificationBufferFilter instanceof ServerNotifForwarder.NotifForwarderBufferFilter))
            try {
              ServerNotifForwarder.checkMBeanPermission(this.mBeanServer, namedNotification.getObjectName(), "addNotificationListener");
            } catch (InstanceNotFoundException|SecurityException instanceNotFoundException) {
              if (logger.debugOn())
                logger.debug("fetchNotifications", "candidate: " + namedNotification + " skipped. exception " + instanceNotFoundException); 
              l3++;
              continue;
            }  
          if (logger.debugOn()) {
            logger.debug("fetchNotifications", "candidate: " + namedNotification);
            logger.debug("fetchNotifications", "nextSeq now " + l3);
          } 
        } else {
          if (arrayList.size() > 0) {
            logger.debug("fetchNotifications", "no more notifs but have some so don't wait");
            break;
          } 
          long l = l1 - System.currentTimeMillis();
          if (l <= 0L) {
            logger.debug("fetchNotifications", "timeout");
            break;
          } 
          if (isDisposed()) {
            if (logger.debugOn())
              logger.debug("fetchNotifications", "dispose callled, no wait"); 
            return new NotificationResult(earliestSequenceNumber(), nextSequenceNumber(), new TargetedNotification[0]);
          } 
          if (logger.debugOn())
            logger.debug("fetchNotifications", "wait(" + l + ")"); 
          wait(l);
          continue;
        } 
      } 
      ObjectName objectName = namedNotification.getObjectName();
      Notification notification = namedNotification.getNotification();
      ArrayList arrayList1 = new ArrayList();
      logger.debug("fetchNotifications", "applying filter to candidate");
      paramNotificationBufferFilter.apply(arrayList1, objectName, notification);
      if (arrayList1.size() > 0) {
        if (paramInt <= 0) {
          logger.debug("fetchNotifications", "reached maxNotifications");
          break;
        } 
        paramInt--;
        if (logger.debugOn())
          logger.debug("fetchNotifications", "add: " + arrayList1); 
        arrayList.addAll(arrayList1);
      } 
      l3++;
    } 
    int i = arrayList.size();
    TargetedNotification[] arrayOfTargetedNotification = new TargetedNotification[i];
    arrayList.toArray(arrayOfTargetedNotification);
    NotificationResult notificationResult = new NotificationResult(l2, l3, arrayOfTargetedNotification);
    if (logger.debugOn())
      logger.debug("fetchNotifications", notificationResult.toString()); 
    logger.trace("fetchNotifications", "ends");
    return notificationResult;
  }
  
  long earliestSequenceNumber() { return this.earliestSequenceNumber; }
  
  long nextSequenceNumber() { return this.nextSequenceNumber; }
  
  void addNotification(NamedNotification paramNamedNotification) {
    if (logger.traceOn())
      logger.trace("addNotification", paramNamedNotification.toString()); 
    while (this.queue.size() >= this.queueSize) {
      dropNotification();
      if (logger.debugOn())
        logger.debug("addNotification", "dropped oldest notif, earliestSeq=" + this.earliestSequenceNumber); 
    } 
    this.queue.add(paramNamedNotification);
    this.nextSequenceNumber++;
    if (logger.debugOn())
      logger.debug("addNotification", "nextSeq=" + this.nextSequenceNumber); 
    notifyAll();
  }
  
  private void dropNotification() {
    this.queue.remove(0);
    this.earliestSequenceNumber++;
  }
  
  NamedNotification notificationAt(long paramLong) {
    long l = paramLong - this.earliestSequenceNumber;
    if (l < 0L || l > 2147483647L) {
      String str = "Bad sequence number: " + paramLong + " (earliest " + this.earliestSequenceNumber + ")";
      logger.trace("notificationAt", str);
      throw new IllegalArgumentException(str);
    } 
    return (NamedNotification)this.queue.get((int)l);
  }
  
  private void createListeners() {
    logger.debug("createListeners", "starts");
    synchronized (this) {
      this.createdDuringQuery = new HashSet();
    } 
    try {
      addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener, creationFilter, null);
      logger.debug("createListeners", "added creationListener");
    } catch (Exception exception) {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Can't add listener to MBean server delegate: " + exception);
      EnvHelp.initCause(illegalArgumentException, exception);
      logger.fine("createListeners", "Can't add listener to MBean server delegate: " + exception);
      logger.debug("createListeners", exception);
      throw illegalArgumentException;
    } 
    Set set = queryNames(null, broadcasterQuery);
    set = new HashSet(set);
    synchronized (this) {
      set.addAll(this.createdDuringQuery);
      this.createdDuringQuery = null;
    } 
    for (ObjectName objectName : set)
      addBufferListener(objectName); 
    logger.debug("createListeners", "ends");
  }
  
  private void addBufferListener(ObjectName paramObjectName) {
    checkNoLocks();
    if (logger.debugOn())
      logger.debug("addBufferListener", paramObjectName.toString()); 
    try {
      addNotificationListener(paramObjectName, this.bufferListener, null, paramObjectName);
    } catch (Exception exception) {
      logger.trace("addBufferListener", exception);
    } 
  }
  
  private void removeBufferListener(ObjectName paramObjectName) {
    checkNoLocks();
    if (logger.debugOn())
      logger.debug("removeBufferListener", paramObjectName.toString()); 
    try {
      removeNotificationListener(paramObjectName, this.bufferListener);
    } catch (Exception exception) {
      logger.trace("removeBufferListener", exception);
    } 
  }
  
  private void addNotificationListener(final ObjectName name, final NotificationListener listener, final NotificationFilter filter, final Object handback) throws Exception {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws InstanceNotFoundException {
              ArrayNotificationBuffer.this.mBeanServer.addNotificationListener(name, listener, filter, handback);
              return null;
            }
          });
    } catch (Exception exception) {
      throw extractException(exception);
    } 
  }
  
  private void removeNotificationListener(final ObjectName name, final NotificationListener listener) throws Exception {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws InstanceNotFoundException {
              ArrayNotificationBuffer.this.mBeanServer.removeNotificationListener(name, listener);
              return null;
            }
          });
    } catch (Exception exception) {
      throw extractException(exception);
    } 
  }
  
  private Set<ObjectName> queryNames(final ObjectName name, final QueryExp query) {
    PrivilegedAction<Set<ObjectName>> privilegedAction = new PrivilegedAction<Set<ObjectName>>() {
        public Set<ObjectName> run() { return ArrayNotificationBuffer.this.mBeanServer.queryNames(name, query); }
      };
    try {
      return (Set)AccessController.doPrivileged(privilegedAction);
    } catch (RuntimeException runtimeException) {
      logger.fine("queryNames", "Failed to query names: " + runtimeException);
      logger.debug("queryNames", runtimeException);
      throw runtimeException;
    } 
  }
  
  private static boolean isInstanceOf(final MBeanServer mbs, final ObjectName name, final String className) {
    PrivilegedExceptionAction<Boolean> privilegedExceptionAction = new PrivilegedExceptionAction<Boolean>() {
        public Boolean run() throws InstanceNotFoundException { return Boolean.valueOf(mbs.isInstanceOf(name, className)); }
      };
    try {
      return ((Boolean)AccessController.doPrivileged(privilegedExceptionAction)).booleanValue();
    } catch (Exception exception) {
      logger.fine("isInstanceOf", "failed: " + exception);
      logger.debug("isInstanceOf", exception);
      return false;
    } 
  }
  
  private void createdNotification(MBeanServerNotification paramMBeanServerNotification) {
    if (!paramMBeanServerNotification.getType().equals("JMX.mbean.registered")) {
      logger.warning("createNotification", "bad type: " + paramMBeanServerNotification.getType());
      return;
    } 
    ObjectName objectName = paramMBeanServerNotification.getMBeanName();
    if (logger.debugOn())
      logger.debug("createdNotification", "for: " + objectName); 
    synchronized (this) {
      if (this.createdDuringQuery != null) {
        this.createdDuringQuery.add(objectName);
        return;
      } 
    } 
    if (isInstanceOf(this.mBeanServer, objectName, broadcasterClass)) {
      addBufferListener(objectName);
      if (isDisposed())
        removeBufferListener(objectName); 
    } 
  }
  
  private void destroyListeners() {
    checkNoLocks();
    logger.debug("destroyListeners", "starts");
    try {
      removeNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this.creationListener);
    } catch (Exception exception) {
      logger.warning("remove listener from MBeanServer delegate", exception);
    } 
    Set set = queryNames(null, broadcasterQuery);
    for (ObjectName objectName : set) {
      if (logger.debugOn())
        logger.debug("destroyListeners", "remove listener from " + objectName); 
      removeBufferListener(objectName);
    } 
    logger.debug("destroyListeners", "ends");
  }
  
  private void checkNoLocks() {
    if (Thread.holdsLock(this) || Thread.holdsLock(globalLock))
      logger.warning("checkNoLocks", "lock protocol violation"); 
  }
  
  private static Exception extractException(Exception paramException) {
    while (paramException instanceof PrivilegedActionException)
      paramException = ((PrivilegedActionException)paramException).getException(); 
    return paramException;
  }
  
  static  {
    NotificationFilterSupport notificationFilterSupport = new NotificationFilterSupport();
    notificationFilterSupport.enableType("JMX.mbean.registered");
    creationFilter = notificationFilterSupport;
    logger = new ClassLogger("javax.management.remote.misc", "ArrayNotificationBuffer");
    broadcasterClass = javax.management.NotificationBroadcaster.class.getName();
  }
  
  private static class BroadcasterQuery extends QueryEval implements QueryExp {
    private static final long serialVersionUID = 7378487660587592048L;
    
    private BroadcasterQuery() {}
    
    public boolean apply(ObjectName param1ObjectName) {
      MBeanServer mBeanServer = QueryEval.getMBeanServer();
      return ArrayNotificationBuffer.isInstanceOf(mBeanServer, param1ObjectName, ArrayNotificationBuffer.broadcasterClass);
    }
  }
  
  private class BufferListener implements NotificationListener {
    private BufferListener() {}
    
    public void handleNotification(Notification param1Notification, Object param1Object) {
      if (logger.debugOn())
        logger.debug("BufferListener.handleNotification", "notif=" + param1Notification + "; handback=" + param1Object); 
      ObjectName objectName = (ObjectName)param1Object;
      ArrayNotificationBuffer.this.addNotification(new ArrayNotificationBuffer.NamedNotification(objectName, param1Notification));
    }
  }
  
  private static class NamedNotification {
    private final ObjectName sender;
    
    private final Notification notification;
    
    NamedNotification(ObjectName param1ObjectName, Notification param1Notification) {
      this.sender = param1ObjectName;
      this.notification = param1Notification;
    }
    
    ObjectName getObjectName() { return this.sender; }
    
    Notification getNotification() { return this.notification; }
    
    public String toString() { return "NamedNotification(" + this.sender + ", " + this.notification + ")"; }
  }
  
  private class ShareBuffer implements NotificationBuffer {
    private final int size;
    
    ShareBuffer(int param1Int) {
      this.size = param1Int;
      this$0.addSharer(this);
    }
    
    public NotificationResult fetchNotifications(NotificationBufferFilter param1NotificationBufferFilter, long param1Long1, long param1Long2, int param1Int) throws InterruptedException {
      ArrayNotificationBuffer arrayNotificationBuffer = ArrayNotificationBuffer.this;
      return arrayNotificationBuffer.fetchNotifications(param1NotificationBufferFilter, param1Long1, param1Long2, param1Int);
    }
    
    public void dispose() { ArrayNotificationBuffer.this.removeSharer(this); }
    
    int getSize() { return this.size; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\ArrayNotificationBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */