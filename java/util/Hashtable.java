package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import sun.misc.SharedSecrets;

public class Hashtable<K, V> extends Dictionary<K, V> implements Map<K, V>, Cloneable, Serializable {
  private Entry<?, ?>[] table;
  
  private int count;
  
  private int threshold;
  
  private float loadFactor;
  
  private int modCount = 0;
  
  private static final long serialVersionUID = 1421746759512286392L;
  
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  private static final int KEYS = 0;
  
  private static final int VALUES = 1;
  
  private static final int ENTRIES = 2;
  
  public Hashtable(int paramInt, float paramFloat) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt); 
    if (paramFloat <= 0.0F || Float.isNaN(paramFloat))
      throw new IllegalArgumentException("Illegal Load: " + paramFloat); 
    if (paramInt == 0)
      paramInt = 1; 
    this.loadFactor = paramFloat;
    this.table = new Entry[paramInt];
    this.threshold = (int)Math.min(paramInt * paramFloat, 2.14748365E9F);
  }
  
  public Hashtable(int paramInt) { this(paramInt, 0.75F); }
  
  public Hashtable() { this(11, 0.75F); }
  
  public Hashtable(Map<? extends K, ? extends V> paramMap) {
    this(Math.max(2 * paramMap.size(), 11), 0.75F);
    putAll(paramMap);
  }
  
  public int size() { return this.count; }
  
  public boolean isEmpty() { return (this.count == 0); }
  
  public Enumeration<K> keys() { return getEnumeration(0); }
  
  public Enumeration<V> elements() { return getEnumeration(1); }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    Entry[] arrayOfEntry = this.table;
    int i = arrayOfEntry.length;
    while (i-- > 0) {
      for (Entry entry = arrayOfEntry[i]; entry != null; entry = entry.next) {
        if (entry.value.equals(paramObject))
          return true; 
      } 
    } 
    return false;
  }
  
  public boolean containsValue(Object paramObject) { return contains(paramObject); }
  
  public boolean containsKey(Object paramObject) {
    Entry[] arrayOfEntry = this.table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramObject))
        return true; 
    } 
    return false;
  }
  
  public V get(Object paramObject) {
    Entry[] arrayOfEntry = this.table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramObject))
        return (V)entry.value; 
    } 
    return null;
  }
  
  protected void rehash() {
    int i = this.table.length;
    Entry[] arrayOfEntry1 = this.table;
    int j = (i << 1) + 1;
    if (j - 2147483639 > 0) {
      if (i == 2147483639)
        return; 
      j = 2147483639;
    } 
    Entry[] arrayOfEntry2 = new Entry[j];
    this.modCount++;
    this.threshold = (int)Math.min(j * this.loadFactor, 2.14748365E9F);
    this.table = arrayOfEntry2;
    int k = i;
    while (k-- > 0) {
      Entry entry = arrayOfEntry1[k];
      while (entry != null) {
        Entry entry1 = entry;
        entry = entry.next;
        int m = (entry1.hash & 0x7FFFFFFF) % j;
        entry1.next = arrayOfEntry2[m];
        arrayOfEntry2[m] = entry1;
      } 
    } 
  }
  
  private void addEntry(int paramInt1, K paramK, V paramV, int paramInt2) {
    this.modCount++;
    Entry[] arrayOfEntry = this.table;
    if (this.count >= this.threshold) {
      rehash();
      arrayOfEntry = this.table;
      paramInt1 = paramK.hashCode();
      paramInt2 = (paramInt1 & 0x7FFFFFFF) % arrayOfEntry.length;
    } 
    Entry entry = arrayOfEntry[paramInt2];
    arrayOfEntry[paramInt2] = new Entry(paramInt1, paramK, paramV, entry);
    this.count++;
  }
  
  public V put(K paramK, V paramV) {
    if (paramV == null)
      throw new NullPointerException(); 
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramK)) {
        Object object = entry.value;
        entry.value = paramV;
        return (V)object;
      } 
    } 
    addEntry(i, paramK, paramV, j);
    return null;
  }
  
  public V remove(Object paramObject) {
    Entry[] arrayOfEntry = this.table;
    int i = paramObject.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry entry1 = arrayOfEntry[j];
    Entry entry2 = null;
    while (entry1 != null) {
      if (entry1.hash == i && entry1.key.equals(paramObject)) {
        this.modCount++;
        if (entry2 != null) {
          entry2.next = entry1.next;
        } else {
          arrayOfEntry[j] = entry1.next;
        } 
        this.count--;
        Object object = entry1.value;
        entry1.value = null;
        return (V)object;
      } 
      entry2 = entry1;
      entry1 = entry1.next;
    } 
    return null;
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    for (Map.Entry entry : paramMap.entrySet())
      put(entry.getKey(), entry.getValue()); 
  }
  
  public void clear() {
    Entry[] arrayOfEntry = this.table;
    this.modCount++;
    int i = arrayOfEntry.length;
    while (--i >= 0)
      arrayOfEntry[i] = null; 
    this.count = 0;
  }
  
  public Object clone() {
    try {
      Hashtable hashtable = (Hashtable)super.clone();
      hashtable.table = new Entry[this.table.length];
      int i = this.table.length;
      while (i-- > 0)
        hashtable.table[i] = (this.table[i] != null) ? (Entry)this.table[i].clone() : null; 
      hashtable.keySet = null;
      hashtable.entrySet = null;
      hashtable.values = null;
      hashtable.modCount = 0;
      return hashtable;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public String toString() {
    int i = size() - 1;
    if (i == -1)
      return "{}"; 
    StringBuilder stringBuilder = new StringBuilder();
    Iterator iterator = entrySet().iterator();
    stringBuilder.append('{');
    for (byte b = 0;; b++) {
      Map.Entry entry = (Map.Entry)iterator.next();
      Object object1 = entry.getKey();
      Object object2 = entry.getValue();
      stringBuilder.append((object1 == this) ? "(this Map)" : object1.toString());
      stringBuilder.append('=');
      stringBuilder.append((object2 == this) ? "(this Map)" : object2.toString());
      if (b == i)
        return stringBuilder.append('}').toString(); 
      stringBuilder.append(", ");
    } 
  }
  
  private <T> Enumeration<T> getEnumeration(int paramInt) { return (this.count == 0) ? Collections.emptyEnumeration() : new Enumerator(paramInt, false); }
  
  private <T> Iterator<T> getIterator(int paramInt) { return (this.count == 0) ? Collections.emptyIterator() : new Enumerator(paramInt, true); }
  
  public Set<K> keySet() {
    if (this.keySet == null)
      this.keySet = Collections.synchronizedSet(new KeySet(null), this); 
    return this.keySet;
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    if (this.entrySet == null)
      this.entrySet = Collections.synchronizedSet(new EntrySet(null), this); 
    return this.entrySet;
  }
  
  public Collection<V> values() {
    if (this.values == null)
      this.values = Collections.synchronizedCollection(new ValueCollection(null), this); 
    return this.values;
  }
  
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
    if (this.count == 0 || this.loadFactor < 0.0F)
      return i; 
    this.loadFactor = -this.loadFactor;
    Entry[] arrayOfEntry1 = this.table;
    Entry[] arrayOfEntry2 = arrayOfEntry1;
    int j = arrayOfEntry2.length;
    for (byte b = 0; b < j; b++) {
      for (Entry entry = arrayOfEntry2[b]; entry != null; entry = entry.next)
        i += entry.hashCode(); 
    } 
    this.loadFactor = -this.loadFactor;
    return i;
  }
  
  public V getOrDefault(Object paramObject, V paramV) {
    Object object = get(paramObject);
    return (null == object) ? paramV : object;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    int i = this.modCount;
    Entry[] arrayOfEntry = this.table;
    for (Entry entry : arrayOfEntry) {
      while (entry != null) {
        paramBiConsumer.accept(entry.key, entry.value);
        entry = entry.next;
        if (i != this.modCount)
          throw new ConcurrentModificationException(); 
      } 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    int i = this.modCount;
    Entry[] arrayOfEntry = (Entry[])this.table;
    for (Entry entry : arrayOfEntry) {
      while (entry != null) {
        entry.value = Objects.requireNonNull(paramBiFunction.apply(entry.key, entry.value));
        entry = entry.next;
        if (i != this.modCount)
          throw new ConcurrentModificationException(); 
      } 
    } 
  }
  
  public V putIfAbsent(K paramK, V paramV) {
    Objects.requireNonNull(paramV);
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramK)) {
        Object object = entry.value;
        if (object == null)
          entry.value = paramV; 
        return (V)object;
      } 
    } 
    addEntry(i, paramK, paramV, j);
    return null;
  }
  
  public boolean remove(Object paramObject1, Object paramObject2) {
    Objects.requireNonNull(paramObject2);
    Entry[] arrayOfEntry = this.table;
    int i = paramObject1.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry entry1 = arrayOfEntry[j];
    Entry entry2 = null;
    while (entry1 != null) {
      if (entry1.hash == i && entry1.key.equals(paramObject1) && entry1.value.equals(paramObject2)) {
        this.modCount++;
        if (entry2 != null) {
          entry2.next = entry1.next;
        } else {
          arrayOfEntry[j] = entry1.next;
        } 
        this.count--;
        entry1.value = null;
        return true;
      } 
      entry2 = entry1;
      entry1 = entry1.next;
    } 
    return false;
  }
  
  public boolean replace(K paramK, V paramV1, V paramV2) {
    Objects.requireNonNull(paramV1);
    Objects.requireNonNull(paramV2);
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramK)) {
        if (entry.value.equals(paramV1)) {
          entry.value = paramV2;
          return true;
        } 
        return false;
      } 
    } 
    return false;
  }
  
  public V replace(K paramK, V paramV) {
    Objects.requireNonNull(paramV);
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramK)) {
        Object object = entry.value;
        entry.value = paramV;
        return (V)object;
      } 
    } 
    return null;
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    Objects.requireNonNull(paramFunction);
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramK))
        return (V)entry.value; 
    } 
    Object object = paramFunction.apply(paramK);
    if (object != null)
      addEntry(i, paramK, object, j); 
    return (V)object;
  }
  
  public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry entry1 = arrayOfEntry[j];
    Entry entry2 = null;
    while (entry1 != null) {
      if (entry1.hash == i && entry1.key.equals(paramK)) {
        Object object = paramBiFunction.apply(paramK, entry1.value);
        if (object == null) {
          this.modCount++;
          if (entry2 != null) {
            entry2.next = entry1.next;
          } else {
            arrayOfEntry[j] = entry1.next;
          } 
          this.count--;
        } else {
          entry1.value = object;
        } 
        return (V)object;
      } 
      entry2 = entry1;
      entry1 = entry1.next;
    } 
    return null;
  }
  
  public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry entry1 = arrayOfEntry[j];
    Entry entry2 = null;
    while (entry1 != null) {
      if (entry1.hash == i && Objects.equals(entry1.key, paramK)) {
        Object object1 = paramBiFunction.apply(paramK, entry1.value);
        if (object1 == null) {
          this.modCount++;
          if (entry2 != null) {
            entry2.next = entry1.next;
          } else {
            arrayOfEntry[j] = entry1.next;
          } 
          this.count--;
        } else {
          entry1.value = object1;
        } 
        return (V)object1;
      } 
      entry2 = entry1;
      entry1 = entry1.next;
    } 
    Object object = paramBiFunction.apply(paramK, null);
    if (object != null)
      addEntry(i, paramK, object, j); 
    return (V)object;
  }
  
  public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    Entry[] arrayOfEntry = this.table;
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
    Entry entry1 = arrayOfEntry[j];
    Entry entry2 = null;
    while (entry1 != null) {
      if (entry1.hash == i && entry1.key.equals(paramK)) {
        Object object = paramBiFunction.apply(entry1.value, paramV);
        if (object == null) {
          this.modCount++;
          if (entry2 != null) {
            entry2.next = entry1.next;
          } else {
            arrayOfEntry[j] = entry1.next;
          } 
          this.count--;
        } else {
          entry1.value = object;
        } 
        return (V)object;
      } 
      entry2 = entry1;
      entry1 = entry1.next;
    } 
    if (paramV != null)
      addEntry(i, paramK, paramV, j); 
    return paramV;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Entry entry = null;
    synchronized (this) {
      paramObjectOutputStream.defaultWriteObject();
      paramObjectOutputStream.writeInt(this.table.length);
      paramObjectOutputStream.writeInt(this.count);
      for (byte b = 0; b < this.table.length; b++) {
        for (Entry entry1 = this.table[b]; entry1 != null; entry1 = entry1.next)
          entry = new Entry(0, entry1.key, entry1.value, entry); 
      } 
    } 
    while (entry != null) {
      paramObjectOutputStream.writeObject(entry.key);
      paramObjectOutputStream.writeObject(entry.value);
      entry = entry.next;
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.loadFactor <= 0.0F || Float.isNaN(this.loadFactor))
      throw new StreamCorruptedException("Illegal Load: " + this.loadFactor); 
    int i = paramObjectInputStream.readInt();
    int j = paramObjectInputStream.readInt();
    if (j < 0)
      throw new StreamCorruptedException("Illegal # of Elements: " + j); 
    i = Math.max(i, (int)(j / this.loadFactor) + 1);
    int k = (int)((j + j / 20) / this.loadFactor) + 3;
    if (k > j && (k & true) == 0)
      k--; 
    k = Math.min(k, i);
    if (k < 0)
      k = i; 
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Entry[].class, k);
    this.table = new Entry[k];
    this.threshold = (int)Math.min(k * this.loadFactor, 2.14748365E9F);
    this.count = 0;
    while (j > 0) {
      Object object1 = paramObjectInputStream.readObject();
      Object object2 = paramObjectInputStream.readObject();
      reconstitutionPut(this.table, object1, object2);
      j--;
    } 
  }
  
  private void reconstitutionPut(Entry<?, ?>[] paramArrayOfEntry, K paramK, V paramV) throws StreamCorruptedException {
    if (paramV == null)
      throw new StreamCorruptedException(); 
    int i = paramK.hashCode();
    int j = (i & 0x7FFFFFFF) % paramArrayOfEntry.length;
    Entry<?, ?> entry;
    for (entry = paramArrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && entry.key.equals(paramK))
        throw new StreamCorruptedException(); 
    } 
    entry = paramArrayOfEntry[j];
    paramArrayOfEntry[j] = new Entry(i, paramK, paramV, entry);
    this.count++;
  }
  
  private static class Entry<K, V> extends Object implements Map.Entry<K, V> {
    final int hash;
    
    final K key;
    
    V value;
    
    Entry<K, V> next;
    
    protected Entry(int param1Int, K param1K, V param1V, Entry<K, V> param1Entry) {
      this.hash = param1Int;
      this.key = param1K;
      this.value = param1V;
      this.next = param1Entry;
    }
    
    protected Object clone() { return new Entry(this.hash, this.key, this.value, (this.next == null) ? null : (Entry)this.next.clone()); }
    
    public K getKey() { return (K)this.key; }
    
    public V getValue() { return (V)this.value; }
    
    public V setValue(V param1V) {
      if (param1V == null)
        throw new NullPointerException(); 
      Object object = this.value;
      this.value = param1V;
      return (V)object;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return (((this.key == null) ? (entry.getKey() == null) : this.key.equals(entry.getKey())) && ((this.value == null) ? (entry.getValue() == null) : this.value.equals(entry.getValue())));
    }
    
    public int hashCode() { return this.hash ^ Objects.hashCode(this.value); }
    
    public String toString() { return this.key.toString() + "=" + this.value.toString(); }
  }
  
  private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator() { return Hashtable.this.getIterator(2); }
    
    public boolean add(Map.Entry<K, V> param1Entry) { return super.add(param1Entry); }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getKey();
      Hashtable.Entry[] arrayOfEntry = Hashtable.this.table;
      int i = object.hashCode();
      int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
      for (Hashtable.Entry entry1 = arrayOfEntry[j]; entry1 != null; entry1 = entry1.next) {
        if (entry1.hash == i && entry1.equals(entry))
          return true; 
      } 
      return false;
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getKey();
      Hashtable.Entry[] arrayOfEntry = Hashtable.this.table;
      int i = object.hashCode();
      int j = (i & 0x7FFFFFFF) % arrayOfEntry.length;
      Hashtable.Entry entry1 = arrayOfEntry[j];
      Hashtable.Entry entry2 = null;
      while (entry1 != null) {
        if (entry1.hash == i && entry1.equals(entry)) {
          Hashtable.this.modCount++;
          if (entry2 != null) {
            entry2.next = entry1.next;
          } else {
            arrayOfEntry[j] = entry1.next;
          } 
          Hashtable.this.count--;
          entry1.value = null;
          return true;
        } 
        entry2 = entry1;
        entry1 = entry1.next;
      } 
      return false;
    }
    
    public int size() { return Hashtable.this.count; }
    
    public void clear() { Hashtable.this.clear(); }
  }
  
  private class Enumerator<T> extends Object implements Enumeration<T>, Iterator<T> {
    Hashtable.Entry<?, ?>[] table = Hashtable.this.table;
    
    int index = this.table.length;
    
    Hashtable.Entry<?, ?> entry;
    
    Hashtable.Entry<?, ?> lastReturned;
    
    int type;
    
    boolean iterator;
    
    protected int expectedModCount = Hashtable.this.modCount;
    
    Enumerator(int param1Int, boolean param1Boolean) {
      this.type = param1Int;
      this.iterator = param1Boolean;
    }
    
    public boolean hasMoreElements() {
      Hashtable.Entry entry1 = this.entry;
      int i = this.index;
      Hashtable.Entry[] arrayOfEntry = this.table;
      while (entry1 == null && i > 0)
        entry1 = arrayOfEntry[--i]; 
      this.entry = entry1;
      this.index = i;
      return (entry1 != null);
    }
    
    public T nextElement() {
      Hashtable.Entry entry1 = this.entry;
      int i = this.index;
      Hashtable.Entry[] arrayOfEntry = this.table;
      while (entry1 == null && i > 0)
        entry1 = arrayOfEntry[--i]; 
      this.entry = entry1;
      this.index = i;
      if (entry1 != null) {
        Hashtable.Entry entry2 = this.lastReturned = this.entry;
        this.entry = entry2.next;
        return (T)((this.type == 0) ? entry2.key : ((this.type == 1) ? entry2.value : entry2));
      } 
      throw new NoSuchElementException("Hashtable Enumerator");
    }
    
    public boolean hasNext() { return hasMoreElements(); }
    
    public T next() {
      if (Hashtable.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      return (T)nextElement();
    }
    
    public void remove() {
      if (!this.iterator)
        throw new UnsupportedOperationException(); 
      if (this.lastReturned == null)
        throw new IllegalStateException("Hashtable Enumerator"); 
      if (Hashtable.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      synchronized (Hashtable.this) {
        Hashtable.Entry[] arrayOfEntry = Hashtable.this.table;
        int i = (this.lastReturned.hash & 0x7FFFFFFF) % arrayOfEntry.length;
        Hashtable.Entry entry1 = arrayOfEntry[i];
        Hashtable.Entry entry2 = null;
        while (entry1 != null) {
          if (entry1 == this.lastReturned) {
            Hashtable.this.modCount++;
            this.expectedModCount++;
            if (entry2 == null) {
              arrayOfEntry[i] = entry1.next;
            } else {
              entry2.next = entry1.next;
            } 
            Hashtable.this.count--;
            this.lastReturned = null;
            return;
          } 
          entry2 = entry1;
          entry1 = entry1.next;
        } 
        throw new ConcurrentModificationException();
      } 
    }
  }
  
  private class KeySet extends AbstractSet<K> {
    private KeySet() {}
    
    public Iterator<K> iterator() { return Hashtable.this.getIterator(0); }
    
    public int size() { return Hashtable.this.count; }
    
    public boolean contains(Object param1Object) { return Hashtable.this.containsKey(param1Object); }
    
    public boolean remove(Object param1Object) { return (Hashtable.this.remove(param1Object) != null); }
    
    public void clear() { Hashtable.this.clear(); }
  }
  
  private class ValueCollection extends AbstractCollection<V> {
    private ValueCollection() {}
    
    public Iterator<V> iterator() { return Hashtable.this.getIterator(1); }
    
    public int size() { return Hashtable.this.count; }
    
    public boolean contains(Object param1Object) { return Hashtable.this.containsValue(param1Object); }
    
    public void clear() { Hashtable.this.clear(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Hashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */