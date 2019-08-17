package com.sun.xml.internal.ws.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

final class FieldSignature {
  static String vms(Type paramType) {
    if (paramType instanceof Class && ((Class)paramType).isPrimitive()) {
      Class clazz = (Class)paramType;
      if (clazz == int.class)
        return "I"; 
      if (clazz == void.class)
        return "V"; 
      if (clazz == boolean.class)
        return "Z"; 
      if (clazz == byte.class)
        return "B"; 
      if (clazz == char.class)
        return "C"; 
      if (clazz == short.class)
        return "S"; 
      if (clazz == double.class)
        return "D"; 
      if (clazz == float.class)
        return "F"; 
      if (clazz == long.class)
        return "J"; 
    } else {
      if (paramType instanceof Class && ((Class)paramType).isArray())
        return "[" + vms(((Class)paramType).getComponentType()); 
      if (paramType instanceof Class || paramType instanceof ParameterizedType)
        return "L" + fqcn(paramType) + ";"; 
      if (paramType instanceof GenericArrayType)
        return "[" + vms(((GenericArrayType)paramType).getGenericComponentType()); 
      if (paramType instanceof java.lang.reflect.TypeVariable)
        return "Ljava/lang/Object;"; 
      if (paramType instanceof WildcardType) {
        WildcardType wildcardType = (WildcardType)paramType;
        if (wildcardType.getLowerBounds().length > 0)
          return "-" + vms(wildcardType.getLowerBounds()[0]); 
        if (wildcardType.getUpperBounds().length > 0) {
          Type type = wildcardType.getUpperBounds()[0];
          return type.equals(Object.class) ? "*" : ("+" + vms(type));
        } 
      } 
    } 
    throw new IllegalArgumentException("Illegal vms arg " + paramType);
  }
  
  private static String fqcn(Type paramType) {
    if (paramType instanceof Class) {
      Class clazz = (Class)paramType;
      return (clazz.getDeclaringClass() == null) ? clazz.getName().replace('.', '/') : (fqcn(clazz.getDeclaringClass()) + "$" + clazz.getSimpleName());
    } 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      if (parameterizedType.getOwnerType() == null)
        return fqcn(parameterizedType.getRawType()) + args(parameterizedType); 
      assert parameterizedType.getRawType() instanceof Class;
      return fqcn(parameterizedType.getOwnerType()) + "." + ((Class)parameterizedType.getRawType()).getSimpleName() + args(parameterizedType);
    } 
    throw new IllegalArgumentException("Illegal fqcn arg = " + paramType);
  }
  
  private static String args(ParameterizedType paramParameterizedType) {
    StringBuilder stringBuilder = new StringBuilder("<");
    for (Type type : paramParameterizedType.getActualTypeArguments())
      stringBuilder.append(vms(type)); 
    return stringBuilder.append(">").toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\FieldSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */