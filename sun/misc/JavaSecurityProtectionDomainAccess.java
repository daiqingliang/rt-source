package sun.misc;

import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public interface JavaSecurityProtectionDomainAccess {
  ProtectionDomainCache getProtectionDomainCache();
  
  boolean getStaticPermissionsField(ProtectionDomain paramProtectionDomain);
  
  public static interface ProtectionDomainCache {
    void put(ProtectionDomain param1ProtectionDomain, PermissionCollection param1PermissionCollection);
    
    PermissionCollection get(ProtectionDomain param1ProtectionDomain);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\JavaSecurityProtectionDomainAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */