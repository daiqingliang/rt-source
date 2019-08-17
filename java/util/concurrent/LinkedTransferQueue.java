package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Consumer;
import sun.misc.Unsafe;

public class LinkedTransferQueue<E> extends AbstractQueue<E> implements TransferQueue<E>, Serializable {
  private static final long serialVersionUID = -3223113410248163686L;
  
  private static final boolean MP = (Runtime.getRuntime().availableProcessors() > 1);
  
  private static final int FRONT_SPINS = 128;
  
  private static final int CHAINED_SPINS = 64;
  
  static final int SWEEP_THRESHOLD = 32;
  
  private static final int NOW = 0;
  
  private static final int ASYNC = 1;
  
  private static final int SYNC = 2;
  
  private static final int TIMED = 3;
  
  private static final Unsafe UNSAFE;
  
  private static final long headOffset;
  
  private static final long tailOffset;
  
  private static final long sweepVotesOffset;
  
  private boolean casTail(Node paramNode1, Node paramNode2) { return UNSAFE.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2); }
  
  private boolean casHead(Node paramNode1, Node paramNode2) { return UNSAFE.compareAndSwapObject(this, headOffset, paramNode1, paramNode2); }
  
  private boolean casSweepVotes(int paramInt1, int paramInt2) { return UNSAFE.compareAndSwapInt(this, sweepVotesOffset, paramInt1, paramInt2); }
  
  static <E> E cast(Object paramObject) { return (E)paramObject; }
  
  private E xfer(E paramE, boolean paramBoolean, int paramInt, long paramLong) {
    if (paramBoolean && paramE == null)
      throw new NullPointerException(); 
    Node node = null;
    while (true) {
      Node node1 = this.head;
      for (Node node2 = node1; node2 != null; node2 = (node2 != node3) ? node3 : (node1 = this.head)) {
        boolean bool = node2.isData;
        Object object = node2.item;
        if (object != node2 && ((object != null)) == bool) {
          if (bool == paramBoolean)
            break; 
          if (node2.casItem(object, paramE)) {
            Node node4 = node2;
            while (node4 != node1) {
              Node node5 = node4.next;
              if (this.head == node1 && casHead(node1, (node5 == null) ? node4 : node5)) {
                node1.forgetNext();
                break;
              } 
              if ((node1 = this.head) == null || (node4 = node1.next) == null || !node4.isMatched())
                break; 
            } 
            LockSupport.unpark(node2.waiter);
            return (E)cast(object);
          } 
        } 
        Node node3 = node2.next;
      } 
      if (paramInt != 0) {
        if (node == null)
          node = new Node(paramE, paramBoolean); 
        node1 = tryAppend(node, paramBoolean);
        if (node1 == null)
          continue; 
        if (paramInt != 1)
          return (E)awaitMatch(node, node1, paramE, (paramInt == 3), paramLong); 
      } 
      break;
    } 
    return paramE;
  }
  
  private Node tryAppend(Node paramNode, boolean paramBoolean) {
    Node node1 = this.tail;
    Node node2 = node1;
    while (true) {
      if (node2 == null && (node2 = this.head) == null) {
        if (casHead(null, paramNode))
          return paramNode; 
        continue;
      } 
      if (node2.cannotPrecede(paramBoolean))
        return null; 
      Node node;
      if ((node = node2.next) != null) {
        Node node3;
        node2 = (node2 != node1 && node1 != (node3 = this.tail)) ? (node1 = node3) : ((node2 != node) ? node : null);
        continue;
      } 
      if (!node2.casNext(null, paramNode)) {
        node2 = node2.next;
        continue;
      } 
      break;
    } 
    if (node2 != node1)
      while ((this.tail != node1 || !casTail(node1, paramNode)) && (node1 = this.tail) != null && (paramNode = node1.next) != null && (paramNode = paramNode.next) != null && paramNode != node1); 
    return node2;
  }
  
  private E awaitMatch(Node paramNode1, Node paramNode2, E paramE, boolean paramBoolean, long paramLong) {
    long l = paramBoolean ? (System.nanoTime() + paramLong) : 0L;
    Thread thread = Thread.currentThread();
    int i = -1;
    ThreadLocalRandom threadLocalRandom = null;
    while (true) {
      Object object = paramNode1.item;
      if (object != paramE) {
        paramNode1.forgetContents();
        return (E)cast(object);
      } 
      if ((thread.isInterrupted() || (paramBoolean && paramLong <= 0L)) && paramNode1.casItem(paramE, paramNode1)) {
        unsplice(paramNode2, paramNode1);
        return paramE;
      } 
      if (i < 0) {
        if ((i = spinsFor(paramNode2, paramNode1.isData)) > 0)
          threadLocalRandom = ThreadLocalRandom.current(); 
        continue;
      } 
      if (i > 0) {
        i--;
        if (threadLocalRandom.nextInt(64) == 0)
          Thread.yield(); 
        continue;
      } 
      if (paramNode1.waiter == null) {
        paramNode1.waiter = thread;
        continue;
      } 
      if (paramBoolean) {
        paramLong = l - System.nanoTime();
        if (paramLong > 0L)
          LockSupport.parkNanos(this, paramLong); 
        continue;
      } 
      LockSupport.park(this);
    } 
  }
  
  private static int spinsFor(Node paramNode, boolean paramBoolean) {
    if (MP && paramNode != null) {
      if (paramNode.isData != paramBoolean)
        return 192; 
      if (paramNode.isMatched())
        return 128; 
      if (paramNode.waiter == null)
        return 64; 
    } 
    return 0;
  }
  
  final Node succ(Node paramNode) {
    Node node = paramNode.next;
    return (paramNode == node) ? this.head : node;
  }
  
  private Node firstOfMode(boolean paramBoolean) {
    for (Node node = this.head; node != null; node = succ(node)) {
      if (!node.isMatched())
        return (node.isData == paramBoolean) ? node : null; 
    } 
    return null;
  }
  
  final Node firstDataNode() {
    Node node = this.head;
    while (node != null) {
      Object object = node.item;
      if (node.isData) {
        if (object != null && object != node)
          return node; 
      } else if (object == null) {
        break;
      } 
      if (node == (node = node.next))
        node = this.head; 
    } 
    return null;
  }
  
  private E firstDataItem() {
    for (Node node = this.head; node != null; node = succ(node)) {
      Object object = node.item;
      if (node.isData) {
        if (object != null && object != node)
          return (E)cast(object); 
      } else if (object == null) {
        return null;
      } 
    } 
    return null;
  }
  
  private int countOfMode(boolean paramBoolean) {
    byte b = 0;
    for (Node node = this.head; node != null; node = this.head) {
      if (!node.isMatched()) {
        if (node.isData != paramBoolean)
          return 0; 
        if (++b == Integer.MAX_VALUE)
          break; 
      } 
      Node node1 = node.next;
      if (node1 != node) {
        node = node1;
        continue;
      } 
      b = 0;
    } 
    return b;
  }
  
  public Spliterator<E> spliterator() { return new LTQSpliterator(this); }
  
  final void unsplice(Node paramNode1, Node paramNode2) {
    paramNode2.forgetContents();
    if (paramNode1 != null && paramNode1 != paramNode2 && paramNode1.next == paramNode2) {
      Node node = paramNode2.next;
      if (node == null || (node != paramNode2 && paramNode1.casNext(paramNode2, node) && paramNode1.isMatched())) {
        while (true) {
          Node node1 = this.head;
          if (node1 == paramNode1 || node1 == paramNode2 || node1 == null)
            return; 
          if (!node1.isMatched())
            break; 
          Node node2 = node1.next;
          if (node2 == null)
            return; 
          if (node2 != node1 && casHead(node1, node2))
            node1.forgetNext(); 
        } 
        if (paramNode1.next != paramNode1 && paramNode2.next != paramNode2)
          while (true) {
            int i = this.sweepVotes;
            if (i < 32) {
              if (casSweepVotes(i, i + 1))
                break; 
              continue;
            } 
            if (casSweepVotes(i, 0)) {
              sweep();
              break;
            } 
          }  
      } 
    } 
  }
  
  private void sweep() {
    Node node1 = this.head;
    Node node2;
    while (node1 != null && (node2 = node1.next) != null) {
      if (!node2.isMatched()) {
        node1 = node2;
        continue;
      } 
      Node node;
      if ((node = node2.next) == null)
        break; 
      if (node2 == node) {
        node1 = this.head;
        continue;
      } 
      node1.casNext(node2, node);
    } 
  }
  
  private boolean findAndRemove(Object paramObject) {
    if (paramObject != null) {
      Node node1 = null;
      Node node2 = this.head;
      while (node2 != null) {
        Object object = node2.item;
        if (node2.isData) {
          if (object != null && object != node2 && paramObject.equals(object) && node2.tryMatchData()) {
            unsplice(node1, node2);
            return true;
          } 
        } else if (object == null) {
          break;
        } 
        node1 = node2;
        if ((node2 = node2.next) == node1) {
          node1 = null;
          node2 = this.head;
        } 
      } 
    } 
    return false;
  }
  
  public LinkedTransferQueue() {}
  
  public LinkedTransferQueue(Collection<? extends E> paramCollection) {
    this();
    addAll(paramCollection);
  }
  
  public void put(E paramE) { xfer(paramE, true, 1, 0L); }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) {
    xfer(paramE, true, 1, 0L);
    return true;
  }
  
  public boolean offer(E paramE) {
    xfer(paramE, true, 1, 0L);
    return true;
  }
  
  public boolean add(E paramE) {
    xfer(paramE, true, 1, 0L);
    return true;
  }
  
  public boolean tryTransfer(E paramE) { return (xfer(paramE, true, false, 0L) == null); }
  
  public void transfer(E paramE) {
    if (xfer(paramE, true, 2, 0L) != null) {
      Thread.interrupted();
      throw new InterruptedException();
    } 
  }
  
  public boolean tryTransfer(E paramE, long paramLong, TimeUnit paramTimeUnit) {
    if (xfer(paramE, true, 3, paramTimeUnit.toNanos(paramLong)) == null)
      return true; 
    if (!Thread.interrupted())
      return false; 
    throw new InterruptedException();
  }
  
  public E take() {
    Object object = xfer(null, false, 2, 0L);
    if (object != null)
      return (E)object; 
    Thread.interrupted();
    throw new InterruptedException();
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    Object object = xfer(null, false, 3, paramTimeUnit.toNanos(paramLong));
    if (object != null || !Thread.interrupted())
      return (E)object; 
    throw new InterruptedException();
  }
  
  public E poll() { return (E)xfer(null, false, 0, 0L); }
  
  public int drainTo(Collection<? super E> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    byte b;
    Object object;
    for (b = 0; (object = poll()) != null; b++)
      paramCollection.add(object); 
    return b;
  }
  
  public int drainTo(Collection<? super E> paramCollection, int paramInt) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    byte b;
    Object object;
    for (b = 0; b < paramInt && (object = poll()) != null; b++)
      paramCollection.add(object); 
    return b;
  }
  
  public Iterator<E> iterator() { return new Itr(); }
  
  public E peek() { return (E)firstDataItem(); }
  
  public boolean isEmpty() {
    for (Node node = this.head; node != null; node = succ(node)) {
      if (!node.isMatched())
        return !node.isData; 
    } 
    return true;
  }
  
  public boolean hasWaitingConsumer() { return (firstOfMode(false) != null); }
  
  public int size() { return countOfMode(true); }
  
  public int getWaitingConsumerCount() { return countOfMode(false); }
  
  public boolean remove(Object paramObject) { return findAndRemove(paramObject); }
  
  public boolean contains(Object paramObject) {
    if (paramObject == null)
      return false; 
    for (Node node = this.head; node != null; node = succ(node)) {
      Object object = node.item;
      if (node.isData) {
        if (object != null && object != node && paramObject.equals(object))
          return true; 
      } else if (object == null) {
        break;
      } 
    } 
    return false;
  }
  
  public int remainingCapacity() { return Integer.MAX_VALUE; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    for (Object object : this)
      paramObjectOutputStream.writeObject(object); 
    paramObjectOutputStream.writeObject(null);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    while (true) {
      Object object = paramObjectInputStream.readObject();
      if (object == null)
        break; 
      offer(object);
    } 
  }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = LinkedTransferQueue.class;
      headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      tailOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tail"));
      sweepVotesOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("sweepVotes"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  final class Itr extends Object implements Iterator<E> {
    private LinkedTransferQueue.Node nextNode;
    
    private E nextItem;
    
    private LinkedTransferQueue.Node lastRet;
    
    private LinkedTransferQueue.Node lastPred;
    
    private void advance(LinkedTransferQueue.Node param1Node) {
      LinkedTransferQueue.Node node1;
      if ((node1 = this.lastRet) != null && !node1.isMatched()) {
        this.lastPred = node1;
      } else {
        LinkedTransferQueue.Node node;
        if ((node = this.lastPred) == null || node.isMatched()) {
          this.lastPred = null;
        } else {
          LinkedTransferQueue.Node node3;
          LinkedTransferQueue.Node node4;
          while ((node3 = node.next) != null && node3 != node && node3.isMatched() && (node4 = node3.next) != null && node4 != node3)
            node.casNext(node3, node4); 
        } 
      } 
      this.lastRet = param1Node;
      LinkedTransferQueue.Node node2 = param1Node;
      while (true) {
        LinkedTransferQueue.Node node3 = (node2 == null) ? LinkedTransferQueue.this.head : node2.next;
        if (node3 == null)
          break; 
        if (node3 == node2) {
          node2 = null;
          continue;
        } 
        Object object = node3.item;
        if (node3.isData) {
          if (object != null && object != node3) {
            this.nextItem = LinkedTransferQueue.cast(object);
            this.nextNode = node3;
            return;
          } 
        } else if (object == null) {
          break;
        } 
        if (node2 == null) {
          node2 = node3;
          continue;
        } 
        LinkedTransferQueue.Node node4;
        if ((node4 = node3.next) == null)
          break; 
        if (node3 == node4) {
          node2 = null;
          continue;
        } 
        node2.casNext(node3, node4);
      } 
      this.nextNode = null;
      this.nextItem = null;
    }
    
    Itr() { advance(null); }
    
    public final boolean hasNext() { return (this.nextNode != null); }
    
    public final E next() {
      LinkedTransferQueue.Node node = this.nextNode;
      if (node == null)
        throw new NoSuchElementException(); 
      Object object = this.nextItem;
      advance(node);
      return (E)object;
    }
    
    public final void remove() {
      LinkedTransferQueue.Node node = this.lastRet;
      if (node == null)
        throw new IllegalStateException(); 
      this.lastRet = null;
      if (node.tryMatchData())
        LinkedTransferQueue.this.unsplice(this.lastPred, node); 
    }
  }
  
  static final class LTQSpliterator<E> extends Object implements Spliterator<E> {
    static final int MAX_BATCH = 33554432;
    
    final LinkedTransferQueue<E> queue;
    
    LinkedTransferQueue.Node current;
    
    int batch;
    
    boolean exhausted;
    
    LTQSpliterator(LinkedTransferQueue<E> param1LinkedTransferQueue) { this.queue = param1LinkedTransferQueue; }
    
    public Spliterator<E> trySplit() {
      LinkedTransferQueue linkedTransferQueue = this.queue;
      int i = this.batch;
      boolean bool = (i <= 0) ? 1 : ((i >= 33554432) ? 33554432 : (i + 1));
      LinkedTransferQueue.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = linkedTransferQueue.firstDataNode()) != null) && node.next != null) {
        Object[] arrayOfObject = new Object[bool];
        byte b = 0;
        do {
          Object object = node.item;
          arrayOfObject[b] = object;
          if (object != node && object != null)
            b++; 
          if (node != (node = node.next))
            continue; 
          node = linkedTransferQueue.firstDataNode();
        } while (node != null && b < bool && node.isData);
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
      LinkedTransferQueue linkedTransferQueue = this.queue;
      LinkedTransferQueue.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = linkedTransferQueue.firstDataNode()) != null)) {
        this.exhausted = true;
        do {
          Object object = node.item;
          if (object != null && object != node)
            param1Consumer.accept(object); 
          if (node != (node = node.next))
            continue; 
          node = linkedTransferQueue.firstDataNode();
        } while (node != null && node.isData);
      } 
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      LinkedTransferQueue linkedTransferQueue = this.queue;
      LinkedTransferQueue.Node node;
      if (!this.exhausted && ((node = this.current) != null || (node = linkedTransferQueue.firstDataNode()) != null)) {
        Object object;
        do {
          if ((object = node.item) == node)
            object = null; 
          if (node != (node = node.next))
            continue; 
          node = linkedTransferQueue.firstDataNode();
        } while (object == null && node != null && node.isData);
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
  
  static final class Node {
    final boolean isData;
    
    private static final long serialVersionUID = -3375979862319811754L;
    
    private static final Unsafe UNSAFE;
    
    private static final long itemOffset;
    
    private static final long nextOffset;
    
    private static final long waiterOffset;
    
    final boolean casNext(Node param1Node1, Node param1Node2) { return UNSAFE.compareAndSwapObject(this, nextOffset, param1Node1, param1Node2); }
    
    final boolean casItem(Object param1Object1, Object param1Object2) { return UNSAFE.compareAndSwapObject(this, itemOffset, param1Object1, param1Object2); }
    
    Node(Object param1Object, boolean param1Boolean) {
      UNSAFE.putObject(this, itemOffset, param1Object);
      this.isData = param1Boolean;
    }
    
    final void forgetNext() { UNSAFE.putObject(this, nextOffset, this); }
    
    final void forgetContents() {
      UNSAFE.putObject(this, itemOffset, this);
      UNSAFE.putObject(this, waiterOffset, null);
    }
    
    final boolean isMatched() {
      Object object = this.item;
      return (object == this || ((object == null)) == this.isData);
    }
    
    final boolean isUnmatchedRequest() { return (!this.isData && this.item == null); }
    
    final boolean cannotPrecede(boolean param1Boolean) {
      boolean bool = this.isData;
      Object object;
      return (bool != param1Boolean && (object = this.item) != this && ((object != null)) == bool);
    }
    
    final boolean tryMatchData() {
      Object object = this.item;
      if (object != null && object != this && casItem(object, null)) {
        LockSupport.unpark(this.waiter);
        return true;
      } 
      return false;
    }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = Node.class;
        itemOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("item"));
        nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
        waiterOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("waiter"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\LinkedTransferQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */