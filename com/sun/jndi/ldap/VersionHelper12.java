package com.sun.jndi.ldap;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.SharedSecrets;

final class VersionHelper12 extends VersionHelper {
  private static final String TRUST_URL_CODEBASE_PROPERTY = "com.sun.jndi.ldap.object.trustURLCodebase";
  
  private static final String trustURLCodebase = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
        public String run() { return System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false"); }
      });
  
  ClassLoader getURLClassLoader(String[] paramArrayOfString) throws MalformedURLException {
    ClassLoader classLoader = getContextClassLoader();
    return (paramArrayOfString != null && "true".equalsIgnoreCase(trustURLCodebase)) ? URLClassLoader.newInstance(getUrlArray(paramArrayOfString), classLoader) : classLoader;
  }
  
  Class<?> loadClass(String paramString) throws ClassNotFoundException {
    ClassLoader classLoader = getContextClassLoader();
    return Class.forName(paramString, true, classLoader);
  }
  
  private ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return Thread.currentThread().getContextClassLoader(); }
        }); }
  
  Thread createThread(final Runnable r) {
    final AccessControlContext acc = AccessController.getContext();
    return (Thread)AccessController.doPrivileged(new PrivilegedAction<Thread>() {
          public Thread run() { return SharedSecrets.getJavaLangAccess().newThreadWithAcc(r, acc); }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\VersionHelper12.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */