package com.sun.jmx.snmp;

public interface SnmpSecurityParameters {
  int encode(byte[] paramArrayOfByte) throws SnmpTooBigException;
  
  void decode(byte[] paramArrayOfByte) throws SnmpStatusException;
  
  String getPrincipal();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpSecurityParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */