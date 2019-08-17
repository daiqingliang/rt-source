package com.sun.jmx.snmp;

public interface SnmpPduFactory {
  SnmpPdu decodeSnmpPdu(SnmpMsg paramSnmpMsg) throws SnmpStatusException;
  
  SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt) throws SnmpStatusException, SnmpTooBigException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPduFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */