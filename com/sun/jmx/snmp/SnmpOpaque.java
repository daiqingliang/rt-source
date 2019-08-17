package com.sun.jmx.snmp;

public class SnmpOpaque extends SnmpString {
  private static final long serialVersionUID = 380952213936036664L;
  
  static final String name = "Opaque";
  
  public SnmpOpaque(byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public SnmpOpaque(Byte[] paramArrayOfByte) { super(paramArrayOfByte); }
  
  public SnmpOpaque(String paramString) { super(paramString); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.value.length; b++) {
      byte b1 = this.value[b];
      byte b2 = (b1 >= 0) ? b1 : (b1 + 256);
      stringBuffer.append(Character.forDigit(b2 / 16, 16));
      stringBuffer.append(Character.forDigit(b2 % 16, 16));
    } 
    return stringBuffer.toString();
  }
  
  public final String getTypeName() { return "Opaque"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpOpaque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */