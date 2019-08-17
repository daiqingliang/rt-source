package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Introspector;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public abstract class Monitor extends NotificationBroadcasterSupport implements MonitorMBean, MBeanRegistration {
  private String observedAttribute;
  
  private long granularityPeriod = 10000L;
  
  private boolean isActive = false;
  
  private final AtomicLong sequenceNumber = new AtomicLong();
  
  private boolean isComplexTypeAttribute = false;
  
  private String firstAttribute;
  
  private final List<String> remainingAttributes = new CopyOnWriteArrayList();
  
  private static final AccessControlContext noPermissionsACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, null) });
  
  private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("Scheduler"));
  
  private static final Map<ThreadPoolExecutor, Void> executors = new WeakHashMap();
  
  private static final Object executorsLock = new Object();
  
  private static final int maximumPoolSize;
  
  private Future<?> monitorFuture;
  
  private final SchedulerTask schedulerTask = new SchedulerTask();
  
  private ScheduledFuture<?> schedulerFuture;
  
  protected static final int capacityIncrement = 16;
  
  protected int elementCount = 0;
  
  @Deprecated
  protected int alreadyNotified = 0;
  
  protected int[] alreadyNotifieds = new int[16];
  
  protected MBeanServer server;
  
  protected static final int RESET_FLAGS_ALREADY_NOTIFIED = 0;
  
  protected static final int OBSERVED_OBJECT_ERROR_NOTIFIED = 1;
  
  protected static final int OBSERVED_ATTRIBUTE_ERROR_NOTIFIED = 2;
  
  protected static final int OBSERVED_ATTRIBUTE_TYPE_ERROR_NOTIFIED = 4;
  
  protected static final int RUNTIME_ERROR_NOTIFIED = 8;
  
  @Deprecated
  protected String dbgTag = Monitor.class.getName();
  
  final List<ObservedObject> observedObjects = new CopyOnWriteArrayList();
  
  static final int THRESHOLD_ERROR_NOTIFIED = 16;
  
  static final Integer INTEGER_ZERO;
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preRegister(MBeanServer, ObjectName)", "initialize the reference on the MBean server");
    this.server = paramMBeanServer;
    return paramObjectName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister() {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "preDeregister()", "stop the monitor");
    stop();
  }
  
  public void postDeregister() {}
  
  public abstract void start();
  
  public abstract void stop();
  
  @Deprecated
  public ObjectName getObservedObject() { return this.observedObjects.isEmpty() ? null : ((ObservedObject)this.observedObjects.get(0)).getObservedObject(); }
  
  @Deprecated
  public void setObservedObject(ObjectName paramObjectName) throws IllegalArgumentException {
    if (paramObjectName == null)
      throw new IllegalArgumentException("Null observed object"); 
    if (this.observedObjects.size() == 1 && containsObservedObject(paramObjectName))
      return; 
    this.observedObjects.clear();
    addObservedObject(paramObjectName);
  }
  
  public void addObservedObject(ObjectName paramObjectName) throws IllegalArgumentException {
    if (paramObjectName == null)
      throw new IllegalArgumentException("Null observed object"); 
    if (containsObservedObject(paramObjectName))
      return; 
    ObservedObject observedObject = createObservedObject(paramObjectName);
    observedObject.setAlreadyNotified(0);
    observedObject.setDerivedGauge(INTEGER_ZERO);
    observedObject.setDerivedGaugeTimeStamp(System.currentTimeMillis());
    this.observedObjects.add(observedObject);
    createAlreadyNotified();
  }
  
  public void removeObservedObject(ObjectName paramObjectName) throws IllegalArgumentException {
    if (paramObjectName == null)
      return; 
    ObservedObject observedObject = getObservedObject(paramObjectName);
    if (observedObject != null) {
      this.observedObjects.remove(observedObject);
      createAlreadyNotified();
    } 
  }
  
  public boolean containsObservedObject(ObjectName paramObjectName) { return (getObservedObject(paramObjectName) != null); }
  
  public ObjectName[] getObservedObjects() {
    ObjectName[] arrayOfObjectName = new ObjectName[this.observedObjects.size()];
    for (byte b = 0; b < arrayOfObjectName.length; b++)
      arrayOfObjectName[b] = ((ObservedObject)this.observedObjects.get(b)).getObservedObject(); 
    return arrayOfObjectName;
  }
  
  public String getObservedAttribute() { return this.observedAttribute; }
  
  public void setObservedAttribute(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("Null observed attribute"); 
    synchronized (this) {
      if (this.observedAttribute != null && this.observedAttribute.equals(paramString))
        return; 
      this.observedAttribute = paramString;
      cleanupIsComplexTypeAttribute();
      byte b = 0;
      for (ObservedObject observedObject : this.observedObjects)
        resetAlreadyNotified(observedObject, b++, 6); 
    } 
  }
  
  public long getGranularityPeriod() { return this.granularityPeriod; }
  
  public void setGranularityPeriod(long paramLong) throws IllegalArgumentException {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("Nonpositive granularity period"); 
    if (this.granularityPeriod == paramLong)
      return; 
    this.granularityPeriod = paramLong;
    if (isActive()) {
      cleanupFutures();
      this.schedulerFuture = scheduler.schedule(this.schedulerTask, paramLong, TimeUnit.MILLISECONDS);
    } 
  }
  
  public boolean isActive() { return this.isActive; }
  
  void doStart() {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "start the monitor");
    synchronized (this) {
      if (isActive()) {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStart()", "the monitor is already active");
        return;
      } 
      this.isActive = true;
      cleanupIsComplexTypeAttribute();
      this.acc = AccessController.getContext();
      cleanupFutures();
      this.schedulerTask.setMonitorTask(new MonitorTask());
      this.schedulerFuture = scheduler.schedule(this.schedulerTask, getGranularityPeriod(), TimeUnit.MILLISECONDS);
    } 
  }
  
  void doStop() {
    JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "stop the monitor");
    synchronized (this) {
      if (!isActive()) {
        JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "doStop()", "the monitor is not active");
        return;
      } 
      this.isActive = false;
      cleanupFutures();
      this.acc = noPermissionsACC;
      cleanupIsComplexTypeAttribute();
    } 
  }
  
  Object getDerivedGauge(ObjectName paramObjectName) {
    ObservedObject observedObject = getObservedObject(paramObjectName);
    return (observedObject == null) ? null : observedObject.getDerivedGauge();
  }
  
  long getDerivedGaugeTimeStamp(ObjectName paramObjectName) {
    ObservedObject observedObject = getObservedObject(paramObjectName);
    return (observedObject == null) ? 0L : observedObject.getDerivedGaugeTimeStamp();
  }
  
  Object getAttribute(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, String paramString) throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, IOException {
    String str;
    Object object;
    boolean bool;
    synchronized (this) {
      if (!isActive())
        throw new IllegalArgumentException("The monitor has been stopped"); 
      if (!paramString.equals(getObservedAttribute()))
        throw new IllegalArgumentException("The observed attribute has been changed"); 
      bool = (this.firstAttribute == null && paramString.indexOf('.') != -1) ? 1 : 0;
    } 
    if (bool) {
      try {
        object = paramMBeanServerConnection.getMBeanInfo(paramObjectName);
      } catch (IntrospectionException null) {
        throw new IllegalArgumentException(str);
      } 
    } else {
      object = null;
    } 
    synchronized (this) {
      if (!isActive())
        throw new IllegalArgumentException("The monitor has been stopped"); 
      if (!paramString.equals(getObservedAttribute()))
        throw new IllegalArgumentException("The observed attribute has been changed"); 
      if (this.firstAttribute == null)
        if (paramString.indexOf('.') != -1) {
          MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = object.getAttributes();
          for (MBeanAttributeInfo mBeanAttributeInfo : arrayOfMBeanAttributeInfo) {
            if (paramString.equals(mBeanAttributeInfo.getName())) {
              this.firstAttribute = paramString;
              break;
            } 
          } 
          if (this.firstAttribute == null) {
            String[] arrayOfString = paramString.split("\\.", -1);
            this.firstAttribute = arrayOfString[0];
            for (byte b = 1; b < arrayOfString.length; b++)
              this.remainingAttributes.add(arrayOfString[b]); 
            this.isComplexTypeAttribute = true;
          } 
        } else {
          this.firstAttribute = paramString;
        }  
      str = this.firstAttribute;
    } 
    return paramMBeanServerConnection.getAttribute(paramObjectName, str);
  }
  
  Comparable<?> getComparableFromAttribute(ObjectName paramObjectName, String paramString, Object paramObject) throws AttributeNotFoundException {
    if (this.isComplexTypeAttribute) {
      Object object = paramObject;
      for (String str : this.remainingAttributes)
        object = Introspector.elementFromComplex(object, str); 
      return (Comparable)object;
    } 
    return (Comparable)paramObject;
  }
  
  boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) { return true; }
  
  String buildErrorNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) { return null; }
  
  void onErrorNotification(MonitorNotification paramMonitorNotification) {}
  
  Comparable<?> getDerivedGaugeFromComparable(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) { return paramComparable; }
  
  MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) { return null; }
  
  boolean isThresholdTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) { return true; }
  
  static Class<? extends Number> classForType(NumericalType paramNumericalType) {
    switch (paramNumericalType) {
      case BYTE:
        return Byte.class;
      case SHORT:
        return Short.class;
      case INTEGER:
        return Integer.class;
      case LONG:
        return Long.class;
      case FLOAT:
        return Float.class;
      case DOUBLE:
        return Double.class;
    } 
    throw new IllegalArgumentException("Unsupported numerical type");
  }
  
  static boolean isValidForType(Object paramObject, Class<? extends Number> paramClass) { return (paramObject == INTEGER_ZERO || paramClass.isInstance(paramObject)); }
  
  ObservedObject getObservedObject(ObjectName paramObjectName) {
    for (ObservedObject observedObject : this.observedObjects) {
      if (observedObject.getObservedObject().equals(paramObjectName))
        return observedObject; 
    } 
    return null;
  }
  
  ObservedObject createObservedObject(ObjectName paramObjectName) { return new ObservedObject(paramObjectName); }
  
  void createAlreadyNotified() {
    this.elementCount = this.observedObjects.size();
    this.alreadyNotifieds = new int[this.elementCount];
    for (byte b = 0; b < this.elementCount; b++)
      this.alreadyNotifieds[b] = ((ObservedObject)this.observedObjects.get(b)).getAlreadyNotified(); 
    updateDeprecatedAlreadyNotified();
  }
  
  void updateDeprecatedAlreadyNotified() {
    if (this.elementCount > 0) {
      this.alreadyNotified = this.alreadyNotifieds[0];
    } else {
      this.alreadyNotified = 0;
    } 
  }
  
  void updateAlreadyNotified(ObservedObject paramObservedObject, int paramInt) {
    this.alreadyNotifieds[paramInt] = paramObservedObject.getAlreadyNotified();
    if (paramInt == 0)
      updateDeprecatedAlreadyNotified(); 
  }
  
  boolean isAlreadyNotified(ObservedObject paramObservedObject, int paramInt) { return ((paramObservedObject.getAlreadyNotified() & paramInt) != 0); }
  
  void setAlreadyNotified(ObservedObject paramObservedObject, int paramInt1, int paramInt2, int[] paramArrayOfInt) {
    int i = computeAlreadyNotifiedIndex(paramObservedObject, paramInt1, paramArrayOfInt);
    if (i == -1)
      return; 
    paramObservedObject.setAlreadyNotified(paramObservedObject.getAlreadyNotified() | paramInt2);
    updateAlreadyNotified(paramObservedObject, i);
  }
  
  void resetAlreadyNotified(ObservedObject paramObservedObject, int paramInt1, int paramInt2) {
    paramObservedObject.setAlreadyNotified(paramObservedObject.getAlreadyNotified() & (paramInt2 ^ 0xFFFFFFFF));
    updateAlreadyNotified(paramObservedObject, paramInt1);
  }
  
  void resetAllAlreadyNotified(ObservedObject paramObservedObject, int paramInt, int[] paramArrayOfInt) {
    int i = computeAlreadyNotifiedIndex(paramObservedObject, paramInt, paramArrayOfInt);
    if (i == -1)
      return; 
    paramObservedObject.setAlreadyNotified(0);
    updateAlreadyNotified(paramObservedObject, paramInt);
  }
  
  int computeAlreadyNotifiedIndex(ObservedObject paramObservedObject, int paramInt, int[] paramArrayOfInt) { return (paramArrayOfInt == this.alreadyNotifieds) ? paramInt : this.observedObjects.indexOf(paramObservedObject); }
  
  private void sendNotification(String paramString1, long paramLong, String paramString2, Object paramObject1, Object paramObject2, ObjectName paramObjectName, boolean paramBoolean) {
    if (!isActive())
      return; 
    if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "sendNotification", "send notification: \n\tNotification observed object = " + paramObjectName + "\n\tNotification observed attribute = " + this.observedAttribute + "\n\tNotification derived gauge = " + paramObject1); 
    long l = this.sequenceNumber.getAndIncrement();
    MonitorNotification monitorNotification = new MonitorNotification(paramString1, this, l, paramLong, paramString2, paramObjectName, this.observedAttribute, paramObject1, paramObject2);
    if (paramBoolean)
      onErrorNotification(monitorNotification); 
    sendNotification(monitorNotification);
  }
  
  private void monitor(ObservedObject paramObservedObject, int paramInt, int[] paramArrayOfInt) {
    ObjectName objectName;
    String str1;
    String str2 = null;
    String str3 = null;
    Comparable comparable1 = null;
    Object object = null;
    Comparable comparable2 = null;
    MonitorNotification monitorNotification = null;
    if (!isActive())
      return; 
    synchronized (this) {
      objectName = paramObservedObject.getObservedObject();
      str1 = getObservedAttribute();
      if (objectName == null || str1 == null)
        return; 
    } 
    Object object1 = null;
    try {
      object1 = getAttribute(this.server, objectName, str1);
      if (object1 == null) {
        if (isAlreadyNotified(paramObservedObject, 4))
          return; 
        str2 = "jmx.monitor.error.type";
        setAlreadyNotified(paramObservedObject, paramInt, 4, paramArrayOfInt);
        str3 = "The observed attribute value is null.";
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      } 
    } catch (NullPointerException nullPointerException) {
      if (isAlreadyNotified(paramObservedObject, 8))
        return; 
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = "The monitor must be registered in the MBean server or an MBeanServerConnection must be explicitly supplied.";
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", nullPointerException.toString());
    } catch (InstanceNotFoundException instanceNotFoundException) {
      if (isAlreadyNotified(paramObservedObject, 1))
        return; 
      str2 = "jmx.monitor.error.mbean";
      setAlreadyNotified(paramObservedObject, paramInt, 1, paramArrayOfInt);
      str3 = "The observed object must be accessible in the MBeanServerConnection.";
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", instanceNotFoundException.toString());
    } catch (AttributeNotFoundException attributeNotFoundException) {
      if (isAlreadyNotified(paramObservedObject, 2))
        return; 
      str2 = "jmx.monitor.error.attribute";
      setAlreadyNotified(paramObservedObject, paramInt, 2, paramArrayOfInt);
      str3 = "The observed attribute must be accessible in the observed object.";
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", attributeNotFoundException.toString());
    } catch (MBeanException mBeanException) {
      if (isAlreadyNotified(paramObservedObject, 8))
        return; 
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = (mBeanException.getMessage() == null) ? "" : mBeanException.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", mBeanException.toString());
    } catch (ReflectionException reflectionException) {
      if (isAlreadyNotified(paramObservedObject, 8))
        return; 
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = (reflectionException.getMessage() == null) ? "" : reflectionException.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", reflectionException.toString());
    } catch (IOException iOException) {
      if (isAlreadyNotified(paramObservedObject, 8))
        return; 
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = (iOException.getMessage() == null) ? "" : iOException.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", iOException.toString());
    } catch (RuntimeException runtimeException) {
      if (isAlreadyNotified(paramObservedObject, 8))
        return; 
      str2 = "jmx.monitor.error.runtime";
      setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
      str3 = (runtimeException.getMessage() == null) ? "" : runtimeException.getMessage();
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", runtimeException.toString());
    } 
    synchronized (this) {
      if (!isActive())
        return; 
      if (!str1.equals(getObservedAttribute()))
        return; 
      if (str3 == null)
        try {
          comparable2 = getComparableFromAttribute(objectName, str1, object1);
        } catch (ClassCastException classCastException) {
          if (isAlreadyNotified(paramObservedObject, 4))
            return; 
          str2 = "jmx.monitor.error.type";
          setAlreadyNotified(paramObservedObject, paramInt, 4, paramArrayOfInt);
          str3 = "The observed attribute value does not implement the Comparable interface.";
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", classCastException.toString());
        } catch (AttributeNotFoundException attributeNotFoundException) {
          if (isAlreadyNotified(paramObservedObject, 2))
            return; 
          str2 = "jmx.monitor.error.attribute";
          setAlreadyNotified(paramObservedObject, paramInt, 2, paramArrayOfInt);
          str3 = "The observed attribute must be accessible in the observed object.";
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", attributeNotFoundException.toString());
        } catch (RuntimeException runtimeException) {
          if (isAlreadyNotified(paramObservedObject, 8))
            return; 
          str2 = "jmx.monitor.error.runtime";
          setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
          str3 = (runtimeException.getMessage() == null) ? "" : runtimeException.getMessage();
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", runtimeException.toString());
        }  
      if (str3 == null && !isComparableTypeValid(objectName, str1, comparable2)) {
        if (isAlreadyNotified(paramObservedObject, 4))
          return; 
        str2 = "jmx.monitor.error.type";
        setAlreadyNotified(paramObservedObject, paramInt, 4, paramArrayOfInt);
        str3 = "The observed attribute type is not valid.";
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      } 
      if (str3 == null && !isThresholdTypeValid(objectName, str1, comparable2)) {
        if (isAlreadyNotified(paramObservedObject, 16))
          return; 
        str2 = "jmx.monitor.error.threshold";
        setAlreadyNotified(paramObservedObject, paramInt, 16, paramArrayOfInt);
        str3 = "The threshold type is not valid.";
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
      } 
      if (str3 == null) {
        str3 = buildErrorNotification(objectName, str1, comparable2);
        if (str3 != null) {
          if (isAlreadyNotified(paramObservedObject, 8))
            return; 
          str2 = "jmx.monitor.error.runtime";
          setAlreadyNotified(paramObservedObject, paramInt, 8, paramArrayOfInt);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, Monitor.class.getName(), "monitor", str3);
        } 
      } 
      if (str3 == null) {
        resetAllAlreadyNotified(paramObservedObject, paramInt, paramArrayOfInt);
        comparable1 = getDerivedGaugeFromComparable(objectName, str1, comparable2);
        paramObservedObject.setDerivedGauge(comparable1);
        paramObservedObject.setDerivedGaugeTimeStamp(System.currentTimeMillis());
        monitorNotification = buildAlarmNotification(objectName, str1, (Comparable)comparable1);
      } 
    } 
    if (str3 != null)
      sendNotification(str2, System.currentTimeMillis(), str3, comparable1, object, objectName, true); 
    if (monitorNotification != null && monitorNotification.getType() != null)
      sendNotification(monitorNotification.getType(), System.currentTimeMillis(), monitorNotification.getMessage(), comparable1, monitorNotification.getTrigger(), objectName, false); 
  }
  
  private void cleanupFutures() {
    if (this.schedulerFuture != null) {
      this.schedulerFuture.cancel(false);
      this.schedulerFuture = null;
    } 
    if (this.monitorFuture != null) {
      this.monitorFuture.cancel(false);
      this.monitorFuture = null;
    } 
  }
  
  private void cleanupIsComplexTypeAttribute() {
    this.firstAttribute = null;
    this.remainingAttributes.clear();
    this.isComplexTypeAttribute = false;
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jmx.x.monitor.maximum.pool.size"));
    if (str == null || str.trim().length() == 0) {
      maximumPoolSize = 10;
    } else {
      int i = 10;
      try {
        i = Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {
        if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
          JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", "Wrong value for jmx.x.monitor.maximum.pool.size system property", numberFormatException);
          JmxProperties.MONITOR_LOGGER.logp(Level.FINER, Monitor.class.getName(), "<static initializer>", "jmx.x.monitor.maximum.pool.size defaults to 10");
        } 
        i = 10;
      } 
      if (i < 1) {
        maximumPoolSize = 1;
      } else {
        maximumPoolSize = i;
      } 
    } 
    INTEGER_ZERO = Integer.valueOf(0);
  }
  
  private static class DaemonThreadFactory implements ThreadFactory {
    final ThreadGroup group;
    
    final AtomicInteger threadNumber = new AtomicInteger(1);
    
    final String namePrefix;
    
    static final String nameSuffix = "]";
    
    public DaemonThreadFactory(String param1String) throws IllegalArgumentException {
      SecurityManager securityManager = System.getSecurityManager();
      this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = "JMX Monitor " + param1String + " Pool [Thread-";
    }
    
    public DaemonThreadFactory(String param1String, ThreadGroup param1ThreadGroup) {
      this.group = param1ThreadGroup;
      this.namePrefix = "JMX Monitor " + param1String + " Pool [Thread-";
    }
    
    public ThreadGroup getThreadGroup() { return this.group; }
    
    public Thread newThread(Runnable param1Runnable) {
      Thread thread = new Thread(this.group, param1Runnable, this.namePrefix + this.threadNumber.getAndIncrement() + "]", 0L);
      thread.setDaemon(true);
      if (thread.getPriority() != 5)
        thread.setPriority(5); 
      return thread;
    }
  }
  
  private class MonitorTask implements Runnable {
    private ThreadPoolExecutor executor;
    
    public MonitorTask() {
      SecurityManager securityManager = System.getSecurityManager();
      ThreadGroup threadGroup = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
      synchronized (executorsLock) {
        for (ThreadPoolExecutor threadPoolExecutor : executors.keySet()) {
          Monitor.DaemonThreadFactory daemonThreadFactory = (Monitor.DaemonThreadFactory)threadPoolExecutor.getThreadFactory();
          ThreadGroup threadGroup1 = daemonThreadFactory.getThreadGroup();
          if (threadGroup1 == threadGroup) {
            this.executor = threadPoolExecutor;
            break;
          } 
        } 
        if (this.executor == null) {
          this.executor = new ThreadPoolExecutor(maximumPoolSize, maximumPoolSize, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(), new Monitor.DaemonThreadFactory("ThreadGroup<" + threadGroup.getName() + "> Executor", threadGroup));
          this.executor.allowCoreThreadTimeOut(true);
          executors.put(this.executor, null);
        } 
      } 
    }
    
    public Future<?> submit() { return this.executor.submit(this); }
    
    public void run() {
      AccessControlContext accessControlContext;
      ScheduledFuture scheduledFuture;
      synchronized (Monitor.this) {
        scheduledFuture = Monitor.this.schedulerFuture;
        accessControlContext = Monitor.this.acc;
      } 
      PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
          public Void run() {
            if (Monitor.MonitorTask.this.this$0.isActive()) {
              int[] arrayOfInt = Monitor.this.alreadyNotifieds;
              byte b = 0;
              for (Monitor.ObservedObject observedObject : Monitor.this.observedObjects) {
                if (Monitor.MonitorTask.this.this$0.isActive())
                  Monitor.MonitorTask.this.this$0.monitor(observedObject, b++, arrayOfInt); 
              } 
            } 
            return null;
          }
        };
      if (accessControlContext == null)
        throw new SecurityException("AccessControlContext cannot be null"); 
      AccessController.doPrivileged(privilegedAction, accessControlContext);
      synchronized (Monitor.this) {
        if (Monitor.this.isActive() && Monitor.this.schedulerFuture == scheduledFuture) {
          Monitor.this.monitorFuture = null;
          Monitor.this.schedulerFuture = scheduler.schedule(Monitor.this.schedulerTask, Monitor.this.getGranularityPeriod(), TimeUnit.MILLISECONDS);
        } 
      } 
    }
  }
  
  enum NumericalType {
    BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE;
  }
  
  static class ObservedObject {
    private final ObjectName observedObject;
    
    private int alreadyNotified;
    
    private Object derivedGauge;
    
    private long derivedGaugeTimeStamp;
    
    public ObservedObject(ObjectName param1ObjectName) throws IllegalArgumentException { this.observedObject = param1ObjectName; }
    
    public final ObjectName getObservedObject() { return this.observedObject; }
    
    public final int getAlreadyNotified() { return this.alreadyNotified; }
    
    public final void setAlreadyNotified(int param1Int) { this.alreadyNotified = param1Int; }
    
    public final Object getDerivedGauge() { return this.derivedGauge; }
    
    public final void setDerivedGauge(Object param1Object) { this.derivedGauge = param1Object; }
    
    public final long getDerivedGaugeTimeStamp() { return this.derivedGaugeTimeStamp; }
    
    public final void setDerivedGaugeTimeStamp(long param1Long) throws IllegalArgumentException { this.derivedGaugeTimeStamp = param1Long; }
  }
  
  private class SchedulerTask implements Runnable {
    private Monitor.MonitorTask task;
    
    public void setMonitorTask(Monitor.MonitorTask param1MonitorTask) { this.task = param1MonitorTask; }
    
    public void run() {
      synchronized (Monitor.this) {
        Monitor.this.monitorFuture = this.task.submit();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\Monitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */