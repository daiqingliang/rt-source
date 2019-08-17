package com.sun.jmx.snmp.IPAcl;

class JDMInformCommunity extends SimpleNode {
  protected String community = "";
  
  JDMInformCommunity(int paramInt) { super(paramInt); }
  
  JDMInformCommunity(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMInformCommunity(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMInformCommunity(paramParser, paramInt); }
  
  public String getCommunity() { return this.community; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMInformCommunity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */