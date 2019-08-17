package com.sun.net.httpserver.spi;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import jdk.Exported;

@Exported
public abstract class HttpServerProvider {
  private static final Object lock = new Object();
  
  private static HttpServerProvider provider = null;
  
  public abstract HttpServer createHttpServer(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException;
  
  public abstract HttpsServer createHttpsServer(InetSocketAddress paramInetSocketAddress, int paramInt) throws IOException;
  
  protected HttpServerProvider() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("httpServerProvider")); 
  }
  
  private static boolean loadProviderFromProperty() {
    String str = System.getProperty("com.sun.net.httpserver.HttpServerProvider");
    if (str == null)
      return false; 
    try {
      Class clazz = Class.forName(str, true, ClassLoader.getSystemClassLoader());
      provider = (HttpServerProvider)clazz.newInstance();
      return true;
    } catch (ClassNotFoundException|IllegalAccessException|InstantiationException|SecurityException classNotFoundException) {
      throw new ServiceConfigurationError(null, classNotFoundException);
    } 
  }
  
  private static boolean loadProviderAsService() {
    Iterator iterator = ServiceLoader.load(HttpServerProvider.class, ClassLoader.getSystemClassLoader()).iterator();
    while (true) {
      try {
        if (!iterator.hasNext())
          return false; 
        provider = (HttpServerProvider)iterator.next();
        return true;
      } catch (ServiceConfigurationError serviceConfigurationError) {
        if (serviceConfigurationError.getCause() instanceof SecurityException)
          continue; 
        break;
      } 
    } 
    throw serviceConfigurationError;
  }
  
  public static HttpServerProvider provider() {
    synchronized (lock) {
      if (provider != null)
        return provider; 
      return (HttpServerProvider)AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() { return HttpServerProvider.loadProviderFromProperty() ? provider : (HttpServerProvider.loadProviderAsService() ? provider : provider); }
          });
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\spi\HttpServerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */