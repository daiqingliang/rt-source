package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
  private static final long serialVersionUID = -6903933977591709194L;
  
  private final int capacity;
  
  private final AtomicInteger count = new AtomicInteger();
  
  Node<E> head;
  
  private Node<E> last;
  
  private final ReentrantLock takeLock = new ReentrantLock();
  
  private final Condition notEmpty = this.takeLock.newCondition();
  
  private final ReentrantLock putLock = new ReentrantLock();
  
  private final Condition notFull = this.putLock.newCondition();
  
  private void signalNotEmpty() {
    reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      this.notEmpty.signal();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void signalNotFull() {
    reentrantLock = this.putLock;
    reentrantLock.lock();
    try {
      this.notFull.signal();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void enqueue(Node<E> paramNode) { this.last = this.last.next = paramNode; }
  
  private E dequeue() {
    Node node1 = this.head;
    Node node2 = node1.next;
    node1.next = node1;
    this.head = node2;
    Object object = node2.item;
    node2.item = null;
    return (E)object;
  }
  
  void fullyLock() {
    this.putLock.lock();
    this.takeLock.lock();
  }
  
  void fullyUnlock() {
    this.takeLock.unlock();
    this.putLock.unlock();
  }
  
  public LinkedBlockingQueue() { this(2147483647); }
  
  public LinkedBlockingQueue(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.capacity = paramInt;
    this.last = this.head = new Node(null);
  }
  
  public LinkedBlockingQueue(Collection<? extends E> paramCollection) {
    this(2147483647);
    reentrantLock = this.putLock;
    reentrantLock.lock();
    try {
      byte b = 0;
      for (Object object : paramCollection) {
        if (object == null)
          throw new NullPointerException(); 
        if (b == this.capacity)
          throw new IllegalStateException("Queue full"); 
        enqueue(new Node(object));
        b++;
      } 
      this.count.set(b);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int size() { return this.count.get(); }
  
  public int remainingCapacity() { return this.capacity - this.count.get(); }
  
  public void put(E paramE) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    int i = -1;
    Node node = new Node(paramE);
    reentrantLock = this.putLock;
    AtomicInteger atomicInteger = this.count;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == this.capacity)
        this.notFull.await(); 
      enqueue(node);
      i = atomicInteger.getAndIncrement();
      if (i + 1 < this.capacity)
        this.notFull.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == 0)
      signalNotEmpty(); 
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    long l = paramTimeUnit.toNanos(paramLong);
    int i = -1;
    reentrantLock = this.putLock;
    AtomicInteger atomicInteger = this.count;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == this.capacity) {
        if (l <= 0L)
          return false; 
        l = this.notFull.awaitNanos(l);
      } 
      enqueue(new Node(paramE));
      i = atomicInteger.getAndIncrement();
      if (i + 1 < this.capacity)
        this.notFull.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == 0)
      signalNotEmpty(); 
    return true;
  }
  
  public boolean offer(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    AtomicInteger atomicInteger = this.count;
    if (atomicInteger.get() == this.capacity)
      return false; 
    int i = -1;
    Node node = new Node(paramE);
    reentrantLock = this.putLock;
    reentrantLock.lock();
    try {
      if (atomicInteger.get() < this.capacity) {
        enqueue(node);
        i = atomicInteger.getAndIncrement();
        if (i + 1 < this.capacity)
          this.notFull.signal(); 
      } 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == 0)
      signalNotEmpty(); 
    return (i >= 0);
  }
  
  public E take() {
    Object object;
    int i = -1;
    AtomicInteger atomicInteger = this.count;
    reentrantLock = this.takeLock;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == 0)
        this.notEmpty.await(); 
      object = dequeue();
      i = atomicInteger.getAndDecrement();
      if (i > 1)
        this.notEmpty.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == this.capacity)
      signalNotFull(); 
    return (E)object;
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    Object object = null;
    int i = -1;
    long l = paramTimeUnit.toNanos(paramLong);
    AtomicInteger atomicInteger = this.count;
    reentrantLock = this.takeLock;
    reentrantLock.lockInterruptibly();
    try {
      while (atomicInteger.get() == 0) {
        if (l <= 0L) {
          object1 = null;
          return (E)object1;
        } 
        l = this.notEmpty.awaitNanos(l);
      } 
      object = dequeue();
      i = atomicInteger.getAndDecrement();
      if (i > 1)
        this.notEmpty.signal(); 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == this.capacity)
      signalNotFull(); 
    return (E)object;
  }
  
  public E poll() {
    AtomicInteger atomicInteger = this.count;
    if (atomicInteger.get() == 0)
      return null; 
    Object object = null;
    int i = -1;
    reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      if (atomicInteger.get() > 0) {
        object = dequeue();
        i = atomicInteger.getAndDecrement();
        if (i > 1)
          this.notEmpty.signal(); 
      } 
    } finally {
      reentrantLock.unlock();
    } 
    if (i == this.capacity)
      signalNotFull(); 
    return (E)object;
  }
  
  public E peek() {
    if (this.count.get() == 0)
      return null; 
    reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      Node node = this.head.next;
      if (node == null) {
        object1 = null;
        return (E)object1;
      } 
      object = node.item;
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  void unlink(Node<E> paramNode1, Node<E> paramNode2) {
    paramNode1.item = null;
    paramNode2.next = paramNode1.next;
    if (this.last == paramNode1)
      this.last = paramNode2; 
    if (this.count.getAndDecrement() == this.capacity)
      this.notFull.signal(); 
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null)
      return false; 
    fullyLock();
    try {
      Node node1 = this.head;
      for (Node node2 = node1.next; node2 != null; node2 = node2.next) {
        if (paramObject.equals(node2.item)) {
          unlink(node2, node1);
          return true;
        } 
        node1 = node2;
      } 
      return false;
    } finally {
      fullyUnlock();
    } 
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    fullyLock();
    try {
      for (Node node = this.head.next; node != null; node = node.next) {
        if (paramObject.equals(node.item))
          return true; 
      } 
      return false;
    } finally {
      fullyUnlock();
    } 
  }
  
  public Object[] toArray() {
    fullyLock();
    try {
      int i = this.count.get();
      Object[] arrayOfObject = new Object[i];
      byte b = 0;
      for (Node node = this.head.next; node != null; node = node.next)
        arrayOfObject[b++] = node.item; 
      return arrayOfObject;
    } finally {
      fullyUnlock();
    } 
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    fullyLock();
    try {
      int i = this.count.get();
      if (paramArrayOfT.length < i)
        paramArrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), i); 
      byte b = 0;
      for (Node node = this.head.next; node != null; node = node.next)
        paramArrayOfT[b++] = node.item; 
      if (paramArrayOfT.length > b)
        paramArrayOfT[b] = null; 
      return paramArrayOfT;
    } finally {
      fullyUnlock();
    } 
  }
  
  public String toString() {
    fullyLock();
    try {
      Node node = this.head.next;
      if (node == null)
        return "[]"; 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('[');
      while (true) {
        Object object = node.item;
        stringBuilder.append((object == this) ? "(this Collection)" : object);
        node = node.next;
        if (node == null)
          return stringBuilder.append(']').toString(); 
        stringBuilder.append(',').append(' ');
      } 
    } finally {
      fullyUnlock();
    } 
  }
  
  public void clear() {
    fullyLock();
    try {
      Node node1;
      for (Node node2 = this.head; (node1 = node2.next) != null; node2 = node1) {
        node2.next = node2;
        node1.item = null;
      } 
      this.head = this.last;
      if (this.count.getAndSet(0) == this.capacity)
        this.notFull.signal(); 
    } finally {
      fullyUnlock();
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
    bool = false;
    reentrantLock = this.takeLock;
    reentrantLock.lock();
    try {
      i = Math.min(paramInt, this.count.get());
      node = this.head;
    } finally {
      reentrantLock.unlock();
      if (bool)
        signalNotFull(); 
    } 
  }
  
  public Iterator<E> iterator() { return new Itr(); }
  
  public Spliterator<E> spliterator() { return new LBQSpliterator(this); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    fullyLock();
    try {
      paramObjectOutputStream.defaultWriteObject();
      for (Node node = this.head.next; node != null; node = node.next)
        paramObjectOutputStream.writeObject(node.item); 
      paramObjectOutputStream.writeObject(null);
    } finally {
      fullyUnlock();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.count.set(0);
    this.last = this.head = new Node(null);
    while (true) {
      Object object = paramObjectInputStream.readObject();
      if (object == null)
        break; 
      add(object);
    } 
  }
  
  private class Itr extends Object implements Iterator<E> {
    private LinkedBlockingQueue.Node<E> current;
    
    private LinkedBlockingQueue.Node<E> lastRet;
    
    private E currentElement;
    
    Itr() {
      this$0.fullyLock();
      try {
        this.current = this$0.head.next;
        if (this.current != null)
          this.currentElement = this.current.item; 
      } finally {
        this$0.fullyUnlock();
      } 
    }
    
    public boolean hasNext() { return (this.current != null); }
    
    private LinkedBlockingQueue.Node<E> nextNode(LinkedBlockingQueue.Node<E> param1Node) {
      while (true) {
        LinkedBlockingQueue.Node node = param1Node.next;
        if (node == param1Node)
          return this.this$0.head.next; 
        if (node == null || node.item != null)
          return node; 
        param1Node = node;
      } 
    }
    
    public E next() {
      LinkedBlockingQueue.this.fullyLock();
      try {
        if (this.current == null)
          throw new NoSuchElementException(); 
        Object object1 = this.currentElement;
        this.lastRet = this.current;
        this.current = nextNode(this.current);
        this.currentElement = (this.current == null) ? null : this.current.item;
        object2 = object1;
        return (E)object2;
      } finally {
        LinkedBlockingQueue.this.fullyUnlock();
      } 
    }
    
    public void remove() {
      if (this.lastRet == null)
        throw new IllegalStateException(); 
      LinkedBlockingQueue.this.fullyLock();
      try {
        LinkedBlockingQueue.Node node1 = this.lastRet;
        this.lastRet = null;
        LinkedBlockingQueue.Node node2 = LinkedBlockingQueue.this.head;
        for (LinkedBlockingQueue.Node node3 = node2.next; node3 != null; node3 = node3.next) {
          if (node3 == node1) {
            LinkedBlockingQueue.this.unlink(node3, node2);
            break;
          } 
          node2 = node3;
        } 
      } finally {
        LinkedBlockingQueue.this.fullyUnlock();
      } 
    }
  }
  
  static final class LBQSpliterator<E> extends Object implements Spliterator<E> {
    static final int MAX_BATCH = 33554432;
    
    final LinkedBlockingQueue<E> queue;
    
    LinkedBlockingQueue.Node<E> current;
    
    int batch;
    
    boolean exhausted;
    
    long est;
    
    LBQSpliterator(LinkedBlockingQueue<E> param1LinkedBlockingQueue) {
      this.queue = param1LinkedBlockingQueue;
      this.est = param1LinkedBlockingQueue.size();
    }
    
    public long estimateSize() { return this.est; }
    
    public Spliterator<E> trySplit() {
      linkedBlockingQueue = this.queue;
      int i = this.batch;
      boolean bool = (i <= 0) ? 1 : ((i >= 33554432) ? 33554432 : (i + 1));
      LinkedBlockingQueue.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = linkedBlockingQueue.head.next) != null) && node.next != null) {
        Object[] arrayOfObject = new Object[bool];
        byte b = 0;
        LinkedBlockingQueue.Node node1 = this.current;
        linkedBlockingQueue.fullyLock();
        try {
          if (node1 != null || (node1 = linkedBlockingQueue.head.next) != null)
            do {
              arrayOfObject[b] = node1.item;
              if (node1.item == null)
                continue; 
              b++;
            } while ((node1 = node1.next) != null && b < bool); 
        } finally {
          linkedBlockingQueue.fullyUnlock();
        } 
        if ((this.current = node1) == null) {
          this.est = 0L;
          this.exhausted = true;
        } else if (this.est -= b < 0L) {
          this.est = 0L;
        } 
        if (b > 0) {
          this.batch = b;
          return Spliterators.spliterator(arrayOfObject, 0, b, 4368);
        } 
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      linkedBlockingQueue = this.queue;
      if (!this.exhausted) {
        this.exhausted = true;
        LinkedBlockingQueue.Node node = this.current;
        do {
          Object object = null;
          linkedBlockingQueue.fullyLock();
          try {
            if (node == null)
              node = linkedBlockingQueue.head.next; 
            while (node != null) {
              object = node.item;
              node = node.next;
              if (object != null)
                break; 
            } 
          } finally {
            linkedBlockingQueue.fullyUnlock();
          } 
          if (object == null)
            continue; 
          param1Consumer.accept(object);
        } while (node != null);
      } 
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      linkedBlockingQueue = this.queue;
      if (!this.exhausted) {
        Object object = null;
        linkedBlockingQueue.fullyLock();
        try {
          if (this.current == null)
            this.current = linkedBlockingQueue.head.next; 
          while (this.current != null) {
            object = this.current.item;
            this.current = this.current.next;
            if (object != null)
              break; 
          } 
        } finally {
          linkedBlockingQueue.fullyUnlock();
        } 
        if (this.current == null)
          this.exhausted = true; 
        if (object != null) {
          param1Consumer.accept(object);
          return true;
        } 
      } 
      return false;
    }
    
    public int characteristics() { return 4368; }
  }
  
  static class Node<E> extends Object {
    E item;
    
    Node<E> next;
    
    Node(E param1E) throws InterruptedException { this.item = param1E; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\LinkedBlockingQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */