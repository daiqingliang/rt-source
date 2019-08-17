package javax.lang.model.element;

import java.util.List;

public interface PackageElement extends Element, QualifiedNameable {
  Name getQualifiedName();
  
  Name getSimpleName();
  
  List<? extends Element> getEnclosedElements();
  
  boolean isUnnamed();
  
  Element getEnclosingElement();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\element\PackageElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */