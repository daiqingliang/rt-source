package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpSecurityException;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;

public interface SnmpSecurityModel extends SnmpModel {
  int generateRequestMsg(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, SnmpSecurityParameters paramSnmpSecurityParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4) throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException;
  
  int generateResponseMsg(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, SnmpSecurityParameters paramSnmpSecurityParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4) throws SnmpTooBigException, SnmpStatusException, SnmpSecurityException;
  
  SnmpSecurityParameters processIncomingRequest(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, byte[] paramArrayOfByte5, SnmpDecryptedPdu paramSnmpDecryptedPdu) throws SnmpStatusException, SnmpSecurityException;
  
  SnmpSecurityParameters processIncomingResponse(SnmpSecurityCache paramSnmpSecurityCache, int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4, byte[] paramArrayOfByte5, SnmpDecryptedPdu paramSnmpDecryptedPdu) throws SnmpStatusException, SnmpSecurityException;
  
  SnmpSecurityCache createSecurityCache();
  
  void releaseSecurityCache(SnmpSecurityCache paramSnmpSecurityCache);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\internal\SnmpSecurityModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */