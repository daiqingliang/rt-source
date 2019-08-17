package com.sun.naming.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.naming.NamingEnumeration;

final class VersionHelper12 extends VersionHelper {
  private static final String TRUST_URL_CODEBASE_PROPERTY = "com.sun.jndi.ldap.object.trustURLCodebase";
  
  private static final String trustURLCodebase = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
        public String run() {
          try {
            return System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
          } catch (SecurityException securityException) {
            return "false";
          } 
        }
      });
  
  public Class<?> loadClass(String paramString) throws ClassNotFoundException { return loadClass(paramString, getContextClassLoader()); }
  
  Class<?> loadClass(String paramString, ClassLoader paramClassLoader) throws ClassNotFoundException { return Class.forName(paramString, true, paramClassLoader); }
  
  public Class<?> loadClass(String paramString1, String paramString2) throws ClassNotFoundException, MalformedURLException {
    if ("true".equalsIgnoreCase(trustURLCodebase)) {
      ClassLoader classLoader = getContextClassLoader();
      URLClassLoader uRLClassLoader = URLClassLoader.newInstance(getUrlArray(paramString2), classLoader);
      return loadClass(paramString1, uRLClassLoader);
    } 
    return null;
  }
  
  String getJndiProperty(final int i) { return (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() {
            try {
              return System.getProperty(VersionHelper.PROPS[i]);
            } catch (SecurityException securityException) {
              return null;
            } 
          }
        }); }
  
  String[] getJndiProperties() {
    Properties properties = (Properties)AccessController.doPrivileged(new PrivilegedAction<Properties>() {
          public Properties run() {
            try {
              return System.getProperties();
            } catch (SecurityException securityException) {
              return null;
            } 
          }
        });
    if (properties == null)
      return null; 
    String[] arrayOfString = new String[PROPS.length];
    for (byte b = 0; b < PROPS.length; b++)
      arrayOfString[b] = properties.getProperty(PROPS[b]); 
    return arrayOfString;
  }
  
  InputStream getResourceAsStream(final Class<?> c, final String name) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
          public InputStream run() { return c.getResourceAsStream(name); }
        }); }
  
  InputStream getJavaHomeLibStream(final String filename) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
          public InputStream run() {
            try {
              String str1 = System.getProperty("java.home");
              if (str1 == null)
                return null; 
              String str2 = str1 + File.separator + "lib" + File.separator + filename;
              return new FileInputStream(str2);
            } catch (Exception exception) {
              return null;
            } 
          }
        }); }
  
  NamingEnumeration<InputStream> getResources(final ClassLoader cl, final String name) throws IOException {
    Enumeration enumeration;
    try {
      enumeration = (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction<Enumeration<URL>>() {
            public Enumeration<URL> run() throws IOException { return (cl == null) ? ClassLoader.getSystemResources(name) : cl.getResources(name); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
    return new InputStreamEnumeration(enumeration);
  }
  
  ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null)
              classLoader = ClassLoader.getSystemClassLoader(); 
            return classLoader;
          }
        }); }
  
  class InputStreamEnumeration extends Object implements NamingEnumeration<InputStream> {
    private final Enumeration<URL> urls;
    
    private InputStream nextElement = null;
    
    InputStreamEnumeration(Enumeration<URL> param1Enumeration) { this.urls = param1Enumeration; }
    
    private InputStream getNextElement() { return (InputStream)AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
              while (VersionHelper12.InputStreamEnumeration.this.urls.hasMoreElements()) {
                try {
                  return ((URL)VersionHelper12.InputStreamEnumeration.this.urls.nextElement()).openStream();
                } catch (IOException iOException) {}
              } 
              return null;
            }
          }); }
    
    public boolean hasMore() {
      if (this.nextElement != null)
        return true; 
      this.nextElement = getNextElement();
      return (this.nextElement != null);
    }
    
    public boolean hasMoreElements() { return hasMore(); }
    
    public InputStream next() {
      if (hasMore()) {
        InputStream inputStream = this.nextElement;
        this.nextElement = null;
        return inputStream;
      } 
      throw new NoSuchElementException();
    }
    
    public InputStream nextElement() { return next(); }
    
    public void close() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\naming\internal\VersionHelper12.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */