package com.sun.jmx.snmp;

public class SnmpNull extends SnmpValue {
  private static final long serialVersionUID = 1783782515994279177L;
  
  static final String name = "Null";
  
  private int tag = 5;
  
  public SnmpNull() { this.tag = 5; }
  
  public SnmpNull(String paramString) { this(); }
  
  public SnmpNull(int paramInt) { this.tag = paramInt; }
  
  public int getTag() { return this.tag; }
  
  public String toString() {
    String str = "";
    if (this.tag != 5)
      str = str + "[" + this.tag + "] "; 
    str = str + "NULL";
    switch (this.tag) {
      case 128:
        str = str + " (noSuchObject)";
        break;
      case 129:
        str = str + " (noSuchInstance)";
        break;
      case 130:
        str = str + " (endOfMibView)";
        break;
    } 
    return str;
  }
  
  public SnmpOid toOid() { throw new IllegalArgumentException(); }
  
  public final SnmpValue duplicate() { return (SnmpValue)clone(); }
  
  public final Object clone() {
    SnmpNull snmpNull = null;
    try {
      snmpNull = (SnmpNull)super.clone();
      snmpNull.tag = this.tag;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    return snmpNull;
  }
  
  public final String getTypeName() { return "Null"; }
  
  public boolean isNoSuchObjectValue() { return (this.tag == 128); }
  
  public boolean isNoSuchInstanceValue() { return (this.tag == 129); }
  
  public boolean isEndOfMibViewValue() { return (this.tag == 130); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpNull.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */