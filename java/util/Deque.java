package java.util;

public interface Deque<E> extends Queue<E> {
  void addFirst(E paramE);
  
  void addLast(E paramE);
  
  boolean offerFirst(E paramE);
  
  boolean offerLast(E paramE);
  
  E removeFirst();
  
  E removeLast();
  
  E pollFirst();
  
  E pollLast();
  
  E getFirst();
  
  E getLast();
  
  E peekFirst();
  
  E peekLast();
  
  boolean removeFirstOccurrence(Object paramObject);
  
  boolean removeLastOccurrence(Object paramObject);
  
  boolean add(E paramE);
  
  boolean offer(E paramE);
  
  E remove();
  
  E poll();
  
  E element();
  
  E peek();
  
  void push(E paramE);
  
  E pop();
  
  boolean remove(Object paramObject);
  
  boolean contains(Object paramObject);
  
  int size();
  
  Iterator<E> iterator();
  
  Iterator<E> descendingIterator();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Deque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */