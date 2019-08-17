package java.util.concurrent;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ConcurrentMap<K, V> extends Map<K, V> {
  default V getOrDefault(Object paramObject, V paramV) {
    Object object;
    return (V)(((object = get(paramObject)) != null) ? object : paramV);
  }
  
  default void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    for (Map.Entry entry : entrySet()) {
      Object object2;
      Object object1;
      try {
        object1 = entry.getKey();
        object2 = entry.getValue();
      } catch (IllegalStateException illegalStateException) {
        continue;
      } 
      paramBiConsumer.accept(object1, object2);
    } 
  }
  
  V putIfAbsent(K paramK, V paramV);
  
  boolean remove(Object paramObject1, Object paramObject2);
  
  boolean replace(K paramK, V paramV1, V paramV2);
  
  V replace(K paramK, V paramV);
  
  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    forEach((paramObject1, paramObject2) -> {
          do {
          
          } while (!replace(paramObject1, paramObject2, paramBiFunction.apply(paramObject1, paramObject2)) && (paramObject2 = get(paramObject1)) != null);
        });
  }
  
  default V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    Objects.requireNonNull(paramFunction);
    Object object1;
    Object object2;
    return (V)(((object1 = get(paramK)) == null && (object2 = paramFunction.apply(paramK)) != null && (object1 = putIfAbsent(paramK, object2)) == null) ? object2 : object1);
  }
  
  default V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Object object;
    while ((object = get(paramK)) != null) {
      Object object1 = paramBiFunction.apply(paramK, object);
      if (object1 != null) {
        if (replace(paramK, object, object1))
          return (V)object1; 
        continue;
      } 
      if (remove(paramK, object))
        return null; 
    } 
    return (V)object;
  }
  
  default V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Object object2;
    Objects.requireNonNull(paramBiFunction);
    Object object1 = get(paramK);
    while (true) {
      object2 = paramBiFunction.apply(paramK, object1);
      if (object2 == null) {
        if (object1 != null || containsKey(paramK)) {
          if (remove(paramK, object1))
            return null; 
          object1 = get(paramK);
          continue;
        } 
        return null;
      } 
      if (object1 != null) {
        if (replace(paramK, object1, object2))
          return (V)object2; 
        object1 = get(paramK);
        continue;
      } 
      if ((object1 = putIfAbsent(paramK, object2)) == null)
        break; 
    } 
    return (V)object2;
  }
  
  default V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Objects.requireNonNull(paramV);
    Object object = get(paramK);
    do {
      while (object != null) {
        Object object1 = paramBiFunction.apply(object, paramV);
        if (object1 != null) {
          if (replace(paramK, object, object1))
            return (V)object1; 
        } else if (remove(paramK, object)) {
          return null;
        } 
        object = get(paramK);
      } 
    } while ((object = putIfAbsent(paramK, paramV)) != null);
    return paramV;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ConcurrentMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */