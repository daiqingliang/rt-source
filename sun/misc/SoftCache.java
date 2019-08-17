package sun.misc;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class SoftCache extends AbstractMap implements Map {
  private Map hash;
  
  private ReferenceQueue queue = new ReferenceQueue();
  
  private Set entrySet = null;
  
  private void processQueue() {
    ValueCell valueCell;
    while ((valueCell = (ValueCell)this.queue.poll()) != null) {
      if (valueCell.isValid()) {
        this.hash.remove(valueCell.key);
        continue;
      } 
      dropped--;
    } 
  }
  
  public SoftCache(int paramInt, float paramFloat) { this.hash = new HashMap(paramInt, paramFloat); }
  
  public SoftCache(int paramInt) { this.hash = new HashMap(paramInt); }
  
  public SoftCache() { this.hash = new HashMap(); }
  
  public int size() { return entrySet().size(); }
  
  public boolean isEmpty() { return entrySet().isEmpty(); }
  
  public boolean containsKey(Object paramObject) { return (ValueCell.strip(this.hash.get(paramObject), false) != null); }
  
  protected Object fill(Object paramObject) { return null; }
  
  public Object get(Object paramObject) {
    processQueue();
    Object object = this.hash.get(paramObject);
    if (object == null) {
      object = fill(paramObject);
      if (object != null) {
        this.hash.put(paramObject, ValueCell.create(paramObject, object, this.queue));
        return object;
      } 
    } 
    return ValueCell.strip(object, false);
  }
  
  public Object put(Object paramObject1, Object paramObject2) {
    processQueue();
    ValueCell valueCell;
    return ValueCell.strip(this.hash.put(paramObject1, valueCell), true);
  }
  
  public Object remove(Object paramObject) {
    processQueue();
    return ValueCell.strip(this.hash.remove(paramObject), true);
  }
  
  public void clear() {
    processQueue();
    this.hash.clear();
  }
  
  private static boolean valEquals(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  public Set entrySet() {
    if (this.entrySet == null)
      this.entrySet = new EntrySet(null); 
    return this.entrySet;
  }
  
  private class Entry implements Map.Entry {
    private Map.Entry ent;
    
    private Object value;
    
    Entry(Map.Entry param1Entry, Object param1Object) {
      this.ent = param1Entry;
      this.value = param1Object;
    }
    
    public Object getKey() { return this.ent.getKey(); }
    
    public Object getValue() { return this.value; }
    
    public Object setValue(Object param1Object) { return this.ent.setValue(SoftCache.ValueCell.create(this.ent.getKey(), param1Object, SoftCache.this.queue)); }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return (SoftCache.valEquals(this.ent.getKey(), entry.getKey()) && SoftCache.valEquals(this.value, entry.getValue()));
    }
    
    public int hashCode() {
      Object object;
      return (((object = getKey()) == null) ? 0 : object.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
    }
  }
  
  private class EntrySet extends AbstractSet {
    Set hashEntries = SoftCache.this.hash.entrySet();
    
    private EntrySet() {}
    
    public Iterator iterator() { return new Iterator() {
          Iterator hashIterator = SoftCache.EntrySet.this.hashEntries.iterator();
          
          SoftCache.Entry next = null;
          
          public boolean hasNext() {
            while (this.hashIterator.hasNext()) {
              Map.Entry entry = (Map.Entry)this.hashIterator.next();
              SoftCache.ValueCell valueCell = (SoftCache.ValueCell)entry.getValue();
              Object object = null;
              if (valueCell != null && (object = valueCell.get()) == null)
                continue; 
              this.next = new SoftCache.Entry(SoftCache.EntrySet.this.this$0, entry, object);
              return true;
            } 
            return false;
          }
          
          public Object next() {
            if (this.next == null && !hasNext())
              throw new NoSuchElementException(); 
            SoftCache.Entry entry = this.next;
            this.next = null;
            return entry;
          }
          
          public void remove() { this.hashIterator.remove(); }
        }; }
    
    public boolean isEmpty() { return !iterator().hasNext(); }
    
    public int size() {
      byte b = 0;
      Iterator iterator = iterator();
      while (iterator.hasNext()) {
        b++;
        iterator.next();
      } 
      return b;
    }
    
    public boolean remove(Object param1Object) {
      SoftCache.this.processQueue();
      return (param1Object instanceof SoftCache.Entry) ? this.hashEntries.remove(((SoftCache.Entry)param1Object).ent) : 0;
    }
  }
  
  private static class ValueCell extends SoftReference {
    private static Object INVALID_KEY = new Object();
    
    private static int dropped = 0;
    
    private Object key;
    
    private ValueCell(Object param1Object1, Object param1Object2, ReferenceQueue param1ReferenceQueue) {
      super(param1Object2, param1ReferenceQueue);
      this.key = param1Object1;
    }
    
    private static ValueCell create(Object param1Object1, Object param1Object2, ReferenceQueue param1ReferenceQueue) { return (param1Object2 == null) ? null : new ValueCell(param1Object1, param1Object2, param1ReferenceQueue); }
    
    private static Object strip(Object param1Object, boolean param1Boolean) {
      if (param1Object == null)
        return null; 
      ValueCell valueCell = (ValueCell)param1Object;
      Object object = valueCell.get();
      if (param1Boolean)
        valueCell.drop(); 
      return object;
    }
    
    private boolean isValid() { return (this.key != INVALID_KEY); }
    
    private void drop() {
      clear();
      this.key = INVALID_KEY;
      dropped++;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\SoftCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */