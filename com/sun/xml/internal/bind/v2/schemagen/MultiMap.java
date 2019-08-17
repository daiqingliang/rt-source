package com.sun.xml.internal.bind.v2.schemagen;

import java.util.Map;
import java.util.TreeMap;

final class MultiMap<K extends Comparable<K>, V> extends TreeMap<K, V> {
  private final V many;
  
  public MultiMap(V paramV) { this.many = paramV; }
  
  public V put(K paramK, V paramV) {
    Object object = super.put(paramK, paramV);
    if (object != null && !object.equals(paramV))
      super.put(paramK, this.many); 
    return (V)object;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\MultiMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */