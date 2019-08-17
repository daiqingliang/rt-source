package javax.lang.model.type;

import java.util.List;
import javax.lang.model.element.Element;

public interface DeclaredType extends ReferenceType {
  Element asElement();
  
  TypeMirror getEnclosingType();
  
  List<? extends TypeMirror> getTypeArguments();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\DeclaredType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */