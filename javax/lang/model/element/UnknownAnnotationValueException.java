package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownAnnotationValueException extends UnknownEntityException {
  private static final long serialVersionUID = 269L;
  
  private AnnotationValue av;
  
  private Object parameter;
  
  public UnknownAnnotationValueException(AnnotationValue paramAnnotationValue, Object paramObject) {
    super("Unknown annotation value: " + paramAnnotationValue);
    this.av = paramAnnotationValue;
    this.parameter = paramObject;
  }
  
  public AnnotationValue getUnknownAnnotationValue() { return this.av; }
  
  public Object getArgument() { return this.parameter; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\UnknownAnnotationValueException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */