package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.UnknownElementException;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public abstract class AbstractElementVisitor6<R, P> extends Object implements ElementVisitor<R, P> {
  public final R visit(Element paramElement, P paramP) { return (R)paramElement.accept(this, paramP); }
  
  public final R visit(Element paramElement) { return (R)paramElement.accept(this, null); }
  
  public R visitUnknown(Element paramElement, P paramP) { throw new UnknownElementException(paramElement, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\AbstractElementVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */