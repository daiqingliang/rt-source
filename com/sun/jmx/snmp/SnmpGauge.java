package com.sun.jmx.snmp;

public class SnmpGauge extends SnmpUnsignedInt {
  private static final long serialVersionUID = -8366622742122792945L;
  
  static final String name = "Gauge32";
  
  public SnmpGauge(int paramInt) throws IllegalArgumentException { super(paramInt); }
  
  public SnmpGauge(Integer paramInteger) throws IllegalArgumentException { super(paramInteger); }
  
  public SnmpGauge(long paramLong) throws IllegalArgumentException { super(paramLong); }
  
  public SnmpGauge(Long paramLong) throws IllegalArgumentException { super(paramLong); }
  
  public final String getTypeName() { return "Gauge32"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\SnmpGauge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */