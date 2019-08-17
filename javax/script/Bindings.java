package javax.script;

import java.util.Map;

public interface Bindings extends Map<String, Object> {
  Object put(String paramString, Object paramObject);
  
  void putAll(Map<? extends String, ? extends Object> paramMap);
  
  boolean containsKey(Object paramObject);
  
  Object get(Object paramObject);
  
  Object remove(Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\Bindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */