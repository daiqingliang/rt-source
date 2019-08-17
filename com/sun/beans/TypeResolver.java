package com.sun.beans;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public final class TypeResolver {
  private static final WeakCache<Type, Map<Type, Type>> CACHE = new WeakCache();
  
  public static Type resolveInClass(Class<?> paramClass, Type paramType) { return resolve(getActualType(paramClass), paramType); }
  
  public static Type[] resolveInClass(Class<?> paramClass, Type[] paramArrayOfType) { return resolve(getActualType(paramClass), paramArrayOfType); }
  
  public static Type resolve(Type paramType1, Type paramType2) {
    if (paramType2 instanceof Class)
      return paramType2; 
    if (paramType2 instanceof GenericArrayType) {
      Type type = ((GenericArrayType)paramType2).getGenericComponentType();
      type = resolve(paramType1, type);
      return (type instanceof Class) ? Array.newInstance((Class)type, 0).getClass() : GenericArrayTypeImpl.make(type);
    } 
    if (paramType2 instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType2;
      Type[] arrayOfType = resolve(paramType1, parameterizedType.getActualTypeArguments());
      return ParameterizedTypeImpl.make((Class)parameterizedType.getRawType(), arrayOfType, parameterizedType.getOwnerType());
    } 
    if (paramType2 instanceof WildcardType) {
      WildcardType wildcardType = (WildcardType)paramType2;
      Type[] arrayOfType1 = resolve(paramType1, wildcardType.getUpperBounds());
      Type[] arrayOfType2 = resolve(paramType1, wildcardType.getLowerBounds());
      return new WildcardTypeImpl(arrayOfType1, arrayOfType2);
    } 
    if (paramType2 instanceof TypeVariable) {
      Map map;
      synchronized (CACHE) {
        map = (Map)CACHE.get(paramType1);
        if (map == null) {
          map = new HashMap();
          prepare(map, paramType1);
          CACHE.put(paramType1, map);
        } 
      } 
      Type type = (Type)map.get(paramType2);
      if (type == null || type.equals(paramType2))
        return paramType2; 
      type = fixGenericArray(type);
      return resolve(paramType1, type);
    } 
    throw new IllegalArgumentException("Bad Type kind: " + paramType2.getClass());
  }
  
  public static Type[] resolve(Type paramType, Type[] paramArrayOfType) {
    int i = paramArrayOfType.length;
    Type[] arrayOfType = new Type[i];
    for (byte b = 0; b < i; b++)
      arrayOfType[b] = resolve(paramType, paramArrayOfType[b]); 
    return arrayOfType;
  }
  
  public static Class<?> erase(Type paramType) {
    if (paramType instanceof Class)
      return (Class)paramType; 
    if (paramType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)paramType;
      return (Class)parameterizedType.getRawType();
    } 
    if (paramType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable)paramType;
      Type[] arrayOfType = typeVariable.getBounds();
      return (0 < arrayOfType.length) ? erase(arrayOfType[0]) : Object.class;
    } 
    if (paramType instanceof WildcardType) {
      WildcardType wildcardType = (WildcardType)paramType;
      Type[] arrayOfType = wildcardType.getUpperBounds();
      return (0 < arrayOfType.length) ? erase(arrayOfType[0]) : Object.class;
    } 
    if (paramType instanceof GenericArrayType) {
      GenericArrayType genericArrayType = (GenericArrayType)paramType;
      return Array.newInstance(erase(genericArrayType.getGenericComponentType()), 0).getClass();
    } 
    throw new IllegalArgumentException("Unknown Type kind: " + paramType.getClass());
  }
  
  public static Class[] erase(Type[] paramArrayOfType) {
    int i = paramArrayOfType.length;
    Class[] arrayOfClass = new Class[i];
    for (byte b = 0; b < i; b++)
      arrayOfClass[b] = erase(paramArrayOfType[b]); 
    return arrayOfClass;
  }
  
  private static void prepare(Map<Type, Type> paramMap, Type paramType) {
    Class clazz = (Class)((paramType instanceof Class) ? paramType : ((ParameterizedType)paramType).getRawType());
    TypeVariable[] arrayOfTypeVariable1 = clazz.getTypeParameters();
    TypeVariable[] arrayOfTypeVariable2 = (paramType instanceof Class) ? arrayOfTypeVariable1 : ((ParameterizedType)paramType).getActualTypeArguments();
    assert arrayOfTypeVariable1.length == arrayOfTypeVariable2.length;
    for (byte b = 0; b < arrayOfTypeVariable1.length; b++)
      paramMap.put(arrayOfTypeVariable1[b], arrayOfTypeVariable2[b]); 
    Type type = clazz.getGenericSuperclass();
    if (type != null)
      prepare(paramMap, type); 
    for (Type type1 : clazz.getGenericInterfaces())
      prepare(paramMap, type1); 
    if (paramType instanceof Class && arrayOfTypeVariable1.length > 0)
      for (Map.Entry entry : paramMap.entrySet())
        entry.setValue(erase((Type)entry.getValue()));  
  }
  
  private static Type fixGenericArray(Type paramType) {
    if (paramType instanceof GenericArrayType) {
      Type type = ((GenericArrayType)paramType).getGenericComponentType();
      type = fixGenericArray(type);
      if (type instanceof Class)
        return Array.newInstance((Class)type, 0).getClass(); 
    } 
    return paramType;
  }
  
  private static Type getActualType(Class<?> paramClass) {
    TypeVariable[] arrayOfTypeVariable = paramClass.getTypeParameters();
    return (arrayOfTypeVariable.length == 0) ? paramClass : ParameterizedTypeImpl.make(paramClass, arrayOfTypeVariable, paramClass.getEnclosingClass());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\TypeResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */