package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class SynchronousQueue<E> extends AbstractQueue<E> implements BlockingQueue<E>, Serializable {
  private static final long serialVersionUID = -3223113410248163686L;
  
  static final int NCPUS = Runtime.getRuntime().availableProcessors();
  
  static final int maxTimedSpins = (NCPUS < 2) ? 0 : 32;
  
  static final int maxUntimedSpins = maxTimedSpins * 16;
  
  static final long spinForTimeoutThreshold = 1000L;
  
  private ReentrantLock qlock;
  
  private WaitQueue waitingProducers;
  
  private WaitQueue waitingConsumers;
  
  public SynchronousQueue() { this(false); }
  
  public SynchronousQueue(boolean paramBoolean) { this.transferer = paramBoolean ? new TransferQueue() : new TransferStack(); }
  
  public void put(E paramE) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    if (this.transferer.transfer(paramE, false, 0L) == null) {
      Thread.interrupted();
      throw new InterruptedException();
    } 
  }
  
  public boolean offer(E paramE, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramE == null)
      throw new NullPointerException(); 
    if (this.transferer.transfer(paramE, true, paramTimeUnit.toNanos(paramLong)) != null)
      return true; 
    if (!Thread.interrupted())
      return false; 
    throw new InterruptedException();
  }
  
  public boolean offer(E paramE) {
    if (paramE == null)
      throw new NullPointerException(); 
    return (this.transferer.transfer(paramE, true, 0L) != null);
  }
  
  public E take() throws InterruptedException {
    Object object = this.transferer.transfer(null, false, 0L);
    if (object != null)
      return (E)object; 
    Thread.interrupted();
    throw new InterruptedException();
  }
  
  public E poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    Object object = this.transferer.transfer(null, true, paramTimeUnit.toNanos(paramLong));
    if (object != null || !Thread.interrupted())
      return (E)object; 
    throw new InterruptedException();
  }
  
  public E poll() throws InterruptedException { return (E)this.transferer.transfer(null, true, 0L); }
  
  public boolean isEmpty() { return true; }
  
  public int size() { return 0; }
  
  public int remainingCapacity() { return 0; }
  
  public void clear() {}
  
  public boolean contains(Object paramObject) { return false; }
  
  public boolean remove(Object paramObject) { return false; }
  
  public boolean containsAll(Collection<?> paramCollection) { return paramCollection.isEmpty(); }
  
  public boolean removeAll(Collection<?> paramCollection) { return false; }
  
  public boolean retainAll(Collection<?> paramCollection) { return false; }
  
  public E peek() throws InterruptedException { return null; }
  
  public Iterator<E> iterator() { return Collections.emptyIterator(); }
  
  public Spliterator<E> spliterator() { return Spliterators.emptySpliterator(); }
  
  public Object[] toArray() { return new Object[0]; }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    if (paramArrayOfT.length > 0)
      paramArrayOfT[0] = null; 
    return paramArrayOfT;
  }
  
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
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    boolean bool = this.transferer instanceof TransferQueue;
    if (bool) {
      this.qlock = new ReentrantLock(true);
      this.waitingProducers = new FifoWaitQueue();
      this.waitingConsumers = new FifoWaitQueue();
    } else {
      this.qlock = new ReentrantLock();
      this.waitingProducers = new LifoWaitQueue();
      this.waitingConsumers = new LifoWaitQueue();
    } 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.waitingProducers instanceof FifoWaitQueue) {
      this.transferer = new TransferQueue();
    } else {
      this.transferer = new TransferStack();
    } 
  }
  
  static long objectFieldOffset(Unsafe paramUnsafe, String paramString, Class<?> paramClass) {
    try {
      return paramUnsafe.objectFieldOffset(paramClass.getDeclaredField(paramString));
    } catch (NoSuchFieldException noSuchFieldException) {
      NoSuchFieldError noSuchFieldError = new NoSuchFieldError(paramString);
      noSuchFieldError.initCause(noSuchFieldException);
      throw noSuchFieldError;
    } 
  }
  
  static class FifoWaitQueue extends WaitQueue {
    private static final long serialVersionUID = -3623113410248163686L;
  }
  
  static class LifoWaitQueue extends WaitQueue {
    private static final long serialVersionUID = -3633113410248163686L;
  }
  
  static final class TransferQueue<E> extends Transferer<E> {
    private static final Unsafe UNSAFE;
    
    private static final long headOffset;
    
    private static final long tailOffset;
    
    private static final long cleanMeOffset;
    
    TransferQueue() {
      QNode qNode = new QNode(null, false);
      this.head = qNode;
      this.tail = qNode;
    }
    
    void advanceHead(QNode param1QNode1, QNode param1QNode2) {
      if (param1QNode1 == this.head && UNSAFE.compareAndSwapObject(this, headOffset, param1QNode1, param1QNode2))
        param1QNode1.next = param1QNode1; 
    }
    
    void advanceTail(QNode param1QNode1, QNode param1QNode2) {
      if (this.tail == param1QNode1)
        UNSAFE.compareAndSwapObject(this, tailOffset, param1QNode1, param1QNode2); 
    }
    
    boolean casCleanMe(QNode param1QNode1, QNode param1QNode2) { return (this.cleanMe == param1QNode1 && UNSAFE.compareAndSwapObject(this, cleanMeOffset, param1QNode1, param1QNode2)); }
    
    E transfer(E param1E, boolean param1Boolean, long param1Long) {
      Object object;
      QNode qNode3;
      QNode qNode2;
      QNode qNode1 = null;
      boolean bool = (param1E != null);
      while (true) {
        QNode qNode = this.tail;
        qNode2 = this.head;
        if (qNode == null || qNode2 == null)
          continue; 
        if (qNode2 == qNode || qNode.isData == bool) {
          QNode qNode4 = qNode.next;
          if (qNode != this.tail)
            continue; 
          if (qNode4 != null) {
            advanceTail(qNode, qNode4);
            continue;
          } 
          if (param1Boolean && param1Long <= 0L)
            return null; 
          if (qNode1 == null)
            qNode1 = new QNode(param1E, bool); 
          if (!qNode.casNext(null, qNode1))
            continue; 
          advanceTail(qNode, qNode1);
          Object object1 = awaitFulfill(qNode1, param1E, param1Boolean, param1Long);
          if (object1 == qNode1) {
            clean(qNode, qNode1);
            return null;
          } 
          if (!qNode1.isOffList()) {
            advanceHead(qNode, qNode1);
            if (object1 != null)
              qNode1.item = qNode1; 
            qNode1.waiter = null;
          } 
          return (E)((object1 != null) ? object1 : param1E);
        } 
        qNode3 = qNode2.next;
        if (qNode != this.tail || qNode3 == null || qNode2 != this.head)
          continue; 
        object = qNode3.item;
        if (bool == ((object != null)) || object == qNode3 || !qNode3.casItem(object, param1E)) {
          advanceHead(qNode2, qNode3);
          continue;
        } 
        break;
      } 
      advanceHead(qNode2, qNode3);
      LockSupport.unpark(qNode3.waiter);
      return (E)((object != null) ? object : param1E);
    }
    
    Object awaitFulfill(QNode param1QNode, E param1E, boolean param1Boolean, long param1Long) {
      long l = param1Boolean ? (System.nanoTime() + param1Long) : 0L;
      Thread thread = Thread.currentThread();
      int i = (this.head.next == param1QNode) ? (param1Boolean ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins) : 0;
      while (true) {
        if (thread.isInterrupted())
          param1QNode.tryCancel(param1E); 
        Object object = param1QNode.item;
        if (object != param1E)
          return object; 
        if (param1Boolean) {
          param1Long = l - System.nanoTime();
          if (param1Long <= 0L) {
            param1QNode.tryCancel(param1E);
            continue;
          } 
        } 
        if (i > 0) {
          i--;
          continue;
        } 
        if (param1QNode.waiter == null) {
          param1QNode.waiter = thread;
          continue;
        } 
        if (!param1Boolean) {
          LockSupport.park(this);
          continue;
        } 
        if (param1Long > 1000L)
          LockSupport.parkNanos(this, param1Long); 
      } 
    }
    
    void clean(QNode param1QNode1, QNode param1QNode2) {
      param1QNode2.waiter = null;
      while (param1QNode1.next == param1QNode2) {
        QNode qNode1 = this.head;
        QNode qNode2 = qNode1.next;
        if (qNode2 != null && qNode2.isCancelled()) {
          advanceHead(qNode1, qNode2);
          continue;
        } 
        QNode qNode3 = this.tail;
        if (qNode3 == qNode1)
          return; 
        QNode qNode4 = qNode3.next;
        if (qNode3 != this.tail)
          continue; 
        if (qNode4 != null) {
          advanceTail(qNode3, qNode4);
          continue;
        } 
        if (param1QNode2 != qNode3) {
          QNode qNode = param1QNode2.next;
          if (qNode == param1QNode2 || param1QNode1.casNext(param1QNode2, qNode))
            return; 
        } 
        QNode qNode5 = this.cleanMe;
        if (qNode5 != null) {
          QNode qNode6 = qNode5.next;
          QNode qNode7;
          if (qNode6 == null || qNode6 == qNode5 || !qNode6.isCancelled() || (qNode6 != qNode3 && (qNode7 = qNode6.next) != null && qNode7 != qNode6 && qNode5.casNext(qNode6, qNode7)))
            casCleanMe(qNode5, null); 
          if (qNode5 == param1QNode1)
            return; 
          continue;
        } 
        if (casCleanMe(null, param1QNode1))
          return; 
      } 
    }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = TransferQueue.class;
        headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
        tailOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tail"));
        cleanMeOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("cleanMe"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
    
    static final class QNode {
      final boolean isData;
      
      private static final Unsafe UNSAFE;
      
      private static final long itemOffset;
      
      private static final long nextOffset;
      
      QNode(Object param2Object, boolean param2Boolean) {
        this.item = param2Object;
        this.isData = param2Boolean;
      }
      
      boolean casNext(QNode param2QNode1, QNode param2QNode2) { return (this.next == param2QNode1 && UNSAFE.compareAndSwapObject(this, nextOffset, param2QNode1, param2QNode2)); }
      
      boolean casItem(Object param2Object1, Object param2Object2) { return (this.item == param2Object1 && UNSAFE.compareAndSwapObject(this, itemOffset, param2Object1, param2Object2)); }
      
      void tryCancel(Object param2Object) { UNSAFE.compareAndSwapObject(this, itemOffset, param2Object, this); }
      
      boolean isCancelled() { return (this.item == this); }
      
      boolean isOffList() { return (this.next == this); }
      
      static  {
        try {
          UNSAFE = Unsafe.getUnsafe();
          Class clazz = QNode.class;
          itemOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("item"));
          nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
        } catch (Exception exception) {
          throw new Error(exception);
        } 
      }
    }
  }
  
  static final class TransferStack<E> extends Transferer<E> {
    static final int REQUEST = 0;
    
    static final int DATA = 1;
    
    static final int FULFILLING = 2;
    
    private static final Unsafe UNSAFE;
    
    private static final long headOffset;
    
    static boolean isFulfilling(int param1Int) { return ((param1Int & 0x2) != 0); }
    
    boolean casHead(SNode param1SNode1, SNode param1SNode2) { return (param1SNode1 == this.head && UNSAFE.compareAndSwapObject(this, headOffset, param1SNode1, param1SNode2)); }
    
    static SNode snode(SNode param1SNode1, Object param1Object, SNode param1SNode2, int param1Int) {
      if (param1SNode1 == null)
        param1SNode1 = new SNode(param1Object); 
      param1SNode1.mode = param1Int;
      param1SNode1.next = param1SNode2;
      return param1SNode1;
    }
    
    E transfer(E param1E, boolean param1Boolean, long param1Long) {
      SNode sNode = null;
      byte b = (param1E == null) ? 0 : 1;
      label60: while (true) {
        SNode sNode1 = this.head;
        if (sNode1 == null || sNode1.mode == b) {
          if (param1Boolean && param1Long <= 0L) {
            if (sNode1 != null && sNode1.isCancelled()) {
              casHead(sNode1, sNode1.next);
              continue;
            } 
            return null;
          } 
          if (casHead(sNode1, sNode = snode(sNode, param1E, sNode1, b))) {
            SNode sNode4 = awaitFulfill(sNode, param1Boolean, param1Long);
            if (sNode4 == sNode) {
              clean(sNode);
              return null;
            } 
            if ((sNode1 = this.head) != null && sNode1.next == sNode)
              casHead(sNode1, sNode.next); 
            return (E)((b == 0) ? sNode4.item : sNode.item);
          } 
          continue;
        } 
        if (!isFulfilling(sNode1.mode)) {
          if (sNode1.isCancelled()) {
            casHead(sNode1, sNode1.next);
            continue;
          } 
          if (casHead(sNode1, sNode = snode(sNode, param1E, sNode1, 0x2 | b))) {
            while (true) {
              SNode sNode4 = sNode.next;
              if (sNode4 == null) {
                casHead(sNode, null);
                sNode = null;
                continue label60;
              } 
              SNode sNode5 = sNode4.next;
              if (sNode4.tryMatch(sNode)) {
                casHead(sNode, sNode5);
                return (E)((b == 0) ? sNode4.item : sNode.item);
              } 
              sNode.casNext(sNode4, sNode5);
            } 
            break;
          } 
          continue;
        } 
        SNode sNode2 = sNode1.next;
        if (sNode2 == null) {
          casHead(sNode1, null);
          continue;
        } 
        SNode sNode3 = sNode2.next;
        if (sNode2.tryMatch(sNode1)) {
          casHead(sNode1, sNode3);
          continue;
        } 
        sNode1.casNext(sNode2, sNode3);
      } 
    }
    
    SNode awaitFulfill(SNode param1SNode, boolean param1Boolean, long param1Long) {
      long l = param1Boolean ? (System.nanoTime() + param1Long) : 0L;
      Thread thread = Thread.currentThread();
      int i = shouldSpin(param1SNode) ? (param1Boolean ? SynchronousQueue.maxTimedSpins : SynchronousQueue.maxUntimedSpins) : 0;
      while (true) {
        if (thread.isInterrupted())
          param1SNode.tryCancel(); 
        SNode sNode = param1SNode.match;
        if (sNode != null)
          return sNode; 
        if (param1Boolean) {
          param1Long = l - System.nanoTime();
          if (param1Long <= 0L) {
            param1SNode.tryCancel();
            continue;
          } 
        } 
        if (i > 0) {
          i = shouldSpin(param1SNode) ? (i - 1) : 0;
          continue;
        } 
        if (param1SNode.waiter == null) {
          param1SNode.waiter = thread;
          continue;
        } 
        if (!param1Boolean) {
          LockSupport.park(this);
          continue;
        } 
        if (param1Long > 1000L)
          LockSupport.parkNanos(this, param1Long); 
      } 
    }
    
    boolean shouldSpin(SNode param1SNode) {
      SNode sNode = this.head;
      return (sNode == param1SNode || sNode == null || isFulfilling(sNode.mode));
    }
    
    void clean(SNode param1SNode) {
      param1SNode.item = null;
      param1SNode.waiter = null;
      SNode sNode1 = param1SNode.next;
      if (sNode1 != null && sNode1.isCancelled())
        sNode1 = sNode1.next; 
      SNode sNode2;
      while ((sNode2 = this.head) != null && sNode2 != sNode1 && sNode2.isCancelled())
        casHead(sNode2, sNode2.next); 
      while (sNode2 != null && sNode2 != sNode1) {
        SNode sNode = sNode2.next;
        if (sNode != null && sNode.isCancelled()) {
          sNode2.casNext(sNode, sNode.next);
          continue;
        } 
        sNode2 = sNode;
      } 
    }
    
    static  {
      try {
        UNSAFE = Unsafe.getUnsafe();
        Class clazz = TransferStack.class;
        headOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("head"));
      } catch (Exception exception) {
        throw new Error(exception);
      } 
    }
    
    static final class SNode {
      Object item;
      
      int mode;
      
      private static final Unsafe UNSAFE;
      
      private static final long matchOffset;
      
      private static final long nextOffset;
      
      SNode(Object param2Object) { this.item = param2Object; }
      
      boolean casNext(SNode param2SNode1, SNode param2SNode2) { return (param2SNode1 == this.next && UNSAFE.compareAndSwapObject(this, nextOffset, param2SNode1, param2SNode2)); }
      
      boolean tryMatch(SNode param2SNode) {
        if (this.match == null && UNSAFE.compareAndSwapObject(this, matchOffset, null, param2SNode)) {
          Thread thread = this.waiter;
          if (thread != null) {
            this.waiter = null;
            LockSupport.unpark(thread);
          } 
          return true;
        } 
        return (this.match == param2SNode);
      }
      
      void tryCancel() { UNSAFE.compareAndSwapObject(this, matchOffset, null, this); }
      
      boolean isCancelled() { return (this.match == this); }
      
      static  {
        try {
          UNSAFE = Unsafe.getUnsafe();
          Class clazz = SNode.class;
          matchOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("match"));
          nextOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("next"));
        } catch (Exception exception) {
          throw new Error(exception);
        } 
      }
    }
  }
  
  static abstract class Transferer<E> extends Object {
    abstract E transfer(E param1E, boolean param1Boolean, long param1Long);
  }
  
  static class WaitQueue implements Serializable {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\SynchronousQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */