package sun.misc;

import java.security.AccessControlContext;
import java.security.PrivilegedAction;

public interface JavaSecurityAccess {
  <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2);
  
  <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\JavaSecurityAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */