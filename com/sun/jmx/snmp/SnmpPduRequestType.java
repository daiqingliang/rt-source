package com.sun.jmx.snmp;

public interface SnmpPduRequestType extends SnmpAckPdu {
  void setErrorIndex(int paramInt);
  
  void setErrorStatus(int paramInt);
  
  int getErrorIndex();
  
  int getErrorStatus();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPduRequestType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */