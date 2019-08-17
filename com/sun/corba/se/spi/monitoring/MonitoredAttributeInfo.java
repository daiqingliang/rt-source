package com.sun.corba.se.spi.monitoring;

public interface MonitoredAttributeInfo {
  boolean isWritable();
  
  boolean isStatistic();
  
  Class type();
  
  String getDescription();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoredAttributeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */