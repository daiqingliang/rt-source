package java.beans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class EventHandler implements InvocationHandler {
  private Object target;
  
  private String action;
  
  private final String eventPropertyName;
  
  private final String listenerMethodName;
  
  private final AccessControlContext acc = AccessController.getContext();
  
  @ConstructorProperties({"target", "action", "eventPropertyName", "listenerMethodName"})
  public EventHandler(Object paramObject, String paramString1, String paramString2, String paramString3) {
    this.target = paramObject;
    this.action = paramString1;
    if (paramObject == null)
      throw new NullPointerException("target must be non-null"); 
    if (paramString1 == null)
      throw new NullPointerException("action must be non-null"); 
    this.eventPropertyName = paramString2;
    this.listenerMethodName = paramString3;
  }
  
  public Object getTarget() { return this.target; }
  
  public String getAction() { return this.action; }
  
  public String getEventPropertyName() { return this.eventPropertyName; }
  
  public String getListenerMethodName() { return this.listenerMethodName; }
  
  private Object applyGetters(Object paramObject, String paramString) {
    if (paramString == null || paramString.equals(""))
      return paramObject; 
    int i = paramString.indexOf('.');
    if (i == -1)
      i = paramString.length(); 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(Math.min(i + 1, paramString.length()));
    try {
      Method method = null;
      if (paramObject != null) {
        method = Statement.getMethod(paramObject.getClass(), "get" + NameGenerator.capitalize(str1), new Class[0]);
        if (method == null)
          method = Statement.getMethod(paramObject.getClass(), "is" + NameGenerator.capitalize(str1), new Class[0]); 
        if (method == null)
          method = Statement.getMethod(paramObject.getClass(), str1, new Class[0]); 
      } 
      if (method == null)
        throw new RuntimeException("No method called: " + str1 + " defined on " + paramObject); 
      Object object = MethodUtil.invoke(method, paramObject, new Object[0]);
      return applyGetters(object, str2);
    } catch (Exception exception) {
      throw new RuntimeException("Failed to call method: " + str1 + " on " + paramObject, exception);
    } 
  }
  
  public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
    AccessControlContext accessControlContext = this.acc;
    if (accessControlContext == null && System.getSecurityManager() != null)
      throw new SecurityException("AccessControlContext is not set"); 
    return AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() { return EventHandler.this.invokeInternal(proxy, method, arguments); }
        }accessControlContext);
  }
  
  private Object invokeInternal(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) {
    String str = paramMethod.getName();
    if (paramMethod.getDeclaringClass() == Object.class) {
      if (str.equals("hashCode"))
        return new Integer(System.identityHashCode(paramObject)); 
      if (str.equals("equals"))
        return (paramObject == paramArrayOfObject[false]) ? Boolean.TRUE : Boolean.FALSE; 
      if (str.equals("toString"))
        return paramObject.getClass().getName() + '@' + Integer.toHexString(paramObject.hashCode()); 
    } 
    if (this.listenerMethodName == null || this.listenerMethodName.equals(str)) {
      Class[] arrayOfClass = null;
      Object[] arrayOfObject = null;
      if (this.eventPropertyName == null) {
        arrayOfObject = new Object[0];
        arrayOfClass = new Class[0];
      } else {
        Object object = applyGetters(paramArrayOfObject[0], getEventPropertyName());
        arrayOfObject = new Object[] { object };
        arrayOfClass = new Class[] { (object == null) ? null : object.getClass() };
      } 
      try {
        int i = this.action.lastIndexOf('.');
        if (i != -1) {
          this.target = applyGetters(this.target, this.action.substring(0, i));
          this.action = this.action.substring(i + 1);
        } 
        Method method = Statement.getMethod(this.target.getClass(), this.action, arrayOfClass);
        if (method == null)
          method = Statement.getMethod(this.target.getClass(), "set" + NameGenerator.capitalize(this.action), arrayOfClass); 
        if (method == null) {
          String str1 = (arrayOfClass.length == 0) ? " with no arguments" : (" with argument " + arrayOfClass[0]);
          throw new RuntimeException("No method called " + this.action + " on " + this.target.getClass() + str1);
        } 
        return MethodUtil.invoke(method, this.target, arrayOfObject);
      } catch (IllegalAccessException illegalAccessException) {
        throw new RuntimeException(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getTargetException();
        throw (throwable instanceof RuntimeException) ? (RuntimeException)throwable : new RuntimeException(throwable);
      } 
    } 
    return null;
  }
  
  public static <T> T create(Class<T> paramClass, Object paramObject, String paramString) { return (T)create(paramClass, paramObject, paramString, null, null); }
  
  public static <T> T create(Class<T> paramClass, Object paramObject, String paramString1, String paramString2) { return (T)create(paramClass, paramObject, paramString1, paramString2, null); }
  
  public static <T> T create(Class<T> paramClass, Object paramObject, String paramString1, String paramString2, String paramString3) {
    final EventHandler handler = new EventHandler(paramObject, paramString1, paramString2, paramString3);
    if (paramClass == null)
      throw new NullPointerException("listenerInterface must be non-null"); 
    final ClassLoader loader = getClassLoader(paramClass);
    final Class[] interfaces = { paramClass };
    return (T)AccessController.doPrivileged(new PrivilegedAction<T>() {
          public T run() { return (T)Proxy.newProxyInstance(loader, interfaces, handler); }
        });
  }
  
  private static ClassLoader getClassLoader(Class<?> paramClass) {
    ReflectUtil.checkPackageAccess(paramClass);
    ClassLoader classLoader = paramClass.getClassLoader();
    if (classLoader == null) {
      classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null)
        classLoader = ClassLoader.getSystemClassLoader(); 
    } 
    return classLoader;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\EventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */