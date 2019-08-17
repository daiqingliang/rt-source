package java.beans;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

class NameGenerator {
  private Map<Object, String> valueToName = new IdentityHashMap();
  
  private Map<String, Integer> nameToCount = new HashMap();
  
  public void clear() {
    this.valueToName.clear();
    this.nameToCount.clear();
  }
  
  public static String unqualifiedClassName(Class paramClass) {
    if (paramClass.isArray())
      return unqualifiedClassName(paramClass.getComponentType()) + "Array"; 
    String str = paramClass.getName();
    return str.substring(str.lastIndexOf('.') + 1);
  }
  
  public static String capitalize(String paramString) { return (paramString == null || paramString.length() == 0) ? paramString : (paramString.substring(0, 1).toUpperCase(Locale.ENGLISH) + paramString.substring(1)); }
  
  public String instanceName(Object paramObject) {
    if (paramObject == null)
      return "null"; 
    if (paramObject instanceof Class)
      return unqualifiedClassName((Class)paramObject); 
    String str1 = (String)this.valueToName.get(paramObject);
    if (str1 != null)
      return str1; 
    Class clazz = paramObject.getClass();
    String str2 = unqualifiedClassName(clazz);
    Integer integer = (Integer)this.nameToCount.get(str2);
    byte b = (integer == null) ? 0 : (integer.intValue() + 1);
    this.nameToCount.put(str2, new Integer(b));
    str1 = str2 + b;
    this.valueToName.put(paramObject, str1);
    return str1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\NameGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */