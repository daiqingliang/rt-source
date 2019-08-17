package sun.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class PreHashedMap<V> extends AbstractMap<String, V> {
  private final int rows;
  
  private final int size;
  
  private final int shift;
  
  private final int mask;
  
  private final Object[] ht;
  
  protected PreHashedMap(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.rows = paramInt1;
    this.size = paramInt2;
    this.shift = paramInt3;
    this.mask = paramInt4;
    this.ht = new Object[paramInt1];
    init(this.ht);
  }
  
  protected abstract void init(Object[] paramArrayOfObject);
  
  private V toV(Object paramObject) { return (V)paramObject; }
  
  public V get(Object paramObject) {
    int i = paramObject.hashCode() >> this.shift & this.mask;
    Object[] arrayOfObject = (Object[])this.ht[i];
    if (arrayOfObject == null)
      return null; 
    while (true) {
      if (arrayOfObject[0].equals(paramObject))
        return (V)toV(arrayOfObject[1]); 
      if (arrayOfObject.length < 3)
        return null; 
      arrayOfObject = (Object[])arrayOfObject[2];
    } 
  }
  
  public V put(String paramString, V paramV) {
    int i = paramString.hashCode() >> this.shift & this.mask;
    Object[] arrayOfObject = (Object[])this.ht[i];
    if (arrayOfObject == null)
      throw new UnsupportedOperationException(paramString); 
    while (true) {
      if (arrayOfObject[0].equals(paramString)) {
        Object object = toV(arrayOfObject[1]);
        arrayOfObject[1] = paramV;
        return (V)object;
      } 
      if (arrayOfObject.length < 3)
        throw new UnsupportedOperationException(paramString); 
      arrayOfObject = (Object[])arrayOfObject[2];
    } 
  }
  
  public Set<String> keySet() { return new AbstractSet<String>() {
        public int size() { return PreHashedMap.this.size; }
        
        public Iterator<String> iterator() { return new Iterator<String>() {
              private int i = -1;
              
              Object[] a = null;
              
              String cur = null;
              
              private boolean findNext() {
                if (this.a != null) {
                  if (this.a.length == 3) {
                    this.a = (Object[])this.a[2];
                    this.cur = (String)this.a[0];
                    return true;
                  } 
                  this.i++;
                  this.a = null;
                } 
                this.cur = null;
                if (this.i >= PreHashedMap.null.this.this$0.rows)
                  return false; 
                if (this.i < 0 || PreHashedMap.null.this.this$0.ht[this.i] == null)
                  do {
                    if (++this.i >= PreHashedMap.null.this.this$0.rows)
                      return false; 
                  } while (PreHashedMap.null.this.this$0.ht[this.i] == null); 
                this.a = (Object[])PreHashedMap.null.this.this$0.ht[this.i];
                this.cur = (String)this.a[0];
                return true;
              }
              
              public boolean hasNext() { return (this.cur != null) ? true : findNext(); }
              
              public String next() {
                if (this.cur == null && !findNext())
                  throw new NoSuchElementException(); 
                String str = this.cur;
                this.cur = null;
                return str;
              }
              
              public void remove() { throw new UnsupportedOperationException(); }
            }; }
      }; }
  
  public Set<Map.Entry<String, V>> entrySet() { return new AbstractSet<Map.Entry<String, V>>() {
        public int size() { return PreHashedMap.this.size; }
        
        public Iterator<Map.Entry<String, V>> iterator() { return new Iterator<Map.Entry<String, V>>() {
              final Iterator<String> i = PreHashedMap.null.this.this$0.keySet().iterator();
              
              public boolean hasNext() { return this.i.hasNext(); }
              
              public Map.Entry<String, V> next() { return new Map.Entry<String, V>() {
                    String k = (String)PreHashedMap.null.null.this.i.next();
                    
                    public String getKey() { return this.k; }
                    
                    public V getValue() { return (V)PreHashedMap.null.this.this$0.get(this.k); }
                    
                    public int hashCode() {
                      Object object = PreHashedMap.null.this.this$0.get(this.k);
                      return this.k.hashCode() + ((object == null) ? 0 : object.hashCode());
                    }
                    
                    public boolean equals(Object param3Object) {
                      if (param3Object == this)
                        return true; 
                      if (!(param3Object instanceof Entry))
                        return false; 
                      Entry entry = (Entry)param3Object;
                      return (((getKey() == null) ? (entry.getKey() == null) : getKey().equals(entry.getKey())) && ((getValue() == null) ? (entry.getValue() == null) : getValue().equals(entry.getValue())));
                    }
                    
                    public V setValue(V param3V) { throw new UnsupportedOperationException(); }
                  }; }
              
              public void remove() { throw new UnsupportedOperationException(); }
            }; }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\PreHashedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */