package java.security;

import java.io.Serializable;
import java.util.Enumeration;
import sun.security.util.SecurityConstants;

final class AllPermissionCollection extends PermissionCollection implements Serializable {
  private static final long serialVersionUID = -4023755556366636806L;
  
  private boolean all_allowed = false;
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof AllPermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    this.all_allowed = true;
  }
  
  public boolean implies(Permission paramPermission) { return this.all_allowed; }
  
  public Enumeration<Permission> elements() { return new Enumeration<Permission>() {
        private boolean hasMore = AllPermissionCollection.this.all_allowed;
        
        public boolean hasMoreElements() { return this.hasMore; }
        
        public Permission nextElement() {
          this.hasMore = false;
          return SecurityConstants.ALL_PERMISSION;
        }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\AllPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */