package com.sun.jmx.mbeanserver;

import javax.management.MBeanServer;
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerDelegate;

public class JmxMBeanServerBuilder extends MBeanServerBuilder {
  public MBeanServerDelegate newMBeanServerDelegate() { return JmxMBeanServer.newMBeanServerDelegate(); }
  
  public MBeanServer newMBeanServer(String paramString, MBeanServer paramMBeanServer, MBeanServerDelegate paramMBeanServerDelegate) { return JmxMBeanServer.newMBeanServer(paramString, paramMBeanServer, paramMBeanServerDelegate, true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\JmxMBeanServerBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */