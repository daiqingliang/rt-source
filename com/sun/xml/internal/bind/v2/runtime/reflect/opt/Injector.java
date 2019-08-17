package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

final class Injector {
  private static final ReentrantReadWriteLock irwl = new ReentrantReadWriteLock();
  
  private static final Lock ir = irwl.readLock();
  
  private static final Lock iw = irwl.writeLock();
  
  private static final Map<ClassLoader, WeakReference<Injector>> injectors = new WeakHashMap();
  
  private static final Logger logger = Util.getClassLogger();
  
  private final Map<String, Class> classes = new HashMap();
  
  private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
  
  private final Lock r = this.rwl.readLock();
  
  private final Lock w = this.rwl.writeLock();
  
  private final ClassLoader parent;
  
  private final boolean loadable;
  
  private static final Method defineClass;
  
  private static final Method resolveClass;
  
  private static final Method findLoadedClass;
  
  static Class inject(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfByte) {
    Injector injector = get(paramClassLoader);
    return (injector != null) ? injector.inject(paramString, paramArrayOfByte) : null;
  }
  
  static Class find(ClassLoader paramClassLoader, String paramString) {
    Injector injector = get(paramClassLoader);
    return (injector != null) ? injector.find(paramString) : null;
  }
  
  private static Injector get(ClassLoader paramClassLoader) {
    WeakReference weakReference;
    Injector injector = null;
    ir.lock();
    try {
      weakReference = (WeakReference)injectors.get(paramClassLoader);
    } finally {
      ir.unlock();
    } 
    if (weakReference != null)
      injector = (Injector)weakReference.get(); 
    if (injector == null)
      try {
        weakReference = new WeakReference(injector = new Injector(paramClassLoader));
        iw.lock();
        try {
          if (!injectors.containsKey(paramClassLoader))
            injectors.put(paramClassLoader, weakReference); 
        } finally {
          iw.unlock();
        } 
      } catch (SecurityException securityException) {
        logger.log(Level.FINE, "Unable to set up a back-door for the injector", securityException);
        return null;
      }  
    return injector;
  }
  
  private Injector(ClassLoader paramClassLoader) {
    this.parent = paramClassLoader;
    assert paramClassLoader != null;
    boolean bool = false;
    try {
      bool = (paramClassLoader.loadClass(com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.class.getName()) == com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.class) ? 1 : 0;
    } catch (ClassNotFoundException classNotFoundException) {}
    this.loadable = bool;
  }
  
  private Class inject(String paramString, byte[] paramArrayOfByte) {
    if (!this.loadable)
      return null; 
    bool1 = false;
    bool2 = false;
    try {
      this.r.lock();
      bool2 = true;
      Class clazz = (Class)this.classes.get(paramString);
      this.r.unlock();
      bool2 = false;
      if (clazz == null) {
        try {
          clazz = (Class)findLoadedClass.invoke(this.parent, new Object[] { paramString.replace('/', '.') });
        } catch (IllegalArgumentException illegalArgumentException) {
          logger.log(Level.FINE, "Unable to find " + paramString, illegalArgumentException);
        } catch (IllegalAccessException illegalAccessException) {
          logger.log(Level.FINE, "Unable to find " + paramString, illegalAccessException);
        } catch (InvocationTargetException invocationTargetException) {
          Throwable throwable = invocationTargetException.getTargetException();
          logger.log(Level.FINE, "Unable to find " + paramString, throwable);
        } 
        if (clazz != null) {
          this.w.lock();
          bool1 = true;
          this.classes.put(paramString, clazz);
          this.w.unlock();
          bool1 = false;
          return clazz;
        } 
      } 
      if (clazz == null) {
        this.r.lock();
        bool2 = true;
        clazz = (Class)this.classes.get(paramString);
        this.r.unlock();
        bool2 = false;
        if (clazz == null) {
          try {
            clazz = (Class)defineClass.invoke(this.parent, new Object[] { paramString.replace('/', '.'), paramArrayOfByte, Integer.valueOf(0), Integer.valueOf(paramArrayOfByte.length) });
            resolveClass.invoke(this.parent, new Object[] { clazz });
          } catch (IllegalAccessException illegalAccessException) {
            logger.log(Level.FINE, "Unable to inject " + paramString, illegalAccessException);
            return null;
          } catch (InvocationTargetException invocationTargetException) {
            Throwable throwable = invocationTargetException.getTargetException();
            if (throwable instanceof LinkageError) {
              logger.log(Level.FINE, "duplicate class definition bug occured? Please report this : " + paramString, throwable);
            } else {
              logger.log(Level.FINE, "Unable to inject " + paramString, throwable);
            } 
            return null;
          } catch (SecurityException securityException) {
            logger.log(Level.FINE, "Unable to inject " + paramString, securityException);
            return null;
          } catch (LinkageError linkageError) {
            logger.log(Level.FINE, "Unable to inject " + paramString, linkageError);
            return null;
          } 
          this.w.lock();
          bool1 = true;
          if (!this.classes.containsKey(paramString))
            this.classes.put(paramString, clazz); 
          this.w.unlock();
          bool1 = false;
        } 
      } 
      return clazz;
    } finally {
      if (bool2)
        this.r.unlock(); 
      if (bool1)
        this.w.unlock(); 
    } 
  }
  
  private Class find(String paramString) {
    this.r.lock();
    try {
      return (Class)this.classes.get(paramString);
    } finally {
      this.r.unlock();
    } 
  }
  
  static  {
    try {
      defineClass = ClassLoader.class.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class, int.class, int.class });
      resolveClass = ClassLoader.class.getDeclaredMethod("resolveClass", new Class[] { Class.class });
      findLoadedClass = ClassLoader.class.getDeclaredMethod("findLoadedClass", new Class[] { String.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } 
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            defineClass.setAccessible(true);
            resolveClass.setAccessible(true);
            findLoadedClass.setAccessible(true);
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\Injector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */