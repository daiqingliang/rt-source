package java.nio.channels.spi;

import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public abstract class SelectorProvider {
  private static final Object lock = new Object();
  
  private static SelectorProvider provider = null;
  
  protected SelectorProvider() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("selectorProvider")); 
  }
  
  private static boolean loadProviderFromProperty() {
    String str = System.getProperty("java.nio.channels.spi.SelectorProvider");
    if (str == null)
      return false; 
    try {
      Class clazz = Class.forName(str, true, ClassLoader.getSystemClassLoader());
      provider = (SelectorProvider)clazz.newInstance();
      return true;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ServiceConfigurationError(null, classNotFoundException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ServiceConfigurationError(null, illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throw new ServiceConfigurationError(null, instantiationException);
    } catch (SecurityException securityException) {
      throw new ServiceConfigurationError(null, securityException);
    } 
  }
  
  private static boolean loadProviderAsService() {
    ServiceLoader serviceLoader = ServiceLoader.load(SelectorProvider.class, ClassLoader.getSystemClassLoader());
    Iterator iterator = serviceLoader.iterator();
    while (true) {
      try {
        if (!iterator.hasNext())
          return false; 
        provider = (SelectorProvider)iterator.next();
        return true;
      } catch (ServiceConfigurationError serviceConfigurationError) {
        if (serviceConfigurationError.getCause() instanceof SecurityException)
          continue; 
        break;
      } 
    } 
    throw serviceConfigurationError;
  }
  
  public static SelectorProvider provider() {
    synchronized (lock) {
      if (provider != null)
        return provider; 
      return (SelectorProvider)AccessController.doPrivileged(new PrivilegedAction<SelectorProvider>() {
            public SelectorProvider run() { return SelectorProvider.loadProviderFromProperty() ? provider : (SelectorProvider.loadProviderAsService() ? provider : provider); }
          });
    } 
  }
  
  public abstract DatagramChannel openDatagramChannel() throws IOException;
  
  public abstract DatagramChannel openDatagramChannel(ProtocolFamily paramProtocolFamily) throws IOException;
  
  public abstract Pipe openPipe() throws IOException;
  
  public abstract AbstractSelector openSelector() throws IOException;
  
  public abstract ServerSocketChannel openServerSocketChannel() throws IOException;
  
  public abstract SocketChannel openSocketChannel() throws IOException;
  
  public Channel inheritedChannel() throws IOException { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\spi\SelectorProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */