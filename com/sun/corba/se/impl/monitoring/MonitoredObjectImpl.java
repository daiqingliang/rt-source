package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MonitoredObjectImpl implements MonitoredObject {
  private final String name;
  
  private final String description;
  
  private Map children = new HashMap();
  
  private Map monitoredAttributes = new HashMap();
  
  private MonitoredObject parent = null;
  
  MonitoredObjectImpl(String paramString1, String paramString2) {
    this.name = paramString1;
    this.description = paramString2;
  }
  
  public MonitoredObject getChild(String paramString) {
    synchronized (this) {
      return (MonitoredObject)this.children.get(paramString);
    } 
  }
  
  public Collection getChildren() {
    synchronized (this) {
      return this.children.values();
    } 
  }
  
  public void addChild(MonitoredObject paramMonitoredObject) {
    if (paramMonitoredObject != null)
      synchronized (this) {
        this.children.put(paramMonitoredObject.getName(), paramMonitoredObject);
        paramMonitoredObject.setParent(this);
      }  
  }
  
  public void removeChild(String paramString) {
    if (paramString != null)
      synchronized (this) {
        this.children.remove(paramString);
      }  
  }
  
  public MonitoredObject getParent() { return this.parent; }
  
  public void setParent(MonitoredObject paramMonitoredObject) { this.parent = paramMonitoredObject; }
  
  public MonitoredAttribute getAttribute(String paramString) {
    synchronized (this) {
      return (MonitoredAttribute)this.monitoredAttributes.get(paramString);
    } 
  }
  
  public Collection getAttributes() {
    synchronized (this) {
      return this.monitoredAttributes.values();
    } 
  }
  
  public void addAttribute(MonitoredAttribute paramMonitoredAttribute) {
    if (paramMonitoredAttribute != null)
      synchronized (this) {
        this.monitoredAttributes.put(paramMonitoredAttribute.getName(), paramMonitoredAttribute);
      }  
  }
  
  public void removeAttribute(String paramString) {
    if (paramString != null)
      synchronized (this) {
        this.monitoredAttributes.remove(paramString);
      }  
  }
  
  public void clearState() {
    synchronized (this) {
      Iterator iterator = this.monitoredAttributes.values().iterator();
      while (iterator.hasNext())
        ((MonitoredAttribute)iterator.next()).clearState(); 
      iterator = this.children.values().iterator();
      while (iterator.hasNext())
        ((MonitoredObject)iterator.next()).clearState(); 
    } 
  }
  
  public String getName() { return this.name; }
  
  public String getDescription() { return this.description; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoredObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */