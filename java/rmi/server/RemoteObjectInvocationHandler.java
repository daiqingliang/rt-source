package java.rmi.server;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.UnexpectedException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;
import sun.rmi.server.Util;
import sun.rmi.server.WeakClassHashMap;

public class RemoteObjectInvocationHandler extends RemoteObject implements InvocationHandler {
  private static final long serialVersionUID = 2L;
  
  private static final boolean allowFinalizeInvocation;
  
  private static final MethodToHash_Maps methodToHash_Maps;
  
  public RemoteObjectInvocationHandler(RemoteRef paramRemoteRef) {
    super(paramRemoteRef);
    if (paramRemoteRef == null)
      throw new NullPointerException(); 
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    if (!Proxy.isProxyClass(paramObject.getClass()))
      throw new IllegalArgumentException("not a proxy"); 
    if (Proxy.getInvocationHandler(paramObject) != this)
      throw new IllegalArgumentException("handler mismatch"); 
    return (paramMethod.getDeclaringClass() == Object.class) ? invokeObjectMethod(paramObject, paramMethod, paramArrayOfObject) : (("finalize".equals(paramMethod.getName()) && paramMethod.getParameterCount() == 0 && !allowFinalizeInvocation) ? null : invokeRemoteMethod(paramObject, paramMethod, paramArrayOfObject));
  }
  
  private Object invokeObjectMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    String str = paramMethod.getName();
    if (str.equals("hashCode"))
      return Integer.valueOf(hashCode()); 
    if (str.equals("equals")) {
      Object object = paramArrayOfObject[0];
      InvocationHandler invocationHandler;
      return Boolean.valueOf((paramObject == object || (object != null && Proxy.isProxyClass(object.getClass()) && invocationHandler = Proxy.getInvocationHandler(object) instanceof RemoteObjectInvocationHandler && equals(invocationHandler))));
    } 
    if (str.equals("toString"))
      return proxyToString(paramObject); 
    throw new IllegalArgumentException("unexpected Object method: " + paramMethod);
  }
  
  private Object invokeRemoteMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    try {
      if (!(paramObject instanceof Remote))
        throw new IllegalArgumentException("proxy not Remote instance"); 
      return this.ref.invoke((Remote)paramObject, paramMethod, paramArrayOfObject, getMethodHash(paramMethod));
    } catch (Exception exception) {
      if (!(exception instanceof RuntimeException)) {
        Class clazz1 = paramObject.getClass();
        try {
          paramMethod = clazz1.getMethod(paramMethod.getName(), paramMethod.getParameterTypes());
        } catch (NoSuchMethodException noSuchMethodException) {
          throw (IllegalArgumentException)(new IllegalArgumentException()).initCause(noSuchMethodException);
        } 
        Class clazz2 = exception.getClass();
        for (Class clazz : paramMethod.getExceptionTypes()) {
          if (clazz.isAssignableFrom(clazz2))
            throw exception; 
        } 
        exception = new UnexpectedException("unexpected exception", exception);
      } 
      throw exception;
    } 
  }
  
  private String proxyToString(Object paramObject) {
    Class[] arrayOfClass = paramObject.getClass().getInterfaces();
    if (arrayOfClass.length == 0)
      return "Proxy[" + this + "]"; 
    String str = arrayOfClass[0].getName();
    if (str.equals("java.rmi.Remote") && arrayOfClass.length > 1)
      str = arrayOfClass[1].getName(); 
    int i = str.lastIndexOf('.');
    if (i >= 0)
      str = str.substring(i + 1); 
    return "Proxy[" + str + "," + this + "]";
  }
  
  private void readObjectNoData() throws InvalidObjectException { throw new InvalidObjectException("no data in stream; class: " + getClass().getName()); }
  
  private static long getMethodHash(Method paramMethod) { return ((Long)((Map)methodToHash_Maps.get(paramMethod.getDeclaringClass())).get(paramMethod)).longValue(); }
  
  static  {
    final String propName = "sun.rmi.server.invocationhandler.allowFinalizeInvocation";
    String str2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return System.getProperty(propName); }
        });
    if ("".equals(str2)) {
      allowFinalizeInvocation = true;
    } else {
      allowFinalizeInvocation = Boolean.parseBoolean(str2);
    } 
    methodToHash_Maps = new MethodToHash_Maps();
  }
  
  private static class MethodToHash_Maps extends WeakClassHashMap<Map<Method, Long>> {
    protected Map<Method, Long> computeValue(Class<?> param1Class) { return new WeakHashMap<Method, Long>() {
          public Long get(Object param2Object) {
            Long long = (Long)super.get(param2Object);
            if (long == null) {
              Method method = (Method)param2Object;
              long = Long.valueOf(Util.computeMethodHash(method));
              put(method, long);
            } 
            return long;
          }
        }; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\rmi\server\RemoteObjectInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */