package java.util;

public abstract class Dictionary<K, V> extends Object {
  public abstract int size();
  
  public abstract boolean isEmpty();
  
  public abstract Enumeration<K> keys();
  
  public abstract Enumeration<V> elements();
  
  public abstract V get(Object paramObject);
  
  public abstract V put(K paramK, V paramV);
  
  public abstract V remove(Object paramObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Dictionary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */