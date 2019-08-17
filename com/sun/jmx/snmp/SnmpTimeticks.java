package com.sun.jmx.snmp;

public class SnmpTimeticks extends SnmpUnsignedInt {
  static final String name = "TimeTicks";
  
  private static final long serialVersionUID = -5486435222360030630L;
  
  public SnmpTimeticks(int paramInt) throws IllegalArgumentException { super(paramInt); }
  
  public SnmpTimeticks(Integer paramInteger) throws IllegalArgumentException { super(paramInteger); }
  
  public SnmpTimeticks(long paramLong) throws IllegalArgumentException { super((paramLong > 0L) ? (paramLong & 0xFFFFFFFFL) : paramLong); }
  
  public SnmpTimeticks(Long paramLong) throws IllegalArgumentException { this(paramLong.longValue()); }
  
  public static final String printTimeTicks(long paramLong) {
    StringBuffer stringBuffer = new StringBuffer();
    paramLong /= 100L;
    int m = (int)(paramLong / 86400L);
    paramLong %= 86400L;
    int k = (int)(paramLong / 3600L);
    paramLong %= 3600L;
    int j = (int)(paramLong / 60L);
    int i = (int)(paramLong % 60L);
    if (m == 0) {
      stringBuffer.append(k + ":" + j + ":" + i);
      return stringBuffer.toString();
    } 
    if (m == 1) {
      stringBuffer.append("1 day ");
    } else {
      stringBuffer.append(m + " days ");
    } 
    stringBuffer.append(k + ":" + j + ":" + i);
    return stringBuffer.toString();
  }
  
  public final String toString() { return printTimeTicks(this.value); }
  
  public final String getTypeName() { return "TimeTicks"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpTimeticks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */