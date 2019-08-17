package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MultiHashtable<K, V> extends Object {
  static final long serialVersionUID = -6151608290510033572L;
  
  private final Map<K, Set<V>> map = new HashMap();
  
  private boolean modifiable = true;
  
  public Set<V> put(K paramK, V paramV) {
    if (this.modifiable) {
      Set set = (Set)this.map.get(paramK);
      if (set == null) {
        set = new HashSet();
        this.map.put(paramK, set);
      } 
      set.add(paramV);
      return set;
    } 
    throw new UnsupportedOperationException("The MultiHashtable instance is not modifiable.");
  }
  
  public V maps(K paramK, V paramV) {
    if (paramK == null)
      return null; 
    Set set = (Set)this.map.get(paramK);
    if (set != null)
      for (Object object : set) {
        if (object.equals(paramV))
          return (V)object; 
      }  
    return null;
  }
  
  public void makeUnmodifiable() { this.modifiable = false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compile\\util\MultiHashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */