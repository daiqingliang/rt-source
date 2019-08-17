package javax.lang.model.util;

import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TypeKindVisitor6<R, P> extends SimpleTypeVisitor6<R, P> {
  protected TypeKindVisitor6() { super(null); }
  
  protected TypeKindVisitor6(R paramR) { super(paramR); }
  
  public R visitPrimitive(PrimitiveType paramPrimitiveType, P paramP) {
    TypeKind typeKind = paramPrimitiveType.getKind();
    switch (typeKind) {
      case BOOLEAN:
        return (R)visitPrimitiveAsBoolean(paramPrimitiveType, paramP);
      case BYTE:
        return (R)visitPrimitiveAsByte(paramPrimitiveType, paramP);
      case SHORT:
        return (R)visitPrimitiveAsShort(paramPrimitiveType, paramP);
      case INT:
        return (R)visitPrimitiveAsInt(paramPrimitiveType, paramP);
      case LONG:
        return (R)visitPrimitiveAsLong(paramPrimitiveType, paramP);
      case CHAR:
        return (R)visitPrimitiveAsChar(paramPrimitiveType, paramP);
      case FLOAT:
        return (R)visitPrimitiveAsFloat(paramPrimitiveType, paramP);
      case DOUBLE:
        return (R)visitPrimitiveAsDouble(paramPrimitiveType, paramP);
    } 
    throw new AssertionError("Bad kind " + typeKind + " for PrimitiveType" + paramPrimitiveType);
  }
  
  public R visitPrimitiveAsBoolean(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitPrimitiveAsByte(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitPrimitiveAsShort(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitPrimitiveAsInt(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitPrimitiveAsLong(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitPrimitiveAsChar(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitPrimitiveAsFloat(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitPrimitiveAsDouble(PrimitiveType paramPrimitiveType, P paramP) { return (R)defaultAction(paramPrimitiveType, paramP); }
  
  public R visitNoType(NoType paramNoType, P paramP) {
    TypeKind typeKind = paramNoType.getKind();
    switch (typeKind) {
      case VOID:
        return (R)visitNoTypeAsVoid(paramNoType, paramP);
      case PACKAGE:
        return (R)visitNoTypeAsPackage(paramNoType, paramP);
      case NONE:
        return (R)visitNoTypeAsNone(paramNoType, paramP);
    } 
    throw new AssertionError("Bad kind " + typeKind + " for NoType" + paramNoType);
  }
  
  public R visitNoTypeAsVoid(NoType paramNoType, P paramP) { return (R)defaultAction(paramNoType, paramP); }
  
  public R visitNoTypeAsPackage(NoType paramNoType, P paramP) { return (R)defaultAction(paramNoType, paramP); }
  
  public R visitNoTypeAsNone(NoType paramNoType, P paramP) { return (R)defaultAction(paramNoType, paramP); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\TypeKindVisitor6.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */