package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

public class PropertyDescriptor extends FeatureDescriptor {
  private Reference<? extends Class<?>> propertyTypeRef;
  
  private final MethodRef readMethodRef = new MethodRef();
  
  private final MethodRef writeMethodRef = new MethodRef();
  
  private Reference<? extends Class<?>> propertyEditorClassRef;
  
  private boolean bound;
  
  private boolean constrained;
  
  private String baseName;
  
  private String writeMethodName;
  
  private String readMethodName;
  
  public PropertyDescriptor(String paramString, Class<?> paramClass) throws IntrospectionException { this(paramString, paramClass, "is" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString)); }
  
  public PropertyDescriptor(String paramString1, Class<?> paramClass, String paramString2, String paramString3) throws IntrospectionException {
    if (paramClass == null)
      throw new IntrospectionException("Target Bean class is null"); 
    if (paramString1 == null || paramString1.length() == 0)
      throw new IntrospectionException("bad property name"); 
    if ("".equals(paramString2) || "".equals(paramString3))
      throw new IntrospectionException("read or write method name should not be the empty string"); 
    setName(paramString1);
    setClass0(paramClass);
    this.readMethodName = paramString2;
    if (paramString2 != null && getReadMethod() == null)
      throw new IntrospectionException("Method not found: " + paramString2); 
    this.writeMethodName = paramString3;
    if (paramString3 != null && getWriteMethod() == null)
      throw new IntrospectionException("Method not found: " + paramString3); 
    Class[] arrayOfClass = { PropertyChangeListener.class };
    this.bound = (null != Introspector.findMethod(paramClass, "addPropertyChangeListener", arrayOfClass.length, arrayOfClass));
  }
  
  public PropertyDescriptor(String paramString, Method paramMethod1, Method paramMethod2) throws IntrospectionException {
    if (paramString == null || paramString.length() == 0)
      throw new IntrospectionException("bad property name"); 
    setName(paramString);
    setReadMethod(paramMethod1);
    setWriteMethod(paramMethod2);
  }
  
  PropertyDescriptor(Class<?> paramClass, String paramString, Method paramMethod1, Method paramMethod2) throws IntrospectionException {
    if (paramClass == null)
      throw new IntrospectionException("Target Bean class is null"); 
    setClass0(paramClass);
    setName(Introspector.decapitalize(paramString));
    setReadMethod(paramMethod1);
    setWriteMethod(paramMethod2);
    this.baseName = paramString;
  }
  
  public Class<?> getPropertyType() {
    Class clazz = getPropertyType0();
    if (clazz == null)
      try {
        clazz = findPropertyType(getReadMethod(), getWriteMethod());
        setPropertyType(clazz);
      } catch (IntrospectionException introspectionException) {} 
    return clazz;
  }
  
  private void setPropertyType(Class<?> paramClass) { this.propertyTypeRef = getWeakReference(paramClass); }
  
  private Class<?> getPropertyType0() { return (this.propertyTypeRef != null) ? (Class)this.propertyTypeRef.get() : null; }
  
  public Method getReadMethod() {
    Method method = this.readMethodRef.get();
    if (method == null) {
      Class clazz = getClass0();
      if (clazz == null || (this.readMethodName == null && !this.readMethodRef.isSet()))
        return null; 
      String str = "get" + getBaseName();
      if (this.readMethodName == null) {
        Class clazz1 = getPropertyType0();
        if (clazz1 == boolean.class || clazz1 == null) {
          this.readMethodName = "is" + getBaseName();
        } else {
          this.readMethodName = str;
        } 
      } 
      method = Introspector.findMethod(clazz, this.readMethodName, 0);
      if (method == null && !this.readMethodName.equals(str)) {
        this.readMethodName = str;
        method = Introspector.findMethod(clazz, this.readMethodName, 0);
      } 
      try {
        setReadMethod(method);
      } catch (IntrospectionException introspectionException) {}
    } 
    return method;
  }
  
  public void setReadMethod(Method paramMethod) throws IntrospectionException {
    this.readMethodRef.set(paramMethod);
    if (paramMethod == null) {
      this.readMethodName = null;
      return;
    } 
    setPropertyType(findPropertyType(paramMethod, this.writeMethodRef.get()));
    setClass0(paramMethod.getDeclaringClass());
    this.readMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public Method getWriteMethod() {
    Method method = this.writeMethodRef.get();
    if (method == null) {
      Class clazz1 = getClass0();
      if (clazz1 == null || (this.writeMethodName == null && !this.writeMethodRef.isSet()))
        return null; 
      Class clazz2 = getPropertyType0();
      if (clazz2 == null)
        try {
          clazz2 = findPropertyType(getReadMethod(), null);
          setPropertyType(clazz2);
        } catch (IntrospectionException introspectionException) {
          return null;
        }  
      if (this.writeMethodName == null)
        this.writeMethodName = "set" + getBaseName(); 
      new Class[1][0] = clazz2;
      Class[] arrayOfClass = (clazz2 == null) ? null : new Class[1];
      method = Introspector.findMethod(clazz1, this.writeMethodName, 1, arrayOfClass);
      if (method != null && !method.getReturnType().equals(void.class))
        method = null; 
      try {
        setWriteMethod(method);
      } catch (IntrospectionException introspectionException) {}
    } 
    return method;
  }
  
  public void setWriteMethod(Method paramMethod) throws IntrospectionException {
    this.writeMethodRef.set(paramMethod);
    if (paramMethod == null) {
      this.writeMethodName = null;
      return;
    } 
    setPropertyType(findPropertyType(getReadMethod(), paramMethod));
    setClass0(paramMethod.getDeclaringClass());
    this.writeMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  void setClass0(Class<?> paramClass) {
    if (getClass0() != null && paramClass.isAssignableFrom(getClass0()))
      return; 
    super.setClass0(paramClass);
  }
  
  public boolean isBound() { return this.bound; }
  
  public void setBound(boolean paramBoolean) { this.bound = paramBoolean; }
  
  public boolean isConstrained() { return this.constrained; }
  
  public void setConstrained(boolean paramBoolean) { this.constrained = paramBoolean; }
  
  public void setPropertyEditorClass(Class<?> paramClass) { this.propertyEditorClassRef = getWeakReference(paramClass); }
  
  public Class<?> getPropertyEditorClass() { return (this.propertyEditorClassRef != null) ? (Class)this.propertyEditorClassRef.get() : null; }
  
  public PropertyEditor createPropertyEditor(Object paramObject) {
    Object object = null;
    Class clazz = getPropertyEditorClass();
    if (clazz != null && PropertyEditor.class.isAssignableFrom(clazz) && ReflectUtil.isPackageAccessible(clazz)) {
      Constructor constructor = null;
      if (paramObject != null)
        try {
          constructor = clazz.getConstructor(new Class[] { Object.class });
        } catch (Exception exception) {} 
      try {
        if (constructor == null) {
          object = clazz.newInstance();
        } else {
          object = constructor.newInstance(new Object[] { paramObject });
        } 
      } catch (Exception exception) {}
    } 
    return (PropertyEditor)object;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject != null && paramObject instanceof PropertyDescriptor) {
      PropertyDescriptor propertyDescriptor = (PropertyDescriptor)paramObject;
      Method method1 = propertyDescriptor.getReadMethod();
      Method method2 = propertyDescriptor.getWriteMethod();
      if (!compareMethods(getReadMethod(), method1))
        return false; 
      if (!compareMethods(getWriteMethod(), method2))
        return false; 
      if (getPropertyType() == propertyDescriptor.getPropertyType() && getPropertyEditorClass() == propertyDescriptor.getPropertyEditorClass() && this.bound == propertyDescriptor.isBound() && this.constrained == propertyDescriptor.isConstrained() && this.writeMethodName == propertyDescriptor.writeMethodName && this.readMethodName == propertyDescriptor.readMethodName)
        return true; 
    } 
    return false;
  }
  
  boolean compareMethods(Method paramMethod1, Method paramMethod2) { return (((paramMethod1 == null) ? 1 : 0) != ((paramMethod2 == null) ? 1 : 0)) ? false : (!(paramMethod1 != null && paramMethod2 != null && !paramMethod1.equals(paramMethod2))); }
  
  PropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2) {
    super(paramPropertyDescriptor1, paramPropertyDescriptor2);
    if (paramPropertyDescriptor2.baseName != null) {
      this.baseName = paramPropertyDescriptor2.baseName;
    } else {
      this.baseName = paramPropertyDescriptor1.baseName;
    } 
    if (paramPropertyDescriptor2.readMethodName != null) {
      this.readMethodName = paramPropertyDescriptor2.readMethodName;
    } else {
      this.readMethodName = paramPropertyDescriptor1.readMethodName;
    } 
    if (paramPropertyDescriptor2.writeMethodName != null) {
      this.writeMethodName = paramPropertyDescriptor2.writeMethodName;
    } else {
      this.writeMethodName = paramPropertyDescriptor1.writeMethodName;
    } 
    if (paramPropertyDescriptor2.propertyTypeRef != null) {
      this.propertyTypeRef = paramPropertyDescriptor2.propertyTypeRef;
    } else {
      this.propertyTypeRef = paramPropertyDescriptor1.propertyTypeRef;
    } 
    Method method1 = paramPropertyDescriptor1.getReadMethod();
    Method method2 = paramPropertyDescriptor2.getReadMethod();
    try {
      if (isAssignable(method1, method2)) {
        setReadMethod(method2);
      } else {
        setReadMethod(method1);
      } 
    } catch (IntrospectionException introspectionException) {}
    if (method1 != null && method2 != null && method1.getDeclaringClass() == method2.getDeclaringClass() && getReturnType(getClass0(), method1) == boolean.class && getReturnType(getClass0(), method2) == boolean.class && method1.getName().indexOf("is") == 0 && method2.getName().indexOf("get") == 0)
      try {
        setReadMethod(method1);
      } catch (IntrospectionException introspectionException) {} 
    Method method3 = paramPropertyDescriptor1.getWriteMethod();
    Method method4 = paramPropertyDescriptor2.getWriteMethod();
    try {
      if (method4 != null) {
        setWriteMethod(method4);
      } else {
        setWriteMethod(method3);
      } 
    } catch (IntrospectionException introspectionException) {}
    if (paramPropertyDescriptor2.getPropertyEditorClass() != null) {
      setPropertyEditorClass(paramPropertyDescriptor2.getPropertyEditorClass());
    } else {
      setPropertyEditorClass(paramPropertyDescriptor1.getPropertyEditorClass());
    } 
    paramPropertyDescriptor1.bound |= paramPropertyDescriptor2.bound;
    paramPropertyDescriptor1.constrained |= paramPropertyDescriptor2.constrained;
  }
  
  PropertyDescriptor(PropertyDescriptor paramPropertyDescriptor) {
    super(paramPropertyDescriptor);
    this.propertyTypeRef = paramPropertyDescriptor.propertyTypeRef;
    this.readMethodRef.set(paramPropertyDescriptor.readMethodRef.get());
    this.writeMethodRef.set(paramPropertyDescriptor.writeMethodRef.get());
    this.propertyEditorClassRef = paramPropertyDescriptor.propertyEditorClassRef;
    this.writeMethodName = paramPropertyDescriptor.writeMethodName;
    this.readMethodName = paramPropertyDescriptor.readMethodName;
    this.baseName = paramPropertyDescriptor.baseName;
    this.bound = paramPropertyDescriptor.bound;
    this.constrained = paramPropertyDescriptor.constrained;
  }
  
  void updateGenericsFor(Class<?> paramClass) {
    setClass0(paramClass);
    try {
      setPropertyType(findPropertyType(this.readMethodRef.get(), this.writeMethodRef.get()));
    } catch (IntrospectionException introspectionException) {
      setPropertyType(null);
    } 
  }
  
  private Class<?> findPropertyType(Method paramMethod1, Method paramMethod2) throws IntrospectionException {
    Class clazz = null;
    try {
      if (paramMethod1 != null) {
        Class[] arrayOfClass = getParameterTypes(getClass0(), paramMethod1);
        if (arrayOfClass.length != 0)
          throw new IntrospectionException("bad read method arg count: " + paramMethod1); 
        clazz = getReturnType(getClass0(), paramMethod1);
        if (clazz == void.class)
          throw new IntrospectionException("read method " + paramMethod1.getName() + " returns void"); 
      } 
      if (paramMethod2 != null) {
        Class[] arrayOfClass = getParameterTypes(getClass0(), paramMethod2);
        if (arrayOfClass.length != 1)
          throw new IntrospectionException("bad write method arg count: " + paramMethod2); 
        if (clazz != null && !arrayOfClass[0].isAssignableFrom(clazz))
          throw new IntrospectionException("type mismatch between read and write methods"); 
        clazz = arrayOfClass[0];
      } 
    } catch (IntrospectionException introspectionException) {
      throw introspectionException;
    } 
    return clazz;
  }
  
  public int hashCode() {
    null = 7;
    null = 37 * null + ((getPropertyType() == null) ? 0 : getPropertyType().hashCode());
    null = 37 * null + ((getReadMethod() == null) ? 0 : getReadMethod().hashCode());
    null = 37 * null + ((getWriteMethod() == null) ? 0 : getWriteMethod().hashCode());
    null = 37 * null + ((getPropertyEditorClass() == null) ? 0 : getPropertyEditorClass().hashCode());
    null = 37 * null + ((this.writeMethodName == null) ? 0 : this.writeMethodName.hashCode());
    null = 37 * null + ((this.readMethodName == null) ? 0 : this.readMethodName.hashCode());
    null = 37 * null + getName().hashCode();
    null = 37 * null + (!this.bound ? 0 : 1);
    return 37 * null + (!this.constrained ? 0 : 1);
  }
  
  String getBaseName() {
    if (this.baseName == null)
      this.baseName = NameGenerator.capitalize(getName()); 
    return this.baseName;
  }
  
  void appendTo(StringBuilder paramStringBuilder) {
    appendTo(paramStringBuilder, "bound", this.bound);
    appendTo(paramStringBuilder, "constrained", this.constrained);
    appendTo(paramStringBuilder, "propertyEditorClass", this.propertyEditorClassRef);
    appendTo(paramStringBuilder, "propertyType", this.propertyTypeRef);
    appendTo(paramStringBuilder, "readMethod", this.readMethodRef.get());
    appendTo(paramStringBuilder, "writeMethod", this.writeMethodRef.get());
  }
  
  private boolean isAssignable(Method paramMethod1, Method paramMethod2) {
    if (paramMethod1 == null)
      return true; 
    if (paramMethod2 == null)
      return false; 
    if (!paramMethod1.getName().equals(paramMethod2.getName()))
      return true; 
    Class clazz1 = paramMethod1.getDeclaringClass();
    Class clazz2 = paramMethod2.getDeclaringClass();
    if (!clazz1.isAssignableFrom(clazz2))
      return false; 
    clazz1 = getReturnType(getClass0(), paramMethod1);
    clazz2 = getReturnType(getClass0(), paramMethod2);
    if (!clazz1.isAssignableFrom(clazz2))
      return false; 
    Class[] arrayOfClass1 = getParameterTypes(getClass0(), paramMethod1);
    Class[] arrayOfClass2 = getParameterTypes(getClass0(), paramMethod2);
    if (arrayOfClass1.length != arrayOfClass2.length)
      return true; 
    for (byte b = 0; b < arrayOfClass1.length; b++) {
      if (!arrayOfClass1[b].isAssignableFrom(arrayOfClass2[b]))
        return false; 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\PropertyDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */