package javax.management;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

class MBeanServerPermissionCollection extends PermissionCollection {
  private MBeanServerPermission collectionPermission;
  
  private static final long serialVersionUID = -5661980843569388590L;
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof MBeanServerPermission)) {
      String str = "Permission not an MBeanServerPermission: " + paramPermission;
      throw new IllegalArgumentException(str);
    } 
    if (isReadOnly())
      throw new SecurityException("Read-only permission collection"); 
    MBeanServerPermission mBeanServerPermission = (MBeanServerPermission)paramPermission;
    if (this.collectionPermission == null) {
      this.collectionPermission = mBeanServerPermission;
    } else if (!this.collectionPermission.implies(paramPermission)) {
      int i = this.collectionPermission.mask | mBeanServerPermission.mask;
      this.collectionPermission = new MBeanServerPermission(i);
    } 
  }
  
  public boolean implies(Permission paramPermission) { return (this.collectionPermission != null && this.collectionPermission.implies(paramPermission)); }
  
  public Enumeration<Permission> elements() {
    Set set;
    if (this.collectionPermission == null) {
      set = Collections.emptySet();
    } else {
      set = Collections.singleton(this.collectionPermission);
    } 
    return Collections.enumeration(set);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServerPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */