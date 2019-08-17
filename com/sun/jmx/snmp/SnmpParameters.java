package com.sun.jmx.snmp;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

public class SnmpParameters extends SnmpParams implements Cloneable, Serializable {
  private static final long serialVersionUID = -1822462497931733790L;
  
  static final String defaultRdCommunity = "public";
  
  private int _protocolVersion = 0;
  
  private String _readCommunity = "public";
  
  private String _writeCommunity;
  
  private String _informCommunity;
  
  public SnmpParameters() { this._informCommunity = "public"; }
  
  public SnmpParameters(String paramString1, String paramString2) {
    this._writeCommunity = paramString2;
    this._informCommunity = "public";
  }
  
  public SnmpParameters(String paramString1, String paramString2, String paramString3) {
    this._writeCommunity = paramString2;
    this._informCommunity = paramString3;
  }
  
  public String getRdCommunity() { return this._readCommunity; }
  
  public void setRdCommunity(String paramString) {
    if (paramString == null) {
      this._readCommunity = "public";
    } else {
      this._readCommunity = paramString;
    } 
  }
  
  public String getWrCommunity() { return this._writeCommunity; }
  
  public void setWrCommunity(String paramString) { this._writeCommunity = paramString; }
  
  public String getInformCommunity() { return this._informCommunity; }
  
  public void setInformCommunity(String paramString) {
    if (paramString == null) {
      this._informCommunity = "public";
    } else {
      this._informCommunity = paramString;
    } 
  }
  
  public boolean allowSnmpSets() { return (this._writeCommunity != null); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof SnmpParameters))
      return false; 
    if (this == paramObject)
      return true; 
    SnmpParameters snmpParameters = (SnmpParameters)paramObject;
    return (this._protocolVersion == snmpParameters._protocolVersion && this._readCommunity.equals(snmpParameters._readCommunity));
  }
  
  public int hashCode() { return this._protocolVersion * 31 ^ Objects.hashCode(this._readCommunity); }
  
  public Object clone() {
    SnmpParameters snmpParameters = null;
    try {
      snmpParameters = (SnmpParameters)super.clone();
      snmpParameters._readCommunity = this._readCommunity;
      snmpParameters._writeCommunity = this._writeCommunity;
      snmpParameters._informCommunity = this._informCommunity;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
    return snmpParameters;
  }
  
  public byte[] encodeAuthentication(int paramInt) throws SnmpStatusException {
    try {
      return (paramInt == 163) ? this._writeCommunity.getBytes("8859_1") : ((paramInt == 166) ? this._informCommunity.getBytes("8859_1") : this._readCommunity.getBytes("8859_1"));
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new SnmpStatusException(unsupportedEncodingException.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */