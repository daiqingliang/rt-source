package com.sun.jmx.snmp.agent;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;

public interface SnmpTableEntryFactory extends SnmpTableCallbackHandler {
  void createNewEntry(SnmpMibSubRequest paramSnmpMibSubRequest, SnmpOid paramSnmpOid, int paramInt, SnmpMibTable paramSnmpMibTable) throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\agent\SnmpTableEntryFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */