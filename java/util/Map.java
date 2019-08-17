package java.util;

import java.lang.invoke.SerializedLambda;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Map<K, V> {
  int size();
  
  boolean isEmpty();
  
  boolean containsKey(Object paramObject);
  
  boolean containsValue(Object paramObject);
  
  V get(Object paramObject);
  
  V put(K paramK, V paramV);
  
  V remove(Object paramObject);
  
  void putAll(Map<? extends K, ? extends V> paramMap);
  
  void clear();
  
  Set<K> keySet();
  
  Collection<V> values();
  
  Set<Entry<K, V>> entrySet();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  default V getOrDefault(Object paramObject, V paramV) {
    Object object;
    return (V)(((object = get(paramObject)) != null || containsKey(paramObject)) ? object : paramV);
  }
  
  default void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    for (Entry entry : entrySet()) {
      Object object2;
      Object object1;
      try {
        object1 = entry.getKey();
        object2 = entry.getValue();
      } catch (IllegalStateException illegalStateException) {
        throw new ConcurrentModificationException(illegalStateException);
      } 
      paramBiConsumer.accept(object1, object2);
    } 
  }
  
  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    for (Entry entry : entrySet()) {
      Object object1;
      try {
        object1 = entry.getKey();
        object2 = entry.getValue();
      } catch (IllegalStateException illegalStateException) {
        throw new ConcurrentModificationException(illegalStateException);
      } 
      Object object2 = paramBiFunction.apply(object1, object2);
      try {
        entry.setValue(object2);
      } catch (IllegalStateException illegalStateException) {
        throw new ConcurrentModificationException(illegalStateException);
      } 
    } 
  }
  
  default V putIfAbsent(K paramK, V paramV) {
    Object object = get(paramK);
    if (object == null)
      object = put(paramK, paramV); 
    return (V)object;
  }
  
  default boolean remove(Object paramObject1, Object paramObject2) {
    Object object = get(paramObject1);
    if (!Objects.equals(object, paramObject2) || (object == null && !containsKey(paramObject1)))
      return false; 
    remove(paramObject1);
    return true;
  }
  
  default boolean replace(K paramK, V paramV1, V paramV2) {
    Object object = get(paramK);
    if (!Objects.equals(object, paramV1) || (object == null && !containsKey(paramK)))
      return false; 
    put(paramK, paramV2);
    return true;
  }
  
  default V replace(K paramK, V paramV) {
    Object object;
    if ((object = get(paramK)) != null || containsKey(paramK))
      object = put(paramK, paramV); 
    return (V)object;
  }
  
  default V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    Objects.requireNonNull(paramFunction);
    Object object1;
    Object object2;
    if ((object1 = get(paramK)) == null && (object2 = paramFunction.apply(paramK)) != null) {
      put(paramK, object2);
      return (V)object2;
    } 
    return (V)object1;
  }
  
  default V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Object object;
    if ((object = get(paramK)) != null) {
      Object object1 = paramBiFunction.apply(paramK, object);
      if (object1 != null) {
        put(paramK, object1);
        return (V)object1;
      } 
      remove(paramK);
      return null;
    } 
    return null;
  }
  
  default V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Object object1 = get(paramK);
    Object object2 = paramBiFunction.apply(paramK, object1);
    if (object2 == null) {
      if (object1 != null || containsKey(paramK)) {
        remove(paramK);
        return null;
      } 
      return null;
    } 
    put(paramK, object2);
    return (V)object2;
  }
  
  default V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Objects.requireNonNull(paramV);
    Object object = get(paramK);
    V v = (object == null) ? paramV : paramBiFunction.apply(object, paramV);
    if (v == null) {
      remove(paramK);
    } else {
      put(paramK, v);
    } 
    return v;
  }
  
  public static interface Entry<K, V> {
    K getKey();
    
    V getValue();
    
    V setValue(V param1V);
    
    boolean equals(Object param1Object);
    
    int hashCode();
    
    static <K extends Comparable<? super K>, V> Comparator<Entry<K, V>> comparingByKey() { return (Comparator)((param1Entry1, param1Entry2) -> ((Comparable)param1Entry1.getKey()).compareTo(param1Entry2.getKey())); }
    
    static <K, V extends Comparable<? super V>> Comparator<Entry<K, V>> comparingByValue() { return (Comparator)((param1Entry1, param1Entry2) -> ((Comparable)param1Entry1.getValue()).compareTo(param1Entry2.getValue())); }
    
    static <K, V> Comparator<Entry<K, V>> comparingByKey(Comparator<? super K> param1Comparator) {
      Objects.requireNonNull(param1Comparator);
      return (Comparator)((param1Entry1, param1Entry2) -> param1Comparator.compare(param1Entry1.getKey(), param1Entry2.getKey()));
    }
    
    static <K, V> Comparator<Entry<K, V>> comparingByValue(Comparator<? super V> param1Comparator) {
      Objects.requireNonNull(param1Comparator);
      return (Comparator)((param1Entry1, param1Entry2) -> param1Comparator.compare(param1Entry1.getValue(), param1Entry2.getValue()));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Map.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */