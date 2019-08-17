package com.sun.jmx.snmp;

import com.sun.jmx.defaults.JmxProperties;
import java.util.logging.Level;

public class SnmpMessage extends SnmpMsg implements SnmpDefinitions {
  public byte[] community;
  
  public int encodeMessage(byte[] paramArrayOfByte) throws SnmpTooBigException {
    int i = 0;
    if (this.data == null)
      throw new IllegalArgumentException("Data field is null"); 
    try {
      BerEncoder berEncoder = new BerEncoder(paramArrayOfByte);
      berEncoder.openSequence();
      berEncoder.putAny(this.data, this.dataLength);
      berEncoder.putOctetString((this.community != null) ? this.community : new byte[0]);
      berEncoder.putInteger(this.version);
      berEncoder.closeSequence();
      i = berEncoder.trim();
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SnmpTooBigException();
    } 
    return i;
  }
  
  public int getRequestId(byte[] paramArrayOfByte) throws SnmpTooBigException {
    int i = 0;
    BerDecoder berDecoder1 = null;
    BerDecoder berDecoder2 = null;
    byte[] arrayOfByte = null;
    try {
      berDecoder1 = new BerDecoder(paramArrayOfByte);
      berDecoder1.openSequence();
      berDecoder1.fetchInteger();
      berDecoder1.fetchOctetString();
      arrayOfByte = berDecoder1.fetchAny();
      berDecoder2 = new BerDecoder(arrayOfByte);
      int j = berDecoder2.getTag();
      berDecoder2.openSequence(j);
      i = berDecoder2.fetchInteger();
    } catch (BerException berException) {
      throw new SnmpStatusException("Invalid encoding");
    } 
    try {
      berDecoder1.closeSequence();
    } catch (BerException berException) {}
    try {
      berDecoder2.closeSequence();
    } catch (BerException berException) {}
    return i;
  }
  
  public void decodeMessage(byte[] paramArrayOfByte, int paramInt) throws SnmpStatusException {
    try {
      BerDecoder berDecoder = new BerDecoder(paramArrayOfByte);
      berDecoder.openSequence();
      this.version = berDecoder.fetchInteger();
      this.community = berDecoder.fetchOctetString();
      this.data = berDecoder.fetchAny();
      this.dataLength = this.data.length;
      berDecoder.closeSequence();
    } catch (BerException berException) {
      throw new SnmpStatusException("Invalid encoding");
    } 
  }
  
  public void encodeSnmpPdu(SnmpPdu paramSnmpPdu, int paramInt) throws SnmpStatusException, SnmpTooBigException {
    SnmpPduPacket snmpPduPacket = (SnmpPduPacket)paramSnmpPdu;
    this.version = snmpPduPacket.version;
    this.community = snmpPduPacket.community;
    this.address = snmpPduPacket.address;
    this.port = snmpPduPacket.port;
    this.data = new byte[paramInt];
    try {
      SnmpPduTrap snmpPduTrap;
      SnmpPduBulk snmpPduBulk;
      SnmpPduRequest snmpPduRequest;
      BerEncoder berEncoder = new BerEncoder(this.data);
      berEncoder.openSequence();
      encodeVarBindList(berEncoder, snmpPduPacket.varBindList);
      switch (snmpPduPacket.type) {
        case 160:
        case 161:
        case 162:
        case 163:
        case 166:
        case 167:
        case 168:
          snmpPduRequest = (SnmpPduRequest)snmpPduPacket;
          berEncoder.putInteger(snmpPduRequest.errorIndex);
          berEncoder.putInteger(snmpPduRequest.errorStatus);
          berEncoder.putInteger(snmpPduRequest.requestId);
          break;
        case 165:
          snmpPduBulk = (SnmpPduBulk)snmpPduPacket;
          berEncoder.putInteger(snmpPduBulk.maxRepetitions);
          berEncoder.putInteger(snmpPduBulk.nonRepeaters);
          berEncoder.putInteger(snmpPduBulk.requestId);
          break;
        case 164:
          snmpPduTrap = (SnmpPduTrap)snmpPduPacket;
          berEncoder.putInteger(snmpPduTrap.timeStamp, 67);
          berEncoder.putInteger(snmpPduTrap.specificTrap);
          berEncoder.putInteger(snmpPduTrap.genericTrap);
          if (snmpPduTrap.agentAddr != null) {
            berEncoder.putOctetString(snmpPduTrap.agentAddr.byteValue(), 64);
          } else {
            berEncoder.putOctetString(new byte[0], 64);
          } 
          berEncoder.putOid(snmpPduTrap.enterprise.longValue());
          break;
        default:
          throw new SnmpStatusException("Invalid pdu type " + String.valueOf(snmpPduPacket.type));
      } 
      berEncoder.closeSequence(snmpPduPacket.type);
      this.dataLength = berEncoder.trim();
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      throw new SnmpTooBigException();
    } 
  }
  
  public SnmpPdu decodeSnmpPdu() throws SnmpStatusException {
    SnmpPduTrap snmpPduTrap = null;
    BerDecoder berDecoder = new BerDecoder(this.data);
    try {
      byte[] arrayOfByte;
      SnmpPduTrap snmpPduTrap1;
      SnmpPduBulk snmpPduBulk2;
      SnmpPduRequest snmpPduRequest;
      SnmpPduBulk snmpPduBulk1;
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
          snmpPduRequest = new SnmpPduRequest();
          snmpPduRequest.requestId = berDecoder.fetchInteger();
          snmpPduRequest.errorStatus = berDecoder.fetchInteger();
          snmpPduRequest.errorIndex = berDecoder.fetchInteger();
          snmpPduTrap = snmpPduRequest;
          break;
        case 165:
          snmpPduBulk2 = new SnmpPduBulk();
          snmpPduBulk2.requestId = berDecoder.fetchInteger();
          snmpPduBulk2.nonRepeaters = berDecoder.fetchInteger();
          snmpPduBulk2.maxRepetitions = berDecoder.fetchInteger();
          snmpPduBulk1 = snmpPduBulk2;
          break;
        case 164:
          snmpPduTrap1 = new SnmpPduTrap();
          snmpPduTrap1.enterprise = new SnmpOid(berDecoder.fetchOid());
          arrayOfByte = berDecoder.fetchOctetString(64);
          if (arrayOfByte.length != 0) {
            snmpPduTrap1.agentAddr = new SnmpIpAddress(arrayOfByte);
          } else {
            snmpPduTrap1.agentAddr = null;
          } 
          snmpPduTrap1.genericTrap = berDecoder.fetchInteger();
          snmpPduTrap1.specificTrap = berDecoder.fetchInteger();
          snmpPduTrap1.timeStamp = berDecoder.fetchInteger(67);
          snmpPduTrap = snmpPduTrap1;
          break;
        default:
          throw new SnmpStatusException(9);
      } 
      snmpPduTrap.type = i;
      snmpPduTrap.varBindList = decodeVarBindList(berDecoder);
      berDecoder.closeSequence();
    } catch (BerException berException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpMessage.class.getName(), "decodeSnmpPdu", "BerException", berException); 
      throw new SnmpStatusException(9);
    } catch (IllegalArgumentException illegalArgumentException) {
      if (JmxProperties.SNMP_LOGGER.isLoggable(Level.FINEST))
        JmxProperties.SNMP_LOGGER.logp(Level.FINEST, SnmpMessage.class.getName(), "decodeSnmpPdu", "IllegalArgumentException", illegalArgumentException); 
      throw new SnmpStatusException(9);
    } 
    snmpPduTrap.version = this.version;
    snmpPduTrap.community = this.community;
    snmpPduTrap.address = this.address;
    snmpPduTrap.port = this.port;
    return snmpPduTrap;
  }
  
  public String printMessage() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.community == null) {
      stringBuffer.append("Community: null");
    } else {
      stringBuffer.append("Community: {\n");
      stringBuffer.append(dumpHexBuffer(this.community, 0, this.community.length));
      stringBuffer.append("\n}\n");
    } 
    return stringBuffer.append(super.printMessage()).toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */