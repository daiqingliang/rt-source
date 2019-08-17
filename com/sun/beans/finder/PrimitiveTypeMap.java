package com.sun.beans.finder;

import java.util.HashMap;
import java.util.Map;

final class PrimitiveTypeMap {
  private static final Map<String, Class<?>> map = new HashMap(9);
  
  static Class<?> getType(String paramString) { return (Class)map.get(paramString); }
  
  static  {
    map.put(boolean.class.getName(), boolean.class);
    map.put(char.class.getName(), char.class);
    map.put(byte.class.getName(), byte.class);
    map.put(short.class.getName(), short.class);
    map.put(int.class.getName(), int.class);
    map.put(long.class.getName(), long.class);
    map.put(float.class.getName(), float.class);
    map.put(double.class.getName(), double.class);
    map.put(void.class.getName(), void.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\finder\PrimitiveTypeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */