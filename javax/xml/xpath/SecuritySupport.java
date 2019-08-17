package javax.xml.xpath;

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
import java.util.Enumeration;

class SecuritySupport {
  ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
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
  
  InputStream getURLInputStream(final URL url) throws IOException {
    try {
      return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() { return url.openStream(); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  URL getResourceAsURL(final ClassLoader cl, final String name) { return (URL)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            URL uRL;
            if (cl == null) {
              uRL = Object.class.getResource(name);
            } else {
              uRL = cl.getResource(name);
            } 
            return uRL;
          }
        }); }
  
  Enumeration getResources(final ClassLoader cl, final String name) throws IOException {
    try {
      return (Enumeration)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() {
              Enumeration enumeration;
              if (cl == null) {
                enumeration = ClassLoader.getSystemResources(name);
              } else {
                enumeration = cl.getResources(name);
              } 
              return enumeration;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  InputStream getResourceAsStream(final ClassLoader cl, final String name) { return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            InputStream inputStream;
            if (cl == null) {
              inputStream = Object.class.getResourceAsStream(name);
            } else {
              inputStream = cl.getResourceAsStream(name);
            } 
            return inputStream;
          }
        }); }
  
  boolean doesFileExist(final File f) { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return new Boolean(f.exists()); }
        })).booleanValue(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\xpath\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */