package com.sun.jmx.mbeanserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.WeakHashMap;
import javax.management.Descriptor;
import javax.management.ImmutableDescriptor;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.NotCompliantMBeanException;
import sun.reflect.misc.MethodUtil;

class StandardMBeanIntrospector extends MBeanIntrospector<Method> {
  private static final StandardMBeanIntrospector instance = new StandardMBeanIntrospector();
  
  private static final WeakHashMap<Class<?>, Boolean> definitelyImmutable = new WeakHashMap();
  
  private static final MBeanIntrospector.PerInterfaceMap<Method> perInterfaceMap = new MBeanIntrospector.PerInterfaceMap();
  
  private static final MBeanIntrospector.MBeanInfoMap mbeanInfoMap = new MBeanIntrospector.MBeanInfoMap();
  
  static StandardMBeanIntrospector getInstance() { return instance; }
  
  MBeanIntrospector.PerInterfaceMap<Method> getPerInterfaceMap() { return perInterfaceMap; }
  
  MBeanIntrospector.MBeanInfoMap getMBeanInfoMap() { return mbeanInfoMap; }
  
  MBeanAnalyzer<Method> getAnalyzer(Class<?> paramClass) throws NotCompliantMBeanException { return MBeanAnalyzer.analyzer(paramClass, this); }
  
  boolean isMXBean() { return false; }
  
  Method mFrom(Method paramMethod) { return paramMethod; }
  
  String getName(Method paramMethod) { return paramMethod.getName(); }
  
  Type getGenericReturnType(Method paramMethod) { return paramMethod.getGenericReturnType(); }
  
  Type[] getGenericParameterTypes(Method paramMethod) { return paramMethod.getGenericParameterTypes(); }
  
  String[] getSignature(Method paramMethod) {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    String[] arrayOfString = new String[arrayOfClass.length];
    for (byte b = 0; b < arrayOfClass.length; b++)
      arrayOfString[b] = arrayOfClass[b].getName(); 
    return arrayOfString;
  }
  
  void checkMethod(Method paramMethod) {}
  
  Object invokeM2(Method paramMethod, Object paramObject1, Object[] paramArrayOfObject, Object paramObject2) throws InvocationTargetException, IllegalAccessException, MBeanException { return MethodUtil.invoke(paramMethod, paramObject1, paramArrayOfObject); }
  
  boolean validParameter(Method paramMethod, Object paramObject1, int paramInt, Object paramObject2) { return isValidParameter(paramMethod, paramObject1, paramInt); }
  
  MBeanAttributeInfo getMBeanAttributeInfo(String paramString, Method paramMethod1, Method paramMethod2) {
    try {
      return new MBeanAttributeInfo(paramString, "Attribute exposed for management", paramMethod1, paramMethod2);
    } catch (IntrospectionException introspectionException) {
      throw new RuntimeException(introspectionException);
    } 
  }
  
  MBeanOperationInfo getMBeanOperationInfo(String paramString, Method paramMethod) { return new MBeanOperationInfo("Operation exposed for management", paramMethod); }
  
  Descriptor getBasicMBeanDescriptor() { return ImmutableDescriptor.EMPTY_DESCRIPTOR; }
  
  Descriptor getMBeanDescriptor(Class<?> paramClass) {
    boolean bool = isDefinitelyImmutableInfo(paramClass);
    return new ImmutableDescriptor(new String[] { "mxbean=false", "immutableInfo=" + bool });
  }
  
  static boolean isDefinitelyImmutableInfo(Class<?> paramClass) {
    if (!javax.management.NotificationBroadcaster.class.isAssignableFrom(paramClass))
      return true; 
    synchronized (definitelyImmutable) {
      Boolean bool = (Boolean)definitelyImmutable.get(paramClass);
      if (bool == null) {
        Class clazz = javax.management.NotificationBroadcasterSupport.class;
        if (clazz.isAssignableFrom(paramClass)) {
          try {
            Method method = paramClass.getMethod("getNotificationInfo", new Class[0]);
            bool = Boolean.valueOf((method.getDeclaringClass() == clazz));
          } catch (Exception exception) {
            return false;
          } 
        } else {
          bool = Boolean.valueOf(false);
        } 
        definitelyImmutable.put(paramClass, bool);
      } 
      return bool.booleanValue();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\StandardMBeanIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */