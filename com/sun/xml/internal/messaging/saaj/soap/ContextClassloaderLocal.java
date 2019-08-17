package com.sun.xml.internal.messaging.saaj.soap;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

abstract class ContextClassloaderLocal<V> extends Object {
  private static final String FAILED_TO_CREATE_NEW_INSTANCE = "FAILED_TO_CREATE_NEW_INSTANCE";
  
  private WeakHashMap<ClassLoader, V> CACHE = new WeakHashMap();
  
  public V get() throws Error {
    ClassLoader classLoader = getContextClassLoader();
    Object object = this.CACHE.get(classLoader);
    if (object == null) {
      object = createNewInstance();
      this.CACHE.put(classLoader, object);
    } 
    return (V)object;
  }
  
  public void set(V paramV) { this.CACHE.put(getContextClassLoader(), paramV); }
  
  protected abstract V initialValue() throws Error;
  
  private V createNewInstance() throws Error {
    try {
      return (V)initialValue();
    } catch (Exception exception) {
      throw new Error(format("FAILED_TO_CREATE_NEW_INSTANCE", new Object[] { getClass().getName() }), exception);
    } 
  }
  
  private static String format(String paramString, Object... paramVarArgs) {
    String str = ResourceBundle.getBundle(ContextClassloaderLocal.class.getName()).getString(paramString);
    return MessageFormat.format(str, paramVarArgs);
  }
  
  private static ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            ClassLoader classLoader = null;
            try {
              classLoader = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return classLoader;
          }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ContextClassloaderLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */