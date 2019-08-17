package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import sun.security.util.SecurityConstants;

public abstract class CookieHandler {
  private static CookieHandler cookieHandler;
  
  public static CookieHandler getDefault() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.GET_COOKIEHANDLER_PERMISSION); 
    return cookieHandler;
  }
  
  public static void setDefault(CookieHandler paramCookieHandler) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.SET_COOKIEHANDLER_PERMISSION); 
    cookieHandler = paramCookieHandler;
  }
  
  public abstract Map<String, List<String>> get(URI paramURI, Map<String, List<String>> paramMap) throws IOException;
  
  public abstract void put(URI paramURI, Map<String, List<String>> paramMap) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\CookieHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */