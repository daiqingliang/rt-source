package javax.xml.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class SecuritySupport {
  ClassLoader getContextClassLoader() throws SecurityException { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null)
              classLoader = ClassLoader.getSystemClassLoader(); 
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */