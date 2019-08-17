package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

public interface BlockingQueue<E> extends Queue<E> {
  boolean add(E paramE);
  
  boolean offer(E paramE);
  
  void put(E paramE) throws InterruptedException;
  
  boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  E take() throws InterruptedException;
  
  E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  int remainingCapacity();
  
  boolean remove(Object paramObject);
  
  boolean contains(Object paramObject);
  
  int drainTo(Collection<? super E> paramCollection);
  
  int drainTo(Collection<? super E> paramCollection, int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\BlockingQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */