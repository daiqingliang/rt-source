package java.lang.reflect;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.WeakCache;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

final class WeakCache<K, P, V> extends Object {
  private final ReferenceQueue<K> refQueue = new ReferenceQueue();
  
  private final ConcurrentMap<Object, ConcurrentMap<Object, Supplier<V>>> map = new ConcurrentHashMap();
  
  private final ConcurrentMap<Supplier<V>, Boolean> reverseMap = new ConcurrentHashMap();
  
  private final BiFunction<K, P, ?> subKeyFactory;
  
  private final BiFunction<K, P, V> valueFactory;
  
  public WeakCache(BiFunction<K, P, ?> paramBiFunction1, BiFunction<K, P, V> paramBiFunction2) {
    this.subKeyFactory = (BiFunction)Objects.requireNonNull(paramBiFunction1);
    this.valueFactory = (BiFunction)Objects.requireNonNull(paramBiFunction2);
  }
  
  public V get(K paramK, P paramP) {
    Objects.requireNonNull(paramP);
    expungeStaleEntries();
    Object object1 = CacheKey.valueOf(paramK, this.refQueue);
    ConcurrentMap concurrentMap = (ConcurrentMap)this.map.get(object1);
    if (concurrentMap == null) {
      ConcurrentMap concurrentMap1 = (ConcurrentMap)this.map.putIfAbsent(object1, concurrentMap = new ConcurrentHashMap());
      if (concurrentMap1 != null)
        concurrentMap = concurrentMap1; 
    } 
    Object object2 = Objects.requireNonNull(this.subKeyFactory.apply(paramK, paramP));
    Supplier supplier = (Supplier)concurrentMap.get(object2);
    Factory factory = null;
    while (true) {
      if (supplier != null) {
        Object object = supplier.get();
        if (object != null)
          return (V)object; 
      } 
      if (factory == null)
        factory = new Factory(paramK, paramP, object2, concurrentMap); 
      if (supplier == null) {
        supplier = (Supplier)concurrentMap.putIfAbsent(object2, factory);
        if (supplier == null)
          supplier = factory; 
        continue;
      } 
      if (concurrentMap.replace(object2, supplier, factory)) {
        supplier = factory;
        continue;
      } 
      supplier = (Supplier)concurrentMap.get(object2);
    } 
  }
  
  public boolean containsValue(V paramV) {
    Objects.requireNonNull(paramV);
    expungeStaleEntries();
    return this.reverseMap.containsKey(new LookupValue(paramV));
  }
  
  public int size() {
    expungeStaleEntries();
    return this.reverseMap.size();
  }
  
  private void expungeStaleEntries() {
    CacheKey cacheKey;
    while ((cacheKey = (CacheKey)this.refQueue.poll()) != null)
      cacheKey.expungeFrom(this.map, this.reverseMap); 
  }
  
  private static final class CacheKey<K> extends WeakReference<K> {
    private static final Object NULL_KEY = new Object();
    
    private final int hash;
    
    static <K> Object valueOf(K param1K, ReferenceQueue<K> param1ReferenceQueue) { return (param1K == null) ? NULL_KEY : new CacheKey(param1K, param1ReferenceQueue); }
    
    private CacheKey(K param1K, ReferenceQueue<K> param1ReferenceQueue) {
      super(param1K, param1ReferenceQueue);
      this.hash = System.identityHashCode(param1K);
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      Object object;
      return (param1Object == this || (param1Object != null && param1Object.getClass() == getClass() && (object = get()) != null && object == ((CacheKey)param1Object).get()));
    }
    
    void expungeFrom(ConcurrentMap<?, ? extends ConcurrentMap<?, ?>> param1ConcurrentMap1, ConcurrentMap<?, Boolean> param1ConcurrentMap2) {
      ConcurrentMap concurrentMap = (ConcurrentMap)param1ConcurrentMap1.remove(this);
      if (concurrentMap != null)
        for (Object object : concurrentMap.values())
          param1ConcurrentMap2.remove(object);  
    }
  }
  
  private static final class CacheValue<V> extends WeakReference<V> implements Value<V> {
    private final int hash;
    
    CacheValue(V param1V) {
      super(param1V);
      this.hash = System.identityHashCode(param1V);
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      Object object;
      return (param1Object == this || (param1Object instanceof WeakCache.Value && (object = get()) != null && object == ((WeakCache.Value)param1Object).get()));
    }
  }
  
  private final class Factory extends Object implements Supplier<V> {
    private final K key;
    
    private final P parameter;
    
    private final Object subKey;
    
    private final ConcurrentMap<Object, Supplier<V>> valuesMap;
    
    Factory(K param1K, P param1P, Object param1Object, ConcurrentMap<Object, Supplier<V>> param1ConcurrentMap) {
      this.key = param1K;
      this.parameter = param1P;
      this.subKey = param1Object;
      this.valuesMap = param1ConcurrentMap;
    }
    
    public V get() {
      Supplier supplier = (Supplier)this.valuesMap.get(this.subKey);
      if (supplier != this)
        return null; 
      object = null;
      try {
        object = Objects.requireNonNull(WeakCache.this.valueFactory.apply(this.key, this.parameter));
      } finally {
        if (object == null)
          this.valuesMap.remove(this.subKey, this); 
      } 
      assert object != null;
      WeakCache.CacheValue cacheValue = new WeakCache.CacheValue(object);
      WeakCache.this.reverseMap.put(cacheValue, Boolean.TRUE);
      if (!this.valuesMap.replace(this.subKey, this, cacheValue))
        throw new AssertionError("Should not reach here"); 
      return (V)object;
    }
  }
  
  private static final class LookupValue<V> extends Object implements Value<V> {
    private final V value;
    
    LookupValue(V param1V) { this.value = param1V; }
    
    public V get() { return (V)this.value; }
    
    public int hashCode() { return System.identityHashCode(this.value); }
    
    public boolean equals(Object param1Object) { return (param1Object == this || (param1Object instanceof WeakCache.Value && this.value == ((WeakCache.Value)param1Object).get())); }
  }
  
  private static interface Value<V> extends Supplier<V> {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\WeakCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */