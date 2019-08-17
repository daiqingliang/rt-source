package java.io;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ExpiringCache {
  private long millisUntilExpiration;
  
  private Map<String, Entry> map;
  
  private int queryCount;
  
  private int queryOverflow = 300;
  
  private int MAX_ENTRIES = 200;
  
  ExpiringCache() { this(30000L); }
  
  ExpiringCache(long paramLong) {
    this.millisUntilExpiration = paramLong;
    this.map = new LinkedHashMap<String, Entry>() {
        protected boolean removeEldestEntry(Map.Entry<String, ExpiringCache.Entry> param1Entry) { return (size() > ExpiringCache.this.MAX_ENTRIES); }
      };
  }
  
  String get(String paramString) {
    if (++this.queryCount >= this.queryOverflow)
      cleanup(); 
    Entry entry = entryFor(paramString);
    return (entry != null) ? entry.val() : null;
  }
  
  void put(String paramString1, String paramString2) {
    if (++this.queryCount >= this.queryOverflow)
      cleanup(); 
    Entry entry = entryFor(paramString1);
    if (entry != null) {
      entry.setTimestamp(System.currentTimeMillis());
      entry.setVal(paramString2);
    } else {
      this.map.put(paramString1, new Entry(System.currentTimeMillis(), paramString2));
    } 
  }
  
  void clear() { this.map.clear(); }
  
  private Entry entryFor(String paramString) {
    Entry entry = (Entry)this.map.get(paramString);
    if (entry != null) {
      long l = System.currentTimeMillis() - entry.timestamp();
      if (l < 0L || l >= this.millisUntilExpiration) {
        this.map.remove(paramString);
        entry = null;
      } 
    } 
    return entry;
  }
  
  private void cleanup() {
    Set set = this.map.keySet();
    String[] arrayOfString = new String[set.size()];
    byte b1 = 0;
    for (String str : set)
      arrayOfString[b1++] = str; 
    for (byte b2 = 0; b2 < arrayOfString.length; b2++)
      entryFor(arrayOfString[b2]); 
    this.queryCount = 0;
  }
  
  static class Entry {
    private long timestamp;
    
    private String val;
    
    Entry(long param1Long, String param1String) {
      this.timestamp = param1Long;
      this.val = param1String;
    }
    
    long timestamp() { return this.timestamp; }
    
    void setTimestamp(long param1Long) { this.timestamp = param1Long; }
    
    String val() { return this.val; }
    
    void setVal(String param1String) { this.val = param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ExpiringCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */