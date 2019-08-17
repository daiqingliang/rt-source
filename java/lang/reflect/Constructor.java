package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import sun.misc.SharedSecrets;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstructorAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.scope.ConstructorScope;

public final class Constructor<T> extends Executable {
  private Class<T> clazz;
  
  private int slot;
  
  private Class<?>[] parameterTypes;
  
  private Class<?>[] exceptionTypes;
  
  private int modifiers;
  
  private String signature;
  
  private ConstructorRepository genericInfo;
  
  private byte[] annotations;
  
  private byte[] parameterAnnotations;
  
  private Constructor<T> root;
  
  private GenericsFactory getFactory() { return CoreReflectionFactory.make(this, ConstructorScope.make(this)); }
  
  ConstructorRepository getGenericInfo() {
    if (this.genericInfo == null)
      this.genericInfo = ConstructorRepository.make(getSignature(), getFactory()); 
    return this.genericInfo;
  }
  
  Executable getRoot() { return this.root; }
  
  Constructor(Class<T> paramClass, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2, int paramInt1, int paramInt2, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
    this.clazz = paramClass;
    this.parameterTypes = paramArrayOfClass1;
    this.exceptionTypes = paramArrayOfClass2;
    this.modifiers = paramInt1;
    this.slot = paramInt2;
    this.signature = paramString;
    this.annotations = paramArrayOfByte1;
    this.parameterAnnotations = paramArrayOfByte2;
  }
  
  Constructor<T> copy() {
    if (this.root != null)
      throw new IllegalArgumentException("Can not copy a non-root Constructor"); 
    Constructor constructor = new Constructor(this.clazz, this.parameterTypes, this.exceptionTypes, this.modifiers, this.slot, this.signature, this.annotations, this.parameterAnnotations);
    constructor.root = this;
    constructor.constructorAccessor = this.constructorAccessor;
    return constructor;
  }
  
  boolean hasGenericInformation() { return (getSignature() != null); }
  
  byte[] getAnnotationBytes() { return this.annotations; }
  
  public Class<T> getDeclaringClass() { return this.clazz; }
  
  public String getName() { return getDeclaringClass().getName(); }
  
  public int getModifiers() { return this.modifiers; }
  
  public TypeVariable<Constructor<T>>[] getTypeParameters() { return (getSignature() != null) ? (TypeVariable[])getGenericInfo().getTypeParameters() : (TypeVariable[])new TypeVariable[0]; }
  
  public Class<?>[] getParameterTypes() { return (Class[])this.parameterTypes.clone(); }
  
  public int getParameterCount() { return this.parameterTypes.length; }
  
  public Type[] getGenericParameterTypes() { return super.getGenericParameterTypes(); }
  
  public Class<?>[] getExceptionTypes() { return (Class[])this.exceptionTypes.clone(); }
  
  public Type[] getGenericExceptionTypes() { return super.getGenericExceptionTypes(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof Constructor) {
      Constructor constructor = (Constructor)paramObject;
      if (getDeclaringClass() == constructor.getDeclaringClass())
        return equalParamTypes(this.parameterTypes, constructor.parameterTypes); 
    } 
    return false;
  }
  
  public int hashCode() { return getDeclaringClass().getName().hashCode(); }
  
  public String toString() { return sharedToString(Modifier.constructorModifiers(), false, this.parameterTypes, this.exceptionTypes); }
  
  void specificToStringHeader(StringBuilder paramStringBuilder) { paramStringBuilder.append(getDeclaringClass().getTypeName()); }
  
  public String toGenericString() { return sharedToGenericString(Modifier.constructorModifiers(), false); }
  
  void specificToGenericStringHeader(StringBuilder paramStringBuilder) { specificToStringHeader(paramStringBuilder); }
  
  @CallerSensitive
  public T newInstance(Object... paramVarArgs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (!this.override && !Reflection.quickCheckMemberAccess(this.clazz, this.modifiers)) {
      Class clazz1 = Reflection.getCallerClass();
      checkAccess(clazz1, this.clazz, null, this.modifiers);
    } 
    if ((this.clazz.getModifiers() & 0x4000) != 0)
      throw new IllegalArgumentException("Cannot reflectively create enum objects"); 
    ConstructorAccessor constructorAccessor1 = this.constructorAccessor;
    if (constructorAccessor1 == null)
      constructorAccessor1 = acquireConstructorAccessor(); 
    return (T)constructorAccessor1.newInstance(paramVarArgs);
  }
  
  public boolean isVarArgs() { return super.isVarArgs(); }
  
  public boolean isSynthetic() { return super.isSynthetic(); }
  
  private ConstructorAccessor acquireConstructorAccessor() {
    ConstructorAccessor constructorAccessor1 = null;
    if (this.root != null)
      constructorAccessor1 = this.root.getConstructorAccessor(); 
    if (constructorAccessor1 != null) {
      this.constructorAccessor = constructorAccessor1;
    } else {
      constructorAccessor1 = reflectionFactory.newConstructorAccessor(this);
      setConstructorAccessor(constructorAccessor1);
    } 
    return constructorAccessor1;
  }
  
  ConstructorAccessor getConstructorAccessor() { return this.constructorAccessor; }
  
  void setConstructorAccessor(ConstructorAccessor paramConstructorAccessor) {
    this.constructorAccessor = paramConstructorAccessor;
    if (this.root != null)
      this.root.setConstructorAccessor(paramConstructorAccessor); 
  }
  
  int getSlot() { return this.slot; }
  
  String getSignature() { return this.signature; }
  
  byte[] getRawAnnotations() { return this.annotations; }
  
  byte[] getRawParameterAnnotations() { return this.parameterAnnotations; }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) { return (T)super.getAnnotation(paramClass); }
  
  public Annotation[] getDeclaredAnnotations() { return super.getDeclaredAnnotations(); }
  
  public Annotation[][] getParameterAnnotations() { return sharedGetParameterAnnotations(this.parameterTypes, this.parameterAnnotations); }
  
  void handleParameterNumberMismatch(int paramInt1, int paramInt2) {
    Class clazz1 = getDeclaringClass();
    if (clazz1.isEnum() || clazz1.isAnonymousClass() || clazz1.isLocalClass())
      return; 
    if (!clazz1.isMemberClass() || (clazz1.isMemberClass() && (clazz1.getModifiers() & 0x8) == 0 && paramInt1 + 1 != paramInt2))
      throw new AnnotationFormatError("Parameter annotations don't match number of parameters"); 
  }
  
  public AnnotatedType getAnnotatedReturnType() { return getAnnotatedReturnType0(getDeclaringClass()); }
  
  public AnnotatedType getAnnotatedReceiverType() { return (getDeclaringClass().getEnclosingClass() == null) ? super.getAnnotatedReceiverType() : TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getDeclaringClass().getEnclosingClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Constructor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */