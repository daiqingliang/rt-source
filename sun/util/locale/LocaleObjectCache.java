package sun.util.locale;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class LocaleObjectCache<K, V> extends Object {
  private ConcurrentMap<K, CacheEntry<K, V>> map;
  
  private ReferenceQueue<V> queue = new ReferenceQueue();
  
  public LocaleObjectCache() { this(16, 0.75F, 16); }
  
  public LocaleObjectCache(int paramInt1, float paramFloat, int paramInt2) { this.map = new ConcurrentHashMap(paramInt1, paramFloat, paramInt2); }
  
  public V get(K paramK) {
    Object object = null;
    cleanStaleEntries();
    CacheEntry cacheEntry = (CacheEntry)this.map.get(paramK);
    if (cacheEntry != null)
      object = cacheEntry.get(); 
    if (object == null) {
      Object object1 = createObject(paramK);
      paramK = (K)normalizeKey(paramK);
      if (paramK == null || object1 == null)
        return null; 
      CacheEntry cacheEntry1 = new CacheEntry(paramK, object1, this.queue);
      cacheEntry = (CacheEntry)this.map.putIfAbsent(paramK, cacheEntry1);
      if (cacheEntry == null) {
        object = object1;
      } else {
        object = cacheEntry.get();
        if (object == null) {
          this.map.put(paramK, cacheEntry1);
          object = object1;
        } 
      } 
    } 
    return (V)object;
  }
  
  protected V put(K paramK, V paramV) {
    CacheEntry cacheEntry1 = new CacheEntry(paramK, paramV, this.queue);
    CacheEntry cacheEntry2 = (CacheEntry)this.map.put(paramK, cacheEntry1);
    return (V)((cacheEntry2 == null) ? null : cacheEntry2.get());
  }
  
  private void cleanStaleEntries() {
    CacheEntry cacheEntry;
    while ((cacheEntry = (CacheEntry)this.queue.poll()) != null)
      this.map.remove(cacheEntry.getKey()); 
  }
  
  protected abstract V createObject(K paramK);
  
  protected K normalizeKey(K paramK) { return paramK; }
  
  private static class CacheEntry<K, V> extends SoftReference<V> {
    private K key;
    
    CacheEntry(K param1K, V param1V, ReferenceQueue<V> param1ReferenceQueue) {
      super(param1V, param1ReferenceQueue);
      this.key = param1K;
    }
    
    K getKey() { return (K)this.key; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\LocaleObjectCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */