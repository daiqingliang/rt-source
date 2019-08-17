package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.WildcardType;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleTypeVisitor6<R, P> extends AbstractTypeVisitor6<R, P> {
  protected final R DEFAULT_VALUE = null;
  
  protected SimpleTypeVisitor6() {}
  
  protected SimpleTypeVisitor6(R paramR) {}
  
  protected R defaultAction(TypeMirror paramTypeMirror, P paramP) { return (R)this.DEFAULT_VALUE; }
  
  public R visitPrimitive(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitNull(NullType paramNullType, P paramP) { return (R)defaultAction(paramNullType, paramP); }
  
  public R visitArray(ArrayType paramArrayType, P paramP) { return (R)defaultAction(paramArrayType, paramP); }
  
  public R visitDeclared(DeclaredType paramDeclaredType, P paramP) { return (R)defaultAction(paramDeclaredType, paramP); }
  
  public R visitError(ErrorType paramErrorType, P paramP) { return (R)defaultAction(paramErrorType, paramP); }
  
  public R visitTypeVariable(TypeVariable paramTypeVariable, P paramP) { return (R)defaultAction(paramTypeVariable, paramP); }
  
  public R visitWildcard(WildcardType paramWildcardType, P paramP) { return (R)defaultAction(paramWildcardType, paramP); }
  
  public R visitExecutable(ExecutableType paramExecutableType, P paramP) { return (R)defaultAction(paramExecutableType, paramP); }
  
  public R visitNoType(NoType paramNoType, P paramP) { return (R)defaultAction(paramNoType, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\SimpleTypeVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */