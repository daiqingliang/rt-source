package com.sun.jmx.snmp.IPAcl;

class JDMTrapItem extends SimpleNode {
  protected JDMTrapCommunity comm = null;
  
  JDMTrapItem(int paramInt) { super(paramInt); }
  
  JDMTrapItem(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMTrapItem(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMTrapItem(paramParser, paramInt); }
  
  public JDMTrapCommunity getCommunity() { return this.comm; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMTrapItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */