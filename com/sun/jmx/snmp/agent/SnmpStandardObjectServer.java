package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpVarBind;
import java.io.Serializable;
import java.util.Enumeration;

public class SnmpStandardObjectServer implements Serializable {
  private static final long serialVersionUID = -4641068116505308488L;
  
  public void get(SnmpStandardMetaServer paramSnmpStandardMetaServer, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    Object object = paramSnmpMibSubRequest.getUserData();
    Enumeration enumeration = paramSnmpMibSubRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      try {
        long l = snmpVarBind.oid.getOidArc(paramInt);
        snmpVarBind.value = paramSnmpStandardMetaServer.get(l, object);
      } catch (SnmpStatusException snmpStatusException) {
        paramSnmpMibSubRequest.registerGetException(snmpVarBind, snmpStatusException);
      } 
    } 
  }
  
  public void set(SnmpStandardMetaServer paramSnmpStandardMetaServer, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    Object object = paramSnmpMibSubRequest.getUserData();
    Enumeration enumeration = paramSnmpMibSubRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      try {
        long l = snmpVarBind.oid.getOidArc(paramInt);
        snmpVarBind.value = paramSnmpStandardMetaServer.set(snmpVarBind.value, l, object);
      } catch (SnmpStatusException snmpStatusException) {
        paramSnmpMibSubRequest.registerSetException(snmpVarBind, snmpStatusException);
      } 
    } 
  }
  
  public void check(SnmpStandardMetaServer paramSnmpStandardMetaServer, SnmpMibSubRequest paramSnmpMibSubRequest, int paramInt) throws SnmpStatusException {
    Object object = paramSnmpMibSubRequest.getUserData();
    Enumeration enumeration = paramSnmpMibSubRequest.getElements();
    while (enumeration.hasMoreElements()) {
      SnmpVarBind snmpVarBind = (SnmpVarBind)enumeration.nextElement();
      try {
        long l = snmpVarBind.oid.getOidArc(paramInt);
        paramSnmpStandardMetaServer.check(snmpVarBind.value, l, object);
      } catch (SnmpStatusException snmpStatusException) {
        paramSnmpMibSubRequest.registerCheckException(snmpVarBind, snmpStatusException);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpStandardObjectServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */