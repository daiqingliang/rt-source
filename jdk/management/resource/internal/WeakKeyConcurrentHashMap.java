package jdk.management.resource.internal;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

final class WeakKeyConcurrentHashMap<K, V> extends Object {
  private final ConcurrentHashMap<WeakKey<K>, V> hashmap = new ConcurrentHashMap();
  
  private final ReferenceQueue<K> lastQueue = new ReferenceQueue();
  
  public int size() {
    purgeStaleKeys();
    return this.hashmap.size();
  }
  
  public V get(K paramK) {
    Objects.requireNonNull(paramK, "key");
    purgeStaleKeys();
    WeakKey weakKey = new WeakKey(paramK, null);
    return (V)this.hashmap.get(weakKey);
  }
  
  private boolean containsKey(K paramK) {
    Objects.requireNonNull(paramK, "key");
    WeakKey weakKey = new WeakKey(paramK, null);
    return this.hashmap.containsKey(weakKey);
  }
  
  public V put(K paramK, V paramV) {
    Objects.requireNonNull(paramK, "key");
    purgeStaleKeys();
    WeakKey weakKey = new WeakKey(paramK, this.lastQueue);
    return (V)this.hashmap.put(weakKey, paramV);
  }
  
  public V remove(K paramK) {
    Objects.requireNonNull(paramK, "key");
    purgeStaleKeys();
    WeakKey weakKey = new WeakKey(paramK, null);
    return (V)this.hashmap.remove(weakKey);
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    Objects.requireNonNull(paramK, "key");
    Objects.requireNonNull(paramFunction, "mappingFunction");
    purgeStaleKeys();
    WeakKey weakKey = new WeakKey(paramK, this.lastQueue);
    return (V)this.hashmap.computeIfAbsent(weakKey, paramWeakKey -> paramFunction.apply(paramObject));
  }
  
  public Stream<K> keysForValue(V paramV) { return this.hashmap.entrySet().stream().filter(paramEntry -> (paramEntry.getValue() == paramObject)).map(paramEntry -> ((WeakKey)paramEntry.getKey()).get()).filter(paramObject -> (paramObject != null)); }
  
  public void purgeValue(V paramV) {
    purgeStaleKeys();
    Objects.requireNonNull(paramV, "value");
    this.hashmap.forEach((paramWeakKey, paramObject2) -> {
          if (paramObject1.equals(paramObject2))
            this.hashmap.remove(paramWeakKey, paramObject2); 
        });
  }
  
  private void purgeStaleKeys() {
    Reference reference;
    while ((reference = this.lastQueue.poll()) != null)
      this.hashmap.remove(reference); 
  }
  
  static class WeakKey<K> extends WeakReference<K> {
    private final int hash;
    
    WeakKey(K param1K, ReferenceQueue<K> param1ReferenceQueue) {
      super(param1K, param1ReferenceQueue);
      this.hash = System.identityHashCode(param1K);
    }
    
    public int hashCode() { return this.hash; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (param1Object instanceof WeakKey) {
        Object object = get();
        return (object != null && object == ((WeakKey)param1Object).get());
      } 
      return false;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\WeakKeyConcurrentHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */