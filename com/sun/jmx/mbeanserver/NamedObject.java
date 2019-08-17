package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;

public class NamedObject {
  private final ObjectName name;
  
  private final DynamicMBean object;
  
  public NamedObject(ObjectName paramObjectName, DynamicMBean paramDynamicMBean) {
    if (paramObjectName.isPattern())
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + paramObjectName.toString())); 
    this.name = paramObjectName;
    this.object = paramDynamicMBean;
  }
  
  public NamedObject(String paramString, DynamicMBean paramDynamicMBean) throws MalformedObjectNameException {
    ObjectName objectName = new ObjectName(paramString);
    if (objectName.isPattern())
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + objectName.toString())); 
    this.name = objectName;
    this.object = paramDynamicMBean;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof NamedObject))
      return false; 
    NamedObject namedObject = (NamedObject)paramObject;
    return this.name.equals(namedObject.getName());
  }
  
  public int hashCode() { return this.name.hashCode(); }
  
  public ObjectName getName() { return this.name; }
  
  public DynamicMBean getObject() { return this.object; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\NamedObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */