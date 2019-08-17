package com.sun.jmx.snmp.IPAcl;

class JDMAclItem extends SimpleNode {
  protected JDMAccess access = null;
  
  protected JDMCommunities com = null;
  
  JDMAclItem(int paramInt) { super(paramInt); }
  
  JDMAclItem(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMAclItem(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMAclItem(paramParser, paramInt); }
  
  public JDMAccess getAccess() { return this.access; }
  
  public JDMCommunities getCommunities() { return this.com; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMAclItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */