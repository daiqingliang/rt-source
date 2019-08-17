package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class GaugeMonitor extends Monitor implements GaugeMonitorMBean {
  private Number highThreshold = INTEGER_ZERO;
  
  private Number lowThreshold = INTEGER_ZERO;
  
  private boolean notifyHigh = false;
  
  private boolean notifyLow = false;
  
  private boolean differenceMode = false;
  
  private static final String[] types = { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.error.threshold", "jmx.monitor.gauge.high", "jmx.monitor.gauge.low" };
  
  private static final MBeanNotificationInfo[] notifsInfo = { new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the GaugeMonitor MBean") };
  
  private static final int RISING = 0;
  
  private static final int FALLING = 1;
  
  private static final int RISING_OR_FALLING = 2;
  
  public void start() {
    if (isActive()) {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, GaugeMonitor.class.getName(), "start", "the monitor is already active");
      return;
    } 
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)observedObject;
      gaugeMonitorObservedObject.setStatus(2);
      gaugeMonitorObservedObject.setPreviousScanGauge(null);
    } 
    doStart();
  }
  
  public void stop() { doStop(); }
  
  public Number getDerivedGauge(ObjectName paramObjectName) { return (Number)super.getDerivedGauge(paramObjectName); }
  
  public long getDerivedGaugeTimeStamp(ObjectName paramObjectName) { return super.getDerivedGaugeTimeStamp(paramObjectName); }
  
  @Deprecated
  public Number getDerivedGauge() { return this.observedObjects.isEmpty() ? null : (Number)((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGauge(); }
  
  @Deprecated
  public long getDerivedGaugeTimeStamp() { return this.observedObjects.isEmpty() ? 0L : ((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGaugeTimeStamp(); }
  
  public Number getHighThreshold() { return this.highThreshold; }
  
  public Number getLowThreshold() { return this.lowThreshold; }
  
  public void setThresholds(Number paramNumber1, Number paramNumber2) throws IllegalArgumentException {
    if (paramNumber1 == null || paramNumber2 == null)
      throw new IllegalArgumentException("Null threshold value"); 
    if (paramNumber1.getClass() != paramNumber2.getClass())
      throw new IllegalArgumentException("Different type threshold values"); 
    if (isFirstStrictlyGreaterThanLast(paramNumber2, paramNumber1, paramNumber1.getClass().getName()))
      throw new IllegalArgumentException("High threshold less than low threshold"); 
    if (this.highThreshold.equals(paramNumber1) && this.lowThreshold.equals(paramNumber2))
      return; 
    this.highThreshold = paramNumber1;
    this.lowThreshold = paramNumber2;
    byte b = 0;
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      resetAlreadyNotified(observedObject, b++, 16);
      GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)observedObject;
      gaugeMonitorObservedObject.setStatus(2);
    } 
  }
  
  public boolean getNotifyHigh() { return this.notifyHigh; }
  
  public void setNotifyHigh(boolean paramBoolean) {
    if (this.notifyHigh == paramBoolean)
      return; 
    this.notifyHigh = paramBoolean;
  }
  
  public boolean getNotifyLow() { return this.notifyLow; }
  
  public void setNotifyLow(boolean paramBoolean) {
    if (this.notifyLow == paramBoolean)
      return; 
    this.notifyLow = paramBoolean;
  }
  
  public boolean getDifferenceMode() { return this.differenceMode; }
  
  public void setDifferenceMode(boolean paramBoolean) {
    if (this.differenceMode == paramBoolean)
      return; 
    this.differenceMode = paramBoolean;
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)observedObject;
      gaugeMonitorObservedObject.setStatus(2);
      gaugeMonitorObservedObject.setPreviousScanGauge(null);
    } 
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() { return (MBeanNotificationInfo[])notifsInfo.clone(); }
  
  private boolean updateDerivedGauge(Object paramObject, GaugeMonitorObservedObject paramGaugeMonitorObservedObject) {
    boolean bool;
    if (this.differenceMode) {
      if (paramGaugeMonitorObservedObject.getPreviousScanGauge() != null) {
        setDerivedGaugeWithDifference((Number)paramObject, paramGaugeMonitorObservedObject);
        bool = true;
      } else {
        bool = false;
      } 
      paramGaugeMonitorObservedObject.setPreviousScanGauge((Number)paramObject);
    } else {
      paramGaugeMonitorObservedObject.setDerivedGauge((Number)paramObject);
      bool = true;
    } 
    return bool;
  }
  
  private MonitorNotification updateNotifications(GaugeMonitorObservedObject paramGaugeMonitorObservedObject) {
    MonitorNotification monitorNotification = null;
    if (paramGaugeMonitorObservedObject.getStatus() == 2) {
      if (isFirstGreaterThanLast((Number)paramGaugeMonitorObservedObject.getDerivedGauge(), this.highThreshold, paramGaugeMonitorObservedObject.getType())) {
        if (this.notifyHigh)
          monitorNotification = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", null, null, null, this.highThreshold); 
        paramGaugeMonitorObservedObject.setStatus(1);
      } else if (isFirstGreaterThanLast(this.lowThreshold, (Number)paramGaugeMonitorObservedObject.getDerivedGauge(), paramGaugeMonitorObservedObject.getType())) {
        if (this.notifyLow)
          monitorNotification = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", null, null, null, this.lowThreshold); 
        paramGaugeMonitorObservedObject.setStatus(0);
      } 
    } else if (paramGaugeMonitorObservedObject.getStatus() == 0) {
      if (isFirstGreaterThanLast((Number)paramGaugeMonitorObservedObject.getDerivedGauge(), this.highThreshold, paramGaugeMonitorObservedObject.getType())) {
        if (this.notifyHigh)
          monitorNotification = new MonitorNotification("jmx.monitor.gauge.high", this, 0L, 0L, "", null, null, null, this.highThreshold); 
        paramGaugeMonitorObservedObject.setStatus(1);
      } 
    } else if (paramGaugeMonitorObservedObject.getStatus() == 1 && isFirstGreaterThanLast(this.lowThreshold, (Number)paramGaugeMonitorObservedObject.getDerivedGauge(), paramGaugeMonitorObservedObject.getType())) {
      if (this.notifyLow)
        monitorNotification = new MonitorNotification("jmx.monitor.gauge.low", this, 0L, 0L, "", null, null, null, this.lowThreshold); 
      paramGaugeMonitorObservedObject.setStatus(0);
    } 
    return monitorNotification;
  }
  
  private void setDerivedGaugeWithDifference(Number paramNumber, GaugeMonitorObservedObject paramGaugeMonitorObservedObject) {
    Double double;
    Float float;
    Long long;
    Integer integer;
    Byte byte;
    Short short;
    Number number = paramGaugeMonitorObservedObject.getPreviousScanGauge();
    switch (paramGaugeMonitorObservedObject.getType()) {
      case INTEGER:
        integer = Integer.valueOf(((Integer)paramNumber).intValue() - ((Integer)number).intValue());
        break;
      case BYTE:
        byte = Byte.valueOf((byte)(((Byte)paramNumber).byteValue() - ((Byte)number).byteValue()));
        break;
      case SHORT:
        short = Short.valueOf((short)(((Short)paramNumber).shortValue() - ((Short)number).shortValue()));
        break;
      case LONG:
        long = Long.valueOf(((Long)paramNumber).longValue() - ((Long)number).longValue());
        break;
      case FLOAT:
        float = Float.valueOf(((Float)paramNumber).floatValue() - ((Float)number).floatValue());
        break;
      case DOUBLE:
        double = Double.valueOf(((Double)paramNumber).doubleValue() - ((Double)number).doubleValue());
        break;
      default:
        JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "setDerivedGaugeWithDifference", "the threshold type is invalid");
        return;
    } 
    paramGaugeMonitorObservedObject.setDerivedGauge(double);
  }
  
  private boolean isFirstGreaterThanLast(Number paramNumber1, Number paramNumber2, Monitor.NumericalType paramNumericalType) {
    switch (paramNumericalType) {
      case INTEGER:
      case BYTE:
      case SHORT:
      case LONG:
        return (paramNumber1.longValue() >= paramNumber2.longValue());
      case FLOAT:
      case DOUBLE:
        return (paramNumber1.doubleValue() >= paramNumber2.doubleValue());
    } 
    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstGreaterThanLast", "the threshold type is invalid");
    return false;
  }
  
  private boolean isFirstStrictlyGreaterThanLast(Number paramNumber1, Number paramNumber2, String paramString) {
    if (paramString.equals("java.lang.Integer") || paramString.equals("java.lang.Byte") || paramString.equals("java.lang.Short") || paramString.equals("java.lang.Long"))
      return (paramNumber1.longValue() > paramNumber2.longValue()); 
    if (paramString.equals("java.lang.Float") || paramString.equals("java.lang.Double"))
      return (paramNumber1.doubleValue() > paramNumber2.doubleValue()); 
    JmxProperties.MONITOR_LOGGER.logp(Level.FINEST, GaugeMonitor.class.getName(), "isFirstStrictlyGreaterThanLast", "the threshold type is invalid");
    return false;
  }
  
  Monitor.ObservedObject createObservedObject(ObjectName paramObjectName) {
    GaugeMonitorObservedObject gaugeMonitorObservedObject = new GaugeMonitorObservedObject(paramObjectName);
    gaugeMonitorObservedObject.setStatus(2);
    gaugeMonitorObservedObject.setPreviousScanGauge(null);
    return gaugeMonitorObservedObject;
  }
  
  boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (gaugeMonitorObservedObject == null)
      return false; 
    if (paramComparable instanceof Integer) {
      gaugeMonitorObservedObject.setType(Monitor.NumericalType.INTEGER);
    } else if (paramComparable instanceof Byte) {
      gaugeMonitorObservedObject.setType(Monitor.NumericalType.BYTE);
    } else if (paramComparable instanceof Short) {
      gaugeMonitorObservedObject.setType(Monitor.NumericalType.SHORT);
    } else if (paramComparable instanceof Long) {
      gaugeMonitorObservedObject.setType(Monitor.NumericalType.LONG);
    } else if (paramComparable instanceof Float) {
      gaugeMonitorObservedObject.setType(Monitor.NumericalType.FLOAT);
    } else if (paramComparable instanceof Double) {
      gaugeMonitorObservedObject.setType(Monitor.NumericalType.DOUBLE);
    } else {
      return false;
    } 
    return true;
  }
  
  Comparable<?> getDerivedGaugeFromComparable(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (gaugeMonitorObservedObject == null)
      return null; 
    gaugeMonitorObservedObject.setDerivedGaugeValid(updateDerivedGauge(paramComparable, gaugeMonitorObservedObject));
    return (Comparable)gaugeMonitorObservedObject.getDerivedGauge();
  }
  
  void onErrorNotification(MonitorNotification paramMonitorNotification) {
    GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramMonitorNotification.getObservedObject());
    if (gaugeMonitorObservedObject == null)
      return; 
    gaugeMonitorObservedObject.setStatus(2);
    gaugeMonitorObservedObject.setPreviousScanGauge(null);
  }
  
  MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    MonitorNotification monitorNotification;
    GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (gaugeMonitorObservedObject == null)
      return null; 
    if (gaugeMonitorObservedObject.getDerivedGaugeValid()) {
      monitorNotification = updateNotifications(gaugeMonitorObservedObject);
    } else {
      monitorNotification = null;
    } 
    return monitorNotification;
  }
  
  boolean isThresholdTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    GaugeMonitorObservedObject gaugeMonitorObservedObject = (GaugeMonitorObservedObject)getObservedObject(paramObjectName);
    if (gaugeMonitorObservedObject == null)
      return false; 
    Class clazz = classForType(gaugeMonitorObservedObject.getType());
    return (isValidForType(this.highThreshold, clazz) && isValidForType(this.lowThreshold, clazz));
  }
  
  static class GaugeMonitorObservedObject extends Monitor.ObservedObject {
    private boolean derivedGaugeValid;
    
    private Monitor.NumericalType type;
    
    private Number previousScanGauge;
    
    private int status;
    
    public GaugeMonitorObservedObject(ObjectName param1ObjectName) { super(param1ObjectName); }
    
    public final boolean getDerivedGaugeValid() { return this.derivedGaugeValid; }
    
    public final void setDerivedGaugeValid(boolean param1Boolean) { this.derivedGaugeValid = param1Boolean; }
    
    public final Monitor.NumericalType getType() { return this.type; }
    
    public final void setType(Monitor.NumericalType param1NumericalType) { this.type = param1NumericalType; }
    
    public final Number getPreviousScanGauge() { return this.previousScanGauge; }
    
    public final void setPreviousScanGauge(Number param1Number) { this.previousScanGauge = param1Number; }
    
    public final int getStatus() { return this.status; }
    
    public final void setStatus(int param1Int) { this.status = param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\GaugeMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */