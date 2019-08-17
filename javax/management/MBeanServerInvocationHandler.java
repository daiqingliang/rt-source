package javax.management;

import com.sun.jmx.mbeanserver.MXBeanProxy;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.WeakHashMap;

public class MBeanServerInvocationHandler implements InvocationHandler {
  private static final WeakHashMap<Class<?>, WeakReference<MXBeanProxy>> mxbeanProxies = new WeakHashMap();
  
  private final MBeanServerConnection connection;
  
  private final ObjectName objectName;
  
  private final boolean isMXBean;
  
  public MBeanServerInvocationHandler(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName) { this(paramMBeanServerConnection, paramObjectName, false); }
  
  public MBeanServerInvocationHandler(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, boolean paramBoolean) {
    if (paramMBeanServerConnection == null)
      throw new IllegalArgumentException("Null connection"); 
    if (Proxy.isProxyClass(paramMBeanServerConnection.getClass()) && MBeanServerInvocationHandler.class.isAssignableFrom(Proxy.getInvocationHandler(paramMBeanServerConnection).getClass()))
      throw new IllegalArgumentException("Wrapping MBeanServerInvocationHandler"); 
    if (paramObjectName == null)
      throw new IllegalArgumentException("Null object name"); 
    this.connection = paramMBeanServerConnection;
    this.objectName = paramObjectName;
    this.isMXBean = paramBoolean;
  }
  
  public MBeanServerConnection getMBeanServerConnection() { return this.connection; }
  
  public ObjectName getObjectName() { return this.objectName; }
  
  public boolean isMXBean() { return this.isMXBean; }
  
  public static <T> T newProxyInstance(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName, Class<T> paramClass, boolean paramBoolean) { return (T)JMX.newMBeanProxy(paramMBeanServerConnection, paramObjectName, paramClass, paramBoolean); }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    Class clazz = paramMethod.getDeclaringClass();
    if (clazz.equals(NotificationBroadcaster.class) || clazz.equals(NotificationEmitter.class))
      return invokeBroadcasterMethod(paramObject, paramMethod, paramArrayOfObject); 
    if (shouldDoLocally(paramObject, paramMethod))
      return doLocally(paramObject, paramMethod, paramArrayOfObject); 
    try {
      if (isMXBean()) {
        MXBeanProxy mXBeanProxy = findMXBeanProxy(clazz);
        return mXBeanProxy.invoke(this.connection, this.objectName, paramMethod, paramArrayOfObject);
      } 
      String str = paramMethod.getName();
      Class[] arrayOfClass = paramMethod.getParameterTypes();
      Class clazz1 = paramMethod.getReturnType();
      boolean bool = (paramArrayOfObject == null) ? 0 : paramArrayOfObject.length;
      if (str.startsWith("get") && str.length() > 3 && !bool && !clazz1.equals(void.class))
        return this.connection.getAttribute(this.objectName, str.substring(3)); 
      if (str.startsWith("is") && str.length() > 2 && !bool && (clazz1.equals(boolean.class) || clazz1.equals(Boolean.class)))
        return this.connection.getAttribute(this.objectName, str.substring(2)); 
      if (str.startsWith("set") && str.length() > 3 && bool == true && clazz1.equals(void.class)) {
        Attribute attribute = new Attribute(str.substring(3), paramArrayOfObject[0]);
        this.connection.setAttribute(this.objectName, attribute);
        return null;
      } 
      String[] arrayOfString = new String[arrayOfClass.length];
      for (byte b = 0; b < arrayOfClass.length; b++)
        arrayOfString[b] = arrayOfClass[b].getName(); 
      return this.connection.invoke(this.objectName, str, paramArrayOfObject, arrayOfString);
    } catch (MBeanException mBeanException) {
      throw mBeanException.getTargetException();
    } catch (RuntimeMBeanException runtimeMBeanException) {
      throw runtimeMBeanException.getTargetException();
    } catch (RuntimeErrorException runtimeErrorException) {
      throw runtimeErrorException.getTargetError();
    } 
  }
  
  private static MXBeanProxy findMXBeanProxy(Class<?> paramClass) {
    synchronized (mxbeanProxies) {
      WeakReference weakReference = (WeakReference)mxbeanProxies.get(paramClass);
      MXBeanProxy mXBeanProxy = (weakReference == null) ? null : (MXBeanProxy)weakReference.get();
      if (mXBeanProxy == null) {
        try {
          mXBeanProxy = new MXBeanProxy(paramClass);
        } catch (IllegalArgumentException illegalArgumentException1) {
          String str = "Cannot make MXBean proxy for " + paramClass.getName() + ": " + illegalArgumentException1.getMessage();
          IllegalArgumentException illegalArgumentException2 = new IllegalArgumentException(str, illegalArgumentException1.getCause());
          illegalArgumentException2.setStackTrace(illegalArgumentException1.getStackTrace());
          throw illegalArgumentException2;
        } 
        mxbeanProxies.put(paramClass, new WeakReference(mXBeanProxy));
      } 
      return mXBeanProxy;
    } 
  }
  
  private Object invokeBroadcasterMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    String str = paramMethod.getName();
    byte b = (paramArrayOfObject == null) ? 0 : paramArrayOfObject.length;
    if (str.equals("addNotificationListener")) {
      if (b != 3) {
        String str1 = "Bad arg count to addNotificationListener: " + b;
        throw new IllegalArgumentException(str1);
      } 
      NotificationListener notificationListener = (NotificationListener)paramArrayOfObject[0];
      NotificationFilter notificationFilter = (NotificationFilter)paramArrayOfObject[1];
      Object object = paramArrayOfObject[2];
      this.connection.addNotificationListener(this.objectName, notificationListener, notificationFilter, object);
      return null;
    } 
    if (str.equals("removeNotificationListener")) {
      Object object;
      NotificationFilter notificationFilter;
      NotificationListener notificationListener = (NotificationListener)paramArrayOfObject[0];
      switch (b) {
        case 1:
          this.connection.removeNotificationListener(this.objectName, notificationListener);
          return null;
        case 3:
          notificationFilter = (NotificationFilter)paramArrayOfObject[1];
          object = paramArrayOfObject[2];
          this.connection.removeNotificationListener(this.objectName, notificationListener, notificationFilter, object);
          return null;
      } 
      String str1 = "Bad arg count to removeNotificationListener: " + b;
      throw new IllegalArgumentException(str1);
    } 
    if (str.equals("getNotificationInfo")) {
      if (paramArrayOfObject != null)
        throw new IllegalArgumentException("getNotificationInfo has args"); 
      MBeanInfo mBeanInfo = this.connection.getMBeanInfo(this.objectName);
      return mBeanInfo.getNotifications();
    } 
    throw new IllegalArgumentException("Bad method name: " + str);
  }
  
  private boolean shouldDoLocally(Object paramObject, Method paramMethod) {
    String str = paramMethod.getName();
    return ((str.equals("hashCode") || str.equals("toString")) && paramMethod.getParameterTypes().length == 0 && isLocal(paramObject, paramMethod)) ? true : ((str.equals("equals") && Arrays.equals(paramMethod.getParameterTypes(), new Class[] { Object.class }) && isLocal(paramObject, paramMethod)) ? true : ((str.equals("finalize") && paramMethod.getParameterTypes().length == 0)));
  }
  
  private Object doLocally(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    String str = paramMethod.getName();
    if (str.equals("equals")) {
      if (this == paramArrayOfObject[false])
        return Boolean.valueOf(true); 
      if (!(paramArrayOfObject[0] instanceof Proxy))
        return Boolean.valueOf(false); 
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(paramArrayOfObject[0]);
      if (invocationHandler == null || !(invocationHandler instanceof MBeanServerInvocationHandler))
        return Boolean.valueOf(false); 
      MBeanServerInvocationHandler mBeanServerInvocationHandler = (MBeanServerInvocationHandler)invocationHandler;
      return Boolean.valueOf((this.connection.equals(mBeanServerInvocationHandler.connection) && this.objectName.equals(mBeanServerInvocationHandler.objectName) && paramObject.getClass().equals(paramArrayOfObject[0].getClass())));
    } 
    if (str.equals("toString"))
      return (isMXBean() ? "MX" : "M") + "BeanProxy(" + this.connection + "[" + this.objectName + "])"; 
    if (str.equals("hashCode"))
      return Integer.valueOf(this.objectName.hashCode() + this.connection.hashCode()); 
    if (str.equals("finalize"))
      return null; 
    throw new RuntimeException("Unexpected method name: " + str);
  }
  
  private static boolean isLocal(Object paramObject, Method paramMethod) {
    Class[] arrayOfClass1 = paramObject.getClass().getInterfaces();
    if (arrayOfClass1 == null)
      return true; 
    String str = paramMethod.getName();
    Class[] arrayOfClass2 = paramMethod.getParameterTypes();
    Class[] arrayOfClass3 = arrayOfClass1;
    int i = arrayOfClass3.length;
    byte b = 0;
    while (b < i) {
      Class clazz = arrayOfClass3[b];
      try {
        clazz.getMethod(str, arrayOfClass2);
        return false;
      } catch (NoSuchMethodException noSuchMethodException) {
        b++;
      } 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServerInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */