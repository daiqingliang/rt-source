package javax.lang.model.type;

public interface TypeVisitor<R, P> {
  R visit(TypeMirror paramTypeMirror, P paramP);
  
  R visit(TypeMirror paramTypeMirror);
  
  R visitPrimitive(PrimitiveType paramPrimitiveType, P paramP);
  
  R visitNull(NullType paramNullType, P paramP);
  
  R visitArray(ArrayType paramArrayType, P paramP);
  
  R visitDeclared(DeclaredType paramDeclaredType, P paramP);
  
  R visitError(ErrorType paramErrorType, P paramP);
  
  R visitTypeVariable(TypeVariable paramTypeVariable, P paramP);
  
  R visitWildcard(WildcardType paramWildcardType, P paramP);
  
  R visitExecutable(ExecutableType paramExecutableType, P paramP);
  
  R visitNoType(NoType paramNoType, P paramP);
  
  R visitUnknown(TypeMirror paramTypeMirror, P paramP);
  
  R visitUnion(UnionType paramUnionType, P paramP);
  
  R visitIntersection(IntersectionType paramIntersectionType, P paramP);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\TypeVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */