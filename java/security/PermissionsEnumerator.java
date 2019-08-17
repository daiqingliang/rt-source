package java.security;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class PermissionsEnumerator extends Object implements Enumeration<Permission> {
  private Iterator<PermissionCollection> perms;
  
  private Enumeration<Permission> permset;
  
  PermissionsEnumerator(Iterator<PermissionCollection> paramIterator) {
    this.perms = paramIterator;
    this.permset = getNextEnumWithMore();
  }
  
  public boolean hasMoreElements() {
    if (this.permset == null)
      return false; 
    if (this.permset.hasMoreElements())
      return true; 
    this.permset = getNextEnumWithMore();
    return (this.permset != null);
  }
  
  public Permission nextElement() {
    if (hasMoreElements())
      return (Permission)this.permset.nextElement(); 
    throw new NoSuchElementException("PermissionsEnumerator");
  }
  
  private Enumeration<Permission> getNextEnumWithMore() {
    while (this.perms.hasNext()) {
      PermissionCollection permissionCollection = (PermissionCollection)this.perms.next();
      Enumeration enumeration = permissionCollection.elements();
      if (enumeration.hasMoreElements())
        return enumeration; 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\PermissionsEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */