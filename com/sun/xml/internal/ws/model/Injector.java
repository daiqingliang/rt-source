package com.sun.xml.internal.ws.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

final class Injector {
  private static final Logger LOGGER = Logger.getLogger(Injector.class.getName());
  
  private static final Method defineClass;
  
  private static final Method resolveClass;
  
  private static final Method getPackage;
  
  private static final Method definePackage;
  
  static Class inject(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte) {
    try {
      return paramClassLoader.loadClass(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      try {
        int i = paramString.lastIndexOf('.');
        if (i != -1) {
          String str = paramString.substring(0, i);
          Package package = (Package)getPackage.invoke(paramClassLoader, new Object[] { str });
          if (package == null)
            definePackage.invoke(paramClassLoader, new Object[] { str, null, null, null, null, null, null, null }); 
        } 
        Class clazz = (Class)defineClass.invoke(paramClassLoader, new Object[] { paramString.replace('/', '.'), paramArrayOfByte, Integer.valueOf(0), Integer.valueOf(paramArrayOfByte.length) });
        resolveClass.invoke(paramClassLoader, new Object[] { clazz });
        return clazz;
      } catch (IllegalAccessException classNotFoundException) {
        LOGGER.log(Level.FINE, "Unable to inject " + paramString, classNotFoundException);
        throw new WebServiceException(classNotFoundException);
      } catch (InvocationTargetException classNotFoundException) {
        LOGGER.log(Level.FINE, "Unable to inject " + paramString, classNotFoundException);
        throw new WebServiceException(classNotFoundException);
      } 
    } 
  }
  
  static  {
    try {
      defineClass = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });
      resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", new Class[] { Class.class });
      getPackage = ClassLoader.class.getDeclaredMethod("getPackage", new Class[] { String.class });
      definePackage = ClassLoader.class.getDeclaredMethod("definePackage", new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, String.class, java.net.URL.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            defineClass.setAccessible(true);
            resolveClass.setAccessible(true);
            getPackage.setAccessible(true);
            definePackage.setAccessible(true);
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\Injector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */