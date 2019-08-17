package sun.security.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class MemoryCache<K, V> extends Cache<K, V> {
  private static final float LOAD_FACTOR = 0.75F;
  
  private static final boolean DEBUG = false;
  
  private final Map<K, CacheEntry<K, V>> cacheMap;
  
  private int maxSize;
  
  private long lifetime;
  
  private final ReferenceQueue<V> queue;
  
  public MemoryCache(boolean paramBoolean, int paramInt) { this(paramBoolean, paramInt, 0); }
  
  public MemoryCache(boolean paramBoolean, int paramInt1, int paramInt2) {
    this.maxSize = paramInt1;
    this.lifetime = (paramInt2 * 1000);
    if (paramBoolean) {
      this.queue = new ReferenceQueue();
    } else {
      this.queue = null;
    } 
    int i = (int)(paramInt1 / 0.75F) + 1;
    this.cacheMap = new LinkedHashMap(i, 0.75F, true);
  }
  
  private void emptyQueue() {
    if (this.queue == null)
      return; 
    int i = this.cacheMap.size();
    while (true) {
      CacheEntry cacheEntry1 = (CacheEntry)this.queue.poll();
      if (cacheEntry1 == null)
        break; 
      Object object = cacheEntry1.getKey();
      if (object == null)
        continue; 
      CacheEntry cacheEntry2 = (CacheEntry)this.cacheMap.remove(object);
      if (cacheEntry2 != null && cacheEntry1 != cacheEntry2)
        this.cacheMap.put(object, cacheEntry2); 
    } 
  }
  
  private void expungeExpiredEntries() {
    emptyQueue();
    if (this.lifetime == 0L)
      return; 
    byte b = 0;
    long l = System.currentTimeMillis();
    Iterator iterator = this.cacheMap.values().iterator();
    while (iterator.hasNext()) {
      CacheEntry cacheEntry = (CacheEntry)iterator.next();
      if (!cacheEntry.isValid(l)) {
        iterator.remove();
        b++;
      } 
    } 
  }
  
  public int size() {
    expungeExpiredEntries();
    return this.cacheMap.size();
  }
  
  public void clear() {
    if (this.queue != null) {
      for (CacheEntry cacheEntry : this.cacheMap.values())
        cacheEntry.invalidate(); 
      while (this.queue.poll() != null);
    } 
    this.cacheMap.clear();
  }
  
  public void put(K paramK, V paramV) {
    emptyQueue();
    long l = (this.lifetime == 0L) ? 0L : (System.currentTimeMillis() + this.lifetime);
    CacheEntry cacheEntry1 = newEntry(paramK, paramV, l, this.queue);
    CacheEntry cacheEntry2 = (CacheEntry)this.cacheMap.put(paramK, cacheEntry1);
    if (cacheEntry2 != null) {
      cacheEntry2.invalidate();
      return;
    } 
    if (this.maxSize > 0 && this.cacheMap.size() > this.maxSize) {
      expungeExpiredEntries();
      if (this.cacheMap.size() > this.maxSize) {
        Iterator iterator = this.cacheMap.values().iterator();
        CacheEntry cacheEntry = (CacheEntry)iterator.next();
        iterator.remove();
        cacheEntry.invalidate();
      } 
    } 
  }
  
  public V get(Object paramObject) {
    emptyQueue();
    CacheEntry cacheEntry = (CacheEntry)this.cacheMap.get(paramObject);
    if (cacheEntry == null)
      return null; 
    long l = (this.lifetime == 0L) ? 0L : System.currentTimeMillis();
    if (!cacheEntry.isValid(l)) {
      this.cacheMap.remove(paramObject);
      return null;
    } 
    return (V)cacheEntry.getValue();
  }
  
  public void remove(Object paramObject) {
    emptyQueue();
    CacheEntry cacheEntry = (CacheEntry)this.cacheMap.remove(paramObject);
    if (cacheEntry != null)
      cacheEntry.invalidate(); 
  }
  
  public void setCapacity(int paramInt) {
    expungeExpiredEntries();
    if (paramInt > 0 && this.cacheMap.size() > paramInt) {
      Iterator iterator = this.cacheMap.values().iterator();
      for (int i = this.cacheMap.size() - paramInt; i > 0; i--) {
        CacheEntry cacheEntry = (CacheEntry)iterator.next();
        iterator.remove();
        cacheEntry.invalidate();
      } 
    } 
    this.maxSize = (paramInt > 0) ? paramInt : 0;
  }
  
  public void setTimeout(int paramInt) {
    emptyQueue();
    this.lifetime = (paramInt > 0) ? (paramInt * 1000L) : 0L;
  }
  
  public void accept(Cache.CacheVisitor<K, V> paramCacheVisitor) {
    expungeExpiredEntries();
    Map map = getCachedEntries();
    paramCacheVisitor.visit(map);
  }
  
  private Map<K, V> getCachedEntries() {
    HashMap hashMap = new HashMap(this.cacheMap.size());
    for (CacheEntry cacheEntry : this.cacheMap.values())
      hashMap.put(cacheEntry.getKey(), cacheEntry.getValue()); 
    return hashMap;
  }
  
  protected CacheEntry<K, V> newEntry(K paramK, V paramV, long paramLong, ReferenceQueue<V> paramReferenceQueue) { return (paramReferenceQueue != null) ? new SoftCacheEntry(paramK, paramV, paramLong, paramReferenceQueue) : new HardCacheEntry(paramK, paramV, paramLong); }
  
  private static interface CacheEntry<K, V> {
    boolean isValid(long param1Long);
    
    void invalidate();
    
    K getKey();
    
    V getValue();
  }
  
  private static class HardCacheEntry<K, V> extends Object implements CacheEntry<K, V> {
    private K key;
    
    private V value;
    
    private long expirationTime;
    
    HardCacheEntry(K param1K, V param1V, long param1Long) {
      this.key = param1K;
      this.value = param1V;
      this.expirationTime = param1Long;
    }
    
    public K getKey() { return (K)this.key; }
    
    public V getValue() { return (V)this.value; }
    
    public boolean isValid(long param1Long) {
      boolean bool = (param1Long <= this.expirationTime);
      if (!bool)
        invalidate(); 
      return bool;
    }
    
    public void invalidate() {
      this.key = null;
      this.value = null;
      this.expirationTime = -1L;
    }
  }
  
  private static class SoftCacheEntry<K, V> extends SoftReference<V> implements CacheEntry<K, V> {
    private K key;
    
    private long expirationTime;
    
    SoftCacheEntry(K param1K, V param1V, long param1Long, ReferenceQueue<V> param1ReferenceQueue) {
      super(param1V, param1ReferenceQueue);
      this.key = param1K;
      this.expirationTime = param1Long;
    }
    
    public K getKey() { return (K)this.key; }
    
    public V getValue() { return (V)get(); }
    
    public boolean isValid(long param1Long) {
      boolean bool = (param1Long <= this.expirationTime && get() != null);
      if (!bool)
        invalidate(); 
      return bool;
    }
    
    public void invalidate() {
      clear();
      this.key = null;
      this.expirationTime = -1L;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */