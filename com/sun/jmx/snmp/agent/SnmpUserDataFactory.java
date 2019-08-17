package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;

public interface SnmpUserDataFactory {
  Object allocateUserData(SnmpPdu paramSnmpPdu) throws SnmpStatusException;
  
  void releaseUserData(Object paramObject, SnmpPdu paramSnmpPdu) throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpUserDataFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */