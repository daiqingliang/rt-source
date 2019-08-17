package javax.lang.model.type;

import javax.lang.model.AnnotatedConstruct;

public interface TypeMirror extends AnnotatedConstruct {
  TypeKind getKind();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
  
  <R, P> R accept(TypeVisitor<R, P> paramTypeVisitor, P paramP);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\TypeMirror.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */