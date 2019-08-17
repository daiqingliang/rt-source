package com.sun.corba.se.spi.monitoring;

import com.sun.corba.se.impl.monitoring.MonitoredAttributeInfoFactoryImpl;
import com.sun.corba.se.impl.monitoring.MonitoredObjectFactoryImpl;
import com.sun.corba.se.impl.monitoring.MonitoringManagerFactoryImpl;

public class MonitoringFactories {
  private static final MonitoredObjectFactoryImpl monitoredObjectFactory = new MonitoredObjectFactoryImpl();
  
  private static final MonitoredAttributeInfoFactoryImpl monitoredAttributeInfoFactory = new MonitoredAttributeInfoFactoryImpl();
  
  private static final MonitoringManagerFactoryImpl monitoringManagerFactory = new MonitoringManagerFactoryImpl();
  
  public static MonitoredObjectFactory getMonitoredObjectFactory() { return monitoredObjectFactory; }
  
  public static MonitoredAttributeInfoFactory getMonitoredAttributeInfoFactory() { return monitoredAttributeInfoFactory; }
  
  public static MonitoringManagerFactory getMonitoringManagerFactory() { return monitoringManagerFactory; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\monitoring\MonitoringFactories.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */