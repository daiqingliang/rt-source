package java.util;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Collection<E> extends Iterable<E> {
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
  
  boolean removeAll(Collection<?> paramCollection);
  
  default boolean removeIf(Predicate<? super E> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    boolean bool = false;
    Iterator iterator = iterator();
    while (iterator.hasNext()) {
      if (paramPredicate.test(iterator.next())) {
        iterator.remove();
        bool = true;
      } 
    } 
    return bool;
  }
  
  boolean retainAll(Collection<?> paramCollection);
  
  void clear();
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  default Spliterator<E> spliterator() { return Spliterators.spliterator(this, 0); }
  
  default Stream<E> stream() { return StreamSupport.stream(spliterator(), false); }
  
  default Stream<E> parallelStream() { return StreamSupport.stream(spliterator(), true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Collection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */