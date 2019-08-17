package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public abstract class AbstractQueuedLongSynchronizer extends AbstractOwnableSynchronizer implements Serializable {
  private static final long serialVersionUID = 7373984972572414692L;
  
  static final long spinForTimeoutThreshold = 1000L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long stateOffset;
  
  private static final long headOffset;
  
  private static final long tailOffset;
  
  private static final long waitStatusOffset;
  
  private static final long nextOffset;
  
  protected final long getState() { return this.state; }
  
  protected final void setState(long paramLong) { this.state = paramLong; }
  
  protected final boolean compareAndSetState(long paramLong1, long paramLong2) { return unsafe.compareAndSwapLong(this, stateOffset, paramLong1, paramLong2); }
  
  private Node enq(Node paramNode) {
    Node node;
    while (true) {
      node = this.tail;
      if (node == null) {
        if (compareAndSetHead(new Node()))
          this.tail = this.head; 
        continue;
      } 
      paramNode.prev = node;
      if (compareAndSetTail(node, paramNode))
        break; 
    } 
    node.next = paramNode;
    return node;
  }
  
  private Node addWaiter(Node paramNode) {
    Node node1 = new Node(Thread.currentThread(), paramNode);
    Node node2 = this.tail;
    if (node2 != null) {
      node1.prev = node2;
      if (compareAndSetTail(node2, node1)) {
        node2.next = node1;
        return node1;
      } 
    } 
    enq(node1);
    return node1;
  }
  
  private void setHead(Node paramNode) {
    this.head = paramNode;
    paramNode.thread = null;
    paramNode.prev = null;
  }
  
  private void unparkSuccessor(Node paramNode) {
    int i = paramNode.waitStatus;
    if (i < 0)
      compareAndSetWaitStatus(paramNode, i, 0); 
    Node node = paramNode.next;
    if (node == null || node.waitStatus > 0) {
      node = null;
      for (Node node1 = this.tail; node1 != null && node1 != paramNode; node1 = node1.prev) {
        if (node1.waitStatus <= 0)
          node = node1; 
      } 
    } 
    if (node != null)
      LockSupport.unpark(node.thread); 
  }
  
  private void doReleaseShared() {
    while (true) {
      Node node = this.head;
      if (node != null && node != this.tail) {
        int i = node.waitStatus;
        if (i == -1) {
          if (!compareAndSetWaitStatus(node, -1, 0))
            continue; 
          unparkSuccessor(node);
        } else if (i == 0 && !compareAndSetWaitStatus(node, 0, -3)) {
          continue;
        } 
      } 
      if (node == this.head)
        break; 
    } 
  }
  
  private void setHeadAndPropagate(Node paramNode, long paramLong) {
    Node node = this.head;
    setHead(paramNode);
    if (paramLong > 0L || node == null || node.waitStatus < 0 || (node = this.head) == null || node.waitStatus < 0) {
      Node node1 = paramNode.next;
      if (node1 == null || node1.isShared())
        doReleaseShared(); 
    } 
  }
  
  private void cancelAcquire(Node paramNode) {
    if (paramNode == null)
      return; 
    paramNode.thread = null;
    Node node1 = paramNode.prev;
    while (node1.waitStatus > 0)
      paramNode.prev = node1 = node1.prev; 
    Node node2 = node1.next;
    paramNode.waitStatus = 1;
    if (paramNode == this.tail && compareAndSetTail(paramNode, node1)) {
      compareAndSetNext(node1, node2, null);
    } else {
      int i;
      if (node1 != this.head && ((i = node1.waitStatus) == -1 || (i <= 0 && compareAndSetWaitStatus(node1, i, -1))) && node1.thread != null) {
        Node node = paramNode.next;
        if (node != null && node.waitStatus <= 0)
          compareAndSetNext(node1, node2, node); 
      } else {
        unparkSuccessor(paramNode);
      } 
      paramNode.next = paramNode;
    } 
  }
  
  private static boolean shouldParkAfterFailedAcquire(Node paramNode1, Node paramNode2) {
    int i = paramNode1.waitStatus;
    if (i == -1)
      return true; 
    if (i > 0) {
      do {
        paramNode2.prev = paramNode1 = paramNode1.prev;
      } while (paramNode1.waitStatus > 0);
      paramNode1.next = paramNode2;
    } else {
      compareAndSetWaitStatus(paramNode1, i, -1);
    } 
    return false;
  }
  
  static void selfInterrupt() { Thread.currentThread().interrupt(); }
  
  private final boolean parkAndCheckInterrupt() {
    LockSupport.park(this);
    return Thread.interrupted();
  }
  
  final boolean acquireQueued(Node paramNode, long paramLong) {
    bool = true;
    try {
      boolean bool1 = false;
      while (true) {
        Node node = paramNode.predecessor();
        if (node == this.head && tryAcquire(paramLong)) {
          setHead(paramNode);
          node.next = null;
          bool = false;
          return bool1;
        } 
        if (shouldParkAfterFailedAcquire(node, paramNode) && parkAndCheckInterrupt())
          bool1 = true; 
      } 
    } finally {
      if (bool)
        cancelAcquire(paramNode); 
    } 
  }
  
  private void doAcquireInterruptibly(long paramLong) {
    node = addWaiter(Node.EXCLUSIVE);
    bool = true;
    try {
      Node node1;
      do {
        node1 = node.predecessor();
        if (node1 == this.head && tryAcquire(paramLong)) {
          setHead(node);
          node1.next = null;
          bool = false;
          return;
        } 
      } while (!shouldParkAfterFailedAcquire(node1, node) || !parkAndCheckInterrupt());
      throw new InterruptedException();
    } finally {
      if (bool)
        cancelAcquire(node); 
    } 
  }
  
  private boolean doAcquireNanos(long paramLong1, long paramLong2) {
    if (paramLong2 <= 0L)
      return false; 
    long l = System.nanoTime() + paramLong2;
    node = addWaiter(Node.EXCLUSIVE);
    bool = true;
    try {
      do {
        Node node1 = node.predecessor();
        if (node1 == this.head && tryAcquire(paramLong1)) {
          setHead(node);
          node1.next = null;
          bool = false;
          return true;
        } 
        paramLong2 = l - System.nanoTime();
        if (paramLong2 <= 0L)
          return false; 
        if (!shouldParkAfterFailedAcquire(node1, node) || paramLong2 <= 1000L)
          continue; 
        LockSupport.parkNanos(this, paramLong2);
      } while (!Thread.interrupted());
      throw new InterruptedException();
    } finally {
      if (bool)
        cancelAcquire(node); 
    } 
  }
  
  private void doAcquireShared(long paramLong) {
    node = addWaiter(Node.SHARED);
    bool = true;
    try {
      boolean bool1 = false;
      while (true) {
        Node node1 = node.predecessor();
        if (node1 == this.head) {
          long l = tryAcquireShared(paramLong);
          if (l >= 0L) {
            setHeadAndPropagate(node, l);
            node1.next = null;
            if (bool1)
              selfInterrupt(); 
            bool = false;
            return;
          } 
        } 
        if (shouldParkAfterFailedAcquire(node1, node) && parkAndCheckInterrupt())
          bool1 = true; 
      } 
    } finally {
      if (bool)
        cancelAcquire(node); 
    } 
  }
  
  private void doAcquireSharedInterruptibly(long paramLong) {
    node = addWaiter(Node.SHARED);
    bool = true;
    try {
      Node node1;
      do {
        node1 = node.predecessor();
        if (node1 != this.head)
          continue; 
        long l = tryAcquireShared(paramLong);
        if (l >= 0L) {
          setHeadAndPropagate(node, l);
          node1.next = null;
          bool = false;
          return;
        } 
      } while (!shouldParkAfterFailedAcquire(node1, node) || !parkAndCheckInterrupt());
      throw new InterruptedException();
    } finally {
      if (bool)
        cancelAcquire(node); 
    } 
  }
  
  private boolean doAcquireSharedNanos(long paramLong1, long paramLong2) {
    if (paramLong2 <= 0L)
      return false; 
    long l = System.nanoTime() + paramLong2;
    node = addWaiter(Node.SHARED);
    bool = true;
    try {
      do {
        Node node1 = node.predecessor();
        if (node1 == this.head) {
          long l1 = tryAcquireShared(paramLong1);
          if (l1 >= 0L) {
            setHeadAndPropagate(node, l1);
            node1.next = null;
            bool = false;
            return true;
          } 
        } 
        paramLong2 = l - System.nanoTime();
        if (paramLong2 <= 0L)
          return false; 
        if (!shouldParkAfterFailedAcquire(node1, node) || paramLong2 <= 1000L)
          continue; 
        LockSupport.parkNanos(this, paramLong2);
      } while (!Thread.interrupted());
      throw new InterruptedException();
    } finally {
      if (bool)
        cancelAcquire(node); 
    } 
  }
  
  protected boolean tryAcquire(long paramLong) { throw new UnsupportedOperationException(); }
  
  protected boolean tryRelease(long paramLong) { throw new UnsupportedOperationException(); }
  
  protected long tryAcquireShared(long paramLong) { throw new UnsupportedOperationException(); }
  
  protected boolean tryReleaseShared(long paramLong) { throw new UnsupportedOperationException(); }
  
  protected boolean isHeldExclusively() { throw new UnsupportedOperationException(); }
  
  public final void acquire(long paramLong) {
    if (!tryAcquire(paramLong) && acquireQueued(addWaiter(Node.EXCLUSIVE), paramLong))
      selfInterrupt(); 
  }
  
  public final void acquireInterruptibly(long paramLong) {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    if (!tryAcquire(paramLong))
      doAcquireInterruptibly(paramLong); 
  }
  
  public final boolean tryAcquireNanos(long paramLong1, long paramLong2) {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    return (tryAcquire(paramLong1) || doAcquireNanos(paramLong1, paramLong2));
  }
  
  public final boolean release(long paramLong) {
    if (tryRelease(paramLong)) {
      Node node = this.head;
      if (node != null && node.waitStatus != 0)
        unparkSuccessor(node); 
      return true;
    } 
    return false;
  }
  
  public final void acquireShared(long paramLong) {
    if (tryAcquireShared(paramLong) < 0L)
      doAcquireShared(paramLong); 
  }
  
  public final void acquireSharedInterruptibly(long paramLong) {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    if (tryAcquireShared(paramLong) < 0L)
      doAcquireSharedInterruptibly(paramLong); 
  }
  
  public final boolean tryAcquireSharedNanos(long paramLong1, long paramLong2) {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    return (tryAcquireShared(paramLong1) >= 0L || doAcquireSharedNanos(paramLong1, paramLong2));
  }
  
  public final boolean releaseShared(long paramLong) {
    if (tryReleaseShared(paramLong)) {
      doReleaseShared();
      return true;
    } 
    return false;
  }
  
  public final boolean hasQueuedThreads() { return (this.head != this.tail); }
  
  public final boolean hasContended() { return (this.head != null); }
  
  public final Thread getFirstQueuedThread() { return (this.head == this.tail) ? null : fullGetFirstQueuedThread(); }
  
  private Thread fullGetFirstQueuedThread() {
    Node node1;
    Node node2;
    Thread thread1;
    if (((node1 = this.head) != null && (node2 = node1.next) != null && node2.prev == this.head && (thread1 = node2.thread) != null) || ((node1 = this.head) != null && (node2 = node1.next) != null && node2.prev == this.head && (thread1 = node2.thread) != null))
      return thread1; 
    Node node3 = this.tail;
    Thread thread2 = null;
    while (node3 != null && node3 != this.head) {
      Thread thread = node3.thread;
      if (thread != null)
        thread2 = thread; 
      node3 = node3.prev;
    } 
    return thread2;
  }
  
  public final boolean isQueued(Thread paramThread) {
    if (paramThread == null)
      throw new NullPointerException(); 
    for (Node node = this.tail; node != null; node = node.prev) {
      if (node.thread == paramThread)
        return true; 
    } 
    return false;
  }
  
  final boolean apparentlyFirstQueuedIsExclusive() {
    Node node1;
    Node node2;
    return ((node1 = this.head) != null && (node2 = node1.next) != null && !node2.isShared() && node2.thread != null);
  }
  
  public final boolean hasQueuedPredecessors() {
    Node node1 = this.tail;
    Node node2 = this.head;
    Node node3;
    return (node2 != node1 && ((node3 = node2.next) == null || node3.thread != Thread.currentThread()));
  }
  
  public final int getQueueLength() {
    byte b = 0;
    for (Node node = this.tail; node != null; node = node.prev) {
      if (node.thread != null)
        b++; 
    } 
    return b;
  }
  
  public final Collection<Thread> getQueuedThreads() {
    ArrayList arrayList = new ArrayList();
    for (Node node = this.tail; node != null; node = node.prev) {
      Thread thread = node.thread;
      if (thread != null)
        arrayList.add(thread); 
    } 
    return arrayList;
  }
  
  public final Collection<Thread> getExclusiveQueuedThreads() {
    ArrayList arrayList = new ArrayList();
    for (Node node = this.tail; node != null; node = node.prev) {
      if (!node.isShared()) {
        Thread thread = node.thread;
        if (thread != null)
          arrayList.add(thread); 
      } 
    } 
    return arrayList;
  }
  
  public final Collection<Thread> getSharedQueuedThreads() {
    ArrayList arrayList = new ArrayList();
    for (Node node = this.tail; node != null; node = node.prev) {
      if (node.isShared()) {
        Thread thread = node.thread;
        if (thread != null)
          arrayList.add(thread); 
      } 
    } 
    return arrayList;
  }
  
  public String toString() {
    long l = getState();
    String str = hasQueuedThreads() ? "non" : "";
    return super.toString() + "[State = " + l + ", " + str + "empty queue]";
  }
  
  final boolean isOnSyncQueue(Node paramNode) { return (paramNode.waitStatus == -2 || paramNode.prev == null) ? false : ((paramNode.next != null) ? true : findNodeFromTail(paramNode)); }
  
  private boolean findNodeFromTail(Node paramNode) {
    for (Node node = this.tail;; node = node.prev) {
      if (node == paramNode)
        return true; 
      if (node == null)
        return false; 
    } 
  }
  
  final boolean transferForSignal(Node paramNode) {
    if (!compareAndSetWaitStatus(paramNode, -2, 0))
      return false; 
    Node node = enq(paramNode);
    int i = node.waitStatus;
    if (i > 0 || !compareAndSetWaitStatus(node, i, -1))
      LockSupport.unpark(paramNode.thread); 
    return true;
  }
  
  final boolean transferAfterCancelledWait(Node paramNode) {
    if (compareAndSetWaitStatus(paramNode, -2, 0)) {
      enq(paramNode);
      return true;
    } 
    while (!isOnSyncQueue(paramNode))
      Thread.yield(); 
    return false;
  }
  
  final long fullyRelease(Node paramNode) {
    bool = true;
    try {
      long l = getState();
      if (release(l)) {
        bool = false;
        return l;
      } 
      throw new IllegalMonitorStateException();
    } finally {
      if (bool)
        paramNode.waitStatus = 1; 
    } 
  }
  
  public final boolean owns(ConditionObject paramConditionObject) { return paramConditionObject.isOwnedBy(this); }
  
  public final boolean hasWaiters(ConditionObject paramConditionObject) {
    if (!owns(paramConditionObject))
      throw new IllegalArgumentException("Not owner"); 
    return paramConditionObject.hasWaiters();
  }
  
  public final int getWaitQueueLength(ConditionObject paramConditionObject) {
    if (!owns(paramConditionObject))
      throw new IllegalArgumentException("Not owner"); 
    return paramConditionObject.getWaitQueueLength();
  }
  
  public final Collection<Thread> getWaitingThreads(ConditionObject paramConditionObject) {
    if (!owns(paramConditionObject))
      throw new IllegalArgumentException("Not owner"); 
    return paramConditionObject.getWaitingThreads();
  }
  
  private final boolean compareAndSetHead(Node paramNode) { return unsafe.compareAndSwapObject(this, headOffset, null, paramNode); }
  
  private final boolean compareAndSetTail(Node paramNode1, Node paramNode2) { return unsafe.compareAndSwapObject(this, tailOffset, paramNode1, paramNode2); }
  
  private static final boolean compareAndSetWaitStatus(Node paramNode, int paramInt1, int paramInt2) { return unsafe.compareAndSwapInt(paramNode, waitStatusOffset, paramInt1, paramInt2); }
  
  private static final boolean compareAndSetNext(Node paramNode1, Node paramNode2, Node paramNode3) { return unsafe.compareAndSwapObject(paramNode1, nextOffset, paramNode2, paramNode3); }
  
  static  {
    try {
      stateOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("state"));
      headOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("head"));
      tailOffset = unsafe.objectFieldOffset(AbstractQueuedLongSynchronizer.class.getDeclaredField("tail"));
      waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
      nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  public class ConditionObject implements Condition, Serializable {
    private static final long serialVersionUID = 1173984872572414699L;
    
    private AbstractQueuedLongSynchronizer.Node firstWaiter;
    
    private AbstractQueuedLongSynchronizer.Node lastWaiter;
    
    private static final int REINTERRUPT = 1;
    
    private static final int THROW_IE = -1;
    
    private AbstractQueuedLongSynchronizer.Node addConditionWaiter() {
      AbstractQueuedLongSynchronizer.Node node1 = this.lastWaiter;
      if (node1 != null && node1.waitStatus != -2) {
        unlinkCancelledWaiters();
        node1 = this.lastWaiter;
      } 
      AbstractQueuedLongSynchronizer.Node node2 = new AbstractQueuedLongSynchronizer.Node(Thread.currentThread(), -2);
      if (node1 == null) {
        this.firstWaiter = node2;
      } else {
        node1.nextWaiter = node2;
      } 
      this.lastWaiter = node2;
      return node2;
    }
    
    private void doSignal(AbstractQueuedLongSynchronizer.Node param1Node) {
      do {
        if ((this.firstWaiter = param1Node.nextWaiter) == null)
          this.lastWaiter = null; 
        param1Node.nextWaiter = null;
      } while (!AbstractQueuedLongSynchronizer.this.transferForSignal(param1Node) && (param1Node = this.firstWaiter) != null);
    }
    
    private void doSignalAll(AbstractQueuedLongSynchronizer.Node param1Node) {
      this.lastWaiter = this.firstWaiter = null;
      do {
        AbstractQueuedLongSynchronizer.Node node = param1Node.nextWaiter;
        param1Node.nextWaiter = null;
        AbstractQueuedLongSynchronizer.this.transferForSignal(param1Node);
        param1Node = node;
      } while (param1Node != null);
    }
    
    private void unlinkCancelledWaiters() {
      AbstractQueuedLongSynchronizer.Node node1 = this.firstWaiter;
      AbstractQueuedLongSynchronizer.Node node2 = null;
      while (node1 != null) {
        AbstractQueuedLongSynchronizer.Node node = node1.nextWaiter;
        if (node1.waitStatus != -2) {
          node1.nextWaiter = null;
          if (node2 == null) {
            this.firstWaiter = node;
          } else {
            node2.nextWaiter = node;
          } 
          if (node == null)
            this.lastWaiter = node2; 
        } else {
          node2 = node1;
        } 
        node1 = node;
      } 
    }
    
    public final void signal() {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      AbstractQueuedLongSynchronizer.Node node = this.firstWaiter;
      if (node != null)
        doSignal(node); 
    }
    
    public final void signalAll() {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      AbstractQueuedLongSynchronizer.Node node = this.firstWaiter;
      if (node != null)
        doSignalAll(node); 
    }
    
    public final void awaitUninterruptibly() {
      AbstractQueuedLongSynchronizer.Node node = addConditionWaiter();
      long l = AbstractQueuedLongSynchronizer.this.fullyRelease(node);
      boolean bool = false;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(node)) {
        LockSupport.park(this);
        if (Thread.interrupted())
          bool = true; 
      } 
      if (AbstractQueuedLongSynchronizer.this.acquireQueued(node, l) || bool)
        AbstractQueuedLongSynchronizer.selfInterrupt(); 
    }
    
    private int checkInterruptWhileWaiting(AbstractQueuedLongSynchronizer.Node param1Node) { return Thread.interrupted() ? (AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(param1Node) ? -1 : 1) : 0; }
    
    private void reportInterruptAfterWait(int param1Int) throws InterruptedException {
      if (param1Int == -1)
        throw new InterruptedException(); 
      if (param1Int == 1)
        AbstractQueuedLongSynchronizer.selfInterrupt(); 
    }
    
    public final void await() {
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedLongSynchronizer.Node node = addConditionWaiter();
      long l = AbstractQueuedLongSynchronizer.this.fullyRelease(node);
      int i = 0;
      do {
        LockSupport.park(this);
      } while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(node) && (i = checkInterruptWhileWaiting(node)) == 0);
      if (AbstractQueuedLongSynchronizer.this.acquireQueued(node, l) && i != -1)
        i = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (i != 0)
        reportInterruptAfterWait(i); 
    }
    
    public final long awaitNanos(long param1Long) {
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedLongSynchronizer.Node node = addConditionWaiter();
      long l1 = AbstractQueuedLongSynchronizer.this.fullyRelease(node);
      long l2 = System.nanoTime() + param1Long;
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(node)) {
        if (param1Long <= 0L) {
          AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(node);
          break;
        } 
        if (param1Long >= 1000L)
          LockSupport.parkNanos(this, param1Long); 
        if ((i = checkInterruptWhileWaiting(node)) != 0)
          break; 
        param1Long = l2 - System.nanoTime();
      } 
      if (AbstractQueuedLongSynchronizer.this.acquireQueued(node, l1) && i != -1)
        i = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (i != 0)
        reportInterruptAfterWait(i); 
      return l2 - System.nanoTime();
    }
    
    public final boolean awaitUntil(Date param1Date) throws InterruptedException {
      long l1 = param1Date.getTime();
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedLongSynchronizer.Node node = addConditionWaiter();
      long l2 = AbstractQueuedLongSynchronizer.this.fullyRelease(node);
      boolean bool = false;
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(node)) {
        if (System.currentTimeMillis() > l1) {
          bool = AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(node);
          break;
        } 
        LockSupport.parkUntil(this, l1);
        if ((i = checkInterruptWhileWaiting(node)) != 0)
          break; 
      } 
      if (AbstractQueuedLongSynchronizer.this.acquireQueued(node, l2) && i != -1)
        i = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (i != 0)
        reportInterruptAfterWait(i); 
      return !bool;
    }
    
    public final boolean await(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException {
      long l1 = param1TimeUnit.toNanos(param1Long);
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedLongSynchronizer.Node node = addConditionWaiter();
      long l2 = AbstractQueuedLongSynchronizer.this.fullyRelease(node);
      long l3 = System.nanoTime() + l1;
      boolean bool = false;
      int i = 0;
      while (!AbstractQueuedLongSynchronizer.this.isOnSyncQueue(node)) {
        if (l1 <= 0L) {
          bool = AbstractQueuedLongSynchronizer.this.transferAfterCancelledWait(node);
          break;
        } 
        if (l1 >= 1000L)
          LockSupport.parkNanos(this, l1); 
        if ((i = checkInterruptWhileWaiting(node)) != 0)
          break; 
        l1 = l3 - System.nanoTime();
      } 
      if (AbstractQueuedLongSynchronizer.this.acquireQueued(node, l2) && i != -1)
        i = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (i != 0)
        reportInterruptAfterWait(i); 
      return !bool;
    }
    
    final boolean isOwnedBy(AbstractQueuedLongSynchronizer param1AbstractQueuedLongSynchronizer) { return (param1AbstractQueuedLongSynchronizer == AbstractQueuedLongSynchronizer.this); }
    
    protected final boolean hasWaiters() {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      for (AbstractQueuedLongSynchronizer.Node node = this.firstWaiter; node != null; node = node.nextWaiter) {
        if (node.waitStatus == -2)
          return true; 
      } 
      return false;
    }
    
    protected final int getWaitQueueLength() {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      byte b = 0;
      for (AbstractQueuedLongSynchronizer.Node node = this.firstWaiter; node != null; node = node.nextWaiter) {
        if (node.waitStatus == -2)
          b++; 
      } 
      return b;
    }
    
    protected final Collection<Thread> getWaitingThreads() {
      if (!AbstractQueuedLongSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      ArrayList arrayList = new ArrayList();
      for (AbstractQueuedLongSynchronizer.Node node = this.firstWaiter; node != null; node = node.nextWaiter) {
        if (node.waitStatus == -2) {
          Thread thread = node.thread;
          if (thread != null)
            arrayList.add(thread); 
        } 
      } 
      return arrayList;
    }
  }
  
  static final class Node {
    static final Node SHARED = new Node();
    
    static final Node EXCLUSIVE = null;
    
    static final int CANCELLED = 1;
    
    static final int SIGNAL = -1;
    
    static final int CONDITION = -2;
    
    static final int PROPAGATE = -3;
    
    Node nextWaiter;
    
    final boolean isShared() { return (this.nextWaiter == SHARED); }
    
    final Node predecessor() {
      Node node = this.prev;
      if (node == null)
        throw new NullPointerException(); 
      return node;
    }
    
    Node() {}
    
    Node(Thread param1Thread, Node param1Node) {
      this.nextWaiter = param1Node;
      this.thread = param1Thread;
    }
    
    Node(Thread param1Thread, int param1Int) {
      this.waitStatus = param1Int;
      this.thread = param1Thread;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\AbstractQueuedLongSynchronizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */