package java.util.concurrent;

import java.util.Deque;
import java.util.Iterator;

public interface BlockingDeque<E> extends BlockingQueue<E>, Deque<E> {
  void addFirst(E paramE);
  
  void addLast(E paramE);
  
  boolean offerFirst(E paramE);
  
  boolean offerLast(E paramE);
  
  void putFirst(E paramE);
  
  void putLast(E paramE);
  
  boolean offerFirst(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  boolean offerLast(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  E takeFirst() throws InterruptedException;
  
  E takeLast() throws InterruptedException;
  
  E pollFirst(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  E pollLast(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  boolean removeFirstOccurrence(Object paramObject);
  
  boolean removeLastOccurrence(Object paramObject);
  
  boolean add(E paramE);
  
  boolean offer(E paramE);
  
  void put(E paramE);
  
  boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  E remove() throws InterruptedException;
  
  E poll() throws InterruptedException;
  
  E take() throws InterruptedException;
  
  E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  E element() throws InterruptedException;
  
  E peek() throws InterruptedException;
  
  boolean remove(Object paramObject);
  
  boolean contains(Object paramObject);
  
  int size();
  
  Iterator<E> iterator();
  
  void push(E paramE);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\BlockingDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */