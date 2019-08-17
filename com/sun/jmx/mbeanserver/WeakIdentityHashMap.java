package com.sun.jmx.mbeanserver;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;

class WeakIdentityHashMap<K, V> extends Object {
  private Map<WeakReference<K>, V> map = Util.newMap();
  
  private ReferenceQueue<K> refQueue = new ReferenceQueue();
  
  static <K, V> WeakIdentityHashMap<K, V> make() { return new WeakIdentityHashMap(); }
  
  V get(K paramK) {
    expunge();
    WeakReference weakReference = makeReference(paramK);
    return (V)this.map.get(weakReference);
  }
  
  public V put(K paramK, V paramV) {
    expunge();
    if (paramK == null)
      throw new IllegalArgumentException("Null key"); 
    WeakReference weakReference = makeReference(paramK, this.refQueue);
    return (V)this.map.put(weakReference, paramV);
  }
  
  public V remove(K paramK) {
    expunge();
    WeakReference weakReference = makeReference(paramK);
    return (V)this.map.remove(weakReference);
  }
  
  private void expunge() {
    Reference reference;
    while ((reference = this.refQueue.poll()) != null)
      this.map.remove(reference); 
  }
  
  private WeakReference<K> makeReference(K paramK) { return new IdentityWeakReference(paramK); }
  
  private WeakReference<K> makeReference(K paramK, ReferenceQueue<K> paramReferenceQueue) { return new IdentityWeakReference(paramK, paramReferenceQueue); }
  
  private static class IdentityWeakReference<T> extends WeakReference<T> {
    private final int hashCode;
    
    IdentityWeakReference(T param1T) { this(param1T, null); }
    
    IdentityWeakReference(T param1T, ReferenceQueue<T> param1ReferenceQueue) {
      super(param1T, param1ReferenceQueue);
      this.hashCode = (param1T == null) ? 0 : System.identityHashCode(param1T);
    }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (!(param1Object instanceof IdentityWeakReference))
        return false; 
      IdentityWeakReference identityWeakReference = (IdentityWeakReference)param1Object;
      Object object = get();
      return (object != null && object == identityWeakReference.get());
    }
    
    public int hashCode() { return this.hashCode; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\mbeanserver\WeakIdentityHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */