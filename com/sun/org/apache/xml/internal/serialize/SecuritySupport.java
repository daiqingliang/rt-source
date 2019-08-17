package com.sun.org.apache.xml.internal.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

final class SecuritySupport {
  private static final SecuritySupport securitySupport = new SecuritySupport();
  
  static SecuritySupport getInstance() { return securitySupport; }
  
  ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  ClassLoader getSystemClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = ClassLoader.getSystemClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  ClassLoader getParentClassLoader(final ClassLoader cl) { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = cl.getParent();
            } catch (SecurityException securityException) {}
            return (classLoader == cl) ? null : classLoader;
          }
        }); }
  
  String getSystemProperty(final String propName) { return (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperty(propName); }
        }); }
  
  FileInputStream getFileInputStream(final File file) throws FileNotFoundException {
    try {
      return (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() { return new FileInputStream(file); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (FileNotFoundException)privilegedActionException.getException();
    } 
  }
  
  InputStream getResourceAsStream(final ClassLoader cl, final String name) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            InputStream inputStream;
            if (cl == null) {
              inputStream = ClassLoader.getSystemResourceAsStream(name);
            } else {
              inputStream = cl.getResourceAsStream(name);
            } 
            return inputStream;
          }
        }); }
  
  boolean getFileExists(final File f) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return new Boolean(f.exists()); }
        })).booleanValue(); }
  
  long getLastModified(final File f) { return ((Long)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return new Long(f.lastModified()); }
        })).longValue(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */