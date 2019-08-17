package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationSupport;

public final class Parameter implements AnnotatedElement {
  private final String name;
  
  private final int modifiers;
  
  private final Executable executable;
  
  private final int index;
  
  private Map<Class<? extends Annotation>, Annotation> declaredAnnotations;
  
  Parameter(String paramString, int paramInt1, Executable paramExecutable, int paramInt2) {
    this.name = paramString;
    this.modifiers = paramInt1;
    this.executable = paramExecutable;
    this.index = paramInt2;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof Parameter) {
      Parameter parameter = (Parameter)paramObject;
      return (parameter.executable.equals(this.executable) && parameter.index == this.index);
    } 
    return false;
  }
  
  public int hashCode() { return this.executable.hashCode() ^ this.index; }
  
  public boolean isNamePresent() { return (this.executable.hasRealParameterData() && this.name != null); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    Type type = getParameterizedType();
    String str = type.getTypeName();
    stringBuilder.append(Modifier.toString(getModifiers()));
    if (0 != this.modifiers)
      stringBuilder.append(' '); 
    if (isVarArgs()) {
      stringBuilder.append(str.replaceFirst("\\[\\]$", "..."));
    } else {
      stringBuilder.append(str);
    } 
    stringBuilder.append(' ');
    stringBuilder.append(getName());
    return stringBuilder.toString();
  }
  
  public Executable getDeclaringExecutable() { return this.executable; }
  
  public int getModifiers() { return this.modifiers; }
  
  public String getName() { return (this.name == null || this.name.equals("")) ? ("arg" + this.index) : this.name; }
  
  String getRealName() { return this.name; }
  
  public Type getParameterizedType() {
    Type type = this.parameterTypeCache;
    if (null == type) {
      type = this.executable.getAllGenericParameterTypes()[this.index];
      this.parameterTypeCache = type;
    } 
    return type;
  }
  
  public Class<?> getType() {
    Class clazz = this.parameterClassCache;
    if (null == clazz) {
      clazz = this.executable.getParameterTypes()[this.index];
      this.parameterClassCache = clazz;
    } 
    return clazz;
  }
  
  public AnnotatedType getAnnotatedType() { return this.executable.getAnnotatedParameterTypes()[this.index]; }
  
  public boolean isImplicit() { return Modifier.isMandated(getModifiers()); }
  
  public boolean isSynthetic() { return Modifier.isSynthetic(getModifiers()); }
  
  public boolean isVarArgs() { return (this.executable.isVarArgs() && this.index == this.executable.getParameterCount() - 1); }
  
  public <T extends Annotation> T getAnnotation(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T)(Annotation)paramClass.cast(declaredAnnotations().get(paramClass));
  }
  
  public <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T[])AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), paramClass);
  }
  
  public Annotation[] getDeclaredAnnotations() { return this.executable.getParameterAnnotations()[this.index]; }
  
  public <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass) { return (T)getAnnotation(paramClass); }
  
  public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass) { return (T[])getAnnotationsByType(paramClass); }
  
  public Annotation[] getAnnotations() { return getDeclaredAnnotations(); }
  
  private Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
    if (null == this.declaredAnnotations) {
      this.declaredAnnotations = new HashMap();
      Annotation[] arrayOfAnnotation = getDeclaredAnnotations();
      for (byte b = 0; b < arrayOfAnnotation.length; b++)
        this.declaredAnnotations.put(arrayOfAnnotation[b].annotationType(), arrayOfAnnotation[b]); 
    } 
    return this.declaredAnnotations;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */