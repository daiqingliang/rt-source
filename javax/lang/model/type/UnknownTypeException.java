package javax.lang.model.type;

import javax.lang.model.UnknownEntityException;

public class UnknownTypeException extends UnknownEntityException {
  private static final long serialVersionUID = 269L;
  
  private TypeMirror type;
  
  private Object parameter;
  
  public UnknownTypeException(TypeMirror paramTypeMirror, Object paramObject) {
    super("Unknown type: " + paramTypeMirror);
    this.type = paramTypeMirror;
    this.parameter = paramObject;
  }
  
  public TypeMirror getUnknownType() { return this.type; }
  
  public Object getArgument() { return this.parameter; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\UnknownTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */