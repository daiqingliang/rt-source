package sun.security.provider;

import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.util.Enumeration;
import java.util.Vector;

class PolicyPermissions extends PermissionCollection {
  private static final long serialVersionUID = -1954188373270545523L;
  
  private CodeSource codesource;
  
  private Permissions perms;
  
  private AuthPolicyFile policy;
  
  private boolean notInit;
  
  private Vector<Permission> additionalPerms;
  
  PolicyPermissions(AuthPolicyFile paramAuthPolicyFile, CodeSource paramCodeSource) {
    this.codesource = paramCodeSource;
    this.policy = paramAuthPolicyFile;
    this.perms = null;
    this.notInit = true;
    this.additionalPerms = null;
  }
  
  public void add(Permission paramPermission) {
    if (isReadOnly())
      throw new SecurityException(AuthPolicyFile.rb.getString("attempt.to.add.a.Permission.to.a.readonly.PermissionCollection")); 
    if (this.perms == null) {
      if (this.additionalPerms == null)
        this.additionalPerms = new Vector(); 
      this.additionalPerms.add(paramPermission);
    } else {
      this.perms.add(paramPermission);
    } 
  }
  
  private void init() {
    if (this.notInit) {
      if (this.perms == null)
        this.perms = new Permissions(); 
      if (this.additionalPerms != null) {
        Enumeration enumeration = this.additionalPerms.elements();
        while (enumeration.hasMoreElements())
          this.perms.add((Permission)enumeration.nextElement()); 
        this.additionalPerms = null;
      } 
      this.policy.getPermissions(this.perms, this.codesource);
      this.notInit = false;
    } 
  }
  
  public boolean implies(Permission paramPermission) {
    if (this.notInit)
      init(); 
    return this.perms.implies(paramPermission);
  }
  
  public Enumeration<Permission> elements() {
    if (this.notInit)
      init(); 
    return this.perms.elements();
  }
  
  public String toString() {
    if (this.notInit)
      init(); 
    return this.perms.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\PolicyPermissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */