package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

class UnmodifiableHeaders extends Headers {
  Headers map;
  
  UnmodifiableHeaders(Headers paramHeaders) { this.map = paramHeaders; }
  
  public int size() { return this.map.size(); }
  
  public boolean isEmpty() { return this.map.isEmpty(); }
  
  public boolean containsKey(Object paramObject) { return this.map.containsKey(paramObject); }
  
  public boolean containsValue(Object paramObject) { return this.map.containsValue(paramObject); }
  
  public List<String> get(Object paramObject) { return this.map.get(paramObject); }
  
  public String getFirst(String paramString) { return this.map.getFirst(paramString); }
  
  public List<String> put(String paramString, List<String> paramList) { return this.map.put(paramString, paramList); }
  
  public void add(String paramString1, String paramString2) { throw new UnsupportedOperationException("unsupported operation"); }
  
  public void set(String paramString1, String paramString2) { throw new UnsupportedOperationException("unsupported operation"); }
  
  public List<String> remove(Object paramObject) { throw new UnsupportedOperationException("unsupported operation"); }
  
  public void putAll(Map<? extends String, ? extends List<String>> paramMap) { throw new UnsupportedOperationException("unsupported operation"); }
  
  public void clear() { throw new UnsupportedOperationException("unsupported operation"); }
  
  public Set<String> keySet() { return Collections.unmodifiableSet(this.map.keySet()); }
  
  public Collection<List<String>> values() { return Collections.unmodifiableCollection(this.map.values()); }
  
  public Set<Map.Entry<String, List<String>>> entrySet() { return Collections.unmodifiableSet(this.map.entrySet()); }
  
  public boolean equals(Object paramObject) { return this.map.equals(paramObject); }
  
  public int hashCode() { return this.map.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\UnmodifiableHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */