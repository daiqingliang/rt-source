package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeInlineAnnotationReader extends AbstractInlineAnnotationReaderImpl<Type, Class, Field, Method> implements RuntimeAnnotationReader {
  private final Map<Class<? extends Annotation>, Map<Package, Annotation>> packageCache = new HashMap();
  
  public <A extends Annotation> A getFieldAnnotation(Class<A> paramClass, Field paramField, Locatable paramLocatable) { return (A)LocatableAnnotation.create(paramField.getAnnotation(paramClass), paramLocatable); }
  
  public boolean hasFieldAnnotation(Class<? extends Annotation> paramClass, Field paramField) { return paramField.isAnnotationPresent(paramClass); }
  
  public boolean hasClassAnnotation(Class paramClass1, Class<? extends Annotation> paramClass2) { return paramClass1.isAnnotationPresent(paramClass2); }
  
  public Annotation[] getAllFieldAnnotations(Field paramField, Locatable paramLocatable) {
    Annotation[] arrayOfAnnotation = paramField.getAnnotations();
    for (byte b = 0; b < arrayOfAnnotation.length; b++)
      arrayOfAnnotation[b] = LocatableAnnotation.create(arrayOfAnnotation[b], paramLocatable); 
    return arrayOfAnnotation;
  }
  
  public <A extends Annotation> A getMethodAnnotation(Class<A> paramClass, Method paramMethod, Locatable paramLocatable) { return (A)LocatableAnnotation.create(paramMethod.getAnnotation(paramClass), paramLocatable); }
  
  public boolean hasMethodAnnotation(Class<? extends Annotation> paramClass, Method paramMethod) { return paramMethod.isAnnotationPresent(paramClass); }
  
  public Annotation[] getAllMethodAnnotations(Method paramMethod, Locatable paramLocatable) {
    Annotation[] arrayOfAnnotation = paramMethod.getAnnotations();
    for (byte b = 0; b < arrayOfAnnotation.length; b++)
      arrayOfAnnotation[b] = LocatableAnnotation.create(arrayOfAnnotation[b], paramLocatable); 
    return arrayOfAnnotation;
  }
  
  public <A extends Annotation> A getMethodParameterAnnotation(Class<A> paramClass, Method paramMethod, int paramInt, Locatable paramLocatable) {
    Annotation[] arrayOfAnnotation = paramMethod.getParameterAnnotations()[paramInt];
    for (Annotation annotation : arrayOfAnnotation) {
      if (annotation.annotationType() == paramClass)
        return (A)LocatableAnnotation.create(annotation, paramLocatable); 
    } 
    return null;
  }
  
  public <A extends Annotation> A getClassAnnotation(Class<A> paramClass1, Class paramClass2, Locatable paramLocatable) { return (A)LocatableAnnotation.create(paramClass2.getAnnotation(paramClass1), paramLocatable); }
  
  public <A extends Annotation> A getPackageAnnotation(Class<A> paramClass1, Class paramClass2, Locatable paramLocatable) {
    Package package = paramClass2.getPackage();
    if (package == null)
      return null; 
    Map map = (Map)this.packageCache.get(paramClass1);
    if (map == null) {
      map = new HashMap();
      this.packageCache.put(paramClass1, map);
    } 
    if (map.containsKey(package))
      return (A)(Annotation)map.get(package); 
    Annotation annotation = LocatableAnnotation.create(package.getAnnotation(paramClass1), paramLocatable);
    map.put(package, annotation);
    return (A)annotation;
  }
  
  public Class getClassValue(Annotation paramAnnotation, String paramString) {
    try {
      return (Class)paramAnnotation.annotationType().getMethod(paramString, new Class[0]).invoke(paramAnnotation, new Object[0]);
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalAccessError(illegalAccessException.getMessage());
    } catch (InvocationTargetException invocationTargetException) {
      throw new InternalError(Messages.CLASS_NOT_FOUND.format(new Object[] { paramAnnotation.annotationType(), invocationTargetException.getMessage() }));
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } 
  }
  
  public Class[] getClassArrayValue(Annotation paramAnnotation, String paramString) {
    try {
      return (Class[])paramAnnotation.annotationType().getMethod(paramString, new Class[0]).invoke(paramAnnotation, new Object[0]);
    } catch (IllegalAccessException illegalAccessException) {
      throw new IllegalAccessError(illegalAccessException.getMessage());
    } catch (InvocationTargetException invocationTargetException) {
      throw new InternalError(invocationTargetException.getMessage());
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } 
  }
  
  protected String fullName(Method paramMethod) { return paramMethod.getDeclaringClass().getName() + '#' + paramMethod.getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\RuntimeInlineAnnotationReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */