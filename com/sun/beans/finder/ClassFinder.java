package com.sun.beans.finder;

import sun.reflect.misc.ReflectUtil;

public final class ClassFinder {
  public static Class<?> findClass(String paramString) throws ClassNotFoundException {
    ReflectUtil.checkPackageAccess(paramString);
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null)
        classLoader = ClassLoader.getSystemClassLoader(); 
      if (classLoader != null)
        return Class.forName(paramString, false, classLoader); 
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (SecurityException securityException) {}
    return Class.forName(paramString);
  }
  
  public static Class<?> findClass(String paramString, ClassLoader paramClassLoader) throws ClassNotFoundException {
    ReflectUtil.checkPackageAccess(paramString);
    if (paramClassLoader != null)
      try {
        return Class.forName(paramString, false, paramClassLoader);
      } catch (ClassNotFoundException classNotFoundException) {
      
      } catch (SecurityException securityException) {} 
    return findClass(paramString);
  }
  
  public static Class<?> resolveClass(String paramString) throws ClassNotFoundException { return resolveClass(paramString, null); }
  
  public static Class<?> resolveClass(String paramString, ClassLoader paramClassLoader) throws ClassNotFoundException {
    Class clazz = PrimitiveTypeMap.getType(paramString);
    return (clazz == null) ? findClass(paramString, paramClassLoader) : clazz;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\ClassFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */