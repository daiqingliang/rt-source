package sun.misc;

import java.util.Dictionary;
import java.util.Enumeration;

public class Cache extends Dictionary {
  private CacheEntry[] table;
  
  private int count;
  
  private int threshold;
  
  private float loadFactor;
  
  private void init(int paramInt, float paramFloat) {
    if (paramInt <= 0 || paramFloat <= 0.0D)
      throw new IllegalArgumentException(); 
    this.loadFactor = paramFloat;
    this.table = new CacheEntry[paramInt];
    this.threshold = (int)(paramInt * paramFloat);
  }
  
  public Cache(int paramInt, float paramFloat) { init(paramInt, paramFloat); }
  
  public Cache(int paramInt) { init(paramInt, 0.75F); }
  
  public Cache() {
    try {
      init(101, 0.75F);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new Error("panic");
    } 
  }
  
  public int size() { return this.count; }
  
  public boolean isEmpty() { return (this.count == 0); }
  
  public Enumeration keys() { return new CacheEnumerator(this.table, true); }
  
  public Enumeration elements() { return new CacheEnumerator(this.table, false); }
  
  public Object get(Object paramObject) {
    CacheEntry[] arrayOfCacheEntry = this.table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfCacheEntry.length;
    for (CacheEntry cacheEntry = arrayOfCacheEntry[j]; cacheEntry != null; cacheEntry = cacheEntry.next) {
      if (cacheEntry.hash == i && cacheEntry.key.equals(paramObject))
        return cacheEntry.check(); 
    } 
    return null;
  }
  
  protected void rehash() {
    int i = this.table.length;
    CacheEntry[] arrayOfCacheEntry1 = this.table;
    int j = i * 2 + 1;
    CacheEntry[] arrayOfCacheEntry2 = new CacheEntry[j];
    this.threshold = (int)(j * this.loadFactor);
    this.table = arrayOfCacheEntry2;
    int k = i;
    while (k-- > 0) {
      CacheEntry cacheEntry = arrayOfCacheEntry1[k];
      while (cacheEntry != null) {
        CacheEntry cacheEntry1 = cacheEntry;
        cacheEntry = cacheEntry.next;
        if (cacheEntry1.check() != null) {
          int m = (cacheEntry1.hash & 0x7FFFFFFF) % j;
          cacheEntry1.next = arrayOfCacheEntry2[m];
          arrayOfCacheEntry2[m] = cacheEntry1;
          continue;
        } 
        this.count--;
      } 
    } 
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    if (paramObject2 == null)
      throw new NullPointerException(); 
    CacheEntry[] arrayOfCacheEntry = this.table;
    int i = paramObject1.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfCacheEntry.length;
    CacheEntry cacheEntry1 = null;
    for (CacheEntry cacheEntry2 = arrayOfCacheEntry[j]; cacheEntry2 != null; cacheEntry2 = cacheEntry2.next) {
      if (cacheEntry2.hash == i && cacheEntry2.key.equals(paramObject1)) {
        Object object = cacheEntry2.check();
        cacheEntry2.setThing(paramObject2);
        return object;
      } 
      if (cacheEntry2.check() == null)
        cacheEntry1 = cacheEntry2; 
    } 
    if (this.count >= this.threshold) {
      rehash();
      return put(paramObject1, paramObject2);
    } 
    if (cacheEntry1 == null) {
      cacheEntry1 = new CacheEntry();
      cacheEntry1.next = arrayOfCacheEntry[j];
      arrayOfCacheEntry[j] = cacheEntry1;
      this.count++;
    } 
    cacheEntry1.hash = i;
    cacheEntry1.key = paramObject1;
    cacheEntry1.setThing(paramObject2);
    return null;
  }
  
  public Object remove(Object paramObject) {
    CacheEntry[] arrayOfCacheEntry = this.table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfCacheEntry.length;
    CacheEntry cacheEntry1 = arrayOfCacheEntry[j];
    CacheEntry cacheEntry2 = null;
    while (cacheEntry1 != null) {
      if (cacheEntry1.hash == i && cacheEntry1.key.equals(paramObject)) {
        if (cacheEntry2 != null) {
          cacheEntry2.next = cacheEntry1.next;
        } else {
          arrayOfCacheEntry[j] = cacheEntry1.next;
        } 
        this.count--;
        return cacheEntry1.check();
      } 
      cacheEntry2 = cacheEntry1;
      cacheEntry1 = cacheEntry1.next;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Cache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */