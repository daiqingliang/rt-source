package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Method;

public class IndexedPropertyDescriptor extends PropertyDescriptor {
  private Reference<? extends Class<?>> indexedPropertyTypeRef;
  
  private final MethodRef indexedReadMethodRef = new MethodRef();
  
  private final MethodRef indexedWriteMethodRef = new MethodRef();
  
  private String indexedReadMethodName;
  
  private String indexedWriteMethodName;
  
  public IndexedPropertyDescriptor(String paramString, Class<?> paramClass) throws IntrospectionException { this(paramString, paramClass, "get" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString), "get" + NameGenerator.capitalize(paramString), "set" + NameGenerator.capitalize(paramString)); }
  
  public IndexedPropertyDescriptor(String paramString1, Class<?> paramClass, String paramString2, String paramString3, String paramString4, String paramString5) throws IntrospectionException {
    super(paramString1, paramClass, paramString2, paramString3);
    this.indexedReadMethodName = paramString4;
    if (paramString4 != null && getIndexedReadMethod() == null)
      throw new IntrospectionException("Method not found: " + paramString4); 
    this.indexedWriteMethodName = paramString5;
    if (paramString5 != null && getIndexedWriteMethod() == null)
      throw new IntrospectionException("Method not found: " + paramString5); 
    findIndexedPropertyType(getIndexedReadMethod(), getIndexedWriteMethod());
  }
  
  public IndexedPropertyDescriptor(String paramString, Method paramMethod1, Method paramMethod2, Method paramMethod3, Method paramMethod4) throws IntrospectionException {
    super(paramString, paramMethod1, paramMethod2);
    setIndexedReadMethod0(paramMethod3);
    setIndexedWriteMethod0(paramMethod4);
    setIndexedPropertyType(findIndexedPropertyType(paramMethod3, paramMethod4));
  }
  
  IndexedPropertyDescriptor(Class<?> paramClass, String paramString, Method paramMethod1, Method paramMethod2, Method paramMethod3, Method paramMethod4) throws IntrospectionException {
    super(paramClass, paramString, paramMethod1, paramMethod2);
    setIndexedReadMethod0(paramMethod3);
    setIndexedWriteMethod0(paramMethod4);
    setIndexedPropertyType(findIndexedPropertyType(paramMethod3, paramMethod4));
  }
  
  public Method getIndexedReadMethod() {
    Method method = this.indexedReadMethodRef.get();
    if (method == null) {
      Class clazz = getClass0();
      if (clazz == null || (this.indexedReadMethodName == null && !this.indexedReadMethodRef.isSet()))
        return null; 
      String str = "get" + getBaseName();
      if (this.indexedReadMethodName == null) {
        Class clazz1 = getIndexedPropertyType0();
        if (clazz1 == boolean.class || clazz1 == null) {
          this.indexedReadMethodName = "is" + getBaseName();
        } else {
          this.indexedReadMethodName = str;
        } 
      } 
      Class[] arrayOfClass = { int.class };
      method = Introspector.findMethod(clazz, this.indexedReadMethodName, 1, arrayOfClass);
      if (method == null && !this.indexedReadMethodName.equals(str)) {
        this.indexedReadMethodName = str;
        method = Introspector.findMethod(clazz, this.indexedReadMethodName, 1, arrayOfClass);
      } 
      setIndexedReadMethod0(method);
    } 
    return method;
  }
  
  public void setIndexedReadMethod(Method paramMethod) throws IntrospectionException {
    setIndexedPropertyType(findIndexedPropertyType(paramMethod, this.indexedWriteMethodRef.get()));
    setIndexedReadMethod0(paramMethod);
  }
  
  private void setIndexedReadMethod0(Method paramMethod) throws IntrospectionException {
    this.indexedReadMethodRef.set(paramMethod);
    if (paramMethod == null) {
      this.indexedReadMethodName = null;
      return;
    } 
    setClass0(paramMethod.getDeclaringClass());
    this.indexedReadMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public Method getIndexedWriteMethod() {
    Method method = this.indexedWriteMethodRef.get();
    if (method == null) {
      Class clazz1 = getClass0();
      if (clazz1 == null || (this.indexedWriteMethodName == null && !this.indexedWriteMethodRef.isSet()))
        return null; 
      Class clazz2 = getIndexedPropertyType0();
      if (clazz2 == null)
        try {
          clazz2 = findIndexedPropertyType(getIndexedReadMethod(), null);
          setIndexedPropertyType(clazz2);
        } catch (IntrospectionException introspectionException) {
          Class clazz = getPropertyType();
          if (clazz.isArray())
            clazz2 = clazz.getComponentType(); 
        }  
      if (this.indexedWriteMethodName == null)
        this.indexedWriteMethodName = "set" + getBaseName(); 
      new Class[2][0] = int.class;
      new Class[2][1] = clazz2;
      Class[] arrayOfClass = (clazz2 == null) ? null : new Class[2];
      method = Introspector.findMethod(clazz1, this.indexedWriteMethodName, 2, arrayOfClass);
      if (method != null && !method.getReturnType().equals(void.class))
        method = null; 
      setIndexedWriteMethod0(method);
    } 
    return method;
  }
  
  public void setIndexedWriteMethod(Method paramMethod) throws IntrospectionException {
    Class clazz = findIndexedPropertyType(getIndexedReadMethod(), paramMethod);
    setIndexedPropertyType(clazz);
    setIndexedWriteMethod0(paramMethod);
  }
  
  private void setIndexedWriteMethod0(Method paramMethod) throws IntrospectionException {
    this.indexedWriteMethodRef.set(paramMethod);
    if (paramMethod == null) {
      this.indexedWriteMethodName = null;
      return;
    } 
    setClass0(paramMethod.getDeclaringClass());
    this.indexedWriteMethodName = paramMethod.getName();
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public Class<?> getIndexedPropertyType() {
    Class clazz = getIndexedPropertyType0();
    if (clazz == null)
      try {
        clazz = findIndexedPropertyType(getIndexedReadMethod(), getIndexedWriteMethod());
        setIndexedPropertyType(clazz);
      } catch (IntrospectionException introspectionException) {} 
    return clazz;
  }
  
  private void setIndexedPropertyType(Class<?> paramClass) { this.indexedPropertyTypeRef = getWeakReference(paramClass); }
  
  private Class<?> getIndexedPropertyType0() { return (this.indexedPropertyTypeRef != null) ? (Class)this.indexedPropertyTypeRef.get() : null; }
  
  private Class<?> findIndexedPropertyType(Method paramMethod1, Method paramMethod2) throws IntrospectionException {
    Class clazz1 = null;
    if (paramMethod1 != null) {
      Class[] arrayOfClass = getParameterTypes(getClass0(), paramMethod1);
      if (arrayOfClass.length != 1)
        throw new IntrospectionException("bad indexed read method arg count"); 
      if (arrayOfClass[false] != int.class)
        throw new IntrospectionException("non int index to indexed read method"); 
      clazz1 = getReturnType(getClass0(), paramMethod1);
      if (clazz1 == void.class)
        throw new IntrospectionException("indexed read method returns void"); 
    } 
    if (paramMethod2 != null) {
      Class[] arrayOfClass = getParameterTypes(getClass0(), paramMethod2);
      if (arrayOfClass.length != 2)
        throw new IntrospectionException("bad indexed write method arg count"); 
      if (arrayOfClass[false] != int.class)
        throw new IntrospectionException("non int index to indexed write method"); 
      if (clazz1 == null || arrayOfClass[1].isAssignableFrom(clazz1)) {
        clazz1 = arrayOfClass[1];
      } else if (!clazz1.isAssignableFrom(arrayOfClass[1])) {
        throw new IntrospectionException("type mismatch between indexed read and indexed write methods: " + getName());
      } 
    } 
    Class clazz2 = getPropertyType();
    if (clazz2 != null && (!clazz2.isArray() || clazz2.getComponentType() != clazz1))
      throw new IntrospectionException("type mismatch between indexed and non-indexed methods: " + getName()); 
    return clazz1;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject != null && paramObject instanceof IndexedPropertyDescriptor) {
      IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)paramObject;
      Method method1 = indexedPropertyDescriptor.getIndexedReadMethod();
      Method method2 = indexedPropertyDescriptor.getIndexedWriteMethod();
      return !compareMethods(getIndexedReadMethod(), method1) ? false : (!compareMethods(getIndexedWriteMethod(), method2) ? false : ((getIndexedPropertyType() != indexedPropertyDescriptor.getIndexedPropertyType()) ? false : super.equals(paramObject)));
    } 
    return false;
  }
  
  IndexedPropertyDescriptor(PropertyDescriptor paramPropertyDescriptor1, PropertyDescriptor paramPropertyDescriptor2) {
    super(paramPropertyDescriptor1, paramPropertyDescriptor2);
    if (paramPropertyDescriptor1 instanceof IndexedPropertyDescriptor) {
      IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor1;
      try {
        Method method1 = indexedPropertyDescriptor.getIndexedReadMethod();
        if (method1 != null)
          setIndexedReadMethod(method1); 
        Method method2 = indexedPropertyDescriptor.getIndexedWriteMethod();
        if (method2 != null)
          setIndexedWriteMethod(method2); 
      } catch (IntrospectionException introspectionException) {
        throw new AssertionError(introspectionException);
      } 
    } 
    if (paramPropertyDescriptor2 instanceof IndexedPropertyDescriptor) {
      IndexedPropertyDescriptor indexedPropertyDescriptor = (IndexedPropertyDescriptor)paramPropertyDescriptor2;
      try {
        Method method1 = indexedPropertyDescriptor.getIndexedReadMethod();
        if (method1 != null && method1.getDeclaringClass() == getClass0())
          setIndexedReadMethod(method1); 
        Method method2 = indexedPropertyDescriptor.getIndexedWriteMethod();
        if (method2 != null && method2.getDeclaringClass() == getClass0())
          setIndexedWriteMethod(method2); 
      } catch (IntrospectionException introspectionException) {
        throw new AssertionError(introspectionException);
      } 
    } 
  }
  
  IndexedPropertyDescriptor(IndexedPropertyDescriptor paramIndexedPropertyDescriptor) {
    super(paramIndexedPropertyDescriptor);
    this.indexedReadMethodRef.set(paramIndexedPropertyDescriptor.indexedReadMethodRef.get());
    this.indexedWriteMethodRef.set(paramIndexedPropertyDescriptor.indexedWriteMethodRef.get());
    this.indexedPropertyTypeRef = paramIndexedPropertyDescriptor.indexedPropertyTypeRef;
    this.indexedWriteMethodName = paramIndexedPropertyDescriptor.indexedWriteMethodName;
    this.indexedReadMethodName = paramIndexedPropertyDescriptor.indexedReadMethodName;
  }
  
  void updateGenericsFor(Class<?> paramClass) {
    super.updateGenericsFor(paramClass);
    try {
      setIndexedPropertyType(findIndexedPropertyType(this.indexedReadMethodRef.get(), this.indexedWriteMethodRef.get()));
    } catch (IntrospectionException introspectionException) {
      setIndexedPropertyType(null);
    } 
  }
  
  public int hashCode() {
    null = super.hashCode();
    null = 37 * null + ((this.indexedWriteMethodName == null) ? 0 : this.indexedWriteMethodName.hashCode());
    null = 37 * null + ((this.indexedReadMethodName == null) ? 0 : this.indexedReadMethodName.hashCode());
    return 37 * null + ((getIndexedPropertyType() == null) ? 0 : getIndexedPropertyType().hashCode());
  }
  
  void appendTo(StringBuilder paramStringBuilder) {
    super.appendTo(paramStringBuilder);
    appendTo(paramStringBuilder, "indexedPropertyType", this.indexedPropertyTypeRef);
    appendTo(paramStringBuilder, "indexedReadMethod", this.indexedReadMethodRef.get());
    appendTo(paramStringBuilder, "indexedWriteMethod", this.indexedWriteMethodRef.get());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\IndexedPropertyDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */