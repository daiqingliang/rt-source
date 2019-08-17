package java.util;

import java.util.function.UnaryOperator;

public interface List<E> extends Collection<E> {
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
  
  boolean addAll(int paramInt, Collection<? extends E> paramCollection);
  
  boolean removeAll(Collection<?> paramCollection);
  
  boolean retainAll(Collection<?> paramCollection);
  
  default void replaceAll(UnaryOperator<E> paramUnaryOperator) {
    Objects.requireNonNull(paramUnaryOperator);
    ListIterator listIterator = listIterator();
    while (listIterator.hasNext())
      listIterator.set(paramUnaryOperator.apply(listIterator.next())); 
  }
  
  default void sort(Comparator<? super E> paramComparator) {
    Object[] arrayOfObject = toArray();
    Arrays.sort(arrayOfObject, paramComparator);
    ListIterator listIterator = listIterator();
    for (Object object : arrayOfObject) {
      listIterator.next();
      listIterator.set(object);
    } 
  }
  
  void clear();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  E get(int paramInt);
  
  E set(int paramInt, E paramE);
  
  void add(int paramInt, E paramE);
  
  E remove(int paramInt);
  
  int indexOf(Object paramObject);
  
  int lastIndexOf(Object paramObject);
  
  ListIterator<E> listIterator();
  
  ListIterator<E> listIterator(int paramInt);
  
  List<E> subList(int paramInt1, int paramInt2);
  
  default Spliterator<E> spliterator() { return Spliterators.spliterator(this, 16); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\List.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */