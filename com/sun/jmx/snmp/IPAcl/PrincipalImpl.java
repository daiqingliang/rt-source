package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;

class PrincipalImpl implements Principal, Serializable {
  private static final long serialVersionUID = -7910027842878976761L;
  
  private InetAddress[] add = null;
  
  public PrincipalImpl() throws UnknownHostException {
    this.add = new InetAddress[1];
    this.add[0] = InetAddress.getLocalHost();
  }
  
  public PrincipalImpl(String paramString) throws UnknownHostException {
    if (paramString.equals("localhost") || paramString.equals("127.0.0.1")) {
      this.add = new InetAddress[1];
      this.add[0] = InetAddress.getByName(paramString);
    } else {
      this.add = InetAddress.getAllByName(paramString);
    } 
  }
  
  public PrincipalImpl(InetAddress paramInetAddress) {
    this.add = new InetAddress[1];
    this.add[0] = paramInetAddress;
  }
  
  public String getName() { return this.add[0].toString(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof PrincipalImpl) {
      for (byte b = 0; b < this.add.length; b++) {
        if (this.add[b].equals(((PrincipalImpl)paramObject).getAddress()))
          return true; 
      } 
      return false;
    } 
    return false;
  }
  
  public int hashCode() { return this.add[0].hashCode(); }
  
  public String toString() { return "PrincipalImpl :" + this.add[0].toString(); }
  
  public InetAddress getAddress() { return this.add[0]; }
  
  public InetAddress[] getAddresses() { return this.add; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\PrincipalImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */