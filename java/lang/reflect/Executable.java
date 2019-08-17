package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;
import java.util.Objects;
import sun.misc.SharedSecrets;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.repository.ConstructorRepository;

public abstract class Executable extends AccessibleObject implements Member, GenericDeclaration {
  private Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
  
  abstract byte[] getAnnotationBytes();
  
  abstract Executable getRoot();
  
  abstract boolean hasGenericInformation();
  
  abstract ConstructorRepository getGenericInfo();
  
  boolean equalParamTypes(Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2) {
    if (paramArrayOfClass1.length == paramArrayOfClass2.length) {
      for (byte b = 0; b < paramArrayOfClass1.length; b++) {
        if (paramArrayOfClass1[b] != paramArrayOfClass2[b])
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  Annotation[][] parseParameterAnnotations(byte[] paramArrayOfByte) { return AnnotationParser.parseParameterAnnotations(paramArrayOfByte, SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass()); }
  
  void separateWithCommas(Class<?>[] paramArrayOfClass, StringBuilder paramStringBuilder) {
    for (byte b = 0; b < paramArrayOfClass.length; b++) {
      paramStringBuilder.append(paramArrayOfClass[b].getTypeName());
      if (b < paramArrayOfClass.length - 1)
        paramStringBuilder.append(","); 
    } 
  }
  
  void printModifiersIfNonzero(StringBuilder paramStringBuilder, int paramInt, boolean paramBoolean) {
    int i = getModifiers() & paramInt;
    if (i != 0 && !paramBoolean) {
      paramStringBuilder.append(Modifier.toString(i)).append(' ');
    } else {
      int j = i & 0x7;
      if (j != 0)
        paramStringBuilder.append(Modifier.toString(j)).append(' '); 
      if (paramBoolean)
        paramStringBuilder.append("default "); 
      i &= 0xFFFFFFF8;
      if (i != 0)
        paramStringBuilder.append(Modifier.toString(i)).append(' '); 
    } 
  }
  
  String sharedToString(int paramInt, boolean paramBoolean, Class<?>[] paramArrayOfClass1, Class<?>[] paramArrayOfClass2) {
    try {
      StringBuilder stringBuilder = new StringBuilder();
      printModifiersIfNonzero(stringBuilder, paramInt, paramBoolean);
      specificToStringHeader(stringBuilder);
      stringBuilder.append('(');
      separateWithCommas(paramArrayOfClass1, stringBuilder);
      stringBuilder.append(')');
      if (paramArrayOfClass2.length > 0) {
        stringBuilder.append(" throws ");
        separateWithCommas(paramArrayOfClass2, stringBuilder);
      } 
      return stringBuilder.toString();
    } catch (Exception exception) {
      return "<" + exception + ">";
    } 
  }
  
  abstract void specificToStringHeader(StringBuilder paramStringBuilder);
  
  String sharedToGenericString(int paramInt, boolean paramBoolean) {
    try {
      StringBuilder stringBuilder = new StringBuilder();
      printModifiersIfNonzero(stringBuilder, paramInt, paramBoolean);
      TypeVariable[] arrayOfTypeVariable = getTypeParameters();
      if (arrayOfTypeVariable.length > 0) {
        boolean bool = true;
        stringBuilder.append('<');
        for (TypeVariable typeVariable : arrayOfTypeVariable) {
          if (!bool)
            stringBuilder.append(','); 
          stringBuilder.append(typeVariable.toString());
          bool = false;
        } 
        stringBuilder.append("> ");
      } 
      specificToGenericStringHeader(stringBuilder);
      stringBuilder.append('(');
      Type[] arrayOfType1 = getGenericParameterTypes();
      for (byte b = 0; b < arrayOfType1.length; b++) {
        String str = arrayOfType1[b].getTypeName();
        if (isVarArgs() && b == arrayOfType1.length - 1)
          str = str.replaceFirst("\\[\\]$", "..."); 
        stringBuilder.append(str);
        if (b < arrayOfType1.length - 1)
          stringBuilder.append(','); 
      } 
      stringBuilder.append(')');
      Type[] arrayOfType2 = getGenericExceptionTypes();
      if (arrayOfType2.length > 0) {
        stringBuilder.append(" throws ");
        for (byte b1 = 0; b1 < arrayOfType2.length; b1++) {
          stringBuilder.append((arrayOfType2[b1] instanceof Class) ? ((Class)arrayOfType2[b1]).getName() : arrayOfType2[b1].toString());
          if (b1 < arrayOfType2.length - 1)
            stringBuilder.append(','); 
        } 
      } 
      return stringBuilder.toString();
    } catch (Exception exception) {
      return "<" + exception + ">";
    } 
  }
  
  abstract void specificToGenericStringHeader(StringBuilder paramStringBuilder);
  
  public abstract Class<?> getDeclaringClass();
  
  public abstract String getName();
  
  public abstract int getModifiers();
  
  public abstract TypeVariable<?>[] getTypeParameters();
  
  public abstract Class<?>[] getParameterTypes();
  
  public int getParameterCount() { throw new AbstractMethodError(); }
  
  public Type[] getGenericParameterTypes() { return hasGenericInformation() ? getGenericInfo().getParameterTypes() : getParameterTypes(); }
  
  Type[] getAllGenericParameterTypes() {
    boolean bool1 = hasGenericInformation();
    if (!bool1)
      return getParameterTypes(); 
    boolean bool2 = hasRealParameterData();
    Type[] arrayOfType1 = getGenericParameterTypes();
    Class[] arrayOfClass = getParameterTypes();
    Type[] arrayOfType2 = new Type[arrayOfClass.length];
    Parameter[] arrayOfParameter = getParameters();
    byte b = 0;
    if (bool2) {
      for (byte b1 = 0; b1 < arrayOfType2.length; b1++) {
        Parameter parameter = arrayOfParameter[b1];
        if (parameter.isSynthetic() || parameter.isImplicit()) {
          arrayOfType2[b1] = arrayOfClass[b1];
        } else {
          arrayOfType2[b1] = arrayOfType1[b];
          b++;
        } 
      } 
    } else {
      return (arrayOfType1.length == arrayOfClass.length) ? arrayOfType1 : arrayOfClass;
    } 
    return arrayOfType2;
  }
  
  public Parameter[] getParameters() { return (Parameter[])privateGetParameters().clone(); }
  
  private Parameter[] synthesizeAllParams() {
    int i = getParameterCount();
    Parameter[] arrayOfParameter = new Parameter[i];
    for (byte b = 0; b < i; b++)
      arrayOfParameter[b] = new Parameter("arg" + b, 0, this, b); 
    return arrayOfParameter;
  }
  
  private void verifyParameters(Parameter[] paramArrayOfParameter) {
    if (getParameterTypes().length != paramArrayOfParameter.length)
      throw new MalformedParametersException("Wrong number of parameters in MethodParameters attribute"); 
    for (Parameter parameter : paramArrayOfParameter) {
      String str = parameter.getRealName();
      int i = parameter.getModifiers();
      if (str != null && (str.isEmpty() || str.indexOf('.') != -1 || str.indexOf(';') != -1 || str.indexOf('[') != -1 || str.indexOf('/') != -1))
        throw new MalformedParametersException("Invalid parameter name \"" + str + "\""); 
      if (i != (i & 0x9010))
        throw new MalformedParametersException("Invalid parameter modifiers"); 
    } 
  }
  
  private Parameter[] privateGetParameters() {
    Parameter[] arrayOfParameter = this.parameters;
    if (arrayOfParameter == null) {
      try {
        arrayOfParameter = getParameters0();
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new MalformedParametersException("Invalid constant pool index");
      } 
      if (arrayOfParameter == null) {
        this.hasRealParameterData = false;
        arrayOfParameter = synthesizeAllParams();
      } else {
        this.hasRealParameterData = true;
        verifyParameters(arrayOfParameter);
      } 
      this.parameters = arrayOfParameter;
    } 
    return arrayOfParameter;
  }
  
  boolean hasRealParameterData() {
    if (this.parameters == null)
      privateGetParameters(); 
    return this.hasRealParameterData;
  }
  
  private native Parameter[] getParameters0();
  
  native byte[] getTypeAnnotationBytes0();
  
  byte[] getTypeAnnotationBytes() { return getTypeAnnotationBytes0(); }
  
  public abstract Class<?>[] getExceptionTypes();
  
  public Type[] getGenericExceptionTypes() {
    Type[] arrayOfType;
    return (hasGenericInformation() && arrayOfType = getGenericInfo().getExceptionTypes().length > 0) ? arrayOfType : getExceptionTypes();
  }
  
  public abstract String toGenericString();
  
  public boolean isVarArgs() { return ((getModifiers() & 0x80) != 0); }
  
  public boolean isSynthetic() { return Modifier.isSynthetic(getModifiers()); }
  
  public abstract Annotation[][] getParameterAnnotations();
  
  Annotation[][] sharedGetParameterAnnotations(Class<?>[] paramArrayOfClass, byte[] paramArrayOfByte) {
    int i = paramArrayOfClass.length;
    if (paramArrayOfByte == null)
      return new Annotation[i][0]; 
    Annotation[][] arrayOfAnnotation = parseParameterAnnotations(paramArrayOfByte);
    if (arrayOfAnnotation.length != i)
      handleParameterNumberMismatch(arrayOfAnnotation.length, i); 
    return arrayOfAnnotation;
  }
  
  abstract void handleParameterNumberMismatch(int paramInt1, int paramInt2);
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T)(Annotation)paramClass.cast(declaredAnnotations().get(paramClass));
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T[])AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations() { return AnnotationParser.toArray(declaredAnnotations()); }
  
  private Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
    if (this.declaredAnnotations == null) {
      Executable executable = getRoot();
      if (executable != null) {
        this.declaredAnnotations = executable.declaredAnnotations();
      } else {
        this.declaredAnnotations = AnnotationParser.parseAnnotations(getAnnotationBytes(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), getDeclaringClass());
      } 
    } 
    return this.declaredAnnotations;
  }
  
  public abstract AnnotatedType getAnnotatedReturnType();
  
  AnnotatedType getAnnotatedReturnType0(Type paramType) { return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), paramType, TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN); }
  
  public AnnotatedType getAnnotatedReceiverType() { return Modifier.isStatic(getModifiers()) ? null : TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getDeclaringClass(), TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER); }
  
  public AnnotatedType[] getAnnotatedParameterTypes() { return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getAllGenericParameterTypes(), TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER); }
  
  public AnnotatedType[] getAnnotatedExceptionTypes() { return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(), SharedSecrets.getJavaLangAccess().getConstantPool(getDeclaringClass()), this, getDeclaringClass(), getGenericExceptionTypes(), TypeAnnotation.TypeAnnotationTarget.THROWS); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Executable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */