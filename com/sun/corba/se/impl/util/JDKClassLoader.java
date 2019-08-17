package com.sun.corba.se.impl.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import sun.corba.Bridge;

class JDKClassLoader {
  private static final JDKClassLoaderCache classCache = new JDKClassLoaderCache(null);
  
  private static final Bridge bridge = (Bridge)AccessController.doPrivileged(new PrivilegedAction() {
        public Object run() { return Bridge.get(); }
      });
  
  static Class loadClass(Class paramClass, String paramString) throws ClassNotFoundException {
    ClassLoader classLoader;
    if (paramString == null)
      throw new NullPointerException(); 
    if (paramString.length() == 0)
      throw new ClassNotFoundException(); 
    if (paramClass != null) {
      classLoader = paramClass.getClassLoader();
    } else {
      classLoader = bridge.getLatestUserDefinedLoader();
    } 
    Object object = classCache.createKey(paramString, classLoader);
    if (classCache.knownToFail(object))
      throw new ClassNotFoundException(paramString); 
    try {
      return Class.forName(paramString, false, classLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      classCache.recordFailure(object);
      throw classNotFoundException;
    } 
  }
  
  private static class JDKClassLoaderCache {
    private final Map cache = Collections.synchronizedMap(new WeakHashMap());
    
    private static final Object KNOWN_TO_FAIL = new Object();
    
    private JDKClassLoaderCache() {}
    
    public final void recordFailure(Object param1Object) { this.cache.put(param1Object, KNOWN_TO_FAIL); }
    
    public final Object createKey(String param1String, ClassLoader param1ClassLoader) { return new CacheKey(param1String, param1ClassLoader); }
    
    public final boolean knownToFail(Object param1Object) { return (this.cache.get(param1Object) == KNOWN_TO_FAIL); }
    
    private static class CacheKey {
      String className;
      
      ClassLoader loader;
      
      public CacheKey(String param2String, ClassLoader param2ClassLoader) {
        this.className = param2String;
        this.loader = param2ClassLoader;
      }
      
      public int hashCode() { return (this.loader == null) ? this.className.hashCode() : (this.className.hashCode() ^ this.loader.hashCode()); }
      
      public boolean equals(Object param2Object) {
        try {
          if (param2Object == null)
            return false; 
          CacheKey cacheKey = (CacheKey)param2Object;
          return (this.className.equals(cacheKey.className) && this.loader == cacheKey.loader);
        } catch (ClassCastException classCastException) {
          return false;
        } 
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\JDKClassLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */