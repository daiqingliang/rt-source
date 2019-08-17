package com.sun.jmx.snmp;

import java.io.Serializable;

public abstract class SnmpPduPacket extends SnmpPdu implements Serializable {
  public byte[] community;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPduPacket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */