package com.sun.jmx.snmp;

public class SnmpScopedPduRequest extends SnmpScopedPduPacket implements SnmpPduRequestType {
  private static final long serialVersionUID = 6463060973056773680L;
  
  int errorStatus = 0;
  
  int errorIndex = 0;
  
  public void setErrorIndex(int paramInt) { this.errorIndex = paramInt; }
  
  public void setErrorStatus(int paramInt) { this.errorStatus = paramInt; }
  
  public int getErrorIndex() { return this.errorIndex; }
  
  public int getErrorStatus() { return this.errorStatus; }
  
  public SnmpPdu getResponsePdu() {
    SnmpScopedPduRequest snmpScopedPduRequest = new SnmpScopedPduRequest();
    snmpScopedPduRequest.address = this.address;
    snmpScopedPduRequest.port = this.port;
    snmpScopedPduRequest.version = this.version;
    snmpScopedPduRequest.requestId = this.requestId;
    snmpScopedPduRequest.msgId = this.msgId;
    snmpScopedPduRequest.msgMaxSize = this.msgMaxSize;
    snmpScopedPduRequest.msgFlags = this.msgFlags;
    snmpScopedPduRequest.msgSecurityModel = this.msgSecurityModel;
    snmpScopedPduRequest.contextEngineId = this.contextEngineId;
    snmpScopedPduRequest.contextName = this.contextName;
    snmpScopedPduRequest.securityParameters = this.securityParameters;
    snmpScopedPduRequest.type = 162;
    snmpScopedPduRequest.errorStatus = 0;
    snmpScopedPduRequest.errorIndex = 0;
    return snmpScopedPduRequest;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpScopedPduRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */