package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
  private NavigableMap<E, Object> m;
  
  private static final Object PRESENT = new Object();
  
  private static final long serialVersionUID = -2479143000061671589L;
  
  TreeSet(NavigableMap<E, Object> paramNavigableMap) { this.m = paramNavigableMap; }
  
  public TreeSet() { this(new TreeMap()); }
  
  public TreeSet(Comparator<? super E> paramComparator) { this(new TreeMap(paramComparator)); }
  
  public TreeSet(Collection<? extends E> paramCollection) {
    this();
    addAll(paramCollection);
  }
  
  public TreeSet(SortedSet<E> paramSortedSet) {
    this(paramSortedSet.comparator());
    addAll(paramSortedSet);
  }
  
  public Iterator<E> iterator() { return this.m.navigableKeySet().iterator(); }
  
  public Iterator<E> descendingIterator() { return this.m.descendingKeySet().iterator(); }
  
  public NavigableSet<E> descendingSet() { return new TreeSet(this.m.descendingMap()); }
  
  public int size() { return this.m.size(); }
  
  public boolean isEmpty() { return this.m.isEmpty(); }
  
  public boolean contains(Object paramObject) { return this.m.containsKey(paramObject); }
  
  public boolean add(E paramE) { return (this.m.put(paramE, PRESENT) == null); }
  
  public boolean remove(Object paramObject) { return (this.m.remove(paramObject) == PRESENT); }
  
  public void clear() { this.m.clear(); }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    if (this.m.size() == 0 && paramCollection.size() > 0 && paramCollection instanceof SortedSet && this.m instanceof TreeMap) {
      SortedSet sortedSet = (SortedSet)paramCollection;
      TreeMap treeMap = (TreeMap)this.m;
      Comparator comparator1 = sortedSet.comparator();
      Comparator comparator2 = treeMap.comparator();
      if (comparator1 == comparator2 || (comparator1 != null && comparator1.equals(comparator2))) {
        treeMap.addAllForTreeSet(sortedSet, PRESENT);
        return true;
      } 
    } 
    return super.addAll(paramCollection);
  }
  
  public NavigableSet<E> subSet(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2) { return new TreeSet(this.m.subMap(paramE1, paramBoolean1, paramE2, paramBoolean2)); }
  
  public NavigableSet<E> headSet(E paramE, boolean paramBoolean) { return new TreeSet(this.m.headMap(paramE, paramBoolean)); }
  
  public NavigableSet<E> tailSet(E paramE, boolean paramBoolean) { return new TreeSet(this.m.tailMap(paramE, paramBoolean)); }
  
  public SortedSet<E> subSet(E paramE1, E paramE2) { return subSet(paramE1, true, paramE2, false); }
  
  public SortedSet<E> headSet(E paramE) { return headSet(paramE, false); }
  
  public SortedSet<E> tailSet(E paramE) { return tailSet(paramE, true); }
  
  public Comparator<? super E> comparator() { return this.m.comparator(); }
  
  public E first() { return (E)this.m.firstKey(); }
  
  public E last() { return (E)this.m.lastKey(); }
  
  public E lower(E paramE) { return (E)this.m.lowerKey(paramE); }
  
  public E floor(E paramE) { return (E)this.m.floorKey(paramE); }
  
  public E ceiling(E paramE) { return (E)this.m.ceilingKey(paramE); }
  
  public E higher(E paramE) { return (E)this.m.higherKey(paramE); }
  
  public E pollFirst() {
    Map.Entry entry = this.m.pollFirstEntry();
    return (E)((entry == null) ? null : entry.getKey());
  }
  
  public E pollLast() {
    Map.Entry entry = this.m.pollLastEntry();
    return (E)((entry == null) ? null : entry.getKey());
  }
  
  public Object clone() {
    TreeSet treeSet;
    try {
      treeSet = (TreeSet)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
    treeSet.m = new TreeMap(this.m);
    return treeSet;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(this.m.comparator());
    paramObjectOutputStream.writeInt(this.m.size());
    for (Object object : this.m.keySet())
      paramObjectOutputStream.writeObject(object); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Comparator comparator = (Comparator)paramObjectInputStream.readObject();
    TreeMap treeMap = new TreeMap(comparator);
    this.m = treeMap;
    int i = paramObjectInputStream.readInt();
    treeMap.readTreeSet(i, paramObjectInputStream, PRESENT);
  }
  
  public Spliterator<E> spliterator() { return TreeMap.keySpliteratorFor(this.m); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\TreeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */