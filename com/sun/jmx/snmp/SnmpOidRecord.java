package com.sun.jmx.snmp;

public class SnmpOidRecord {
  private String name;
  
  private String oid;
  
  private String type;
  
  public SnmpOidRecord(String paramString1, String paramString2, String paramString3) {
    this.name = paramString1;
    this.oid = paramString2;
    this.type = paramString3;
  }
  
  public String getName() { return this.name; }
  
  public String getOid() { return this.oid; }
  
  public String getType() { return this.type; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpOidRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */