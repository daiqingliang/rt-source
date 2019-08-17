package javax.xml.transform;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

class FactoryFinder {
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xalan.internal.";
  
  private static boolean debug = false;
  
  private static final Properties cacheProps = new Properties();
  
  private static final SecuritySupport ss = new SecuritySupport();
  
  private static void dPrint(String paramString) {
    if (debug)
      System.err.println("JAXP: " + paramString); 
  }
  
  private static Class<?> getProviderClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2) throws ClassNotFoundException {
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
  
  static <T> T newInstance(Class<T> paramClass, String paramString, ClassLoader paramClassLoader, boolean paramBoolean) throws TransformerFactoryConfigurationError {
    assert paramClass != null;
    boolean bool = false;
    if (System.getSecurityManager() != null && paramString != null && paramString.startsWith("com.sun.org.apache.xalan.internal.")) {
      paramClassLoader = null;
      bool = true;
    } 
    try {
      Class clazz = getProviderClass(paramString, paramClassLoader, paramBoolean, bool);
      if (!paramClass.isAssignableFrom(clazz))
        throw new ClassCastException(paramString + " cannot be cast to " + paramClass.getName()); 
      Object object = clazz.newInstance();
      if (debug)
        dPrint("created new instance of " + clazz + " using ClassLoader: " + paramClassLoader); 
      return (T)paramClass.cast(object);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new TransformerFactoryConfigurationError(classNotFoundException, "Provider " + paramString + " not found");
    } catch (Exception exception) {
      throw new TransformerFactoryConfigurationError(exception, "Provider " + paramString + " could not be instantiated: " + exception);
    } 
  }
  
  static <T> T find(Class<T> paramClass, String paramString) throws TransformerFactoryConfigurationError {
    assert paramClass != null;
    String str = paramClass.getName();
    dPrint("find factoryId =" + str);
    try {
      String str1 = ss.getSystemProperty(str);
      if (str1 != null) {
        dPrint("found system property, value=" + str1);
        return (T)newInstance(paramClass, str1, null, true);
      } 
    } catch (SecurityException securityException) {
      if (debug)
        securityException.printStackTrace(); 
    } 
    try {
      if (firstTime)
        synchronized (cacheProps) {
          if (firstTime) {
            String str2 = ss.getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File file = new File(str2);
            firstTime = false;
            if (ss.doesFileExist(file)) {
              dPrint("Read properties file " + file);
              cacheProps.load(ss.getFileInputStream(file));
            } 
          } 
        }  
      String str1 = cacheProps.getProperty(str);
      if (str1 != null) {
        dPrint("found in $java.home/jaxp.properties, value=" + str1);
        return (T)newInstance(paramClass, str1, null, true);
      } 
    } catch (Exception exception) {
      if (debug)
        exception.printStackTrace(); 
    } 
    Object object = findServiceProvider(paramClass);
    if (object != null)
      return (T)object; 
    if (paramString == null)
      throw new TransformerFactoryConfigurationError(null, "Provider for " + str + " cannot be found"); 
    dPrint("loaded from fallback value: " + paramString);
    return (T)newInstance(paramClass, paramString, null, true);
  }
  
  private static <T> T findServiceProvider(final Class<T> type) throws TransformerFactoryConfigurationError {
    try {
      return (T)AccessController.doPrivileged(new PrivilegedAction<T>() {
            public T run() {
              ServiceLoader serviceLoader = ServiceLoader.load(type);
              Iterator iterator = serviceLoader.iterator();
              return iterator.hasNext() ? (T)iterator.next() : null;
            }
          });
    } catch (ServiceConfigurationError serviceConfigurationError) {
      RuntimeException runtimeException = new RuntimeException("Provider for " + paramClass + " cannot be created", serviceConfigurationError);
      TransformerFactoryConfigurationError transformerFactoryConfigurationError = new TransformerFactoryConfigurationError(runtimeException, runtimeException.getMessage());
      throw transformerFactoryConfigurationError;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\FactoryFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */