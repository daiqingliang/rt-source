package com.sun.jmx.snmp.IPAcl;

class JDMTrapNum extends SimpleNode {
  protected int low = 0;
  
  protected int high = 0;
  
  JDMTrapNum(int paramInt) { super(paramInt); }
  
  JDMTrapNum(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMTrapNum(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMTrapNum(paramParser, paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMTrapNum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */