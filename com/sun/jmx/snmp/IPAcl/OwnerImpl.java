package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.acl.Owner;
import java.util.Vector;

class OwnerImpl implements Owner, Serializable {
  private static final long serialVersionUID = -576066072046319874L;
  
  private Vector<Principal> ownerList = null;
  
  public OwnerImpl() { this.ownerList = new Vector(); }
  
  public OwnerImpl(PrincipalImpl paramPrincipalImpl) {
    this.ownerList = new Vector();
    this.ownerList.addElement(paramPrincipalImpl);
  }
  
  public boolean addOwner(Principal paramPrincipal1, Principal paramPrincipal2) throws NotOwnerException {
    if (!this.ownerList.contains(paramPrincipal1))
      throw new NotOwnerException(); 
    if (this.ownerList.contains(paramPrincipal2))
      return false; 
    this.ownerList.addElement(paramPrincipal2);
    return true;
  }
  
  public boolean deleteOwner(Principal paramPrincipal1, Principal paramPrincipal2) throws NotOwnerException {
    if (!this.ownerList.contains(paramPrincipal1))
      throw new NotOwnerException(); 
    if (!this.ownerList.contains(paramPrincipal2))
      return false; 
    if (this.ownerList.size() == 1)
      throw new LastOwnerException(); 
    this.ownerList.removeElement(paramPrincipal2);
    return true;
  }
  
  public boolean isOwner(Principal paramPrincipal) { return this.ownerList.contains(paramPrincipal); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\OwnerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */