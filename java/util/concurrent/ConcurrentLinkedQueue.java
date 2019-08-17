package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {
  private static final long serialVersionUID = 196745693267521676L;
  
  private static final Unsafe UNSAFE;
  
  private static final long headOffset;
  
  private static final long tailOffset;
  
  public ConcurrentLinkedQueue() { this.head = this.tail = new Node(null); }
  
  public ConcurrentLinkedQueue(Collection<? extends E> paramCollection) {
    Node node1 = null;
    Node node2 = null;
    for (Object object : paramCollection) {
      checkNotNull(object);
      Node node = new Node(object);
      if (node1 == null) {
        node1 = node2 = node;
        continue;
      } 
      node2.lazySetNext(node);
      node2 = node;
    } 
    if (node1 == null)
      node1 = node2 = new Node(null); 
    this.head = node1;
    this.tail = node2;
  }
  
  public boolean add(E paramE) { return offer(paramE); }
  
  final void updateHead(Node<E> paramNode1, Node<E> paramNode2) {
    if (paramNode1 != paramNode2 && casHead(paramNode1, paramNode2))
      paramNode1.lazySetNext(paramNode1); 
  }
  
  final Node<E> succ(Node<E> paramNode) {
    Node node = paramNode.next;
    return (paramNode == node) ? this.head : node;
  }
  
  public boolean offer(E paramE) {
    checkNotNull(paramE);
    Node node1 = new Node(paramE);
    Node node2 = this.tail;
    for (Node node3 = node2;; node3 = (node3 != node2 && node2 != (node2 = this.tail)) ? node2 : node) {
      Node node = node3.next;
      if (node == null) {
        if (node3.casNext(null, node1)) {
          if (node3 != node2)
            casTail(node2, node1); 
          return true;
        } 
        continue;
      } 
      if (node3 == node) {
        node3 = (node2 != (node2 = this.tail)) ? node2 : this.head;
        continue;
      } 
    } 
  }
  
  public E poll() { label21: while (true) {
      Node node1 = this.head;
      for (Node node2 = node1;; node2 = node) {
        Object object = node2.item;
        if (object != null && node2.casItem(object, null)) {
          if (node2 != node1) {
            Node node3;
            updateHead(node1, ((node3 = node2.next) != null) ? node3 : node2);
          } 
          return (E)object;
        } 
        Node node;
        if ((node = node2.next) == null) {
          updateHead(node1, node2);
          return null;
        } 
        if (node2 == node)
          continue label21; 
      } 
      break;
    }  }
  
  public E peek() { label13: while (true) {
      Node node1 = this.head;
      for (Node node2 = node1;; node2 = node) {
        Object object = node2.item;
        Node node;
        if (object != null || (node = node2.next) == null) {
          updateHead(node1, node2);
          return (E)object;
        } 
        if (node2 == node)
          continue label13; 
      } 
      break;
    }  }
  
  Node<E> first() { label20: while (true) {
      Node node1 = this.head;
      for (Node node2 = node1;; node2 = node) {
        boolean bool = (node2.item != null) ? 1 : 0;
        Node node;
        if (bool || (node = node2.next) == null) {
          updateHead(node1, node2);
          return bool ? node2 : null;
        } 
        if (node2 == node)
          continue label20; 
      } 
      break;
    }  }
  
  public boolean isEmpty() { return (first() == null); }
  
  public int size() {
    byte b = 0;
    for (Node node = first(); node != null && (node.item == null || ++b != Integer.MAX_VALUE); node = succ(node));
    return b;
  }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    for (Node node = first(); node != null; node = succ(node)) {
      Object object = node.item;
      if (object != null && paramObject.equals(object))
        return true; 
    } 
    return false;
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject != null) {
      Node node = null;
      for (Object object = first();; object = SYNTHETIC_LOCAL_VARIABLE_2) {
        if (object != null) {
          boolean bool = false;
          Object object1 = object.item;
          if (object1 != null) {
            if (!paramObject.equals(object1)) {
              Node node2 = succ(object);
            } else {
              bool = object.casItem(object1, null);
              Node node2 = succ(object);
            } 
            continue;
          } 
        } else {
          break;
        } 
        Node node1 = succ(object);
        node = object;
      } 
    } 
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    Node node1 = null;
    Node node2 = null;
    for (Object object : paramCollection) {
      checkNotNull(object);
      Node node = new Node(object);
      if (node1 == null) {
        node1 = node2 = node;
        continue;
      } 
      node2.lazySetNext(node);
      node2 = node;
    } 
    if (node1 == null)
      return false; 
    Node node3 = this.tail;
    for (Node node4 = node3;; node4 = (node4 != node3 && node3 != (node3 = this.tail)) ? node3 : node) {
      Node node = node4.next;
      if (node == null) {
        if (node4.casNext(null, node1)) {
          if (!casTail(node3, node2)) {
            node3 = this.tail;
            if (node2.next == null)
              casTail(node3, node2); 
          } 
          return true;
        } 
        continue;
      } 
      if (node4 == node) {
        node4 = (node3 != (node3 = this.tail)) ? node3 : this.head;
        continue;
      } 
    } 
  }
  
  public Object[] toArray() {
    ArrayList arrayList = new ArrayList();
    for (Node node = first(); node != null; node = succ(node)) {
      Object object = node.item;
      if (object != null)
        arrayList.add(object); 
    } 
    return arrayList.toArray();
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    byte b = 0;
    Node node1;
    for (node1 = first(); node1 != null && b < paramArrayOfT.length; node1 = succ(node1)) {
      Object object = node1.item;
      if (object != null)
        paramArrayOfT[b++] = object; 
    } 
    if (node1 == null) {
      if (b < paramArrayOfT.length)
        paramArrayOfT[b] = null; 
      return paramArrayOfT;
    } 
    ArrayList arrayList = new ArrayList();
    for (Node node2 = first(); node2 != null; node2 = succ(node2)) {
      Object object = node2.item;
      if (object != null)
        arrayList.add(object); 
    } 
    return (T[])arrayList.toArray(paramArrayOfT);
  }
  
  public Iterator<E> iterator() { return new Itr(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    for (Node node = first(); node != null; node = succ(node)) {
      Object object = node.item;
      if (object != null)
        paramObjectOutputStream.writeObject(object); 
    } 
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Node node1 = null;
    Node node2;
    Object object;
    for (node2 = null; (object = paramObjectInputStream.readObject()) != null; node2 = node) {
      Node node = new Node(object);
      if (node1 == null) {
        node1 = node2 = node;
        continue;
      } 
      node2.lazySetNext(node);
    } 
    if (node1 == null)
      node1 = node2 = new Node(null); 
    this.head = node1;
    this.tail = node2;
  }
  
  public Spliterator<E> spliterator() { return new CLQSpliterator(this); }
  
  private static void checkNotNull(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
  }
  
  private boolean casTail(Node<E> paramNode1, Node<E> paramNode2) { return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2); }
  
  private boolean casHead(Node<E> paramNode1, Node<E> paramNode2) { return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2); }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = ConcurrentLinkedQueue.class;
      headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tail"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class CLQSpliterator<E> extends Object implements Spliterator<E> {
    static final int MAX_BATCH = 33554432;
    
    final ConcurrentLinkedQueue<E> queue;
    
    ConcurrentLinkedQueue.Node<E> current;
    
    int batch;
    
    boolean exhausted;
    
    CLQSpliterator(ConcurrentLinkedQueue<E> param1ConcurrentLinkedQueue) { this.queue = param1ConcurrentLinkedQueue; }
    
    public Spliterator<E> trySplit() {
      ConcurrentLinkedQueue concurrentLinkedQueue = this.queue;
      int i = this.batch;
      boolean bool = (i <= 0) ? 1 : ((i >= 33554432) ? 33554432 : (i + 1));
      ConcurrentLinkedQueue.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = concurrentLinkedQueue.first()) != null) && node.next != null) {
        Object[] arrayOfObject = new Object[bool];
        byte b = 0;
        do {
          arrayOfObject[b] = node.item;
          if (node.item != null)
            b++; 
          if (node != (node = node.next))
            continue; 
          node = concurrentLinkedQueue.first();
        } while (node != null && b < bool);
        if ((this.current = node) == null)
          this.exhausted = true; 
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
      ConcurrentLinkedQueue concurrentLinkedQueue = this.queue;
      ConcurrentLinkedQueue.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = concurrentLinkedQueue.first()) != null)) {
        this.exhausted = true;
        do {
          Object object = node.item;
          if (node == (node = node.next))
            node = concurrentLinkedQueue.first(); 
          if (object == null)
            continue; 
          param1Consumer.accept(object);
        } while (node != null);
      } 
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentLinkedQueue concurrentLinkedQueue = this.queue;
      ConcurrentLinkedQueue.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = concurrentLinkedQueue.first()) != null)) {
        Object object;
        do {
          object = node.item;
          if (node != (node = node.next))
            continue; 
          node = concurrentLinkedQueue.first();
        } while (object == null && node != null);
        if ((this.current = node) == null)
          this.exhausted = true; 
        if (object != null) {
          param1Consumer.accept(object);
          return true;
        } 
      } 
      return false;
    }
    
    public long estimateSize() { return Float.MAX_VALUE; }
    
    public int characteristics() { return 4368; }
  }
  
  private class Itr extends Object implements Iterator<E> {
    private ConcurrentLinkedQueue.Node<E> nextNode;
    
    private E nextItem;
    
    private ConcurrentLinkedQueue.Node<E> lastRet;
    
    Itr() { advance(); }
    
    private E advance() {
      ConcurrentLinkedQueue.Node node2;
      ConcurrentLinkedQueue.Node node1;
      this.lastRet = this.nextNode;
      Object object = this.nextItem;
      if (this.nextNode == null) {
        node2 = ConcurrentLinkedQueue.this.first();
        node1 = null;
      } else {
        node1 = this.nextNode;
        node2 = ConcurrentLinkedQueue.this.succ(this.nextNode);
      } 
      while (true) {
        if (node2 == null) {
          this.nextNode = null;
          this.nextItem = null;
          return (E)object;
        } 
        Object object1 = node2.item;
        if (object1 != null) {
          this.nextNode = node2;
          this.nextItem = object1;
          return (E)object;
        } 
        ConcurrentLinkedQueue.Node node = ConcurrentLinkedQueue.this.succ(node2);
        if (node1 != null && node != null)
          node1.casNext(node2, node); 
        node2 = node;
      } 
    }
    
    public boolean hasNext() { return (this.nextNode != null); }
    
    public E next() {
      if (this.nextNode == null)
        throw new NoSuchElementException(); 
      return (E)advance();
    }
    
    public void remove() {
      ConcurrentLinkedQueue.Node node = this.lastRet;
      if (node == null)
        throw new IllegalStateException(); 
      node.item = null;
      this.lastRet = null;
    }
  }
  
  private static class Node<E> extends Object {
    private static final Unsafe UNSAFE;
    
    private static final long itemOffset;
    
    private static final long nextOffset;
    
    Node(E param1E) { UNSAFE.putObject(this, itemOffset, param1E); }
    
    boolean casItem(E param1E1, E param1E2) { return UNSAFE.compareAndSwapObject(this, itemOffset, param1E1, param1E2); }
    
    void lazySetNext(Node<E> param1Node) { UNSAFE.putOrderedObject(this, nextOffset, param1Node); }
    
    boolean casNext(Node<E> param1Node1, Node<E> param1Node2) { return UNSAFE.compareAndSwapObject(this, nextOffset, param1Node1, param1Node2); }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = Node.class;
        itemOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("item"));
        nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ConcurrentLinkedQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */