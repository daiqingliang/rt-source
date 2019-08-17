package java.util;

import java.io.Serializable;

public abstract class AbstractMap<K, V> extends Object implements Map<K, V> {
  Set<K> keySet;
  
  Collection<V> values;
  
  public int size() { return entrySet().size(); }
  
  public boolean isEmpty() { return (size() == 0); }
  
  public boolean containsValue(Object paramObject) {
    Iterator iterator = entrySet().iterator();
    if (paramObject == null) {
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (entry.getValue() == null)
          return true; 
      } 
    } else {
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (paramObject.equals(entry.getValue()))
          return true; 
      } 
    } 
    return false;
  }
  
  public boolean containsKey(Object paramObject) {
    Iterator iterator = entrySet().iterator();
    if (paramObject == null) {
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (entry.getKey() == null)
          return true; 
      } 
    } else {
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (paramObject.equals(entry.getKey()))
          return true; 
      } 
    } 
    return false;
  }
  
  public V get(Object paramObject) {
    Iterator iterator = entrySet().iterator();
    if (paramObject == null) {
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (entry.getKey() == null)
          return (V)entry.getValue(); 
      } 
    } else {
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry)iterator.next();
        if (paramObject.equals(entry.getKey()))
          return (V)entry.getValue(); 
      } 
    } 
    return null;
  }
  
  public V put(K paramK, V paramV) { throw new UnsupportedOperationException(); }
  
  public V remove(Object paramObject) {
    Iterator iterator = entrySet().iterator();
    Map.Entry entry = null;
    if (paramObject == null) {
      while (entry == null && iterator.hasNext()) {
        Map.Entry entry1 = (Map.Entry)iterator.next();
        if (entry1.getKey() == null)
          entry = entry1; 
      } 
    } else {
      while (entry == null && iterator.hasNext()) {
        Map.Entry entry1 = (Map.Entry)iterator.next();
        if (paramObject.equals(entry1.getKey()))
          entry = entry1; 
      } 
    } 
    Object object = null;
    if (entry != null) {
      object = entry.getValue();
      iterator.remove();
    } 
    return (V)object;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    for (Map.Entry entry : paramMap.entrySet())
      put(entry.getKey(), entry.getValue()); 
  }
  
  public void clear() { entrySet().clear(); }
  
  public Set<K> keySet() {
    Set set = this.keySet;
    if (set == null) {
      set = new AbstractSet<K>() {
          public Iterator<K> iterator() { return new Iterator<K>() {
                private Iterator<Map.Entry<K, V>> i = AbstractMap.null.this.this$0.entrySet().iterator();
                
                public boolean hasNext() { return this.i.hasNext(); }
                
                public K next() { return (K)((Map.Entry)this.i.next()).getKey(); }
                
                public void remove() { this.i.remove(); }
              }; }
          
          public int size() { return AbstractMap.this.size(); }
          
          public boolean isEmpty() { return AbstractMap.this.isEmpty(); }
          
          public void clear() { AbstractMap.this.clear(); }
          
          public boolean contains(Object param1Object) { return AbstractMap.this.containsKey(param1Object); }
        };
      this.keySet = set;
    } 
    return set;
  }
  
  public Collection<V> values() {
    Collection collection = this.values;
    if (collection == null) {
      collection = new AbstractCollection<V>() {
          public Iterator<V> iterator() { return new Iterator<V>() {
                private Iterator<Map.Entry<K, V>> i = AbstractMap.null.this.this$0.entrySet().iterator();
                
                public boolean hasNext() { return this.i.hasNext(); }
                
                public V next() { return (V)((Map.Entry)this.i.next()).getValue(); }
                
                public void remove() { this.i.remove(); }
              }; }
          
          public int size() { return AbstractMap.this.size(); }
          
          public boolean isEmpty() { return AbstractMap.this.isEmpty(); }
          
          public void clear() { AbstractMap.this.clear(); }
          
          public boolean contains(Object param1Object) { return AbstractMap.this.containsValue(param1Object); }
        };
      this.values = collection;
    } 
    return collection;
  }
  
  public abstract Set<Map.Entry<K, V>> entrySet();
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Map))
      return false; 
    Map map = (Map)paramObject;
    if (map.size() != size())
      return false; 
    try {
      for (Map.Entry entry : entrySet()) {
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        if (object2 == null) {
          if (map.get(object1) != null || !map.containsKey(object1))
            return false; 
          continue;
        } 
        if (!object2.equals(map.get(object1)))
          return false; 
      } 
    } catch (ClassCastException classCastException) {
      return false;
    } catch (NullPointerException nullPointerException) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 0;
    Iterator iterator = entrySet().iterator();
    while (iterator.hasNext())
      i += ((Map.Entry)iterator.next()).hashCode(); 
    return i;
  }
  
  public String toString() {
    Iterator iterator = entrySet().iterator();
    if (!iterator.hasNext())
      return "{}"; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append('{');
    while (true) {
      Map.Entry entry = (Map.Entry)iterator.next();
      Object object1 = entry.getKey();
      Object object2 = entry.getValue();
      stringBuilder.append((object1 == this) ? "(this Map)" : object1);
      stringBuilder.append('=');
      stringBuilder.append((object2 == this) ? "(this Map)" : object2);
      if (!iterator.hasNext())
        return stringBuilder.append('}').toString(); 
      stringBuilder.append(',').append(' ');
    } 
  }
  
  protected Object clone() throws CloneNotSupportedException {
    AbstractMap abstractMap = (AbstractMap)super.clone();
    abstractMap.keySet = null;
    abstractMap.values = null;
    return abstractMap;
  }
  
  private static boolean eq(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  public static class SimpleEntry<K, V> extends Object implements Map.Entry<K, V>, Serializable {
    private static final long serialVersionUID = -8499721149061103585L;
    
    private final K key;
    
    private V value;
    
    public SimpleEntry(K param1K, V param1V) {
      this.key = param1K;
      this.value = param1V;
    }
    
    public SimpleEntry(Map.Entry<? extends K, ? extends V> param1Entry) {
      this.key = param1Entry.getKey();
      this.value = param1Entry.getValue();
    }
    
    public K getKey() { return (K)this.key; }
    
    public V getValue() { return (V)this.value; }
    
    public V setValue(V param1V) {
      Object object = this.value;
      this.value = param1V;
      return (V)object;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return (AbstractMap.eq(this.key, entry.getKey()) && AbstractMap.eq(this.value, entry.getValue()));
    }
    
    public int hashCode() { return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode()); }
    
    public String toString() { return this.key + "=" + this.value; }
  }
  
  public static class SimpleImmutableEntry<K, V> extends Object implements Map.Entry<K, V>, Serializable {
    private static final long serialVersionUID = 7138329143949025153L;
    
    private final K key;
    
    private final V value;
    
    public SimpleImmutableEntry(K param1K, V param1V) {
      this.key = param1K;
      this.value = param1V;
    }
    
    public SimpleImmutableEntry(Map.Entry<? extends K, ? extends V> param1Entry) {
      this.key = param1Entry.getKey();
      this.value = param1Entry.getValue();
    }
    
    public K getKey() { return (K)this.key; }
    
    public V getValue() { return (V)this.value; }
    
    public V setValue(V param1V) { throw new UnsupportedOperationException(); }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return (AbstractMap.eq(this.key, entry.getKey()) && AbstractMap.eq(this.value, entry.getValue()));
    }
    
    public int hashCode() { return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode()); }
    
    public String toString() { return this.key + "=" + this.value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\AbstractMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */