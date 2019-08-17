package java.util;

public interface SortedSet<E> extends Set<E> {
  Comparator<? super E> comparator();
  
  SortedSet<E> subSet(E paramE1, E paramE2);
  
  SortedSet<E> headSet(E paramE);
  
  SortedSet<E> tailSet(E paramE);
  
  E first();
  
  E last();
  
  default Spliterator<E> spliterator() { return new Spliterators.IteratorSpliterator<E>(this, 21) {
        public Comparator<? super E> getComparator() { return SortedSet.this.comparator(); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\SortedSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */