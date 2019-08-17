package com.sun.xml.internal.bind.v2.util;

import java.util.Map;

public class TypeCast {
  public static <K, V> Map<K, V> checkedCast(Map<?, ?> paramMap, Class<K> paramClass1, Class<V> paramClass2) {
    if (paramMap == null)
      return null; 
    for (Map.Entry entry : paramMap.entrySet()) {
      if (!paramClass1.isInstance(entry.getKey()))
        throw new ClassCastException(entry.getKey().getClass().toString()); 
      if (!paramClass2.isInstance(entry.getValue()))
        throw new ClassCastException(entry.getValue().getClass().toString()); 
    } 
    return paramMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v\\util\TypeCast.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */