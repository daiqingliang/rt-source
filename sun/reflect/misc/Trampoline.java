package sun.reflect.misc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Trampoline {
  private static void ensureInvocableMethod(Method paramMethod) throws InvocationTargetException {
    Class clazz = paramMethod.getDeclaringClass();
    if (clazz.equals(java.security.AccessController.class) || clazz.equals(Method.class) || clazz.getName().startsWith("java.lang.invoke."))
      throw new InvocationTargetException(new UnsupportedOperationException("invocation not supported")); 
  }
  
  private static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject) throws InvocationTargetException, IllegalAccessException {
    ensureInvocableMethod(paramMethod);
    return paramMethod.invoke(paramObject, paramArrayOfObject);
  }
  
  static  {
    if (Trampoline.class.getClassLoader() == null)
      throw new Error("Trampoline must not be defined by the bootstrap classloader"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\misc\Trampoline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */