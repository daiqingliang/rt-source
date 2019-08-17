package com.sun.corba.se.impl.orbutil;

import java.lang.reflect.Array;

public final class ObjectUtility {
  public static Object concatenateArrays(Object paramObject1, Object paramObject2) {
    Class clazz1 = paramObject1.getClass().getComponentType();
    Class clazz2 = paramObject2.getClass().getComponentType();
    int i = Array.getLength(paramObject1);
    int j = Array.getLength(paramObject2);
    if (clazz1 == null || clazz2 == null)
      throw new IllegalStateException("Arguments must be arrays"); 
    if (!clazz1.equals(clazz2))
      throw new IllegalStateException("Arguments must be arrays with the same component type"); 
    Object object = Array.newInstance(clazz1, i + j);
    byte b1 = 0;
    byte b2;
    for (b2 = 0; b2 < i; b2++)
      Array.set(object, b1++, Array.get(paramObject1, b2)); 
    for (b2 = 0; b2 < j; b2++)
      Array.set(object, b1++, Array.get(paramObject2, b2)); 
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\ObjectUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */