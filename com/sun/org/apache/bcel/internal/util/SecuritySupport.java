package com.sun.org.apache.bcel.internal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class SecuritySupport {
  private static final SecuritySupport securitySupport = new SecuritySupport();
  
  public static SecuritySupport getInstance() { return securitySupport; }
  
  static java.lang.ClassLoader getContextClassLoader() { return (java.lang.ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            java.lang.ClassLoader classLoader = null;
            try {
              classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  static java.lang.ClassLoader getSystemClassLoader() { return (java.lang.ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            java.lang.ClassLoader classLoader = null;
            try {
              classLoader = java.lang.ClassLoader.getSystemClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  static java.lang.ClassLoader getParentClassLoader(final java.lang.ClassLoader cl) { return (java.lang.ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            java.lang.ClassLoader classLoader = null;
            try {
              classLoader = cl.getParent();
            } catch (SecurityException securityException) {}
            return (classLoader == cl) ? null : classLoader;
          }
        }); }
  
  public static String getSystemProperty(final String propName) { return (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperty(propName); }
        }); }
  
  static FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
    try {
      return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() { return new FileInputStream(file); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (FileNotFoundException)privilegedActionException.getException();
    } 
  }
  
  public static InputStream getResourceAsStream(String paramString) { return (System.getSecurityManager() != null) ? getResourceAsStream(null, paramString) : getResourceAsStream(findClassLoader(), paramString); }
  
  public static InputStream getResourceAsStream(final java.lang.ClassLoader cl, final String name) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            InputStream inputStream;
            if (cl == null) {
              inputStream = Object.class.getResourceAsStream("/" + name);
            } else {
              inputStream = cl.getResourceAsStream(name);
            } 
            return inputStream;
          }
        }); }
  
  public static ListResourceBundle getResourceBundle(String paramString) { return getResourceBundle(paramString, Locale.getDefault()); }
  
  public static ListResourceBundle getResourceBundle(final String bundle, final Locale locale) { return (ListResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ListResourceBundle>() {
          public ListResourceBundle run() {
            try {
              return (ListResourceBundle)ResourceBundle.getBundle(bundle, locale);
            } catch (MissingResourceException missingResourceException) {
              try {
                return (ListResourceBundle)ResourceBundle.getBundle(bundle, new Locale("en", "US"));
              } catch (MissingResourceException missingResourceException1) {
                throw new MissingResourceException("Could not load any resource bundle by " + bundle, bundle, "");
              } 
            } 
          }
        }); }
  
  public static String[] getFileList(final File f, final FilenameFilter filter) { return (String[])AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return f.list(filter); }
        }); }
  
  public static boolean getFileExists(final File f) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return f.exists() ? Boolean.TRUE : Boolean.FALSE; }
        })).booleanValue(); }
  
  static long getLastModified(final File f) { return ((Long)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return new Long(f.lastModified()); }
        })).longValue(); }
  
  public static java.lang.ClassLoader findClassLoader() { return (System.getSecurityManager() != null) ? null : SecuritySupport.class.getClassLoader(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */