package javax.lang.model.element;

public interface VariableElement extends Element {
  Object getConstantValue();
  
  Name getSimpleName();
  
  Element getEnclosingElement();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\VariableElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */