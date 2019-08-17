package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import com.sun.jmx.snmp.SnmpMsg;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.SnmpUnknownSecModelException;

public interface SnmpOutgoingRequest {
  SnmpSecurityCache getSecurityCache();
  
  int encodeMessage(byte[] paramArrayOfByte) throws SnmpStatusException, SnmpTooBigException, SnmpSecurityException, SnmpUnknownSecModelException, SnmpBadSecurityLevelException;
  
  SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt) throws SnmpStatusException, SnmpTooBigException;
  
  String printMessage();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\internal\SnmpOutgoingRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */