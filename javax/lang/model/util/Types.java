package javax.lang.model.util;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

public interface Types {
  Element asElement(TypeMirror paramTypeMirror);
  
  boolean isSameType(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  boolean isSubtype(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  boolean isAssignable(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  boolean contains(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  boolean isSubsignature(ExecutableType paramExecutableType1, ExecutableType paramExecutableType2);
  
  List<? extends TypeMirror> directSupertypes(TypeMirror paramTypeMirror);
  
  TypeMirror erasure(TypeMirror paramTypeMirror);
  
  TypeElement boxedClass(PrimitiveType paramPrimitiveType);
  
  PrimitiveType unboxedType(TypeMirror paramTypeMirror);
  
  TypeMirror capture(TypeMirror paramTypeMirror);
  
  PrimitiveType getPrimitiveType(TypeKind paramTypeKind);
  
  NullType getNullType();
  
  NoType getNoType(TypeKind paramTypeKind);
  
  ArrayType getArrayType(TypeMirror paramTypeMirror);
  
  WildcardType getWildcardType(TypeMirror paramTypeMirror1, TypeMirror paramTypeMirror2);
  
  DeclaredType getDeclaredType(TypeElement paramTypeElement, TypeMirror... paramVarArgs);
  
  DeclaredType getDeclaredType(DeclaredType paramDeclaredType, TypeElement paramTypeElement, TypeMirror... paramVarArgs);
  
  TypeMirror asMemberOf(DeclaredType paramDeclaredType, Element paramElement);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\mode\\util\Types.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */