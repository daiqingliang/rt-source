package com.sun.jmx.snmp.IPAcl;

import java.io.Serializable;
import java.security.acl.Permission;

class PermissionImpl implements Permission, Serializable {
  private static final long serialVersionUID = 4478110422746916589L;
  
  private String perm = null;
  
  public PermissionImpl(String paramString) { this.perm = paramString; }
  
  public int hashCode() { return super.hashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof PermissionImpl) ? this.perm.equals(((PermissionImpl)paramObject).getString()) : 0; }
  
  public String toString() { return this.perm; }
  
  public String getString() { return this.perm; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\IPAcl\PermissionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */