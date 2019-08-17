package java.util;

public interface Set<E> extends Collection<E> {
  int size();
  
  boolean isEmpty();
  
  boolean contains(Object paramObject);
  
  Iterator<E> iterator();
  
  Object[] toArray();
  
  <T> T[] toArray(T[] paramArrayOfT);
  
  boolean add(E paramE);
  
  boolean remove(Object paramObject);
  
  boolean containsAll(Collection<?> paramCollection);
  
  boolean addAll(Collection<? extends E> paramCollection);
  
  boolean retainAll(Collection<?> paramCollection);
  
  boolean removeAll(Collection<?> paramCollection);
  
  void clear();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  default Spliterator<E> spliterator() { return Spliterators.spliterator(this, 1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Set.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */