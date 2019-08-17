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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LinkedBlockingDeque<E> extends AbstractQueue<E> implements BlockingDeque<E>, Serializable {
  private static final long serialVersionUID = -387911632671998426L;
  
  Node<E> first;
  
  Node<E> last;
  
  private int count;
  
  private final int capacity;
  
  final ReentrantLock lock = new ReentrantLock();
  
  private final Condition notEmpty = this.lock.newCondition();
  
  private final Condition notFull = this.lock.newCondition();
  
  public LinkedBlockingDeque() { this(2147483647); }
  
  public LinkedBlockingDeque(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException(); 
    this.capacity = paramInt;
  }
  
  public LinkedBlockingDeque(Collection<? extends E> paramCollection) {
    this(2147483647);
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      for (Object object : paramCollection) {
        if (object == null)
          throw new NullPointerException(); 
        if (!linkLast(new Node(object)))
          throw new IllegalStateException("Deque full"); 
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private boolean linkFirst(Node<E> paramNode) {
    if (this.count >= this.capacity)
      return false; 
    Node node = this.first;
    paramNode.next = node;
    this.first = paramNode;
    if (this.last == null) {
      this.last = paramNode;
    } else {
      node.prev = paramNode;
    } 
    this.count++;
    this.notEmpty.signal();
    return true;
  }
  
  private boolean linkLast(Node<E> paramNode) {
    if (this.count >= this.capacity)
      return false; 
    Node node = this.last;
    paramNode.prev = node;
    this.last = paramNode;
    if (this.first == null) {
      this.first = paramNode;
    } else {
      node.next = paramNode;
    } 
    this.count++;
    this.notEmpty.signal();
    return true;
  }
  
  private E unlinkFirst() {
    Node node1 = this.first;
    if (node1 == null)
      return null; 
    Node node2 = node1.next;
    Object object = node1.item;
    node1.item = null;
    node1.next = node1;
    this.first = node2;
    if (node2 == null) {
      this.last = null;
    } else {
      node2.prev = null;
    } 
    this.count--;
    this.notFull.signal();
    return (E)object;
  }
  
  private E unlinkLast() {
    Node node1 = this.last;
    if (node1 == null)
      return null; 
    Node node2 = node1.prev;
    Object object = node1.item;
    node1.item = null;
    node1.prev = node1;
    this.last = node2;
    if (node2 == null) {
      this.first = null;
    } else {
      node2.next = null;
    } 
    this.count--;
    this.notFull.signal();
    return (E)object;
  }
  
  void unlink(Node<E> paramNode) {
    Node node1 = paramNode.prev;
    Node node2 = paramNode.next;
    if (node1 == null) {
      unlinkFirst();
    } else if (node2 == null) {
      unlinkLast();
    } else {
      node1.next = node2;
      node2.prev = node1;
      paramNode.item = null;
      this.count--;
      this.notFull.signal();
    } 
  }
  
  public void addFirst(E paramE) {
    if (!offerFirst(paramE))
      throw new IllegalStateException("Deque full"); 
  }
  
  public void addLast(E paramE) {
    if (!offerLast(paramE))
      throw new IllegalStateException("Deque full"); 
  }
  
  public boolean offerFirst(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    Node node = new Node(paramE);
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return linkFirst(node);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean offerLast(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    Node node = new Node(paramE);
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return linkLast(node);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void putFirst(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    Node node = new Node(paramE);
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      while (!linkFirst(node))
        this.notFull.await(); 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void putLast(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    Node node = new Node(paramE);
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      while (!linkLast(node))
        this.notFull.await(); 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean offerFirst(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    Node node = new Node(paramE);
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (!linkFirst(node)) {
        if (l <= 0L)
          return false; 
        l = this.notFull.awaitNanos(l);
      } 
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean offerLast(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    Node node = new Node(paramE);
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      while (!linkLast(node)) {
        if (l <= 0L)
          return false; 
        l = this.notFull.awaitNanos(l);
      } 
      return true;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E removeFirst() {
    Object object = pollFirst();
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E removeLast() {
    Object object = pollLast();
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E pollFirst() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = unlinkFirst();
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E pollLast() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = unlinkLast();
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E takeFirst() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object object1;
      while ((object1 = unlinkFirst()) == null)
        this.notEmpty.await(); 
      object2 = object1;
      return (E)object2;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E takeLast() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object object1;
      while ((object1 = unlinkLast()) == null)
        this.notEmpty.await(); 
      object2 = object1;
      return (E)object2;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E pollFirst(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      Object object1;
      while ((object1 = unlinkFirst()) == null) {
        if (l <= 0L) {
          object = null;
          return (E)object;
        } 
        l = this.notEmpty.awaitNanos(l);
      } 
      object2 = object1;
      return (E)object2;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E pollLast(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.lock;
    reentrantLock.lockInterruptibly();
    try {
      Object object1;
      while ((object1 = unlinkLast()) == null) {
        if (l <= 0L) {
          object = null;
          return (E)object;
        } 
        l = this.notEmpty.awaitNanos(l);
      } 
      object2 = object1;
      return (E)object2;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E getFirst() {
    Object object = peekFirst();
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E getLast() {
    Object object = peekLast();
    if (object == null)
      throw new NoSuchElementException(); 
    return (E)object;
  }
  
  public E peekFirst() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = (this.first == null) ? null : this.first.item;
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public E peekLast() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      object = (this.last == null) ? null : this.last.item;
      return (E)object;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean removeFirstOccurrence(Object paramObject) {
    if (paramObject == null)
      return false; 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      for (Node node = this.first; node != null; node = node.next) {
        if (paramObject.equals(node.item)) {
          unlink(node);
          return true;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean removeLastOccurrence(Object paramObject) {
    if (paramObject == null)
      return false; 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      for (Node node = this.last; node != null; node = node.prev) {
        if (paramObject.equals(node.item)) {
          unlink(node);
          return true;
        } 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean add(E paramE) {
    addLast(paramE);
    return true;
  }
  
  public boolean offer(E paramE) { return offerLast(paramE); }
  
  public void put(E paramE) { putLast(paramE); }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException { return offerLast(paramE, paramLong, paramTimeUnit); }
  
  public E remove() { return (E)removeFirst(); }
  
  public E poll() { return (E)pollFirst(); }
  
  public E take() { return (E)takeFirst(); }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException { return (E)pollFirst(paramLong, paramTimeUnit); }
  
  public E element() { return (E)getFirst(); }
  
  public E peek() { return (E)peekFirst(); }
  
  public int remainingCapacity() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.capacity - this.count;
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
      int i = Math.min(paramInt, this.count);
      for (null = 0; null < i; null++) {
        paramCollection.add(this.first.item);
        unlinkFirst();
      } 
      return i;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public void push(E paramE) { addFirst(paramE); }
  
  public E pop() { return (E)removeFirst(); }
  
  public boolean remove(Object paramObject) { return removeFirstOccurrence(paramObject); }
  
  public int size() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      return this.count;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      for (Node node = this.first; node != null; node = node.next) {
        if (paramObject.equals(node.item))
          return true; 
      } 
      return false;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Object[] toArray() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Object[] arrayOfObject = new Object[this.count];
      byte b = 0;
      for (Node node = this.first; node != null; node = node.next)
        arrayOfObject[b++] = node.item; 
      return arrayOfObject;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      if (paramArrayOfT.length < this.count)
        paramArrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), this.count); 
      byte b = 0;
      for (Node node = this.first; node != null; node = node.next)
        paramArrayOfT[b++] = node.item; 
      if (paramArrayOfT.length > b)
        paramArrayOfT[b] = null; 
      return paramArrayOfT;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public String toString() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      Node node = this.first;
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
      reentrantLock.unlock();
    } 
  }
  
  public void clear() {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      for (Node node = this.first; node != null; node = node1) {
        node.item = null;
        Node node1 = node.next;
        node.prev = null;
        node.next = null;
      } 
      this.first = this.last = null;
      this.count = 0;
      this.notFull.signalAll();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public Iterator<E> iterator() { return new Itr(null); }
  
  public Iterator<E> descendingIterator() { return new DescendingItr(null); }
  
  public Spliterator<E> spliterator() { return new LBDSpliterator(this); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    reentrantLock = this.lock;
    reentrantLock.lock();
    try {
      paramObjectOutputStream.defaultWriteObject();
      for (Node node = this.first; node != null; node = node.next)
        paramObjectOutputStream.writeObject(node.item); 
      paramObjectOutputStream.writeObject(null);
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.count = 0;
    this.first = null;
    this.last = null;
    while (true) {
      Object object = paramObjectInputStream.readObject();
      if (object == null)
        break; 
      add(object);
    } 
  }
  
  private abstract class AbstractItr extends Object implements Iterator<E> {
    LinkedBlockingDeque.Node<E> next;
    
    E nextItem;
    
    private LinkedBlockingDeque.Node<E> lastRet;
    
    abstract LinkedBlockingDeque.Node<E> firstNode();
    
    abstract LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> param1Node);
    
    AbstractItr() {
      reentrantLock = LinkedBlockingDeque.this.lock;
      reentrantLock.lock();
      try {
        this.next = firstNode();
        this.nextItem = (this.next == null) ? null : this.next.item;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    private LinkedBlockingDeque.Node<E> succ(LinkedBlockingDeque.Node<E> param1Node) {
      while (true) {
        LinkedBlockingDeque.Node node = nextNode(param1Node);
        if (node == null)
          return null; 
        if (node.item != null)
          return node; 
        if (node == param1Node)
          return firstNode(); 
        param1Node = node;
      } 
    }
    
    void advance() {
      reentrantLock = LinkedBlockingDeque.this.lock;
      reentrantLock.lock();
      try {
        this.next = succ(this.next);
        this.nextItem = (this.next == null) ? null : this.next.item;
      } finally {
        reentrantLock.unlock();
      } 
    }
    
    public boolean hasNext() { return (this.next != null); }
    
    public E next() {
      if (this.next == null)
        throw new NoSuchElementException(); 
      this.lastRet = this.next;
      Object object = this.nextItem;
      advance();
      return (E)object;
    }
    
    public void remove() {
      LinkedBlockingDeque.Node node = this.lastRet;
      if (node == null)
        throw new IllegalStateException(); 
      this.lastRet = null;
      reentrantLock = LinkedBlockingDeque.this.lock;
      reentrantLock.lock();
      try {
        if (node.item != null)
          LinkedBlockingDeque.this.unlink(node); 
      } finally {
        reentrantLock.unlock();
      } 
    }
  }
  
  private class DescendingItr extends AbstractItr {
    private DescendingItr() { super(LinkedBlockingDeque.this); }
    
    LinkedBlockingDeque.Node<E> firstNode() { return LinkedBlockingDeque.this.last; }
    
    LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> param1Node) { return param1Node.prev; }
  }
  
  private class Itr extends AbstractItr {
    private Itr() { super(LinkedBlockingDeque.this); }
    
    LinkedBlockingDeque.Node<E> firstNode() { return LinkedBlockingDeque.this.first; }
    
    LinkedBlockingDeque.Node<E> nextNode(LinkedBlockingDeque.Node<E> param1Node) { return param1Node.next; }
  }
  
  static final class LBDSpliterator<E> extends Object implements Spliterator<E> {
    static final int MAX_BATCH = 33554432;
    
    final LinkedBlockingDeque<E> queue;
    
    LinkedBlockingDeque.Node<E> current;
    
    int batch;
    
    boolean exhausted;
    
    long est;
    
    LBDSpliterator(LinkedBlockingDeque<E> param1LinkedBlockingDeque) {
      this.queue = param1LinkedBlockingDeque;
      this.est = param1LinkedBlockingDeque.size();
    }
    
    public long estimateSize() { return this.est; }
    
    public Spliterator<E> trySplit() {
      LinkedBlockingDeque linkedBlockingDeque = this.queue;
      int i = this.batch;
      boolean bool = (i <= 0) ? 1 : ((i >= 33554432) ? 33554432 : (i + 1));
      LinkedBlockingDeque.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = linkedBlockingDeque.first) != null) && node.next != null) {
        Object[] arrayOfObject = new Object[bool];
        reentrantLock = linkedBlockingDeque.lock;
        byte b = 0;
        LinkedBlockingDeque.Node node1 = this.current;
        reentrantLock.lock();
        try {
          if (node1 != null || (node1 = linkedBlockingDeque.first) != null)
            do {
              arrayOfObject[b] = node1.item;
              if (node1.item == null)
                continue; 
              b++;
            } while ((node1 = node1.next) != null && b < bool); 
        } finally {
          reentrantLock.unlock();
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
      LinkedBlockingDeque linkedBlockingDeque = this.queue;
      reentrantLock = linkedBlockingDeque.lock;
      if (!this.exhausted) {
        this.exhausted = true;
        LinkedBlockingDeque.Node node = this.current;
        do {
          Object object = null;
          reentrantLock.lock();
          try {
            if (node == null)
              node = linkedBlockingDeque.first; 
            while (node != null) {
              object = node.item;
              node = node.next;
              if (object != null)
                break; 
            } 
          } finally {
            reentrantLock.unlock();
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
      LinkedBlockingDeque linkedBlockingDeque = this.queue;
      reentrantLock = linkedBlockingDeque.lock;
      if (!this.exhausted) {
        Object object = null;
        reentrantLock.lock();
        try {
          if (this.current == null)
            this.current = linkedBlockingDeque.first; 
          while (this.current != null) {
            object = this.current.item;
            this.current = this.current.next;
            if (object != null)
              break; 
          } 
        } finally {
          reentrantLock.unlock();
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
  
  static final class Node<E> extends Object {
    E item;
    
    Node<E> prev;
    
    Node<E> next;
    
    Node(E param1E) { this.item = param1E; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\LinkedBlockingDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */