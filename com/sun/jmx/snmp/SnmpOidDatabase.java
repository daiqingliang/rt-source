package com.sun.jmx.snmp;

import java.util.Vector;

public interface SnmpOidDatabase extends SnmpOidTable {
  void add(SnmpOidTable paramSnmpOidTable);
  
  void remove(SnmpOidTable paramSnmpOidTable);
  
  void removeAll();
  
  SnmpOidRecord resolveVarName(String paramString) throws SnmpStatusException;
  
  SnmpOidRecord resolveVarOid(String paramString) throws SnmpStatusException;
  
  Vector<?> getAllEntries();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpOidDatabase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */