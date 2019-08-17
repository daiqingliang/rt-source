package com.sun.jmx.snmp;

public class BerException extends Exception {
  private static final long serialVersionUID = 494709767137042951L;
  
  public static final int BAD_VERSION = 1;
  
  private int errorType = 0;
  
  public BerException() { this.errorType = 0; }
  
  public BerException(int paramInt) { this.errorType = paramInt; }
  
  public boolean isInvalidSnmpVersion() { return (this.errorType == 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\BerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */