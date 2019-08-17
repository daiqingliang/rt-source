package java.lang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;

public interface AnnotatedElement {
  default boolean isAnnotationPresent(Class<? extends Annotation> paramClass) { return (getAnnotation(paramClass) != null); }
  
  <T extends Annotation> T getAnnotation(Class<T> paramClass);
  
  Annotation[] getAnnotations();
  
  default <T extends Annotation> T[] getAnnotationsByType(Class<T> paramClass) {
    Annotation[] arrayOfAnnotation = getDeclaredAnnotationsByType(paramClass);
    if (arrayOfAnnotation.length == 0 && this instanceof Class && AnnotationType.getInstance(paramClass).isInherited()) {
      Class clazz = ((Class)this).getSuperclass();
      if (clazz != null)
        arrayOfAnnotation = clazz.getAnnotationsByType(paramClass); 
    } 
    return (T[])arrayOfAnnotation;
  }
  
  default <T extends Annotation> T getDeclaredAnnotation(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    for (Annotation annotation : getDeclaredAnnotations()) {
      if (paramClass.equals(annotation.annotationType()))
        return (T)(Annotation)paramClass.cast(annotation); 
    } 
    return null;
  }
  
  default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> paramClass) {
    Objects.requireNonNull(paramClass);
    return (T[])AnnotationSupport.getDirectlyAndIndirectlyPresent((Map)Arrays.stream(getDeclaredAnnotations()).collect(Collectors.toMap(Annotation::annotationType, Function.identity(), (paramAnnotation1, paramAnnotation2) -> paramAnnotation1, java.util.LinkedHashMap::new)), paramClass);
  }
  
  Annotation[] getDeclaredAnnotations();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\AnnotatedElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */