package javax.lang.model.element;

public interface AnnotationValue {
  Object getValue();
  
  String toString();
  
  <R, P> R accept(AnnotationValueVisitor<R, P> paramAnnotationValueVisitor, P paramP);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\AnnotationValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */