package com.sun.beans.finder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;

public final class FieldFinder {
  public static Field findField(Class<?> paramClass, String paramString) throws NoSuchFieldException {
    if (paramString == null)
      throw new IllegalArgumentException("Field name is not set"); 
    Field field = paramClass.getField(paramString);
    if (!Modifier.isPublic(field.getModifiers()))
      throw new NoSuchFieldException("Field '" + paramString + "' is not public"); 
    paramClass = field.getDeclaringClass();
    if (!Modifier.isPublic(paramClass.getModifiers()) || !ReflectUtil.isPackageAccessible(paramClass))
      throw new NoSuchFieldException("Field '" + paramString + "' is not accessible"); 
    return field;
  }
  
  public static Field findInstanceField(Class<?> paramClass, String paramString) throws NoSuchFieldException {
    Field field = findField(paramClass, paramString);
    if (Modifier.isStatic(field.getModifiers()))
      throw new NoSuchFieldException("Field '" + paramString + "' is static"); 
    return field;
  }
  
  public static Field findStaticField(Class<?> paramClass, String paramString) throws NoSuchFieldException {
    Field field = findField(paramClass, paramString);
    if (!Modifier.isStatic(field.getModifiers()))
      throw new NoSuchFieldException("Field '" + paramString + "' is not static"); 
    return field;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\FieldFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */