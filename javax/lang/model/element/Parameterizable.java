package javax.lang.model.element;

import java.util.List;

public interface Parameterizable extends Element {
  List<? extends TypeParameterElement> getTypeParameters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\Parameterizable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */