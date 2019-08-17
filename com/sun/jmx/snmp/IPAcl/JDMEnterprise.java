package com.sun.jmx.snmp.IPAcl;

class JDMEnterprise extends SimpleNode {
  protected String enterprise = "";
  
  JDMEnterprise(int paramInt) { super(paramInt); }
  
  JDMEnterprise(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMEnterprise(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMEnterprise(paramParser, paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMEnterprise.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */