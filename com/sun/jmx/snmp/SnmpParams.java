package com.sun.jmx.snmp;

public abstract class SnmpParams implements SnmpDefinitions {
  private int protocolVersion = 0;
  
  SnmpParams(int paramInt) { this.protocolVersion = paramInt; }
  
  SnmpParams() {}
  
  public abstract boolean allowSnmpSets();
  
  public int getProtocolVersion() { return this.protocolVersion; }
  
  public void setProtocolVersion(int paramInt) { this.protocolVersion = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */