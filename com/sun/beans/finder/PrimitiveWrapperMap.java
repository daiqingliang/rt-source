package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

public final class PrimitiveWrapperMap {
  private static final Map<String, Class<?>> map = new HashMap(9);
  
  static void replacePrimitivesWithWrappers(Class<?>[] paramArrayOfClass) {
    for (byte b = 0; b < paramArrayOfClass.length; b++) {
      if (paramArrayOfClass[b] != null && paramArrayOfClass[b].isPrimitive())
        paramArrayOfClass[b] = getType(paramArrayOfClass[b].getName()); 
    } 
  }
  
  public static Class<?> getType(String paramString) { return (Class)map.get(paramString); }
  
  static  {
    map.put(boolean.class.getName(), Boolean.class);
    map.put(char.class.getName(), Character.class);
    map.put(byte.class.getName(), Byte.class);
    map.put(short.class.getName(), Short.class);
    map.put(int.class.getName(), Integer.class);
    map.put(long.class.getName(), Long.class);
    map.put(float.class.getName(), Float.class);
    map.put(double.class.getName(), Double.class);
    map.put(void.class.getName(), Void.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\PrimitiveWrapperMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */