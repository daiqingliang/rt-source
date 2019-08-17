package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import sun.misc.Unsafe;

public class ConcurrentSkipListMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable {
  private static final long serialVersionUID = -8627078645895051609L;
  
  private static final Object BASE_HEADER = new Object();
  
  final Comparator<? super K> comparator = null;
  
  private KeySet<K> keySet;
  
  private EntrySet<K, V> entrySet;
  
  private Values<V> values;
  
  private ConcurrentNavigableMap<K, V> descendingMap;
  
  private static final int EQ = 1;
  
  private static final int LT = 2;
  
  private static final int GT = 0;
  
  private static final Unsafe UNSAFE;
  
  private static final long headOffset;
  
  private static final long SECONDARY;
  
  private void initialize() {
    this.keySet = null;
    this.entrySet = null;
    this.values = null;
    this.descendingMap = null;
    this.head = new HeadIndex(new Node(null, BASE_HEADER, null), null, null, 1);
  }
  
  private boolean casHead(HeadIndex<K, V> paramHeadIndex1, HeadIndex<K, V> paramHeadIndex2) { return UNSAFE.compareAndSwapObject(this, headOffset, paramHeadIndex1, paramHeadIndex2); }
  
  static final int cpr(Comparator paramComparator, Object paramObject1, Object paramObject2) { return (paramComparator != null) ? paramComparator.compare(paramObject1, paramObject2) : ((Comparable)paramObject1).compareTo(paramObject2); }
  
  private Node<K, V> findPredecessor(Object paramObject, Comparator<? super K> paramComparator) {
    if (paramObject == null)
      throw new NullPointerException(); 
    label22: while (true) {
      HeadIndex headIndex = this.head;
      for (Index index = headIndex.right;; index = index2.right) {
        if (index != null) {
          Node node = index.node;
          Object object = node.key;
          if (node.value == null) {
            if (!headIndex.unlink(index))
              continue label22; 
            index = headIndex.right;
            continue;
          } 
          if (cpr(paramComparator, paramObject, object) > 0) {
            index1 = index;
            index = index.right;
            continue;
          } 
        } 
        Index index2;
        if ((index2 = index1.down) == null)
          return index1.node; 
        Index index1 = index2;
      } 
      break;
    } 
  }
  
  private Node<K, V> findNode(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    Comparator comparator1 = this.comparator;
    label31: while (true) {
      Node node1 = findPredecessor(paramObject, comparator1);
      Node node2 = node1.next;
      while (node2 != null) {
        Node node = node2.next;
        if (node2 != node1.next)
          continue label31; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label31;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label31; 
          int i;
          if ((i = cpr(comparator1, paramObject, node2.key)) == 0)
            return node2; 
          if (i < 0)
            break; 
          node1 = node2;
          node2 = node;
          continue;
        } 
        continue label31;
      } 
      break;
    } 
    return null;
  }
  
  private V doGet(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    Comparator comparator1 = this.comparator;
    label31: while (true) {
      Node node1 = findPredecessor(paramObject, comparator1);
      Node node2 = node1.next;
      while (node2 != null) {
        Node node = node2.next;
        if (node2 != node1.next)
          continue label31; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label31;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label31; 
          int i;
          if ((i = cpr(comparator1, paramObject, node2.key)) == 0)
            return (V)object; 
          if (i < 0)
            break; 
          node1 = node2;
          node2 = node;
          continue;
        } 
        continue label31;
      } 
      break;
    } 
    return null;
  }
  
  private V doPut(K paramK, V paramV, boolean paramBoolean) {
    Node node;
    if (paramK == null)
      throw new NullPointerException(); 
    Comparator comparator1 = this.comparator;
    label98: while (true) {
      Node node1 = findPredecessor(paramK, comparator1);
      Node node2 = node1.next;
      while (node2 != null) {
        Node node3 = node2.next;
        if (node2 != node1.next)
          continue label98; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node3);
          continue label98;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label98; 
          int j;
          if ((j = cpr(comparator1, paramK, node2.key)) > 0) {
            node1 = node2;
            node2 = node3;
            continue;
          } 
          if (j == 0) {
            if (!paramBoolean) {
              if (node2.casValue(object, paramV))
                return (V)object; 
              continue;
            } 
            continue;
          } 
          break;
        } 
        continue label98;
      } 
      node = new Node(paramK, paramV, node2);
      if (!node1.casNext(node2, node))
        continue; 
      break;
    } 
    int i = ThreadLocalRandom.nextSecondarySeed();
    if ((i & 0x80000001) == 0) {
      int j;
      for (j = 1; (i >>>= 1 & true) != 0; j++);
      Index index = null;
      HeadIndex headIndex = this.head;
      int k;
      if (j <= (k = headIndex.level)) {
        for (byte b = 1; b <= j; b++)
          index = new Index(node, index, null); 
      } else {
        j = k + 1;
        Index[] arrayOfIndex = (Index[])new Index[j + 1];
        int n;
        for (n = 1; n <= j; n++)
          arrayOfIndex[n] = index = new Index(node, index, null); 
        while (true) {
          headIndex = this.head;
          n = headIndex.level;
          if (j <= n)
            break; 
          HeadIndex headIndex1 = headIndex;
          Node node1 = headIndex.node;
          for (int i1 = n + 1; i1 <= j; i1++)
            headIndex1 = new HeadIndex(node1, headIndex1, arrayOfIndex[i1], i1); 
          if (casHead(headIndex, headIndex1)) {
            headIndex = headIndex1;
            index = arrayOfIndex[j = n];
            break;
          } 
        } 
      } 
      int m = j;
      label102: while (true) {
        int n = headIndex.level;
        HeadIndex headIndex1 = headIndex;
        Index index1 = headIndex1.right;
        Index index2 = index;
        while (headIndex1 != null && index2 != null) {
          if (index1 != null) {
            Node node1 = index1.node;
            int i1 = cpr(comparator1, paramK, node1.key);
            if (node1.value == null) {
              if (!headIndex1.unlink(index1))
                continue label102; 
              index1 = headIndex1.right;
              continue;
            } 
            if (i1 > 0) {
              index3 = index1;
              index1 = index1.right;
              continue;
            } 
          } 
          if (n == m) {
            if (!index3.link(index1, index2))
              continue label102; 
            if (index2.node.value == null) {
              findNode(paramK);
              break;
            } 
            if (--m == 0)
              break; 
          } 
          if (--n >= m && n < j)
            index2 = index2.down; 
          Index index3 = index3.down;
          index1 = index3.right;
        } 
        break;
      } 
    } 
    return null;
  }
  
  final V doRemove(Object paramObject1, Object paramObject2) {
    if (paramObject1 == null)
      throw new NullPointerException(); 
    Comparator comparator1 = this.comparator;
    label44: while (true) {
      Node node1 = findPredecessor(paramObject1, comparator1);
      Node node2 = node1.next;
      while (node2 != null) {
        Node node = node2.next;
        if (node2 != node1.next)
          continue label44; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label44;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label44; 
          int i;
          if ((i = cpr(comparator1, paramObject1, node2.key)) < 0)
            break; 
          if (i > 0) {
            node1 = node2;
            node2 = node;
            continue;
          } 
          if (paramObject2 != null) {
            if (!paramObject2.equals(object))
              break; 
            if (!node2.casValue(object, null))
              continue; 
            if (!node2.appendMarker(node) || !node1.casNext(node2, node)) {
              findNode(paramObject1);
            } else {
              findPredecessor(paramObject1, comparator1);
              if (this.head.right == null)
                tryReduceLevel(); 
            } 
            return (V)object;
          } 
          continue label44;
        } 
        continue label44;
      } 
      break;
    } 
    return null;
  }
  
  private void tryReduceLevel() {
    HeadIndex headIndex1 = this.head;
    HeadIndex headIndex2;
    HeadIndex headIndex3;
    if (headIndex1.level > 3 && (headIndex2 = (HeadIndex)headIndex1.down) != null && (headIndex3 = (HeadIndex)headIndex2.down) != null && headIndex3.right == null && headIndex2.right == null && headIndex1.right == null && casHead(headIndex1, headIndex2) && headIndex1.right != null)
      casHead(headIndex2, headIndex1); 
  }
  
  final Node<K, V> findFirst() {
    while (true) {
      Node node1;
      Node node2;
      if ((node2 = (node1 = this.head.node).next) == null)
        return null; 
      if (node2.value != null)
        return node2; 
      node2.helpDelete(node1, node2.next);
    } 
  }
  
  private Map.Entry<K, V> doRemoveFirstEntry() {
    Object object1;
    Node node3;
    Node node2;
    Node node1;
    while (true) {
      if ((node2 = (node1 = this.head.node).next) == null)
        return null; 
      node3 = node2.next;
      if (node2 != node1.next)
        continue; 
      object1 = node2.value;
      if (object1 == null) {
        node2.helpDelete(node1, node3);
        continue;
      } 
      if (!node2.casValue(object1, null))
        continue; 
      break;
    } 
    if (!node2.appendMarker(node3) || !node1.casNext(node2, node3))
      findFirst(); 
    clearIndexToFirst();
    Object object2 = object1;
    return new AbstractMap.SimpleImmutableEntry(node2.key, object2);
  }
  
  private void clearIndexToFirst() {
    label16: while (true) {
      HeadIndex headIndex = this.head;
      Index index;
      do {
        Index index1 = headIndex.right;
        if (index1 != null && index1.indexesDeletedNode() && !headIndex.unlink(index1))
          continue label16; 
      } while ((index = headIndex.down) != null);
      break;
    } 
    if (this.head.right == null)
      tryReduceLevel(); 
  }
  
  private Map.Entry<K, V> doRemoveLastEntry() {
    Object object1;
    Node node3;
    Node node2;
    Node node1;
    label35: while (true) {
      node1 = findPredecessorOfLast();
      node2 = node1.next;
      if (node2 == null) {
        if (node1.isBaseHeader())
          return null; 
        continue;
      } 
      while (true) {
        node3 = node2.next;
        if (node2 != node1.next)
          continue label35; 
        object1 = node2.value;
        if (object1 == null) {
          node2.helpDelete(node1, node3);
          continue label35;
        } 
        if (node1.value != null) {
          if (object1 == node2)
            continue label35; 
          if (node3 != null) {
            node1 = node2;
            node2 = node3;
            continue;
          } 
          break;
        } 
        continue label35;
      } 
      if (!node2.casValue(object1, null))
        continue; 
      break;
    } 
    Object object2 = node2.key;
    if (!node2.appendMarker(node3) || !node1.casNext(node2, node3)) {
      findNode(object2);
    } else {
      findPredecessor(object2, this.comparator);
      if (this.head.right == null)
        tryReduceLevel(); 
    } 
    Object object3 = object1;
    return new AbstractMap.SimpleImmutableEntry(object2, object3);
  }
  
  final Node<K, V> findLast() {
    HeadIndex headIndex = this.head;
    while (true) {
      Index index3;
      while ((index3 = headIndex.right) != null) {
        if (index3.indexesDeletedNode()) {
          headIndex.unlink(index3);
          headIndex = this.head;
          continue;
        } 
        index1 = index3;
      } 
      Index index2;
      if ((index2 = index1.down) != null) {
        index1 = index2;
        continue;
      } 
      Node node1 = index1.node;
      for (Node node2 = node1.next;; node2 = node) {
        if (node2 == null)
          return node1.isBaseHeader() ? null : node1; 
        Node node = node2.next;
        if (node2 != node1.next)
          break; 
        Object object = node2.value;
        if (object == null) {
          node2.helpDelete(node1, node);
          break;
        } 
        if (node1.value == null || object == node2)
          break; 
        node1 = node2;
      } 
      Index index1 = this.head;
    } 
  }
  
  private Node<K, V> findPredecessorOfLast() {
    Index index;
    label19: while (true) {
      index = this.head;
      while (true) {
        Index index2;
        if ((index2 = index.right) != null) {
          if (index2.indexesDeletedNode()) {
            index.unlink(index2);
            continue label19;
          } 
          if (index2.node.next != null) {
            index = index2;
            continue;
          } 
        } 
        Index index1;
        if ((index1 = index.down) != null) {
          index = index1;
          continue;
        } 
        break;
      } 
      break;
    } 
    return index.node;
  }
  
  final Node<K, V> findNear(K paramK, int paramInt, Comparator<? super K> paramComparator) {
    if (paramK == null)
      throw new NullPointerException(); 
    label40: while (true) {
      Node node1 = findPredecessor(paramK, paramComparator);
      Node node2 = node1.next;
      while (true) {
        if (node2 == null)
          return ((paramInt & 0x2) == 0 || node1.isBaseHeader()) ? null : node1; 
        Node node = node2.next;
        if (node2 != node1.next)
          continue label40; 
        Object object;
        if ((object = node2.value) == null) {
          node2.helpDelete(node1, node);
          continue label40;
        } 
        if (node1.value != null) {
          if (object == node2)
            continue label40; 
          int i = cpr(paramComparator, paramK, node2.key);
          if ((i == 0 && (paramInt & true) != 0) || (i < 0 && (paramInt & 0x2) == 0))
            return node2; 
          if (i <= 0 && (paramInt & 0x2) != 0)
            return node1.isBaseHeader() ? null : node1; 
          node1 = node2;
          node2 = node;
          continue;
        } 
        continue label40;
      } 
      break;
    } 
  }
  
  final AbstractMap.SimpleImmutableEntry<K, V> getNear(K paramK, int paramInt) {
    AbstractMap.SimpleImmutableEntry simpleImmutableEntry;
    Comparator comparator1 = this.comparator;
    do {
      Node node = findNear(paramK, paramInt, comparator1);
      if (node == null)
        return null; 
      simpleImmutableEntry = node.createSnapshot();
    } while (simpleImmutableEntry == null);
    return simpleImmutableEntry;
  }
  
  public ConcurrentSkipListMap() { initialize(); }
  
  public ConcurrentSkipListMap(Comparator<? super K> paramComparator) { initialize(); }
  
  public ConcurrentSkipListMap(Map<? extends K, ? extends V> paramMap) {
    initialize();
    putAll(paramMap);
  }
  
  public ConcurrentSkipListMap(SortedMap<K, ? extends V> paramSortedMap) {
    initialize();
    buildFromSorted(paramSortedMap);
  }
  
  public ConcurrentSkipListMap<K, V> clone() {
    try {
      ConcurrentSkipListMap concurrentSkipListMap = (ConcurrentSkipListMap)super.clone();
      concurrentSkipListMap.initialize();
      concurrentSkipListMap.buildFromSorted(this);
      return concurrentSkipListMap;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void buildFromSorted(SortedMap<K, ? extends V> paramSortedMap) {
    if (paramSortedMap == null)
      throw new NullPointerException(); 
    HeadIndex headIndex1 = this.head;
    Node node = headIndex1.node;
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b <= headIndex1.level; b++)
      arrayList.add(null); 
    HeadIndex headIndex2 = headIndex1;
    for (int i = headIndex1.level; i > 0; i--) {
      arrayList.set(i, headIndex2);
      Index index = headIndex2.down;
    } 
    for (Map.Entry entry : paramSortedMap.entrySet()) {
      int j = ThreadLocalRandom.current().nextInt();
      int k = 0;
      if ((j & 0x80000001) == 0) {
        do {
          k++;
        } while ((j >>>= 1 & true) != 0);
        if (k > headIndex1.level)
          k = headIndex1.level + 1; 
      } 
      Object object1 = entry.getKey();
      Object object2 = entry.getValue();
      if (object1 == null || object2 == null)
        throw new NullPointerException(); 
      Node node1 = new Node(object1, object2, null);
      node.next = node1;
      node = node1;
      if (k > 0) {
        Index index = null;
        for (byte b1 = 1; b1 <= k; b1++) {
          index = new Index(node1, index, null);
          if (b1 > headIndex1.level)
            headIndex1 = new HeadIndex(headIndex1.node, headIndex1, index, b1); 
          if (b1 < arrayList.size()) {
            ((Index)arrayList.get(b1)).right = index;
            arrayList.set(b1, index);
          } else {
            arrayList.add(index);
          } 
        } 
      } 
    } 
    this.head = headIndex1;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    for (Node node = findFirst(); node != null; node = node.next) {
      Object object = node.getValidValue();
      if (object != null) {
        paramObjectOutputStream.writeObject(node.key);
        paramObjectOutputStream.writeObject(object);
      } 
    } 
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    initialize();
    HeadIndex headIndex1 = this.head;
    Node node = headIndex1.node;
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b <= headIndex1.level; b++)
      arrayList.add(null); 
    HeadIndex headIndex2 = headIndex1;
    for (int i = headIndex1.level; i > 0; i--) {
      arrayList.set(i, headIndex2);
      Index index = headIndex2.down;
    } 
    while (true) {
      Object object1 = paramObjectInputStream.readObject();
      if (object1 == null)
        break; 
      Object object2 = paramObjectInputStream.readObject();
      if (object2 == null)
        throw new NullPointerException(); 
      Object object3 = object1;
      Object object4 = object2;
      int j = ThreadLocalRandom.current().nextInt();
      int k = 0;
      if ((j & 0x80000001) == 0) {
        do {
          k++;
        } while ((j >>>= 1 & true) != 0);
        if (k > headIndex1.level)
          k = headIndex1.level + 1; 
      } 
      Node node1 = new Node(object3, object4, null);
      node.next = node1;
      node = node1;
      if (k > 0) {
        Index index = null;
        for (byte b1 = 1; b1 <= k; b1++) {
          index = new Index(node1, index, null);
          if (b1 > headIndex1.level)
            headIndex1 = new HeadIndex(headIndex1.node, headIndex1, index, b1); 
          if (b1 < arrayList.size()) {
            ((Index)arrayList.get(b1)).right = index;
            arrayList.set(b1, index);
          } else {
            arrayList.add(index);
          } 
        } 
      } 
    } 
    this.head = headIndex1;
  }
  
  public boolean containsKey(Object paramObject) { return (doGet(paramObject) != null); }
  
  public V get(Object paramObject) { return (V)doGet(paramObject); }
  
  public V getOrDefault(Object paramObject, V paramV) {
    Object object;
    return ((object = doGet(paramObject)) == null) ? paramV : object;
  }
  
  public V put(K paramK, V paramV) {
    if (paramV == null)
      throw new NullPointerException(); 
    return (V)doPut(paramK, paramV, false);
  }
  
  public V remove(Object paramObject) { return (V)doRemove(paramObject, null); }
  
  public boolean containsValue(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
    for (Node node = findFirst(); node != null; node = node.next) {
      Object object = node.getValidValue();
      if (object != null && paramObject.equals(object))
        return true; 
    } 
    return false;
  }
  
  public int size() {
    long l = 0L;
    for (Node node = findFirst(); node != null; node = node.next) {
      if (node.getValidValue() != null)
        l++; 
    } 
    return (l >= 2147483647L) ? Integer.MAX_VALUE : (int)l;
  }
  
  public boolean isEmpty() { return (findFirst() == null); }
  
  public void clear() {
    while (true) {
      HeadIndex headIndex1 = this.head;
      HeadIndex headIndex2 = (HeadIndex)headIndex1.down;
      if (headIndex2 != null) {
        casHead(headIndex1, headIndex2);
        continue;
      } 
      Node node1;
      Node node2;
      if ((node1 = headIndex1.node) != null && (node2 = node1.next) != null) {
        Node node = node2.next;
        if (node2 == node1.next) {
          Object object = node2.value;
          if (object == null) {
            node2.helpDelete(node1, node);
            continue;
          } 
          if (node2.casValue(object, null) && node2.appendMarker(node))
            node1.casNext(node2, node); 
        } 
        continue;
      } 
      break;
    } 
  }
  
  public V computeIfAbsent(K paramK, Function<? super K, ? extends V> paramFunction) {
    if (paramK == null || paramFunction == null)
      throw new NullPointerException(); 
    Object object1;
    Object object2;
    if ((object1 = doGet(paramK)) == null && (object2 = paramFunction.apply(paramK)) != null) {
      Object object;
      object1 = ((object = doPut(paramK, object2, true)) == null) ? object2 : object;
    } 
    return (V)object1;
  }
  
  public V computeIfPresent(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramBiFunction == null)
      throw new NullPointerException(); 
    Node node;
    while ((node = findNode(paramK)) != null) {
      Object object;
      if ((object = node.value) != null) {
        Object object1 = object;
        Object object2 = paramBiFunction.apply(paramK, object1);
        if (object2 != null) {
          if (node.casValue(object1, object2))
            return (V)object2; 
          continue;
        } 
        if (doRemove(paramK, object1) != null)
          break; 
      } 
    } 
    return null;
  }
  
  public V compute(K paramK, BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramBiFunction == null)
      throw new NullPointerException(); 
    label23: while (true) {
      Node node;
      while ((node = findNode(paramK)) == null) {
        Object object1;
        if ((object1 = paramBiFunction.apply(paramK, null)) == null)
          break label23; 
        if (doPut(paramK, object1, true) == null)
          return (V)object1; 
      } 
      Object object;
      if ((object = node.value) != null) {
        Object object2 = object;
        Object object1;
        if ((object1 = paramBiFunction.apply(paramK, object2)) != null) {
          if (node.casValue(object2, object1))
            return (V)object1; 
          continue;
        } 
        if (doRemove(paramK, object2) != null)
          break; 
      } 
    } 
    return null;
  }
  
  public V merge(K paramK, V paramV, BiFunction<? super V, ? super V, ? extends V> paramBiFunction) {
    if (paramK == null || paramV == null || paramBiFunction == null)
      throw new NullPointerException(); 
    while (true) {
      Node node;
      while ((node = findNode(paramK)) == null) {
        if (doPut(paramK, paramV, true) == null)
          return paramV; 
      } 
      Object object;
      if ((object = node.value) != null) {
        Object object2 = object;
        Object object1;
        if ((object1 = paramBiFunction.apply(object2, paramV)) != null) {
          if (node.casValue(object2, object1))
            return (V)object1; 
          continue;
        } 
        if (doRemove(paramK, object2) != null)
          break; 
      } 
    } 
    return null;
  }
  
  public NavigableSet<K> keySet() {
    KeySet keySet1 = this.keySet;
    return (keySet1 != null) ? keySet1 : (this.keySet = new KeySet(this));
  }
  
  public NavigableSet<K> navigableKeySet() {
    KeySet keySet1 = this.keySet;
    return (keySet1 != null) ? keySet1 : (this.keySet = new KeySet(this));
  }
  
  public Collection<V> values() {
    Values values1 = this.values;
    return (values1 != null) ? values1 : (this.values = new Values(this));
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    EntrySet entrySet1 = this.entrySet;
    return (entrySet1 != null) ? entrySet1 : (this.entrySet = new EntrySet(this));
  }
  
  public ConcurrentNavigableMap<K, V> descendingMap() {
    ConcurrentNavigableMap concurrentNavigableMap = this.descendingMap;
    return (concurrentNavigableMap != null) ? concurrentNavigableMap : (this.descendingMap = new SubMap(this, null, false, null, false, true));
  }
  
  public NavigableSet<K> descendingKeySet() { return descendingMap().navigableKeySet(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Map))
      return false; 
    Map map = (Map)paramObject;
    try {
      for (Map.Entry entry : entrySet()) {
        if (!entry.getValue().equals(map.get(entry.getKey())))
          return false; 
      } 
      for (Map.Entry entry : map.entrySet()) {
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        if (object1 == null || object2 == null || !object2.equals(get(object1)))
          return false; 
      } 
      return true;
    } catch (ClassCastException classCastException) {
      return false;
    } catch (NullPointerException nullPointerException) {
      return false;
    } 
  }
  
  public V putIfAbsent(K paramK, V paramV) {
    if (paramV == null)
      throw new NullPointerException(); 
    return (V)doPut(paramK, paramV, true);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2) {
    if (paramObject1 == null)
      throw new NullPointerException(); 
    return (paramObject2 != null && doRemove(paramObject1, paramObject2) != null);
  }
  
  public boolean replace(K paramK, V paramV1, V paramV2) {
    if (paramK == null || paramV1 == null || paramV2 == null)
      throw new NullPointerException(); 
    while (true) {
      Node node;
      if ((node = findNode(paramK)) == null)
        return false; 
      Object object;
      if ((object = node.value) != null) {
        if (!paramV1.equals(object))
          return false; 
        if (node.casValue(object, paramV2))
          break; 
      } 
    } 
    return true;
  }
  
  public V replace(K paramK, V paramV) {
    Node node;
    if (paramK == null || paramV == null)
      throw new NullPointerException(); 
    Object object;
    do {
      if ((node = findNode(paramK)) == null)
        return null; 
    } while ((object = node.value) == null || !node.casValue(object, paramV));
    return (V)object;
  }
  
  public Comparator<? super K> comparator() { return this.comparator; }
  
  public K firstKey() {
    Node node = findFirst();
    if (node == null)
      throw new NoSuchElementException(); 
    return (K)node.key;
  }
  
  public K lastKey() {
    Node node = findLast();
    if (node == null)
      throw new NoSuchElementException(); 
    return (K)node.key;
  }
  
  public ConcurrentNavigableMap<K, V> subMap(K paramK1, boolean paramBoolean1, K paramK2, boolean paramBoolean2) {
    if (paramK1 == null || paramK2 == null)
      throw new NullPointerException(); 
    return new SubMap(this, paramK1, paramBoolean1, paramK2, paramBoolean2, false);
  }
  
  public ConcurrentNavigableMap<K, V> headMap(K paramK, boolean paramBoolean) {
    if (paramK == null)
      throw new NullPointerException(); 
    return new SubMap(this, null, false, paramK, paramBoolean, false);
  }
  
  public ConcurrentNavigableMap<K, V> tailMap(K paramK, boolean paramBoolean) {
    if (paramK == null)
      throw new NullPointerException(); 
    return new SubMap(this, paramK, paramBoolean, null, false, false);
  }
  
  public ConcurrentNavigableMap<K, V> subMap(K paramK1, K paramK2) { return subMap(paramK1, true, paramK2, false); }
  
  public ConcurrentNavigableMap<K, V> headMap(K paramK) { return headMap(paramK, false); }
  
  public ConcurrentNavigableMap<K, V> tailMap(K paramK) { return tailMap(paramK, true); }
  
  public Map.Entry<K, V> lowerEntry(K paramK) { return getNear(paramK, 2); }
  
  public K lowerKey(K paramK) {
    Node node = findNear(paramK, 2, this.comparator);
    return (K)((node == null) ? null : node.key);
  }
  
  public Map.Entry<K, V> floorEntry(K paramK) { return getNear(paramK, 3); }
  
  public K floorKey(K paramK) {
    Node node = findNear(paramK, 3, this.comparator);
    return (K)((node == null) ? null : node.key);
  }
  
  public Map.Entry<K, V> ceilingEntry(K paramK) { return getNear(paramK, 1); }
  
  public K ceilingKey(K paramK) {
    Node node = findNear(paramK, 1, this.comparator);
    return (K)((node == null) ? null : node.key);
  }
  
  public Map.Entry<K, V> higherEntry(K paramK) { return getNear(paramK, 0); }
  
  public K higherKey(K paramK) {
    Node node = findNear(paramK, 0, this.comparator);
    return (K)((node == null) ? null : node.key);
  }
  
  public Map.Entry<K, V> firstEntry() {
    AbstractMap.SimpleImmutableEntry simpleImmutableEntry;
    do {
      Node node = findFirst();
      if (node == null)
        return null; 
      simpleImmutableEntry = node.createSnapshot();
    } while (simpleImmutableEntry == null);
    return simpleImmutableEntry;
  }
  
  public Map.Entry<K, V> lastEntry() {
    AbstractMap.SimpleImmutableEntry simpleImmutableEntry;
    do {
      Node node = findLast();
      if (node == null)
        return null; 
      simpleImmutableEntry = node.createSnapshot();
    } while (simpleImmutableEntry == null);
    return simpleImmutableEntry;
  }
  
  public Map.Entry<K, V> pollFirstEntry() { return doRemoveFirstEntry(); }
  
  public Map.Entry<K, V> pollLastEntry() { return doRemoveLastEntry(); }
  
  Iterator<K> keyIterator() { return new KeyIterator(); }
  
  Iterator<V> valueIterator() { return new ValueIterator(); }
  
  Iterator<Map.Entry<K, V>> entryIterator() { return new EntryIterator(); }
  
  static final <E> List<E> toList(Collection<E> paramCollection) {
    ArrayList arrayList = new ArrayList();
    for (Object object : paramCollection)
      arrayList.add(object); 
    return arrayList;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    for (Node node = findFirst(); node != null; node = node.next) {
      Object object;
      if ((object = node.getValidValue()) != null)
        paramBiConsumer.accept(node.key, object); 
    } 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    for (Node node = findFirst(); node != null; node = node.next) {
      Object object;
      while ((object = node.getValidValue()) != null) {
        Object object1 = paramBiFunction.apply(node.key, object);
        if (object1 == null)
          throw new NullPointerException(); 
        if (node.casValue(object, object1))
          break; 
      } 
    } 
  }
  
  final KeySpliterator<K, V> keySpliterator() {
    Comparator comparator1 = this.comparator;
    while (true) {
      HeadIndex headIndex;
      Node node2 = (headIndex = this.head).node;
      Node node1;
      if ((node1 = node2.next) == null || node1.value != null)
        return new KeySpliterator(comparator1, headIndex, node1, null, (node1 == null) ? 0 : Integer.MAX_VALUE); 
      node1.helpDelete(node2, node1.next);
    } 
  }
  
  final ValueSpliterator<K, V> valueSpliterator() {
    Comparator comparator1 = this.comparator;
    while (true) {
      HeadIndex headIndex;
      Node node2 = (headIndex = this.head).node;
      Node node1;
      if ((node1 = node2.next) == null || node1.value != null)
        return new ValueSpliterator(comparator1, headIndex, node1, null, (node1 == null) ? 0 : Integer.MAX_VALUE); 
      node1.helpDelete(node2, node1.next);
    } 
  }
  
  final EntrySpliterator<K, V> entrySpliterator() {
    Comparator comparator1 = this.comparator;
    while (true) {
      HeadIndex headIndex;
      Node node2 = (headIndex = this.head).node;
      Node node1;
      if ((node1 = node2.next) == null || node1.value != null)
        return new EntrySpliterator(comparator1, headIndex, node1, null, (node1 == null) ? 0 : Integer.MAX_VALUE); 
      node1.helpDelete(node2, node1.next);
    } 
  }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz1 = ConcurrentSkipListMap.class;
      headOffset = UNSAFE.objectFieldOffset(clazz1.getDeclaredField("head"));
      Class clazz2 = Thread.class;
      SECONDARY = UNSAFE.objectFieldOffset(clazz2.getDeclaredField("threadLocalRandomSecondarySeed"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static abstract class CSLMSpliterator<K, V> extends Object {
    final Comparator<? super K> comparator;
    
    final K fence;
    
    ConcurrentSkipListMap.Index<K, V> row;
    
    ConcurrentSkipListMap.Node<K, V> current;
    
    int est;
    
    CSLMSpliterator(Comparator<? super K> param1Comparator, ConcurrentSkipListMap.Index<K, V> param1Index, ConcurrentSkipListMap.Node<K, V> param1Node, K param1K, int param1Int) {
      this.comparator = param1Comparator;
      this.row = param1Index;
      this.current = param1Node;
      this.fence = param1K;
      this.est = param1Int;
    }
    
    public final long estimateSize() { return this.est; }
  }
  
  final class EntryIterator extends Iter<Map.Entry<K, V>> {
    EntryIterator() { super(ConcurrentSkipListMap.this); }
    
    public Map.Entry<K, V> next() {
      ConcurrentSkipListMap.Node node = this.next;
      Object object = this.nextValue;
      advance();
      return new AbstractMap.SimpleImmutableEntry(node.key, object);
    }
  }
  
  static final class EntrySet<K1, V1> extends AbstractSet<Map.Entry<K1, V1>> {
    final ConcurrentNavigableMap<K1, V1> m;
    
    EntrySet(ConcurrentNavigableMap<K1, V1> param1ConcurrentNavigableMap) { this.m = param1ConcurrentNavigableMap; }
    
    public Iterator<Map.Entry<K1, V1>> iterator() { return (this.m instanceof ConcurrentSkipListMap) ? ((ConcurrentSkipListMap)this.m).entryIterator() : ((ConcurrentSkipListMap.SubMap)this.m).entryIterator(); }
    
    public boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = this.m.get(entry.getKey());
      return (object != null && object.equals(entry.getValue()));
    }
    
    public boolean remove(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      return this.m.remove(entry.getKey(), entry.getValue());
    }
    
    public boolean isEmpty() { return this.m.isEmpty(); }
    
    public int size() { return this.m.size(); }
    
    public void clear() { this.m.clear(); }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof Set))
        return false; 
      Collection collection = (Collection)param1Object;
      try {
        return (containsAll(collection) && collection.containsAll(this));
      } catch (ClassCastException classCastException) {
        return false;
      } catch (NullPointerException nullPointerException) {
        return false;
      } 
    }
    
    public Object[] toArray() { return ConcurrentSkipListMap.toList(this).toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])ConcurrentSkipListMap.toList(this).toArray(param1ArrayOfT); }
    
    public Spliterator<Map.Entry<K1, V1>> spliterator() { return (this.m instanceof ConcurrentSkipListMap) ? ((ConcurrentSkipListMap)this.m).entrySpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).entryIterator(); }
  }
  
  static final class EntrySpliterator<K, V> extends CSLMSpliterator<K, V> implements Spliterator<Map.Entry<K, V>> {
    EntrySpliterator(Comparator<? super K> param1Comparator, ConcurrentSkipListMap.Index<K, V> param1Index, ConcurrentSkipListMap.Node<K, V> param1Node, K param1K, int param1Int) { super(param1Comparator, param1Index, param1Node, param1K, param1Int); }
    
    public Spliterator<Map.Entry<K, V>> trySplit() {
      Comparator comparator = this.comparator;
      Object object2 = this.fence;
      ConcurrentSkipListMap.Node node;
      Object object1;
      if ((node = this.current) != null && (object1 = node.key) != null)
        for (ConcurrentSkipListMap.Index index = this.row; index != null; index = this.row = index.down) {
          ConcurrentSkipListMap.Index index1;
          ConcurrentSkipListMap.Node node1;
          ConcurrentSkipListMap.Node node2;
          Object object;
          if ((index1 = index.right) != null && (node1 = index1.node) != null && (node2 = node1.next) != null && node2.value != null && (object = node2.key) != null && ConcurrentSkipListMap.cpr(comparator, object, object1) > 0 && (object2 == null || ConcurrentSkipListMap.cpr(comparator, object, object2) < 0)) {
            this.current = node2;
            ConcurrentSkipListMap.Index index2 = index.down;
            this.row = (index1.right != null) ? index1 : index1.down;
            this.est -= (this.est >>> 2);
            return new EntrySpliterator(comparator, index2, node, object, this.est);
          } 
        }  
      return null;
    }
    
    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Comparator comparator = this.comparator;
      Object object1 = this.fence;
      ConcurrentSkipListMap.Node node = this.current;
      this.current = null;
      Object object2;
      while (node != null && ((object2 = node.key) == null || object1 == null || ConcurrentSkipListMap.cpr(comparator, object1, object2) > 0)) {
        Object object;
        if ((object = node.value) != null && object != node) {
          Object object3 = object;
          param1Consumer.accept(new AbstractMap.SimpleImmutableEntry(object2, object3));
        } 
        node = node.next;
      } 
    }
    
    public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Comparator comparator = this.comparator;
      Object object = this.fence;
      ConcurrentSkipListMap.Node node;
      for (node = this.current; node != null; node = node.next) {
        Object object1;
        if ((object1 = node.key) != null && object != null && ConcurrentSkipListMap.cpr(comparator, object, object1) <= 0) {
          node = null;
          break;
        } 
        Object object2;
        if ((object2 = node.value) != null && object2 != node) {
          this.current = node.next;
          Object object3 = object2;
          param1Consumer.accept(new AbstractMap.SimpleImmutableEntry(object1, object3));
          return true;
        } 
      } 
      this.current = node;
      return false;
    }
    
    public int characteristics() { return 4373; }
    
    public final Comparator<Map.Entry<K, V>> getComparator() { return (this.comparator != null) ? Map.Entry.comparingByKey(this.comparator) : (Comparator)((param1Entry1, param1Entry2) -> {
          Comparable comparable = (Comparable)param1Entry1.getKey();
          return comparable.compareTo(param1Entry2.getKey());
        }); }
  }
  
  static final class HeadIndex<K, V> extends Index<K, V> {
    final int level;
    
    HeadIndex(ConcurrentSkipListMap.Node<K, V> param1Node, ConcurrentSkipListMap.Index<K, V> param1Index1, ConcurrentSkipListMap.Index<K, V> param1Index2, int param1Int) {
      super(param1Node, param1Index1, param1Index2);
      this.level = param1Int;
    }
  }
  
  static class Index<K, V> extends Object {
    final ConcurrentSkipListMap.Node<K, V> node;
    
    final Index<K, V> down;
    
    private static final Unsafe UNSAFE;
    
    private static final long rightOffset;
    
    Index(ConcurrentSkipListMap.Node<K, V> param1Node, Index<K, V> param1Index1, Index<K, V> param1Index2) {
      this.node = param1Node;
      this.down = param1Index1;
      this.right = param1Index2;
    }
    
    final boolean casRight(Index<K, V> param1Index1, Index<K, V> param1Index2) { return UNSAFE.compareAndSwapObject(this, rightOffset, param1Index1, param1Index2); }
    
    final boolean indexesDeletedNode() { return (this.node.value == null); }
    
    final boolean link(Index<K, V> param1Index1, Index<K, V> param1Index2) {
      ConcurrentSkipListMap.Node node1 = this.node;
      param1Index2.right = param1Index1;
      return (node1.value != null && casRight(param1Index1, param1Index2));
    }
    
    final boolean unlink(Index<K, V> param1Index) { return (this.node.value != null && casRight(param1Index, param1Index.right)); }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = Index.class;
        rightOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("right"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  abstract class Iter<T> extends Object implements Iterator<T> {
    ConcurrentSkipListMap.Node<K, V> lastReturned;
    
    ConcurrentSkipListMap.Node<K, V> next;
    
    V nextValue;
    
    Iter() {
      while ((this.next = this$0.findFirst()) != null) {
        Object object = this.next.value;
        if (object != null && object != this.next) {
          Object object1 = object;
          this.nextValue = object1;
          break;
        } 
      } 
    }
    
    public final boolean hasNext() { return (this.next != null); }
    
    final void advance() {
      if (this.next == null)
        throw new NoSuchElementException(); 
      this.lastReturned = this.next;
      while ((this.next = this.next.next) != null) {
        Object object = this.next.value;
        if (object != null && object != this.next) {
          Object object1 = object;
          this.nextValue = object1;
          break;
        } 
      } 
    }
    
    public void remove() {
      ConcurrentSkipListMap.Node node = this.lastReturned;
      if (node == null)
        throw new IllegalStateException(); 
      ConcurrentSkipListMap.this.remove(node.key);
      this.lastReturned = null;
    }
  }
  
  final class KeyIterator extends Iter<K> {
    KeyIterator() { super(ConcurrentSkipListMap.this); }
    
    public K next() {
      ConcurrentSkipListMap.Node node = this.next;
      advance();
      return (K)node.key;
    }
  }
  
  static final class KeySet<E> extends AbstractSet<E> implements NavigableSet<E> {
    final ConcurrentNavigableMap<E, ?> m;
    
    KeySet(ConcurrentNavigableMap<E, ?> param1ConcurrentNavigableMap) { this.m = param1ConcurrentNavigableMap; }
    
    public int size() { return this.m.size(); }
    
    public boolean isEmpty() { return this.m.isEmpty(); }
    
    public boolean contains(Object param1Object) { return this.m.containsKey(param1Object); }
    
    public boolean remove(Object param1Object) { return (this.m.remove(param1Object) != null); }
    
    public void clear() { this.m.clear(); }
    
    public E lower(E param1E) { return (E)this.m.lowerKey(param1E); }
    
    public E floor(E param1E) { return (E)this.m.floorKey(param1E); }
    
    public E ceiling(E param1E) { return (E)this.m.ceilingKey(param1E); }
    
    public E higher(E param1E) { return (E)this.m.higherKey(param1E); }
    
    public Comparator<? super E> comparator() { return this.m.comparator(); }
    
    public E first() { return (E)this.m.firstKey(); }
    
    public E last() { return (E)this.m.lastKey(); }
    
    public E pollFirst() {
      Map.Entry entry = this.m.pollFirstEntry();
      return (E)((entry == null) ? null : entry.getKey());
    }
    
    public E pollLast() {
      Map.Entry entry = this.m.pollLastEntry();
      return (E)((entry == null) ? null : entry.getKey());
    }
    
    public Iterator<E> iterator() { return (this.m instanceof ConcurrentSkipListMap) ? ((ConcurrentSkipListMap)this.m).keyIterator() : ((ConcurrentSkipListMap.SubMap)this.m).keyIterator(); }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof Set))
        return false; 
      Collection collection = (Collection)param1Object;
      try {
        return (containsAll(collection) && collection.containsAll(this));
      } catch (ClassCastException classCastException) {
        return false;
      } catch (NullPointerException nullPointerException) {
        return false;
      } 
    }
    
    public Object[] toArray() { return ConcurrentSkipListMap.toList(this).toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])ConcurrentSkipListMap.toList(this).toArray(param1ArrayOfT); }
    
    public Iterator<E> descendingIterator() { return descendingSet().iterator(); }
    
    public NavigableSet<E> subSet(E param1E1, boolean param1Boolean1, E param1E2, boolean param1Boolean2) { return new KeySet(this.m.subMap(param1E1, param1Boolean1, param1E2, param1Boolean2)); }
    
    public NavigableSet<E> headSet(E param1E, boolean param1Boolean) { return new KeySet(this.m.headMap(param1E, param1Boolean)); }
    
    public NavigableSet<E> tailSet(E param1E, boolean param1Boolean) { return new KeySet(this.m.tailMap(param1E, param1Boolean)); }
    
    public NavigableSet<E> subSet(E param1E1, E param1E2) { return subSet(param1E1, true, param1E2, false); }
    
    public NavigableSet<E> headSet(E param1E) { return headSet(param1E, false); }
    
    public NavigableSet<E> tailSet(E param1E) { return tailSet(param1E, true); }
    
    public NavigableSet<E> descendingSet() { return new KeySet(this.m.descendingMap()); }
    
    public Spliterator<E> spliterator() { return (this.m instanceof ConcurrentSkipListMap) ? ((ConcurrentSkipListMap)this.m).keySpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).keyIterator(); }
  }
  
  static final class KeySpliterator<K, V> extends CSLMSpliterator<K, V> implements Spliterator<K> {
    KeySpliterator(Comparator<? super K> param1Comparator, ConcurrentSkipListMap.Index<K, V> param1Index, ConcurrentSkipListMap.Node<K, V> param1Node, K param1K, int param1Int) { super(param1Comparator, param1Index, param1Node, param1K, param1Int); }
    
    public Spliterator<K> trySplit() {
      Comparator comparator = this.comparator;
      Object object2 = this.fence;
      ConcurrentSkipListMap.Node node;
      Object object1;
      if ((node = this.current) != null && (object1 = node.key) != null)
        for (ConcurrentSkipListMap.Index index = this.row; index != null; index = this.row = index.down) {
          ConcurrentSkipListMap.Index index1;
          ConcurrentSkipListMap.Node node1;
          ConcurrentSkipListMap.Node node2;
          Object object;
          if ((index1 = index.right) != null && (node1 = index1.node) != null && (node2 = node1.next) != null && node2.value != null && (object = node2.key) != null && ConcurrentSkipListMap.cpr(comparator, object, object1) > 0 && (object2 == null || ConcurrentSkipListMap.cpr(comparator, object, object2) < 0)) {
            this.current = node2;
            ConcurrentSkipListMap.Index index2 = index.down;
            this.row = (index1.right != null) ? index1 : index1.down;
            this.est -= (this.est >>> 2);
            return new KeySpliterator(comparator, index2, node, object, this.est);
          } 
        }  
      return null;
    }
    
    public void forEachRemaining(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Comparator comparator = this.comparator;
      Object object1 = this.fence;
      ConcurrentSkipListMap.Node node = this.current;
      this.current = null;
      Object object2;
      while (node != null && ((object2 = node.key) == null || object1 == null || ConcurrentSkipListMap.cpr(comparator, object1, object2) > 0)) {
        Object object;
        if ((object = node.value) != null && object != node)
          param1Consumer.accept(object2); 
        node = node.next;
      } 
    }
    
    public boolean tryAdvance(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Comparator comparator = this.comparator;
      Object object = this.fence;
      ConcurrentSkipListMap.Node node;
      for (node = this.current; node != null; node = node.next) {
        Object object1;
        if ((object1 = node.key) != null && object != null && ConcurrentSkipListMap.cpr(comparator, object, object1) <= 0) {
          node = null;
          break;
        } 
        Object object2;
        if ((object2 = node.value) != null && object2 != node) {
          this.current = node.next;
          param1Consumer.accept(object1);
          return true;
        } 
      } 
      this.current = node;
      return false;
    }
    
    public int characteristics() { return 4373; }
    
    public final Comparator<? super K> getComparator() { return this.comparator; }
  }
  
  static final class Node<K, V> extends Object {
    final K key;
    
    private static final Unsafe UNSAFE;
    
    private static final long valueOffset;
    
    private static final long nextOffset;
    
    Node(K param1K, Object param1Object, Node<K, V> param1Node) {
      this.key = param1K;
      this.value = param1Object;
      this.next = param1Node;
    }
    
    Node(Node<K, V> param1Node) {
      this.key = null;
      this.value = this;
      this.next = param1Node;
    }
    
    boolean casValue(Object param1Object1, Object param1Object2) { return UNSAFE.compareAndSwapObject(this, valueOffset, param1Object1, param1Object2); }
    
    boolean casNext(Node<K, V> param1Node1, Node<K, V> param1Node2) { return UNSAFE.compareAndSwapObject(this, nextOffset, param1Node1, param1Node2); }
    
    boolean isMarker() { return (this.value == this); }
    
    boolean isBaseHeader() { return (this.value == BASE_HEADER); }
    
    boolean appendMarker(Node<K, V> param1Node) { return casNext(param1Node, new Node(param1Node)); }
    
    void helpDelete(Node<K, V> param1Node1, Node<K, V> param1Node2) {
      if (param1Node2 == this.next && this == param1Node1.next)
        if (param1Node2 == null || param1Node2.value != param1Node2) {
          casNext(param1Node2, new Node(param1Node2));
        } else {
          param1Node1.casNext(this, param1Node2.next);
        }  
    }
    
    V getValidValue() {
      Object object = this.value;
      return (object == this || object == BASE_HEADER) ? null : (V)object;
    }
    
    AbstractMap.SimpleImmutableEntry<K, V> createSnapshot() {
      Object object1 = this.value;
      if (object1 == null || object1 == this || object1 == BASE_HEADER)
        return null; 
      Object object2 = object1;
      return new AbstractMap.SimpleImmutableEntry(this.key, object2);
    }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = Node.class;
        valueOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("value"));
        nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
  
  static final class SubMap<K, V> extends AbstractMap<K, V> implements ConcurrentNavigableMap<K, V>, Cloneable, Serializable {
    private static final long serialVersionUID = -7647078645895051609L;
    
    private final ConcurrentSkipListMap<K, V> m;
    
    private final K lo;
    
    private final K hi;
    
    private final boolean loInclusive;
    
    private final boolean hiInclusive;
    
    private final boolean isDescending;
    
    private ConcurrentSkipListMap.KeySet<K> keySetView;
    
    private Set<Map.Entry<K, V>> entrySetView;
    
    private Collection<V> valuesView;
    
    SubMap(ConcurrentSkipListMap<K, V> param1ConcurrentSkipListMap, K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2, boolean param1Boolean3) {
      Comparator comparator = param1ConcurrentSkipListMap.comparator;
      if (param1K1 != null && param1K2 != null && ConcurrentSkipListMap.cpr(comparator, param1K1, param1K2) > 0)
        throw new IllegalArgumentException("inconsistent range"); 
      this.m = param1ConcurrentSkipListMap;
      this.lo = param1K1;
      this.hi = param1K2;
      this.loInclusive = param1Boolean1;
      this.hiInclusive = param1Boolean2;
      this.isDescending = param1Boolean3;
    }
    
    boolean tooLow(Object param1Object, Comparator<? super K> param1Comparator) {
      int i;
      return (this.lo != null && ((i = ConcurrentSkipListMap.cpr(param1Comparator, param1Object, this.lo)) < 0 || (i == 0 && !this.loInclusive)));
    }
    
    boolean tooHigh(Object param1Object, Comparator<? super K> param1Comparator) {
      int i;
      return (this.hi != null && ((i = ConcurrentSkipListMap.cpr(param1Comparator, param1Object, this.hi)) > 0 || (i == 0 && !this.hiInclusive)));
    }
    
    boolean inBounds(Object param1Object, Comparator<? super K> param1Comparator) { return (!tooLow(param1Object, param1Comparator) && !tooHigh(param1Object, param1Comparator)); }
    
    void checkKeyBounds(K param1K, Comparator<? super K> param1Comparator) {
      if (param1K == null)
        throw new NullPointerException(); 
      if (!inBounds(param1K, param1Comparator))
        throw new IllegalArgumentException("key out of range"); 
    }
    
    boolean isBeforeEnd(ConcurrentSkipListMap.Node<K, V> param1Node, Comparator<? super K> param1Comparator) {
      if (param1Node == null)
        return false; 
      if (this.hi == null)
        return true; 
      Object object = param1Node.key;
      if (object == null)
        return true; 
      int i = ConcurrentSkipListMap.cpr(param1Comparator, object, this.hi);
      return !(i > 0 || (i == 0 && !this.hiInclusive));
    }
    
    ConcurrentSkipListMap.Node<K, V> loNode(Comparator<? super K> param1Comparator) { return (this.lo == null) ? this.m.findFirst() : (this.loInclusive ? this.m.findNear(this.lo, 1, param1Comparator) : this.m.findNear(this.lo, 0, param1Comparator)); }
    
    ConcurrentSkipListMap.Node<K, V> hiNode(Comparator<? super K> param1Comparator) { return (this.hi == null) ? this.m.findLast() : (this.hiInclusive ? this.m.findNear(this.hi, 3, param1Comparator) : this.m.findNear(this.hi, 2, param1Comparator)); }
    
    K lowestKey() {
      Comparator comparator = this.m.comparator;
      ConcurrentSkipListMap.Node node = loNode(comparator);
      if (isBeforeEnd(node, comparator))
        return (K)node.key; 
      throw new NoSuchElementException();
    }
    
    K highestKey() {
      Comparator comparator = this.m.comparator;
      ConcurrentSkipListMap.Node node = hiNode(comparator);
      if (node != null) {
        Object object = node.key;
        if (inBounds(object, comparator))
          return (K)object; 
      } 
      throw new NoSuchElementException();
    }
    
    Map.Entry<K, V> lowestEntry() {
      AbstractMap.SimpleImmutableEntry simpleImmutableEntry;
      Comparator comparator = this.m.comparator;
      do {
        ConcurrentSkipListMap.Node node = loNode(comparator);
        if (!isBeforeEnd(node, comparator))
          return null; 
        simpleImmutableEntry = node.createSnapshot();
      } while (simpleImmutableEntry == null);
      return simpleImmutableEntry;
    }
    
    Map.Entry<K, V> highestEntry() {
      AbstractMap.SimpleImmutableEntry simpleImmutableEntry;
      Comparator comparator = this.m.comparator;
      do {
        ConcurrentSkipListMap.Node node = hiNode(comparator);
        if (node == null || !inBounds(node.key, comparator))
          return null; 
        simpleImmutableEntry = node.createSnapshot();
      } while (simpleImmutableEntry == null);
      return simpleImmutableEntry;
    }
    
    Map.Entry<K, V> removeLowest() {
      Object object2;
      Object object1;
      Comparator comparator = this.m.comparator;
      do {
        ConcurrentSkipListMap.Node node = loNode(comparator);
        if (node == null)
          return null; 
        object1 = node.key;
        if (!inBounds(object1, comparator))
          return null; 
        object2 = this.m.doRemove(object1, null);
      } while (object2 == null);
      return new AbstractMap.SimpleImmutableEntry(object1, object2);
    }
    
    Map.Entry<K, V> removeHighest() {
      Object object2;
      Object object1;
      Comparator comparator = this.m.comparator;
      do {
        ConcurrentSkipListMap.Node node = hiNode(comparator);
        if (node == null)
          return null; 
        object1 = node.key;
        if (!inBounds(object1, comparator))
          return null; 
        object2 = this.m.doRemove(object1, null);
      } while (object2 == null);
      return new AbstractMap.SimpleImmutableEntry(object1, object2);
    }
    
    Map.Entry<K, V> getNearEntry(K param1K, int param1Int) {
      Object object2;
      Object object1;
      Comparator comparator = this.m.comparator;
      if (this.isDescending)
        if ((param1Int & 0x2) == 0) {
          param1Int |= 0x2;
        } else {
          param1Int &= 0xFFFFFFFD;
        }  
      if (tooLow(param1K, comparator))
        return ((param1Int & 0x2) != 0) ? null : lowestEntry(); 
      if (tooHigh(param1K, comparator))
        return ((param1Int & 0x2) != 0) ? highestEntry() : null; 
      do {
        ConcurrentSkipListMap.Node node = this.m.findNear(param1K, param1Int, comparator);
        if (node == null || !inBounds(node.key, comparator))
          return null; 
        object1 = node.key;
        object2 = node.getValidValue();
      } while (object2 == null);
      return new AbstractMap.SimpleImmutableEntry(object1, object2);
    }
    
    K getNearKey(K param1K, int param1Int) {
      Object object2;
      Object object1;
      Comparator comparator = this.m.comparator;
      if (this.isDescending)
        if ((param1Int & 0x2) == 0) {
          param1Int |= 0x2;
        } else {
          param1Int &= 0xFFFFFFFD;
        }  
      if (tooLow(param1K, comparator)) {
        if ((param1Int & 0x2) == 0) {
          ConcurrentSkipListMap.Node node = loNode(comparator);
          if (isBeforeEnd(node, comparator))
            return (K)node.key; 
        } 
        return null;
      } 
      if (tooHigh(param1K, comparator)) {
        if ((param1Int & 0x2) != 0) {
          ConcurrentSkipListMap.Node node = hiNode(comparator);
          if (node != null) {
            object1 = node.key;
            if (inBounds(object1, comparator))
              return (K)object1; 
          } 
        } 
        return null;
      } 
      do {
        ConcurrentSkipListMap.Node node = this.m.findNear(param1K, param1Int, comparator);
        if (node == null || !inBounds(node.key, comparator))
          return null; 
        object1 = node.key;
        object2 = node.getValidValue();
      } while (object2 == null);
      return (K)object1;
    }
    
    public boolean containsKey(Object param1Object) {
      if (param1Object == null)
        throw new NullPointerException(); 
      return (inBounds(param1Object, this.m.comparator) && this.m.containsKey(param1Object));
    }
    
    public V get(Object param1Object) {
      if (param1Object == null)
        throw new NullPointerException(); 
      return (V)(!inBounds(param1Object, this.m.comparator) ? null : this.m.get(param1Object));
    }
    
    public V put(K param1K, V param1V) {
      checkKeyBounds(param1K, this.m.comparator);
      return (V)this.m.put(param1K, param1V);
    }
    
    public V remove(Object param1Object) { return (V)(!inBounds(param1Object, this.m.comparator) ? null : this.m.remove(param1Object)); }
    
    public int size() {
      Comparator comparator = this.m.comparator;
      long l = 0L;
      for (ConcurrentSkipListMap.Node node = loNode(comparator); isBeforeEnd(node, comparator); node = node.next) {
        if (node.getValidValue() != null)
          l++; 
      } 
      return (l >= 2147483647L) ? Integer.MAX_VALUE : (int)l;
    }
    
    public boolean isEmpty() {
      Comparator comparator = this.m.comparator;
      return !isBeforeEnd(loNode(comparator), comparator);
    }
    
    public boolean containsValue(Object param1Object) {
      if (param1Object == null)
        throw new NullPointerException(); 
      Comparator comparator = this.m.comparator;
      for (ConcurrentSkipListMap.Node node = loNode(comparator); isBeforeEnd(node, comparator); node = node.next) {
        Object object = node.getValidValue();
        if (object != null && param1Object.equals(object))
          return true; 
      } 
      return false;
    }
    
    public void clear() {
      Comparator comparator = this.m.comparator;
      for (ConcurrentSkipListMap.Node node = loNode(comparator); isBeforeEnd(node, comparator); node = node.next) {
        if (node.getValidValue() != null)
          this.m.remove(node.key); 
      } 
    }
    
    public V putIfAbsent(K param1K, V param1V) {
      checkKeyBounds(param1K, this.m.comparator);
      return (V)this.m.putIfAbsent(param1K, param1V);
    }
    
    public boolean remove(Object param1Object1, Object param1Object2) { return (inBounds(param1Object1, this.m.comparator) && this.m.remove(param1Object1, param1Object2)); }
    
    public boolean replace(K param1K, V param1V1, V param1V2) {
      checkKeyBounds(param1K, this.m.comparator);
      return this.m.replace(param1K, param1V1, param1V2);
    }
    
    public V replace(K param1K, V param1V) {
      checkKeyBounds(param1K, this.m.comparator);
      return (V)this.m.replace(param1K, param1V);
    }
    
    public Comparator<? super K> comparator() {
      Comparator comparator = this.m.comparator();
      return this.isDescending ? Collections.reverseOrder(comparator) : comparator;
    }
    
    SubMap<K, V> newSubMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) {
      Comparator comparator = this.m.comparator;
      if (this.isDescending) {
        K k = param1K1;
        param1K1 = param1K2;
        param1K2 = k;
        boolean bool = param1Boolean1;
        param1Boolean1 = param1Boolean2;
        param1Boolean2 = bool;
      } 
      if (this.lo != null)
        if (param1K1 == null) {
          param1K1 = (K)this.lo;
          param1Boolean1 = this.loInclusive;
        } else {
          int i = ConcurrentSkipListMap.cpr(comparator, param1K1, this.lo);
          if (i < 0 || (i == 0 && !this.loInclusive && param1Boolean1))
            throw new IllegalArgumentException("key out of range"); 
        }  
      if (this.hi != null)
        if (param1K2 == null) {
          param1K2 = (K)this.hi;
          param1Boolean2 = this.hiInclusive;
        } else {
          int i = ConcurrentSkipListMap.cpr(comparator, param1K2, this.hi);
          if (i > 0 || (i == 0 && !this.hiInclusive && param1Boolean2))
            throw new IllegalArgumentException("key out of range"); 
        }  
      return new SubMap(this.m, param1K1, param1Boolean1, param1K2, param1Boolean2, this.isDescending);
    }
    
    public SubMap<K, V> subMap(K param1K1, boolean param1Boolean1, K param1K2, boolean param1Boolean2) {
      if (param1K1 == null || param1K2 == null)
        throw new NullPointerException(); 
      return newSubMap(param1K1, param1Boolean1, param1K2, param1Boolean2);
    }
    
    public SubMap<K, V> headMap(K param1K, boolean param1Boolean) {
      if (param1K == null)
        throw new NullPointerException(); 
      return newSubMap(null, false, param1K, param1Boolean);
    }
    
    public SubMap<K, V> tailMap(K param1K, boolean param1Boolean) {
      if (param1K == null)
        throw new NullPointerException(); 
      return newSubMap(param1K, param1Boolean, null, false);
    }
    
    public SubMap<K, V> subMap(K param1K1, K param1K2) { return subMap(param1K1, true, param1K2, false); }
    
    public SubMap<K, V> headMap(K param1K) { return headMap(param1K, false); }
    
    public SubMap<K, V> tailMap(K param1K) { return tailMap(param1K, true); }
    
    public SubMap<K, V> descendingMap() { return new SubMap(this.m, this.lo, this.loInclusive, this.hi, this.hiInclusive, !this.isDescending); }
    
    public Map.Entry<K, V> ceilingEntry(K param1K) { return getNearEntry(param1K, 1); }
    
    public K ceilingKey(K param1K) { return (K)getNearKey(param1K, 1); }
    
    public Map.Entry<K, V> lowerEntry(K param1K) { return getNearEntry(param1K, 2); }
    
    public K lowerKey(K param1K) { return (K)getNearKey(param1K, 2); }
    
    public Map.Entry<K, V> floorEntry(K param1K) { return getNearEntry(param1K, 3); }
    
    public K floorKey(K param1K) { return (K)getNearKey(param1K, 3); }
    
    public Map.Entry<K, V> higherEntry(K param1K) { return getNearEntry(param1K, 0); }
    
    public K higherKey(K param1K) { return (K)getNearKey(param1K, 0); }
    
    public K firstKey() { return (K)(this.isDescending ? highestKey() : lowestKey()); }
    
    public K lastKey() { return (K)(this.isDescending ? lowestKey() : highestKey()); }
    
    public Map.Entry<K, V> firstEntry() { return this.isDescending ? highestEntry() : lowestEntry(); }
    
    public Map.Entry<K, V> lastEntry() { return this.isDescending ? lowestEntry() : highestEntry(); }
    
    public Map.Entry<K, V> pollFirstEntry() { return this.isDescending ? removeHighest() : removeLowest(); }
    
    public Map.Entry<K, V> pollLastEntry() { return this.isDescending ? removeLowest() : removeHighest(); }
    
    public NavigableSet<K> keySet() {
      ConcurrentSkipListMap.KeySet keySet = this.keySetView;
      return (keySet != null) ? keySet : (this.keySetView = new ConcurrentSkipListMap.KeySet(this));
    }
    
    public NavigableSet<K> navigableKeySet() {
      ConcurrentSkipListMap.KeySet keySet = this.keySetView;
      return (keySet != null) ? keySet : (this.keySetView = new ConcurrentSkipListMap.KeySet(this));
    }
    
    public Collection<V> values() {
      Collection collection = this.valuesView;
      return (collection != null) ? collection : (this.valuesView = new ConcurrentSkipListMap.Values(this));
    }
    
    public Set<Map.Entry<K, V>> entrySet() {
      Set set = this.entrySetView;
      return (set != null) ? set : (this.entrySetView = new ConcurrentSkipListMap.EntrySet(this));
    }
    
    public NavigableSet<K> descendingKeySet() { return descendingMap().navigableKeySet(); }
    
    Iterator<K> keyIterator() { return new SubMapKeyIterator(); }
    
    Iterator<V> valueIterator() { return new SubMapValueIterator(); }
    
    Iterator<Map.Entry<K, V>> entryIterator() { return new SubMapEntryIterator(); }
    
    final class SubMapEntryIterator extends SubMapIter<Map.Entry<K, V>> {
      SubMapEntryIterator() { super(ConcurrentSkipListMap.SubMap.this); }
      
      public Map.Entry<K, V> next() {
        ConcurrentSkipListMap.Node node = this.next;
        Object object = this.nextValue;
        advance();
        return new AbstractMap.SimpleImmutableEntry(node.key, object);
      }
      
      public int characteristics() { return 1; }
    }
    
    abstract class SubMapIter<T> extends Object implements Iterator<T>, Spliterator<T> {
      ConcurrentSkipListMap.Node<K, V> lastReturned;
      
      ConcurrentSkipListMap.Node<K, V> next;
      
      V nextValue;
      
      SubMapIter() {
        Comparator comparator = this$0.m.comparator;
        while (true) {
          this.next = this$0.isDescending ? this$0.hiNode(comparator) : this$0.loNode(comparator);
          if (this.next == null)
            break; 
          Object object = this.next.value;
          if (object != null && object != this.next) {
            if (!this$0.inBounds(this.next.key, comparator)) {
              this.next = null;
              break;
            } 
            Object object1 = object;
            this.nextValue = object1;
            break;
          } 
        } 
      }
      
      public final boolean hasNext() { return (this.next != null); }
      
      final void advance() {
        if (this.next == null)
          throw new NoSuchElementException(); 
        this.lastReturned = this.next;
        if (ConcurrentSkipListMap.SubMap.this.isDescending) {
          descend();
        } else {
          ascend();
        } 
      }
      
      private void ascend() {
        Comparator comparator = this.this$0.m.comparator;
        while (true) {
          this.next = this.next.next;
          if (this.next == null)
            break; 
          Object object = this.next.value;
          if (object != null && object != this.next) {
            if (ConcurrentSkipListMap.SubMap.this.tooHigh(this.next.key, comparator)) {
              this.next = null;
              break;
            } 
            Object object1 = object;
            this.nextValue = object1;
            break;
          } 
        } 
      }
      
      private void descend() {
        Comparator comparator = this.this$0.m.comparator;
        while (true) {
          this.next = ConcurrentSkipListMap.SubMap.this.m.findNear(this.lastReturned.key, 2, comparator);
          if (this.next == null)
            break; 
          Object object = this.next.value;
          if (object != null && object != this.next) {
            if (ConcurrentSkipListMap.SubMap.this.tooLow(this.next.key, comparator)) {
              this.next = null;
              break;
            } 
            Object object1 = object;
            this.nextValue = object1;
            break;
          } 
        } 
      }
      
      public void remove() {
        ConcurrentSkipListMap.Node node = this.lastReturned;
        if (node == null)
          throw new IllegalStateException(); 
        ConcurrentSkipListMap.SubMap.this.m.remove(node.key);
        this.lastReturned = null;
      }
      
      public Spliterator<T> trySplit() { return null; }
      
      public boolean tryAdvance(Consumer<? super T> param2Consumer) {
        if (hasNext()) {
          param2Consumer.accept(next());
          return true;
        } 
        return false;
      }
      
      public void forEachRemaining(Consumer<? super T> param2Consumer) {
        while (hasNext())
          param2Consumer.accept(next()); 
      }
      
      public long estimateSize() { return Float.MAX_VALUE; }
    }
    
    final class SubMapKeyIterator extends SubMapIter<K> {
      SubMapKeyIterator() { super(ConcurrentSkipListMap.SubMap.this); }
      
      public K next() {
        ConcurrentSkipListMap.Node node = this.next;
        advance();
        return (K)node.key;
      }
      
      public int characteristics() { return 21; }
      
      public final Comparator<? super K> getComparator() { return ConcurrentSkipListMap.SubMap.this.comparator(); }
    }
    
    final class SubMapValueIterator extends SubMapIter<V> {
      SubMapValueIterator() { super(ConcurrentSkipListMap.SubMap.this); }
      
      public V next() {
        Object object = this.nextValue;
        advance();
        return (V)object;
      }
      
      public int characteristics() { return 0; }
    }
  }
  
  final class ValueIterator extends Iter<V> {
    ValueIterator() { super(ConcurrentSkipListMap.this); }
    
    public V next() {
      Object object = this.nextValue;
      advance();
      return (V)object;
    }
  }
  
  static final class ValueSpliterator<K, V> extends CSLMSpliterator<K, V> implements Spliterator<V> {
    ValueSpliterator(Comparator<? super K> param1Comparator, ConcurrentSkipListMap.Index<K, V> param1Index, ConcurrentSkipListMap.Node<K, V> param1Node, K param1K, int param1Int) { super(param1Comparator, param1Index, param1Node, param1K, param1Int); }
    
    public Spliterator<V> trySplit() {
      Comparator comparator = this.comparator;
      Object object2 = this.fence;
      ConcurrentSkipListMap.Node node;
      Object object1;
      if ((node = this.current) != null && (object1 = node.key) != null)
        for (ConcurrentSkipListMap.Index index = this.row; index != null; index = this.row = index.down) {
          ConcurrentSkipListMap.Index index1;
          ConcurrentSkipListMap.Node node1;
          ConcurrentSkipListMap.Node node2;
          Object object;
          if ((index1 = index.right) != null && (node1 = index1.node) != null && (node2 = node1.next) != null && node2.value != null && (object = node2.key) != null && ConcurrentSkipListMap.cpr(comparator, object, object1) > 0 && (object2 == null || ConcurrentSkipListMap.cpr(comparator, object, object2) < 0)) {
            this.current = node2;
            ConcurrentSkipListMap.Index index2 = index.down;
            this.row = (index1.right != null) ? index1 : index1.down;
            this.est -= (this.est >>> 2);
            return new ValueSpliterator(comparator, index2, node, object, this.est);
          } 
        }  
      return null;
    }
    
    public void forEachRemaining(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Comparator comparator = this.comparator;
      Object object1 = this.fence;
      ConcurrentSkipListMap.Node node = this.current;
      this.current = null;
      Object object2;
      while (node != null && ((object2 = node.key) == null || object1 == null || ConcurrentSkipListMap.cpr(comparator, object1, object2) > 0)) {
        Object object;
        if ((object = node.value) != null && object != node) {
          Object object3 = object;
          param1Consumer.accept(object3);
        } 
        node = node.next;
      } 
    }
    
    public boolean tryAdvance(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Comparator comparator = this.comparator;
      Object object = this.fence;
      ConcurrentSkipListMap.Node node;
      for (node = this.current; node != null; node = node.next) {
        Object object1;
        if ((object1 = node.key) != null && object != null && ConcurrentSkipListMap.cpr(comparator, object, object1) <= 0) {
          node = null;
          break;
        } 
        Object object2;
        if ((object2 = node.value) != null && object2 != node) {
          this.current = node.next;
          Object object3 = object2;
          param1Consumer.accept(object3);
          return true;
        } 
      } 
      this.current = node;
      return false;
    }
    
    public int characteristics() { return 4368; }
  }
  
  static final class Values<E> extends AbstractCollection<E> {
    final ConcurrentNavigableMap<?, E> m;
    
    Values(ConcurrentNavigableMap<?, E> param1ConcurrentNavigableMap) { this.m = param1ConcurrentNavigableMap; }
    
    public Iterator<E> iterator() { return (this.m instanceof ConcurrentSkipListMap) ? ((ConcurrentSkipListMap)this.m).valueIterator() : ((ConcurrentSkipListMap.SubMap)this.m).valueIterator(); }
    
    public boolean isEmpty() { return this.m.isEmpty(); }
    
    public int size() { return this.m.size(); }
    
    public boolean contains(Object param1Object) { return this.m.containsValue(param1Object); }
    
    public void clear() { this.m.clear(); }
    
    public Object[] toArray() { return ConcurrentSkipListMap.toList(this).toArray(); }
    
    public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])ConcurrentSkipListMap.toList(this).toArray(param1ArrayOfT); }
    
    public Spliterator<E> spliterator() { return (this.m instanceof ConcurrentSkipListMap) ? ((ConcurrentSkipListMap)this.m).valueSpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).valueIterator(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ConcurrentSkipListMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */