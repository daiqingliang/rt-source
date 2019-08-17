package sun.reflect.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public final class AnnotationSupport {
  private static final JavaLangAccess LANG_ACCESS = SharedSecrets.getJavaLangAccess();
  
  public static <A extends Annotation> A[] getDirectlyAndIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass) {
    ArrayList arrayList = new ArrayList();
    Annotation annotation = (Annotation)paramMap.get(paramClass);
    if (annotation != null)
      arrayList.add(annotation); 
    Annotation[] arrayOfAnnotation1 = getIndirectlyPresent(paramMap, paramClass);
    if (arrayOfAnnotation1 != null && arrayOfAnnotation1.length != 0) {
      boolean bool = (annotation == null || containerBeforeContainee(paramMap, paramClass)) ? 1 : 0;
      arrayList.addAll(bool ? 0 : 1, Arrays.asList(arrayOfAnnotation1));
    } 
    Annotation[] arrayOfAnnotation2 = (Annotation[])Array.newInstance(paramClass, arrayList.size());
    return (A[])(Annotation[])arrayList.toArray(arrayOfAnnotation2);
  }
  
  private static <A extends Annotation> A[] getIndirectlyPresent(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass) {
    Repeatable repeatable = (Repeatable)paramClass.getDeclaredAnnotation(Repeatable.class);
    if (repeatable == null)
      return null; 
    Class clazz = repeatable.value();
    Annotation annotation = (Annotation)paramMap.get(clazz);
    if (annotation == null)
      return null; 
    Annotation[] arrayOfAnnotation = getValueArray(annotation);
    checkTypes(arrayOfAnnotation, annotation, paramClass);
    return (A[])arrayOfAnnotation;
  }
  
  private static <A extends Annotation> boolean containerBeforeContainee(Map<Class<? extends Annotation>, Annotation> paramMap, Class<A> paramClass) {
    Class clazz = ((Repeatable)paramClass.getDeclaredAnnotation(Repeatable.class)).value();
    for (Class clazz1 : paramMap.keySet()) {
      if (clazz1 == clazz)
        return true; 
      if (clazz1 == paramClass)
        return false; 
    } 
    return false;
  }
  
  public static <A extends Annotation> A[] getAssociatedAnnotations(Map<Class<? extends Annotation>, Annotation> paramMap, Class<?> paramClass1, Class<A> paramClass2) {
    Objects.requireNonNull(paramClass1);
    Annotation[] arrayOfAnnotation = getDirectlyAndIndirectlyPresent(paramMap, paramClass2);
    if (AnnotationType.getInstance(paramClass2).isInherited())
      for (Class clazz = paramClass1.getSuperclass(); arrayOfAnnotation.length == 0 && clazz != null; clazz = clazz.getSuperclass())
        arrayOfAnnotation = getDirectlyAndIndirectlyPresent(LANG_ACCESS.getDeclaredAnnotationMap(clazz), paramClass2);  
    return (A[])arrayOfAnnotation;
  }
  
  private static <A extends Annotation> A[] getValueArray(Annotation paramAnnotation) {
    try {
      Class clazz = paramAnnotation.annotationType();
      AnnotationType annotationType = AnnotationType.getInstance(clazz);
      if (annotationType == null)
        throw invalidContainerException(paramAnnotation, null); 
      Method method = (Method)annotationType.members().get("value");
      if (method == null)
        throw invalidContainerException(paramAnnotation, null); 
      method.setAccessible(true);
      return (A[])(Annotation[])method.invoke(paramAnnotation, new Object[0]);
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|ClassCastException illegalAccessException) {
      throw invalidContainerException(paramAnnotation, illegalAccessException);
    } 
  }
  
  private static AnnotationFormatError invalidContainerException(Annotation paramAnnotation, Throwable paramThrowable) { return new AnnotationFormatError(paramAnnotation + " is an invalid container for repeating annotations", paramThrowable); }
  
  private static <A extends Annotation> void checkTypes(A[] paramArrayOfA, Annotation paramAnnotation, Class<A> paramClass) {
    for (A a : paramArrayOfA) {
      if (!paramClass.isInstance(a))
        throw new AnnotationFormatError(String.format("%s is an invalid container for repeating annotations of type: %s", new Object[] { paramAnnotation, paramClass })); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\annotation\AnnotationSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */