package com.sun.jmx.snmp;

import java.io.Serializable;

public abstract class SnmpValue implements Cloneable, Serializable, SnmpDataTypeEnums {
  public String toAsn1String() { return "[" + getTypeName() + "] " + toString(); }
  
  public abstract SnmpOid toOid();
  
  public abstract String getTypeName();
  
  public abstract SnmpValue duplicate();
  
  public boolean isNoSuchObjectValue() { return false; }
  
  public boolean isNoSuchInstanceValue() { return false; }
  
  public boolean isEndOfMibViewValue() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */