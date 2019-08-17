package java.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class WeakHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V> {
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  private static final int MAXIMUM_CAPACITY = 1073741824;
  
  private static final float DEFAULT_LOAD_FACTOR = 0.75F;
  
  Entry<K, V>[] table;
  
  private int size;
  
  private int threshold;
  
  private final float loadFactor;
  
  private final ReferenceQueue<Object> queue = new ReferenceQueue();
  
  int modCount;
  
  private static final Object NULL_KEY = new Object();
  
  private Set<Map.Entry<K, V>> entrySet;
  
  private Entry<K, V>[] newTable(int paramInt) { return (Entry[])new Entry[paramInt]; }
  
  public WeakHashMap(int paramInt, float paramFloat) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Illegal Initial Capacity: " + paramInt); 
    if (paramInt > 1073741824)
      paramInt = 1073741824; 
    if (paramFloat <= 0.0F || Float.isNaN(paramFloat))
      throw new IllegalArgumentException("Illegal Load factor: " + paramFloat); 
    byte b;
    for (b = 1; b < paramInt; b <<= true);
    this.table = newTable(b);
    this.loadFactor = paramFloat;
    this.threshold = (int)(b * paramFloat);
  }
  
  public WeakHashMap(int paramInt) { this(paramInt, 0.75F); }
  
  public WeakHashMap() { this(16, 0.75F); }
  
  public WeakHashMap(Map<? extends K, ? extends V> paramMap) {
    this(Math.max((int)(paramMap.size() / 0.75F) + 1, 16), 0.75F);
    putAll(paramMap);
  }
  
  private static Object maskNull(Object paramObject) { return (paramObject == null) ? NULL_KEY : paramObject; }
  
  static Object unmaskNull(Object paramObject) { return (paramObject == NULL_KEY) ? null : paramObject; }
  
  private static boolean eq(Object paramObject1, Object paramObject2) { return (paramObject1 == paramObject2 || paramObject1.equals(paramObject2)); }
  
  final int hash(Object paramObject) {
    int i = paramObject.hashCode();
    i ^= i >>> 20 ^ i >>> 12;
    return i ^ i >>> 7 ^ i >>> 4;
  }
  
  private static int indexFor(int paramInt1, int paramInt2) { return paramInt1 & paramInt2 - 1; }
  
  private void expungeStaleEntries() {
    Reference reference;
    while ((reference = this.queue.poll()) != null) {
      synchronized (this.queue) {
        Entry entry1 = (Entry)reference;
        int i = indexFor(entry1.hash, this.table.length);
        Entry entry2 = this.table[i];
        for (Entry entry3 = entry2; entry3 != null; entry3 = entry) {
          Entry entry = entry3.next;
          if (entry3 == entry1) {
            if (entry2 == entry1) {
              this.table[i] = entry;
            } else {
              entry2.next = entry;
            } 
            entry1.value = null;
            this.size--;
            break;
          } 
          entry2 = entry3;
        } 
      } 
    } 
  }
  
  private Entry<K, V>[] getTable() {
    expungeStaleEntries();
    return this.table;
  }
  
  public int size() {
    if (this.size == 0)
      return 0; 
    expungeStaleEntries();
    return this.size;
  }
  
  public boolean isEmpty() { return (size() == 0); }
  
  public V get(Object paramObject) {
    Object object = maskNull(paramObject);
    int i = hash(object);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (entry.hash == i && eq(object, entry.get()))
        return (V)entry.value; 
    } 
    return null;
  }
  
  public boolean containsKey(Object paramObject) { return (getEntry(paramObject) != null); }
  
  Entry<K, V> getEntry(Object paramObject) {
    Object object = maskNull(paramObject);
    int i = hash(object);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    Entry entry;
    for (entry = arrayOfEntry[j]; entry != null && (entry.hash != i || !eq(object, entry.get())); entry = entry.next);
    return entry;
  }
  
  public V put(K paramK, V paramV) {
    Object object = maskNull(paramK);
    int i = hash(object);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    Entry entry;
    for (entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
      if (i == entry.hash && eq(object, entry.get())) {
        Object object1 = entry.value;
        if (paramV != object1)
          entry.value = paramV; 
        return (V)object1;
      } 
    } 
    this.modCount++;
    entry = arrayOfEntry[j];
    arrayOfEntry[j] = new Entry(object, paramV, this.queue, i, entry);
    if (++this.size >= this.threshold)
      resize(arrayOfEntry.length * 2); 
    return null;
  }
  
  void resize(int paramInt) {
    Entry[] arrayOfEntry1 = getTable();
    int i = arrayOfEntry1.length;
    if (i == 1073741824) {
      this.threshold = Integer.MAX_VALUE;
      return;
    } 
    Entry[] arrayOfEntry2 = newTable(paramInt);
    transfer(arrayOfEntry1, arrayOfEntry2);
    this.table = arrayOfEntry2;
    if (this.size >= this.threshold / 2) {
      this.threshold = (int)(paramInt * this.loadFactor);
    } else {
      expungeStaleEntries();
      transfer(arrayOfEntry2, arrayOfEntry1);
      this.table = arrayOfEntry1;
    } 
  }
  
  private void transfer(Entry<K, V>[] paramArrayOfEntry1, Entry<K, V>[] paramArrayOfEntry2) {
    for (byte b = 0; b < paramArrayOfEntry1.length; b++) {
      Entry<K, V> entry = paramArrayOfEntry1[b];
      paramArrayOfEntry1[b] = null;
      while (entry != null) {
        Entry entry1 = entry.next;
        Object object = entry.get();
        if (object == null) {
          entry.next = null;
          entry.value = null;
          this.size--;
        } else {
          int i = indexFor(entry.hash, paramArrayOfEntry2.length);
          entry.next = paramArrayOfEntry2[i];
          paramArrayOfEntry2[i] = entry;
        } 
        entry = entry1;
      } 
    } 
  }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    int i = paramMap.size();
    if (i == 0)
      return; 
    if (i > this.threshold) {
      int j = (int)(i / this.loadFactor + 1.0F);
      if (j > 1073741824)
        j = 1073741824; 
      int k;
      for (k = this.table.length; k < j; k <<= 1);
      if (k > this.table.length)
        resize(k); 
    } 
    for (Map.Entry entry : paramMap.entrySet())
      put(entry.getKey(), entry.getValue()); 
  }
  
  public V remove(Object paramObject) {
    Object object = maskNull(paramObject);
    int i = hash(object);
    Entry[] arrayOfEntry = getTable();
    int j = indexFor(i, arrayOfEntry.length);
    Entry entry1 = arrayOfEntry[j];
    for (Entry entry2 = entry1; entry2 != null; entry2 = entry) {
      Entry entry = entry2.next;
      if (i == entry2.hash && eq(object, entry2.get())) {
        this.modCount++;
        this.size--;
        if (entry1 == entry2) {
          arrayOfEntry[j] = entry;
        } else {
          entry1.next = entry;
        } 
        return (V)entry2.value;
      } 
      entry1 = entry2;
    } 
    return null;
  }
  
  boolean removeMapping(Object paramObject) {
    if (!(paramObject instanceof Map.Entry))
      return false; 
    Entry[] arrayOfEntry = getTable();
    Map.Entry entry = (Map.Entry)paramObject;
    Object object = maskNull(entry.getKey());
    int i = hash(object);
    int j = indexFor(i, arrayOfEntry.length);
    Entry entry1 = arrayOfEntry[j];
    for (Entry entry2 = entry1; entry2 != null; entry2 = entry3) {
      Entry entry3 = entry2.next;
      if (i == entry2.hash && entry2.equals(entry)) {
        this.modCount++;
        this.size--;
        if (entry1 == entry2) {
          arrayOfEntry[j] = entry3;
        } else {
          entry1.next = entry3;
        } 
        return true;
      } 
      entry1 = entry2;
    } 
    return false;
  }
  
  public void clear() {
    while (this.queue.poll() != null);
    this.modCount++;
    Arrays.fill(this.table, null);
    this.size = 0;
    while (this.queue.poll() != null);
  }
  
  public boolean containsValue(Object paramObject) {
    if (paramObject == null)
      return containsNullValue(); 
    Entry[] arrayOfEntry = getTable();
    int i = arrayOfEntry.length;
    while (i-- > 0) {
      for (Entry entry = arrayOfEntry[i]; entry != null; entry = entry.next) {
        if (paramObject.equals(entry.value))
          return true; 
      } 
    } 
    return false;
  }
  
  private boolean containsNullValue() {
    Entry[] arrayOfEntry = getTable();
    int i = arrayOfEntry.length;
    while (i-- > 0) {
      for (Entry entry = arrayOfEntry[i]; entry != null; entry = entry.next) {
        if (entry.value == null)
          return true; 
      } 
    } 
    return false;
  }
  
  public Set<K> keySet() {
    Set set = this.keySet;
    if (set == null) {
      set = new KeySet(null);
      this.keySet = set;
    } 
    return set;
  }
  
  public Collection<V> values() {
    Collection collection = this.values;
    if (collection == null) {
      collection = new Values(null);
      this.values = collection;
    } 
    return collection;
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    Set set = this.entrySet;
    return (set != null) ? set : (this.entrySet = new EntrySet(null));
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    int i = this.modCount;
    Entry[] arrayOfEntry = getTable();
    for (Entry entry : arrayOfEntry) {
      while (entry != null) {
        Object object = entry.get();
        if (object != null)
          paramBiConsumer.accept(unmaskNull(object), entry.value); 
        entry = entry.next;
        if (i != this.modCount)
          throw new ConcurrentModificationException(); 
      } 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    int i = this.modCount;
    Entry[] arrayOfEntry = getTable();
    for (Entry entry : arrayOfEntry) {
      while (entry != null) {
        Object object = entry.get();
        if (object != null)
          entry.value = paramBiFunction.apply(unmaskNull(object), entry.value); 
        entry = entry.next;
        if (i != this.modCount)
          throw new ConcurrentModificationException(); 
      } 
    } 
  }
  
  private static class Entry<K, V> extends WeakReference<Object> implements Map.Entry<K, V> {
    V value;
    
    final int hash;
    
    Entry<K, V> next;
    
    Entry(Object param1Object, V param1V, ReferenceQueue<Object> param1ReferenceQueue, int param1Int, Entry<K, V> param1Entry) {
      super(param1Object, param1ReferenceQueue);
      this.value = param1V;
      this.hash = param1Int;
      this.next = param1Entry;
    }
    
    public K getKey() { return (K)WeakHashMap.unmaskNull(get()); }
    
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
      Object object1 = getKey();
      Object object2 = entry.getKey();
      if (object1 == object2 || (object1 != null && object1.equals(object2))) {
        Object object3 = getValue();
        Object object4 = entry.getValue();
        if (object3 == object4 || (object3 != null && object3.equals(object4)))
          return true; 
      } 
      return false;
    }
    
    public int hashCode() {
      Object object1 = getKey();
      Object object2 = getValue();
      return Objects.hashCode(object1) ^ Objects.hashCode(object2);
    }
    
    public String toString() { return getKey() + "=" + getValue(); }
  }
  
  private class EntryIterator extends HashIterator<Map.Entry<K, V>> {
    private EntryIterator() { super(WeakHashMap.this); }
    
    public Map.Entry<K, V> next() { return nextEntry(); }
  }
  
  private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    private EntrySet() {}
    
    public Iterator<Map.Entry<K, V>> iterator() { return new WeakHashMap.EntryIterator(WeakHashMap.this, null); }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      WeakHashMap.Entry entry1 = WeakHashMap.this.getEntry(entry.getKey());
      return (entry1 != null && entry1.equals(entry));
    }
    
    public boolean remove(Object param1Object) { return WeakHashMap.this.removeMapping(param1Object); }
    
    public int size() { return WeakHashMap.this.size(); }
    
    public void clear() { WeakHashMap.this.clear(); }
    
    private List<Map.Entry<K, V>> deepCopy() {
      ArrayList arrayList = new ArrayList(size());
      for (Map.Entry entry : this)
        arrayList.add(new AbstractMap.SimpleEntry(entry)); 
      return arrayList;
    }
    
    public Object[] toArray() { return deepCopy().toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])deepCopy().toArray(param1ArrayOfT); }
    
    public Spliterator<Map.Entry<K, V>> spliterator() { return new WeakHashMap.EntrySpliterator(WeakHashMap.this, 0, -1, 0, 0); }
  }
  
  static final class EntrySpliterator<K, V> extends WeakHashMapSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
    EntrySpliterator(WeakHashMap<K, V> param1WeakHashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1WeakHashMap, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public EntrySpliterator<K, V> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) ? null : new EntrySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      int k;
      if (param1Consumer == null)
        throw new NullPointerException(); 
      WeakHashMap weakHashMap = this.map;
      WeakHashMap.Entry[] arrayOfEntry = weakHashMap.table;
      int j;
      if ((j = this.fence) < 0) {
        k = this.expectedModCount = weakHashMap.modCount;
        j = this.fence = arrayOfEntry.length;
      } else {
        k = this.expectedModCount;
      } 
      int i;
      if (arrayOfEntry.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
        WeakHashMap.Entry entry = this.current;
        this.current = null;
        do {
          if (entry == null) {
            entry = arrayOfEntry[i++];
          } else {
            Object object1 = entry.get();
            Object object2 = entry.value;
            entry = entry.next;
            if (object1 != null) {
              Object object = WeakHashMap.unmaskNull(object1);
              param1Consumer.accept(new AbstractMap.SimpleImmutableEntry(object, object2));
            } 
          } 
        } while (entry != null || i < j);
      } 
      if (weakHashMap.modCount != k)
        throw new ConcurrentModificationException(); 
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      WeakHashMap.Entry[] arrayOfEntry = this.map.table;
      int i;
      if (arrayOfEntry.length >= (i = getFence()) && this.index >= 0)
        while (this.current != null || this.index < i) {
          if (this.current == null) {
            this.current = arrayOfEntry[this.index++];
            continue;
          } 
          Object object1 = this.current.get();
          Object object2 = this.current.value;
          this.current = this.current.next;
          if (object1 != null) {
            Object object = WeakHashMap.unmaskNull(object1);
            param1Consumer.accept(new AbstractMap.SimpleImmutableEntry(object, object2));
            if (this.map.modCount != this.expectedModCount)
              throw new ConcurrentModificationException(); 
            return true;
          } 
        }  
      return false;
    }
    
    public int characteristics() { return 1; }
  }
  
  private abstract class HashIterator<T> extends Object implements Iterator<T> {
    private int index;
    
    private WeakHashMap.Entry<K, V> entry;
    
    private WeakHashMap.Entry<K, V> lastReturned;
    
    private int expectedModCount = WeakHashMap.this.modCount;
    
    private Object nextKey;
    
    private Object currentKey;
    
    HashIterator() { this.index = this$0.isEmpty() ? 0 : WeakHashMap.this.table.length; }
    
    public boolean hasNext() {
      WeakHashMap.Entry[] arrayOfEntry = WeakHashMap.this.table;
      while (this.nextKey == null) {
        WeakHashMap.Entry entry1 = this.entry;
        int i = this.index;
        while (entry1 == null && i > 0)
          entry1 = arrayOfEntry[--i]; 
        this.entry = entry1;
        this.index = i;
        if (entry1 == null) {
          this.currentKey = null;
          return false;
        } 
        this.nextKey = entry1.get();
        if (this.nextKey == null)
          this.entry = this.entry.next; 
      } 
      return true;
    }
    
    protected WeakHashMap.Entry<K, V> nextEntry() {
      if (WeakHashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      if (this.nextKey == null && !hasNext())
        throw new NoSuchElementException(); 
      this.lastReturned = this.entry;
      this.entry = this.entry.next;
      this.currentKey = this.nextKey;
      this.nextKey = null;
      return this.lastReturned;
    }
    
    public void remove() {
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      if (WeakHashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      WeakHashMap.this.remove(this.currentKey);
      this.expectedModCount = WeakHashMap.this.modCount;
      this.lastReturned = null;
      this.currentKey = null;
    }
  }
  
  private class KeyIterator extends HashIterator<K> {
    private KeyIterator() { super(WeakHashMap.this); }
    
    public K next() { return (K)nextEntry().getKey(); }
  }
  
  private class KeySet extends AbstractSet<K> {
    private KeySet() {}
    
    public Iterator<K> iterator() { return new WeakHashMap.KeyIterator(WeakHashMap.this, null); }
    
    public int size() { return WeakHashMap.this.size(); }
    
    public boolean contains(Object param1Object) { return WeakHashMap.this.containsKey(param1Object); }
    
    public boolean remove(Object param1Object) {
      if (WeakHashMap.this.containsKey(param1Object)) {
        WeakHashMap.this.remove(param1Object);
        return true;
      } 
      return false;
    }
    
    public void clear() { WeakHashMap.this.clear(); }
    
    public Spliterator<K> spliterator() { return new WeakHashMap.KeySpliterator(WeakHashMap.this, 0, -1, 0, 0); }
  }
  
  static final class KeySpliterator<K, V> extends WeakHashMapSpliterator<K, V> implements Spliterator<K> {
    KeySpliterator(WeakHashMap<K, V> param1WeakHashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1WeakHashMap, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public KeySpliterator<K, V> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) ? null : new KeySpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super K> param1Consumer) {
      int k;
      if (param1Consumer == null)
        throw new NullPointerException(); 
      WeakHashMap weakHashMap = this.map;
      WeakHashMap.Entry[] arrayOfEntry = weakHashMap.table;
      int j;
      if ((j = this.fence) < 0) {
        k = this.expectedModCount = weakHashMap.modCount;
        j = this.fence = arrayOfEntry.length;
      } else {
        k = this.expectedModCount;
      } 
      int i;
      if (arrayOfEntry.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
        WeakHashMap.Entry entry = this.current;
        this.current = null;
        do {
          if (entry == null) {
            entry = arrayOfEntry[i++];
          } else {
            Object object = entry.get();
            entry = entry.next;
            if (object != null) {
              Object object1 = WeakHashMap.unmaskNull(object);
              param1Consumer.accept(object1);
            } 
          } 
        } while (entry != null || i < j);
      } 
      if (weakHashMap.modCount != k)
        throw new ConcurrentModificationException(); 
    }
    
    public boolean tryAdvance(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      WeakHashMap.Entry[] arrayOfEntry = this.map.table;
      int i;
      if (arrayOfEntry.length >= (i = getFence()) && this.index >= 0)
        while (this.current != null || this.index < i) {
          if (this.current == null) {
            this.current = arrayOfEntry[this.index++];
            continue;
          } 
          Object object = this.current.get();
          this.current = this.current.next;
          if (object != null) {
            Object object1 = WeakHashMap.unmaskNull(object);
            param1Consumer.accept(object1);
            if (this.map.modCount != this.expectedModCount)
              throw new ConcurrentModificationException(); 
            return true;
          } 
        }  
      return false;
    }
    
    public int characteristics() { return 1; }
  }
  
  private class ValueIterator extends HashIterator<V> {
    private ValueIterator() { super(WeakHashMap.this); }
    
    public V next() { return (V)(nextEntry()).value; }
  }
  
  static final class ValueSpliterator<K, V> extends WeakHashMapSpliterator<K, V> implements Spliterator<V> {
    ValueSpliterator(WeakHashMap<K, V> param1WeakHashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { super(param1WeakHashMap, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public ValueSpliterator<K, V> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) ? null : new ValueSpliterator(this.map, j, this.index = k, this.est >>>= 1, this.expectedModCount);
    }
    
    public void forEachRemaining(Consumer<? super V> param1Consumer) {
      int k;
      if (param1Consumer == null)
        throw new NullPointerException(); 
      WeakHashMap weakHashMap = this.map;
      WeakHashMap.Entry[] arrayOfEntry = weakHashMap.table;
      int j;
      if ((j = this.fence) < 0) {
        k = this.expectedModCount = weakHashMap.modCount;
        j = this.fence = arrayOfEntry.length;
      } else {
        k = this.expectedModCount;
      } 
      int i;
      if (arrayOfEntry.length >= j && (i = this.index) >= 0 && (i < (this.index = j) || this.current != null)) {
        WeakHashMap.Entry entry = this.current;
        this.current = null;
        do {
          if (entry == null) {
            entry = arrayOfEntry[i++];
          } else {
            Object object1 = entry.get();
            Object object2 = entry.value;
            entry = entry.next;
            if (object1 != null)
              param1Consumer.accept(object2); 
          } 
        } while (entry != null || i < j);
      } 
      if (weakHashMap.modCount != k)
        throw new ConcurrentModificationException(); 
    }
    
    public boolean tryAdvance(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      WeakHashMap.Entry[] arrayOfEntry = this.map.table;
      int i;
      if (arrayOfEntry.length >= (i = getFence()) && this.index >= 0)
        while (this.current != null || this.index < i) {
          if (this.current == null) {
            this.current = arrayOfEntry[this.index++];
            continue;
          } 
          Object object1 = this.current.get();
          Object object2 = this.current.value;
          this.current = this.current.next;
          if (object1 != null) {
            param1Consumer.accept(object2);
            if (this.map.modCount != this.expectedModCount)
              throw new ConcurrentModificationException(); 
            return true;
          } 
        }  
      return false;
    }
    
    public int characteristics() { return 0; }
  }
  
  private class Values extends AbstractCollection<V> {
    private Values() {}
    
    public Iterator<V> iterator() { return new WeakHashMap.ValueIterator(WeakHashMap.this, null); }
    
    public int size() { return WeakHashMap.this.size(); }
    
    public boolean contains(Object param1Object) { return WeakHashMap.this.containsValue(param1Object); }
    
    public void clear() { WeakHashMap.this.clear(); }
    
    public Spliterator<V> spliterator() { return new WeakHashMap.ValueSpliterator(WeakHashMap.this, 0, -1, 0, 0); }
  }
  
  static class WeakHashMapSpliterator<K, V> extends Object {
    final WeakHashMap<K, V> map;
    
    WeakHashMap.Entry<K, V> current;
    
    int index;
    
    int fence;
    
    int est;
    
    int expectedModCount;
    
    WeakHashMapSpliterator(WeakHashMap<K, V> param1WeakHashMap, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.map = param1WeakHashMap;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.est = param1Int3;
      this.expectedModCount = param1Int4;
    }
    
    final int getFence() {
      int i;
      if ((i = this.fence) < 0) {
        WeakHashMap weakHashMap = this.map;
        this.est = weakHashMap.size();
        this.expectedModCount = weakHashMap.modCount;
        i = this.fence = weakHashMap.table.length;
      } 
      return i;
    }
    
    public final long estimateSize() {
      getFence();
      return this.est;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\WeakHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */