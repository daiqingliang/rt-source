package java.util;

public interface NavigableMap<K, V> extends SortedMap<K, V> {
  Map.Entry<K, V> lowerEntry(K paramK);
  
  K lowerKey(K paramK);
  
  Map.Entry<K, V> floorEntry(K paramK);
  
  K floorKey(K paramK);
  
  Map.Entry<K, V> ceilingEntry(K paramK);
  
  K ceilingKey(K paramK);
  
  Map.Entry<K, V> higherEntry(K paramK);
  
  K higherKey(K paramK);
  
  Map.Entry<K, V> firstEntry();
  
  Map.Entry<K, V> lastEntry();
  
  Map.Entry<K, V> pollFirstEntry();
  
  Map.Entry<K, V> pollLastEntry();
  
  NavigableMap<K, V> descendingMap();
  
  NavigableSet<K> navigableKeySet();
  
  NavigableSet<K> descendingKeySet();
  
  NavigableMap<K, V> subMap(K paramK1, boolean paramBoolean1, K paramK2, boolean paramBoolean2);
  
  NavigableMap<K, V> headMap(K paramK, boolean paramBoolean);
  
  NavigableMap<K, V> tailMap(K paramK, boolean paramBoolean);
  
  SortedMap<K, V> subMap(K paramK1, K paramK2);
  
  SortedMap<K, V> headMap(K paramK);
  
  SortedMap<K, V> tailMap(K paramK);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\NavigableMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */