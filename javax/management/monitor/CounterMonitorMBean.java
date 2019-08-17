package javax.management.monitor;

import javax.management.ObjectName;

public interface CounterMonitorMBean extends MonitorMBean {
  @Deprecated
  Number getDerivedGauge();
  
  @Deprecated
  long getDerivedGaugeTimeStamp();
  
  @Deprecated
  Number getThreshold();
  
  @Deprecated
  void setThreshold(Number paramNumber) throws IllegalArgumentException;
  
  Number getDerivedGauge(ObjectName paramObjectName);
  
  long getDerivedGaugeTimeStamp(ObjectName paramObjectName);
  
  Number getThreshold(ObjectName paramObjectName);
  
  Number getInitThreshold();
  
  void setInitThreshold(Number paramNumber) throws IllegalArgumentException;
  
  Number getOffset();
  
  void setOffset(Number paramNumber) throws IllegalArgumentException;
  
  Number getModulus();
  
  void setModulus(Number paramNumber) throws IllegalArgumentException;
  
  boolean getNotify();
  
  void setNotify(boolean paramBoolean);
  
  boolean getDifferenceMode();
  
  void setDifferenceMode(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\CounterMonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */