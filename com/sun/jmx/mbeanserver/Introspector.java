package com.sun.jmx.mbeanserver;

import com.sun.jmx.remote.util.EnvHelp;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.DescriptorKey;
import javax.management.DynamicMBean;
import javax.management.ImmutableDescriptor;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.openmbean.CompositeData;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class Introspector {
  public static final boolean ALLOW_NONPUBLIC_MBEAN;
  
  public static final boolean isDynamic(Class<?> paramClass) { return DynamicMBean.class.isAssignableFrom(paramClass); }
  
  public static void testCreation(Class<?> paramClass) throws NotCompliantMBeanException {
    int i = paramClass.getModifiers();
    if (Modifier.isAbstract(i) || Modifier.isInterface(i))
      throw new NotCompliantMBeanException("MBean class must be concrete"); 
    Constructor[] arrayOfConstructor = paramClass.getConstructors();
    if (arrayOfConstructor.length == 0)
      throw new NotCompliantMBeanException("MBean class must have public constructor"); 
  }
  
  public static void checkCompliance(Class<?> paramClass) throws NotCompliantMBeanException {
    if (DynamicMBean.class.isAssignableFrom(paramClass))
      return; 
    try {
      getStandardMBeanInterface(paramClass);
      return;
    } catch (NotCompliantMBeanException notCompliantMBeanException2) {
      NotCompliantMBeanException notCompliantMBeanException1 = notCompliantMBeanException2;
      try {
        getMXBeanInterface(paramClass);
        return;
      } catch (NotCompliantMBeanException notCompliantMBeanException) {
        notCompliantMBeanException2 = notCompliantMBeanException;
        String str = "MBean class " + paramClass.getName() + " does not implement DynamicMBean, and neither follows the Standard MBean conventions (" + notCompliantMBeanException1.toString() + ") nor the MXBean conventions (" + notCompliantMBeanException2.toString() + ")";
        throw new NotCompliantMBeanException(str);
      } 
    } 
  }
  
  public static <T> DynamicMBean makeDynamicMBean(T paramT) throws NotCompliantMBeanException {
    if (paramT instanceof DynamicMBean)
      return (DynamicMBean)paramT; 
    Class clazz1 = paramT.getClass();
    Class clazz2 = null;
    try {
      clazz2 = (Class)Util.cast(getStandardMBeanInterface(clazz1));
    } catch (NotCompliantMBeanException notCompliantMBeanException) {}
    if (clazz2 != null)
      return new StandardMBeanSupport(paramT, clazz2); 
    try {
      clazz2 = (Class)Util.cast(getMXBeanInterface(clazz1));
    } catch (NotCompliantMBeanException notCompliantMBeanException) {}
    if (clazz2 != null)
      return new MXBeanSupport(paramT, clazz2); 
    checkCompliance(clazz1);
    throw new NotCompliantMBeanException("Not compliant");
  }
  
  public static MBeanInfo testCompliance(Class<?> paramClass) throws NotCompliantMBeanException { return isDynamic(paramClass) ? null : testCompliance(paramClass, null); }
  
  public static void testComplianceMXBeanInterface(Class<?> paramClass) throws NotCompliantMBeanException { MXBeanIntrospector.getInstance().getAnalyzer(paramClass); }
  
  public static void testComplianceMBeanInterface(Class<?> paramClass) throws NotCompliantMBeanException { StandardMBeanIntrospector.getInstance().getAnalyzer(paramClass); }
  
  public static MBeanInfo testCompliance(Class<?> paramClass1, Class<?> paramClass2) throws NotCompliantMBeanException {
    if (paramClass2 == null)
      paramClass2 = getStandardMBeanInterface(paramClass1); 
    ReflectUtil.checkPackageAccess(paramClass2);
    StandardMBeanIntrospector standardMBeanIntrospector = StandardMBeanIntrospector.getInstance();
    return getClassMBeanInfo(standardMBeanIntrospector, paramClass1, paramClass2);
  }
  
  private static <M> MBeanInfo getClassMBeanInfo(MBeanIntrospector<M> paramMBeanIntrospector, Class<?> paramClass1, Class<?> paramClass2) throws NotCompliantMBeanException {
    PerInterface perInterface = paramMBeanIntrospector.getPerInterface(paramClass2);
    return paramMBeanIntrospector.getClassMBeanInfo(paramClass1, perInterface);
  }
  
  public static Class<?> getMBeanInterface(Class<?> paramClass) {
    if (isDynamic(paramClass))
      return null; 
    try {
      return getStandardMBeanInterface(paramClass);
    } catch (NotCompliantMBeanException notCompliantMBeanException) {
      return null;
    } 
  }
  
  public static <T> Class<? super T> getStandardMBeanInterface(Class<T> paramClass) throws NotCompliantMBeanException {
    Class<T> clazz1 = paramClass;
    Class clazz2 = null;
    while (clazz1 != null) {
      clazz2 = findMBeanInterface(clazz1, clazz1.getName());
      if (clazz2 != null)
        break; 
      clazz1 = clazz1.getSuperclass();
    } 
    if (clazz2 != null)
      return clazz2; 
    String str = "Class " + paramClass.getName() + " is not a JMX compliant Standard MBean";
    throw new NotCompliantMBeanException(str);
  }
  
  public static <T> Class<? super T> getMXBeanInterface(Class<T> paramClass) throws NotCompliantMBeanException {
    try {
      return MXBeanSupport.findMXBeanInterface(paramClass);
    } catch (Exception exception) {
      throw throwException(paramClass, exception);
    } 
  }
  
  private static <T> Class<? super T> findMBeanInterface(Class<T> paramClass, String paramString) {
    for (Class<T> clazz = paramClass; clazz != null; clazz = clazz.getSuperclass()) {
      Class[] arrayOfClass = clazz.getInterfaces();
      int i = arrayOfClass.length;
      for (byte b = 0; b < i; b++) {
        Class clazz1 = (Class)Util.cast(arrayOfClass[b]);
        clazz1 = implementsMBean(clazz1, paramString);
        if (clazz1 != null)
          return clazz1; 
      } 
    } 
    return null;
  }
  
  public static Descriptor descriptorForElement(AnnotatedElement paramAnnotatedElement) {
    if (paramAnnotatedElement == null)
      return ImmutableDescriptor.EMPTY_DESCRIPTOR; 
    Annotation[] arrayOfAnnotation = paramAnnotatedElement.getAnnotations();
    return descriptorForAnnotations(arrayOfAnnotation);
  }
  
  public static Descriptor descriptorForAnnotations(Annotation[] paramArrayOfAnnotation) {
    if (paramArrayOfAnnotation.length == 0)
      return ImmutableDescriptor.EMPTY_DESCRIPTOR; 
    HashMap hashMap = new HashMap();
    for (Annotation annotation : paramArrayOfAnnotation) {
      Class clazz = annotation.annotationType();
      Method[] arrayOfMethod = clazz.getMethods();
      boolean bool = false;
      for (Method method : arrayOfMethod) {
        DescriptorKey descriptorKey = (DescriptorKey)method.getAnnotation(DescriptorKey.class);
        if (descriptorKey != null) {
          String str = descriptorKey.value();
          try {
            if (!bool) {
              ReflectUtil.checkPackageAccess(clazz);
              bool = true;
            } 
            object1 = MethodUtil.invoke(method, annotation, null);
          } catch (RuntimeException runtimeException) {
            throw runtimeException;
          } catch (Exception exception) {
            throw new UndeclaredThrowableException(exception);
          } 
          Object object1 = annotationToField(object1);
          Object object2 = hashMap.put(str, object1);
          if (object2 != null && !equals(object2, object1)) {
            String str1 = "Inconsistent values for descriptor field " + str + " from annotations: " + object1 + " :: " + object2;
            throw new IllegalArgumentException(str1);
          } 
        } 
      } 
    } 
    return hashMap.isEmpty() ? ImmutableDescriptor.EMPTY_DESCRIPTOR : new ImmutableDescriptor(hashMap);
  }
  
  static NotCompliantMBeanException throwException(Class<?> paramClass, Throwable paramThrowable) throws NotCompliantMBeanException, SecurityException {
    if (paramThrowable instanceof SecurityException)
      throw (SecurityException)paramThrowable; 
    if (paramThrowable instanceof NotCompliantMBeanException)
      throw (NotCompliantMBeanException)paramThrowable; 
    String str1 = (paramClass == null) ? "null class" : paramClass.getName();
    String str2 = (paramThrowable == null) ? "Not compliant" : paramThrowable.getMessage();
    NotCompliantMBeanException notCompliantMBeanException = new NotCompliantMBeanException(str1 + ": " + str2);
    notCompliantMBeanException.initCause(paramThrowable);
    throw notCompliantMBeanException;
  }
  
  private static Object annotationToField(Object paramObject) {
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Number || paramObject instanceof String || paramObject instanceof Character || paramObject instanceof Boolean || paramObject instanceof String[])
      return paramObject; 
    Class clazz = paramObject.getClass();
    if (clazz.isArray()) {
      if (clazz.getComponentType().isPrimitive())
        return paramObject; 
      Object[] arrayOfObject = (Object[])paramObject;
      String[] arrayOfString = new String[arrayOfObject.length];
      for (byte b = 0; b < arrayOfObject.length; b++)
        arrayOfString[b] = (String)annotationToField(arrayOfObject[b]); 
      return arrayOfString;
    } 
    if (paramObject instanceof Class)
      return ((Class)paramObject).getName(); 
    if (paramObject instanceof Enum)
      return ((Enum)paramObject).name(); 
    if (Proxy.isProxyClass(clazz))
      clazz = clazz.getInterfaces()[0]; 
    throw new IllegalArgumentException("Illegal type for annotation element using @DescriptorKey: " + clazz.getName());
  }
  
  private static boolean equals(Object paramObject1, Object paramObject2) { return Arrays.deepEquals(new Object[] { paramObject1 }, new Object[] { paramObject2 }); }
  
  private static <T> Class<? super T> implementsMBean(Class<T> paramClass, String paramString) {
    String str = paramString + "MBean";
    if (paramClass.getName().equals(str))
      return paramClass; 
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (arrayOfClass[b].getName().equals(str) && (Modifier.isPublic(arrayOfClass[b].getModifiers()) || ALLOW_NONPUBLIC_MBEAN))
        return (Class)Util.cast(arrayOfClass[b]); 
    } 
    return null;
  }
  
  public static Object elementFromComplex(Object paramObject, String paramString) throws AttributeNotFoundException {
    try {
      if (paramObject.getClass().isArray() && paramString.equals("length"))
        return Integer.valueOf(Array.getLength(paramObject)); 
      if (paramObject instanceof CompositeData)
        return ((CompositeData)paramObject).get(paramString); 
      Class clazz = paramObject.getClass();
      Method method = null;
      if (BeansHelper.isAvailable()) {
        Object object = BeansHelper.getBeanInfo(clazz);
        Object[] arrayOfObject = BeansHelper.getPropertyDescriptors(object);
        for (Object object1 : arrayOfObject) {
          if (BeansHelper.getPropertyName(object1).equals(paramString)) {
            method = BeansHelper.getReadMethod(object1);
            break;
          } 
        } 
      } else {
        method = SimpleIntrospector.getReadMethod(clazz, paramString);
      } 
      if (method != null) {
        ReflectUtil.checkPackageAccess(method.getDeclaringClass());
        return MethodUtil.invoke(method, paramObject, new Class[0]);
      } 
      throw new AttributeNotFoundException("Could not find the getter method for the property " + paramString + " using the Java Beans introspector");
    } catch (InvocationTargetException invocationTargetException) {
      throw new IllegalArgumentException(invocationTargetException);
    } catch (AttributeNotFoundException attributeNotFoundException) {
      throw attributeNotFoundException;
    } catch (Exception exception) {
      throw (AttributeNotFoundException)EnvHelp.initCause(new AttributeNotFoundException(exception.getMessage()), exception);
    } 
  }
  
  static  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.jmx.mbeans.allowNonPublic"));
    ALLOW_NONPUBLIC_MBEAN = Boolean.parseBoolean(str);
  }
  
  private static class BeansHelper {
    private static final Class<?> introspectorClass = getClass("java.beans.Introspector");
    
    private static final Class<?> beanInfoClass = (introspectorClass == null) ? null : getClass("java.beans.BeanInfo");
    
    private static final Class<?> getPropertyDescriptorClass = (beanInfoClass == null) ? null : getClass("java.beans.PropertyDescriptor");
    
    private static final Method getBeanInfo = getMethod(introspectorClass, "getBeanInfo", new Class[] { Class.class });
    
    private static final Method getPropertyDescriptors = getMethod(beanInfoClass, "getPropertyDescriptors", new Class[0]);
    
    private static final Method getPropertyName = getMethod(getPropertyDescriptorClass, "getName", new Class[0]);
    
    private static final Method getReadMethod = getMethod(getPropertyDescriptorClass, "getReadMethod", new Class[0]);
    
    private static Class<?> getClass(String param1String) {
      try {
        return Class.forName(param1String, true, null);
      } catch (ClassNotFoundException classNotFoundException) {
        return null;
      } 
    }
    
    private static Method getMethod(Class<?> param1Class, String param1String, Class<?>... param1VarArgs) {
      if (param1Class != null)
        try {
          return param1Class.getMethod(param1String, param1VarArgs);
        } catch (NoSuchMethodException noSuchMethodException) {
          throw new AssertionError(noSuchMethodException);
        }  
      return null;
    }
    
    static boolean isAvailable() { return (introspectorClass != null); }
    
    static Object getBeanInfo(Class<?> param1Class) throws Exception {
      try {
        return getBeanInfo.invoke(null, new Object[] { param1Class });
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof Exception)
          throw (Exception)throwable; 
        throw new AssertionError(invocationTargetException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } 
    }
    
    static Object[] getPropertyDescriptors(Object param1Object) {
      try {
        return (Object[])getPropertyDescriptors.invoke(param1Object, new Object[0]);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        throw new AssertionError(invocationTargetException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } 
    }
    
    static String getPropertyName(Object param1Object) {
      try {
        return (String)getPropertyName.invoke(param1Object, new Object[0]);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        throw new AssertionError(invocationTargetException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } 
    }
    
    static Method getReadMethod(Object param1Object) {
      try {
        return (Method)getReadMethod.invoke(param1Object, new Object[0]);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        throw new AssertionError(invocationTargetException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } 
    }
  }
  
  private static class SimpleIntrospector {
    private static final String GET_METHOD_PREFIX = "get";
    
    private static final String IS_METHOD_PREFIX = "is";
    
    private static final Map<Class<?>, SoftReference<List<Method>>> cache = Collections.synchronizedMap(new WeakHashMap());
    
    private static List<Method> getCachedMethods(Class<?> param1Class) {
      SoftReference softReference = (SoftReference)cache.get(param1Class);
      if (softReference != null) {
        List list = (List)softReference.get();
        if (list != null)
          return list; 
      } 
      return null;
    }
    
    static boolean isReadMethod(Method param1Method) {
      int i = param1Method.getModifiers();
      if (Modifier.isStatic(i))
        return false; 
      String str = param1Method.getName();
      Class[] arrayOfClass = param1Method.getParameterTypes();
      int j = arrayOfClass.length;
      if (j == 0 && str.length() > 2) {
        if (str.startsWith("is"))
          return (param1Method.getReturnType() == boolean.class); 
        if (str.length() > 3 && str.startsWith("get"))
          return (param1Method.getReturnType() != void.class); 
      } 
      return false;
    }
    
    static List<Method> getReadMethods(Class<?> param1Class) {
      List list1 = getCachedMethods(param1Class);
      if (list1 != null)
        return list1; 
      List list2 = StandardMBeanIntrospector.getInstance().getMethods(param1Class);
      list2 = MBeanAnalyzer.eliminateCovariantMethods(list2);
      LinkedList linkedList = new LinkedList();
      for (Method method : list2) {
        if (isReadMethod(method)) {
          if (method.getName().startsWith("is")) {
            linkedList.add(0, method);
            continue;
          } 
          linkedList.add(method);
        } 
      } 
      cache.put(param1Class, new SoftReference(linkedList));
      return linkedList;
    }
    
    static Method getReadMethod(Class<?> param1Class, String param1String) {
      param1String = param1String.substring(0, 1).toUpperCase(Locale.ENGLISH) + param1String.substring(1);
      String str1 = "get" + param1String;
      String str2 = "is" + param1String;
      for (Method method : getReadMethods(param1Class)) {
        String str = method.getName();
        if (str.equals(str2) || str.equals(str1))
          return method; 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\Introspector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */