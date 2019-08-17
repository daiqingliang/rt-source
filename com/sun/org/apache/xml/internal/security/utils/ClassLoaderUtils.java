package com.sun.org.apache.xml.internal.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ClassLoaderUtils {
  private static final Logger log = Logger.getLogger(ClassLoaderUtils.class.getName());
  
  static URL getResource(String paramString, Class<?> paramClass) {
    URL uRL = Thread.currentThread().getContextClassLoader().getResource(paramString);
    if (uRL == null && paramString.startsWith("/"))
      uRL = Thread.currentThread().getContextClassLoader().getResource(paramString.substring(1)); 
    ClassLoader classLoader = ClassLoaderUtils.class.getClassLoader();
    if (classLoader == null)
      classLoader = ClassLoader.getSystemClassLoader(); 
    if (uRL == null)
      uRL = classLoader.getResource(paramString); 
    if (uRL == null && paramString.startsWith("/"))
      uRL = classLoader.getResource(paramString.substring(1)); 
    if (uRL == null) {
      ClassLoader classLoader1 = paramClass.getClassLoader();
      if (classLoader1 != null)
        uRL = classLoader1.getResource(paramString); 
    } 
    if (uRL == null)
      uRL = paramClass.getResource(paramString); 
    return (uRL == null && paramString != null && paramString.charAt(0) != '/') ? getResource('/' + paramString, paramClass) : uRL;
  }
  
  static List<URL> getResources(String paramString, Class<?> paramClass) {
    ArrayList arrayList = new ArrayList();
    Enumeration<URL> enumeration = new Enumeration<URL>() {
        public boolean hasMoreElements() { return false; }
        
        public URL nextElement() { return null; }
      };
    try {
      enumeration = Thread.currentThread().getContextClassLoader().getResources(paramString);
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, iOException.getMessage(), iOException); 
    } 
    if (!enumeration.hasMoreElements() && paramString.startsWith("/"))
      try {
        enumeration = Thread.currentThread().getContextClassLoader().getResources(paramString.substring(1));
      } catch (IOException iOException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, iOException.getMessage(), iOException); 
      }  
    ClassLoader classLoader = ClassLoaderUtils.class.getClassLoader();
    if (classLoader == null)
      classLoader = ClassLoader.getSystemClassLoader(); 
    if (!enumeration.hasMoreElements())
      try {
        enumeration = classLoader.getResources(paramString);
      } catch (IOException iOException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, iOException.getMessage(), iOException); 
      }  
    if (!enumeration.hasMoreElements() && paramString.startsWith("/"))
      try {
        enumeration = classLoader.getResources(paramString.substring(1));
      } catch (IOException iOException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, iOException.getMessage(), iOException); 
      }  
    if (!enumeration.hasMoreElements()) {
      ClassLoader classLoader1 = paramClass.getClassLoader();
      if (classLoader1 != null)
        try {
          enumeration = classLoader1.getResources(paramString);
        } catch (IOException iOException) {
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, iOException.getMessage(), iOException); 
        }  
    } 
    if (!enumeration.hasMoreElements()) {
      URL uRL = paramClass.getResource(paramString);
      if (uRL != null)
        arrayList.add(uRL); 
    } 
    while (enumeration.hasMoreElements())
      arrayList.add(enumeration.nextElement()); 
    return (arrayList.isEmpty() && paramString != null && paramString.charAt(0) != '/') ? getResources('/' + paramString, paramClass) : arrayList;
  }
  
  static InputStream getResourceAsStream(String paramString, Class<?> paramClass) {
    URL uRL = getResource(paramString, paramClass);
    try {
      return (uRL != null) ? uRL.openStream() : null;
    } catch (IOException iOException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, iOException.getMessage(), iOException); 
      return null;
    } 
  }
  
  static Class<?> loadClass(String paramString, Class<?> paramClass) throws ClassNotFoundException {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader != null)
        return classLoader.loadClass(paramString); 
    } catch (ClassNotFoundException classNotFoundException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, classNotFoundException.getMessage(), classNotFoundException); 
    } 
    return loadClass2(paramString, paramClass);
  }
  
  private static Class<?> loadClass2(String paramString, Class<?> paramClass) throws ClassNotFoundException {
    try {
      return Class.forName(paramString);
    } catch (ClassNotFoundException classNotFoundException) {
      try {
        if (ClassLoaderUtils.class.getClassLoader() != null)
          return ClassLoaderUtils.class.getClassLoader().loadClass(paramString); 
      } catch (ClassNotFoundException classNotFoundException1) {
        if (paramClass != null && paramClass.getClassLoader() != null)
          return paramClass.getClassLoader().loadClass(paramString); 
      } 
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, classNotFoundException.getMessage(), classNotFoundException); 
      throw classNotFoundException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\ClassLoaderUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */