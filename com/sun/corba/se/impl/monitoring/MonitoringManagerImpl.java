package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;

public class MonitoringManagerImpl implements MonitoringManager {
  private final MonitoredObject rootMonitoredObject;
  
  MonitoringManagerImpl(String paramString1, String paramString2) {
    MonitoredObjectFactory monitoredObjectFactory = MonitoringFactories.getMonitoredObjectFactory();
    this.rootMonitoredObject = monitoredObjectFactory.createMonitoredObject(paramString1, paramString2);
  }
  
  public void clearState() { this.rootMonitoredObject.clearState(); }
  
  public MonitoredObject getRootMonitoredObject() { return this.rootMonitoredObject; }
  
  public void close() {
    MonitoringManagerFactory monitoringManagerFactory = MonitoringFactories.getMonitoringManagerFactory();
    monitoringManagerFactory.remove(this.rootMonitoredObject.getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoringManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */