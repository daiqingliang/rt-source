package com.sun.xml.internal.bind.v2;

import com.sun.xml.internal.bind.Util;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClassFactory {
  private static final Class[] emptyClass = new Class[0];
  
  private static final Object[] emptyObject = new Object[0];
  
  private static final Logger logger = Util.getClassLogger();
  
  private static final ThreadLocal<Map<Class, WeakReference<Constructor>>> tls = new ThreadLocal<Map<Class, WeakReference<Constructor>>>() {
      public Map<Class, WeakReference<Constructor>> initialValue() { return new WeakHashMap(); }
    };
  
  public static void cleanCache() {
    if (tls != null)
      try {
        tls.remove();
      } catch (Exception exception) {
        logger.log(Level.WARNING, "Unable to clean Thread Local cache of classes used in Unmarshaller: {0}", exception.getLocalizedMessage());
      }  
  }
  
  public static <T> T create0(Class<T> paramClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
    Map map = (Map)tls.get();
    Constructor constructor = null;
    WeakReference weakReference = (WeakReference)map.get(paramClass);
    if (weakReference != null)
      constructor = (Constructor)weakReference.get(); 
    if (constructor == null) {
      try {
        constructor = paramClass.getDeclaredConstructor(emptyClass);
      } catch (NoSuchMethodException noSuchMethodException) {
        NoSuchMethodError noSuchMethodError;
        logger.log(Level.INFO, "No default constructor found on " + paramClass, noSuchMethodException);
        if (paramClass.getDeclaringClass() != null && !Modifier.isStatic(paramClass.getModifiers())) {
          noSuchMethodError = new NoSuchMethodError(Messages.NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS.format(new Object[] { paramClass.getName() }));
        } else {
          noSuchMethodError = new NoSuchMethodError(noSuchMethodException.getMessage());
        } 
        noSuchMethodError.initCause(noSuchMethodException);
        throw noSuchMethodError;
      } 
      int i = paramClass.getModifiers();
      if (!Modifier.isPublic(i) || !Modifier.isPublic(constructor.getModifiers()))
        try {
          constructor.setAccessible(true);
        } catch (SecurityException securityException) {
          logger.log(Level.FINE, "Unable to make the constructor of " + paramClass + " accessible", securityException);
          throw securityException;
        }  
      map.put(paramClass, new WeakReference(constructor));
    } 
    return (T)constructor.newInstance(emptyObject);
  }
  
  public static <T> T create(Class<T> paramClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
    try {
      return (T)create0(paramClass);
    } catch (InstantiationException instantiationException) {
      logger.log(Level.INFO, "failed to create a new instance of " + paramClass, instantiationException);
      throw new InstantiationError(instantiationException.toString());
    } catch (IllegalAccessException illegalAccessException) {
      logger.log(Level.INFO, "failed to create a new instance of " + paramClass, illegalAccessException);
      throw new IllegalAccessError(illegalAccessException.toString());
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new IllegalStateException(throwable);
    } 
  }
  
  public static Object create(Method paramMethod) {
    ExceptionInInitializerError exceptionInInitializerError;
    try {
      return paramMethod.invoke(null, emptyObject);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      if (throwable instanceof Error)
        throw (Error)throwable; 
      throw new IllegalStateException(throwable);
    } catch (IllegalAccessException illegalAccessException) {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), illegalAccessException);
      throw new IllegalAccessError(illegalAccessException.toString());
    } catch (IllegalArgumentException illegalArgumentException) {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), illegalArgumentException);
      exceptionInInitializerError = illegalArgumentException;
    } catch (NullPointerException nullPointerException) {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), nullPointerException);
      exceptionInInitializerError = nullPointerException;
    } catch (ExceptionInInitializerError exceptionInInitializerError1) {
      logger.log(Level.INFO, "failed to create a new instance of " + paramMethod.getReturnType().getName(), exceptionInInitializerError1);
      exceptionInInitializerError = exceptionInInitializerError1;
    } 
    NoSuchMethodError noSuchMethodError = new NoSuchMethodError(exceptionInInitializerError.getMessage());
    noSuchMethodError.initCause(exceptionInInitializerError);
    throw noSuchMethodError;
  }
  
  public static <T> Class<? extends T> inferImplClass(Class<T> paramClass, Class[] paramArrayOfClass) {
    if (!paramClass.isInterface())
      return paramClass; 
    for (Class clazz : paramArrayOfClass) {
      if (paramClass.isAssignableFrom(clazz))
        return clazz.asSubclass(paramClass); 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\ClassFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */