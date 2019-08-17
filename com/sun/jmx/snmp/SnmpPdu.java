package com.sun.jmx.snmp;

import java.io.Serializable;
import java.net.InetAddress;

public abstract class SnmpPdu implements SnmpDefinitions, Serializable {
  public int type = 0;
  
  public int version = 0;
  
  public SnmpVarBind[] varBindList;
  
  public int requestId = 0;
  
  public InetAddress address;
  
  public int port = 0;
  
  public static String pduTypeToString(int paramInt) {
    switch (paramInt) {
      case 160:
        return "SnmpGet";
      case 161:
        return "SnmpGetNext";
      case 253:
        return "SnmpWalk(*)";
      case 163:
        return "SnmpSet";
      case 162:
        return "SnmpResponse";
      case 164:
        return "SnmpV1Trap";
      case 167:
        return "SnmpV2Trap";
      case 165:
        return "SnmpGetBulk";
      case 166:
        return "SnmpInform";
    } 
    return "Unknown Command = " + paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPdu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */