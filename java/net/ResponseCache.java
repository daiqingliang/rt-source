package java.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import sun.security.util.SecurityConstants;

public abstract class ResponseCache {
  private static ResponseCache theResponseCache;
  
  public static ResponseCache getDefault() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.GET_RESPONSECACHE_PERMISSION); 
    return theResponseCache;
  }
  
  public static void setDefault(ResponseCache paramResponseCache) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.SET_RESPONSECACHE_PERMISSION); 
    theResponseCache = paramResponseCache;
  }
  
  public abstract CacheResponse get(URI paramURI, String paramString, Map<String, List<String>> paramMap) throws IOException;
  
  public abstract CacheRequest put(URI paramURI, URLConnection paramURLConnection) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\ResponseCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */