package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public interface SnmpGenericMetaServer {
  Object buildAttributeValue(long paramLong, SnmpValue paramSnmpValue) throws SnmpStatusException;
  
  SnmpValue buildSnmpValue(long paramLong, Object paramObject) throws SnmpStatusException;
  
  String getAttributeName(long paramLong) throws SnmpStatusException;
  
  void checkSetAccess(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException;
  
  void checkGetAccess(long paramLong, Object paramObject) throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpGenericMetaServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */