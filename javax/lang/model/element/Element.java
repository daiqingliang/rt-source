package javax.lang.model.element;

import java.util.List;
import java.util.Set;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.type.TypeMirror;

public interface Element extends AnnotatedConstruct {
  TypeMirror asType();
  
  ElementKind getKind();
  
  Set<Modifier> getModifiers();
  
  Name getSimpleName();
  
  Element getEnclosingElement();
  
  List<? extends Element> getEnclosedElements();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  List<? extends AnnotationMirror> getAnnotationMirrors();
  
  <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> paramClass);
  
  <R, P> R accept(ElementVisitor<R, P> paramElementVisitor, P paramP);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */