package java.beans;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodDescriptor extends FeatureDescriptor {
  private final MethodRef methodRef = new MethodRef();
  
  private String[] paramNames;
  
  private List<WeakReference<Class<?>>> params;
  
  private ParameterDescriptor[] parameterDescriptors;
  
  public MethodDescriptor(Method paramMethod) { this(paramMethod, null); }
  
  public MethodDescriptor(Method paramMethod, ParameterDescriptor[] paramArrayOfParameterDescriptor) {
    setName(paramMethod.getName());
    setMethod(paramMethod);
    this.parameterDescriptors = (paramArrayOfParameterDescriptor != null) ? (ParameterDescriptor[])paramArrayOfParameterDescriptor.clone() : null;
  }
  
  public Method getMethod() {
    Method method = this.methodRef.get();
    if (method == null) {
      Class clazz = getClass0();
      String str = getName();
      if (clazz != null && str != null) {
        Class[] arrayOfClass = getParams();
        if (arrayOfClass == null) {
          for (byte b = 0; b < 3; b++) {
            method = Introspector.findMethod(clazz, str, b, null);
            if (method != null)
              break; 
          } 
        } else {
          method = Introspector.findMethod(clazz, str, arrayOfClass.length, arrayOfClass);
        } 
        setMethod(method);
      } 
    } 
    return method;
  }
  
  private void setMethod(Method paramMethod) {
    if (paramMethod == null)
      return; 
    if (getClass0() == null)
      setClass0(paramMethod.getDeclaringClass()); 
    setParams(getParameterTypes(getClass0(), paramMethod));
    this.methodRef.set(paramMethod);
  }
  
  private void setParams(Class<?>[] paramArrayOfClass) {
    if (paramArrayOfClass == null)
      return; 
    this.paramNames = new String[paramArrayOfClass.length];
    this.params = new ArrayList(paramArrayOfClass.length);
    for (byte b = 0; b < paramArrayOfClass.length; b++) {
      this.paramNames[b] = paramArrayOfClass[b].getName();
      this.params.add(new WeakReference(paramArrayOfClass[b]));
    } 
  }
  
  String[] getParamNames() { return this.paramNames; }
  
  private Class<?>[] getParams() {
    Class[] arrayOfClass = new Class[this.params.size()];
    for (byte b = 0; b < this.params.size(); b++) {
      Reference reference = (Reference)this.params.get(b);
      Class clazz = (Class)reference.get();
      if (clazz == null)
        return null; 
      arrayOfClass[b] = clazz;
    } 
    return arrayOfClass;
  }
  
  public ParameterDescriptor[] getParameterDescriptors() { return (this.parameterDescriptors != null) ? (ParameterDescriptor[])this.parameterDescriptors.clone() : null; }
  
  private static Method resolve(Method paramMethod1, Method paramMethod2) { return (paramMethod1 == null) ? paramMethod2 : ((paramMethod2 == null) ? paramMethod1 : ((!paramMethod1.isSynthetic() && paramMethod2.isSynthetic()) ? paramMethod1 : paramMethod2)); }
  
  MethodDescriptor(MethodDescriptor paramMethodDescriptor1, MethodDescriptor paramMethodDescriptor2) {
    super(paramMethodDescriptor1, paramMethodDescriptor2);
    this.methodRef.set(resolve(paramMethodDescriptor1.methodRef.get(), paramMethodDescriptor2.methodRef.get()));
    this.params = paramMethodDescriptor1.params;
    if (paramMethodDescriptor2.params != null)
      this.params = paramMethodDescriptor2.params; 
    this.paramNames = paramMethodDescriptor1.paramNames;
    if (paramMethodDescriptor2.paramNames != null)
      this.paramNames = paramMethodDescriptor2.paramNames; 
    this.parameterDescriptors = paramMethodDescriptor1.parameterDescriptors;
    if (paramMethodDescriptor2.parameterDescriptors != null)
      this.parameterDescriptors = paramMethodDescriptor2.parameterDescriptors; 
  }
  
  MethodDescriptor(MethodDescriptor paramMethodDescriptor) {
    super(paramMethodDescriptor);
    this.methodRef.set(paramMethodDescriptor.getMethod());
    this.params = paramMethodDescriptor.params;
    this.paramNames = paramMethodDescriptor.paramNames;
    if (paramMethodDescriptor.parameterDescriptors != null) {
      int i = paramMethodDescriptor.parameterDescriptors.length;
      this.parameterDescriptors = new ParameterDescriptor[i];
      for (byte b = 0; b < i; b++)
        this.parameterDescriptors[b] = new ParameterDescriptor(paramMethodDescriptor.parameterDescriptors[b]); 
    } 
  }
  
  void appendTo(StringBuilder paramStringBuilder) {
    appendTo(paramStringBuilder, "method", this.methodRef.get());
    if (this.parameterDescriptors != null) {
      paramStringBuilder.append("; parameterDescriptors={");
      for (ParameterDescriptor parameterDescriptor : this.parameterDescriptors)
        paramStringBuilder.append(parameterDescriptor).append(", "); 
      paramStringBuilder.setLength(paramStringBuilder.length() - 2);
      paramStringBuilder.append("}");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\MethodDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */