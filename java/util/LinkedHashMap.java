package java.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LinkedHashMap<K, V> extends HashMap<K, V> implements Map<K, V> {
  private static final long serialVersionUID = 3801124242820219131L;
  
  Entry<K, V> head;
  
  Entry<K, V> tail;
  
  final boolean accessOrder = false;
  
  private void linkNodeLast(Entry<K, V> paramEntry) {
    Entry entry = this.tail;
    this.tail = paramEntry;
    if (entry == null) {
      this.head = paramEntry;
    } else {
      paramEntry.before = entry;
      entry.after = paramEntry;
    } 
  }
  
  private void transferLinks(Entry<K, V> paramEntry1, Entry<K, V> paramEntry2) {
    Entry entry1 = paramEntry2.before = paramEntry1.before;
    Entry entry2 = paramEntry2.after = paramEntry1.after;
    if (entry1 == null) {
      this.head = paramEntry2;
    } else {
      entry1.after = paramEntry2;
    } 
    if (entry2 == null) {
      this.tail = paramEntry2;
    } else {
      entry2.before = paramEntry2;
    } 
  }
  
  void reinitialize() {
    super.reinitialize();
    this.head = this.tail = null;
  }
  
  HashMap.Node<K, V> newNode(int paramInt, K paramK, V paramV, HashMap.Node<K, V> paramNode) {
    Entry entry = new Entry(paramInt, paramK, paramV, paramNode);
    linkNodeLast(entry);
    return entry;
  }
  
  HashMap.Node<K, V> replacementNode(HashMap.Node<K, V> paramNode1, HashMap.Node<K, V> paramNode2) {
    Entry entry1 = (Entry)paramNode1;
    Entry entry2 = new Entry(entry1.hash, entry1.key, entry1.value, paramNode2);
    transferLinks(entry1, entry2);
    return entry2;
  }
  
  HashMap.TreeNode<K, V> newTreeNode(int paramInt, K paramK, V paramV, HashMap.Node<K, V> paramNode) {
    HashMap.TreeNode treeNode = new HashMap.TreeNode(paramInt, paramK, paramV, paramNode);
    linkNodeLast(treeNode);
    return treeNode;
  }
  
  HashMap.TreeNode<K, V> replacementTreeNode(HashMap.Node<K, V> paramNode1, HashMap.Node<K, V> paramNode2) {
    Entry entry = (Entry)paramNode1;
    HashMap.TreeNode treeNode = new HashMap.TreeNode(entry.hash, entry.key, entry.value, paramNode2);
    transferLinks(entry, treeNode);
    return treeNode;
  }
  
  void afterNodeRemoval(HashMap.Node<K, V> paramNode) {
    Entry entry1 = (Entry)paramNode;
    Entry entry2 = entry1.before;
    Entry entry3 = entry1.after;
    entry1.before = entry1.after = null;
    if (entry2 == null) {
      this.head = entry3;
    } else {
      entry2.after = entry3;
    } 
    if (entry3 == null) {
      this.tail = entry2;
    } else {
      entry3.before = entry2;
    } 
  }
  
  void afterNodeInsertion(boolean paramBoolean) {
    Entry entry;
    if (paramBoolean && (entry = this.head) != null && removeEldestEntry(entry)) {
      Object object = entry.key;
      removeNode(hash(object), object, null, false, true);
    } 
  }
  
  void afterNodeAccess(HashMap.Node<K, V> paramNode) {
    Entry entry;
    if (this.accessOrder && (entry = this.tail) != paramNode) {
      Entry entry1 = (Entry)paramNode;
      Entry entry2 = entry1.before;
      Entry entry3 = entry1.after;
      entry1.after = null;
      if (entry2 == null) {
        this.head = entry3;
      } else {
        entry2.after = entry3;
      } 
      if (entry3 != null) {
        entry3.before = entry2;
      } else {
        entry = entry2;
      } 
      if (entry == null) {
        this.head = entry1;
      } else {
        entry1.before = entry;
        entry.after = entry1;
      } 
      this.tail = entry1;
      this.modCount++;
    } 
  }
  
  void internalWriteEntries(ObjectOutputStream paramObjectOutputStream) throws IOException {
    for (Entry entry = this.head; entry != null; entry = entry.after) {
      paramObjectOutputStream.writeObject(entry.key);
      paramObjectOutputStream.writeObject(entry.value);
    } 
  }
  
  public LinkedHashMap(int paramInt, float paramFloat) { super(paramInt, paramFloat); }
  
  public LinkedHashMap(int paramInt) { super(paramInt); }
  
  public LinkedHashMap() {}
  
  public LinkedHashMap(Map<? extends K, ? extends V> paramMap) { putMapEntries(paramMap, false); }
  
  public LinkedHashMap(int paramInt, float paramFloat, boolean paramBoolean) { super(paramInt, paramFloat); }
  
  public boolean containsValue(Object paramObject) {
    for (Entry entry = this.head; entry != null; entry = entry.after) {
      Object object = entry.value;
      if (object == paramObject || (paramObject != null && paramObject.equals(object)))
        return true; 
    } 
    return false;
  }
  
  public V get(Object paramObject) {
    HashMap.Node node;
    if ((node = getNode(hash(paramObject), paramObject)) == null)
      return null; 
    if (this.accessOrder)
      afterNodeAccess(node); 
    return (V)node.value;
  }
  
  public V getOrDefault(Object paramObject, V paramV) {
    HashMap.Node node;
    if ((node = getNode(hash(paramObject), paramObject)) == null)
      return paramV; 
    if (this.accessOrder)
      afterNodeAccess(node); 
    return (V)node.value;
  }
  
  public void clear() {
    super.clear();
    this.head = this.tail = null;
  }
  
  protected boolean removeEldestEntry(Map.Entry<K, V> paramEntry) { return false; }
  
  public Set<K> keySet() {
    Set set = this.keySet;
    if (set == null) {
      set = new LinkedKeySet();
      this.keySet = set;
    } 
    return set;
  }
  
  public Collection<V> values() {
    Collection collection = this.values;
    if (collection == null) {
      collection = new LinkedValues();
      this.values = collection;
    } 
    return collection;
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    Set set;
    return ((set = this.entrySet) == null) ? (this.entrySet = new LinkedEntrySet()) : set;
  }
  
  public void forEach(BiConsumer<? super K, ? super V> paramBiConsumer) {
    if (paramBiConsumer == null)
      throw new NullPointerException(); 
    int i = this.modCount;
    for (Entry entry = this.head; entry != null; entry = entry.after)
      paramBiConsumer.accept(entry.key, entry.value); 
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
  }
  
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> paramBiFunction) {
    if (paramBiFunction == null)
      throw new NullPointerException(); 
    int i = this.modCount;
    for (Entry entry = this.head; entry != null; entry = entry.after)
      entry.value = paramBiFunction.apply(entry.key, entry.value); 
    if (this.modCount != i)
      throw new ConcurrentModificationException(); 
  }
  
  static class Entry<K, V> extends HashMap.Node<K, V> {
    Entry<K, V> before;
    
    Entry<K, V> after;
    
    Entry(int param1Int, K param1K, V param1V, HashMap.Node<K, V> param1Node) { super(param1Int, param1K, param1V, param1Node); }
  }
  
  final class LinkedEntryIterator extends LinkedHashIterator implements Iterator<Map.Entry<K, V>> {
    LinkedEntryIterator() { super(LinkedHashMap.this); }
    
    public final Map.Entry<K, V> next() { return nextNode(); }
  }
  
  final class LinkedEntrySet extends AbstractSet<Map.Entry<K, V>> {
    public final int size() { return LinkedHashMap.this.size; }
    
    public final void clear() { LinkedHashMap.this.clear(); }
    
    public final Iterator<Map.Entry<K, V>> iterator() { return new LinkedHashMap.LinkedEntryIterator(LinkedHashMap.this); }
    
    public final boolean contains(Object param1Object) {
      if (!(param1Object instanceof Map.Entry))
        return false; 
      Map.Entry entry = (Map.Entry)param1Object;
      Object object = entry.getKey();
      HashMap.Node node = LinkedHashMap.this.getNode(HashMap.hash(object), object);
      return (node != null && node.equals(entry));
    }
    
    public final boolean remove(Object param1Object) {
      if (param1Object instanceof Map.Entry) {
        Map.Entry entry = (Map.Entry)param1Object;
        Object object1 = entry.getKey();
        Object object2 = entry.getValue();
        return (LinkedHashMap.this.removeNode(HashMap.hash(object1), object1, object2, true, true) != null);
      } 
      return false;
    }
    
    public final Spliterator<Map.Entry<K, V>> spliterator() { return Spliterators.spliterator(this, 81); }
    
    public final void forEach(Consumer<? super Map.Entry<K, V>> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      int i = LinkedHashMap.this.modCount;
      for (LinkedHashMap.Entry entry = LinkedHashMap.this.head; entry != null; entry = entry.after)
        param1Consumer.accept(entry); 
      if (LinkedHashMap.this.modCount != i)
        throw new ConcurrentModificationException(); 
    }
  }
  
  abstract class LinkedHashIterator {
    LinkedHashMap.Entry<K, V> next = LinkedHashMap.this.head;
    
    LinkedHashMap.Entry<K, V> current = null;
    
    int expectedModCount = LinkedHashMap.this.modCount;
    
    public final boolean hasNext() { return (this.next != null); }
    
    final LinkedHashMap.Entry<K, V> nextNode() {
      LinkedHashMap.Entry entry = this.next;
      if (LinkedHashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      if (entry == null)
        throw new NoSuchElementException(); 
      this.current = entry;
      this.next = entry.after;
      return entry;
    }
    
    public final void remove() {
      LinkedHashMap.Entry entry = this.current;
      if (entry == null)
        throw new IllegalStateException(); 
      if (LinkedHashMap.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
      this.current = null;
      Object object = entry.key;
      LinkedHashMap.this.removeNode(HashMap.hash(object), object, null, false, false);
      this.expectedModCount = LinkedHashMap.this.modCount;
    }
  }
  
  final class LinkedKeyIterator extends LinkedHashIterator implements Iterator<K> {
    LinkedKeyIterator() { super(LinkedHashMap.this); }
    
    public final K next() { return (K)nextNode().getKey(); }
  }
  
  final class LinkedKeySet extends AbstractSet<K> {
    public final int size() { return LinkedHashMap.this.size; }
    
    public final void clear() { LinkedHashMap.this.clear(); }
    
    public final Iterator<K> iterator() { return new LinkedHashMap.LinkedKeyIterator(LinkedHashMap.this); }
    
    public final boolean contains(Object param1Object) { return LinkedHashMap.this.containsKey(param1Object); }
    
    public final boolean remove(Object param1Object) { return (LinkedHashMap.this.removeNode(HashMap.hash(param1Object), param1Object, null, false, true) != null); }
    
    public final Spliterator<K> spliterator() { return Spliterators.spliterator(this, 81); }
    
    public final void forEach(Consumer<? super K> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      int i = LinkedHashMap.this.modCount;
      for (LinkedHashMap.Entry entry = LinkedHashMap.this.head; entry != null; entry = entry.after)
        param1Consumer.accept(entry.key); 
      if (LinkedHashMap.this.modCount != i)
        throw new ConcurrentModificationException(); 
    }
  }
  
  final class LinkedValueIterator extends LinkedHashIterator implements Iterator<V> {
    LinkedValueIterator() { super(LinkedHashMap.this); }
    
    public final V next() { return (V)(nextNode()).value; }
  }
  
  final class LinkedValues extends AbstractCollection<V> {
    public final int size() { return LinkedHashMap.this.size; }
    
    public final void clear() { LinkedHashMap.this.clear(); }
    
    public final Iterator<V> iterator() { return new LinkedHashMap.LinkedValueIterator(LinkedHashMap.this); }
    
    public final boolean contains(Object param1Object) { return LinkedHashMap.this.containsValue(param1Object); }
    
    public final Spliterator<V> spliterator() { return Spliterators.spliterator(this, 80); }
    
    public final void forEach(Consumer<? super V> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      int i = LinkedHashMap.this.modCount;
      for (LinkedHashMap.Entry entry = LinkedHashMap.this.head; entry != null; entry = entry.after)
        param1Consumer.accept(entry.value); 
      if (LinkedHashMap.this.modCount != i)
        throw new ConcurrentModificationException(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\LinkedHashMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */