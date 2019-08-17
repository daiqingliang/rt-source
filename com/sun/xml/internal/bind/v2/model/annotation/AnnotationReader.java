package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import java.lang.annotation.Annotation;

public interface AnnotationReader<T, C, F, M> {
  void setErrorHandler(ErrorHandler paramErrorHandler);
  
  <A extends Annotation> A getFieldAnnotation(Class<A> paramClass, F paramF, Locatable paramLocatable);
  
  boolean hasFieldAnnotation(Class<? extends Annotation> paramClass, F paramF);
  
  boolean hasClassAnnotation(C paramC, Class<? extends Annotation> paramClass);
  
  Annotation[] getAllFieldAnnotations(F paramF, Locatable paramLocatable);
  
  <A extends Annotation> A getMethodAnnotation(Class<A> paramClass, M paramM1, M paramM2, Locatable paramLocatable);
  
  boolean hasMethodAnnotation(Class<? extends Annotation> paramClass, String paramString, M paramM1, M paramM2, Locatable paramLocatable);
  
  Annotation[] getAllMethodAnnotations(M paramM, Locatable paramLocatable);
  
  <A extends Annotation> A getMethodAnnotation(Class<A> paramClass, M paramM, Locatable paramLocatable);
  
  boolean hasMethodAnnotation(Class<? extends Annotation> paramClass, M paramM);
  
  @Nullable
  <A extends Annotation> A getMethodParameterAnnotation(Class<A> paramClass, M paramM, int paramInt, Locatable paramLocatable);
  
  @Nullable
  <A extends Annotation> A getClassAnnotation(Class<A> paramClass, C paramC, Locatable paramLocatable);
  
  @Nullable
  <A extends Annotation> A getPackageAnnotation(Class<A> paramClass, C paramC, Locatable paramLocatable);
  
  T getClassValue(Annotation paramAnnotation, String paramString);
  
  T[] getClassArrayValue(Annotation paramAnnotation, String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\AnnotationReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */