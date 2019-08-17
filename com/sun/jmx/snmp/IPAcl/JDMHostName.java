package com.sun.jmx.snmp.IPAcl;

import java.net.UnknownHostException;

class JDMHostName extends Host {
  private static final long serialVersionUID = -9120082068923591122L;
  
  protected StringBuffer name = new StringBuffer();
  
  JDMHostName(int paramInt) { super(paramInt); }
  
  JDMHostName(Parser paramParser, int paramInt) { super(paramParser, paramInt); }
  
  public static Node jjtCreate(int paramInt) { return new JDMHostName(paramInt); }
  
  public static Node jjtCreate(Parser paramParser, int paramInt) { return new JDMHostName(paramParser, paramInt); }
  
  protected String getHname() { return this.name.toString(); }
  
  protected PrincipalImpl createAssociatedPrincipal() throws UnknownHostException { return new PrincipalImpl(this.name.toString()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\JDMHostName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */