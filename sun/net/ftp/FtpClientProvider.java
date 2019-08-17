package sun.net.ftp;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceConfigurationError;

public abstract class FtpClientProvider {
  private static final Object lock = new Object();
  
  private static FtpClientProvider provider = null;
  
  public abstract FtpClient createFtpClient();
  
  protected FtpClientProvider() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("ftpClientProvider")); 
  }
  
  private static boolean loadProviderFromProperty() {
    String str = System.getProperty("sun.net.ftpClientProvider");
    if (str == null)
      return false; 
    try {
      Class clazz = Class.forName(str, true, null);
      provider = (FtpClientProvider)clazz.newInstance();
      return true;
    } catch (ClassNotFoundException|IllegalAccessException|InstantiationException|SecurityException classNotFoundException) {
      throw new ServiceConfigurationError(classNotFoundException.toString());
    } 
  }
  
  private static boolean loadProviderAsService() { return false; }
  
  public static FtpClientProvider provider() {
    synchronized (lock) {
      if (provider != null)
        return provider; 
      return (FtpClientProvider)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() { return FtpClientProvider.loadProviderFromProperty() ? provider : (FtpClientProvider.loadProviderAsService() ? provider : provider); }
          });
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ftp\FtpClientProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */