package java.beans;

import com.sun.beans.TypeResolver;
import com.sun.beans.WeakCache;
import com.sun.beans.finder.ClassFinder;
import com.sun.beans.finder.MethodFinder;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import sun.reflect.misc.ReflectUtil;

public class Introspector {
  public static final int USE_ALL_BEANINFO = 1;
  
  public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
  
  public static final int IGNORE_ALL_BEANINFO = 3;
  
  private static final WeakCache<Class<?>, Method[]> declaredMethodCache = new WeakCache();
  
  private Class<?> beanClass;
  
  private BeanInfo explicitBeanInfo;
  
  private BeanInfo superBeanInfo;
  
  private BeanInfo[] additionalBeanInfo;
  
  private boolean propertyChangeSource = false;
  
  private static Class<EventListener> eventListenerType = EventListener.class;
  
  private String defaultEventName;
  
  private String defaultPropertyName;
  
  private int defaultEventIndex = -1;
  
  private int defaultPropertyIndex = -1;
  
  private Map<String, MethodDescriptor> methods;
  
  private Map<String, PropertyDescriptor> properties;
  
  private Map<String, EventSetDescriptor> events;
  
  private static final EventSetDescriptor[] EMPTY_EVENTSETDESCRIPTORS = new EventSetDescriptor[0];
  
  static final String ADD_PREFIX = "add";
  
  static final String REMOVE_PREFIX = "remove";
  
  static final String GET_PREFIX = "get";
  
  static final String SET_PREFIX = "set";
  
  static final String IS_PREFIX = "is";
  
  private HashMap<String, List<PropertyDescriptor>> pdStore = new HashMap();
  
  public static BeanInfo getBeanInfo(Class<?> paramClass) throws IntrospectionException {
    BeanInfo beanInfo;
    if (!ReflectUtil.isPackageAccessible(paramClass))
      return (new Introspector(paramClass, null, 1)).getBeanInfo(); 
    ThreadGroupContext threadGroupContext = ThreadGroupContext.getContext();
    synchronized (declaredMethodCache) {
      beanInfo = threadGroupContext.getBeanInfo(paramClass);
    } 
    if (beanInfo == null) {
      beanInfo = (new Introspector(paramClass, null, 1)).getBeanInfo();
      synchronized (declaredMethodCache) {
        threadGroupContext.putBeanInfo(paramClass, beanInfo);
      } 
    } 
    return beanInfo;
  }
  
  public static BeanInfo getBeanInfo(Class<?> paramClass, int paramInt) throws IntrospectionException { return getBeanInfo(paramClass, null, paramInt); }
  
  public static BeanInfo getBeanInfo(Class<?> paramClass1, Class<?> paramClass2) throws IntrospectionException { return getBeanInfo(paramClass1, paramClass2, 1); }
  
  public static BeanInfo getBeanInfo(Class<?> paramClass1, Class<?> paramClass2, int paramInt) throws IntrospectionException {
    BeanInfo beanInfo;
    if (paramClass2 == null && paramInt == 1) {
      beanInfo = getBeanInfo(paramClass1);
    } else {
      beanInfo = (new Introspector(paramClass1, paramClass2, paramInt)).getBeanInfo();
    } 
    return beanInfo;
  }
  
  public static String decapitalize(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    if (paramString.length() > 1 && Character.isUpperCase(paramString.charAt(1)) && Character.isUpperCase(paramString.charAt(0)))
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    arrayOfChar[0] = Character.toLowerCase(arrayOfChar[0]);
    return new String(arrayOfChar);
  }
  
  public static String[] getBeanInfoSearchPath() { return ThreadGroupContext.getContext().getBeanInfoFinder().getPackages(); }
  
  public static void setBeanInfoSearchPath(String[] paramArrayOfString) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertiesAccess(); 
    ThreadGroupContext.getContext().getBeanInfoFinder().setPackages(paramArrayOfString);
  }
  
  public static void flushCaches() {
    synchronized (declaredMethodCache) {
      ThreadGroupContext.getContext().clearBeanInfoCache();
      declaredMethodCache.clear();
    } 
  }
  
  public static void flushFromCaches(Class<?> paramClass) {
    if (paramClass == null)
      throw new NullPointerException(); 
    synchronized (declaredMethodCache) {
      ThreadGroupContext.getContext().removeBeanInfo(paramClass);
      declaredMethodCache.put(paramClass, null);
    } 
  }
  
  private Introspector(Class<?> paramClass1, Class<?> paramClass2, int paramInt) throws IntrospectionException {
    this.beanClass = paramClass1;
    if (paramClass2 != null) {
      boolean bool = false;
      for (Class clazz1 = paramClass1.getSuperclass(); clazz1 != null; clazz1 = clazz1.getSuperclass()) {
        if (clazz1 == paramClass2)
          bool = true; 
      } 
      if (!bool)
        throw new IntrospectionException(paramClass2.getName() + " not superclass of " + paramClass1.getName()); 
    } 
    if (paramInt == 1)
      this.explicitBeanInfo = findExplicitBeanInfo(paramClass1); 
    Class clazz = paramClass1.getSuperclass();
    if (clazz != paramClass2) {
      int i = paramInt;
      if (i == 2)
        i = 1; 
      this.superBeanInfo = getBeanInfo(clazz, paramClass2, i);
    } 
    if (this.explicitBeanInfo != null)
      this.additionalBeanInfo = this.explicitBeanInfo.getAdditionalBeanInfo(); 
    if (this.additionalBeanInfo == null)
      this.additionalBeanInfo = new BeanInfo[0]; 
  }
  
  private BeanInfo getBeanInfo() throws IntrospectionException {
    BeanDescriptor beanDescriptor = getTargetBeanDescriptor();
    MethodDescriptor[] arrayOfMethodDescriptor = getTargetMethodInfo();
    EventSetDescriptor[] arrayOfEventSetDescriptor = getTargetEventInfo();
    PropertyDescriptor[] arrayOfPropertyDescriptor = getTargetPropertyInfo();
    int i = getTargetDefaultEventIndex();
    int j = getTargetDefaultPropertyIndex();
    return new GenericBeanInfo(beanDescriptor, arrayOfEventSetDescriptor, i, arrayOfPropertyDescriptor, j, arrayOfMethodDescriptor, this.explicitBeanInfo);
  }
  
  private static BeanInfo findExplicitBeanInfo(Class<?> paramClass) throws IntrospectionException { return (BeanInfo)ThreadGroupContext.getContext().getBeanInfoFinder().find(paramClass); }
  
  private PropertyDescriptor[] getTargetPropertyInfo() {
    PropertyDescriptor[] arrayOfPropertyDescriptor1 = null;
    if (this.explicitBeanInfo != null)
      arrayOfPropertyDescriptor1 = getPropertyDescriptors(this.explicitBeanInfo); 
    if (arrayOfPropertyDescriptor1 == null && this.superBeanInfo != null)
      addPropertyDescriptors(getPropertyDescriptors(this.superBeanInfo)); 
    for (byte b = 0; b < this.additionalBeanInfo.length; b++)
      addPropertyDescriptors(this.additionalBeanInfo[b].getPropertyDescriptors()); 
    if (arrayOfPropertyDescriptor1 != null) {
      addPropertyDescriptors(arrayOfPropertyDescriptor1);
    } else {
      Method[] arrayOfMethod = getPublicDeclaredMethods(this.beanClass);
      for (byte b1 = 0; b1 < arrayOfMethod.length; b1++) {
        Method method = arrayOfMethod[b1];
        if (method != null) {
          int i = method.getModifiers();
          if (!Modifier.isStatic(i)) {
            String str = method.getName();
            Class[] arrayOfClass = method.getParameterTypes();
            Class clazz = method.getReturnType();
            int j = arrayOfClass.length;
            PropertyDescriptor propertyDescriptor = null;
            if (str.length() > 3 || str.startsWith("is")) {
              try {
                if (j == 0) {
                  if (str.startsWith("get")) {
                    propertyDescriptor = new PropertyDescriptor(this.beanClass, str.substring(3), method, null);
                  } else if (clazz == boolean.class && str.startsWith("is")) {
                    propertyDescriptor = new PropertyDescriptor(this.beanClass, str.substring(2), method, null);
                  } 
                } else if (j == 1) {
                  if (int.class.equals(arrayOfClass[0]) && str.startsWith("get")) {
                    propertyDescriptor = new IndexedPropertyDescriptor(this.beanClass, str.substring(3), null, null, method, null);
                  } else if (void.class.equals(clazz) && str.startsWith("set")) {
                    propertyDescriptor = new PropertyDescriptor(this.beanClass, str.substring(3), null, method);
                    if (throwsException(method, PropertyVetoException.class))
                      propertyDescriptor.setConstrained(true); 
                  } 
                } else if (j == 2 && void.class.equals(clazz) && int.class.equals(arrayOfClass[0]) && str.startsWith("set")) {
                  propertyDescriptor = new IndexedPropertyDescriptor(this.beanClass, str.substring(3), null, null, null, method);
                  if (throwsException(method, PropertyVetoException.class))
                    propertyDescriptor.setConstrained(true); 
                } 
              } catch (IntrospectionException introspectionException) {
                propertyDescriptor = null;
              } 
              if (propertyDescriptor != null) {
                if (this.propertyChangeSource)
                  propertyDescriptor.setBound(true); 
                addPropertyDescriptor(propertyDescriptor);
              } 
            } 
          } 
        } 
      } 
    } 
    processPropertyDescriptors();
    PropertyDescriptor[] arrayOfPropertyDescriptor2 = (PropertyDescriptor[])this.properties.values().toArray(new PropertyDescriptor[this.properties.size()]);
    if (this.defaultPropertyName != null)
      for (byte b1 = 0; b1 < arrayOfPropertyDescriptor2.length; b1++) {
        if (this.defaultPropertyName.equals(arrayOfPropertyDescriptor2[b1].getName()))
          this.defaultPropertyIndex = b1; 
      }  
    return arrayOfPropertyDescriptor2;
  }
  
  private void addPropertyDescriptor(PropertyDescriptor paramPropertyDescriptor) {
    String str = paramPropertyDescriptor.getName();
    List list = (List)this.pdStore.get(str);
    if (list == null) {
      list = new ArrayList();
      this.pdStore.put(str, list);
    } 
    if (this.beanClass != paramPropertyDescriptor.getClass0()) {
      Method method1 = paramPropertyDescriptor.getReadMethod();
      Method method2 = paramPropertyDescriptor.getWriteMethod();
      boolean bool = true;
      if (method1 != null)
        bool = (bool && method1.getGenericReturnType() instanceof Class) ? 1 : 0; 
      if (method2 != null)
        bool = (bool && method2.getGenericParameterTypes()[0] instanceof Class) ? 1 : 0; 
      if (paramPropertyDescriptor instanceof IndexedPropertyDescriptor) {
        IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor;
        Method method3 = indexedPropertyDescriptor.getIndexedReadMethod();
        Method method4 = indexedPropertyDescriptor.getIndexedWriteMethod();
        if (method3 != null)
          bool = (bool && method3.getGenericReturnType() instanceof Class) ? 1 : 0; 
        if (method4 != null)
          bool = (bool && method4.getGenericParameterTypes()[1] instanceof Class) ? 1 : 0; 
        if (!bool) {
          paramPropertyDescriptor = new IndexedPropertyDescriptor(indexedPropertyDescriptor);
          paramPropertyDescriptor.updateGenericsFor(this.beanClass);
        } 
      } else if (!bool) {
        paramPropertyDescriptor = new PropertyDescriptor(paramPropertyDescriptor);
        paramPropertyDescriptor.updateGenericsFor(this.beanClass);
      } 
    } 
    list.add(paramPropertyDescriptor);
  }
  
  private void addPropertyDescriptors(PropertyDescriptor[] paramArrayOfPropertyDescriptor) {
    if (paramArrayOfPropertyDescriptor != null)
      for (PropertyDescriptor propertyDescriptor : paramArrayOfPropertyDescriptor)
        addPropertyDescriptor(propertyDescriptor);  
  }
  
  private PropertyDescriptor[] getPropertyDescriptors(BeanInfo paramBeanInfo) {
    PropertyDescriptor[] arrayOfPropertyDescriptor = paramBeanInfo.getPropertyDescriptors();
    int i = paramBeanInfo.getDefaultPropertyIndex();
    if (0 <= i && i < arrayOfPropertyDescriptor.length)
      this.defaultPropertyName = arrayOfPropertyDescriptor[i].getName(); 
    return arrayOfPropertyDescriptor;
  }
  
  private void processPropertyDescriptors() {
    if (this.properties == null)
      this.properties = new TreeMap(); 
    Iterator iterator = this.pdStore.values().iterator();
    while (iterator.hasNext()) {
      PropertyDescriptor propertyDescriptor1 = null;
      PropertyDescriptor propertyDescriptor2 = null;
      PropertyDescriptor propertyDescriptor3 = null;
      IndexedPropertyDescriptor indexedPropertyDescriptor = null;
      PropertyDescriptor propertyDescriptor4 = null;
      PropertyDescriptor propertyDescriptor5 = null;
      List list = (List)iterator.next();
      byte b;
      for (b = 0; b < list.size(); b++) {
        propertyDescriptor1 = (PropertyDescriptor)list.get(b);
        if (propertyDescriptor1 instanceof IndexedPropertyDescriptor) {
          indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor1;
          if (indexedPropertyDescriptor.getIndexedReadMethod() != null)
            if (propertyDescriptor4 != null) {
              propertyDescriptor4 = new IndexedPropertyDescriptor(propertyDescriptor4, indexedPropertyDescriptor);
            } else {
              propertyDescriptor4 = indexedPropertyDescriptor;
            }  
        } else if (propertyDescriptor1.getReadMethod() != null) {
          String str = propertyDescriptor1.getReadMethod().getName();
          if (propertyDescriptor2 != null) {
            String str1 = propertyDescriptor2.getReadMethod().getName();
            if (str1.equals(str) || !str1.startsWith("is"))
              propertyDescriptor2 = new PropertyDescriptor(propertyDescriptor2, propertyDescriptor1); 
          } else {
            propertyDescriptor2 = propertyDescriptor1;
          } 
        } 
      } 
      for (b = 0; b < list.size(); b++) {
        propertyDescriptor1 = (PropertyDescriptor)list.get(b);
        if (propertyDescriptor1 instanceof IndexedPropertyDescriptor) {
          indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor1;
          if (indexedPropertyDescriptor.getIndexedWriteMethod() != null)
            if (propertyDescriptor4 != null) {
              if (isAssignable(propertyDescriptor4.getIndexedPropertyType(), indexedPropertyDescriptor.getIndexedPropertyType()))
                if (propertyDescriptor5 != null) {
                  propertyDescriptor5 = new IndexedPropertyDescriptor(propertyDescriptor5, indexedPropertyDescriptor);
                } else {
                  propertyDescriptor5 = indexedPropertyDescriptor;
                }  
            } else if (propertyDescriptor5 != null) {
              propertyDescriptor5 = new IndexedPropertyDescriptor(propertyDescriptor5, indexedPropertyDescriptor);
            } else {
              propertyDescriptor5 = indexedPropertyDescriptor;
            }  
        } else if (propertyDescriptor1.getWriteMethod() != null) {
          if (propertyDescriptor2 != null) {
            if (isAssignable(propertyDescriptor2.getPropertyType(), propertyDescriptor1.getPropertyType()))
              if (propertyDescriptor3 != null) {
                propertyDescriptor3 = new PropertyDescriptor(propertyDescriptor3, propertyDescriptor1);
              } else {
                propertyDescriptor3 = propertyDescriptor1;
              }  
          } else if (propertyDescriptor3 != null) {
            propertyDescriptor3 = new PropertyDescriptor(propertyDescriptor3, propertyDescriptor1);
          } else {
            propertyDescriptor3 = propertyDescriptor1;
          } 
        } 
      } 
      propertyDescriptor1 = null;
      indexedPropertyDescriptor = null;
      if (propertyDescriptor4 != null && propertyDescriptor5 != null) {
        if (propertyDescriptor2 == propertyDescriptor3 || propertyDescriptor2 == null) {
          propertyDescriptor1 = propertyDescriptor3;
        } else if (propertyDescriptor3 == null) {
          propertyDescriptor1 = propertyDescriptor2;
        } else if (propertyDescriptor3 instanceof IndexedPropertyDescriptor) {
          propertyDescriptor1 = mergePropertyWithIndexedProperty(propertyDescriptor2, (IndexedPropertyDescriptor)propertyDescriptor3);
        } else if (propertyDescriptor2 instanceof IndexedPropertyDescriptor) {
          propertyDescriptor1 = mergePropertyWithIndexedProperty(propertyDescriptor3, (IndexedPropertyDescriptor)propertyDescriptor2);
        } else {
          propertyDescriptor1 = mergePropertyDescriptor(propertyDescriptor2, propertyDescriptor3);
        } 
        if (propertyDescriptor4 == propertyDescriptor5) {
          PropertyDescriptor propertyDescriptor = propertyDescriptor4;
        } else {
          indexedPropertyDescriptor = mergePropertyDescriptor(propertyDescriptor4, propertyDescriptor5);
        } 
        if (propertyDescriptor1 == null) {
          propertyDescriptor1 = indexedPropertyDescriptor;
        } else {
          Class clazz1 = propertyDescriptor1.getPropertyType();
          Class clazz2 = indexedPropertyDescriptor.getIndexedPropertyType();
          if (clazz1.isArray() && clazz1.getComponentType() == clazz2) {
            propertyDescriptor1 = propertyDescriptor1.getClass0().isAssignableFrom(indexedPropertyDescriptor.getClass0()) ? new IndexedPropertyDescriptor(propertyDescriptor1, indexedPropertyDescriptor) : new IndexedPropertyDescriptor(indexedPropertyDescriptor, propertyDescriptor1);
          } else if (propertyDescriptor1.getClass0().isAssignableFrom(indexedPropertyDescriptor.getClass0())) {
            propertyDescriptor1 = propertyDescriptor1.getClass0().isAssignableFrom(indexedPropertyDescriptor.getClass0()) ? new PropertyDescriptor(propertyDescriptor1, indexedPropertyDescriptor) : new PropertyDescriptor(indexedPropertyDescriptor, propertyDescriptor1);
          } else {
            propertyDescriptor1 = indexedPropertyDescriptor;
          } 
        } 
      } else if (propertyDescriptor2 != null && propertyDescriptor3 != null) {
        if (propertyDescriptor4 != null)
          propertyDescriptor2 = mergePropertyWithIndexedProperty(propertyDescriptor2, propertyDescriptor4); 
        if (propertyDescriptor5 != null)
          propertyDescriptor3 = mergePropertyWithIndexedProperty(propertyDescriptor3, propertyDescriptor5); 
        if (propertyDescriptor2 == propertyDescriptor3) {
          propertyDescriptor1 = propertyDescriptor2;
        } else if (propertyDescriptor3 instanceof IndexedPropertyDescriptor) {
          propertyDescriptor1 = mergePropertyWithIndexedProperty(propertyDescriptor2, (IndexedPropertyDescriptor)propertyDescriptor3);
        } else if (propertyDescriptor2 instanceof IndexedPropertyDescriptor) {
          propertyDescriptor1 = mergePropertyWithIndexedProperty(propertyDescriptor3, (IndexedPropertyDescriptor)propertyDescriptor2);
        } else {
          propertyDescriptor1 = mergePropertyDescriptor(propertyDescriptor2, propertyDescriptor3);
        } 
      } else if (propertyDescriptor5 != null) {
        propertyDescriptor1 = propertyDescriptor5;
        if (propertyDescriptor3 != null)
          propertyDescriptor1 = mergePropertyDescriptor(propertyDescriptor5, propertyDescriptor3); 
        if (propertyDescriptor2 != null)
          propertyDescriptor1 = mergePropertyDescriptor(propertyDescriptor5, propertyDescriptor2); 
      } else if (propertyDescriptor4 != null) {
        propertyDescriptor1 = propertyDescriptor4;
        if (propertyDescriptor2 != null)
          propertyDescriptor1 = mergePropertyDescriptor(propertyDescriptor4, propertyDescriptor2); 
        if (propertyDescriptor3 != null)
          propertyDescriptor1 = mergePropertyDescriptor(propertyDescriptor4, propertyDescriptor3); 
      } else if (propertyDescriptor3 != null) {
        propertyDescriptor1 = propertyDescriptor3;
      } else if (propertyDescriptor2 != null) {
        propertyDescriptor1 = propertyDescriptor2;
      } 
      if (propertyDescriptor1 instanceof IndexedPropertyDescriptor) {
        indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor1;
        if (indexedPropertyDescriptor.getIndexedReadMethod() == null && indexedPropertyDescriptor.getIndexedWriteMethod() == null)
          propertyDescriptor1 = new PropertyDescriptor(indexedPropertyDescriptor); 
      } 
      if (propertyDescriptor1 == null && list.size() > 0)
        propertyDescriptor1 = (PropertyDescriptor)list.get(0); 
      if (propertyDescriptor1 != null)
        this.properties.put(propertyDescriptor1.getName(), propertyDescriptor1); 
    } 
  }
  
  private static boolean isAssignable(Class<?> paramClass1, Class<?> paramClass2) { return (paramClass1 == null || paramClass2 == null) ? ((paramClass1 == paramClass2)) : paramClass1.isAssignableFrom(paramClass2); }
  
  private PropertyDescriptor mergePropertyWithIndexedProperty(PropertyDescriptor paramPropertyDescriptor, IndexedPropertyDescriptor paramIndexedPropertyDescriptor) {
    Class clazz = paramPropertyDescriptor.getPropertyType();
    return (clazz.isArray() && clazz.getComponentType() == paramIndexedPropertyDescriptor.getIndexedPropertyType()) ? (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0()) ? new IndexedPropertyDescriptor(paramPropertyDescriptor, paramIndexedPropertyDescriptor) : new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor, paramPropertyDescriptor)) : paramPropertyDescriptor;
  }
  
  private PropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor, PropertyDescriptor paramPropertyDescriptor) {
    PropertyDescriptor propertyDescriptor = null;
    Class clazz1 = paramPropertyDescriptor.getPropertyType();
    Class clazz2 = paramIndexedPropertyDescriptor.getIndexedPropertyType();
    if (clazz1.isArray() && clazz1.getComponentType() == clazz2) {
      if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0())) {
        propertyDescriptor = new IndexedPropertyDescriptor(paramPropertyDescriptor, paramIndexedPropertyDescriptor);
      } else {
        propertyDescriptor = new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor, paramPropertyDescriptor);
      } 
    } else if (paramIndexedPropertyDescriptor.getReadMethod() == null && paramIndexedPropertyDescriptor.getWriteMethod() == null) {
      if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0())) {
        PropertyDescriptor propertyDescriptor1 = new PropertyDescriptor(paramPropertyDescriptor, paramIndexedPropertyDescriptor);
      } else {
        PropertyDescriptor propertyDescriptor1 = new PropertyDescriptor(paramIndexedPropertyDescriptor, paramPropertyDescriptor);
      } 
    } else if (paramPropertyDescriptor.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor.getClass0())) {
      propertyDescriptor = paramIndexedPropertyDescriptor;
    } else {
      propertyDescriptor = paramPropertyDescriptor;
      Method method1 = propertyDescriptor.getWriteMethod();
      Method method2 = propertyDescriptor.getReadMethod();
      if (method2 == null && method1 != null) {
        method2 = findMethod(propertyDescriptor.getClass0(), "get" + NameGenerator.capitalize(propertyDescriptor.getName()), 0);
        if (method2 != null)
          try {
            propertyDescriptor.setReadMethod(method2);
          } catch (IntrospectionException introspectionException) {} 
      } 
      if (method1 == null && method2 != null) {
        method1 = findMethod(propertyDescriptor.getClass0(), "set" + NameGenerator.capitalize(propertyDescriptor.getName()), 1, new Class[] { FeatureDescriptor.getReturnType(propertyDescriptor.getClass0(), method2) });
        if (method1 != null)
          try {
            propertyDescriptor.setWriteMethod(method1);
          } catch (IntrospectionException introspectionException) {} 
      } 
    } 
    return propertyDescriptor;
  }
  
  private PropertyDescriptor mergePropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2) { return paramPropertyDescriptor1.getClass0().isAssignableFrom(paramPropertyDescriptor2.getClass0()) ? new PropertyDescriptor(paramPropertyDescriptor1, paramPropertyDescriptor2) : new PropertyDescriptor(paramPropertyDescriptor2, paramPropertyDescriptor1); }
  
  private IndexedPropertyDescriptor mergePropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor1, IndexedPropertyDescriptor paramIndexedPropertyDescriptor2) { return paramIndexedPropertyDescriptor1.getClass0().isAssignableFrom(paramIndexedPropertyDescriptor2.getClass0()) ? new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor1, paramIndexedPropertyDescriptor2) : new IndexedPropertyDescriptor(paramIndexedPropertyDescriptor2, paramIndexedPropertyDescriptor1); }
  
  private EventSetDescriptor[] getTargetEventInfo() throws IntrospectionException {
    EventSetDescriptor[] arrayOfEventSetDescriptor2;
    if (this.events == null)
      this.events = new HashMap(); 
    EventSetDescriptor[] arrayOfEventSetDescriptor1 = null;
    if (this.explicitBeanInfo != null) {
      arrayOfEventSetDescriptor1 = this.explicitBeanInfo.getEventSetDescriptors();
      int i = this.explicitBeanInfo.getDefaultEventIndex();
      if (i >= 0 && i < arrayOfEventSetDescriptor1.length)
        this.defaultEventName = arrayOfEventSetDescriptor1[i].getName(); 
    } 
    if (arrayOfEventSetDescriptor1 == null && this.superBeanInfo != null) {
      arrayOfEventSetDescriptor2 = this.superBeanInfo.getEventSetDescriptors();
      int i;
      for (i = 0; i < arrayOfEventSetDescriptor2.length; i++)
        addEvent(arrayOfEventSetDescriptor2[i]); 
      i = this.superBeanInfo.getDefaultEventIndex();
      if (i >= 0 && i < arrayOfEventSetDescriptor2.length)
        this.defaultEventName = arrayOfEventSetDescriptor2[i].getName(); 
    } 
    byte b;
    for (b = 0; b < this.additionalBeanInfo.length; b++) {
      EventSetDescriptor[] arrayOfEventSetDescriptor = this.additionalBeanInfo[b].getEventSetDescriptors();
      if (arrayOfEventSetDescriptor != null)
        for (byte b1 = 0; b1 < arrayOfEventSetDescriptor.length; b1++)
          addEvent(arrayOfEventSetDescriptor[b1]);  
    } 
    if (arrayOfEventSetDescriptor1 != null) {
      for (b = 0; b < arrayOfEventSetDescriptor1.length; b++)
        addEvent(arrayOfEventSetDescriptor1[b]); 
    } else {
      arrayOfEventSetDescriptor2 = getPublicDeclaredMethods(this.beanClass);
      HashMap hashMap1 = null;
      HashMap hashMap2 = null;
      HashMap hashMap3 = null;
      for (byte b1 = 0; b1 < arrayOfEventSetDescriptor2.length; b1++) {
        Method method = arrayOfEventSetDescriptor2[b1];
        if (method != null) {
          int i = method.getModifiers();
          if (!Modifier.isStatic(i)) {
            String str = method.getName();
            if (str.startsWith("add") || str.startsWith("remove") || str.startsWith("get"))
              if (str.startsWith("add")) {
                Class clazz = method.getReturnType();
                if (clazz == void.class) {
                  Type[] arrayOfType = method.getGenericParameterTypes();
                  if (arrayOfType.length == 1) {
                    Class clazz1 = TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, arrayOfType[0]));
                    if (isSubclass(clazz1, eventListenerType)) {
                      String str1 = str.substring(3);
                      if (str1.length() > 0 && clazz1.getName().endsWith(str1)) {
                        if (hashMap1 == null)
                          hashMap1 = new HashMap(); 
                        hashMap1.put(str1, method);
                      } 
                    } 
                  } 
                } 
              } else if (str.startsWith("remove")) {
                Class clazz = method.getReturnType();
                if (clazz == void.class) {
                  Type[] arrayOfType = method.getGenericParameterTypes();
                  if (arrayOfType.length == 1) {
                    Class clazz1 = TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, arrayOfType[0]));
                    if (isSubclass(clazz1, eventListenerType)) {
                      String str1 = str.substring(6);
                      if (str1.length() > 0 && clazz1.getName().endsWith(str1)) {
                        if (hashMap2 == null)
                          hashMap2 = new HashMap(); 
                        hashMap2.put(str1, method);
                      } 
                    } 
                  } 
                } 
              } else if (str.startsWith("get")) {
                Class[] arrayOfClass = method.getParameterTypes();
                if (arrayOfClass.length == 0) {
                  Class clazz = FeatureDescriptor.getReturnType(this.beanClass, method);
                  if (clazz.isArray()) {
                    Class clazz1 = clazz.getComponentType();
                    if (isSubclass(clazz1, eventListenerType)) {
                      String str1 = str.substring(3, str.length() - 1);
                      if (str1.length() > 0 && clazz1.getName().endsWith(str1)) {
                        if (hashMap3 == null)
                          hashMap3 = new HashMap(); 
                        hashMap3.put(str1, method);
                      } 
                    } 
                  } 
                } 
              }  
          } 
        } 
      } 
      if (hashMap1 != null && hashMap2 != null)
        for (String str1 : hashMap1.keySet()) {
          if (hashMap2.get(str1) == null || !str1.endsWith("Listener"))
            continue; 
          String str2 = decapitalize(str1.substring(0, str1.length() - 8));
          Method method1 = (Method)hashMap1.get(str1);
          Method method2 = (Method)hashMap2.get(str1);
          Method method3 = null;
          if (hashMap3 != null)
            method3 = (Method)hashMap3.get(str1); 
          Class clazz = FeatureDescriptor.getParameterTypes(this.beanClass, method1)[0];
          Method[] arrayOfMethod1 = getPublicDeclaredMethods(clazz);
          ArrayList arrayList = new ArrayList(arrayOfMethod1.length);
          for (byte b2 = 0; b2 < arrayOfMethod1.length; b2++) {
            if (arrayOfMethod1[b2] != null && isEventHandler(arrayOfMethod1[b2]))
              arrayList.add(arrayOfMethod1[b2]); 
          } 
          Method[] arrayOfMethod2 = (Method[])arrayList.toArray(new Method[arrayList.size()]);
          EventSetDescriptor eventSetDescriptor = new EventSetDescriptor(str2, clazz, arrayOfMethod2, method1, method2, method3);
          if (throwsException(method1, java.util.TooManyListenersException.class))
            eventSetDescriptor.setUnicast(true); 
          addEvent(eventSetDescriptor);
        }  
    } 
    if (this.events.size() == 0) {
      arrayOfEventSetDescriptor2 = EMPTY_EVENTSETDESCRIPTORS;
    } else {
      arrayOfEventSetDescriptor2 = new EventSetDescriptor[this.events.size()];
      arrayOfEventSetDescriptor2 = (EventSetDescriptor[])this.events.values().toArray(arrayOfEventSetDescriptor2);
      if (this.defaultEventName != null)
        for (byte b1 = 0; b1 < arrayOfEventSetDescriptor2.length; b1++) {
          if (this.defaultEventName.equals(arrayOfEventSetDescriptor2[b1].getName()))
            this.defaultEventIndex = b1; 
        }  
    } 
    return arrayOfEventSetDescriptor2;
  }
  
  private void addEvent(EventSetDescriptor paramEventSetDescriptor) {
    String str = paramEventSetDescriptor.getName();
    if (paramEventSetDescriptor.getName().equals("propertyChange"))
      this.propertyChangeSource = true; 
    EventSetDescriptor eventSetDescriptor1 = (EventSetDescriptor)this.events.get(str);
    if (eventSetDescriptor1 == null) {
      this.events.put(str, paramEventSetDescriptor);
      return;
    } 
    EventSetDescriptor eventSetDescriptor2 = new EventSetDescriptor(eventSetDescriptor1, paramEventSetDescriptor);
    this.events.put(str, eventSetDescriptor2);
  }
  
  private MethodDescriptor[] getTargetMethodInfo() {
    if (this.methods == null)
      this.methods = new HashMap(100); 
    MethodDescriptor[] arrayOfMethodDescriptor = null;
    if (this.explicitBeanInfo != null)
      arrayOfMethodDescriptor = this.explicitBeanInfo.getMethodDescriptors(); 
    if (arrayOfMethodDescriptor == null && this.superBeanInfo != null) {
      MethodDescriptor[] arrayOfMethodDescriptor1 = this.superBeanInfo.getMethodDescriptors();
      for (byte b1 = 0; b1 < arrayOfMethodDescriptor1.length; b1++)
        addMethod(arrayOfMethodDescriptor1[b1]); 
    } 
    byte b;
    for (b = 0; b < this.additionalBeanInfo.length; b++) {
      MethodDescriptor[] arrayOfMethodDescriptor1 = this.additionalBeanInfo[b].getMethodDescriptors();
      if (arrayOfMethodDescriptor1 != null)
        for (byte b1 = 0; b1 < arrayOfMethodDescriptor1.length; b1++)
          addMethod(arrayOfMethodDescriptor1[b1]);  
    } 
    if (arrayOfMethodDescriptor != null) {
      for (b = 0; b < arrayOfMethodDescriptor.length; b++)
        addMethod(arrayOfMethodDescriptor[b]); 
    } else {
      Method[] arrayOfMethod = getPublicDeclaredMethods(this.beanClass);
      for (byte b1 = 0; b1 < arrayOfMethod.length; b1++) {
        Method method = arrayOfMethod[b1];
        if (method != null) {
          MethodDescriptor methodDescriptor = new MethodDescriptor(method);
          addMethod(methodDescriptor);
        } 
      } 
    } 
    null = new MethodDescriptor[this.methods.size()];
    return (MethodDescriptor[])this.methods.values().toArray(null);
  }
  
  private void addMethod(MethodDescriptor paramMethodDescriptor) {
    String str1 = paramMethodDescriptor.getName();
    MethodDescriptor methodDescriptor1 = (MethodDescriptor)this.methods.get(str1);
    if (methodDescriptor1 == null) {
      this.methods.put(str1, paramMethodDescriptor);
      return;
    } 
    String[] arrayOfString1 = paramMethodDescriptor.getParamNames();
    String[] arrayOfString2 = methodDescriptor1.getParamNames();
    boolean bool = false;
    if (arrayOfString1.length == arrayOfString2.length) {
      bool = true;
      for (byte b = 0; b < arrayOfString1.length; b++) {
        if (arrayOfString1[b] != arrayOfString2[b]) {
          bool = false;
          break;
        } 
      } 
    } 
    if (bool) {
      MethodDescriptor methodDescriptor = new MethodDescriptor(methodDescriptor1, paramMethodDescriptor);
      this.methods.put(str1, methodDescriptor);
      return;
    } 
    String str2 = makeQualifiedMethodName(str1, arrayOfString1);
    methodDescriptor1 = (MethodDescriptor)this.methods.get(str2);
    if (methodDescriptor1 == null) {
      this.methods.put(str2, paramMethodDescriptor);
      return;
    } 
    MethodDescriptor methodDescriptor2 = new MethodDescriptor(methodDescriptor1, paramMethodDescriptor);
    this.methods.put(str2, methodDescriptor2);
  }
  
  private static String makeQualifiedMethodName(String paramString, String[] paramArrayOfString) {
    StringBuffer stringBuffer = new StringBuffer(paramString);
    stringBuffer.append('=');
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      stringBuffer.append(':');
      stringBuffer.append(paramArrayOfString[b]);
    } 
    return stringBuffer.toString();
  }
  
  private int getTargetDefaultEventIndex() { return this.defaultEventIndex; }
  
  private int getTargetDefaultPropertyIndex() { return this.defaultPropertyIndex; }
  
  private BeanDescriptor getTargetBeanDescriptor() {
    if (this.explicitBeanInfo != null) {
      BeanDescriptor beanDescriptor = this.explicitBeanInfo.getBeanDescriptor();
      if (beanDescriptor != null)
        return beanDescriptor; 
    } 
    return new BeanDescriptor(this.beanClass, findCustomizerClass(this.beanClass));
  }
  
  private static Class<?> findCustomizerClass(Class<?> paramClass) {
    String str = paramClass.getName() + "Customizer";
    try {
      paramClass = ClassFinder.findClass(str, paramClass.getClassLoader());
      if (java.awt.Component.class.isAssignableFrom(paramClass) && Customizer.class.isAssignableFrom(paramClass))
        return paramClass; 
    } catch (Exception exception) {}
    return null;
  }
  
  private boolean isEventHandler(Method paramMethod) {
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    return (arrayOfType.length != 1) ? false : isSubclass(TypeResolver.erase(TypeResolver.resolveInClass(this.beanClass, arrayOfType[0])), java.util.EventObject.class);
  }
  
  private static Method[] getPublicDeclaredMethods(Class<?> paramClass) {
    if (!ReflectUtil.isPackageAccessible(paramClass))
      return new Method[0]; 
    synchronized (declaredMethodCache) {
      Method[] arrayOfMethod = (Method[])declaredMethodCache.get(paramClass);
      if (arrayOfMethod == null) {
        arrayOfMethod = paramClass.getMethods();
        for (byte b = 0; b < arrayOfMethod.length; b++) {
          Method method = arrayOfMethod[b];
          if (!method.getDeclaringClass().equals(paramClass)) {
            arrayOfMethod[b] = null;
          } else {
            try {
              method = MethodFinder.findAccessibleMethod(method);
              Class clazz = method.getDeclaringClass();
              arrayOfMethod[b] = (clazz.equals(paramClass) || clazz.isInterface()) ? method : null;
            } catch (NoSuchMethodException noSuchMethodException) {}
          } 
        } 
        declaredMethodCache.put(paramClass, arrayOfMethod);
      } 
      return arrayOfMethod;
    } 
  }
  
  private static Method internalFindMethod(Class<?> paramClass, String paramString, int paramInt, Class[] paramArrayOfClass) {
    Method method = null;
    for (Class<?> clazz = paramClass; clazz != null; clazz = clazz.getSuperclass()) {
      Method[] arrayOfMethod = getPublicDeclaredMethods(clazz);
      for (byte b1 = 0; b1 < arrayOfMethod.length; b1++) {
        method = arrayOfMethod[b1];
        if (method != null && method.getName().equals(paramString)) {
          Type[] arrayOfType = method.getGenericParameterTypes();
          if (arrayOfType.length == paramInt) {
            if (paramArrayOfClass != null) {
              boolean bool = false;
              if (paramInt > 0) {
                for (byte b2 = 0; b2 < paramInt; b2++) {
                  if (TypeResolver.erase(TypeResolver.resolveInClass(paramClass, arrayOfType[b2])) != paramArrayOfClass[b2])
                    bool = true; 
                } 
                if (bool)
                  continue; 
              } 
            } 
            return method;
          } 
        } 
        continue;
      } 
    } 
    method = null;
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      method = internalFindMethod(arrayOfClass[b], paramString, paramInt, null);
      if (method != null)
        break; 
    } 
    return method;
  }
  
  static Method findMethod(Class<?> paramClass, String paramString, int paramInt) { return findMethod(paramClass, paramString, paramInt, null); }
  
  static Method findMethod(Class<?> paramClass, String paramString, int paramInt, Class[] paramArrayOfClass) { return (paramString == null) ? null : internalFindMethod(paramClass, paramString, paramInt, paramArrayOfClass); }
  
  static boolean isSubclass(Class<?> paramClass1, Class<?> paramClass2) {
    if (paramClass1 == paramClass2)
      return true; 
    if (paramClass1 == null || paramClass2 == null)
      return false; 
    for (Class<?> clazz = paramClass1; clazz != null; clazz = clazz.getSuperclass()) {
      if (clazz == paramClass2)
        return true; 
      if (paramClass2.isInterface()) {
        Class[] arrayOfClass = clazz.getInterfaces();
        for (byte b = 0; b < arrayOfClass.length; b++) {
          if (isSubclass(arrayOfClass[b], paramClass2))
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  private boolean throwsException(Method paramMethod, Class<?> paramClass) {
    Class[] arrayOfClass = paramMethod.getExceptionTypes();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (arrayOfClass[b] == paramClass)
        return true; 
    } 
    return false;
  }
  
  static Object instantiate(Class<?> paramClass, String paramString) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    ClassLoader classLoader = paramClass.getClassLoader();
    Class clazz = ClassFinder.findClass(paramString, classLoader);
    return clazz.newInstance();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\Introspector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */