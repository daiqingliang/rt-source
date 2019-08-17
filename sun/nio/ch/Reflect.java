package sun.nio.ch;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

class Reflect {
  private static void setAccessible(final AccessibleObject ao) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            ao.setAccessible(true);
            return null;
          }
        }); }
  
  static Constructor<?> lookupConstructor(String paramString, Class<?>[] paramArrayOfClass) {
    try {
      Class clazz = Class.forName(paramString);
      Constructor constructor = clazz.getDeclaredConstructor(paramArrayOfClass);
      setAccessible(constructor);
      return constructor;
    } catch (ClassNotFoundException|NoSuchMethodException classNotFoundException) {
      throw new ReflectionError(classNotFoundException);
    } 
  }
  
  static Object invoke(Constructor<?> paramConstructor, Object[] paramArrayOfObject) {
    try {
      return paramConstructor.newInstance(paramArrayOfObject);
    } catch (InstantiationException|IllegalAccessException|InvocationTargetException instantiationException) {
      throw new ReflectionError(instantiationException);
    } 
  }
  
  static Method lookupMethod(String paramString1, String paramString2, Class... paramVarArgs) {
    try {
      Class clazz = Class.forName(paramString1);
      Method method = clazz.getDeclaredMethod(paramString2, paramVarArgs);
      setAccessible(method);
      return method;
    } catch (ClassNotFoundException|NoSuchMethodException classNotFoundException) {
      throw new ReflectionError(classNotFoundException);
    } 
  }
  
  static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject) {
    try {
      return paramMethod.invoke(paramObject, paramArrayOfObject);
    } catch (IllegalAccessException|InvocationTargetException illegalAccessException) {
      throw new ReflectionError(illegalAccessException);
    } 
  }
  
  static Object invokeIO(Method paramMethod, Object paramObject, Object[] paramArrayOfObject) {
    try {
      return paramMethod.invoke(paramObject, paramArrayOfObject);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionError(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      if (IOException.class.isInstance(invocationTargetException.getCause()))
        throw (IOException)invocationTargetException.getCause(); 
      throw new ReflectionError(invocationTargetException);
    } 
  }
  
  static Field lookupField(String paramString1, String paramString2) {
    try {
      Class clazz = Class.forName(paramString1);
      Field field = clazz.getDeclaredField(paramString2);
      setAccessible(field);
      return field;
    } catch (ClassNotFoundException|NoSuchFieldException classNotFoundException) {
      throw new ReflectionError(classNotFoundException);
    } 
  }
  
  static Object get(Object paramObject, Field paramField) {
    try {
      return paramField.get(paramObject);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionError(illegalAccessException);
    } 
  }
  
  static Object get(Field paramField) { return get(null, paramField); }
  
  static void set(Object paramObject1, Field paramField, Object paramObject2) {
    try {
      paramField.set(paramObject1, paramObject2);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionError(illegalAccessException);
    } 
  }
  
  static void setInt(Object paramObject, Field paramField, int paramInt) {
    try {
      paramField.setInt(paramObject, paramInt);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionError(illegalAccessException);
    } 
  }
  
  static void setBoolean(Object paramObject, Field paramField, boolean paramBoolean) {
    try {
      paramField.setBoolean(paramObject, paramBoolean);
    } catch (IllegalAccessException illegalAccessException) {
      throw new ReflectionError(illegalAccessException);
    } 
  }
  
  private static class ReflectionError extends Error {
    private static final long serialVersionUID = -8659519328078164097L;
    
    ReflectionError(Throwable param1Throwable) { super(param1Throwable); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\Reflect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */