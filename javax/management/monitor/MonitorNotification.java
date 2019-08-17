package javax.management.monitor;

import javax.management.Notification;
import javax.management.ObjectName;

public class MonitorNotification extends Notification {
  public static final String OBSERVED_OBJECT_ERROR = "jmx.monitor.error.mbean";
  
  public static final String OBSERVED_ATTRIBUTE_ERROR = "jmx.monitor.error.attribute";
  
  public static final String OBSERVED_ATTRIBUTE_TYPE_ERROR = "jmx.monitor.error.type";
  
  public static final String THRESHOLD_ERROR = "jmx.monitor.error.threshold";
  
  public static final String RUNTIME_ERROR = "jmx.monitor.error.runtime";
  
  public static final String THRESHOLD_VALUE_EXCEEDED = "jmx.monitor.counter.threshold";
  
  public static final String THRESHOLD_HIGH_VALUE_EXCEEDED = "jmx.monitor.gauge.high";
  
  public static final String THRESHOLD_LOW_VALUE_EXCEEDED = "jmx.monitor.gauge.low";
  
  public static final String STRING_TO_COMPARE_VALUE_MATCHED = "jmx.monitor.string.matches";
  
  public static final String STRING_TO_COMPARE_VALUE_DIFFERED = "jmx.monitor.string.differs";
  
  private static final long serialVersionUID = -4608189663661929204L;
  
  private ObjectName observedObject = null;
  
  private String observedAttribute = null;
  
  private Object derivedGauge = null;
  
  private Object trigger = null;
  
  MonitorNotification(String paramString1, Object paramObject1, long paramLong1, long paramLong2, String paramString2, ObjectName paramObjectName, String paramString3, Object paramObject2, Object paramObject3) {
    super(paramString1, paramObject1, paramLong1, paramLong2, paramString2);
    this.observedObject = paramObjectName;
    this.observedAttribute = paramString3;
    this.derivedGauge = paramObject2;
    this.trigger = paramObject3;
  }
  
  public ObjectName getObservedObject() { return this.observedObject; }
  
  public String getObservedAttribute() { return this.observedAttribute; }
  
  public Object getDerivedGauge() { return this.derivedGauge; }
  
  public Object getTrigger() { return this.trigger; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\MonitorNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */