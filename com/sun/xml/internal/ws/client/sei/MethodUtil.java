package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

class MethodUtil {
  private static final Logger LOGGER;
  
  private static final Method INVOKE_METHOD;
  
  static Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws IllegalAccessException, InvocationTargetException {
    if (INVOKE_METHOD != null) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Invoking method using sun.reflect.misc.MethodUtil"); 
      try {
        return INVOKE_METHOD.invoke(null, new Object[] { paramMethod, paramObject, paramArrayOfObject });
      } catch (InvocationTargetException invocationTargetException) {
        throw unwrapException(invocationTargetException);
      } 
    } 
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Invoking method directly, probably non-Oracle JVM"); 
    return paramMethod.invoke(paramObject, paramArrayOfObject);
  }
  
  private static InvocationTargetException unwrapException(InvocationTargetException paramInvocationTargetException) {
    Throwable throwable = paramInvocationTargetException.getTargetException();
    if (throwable != null && throwable instanceof InvocationTargetException) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Unwrapping invocation target exception"); 
      return (InvocationTargetException)throwable;
    } 
    return paramInvocationTargetException;
  }
  
  static  {
    Object object;
    LOGGER = Logger.getLogger(MethodUtil.class.getName());
    try {
      Class clazz = Class.forName("sun.reflect.misc.MethodUtil");
      object = clazz.getMethod("invoke", new Class[] { Method.class, Object.class, Object[].class });
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil found; it will be used to invoke methods."); 
    } catch (Throwable throwable) {
      object = null;
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Class sun.reflect.misc.MethodUtil not found, probably non-Oracle JVM"); 
    } 
    INVOKE_METHOD = object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\MethodUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */