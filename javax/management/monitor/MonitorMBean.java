package javax.management.monitor;

import javax.management.ObjectName;

public interface MonitorMBean {
  void start();
  
  void stop();
  
  void addObservedObject(ObjectName paramObjectName) throws IllegalArgumentException;
  
  void removeObservedObject(ObjectName paramObjectName) throws IllegalArgumentException;
  
  boolean containsObservedObject(ObjectName paramObjectName);
  
  ObjectName[] getObservedObjects();
  
  @Deprecated
  ObjectName getObservedObject();
  
  @Deprecated
  void setObservedObject(ObjectName paramObjectName) throws IllegalArgumentException;
  
  String getObservedAttribute();
  
  void setObservedAttribute(String paramString);
  
  long getGranularityPeriod();
  
  void setGranularityPeriod(long paramLong) throws IllegalArgumentException;
  
  boolean isActive();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\monitor\MonitorMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */