package com.sun.jmx.snmp;

public class SnmpPduRequest extends SnmpPduPacket implements SnmpPduRequestType {
  private static final long serialVersionUID = 2218754017025258979L;
  
  public int errorStatus = 0;
  
  public int errorIndex = 0;
  
  public void setErrorIndex(int paramInt) { this.errorIndex = paramInt; }
  
  public void setErrorStatus(int paramInt) { this.errorStatus = paramInt; }
  
  public int getErrorIndex() { return this.errorIndex; }
  
  public int getErrorStatus() { return this.errorStatus; }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpPduRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */