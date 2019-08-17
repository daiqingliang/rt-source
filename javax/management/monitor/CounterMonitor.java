package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class CounterMonitor extends Monitor implements CounterMonitorMBean {
  private Number modulus = INTEGER_ZERO;
  
  private Number offset = INTEGER_ZERO;
  
  private boolean notify = false;
  
  private boolean differenceMode = false;
  
  private Number initThreshold = INTEGER_ZERO;
  
  private static final String[] types = { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.counter.threshold" };
  
  private static final MBeanNotificationInfo[] notifsInfo = { new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the CounterMonitor MBean") };
  
  public void start() {
    if (isActive()) {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "start", "the monitor is already active");
      return;
    } 
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)observedObject;
      counterMonitorObservedObject.setThreshold(this.initThreshold);
      counterMonitorObservedObject.setModulusExceeded(false);
      counterMonitorObservedObject.setEventAlreadyNotified(false);
      counterMonitorObservedObject.setPreviousScanCounter(null);
    } 
    doStart();
  }
  
  public void stop() { doStop(); }
  
  public Number getDerivedGauge(ObjectName paramObjectName) { return (Number)super.getDerivedGauge(paramObjectName); }
  
  public long getDerivedGaugeTimeStamp(ObjectName paramObjectName) { return super.getDerivedGaugeTimeStamp(paramObjectName); }
  
  public Number getThreshold(ObjectName paramObjectName) {
    CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    return (counterMonitorObservedObject == null) ? null : ((this.offset.longValue() > 0L && this.modulus.longValue() > 0L && counterMonitorObservedObject.getThreshold().longValue() > this.modulus.longValue()) ? this.initThreshold : counterMonitorObservedObject.getThreshold());
  }
  
  public Number getInitThreshold() { return this.initThreshold; }
  
  public void setInitThreshold(Number paramNumber) throws IllegalArgumentException {
    if (paramNumber == null)
      throw new IllegalArgumentException("Null threshold"); 
    if (paramNumber.longValue() < 0L)
      throw new IllegalArgumentException("Negative threshold"); 
    if (this.initThreshold.equals(paramNumber))
      return; 
    this.initThreshold = paramNumber;
    byte b = 0;
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      resetAlreadyNotified(observedObject, b++, 16);
      CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)observedObject;
      counterMonitorObservedObject.setThreshold(paramNumber);
      counterMonitorObservedObject.setModulusExceeded(false);
      counterMonitorObservedObject.setEventAlreadyNotified(false);
    } 
  }
  
  @Deprecated
  public Number getDerivedGauge() { return this.observedObjects.isEmpty() ? null : (Number)((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGauge(); }
  
  @Deprecated
  public long getDerivedGaugeTimeStamp() { return this.observedObjects.isEmpty() ? 0L : ((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGaugeTimeStamp(); }
  
  @Deprecated
  public Number getThreshold() { return getThreshold(getObservedObject()); }
  
  @Deprecated
  public void setThreshold(Number paramNumber) throws IllegalArgumentException { setInitThreshold(paramNumber); }
  
  public Number getOffset() { return this.offset; }
  
  public void setOffset(Number paramNumber) throws IllegalArgumentException {
    if (paramNumber == null)
      throw new IllegalArgumentException("Null offset"); 
    if (paramNumber.longValue() < 0L)
      throw new IllegalArgumentException("Negative offset"); 
    if (this.offset.equals(paramNumber))
      return; 
    this.offset = paramNumber;
    byte b = 0;
    for (Monitor.ObservedObject observedObject : this.observedObjects)
      resetAlreadyNotified(observedObject, b++, 16); 
  }
  
  public Number getModulus() { return this.modulus; }
  
  public void setModulus(Number paramNumber) throws IllegalArgumentException {
    if (paramNumber == null)
      throw new IllegalArgumentException("Null modulus"); 
    if (paramNumber.longValue() < 0L)
      throw new IllegalArgumentException("Negative modulus"); 
    if (this.modulus.equals(paramNumber))
      return; 
    this.modulus = paramNumber;
    byte b = 0;
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      resetAlreadyNotified(observedObject, b++, 16);
      CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)observedObject;
      counterMonitorObservedObject.setModulusExceeded(false);
    } 
  }
  
  public boolean getNotify() { return this.notify; }
  
  public void setNotify(boolean paramBoolean) {
    if (this.notify == paramBoolean)
      return; 
    this.notify = paramBoolean;
  }
  
  public boolean getDifferenceMode() { return this.differenceMode; }
  
  public void setDifferenceMode(boolean paramBoolean) {
    if (this.differenceMode == paramBoolean)
      return; 
    this.differenceMode = paramBoolean;
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)observedObject;
      counterMonitorObservedObject.setThreshold(this.initThreshold);
      counterMonitorObservedObject.setModulusExceeded(false);
      counterMonitorObservedObject.setEventAlreadyNotified(false);
      counterMonitorObservedObject.setPreviousScanCounter(null);
    } 
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() { return (MBeanNotificationInfo[])notifsInfo.clone(); }
  
  private boolean updateDerivedGauge(Object paramObject, CounterMonitorObservedObject paramCounterMonitorObservedObject) {
    boolean bool;
    if (this.differenceMode) {
      if (paramCounterMonitorObservedObject.getPreviousScanCounter() != null) {
        setDerivedGaugeWithDifference((Number)paramObject, null, paramCounterMonitorObservedObject);
        if (((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() < 0L) {
          if (this.modulus.longValue() > 0L)
            setDerivedGaugeWithDifference((Number)paramObject, this.modulus, paramCounterMonitorObservedObject); 
          paramCounterMonitorObservedObject.setThreshold(this.initThreshold);
          paramCounterMonitorObservedObject.setEventAlreadyNotified(false);
        } 
        bool = true;
      } else {
        bool = false;
      } 
      paramCounterMonitorObservedObject.setPreviousScanCounter((Number)paramObject);
    } else {
      paramCounterMonitorObservedObject.setDerivedGauge((Number)paramObject);
      bool = true;
    } 
    return bool;
  }
  
  private MonitorNotification updateNotifications(CounterMonitorObservedObject paramCounterMonitorObservedObject) {
    MonitorNotification monitorNotification = null;
    if (!paramCounterMonitorObservedObject.getEventAlreadyNotified()) {
      if (((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() >= paramCounterMonitorObservedObject.getThreshold().longValue()) {
        if (this.notify)
          monitorNotification = new MonitorNotification("jmx.monitor.counter.threshold", this, 0L, 0L, "", null, null, null, paramCounterMonitorObservedObject.getThreshold()); 
        if (!this.differenceMode)
          paramCounterMonitorObservedObject.setEventAlreadyNotified(true); 
      } 
    } else if (JmxProperties.MONITOR_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("The notification:").append("\n\tNotification observed object = ").append(paramCounterMonitorObservedObject.getObservedObject()).append("\n\tNotification observed attribute = ").append(getObservedAttribute()).append("\n\tNotification threshold level = ").append(paramCounterMonitorObservedObject.getThreshold()).append("\n\tNotification derived gauge = ").append(paramCounterMonitorObservedObject.getDerivedGauge()).append("\nhas already been sent");
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, CounterMonitor.class.getName(), "updateNotifications", stringBuilder.toString());
    } 
    return monitorNotification;
  }
  
  private void updateThreshold(CounterMonitorObservedObject paramCounterMonitorObservedObject) {
    if (((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() >= paramCounterMonitorObservedObject.getThreshold().longValue())
      if (this.offset.longValue() > 0L) {
        long l;
        for (l = paramCounterMonitorObservedObject.getThreshold().longValue(); ((Number)paramCounterMonitorObservedObject.getDerivedGauge()).longValue() >= l; l += this.offset.longValue());
        switch (paramCounterMonitorObservedObject.getType()) {
          case INTEGER:
            paramCounterMonitorObservedObject.setThreshold(Integer.valueOf((int)l));
            break;
          case BYTE:
            paramCounterMonitorObservedObject.setThreshold(Byte.valueOf((byte)(int)l));
            break;
          case SHORT:
            paramCounterMonitorObservedObject.setThreshold(Short.valueOf((short)(int)l));
            break;
          case LONG:
            paramCounterMonitorObservedObject.setThreshold(Long.valueOf(l));
            break;
          default:
            JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "updateThreshold", "the threshold type is invalid");
            break;
        } 
        if (!this.differenceMode && this.modulus.longValue() > 0L && paramCounterMonitorObservedObject.getThreshold().longValue() > this.modulus.longValue()) {
          paramCounterMonitorObservedObject.setModulusExceeded(true);
          paramCounterMonitorObservedObject.setDerivedGaugeExceeded((Number)paramCounterMonitorObservedObject.getDerivedGauge());
        } 
        paramCounterMonitorObservedObject.setEventAlreadyNotified(false);
      } else {
        paramCounterMonitorObservedObject.setModulusExceeded(true);
        paramCounterMonitorObservedObject.setDerivedGaugeExceeded((Number)paramCounterMonitorObservedObject.getDerivedGauge());
      }  
  }
  
  private void setDerivedGaugeWithDifference(Number paramNumber1, Number paramNumber2, CounterMonitorObservedObject paramCounterMonitorObservedObject) {
    long l = paramNumber1.longValue() - paramCounterMonitorObservedObject.getPreviousScanCounter().longValue();
    if (paramNumber2 != null)
      l += this.modulus.longValue(); 
    switch (paramCounterMonitorObservedObject.getType()) {
      case INTEGER:
        paramCounterMonitorObservedObject.setDerivedGauge(Integer.valueOf((int)l));
        return;
      case BYTE:
        paramCounterMonitorObservedObject.setDerivedGauge(Byte.valueOf((byte)(int)l));
        return;
      case SHORT:
        paramCounterMonitorObservedObject.setDerivedGauge(Short.valueOf((short)(int)l));
        return;
      case LONG:
        paramCounterMonitorObservedObject.setDerivedGauge(Long.valueOf(l));
        return;
    } 
    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, CounterMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
  }
  
  Monitor.ObservedObject createObservedObject(ObjectName paramObjectName) {
    CounterMonitorObservedObject counterMonitorObservedObject = new CounterMonitorObservedObject(paramObjectName);
    counterMonitorObservedObject.setThreshold(this.initThreshold);
    counterMonitorObservedObject.setModulusExceeded(false);
    counterMonitorObservedObject.setEventAlreadyNotified(false);
    counterMonitorObservedObject.setPreviousScanCounter(null);
    return counterMonitorObservedObject;
  }
  
  boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (counterMonitorObservedObject == null)
      return false; 
    if (paramComparable instanceof Integer) {
      counterMonitorObservedObject.setType(Monitor.NumericalType.INTEGER);
    } else if (paramComparable instanceof Byte) {
      counterMonitorObservedObject.setType(Monitor.NumericalType.BYTE);
    } else if (paramComparable instanceof Short) {
      counterMonitorObservedObject.setType(Monitor.NumericalType.SHORT);
    } else if (paramComparable instanceof Long) {
      counterMonitorObservedObject.setType(Monitor.NumericalType.LONG);
    } else {
      return false;
    } 
    return true;
  }
  
  Comparable<?> getDerivedGaugeFromComparable(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (counterMonitorObservedObject == null)
      return null; 
    if (counterMonitorObservedObject.getModulusExceeded() && ((Number)counterMonitorObservedObject.getDerivedGauge()).longValue() < counterMonitorObservedObject.getDerivedGaugeExceeded().longValue()) {
      counterMonitorObservedObject.setThreshold(this.initThreshold);
      counterMonitorObservedObject.setModulusExceeded(false);
      counterMonitorObservedObject.setEventAlreadyNotified(false);
    } 
    counterMonitorObservedObject.setDerivedGaugeValid(updateDerivedGauge(paramComparable, counterMonitorObservedObject));
    return (Comparable)counterMonitorObservedObject.getDerivedGauge();
  }
  
  void onErrorNotification(MonitorNotification paramMonitorNotification) {
    CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramMonitorNotification.getObservedObject());
    if (counterMonitorObservedObject == null)
      return; 
    counterMonitorObservedObject.setModulusExceeded(false);
    counterMonitorObservedObject.setEventAlreadyNotified(false);
    counterMonitorObservedObject.setPreviousScanCounter(null);
  }
  
  MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    MonitorNotification monitorNotification;
    CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (counterMonitorObservedObject == null)
      return null; 
    if (counterMonitorObservedObject.getDerivedGaugeValid()) {
      monitorNotification = updateNotifications(counterMonitorObservedObject);
      updateThreshold(counterMonitorObservedObject);
    } else {
      monitorNotification = null;
    } 
    return monitorNotification;
  }
  
  boolean isThresholdTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    CounterMonitorObservedObject counterMonitorObservedObject = (CounterMonitorObservedObject)getObservedObject(paramObjectName);
    if (counterMonitorObservedObject == null)
      return false; 
    Class clazz = classForType(counterMonitorObservedObject.getType());
    return (clazz.isInstance(counterMonitorObservedObject.getThreshold()) && isValidForType(this.offset, clazz) && isValidForType(this.modulus, clazz));
  }
  
  static class CounterMonitorObservedObject extends Monitor.ObservedObject {
    private Number threshold;
    
    private Number previousScanCounter;
    
    private boolean modulusExceeded;
    
    private Number derivedGaugeExceeded;
    
    private boolean derivedGaugeValid;
    
    private boolean eventAlreadyNotified;
    
    private Monitor.NumericalType type;
    
    public CounterMonitorObservedObject(ObjectName param1ObjectName) { super(param1ObjectName); }
    
    public final Number getThreshold() { return this.threshold; }
    
    public final void setThreshold(Number param1Number) throws IllegalArgumentException { this.threshold = param1Number; }
    
    public final Number getPreviousScanCounter() { return this.previousScanCounter; }
    
    public final void setPreviousScanCounter(Number param1Number) throws IllegalArgumentException { this.previousScanCounter = param1Number; }
    
    public final boolean getModulusExceeded() { return this.modulusExceeded; }
    
    public final void setModulusExceeded(boolean param1Boolean) { this.modulusExceeded = param1Boolean; }
    
    public final Number getDerivedGaugeExceeded() { return this.derivedGaugeExceeded; }
    
    public final void setDerivedGaugeExceeded(Number param1Number) throws IllegalArgumentException { this.derivedGaugeExceeded = param1Number; }
    
    public final boolean getDerivedGaugeValid() { return this.derivedGaugeValid; }
    
    public final void setDerivedGaugeValid(boolean param1Boolean) { this.derivedGaugeValid = param1Boolean; }
    
    public final boolean getEventAlreadyNotified() { return this.eventAlreadyNotified; }
    
    public final void setEventAlreadyNotified(boolean param1Boolean) { this.eventAlreadyNotified = param1Boolean; }
    
    public final Monitor.NumericalType getType() { return this.type; }
    
    public final void setType(Monitor.NumericalType param1NumericalType) { this.type = param1NumericalType; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\CounterMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */