package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Enumeration;

class SecuritySupport {
  public static ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  public static InputStream getResourceAsStream(final Class c, final String name) throws IOException {
    try {
      return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() { return c.getResourceAsStream(name); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
  
  public static URL[] getResources(final ClassLoader cl, final String name) { return (URL[])AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            URL[] arrayOfURL = null;
            try {
              ArrayList arrayList = new ArrayList();
              Enumeration enumeration = cl.getResources(name);
              while (enumeration != null && enumeration.hasMoreElements()) {
                URL uRL = (URL)enumeration.nextElement();
                if (uRL != null)
                  arrayList.add(uRL); 
              } 
              if (arrayList.size() > 0) {
                arrayOfURL = new URL[arrayList.size()];
                arrayOfURL = (URL[])arrayList.toArray(arrayOfURL);
              } 
            } catch (IOException iOException) {
            
            } catch (SecurityException securityException) {}
            return arrayOfURL;
          }
        }); }
  
  public static URL[] getSystemResources(final String name) { return (URL[])AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            URL[] arrayOfURL = null;
            try {
              ArrayList arrayList = new ArrayList();
              Enumeration enumeration = ClassLoader.getSystemResources(name);
              while (enumeration != null && enumeration.hasMoreElements()) {
                URL uRL = (URL)enumeration.nextElement();
                if (uRL != null)
                  arrayList.add(uRL); 
              } 
              if (arrayList.size() > 0) {
                arrayOfURL = new URL[arrayList.size()];
                arrayOfURL = (URL[])arrayList.toArray(arrayOfURL);
              } 
            } catch (IOException iOException) {
            
            } catch (SecurityException securityException) {}
            return arrayOfURL;
          }
        }); }
  
  public static InputStream openStream(final URL url) throws IOException {
    try {
      return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() { return url.openStream(); }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw (IOException)privilegedActionException.getException();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\SecuritySupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */