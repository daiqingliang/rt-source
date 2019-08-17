package com.sun.jmx.snmp.IPAcl;

class JDMCommunities extends SimpleNode {
  JDMCommunities(int paramInt) { super(paramInt); }
  
  JDMCommunities(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMCommunities(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMCommunities(paramParser, paramInt); }
  
  public void buildCommunities(AclEntryImpl paramAclEntryImpl) {
    for (byte b = 0; b < this.children.length; b++)
      paramAclEntryImpl.addCommunity(((JDMCommunity)this.children[b]).getCommunity()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMCommunities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */