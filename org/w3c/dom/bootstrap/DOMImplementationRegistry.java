package org.w3c.dom.bootstrap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementationSource;

public final class DOMImplementationRegistry {
  public static final String PROPERTY = "org.w3c.dom.DOMImplementationSourceList";
  
  private static final int DEFAULT_LINE_LENGTH = 80;
  
  private Vector sources;
  
  private static final String FALLBACK_CLASS = "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl";
  
  private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal.dom";
  
  private DOMImplementationRegistry(Vector paramVector) { this.sources = paramVector; }
  
  public static DOMImplementationRegistry newInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException {
    Vector vector = new Vector();
    ClassLoader classLoader = getClassLoader();
    String str = getSystemProperty("org.w3c.dom.DOMImplementationSourceList");
    if (str == null)
      str = getServiceValue(classLoader); 
    if (str == null)
      str = "com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl"; 
    if (str != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str);
      while (stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        boolean bool = false;
        if (System.getSecurityManager() != null && str1 != null && str1.startsWith("com.sun.org.apache.xerces.internal.dom"))
          bool = true; 
        Class clazz = null;
        if (classLoader != null && !bool) {
          clazz = classLoader.loadClass(str1);
        } else {
          clazz = Class.forName(str1);
        } 
        DOMImplementationSource dOMImplementationSource = (DOMImplementationSource)clazz.newInstance();
        vector.addElement(dOMImplementationSource);
      } 
    } 
    return new DOMImplementationRegistry(vector);
  }
  
  public DOMImplementation getDOMImplementation(String paramString) {
    int i = this.sources.size();
    Object object = null;
    for (byte b = 0; b < i; b++) {
      DOMImplementationSource dOMImplementationSource = (DOMImplementationSource)this.sources.elementAt(b);
      DOMImplementation dOMImplementation = dOMImplementationSource.getDOMImplementation(paramString);
      if (dOMImplementation != null)
        return dOMImplementation; 
    } 
    return null;
  }
  
  public DOMImplementationList getDOMImplementationList(String paramString) {
    final Vector implementations = new Vector();
    int i = this.sources.size();
    for (byte b = 0; b < i; b++) {
      DOMImplementationSource dOMImplementationSource = (DOMImplementationSource)this.sources.elementAt(b);
      DOMImplementationList dOMImplementationList = dOMImplementationSource.getDOMImplementationList(paramString);
      for (byte b1 = 0; b1 < dOMImplementationList.getLength(); b1++) {
        DOMImplementation dOMImplementation = dOMImplementationList.item(b1);
        vector.addElement(dOMImplementation);
      } 
    } 
    return new DOMImplementationList() {
        public DOMImplementation item(int param1Int) {
          if (param1Int >= 0 && param1Int < implementations.size())
            try {
              return (DOMImplementation)implementations.elementAt(param1Int);
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
              return null;
            }  
          return null;
        }
        
        public int getLength() { return implementations.size(); }
      };
  }
  
  public void addSource(DOMImplementationSource paramDOMImplementationSource) {
    if (paramDOMImplementationSource == null)
      throw new NullPointerException(); 
    if (!this.sources.contains(paramDOMImplementationSource))
      this.sources.addElement(paramDOMImplementationSource); 
  }
  
  private static ClassLoader getClassLoader() {
    try {
      ClassLoader classLoader = getContextClassLoader();
      if (classLoader != null)
        return classLoader; 
    } catch (Exception exception) {
      return DOMImplementationRegistry.class.getClassLoader();
    } 
    return DOMImplementationRegistry.class.getClassLoader();
  }
  
  private static String getServiceValue(ClassLoader paramClassLoader) {
    String str = "META-INF/services/org.w3c.dom.DOMImplementationSourceList";
    try {
      InputStream inputStream = getResourceAsStream(paramClassLoader, str);
      if (inputStream != null) {
        BufferedReader bufferedReader;
        try {
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
        } 
        String str1 = bufferedReader.readLine();
        bufferedReader.close();
        if (str1 != null && str1.length() > 0)
          return str1; 
      } 
    } catch (Exception exception) {
      return null;
    } 
    return null;
  }
  
  private static boolean isJRE11() {
    try {
      Class clazz = Class.forName("java.security.AccessController");
      return false;
    } catch (Exception exception) {
      return true;
    } 
  }
  
  private static ClassLoader getContextClassLoader() { return isJRE11() ? null : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
  
  private static String getSystemProperty(final String name) { return isJRE11() ? System.getProperty(paramString) : (String)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperty(name); }
        }); }
  
  private static InputStream getResourceAsStream(final ClassLoader classLoader, final String name) {
    if (isJRE11()) {
      InputStream inputStream;
      if (paramClassLoader == null) {
        inputStream = ClassLoader.getSystemResourceAsStream(paramString);
      } else {
        inputStream = paramClassLoader.getResourceAsStream(paramString);
      } 
      return inputStream;
    } 
    return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            InputStream inputStream;
            if (classLoader == null) {
              inputStream = ClassLoader.getSystemResourceAsStream(name);
            } else {
              inputStream = classLoader.getResourceAsStream(name);
            } 
            return inputStream;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\bootstrap\DOMImplementationRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */