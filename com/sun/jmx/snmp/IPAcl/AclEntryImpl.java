package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

class AclEntryImpl implements AclEntry, Serializable {
  private static final long serialVersionUID = -5047185131260073216L;
  
  private Principal princ = null;
  
  private boolean neg = false;
  
  private Vector<Permission> permList = null;
  
  private Vector<String> commList = null;
  
  private AclEntryImpl(AclEntryImpl paramAclEntryImpl) throws UnknownHostException {
    setPrincipal(paramAclEntryImpl.getPrincipal());
    this.permList = new Vector();
    this.commList = new Vector();
    Enumeration enumeration = paramAclEntryImpl.communities();
    while (enumeration.hasMoreElements())
      addCommunity((String)enumeration.nextElement()); 
    enumeration = paramAclEntryImpl.permissions();
    while (enumeration.hasMoreElements())
      addPermission((Permission)enumeration.nextElement()); 
    if (paramAclEntryImpl.isNegative())
      setNegativePermissions(); 
  }
  
  public AclEntryImpl() {
    this.princ = null;
    this.permList = new Vector();
    this.commList = new Vector();
  }
  
  public AclEntryImpl(Principal paramPrincipal) throws UnknownHostException {
    this.princ = paramPrincipal;
    this.permList = new Vector();
    this.commList = new Vector();
  }
  
  public Object clone() {
    Object object;
    try {
      object = new AclEntryImpl(this);
    } catch (UnknownHostException unknownHostException) {
      object = null;
    } 
    return object;
  }
  
  public boolean isNegative() { return this.neg; }
  
  public boolean addPermission(Permission paramPermission) {
    if (this.permList.contains(paramPermission))
      return false; 
    this.permList.addElement(paramPermission);
    return true;
  }
  
  public boolean removePermission(Permission paramPermission) {
    if (!this.permList.contains(paramPermission))
      return false; 
    this.permList.removeElement(paramPermission);
    return true;
  }
  
  public boolean checkPermission(Permission paramPermission) { return this.permList.contains(paramPermission); }
  
  public Enumeration<Permission> permissions() { return this.permList.elements(); }
  
  public void setNegativePermissions() { this.neg = true; }
  
  public Principal getPrincipal() { return this.princ; }
  
  public boolean setPrincipal(Principal paramPrincipal) {
    if (this.princ != null)
      return false; 
    this.princ = paramPrincipal;
    return true;
  }
  
  public String toString() { return "AclEntry:" + this.princ.toString(); }
  
  public Enumeration<String> communities() { return this.commList.elements(); }
  
  public boolean addCommunity(String paramString) {
    if (this.commList.contains(paramString))
      return false; 
    this.commList.addElement(paramString);
    return true;
  }
  
  public boolean removeCommunity(String paramString) {
    if (!this.commList.contains(paramString))
      return false; 
    this.commList.removeElement(paramString);
    return true;
  }
  
  public boolean checkCommunity(String paramString) { return this.commList.contains(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\AclEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */