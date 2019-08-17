package java.net;

import java.io.IOException;
import java.util.List;
import sun.security.util.SecurityConstants;

public abstract class ProxySelector {
  private static ProxySelector theProxySelector;
  
  public static ProxySelector getDefault() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.GET_PROXYSELECTOR_PERMISSION); 
    return theProxySelector;
  }
  
  public static void setDefault(ProxySelector paramProxySelector) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(SecurityConstants.SET_PROXYSELECTOR_PERMISSION); 
    theProxySelector = paramProxySelector;
  }
  
  public abstract List<Proxy> select(URI paramURI);
  
  public abstract void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException);
  
  static  {
    try {
      Class clazz = Class.forName("sun.net.spi.DefaultProxySelector");
      if (clazz != null && ProxySelector.class.isAssignableFrom(clazz))
        theProxySelector = (ProxySelector)clazz.newInstance(); 
    } catch (Exception exception) {
      theProxySelector = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\ProxySelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */