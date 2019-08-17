package com.sun.org.apache.xalan.internal.utils;

public class ObjectFactory {
  private static final String JAXP_INTERNAL = "com.sun.org.apache";
  
  private static final String STAX_INTERNAL = "com.sun.xml.internal";
  
  private static final boolean DEBUG = false;
  
  private static void debugPrintln(String paramString) {}
  
  public static ClassLoader findClassLoader() {
    if (System.getSecurityManager() != null)
      return null; 
    ClassLoader classLoader1 = SecuritySupport.getContextClassLoader();
    ClassLoader classLoader2 = SecuritySupport.getSystemClassLoader();
    for (ClassLoader classLoader3 = classLoader2;; classLoader3 = SecuritySupport.getParentClassLoader(classLoader3)) {
      if (classLoader1 == classLoader3) {
        ClassLoader classLoader = ObjectFactory.class.getClassLoader();
        for (classLoader3 = classLoader2;; classLoader3 = SecuritySupport.getParentClassLoader(classLoader3)) {
          if (classLoader == classLoader3)
            return classLoader2; 
          if (classLoader3 == null)
            break; 
        } 
        return classLoader;
      } 
      if (classLoader3 == null)
        break; 
    } 
    return classLoader1;
  }
  
  public static Object newInstance(String paramString, boolean paramBoolean) throws ConfigurationError { return (System.getSecurityManager() != null) ? newInstance(paramString, null, paramBoolean) : newInstance(paramString, findClassLoader(), paramBoolean); }
  
  static Object newInstance(String paramString, ClassLoader paramClassLoader, boolean paramBoolean) throws ConfigurationError {
    try {
      Class clazz = findProviderClass(paramString, paramClassLoader, paramBoolean);
      return clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new ConfigurationError("Provider " + paramString + " not found", classNotFoundException);
    } catch (Exception exception) {
      throw new ConfigurationError("Provider " + paramString + " could not be instantiated: " + exception, exception);
    } 
  }
  
  public static Class<?> findProviderClass(String paramString, boolean paramBoolean) throws ClassNotFoundException, ConfigurationError { return findProviderClass(paramString, findClassLoader(), paramBoolean); }
  
  private static Class<?> findProviderClass(String paramString, ClassLoader paramClassLoader, boolean paramBoolean) throws ClassNotFoundException, ConfigurationError {
    Class clazz;
    SecurityManager securityManager = System.getSecurityManager();
    try {
      if (securityManager != null)
        if (paramString.startsWith("com.sun.org.apache") || paramString.startsWith("com.sun.xml.internal")) {
          paramClassLoader = null;
        } else {
          int i = paramString.lastIndexOf(".");
          String str = paramString;
          if (i != -1)
            str = paramString.substring(0, i); 
          securityManager.checkPackageAccess(str);
        }  
    } catch (SecurityException null) {
      throw clazz;
    } 
    if (paramClassLoader == null) {
      clazz = Class.forName(paramString, false, ObjectFactory.class.getClassLoader());
    } else {
      try {
        clazz = paramClassLoader.loadClass(paramString);
      } catch (ClassNotFoundException classNotFoundException) {
        if (paramBoolean) {
          ClassLoader classLoader = ObjectFactory.class.getClassLoader();
          if (classLoader == null) {
            clazz = Class.forName(paramString);
          } else if (paramClassLoader != classLoader) {
            paramClassLoader = classLoader;
            clazz = paramClassLoader.loadClass(paramString);
          } else {
            throw classNotFoundException;
          } 
        } else {
          throw classNotFoundException;
        } 
      } 
    } 
    return clazz;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\interna\\utils\ObjectFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */