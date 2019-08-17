package com.sun.jmx.snmp.internal;

import com.sun.jmx.snmp.SnmpParams;
import com.sun.jmx.snmp.SnmpPdu;
import com.sun.jmx.snmp.SnmpPduFactory;
import com.sun.jmx.snmp.SnmpSecurityParameters;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpTooBigException;
import com.sun.jmx.snmp.mpm.SnmpMsgTranslator;

public interface SnmpMsgProcessingModel extends SnmpModel {
  SnmpOutgoingRequest getOutgoingRequest(SnmpPduFactory paramSnmpPduFactory);
  
  SnmpIncomingRequest getIncomingRequest(SnmpPduFactory paramSnmpPduFactory);
  
  SnmpIncomingResponse getIncomingResponse(SnmpPduFactory paramSnmpPduFactory);
  
  SnmpPdu getRequestPdu(SnmpParams paramSnmpParams, int paramInt) throws SnmpStatusException;
  
  int encode(int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, SnmpSecurityParameters paramSnmpSecurityParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt5, byte[] paramArrayOfByte4) throws SnmpTooBigException;
  
  int encodePriv(int paramInt1, int paramInt2, int paramInt3, byte paramByte, int paramInt4, SnmpSecurityParameters paramSnmpSecurityParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws SnmpTooBigException;
  
  SnmpDecryptedPdu decode(byte[] paramArrayOfByte) throws SnmpStatusException;
  
  int encode(SnmpDecryptedPdu paramSnmpDecryptedPdu, byte[] paramArrayOfByte) throws SnmpTooBigException;
  
  void setMsgTranslator(SnmpMsgTranslator paramSnmpMsgTranslator);
  
  SnmpMsgTranslator getMsgTranslator();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\internal\SnmpMsgProcessingModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */