package java.rmi.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import sun.rmi.server.LoaderHandler;

public class RMIClassLoader {
  private static final RMIClassLoaderSpi defaultProvider = newDefaultProviderInstance();
  
  private static final RMIClassLoaderSpi provider = (RMIClassLoaderSpi)AccessController.doPrivileged(new PrivilegedAction<RMIClassLoaderSpi>() {
        public RMIClassLoaderSpi run() { return RMIClassLoader.initializeProvider(); }
      });
  
  @Deprecated
  public static Class<?> loadClass(String paramString) throws MalformedURLException, ClassNotFoundException { return loadClass((String)null, paramString); }
  
  public static Class<?> loadClass(URL paramURL, String paramString) throws MalformedURLException, ClassNotFoundException { return provider.loadClass((paramURL != null) ? paramURL.toString() : null, paramString, null); }
  
  public static Class<?> loadClass(String paramString1, String paramString2) throws MalformedURLException, ClassNotFoundException { return provider.loadClass(paramString1, paramString2, null); }
  
  public static Class<?> loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader) throws MalformedURLException, ClassNotFoundException { return provider.loadClass(paramString1, paramString2, paramClassLoader); }
  
  public static Class<?> loadProxyClass(String paramString, String[] paramArrayOfString, ClassLoader paramClassLoader) throws ClassNotFoundException, MalformedURLException { return provider.loadProxyClass(paramString, paramArrayOfString, paramClassLoader); }
  
  public static ClassLoader getClassLoader(String paramString) throws MalformedURLException, SecurityException { return provider.getClassLoader(paramString); }
  
  public static String getClassAnnotation(Class<?> paramClass) { return provider.getClassAnnotation(paramClass); }
  
  public static RMIClassLoaderSpi getDefaultProviderInstance() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("setFactory")); 
    return defaultProvider;
  }
  
  @Deprecated
  public static Object getSecurityContext(ClassLoader paramClassLoader) { return LoaderHandler.getSecurityContext(paramClassLoader); }
  
  private static RMIClassLoaderSpi newDefaultProviderInstance() { return new RMIClassLoaderSpi() {
        public Class<?> loadClass(String param1String1, String param1String2, ClassLoader param1ClassLoader) throws MalformedURLException, ClassNotFoundException { return LoaderHandler.loadClass(param1String1, param1String2, param1ClassLoader); }
        
        public Class<?> loadProxyClass(String param1String, String[] param1ArrayOfString, ClassLoader param1ClassLoader) throws ClassNotFoundException, MalformedURLException { return LoaderHandler.loadProxyClass(param1String, param1ArrayOfString, param1ClassLoader); }
        
        public ClassLoader getClassLoader(String param1String) throws MalformedURLException, SecurityException { return LoaderHandler.getClassLoader(param1String); }
        
        public String getClassAnnotation(Class<?> param1Class) { return LoaderHandler.getClassAnnotation(param1Class); }
      }; }
  
  private static RMIClassLoaderSpi initializeProvider() {
    String str = System.getProperty("java.rmi.server.RMIClassLoaderSpi");
    if (str != null) {
      if (str.equals("default"))
        return defaultProvider; 
      try {
        Class clazz = Class.forName(str, false, ClassLoader.getSystemClassLoader()).asSubclass(RMIClassLoaderSpi.class);
        return (RMIClassLoaderSpi)clazz.newInstance();
      } catch (ClassNotFoundException classNotFoundException) {
        throw new NoClassDefFoundError(classNotFoundException.getMessage());
      } catch (IllegalAccessException illegalAccessException) {
        throw new IllegalAccessError(illegalAccessException.getMessage());
      } catch (InstantiationException instantiationException) {
        throw new InstantiationError(instantiationException.getMessage());
      } catch (ClassCastException classCastException) {
        LinkageError linkageError = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
        linkageError.initCause(classCastException);
        throw linkageError;
      } 
    } 
    Iterator iterator = ServiceLoader.load(RMIClassLoaderSpi.class, ClassLoader.getSystemClassLoader()).iterator();
    if (iterator.hasNext())
      try {
        return (RMIClassLoaderSpi)iterator.next();
      } catch (ClassCastException classCastException) {
        LinkageError linkageError = new LinkageError("provider class not assignable to RMIClassLoaderSpi");
        linkageError.initCause(classCastException);
        throw linkageError;
      }  
    return defaultProvider;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\RMIClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */