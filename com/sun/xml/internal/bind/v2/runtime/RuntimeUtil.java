package com.sun.xml.internal.bind.v2.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RuntimeUtil {
  public static final Map<Class, Class> boxToPrimitive;
  
  public static final Map<Class, Class> primitiveToBox;
  
  private static String getTypeName(Object paramObject) { return paramObject.getClass().getName(); }
  
  static  {
    HashMap hashMap1 = new HashMap();
    hashMap1.put(byte.class, Byte.class);
    hashMap1.put(short.class, Short.class);
    hashMap1.put(int.class, Integer.class);
    hashMap1.put(long.class, Long.class);
    hashMap1.put(char.class, Character.class);
    hashMap1.put(boolean.class, Boolean.class);
    hashMap1.put(float.class, Float.class);
    hashMap1.put(double.class, Double.class);
    hashMap1.put(void.class, Void.class);
    primitiveToBox = Collections.unmodifiableMap(hashMap1);
    HashMap hashMap2 = new HashMap();
    for (Map.Entry entry : hashMap1.entrySet())
      hashMap2.put(entry.getValue(), entry.getKey()); 
    boxToPrimitive = Collections.unmodifiableMap(hashMap2);
  }
  
  public static final class ToStringAdapter extends XmlAdapter<String, Object> {
    public Object unmarshal(String param1String) { throw new UnsupportedOperationException(); }
    
    public String marshal(Object param1Object) { return (param1Object == null) ? null : param1Object.toString(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\RuntimeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */