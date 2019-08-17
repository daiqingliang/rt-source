package javax.lang.model.type;

import java.util.List;

public interface UnionType extends TypeMirror {
  List<? extends TypeMirror> getAlternatives();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\UnionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */