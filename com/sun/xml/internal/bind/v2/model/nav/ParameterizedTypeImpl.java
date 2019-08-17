package com.sun.xml.internal.bind.v2.model.nav;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

class ParameterizedTypeImpl implements ParameterizedType {
  private Type[] actualTypeArguments;
  
  private Class<?> rawType;
  
  private Type ownerType;
  
  ParameterizedTypeImpl(Class<?> paramClass, Type[] paramArrayOfType, Type paramType) {
    this.actualTypeArguments = paramArrayOfType;
    this.rawType = paramClass;
    if (paramType != null) {
      this.ownerType = paramType;
    } else {
      this.ownerType = paramClass.getDeclaringClass();
    } 
    validateConstructorArguments();
  }
  
  private void validateConstructorArguments() {
    TypeVariable[] arrayOfTypeVariable = this.rawType.getTypeParameters();
    if (arrayOfTypeVariable.length != this.actualTypeArguments.length)
      throw new MalformedParameterizedTypeException(); 
  }
  
  public Type[] getActualTypeArguments() { return (Type[])this.actualTypeArguments.clone(); }
  
  public Class<?> getRawType() { return this.rawType; }
  
  public Type getOwnerType() { return this.ownerType; }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramObject;
      if (this == parameterizedType)
        return true; 
      Type type1 = parameterizedType.getOwnerType();
      Type type2 = parameterizedType.getRawType();
      return (((this.ownerType == null) ? (type1 == null) : this.ownerType.equals(type1)) && ((this.rawType == null) ? (type2 == null) : this.rawType.equals(type2)) && Arrays.equals(this.actualTypeArguments, parameterizedType.getActualTypeArguments()));
    } 
    return false;
  }
  
  public int hashCode() { return Arrays.hashCode(this.actualTypeArguments) ^ ((this.ownerType == null) ? 0 : this.ownerType.hashCode()) ^ ((this.rawType == null) ? 0 : this.rawType.hashCode()); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (this.ownerType != null) {
      if (this.ownerType instanceof Class) {
        stringBuilder.append(((Class)this.ownerType).getName());
      } else {
        stringBuilder.append(this.ownerType.toString());
      } 
      stringBuilder.append(".");
      if (this.ownerType instanceof ParameterizedTypeImpl) {
        stringBuilder.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
      } else {
        stringBuilder.append(this.rawType.getName());
      } 
    } else {
      stringBuilder.append(this.rawType.getName());
    } 
    if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
      stringBuilder.append("<");
      boolean bool = true;
      for (Type type : this.actualTypeArguments) {
        if (!bool)
          stringBuilder.append(", "); 
        if (type instanceof Class) {
          stringBuilder.append(((Class)type).getName());
        } else {
          stringBuilder.append(type.toString());
        } 
        bool = false;
      } 
      stringBuilder.append(">");
    } 
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\nav\ParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */