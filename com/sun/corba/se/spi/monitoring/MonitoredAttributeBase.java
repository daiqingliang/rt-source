package com.sun.corba.se.spi.monitoring;

public abstract class MonitoredAttributeBase implements MonitoredAttribute {
  String name;
  
  MonitoredAttributeInfo attributeInfo;
  
  public MonitoredAttributeBase(String paramString, MonitoredAttributeInfo paramMonitoredAttributeInfo) {
    this.name = paramString;
    this.attributeInfo = paramMonitoredAttributeInfo;
  }
  
  MonitoredAttributeBase(String paramString) { this.name = paramString; }
  
  void setMonitoredAttributeInfo(MonitoredAttributeInfo paramMonitoredAttributeInfo) { this.attributeInfo = paramMonitoredAttributeInfo; }
  
  public void clearState() {}
  
  public abstract Object getValue();
  
  public void setValue(Object paramObject) {
    if (!this.attributeInfo.isWritable())
      throw new IllegalStateException("The Attribute " + this.name + " is not Writable..."); 
    throw new IllegalStateException("The method implementation is not provided for the attribute " + this.name);
  }
  
  public MonitoredAttributeInfo getAttributeInfo() { return this.attributeInfo; }
  
  public String getName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoredAttributeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */