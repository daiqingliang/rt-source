package java.lang.annotation;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Method;

public class AnnotationTypeMismatchException extends RuntimeException {
  private static final long serialVersionUID = 8125925355765570191L;
  
  private final Method element;
  
  private final String foundType;
  
  public AnnotationTypeMismatchException(Method paramMethod, String paramString) {
    super("Incorrectly typed data found for annotation element " + paramMethod + " (Found data of type " + paramString + ")");
    this.element = paramMethod;
    this.foundType = paramString;
  }
  
  public Method element() { return this.element; }
  
  public String foundType() { return this.foundType; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\annotation\AnnotationTypeMismatchException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */