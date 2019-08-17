package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.ByteBuffer;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.MethodAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.scope.MethodScope;

public final class Method extends Executable {
  private Class<?> clazz;
  
  private int slot;
  
  private String name;
  
  private Class<?> returnType;
  
  private Class<?>[] parameterTypes;
  
  private Class<?>[] exceptionTypes;
  
  private int modifiers;
  
  private String signature;
  
  private MethodRepository genericInfo;
  
  private byte[] annotations;
  
  private byte[] parameterAnnotations;
  
  private byte[] annotationDefault;
  
  private Method root;
  
  private String getGenericSignature() { return this.signature; }
  
  private GenericsFactory getFactory() { return CoreReflectionFactory.make(this, MethodScope.make(this)); }
  
  MethodRepository getGenericInfo() {
    if (this.genericInfo == null)
      this.genericInfo = MethodRepository.make(getGenericSignature(), getFactory()); 
    return this.genericInfo;
  }
  
  Method(Class<?> paramClass1, String paramString1, Class<?>[] paramArrayOfClass1, Class<?> paramClass2, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
    this.clazz = paramClass1;
    this.name = paramString1;
    this.parameterTypes = paramArrayOfClass1;
    this.returnType = paramClass2;
    this.exceptionTypes = paramArrayOfClass2;
    this.modifiers = paramInt1;
    this.slot = paramInt2;
    this.signature = paramString2;
    this.annotations = paramArrayOfByte1;
    this.parameterAnnotations = paramArrayOfByte2;
    this.annotationDefault = paramArrayOfByte3;
  }
  
  Method copy() {
    if (this.root != null)
      throw new IllegalArgumentException("Can not copy a non-root Method"); 
    Method method = new Method(this.clazz, this.name, this.parameterTypes, this.returnType, this.exceptionTypes, this.modifiers, this.slot, this.signature, this.annotations, this.parameterAnnotations, this.annotationDefault);
    method.root = this;
    method.methodAccessor = this.methodAccessor;
    return method;
  }
  
  Executable getRoot() { return this.root; }
  
  boolean hasGenericInformation() { return (getGenericSignature() != null); }
  
  byte[] getAnnotationBytes() { return this.annotations; }
  
  public Class<?> getDeclaringClass() { return this.clazz; }
  
  public String getName() { return this.name; }
  
  public int getModifiers() { return this.modifiers; }
  
  public TypeVariable<Method>[] getTypeParameters() { return (getGenericSignature() != null) ? (TypeVariable[])getGenericInfo().getTypeParameters() : (TypeVariable[])new TypeVariable[0]; }
  
  public Class<?> getReturnType() { return this.returnType; }
  
  public Type getGenericReturnType() { return (getGenericSignature() != null) ? getGenericInfo().getReturnType() : getReturnType(); }
  
  public Class<?>[] getParameterTypes() { return (Class[])this.parameterTypes.clone(); }
  
  public int getParameterCount() { return this.parameterTypes.length; }
  
  public Type[] getGenericParameterTypes() { return super.getGenericParameterTypes(); }
  
  public Class<?>[] getExceptionTypes() { return (Class[])this.exceptionTypes.clone(); }
  
  public Type[] getGenericExceptionTypes() { return super.getGenericExceptionTypes(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof Method) {
      Method method = (Method)paramObject;
      if (getDeclaringClass() == method.getDeclaringClass() && getName() == method.getName())
        return !this.returnType.equals(method.getReturnType()) ? false : equalParamTypes(this.parameterTypes, method.parameterTypes); 
    } 
    return false;
  }
  
  public int hashCode() { return getDeclaringClass().getName().hashCode() ^ getName().hashCode(); }
  
  public String toString() { return sharedToString(Modifier.methodModifiers(), isDefault(), this.parameterTypes, this.exceptionTypes); }
  
  void specificToStringHeader(StringBuilder paramStringBuilder) {
    paramStringBuilder.append(getReturnType().getTypeName()).append(' ');
    paramStringBuilder.append(getDeclaringClass().getTypeName()).append('.');
    paramStringBuilder.append(getName());
  }
  
  public String toGenericString() { return sharedToGenericString(Modifier.methodModifiers(), isDefault()); }
  
  void specificToGenericStringHeader(StringBuilder paramStringBuilder) {
    Type type = getGenericReturnType();
    paramStringBuilder.append(type.getTypeName()).append(' ');
    paramStringBuilder.append(getDeclaringClass().getTypeName()).append('.');
    paramStringBuilder.append(getName());
  }
  
  @CallerSensitive
  public Object invoke(Object paramObject, Object... paramVarArgs) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, paramObject, this.modifiers);
    } 
    MethodAccessor methodAccessor1 = this.methodAccessor;
    if (methodAccessor1 == null)
      methodAccessor1 = acquireMethodAccessor(); 
    return methodAccessor1.invoke(paramObject, paramVarArgs);
  }
  
  public boolean isBridge() { return ((getModifiers() & 0x40) != 0); }
  
  public boolean isVarArgs() { return super.isVarArgs(); }
  
  public boolean isSynthetic() { return super.isSynthetic(); }
  
  public boolean isDefault() { return ((getModifiers() & 0x409) == 1 && getDeclaringClass().isInterface()); }
  
  private MethodAccessor acquireMethodAccessor() {
    MethodAccessor methodAccessor1 = null;
    if (this.root != null)
      methodAccessor1 = this.root.getMethodAccessor(); 
    if (methodAccessor1 != null) {
      this.methodAccessor = methodAccessor1;
    } else {
      methodAccessor1 = reflectionFactory.newMethodAccessor(this);
      setMethodAccessor(methodAccessor1);
    } 
    return methodAccessor1;
  }
  
  MethodAccessor getMethodAccessor() { return this.methodAccessor; }
  
  void setMethodAccessor(MethodAccessor paramMethodAccessor) {
    this.methodAccessor = paramMethodAccessor;
    if (this.root != null)
      this.root.setMethodAccessor(paramMethodAccessor); 
  }
  
  public Object getDefaultValue() {
    if (this.annotationDefault == null)
      return null; 
    Class clazz1 = AnnotationType.invocationHandlerReturnType(getReturnType());
    Object object = AnnotationParser.parseMemberValue(clazz1, ByteBuffer.wrap(this.annotationDefault), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
    if (object instanceof sun.reflect.annotation.ExceptionProxy)
      throw new AnnotationFormatError("Invalid default: " + this); 
    return object;
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) { return (T)super.getAnnotation(paramClass); }
  
  public Annotation[] getDeclaredAnnotations() { return super.getDeclaredAnnotations(); }
  
  public Annotation[][] getParameterAnnotations() { return sharedGetParameterAnnotations(this.parameterTypes, this.parameterAnnotations); }
  
  public AnnotatedType getAnnotatedReturnType() { return getAnnotatedReturnType0(getGenericReturnType()); }
  
  void handleParameterNumberMismatch(int paramInt1, int paramInt2) { throw new AnnotationFormatError("Parameter annotations don't match number of parameters"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Method.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */