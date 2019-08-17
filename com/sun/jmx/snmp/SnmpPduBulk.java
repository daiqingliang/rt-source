package com.sun.jmx.snmp;

public class SnmpPduBulk extends SnmpPduPacket implements SnmpPduBulkType {
  private static final long serialVersionUID = -7431306775883371046L;
  
  public int nonRepeaters;
  
  public int maxRepetitions;
  
  public void setMaxRepetitions(int paramInt) { this.maxRepetitions = paramInt; }
  
  public void setNonRepeaters(int paramInt) { this.nonRepeaters = paramInt; }
  
  public int getMaxRepetitions() { return this.maxRepetitions; }
  
  public int getNonRepeaters() { return this.nonRepeaters; }
  
  public SnmpPdu getResponsePdu() {
    SnmpPduRequest snmpPduRequest = new SnmpPduRequest();
    snmpPduRequest.address = this.address;
    snmpPduRequest.port = this.port;
    snmpPduRequest.version = this.version;
    snmpPduRequest.community = this.community;
    snmpPduRequest.type = 162;
    snmpPduRequest.requestId = this.requestId;
    snmpPduRequest.errorStatus = 0;
    snmpPduRequest.errorIndex = 0;
    return snmpPduRequest;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPduBulk.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */