package java.beans;

import java.lang.ref.Reference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class EventSetDescriptor extends FeatureDescriptor {
  private MethodDescriptor[] listenerMethodDescriptors;
  
  private MethodDescriptor addMethodDescriptor;
  
  private MethodDescriptor removeMethodDescriptor;
  
  private MethodDescriptor getMethodDescriptor;
  
  private Reference<Method[]> listenerMethodsRef;
  
  private Reference<? extends Class<?>> listenerTypeRef;
  
  private boolean unicast;
  
  private boolean inDefaultEventSet = true;
  
  public EventSetDescriptor(Class<?> paramClass1, String paramString1, Class<?> paramClass2, String paramString2) throws IntrospectionException {
    this(paramClass1, paramString1, paramClass2, new String[] { paramString2 }, "add" + getListenerClassName(paramClass2), "remove" + getListenerClassName(paramClass2), "get" + getListenerClassName(paramClass2) + "s");
    String str = NameGenerator.capitalize(paramString1) + "Event";
    Method[] arrayOfMethod = getListenerMethods();
    if (arrayOfMethod.length > 0) {
      Class[] arrayOfClass = getParameterTypes(getClass0(), arrayOfMethod[0]);
      if (!"vetoableChange".equals(paramString1) && !arrayOfClass[0].getName().endsWith(str))
        throw new IntrospectionException("Method \"" + paramString2 + "\" should have argument \"" + str + "\""); 
    } 
  }
  
  private static String getListenerClassName(Class<?> paramClass) {
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
  
  public EventSetDescriptor(Class<?> paramClass1, String paramString1, Class<?> paramClass2, String[] paramArrayOfString, String paramString2, String paramString3) throws IntrospectionException { this(paramClass1, paramString1, paramClass2, paramArrayOfString, paramString2, paramString3, null); }
  
  public EventSetDescriptor(Class<?> paramClass1, String paramString1, Class<?> paramClass2, String[] paramArrayOfString, String paramString2, String paramString3, String paramString4) throws IntrospectionException {
    if (paramClass1 == null || paramString1 == null || paramClass2 == null)
      throw new NullPointerException(); 
    setName(paramString1);
    setClass0(paramClass1);
    setListenerType(paramClass2);
    Method[] arrayOfMethod = new Method[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] == null)
        throw new NullPointerException(); 
      arrayOfMethod[b] = getMethod(paramClass2, paramArrayOfString[b], 1);
    } 
    setListenerMethods(arrayOfMethod);
    setAddListenerMethod(getMethod(paramClass1, paramString2, 1));
    setRemoveListenerMethod(getMethod(paramClass1, paramString3, 1));
    Method method = Introspector.findMethod(paramClass1, paramString4, 0);
    if (method != null)
      setGetListenerMethod(method); 
  }
  
  private static Method getMethod(Class<?> paramClass, String paramString, int paramInt) throws IntrospectionException {
    if (paramString == null)
      return null; 
    Method method = Introspector.findMethod(paramClass, paramString, paramInt);
    if (method == null || Modifier.isStatic(method.getModifiers()))
      throw new IntrospectionException("Method not found: " + paramString + " on class " + paramClass.getName()); 
    return method;
  }
  
  public EventSetDescriptor(String paramString, Class<?> paramClass, Method[] paramArrayOfMethod, Method paramMethod1, Method paramMethod2) throws IntrospectionException { this(paramString, paramClass, paramArrayOfMethod, paramMethod1, paramMethod2, null); }
  
  public EventSetDescriptor(String paramString, Class<?> paramClass, Method[] paramArrayOfMethod, Method paramMethod1, Method paramMethod2, Method paramMethod3) throws IntrospectionException {
    setName(paramString);
    setListenerMethods(paramArrayOfMethod);
    setAddListenerMethod(paramMethod1);
    setRemoveListenerMethod(paramMethod2);
    setGetListenerMethod(paramMethod3);
    setListenerType(paramClass);
  }
  
  public EventSetDescriptor(String paramString, Class<?> paramClass, MethodDescriptor[] paramArrayOfMethodDescriptor, Method paramMethod1, Method paramMethod2) throws IntrospectionException {
    setName(paramString);
    this.listenerMethodDescriptors = (paramArrayOfMethodDescriptor != null) ? (MethodDescriptor[])paramArrayOfMethodDescriptor.clone() : null;
    setAddListenerMethod(paramMethod1);
    setRemoveListenerMethod(paramMethod2);
    setListenerType(paramClass);
  }
  
  public Class<?> getListenerType() { return (this.listenerTypeRef != null) ? (Class)this.listenerTypeRef.get() : null; }
  
  private void setListenerType(Class<?> paramClass) { this.listenerTypeRef = getWeakReference(paramClass); }
  
  public Method[] getListenerMethods() {
    Method[] arrayOfMethod = getListenerMethods0();
    if (arrayOfMethod == null) {
      if (this.listenerMethodDescriptors != null) {
        arrayOfMethod = new Method[this.listenerMethodDescriptors.length];
        for (byte b = 0; b < arrayOfMethod.length; b++)
          arrayOfMethod[b] = this.listenerMethodDescriptors[b].getMethod(); 
      } 
      setListenerMethods(arrayOfMethod);
    } 
    return arrayOfMethod;
  }
  
  private void setListenerMethods(Method[] paramArrayOfMethod) {
    if (paramArrayOfMethod == null)
      return; 
    if (this.listenerMethodDescriptors == null) {
      this.listenerMethodDescriptors = new MethodDescriptor[paramArrayOfMethod.length];
      for (byte b = 0; b < paramArrayOfMethod.length; b++)
        this.listenerMethodDescriptors[b] = new MethodDescriptor(paramArrayOfMethod[b]); 
    } 
    this.listenerMethodsRef = getSoftReference(paramArrayOfMethod);
  }
  
  private Method[] getListenerMethods0() { return (this.listenerMethodsRef != null) ? (Method[])this.listenerMethodsRef.get() : null; }
  
  public MethodDescriptor[] getListenerMethodDescriptors() { return (this.listenerMethodDescriptors != null) ? (MethodDescriptor[])this.listenerMethodDescriptors.clone() : null; }
  
  public Method getAddListenerMethod() { return getMethod(this.addMethodDescriptor); }
  
  private void setAddListenerMethod(Method paramMethod) {
    if (paramMethod == null)
      return; 
    if (getClass0() == null)
      setClass0(paramMethod.getDeclaringClass()); 
    this.addMethodDescriptor = new MethodDescriptor(paramMethod);
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public Method getRemoveListenerMethod() { return getMethod(this.removeMethodDescriptor); }
  
  private void setRemoveListenerMethod(Method paramMethod) {
    if (paramMethod == null)
      return; 
    if (getClass0() == null)
      setClass0(paramMethod.getDeclaringClass()); 
    this.removeMethodDescriptor = new MethodDescriptor(paramMethod);
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public Method getGetListenerMethod() { return getMethod(this.getMethodDescriptor); }
  
  private void setGetListenerMethod(Method paramMethod) {
    if (paramMethod == null)
      return; 
    if (getClass0() == null)
      setClass0(paramMethod.getDeclaringClass()); 
    this.getMethodDescriptor = new MethodDescriptor(paramMethod);
    setTransient((Transient)paramMethod.getAnnotation(Transient.class));
  }
  
  public void setUnicast(boolean paramBoolean) { this.unicast = paramBoolean; }
  
  public boolean isUnicast() { return this.unicast; }
  
  public void setInDefaultEventSet(boolean paramBoolean) { this.inDefaultEventSet = paramBoolean; }
  
  public boolean isInDefaultEventSet() { return this.inDefaultEventSet; }
  
  EventSetDescriptor(EventSetDescriptor paramEventSetDescriptor1, EventSetDescriptor paramEventSetDescriptor2) {
    super(paramEventSetDescriptor1, paramEventSetDescriptor2);
    this.listenerMethodDescriptors = paramEventSetDescriptor1.listenerMethodDescriptors;
    if (paramEventSetDescriptor2.listenerMethodDescriptors != null)
      this.listenerMethodDescriptors = paramEventSetDescriptor2.listenerMethodDescriptors; 
    this.listenerTypeRef = paramEventSetDescriptor1.listenerTypeRef;
    if (paramEventSetDescriptor2.listenerTypeRef != null)
      this.listenerTypeRef = paramEventSetDescriptor2.listenerTypeRef; 
    this.addMethodDescriptor = paramEventSetDescriptor1.addMethodDescriptor;
    if (paramEventSetDescriptor2.addMethodDescriptor != null)
      this.addMethodDescriptor = paramEventSetDescriptor2.addMethodDescriptor; 
    this.removeMethodDescriptor = paramEventSetDescriptor1.removeMethodDescriptor;
    if (paramEventSetDescriptor2.removeMethodDescriptor != null)
      this.removeMethodDescriptor = paramEventSetDescriptor2.removeMethodDescriptor; 
    this.getMethodDescriptor = paramEventSetDescriptor1.getMethodDescriptor;
    if (paramEventSetDescriptor2.getMethodDescriptor != null)
      this.getMethodDescriptor = paramEventSetDescriptor2.getMethodDescriptor; 
    this.unicast = paramEventSetDescriptor2.unicast;
    if (!paramEventSetDescriptor1.inDefaultEventSet || !paramEventSetDescriptor2.inDefaultEventSet)
      this.inDefaultEventSet = false; 
  }
  
  EventSetDescriptor(EventSetDescriptor paramEventSetDescriptor) {
    super(paramEventSetDescriptor);
    if (paramEventSetDescriptor.listenerMethodDescriptors != null) {
      int i = paramEventSetDescriptor.listenerMethodDescriptors.length;
      this.listenerMethodDescriptors = new MethodDescriptor[i];
      for (byte b = 0; b < i; b++)
        this.listenerMethodDescriptors[b] = new MethodDescriptor(paramEventSetDescriptor.listenerMethodDescriptors[b]); 
    } 
    this.listenerTypeRef = paramEventSetDescriptor.listenerTypeRef;
    this.addMethodDescriptor = paramEventSetDescriptor.addMethodDescriptor;
    this.removeMethodDescriptor = paramEventSetDescriptor.removeMethodDescriptor;
    this.getMethodDescriptor = paramEventSetDescriptor.getMethodDescriptor;
    this.unicast = paramEventSetDescriptor.unicast;
    this.inDefaultEventSet = paramEventSetDescriptor.inDefaultEventSet;
  }
  
  void appendTo(StringBuilder paramStringBuilder) {
    appendTo(paramStringBuilder, "unicast", this.unicast);
    appendTo(paramStringBuilder, "inDefaultEventSet", this.inDefaultEventSet);
    appendTo(paramStringBuilder, "listenerType", this.listenerTypeRef);
    appendTo(paramStringBuilder, "getListenerMethod", getMethod(this.getMethodDescriptor));
    appendTo(paramStringBuilder, "addListenerMethod", getMethod(this.addMethodDescriptor));
    appendTo(paramStringBuilder, "removeListenerMethod", getMethod(this.removeMethodDescriptor));
  }
  
  private static Method getMethod(MethodDescriptor paramMethodDescriptor) { return (paramMethodDescriptor != null) ? paramMethodDescriptor.getMethod() : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\EventSetDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */