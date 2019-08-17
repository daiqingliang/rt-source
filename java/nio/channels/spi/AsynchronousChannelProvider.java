package java.nio.channels.spi;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import sun.nio.ch.DefaultAsynchronousChannelProvider;

public abstract class AsynchronousChannelProvider {
  private static Void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("asynchronousChannelProvider")); 
    return null;
  }
  
  private AsynchronousChannelProvider(Void paramVoid) {}
  
  protected AsynchronousChannelProvider() { this(checkPermission()); }
  
  public static AsynchronousChannelProvider provider() { return ProviderHolder.provider; }
  
  public abstract AsynchronousChannelGroup openAsynchronousChannelGroup(int paramInt, ThreadFactory paramThreadFactory) throws IOException;
  
  public abstract AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService paramExecutorService, int paramInt) throws IOException;
  
  public abstract AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup) throws IOException;
  
  public abstract AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup paramAsynchronousChannelGroup) throws IOException;
  
  private static class ProviderHolder {
    static final AsynchronousChannelProvider provider = load();
    
    private static AsynchronousChannelProvider load() { return (AsynchronousChannelProvider)AccessController.doPrivileged(new PrivilegedAction<AsynchronousChannelProvider>() {
            public AsynchronousChannelProvider run() {
              AsynchronousChannelProvider asynchronousChannelProvider = AsynchronousChannelProvider.ProviderHolder.loadProviderFromProperty();
              if (asynchronousChannelProvider != null)
                return asynchronousChannelProvider; 
              asynchronousChannelProvider = AsynchronousChannelProvider.ProviderHolder.loadProviderAsService();
              return (asynchronousChannelProvider != null) ? asynchronousChannelProvider : DefaultAsynchronousChannelProvider.create();
            }
          }); }
    
    private static AsynchronousChannelProvider loadProviderFromProperty() {
      String str = System.getProperty("java.nio.channels.spi.AsynchronousChannelProvider");
      if (str == null)
        return null; 
      try {
        Class clazz = Class.forName(str, true, ClassLoader.getSystemClassLoader());
        return (AsynchronousChannelProvider)clazz.newInstance();
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
    
    private static AsynchronousChannelProvider loadProviderAsService() {
      ServiceLoader serviceLoader = ServiceLoader.load(AsynchronousChannelProvider.class, ClassLoader.getSystemClassLoader());
      Iterator iterator = serviceLoader.iterator();
      while (true) {
        try {
          return iterator.hasNext() ? (AsynchronousChannelProvider)iterator.next() : null;
        } catch (ServiceConfigurationError serviceConfigurationError) {
          if (serviceConfigurationError.getCause() instanceof SecurityException)
            continue; 
          break;
        } 
      } 
      throw serviceConfigurationError;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\channels\spi\AsynchronousChannelProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */