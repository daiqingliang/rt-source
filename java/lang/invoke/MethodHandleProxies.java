package java.lang.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleImpl;
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandleStatics;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import sun.invoke.WrapperInstance;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class MethodHandleProxies {
  @CallerSensitive
  public static <T> T asInterfaceInstance(final Class<T> intfc, final MethodHandle target) {
    Object object;
    MethodHandle methodHandle;
    if (!paramClass.isInterface() || !Modifier.isPublic(paramClass.getModifiers()))
      throw MethodHandleStatics.newIllegalArgumentException("not a public interface", paramClass.getName()); 
    if (System.getSecurityManager() != null) {
      Class clazz = Reflection.getCallerClass();
      final ClassLoader loader = (clazz != null) ? clazz.getClassLoader() : null;
      ReflectUtil.checkProxyPackageAccess(classLoader1, new Class[] { paramClass });
      methodHandle = (classLoader1 != null) ? bindCaller(paramMethodHandle, clazz) : paramMethodHandle;
    } else {
      methodHandle = paramMethodHandle;
    } 
    ClassLoader classLoader = paramClass.getClassLoader();
    if (classLoader == null) {
      final ClassLoader loader;
      classLoader = (classLoader1 != null) ? classLoader1 : (classLoader1 = Thread.currentThread().getContextClassLoader()).getSystemClassLoader();
    } 
    final Method[] methods = getSingleNameMethods(paramClass);
    if (arrayOfMethod == null)
      throw MethodHandleStatics.newIllegalArgumentException("not a single-method interface", paramClass.getName()); 
    final MethodHandle[] vaTargets = new MethodHandle[arrayOfMethod.length];
    for (byte b = 0; b < arrayOfMethod.length; b++) {
      object = arrayOfMethod[b];
      MethodType methodType = MethodType.methodType(object.getReturnType(), object.getParameterTypes());
      MethodHandle methodHandle1 = methodHandle.asType(methodType);
      methodHandle1 = methodHandle1.asType(methodHandle1.type().changeReturnType(Object.class));
      arrayOfMethodHandle[b] = methodHandle1.asSpreader(Object[].class, methodType.parameterCount());
    } 
    final InvocationHandler ih = new InvocationHandler() {
        private Object getArg(String param1String) {
          if (param1String == "getWrapperInstanceTarget")
            return target; 
          if (param1String == "getWrapperInstanceType")
            return intfc; 
          throw new AssertionError();
        }
        
        public Object invoke(Object param1Object, Method param1Method, Object[] param1ArrayOfObject) {
          for (byte b = 0; b < methods.length; b++) {
            if (param1Method.equals(methods[b]))
              return vaTargets[b].invokeExact(param1ArrayOfObject); 
          } 
          if (param1Method.getDeclaringClass() == WrapperInstance.class)
            return getArg(param1Method.getName()); 
          if (MethodHandleProxies.isObjectMethod(param1Method))
            return MethodHandleProxies.callObjectMethod(param1Object, param1Method, param1ArrayOfObject); 
          throw MethodHandleStatics.newInternalError("bad proxy method: " + param1Method);
        }
      };
    if (System.getSecurityManager() != null) {
      final ClassLoader loader = classLoader;
      object = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() { return Proxy.newProxyInstance(loader, new Class[] { intfc, WrapperInstance.class }, ih); }
          });
    } else {
      object = Proxy.newProxyInstance(classLoader, new Class[] { paramClass, WrapperInstance.class }, invocationHandler);
    } 
    return (T)paramClass.cast(object);
  }
  
  private static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass) {
    MethodHandle methodHandle = MethodHandleImpl.bindCaller(paramMethodHandle, paramClass);
    if (paramMethodHandle.isVarargsCollector()) {
      MethodType methodType = methodHandle.type();
      int i = methodType.parameterCount();
      return methodHandle.asVarargsCollector(methodType.parameterType(i - 1));
    } 
    return methodHandle;
  }
  
  public static boolean isWrapperInstance(Object paramObject) { return paramObject instanceof WrapperInstance; }
  
  private static WrapperInstance asWrapperInstance(Object paramObject) {
    try {
      if (paramObject != null)
        return (WrapperInstance)paramObject; 
    } catch (ClassCastException classCastException) {}
    throw MethodHandleStatics.newIllegalArgumentException("not a wrapper instance");
  }
  
  public static MethodHandle wrapperInstanceTarget(Object paramObject) { return asWrapperInstance(paramObject).getWrapperInstanceTarget(); }
  
  public static Class<?> wrapperInstanceType(Object paramObject) { return asWrapperInstance(paramObject).getWrapperInstanceType(); }
  
  private static boolean isObjectMethod(Method paramMethod) {
    switch (paramMethod.getName()) {
      case "toString":
        return (paramMethod.getReturnType() == String.class && paramMethod.getParameterTypes().length == 0);
      case "hashCode":
        return (paramMethod.getReturnType() == int.class && paramMethod.getParameterTypes().length == 0);
      case "equals":
        return (paramMethod.getReturnType() == boolean.class && paramMethod.getParameterTypes().length == 1 && paramMethod.getParameterTypes()[false] == Object.class);
    } 
    return false;
  }
  
  private static Object callObjectMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
    assert isObjectMethod(paramMethod) : paramMethod;
    switch (paramMethod.getName()) {
      case "toString":
        return paramObject.getClass().getName() + "@" + Integer.toHexString(paramObject.hashCode());
      case "hashCode":
        return Integer.valueOf(System.identityHashCode(paramObject));
      case "equals":
        return Boolean.valueOf((paramObject == paramArrayOfObject[false]));
    } 
    return null;
  }
  
  private static Method[] getSingleNameMethods(Class<?> paramClass) {
    ArrayList arrayList = new ArrayList();
    String str = null;
    for (Method method : paramClass.getMethods()) {
      if (!isObjectMethod(method) && Modifier.isAbstract(method.getModifiers())) {
        String str1 = method.getName();
        if (str == null) {
          str = str1;
        } else if (!str.equals(str1)) {
          return null;
        } 
        arrayList.add(method);
      } 
    } 
    return (str == null) ? null : (Method[])arrayList.toArray(new Method[arrayList.size()]);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\MethodHandleProxies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */