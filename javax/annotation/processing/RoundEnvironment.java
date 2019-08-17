package javax.annotation.processing;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public interface RoundEnvironment {
  boolean processingOver();
  
  boolean errorRaised();
  
  Set<? extends Element> getRootElements();
  
  Set<? extends Element> getElementsAnnotatedWith(TypeElement paramTypeElement);
  
  Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> paramClass);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\annotation\processing\RoundEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */