package com.sun.jmx.snmp;

import java.net.InetAddress;
import java.util.Vector;

public abstract class SnmpMsg implements SnmpDefinitions {
  public int version = 0;
  
  public byte[] data = null;
  
  public int dataLength = 0;
  
  public InetAddress address = null;
  
  public int port = 0;
  
  public SnmpSecurityParameters securityParameters = null;
  
  public static int getProtocolVersion(byte[] paramArrayOfByte) throws SnmpStatusException {
    int i = 0;
    BerDecoder berDecoder = null;
    try {
      berDecoder = new BerDecoder(paramArrayOfByte);
      berDecoder.openSequence();
      i = berDecoder.fetchInteger();
    } catch (BerException berException) {
      throw new SnmpStatusException("Invalid encoding");
    } 
    try {
      berDecoder.closeSequence();
    } catch (BerException berException) {}
    return i;
  }
  
  public abstract int getRequestId(byte[] paramArrayOfByte) throws SnmpStatusException;
  
  public abstract int encodeMessage(byte[] paramArrayOfByte) throws SnmpStatusException;
  
  public abstract void decodeMessage(byte[] paramArrayOfByte, int paramInt) throws SnmpStatusException;
  
  public abstract void encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt) throws SnmpStatusException, SnmpTooBigException;
  
  public abstract SnmpPdu decodeSnmpPdu() throws SnmpStatusException;
  
  public static String dumpHexBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    StringBuffer stringBuffer = new StringBuffer(paramInt2 << 1);
    byte b = 1;
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      byte b1 = paramArrayOfByte[j] & 0xFF;
      stringBuffer.append(Character.forDigit(b1 >>> 4, 16));
      stringBuffer.append(Character.forDigit(b1 & 0xF, 16));
      if (++b % 16 == 0) {
        stringBuffer.append('\n');
        b = 1;
      } else {
        stringBuffer.append(' ');
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String printMessage() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Version: ");
    stringBuffer.append(this.version);
    stringBuffer.append("\n");
    if (this.data == null) {
      stringBuffer.append("Data: null");
    } else {
      stringBuffer.append("Data: {\n");
      stringBuffer.append(dumpHexBuffer(this.data, 0, this.dataLength));
      stringBuffer.append("\n}\n");
    } 
    return stringBuffer.toString();
  }
  
  public void encodeVarBindList(BerEncoder paramBerEncoder, SnmpVarBind[] paramArrayOfSnmpVarBind) throws SnmpStatusException, SnmpTooBigException {
    byte b = 0;
    try {
      paramBerEncoder.openSequence();
      if (paramArrayOfSnmpVarBind != null)
        for (int i = paramArrayOfSnmpVarBind.length - 1; i >= 0; i--) {
          SnmpVarBind snmpVarBind = paramArrayOfSnmpVarBind[i];
          if (snmpVarBind != null) {
            paramBerEncoder.openSequence();
            encodeVarBindValue(paramBerEncoder, snmpVarBind.value);
            paramBerEncoder.putOid(snmpVarBind.oid.longValue());
            paramBerEncoder.closeSequence();
            b++;
          } 
        }  
      paramBerEncoder.closeSequence();
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SnmpTooBigException(b);
    } 
  }
  
  void encodeVarBindValue(BerEncoder paramBerEncoder, SnmpValue paramSnmpValue) throws SnmpStatusException {
    if (paramSnmpValue == null) {
      paramBerEncoder.putNull();
    } else if (paramSnmpValue instanceof SnmpIpAddress) {
      paramBerEncoder.putOctetString(((SnmpIpAddress)paramSnmpValue).byteValue(), 64);
    } else if (paramSnmpValue instanceof SnmpCounter) {
      paramBerEncoder.putInteger(((SnmpCounter)paramSnmpValue).longValue(), 65);
    } else if (paramSnmpValue instanceof SnmpGauge) {
      paramBerEncoder.putInteger(((SnmpGauge)paramSnmpValue).longValue(), 66);
    } else if (paramSnmpValue instanceof SnmpTimeticks) {
      paramBerEncoder.putInteger(((SnmpTimeticks)paramSnmpValue).longValue(), 67);
    } else if (paramSnmpValue instanceof SnmpOpaque) {
      paramBerEncoder.putOctetString(((SnmpOpaque)paramSnmpValue).byteValue(), 68);
    } else if (paramSnmpValue instanceof SnmpInt) {
      paramBerEncoder.putInteger(((SnmpInt)paramSnmpValue).intValue());
    } else if (paramSnmpValue instanceof SnmpString) {
      paramBerEncoder.putOctetString(((SnmpString)paramSnmpValue).byteValue());
    } else if (paramSnmpValue instanceof SnmpOid) {
      paramBerEncoder.putOid(((SnmpOid)paramSnmpValue).longValue());
    } else if (paramSnmpValue instanceof SnmpCounter64) {
      if (this.version == 0)
        throw new SnmpStatusException("Invalid value for SNMP v1 : " + paramSnmpValue); 
      paramBerEncoder.putInteger(((SnmpCounter64)paramSnmpValue).longValue(), 70);
    } else if (paramSnmpValue instanceof SnmpNull) {
      int i = ((SnmpNull)paramSnmpValue).getTag();
      if (this.version == 0 && i != 5)
        throw new SnmpStatusException("Invalid value for SNMP v1 : " + paramSnmpValue); 
      if (this.version == 1 && i != 5 && i != 128 && i != 129 && i != 130)
        throw new SnmpStatusException("Invalid value " + paramSnmpValue); 
      paramBerEncoder.putNull(i);
    } else {
      throw new SnmpStatusException("Invalid value " + paramSnmpValue);
    } 
  }
  
  public SnmpVarBind[] decodeVarBindList(BerDecoder paramBerDecoder) throws BerException {
    paramBerDecoder.openSequence();
    Vector vector = new Vector();
    while (paramBerDecoder.cannotCloseSequence()) {
      SnmpVarBind snmpVarBind = new SnmpVarBind();
      paramBerDecoder.openSequence();
      snmpVarBind.oid = new SnmpOid(paramBerDecoder.fetchOid());
      snmpVarBind.setSnmpValue(decodeVarBindValue(paramBerDecoder));
      paramBerDecoder.closeSequence();
      vector.addElement(snmpVarBind);
    } 
    paramBerDecoder.closeSequence();
    SnmpVarBind[] arrayOfSnmpVarBind = new SnmpVarBind[vector.size()];
    vector.copyInto(arrayOfSnmpVarBind);
    return arrayOfSnmpVarBind;
  }
  
  SnmpValue decodeVarBindValue(BerDecoder paramBerDecoder) throws BerException {
    SnmpCounter64 snmpCounter64 = null;
    int i = paramBerDecoder.getTag();
    switch (i) {
      case 2:
        try {
          snmpCounter64 = new SnmpInt(paramBerDecoder.fetchInteger());
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 4:
        try {
          snmpCounter64 = new SnmpString(paramBerDecoder.fetchOctetString());
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 6:
        try {
          snmpCounter64 = new SnmpOid(paramBerDecoder.fetchOid());
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 5:
        paramBerDecoder.fetchNull();
        try {
          snmpCounter64 = new SnmpNull();
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 64:
        try {
          snmpCounter64 = new SnmpIpAddress(paramBerDecoder.fetchOctetString(i));
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 65:
        try {
          snmpCounter64 = new SnmpCounter(paramBerDecoder.fetchIntegerAsLong(i));
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 66:
        try {
          snmpCounter64 = new SnmpGauge(paramBerDecoder.fetchIntegerAsLong(i));
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 67:
        try {
          snmpCounter64 = new SnmpTimeticks(paramBerDecoder.fetchIntegerAsLong(i));
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 68:
        try {
          snmpCounter64 = new SnmpOpaque(paramBerDecoder.fetchOctetString(i));
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 70:
        if (this.version == 0)
          throw new BerException(1); 
        try {
          snmpCounter64 = new SnmpCounter64(paramBerDecoder.fetchIntegerAsLong(i));
        } catch (RuntimeException runtimeException) {
          throw new BerException();
        } 
        return snmpCounter64;
      case 128:
        if (this.version == 0)
          throw new BerException(1); 
        paramBerDecoder.fetchNull(i);
        return SnmpVarBind.noSuchObject;
      case 129:
        if (this.version == 0)
          throw new BerException(1); 
        paramBerDecoder.fetchNull(i);
        return SnmpVarBind.noSuchInstance;
      case 130:
        if (this.version == 0)
          throw new BerException(1); 
        paramBerDecoder.fetchNull(i);
        return SnmpVarBind.endOfMibView;
    } 
    throw new BerException();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpMsg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */