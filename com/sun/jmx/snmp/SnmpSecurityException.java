package com.sun.jmx.snmp;

public class SnmpSecurityException extends Exception {
  private static final long serialVersionUID = 5574448147432833480L;
  
  public SnmpVarBind[] list = null;
  
  public int status = 242;
  
  public SnmpSecurityParameters params = null;
  
  public byte[] contextEngineId = null;
  
  public byte[] contextName = null;
  
  public byte flags = 0;
  
  public SnmpSecurityException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpSecurityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */