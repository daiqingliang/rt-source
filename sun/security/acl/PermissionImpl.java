package sun.security.acl;

import java.security.acl.Permission;

public class PermissionImpl implements Permission {
  private String permission;
  
  public PermissionImpl(String paramString) { this.permission = paramString; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Permission) {
      Permission permission1 = (Permission)paramObject;
      return this.permission.equals(permission1.toString());
    } 
    return false;
  }
  
  public String toString() { return this.permission; }
  
  public int hashCode() { return toString().hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\PermissionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */