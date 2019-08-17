package com.sun.net.httpserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.Exported;

@Exported
public class Headers extends Object implements Map<String, List<String>> {
  HashMap<String, List<String>> map = new HashMap(32);
  
  private String normalize(String paramString) {
    if (paramString == null)
      return null; 
    int i = paramString.length();
    if (i == 0)
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    if (arrayOfChar[0] >= 'a' && arrayOfChar[0] <= 'z')
      arrayOfChar[0] = (char)(arrayOfChar[0] - ' '); 
    for (byte b = 1; b < i; b++) {
      if (arrayOfChar[b] >= 'A' && arrayOfChar[b] <= 'Z')
        arrayOfChar[b] = (char)(arrayOfChar[b] + ' '); 
    } 
    return new String(arrayOfChar);
  }
  
  public int size() { return this.map.size(); }
  
  public boolean isEmpty() { return this.map.isEmpty(); }
  
  public boolean containsKey(Object paramObject) { return (paramObject == null) ? false : (!(paramObject instanceof String) ? false : this.map.containsKey(normalize((String)paramObject))); }
  
  public boolean containsValue(Object paramObject) { return this.map.containsValue(paramObject); }
  
  public List<String> get(Object paramObject) { return (List)this.map.get(normalize((String)paramObject)); }
  
  public String getFirst(String paramString) {
    List list = (List)this.map.get(normalize(paramString));
    return (list == null) ? null : (String)list.get(0);
  }
  
  public List<String> put(String paramString, List<String> paramList) { return (List)this.map.put(normalize(paramString), paramList); }
  
  public void add(String paramString1, String paramString2) {
    String str = normalize(paramString1);
    List list = (List)this.map.get(str);
    if (list == null) {
      list = new LinkedList();
      this.map.put(str, list);
    } 
    list.add(paramString2);
  }
  
  public void set(String paramString1, String paramString2) {
    LinkedList linkedList = new LinkedList();
    linkedList.add(paramString2);
    put(paramString1, linkedList);
  }
  
  public List<String> remove(Object paramObject) { return (List)this.map.remove(normalize((String)paramObject)); }
  
  public void putAll(Map<? extends String, ? extends List<String>> paramMap) { this.map.putAll(paramMap); }
  
  public void clear() { this.map.clear(); }
  
  public Set<String> keySet() { return this.map.keySet(); }
  
  public Collection<List<String>> values() { return this.map.values(); }
  
  public Set<Map.Entry<String, List<String>>> entrySet() { return this.map.entrySet(); }
  
  public boolean equals(Object paramObject) { return this.map.equals(paramObject); }
  
  public int hashCode() { return this.map.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\Headers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */