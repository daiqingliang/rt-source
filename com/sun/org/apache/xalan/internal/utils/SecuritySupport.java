package com.sun.org.apache.xalan.internal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public final class SecuritySupport {
  private static final SecuritySupport securitySupport = new SecuritySupport();
  
  static final Properties cacheProps = new Properties();
  
  public static SecuritySupport getInstance() { return securitySupport; }
  
  public static ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  static ClassLoader getSystemClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = ClassLoader.getSystemClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  static ClassLoader getParentClassLoader(final ClassLoader cl) { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = cl.getParent();
            } catch (SecurityException securityException) {}
            return (classLoader == cl) ? null : classLoader;
          }
        }); }
  
  public static String getSystemProperty(final String propName) { return (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperty(propName); }
        }); }
  
  public static String getSystemProperty(final String propName, final String def) { return (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperty(propName, def); }
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
  
  public static InputStream getResourceAsStream(String paramString) { return (System.getSecurityManager() != null) ? getResourceAsStream(null, paramString) : getResourceAsStream(ObjectFactory.findClassLoader(), paramString); }
  
  public static InputStream getResourceAsStream(final ClassLoader cl, final String name) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
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
  
  public static boolean getFileExists(final File f) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return f.exists() ? Boolean.TRUE : Boolean.FALSE; }
        })).booleanValue(); }
  
  static long getLastModified(final File f) { return ((Long)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return new Long(f.lastModified()); }
        })).longValue(); }
  
  public static String sanitizePath(String paramString) {
    if (paramString == null)
      return ""; 
    int i = paramString.lastIndexOf("/");
    return (i > 0) ? paramString.substring(i + 1, paramString.length()) : "";
  }
  
  public static String checkAccess(String paramString1, String paramString2, String paramString3) throws IOException {
    String str;
    if (paramString1 == null || (paramString2 != null && paramString2.equalsIgnoreCase(paramString3)))
      return null; 
    if (paramString1.indexOf(":") == -1) {
      str = "file";
    } else {
      URL uRL = new URL(paramString1);
      str = uRL.getProtocol();
      if (str.equalsIgnoreCase("jar")) {
        String str1 = uRL.getPath();
        str = str1.substring(0, str1.indexOf(":"));
      } 
    } 
    return isProtocolAllowed(str, paramString2) ? null : str;
  }
  
  private static boolean isProtocolAllowed(String paramString1, String paramString2) {
    if (paramString2 == null)
      return false; 
    String[] arrayOfString = paramString2.split(",");
    for (String str : arrayOfString) {
      str = str.trim();
      if (str.equalsIgnoreCase(paramString1))
        return true; 
    } 
    return false;
  }
  
  public static String getJAXPSystemProperty(String paramString) {
    String str = getSystemProperty(paramString);
    if (str == null)
      str = readJAXPProperty(paramString); 
    return str;
  }
  
  static String readJAXPProperty(String paramString) {
    String str = null;
    fileInputStream = null;
    try {
      if (firstTime)
        synchronized (cacheProps) {
          if (firstTime) {
            String str1 = getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File file = new File(str1);
            if (getFileExists(file)) {
              fileInputStream = getFileInputStream(file);
              cacheProps.load(fileInputStream);
            } 
            firstTime = false;
          } 
        }  
      str = cacheProps.getProperty(paramString);
    } catch (Exception exception) {
    
    } finally {
      if (fileInputStream != null)
        try {
          fileInputStream.close();
        } catch (IOException iOException) {} 
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\interna\\utils\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */