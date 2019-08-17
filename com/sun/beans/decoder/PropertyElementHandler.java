package com.sun.beans.decoder;

import com.sun.beans.finder.MethodFinder;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;

final class PropertyElementHandler extends AccessorElementHandler {
  static final String GETTER = "get";
  
  static final String SETTER = "set";
  
  private Integer index;
  
  public void addAttribute(String paramString1, String paramString2) {
    if (paramString1.equals("index")) {
      this.index = Integer.valueOf(paramString2);
    } else {
      super.addAttribute(paramString1, paramString2);
    } 
  }
  
  protected boolean isArgument() { return false; }
  
  protected Object getValue(String paramString) {
    try {
      return getPropertyValue(getContextBean(), paramString, this.index);
    } catch (Exception exception) {
      getOwner().handleException(exception);
      return null;
    } 
  }
  
  protected void setValue(String paramString, Object paramObject) {
    try {
      setPropertyValue(getContextBean(), paramString, this.index, paramObject);
    } catch (Exception exception) {
      getOwner().handleException(exception);
    } 
  }
  
  private static Object getPropertyValue(Object paramObject, String paramString, Integer paramInteger) throws IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
    Class clazz = paramObject.getClass();
    return (paramInteger == null) ? MethodUtil.invoke(findGetter(clazz, paramString, new Class[0]), paramObject, new Object[0]) : ((clazz.isArray() && paramString == null) ? Array.get(paramObject, paramInteger.intValue()) : MethodUtil.invoke(findGetter(clazz, paramString, new Class[] { int.class }), paramObject, new Object[] { paramInteger }));
  }
  
  private static void setPropertyValue(Object paramObject1, String paramString, Integer paramInteger, Object paramObject2) throws IllegalAccessException, IntrospectionException, InvocationTargetException, NoSuchMethodException {
    Class clazz1 = paramObject1.getClass();
    Class clazz2 = (paramObject2 != null) ? paramObject2.getClass() : null;
    if (paramInteger == null) {
      MethodUtil.invoke(findSetter(clazz1, paramString, new Class[] { clazz2 }), paramObject1, new Object[] { paramObject2 });
    } else if (clazz1.isArray() && paramString == null) {
      Array.set(paramObject1, paramInteger.intValue(), paramObject2);
    } else {
      MethodUtil.invoke(findSetter(clazz1, paramString, new Class[] { int.class, clazz2 }), paramObject1, new Object[] { paramInteger, paramObject2 });
    } 
  }
  
  private static Method findGetter(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) throws IntrospectionException, NoSuchMethodException {
    if (paramString == null)
      return MethodFinder.findInstanceMethod(paramClass, "get", paramVarArgs); 
    PropertyDescriptor propertyDescriptor = getProperty(paramClass, paramString);
    if (paramVarArgs.length == 0) {
      Method method = propertyDescriptor.getReadMethod();
      if (method != null)
        return method; 
    } else if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
      IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor;
      Method method = indexedPropertyDescriptor.getIndexedReadMethod();
      if (method != null)
        return method; 
    } 
    throw new IntrospectionException("Could not find getter for the " + paramString + " property");
  }
  
  private static Method findSetter(Class<?> paramClass, String paramString, Class<?>... paramVarArgs) throws IntrospectionException, NoSuchMethodException {
    if (paramString == null)
      return MethodFinder.findInstanceMethod(paramClass, "set", paramVarArgs); 
    PropertyDescriptor propertyDescriptor = getProperty(paramClass, paramString);
    if (paramVarArgs.length == 1) {
      Method method = propertyDescriptor.getWriteMethod();
      if (method != null)
        return method; 
    } else if (propertyDescriptor instanceof IndexedPropertyDescriptor) {
      IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)propertyDescriptor;
      Method method = indexedPropertyDescriptor.getIndexedWriteMethod();
      if (method != null)
        return method; 
    } 
    throw new IntrospectionException("Could not find setter for the " + paramString + " property");
  }
  
  private static PropertyDescriptor getProperty(Class<?> paramClass, String paramString) throws IntrospectionException {
    for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(paramClass).getPropertyDescriptors()) {
      if (paramString.equals(propertyDescriptor.getName()))
        return propertyDescriptor; 
    } 
    throw new IntrospectionException("Could not find the " + paramString + " property descriptor");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\decoder\PropertyElementHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */