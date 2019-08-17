package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public interface DynamicMBean2 extends DynamicMBean {
  Object getResource();
  
  String getClassName();
  
  void preRegister2(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception;
  
  void registerFailed();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\DynamicMBean2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */