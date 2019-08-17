package javax.xml.bind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class ContextFinder {
  private static final Logger logger = Logger.getLogger("javax.xml.bind");
  
  private static final String PLATFORM_DEFAULT_FACTORY_CLASS = "com.sun.xml.internal.bind.v2.ContextFactory";
  
  private static void handleInvocationTargetException(InvocationTargetException paramInvocationTargetException) throws JAXBException {
    Throwable throwable = paramInvocationTargetException.getTargetException();
    if (throwable != null) {
      if (throwable instanceof JAXBException)
        throw (JAXBException)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
    } 
  }
  
  private static JAXBException handleClassCastException(Class paramClass1, Class paramClass2) {
    URL uRL = which(paramClass2);
    return new JAXBException(Messages.format("JAXBContext.IllegalCast", getClassClassLoader(paramClass1).getResource("javax/xml/bind/JAXBContext.class"), uRL));
  }
  
  static JAXBContext newInstance(String paramString1, String paramString2, ClassLoader paramClassLoader, Map paramMap) throws JAXBException {
    try {
      Class clazz = safeLoadClass(paramString2, paramClassLoader);
      return newInstance(paramString1, clazz, paramClassLoader, paramMap);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new JAXBException(Messages.format("ContextFinder.ProviderNotFound", paramString2), classNotFoundException);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", paramString2, exception), exception);
    } 
  }
  
  static JAXBContext newInstance(String paramString, Class paramClass, ClassLoader paramClassLoader, Map paramMap) throws JAXBException {
    try {
      Object object = null;
      try {
        Method method = paramClass.getMethod("createContext", new Class[] { String.class, ClassLoader.class, Map.class });
        object = method.invoke(null, new Object[] { paramString, paramClassLoader, paramMap });
      } catch (NoSuchMethodException noSuchMethodException) {}
      if (object == null) {
        Method method = paramClass.getMethod("createContext", new Class[] { String.class, ClassLoader.class });
        object = method.invoke(null, new Object[] { paramString, paramClassLoader });
      } 
      if (!(object instanceof JAXBContext))
        throw handleClassCastException(object.getClass(), JAXBContext.class); 
      return (JAXBContext)object;
    } catch (InvocationTargetException invocationTargetException) {
      handleInvocationTargetException(invocationTargetException);
      Throwable throwable = invocationTargetException;
      if (invocationTargetException.getTargetException() != null)
        throwable = invocationTargetException.getTargetException(); 
      throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", paramClass, throwable), throwable);
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", paramClass, exception), exception);
    } 
  }
  
  static JAXBContext newInstance(Class[] paramArrayOfClass, Map paramMap, String paramString) throws JAXBException {
    Class clazz;
    ClassLoader classLoader = getContextClassLoader();
    try {
      clazz = safeLoadClass(paramString, classLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new JAXBException(classNotFoundException);
    } 
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "loaded {0} from {1}", new Object[] { paramString, which(clazz) }); 
    return newInstance(paramArrayOfClass, paramMap, clazz);
  }
  
  static JAXBContext newInstance(Class[] paramArrayOfClass, Map paramMap, Class paramClass) throws JAXBException {
    Method method;
    try {
      method = paramClass.getMethod("createContext", new Class[] { Class[].class, Map.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new JAXBException(noSuchMethodException);
    } 
    try {
      Object object = method.invoke(null, new Object[] { paramArrayOfClass, paramMap });
      if (!(object instanceof JAXBContext))
        throw handleClassCastException(object.getClass(), JAXBContext.class); 
      return (JAXBContext)object;
    } catch (IllegalAccessException illegalAccessException) {
      throw new JAXBException(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      handleInvocationTargetException(invocationTargetException);
      Throwable throwable = invocationTargetException;
      if (invocationTargetException.getTargetException() != null)
        throwable = invocationTargetException.getTargetException(); 
      throw new JAXBException(throwable);
    } 
  }
  
  static JAXBContext find(String paramString1, String paramString2, ClassLoader paramClassLoader, Map paramMap) throws JAXBException {
    String str1 = JAXBContext.class.getName();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString2, ":");
    if (!stringTokenizer.hasMoreTokens())
      throw new JAXBException(Messages.format("ContextFinder.NoPackageInContextPath")); 
    logger.fine("Searching jaxb.properties");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken(":").replace('.', '/');
      StringBuilder stringBuilder = (new StringBuilder()).append(str).append("/jaxb.properties");
      Properties properties = loadJAXBProperties(paramClassLoader, stringBuilder.toString());
      if (properties != null) {
        if (properties.containsKey(paramString1)) {
          String str3 = properties.getProperty(paramString1);
          return newInstance(paramString2, str3, paramClassLoader, paramMap);
        } 
        throw new JAXBException(Messages.format("ContextFinder.MissingProperty", str, paramString1));
      } 
    } 
    logger.fine("Searching the system property");
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("javax.xml.bind.context.factory"));
    if (str2 != null)
      return newInstance(paramString2, str2, paramClassLoader, paramMap); 
    str2 = (String)AccessController.doPrivileged(new GetPropertyAction(str1));
    if (str2 != null)
      return newInstance(paramString2, str2, paramClassLoader, paramMap); 
    Class clazz = lookupJaxbContextUsingOsgiServiceLoader();
    if (clazz != null) {
      logger.fine("OSGi environment detected");
      return newInstance(paramString2, clazz, paramClassLoader, paramMap);
    } 
    logger.fine("Searching META-INF/services");
    bufferedReader = null;
    try {
      stringBuilder = (new StringBuilder()).append("META-INF/services/").append(str1);
      InputStream inputStream = paramClassLoader.getResourceAsStream(stringBuilder.toString());
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        str2 = bufferedReader.readLine();
        if (str2 != null)
          str2 = str2.trim(); 
        bufferedReader.close();
        return newInstance(paramString2, str2, paramClassLoader, paramMap);
      } 
      logger.log(Level.FINE, "Unable to load:{0}", stringBuilder.toString());
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new JAXBException(unsupportedEncodingException);
    } catch (IOException iOException) {
      throw new JAXBException(iOException);
    } finally {
      try {
        if (bufferedReader != null)
          bufferedReader.close(); 
      } catch (IOException iOException) {
        Logger.getLogger(ContextFinder.class.getName()).log(Level.SEVERE, null, iOException);
      } 
    } 
    logger.fine("Trying to create the platform default provider");
    return newInstance(paramString2, "com.sun.xml.internal.bind.v2.ContextFactory", paramClassLoader, paramMap);
  }
  
  static JAXBContext find(Class[] paramArrayOfClass, Map paramMap) throws JAXBException {
    String str1 = JAXBContext.class.getName();
    for (Class clazz1 : paramArrayOfClass) {
      ClassLoader classLoader = getClassClassLoader(clazz1);
      Package package = clazz1.getPackage();
      if (package != null) {
        String str3 = package.getName().replace('.', '/');
        String str4 = str3 + "/jaxb.properties";
        logger.log(Level.FINE, "Trying to locate {0}", str4);
        Properties properties = loadJAXBProperties(classLoader, str4);
        if (properties == null) {
          logger.fine("  not found");
        } else {
          logger.fine("  found");
          if (properties.containsKey("javax.xml.bind.context.factory")) {
            String str = properties.getProperty("javax.xml.bind.context.factory").trim();
            return newInstance(paramArrayOfClass, paramMap, str);
          } 
          throw new JAXBException(Messages.format("ContextFinder.MissingProperty", str3, "javax.xml.bind.context.factory"));
        } 
      } 
    } 
    logger.log(Level.FINE, "Checking system property {0}", "javax.xml.bind.context.factory");
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("javax.xml.bind.context.factory"));
    if (str2 != null) {
      logger.log(Level.FINE, "  found {0}", str2);
      return newInstance(paramArrayOfClass, paramMap, str2);
    } 
    logger.fine("  not found");
    logger.log(Level.FINE, "Checking system property {0}", str1);
    str2 = (String)AccessController.doPrivileged(new GetPropertyAction(str1));
    if (str2 != null) {
      logger.log(Level.FINE, "  found {0}", str2);
      return newInstance(paramArrayOfClass, paramMap, str2);
    } 
    logger.fine("  not found");
    Class clazz = lookupJaxbContextUsingOsgiServiceLoader();
    if (clazz != null) {
      logger.fine("OSGi environment detected");
      return newInstance(paramArrayOfClass, paramMap, clazz);
    } 
    logger.fine("Checking META-INF/services");
    bufferedReader = null;
    try {
      URL uRL;
      str = "META-INF/services/" + str1;
      ClassLoader classLoader = getContextClassLoader();
      if (classLoader == null) {
        uRL = ClassLoader.getSystemResource(str);
      } else {
        uRL = classLoader.getResource(str);
      } 
      if (uRL != null) {
        logger.log(Level.FINE, "Reading {0}", uRL);
        bufferedReader = new BufferedReader(new InputStreamReader(uRL.openStream(), "UTF-8"));
        str2 = bufferedReader.readLine();
        if (str2 != null)
          str2 = str2.trim(); 
        return newInstance(paramArrayOfClass, paramMap, str2);
      } 
      logger.log(Level.FINE, "Unable to find: {0}", str);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new JAXBException(unsupportedEncodingException);
    } catch (IOException iOException) {
      throw new JAXBException(iOException);
    } finally {
      if (bufferedReader != null)
        try {
          bufferedReader.close();
        } catch (IOException iOException) {
          logger.log(Level.FINE, "Unable to close stream", iOException);
        }  
    } 
    logger.fine("Trying to create the platform default provider");
    return newInstance(paramArrayOfClass, paramMap, "com.sun.xml.internal.bind.v2.ContextFactory");
  }
  
  private static Class lookupJaxbContextUsingOsgiServiceLoader() {
    try {
      Class clazz = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
      Method method = clazz.getMethod("lookupProviderClasses", new Class[] { Class.class });
      Iterator iterator = ((Iterable)method.invoke(null, new Object[] { JAXBContext.class })).iterator();
      return iterator.hasNext() ? (Class)iterator.next() : null;
    } catch (Exception exception) {
      logger.log(Level.FINE, "Unable to find from OSGi: javax.xml.bind.JAXBContext");
      return null;
    } 
  }
  
  private static Properties loadJAXBProperties(ClassLoader paramClassLoader, String paramString) throws JAXBException {
    Properties properties = null;
    try {
      URL uRL;
      if (paramClassLoader == null) {
        uRL = ClassLoader.getSystemResource(paramString);
      } else {
        uRL = paramClassLoader.getResource(paramString);
      } 
      if (uRL != null) {
        logger.log(Level.FINE, "loading props from {0}", uRL);
        properties = new Properties();
        InputStream inputStream = uRL.openStream();
        properties.load(inputStream);
        inputStream.close();
      } 
    } catch (IOException iOException) {
      logger.log(Level.FINE, "Unable to load " + paramString, iOException);
      throw new JAXBException(iOException.toString(), iOException);
    } 
    return properties;
  }
  
  static URL which(Class paramClass, ClassLoader paramClassLoader) {
    String str = paramClass.getName().replace('.', '/') + ".class";
    if (paramClassLoader == null)
      paramClassLoader = getSystemClassLoader(); 
    return paramClassLoader.getResource(str);
  }
  
  static URL which(Class paramClass) { return which(paramClass, getClassClassLoader(paramClass)); }
  
  private static Class safeLoadClass(String paramString, ClassLoader paramClassLoader) throws ClassNotFoundException {
    logger.log(Level.FINE, "Trying to load {0}", paramString);
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        int i = paramString.lastIndexOf('.');
        if (i != -1)
          securityManager.checkPackageAccess(paramString.substring(0, i)); 
      } 
      return (paramClassLoader == null) ? Class.forName(paramString) : paramClassLoader.loadClass(paramString);
    } catch (SecurityException securityException) {
      if ("com.sun.xml.internal.bind.v2.ContextFactory".equals(paramString))
        return Class.forName(paramString); 
      throw securityException;
    } 
  }
  
  private static ClassLoader getContextClassLoader() { return (System.getSecurityManager() == null) ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return Thread.currentThread().getContextClassLoader(); }
        }); }
  
  private static ClassLoader getClassClassLoader(final Class c) { return (System.getSecurityManager() == null) ? paramClass.getClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return c.getClassLoader(); }
        }); }
  
  private static ClassLoader getSystemClassLoader() { return (System.getSecurityManager() == null) ? ClassLoader.getSystemClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return ClassLoader.getSystemClassLoader(); }
        }); }
  
  static  {
    try {
      if (AccessController.doPrivileged(new GetPropertyAction("jaxb.debug")) != null) {
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);
      } 
    } catch (Throwable throwable) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\ContextFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */