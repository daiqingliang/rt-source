package java.beans;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EventListener;
import java.util.Objects;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class DefaultPersistenceDelegate extends PersistenceDelegate {
  private static final String[] EMPTY = new String[0];
  
  private final String[] constructor = EMPTY;
  
  private Boolean definesEquals;
  
  public DefaultPersistenceDelegate() {}
  
  public DefaultPersistenceDelegate(String[] paramArrayOfString) {}
  
  private static boolean definesEquals(Class<?> paramClass) {
    try {
      return (paramClass == paramClass.getMethod("equals", new Class[] { Object.class }).getDeclaringClass());
    } catch (NoSuchMethodException noSuchMethodException) {
      return false;
    } 
  }
  
  private boolean definesEquals(Object paramObject) {
    if (this.definesEquals != null)
      return (this.definesEquals == Boolean.TRUE); 
    boolean bool = definesEquals(paramObject.getClass());
    this.definesEquals = bool ? Boolean.TRUE : Boolean.FALSE;
    return bool;
  }
  
  protected boolean mutatesTo(Object paramObject1, Object paramObject2) { return (this.constructor.length == 0 || !definesEquals(paramObject1)) ? super.mutatesTo(paramObject1, paramObject2) : paramObject1.equals(paramObject2); }
  
  protected Expression instantiate(Object paramObject, Encoder paramEncoder) {
    int i = this.constructor.length;
    Class clazz = paramObject.getClass();
    Object[] arrayOfObject = new Object[i];
    for (byte b = 0; b < i; b++) {
      try {
        Method method = findMethod(clazz, this.constructor[b]);
        arrayOfObject[b] = MethodUtil.invoke(method, paramObject, new Object[0]);
      } catch (Exception exception) {
        paramEncoder.getExceptionListener().exceptionThrown(exception);
      } 
    } 
    return new Expression(paramObject, paramObject.getClass(), "new", arrayOfObject);
  }
  
  private Method findMethod(Class<?> paramClass, String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Property name is null"); 
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(paramClass, paramString);
    if (propertyDescriptor == null)
      throw new IllegalStateException("Could not find property by the name " + paramString); 
    Method method = propertyDescriptor.getReadMethod();
    if (method == null)
      throw new IllegalStateException("Could not find getter for the property " + paramString); 
    return method;
  }
  
  private void doProperty(Class<?> paramClass, PropertyDescriptor paramPropertyDescriptor, Object paramObject1, Object paramObject2, Encoder paramEncoder) throws Exception {
    Method method1 = paramPropertyDescriptor.getReadMethod();
    Method method2 = paramPropertyDescriptor.getWriteMethod();
    if (method1 != null && method2 != null) {
      Expression expression1 = new Expression(paramObject1, method1.getName(), new Object[0]);
      Expression expression2 = new Expression(paramObject2, method1.getName(), new Object[0]);
      Object object1 = expression1.getValue();
      Object object2 = expression2.getValue();
      paramEncoder.writeExpression(expression1);
      if (!Objects.equals(object2, paramEncoder.get(object1))) {
        Object[] arrayOfObject = (Object[])paramPropertyDescriptor.getValue("enumerationValues");
        if (arrayOfObject instanceof Object[] && Array.getLength(arrayOfObject) % 3 == 0) {
          Object[] arrayOfObject1 = (Object[])arrayOfObject;
          byte b;
          for (b = 0; b < arrayOfObject1.length; b += 3) {
            try {
              Field field = paramClass.getField((String)arrayOfObject1[b]);
              if (field.get(null).equals(object1)) {
                paramEncoder.remove(object1);
                paramEncoder.writeExpression(new Expression(object1, field, "get", new Object[] { null }));
              } 
            } catch (Exception exception) {}
          } 
        } 
        invokeStatement(paramObject1, method2.getName(), new Object[] { object1 }, paramEncoder);
      } 
    } 
  }
  
  static void invokeStatement(Object paramObject, String paramString, Object[] paramArrayOfObject, Encoder paramEncoder) { paramEncoder.writeStatement(new Statement(paramObject, paramString, paramArrayOfObject)); }
  
  private void initBean(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder) {
    BeanInfo beanInfo;
    for (Field field : paramClass.getFields()) {
      if (ReflectUtil.isPackageAccessible(field.getDeclaringClass())) {
        int i = field.getModifiers();
        if (!Modifier.isFinal(i) && !Modifier.isStatic(i) && !Modifier.isTransient(i))
          try {
            Expression expression1 = new Expression(field, "get", new Object[] { paramObject1 });
            Expression expression2 = new Expression(field, "get", new Object[] { paramObject2 });
            Object object1 = expression1.getValue();
            Object object2 = expression2.getValue();
            paramEncoder.writeExpression(expression1);
            if (!Objects.equals(object2, paramEncoder.get(object1)))
              paramEncoder.writeStatement(new Statement(field, "set", new Object[] { paramObject1, object1 })); 
          } catch (Exception exception) {
            paramEncoder.getExceptionListener().exceptionThrown(exception);
          }  
      } 
    } 
    try {
      beanInfo = Introspector.getBeanInfo(paramClass);
    } catch (IntrospectionException introspectionException) {
      return;
    } 
    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
      if (!propertyDescriptor.isTransient())
        try {
          doProperty(paramClass, propertyDescriptor, paramObject1, paramObject2, paramEncoder);
        } catch (Exception exception) {
          paramEncoder.getExceptionListener().exceptionThrown(exception);
        }  
    } 
    if (!java.awt.Component.class.isAssignableFrom(paramClass))
      return; 
    for (EventSetDescriptor eventSetDescriptor : beanInfo.getEventSetDescriptors()) {
      if (!eventSetDescriptor.isTransient()) {
        Class clazz = eventSetDescriptor.getListenerType();
        if (clazz != java.awt.event.ComponentListener.class && (clazz != javax.swing.event.ChangeListener.class || paramClass != javax.swing.JMenuItem.class)) {
          EventListener[] arrayOfEventListener1 = new EventListener[0];
          EventListener[] arrayOfEventListener2 = new EventListener[0];
          try {
            Method method = eventSetDescriptor.getGetListenerMethod();
            arrayOfEventListener1 = (EventListener[])MethodUtil.invoke(method, paramObject1, new Object[0]);
            arrayOfEventListener2 = (EventListener[])MethodUtil.invoke(method, paramObject2, new Object[0]);
          } catch (Exception exception) {
            try {
              Method method = paramClass.getMethod("getListeners", new Class[] { Class.class });
              arrayOfEventListener1 = (EventListener[])MethodUtil.invoke(method, paramObject1, new Object[] { clazz });
              arrayOfEventListener2 = (EventListener[])MethodUtil.invoke(method, paramObject2, new Object[] { clazz });
            } catch (Exception exception1) {
              return;
            } 
          } 
          String str1 = eventSetDescriptor.getAddListenerMethod().getName();
          for (int i = arrayOfEventListener2.length; i < arrayOfEventListener1.length; i++) {
            invokeStatement(paramObject1, str1, new Object[] { arrayOfEventListener1[i] }, paramEncoder);
          } 
          String str2 = eventSetDescriptor.getRemoveListenerMethod().getName();
          for (int j = arrayOfEventListener1.length; j < arrayOfEventListener2.length; j++) {
            invokeStatement(paramObject1, str2, new Object[] { arrayOfEventListener2[j] }, paramEncoder);
          } 
        } 
      } 
    } 
  }
  
  protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder) {
    super.initialize(paramClass, paramObject1, paramObject2, paramEncoder);
    if (paramObject1.getClass() == paramClass)
      initBean(paramClass, paramObject1, paramObject2, paramEncoder); 
  }
  
  private static PropertyDescriptor getPropertyDescriptor(Class<?> paramClass, String paramString) {
    try {
      for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(paramClass).getPropertyDescriptors()) {
        if (paramString.equals(propertyDescriptor.getName()))
          return propertyDescriptor; 
      } 
    } catch (IntrospectionException introspectionException) {}
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\DefaultPersistenceDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */