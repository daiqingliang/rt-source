package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpStatusException;

public interface SnmpAccessControlModel extends SnmpModel {
  void checkAccess(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, SnmpOid paramSnmpOid) throws SnmpStatusException;
  
  void checkPduAccess(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, SnmpPdu paramSnmpPdu) throws SnmpStatusException;
  
  boolean enableSnmpV1V2SetRequest();
  
  boolean disableSnmpV1V2SetRequest();
  
  boolean isSnmpV1V2SetRequestAuthorized();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\internal\SnmpAccessControlModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */