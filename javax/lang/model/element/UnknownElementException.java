package javax.lang.model.element;

import javax.lang.model.UnknownEntityException;

public class UnknownElementException extends UnknownEntityException {
  private static final long serialVersionUID = 269L;
  
  private Element element;
  
  private Object parameter;
  
  public UnknownElementException(Element paramElement, Object paramObject) {
    super("Unknown element: " + paramElement);
    this.element = paramElement;
    this.parameter = paramObject;
  }
  
  public Element getUnknownElement() { return this.element; }
  
  public Object getArgument() { return this.parameter; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\UnknownElementException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */