package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class ConcurrentLinkedDeque<E> extends AbstractCollection<E> implements Deque<E>, Serializable {
  private static final long serialVersionUID = 876323262645176354L;
  
  private static final Node<Object> PREV_TERMINATOR = new Node();
  
  private static final Node<Object> NEXT_TERMINATOR;
  
  private static final int HOPS = 2;
  
  private static final Unsafe UNSAFE;
  
  private static final long headOffset;
  
  private static final long tailOffset;
  
  Node<E> prevTerminator() { return PREV_TERMINATOR; }
  
  Node<E> nextTerminator() { return NEXT_TERMINATOR; }
  
  private void linkFirst(E paramE) {
    Node node3;
    Node node2;
    checkNotNull(paramE);
    Node node1 = new Node(paramE);
    label22: while (true) {
      node2 = this.head;
      node3 = node2;
      while (true) {
        Node node;
        if ((node = node3.prev) != null && (node = (node3 = node).prev) != null) {
          node3 = (node2 != (node2 = this.head)) ? node2 : node;
          continue;
        } 
        if (node3.next == node3)
          continue label22; 
        node1.lazySetNext(node3);
        if (node3.casPrev(null, node1))
          break; 
      } 
      break;
    } 
    if (node3 != node2)
      casHead(node2, node1); 
  }
  
  private void linkLast(E paramE) {
    Node node3;
    Node node2;
    checkNotNull(paramE);
    Node node1 = new Node(paramE);
    label22: while (true) {
      node2 = this.tail;
      node3 = node2;
      while (true) {
        Node node;
        if ((node = node3.next) != null && (node = (node3 = node).next) != null) {
          node3 = (node2 != (node2 = this.tail)) ? node2 : node;
          continue;
        } 
        if (node3.prev == node3)
          continue label22; 
        node1.lazySetPrev(node3);
        if (node3.casNext(null, node1))
          break; 
      } 
      break;
    } 
    if (node3 != node2)
      casTail(node2, node1); 
  }
  
  void unlink(Node<E> paramNode) {
    Node node1 = paramNode.prev;
    Node node2 = paramNode.next;
    if (node1 == null) {
      unlinkFirst(paramNode, node2);
    } else if (node2 == null) {
      unlinkLast(paramNode, node1);
    } else {
      byte b2;
      byte b1;
      Node node4;
      Node node3;
      byte b3 = 1;
      Node node5 = node1;
      while (true) {
        if (node5.item != null) {
          node3 = node5;
          b1 = 0;
          break;
        } 
        Node node = node5.prev;
        if (node == null) {
          if (node5.next == node5)
            return; 
          node3 = node5;
          b1 = 1;
          break;
        } 
        if (node5 == node)
          return; 
        node5 = node;
        b3++;
      } 
      node5 = node2;
      while (true) {
        if (node5.item != null) {
          node4 = node5;
          b2 = 0;
          break;
        } 
        Node node = node5.next;
        if (node == null) {
          if (node5.prev == node5)
            return; 
          node4 = node5;
          b2 = 1;
          break;
        } 
        if (node5 == node)
          return; 
        node5 = node;
        b3++;
      } 
      if (b3 < 2 && b1 | b2)
        return; 
      skipDeletedSuccessors(node3);
      skipDeletedPredecessors(node4);
      if ((b1 | b2) != 0 && node3.next == node4 && node4.prev == node3 && ((b1 != 0) ? (node3.prev == null) : (node3.item != null)) && ((b2 != 0) ? (node4.next == null) : (node4.item != null))) {
        updateHead();
        updateTail();
        paramNode.lazySetPrev((b1 != 0) ? prevTerminator() : paramNode);
        paramNode.lazySetNext((b2 != 0) ? nextTerminator() : paramNode);
      } 
    } 
  }
  
  private void unlinkFirst(Node<E> paramNode1, Node<E> paramNode2) {
    Node node1 = null;
    for (Node<E> node2 = paramNode2;; node2 = node) {
      Node node;
      if (node2.item != null || (node = node2.next) == null) {
        if (node1 != null && node2.prev != node2 && paramNode1.casNext(paramNode2, node2)) {
          skipDeletedPredecessors(node2);
          if (paramNode1.prev == null && (node2.next == null || node2.item != null) && node2.prev == paramNode1) {
            updateHead();
            updateTail();
            node1.lazySetNext(node1);
            node1.lazySetPrev(prevTerminator());
          } 
        } 
        return;
      } 
      if (node2 == node)
        return; 
      node1 = node2;
    } 
  }
  
  private void unlinkLast(Node<E> paramNode1, Node<E> paramNode2) {
    Node node1 = null;
    for (Node<E> node2 = paramNode2;; node2 = node) {
      Node node;
      if (node2.item != null || (node = node2.prev) == null) {
        if (node1 != null && node2.next != node2 && paramNode1.casPrev(paramNode2, node2)) {
          skipDeletedSuccessors(node2);
          if (paramNode1.next == null && (node2.prev == null || node2.item != null) && node2.next == paramNode1) {
            updateHead();
            updateTail();
            node1.lazySetPrev(node1);
            node1.lazySetNext(nextTerminator());
          } 
        } 
        return;
      } 
      if (node2 == node)
        return; 
      node1 = node2;
    } 
  }
  
  private final void updateHead() {
    Node node1;
    Node node2;
    label19: while ((node1 = this.head).item == null && (node2 = node1.prev) != null) {
      while (true) {
        Node node;
        if ((node = node2.prev) == null || (node = (node2 = node).prev) == null) {
          if (casHead(node1, node2))
            return; 
          continue label19;
        } 
        if (node1 != this.head)
          continue label19; 
        node2 = node;
      } 
    } 
  }
  
  private final void updateTail() {
    Node node1;
    Node node2;
    label19: while ((node1 = this.tail).item == null && (node2 = node1.next) != null) {
      while (true) {
        Node node;
        if ((node = node2.next) == null || (node = (node2 = node).next) == null) {
          if (casTail(node1, node2))
            return; 
          continue label19;
        } 
        if (node1 != this.tail)
          continue label19; 
        node2 = node;
      } 
    } 
  }
  
  private void skipDeletedPredecessors(Node<E> paramNode) { label21: do {
      Node node1 = paramNode.prev;
      Node node2;
      for (node2 = node1; node2.item == null; node2 = node) {
        Node node = node2.prev;
        if (node == null) {
          if (node2.next == node2)
            continue label21; 
          break;
        } 
        if (node2 == node)
          continue label21; 
      } 
      if (node1 == node2 || paramNode.casPrev(node1, node2))
        return; 
    } while (paramNode.item != null || paramNode.next == null); }
  
  private void skipDeletedSuccessors(Node<E> paramNode) { label21: do {
      Node node1 = paramNode.next;
      Node node2;
      for (node2 = node1; node2.item == null; node2 = node) {
        Node node = node2.next;
        if (node == null) {
          if (node2.prev == node2)
            continue label21; 
          break;
        } 
        if (node2 == node)
          continue label21; 
      } 
      if (node1 == node2 || paramNode.casNext(node1, node2))
        return; 
    } while (paramNode.item != null || paramNode.prev == null); }
  
  final Node<E> succ(Node<E> paramNode) {
    Node node = paramNode.next;
    return (paramNode == node) ? first() : node;
  }
  
  final Node<E> pred(Node<E> paramNode) {
    Node node = paramNode.prev;
    return (paramNode == node) ? last() : node;
  }
  
  Node<E> first() {
    Node node2;
    Node node1;
    do {
      node1 = this.head;
      Node node;
      for (node2 = node1; (node = node2.prev) != null && (node = (node2 = node).prev) != null; node2 = (node1 != (node1 = this.head)) ? node1 : node);
    } while (node2 != node1 && !casHead(node1, node2));
    return node2;
  }
  
  Node<E> last() {
    Node node2;
    Node node1;
    do {
      node1 = this.tail;
      Node node;
      for (node2 = node1; (node = node2.next) != null && (node = (node2 = node).next) != null; node2 = (node1 != (node1 = this.tail)) ? node1 : node);
    } while (node2 != node1 && !casTail(node1, node2));
    return node2;
  }
  
  private static void checkNotNull(Object paramObject) {
    if (paramObject == null)
      throw new NullPointerException(); 
  }
  
  private E screenNullResult(E paramE) {
    if (paramE == null)
      throw new NoSuchElementException(); 
    return paramE;
  }
  
  private ArrayList<E> toArrayList() {
    ArrayList arrayList = new ArrayList();
    for (Node node = first(); node != null; node = succ(node)) {
      Object object = node.item;
      if (object != null)
        arrayList.add(object); 
    } 
    return arrayList;
  }
  
  public ConcurrentLinkedDeque() { this.head = this.tail = new Node(null); }
  
  public ConcurrentLinkedDeque(Collection<? extends E> paramCollection) {
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
      node.lazySetPrev(node2);
      node2 = node;
    } 
    initHeadTail(node1, node2);
  }
  
  private void initHeadTail(Node<E> paramNode1, Node<E> paramNode2) {
    if (paramNode1 == paramNode2)
      if (paramNode1 == null) {
        paramNode1 = paramNode2 = new Node<E>(null);
      } else {
        Node node = new Node(null);
        paramNode2.lazySetNext(node);
        node.lazySetPrev(paramNode2);
        paramNode2 = node;
      }  
    this.head = paramNode1;
    this.tail = paramNode2;
  }
  
  public void addFirst(E paramE) { linkFirst(paramE); }
  
  public void addLast(E paramE) { linkLast(paramE); }
  
  public boolean offerFirst(E paramE) {
    linkFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE) {
    linkLast(paramE);
    return true;
  }
  
  public E peekFirst() {
    for (Node node = first(); node != null; node = succ(node)) {
      Object object = node.item;
      if (object != null)
        return (E)object; 
    } 
    return null;
  }
  
  public E peekLast() {
    for (Node node = last(); node != null; node = pred(node)) {
      Object object = node.item;
      if (object != null)
        return (E)object; 
    } 
    return null;
  }
  
  public E getFirst() { return (E)screenNullResult(peekFirst()); }
  
  public E getLast() { return (E)screenNullResult(peekLast()); }
  
  public E pollFirst() {
    for (Node node = first(); node != null; node = succ(node)) {
      Object object = node.item;
      if (object != null && node.casItem(object, null)) {
        unlink(node);
        return (E)object;
      } 
    } 
    return null;
  }
  
  public E pollLast() {
    for (Node node = last(); node != null; node = pred(node)) {
      Object object = node.item;
      if (object != null && node.casItem(object, null)) {
        unlink(node);
        return (E)object;
      } 
    } 
    return null;
  }
  
  public E removeFirst() { return (E)screenNullResult(pollFirst()); }
  
  public E removeLast() { return (E)screenNullResult(pollLast()); }
  
  public boolean offer(E paramE) { return offerLast(paramE); }
  
  public boolean add(E paramE) { return offerLast(paramE); }
  
  public E poll() { return (E)pollFirst(); }
  
  public E peek() { return (E)peekFirst(); }
  
  public E remove() { return (E)removeFirst(); }
  
  public E pop() { return (E)removeFirst(); }
  
  public E element() { return (E)getFirst(); }
  
  public void push(E paramE) { addFirst(paramE); }
  
  public boolean removeFirstOccurrence(Object paramObject) {
    checkNotNull(paramObject);
    for (Node node = first(); node != null; node = succ(node)) {
      Object object = node.item;
      if (object != null && paramObject.equals(object) && node.casItem(object, null)) {
        unlink(node);
        return true;
      } 
    } 
    return false;
  }
  
  public boolean removeLastOccurrence(Object paramObject) {
    checkNotNull(paramObject);
    for (Node node = last(); node != null; node = pred(node)) {
      Object object = node.item;
      if (object != null && paramObject.equals(object) && node.casItem(object, null)) {
        unlink(node);
        return true;
      } 
    } 
    return false;
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
  
  public boolean isEmpty() { return (peekFirst() == null); }
  
  public int size() {
    byte b = 0;
    for (Node node = first(); node != null && (node.item == null || ++b != Integer.MAX_VALUE); node = succ(node));
    return b;
  }
  
  public boolean remove(Object paramObject) { return removeFirstOccurrence(paramObject); }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    Node node3;
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
      node.lazySetPrev(node2);
      node2 = node;
    } 
    if (node1 == null)
      return false; 
    label36: while (true) {
      node3 = this.tail;
      Node node = node3;
      while (true) {
        Node node4;
        if ((node4 = node.next) != null && (node4 = (node = node4).next) != null) {
          node = (node3 != (node3 = this.tail)) ? node3 : node4;
          continue;
        } 
        if (node.prev == node)
          continue label36; 
        node1.lazySetPrev(node);
        if (node.casNext(null, node1))
          break; 
      } 
      break;
    } 
    if (!casTail(node3, node2)) {
      node3 = this.tail;
      if (node2.next == null)
        casTail(node3, node2); 
    } 
    return true;
  }
  
  public void clear() {
    while (pollFirst() != null);
  }
  
  public Object[] toArray() { return toArrayList().toArray(); }
  
  public <T> T[] toArray(T[] paramArrayOfT) { return (T[])toArrayList().toArray(paramArrayOfT); }
  
  public Iterator<E> iterator() { return new Itr(null); }
  
  public Iterator<E> descendingIterator() { return new DescendingItr(null); }
  
  public Spliterator<E> spliterator() { return new CLDSpliterator(this); }
  
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
      node.lazySetPrev(node2);
    } 
    initHeadTail(node1, node2);
  }
  
  private boolean casHead(Node<E> paramNode1, Node<E> paramNode2) { return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2); }
  
  private boolean casTail(Node<E> paramNode1, Node<E> paramNode2) { return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2); }
  
  static  {
    PREV_TERMINATOR.next = PREV_TERMINATOR;
    NEXT_TERMINATOR = new Node();
    NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = ConcurrentLinkedDeque.class;
      headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tail"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  private abstract class AbstractItr extends Object implements Iterator<E> {
    private ConcurrentLinkedDeque.Node<E> nextNode;
    
    private E nextItem;
    
    private ConcurrentLinkedDeque.Node<E> lastRet;
    
    abstract ConcurrentLinkedDeque.Node<E> startNode();
    
    abstract ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> param1Node);
    
    AbstractItr() { advance(); }
    
    private void advance() {
      this.lastRet = this.nextNode;
      for (ConcurrentLinkedDeque.Node node = (this.nextNode == null) ? startNode() : nextNode(this.nextNode);; node = nextNode(node)) {
        if (node == null) {
          this.nextNode = null;
          this.nextItem = null;
          break;
        } 
        Object object = node.item;
        if (object != null) {
          this.nextNode = node;
          this.nextItem = object;
          break;
        } 
      } 
    }
    
    public boolean hasNext() { return (this.nextItem != null); }
    
    public E next() {
      Object object = this.nextItem;
      if (object == null)
        throw new NoSuchElementException(); 
      advance();
      return (E)object;
    }
    
    public void remove() {
      ConcurrentLinkedDeque.Node node = this.lastRet;
      if (node == null)
        throw new IllegalStateException(); 
      node.item = null;
      ConcurrentLinkedDeque.this.unlink(node);
      this.lastRet = null;
    }
  }
  
  static final class CLDSpliterator<E> extends Object implements Spliterator<E> {
    static final int MAX_BATCH = 33554432;
    
    final ConcurrentLinkedDeque<E> queue;
    
    ConcurrentLinkedDeque.Node<E> current;
    
    int batch;
    
    boolean exhausted;
    
    CLDSpliterator(ConcurrentLinkedDeque<E> param1ConcurrentLinkedDeque) { this.queue = param1ConcurrentLinkedDeque; }
    
    public Spliterator<E> trySplit() {
      ConcurrentLinkedDeque concurrentLinkedDeque = this.queue;
      int i = this.batch;
      boolean bool = (i <= 0) ? 1 : ((i >= 33554432) ? 33554432 : (i + 1));
      ConcurrentLinkedDeque.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = concurrentLinkedDeque.first()) != null)) {
        if (node.item == null && node == (node = node.next))
          this.current = node = concurrentLinkedDeque.first(); 
        if (node != null && node.next != null) {
          Object[] arrayOfObject = new Object[bool];
          byte b = 0;
          do {
            arrayOfObject[b] = node.item;
            if (node.item != null)
              b++; 
            if (node != (node = node.next))
              continue; 
            node = concurrentLinkedDeque.first();
          } while (node != null && b < bool);
          if ((this.current = node) == null)
            this.exhausted = true; 
          if (b > 0) {
            this.batch = b;
            return Spliterators.spliterator(arrayOfObject, 0, b, 4368);
          } 
        } 
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentLinkedDeque concurrentLinkedDeque = this.queue;
      ConcurrentLinkedDeque.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = concurrentLinkedDeque.first()) != null)) {
        this.exhausted = true;
        do {
          Object object = node.item;
          if (node == (node = node.next))
            node = concurrentLinkedDeque.first(); 
          if (object == null)
            continue; 
          param1Consumer.accept(object);
        } while (node != null);
      } 
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      ConcurrentLinkedDeque concurrentLinkedDeque = this.queue;
      ConcurrentLinkedDeque.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = concurrentLinkedDeque.first()) != null)) {
        Object object;
        do {
          object = node.item;
          if (node != (node = node.next))
            continue; 
          node = concurrentLinkedDeque.first();
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
  
  private class DescendingItr extends AbstractItr {
    private DescendingItr() { super(ConcurrentLinkedDeque.this); }
    
    ConcurrentLinkedDeque.Node<E> startNode() { return ConcurrentLinkedDeque.this.last(); }
    
    ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> param1Node) { return ConcurrentLinkedDeque.this.pred(param1Node); }
  }
  
  private class Itr extends AbstractItr {
    private Itr() { super(ConcurrentLinkedDeque.this); }
    
    ConcurrentLinkedDeque.Node<E> startNode() { return ConcurrentLinkedDeque.this.first(); }
    
    ConcurrentLinkedDeque.Node<E> nextNode(ConcurrentLinkedDeque.Node<E> param1Node) { return ConcurrentLinkedDeque.this.succ(param1Node); }
  }
  
  static final class Node<E> extends Object {
    private static final Unsafe UNSAFE;
    
    private static final long prevOffset;
    
    private static final long itemOffset;
    
    private static final long nextOffset;
    
    Node() {}
    
    Node(E param1E) { UNSAFE.putObject(this, itemOffset, param1E); }
    
    boolean casItem(E param1E1, E param1E2) { return UNSAFE.compareAndSwapObject(this, itemOffset, param1E1, param1E2); }
    
    void lazySetNext(Node<E> param1Node) { UNSAFE.putOrderedObject(this, nextOffset, param1Node); }
    
    boolean casNext(Node<E> param1Node1, Node<E> param1Node2) { return UNSAFE.compareAndSwapObject(this, nextOffset, param1Node1, param1Node2); }
    
    void lazySetPrev(Node<E> param1Node) { UNSAFE.putOrderedObject(this, prevOffset, param1Node); }
    
    boolean casPrev(Node<E> param1Node1, Node<E> param1Node2) { return UNSAFE.compareAndSwapObject(this, prevOffset, param1Node1, param1Node2); }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = Node.class;
        prevOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("prev"));
        itemOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("item"));
        nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ConcurrentLinkedDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */