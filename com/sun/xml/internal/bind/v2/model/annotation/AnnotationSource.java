package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationSource {
  <A extends Annotation> A readAnnotation(Class<A> paramClass);
  
  boolean hasAnnotation(Class<? extends Annotation> paramClass);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\AnnotationSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */