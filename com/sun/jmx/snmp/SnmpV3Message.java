package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;

public class SnmpV3Message extends SnmpMsg {
  public int msgId = 0;
  
  public int msgMaxSize = 0;
  
  public byte msgFlags = 0;
  
  public int msgSecurityModel = 0;
  
  public byte[] msgSecurityParameters = null;
  
  public byte[] contextEngineId = null;
  
  public byte[] contextName = null;
  
  public byte[] encryptedPdu = null;
  
  public int encodeMessage(byte[] paramArrayOfByte) throws SnmpTooBigException {
    boolean bool = false;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER))
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeMessage", "Can't encode directly V3Message! Need a SecuritySubSystem"); 
    throw new IllegalArgumentException("Can't encode");
  }
  
  public void decodeMessage(byte[] paramArrayOfByte, int paramInt) throws SnmpStatusException {
    try {
      BerDecoder berDecoder = new BerDecoder(paramArrayOfByte);
      berDecoder.openSequence();
      this.version = berDecoder.fetchInteger();
      berDecoder.openSequence();
      this.msgId = berDecoder.fetchInteger();
      this.msgMaxSize = berDecoder.fetchInteger();
      this.msgFlags = berDecoder.fetchOctetString()[0];
      this.msgSecurityModel = berDecoder.fetchInteger();
      berDecoder.closeSequence();
      this.msgSecurityParameters = berDecoder.fetchOctetString();
      if ((this.msgFlags & 0x2) == 0) {
        berDecoder.openSequence();
        this.contextEngineId = berDecoder.fetchOctetString();
        this.contextName = berDecoder.fetchOctetString();
        this.data = berDecoder.fetchAny();
        this.dataLength = this.data.length;
        berDecoder.closeSequence();
      } else {
        this.encryptedPdu = berDecoder.fetchOctetString();
      } 
      berDecoder.closeSequence();
    } catch (BerException berException) {
      berException.printStackTrace();
      throw new SnmpStatusException("Invalid encoding");
    } 
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("Unmarshalled message : \n").append("version : ").append(this.version).append("\n").append("msgId : ").append(this.msgId).append("\n").append("msgMaxSize : ").append(this.msgMaxSize).append("\n").append("msgFlags : ").append(this.msgFlags).append("\n").append("msgSecurityModel : ").append(this.msgSecurityModel).append("\n").append("contextEngineId : ").append((this.contextEngineId == null) ? null : SnmpEngineId.createEngineId(this.contextEngineId)).append("\n").append("contextName : ").append(this.contextName).append("\n").append("data : ").append(this.data).append("\n").append("dat len : ").append((this.data == null) ? 0 : this.data.length).append("\n").append("encryptedPdu : ").append(this.encryptedPdu).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeMessage", stringBuilder.toString());
    } 
  }
  
  public int getRequestId(byte[] paramArrayOfByte) throws SnmpTooBigException {
    BerDecoder berDecoder = null;
    int i = 0;
    try {
      berDecoder = new BerDecoder(paramArrayOfByte);
      berDecoder.openSequence();
      berDecoder.fetchInteger();
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
  
  public void encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt) throws SnmpStatusException, SnmpTooBigException {
    SnmpScopedPduPacket snmpScopedPduPacket = (SnmpScopedPduPacket)paramSnmpPdu;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("PDU to marshall: \n").append("security parameters : ").append(snmpScopedPduPacket.securityParameters).append("\n").append("type : ").append(snmpScopedPduPacket.type).append("\n").append("version : ").append(snmpScopedPduPacket.version).append("\n").append("requestId : ").append(snmpScopedPduPacket.requestId).append("\n").append("msgId : ").append(snmpScopedPduPacket.msgId).append("\n").append("msgMaxSize : ").append(snmpScopedPduPacket.msgMaxSize).append("\n").append("msgFlags : ").append(snmpScopedPduPacket.msgFlags).append("\n").append("msgSecurityModel : ").append(snmpScopedPduPacket.msgSecurityModel).append("\n").append("contextEngineId : ").append(snmpScopedPduPacket.contextEngineId).append("\n").append("contextName : ").append(snmpScopedPduPacket.contextName).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "encodeSnmpPdu", stringBuilder.toString());
    } 
    this.version = snmpScopedPduPacket.version;
    this.address = snmpScopedPduPacket.address;
    this.port = snmpScopedPduPacket.port;
    this.msgId = snmpScopedPduPacket.msgId;
    this.msgMaxSize = snmpScopedPduPacket.msgMaxSize;
    this.msgFlags = snmpScopedPduPacket.msgFlags;
    this.msgSecurityModel = snmpScopedPduPacket.msgSecurityModel;
    this.contextEngineId = snmpScopedPduPacket.contextEngineId;
    this.contextName = snmpScopedPduPacket.contextName;
    this.securityParameters = snmpScopedPduPacket.securityParameters;
    this.data = new byte[paramInt];
    try {
      SnmpPduBulkType snmpPduBulkType;
      SnmpPduRequestType snmpPduRequestType;
      BerEncoder berEncoder = new BerEncoder(this.data);
      berEncoder.openSequence();
      encodeVarBindList(berEncoder, snmpScopedPduPacket.varBindList);
      switch (snmpScopedPduPacket.type) {
        case 160:
        case 161:
        case 162:
        case 163:
        case 166:
        case 167:
        case 168:
          snmpPduRequestType = (SnmpPduRequestType)snmpScopedPduPacket;
          berEncoder.putInteger(snmpPduRequestType.getErrorIndex());
          berEncoder.putInteger(snmpPduRequestType.getErrorStatus());
          berEncoder.putInteger(snmpScopedPduPacket.requestId);
          break;
        case 165:
          snmpPduBulkType = (SnmpPduBulkType)snmpScopedPduPacket;
          berEncoder.putInteger(snmpPduBulkType.getMaxRepetitions());
          berEncoder.putInteger(snmpPduBulkType.getNonRepeaters());
          berEncoder.putInteger(snmpScopedPduPacket.requestId);
          break;
        default:
          throw new SnmpStatusException("Invalid pdu type " + String.valueOf(snmpScopedPduPacket.type));
      } 
      berEncoder.closeSequence(snmpScopedPduPacket.type);
      this.dataLength = berEncoder.trim();
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SnmpTooBigException();
    } 
  }
  
  public SnmpPdu decodeSnmpPdu() throws SnmpStatusException {
    SnmpScopedPduBulk snmpScopedPduBulk = null;
    BerDecoder berDecoder = new BerDecoder(this.data);
    try {
      SnmpScopedPduBulk snmpScopedPduBulk1;
      SnmpScopedPduRequest snmpScopedPduRequest;
      int i = berDecoder.getTag();
      berDecoder.openSequence(i);
      switch (i) {
        case 160:
        case 161:
        case 162:
        case 163:
        case 166:
        case 167:
        case 168:
          snmpScopedPduRequest = new SnmpScopedPduRequest();
          snmpScopedPduRequest.requestId = berDecoder.fetchInteger();
          snmpScopedPduRequest.setErrorStatus(berDecoder.fetchInteger());
          snmpScopedPduRequest.setErrorIndex(berDecoder.fetchInteger());
          snmpScopedPduBulk = snmpScopedPduRequest;
          break;
        case 165:
          snmpScopedPduBulk1 = new SnmpScopedPduBulk();
          snmpScopedPduBulk1.requestId = berDecoder.fetchInteger();
          snmpScopedPduBulk1.setNonRepeaters(berDecoder.fetchInteger());
          snmpScopedPduBulk1.setMaxRepetitions(berDecoder.fetchInteger());
          snmpScopedPduBulk = snmpScopedPduBulk1;
          break;
        default:
          throw new SnmpStatusException(9);
      } 
      snmpScopedPduBulk.type = i;
      snmpScopedPduBulk.varBindList = decodeVarBindList(berDecoder);
      berDecoder.closeSequence();
    } catch (BerException berException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpV3Message.class.getName(), "decodeSnmpPdu", "BerException", berException); 
      throw new SnmpStatusException(9);
    } 
    snmpScopedPduBulk.address = this.address;
    snmpScopedPduBulk.port = this.port;
    snmpScopedPduBulk.msgFlags = this.msgFlags;
    snmpScopedPduBulk.version = this.version;
    snmpScopedPduBulk.msgId = this.msgId;
    snmpScopedPduBulk.msgMaxSize = this.msgMaxSize;
    snmpScopedPduBulk.msgSecurityModel = this.msgSecurityModel;
    snmpScopedPduBulk.contextEngineId = this.contextEngineId;
    snmpScopedPduBulk.contextName = this.contextName;
    snmpScopedPduBulk.securityParameters = this.securityParameters;
    if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINER)) {
      StringBuilder stringBuilder = (new StringBuilder()).append("Unmarshalled PDU : \n").append("type : ").append(snmpScopedPduBulk.type).append("\n").append("version : ").append(snmpScopedPduBulk.version).append("\n").append("requestId : ").append(snmpScopedPduBulk.requestId).append("\n").append("msgId : ").append(snmpScopedPduBulk.msgId).append("\n").append("msgMaxSize : ").append(snmpScopedPduBulk.msgMaxSize).append("\n").append("msgFlags : ").append(snmpScopedPduBulk.msgFlags).append("\n").append("msgSecurityModel : ").append(snmpScopedPduBulk.msgSecurityModel).append("\n").append("contextEngineId : ").append(snmpScopedPduBulk.contextEngineId).append("\n").append("contextName : ").append(snmpScopedPduBulk.contextName).append("\n");
      JmxProperties.SNMP_LOGGER.logp(Level.FINER, SnmpV3Message.class.getName(), "decodeSnmpPdu", stringBuilder.toString());
    } 
    return snmpScopedPduBulk;
  }
  
  public String printMessage() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("msgId : " + this.msgId + "\n");
    stringBuffer.append("msgMaxSize : " + this.msgMaxSize + "\n");
    stringBuffer.append("msgFlags : " + this.msgFlags + "\n");
    stringBuffer.append("msgSecurityModel : " + this.msgSecurityModel + "\n");
    if (this.contextEngineId == null) {
      stringBuffer.append("contextEngineId : null");
    } else {
      stringBuffer.append("contextEngineId : {\n");
      stringBuffer.append(dumpHexBuffer(this.contextEngineId, 0, this.contextEngineId.length));
      stringBuffer.append("\n}\n");
    } 
    if (this.contextName == null) {
      stringBuffer.append("contextName : null");
    } else {
      stringBuffer.append("contextName : {\n");
      stringBuffer.append(dumpHexBuffer(this.contextName, 0, this.contextName.length));
      stringBuffer.append("\n}\n");
    } 
    return stringBuffer.append(super.printMessage()).toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpV3Message.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */