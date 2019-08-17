package javax.management.monitor;

import javax.management.ObjectName;

public interface StringMonitorMBean extends MonitorMBean {
  @Deprecated
  String getDerivedGauge();
  
  @Deprecated
  long getDerivedGaugeTimeStamp();
  
  String getDerivedGauge(ObjectName paramObjectName);
  
  long getDerivedGaugeTimeStamp(ObjectName paramObjectName);
  
  String getStringToCompare();
  
  void setStringToCompare(String paramString) throws IllegalArgumentException;
  
  boolean getNotifyMatch();
  
  void setNotifyMatch(boolean paramBoolean);
  
  boolean getNotifyDiffer();
  
  void setNotifyDiffer(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\StringMonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */