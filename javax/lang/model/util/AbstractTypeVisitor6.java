package javax.lang.model.util;

import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.UnknownTypeException;

public abstract class AbstractTypeVisitor6<R, P> extends Object implements TypeVisitor<R, P> {
  public final R visit(TypeMirror paramTypeMirror, P paramP) { return (R)paramTypeMirror.accept(this, paramP); }
  
  public final R visit(TypeMirror paramTypeMirror) { return (R)paramTypeMirror.accept(this, null); }
  
  public R visitUnion(UnionType paramUnionType, P paramP) { return (R)visitUnknown(paramUnionType, paramP); }
  
  public R visitIntersection(IntersectionType paramIntersectionType, P paramP) { return (R)visitUnknown(paramIntersectionType, paramP); }
  
  public R visitUnknown(TypeMirror paramTypeMirror, P paramP) { throw new UnknownTypeException(paramTypeMirror, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\AbstractTypeVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */