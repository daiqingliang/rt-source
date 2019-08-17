package java.util.concurrent;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
  private static final long serialVersionUID = -817911632652898426L;
  
  final Object[] items;
  
  int takeIndex;
  
  int putIndex;
  
  int count;
  
  final ReentrantLock lock;
  
  private final Condition notEmpty;
  
  private final Condition notFull;
  
  Itrs itrs = null;
  
  final int dec(int paramInt) { return ((paramInt == 0) ? this.items.length : paramInt) - 1; }
  
  final E itemAt(int paramInt) { return (E)this.items[paramInt]; }
  
  private static void checkNotNull(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
  }
  
  private void enqueue(E paramE) {
    Object[] arrayOfObject = this.items;
    arrayOfObject[this.putIndex] = paramE;
    if (++this.putIndex == arrayOfObject.length)
      this.putIndex = 0; 
    this.count++;
    this.notEmpty.signal();
  }
  
  private E dequeue() {
    Object[] arrayOfObject = this.items;
    Object object = arrayOfObject[this.takeIndex];
    arrayOfObject[this.takeIndex] = null;
    if (++this.takeIndex == arrayOfObject.length)
      this.takeIndex = 0; 
    this.count--;
    if (this.itrs != null)
      this.itrs.elementDequeued(); 
    this.notFull.signal();
    return (E)object;
  }
  
  void removeAt(int paramInt) {
    Object[] arrayOfObject = this.items;
    if (paramInt == this.takeIndex) {
      arrayOfObject[this.takeIndex] = null;
      if (++this.takeIndex == arrayOfObject.length)
        this.takeIndex = 0; 
      this.count--;
      if (this.itrs != null)
        this.itrs.elementDequeued(); 
    } else {
      int i = this.putIndex;
      int j = paramInt;
      while (true) {
        int k = j + 1;
        if (k == arrayOfObject.length)
          k = 0; 
        if (k != i) {
          arrayOfObject[j] = arrayOfObject[k];
          j = k;
          continue;
        } 
        break;
      } 
      arrayOfObject[j] = null;
      this.putIndex = j;
      this.count--;
      if (this.itrs != null)
        this.itrs.removedAt(paramInt); 
    } 
    this.notFull.signal();
  }
  
  public ArrayBlockingQueue(int paramInt) { this(paramInt, false); }
  
  public ArrayBlockingQueue(int paramInt, boolean paramBoolean) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.items = new Object[paramInt];
    this.lock = new ReentrantLock(paramBoolean);
    this.notEmpty = this.lock.newCondition();
    this.notFull = this.lock.newCondition();
  }
  
  public ArrayBlockingQueue(int paramInt, boolean paramBoolean, Collection<? extends E> paramCollection) {
    this(paramInt, paramBoolean);
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      byte b = 0;
      try {
        for (Object object : paramCollection) {
          checkNotNull(object);
          this.items[b++] = object;
        } 
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new IllegalArgumentException();
      } 
      this.count = b;
      this.putIndex = (b == paramInt) ? 0 : b;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean add(E paramE) { return super.add(paramE); }
  
  public boolean offer(E paramE) {
    checkNotNull(paramE);
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      if (this.count == this.items.length)
        return false; 
      enqueue(paramE);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void put(E paramE) {
    checkNotNull(paramE);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == this.items.length)
        this.notFull.await(); 
      enqueue(paramE);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    checkNotNull(paramE);
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == this.items.length) {
        if (l <= 0L)
          return false; 
        l = this.notFull.awaitNanos(l);
      } 
      enqueue(paramE);
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E poll() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = (this.count == 0) ? null : dequeue();
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E take() {
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == 0)
        this.notEmpty.await(); 
      object = dequeue();
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (this.count == 0) {
        if (l <= 0L) {
          object1 = null;
          return (E)object1;
        } 
        l = this.notEmpty.awaitNanos(l);
      } 
      object = dequeue();
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E peek() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = itemAt(this.takeIndex);
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int size() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.count;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int remainingCapacity() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.items.length - this.count;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null)
      return false; 
    Object[] arrayOfObject = this.items;
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      if (this.count > 0) {
        int i = this.putIndex;
        int j = this.takeIndex;
        do {
          if (paramObject.equals(arrayOfObject[j])) {
            removeAt(j);
            return true;
          } 
          if (++j != arrayOfObject.length)
            continue; 
          j = 0;
        } while (j != i);
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    Object[] arrayOfObject = this.items;
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      if (this.count > 0) {
        int i = this.putIndex;
        int j = this.takeIndex;
        do {
          if (paramObject.equals(arrayOfObject[j]))
            return true; 
          if (++j != arrayOfObject.length)
            continue; 
          j = 0;
        } while (j != i);
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Object[] toArray() {
    Object[] arrayOfObject;
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      arrayOfObject = new Object[i];
      int j = this.items.length - this.takeIndex;
      if (i <= j) {
        System.arraycopy(this.items, this.takeIndex, arrayOfObject, 0, i);
      } else {
        System.arraycopy(this.items, this.takeIndex, arrayOfObject, 0, j);
        System.arraycopy(this.items, 0, arrayOfObject, j, i - j);
      } 
    } finally {
      reentrantLock.unlock();
    } 
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    Object[] arrayOfObject = this.items;
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      int j = paramArrayOfT.length;
      if (j < i)
        paramArrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i); 
      int k = arrayOfObject.length - this.takeIndex;
      if (i <= k) {
        System.arraycopy(arrayOfObject, this.takeIndex, paramArrayOfT, 0, i);
      } else {
        System.arraycopy(arrayOfObject, this.takeIndex, paramArrayOfT, 0, k);
        System.arraycopy(arrayOfObject, 0, paramArrayOfT, k, i - k);
      } 
      if (j > i)
        paramArrayOfT[i] = null; 
    } finally {
      reentrantLock.unlock();
    } 
    return paramArrayOfT;
  }
  
  public String toString() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      if (i == 0)
        return "[]"; 
      Object[] arrayOfObject = this.items;
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('[');
      int j = this.takeIndex;
      while (true) {
        Object object = arrayOfObject[j];
        stringBuilder.append((object == this) ? "(this Collection)" : object);
        if (--i == 0)
          return stringBuilder.append(']').toString(); 
        stringBuilder.append(',').append(' ');
        if (++j == arrayOfObject.length)
          j = 0; 
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void clear() {
    Object[] arrayOfObject = this.items;
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      int i = this.count;
      if (i > 0) {
        int j = this.putIndex;
        int k = this.takeIndex;
        do {
          arrayOfObject[k] = null;
          if (++k != arrayOfObject.length)
            continue; 
          k = 0;
        } while (k != j);
        this.takeIndex = j;
        this.count = 0;
        if (this.itrs != null)
          this.itrs.queueIsEmpty(); 
        while (i > 0 && reentrantLock.hasWaiters(this.notFull)) {
          this.notFull.signal();
          i--;
        } 
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int drainTo(Collection<? super E> paramCollection) { return drainTo(paramCollection, 2147483647); }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt) {
    checkNotNull(paramCollection);
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    if (paramInt <= 0)
      return 0; 
    arrayOfObject = this.items;
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      i = Math.min(paramInt, this.count);
      j = this.takeIndex;
      k = 0;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Iterator<E> iterator() { return new Itr(); }
  
  public Spliterator<E> spliterator() { return Spliterators.spliterator(this, 4368); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.items.length == 0 || this.takeIndex < 0 || this.takeIndex >= this.items.length || this.putIndex < 0 || this.putIndex >= this.items.length || this.count < 0 || this.count > this.items.length || Math.floorMod(this.putIndex - this.takeIndex, this.items.length) != Math.floorMod(this.count, this.items.length))
      throw new InvalidObjectException("invariants violated"); 
  }
  
  private class Itr extends Object implements Iterator<E> {
    private int cursor;
    
    private E nextItem;
    
    private int nextIndex;
    
    private E lastItem;
    
    private int lastRet = -1;
    
    private int prevTakeIndex;
    
    private int prevCycles;
    
    private static final int NONE = -1;
    
    private static final int REMOVED = -2;
    
    private static final int DETACHED = -3;
    
    Itr() {
      reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (ArrayBlockingQueue.this.count == 0) {
          this.cursor = -1;
          this.nextIndex = -1;
          this.prevTakeIndex = -3;
        } else {
          int i = ArrayBlockingQueue.this.takeIndex;
          this.prevTakeIndex = i;
          this.nextItem = this$0.itemAt(this.nextIndex = i);
          this.cursor = incCursor(i);
          if (ArrayBlockingQueue.this.itrs == null) {
            ArrayBlockingQueue.this.itrs = new ArrayBlockingQueue.Itrs(this$0, this);
          } else {
            ArrayBlockingQueue.this.itrs.register(this);
            ArrayBlockingQueue.this.itrs.doSomeSweeping(false);
          } 
          this.prevCycles = this$0.itrs.cycles;
        } 
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    boolean isDetached() { return (this.prevTakeIndex < 0); }
    
    private int incCursor(int param1Int) {
      if (++param1Int == ArrayBlockingQueue.this.items.length)
        param1Int = 0; 
      if (param1Int == ArrayBlockingQueue.this.putIndex)
        param1Int = -1; 
      return param1Int;
    }
    
    private boolean invalidated(int param1Int1, int param1Int2, long param1Long, int param1Int3) {
      if (param1Int1 < 0)
        return false; 
      int i = param1Int1 - param1Int2;
      if (i < 0)
        i += param1Int3; 
      return (param1Long > i);
    }
    
    private void incorporateDequeues() {
      int i = this.this$0.itrs.cycles;
      int j = ArrayBlockingQueue.this.takeIndex;
      int k = this.prevCycles;
      int m = this.prevTakeIndex;
      if (i != k || j != m) {
        int n = ArrayBlockingQueue.this.items.length;
        long l = ((i - k) * n + j - m);
        if (invalidated(this.lastRet, m, l, n))
          this.lastRet = -2; 
        if (invalidated(this.nextIndex, m, l, n))
          this.nextIndex = -2; 
        if (invalidated(this.cursor, m, l, n))
          this.cursor = j; 
        if (this.cursor < 0 && this.nextIndex < 0 && this.lastRet < 0) {
          detach();
        } else {
          this.prevCycles = i;
          this.prevTakeIndex = j;
        } 
      } 
    }
    
    private void detach() {
      if (this.prevTakeIndex >= 0) {
        this.prevTakeIndex = -3;
        ArrayBlockingQueue.this.itrs.doSomeSweeping(true);
      } 
    }
    
    public boolean hasNext() {
      if (this.nextItem != null)
        return true; 
      noNext();
      return false;
    }
    
    private void noNext() {
      reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (!isDetached()) {
          incorporateDequeues();
          if (this.lastRet >= 0) {
            this.lastItem = ArrayBlockingQueue.this.itemAt(this.lastRet);
            detach();
          } 
        } 
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public E next() {
      Object object = this.nextItem;
      if (object == null)
        throw new NoSuchElementException(); 
      reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (!isDetached())
          incorporateDequeues(); 
        this.lastRet = this.nextIndex;
        int i = this.cursor;
        if (i >= 0) {
          this.nextItem = ArrayBlockingQueue.this.itemAt(this.nextIndex = i);
          this.cursor = incCursor(i);
        } else {
          this.nextIndex = -1;
          this.nextItem = null;
        } 
      } finally {
        reentrantLock.unlock();
      } 
      return (E)object;
    }
    
    public void remove() {
      reentrantLock = ArrayBlockingQueue.this.lock;
      reentrantLock.lock();
      try {
        if (!isDetached())
          incorporateDequeues(); 
        int i = this.lastRet;
        this.lastRet = -1;
        if (i >= 0) {
          if (!isDetached()) {
            ArrayBlockingQueue.this.removeAt(i);
          } else {
            Object object = this.lastItem;
            this.lastItem = null;
            if (ArrayBlockingQueue.this.itemAt(i) == object)
              ArrayBlockingQueue.this.removeAt(i); 
          } 
        } else if (i == -1) {
          throw new IllegalStateException();
        } 
        if (this.cursor < 0 && this.nextIndex < 0)
          detach(); 
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    void shutdown() {
      this.cursor = -1;
      if (this.nextIndex >= 0)
        this.nextIndex = -2; 
      if (this.lastRet >= 0) {
        this.lastRet = -2;
        this.lastItem = null;
      } 
      this.prevTakeIndex = -3;
    }
    
    private int distance(int param1Int1, int param1Int2, int param1Int3) {
      int i = param1Int1 - param1Int2;
      if (i < 0)
        i += param1Int3; 
      return i;
    }
    
    boolean removedAt(int param1Int) {
      if (isDetached())
        return true; 
      int i = this.this$0.itrs.cycles;
      int j = ArrayBlockingQueue.this.takeIndex;
      int k = this.prevCycles;
      int m = this.prevTakeIndex;
      int n = ArrayBlockingQueue.this.items.length;
      int i1 = i - k;
      if (param1Int < j)
        i1++; 
      int i2 = i1 * n + param1Int - m;
      int i3 = this.cursor;
      if (i3 >= 0) {
        int i6 = distance(i3, m, n);
        if (i6 == i2) {
          if (i3 == ArrayBlockingQueue.this.putIndex)
            this.cursor = i3 = -1; 
        } else if (i6 > i2) {
          this.cursor = i3 = ArrayBlockingQueue.this.dec(i3);
        } 
      } 
      int i4 = this.lastRet;
      if (i4 >= 0) {
        int i6 = distance(i4, m, n);
        if (i6 == i2) {
          this.lastRet = i4 = -2;
        } else if (i6 > i2) {
          this.lastRet = i4 = ArrayBlockingQueue.this.dec(i4);
        } 
      } 
      int i5 = this.nextIndex;
      if (i5 >= 0) {
        int i6 = distance(i5, m, n);
        if (i6 == i2) {
          this.nextIndex = i5 = -2;
        } else if (i6 > i2) {
          this.nextIndex = i5 = ArrayBlockingQueue.this.dec(i5);
        } 
      } else if (i3 < 0 && i5 < 0 && i4 < 0) {
        this.prevTakeIndex = -3;
        return true;
      } 
      return false;
    }
    
    boolean takeIndexWrapped() {
      if (isDetached())
        return true; 
      if (this.this$0.itrs.cycles - this.prevCycles > 1) {
        shutdown();
        return true;
      } 
      return false;
    }
  }
  
  class Itrs {
    int cycles = 0;
    
    private ArrayBlockingQueue<E>.Itrs.Node head;
    
    private ArrayBlockingQueue<E>.Itrs.Node sweeper = null;
    
    private static final int SHORT_SWEEP_PROBES = 4;
    
    private static final int LONG_SWEEP_PROBES = 16;
    
    Itrs(ArrayBlockingQueue<E>.Itr param1Itr) { register(param1Itr); }
    
    void doSomeSweeping(boolean param1Boolean) {
      boolean bool;
      Node node2;
      Node node1;
      byte b = param1Boolean ? 16 : 4;
      Node node3 = this.sweeper;
      if (node3 == null) {
        node1 = null;
        node2 = this.head;
        bool = true;
      } else {
        node1 = node3;
        node2 = node1.next;
        bool = false;
      } 
      while (b > 0) {
        if (node2 == null) {
          if (bool)
            break; 
          node1 = null;
          node2 = this.head;
          bool = true;
        } 
        ArrayBlockingQueue.Itr itr = (ArrayBlockingQueue.Itr)node2.get();
        Node node = node2.next;
        if (itr == null || itr.isDetached()) {
          b = 16;
          node2.clear();
          node2.next = null;
          if (node1 == null) {
            this.head = node;
            if (node == null) {
              ArrayBlockingQueue.this.itrs = null;
              return;
            } 
          } else {
            node1.next = node;
          } 
        } else {
          node1 = node2;
        } 
        node2 = node;
        b--;
      } 
      this.sweeper = (node2 == null) ? null : node1;
    }
    
    void register(ArrayBlockingQueue<E>.Itr param1Itr) { this.head = new Node(param1Itr, this.head); }
    
    void takeIndexWrapped() {
      this.cycles++;
      Node node1 = null;
      for (Node node2 = this.head; node2 != null; node2 = node) {
        ArrayBlockingQueue.Itr itr = (ArrayBlockingQueue.Itr)node2.get();
        Node node = node2.next;
        if (itr == null || itr.takeIndexWrapped()) {
          node2.clear();
          node2.next = null;
          if (node1 == null) {
            this.head = node;
          } else {
            node1.next = node;
          } 
        } else {
          node1 = node2;
        } 
      } 
      if (this.head == null)
        ArrayBlockingQueue.this.itrs = null; 
    }
    
    void removedAt(int param1Int) {
      Node node1 = null;
      for (Node node2 = this.head; node2 != null; node2 = node) {
        ArrayBlockingQueue.Itr itr = (ArrayBlockingQueue.Itr)node2.get();
        Node node = node2.next;
        if (itr == null || itr.removedAt(param1Int)) {
          node2.clear();
          node2.next = null;
          if (node1 == null) {
            this.head = node;
          } else {
            node1.next = node;
          } 
        } else {
          node1 = node2;
        } 
      } 
      if (this.head == null)
        ArrayBlockingQueue.this.itrs = null; 
    }
    
    void queueIsEmpty() {
      for (Node node = this.head; node != null; node = node.next) {
        ArrayBlockingQueue.Itr itr = (ArrayBlockingQueue.Itr)node.get();
        if (itr != null) {
          node.clear();
          itr.shutdown();
        } 
      } 
      this.head = null;
      ArrayBlockingQueue.this.itrs = null;
    }
    
    void elementDequeued() {
      if (ArrayBlockingQueue.this.count == 0) {
        queueIsEmpty();
      } else if (ArrayBlockingQueue.this.takeIndex == 0) {
        takeIndexWrapped();
      } 
    }
    
    private class Node extends WeakReference<ArrayBlockingQueue<E>.Itr> {
      ArrayBlockingQueue<E>.Itrs.Node next;
      
      Node(ArrayBlockingQueue<E>.Itr param2Itr, ArrayBlockingQueue<E>.Itrs.Node param2Itrs.Node) {
        super(param2Itr);
        this.next = param2Itrs.Node;
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ArrayBlockingQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */