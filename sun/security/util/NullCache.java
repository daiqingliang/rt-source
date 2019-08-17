package sun.security.util;

class NullCache<K, V> extends Cache<K, V> {
  static final Cache<Object, Object> INSTANCE = new NullCache();
  
  public int size() { return 0; }
  
  public void clear() {}
  
  public void put(K paramK, V paramV) {}
  
  public V get(Object paramObject) { return null; }
  
  public void remove(Object paramObject) {}
  
  public void setCapacity(int paramInt) {}
  
  public void setTimeout(int paramInt) {}
  
  public void accept(Cache.CacheVisitor<K, V> paramCacheVisitor) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\NullCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */