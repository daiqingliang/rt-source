package sun.awt;

import java.awt.AWTPermission;
import java.security.Permission;
import sun.security.util.PermissionFactory;

public class AWTPermissionFactory extends Object implements PermissionFactory<AWTPermission> {
  public AWTPermission newPermission(String paramString) { return new AWTPermission(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\AWTPermissionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */