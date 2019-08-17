package com.sun.jmx.snmp.IPAcl;

class JDMHost extends SimpleNode {
  JDMHost(int paramInt) { super(paramInt); }
  
  JDMHost(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMHost(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMHost(paramParser, paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMHost.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */