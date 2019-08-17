package com.sun.jmx.snmp;

import com.sun.jmx.mbeanserver.Util;
import java.util.Vector;

public class SnmpOidDatabaseSupport implements SnmpOidDatabase {
  private Vector<SnmpOidTable> tables = new Vector();
  
  public SnmpOidDatabaseSupport() {}
  
  public SnmpOidDatabaseSupport(SnmpOidTable paramSnmpOidTable) { this.tables.addElement(paramSnmpOidTable); }
  
  public void add(SnmpOidTable paramSnmpOidTable) {
    if (!this.tables.contains(paramSnmpOidTable))
      this.tables.addElement(paramSnmpOidTable); 
  }
  
  public void remove(SnmpOidTable paramSnmpOidTable) {
    if (!this.tables.contains(paramSnmpOidTable))
      throw new SnmpStatusException("The specified SnmpOidTable does not exist in this SnmpOidDatabase"); 
    this.tables.removeElement(paramSnmpOidTable);
  }
  
  public SnmpOidRecord resolveVarName(String paramString) throws SnmpStatusException {
    byte b = 0;
    while (b < this.tables.size()) {
      try {
        return ((SnmpOidTable)this.tables.elementAt(b)).resolveVarName(paramString);
      } catch (SnmpStatusException snmpStatusException) {
        if (b == this.tables.size() - 1)
          throw new SnmpStatusException(snmpStatusException.getMessage()); 
        b++;
      } 
    } 
    return null;
  }
  
  public SnmpOidRecord resolveVarOid(String paramString) throws SnmpStatusException {
    byte b = 0;
    while (b < this.tables.size()) {
      try {
        return ((SnmpOidTable)this.tables.elementAt(b)).resolveVarOid(paramString);
      } catch (SnmpStatusException snmpStatusException) {
        if (b == this.tables.size() - 1)
          throw new SnmpStatusException(snmpStatusException.getMessage()); 
        b++;
      } 
    } 
    return null;
  }
  
  public Vector<?> getAllEntries() {
    Vector vector = new Vector();
    for (byte b = 0; b < this.tables.size(); b++) {
      Vector vector1 = (Vector)Util.cast(((SnmpOidTable)this.tables.elementAt(b)).getAllEntries());
      if (vector1 != null)
        for (byte b1 = 0; b1 < vector1.size(); b1++)
          vector.addElement(vector1.elementAt(b1));  
    } 
    return vector;
  }
  
  public void removeAll() { this.tables.removeAllElements(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpOidDatabaseSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */