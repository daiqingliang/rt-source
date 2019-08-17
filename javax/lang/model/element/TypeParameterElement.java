package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.TypeMirror;

public interface TypeParameterElement extends Element {
  Element getGenericElement();
  
  List<? extends TypeMirror> getBounds();
  
  Element getEnclosingElement();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\TypeParameterElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */