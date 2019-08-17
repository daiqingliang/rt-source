package com.sun.jmx.snmp;

import java.io.Serializable;

public class SnmpPduFactoryBER implements SnmpPduFactory, Serializable {
  private static final long serialVersionUID = -3525318344000547635L;
  
  public SnmpPdu decodeSnmpPdu(SnmpMsg paramSnmpMsg) throws SnmpStatusException { return paramSnmpMsg.decodeSnmpPdu(); }
  
  public SnmpMsg encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt) throws SnmpStatusException, SnmpTooBigException {
    SnmpV3Message snmpV3Message;
    SnmpMessage snmpMessage;
    switch (paramSnmpPdu.version) {
      case 0:
      case 1:
        snmpMessage = new SnmpMessage();
        snmpMessage.encodeSnmpPdu((SnmpPduPacket)paramSnmpPdu, paramInt);
        return snmpMessage;
      case 3:
        snmpV3Message = new SnmpV3Message();
        snmpV3Message.encodeSnmpPdu(paramSnmpPdu, paramInt);
        return snmpV3Message;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPduFactoryBER.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */