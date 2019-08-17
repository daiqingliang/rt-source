package java.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;

public class IncompleteAnnotationException extends RuntimeException {
  private static final long serialVersionUID = 8445097402741811912L;
  
  private Class<? extends Annotation> annotationType;
  
  private String elementName;
  
  public IncompleteAnnotationException(Class<? extends Annotation> paramClass, String paramString) {
    super(paramClass.getName() + " missing element " + paramString.toString());
    this.annotationType = paramClass;
    this.elementName = paramString;
  }
  
  public Class<? extends Annotation> annotationType() { return this.annotationType; }
  
  public String elementName() { return this.elementName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\annotation\IncompleteAnnotationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */