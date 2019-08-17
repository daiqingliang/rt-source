package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttributeInfo;

public class MonitoredAttributeInfoImpl implements MonitoredAttributeInfo {
  private final String description;
  
  private final Class type;
  
  private final boolean writableFlag;
  
  private final boolean statisticFlag;
  
  MonitoredAttributeInfoImpl(String paramString, Class paramClass, boolean paramBoolean1, boolean paramBoolean2) {
    this.description = paramString;
    this.type = paramClass;
    this.writableFlag = paramBoolean1;
    this.statisticFlag = paramBoolean2;
  }
  
  public String getDescription() { return this.description; }
  
  public Class type() { return this.type; }
  
  public boolean isWritable() { return this.writableFlag; }
  
  public boolean isStatistic() { return this.statisticFlag; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoredAttributeInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */