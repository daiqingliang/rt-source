package com.sun.jndi.ldap;

import java.net.MalformedURLException;
import java.net.URL;

abstract class VersionHelper {
  private static VersionHelper helper = null;
  
  static VersionHelper getVersionHelper() { return helper; }
  
  abstract ClassLoader getURLClassLoader(String[] paramArrayOfString) throws MalformedURLException;
  
  protected static URL[] getUrlArray(String[] paramArrayOfString) throws MalformedURLException {
    URL[] arrayOfURL = new URL[paramArrayOfString.length];
    for (byte b = 0; b < arrayOfURL.length; b++)
      arrayOfURL[b] = new URL(paramArrayOfString[b]); 
    return arrayOfURL;
  }
  
  abstract Class<?> loadClass(String paramString) throws ClassNotFoundException;
  
  abstract Thread createThread(Runnable paramRunnable);
  
  static  {
    try {
      helper = (VersionHelper)Class.forName("java.net.URLClassLoader").forName("java.security.PrivilegedAction").forName("com.sun.jndi.ldap.VersionHelper12").newInstance();
    } catch (Exception exception) {}
    if (helper == null)
      try {
        helper = (VersionHelper)Class.forName("com.sun.jndi.ldap.VersionHelper11").newInstance();
      } catch (Exception exception) {} 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\VersionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */