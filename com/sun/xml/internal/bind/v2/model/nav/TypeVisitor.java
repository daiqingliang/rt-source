package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

abstract class TypeVisitor<T, P> extends Object {
  public final T visit(Type paramType, P paramP) {
    assert paramType != null;
    if (paramType instanceof Class)
      return (T)onClass((Class)paramType, paramP); 
    if (paramType instanceof ParameterizedType)
      return (T)onParameterizdType((ParameterizedType)paramType, paramP); 
    if (paramType instanceof GenericArrayType)
      return (T)onGenericArray((GenericArrayType)paramType, paramP); 
    if (paramType instanceof WildcardType)
      return (T)onWildcard((WildcardType)paramType, paramP); 
    if (paramType instanceof TypeVariable)
      return (T)onVariable((TypeVariable)paramType, paramP); 
    assert false;
    throw new IllegalArgumentException();
  }
  
  protected abstract T onClass(Class paramClass, P paramP);
  
  protected abstract T onParameterizdType(ParameterizedType paramParameterizedType, P paramP);
  
  protected abstract T onGenericArray(GenericArrayType paramGenericArrayType, P paramP);
  
  protected abstract T onVariable(TypeVariable paramTypeVariable, P paramP);
  
  protected abstract T onWildcard(WildcardType paramWildcardType, P paramP);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\TypeVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */