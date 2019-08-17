package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.IntersectionType;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TypeKindVisitor8<R, P> extends TypeKindVisitor7<R, P> {
  protected TypeKindVisitor8() { super(null); }
  
  protected TypeKindVisitor8(R paramR) { super(paramR); }
  
  public R visitIntersection(IntersectionType paramIntersectionType, P paramP) { return (R)defaultAction(paramIntersectionType, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\TypeKindVisitor8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */