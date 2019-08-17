package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ServiceNotFoundException;

public interface SnmpMibAgentMBean {
  void get(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException;
  
  void getNext(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException;
  
  void getBulk(SnmpMibRequest paramSnmpMibRequest, int paramInt1, int paramInt2) throws SnmpStatusException;
  
  void set(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException;
  
  void check(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException;
  
  MBeanServer getMBeanServer();
  
  SnmpMibHandler getSnmpAdaptor();
  
  void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler);
  
  void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler, SnmpOid[] paramArrayOfSnmpOid);
  
  void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler, String paramString);
  
  void setSnmpAdaptor(SnmpMibHandler paramSnmpMibHandler, String paramString, SnmpOid[] paramArrayOfSnmpOid);
  
  ObjectName getSnmpAdaptorName();
  
  void setSnmpAdaptorName(ObjectName paramObjectName) throws InstanceNotFoundException, ServiceNotFoundException;
  
  void setSnmpAdaptorName(ObjectName paramObjectName, SnmpOid[] paramArrayOfSnmpOid) throws InstanceNotFoundException, ServiceNotFoundException;
  
  void setSnmpAdaptorName(ObjectName paramObjectName, String paramString) throws InstanceNotFoundException, ServiceNotFoundException;
  
  void setSnmpAdaptorName(ObjectName paramObjectName, String paramString, SnmpOid[] paramArrayOfSnmpOid) throws InstanceNotFoundException, ServiceNotFoundException;
  
  boolean getBindingState();
  
  String getMibName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpMibAgentMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */