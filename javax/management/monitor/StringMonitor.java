package javax.management.monitor;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

public class StringMonitor extends Monitor implements StringMonitorMBean {
  private String stringToCompare = "";
  
  private boolean notifyMatch = false;
  
  private boolean notifyDiffer = false;
  
  private static final String[] types = { "jmx.monitor.error.runtime", "jmx.monitor.error.mbean", "jmx.monitor.error.attribute", "jmx.monitor.error.type", "jmx.monitor.string.matches", "jmx.monitor.string.differs" };
  
  private static final MBeanNotificationInfo[] notifsInfo = { new MBeanNotificationInfo(types, "javax.management.monitor.MonitorNotification", "Notifications sent by the StringMonitor MBean") };
  
  private static final int MATCHING = 0;
  
  private static final int DIFFERING = 1;
  
  private static final int MATCHING_OR_DIFFERING = 2;
  
  public void start() {
    if (isActive()) {
      JmxProperties.MONITOR_LOGGER.logp(Level.FINER, StringMonitor.class.getName(), "start", "the monitor is already active");
      return;
    } 
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      StringMonitorObservedObject stringMonitorObservedObject = (StringMonitorObservedObject)observedObject;
      stringMonitorObservedObject.setStatus(2);
    } 
    doStart();
  }
  
  public void stop() { doStop(); }
  
  public String getDerivedGauge(ObjectName paramObjectName) { return (String)super.getDerivedGauge(paramObjectName); }
  
  public long getDerivedGaugeTimeStamp(ObjectName paramObjectName) { return super.getDerivedGaugeTimeStamp(paramObjectName); }
  
  @Deprecated
  public String getDerivedGauge() { return this.observedObjects.isEmpty() ? null : (String)((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGauge(); }
  
  @Deprecated
  public long getDerivedGaugeTimeStamp() { return this.observedObjects.isEmpty() ? 0L : ((Monitor.ObservedObject)this.observedObjects.get(0)).getDerivedGaugeTimeStamp(); }
  
  public String getStringToCompare() { return this.stringToCompare; }
  
  public void setStringToCompare(String paramString) throws IllegalArgumentException {
    if (paramString == null)
      throw new IllegalArgumentException("Null string to compare"); 
    if (this.stringToCompare.equals(paramString))
      return; 
    this.stringToCompare = paramString;
    for (Monitor.ObservedObject observedObject : this.observedObjects) {
      StringMonitorObservedObject stringMonitorObservedObject = (StringMonitorObservedObject)observedObject;
      stringMonitorObservedObject.setStatus(2);
    } 
  }
  
  public boolean getNotifyMatch() { return this.notifyMatch; }
  
  public void setNotifyMatch(boolean paramBoolean) {
    if (this.notifyMatch == paramBoolean)
      return; 
    this.notifyMatch = paramBoolean;
  }
  
  public boolean getNotifyDiffer() { return this.notifyDiffer; }
  
  public void setNotifyDiffer(boolean paramBoolean) {
    if (this.notifyDiffer == paramBoolean)
      return; 
    this.notifyDiffer = paramBoolean;
  }
  
  public MBeanNotificationInfo[] getNotificationInfo() { return (MBeanNotificationInfo[])notifsInfo.clone(); }
  
  Monitor.ObservedObject createObservedObject(ObjectName paramObjectName) {
    StringMonitorObservedObject stringMonitorObservedObject = new StringMonitorObservedObject(paramObjectName);
    stringMonitorObservedObject.setStatus(2);
    return stringMonitorObservedObject;
  }
  
  boolean isComparableTypeValid(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) { return (paramComparable instanceof String); }
  
  void onErrorNotification(MonitorNotification paramMonitorNotification) {
    StringMonitorObservedObject stringMonitorObservedObject = (StringMonitorObservedObject)getObservedObject(paramMonitorNotification.getObservedObject());
    if (stringMonitorObservedObject == null)
      return; 
    stringMonitorObservedObject.setStatus(2);
  }
  
  MonitorNotification buildAlarmNotification(ObjectName paramObjectName, String paramString, Comparable<?> paramComparable) {
    String str1 = null;
    String str2 = null;
    String str3 = null;
    StringMonitorObservedObject stringMonitorObservedObject = (StringMonitorObservedObject)getObservedObject(paramObjectName);
    if (stringMonitorObservedObject == null)
      return null; 
    if (stringMonitorObservedObject.getStatus() == 2) {
      if (stringMonitorObservedObject.getDerivedGauge().equals(this.stringToCompare)) {
        if (this.notifyMatch) {
          str1 = "jmx.monitor.string.matches";
          str2 = "";
          str3 = this.stringToCompare;
        } 
        stringMonitorObservedObject.setStatus(1);
      } else {
        if (this.notifyDiffer) {
          str1 = "jmx.monitor.string.differs";
          str2 = "";
          str3 = this.stringToCompare;
        } 
        stringMonitorObservedObject.setStatus(0);
      } 
    } else if (stringMonitorObservedObject.getStatus() == 0) {
      if (stringMonitorObservedObject.getDerivedGauge().equals(this.stringToCompare)) {
        if (this.notifyMatch) {
          str1 = "jmx.monitor.string.matches";
          str2 = "";
          str3 = this.stringToCompare;
        } 
        stringMonitorObservedObject.setStatus(1);
      } 
    } else if (stringMonitorObservedObject.getStatus() == 1 && !stringMonitorObservedObject.getDerivedGauge().equals(this.stringToCompare)) {
      if (this.notifyDiffer) {
        str1 = "jmx.monitor.string.differs";
        str2 = "";
        str3 = this.stringToCompare;
      } 
      stringMonitorObservedObject.setStatus(0);
    } 
    return new MonitorNotification(str1, this, 0L, 0L, str2, null, null, null, str3);
  }
  
  static class StringMonitorObservedObject extends Monitor.ObservedObject {
    private int status;
    
    public StringMonitorObservedObject(ObjectName param1ObjectName) { super(param1ObjectName); }
    
    public final int getStatus() { return this.status; }
    
    public final void setStatus(int param1Int) { this.status = param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\StringMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */