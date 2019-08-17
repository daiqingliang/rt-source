package java.util;

public interface SortedMap<K, V> extends Map<K, V> {
  Comparator<? super K> comparator();
  
  SortedMap<K, V> subMap(K paramK1, K paramK2);
  
  SortedMap<K, V> headMap(K paramK);
  
  SortedMap<K, V> tailMap(K paramK);
  
  K firstKey();
  
  K lastKey();
  
  Set<K> keySet();
  
  Collection<V> values();
  
  Set<Map.Entry<K, V>> entrySet();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\SortedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */