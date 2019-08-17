package sun.security.util;

import java.security.Permission;

public interface PermissionFactory<T extends Permission> {
  T newPermission(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\PermissionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */