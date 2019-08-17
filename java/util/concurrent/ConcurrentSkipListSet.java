package java.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.Spliterator;
import sun.misc.Unsafe;

public class ConcurrentSkipListSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
  private static final long serialVersionUID = -2479143111061671589L;
  
  private final ConcurrentNavigableMap<E, Object> m = new ConcurrentSkipListMap();
  
  private static final Unsafe UNSAFE;
  
  private static final long mapOffset;
  
  public ConcurrentSkipListSet() {}
  
  public ConcurrentSkipListSet(Comparator<? super E> paramComparator) {}
  
  public ConcurrentSkipListSet(Collection<? extends E> paramCollection) { addAll(paramCollection); }
  
  public ConcurrentSkipListSet(SortedSet<E> paramSortedSet) { addAll(paramSortedSet); }
  
  ConcurrentSkipListSet(ConcurrentNavigableMap<E, Object> paramConcurrentNavigableMap) {}
  
  public ConcurrentSkipListSet<E> clone() {
    try {
      ConcurrentSkipListSet concurrentSkipListSet = (ConcurrentSkipListSet)super.clone();
      concurrentSkipListSet.setMap(new ConcurrentSkipListMap(this.m));
      return concurrentSkipListSet;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  public int size() { return this.m.size(); }
  
  public boolean isEmpty() { return this.m.isEmpty(); }
  
  public boolean contains(Object paramObject) { return this.m.containsKey(paramObject); }
  
  public boolean add(E paramE) { return (this.m.putIfAbsent(paramE, Boolean.TRUE) == null); }
  
  public boolean remove(Object paramObject) { return this.m.remove(paramObject, Boolean.TRUE); }
  
  public void clear() { this.m.clear(); }
  
  public Iterator<E> iterator() { return this.m.navigableKeySet().iterator(); }
  
  public Iterator<E> descendingIterator() { return this.m.descendingKeySet().iterator(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof java.util.Set))
      return false; 
    Collection collection = (Collection)paramObject;
    try {
      return (containsAll(collection) && collection.containsAll(this));
    } catch (ClassCastException classCastException) {
      return false;
    } catch (NullPointerException nullPointerException) {
      return false;
    } 
  }
  
  public boolean removeAll(Collection<?> paramCollection) {
    boolean bool = false;
    for (Object object : paramCollection) {
      if (remove(object))
        bool = true; 
    } 
    return bool;
  }
  
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
  
  public Comparator<? super E> comparator() { return this.m.comparator(); }
  
  public E first() { return (E)this.m.firstKey(); }
  
  public E last() { return (E)this.m.lastKey(); }
  
  public NavigableSet<E> subSet(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2) { return new ConcurrentSkipListSet(this.m.subMap(paramE1, paramBoolean1, paramE2, paramBoolean2)); }
  
  public NavigableSet<E> headSet(E paramE, boolean paramBoolean) { return new ConcurrentSkipListSet(this.m.headMap(paramE, paramBoolean)); }
  
  public NavigableSet<E> tailSet(E paramE, boolean paramBoolean) { return new ConcurrentSkipListSet(this.m.tailMap(paramE, paramBoolean)); }
  
  public NavigableSet<E> subSet(E paramE1, E paramE2) { return subSet(paramE1, true, paramE2, false); }
  
  public NavigableSet<E> headSet(E paramE) { return headSet(paramE, false); }
  
  public NavigableSet<E> tailSet(E paramE) { return tailSet(paramE, true); }
  
  public NavigableSet<E> descendingSet() { return new ConcurrentSkipListSet(this.m.descendingMap()); }
  
  public Spliterator<E> spliterator() { return (this.m instanceof ConcurrentSkipListMap) ? ((ConcurrentSkipListMap)this.m).keySpliterator() : (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).keyIterator(); }
  
  private void setMap(ConcurrentNavigableMap<E, Object> paramConcurrentNavigableMap) { UNSAFE.putObjectVolatile(this, mapOffset, paramConcurrentNavigableMap); }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = ConcurrentSkipListSet.class;
      mapOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("m"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ConcurrentSkipListSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */