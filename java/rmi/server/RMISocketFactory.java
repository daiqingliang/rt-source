package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import sun.rmi.transport.proxy.RMIMasterSocketFactory;

public abstract class RMISocketFactory implements RMIClientSocketFactory, RMIServerSocketFactory {
  private static RMISocketFactory factory = null;
  
  private static RMISocketFactory defaultSocketFactory;
  
  private static RMIFailureHandler handler = null;
  
  public abstract Socket createSocket(String paramString, int paramInt) throws IOException;
  
  public abstract ServerSocket createServerSocket(int paramInt) throws IOException;
  
  public static void setSocketFactory(RMISocketFactory paramRMISocketFactory) throws IOException {
    if (factory != null)
      throw new SocketException("factory already defined"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    factory = paramRMISocketFactory;
  }
  
  public static RMISocketFactory getSocketFactory() { return factory; }
  
  public static RMISocketFactory getDefaultSocketFactory() {
    if (defaultSocketFactory == null)
      defaultSocketFactory = new RMIMasterSocketFactory(); 
    return defaultSocketFactory;
  }
  
  public static void setFailureHandler(RMIFailureHandler paramRMIFailureHandler) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkSetFactory(); 
    handler = paramRMIFailureHandler;
  }
  
  public static RMIFailureHandler getFailureHandler() { return handler; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\RMISocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */