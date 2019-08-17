package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.AnnotationValueVisitor;
import javax.lang.model.element.UnknownAnnotationValueException;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class AbstractAnnotationValueVisitor6<R, P> extends Object implements AnnotationValueVisitor<R, P> {
  public final R visit(AnnotationValue paramAnnotationValue, P paramP) { return (R)paramAnnotationValue.accept(this, paramP); }
  
  public final R visit(AnnotationValue paramAnnotationValue) { return (R)paramAnnotationValue.accept(this, null); }
  
  public R visitUnknown(AnnotationValue paramAnnotationValue, P paramP) { throw new UnknownAnnotationValueException(paramAnnotationValue, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\AbstractAnnotationValueVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */