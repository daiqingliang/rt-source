package com.sun.jmx.snmp;

import java.io.Serializable;

public abstract class SnmpScopedPduPacket extends SnmpPdu implements Serializable {
  public int msgMaxSize = 0;
  
  public int msgId = 0;
  
  public byte msgFlags = 0;
  
  public int msgSecurityModel = 0;
  
  public byte[] contextEngineId = null;
  
  public byte[] contextName = null;
  
  public SnmpSecurityParameters securityParameters = null;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpScopedPduPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */