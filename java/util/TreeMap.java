package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable, Serializable {
  private final Comparator<? super K> comparator = null;
  
  private Entry<K, V> root;
  
  private int size = 0;
  
  private int modCount = 0;
  
  private EntrySet entrySet;
  
  private KeySet<K> navigableKeySet;
  
  private NavigableMap<K, V> descendingMap;
  
  private static final Object UNBOUNDED = new Object();
  
  private static final boolean RED = false;
  
  private static final boolean BLACK = true;
  
  private static final long serialVersionUID = 919286545866124006L;
  
  public TreeMap() {}
  
  public TreeMap(Comparator<? super K> paramComparator) {}
  
  public TreeMap(Map<? extends K, ? extends V> paramMap) { putAll(paramMap); }
  
  public TreeMap(SortedMap<K, ? extends V> paramSortedMap) {
    try {
      buildFromSorted(paramSortedMap.size(), paramSortedMap.entrySet().iterator(), null, null);
    } catch (IOException iOException) {
    
    } catch (ClassNotFoundException classNotFoundException) {}
  }
  
  public int size() { return this.size; }
  
  public boolean containsKey(Object paramObject) { return (getEntry(paramObject) != null); }
  
  public boolean containsValue(Object paramObject) {
    for (Entry entry = getFirstEntry(); entry != null; entry = successor(entry)) {
      if (valEquals(paramObject, entry.value))
        return true; 
    } 
    return false;
  }
  
  public V get(Object paramObject) {
    Entry entry = getEntry(paramObject);
    return (V)((entry == null) ? null : entry.value);
  }
  
  public Comparator<? super K> comparator() { return this.comparator; }
  
  public K firstKey() { return (K)key(getFirstEntry()); }
  
  public K lastKey() { return (K)key(getLastEntry()); }
  
  public void putAll(Map<? extends K, ? extends V> paramMap) {
    int i = paramMap.size();
    if (this.size == 0 && i != 0 && paramMap instanceof SortedMap) {
      Comparator comparator1 = ((SortedMap)paramMap).comparator();
      if (comparator1 == this.comparator || (comparator1 != null && comparator1.equals(this.comparator))) {
        this.modCount++;
        try {
          buildFromSorted(i, paramMap.entrySet().iterator(), null, null);
        } catch (IOException iOException) {
        
        } catch (ClassNotFoundException classNotFoundException) {}
        return;
      } 
    } 
    super.putAll(paramMap);
  }
  
  final Entry<K, V> getEntry(Object paramObject) {
    if (this.comparator != null)
      return getEntryUsingComparator(paramObject); 
    if (paramObject == null)
      throw new NullPointerException(); 
    Comparable comparable = (Comparable)paramObject;
    Entry entry = this.root;
    while (entry != null) {
      int i = comparable.compareTo(entry.key);
      if (i < 0) {
        entry = entry.left;
        continue;
      } 
      if (i > 0) {
        entry = entry.right;
        continue;
      } 
      return entry;
    } 
    return null;
  }
  
  final Entry<K, V> getEntryUsingComparator(Object paramObject) {
    Object object = paramObject;
    Comparator comparator1 = this.comparator;
    if (comparator1 != null) {
      Entry entry = this.root;
      while (entry != null) {
        int i = comparator1.compare(object, entry.key);
        if (i < 0) {
          entry = entry.left;
          continue;
        } 
        if (i > 0) {
          entry = entry.right;
          continue;
        } 
        return entry;
      } 
    } 
    return null;
  }
  
  final Entry<K, V> getCeilingEntry(K paramK) {
    Entry entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i < 0) {
        if (entry.left != null) {
          entry = entry.left;
          continue;
        } 
        return entry;
      } 
      if (i > 0) {
        if (entry.right != null) {
          entry = entry.right;
          continue;
        } 
        Entry entry1 = entry.parent;
        Entry entry2 = entry;
        while (entry1 != null && entry2 == entry1.right) {
          entry2 = entry1;
          entry1 = entry1.parent;
        } 
        return entry1;
      } 
      return entry;
    } 
    return null;
  }
  
  final Entry<K, V> getFloorEntry(K paramK) {
    Entry entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i > 0) {
        if (entry.right != null) {
          entry = entry.right;
          continue;
        } 
        return entry;
      } 
      if (i < 0) {
        if (entry.left != null) {
          entry = entry.left;
          continue;
        } 
        Entry entry1 = entry.parent;
        Entry entry2 = entry;
        while (entry1 != null && entry2 == entry1.left) {
          entry2 = entry1;
          entry1 = entry1.parent;
        } 
        return entry1;
      } 
      return entry;
    } 
    return null;
  }
  
  final Entry<K, V> getHigherEntry(K paramK) {
    Entry entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i < 0) {
        if (entry.left != null) {
          entry = entry.left;
          continue;
        } 
        return entry;
      } 
      if (entry.right != null) {
        entry = entry.right;
        continue;
      } 
      Entry entry1 = entry.parent;
      Entry entry2 = entry;
      while (entry1 != null && entry2 == entry1.right) {
        entry2 = entry1;
        entry1 = entry1.parent;
      } 
      return entry1;
    } 
    return null;
  }
  
  final Entry<K, V> getLowerEntry(K paramK) {
    Entry entry = this.root;
    while (entry != null) {
      int i = compare(paramK, entry.key);
      if (i > 0) {
        if (entry.right != null) {
          entry = entry.right;
          continue;
        } 
        return entry;
      } 
      if (entry.left != null) {
        entry = entry.left;
        continue;
      } 
      Entry entry1 = entry.parent;
      Entry entry2 = entry;
      while (entry1 != null && entry2 == entry1.left) {
        entry2 = entry1;
        entry1 = entry1.parent;
      } 
      return entry1;
    } 
    return null;
  }
  
  public V put(K paramK, V paramV) {
    Entry entry2;
    int i;
    Entry entry1 = this.root;
    if (entry1 == null) {
      compare(paramK, paramK);
      this.root = new Entry(paramK, paramV, null);
      this.size = 1;
      this.modCount++;
      return null;
    } 
    Comparator comparator1 = this.comparator;
    if (comparator1 != null) {
      do {
        entry2 = entry1;
        i = comparator1.compare(paramK, entry1.key);
        if (i < 0) {
          entry1 = entry1.left;
        } else if (i > 0) {
          entry1 = entry1.right;
        } else {
          return (V)entry1.setValue(paramV);
        } 
      } while (entry1 != null);
    } else {
      if (paramK == null)
        throw new NullPointerException(); 
      Comparable comparable = (Comparable)paramK;
      do {
        entry2 = entry1;
        i = comparable.compareTo(entry1.key);
        if (i < 0) {
          entry1 = entry1.left;
        } else if (i > 0) {
          entry1 = entry1.right;
        } else {
          return (V)entry1.setValue(paramV);
        } 
      } while (entry1 != null);
    } 
    Entry entry3 = new Entry(paramK, paramV, entry2);
    if (i < 0) {
      entry2.left = entry3;
    } else {
      entry2.right = entry3;
    } 
    fixAfterInsertion(entry3);
    this.size++;
    this.modCount++;
    return null;
  }
  
  public V remove(Object paramObject) {
    Entry entry = getEntry(paramObject);
    if (entry == null)
      return null; 
    Object object = entry.value;
    deleteEntry(entry);
    return (V)object;
  }
  
  public void clear() {
    this.modCount++;
    this.size = 0;
    this.root = null;
  }
  
  public Object clone() {
    TreeMap treeMap;
    try {
      treeMap = (TreeMap)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    treeMap.root = null;
    treeMap.size = 0;
    treeMap.modCount = 0;
    treeMap.entrySet = null;
    treeMap.navigableKeySet = null;
    treeMap.descendingMap = null;
    try {
      treeMap.buildFromSorted(this.size, entrySet().iterator(), null, null);
    } catch (IOException iOException) {
    
    } catch (ClassNotFoundException classNotFoundException) {}
    return treeMap;
  }
  
  public Map.Entry<K, V> firstEntry() { return exportEntry(getFirstEntry()); }
  
  public Map.Entry<K, V> lastEntry() { return exportEntry(getLastEntry()); }
  
  public Map.Entry<K, V> pollFirstEntry() {
    Entry entry = getFirstEntry();
    Map.Entry entry1 = exportEntry(entry);
    if (entry != null)
      deleteEntry(entry); 
    return entry1;
  }
  
  public Map.Entry<K, V> pollLastEntry() {
    Entry entry = getLastEntry();
    Map.Entry entry1 = exportEntry(entry);
    if (entry != null)
      deleteEntry(entry); 
    return entry1;
  }
  
  public Map.Entry<K, V> lowerEntry(K paramK) { return exportEntry(getLowerEntry(paramK)); }
  
  public K lowerKey(K paramK) { return (K)keyOrNull(getLowerEntry(paramK)); }
  
  public Map.Entry<K, V> floorEntry(K paramK) { return exportEntry(getFloorEntry(paramK)); }
  
  public K floorKey(K paramK) { return (K)keyOrNull(getFloorEntry(paramK)); }
  
  public Map.Entry<K, V> ceilingEntry(K paramK) { return exportEntry(getCeilingEntry(paramK)); }
  
  public K ceilingKey(K paramK) { return (K)keyOrNull(getCeilingEntry(paramK)); }
  
  public Map.Entry<K, V> higherEntry(K paramK) { return exportEntry(getHigherEntry(paramK)); }
  
  public K higherKey(K paramK) { return (K)keyOrNull(getHigherEntry(paramK)); }
  
  public Set<K> keySet() { return navigableKeySet(); }
  
  public NavigableSet<K> navigableKeySet() {
    KeySet keySet = this.navigableKeySet;
    return (keySet != null) ? keySet : (this.navigableKeySet = new KeySet(this));
  }
  
  public NavigableSet<K> descendingKeySet() { return descendingMap().navigableKeySet(); }
  
  public Collection<V> values() {
    Collection collection = this.values;
    if (collection == null) {
      collection = new Values();
      this.values = collection;
    } 
    return collection;
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    EntrySet entrySet1 = this.entrySet;
    return (entrySet1 != null) ? entrySet1 : (this.entrySet = new EntrySet());
  }
  
  public NavigableMap<K, V> descendingMap() {
    NavigableMap navigableMap = this.descendingMap;
    return (navigableMap != null) ? navigableMap : (this.descendingMap = new DescendingSubMap(this, true, null, true, true, null, true));
  }
  
  public NavigableMap<K, V> subMap(K paramK1, boolean paramBoolean1, K paramK2, boolean paramBoolean2) { return new AscendingSubMap(this, false, paramK1, paramBoolean1, false, paramK2, paramBoolean2); }
  
  public NavigableMap<K, V> headMap(K paramK, boolean paramBoolean) { return new AscendingSubMap(this, true, null, true, false, paramK, paramBoolean); }
  
  public NavigableMap<K, V> tailMap(K paramK, boolean paramBoolean) { return new AscendingSubMap(this, false, paramK, paramBoolean, true, null, true); }
  
  public SortedMap<K, V> subMap(K paramK1, K paramK2) { return subMap(paramK1, true, paramK2, false); }
  
  public SortedMap<K, V> headMap(K paramK) { return headMap(paramK, false); }
  
  public SortedMap<K, V> tailMap(K paramK) { return tailMap(paramK, true); }
  
  public boolean replace(K paramK, V paramV1, V paramV2) {
    Entry entry = getEntry(paramK);
    if (entry != null && Objects.equals(paramV1, entry.value)) {
      entry.value = paramV2;
      return true;
    } 
    return false;
  }
  
  public V replace(K paramK, V paramV) {
    Entry entry = getEntry(paramK);
    if (entry != null) {
      Object object = entry.value;
      entry.value = paramV;
      return (V)object;
    } 
    return null;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    int i = this.modCount;
    for (Entry entry = getFirstEntry(); entry != null; entry = successor(entry)) {
      paramBiConsumer.accept(entry.key, entry.value);
      if (i != this.modCount)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    Objects.requireNonNull(paramBiFunction);
    int i = this.modCount;
    for (Entry entry = getFirstEntry(); entry != null; entry = successor(entry)) {
      entry.value = paramBiFunction.apply(entry.key, entry.value);
      if (i != this.modCount)
        throw new ConcurrentModificationException(); 
    } 
  }
  
  Iterator<K> keyIterator() { return new KeyIterator(getFirstEntry()); }
  
  Iterator<K> descendingKeyIterator() { return new DescendingKeyIterator(getLastEntry()); }
  
  final int compare(Object paramObject1, Object paramObject2) { return (this.comparator == null) ? ((Comparable)paramObject1).compareTo(paramObject2) : this.comparator.compare(paramObject1, paramObject2); }
  
  static final boolean valEquals(Object paramObject1, Object paramObject2) { return (paramObject1 == null) ? ((paramObject2 == null)) : paramObject1.equals(paramObject2); }
  
  static <K, V> Map.Entry<K, V> exportEntry(Entry<K, V> paramEntry) { return (paramEntry == null) ? null : new AbstractMap.SimpleImmutableEntry(paramEntry); }
  
  static <K, V> K keyOrNull(Entry<K, V> paramEntry) { return (K)((paramEntry == null) ? null : paramEntry.key); }
  
  static <K> K key(Entry<K, ?> paramEntry) {
    if (paramEntry == null)
      throw new NoSuchElementException(); 
    return (K)paramEntry.key;
  }
  
  final Entry<K, V> getFirstEntry() {
    Entry entry = this.root;
    if (entry != null)
      while (entry.left != null)
        entry = entry.left;  
    return entry;
  }
  
  final Entry<K, V> getLastEntry() {
    Entry entry = this.root;
    if (entry != null)
      while (entry.right != null)
        entry = entry.right;  
    return entry;
  }
  
  static <K, V> Entry<K, V> successor(Entry<K, V> paramEntry) {
    if (paramEntry == null)
      return null; 
    if (paramEntry.right != null) {
      Entry entry;
      for (entry = paramEntry.right; entry.left != null; entry = entry.left);
      return entry;
    } 
    Entry entry1 = paramEntry.parent;
    Entry<K, V> entry2 = paramEntry;
    while (entry1 != null && entry2 == entry1.right) {
      entry2 = entry1;
      entry1 = entry1.parent;
    } 
    return entry1;
  }
  
  static <K, V> Entry<K, V> predecessor(Entry<K, V> paramEntry) {
    if (paramEntry == null)
      return null; 
    if (paramEntry.left != null) {
      Entry entry;
      for (entry = paramEntry.left; entry.right != null; entry = entry.right);
      return entry;
    } 
    Entry entry1 = paramEntry.parent;
    Entry<K, V> entry2 = paramEntry;
    while (entry1 != null && entry2 == entry1.left) {
      entry2 = entry1;
      entry1 = entry1.parent;
    } 
    return entry1;
  }
  
  private static <K, V> boolean colorOf(Entry<K, V> paramEntry) { return (paramEntry == null) ? true : paramEntry.color; }
  
  private static <K, V> Entry<K, V> parentOf(Entry<K, V> paramEntry) { return (paramEntry == null) ? null : paramEntry.parent; }
  
  private static <K, V> void setColor(Entry<K, V> paramEntry, boolean paramBoolean) {
    if (paramEntry != null)
      paramEntry.color = paramBoolean; 
  }
  
  private static <K, V> Entry<K, V> leftOf(Entry<K, V> paramEntry) { return (paramEntry == null) ? null : paramEntry.left; }
  
  private static <K, V> Entry<K, V> rightOf(Entry<K, V> paramEntry) { return (paramEntry == null) ? null : paramEntry.right; }
  
  private void rotateLeft(Entry<K, V> paramEntry) {
    if (paramEntry != null) {
      Entry entry = paramEntry.right;
      paramEntry.right = entry.left;
      if (entry.left != null)
        entry.left.parent = paramEntry; 
      entry.parent = paramEntry.parent;
      if (paramEntry.parent == null) {
        this.root = entry;
      } else if (paramEntry.parent.left == paramEntry) {
        paramEntry.parent.left = entry;
      } else {
        paramEntry.parent.right = entry;
      } 
      entry.left = paramEntry;
      paramEntry.parent = entry;
    } 
  }
  
  private void rotateRight(Entry<K, V> paramEntry) {
    if (paramEntry != null) {
      Entry entry = paramEntry.left;
      paramEntry.left = entry.right;
      if (entry.right != null)
        entry.right.parent = paramEntry; 
      entry.parent = paramEntry.parent;
      if (paramEntry.parent == null) {
        this.root = entry;
      } else if (paramEntry.parent.right == paramEntry) {
        paramEntry.parent.right = entry;
      } else {
        paramEntry.parent.left = entry;
      } 
      entry.right = paramEntry;
      paramEntry.parent = entry;
    } 
  }
  
  private void fixAfterInsertion(Entry<K, V> paramEntry) {
    paramEntry.color = false;
    while (paramEntry != null && paramEntry != this.root && !paramEntry.parent.color) {
      if (parentOf(paramEntry) == leftOf(parentOf(parentOf(paramEntry)))) {
        Entry entry1 = rightOf(parentOf(parentOf(paramEntry)));
        if (!colorOf(entry1)) {
          setColor(parentOf(paramEntry), true);
          setColor(entry1, true);
          setColor(parentOf(parentOf(paramEntry)), false);
          paramEntry = parentOf(parentOf(paramEntry));
          continue;
        } 
        if (paramEntry == rightOf(parentOf(paramEntry))) {
          paramEntry = parentOf(paramEntry);
          rotateLeft(paramEntry);
        } 
        setColor(parentOf(paramEntry), true);
        setColor(parentOf(parentOf(paramEntry)), false);
        rotateRight(parentOf(parentOf(paramEntry)));
        continue;
      } 
      Entry entry = leftOf(parentOf(parentOf(paramEntry)));
      if (!colorOf(entry)) {
        setColor(parentOf(paramEntry), true);
        setColor(entry, true);
        setColor(parentOf(parentOf(paramEntry)), false);
        paramEntry = parentOf(parentOf(paramEntry));
        continue;
      } 
      if (paramEntry == leftOf(parentOf(paramEntry))) {
        paramEntry = parentOf(paramEntry);
        rotateRight(paramEntry);
      } 
      setColor(parentOf(paramEntry), true);
      setColor(parentOf(parentOf(paramEntry)), false);
      rotateLeft(parentOf(parentOf(paramEntry)));
    } 
    this.root.color = true;
  }
  
  private void deleteEntry(Entry<K, V> paramEntry) {
    this.modCount++;
    this.size--;
    if (paramEntry.left != null && paramEntry.right != null) {
      Entry entry1 = successor(paramEntry);
      paramEntry.key = entry1.key;
      paramEntry.value = entry1.value;
      paramEntry = entry1;
    } 
    Entry entry = (paramEntry.left != null) ? paramEntry.left : paramEntry.right;
    if (entry != null) {
      entry.parent = paramEntry.parent;
      if (paramEntry.parent == null) {
        this.root = entry;
      } else if (paramEntry == paramEntry.parent.left) {
        paramEntry.parent.left = entry;
      } else {
        paramEntry.parent.right = entry;
      } 
      paramEntry.left = paramEntry.right = paramEntry.parent = null;
      if (paramEntry.color == true)
        fixAfterDeletion(entry); 
    } else if (paramEntry.parent == null) {
      this.root = null;
    } else {
      if (paramEntry.color == true)
        fixAfterDeletion(paramEntry); 
      if (paramEntry.parent != null) {
        if (paramEntry == paramEntry.parent.left) {
          paramEntry.parent.left = null;
        } else if (paramEntry == paramEntry.parent.right) {
          paramEntry.parent.right = null;
        } 
        paramEntry.parent = null;
      } 
    } 
  }
  
  private void fixAfterDeletion(Entry<K, V> paramEntry) {
    while (paramEntry != this.root && colorOf(paramEntry) == true) {
      if (paramEntry == leftOf(parentOf(paramEntry))) {
        Entry entry1 = rightOf(parentOf(paramEntry));
        if (!colorOf(entry1)) {
          setColor(entry1, true);
          setColor(parentOf(paramEntry), false);
          rotateLeft(parentOf(paramEntry));
          entry1 = rightOf(parentOf(paramEntry));
        } 
        if (colorOf(leftOf(entry1)) == true && colorOf(rightOf(entry1)) == true) {
          setColor(entry1, false);
          paramEntry = parentOf(paramEntry);
          continue;
        } 
        if (colorOf(rightOf(entry1)) == true) {
          setColor(leftOf(entry1), true);
          setColor(entry1, false);
          rotateRight(entry1);
          entry1 = rightOf(parentOf(paramEntry));
        } 
        setColor(entry1, colorOf(parentOf(paramEntry)));
        setColor(parentOf(paramEntry), true);
        setColor(rightOf(entry1), true);
        rotateLeft(parentOf(paramEntry));
        paramEntry = this.root;
        continue;
      } 
      Entry entry = leftOf(parentOf(paramEntry));
      if (!colorOf(entry)) {
        setColor(entry, true);
        setColor(parentOf(paramEntry), false);
        rotateRight(parentOf(paramEntry));
        entry = leftOf(parentOf(paramEntry));
      } 
      if (colorOf(rightOf(entry)) == true && colorOf(leftOf(entry)) == true) {
        setColor(entry, false);
        paramEntry = parentOf(paramEntry);
        continue;
      } 
      if (colorOf(leftOf(entry)) == true) {
        setColor(rightOf(entry), true);
        setColor(entry, false);
        rotateLeft(entry);
        entry = leftOf(parentOf(paramEntry));
      } 
      setColor(entry, colorOf(parentOf(paramEntry)));
      setColor(parentOf(paramEntry), true);
      setColor(leftOf(entry), true);
      rotateRight(parentOf(paramEntry));
      paramEntry = this.root;
    } 
    setColor(paramEntry, true);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    for (Map.Entry entry : entrySet()) {
      paramObjectOutputStream.writeObject(entry.getKey());
      paramObjectOutputStream.writeObject(entry.getValue());
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    buildFromSorted(i, null, paramObjectInputStream, null);
  }
  
  void readTreeSet(int paramInt, ObjectInputStream paramObjectInputStream, V paramV) throws IOException, ClassNotFoundException { buildFromSorted(paramInt, null, paramObjectInputStream, paramV); }
  
  void addAllForTreeSet(SortedSet<? extends K> paramSortedSet, V paramV) {
    try {
      buildFromSorted(paramSortedSet.size(), paramSortedSet.iterator(), null, paramV);
    } catch (IOException iOException) {
    
    } catch (ClassNotFoundException classNotFoundException) {}
  }
  
  private void buildFromSorted(int paramInt, Iterator<?> paramIterator, ObjectInputStream paramObjectInputStream, V paramV) throws IOException, ClassNotFoundException {
    this.size = paramInt;
    this.root = buildFromSorted(0, 0, paramInt - 1, computeRedLevel(paramInt), paramIterator, paramObjectInputStream, paramV);
  }
  
  private final Entry<K, V> buildFromSorted(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Iterator<?> paramIterator, ObjectInputStream paramObjectInputStream, V paramV) throws IOException, ClassNotFoundException {
    V v;
    Object object;
    if (paramInt3 < paramInt2)
      return null; 
    int i = paramInt2 + paramInt3 >>> 1;
    Entry entry1 = null;
    if (paramInt2 < i)
      entry1 = buildFromSorted(paramInt1 + 1, paramInt2, i - 1, paramInt4, paramIterator, paramObjectInputStream, paramV); 
    if (paramIterator != null) {
      if (paramV == null) {
        Map.Entry entry = (Map.Entry)paramIterator.next();
        object = entry.getKey();
        Object object1 = entry.getValue();
      } else {
        object = paramIterator.next();
        v = paramV;
      } 
    } else {
      object = paramObjectInputStream.readObject();
      v = (paramV != null) ? paramV : paramObjectInputStream.readObject();
    } 
    Entry entry2 = new Entry(object, v, null);
    if (paramInt1 == paramInt4)
      entry2.color = false; 
    if (entry1 != null) {
      entry2.left = entry1;
      entry1.parent = entry2;
    } 
    if (i < paramInt3) {
      Entry entry = buildFromSorted(paramInt1 + 1, i + 1, paramInt3, paramInt4, paramIterator, paramObjectInputStream, paramV);
      entry2.right = entry;
      entry.parent = entry2;
    } 
    return entry2;
  }
  
  private static int computeRedLevel(int paramInt) {
    byte b = 0;
    for (int i = paramInt - 1; i >= 0; i = i / 2 - 1)
      b++; 
    return b;
  }
  
  static <K> Spliterator<K> keySpliteratorFor(NavigableMap<K, ?> paramNavigableMap) {
    if (paramNavigableMap instanceof TreeMap) {
      TreeMap treeMap = (TreeMap)paramNavigableMap;
      return treeMap.keySpliterator();
    } 
    if (paramNavigableMap instanceof DescendingSubMap) {
      DescendingSubMap descendingSubMap = (DescendingSubMap)paramNavigableMap;
      TreeMap treeMap = descendingSubMap.m;
      if (descendingSubMap == treeMap.descendingMap) {
        TreeMap treeMap1 = treeMap;
        return treeMap1.descendingKeySpliterator();
      } 
    } 
    NavigableSubMap navigableSubMap = (NavigableSubMap)paramNavigableMap;
    return navigableSubMap.keySpliterator();
  }
  
  final Spliterator<K> keySpliterator() { return new KeySpliterator(this, null, null, 0, -1, 0); }
  
  final Spliterator<K> descendingKeySpliterator() { return new DescendingKeySpliterator(this, null, null, 0, -2, 0); }
  
  static final class AscendingSubMap<K, V> extends NavigableSubMap<K, V> {
    private static final long serialVersionUID = 912986545866124060L;
    
    AscendingSubMap(TreeMap<K, V> param1TreeMap, boolean param1Boolean1, K param1K1, boolean param1Boolean2, boolean param1Boolean3, K param1K2, boolean param1Boolean4) { super(param1TreeMap, param1Boolean1, param1K1, param1Boolean2, param1Boolean3, param1K2, param1Boolean4); }
    
    public Comparator<? super K> comparator() { return this.m.comparator(); }
    
    public NavigableMap<K, V> subMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) {
      if (!inRange(param1K1, param1Boolean1))
        throw new IllegalArgumentException("fromKey out of range"); 
      if (!inRange(param1K2, param1Boolean2))
        throw new IllegalArgumentException("toKey out of range"); 
      return new AscendingSubMap(this.m, false, param1K1, param1Boolean1, false, param1K2, param1Boolean2);
    }
    
    public NavigableMap<K, V> headMap(K param1K, boolean param1Boolean) {
      if (!inRange(param1K, param1Boolean))
        throw new IllegalArgumentException("toKey out of range"); 
      return new AscendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, false, param1K, param1Boolean);
    }
    
    public NavigableMap<K, V> tailMap(K param1K, boolean param1Boolean) {
      if (!inRange(param1K, param1Boolean))
        throw new IllegalArgumentException("fromKey out of range"); 
      return new AscendingSubMap(this.m, false, param1K, param1Boolean, this.toEnd, this.hi, this.hiInclusive);
    }
    
    public NavigableMap<K, V> descendingMap() {
      NavigableMap navigableMap = this.descendingMapView;
      return (navigableMap != null) ? navigableMap : (this.descendingMapView = new TreeMap.DescendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, this.toEnd, this.hi, this.hiInclusive));
    }
    
    Iterator<K> keyIterator() { return new TreeMap.NavigableSubMap.SubMapKeyIterator(this, absLowest(), absHighFence()); }
    
    Spliterator<K> keySpliterator() { return new TreeMap.NavigableSubMap.SubMapKeyIterator(this, absLowest(), absHighFence()); }
    
    Iterator<K> descendingKeyIterator() { return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator(this, absHighest(), absLowFence()); }
    
    public Set<Map.Entry<K, V>> entrySet() {
      TreeMap.NavigableSubMap.EntrySetView entrySetView = this.entrySetView;
      return (entrySetView != null) ? entrySetView : (this.entrySetView = new AscendingEntrySetView());
    }
    
    TreeMap.Entry<K, V> subLowest() { return absLowest(); }
    
    TreeMap.Entry<K, V> subHighest() { return absHighest(); }
    
    TreeMap.Entry<K, V> subCeiling(K param1K) { return absCeiling(param1K); }
    
    TreeMap.Entry<K, V> subHigher(K param1K) { return absHigher(param1K); }
    
    TreeMap.Entry<K, V> subFloor(K param1K) { return absFloor(param1K); }
    
    TreeMap.Entry<K, V> subLower(K param1K) { return absLower(param1K); }
    
    final class AscendingEntrySetView extends TreeMap.NavigableSubMap<K, V>.EntrySetView {
      AscendingEntrySetView() { super(TreeMap.AscendingSubMap.this); }
      
      public Iterator<Map.Entry<K, V>> iterator() { return new TreeMap.NavigableSubMap.SubMapEntryIterator(TreeMap.AscendingSubMap.this, TreeMap.AscendingSubMap.this.absLowest(), TreeMap.AscendingSubMap.this.absHighFence()); }
    }
  }
  
  final class DescendingKeyIterator extends PrivateEntryIterator<K> {
    DescendingKeyIterator(TreeMap.Entry<K, V> param1Entry) { super(TreeMap.this, param1Entry); }
    
    public K next() { return (K)(prevEntry()).key; }
    
    public void remove() {
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      if (TreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      TreeMap.this.deleteEntry(this.lastReturned);
      this.lastReturned = null;
      this.expectedModCount = TreeMap.this.modCount;
    }
  }
  
  static final class DescendingKeySpliterator<K, V> extends TreeMapSpliterator<K, V> implements Spliterator<K> {
    DescendingKeySpliterator(TreeMap<K, V> param1TreeMap, TreeMap.Entry<K, V> param1Entry1, TreeMap.Entry<K, V> param1Entry2, int param1Int1, int param1Int2, int param1Int3) { super(param1TreeMap, param1Entry1, param1Entry2, param1Int1, param1Int2, param1Int3); }
    
    public DescendingKeySpliterator<K, V> trySplit() {
      if (this.est < 0)
        getEstimate(); 
      int i = this.side;
      TreeMap.Entry entry1 = this.current;
      TreeMap.Entry entry2 = this.fence;
      TreeMap.Entry entry3 = (entry1 == null || entry1 == entry2) ? null : ((i == 0) ? this.tree.root : ((i < 0) ? entry1.left : ((i > 0 && entry2 != null) ? entry2.right : null)));
      if (entry3 != null && entry3 != entry1 && entry3 != entry2 && this.tree.compare(entry1.key, entry3.key) > 0) {
        this.side = 1;
        return new DescendingKeySpliterator(this.tree, entry1, this.current = entry3, -1, this.est >>>= 1, this.expectedModCount);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry1 = this.fence;
      TreeMap.Entry entry2;
      if ((entry2 = this.current) != null && entry2 != entry1) {
        TreeMap.Entry entry;
        this.current = entry1;
        do {
          param1Consumer.accept(entry2.key);
          if ((entry = entry2.left) != null) {
            TreeMap.Entry entry3;
            while ((entry3 = entry.right) != null)
              entry = entry3; 
          } else {
            while ((entry = entry2.parent) != null && entry2 == entry.left)
              entry2 = entry; 
          } 
        } while ((entry2 = entry) != null && entry2 != entry1);
        if (this.tree.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
      } 
    }
    
    public boolean tryAdvance(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry;
      if ((entry = this.current) == null || entry == this.fence)
        return false; 
      this.current = TreeMap.predecessor(entry);
      param1Consumer.accept(entry.key);
      if (this.tree.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      return true;
    }
    
    public int characteristics() { return ((this.side == 0) ? 64 : 0) | true | 0x10; }
  }
  
  static final class DescendingSubMap<K, V> extends NavigableSubMap<K, V> {
    private static final long serialVersionUID = 912986545866120460L;
    
    private final Comparator<? super K> reverseComparator = Collections.reverseOrder(this.m.comparator);
    
    DescendingSubMap(TreeMap<K, V> param1TreeMap, boolean param1Boolean1, K param1K1, boolean param1Boolean2, boolean param1Boolean3, K param1K2, boolean param1Boolean4) { super(param1TreeMap, param1Boolean1, param1K1, param1Boolean2, param1Boolean3, param1K2, param1Boolean4); }
    
    public Comparator<? super K> comparator() { return this.reverseComparator; }
    
    public NavigableMap<K, V> subMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) {
      if (!inRange(param1K1, param1Boolean1))
        throw new IllegalArgumentException("fromKey out of range"); 
      if (!inRange(param1K2, param1Boolean2))
        throw new IllegalArgumentException("toKey out of range"); 
      return new DescendingSubMap(this.m, false, param1K2, param1Boolean2, false, param1K1, param1Boolean1);
    }
    
    public NavigableMap<K, V> headMap(K param1K, boolean param1Boolean) {
      if (!inRange(param1K, param1Boolean))
        throw new IllegalArgumentException("toKey out of range"); 
      return new DescendingSubMap(this.m, false, param1K, param1Boolean, this.toEnd, this.hi, this.hiInclusive);
    }
    
    public NavigableMap<K, V> tailMap(K param1K, boolean param1Boolean) {
      if (!inRange(param1K, param1Boolean))
        throw new IllegalArgumentException("fromKey out of range"); 
      return new DescendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, false, param1K, param1Boolean);
    }
    
    public NavigableMap<K, V> descendingMap() {
      NavigableMap navigableMap = this.descendingMapView;
      return (navigableMap != null) ? navigableMap : (this.descendingMapView = new TreeMap.AscendingSubMap(this.m, this.fromStart, this.lo, this.loInclusive, this.toEnd, this.hi, this.hiInclusive));
    }
    
    Iterator<K> keyIterator() { return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator(this, absHighest(), absLowFence()); }
    
    Spliterator<K> keySpliterator() { return new TreeMap.NavigableSubMap.DescendingSubMapKeyIterator(this, absHighest(), absLowFence()); }
    
    Iterator<K> descendingKeyIterator() { return new TreeMap.NavigableSubMap.SubMapKeyIterator(this, absLowest(), absHighFence()); }
    
    public Set<Map.Entry<K, V>> entrySet() {
      TreeMap.NavigableSubMap.EntrySetView entrySetView = this.entrySetView;
      return (entrySetView != null) ? entrySetView : (this.entrySetView = new DescendingEntrySetView());
    }
    
    TreeMap.Entry<K, V> subLowest() { return absHighest(); }
    
    TreeMap.Entry<K, V> subHighest() { return absLowest(); }
    
    TreeMap.Entry<K, V> subCeiling(K param1K) { return absFloor(param1K); }
    
    TreeMap.Entry<K, V> subHigher(K param1K) { return absLower(param1K); }
    
    TreeMap.Entry<K, V> subFloor(K param1K) { return absCeiling(param1K); }
    
    TreeMap.Entry<K, V> subLower(K param1K) { return absHigher(param1K); }
    
    final class DescendingEntrySetView extends TreeMap.NavigableSubMap<K, V>.EntrySetView {
      DescendingEntrySetView() { super(TreeMap.DescendingSubMap.this); }
      
      public Iterator<Map.Entry<K, V>> iterator() { return new TreeMap.NavigableSubMap.DescendingSubMapEntryIterator(TreeMap.DescendingSubMap.this, TreeMap.DescendingSubMap.this.absHighest(), TreeMap.DescendingSubMap.this.absLowFence()); }
    }
  }
  
  static final class Entry<K, V> extends Object implements Map.Entry<K, V> {
    K key;
    
    V value;
    
    Entry<K, V> left;
    
    Entry<K, V> right;
    
    Entry<K, V> parent;
    
    boolean color = true;
    
    Entry(K param1K, V param1V, Entry<K, V> param1Entry) {
      this.key = param1K;
      this.value = param1V;
      this.parent = param1Entry;
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
      return (TreeMap.valEquals(this.key, entry.getKey()) && TreeMap.valEquals(this.value, entry.getValue()));
    }
    
    public int hashCode() {
      byte b1 = (this.key == null) ? 0 : this.key.hashCode();
      byte b2 = (this.value == null) ? 0 : this.value.hashCode();
      return b1 ^ b2;
    }
    
    public String toString() { return this.key + "=" + this.value; }
  }
  
  final class EntryIterator extends PrivateEntryIterator<Map.Entry<K, V>> {
    EntryIterator(TreeMap.Entry<K, V> param1Entry) { super(TreeMap.this, param1Entry); }
    
    public Map.Entry<K, V> next() { return nextEntry(); }
  }
  
  class EntrySet extends AbstractSet<Map.Entry<K, V>> {
    public Iterator<Map.Entry<K, V>> iterator() { return new TreeMap.EntryIterator(TreeMap.this, TreeMap.this.getFirstEntry()); }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getValue();
      TreeMap.Entry entry1 = TreeMap.this.getEntry(entry.getKey());
      return (entry1 != null && TreeMap.valEquals(entry1.getValue(), object));
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getValue();
      TreeMap.Entry entry1 = TreeMap.this.getEntry(entry.getKey());
      if (entry1 != null && TreeMap.valEquals(entry1.getValue(), object)) {
        TreeMap.this.deleteEntry(entry1);
        return true;
      } 
      return false;
    }
    
    public int size() { return TreeMap.this.size(); }
    
    public void clear() { TreeMap.this.clear(); }
    
    public Spliterator<Map.Entry<K, V>> spliterator() { return new TreeMap.EntrySpliterator(TreeMap.this, null, null, 0, -1, 0); }
  }
  
  static final class EntrySpliterator<K, V> extends TreeMapSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
    EntrySpliterator(TreeMap<K, V> param1TreeMap, TreeMap.Entry<K, V> param1Entry1, TreeMap.Entry<K, V> param1Entry2, int param1Int1, int param1Int2, int param1Int3) { super(param1TreeMap, param1Entry1, param1Entry2, param1Int1, param1Int2, param1Int3); }
    
    public EntrySpliterator<K, V> trySplit() {
      if (this.est < 0)
        getEstimate(); 
      int i = this.side;
      TreeMap.Entry entry1 = this.current;
      TreeMap.Entry entry2 = this.fence;
      TreeMap.Entry entry3 = (entry1 == null || entry1 == entry2) ? null : ((i == 0) ? this.tree.root : ((i > 0) ? entry1.right : ((i < 0 && entry2 != null) ? entry2.left : null)));
      if (entry3 != null && entry3 != entry1 && entry3 != entry2 && this.tree.compare(entry1.key, entry3.key) < 0) {
        this.side = 1;
        return new EntrySpliterator(this.tree, entry1, this.current = entry3, -1, this.est >>>= 1, this.expectedModCount);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry1 = this.fence;
      TreeMap.Entry entry2;
      if ((entry2 = this.current) != null && entry2 != entry1) {
        TreeMap.Entry entry;
        this.current = entry1;
        do {
          param1Consumer.accept(entry2);
          if ((entry = entry2.right) != null) {
            TreeMap.Entry entry3;
            while ((entry3 = entry.left) != null)
              entry = entry3; 
          } else {
            while ((entry = entry2.parent) != null && entry2 == entry.right)
              entry2 = entry; 
          } 
        } while ((entry2 = entry) != null && entry2 != entry1);
        if (this.tree.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
      } 
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry;
      if ((entry = this.current) == null || entry == this.fence)
        return false; 
      this.current = TreeMap.successor(entry);
      param1Consumer.accept(entry);
      if (this.tree.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      return true;
    }
    
    public int characteristics() { return ((this.side == 0) ? 64 : 0) | true | 0x4 | 0x10; }
    
    public Comparator<Map.Entry<K, V>> getComparator() { return (this.tree.comparator != null) ? Map.Entry.comparingByKey(this.tree.comparator) : (Comparator)((param1Entry1, param1Entry2) -> {
          Comparable comparable = (Comparable)param1Entry1.getKey();
          return comparable.compareTo(param1Entry2.getKey());
        }); }
  }
  
  final class KeyIterator extends PrivateEntryIterator<K> {
    KeyIterator(TreeMap.Entry<K, V> param1Entry) { super(TreeMap.this, param1Entry); }
    
    public K next() { return (K)(nextEntry()).key; }
  }
  
  static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    private final NavigableMap<E, ?> m;
    
    KeySet(NavigableMap<E, ?> param1NavigableMap) { this.m = param1NavigableMap; }
    
    public Iterator<E> iterator() { return (this.m instanceof TreeMap) ? ((TreeMap)this.m).keyIterator() : ((TreeMap.NavigableSubMap)this.m).keyIterator(); }
    
    public Iterator<E> descendingIterator() { return (this.m instanceof TreeMap) ? ((TreeMap)this.m).descendingKeyIterator() : ((TreeMap.NavigableSubMap)this.m).descendingKeyIterator(); }
    
    public int size() { return this.m.size(); }
    
    public boolean isEmpty() { return this.m.isEmpty(); }
    
    public boolean contains(Object param1Object) { return this.m.containsKey(param1Object); }
    
    public void clear() { this.m.clear(); }
    
    public E lower(E param1E) { return (E)this.m.lowerKey(param1E); }
    
    public E floor(E param1E) { return (E)this.m.floorKey(param1E); }
    
    public E ceiling(E param1E) { return (E)this.m.ceilingKey(param1E); }
    
    public E higher(E param1E) { return (E)this.m.higherKey(param1E); }
    
    public E first() { return (E)this.m.firstKey(); }
    
    public E last() { return (E)this.m.lastKey(); }
    
    public Comparator<? super E> comparator() { return this.m.comparator(); }
    
    public E pollFirst() {
      Map.Entry entry = this.m.pollFirstEntry();
      return (E)((entry == null) ? null : entry.getKey());
    }
    
    public E pollLast() {
      Map.Entry entry = this.m.pollLastEntry();
      return (E)((entry == null) ? null : entry.getKey());
    }
    
    public boolean remove(Object param1Object) {
      int i = size();
      this.m.remove(param1Object);
      return (size() != i);
    }
    
    public NavigableSet<E> subSet(E param1E1, boolean param1Boolean1, E param1E2, boolean param1Boolean2) { return new KeySet(this.m.subMap(param1E1, param1Boolean1, param1E2, param1Boolean2)); }
    
    public NavigableSet<E> headSet(E param1E, boolean param1Boolean) { return new KeySet(this.m.headMap(param1E, param1Boolean)); }
    
    public NavigableSet<E> tailSet(E param1E, boolean param1Boolean) { return new KeySet(this.m.tailMap(param1E, param1Boolean)); }
    
    public SortedSet<E> subSet(E param1E1, E param1E2) { return subSet(param1E1, true, param1E2, false); }
    
    public SortedSet<E> headSet(E param1E) { return headSet(param1E, false); }
    
    public SortedSet<E> tailSet(E param1E) { return tailSet(param1E, true); }
    
    public NavigableSet<E> descendingSet() { return new KeySet(this.m.descendingMap()); }
    
    public Spliterator<E> spliterator() { return TreeMap.keySpliteratorFor(this.m); }
  }
  
  static final class KeySpliterator<K, V> extends TreeMapSpliterator<K, V> implements Spliterator<K> {
    KeySpliterator(TreeMap<K, V> param1TreeMap, TreeMap.Entry<K, V> param1Entry1, TreeMap.Entry<K, V> param1Entry2, int param1Int1, int param1Int2, int param1Int3) { super(param1TreeMap, param1Entry1, param1Entry2, param1Int1, param1Int2, param1Int3); }
    
    public KeySpliterator<K, V> trySplit() {
      if (this.est < 0)
        getEstimate(); 
      int i = this.side;
      TreeMap.Entry entry1 = this.current;
      TreeMap.Entry entry2 = this.fence;
      TreeMap.Entry entry3 = (entry1 == null || entry1 == entry2) ? null : ((i == 0) ? this.tree.root : ((i > 0) ? entry1.right : ((i < 0 && entry2 != null) ? entry2.left : null)));
      if (entry3 != null && entry3 != entry1 && entry3 != entry2 && this.tree.compare(entry1.key, entry3.key) < 0) {
        this.side = 1;
        return new KeySpliterator(this.tree, entry1, this.current = entry3, -1, this.est >>>= 1, this.expectedModCount);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry1 = this.fence;
      TreeMap.Entry entry2;
      if ((entry2 = this.current) != null && entry2 != entry1) {
        TreeMap.Entry entry;
        this.current = entry1;
        do {
          param1Consumer.accept(entry2.key);
          if ((entry = entry2.right) != null) {
            TreeMap.Entry entry3;
            while ((entry3 = entry.left) != null)
              entry = entry3; 
          } else {
            while ((entry = entry2.parent) != null && entry2 == entry.right)
              entry2 = entry; 
          } 
        } while ((entry2 = entry) != null && entry2 != entry1);
        if (this.tree.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
      } 
    }
    
    public boolean tryAdvance(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry;
      if ((entry = this.current) == null || entry == this.fence)
        return false; 
      this.current = TreeMap.successor(entry);
      param1Consumer.accept(entry.key);
      if (this.tree.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      return true;
    }
    
    public int characteristics() { return ((this.side == 0) ? 64 : 0) | true | 0x4 | 0x10; }
    
    public final Comparator<? super K> getComparator() { return this.tree.comparator; }
  }
  
  static abstract class NavigableSubMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Serializable {
    private static final long serialVersionUID = -2102997345730753016L;
    
    final TreeMap<K, V> m;
    
    final K lo;
    
    final K hi;
    
    final boolean fromStart;
    
    final boolean toEnd;
    
    final boolean loInclusive;
    
    final boolean hiInclusive;
    
    NavigableMap<K, V> descendingMapView;
    
    EntrySetView entrySetView;
    
    TreeMap.KeySet<K> navigableKeySetView;
    
    NavigableSubMap(TreeMap<K, V> param1TreeMap, boolean param1Boolean1, K param1K1, boolean param1Boolean2, boolean param1Boolean3, K param1K2, boolean param1Boolean4) {
      if (!param1Boolean1 && !param1Boolean3) {
        if (param1TreeMap.compare(param1K1, param1K2) > 0)
          throw new IllegalArgumentException("fromKey > toKey"); 
      } else {
        if (!param1Boolean1)
          param1TreeMap.compare(param1K1, param1K1); 
        if (!param1Boolean3)
          param1TreeMap.compare(param1K2, param1K2); 
      } 
      this.m = param1TreeMap;
      this.fromStart = param1Boolean1;
      this.lo = param1K1;
      this.loInclusive = param1Boolean2;
      this.toEnd = param1Boolean3;
      this.hi = param1K2;
      this.hiInclusive = param1Boolean4;
    }
    
    final boolean tooLow(Object param1Object) {
      if (!this.fromStart) {
        int i = this.m.compare(param1Object, this.lo);
        if (i < 0 || (i == 0 && !this.loInclusive))
          return true; 
      } 
      return false;
    }
    
    final boolean tooHigh(Object param1Object) {
      if (!this.toEnd) {
        int i = this.m.compare(param1Object, this.hi);
        if (i > 0 || (i == 0 && !this.hiInclusive))
          return true; 
      } 
      return false;
    }
    
    final boolean inRange(Object param1Object) { return (!tooLow(param1Object) && !tooHigh(param1Object)); }
    
    final boolean inClosedRange(Object param1Object) { return ((this.fromStart || this.m.compare(param1Object, this.lo) >= 0) && (this.toEnd || this.m.compare(this.hi, param1Object) >= 0)); }
    
    final boolean inRange(Object param1Object, boolean param1Boolean) { return param1Boolean ? inRange(param1Object) : inClosedRange(param1Object); }
    
    final TreeMap.Entry<K, V> absLowest() {
      TreeMap.Entry entry = this.fromStart ? this.m.getFirstEntry() : (this.loInclusive ? this.m.getCeilingEntry(this.lo) : this.m.getHigherEntry(this.lo));
      return (entry == null || tooHigh(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absHighest() {
      TreeMap.Entry entry = this.toEnd ? this.m.getLastEntry() : (this.hiInclusive ? this.m.getFloorEntry(this.hi) : this.m.getLowerEntry(this.hi));
      return (entry == null || tooLow(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absCeiling(K param1K) {
      if (tooLow(param1K))
        return absLowest(); 
      TreeMap.Entry entry = this.m.getCeilingEntry(param1K);
      return (entry == null || tooHigh(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absHigher(K param1K) {
      if (tooLow(param1K))
        return absLowest(); 
      TreeMap.Entry entry = this.m.getHigherEntry(param1K);
      return (entry == null || tooHigh(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absFloor(K param1K) {
      if (tooHigh(param1K))
        return absHighest(); 
      TreeMap.Entry entry = this.m.getFloorEntry(param1K);
      return (entry == null || tooLow(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absLower(K param1K) {
      if (tooHigh(param1K))
        return absHighest(); 
      TreeMap.Entry entry = this.m.getLowerEntry(param1K);
      return (entry == null || tooLow(entry.key)) ? null : entry;
    }
    
    final TreeMap.Entry<K, V> absHighFence() { return this.toEnd ? null : (this.hiInclusive ? this.m.getHigherEntry(this.hi) : this.m.getCeilingEntry(this.hi)); }
    
    final TreeMap.Entry<K, V> absLowFence() { return this.fromStart ? null : (this.loInclusive ? this.m.getLowerEntry(this.lo) : this.m.getFloorEntry(this.lo)); }
    
    abstract TreeMap.Entry<K, V> subLowest();
    
    abstract TreeMap.Entry<K, V> subHighest();
    
    abstract TreeMap.Entry<K, V> subCeiling(K param1K);
    
    abstract TreeMap.Entry<K, V> subHigher(K param1K);
    
    abstract TreeMap.Entry<K, V> subFloor(K param1K);
    
    abstract TreeMap.Entry<K, V> subLower(K param1K);
    
    abstract Iterator<K> keyIterator();
    
    abstract Spliterator<K> keySpliterator();
    
    abstract Iterator<K> descendingKeyIterator();
    
    public boolean isEmpty() { return (this.fromStart && this.toEnd) ? this.m.isEmpty() : entrySet().isEmpty(); }
    
    public int size() { return (this.fromStart && this.toEnd) ? this.m.size() : entrySet().size(); }
    
    public final boolean containsKey(Object param1Object) { return (inRange(param1Object) && this.m.containsKey(param1Object)); }
    
    public final V put(K param1K, V param1V) {
      if (!inRange(param1K))
        throw new IllegalArgumentException("key out of range"); 
      return (V)this.m.put(param1K, param1V);
    }
    
    public final V get(Object param1Object) { return (V)(!inRange(param1Object) ? null : this.m.get(param1Object)); }
    
    public final V remove(Object param1Object) { return (V)(!inRange(param1Object) ? null : this.m.remove(param1Object)); }
    
    public final Map.Entry<K, V> ceilingEntry(K param1K) { return TreeMap.exportEntry(subCeiling(param1K)); }
    
    public final K ceilingKey(K param1K) { return (K)TreeMap.keyOrNull(subCeiling(param1K)); }
    
    public final Map.Entry<K, V> higherEntry(K param1K) { return TreeMap.exportEntry(subHigher(param1K)); }
    
    public final K higherKey(K param1K) { return (K)TreeMap.keyOrNull(subHigher(param1K)); }
    
    public final Map.Entry<K, V> floorEntry(K param1K) { return TreeMap.exportEntry(subFloor(param1K)); }
    
    public final K floorKey(K param1K) { return (K)TreeMap.keyOrNull(subFloor(param1K)); }
    
    public final Map.Entry<K, V> lowerEntry(K param1K) { return TreeMap.exportEntry(subLower(param1K)); }
    
    public final K lowerKey(K param1K) { return (K)TreeMap.keyOrNull(subLower(param1K)); }
    
    public final K firstKey() { return (K)TreeMap.key(subLowest()); }
    
    public final K lastKey() { return (K)TreeMap.key(subHighest()); }
    
    public final Map.Entry<K, V> firstEntry() { return TreeMap.exportEntry(subLowest()); }
    
    public final Map.Entry<K, V> lastEntry() { return TreeMap.exportEntry(subHighest()); }
    
    public final Map.Entry<K, V> pollFirstEntry() {
      TreeMap.Entry entry = subLowest();
      Map.Entry entry1 = TreeMap.exportEntry(entry);
      if (entry != null)
        this.m.deleteEntry(entry); 
      return entry1;
    }
    
    public final Map.Entry<K, V> pollLastEntry() {
      TreeMap.Entry entry = subHighest();
      Map.Entry entry1 = TreeMap.exportEntry(entry);
      if (entry != null)
        this.m.deleteEntry(entry); 
      return entry1;
    }
    
    public final NavigableSet<K> navigableKeySet() {
      TreeMap.KeySet keySet = this.navigableKeySetView;
      return (keySet != null) ? keySet : (this.navigableKeySetView = new TreeMap.KeySet(this));
    }
    
    public final Set<K> keySet() { return navigableKeySet(); }
    
    public NavigableSet<K> descendingKeySet() { return descendingMap().navigableKeySet(); }
    
    public final SortedMap<K, V> subMap(K param1K1, K param1K2) { return subMap(param1K1, true, param1K2, false); }
    
    public final SortedMap<K, V> headMap(K param1K) { return headMap(param1K, false); }
    
    public final SortedMap<K, V> tailMap(K param1K) { return tailMap(param1K, true); }
    
    final class DescendingSubMapEntryIterator extends SubMapIterator<Map.Entry<K, V>> {
      DescendingSubMapEntryIterator(TreeMap.Entry<K, V> param2Entry1, TreeMap.Entry<K, V> param2Entry2) { super(TreeMap.NavigableSubMap.this, param2Entry1, param2Entry2); }
      
      public Map.Entry<K, V> next() { return prevEntry(); }
      
      public void remove() { removeDescending(); }
    }
    
    final class DescendingSubMapKeyIterator extends SubMapIterator<K> implements Spliterator<K> {
      DescendingSubMapKeyIterator(TreeMap.Entry<K, V> param2Entry1, TreeMap.Entry<K, V> param2Entry2) { super(TreeMap.NavigableSubMap.this, param2Entry1, param2Entry2); }
      
      public K next() { return (K)(prevEntry()).key; }
      
      public void remove() { removeDescending(); }
      
      public Spliterator<K> trySplit() { return null; }
      
      public void forEachRemaining(Consumer<? super K> param2Consumer) {
        while (hasNext())
          param2Consumer.accept(next()); 
      }
      
      public boolean tryAdvance(Consumer<? super K> param2Consumer) {
        if (hasNext()) {
          param2Consumer.accept(next());
          return true;
        } 
        return false;
      }
      
      public long estimateSize() { return Float.MAX_VALUE; }
      
      public int characteristics() { return 17; }
    }
    
    abstract class EntrySetView extends AbstractSet<Map.Entry<K, V>> {
      private int size = -1;
      
      private int sizeModCount;
      
      public int size() {
        if (TreeMap.NavigableSubMap.this.fromStart && TreeMap.NavigableSubMap.this.toEnd)
          return TreeMap.NavigableSubMap.this.m.size(); 
        if (this.size == -1 || this.sizeModCount != TreeMap.NavigableSubMap.this.m.modCount) {
          this.sizeModCount = TreeMap.NavigableSubMap.this.m.modCount;
          this.size = 0;
          Iterator iterator = iterator();
          while (iterator.hasNext()) {
            this.size++;
            iterator.next();
          } 
        } 
        return this.size;
      }
      
      public boolean isEmpty() {
        TreeMap.Entry entry = TreeMap.NavigableSubMap.this.absLowest();
        return (entry == null || TreeMap.NavigableSubMap.this.tooHigh(entry.key));
      }
      
      public boolean contains(Object param2Object) {
        if (!(param2Object instanceof Map.Entry))
          return false; 
        Map.Entry entry = (Map.Entry)param2Object;
        Object object = entry.getKey();
        if (!TreeMap.NavigableSubMap.this.inRange(object))
          return false; 
        TreeMap.Entry entry1 = TreeMap.NavigableSubMap.this.m.getEntry(object);
        return (entry1 != null && TreeMap.valEquals(entry1.getValue(), entry.getValue()));
      }
      
      public boolean remove(Object param2Object) {
        if (!(param2Object instanceof Map.Entry))
          return false; 
        Map.Entry entry = (Map.Entry)param2Object;
        Object object = entry.getKey();
        if (!TreeMap.NavigableSubMap.this.inRange(object))
          return false; 
        TreeMap.Entry entry1 = TreeMap.NavigableSubMap.this.m.getEntry(object);
        if (entry1 != null && TreeMap.valEquals(entry1.getValue(), entry.getValue())) {
          TreeMap.NavigableSubMap.this.m.deleteEntry(entry1);
          return true;
        } 
        return false;
      }
    }
    
    final class SubMapEntryIterator extends SubMapIterator<Map.Entry<K, V>> {
      SubMapEntryIterator(TreeMap.Entry<K, V> param2Entry1, TreeMap.Entry<K, V> param2Entry2) { super(TreeMap.NavigableSubMap.this, param2Entry1, param2Entry2); }
      
      public Map.Entry<K, V> next() { return nextEntry(); }
      
      public void remove() { removeAscending(); }
    }
    
    abstract class SubMapIterator<T> extends Object implements Iterator<T> {
      TreeMap.Entry<K, V> lastReturned = null;
      
      TreeMap.Entry<K, V> next;
      
      final Object fenceKey;
      
      int expectedModCount = TreeMap.NavigableSubMap.this.m.modCount;
      
      SubMapIterator(TreeMap.Entry<K, V> param2Entry1, TreeMap.Entry<K, V> param2Entry2) {
        this.next = param2Entry1;
        this.fenceKey = (param2Entry2 == null) ? UNBOUNDED : param2Entry2.key;
      }
      
      public final boolean hasNext() { return (this.next != null && this.next.key != this.fenceKey); }
      
      final TreeMap.Entry<K, V> nextEntry() {
        TreeMap.Entry entry = this.next;
        if (entry == null || entry.key == this.fenceKey)
          throw new NoSuchElementException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        this.next = TreeMap.successor(entry);
        this.lastReturned = entry;
        return entry;
      }
      
      final TreeMap.Entry<K, V> prevEntry() {
        TreeMap.Entry entry = this.next;
        if (entry == null || entry.key == this.fenceKey)
          throw new NoSuchElementException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        this.next = TreeMap.predecessor(entry);
        this.lastReturned = entry;
        return entry;
      }
      
      final void removeAscending() {
        if (this.lastReturned == null)
          throw new IllegalStateException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        if (this.lastReturned.left != null && this.lastReturned.right != null)
          this.next = this.lastReturned; 
        TreeMap.NavigableSubMap.this.m.deleteEntry(this.lastReturned);
        this.lastReturned = null;
        this.expectedModCount = TreeMap.NavigableSubMap.this.m.modCount;
      }
      
      final void removeDescending() {
        if (this.lastReturned == null)
          throw new IllegalStateException(); 
        if (TreeMap.NavigableSubMap.this.m.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        TreeMap.NavigableSubMap.this.m.deleteEntry(this.lastReturned);
        this.lastReturned = null;
        this.expectedModCount = TreeMap.NavigableSubMap.this.m.modCount;
      }
    }
    
    final class SubMapKeyIterator extends SubMapIterator<K> implements Spliterator<K> {
      SubMapKeyIterator(TreeMap.Entry<K, V> param2Entry1, TreeMap.Entry<K, V> param2Entry2) { super(TreeMap.NavigableSubMap.this, param2Entry1, param2Entry2); }
      
      public K next() { return (K)(nextEntry()).key; }
      
      public void remove() { removeAscending(); }
      
      public Spliterator<K> trySplit() { return null; }
      
      public void forEachRemaining(Consumer<? super K> param2Consumer) {
        while (hasNext())
          param2Consumer.accept(next()); 
      }
      
      public boolean tryAdvance(Consumer<? super K> param2Consumer) {
        if (hasNext()) {
          param2Consumer.accept(next());
          return true;
        } 
        return false;
      }
      
      public long estimateSize() { return Float.MAX_VALUE; }
      
      public int characteristics() { return 21; }
      
      public final Comparator<? super K> getComparator() { return TreeMap.NavigableSubMap.this.comparator(); }
    }
  }
  
  abstract class PrivateEntryIterator<T> extends Object implements Iterator<T> {
    TreeMap.Entry<K, V> next;
    
    TreeMap.Entry<K, V> lastReturned;
    
    int expectedModCount;
    
    PrivateEntryIterator(TreeMap.Entry<K, V> param1Entry) {
      this.expectedModCount = this$0.modCount;
      this.lastReturned = null;
      this.next = param1Entry;
    }
    
    public final boolean hasNext() { return (this.next != null); }
    
    final TreeMap.Entry<K, V> nextEntry() {
      TreeMap.Entry entry = this.next;
      if (entry == null)
        throw new NoSuchElementException(); 
      if (TreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.next = TreeMap.successor(entry);
      this.lastReturned = entry;
      return entry;
    }
    
    final TreeMap.Entry<K, V> prevEntry() {
      TreeMap.Entry entry = this.next;
      if (entry == null)
        throw new NoSuchElementException(); 
      if (TreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.next = TreeMap.predecessor(entry);
      this.lastReturned = entry;
      return entry;
    }
    
    public void remove() {
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      if (TreeMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      if (this.lastReturned.left != null && this.lastReturned.right != null)
        this.next = this.lastReturned; 
      TreeMap.this.deleteEntry(this.lastReturned);
      this.expectedModCount = TreeMap.this.modCount;
      this.lastReturned = null;
    }
  }
  
  private class SubMap extends AbstractMap<K, V> implements SortedMap<K, V>, Serializable {
    private static final long serialVersionUID = -6520786458950516097L;
    
    private boolean fromStart = false;
    
    private boolean toEnd = false;
    
    private K fromKey;
    
    private K toKey;
    
    private Object readResolve() { return new TreeMap.AscendingSubMap(TreeMap.this, this.fromStart, this.fromKey, true, this.toEnd, this.toKey, false); }
    
    public Set<Map.Entry<K, V>> entrySet() { throw new InternalError(); }
    
    public K lastKey() { throw new InternalError(); }
    
    public K firstKey() { throw new InternalError(); }
    
    public SortedMap<K, V> subMap(K param1K1, K param1K2) { throw new InternalError(); }
    
    public SortedMap<K, V> headMap(K param1K) { throw new InternalError(); }
    
    public SortedMap<K, V> tailMap(K param1K) { throw new InternalError(); }
    
    public Comparator<? super K> comparator() { throw new InternalError(); }
  }
  
  static class TreeMapSpliterator<K, V> extends Object {
    final TreeMap<K, V> tree;
    
    TreeMap.Entry<K, V> current;
    
    TreeMap.Entry<K, V> fence;
    
    int side;
    
    int est;
    
    int expectedModCount;
    
    TreeMapSpliterator(TreeMap<K, V> param1TreeMap, TreeMap.Entry<K, V> param1Entry1, TreeMap.Entry<K, V> param1Entry2, int param1Int1, int param1Int2, int param1Int3) {
      this.tree = param1TreeMap;
      this.current = param1Entry1;
      this.fence = param1Entry2;
      this.side = param1Int1;
      this.est = param1Int2;
      this.expectedModCount = param1Int3;
    }
    
    final int getEstimate() {
      int i;
      if ((i = this.est) < 0) {
        TreeMap treeMap;
        if ((treeMap = this.tree) != null) {
          this.current = (i == -1) ? treeMap.getFirstEntry() : treeMap.getLastEntry();
          i = this.est = treeMap.size;
          this.expectedModCount = treeMap.modCount;
        } else {
          i = this.est = 0;
        } 
      } 
      return i;
    }
    
    public final long estimateSize() { return getEstimate(); }
  }
  
  final class ValueIterator extends PrivateEntryIterator<V> {
    ValueIterator(TreeMap.Entry<K, V> param1Entry) { super(TreeMap.this, param1Entry); }
    
    public V next() { return (V)(nextEntry()).value; }
  }
  
  static final class ValueSpliterator<K, V> extends TreeMapSpliterator<K, V> implements Spliterator<V> {
    ValueSpliterator(TreeMap<K, V> param1TreeMap, TreeMap.Entry<K, V> param1Entry1, TreeMap.Entry<K, V> param1Entry2, int param1Int1, int param1Int2, int param1Int3) { super(param1TreeMap, param1Entry1, param1Entry2, param1Int1, param1Int2, param1Int3); }
    
    public ValueSpliterator<K, V> trySplit() {
      if (this.est < 0)
        getEstimate(); 
      int i = this.side;
      TreeMap.Entry entry1 = this.current;
      TreeMap.Entry entry2 = this.fence;
      TreeMap.Entry entry3 = (entry1 == null || entry1 == entry2) ? null : ((i == 0) ? this.tree.root : ((i > 0) ? entry1.right : ((i < 0 && entry2 != null) ? entry2.left : null)));
      if (entry3 != null && entry3 != entry1 && entry3 != entry2 && this.tree.compare(entry1.key, entry3.key) < 0) {
        this.side = 1;
        return new ValueSpliterator(this.tree, entry1, this.current = entry3, -1, this.est >>>= 1, this.expectedModCount);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry1 = this.fence;
      TreeMap.Entry entry2;
      if ((entry2 = this.current) != null && entry2 != entry1) {
        TreeMap.Entry entry;
        this.current = entry1;
        do {
          param1Consumer.accept(entry2.value);
          if ((entry = entry2.right) != null) {
            TreeMap.Entry entry3;
            while ((entry3 = entry.left) != null)
              entry = entry3; 
          } else {
            while ((entry = entry2.parent) != null && entry2 == entry.right)
              entry2 = entry; 
          } 
        } while ((entry2 = entry) != null && entry2 != entry1);
        if (this.tree.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
      } 
    }
    
    public boolean tryAdvance(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.est < 0)
        getEstimate(); 
      TreeMap.Entry entry;
      if ((entry = this.current) == null || entry == this.fence)
        return false; 
      this.current = TreeMap.successor(entry);
      param1Consumer.accept(entry.value);
      if (this.tree.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      return true;
    }
    
    public int characteristics() { return ((this.side == 0) ? 64 : 0) | 0x10; }
  }
  
  class Values extends AbstractCollection<V> {
    public Iterator<V> iterator() { return new TreeMap.ValueIterator(TreeMap.this, TreeMap.this.getFirstEntry()); }
    
    public int size() { return TreeMap.this.size(); }
    
    public boolean contains(Object param1Object) { return TreeMap.this.containsValue(param1Object); }
    
    public boolean remove(Object param1Object) {
      for (TreeMap.Entry entry = TreeMap.this.getFirstEntry(); entry != null; entry = TreeMap.successor(entry)) {
        if (TreeMap.valEquals(entry.getValue(), param1Object)) {
          TreeMap.this.deleteEntry(entry);
          return true;
        } 
      } 
      return false;
    }
    
    public void clear() { TreeMap.this.clear(); }
    
    public Spliterator<V> spliterator() { return new TreeMap.ValueSpliterator(TreeMap.this, null, null, 0, -1, 0); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\TreeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */