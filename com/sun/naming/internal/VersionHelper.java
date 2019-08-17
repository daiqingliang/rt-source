package com.sun.naming.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.NamingEnumeration;

public abstract class VersionHelper {
  private static VersionHelper helper = null;
  
  static final String[] PROPS = { "java.naming.factory.initial", "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.provider.url", "java.naming.dns.url", "java.naming.factory.control" };
  
  public static final int INITIAL_CONTEXT_FACTORY = 0;
  
  public static final int OBJECT_FACTORIES = 1;
  
  public static final int URL_PKG_PREFIXES = 2;
  
  public static final int STATE_FACTORIES = 3;
  
  public static final int PROVIDER_URL = 4;
  
  public static final int DNS_URL = 5;
  
  public static final int CONTROL_FACTORIES = 6;
  
  public static VersionHelper getVersionHelper() { return helper; }
  
  public abstract Class<?> loadClass(String paramString) throws ClassNotFoundException;
  
  abstract Class<?> loadClass(String paramString, ClassLoader paramClassLoader) throws ClassNotFoundException;
  
  public abstract Class<?> loadClass(String paramString1, String paramString2) throws ClassNotFoundException, MalformedURLException;
  
  abstract String getJndiProperty(int paramInt);
  
  abstract String[] getJndiProperties();
  
  abstract InputStream getResourceAsStream(Class<?> paramClass, String paramString);
  
  abstract InputStream getJavaHomeLibStream(String paramString);
  
  abstract NamingEnumeration<InputStream> getResources(ClassLoader paramClassLoader, String paramString) throws IOException;
  
  abstract ClassLoader getContextClassLoader();
  
  protected static URL[] getUrlArray(String paramString) throws MalformedURLException {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString);
    Vector vector = new Vector(10);
    while (stringTokenizer.hasMoreTokens())
      vector.addElement(stringTokenizer.nextToken()); 
    String[] arrayOfString = new String[vector.size()];
    for (byte b1 = 0; b1 < arrayOfString.length; b1++)
      arrayOfString[b1] = (String)vector.elementAt(b1); 
    URL[] arrayOfURL = new URL[arrayOfString.length];
    for (byte b2 = 0; b2 < arrayOfURL.length; b2++)
      arrayOfURL[b2] = new URL(arrayOfString[b2]); 
    return arrayOfURL;
  }
  
  static  {
    helper = new VersionHelper12();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\naming\internal\VersionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */