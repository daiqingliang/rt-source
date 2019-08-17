package sun.management;

import java.lang.management.ManagementPermission;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private static ManagementPermission monitorPermission = new ManagementPermission("monitor");
  
  private static ManagementPermission controlPermission = new ManagementPermission("control");
  
  static RuntimeException newException(Exception paramException) { throw new RuntimeException(paramException); }
  
  static String[] toStringArray(List<String> paramList) { return (String[])paramList.toArray(EMPTY_STRING_ARRAY); }
  
  public static ObjectName newObjectName(String paramString1, String paramString2) { return newObjectName(paramString1 + ",name=" + paramString2); }
  
  public static ObjectName newObjectName(String paramString) {
    try {
      return ObjectName.getInstance(paramString);
    } catch (MalformedObjectNameException malformedObjectNameException) {
      throw new IllegalArgumentException(malformedObjectNameException);
    } 
  }
  
  static void checkAccess(ManagementPermission paramManagementPermission) throws SecurityException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(paramManagementPermission); 
  }
  
  static void checkMonitorAccess() { checkAccess(monitorPermission); }
  
  static void checkControlAccess() { checkAccess(controlPermission); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */