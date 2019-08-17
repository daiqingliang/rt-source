package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;

public class PriorityBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
  private static final long serialVersionUID = 5595510919245408276L;
  
  private static final int DEFAULT_INITIAL_CAPACITY = 11;
  
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  private Object[] queue;
  
  private int size;
  
  private Comparator<? super E> comparator;
  
  private final ReentrantLock lock;
  
  private final Condition notEmpty;
  
  private PriorityQueue<E> q;
  
  private static final Unsafe UNSAFE;
  
  private static final long allocationSpinLockOffset;
  
  public PriorityBlockingQueue() { this(11, null); }
  
  public PriorityBlockingQueue(int paramInt) { this(paramInt, null); }
  
  public PriorityBlockingQueue(int paramInt, Comparator<? super E> paramComparator) {
    if (paramInt < 1)
      throw new IllegalArgumentException(); 
    this.lock = new ReentrantLock();
    this.notEmpty = this.lock.newCondition();
    this.comparator = paramComparator;
    this.queue = new Object[paramInt];
  }
  
  public PriorityBlockingQueue(Collection<? extends E> paramCollection) {
    this.lock = new ReentrantLock();
    this.notEmpty = this.lock.newCondition();
    boolean bool1 = true;
    boolean bool2 = true;
    if (paramCollection instanceof SortedSet) {
      SortedSet sortedSet = (SortedSet)paramCollection;
      this.comparator = sortedSet.comparator();
      bool1 = false;
    } else if (paramCollection instanceof PriorityBlockingQueue) {
      PriorityBlockingQueue priorityBlockingQueue = (PriorityBlockingQueue)paramCollection;
      this.comparator = priorityBlockingQueue.comparator();
      bool2 = false;
      if (priorityBlockingQueue.getClass() == PriorityBlockingQueue.class)
        bool1 = false; 
    } 
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    if (arrayOfObject.getClass() != Object[].class)
      arrayOfObject = Arrays.copyOf(arrayOfObject, i, Object[].class); 
    if (bool2 && (i == 1 || this.comparator != null))
      for (byte b = 0; b < i; b++) {
        if (arrayOfObject[b] == null)
          throw new NullPointerException(); 
      }  
    this.queue = arrayOfObject;
    this.size = i;
    if (bool1)
      heapify(); 
  }
  
  private void tryGrow(Object[] paramArrayOfObject, int paramInt) {
    this.lock.unlock();
    Object[] arrayOfObject = null;
    if (this.allocationSpinLock == 0 && UNSAFE.compareAndSwapInt(this, allocationSpinLockOffset, 0, 1))
      try {
        int i = paramInt + ((paramInt < 64) ? (paramInt + 2) : (paramInt >> 1));
        if (i - 2147483639 > 0) {
          int j = paramInt + 1;
          if (j < 0 || j > 2147483639)
            throw new OutOfMemoryError(); 
          i = 2147483639;
        } 
        if (i > paramInt && this.queue == paramArrayOfObject)
          arrayOfObject = new Object[i]; 
      } finally {
        this.allocationSpinLock = 0;
      }  
    if (arrayOfObject == null)
      Thread.yield(); 
    this.lock.lock();
    if (arrayOfObject != null && this.queue == paramArrayOfObject) {
      this.queue = arrayOfObject;
      System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, paramInt);
    } 
  }
  
  private E dequeue() {
    int i = this.size - 1;
    if (i < 0)
      return null; 
    Object[] arrayOfObject = this.queue;
    Object object1 = arrayOfObject[0];
    Object object2 = arrayOfObject[i];
    arrayOfObject[i] = null;
    Comparator comparator1 = this.comparator;
    if (comparator1 == null) {
      siftDownComparable(0, object2, arrayOfObject, i);
    } else {
      siftDownUsingComparator(0, object2, arrayOfObject, i, comparator1);
    } 
    this.size = i;
    return (E)object1;
  }
  
  private static <T> void siftUpComparable(int paramInt, T paramT, Object[] paramArrayOfObject) {
    Comparable comparable = (Comparable)paramT;
    while (paramInt > 0) {
      int i = paramInt - 1 >>> 1;
      Object object = paramArrayOfObject[i];
      if (comparable.compareTo(object) >= 0)
        break; 
      paramArrayOfObject[paramInt] = object;
      paramInt = i;
    } 
    paramArrayOfObject[paramInt] = comparable;
  }
  
  private static <T> void siftUpUsingComparator(int paramInt, T paramT, Object[] paramArrayOfObject, Comparator<? super T> paramComparator) {
    while (paramInt > 0) {
      int i = paramInt - 1 >>> 1;
      Object object = paramArrayOfObject[i];
      if (paramComparator.compare(paramT, object) >= 0)
        break; 
      paramArrayOfObject[paramInt] = object;
      paramInt = i;
    } 
    paramArrayOfObject[paramInt] = paramT;
  }
  
  private static <T> void siftDownComparable(int paramInt1, T paramT, Object[] paramArrayOfObject, int paramInt2) {
    if (paramInt2 > 0) {
      Comparable comparable = (Comparable)paramT;
      int i = paramInt2 >>> 1;
      while (paramInt1 < i) {
        int j = (paramInt1 << 1) + 1;
        Object object = paramArrayOfObject[j];
        int k = j + 1;
        if (k < paramInt2 && ((Comparable)object).compareTo(paramArrayOfObject[k]) > 0)
          object = paramArrayOfObject[j = k]; 
        if (comparable.compareTo(object) <= 0)
          break; 
        paramArrayOfObject[paramInt1] = object;
        paramInt1 = j;
      } 
      paramArrayOfObject[paramInt1] = comparable;
    } 
  }
  
  private static <T> void siftDownUsingComparator(int paramInt1, T paramT, Object[] paramArrayOfObject, int paramInt2, Comparator<? super T> paramComparator) {
    if (paramInt2 > 0) {
      int i = paramInt2 >>> 1;
      while (paramInt1 < i) {
        int j = (paramInt1 << 1) + 1;
        Object object = paramArrayOfObject[j];
        int k = j + 1;
        if (k < paramInt2 && paramComparator.compare(object, paramArrayOfObject[k]) > 0)
          object = paramArrayOfObject[j = k]; 
        if (paramComparator.compare(paramT, object) <= 0)
          break; 
        paramArrayOfObject[paramInt1] = object;
        paramInt1 = j;
      } 
      paramArrayOfObject[paramInt1] = paramT;
    } 
  }
  
  private void heapify() {
    Object[] arrayOfObject = this.queue;
    int i = this.size;
    int j = (i >>> 1) - 1;
    Comparator comparator1 = this.comparator;
    if (comparator1 == null) {
      for (int k = j; k >= 0; k--)
        siftDownComparable(k, arrayOfObject[k], arrayOfObject, i); 
    } else {
      for (int k = j; k >= 0; k--)
        siftDownUsingComparator(k, arrayOfObject[k], arrayOfObject, i, comparator1); 
    } 
  }
  
  public boolean add(E paramE) { return offer(paramE); }
  
  public boolean offer(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    reentrantLock = this.lock;
    reentrantLock.lock();
    int i;
    int j;
    Object[] arrayOfObject;
    while ((i = this.size) >= (j = arrayOfObject = this.queue.length))
      tryGrow(arrayOfObject, j); 
    try {
      Comparator comparator1 = this.comparator;
      if (comparator1 == null) {
        siftUpComparable(i, paramE, arrayOfObject);
      } else {
        siftUpUsingComparator(i, paramE, arrayOfObject, comparator1);
      } 
      this.size = i + 1;
      this.notEmpty.signal();
    } finally {
      reentrantLock.unlock();
    } 
    return true;
  }
  
  public void put(E paramE) { offer(paramE); }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) { return offer(paramE); }
  
  public E poll() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = dequeue();
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E take() {
    Object object;
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while ((object = dequeue()) == null)
        this.notEmpty.await(); 
    } finally {
      reentrantLock.unlock();
    } 
    return (E)object;
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    Object object;
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while ((object = dequeue()) == null && l > 0L)
        l = this.notEmpty.awaitNanos(l); 
    } finally {
      reentrantLock.unlock();
    } 
    return (E)object;
  }
  
  public E peek() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = (this.size == 0) ? null : this.queue[0];
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Comparator<? super E> comparator() { return this.comparator; }
  
  public int size() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.size;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int remainingCapacity() { return Integer.MAX_VALUE; }
  
  private int indexOf(Object paramObject) {
    if (paramObject != null) {
      Object[] arrayOfObject = this.queue;
      int i = this.size;
      for (byte b = 0; b < i; b++) {
        if (paramObject.equals(arrayOfObject[b]))
          return b; 
      } 
    } 
    return -1;
  }
  
  private void removeAt(int paramInt) {
    Object[] arrayOfObject = this.queue;
    int i = this.size - 1;
    if (i == paramInt) {
      arrayOfObject[paramInt] = null;
    } else {
      Object object = arrayOfObject[i];
      arrayOfObject[i] = null;
      Comparator comparator1 = this.comparator;
      if (comparator1 == null) {
        siftDownComparable(paramInt, object, arrayOfObject, i);
      } else {
        siftDownUsingComparator(paramInt, object, arrayOfObject, i, comparator1);
      } 
      if (arrayOfObject[paramInt] == object)
        if (comparator1 == null) {
          siftUpComparable(paramInt, object, arrayOfObject);
        } else {
          siftUpUsingComparator(paramInt, object, arrayOfObject, comparator1);
        }  
    } 
    this.size = i;
  }
  
  public boolean remove(Object paramObject) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = indexOf(paramObject);
      if (i == -1)
        return false; 
      removeAt(i);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  void removeEQ(Object paramObject) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = this.queue;
      byte b = 0;
      int i = this.size;
      while (b < i) {
        if (paramObject == arrayOfObject[b]) {
          removeAt(b);
          break;
        } 
        b++;
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean contains(Object paramObject) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return (indexOf(paramObject) != -1);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Object[] toArray() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return Arrays.copyOf(this.queue, this.size);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public String toString() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.size;
      if (i == 0)
        return "[]"; 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('[');
      for (byte b = 0; b < i; b++) {
        Object object = this.queue[b];
        stringBuilder.append((object == this) ? "(this Collection)" : object);
        if (b != i - 1)
          stringBuilder.append(',').append(' '); 
      } 
      return stringBuilder.append(']').toString();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int drainTo(Collection<? super E> paramCollection) { return drainTo(paramCollection, 2147483647); }
  
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
      int i = Math.min(this.size, paramInt);
      for (null = 0; null < i; null++) {
        paramCollection.add(this.queue[0]);
        dequeue();
      } 
      return i;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void clear() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = this.queue;
      int i = this.size;
      this.size = 0;
      for (byte b = 0; b < i; b++)
        arrayOfObject[b] = null; 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.size;
      if (paramArrayOfT.length < i) {
        arrayOfObject = (Object[])Arrays.copyOf(this.queue, this.size, paramArrayOfT.getClass());
        return (T[])arrayOfObject;
      } 
      System.arraycopy(this.queue, 0, paramArrayOfT, 0, i);
      if (paramArrayOfT.length > i)
        paramArrayOfT[i] = null; 
      return paramArrayOfT;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Iterator<E> iterator() { return new Itr(toArray()); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    this.lock.lock();
    try {
      this.q = new PriorityQueue(Math.max(this.size, 1), this.comparator);
      this.q.addAll(this);
      paramObjectOutputStream.defaultWriteObject();
    } finally {
      this.q = null;
      this.lock.unlock();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    try {
      paramObjectInputStream.defaultReadObject();
      int i = this.q.size();
      SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Object[].class, i);
      this.queue = new Object[i];
      this.comparator = this.q.comparator();
      addAll(this.q);
    } finally {
      this.q = null;
    } 
  }
  
  public Spliterator<E> spliterator() { return new PBQSpliterator(this, null, 0, -1); }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = PriorityBlockingQueue.class;
      allocationSpinLockOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("allocationSpinLock"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  final class Itr extends Object implements Iterator<E> {
    final Object[] array;
    
    int cursor;
    
    int lastRet = -1;
    
    Itr(Object[] param1ArrayOfObject) { this.array = param1ArrayOfObject; }
    
    public boolean hasNext() { return (this.cursor < this.array.length); }
    
    public E next() {
      if (this.cursor >= this.array.length)
        throw new NoSuchElementException(); 
      this.lastRet = this.cursor;
      return (E)this.array[this.cursor++];
    }
    
    public void remove() {
      if (this.lastRet < 0)
        throw new IllegalStateException(); 
      PriorityBlockingQueue.this.removeEQ(this.array[this.lastRet]);
      this.lastRet = -1;
    }
  }
  
  static final class PBQSpliterator<E> extends Object implements Spliterator<E> {
    final PriorityBlockingQueue<E> queue;
    
    Object[] array;
    
    int index;
    
    int fence;
    
    PBQSpliterator(PriorityBlockingQueue<E> param1PriorityBlockingQueue, Object[] param1ArrayOfObject, int param1Int1, int param1Int2) {
      this.queue = param1PriorityBlockingQueue;
      this.array = param1ArrayOfObject;
      this.index = param1Int1;
      this.fence = param1Int2;
    }
    
    final int getFence() {
      int i;
      if ((i = this.fence) < 0)
        i = this.fence = this.array = this.queue.toArray().length; 
      return i;
    }
    
    public Spliterator<E> trySplit() {
      int i = getFence();
      int j = this.index;
      int k = j + i >>> 1;
      return (j >= k) ? null : new PBQSpliterator(this.queue, this.array, j, this.index = k);
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Object[] arrayOfObject;
      if ((arrayOfObject = this.array) == null)
        this.fence = arrayOfObject = this.queue.toArray().length; 
      int i;
      int j;
      if ((j = this.fence) <= arrayOfObject.length && (i = this.index) >= 0 && i < (this.index = j))
        do {
          param1Consumer.accept(arrayOfObject[i]);
        } while (++i < j); 
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (getFence() > this.index && this.index >= 0) {
        Object object = this.array[this.index++];
        param1Consumer.accept(object);
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return (getFence() - this.index); }
    
    public int characteristics() { return 16704; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\PriorityBlockingQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */