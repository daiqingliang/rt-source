package com.sun.beans.finder;

import com.sun.beans.TypeResolver;
import com.sun.beans.util.Cache;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import sun.reflect.misc.ReflectUtil;

public final class MethodFinder extends AbstractFinder<Method> {
  private static final Cache<Signature, Method> CACHE = new Cache<Signature, Method>(Cache.Kind.SOFT, Cache.Kind.SOFT) {
      public Method create(Signature param1Signature) {
        try {
          MethodFinder methodFinder;
          return (methodFinder = new MethodFinder(param1Signature.getName(), param1Signature.getArgs(), null)).findAccessibleMethod((Method)methodFinder.find(param1Signature.getType().getMethods()));
        } catch (Exception exception) {
          throw new SignatureException(exception);
        } 
      }
    };
  
  private final String name;
  
  public static Method findMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) throws NoSuchMethodException {
    if (paramString == null)
      throw new IllegalArgumentException("Method name is not set"); 
    PrimitiveWrapperMap.replacePrimitivesWithWrappers(paramVarArgs);
    Signature signature = new Signature(paramClass, paramString, paramVarArgs);
    try {
      Method method = (Method)CACHE.get(signature);
      return (method == null || ReflectUtil.isPackageAccessible(method.getDeclaringClass())) ? method : (Method)CACHE.create(signature);
    } catch (SignatureException signatureException) {
      throw signatureException.toNoSuchMethodException("Method '" + paramString + "' is not found");
    } 
  }
  
  public static Method findInstanceMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) throws NoSuchMethodException {
    Method method = findMethod(paramClass, paramString, paramVarArgs);
    if (Modifier.isStatic(method.getModifiers()))
      throw new NoSuchMethodException("Method '" + paramString + "' is static"); 
    return method;
  }
  
  public static Method findStaticMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) throws NoSuchMethodException {
    Method method = findMethod(paramClass, paramString, paramVarArgs);
    if (!Modifier.isStatic(method.getModifiers()))
      throw new NoSuchMethodException("Method '" + paramString + "' is not static"); 
    return method;
  }
  
  public static Method findAccessibleMethod(Method paramMethod) throws NoSuchMethodException {
    Class clazz = paramMethod.getDeclaringClass();
    if (Modifier.isPublic(clazz.getModifiers()) && ReflectUtil.isPackageAccessible(clazz))
      return paramMethod; 
    if (Modifier.isStatic(paramMethod.getModifiers()))
      throw new NoSuchMethodException("Method '" + paramMethod.getName() + "' is not accessible"); 
    Type[] arrayOfType = clazz.getGenericInterfaces();
    int i = arrayOfType.length;
    byte b = 0;
    while (b < i) {
      Type type = arrayOfType[b];
      try {
        return findAccessibleMethod(paramMethod, type);
      } catch (NoSuchMethodException noSuchMethodException) {
        b++;
      } 
    } 
    return findAccessibleMethod(paramMethod, clazz.getGenericSuperclass());
  }
  
  private static Method findAccessibleMethod(Method paramMethod, Type paramType) throws NoSuchMethodException {
    String str = paramMethod.getName();
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return findAccessibleMethod(clazz.getMethod(str, arrayOfClass));
    } 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      Class clazz = (Class)parameterizedType.getRawType();
      for (Method method : clazz.getMethods()) {
        if (method.getName().equals(str)) {
          Class[] arrayOfClass1 = method.getParameterTypes();
          if (arrayOfClass1.length == arrayOfClass.length) {
            if (Arrays.equals(arrayOfClass, arrayOfClass1))
              return findAccessibleMethod(method); 
            Type[] arrayOfType = method.getGenericParameterTypes();
            if (arrayOfClass.length == arrayOfType.length && Arrays.equals(arrayOfClass, TypeResolver.erase(TypeResolver.resolve(parameterizedType, arrayOfType))))
              return findAccessibleMethod(method); 
          } 
        } 
      } 
    } 
    throw new NoSuchMethodException("Method '" + str + "' is not accessible");
  }
  
  private MethodFinder(String paramString, Class<?>[] paramArrayOfClass) {
    super(paramArrayOfClass);
    this.name = paramString;
  }
  
  protected boolean isValid(Method paramMethod) { return (super.isValid(paramMethod) && paramMethod.getName().equals(this.name)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\MethodFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */