package com.sun.jmx.snmp.IPAcl;

class JDMTrapCommunity extends SimpleNode {
  protected String community = "";
  
  JDMTrapCommunity(int paramInt) { super(paramInt); }
  
  JDMTrapCommunity(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMTrapCommunity(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMTrapCommunity(paramParser, paramInt); }
  
  public String getCommunity() { return this.community; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMTrapCommunity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */