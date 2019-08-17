package javax.management.remote;

import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import sun.reflect.misc.ReflectUtil;

public class JMXConnectorFactory {
  public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
  
  public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
  
  public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
  
  private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorFactory");
  
  public static JMXConnector connect(JMXServiceURL paramJMXServiceURL) throws IOException { return connect(paramJMXServiceURL, null); }
  
  public static JMXConnector connect(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap) throws IOException {
    if (paramJMXServiceURL == null)
      throw new NullPointerException("Null JMXServiceURL"); 
    JMXConnector jMXConnector = newJMXConnector(paramJMXServiceURL, paramMap);
    jMXConnector.connect(paramMap);
    return jMXConnector;
  }
  
  private static <K, V> Map<K, V> newHashMap() { return new HashMap(); }
  
  private static <K> Map<K, Object> newHashMap(Map<K, ?> paramMap) { return new HashMap(paramMap); }
  
  public static JMXConnector newJMXConnector(JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap) throws IOException {
    Map map1;
    if (paramMap == null) {
      map1 = newHashMap();
    } else {
      EnvHelp.checkAttributes(paramMap);
      map1 = newHashMap(paramMap);
    } 
    ClassLoader classLoader = resolveClassLoader(map1);
    Class clazz = JMXConnectorProvider.class;
    String str = paramJMXServiceURL.getProtocol();
    JMXServiceURL jMXServiceURL = paramJMXServiceURL;
    JMXConnectorProvider jMXConnectorProvider = (JMXConnectorProvider)getProvider(jMXServiceURL, map1, "ClientProvider", clazz, classLoader);
    IOException iOException = null;
    if (jMXConnectorProvider == null) {
      if (classLoader != null)
        try {
          JMXConnector jMXConnector = getConnectorAsService(classLoader, jMXServiceURL, map1);
          if (jMXConnector != null)
            return jMXConnector; 
        } catch (JMXProviderException jMXProviderException) {
          throw jMXProviderException;
        } catch (IOException iOException1) {
          iOException = iOException1;
        }  
      jMXConnectorProvider = (JMXConnectorProvider)getProvider(str, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ClientProvider", clazz);
    } 
    if (jMXConnectorProvider == null) {
      MalformedURLException malformedURLException = new MalformedURLException("Unsupported protocol: " + str);
      if (iOException == null)
        throw malformedURLException; 
      throw (MalformedURLException)EnvHelp.initCause(malformedURLException, iOException);
    } 
    Map map2 = Collections.unmodifiableMap(map1);
    return jMXConnectorProvider.newJMXConnector(paramJMXServiceURL, map2);
  }
  
  private static String resolvePkgs(Map<String, ?> paramMap) throws JMXProviderException {
    Object object = null;
    if (paramMap != null)
      object = paramMap.get("jmx.remote.protocol.provider.pkgs"); 
    if (object == null)
      object = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() { return System.getProperty("jmx.remote.protocol.provider.pkgs"); }
          }); 
    if (object == null)
      return null; 
    if (!(object instanceof String)) {
      String str1 = "Value of jmx.remote.protocol.provider.pkgs parameter is not a String: " + object.getClass().getName();
      throw new JMXProviderException(str1);
    } 
    String str = (String)object;
    if (str.trim().equals(""))
      return null; 
    if (str.startsWith("|") || str.endsWith("|") || str.indexOf("||") >= 0) {
      String str1 = "Value of jmx.remote.protocol.provider.pkgs contains an empty element: " + str;
      throw new JMXProviderException(str1);
    } 
    return str;
  }
  
  static <T> T getProvider(JMXServiceURL paramJMXServiceURL, Map<String, Object> paramMap, String paramString, Class<T> paramClass, ClassLoader paramClassLoader) throws IOException {
    String str1 = paramJMXServiceURL.getProtocol();
    String str2 = resolvePkgs(paramMap);
    Object object = null;
    if (str2 != null) {
      object = getProvider(str1, str2, paramClassLoader, paramString, paramClass);
      if (object != null) {
        boolean bool = (paramClassLoader != object.getClass().getClassLoader()) ? 1 : 0;
        paramMap.put("jmx.remote.protocol.provider.class.loader", bool ? wrap(paramClassLoader) : paramClassLoader);
      } 
    } 
    return (T)object;
  }
  
  static <T> Iterator<T> getProviderIterator(Class<T> paramClass, ClassLoader paramClassLoader) {
    ServiceLoader serviceLoader = ServiceLoader.load(paramClass, paramClassLoader);
    return serviceLoader.iterator();
  }
  
  private static ClassLoader wrap(final ClassLoader parent) { return (paramClassLoader != null) ? (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return new ClassLoader(parent) {
                protected Class<?> loadClass(String param2String, boolean param2Boolean) throws ClassNotFoundException {
                  ReflectUtil.checkPackageAccess(param2String);
                  return super.loadClass(param2String, param2Boolean);
                }
              }; }
        }) : null; }
  
  private static JMXConnector getConnectorAsService(ClassLoader paramClassLoader, JMXServiceURL paramJMXServiceURL, Map<String, ?> paramMap) throws IOException {
    Iterator iterator = getProviderIterator(JMXConnectorProvider.class, paramClassLoader);
    IOException iOException = null;
    while (iterator.hasNext()) {
      JMXConnectorProvider jMXConnectorProvider = (JMXConnectorProvider)iterator.next();
      try {
        return jMXConnectorProvider.newJMXConnector(paramJMXServiceURL, paramMap);
      } catch (JMXProviderException jMXProviderException) {
        throw jMXProviderException;
      } catch (Exception exception) {
        if (logger.traceOn())
          logger.trace("getConnectorAsService", "URL[" + paramJMXServiceURL + "] Service provider exception: " + exception); 
        if (!(exception instanceof MalformedURLException) && iOException == null) {
          if (exception instanceof IOException) {
            iOException = (IOException)exception;
            continue;
          } 
          iOException = (IOException)EnvHelp.initCause(new IOException(exception.getMessage()), exception);
        } 
      } 
    } 
    if (iOException == null)
      return null; 
    throw iOException;
  }
  
  static <T> T getProvider(String paramString1, String paramString2, ClassLoader paramClassLoader, String paramString3, Class<T> paramClass) throws IOException {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString2, "|");
    while (stringTokenizer.hasMoreTokens()) {
      Class clazz1;
      String str1 = stringTokenizer.nextToken();
      String str2 = str1 + "." + protocol2package(paramString1) + "." + paramString3;
      try {
        clazz1 = Class.forName(str2, true, paramClassLoader);
      } catch (ClassNotFoundException classNotFoundException) {
        continue;
      } 
      if (!paramClass.isAssignableFrom(clazz1)) {
        String str = "Provider class does not implement " + paramClass.getName() + ": " + clazz1.getName();
        throw new JMXProviderException(str);
      } 
      Class clazz2 = (Class)Util.cast(clazz1);
      try {
        return (T)clazz2.newInstance();
      } catch (Exception exception) {
        String str = "Exception when instantiating provider [" + str2 + "]";
        throw new JMXProviderException(str, exception);
      } 
    } 
    return null;
  }
  
  static ClassLoader resolveClassLoader(Map<String, ?> paramMap) {
    ClassLoader classLoader = null;
    if (paramMap != null)
      try {
        classLoader = (ClassLoader)paramMap.get("jmx.remote.protocol.provider.class.loader");
      } catch (ClassCastException classCastException) {
        throw new IllegalArgumentException("The ClassLoader supplied in the environment map using the jmx.remote.protocol.provider.class.loader attribute is not an instance of java.lang.ClassLoader");
      }  
    if (classLoader == null)
      classLoader = Thread.currentThread().getContextClassLoader(); 
    return classLoader;
  }
  
  private static String protocol2package(String paramString) { return paramString.replace('+', '.').replace('-', '_'); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXConnectorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */