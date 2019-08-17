package com.sun.beans;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public final class WeakCache<K, V> extends Object {
  private final Map<K, Reference<V>> map = new WeakHashMap();
  
  public V get(K paramK) {
    Reference reference = (Reference)this.map.get(paramK);
    if (reference == null)
      return null; 
    Object object = reference.get();
    if (object == null)
      this.map.remove(paramK); 
    return (V)object;
  }
  
  public void put(K paramK, V paramV) {
    if (paramV != null) {
      this.map.put(paramK, new WeakReference(paramV));
    } else {
      this.map.remove(paramK);
    } 
  }
  
  public void clear() { this.map.clear(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\WeakCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */