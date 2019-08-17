package com.sun.jmx.snmp.agent;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.logging.Level;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SnmpErrorHandlerAgent extends SnmpMibAgent implements Serializable {
  private static final long serialVersionUID = 7751082923508885650L;
  
  public void init() {}
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception { return paramObjectName; }
  
  public long[] getRootOid() { return null; }
  
  public void get(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "get", "Get in Exception");
    if (paramSnmpMibRequest.getVersion() == 0)
      throw new SnmpStatusException(2); 
    Enumeration enumeration = paramSnmpMibRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      snmpVarBind.setNoSuchObject();
    } 
  }
  
  public void check(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "check", "Check in Exception");
    throw new SnmpStatusException(17);
  }
  
  public void set(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "set", "Set in Exception, CANNOT be called");
    throw new SnmpStatusException(17);
  }
  
  public void getNext(SnmpMibRequest paramSnmpMibRequest) throws SnmpStatusException {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "getNext", "GetNext in Exception");
    if (paramSnmpMibRequest.getVersion() == 0)
      throw new SnmpStatusException(2); 
    Enumeration enumeration = paramSnmpMibRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      snmpVarBind.setEndOfMibView();
    } 
  }
  
  public void getBulk(SnmpMibRequest paramSnmpMibRequest, int paramInt1, int paramInt2) throws SnmpStatusException {
    JmxProperties.SNMP_ADAPTOR_LOGGER.logp(Level.FINEST, SnmpErrorHandlerAgent.class.getName(), "getBulk", "GetBulk in Exception");
    if (paramSnmpMibRequest.getVersion() == 0)
      throw new SnmpStatusException(5, 0); 
    Enumeration enumeration = paramSnmpMibRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      snmpVarBind.setEndOfMibView();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpErrorHandlerAgent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */