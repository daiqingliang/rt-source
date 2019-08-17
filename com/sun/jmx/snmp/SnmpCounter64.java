package com.sun.jmx.snmp;

public class SnmpCounter64 extends SnmpValue {
  private static final long serialVersionUID = 8784850650494679937L;
  
  static final String name = "Counter64";
  
  private long value = 0L;
  
  public SnmpCounter64(long paramLong) throws IllegalArgumentException {
    if (paramLong < 0L || paramLong > Float.MAX_VALUE)
      throw new IllegalArgumentException(); 
    this.value = paramLong;
  }
  
  public SnmpCounter64(Long paramLong) throws IllegalArgumentException { this(paramLong.longValue()); }
  
  public long longValue() { return this.value; }
  
  public Long toLong() { return new Long(this.value); }
  
  public int intValue() { return (int)this.value; }
  
  public Integer toInteger() { return new Integer((int)this.value); }
  
  public String toString() { return String.valueOf(this.value); }
  
  public SnmpOid toOid() { return new SnmpOid(this.value); }
  
  public static SnmpOid toOid(long[] paramArrayOfLong, int paramInt) throws SnmpStatusException {
    try {
      return new SnmpOid(paramArrayOfLong[paramInt]);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new SnmpStatusException(2);
    } 
  }
  
  public static int nextOid(long[] paramArrayOfLong, int paramInt) throws SnmpStatusException {
    if (paramInt >= paramArrayOfLong.length)
      throw new SnmpStatusException(2); 
    return paramInt + 1;
  }
  
  public static void appendToOid(SnmpOid paramSnmpOid1, SnmpOid paramSnmpOid2) {
    if (paramSnmpOid1.getLength() != 1)
      throw new IllegalArgumentException(); 
    paramSnmpOid2.append(paramSnmpOid1);
  }
  
  public final SnmpValue duplicate() { return (SnmpValue)clone(); }
  
  public final Object clone() {
    SnmpCounter64 snmpCounter64 = null;
    try {
      snmpCounter64 = (SnmpCounter64)super.clone();
      snmpCounter64.value = this.value;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    return snmpCounter64;
  }
  
  public final String getTypeName() { return "Counter64"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpCounter64.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */