package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

final class GenericArrayTypeImpl implements GenericArrayType {
  private Type genericComponentType;
  
  GenericArrayTypeImpl(Type paramType) {
    assert paramType != null;
    this.genericComponentType = paramType;
  }
  
  public Type getGenericComponentType() { return this.genericComponentType; }
  
  public String toString() {
    Type type = getGenericComponentType();
    StringBuilder stringBuilder = new StringBuilder();
    if (type instanceof Class) {
      stringBuilder.append(((Class)type).getName());
    } else {
      stringBuilder.append(type.toString());
    } 
    stringBuilder.append("[]");
    return stringBuilder.toString();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType)paramObject;
      Type type = genericArrayType.getGenericComponentType();
      return this.genericComponentType.equals(type);
    } 
    return false;
  }
  
  public int hashCode() { return this.genericComponentType.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\GenericArrayTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */