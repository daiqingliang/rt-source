package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;

public interface SunJmxMBeanServer extends MBeanServer {
  MBeanInstantiator getMBeanInstantiator();
  
  boolean interceptorsEnabled();
  
  MBeanServer getMBeanServerInterceptor();
  
  void setMBeanServerInterceptor(MBeanServer paramMBeanServer);
  
  MBeanServerDelegate getMBeanServerDelegate();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\SunJmxMBeanServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */