package java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {
  private final ReentrantLock lock = new ReentrantLock();
  
  private final PriorityQueue<E> q = new PriorityQueue();
  
  private Thread leader = null;
  
  private final Condition available = this.lock.newCondition();
  
  public DelayQueue() {}
  
  public DelayQueue(Collection<? extends E> paramCollection) { addAll(paramCollection); }
  
  public boolean add(E paramE) { return offer(paramE); }
  
  public boolean offer(E paramE) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      this.q.offer(paramE);
      if (this.q.peek() == paramE) {
        this.leader = null;
        this.available.signal();
      } 
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void put(E paramE) { offer(paramE); }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) { return offer(paramE); }
  
  public E poll() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Delayed delayed1 = (Delayed)this.q.peek();
      if (delayed1 == null || delayed1.getDelay(TimeUnit.NANOSECONDS) > 0L) {
        object = null;
        return (E)object;
      } 
      delayed2 = (Delayed)this.q.poll();
      return (E)delayed2;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E take() {
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (true) {
        Delayed delayed = (Delayed)this.q.peek();
        if (delayed == null) {
          this.available.await();
          continue;
        } 
        long l = delayed.getDelay(TimeUnit.NANOSECONDS);
        if (l <= 0L) {
          delayed1 = (Delayed)this.q.poll();
          return (E)delayed1;
        } 
        delayed = null;
        if (this.leader != null) {
          this.available.await();
          continue;
        } 
        thread = Thread.currentThread();
        this.leader = thread;
        try {
          this.available.awaitNanos(l);
        } finally {
          if (this.leader == thread)
            this.leader = null; 
        } 
      } 
    } finally {
      if (this.leader == null && this.q.peek() != null)
        this.available.signal(); 
      reentrantLock.unlock();
    } 
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (true) {
        Delayed delayed = (Delayed)this.q.peek();
        if (delayed == null) {
          if (l <= 0L) {
            object = null;
            return (E)object;
          } 
          l = this.available.awaitNanos(l);
          continue;
        } 
        long l1 = delayed.getDelay(TimeUnit.NANOSECONDS);
        if (l1 <= 0L) {
          delayed1 = (Delayed)this.q.poll();
          return (E)delayed1;
        } 
        if (l <= 0L) {
          object = null;
          return (E)object;
        } 
        delayed = null;
        if (l < l1 || this.leader != null) {
          l = this.available.awaitNanos(l);
          continue;
        } 
        thread = Thread.currentThread();
        this.leader = thread;
        try {
          long l2 = this.available.awaitNanos(l1);
          l -= l1 - l2;
        } finally {
          if (this.leader == thread)
            this.leader = null; 
        } 
      } 
    } finally {
      if (this.leader == null && this.q.peek() != null)
        this.available.signal(); 
      reentrantLock.unlock();
    } 
  }
  
  public E peek() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      delayed = (Delayed)this.q.peek();
      return (E)delayed;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int size() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.q.size();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private E peekExpired() {
    Delayed delayed = (Delayed)this.q.peek();
    return (E)((delayed == null || delayed.getDelay(TimeUnit.NANOSECONDS) > 0L) ? null : delayed);
  }
  
  public int drainTo(Collection<? super E> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      byte b;
      Delayed delayed;
      for (b = 0; (delayed = peekExpired()) != null; b++) {
        paramCollection.add(delayed);
        this.q.poll();
      } 
      return b;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    if (paramInt <= 0)
      return 0; 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      byte b;
      Delayed delayed;
      for (b = 0; b < paramInt && (delayed = peekExpired()) != null; b++) {
        paramCollection.add(delayed);
        this.q.poll();
      } 
      return b;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void clear() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      this.q.clear();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int remainingCapacity() { return Integer.MAX_VALUE; }
  
  public Object[] toArray() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.q.toArray();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      arrayOfObject = this.q.toArray(paramArrayOfT);
      return (T[])arrayOfObject;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean remove(Object paramObject) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.q.remove(paramObject);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  void removeEQ(Object paramObject) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Iterator iterator = this.q.iterator();
      while (iterator.hasNext()) {
        if (paramObject == iterator.next()) {
          iterator.remove();
          break;
        } 
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Iterator<E> iterator() { return new Itr(toArray()); }
  
  private class Itr extends Object implements Iterator<E> {
    final Object[] array;
    
    int cursor;
    
    int lastRet = -1;
    
    Itr(Object[] param1ArrayOfObject) { this.array = param1ArrayOfObject; }
    
    public boolean hasNext() { return (this.cursor < this.array.length); }
    
    public E next() {
      if (this.cursor >= this.array.length)
        throw new NoSuchElementException(); 
      this.lastRet = this.cursor;
      return (E)(Delayed)this.array[this.cursor++];
    }
    
    public void remove() {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      DelayQueue.this.removeEQ(this.array[this.lastRet]);
      this.lastRet = -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\DelayQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */