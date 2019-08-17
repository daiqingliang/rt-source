package javax.xml.stream;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class FactoryFinder {
  private static final String DEFAULT_PACKAGE = "com.sun.xml.internal.";
  
  private static boolean debug = false;
  
  private static final Properties cacheProps = new Properties();
  
  private static final SecuritySupport ss = new SecuritySupport();
  
  private static void dPrint(String paramString) {
    if (debug)
      System.err.println("JAXP: " + paramString); 
  }
  
  private static Class getProviderClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2) throws ClassNotFoundException {
    try {
      if (paramClassLoader == null) {
        if (paramBoolean2)
          return Class.forName(paramString, false, FactoryFinder.class.getClassLoader()); 
        paramClassLoader = ss.getContextClassLoader();
        if (paramClassLoader == null)
          throw new ClassNotFoundException(); 
        return Class.forName(paramString, false, paramClassLoader);
      } 
      return Class.forName(paramString, false, paramClassLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      if (paramBoolean1)
        return Class.forName(paramString, false, FactoryFinder.class.getClassLoader()); 
      throw classNotFoundException;
    } 
  }
  
  static <T> T newInstance(Class<T> paramClass, String paramString, ClassLoader paramClassLoader, boolean paramBoolean) throws FactoryConfigurationError { return (T)newInstance(paramClass, paramString, paramClassLoader, paramBoolean, false); }
  
  static <T> T newInstance(Class<T> paramClass, String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2) throws FactoryConfigurationError {
    assert paramClass != null;
    if (System.getSecurityManager() != null && paramString != null && paramString.startsWith("com.sun.xml.internal.")) {
      paramClassLoader = null;
      paramBoolean2 = true;
    } 
    try {
      Class clazz = getProviderClass(paramString, paramClassLoader, paramBoolean1, paramBoolean2);
      if (!paramClass.isAssignableFrom(clazz))
        throw new ClassCastException(paramString + " cannot be cast to " + paramClass.getName()); 
      Object object = clazz.newInstance();
      if (debug)
        dPrint("created new instance of " + clazz + " using ClassLoader: " + paramClassLoader); 
      return (T)paramClass.cast(object);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new FactoryConfigurationError("Provider " + paramString + " not found", classNotFoundException);
    } catch (Exception exception) {
      throw new FactoryConfigurationError("Provider " + paramString + " could not be instantiated: " + exception, exception);
    } 
  }
  
  static <T> T find(Class<T> paramClass, String paramString) throws FactoryConfigurationError { return (T)find(paramClass, paramClass.getName(), null, paramString); }
  
  static <T> T find(Class<T> paramClass, String paramString1, ClassLoader paramClassLoader, String paramString2) throws FactoryConfigurationError {
    dPrint("find factoryId =" + paramString1);
    try {
      String str1;
      if (paramClass.getName().equals(paramString1)) {
        str1 = ss.getSystemProperty(paramString1);
      } else {
        str1 = System.getProperty(paramString1);
      } 
      if (str1 != null) {
        dPrint("found system property, value=" + str1);
        return (T)newInstance(paramClass, str1, paramClassLoader, true);
      } 
    } catch (SecurityException securityException) {
      throw new FactoryConfigurationError("Failed to read factoryId '" + paramString1 + "'", securityException);
    } 
    String str = null;
    try {
      if (firstTime)
        synchronized (cacheProps) {
          if (firstTime) {
            str = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "stax.properties";
            File file = new File(str);
            firstTime = false;
            if (ss.doesFileExist(file)) {
              dPrint("Read properties file " + file);
              cacheProps.load(ss.getFileInputStream(file));
            } else {
              str = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
              file = new File(str);
              if (ss.doesFileExist(file)) {
                dPrint("Read properties file " + file);
                cacheProps.load(ss.getFileInputStream(file));
              } 
            } 
          } 
        }  
      String str1 = cacheProps.getProperty(paramString1);
      if (str1 != null) {
        dPrint("found in " + str + " value=" + str1);
        return (T)newInstance(paramClass, str1, paramClassLoader, true);
      } 
    } catch (Exception exception) {
      if (debug)
        exception.printStackTrace(); 
    } 
    if (paramClass.getName().equals(paramString1)) {
      Object object = findServiceProvider(paramClass, paramClassLoader);
      if (object != null)
        return (T)object; 
    } else {
      assert paramString2 == null;
    } 
    if (paramString2 == null)
      throw new FactoryConfigurationError("Provider for " + paramString1 + " cannot be found", null); 
    dPrint("loaded from fallback value: " + paramString2);
    return (T)newInstance(paramClass, paramString2, paramClassLoader, true);
  }
  
  private static <T> T findServiceProvider(final Class<T> type, final ClassLoader cl) {
    try {
      return (T)AccessController.doPrivileged(new PrivilegedAction<T>() {
            public T run() {
              ServiceLoader serviceLoader;
              if (cl == null) {
                serviceLoader = ServiceLoader.load(type);
              } else {
                serviceLoader = ServiceLoader.load(type, cl);
              } 
              Iterator iterator = serviceLoader.iterator();
              return iterator.hasNext() ? (T)iterator.next() : null;
            }
          });
    } catch (ServiceConfigurationError serviceConfigurationError) {
      RuntimeException runtimeException = new RuntimeException("Provider for " + paramClass + " cannot be created", serviceConfigurationError);
      FactoryConfigurationError factoryConfigurationError = new FactoryConfigurationError(runtimeException, runtimeException.getMessage());
      throw factoryConfigurationError;
    } 
  }
  
  static  {
    try {
      String str = ss.getSystemProperty("jaxp.debug");
      debug = (str != null && !"false".equals(str));
    } catch (SecurityException securityException) {
      debug = false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */