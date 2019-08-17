package sun.security.acl;

import java.security.acl.Permission;

public class AllPermissionsImpl extends PermissionImpl {
  public AllPermissionsImpl(String paramString) { super(paramString); }
  
  public boolean equals(Permission paramPermission) { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\acl\AllPermissionsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */