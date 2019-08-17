package com.sun.jmx.snmp;

public class SnmpTooBigException extends Exception {
  private static final long serialVersionUID = 4754796246674803969L;
  
  private int varBindCount = 0;
  
  public SnmpTooBigException() {}
  
  public SnmpTooBigException(int paramInt) {}
  
  public int getVarBindCount() { return this.varBindCount; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpTooBigException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */