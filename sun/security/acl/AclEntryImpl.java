package sun.security.acl;

import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

public class AclEntryImpl implements AclEntry {
  private Principal user = null;
  
  private Vector<Permission> permissionSet = new Vector(10, 10);
  
  private boolean negative = false;
  
  public AclEntryImpl(Principal paramPrincipal) { this.user = paramPrincipal; }
  
  public AclEntryImpl() {}
  
  public boolean setPrincipal(Principal paramPrincipal) {
    if (this.user != null)
      return false; 
    this.user = paramPrincipal;
    return true;
  }
  
  public void setNegativePermissions() { this.negative = true; }
  
  public boolean isNegative() { return this.negative; }
  
  public boolean addPermission(Permission paramPermission) {
    if (this.permissionSet.contains(paramPermission))
      return false; 
    this.permissionSet.addElement(paramPermission);
    return true;
  }
  
  public boolean removePermission(Permission paramPermission) { return this.permissionSet.removeElement(paramPermission); }
  
  public boolean checkPermission(Permission paramPermission) { return this.permissionSet.contains(paramPermission); }
  
  public Enumeration<Permission> permissions() { return this.permissionSet.elements(); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.negative) {
      stringBuffer.append("-");
    } else {
      stringBuffer.append("+");
    } 
    if (this.user instanceof java.security.acl.Group) {
      stringBuffer.append("Group.");
    } else {
      stringBuffer.append("User.");
    } 
    stringBuffer.append(this.user + "=");
    Enumeration enumeration = permissions();
    while (enumeration.hasMoreElements()) {
      Permission permission = (Permission)enumeration.nextElement();
      stringBuffer.append(permission);
      if (enumeration.hasMoreElements())
        stringBuffer.append(","); 
    } 
    return new String(stringBuffer);
  }
  
  public Object clone() {
    AclEntryImpl aclEntryImpl = new AclEntryImpl(this.user);
    aclEntryImpl.permissionSet = (Vector)this.permissionSet.clone();
    aclEntryImpl.negative = this.negative;
    return aclEntryImpl;
  }
  
  public Principal getPrincipal() { return this.user; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\AclEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */