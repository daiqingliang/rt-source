package com.sun.jmx.snmp;

public interface SnmpPduBulkType extends SnmpAckPdu {
  void setMaxRepetitions(int paramInt);
  
  void setNonRepeaters(int paramInt);
  
  int getMaxRepetitions();
  
  int getNonRepeaters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPduBulkType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */