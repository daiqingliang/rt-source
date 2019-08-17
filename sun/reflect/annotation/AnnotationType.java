package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class AnnotationType {
  private final Map<String, Class<?>> memberTypes;
  
  private final Map<String, Object> memberDefaults;
  
  private final Map<String, Method> members;
  
  private final RetentionPolicy retention;
  
  private final boolean inherited;
  
  public static AnnotationType getInstance(Class<? extends Annotation> paramClass) {
    JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
    AnnotationType annotationType = javaLangAccess.getAnnotationType(paramClass);
    if (annotationType == null) {
      annotationType = new AnnotationType(paramClass);
      if (!javaLangAccess.casAnnotationType(paramClass, null, annotationType)) {
        annotationType = javaLangAccess.getAnnotationType(paramClass);
        assert annotationType != null;
      } 
    } 
    return annotationType;
  }
  
  private AnnotationType(final Class<? extends Annotation> annotationClass) {
    if (!paramClass.isAnnotation())
      throw new IllegalArgumentException("Not an annotation type"); 
    Method[] arrayOfMethod = (Method[])AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
          public Method[] run() { return annotationClass.getDeclaredMethods(); }
        });
    this.memberTypes = new HashMap(arrayOfMethod.length + 1, 1.0F);
    this.memberDefaults = new HashMap(0);
    this.members = new HashMap(arrayOfMethod.length + 1, 1.0F);
    for (Method method : arrayOfMethod) {
      if (Modifier.isPublic(method.getModifiers()) && Modifier.isAbstract(method.getModifiers()) && !method.isSynthetic()) {
        if (method.getParameterTypes().length != 0)
          throw new IllegalArgumentException(method + " has params"); 
        String str = method.getName();
        Class clazz = method.getReturnType();
        this.memberTypes.put(str, invocationHandlerReturnType(clazz));
        this.members.put(str, method);
        Object object = method.getDefaultValue();
        if (object != null)
          this.memberDefaults.put(str, object); 
      } 
    } 
    if (paramClass != Retention.class && paramClass != java.lang.annotation.Inherited.class) {
      JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
      Map map = AnnotationParser.parseSelectAnnotations(javaLangAccess.getRawClassAnnotations(paramClass), javaLangAccess.getConstantPool(paramClass), paramClass, new Class[] { Retention.class, java.lang.annotation.Inherited.class });
      Retention retention1 = (Retention)map.get(Retention.class);
      this.retention = (retention1 == null) ? RetentionPolicy.CLASS : retention1.value();
      this.inherited = map.containsKey(java.lang.annotation.Inherited.class);
    } else {
      this.retention = RetentionPolicy.RUNTIME;
      this.inherited = false;
    } 
  }
  
  public static Class<?> invocationHandlerReturnType(Class<?> paramClass) { return (paramClass == byte.class) ? Byte.class : ((paramClass == char.class) ? Character.class : ((paramClass == double.class) ? Double.class : ((paramClass == float.class) ? Float.class : ((paramClass == int.class) ? Integer.class : ((paramClass == long.class) ? Long.class : ((paramClass == short.class) ? Short.class : ((paramClass == boolean.class) ? Boolean.class : paramClass))))))); }
  
  public Map<String, Class<?>> memberTypes() { return this.memberTypes; }
  
  public Map<String, Method> members() { return this.members; }
  
  public Map<String, Object> memberDefaults() { return this.memberDefaults; }
  
  public RetentionPolicy retention() { return this.retention; }
  
  public boolean isInherited() { return this.inherited; }
  
  public String toString() { return "Annotation Type:\n   Member types: " + this.memberTypes + "\n   Member defaults: " + this.memberDefaults + "\n   Retention policy: " + this.retention + "\n   Inherited: " + this.inherited; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\AnnotationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */