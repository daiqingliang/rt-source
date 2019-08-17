package com.sun.jmx.snmp;

public class SnmpCounter extends SnmpUnsignedInt {
  private static final long serialVersionUID = 4655264728839396879L;
  
  static final String name = "Counter32";
  
  public SnmpCounter(int paramInt) throws IllegalArgumentException { super(paramInt); }
  
  public SnmpCounter(Integer paramInteger) throws IllegalArgumentException { super(paramInteger); }
  
  public SnmpCounter(long paramLong) throws IllegalArgumentException { super(paramLong); }
  
  public SnmpCounter(Long paramLong) throws IllegalArgumentException { super(paramLong); }
  
  public final String getTypeName() { return "Counter32"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */