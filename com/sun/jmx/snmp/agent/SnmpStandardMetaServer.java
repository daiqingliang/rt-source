package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpValue;

public interface SnmpStandardMetaServer {
  SnmpValue get(long paramLong, Object paramObject) throws SnmpStatusException;
  
  SnmpValue set(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException;
  
  void check(SnmpValue paramSnmpValue, long paramLong, Object paramObject) throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpStandardMetaServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */