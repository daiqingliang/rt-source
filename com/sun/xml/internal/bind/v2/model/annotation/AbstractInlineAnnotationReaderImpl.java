package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.annotation.Annotation;

public abstract class AbstractInlineAnnotationReaderImpl<T, C, F, M> extends Object implements AnnotationReader<T, C, F, M> {
  private ErrorHandler errorHandler;
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) {
    if (paramErrorHandler == null)
      throw new IllegalArgumentException(); 
    this.errorHandler = paramErrorHandler;
  }
  
  public final ErrorHandler getErrorHandler() {
    assert this.errorHandler != null : "error handler must be set before use";
    return this.errorHandler;
  }
  
  public final <A extends Annotation> A getMethodAnnotation(Class<A> paramClass, M paramM1, M paramM2, Locatable paramLocatable) {
    Annotation annotation1 = (paramM1 == null) ? null : getMethodAnnotation(paramClass, paramM1, paramLocatable);
    Annotation annotation2 = (paramM2 == null) ? null : getMethodAnnotation(paramClass, paramM2, paramLocatable);
    if (annotation1 == null)
      return (annotation2 == null) ? null : (A)annotation2; 
    if (annotation2 == null)
      return (A)annotation1; 
    getErrorHandler().error(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(new Object[] { paramClass.getName(), fullName(paramM1), fullName(paramM2) }, ), annotation1, annotation2));
    return (A)annotation1;
  }
  
  public boolean hasMethodAnnotation(Class<? extends Annotation> paramClass, String paramString, M paramM1, M paramM2, Locatable paramLocatable) {
    boolean bool1 = (paramM1 != null && hasMethodAnnotation(paramClass, paramM1)) ? 1 : 0;
    boolean bool2 = (paramM2 != null && hasMethodAnnotation(paramClass, paramM2)) ? 1 : 0;
    if (bool1 && bool2)
      getMethodAnnotation(paramClass, paramM1, paramM2, paramLocatable); 
    return (bool1 || bool2);
  }
  
  protected abstract String fullName(M paramM);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\AbstractInlineAnnotationReaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */