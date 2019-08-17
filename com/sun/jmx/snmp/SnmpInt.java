package com.sun.jmx.snmp;

public class SnmpInt extends SnmpValue {
  private static final long serialVersionUID = -7163624758070343373L;
  
  static final String name = "Integer32";
  
  protected long value = 0L;
  
  public SnmpInt(int paramInt) throws IllegalArgumentException {
    if (!isInitValueValid(paramInt))
      throw new IllegalArgumentException(); 
    this.value = paramInt;
  }
  
  public SnmpInt(Integer paramInteger) throws IllegalArgumentException { this(paramInteger.intValue()); }
  
  public SnmpInt(long paramLong) throws IllegalArgumentException {
    if (!isInitValueValid(paramLong))
      throw new IllegalArgumentException(); 
    this.value = paramLong;
  }
  
  public SnmpInt(Long paramLong) throws IllegalArgumentException { this(paramLong.longValue()); }
  
  public SnmpInt(Enumerated paramEnumerated) throws IllegalArgumentException { this(paramEnumerated.intValue()); }
  
  public SnmpInt(boolean paramBoolean) { this.value = paramBoolean ? 1L : 2L; }
  
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
    SnmpInt snmpInt = null;
    try {
      snmpInt = (SnmpInt)super.clone();
      snmpInt.value = this.value;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    return snmpInt;
  }
  
  public String getTypeName() { return "Integer32"; }
  
  boolean isInitValueValid(int paramInt) { return !(paramInt < Integer.MIN_VALUE || paramInt > Integer.MAX_VALUE); }
  
  boolean isInitValueValid(long paramLong) { return !(paramLong < -2147483648L || paramLong > 2147483647L); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */