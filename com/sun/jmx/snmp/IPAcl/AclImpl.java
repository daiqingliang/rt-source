package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

class AclImpl extends OwnerImpl implements Acl, Serializable {
  private static final long serialVersionUID = -2250957591085270029L;
  
  private Vector<AclEntry> entryList = null;
  
  private String aclName = null;
  
  public AclImpl(PrincipalImpl paramPrincipalImpl, String paramString) {
    super(paramPrincipalImpl);
    this.entryList = new Vector();
    this.aclName = paramString;
  }
  
  public void setName(Principal paramPrincipal, String paramString) throws NotOwnerException {
    if (!isOwner(paramPrincipal))
      throw new NotOwnerException(); 
    this.aclName = paramString;
  }
  
  public String getName() { return this.aclName; }
  
  public boolean addEntry(Principal paramPrincipal, AclEntry paramAclEntry) throws NotOwnerException {
    if (!isOwner(paramPrincipal))
      throw new NotOwnerException(); 
    if (this.entryList.contains(paramAclEntry))
      return false; 
    this.entryList.addElement(paramAclEntry);
    return true;
  }
  
  public boolean removeEntry(Principal paramPrincipal, AclEntry paramAclEntry) throws NotOwnerException {
    if (!isOwner(paramPrincipal))
      throw new NotOwnerException(); 
    return this.entryList.removeElement(paramAclEntry);
  }
  
  public void removeAll(Principal paramPrincipal) throws NotOwnerException {
    if (!isOwner(paramPrincipal))
      throw new NotOwnerException(); 
    this.entryList.removeAllElements();
  }
  
  public Enumeration<Permission> getPermissions(Principal paramPrincipal) {
    Vector vector = new Vector();
    Enumeration enumeration = this.entryList.elements();
    while (enumeration.hasMoreElements()) {
      AclEntry aclEntry = (AclEntry)enumeration.nextElement();
      if (aclEntry.getPrincipal().equals(paramPrincipal))
        return aclEntry.permissions(); 
    } 
    return vector.elements();
  }
  
  public Enumeration<AclEntry> entries() { return this.entryList.elements(); }
  
  public boolean checkPermission(Principal paramPrincipal, Permission paramPermission) {
    Enumeration enumeration = this.entryList.elements();
    while (enumeration.hasMoreElements()) {
      AclEntry aclEntry = (AclEntry)enumeration.nextElement();
      if (aclEntry.getPrincipal().equals(paramPrincipal) && aclEntry.checkPermission(paramPermission))
        return true; 
    } 
    return false;
  }
  
  public boolean checkPermission(Principal paramPrincipal, String paramString, Permission paramPermission) {
    Enumeration enumeration = this.entryList.elements();
    while (enumeration.hasMoreElements()) {
      AclEntryImpl aclEntryImpl = (AclEntryImpl)enumeration.nextElement();
      if (aclEntryImpl.getPrincipal().equals(paramPrincipal) && aclEntryImpl.checkPermission(paramPermission) && aclEntryImpl.checkCommunity(paramString))
        return true; 
    } 
    return false;
  }
  
  public boolean checkCommunity(String paramString) {
    Enumeration enumeration = this.entryList.elements();
    while (enumeration.hasMoreElements()) {
      AclEntryImpl aclEntryImpl = (AclEntryImpl)enumeration.nextElement();
      if (aclEntryImpl.checkCommunity(paramString))
        return true; 
    } 
    return false;
  }
  
  public String toString() { return "AclImpl: " + getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\AclImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */