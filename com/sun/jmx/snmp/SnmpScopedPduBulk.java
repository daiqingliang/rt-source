package com.sun.jmx.snmp;

public class SnmpScopedPduBulk extends SnmpScopedPduPacket implements SnmpPduBulkType {
  private static final long serialVersionUID = -1648623646227038885L;
  
  int nonRepeaters;
  
  int maxRepetitions;
  
  public void setMaxRepetitions(int paramInt) { this.maxRepetitions = paramInt; }
  
  public void setNonRepeaters(int paramInt) { this.nonRepeaters = paramInt; }
  
  public int getMaxRepetitions() { return this.maxRepetitions; }
  
  public int getNonRepeaters() { return this.nonRepeaters; }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpScopedPduBulk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */