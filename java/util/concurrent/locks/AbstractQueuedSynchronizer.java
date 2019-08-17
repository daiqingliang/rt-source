package java.util.concurrent.locks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements Serializable {
  private static final long serialVersionUID = 7373984972572414691L;
  
  static final long spinForTimeoutThreshold = 1000L;
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long stateOffset;
  
  private static final long headOffset;
  
  private static final long tailOffset;
  
  private static final long waitStatusOffset;
  
  private static final long nextOffset;
  
  protected final int getState() { return this.state; }
  
  protected final void setState(int paramInt) { this.state = paramInt; }
  
  protected final boolean compareAndSetState(int paramInt1, int paramInt2) { return unsafe.compareAndSwapInt(this, stateOffset, paramInt1, paramInt2); }
  
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
  
  private void setHeadAndPropagate(Node paramNode, int paramInt) {
    Node node = this.head;
    setHead(paramNode);
    if (paramInt > 0 || node == null || node.waitStatus < 0 || (node = this.head) == null || node.waitStatus < 0) {
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
  
  final boolean acquireQueued(Node paramNode, int paramInt) {
    bool = true;
    try {
      boolean bool1 = false;
      while (true) {
        Node node = paramNode.predecessor();
        if (node == this.head && tryAcquire(paramInt)) {
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
  
  private void doAcquireInterruptibly(int paramInt) {
    node = addWaiter(Node.EXCLUSIVE);
    bool = true;
    try {
      Node node1;
      do {
        node1 = node.predecessor();
        if (node1 == this.head && tryAcquire(paramInt)) {
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
  
  private boolean doAcquireNanos(int paramInt, long paramLong) throws InterruptedException {
    if (paramLong <= 0L)
      return false; 
    long l = System.nanoTime() + paramLong;
    node = addWaiter(Node.EXCLUSIVE);
    bool = true;
    try {
      do {
        Node node1 = node.predecessor();
        if (node1 == this.head && tryAcquire(paramInt)) {
          setHead(node);
          node1.next = null;
          bool = false;
          return true;
        } 
        paramLong = l - System.nanoTime();
        if (paramLong <= 0L)
          return false; 
        if (!shouldParkAfterFailedAcquire(node1, node) || paramLong <= 1000L)
          continue; 
        LockSupport.parkNanos(this, paramLong);
      } while (!Thread.interrupted());
      throw new InterruptedException();
    } finally {
      if (bool)
        cancelAcquire(node); 
    } 
  }
  
  private void doAcquireShared(int paramInt) {
    node = addWaiter(Node.SHARED);
    bool = true;
    try {
      boolean bool1 = false;
      while (true) {
        Node node1 = node.predecessor();
        if (node1 == this.head) {
          int i = tryAcquireShared(paramInt);
          if (i >= 0) {
            setHeadAndPropagate(node, i);
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
  
  private void doAcquireSharedInterruptibly(int paramInt) {
    node = addWaiter(Node.SHARED);
    bool = true;
    try {
      Node node1;
      do {
        node1 = node.predecessor();
        if (node1 != this.head)
          continue; 
        int i = tryAcquireShared(paramInt);
        if (i >= 0) {
          setHeadAndPropagate(node, i);
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
  
  private boolean doAcquireSharedNanos(int paramInt, long paramLong) throws InterruptedException {
    if (paramLong <= 0L)
      return false; 
    long l = System.nanoTime() + paramLong;
    node = addWaiter(Node.SHARED);
    bool = true;
    try {
      do {
        Node node1 = node.predecessor();
        if (node1 == this.head) {
          int i = tryAcquireShared(paramInt);
          if (i >= 0) {
            setHeadAndPropagate(node, i);
            node1.next = null;
            bool = false;
            return true;
          } 
        } 
        paramLong = l - System.nanoTime();
        if (paramLong <= 0L)
          return false; 
        if (!shouldParkAfterFailedAcquire(node1, node) || paramLong <= 1000L)
          continue; 
        LockSupport.parkNanos(this, paramLong);
      } while (!Thread.interrupted());
      throw new InterruptedException();
    } finally {
      if (bool)
        cancelAcquire(node); 
    } 
  }
  
  protected boolean tryAcquire(int paramInt) { throw new UnsupportedOperationException(); }
  
  protected boolean tryRelease(int paramInt) { throw new UnsupportedOperationException(); }
  
  protected int tryAcquireShared(int paramInt) { throw new UnsupportedOperationException(); }
  
  protected boolean tryReleaseShared(int paramInt) { throw new UnsupportedOperationException(); }
  
  protected boolean isHeldExclusively() { throw new UnsupportedOperationException(); }
  
  public final void acquire(int paramInt) {
    if (!tryAcquire(paramInt) && acquireQueued(addWaiter(Node.EXCLUSIVE), paramInt))
      selfInterrupt(); 
  }
  
  public final void acquireInterruptibly(int paramInt) {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    if (!tryAcquire(paramInt))
      doAcquireInterruptibly(paramInt); 
  }
  
  public final boolean tryAcquireNanos(int paramInt, long paramLong) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    return (tryAcquire(paramInt) || doAcquireNanos(paramInt, paramLong));
  }
  
  public final boolean release(int paramInt) {
    if (tryRelease(paramInt)) {
      Node node = this.head;
      if (node != null && node.waitStatus != 0)
        unparkSuccessor(node); 
      return true;
    } 
    return false;
  }
  
  public final void acquireShared(int paramInt) {
    if (tryAcquireShared(paramInt) < 0)
      doAcquireShared(paramInt); 
  }
  
  public final void acquireSharedInterruptibly(int paramInt) {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    if (tryAcquireShared(paramInt) < 0)
      doAcquireSharedInterruptibly(paramInt); 
  }
  
  public final boolean tryAcquireSharedNanos(int paramInt, long paramLong) throws InterruptedException {
    if (Thread.interrupted())
      throw new InterruptedException(); 
    return (tryAcquireShared(paramInt) >= 0 || doAcquireSharedNanos(paramInt, paramLong));
  }
  
  public final boolean releaseShared(int paramInt) {
    if (tryReleaseShared(paramInt)) {
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
    int i = getState();
    String str = hasQueuedThreads() ? "non" : "";
    return super.toString() + "[State = " + i + ", " + str + "empty queue]";
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
  
  final int fullyRelease(Node paramNode) {
    bool = true;
    try {
      int i = getState();
      if (release(i)) {
        bool = false;
        return i;
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
      stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
      headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
      tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
      waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
      nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  public class ConditionObject implements Condition, Serializable {
    private static final long serialVersionUID = 1173984872572414699L;
    
    private AbstractQueuedSynchronizer.Node firstWaiter;
    
    private AbstractQueuedSynchronizer.Node lastWaiter;
    
    private static final int REINTERRUPT = 1;
    
    private static final int THROW_IE = -1;
    
    private AbstractQueuedSynchronizer.Node addConditionWaiter() {
      AbstractQueuedSynchronizer.Node node1 = this.lastWaiter;
      if (node1 != null && node1.waitStatus != -2) {
        unlinkCancelledWaiters();
        node1 = this.lastWaiter;
      } 
      AbstractQueuedSynchronizer.Node node2 = new AbstractQueuedSynchronizer.Node(Thread.currentThread(), -2);
      if (node1 == null) {
        this.firstWaiter = node2;
      } else {
        node1.nextWaiter = node2;
      } 
      this.lastWaiter = node2;
      return node2;
    }
    
    private void doSignal(AbstractQueuedSynchronizer.Node param1Node) {
      do {
        if ((this.firstWaiter = param1Node.nextWaiter) == null)
          this.lastWaiter = null; 
        param1Node.nextWaiter = null;
      } while (!AbstractQueuedSynchronizer.this.transferForSignal(param1Node) && (param1Node = this.firstWaiter) != null);
    }
    
    private void doSignalAll(AbstractQueuedSynchronizer.Node param1Node) {
      this.lastWaiter = this.firstWaiter = null;
      do {
        AbstractQueuedSynchronizer.Node node = param1Node.nextWaiter;
        param1Node.nextWaiter = null;
        AbstractQueuedSynchronizer.this.transferForSignal(param1Node);
        param1Node = node;
      } while (param1Node != null);
    }
    
    private void unlinkCancelledWaiters() {
      AbstractQueuedSynchronizer.Node node1 = this.firstWaiter;
      AbstractQueuedSynchronizer.Node node2 = null;
      while (node1 != null) {
        AbstractQueuedSynchronizer.Node node = node1.nextWaiter;
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
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      AbstractQueuedSynchronizer.Node node = this.firstWaiter;
      if (node != null)
        doSignal(node); 
    }
    
    public final void signalAll() {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      AbstractQueuedSynchronizer.Node node = this.firstWaiter;
      if (node != null)
        doSignalAll(node); 
    }
    
    public final void awaitUninterruptibly() {
      AbstractQueuedSynchronizer.Node node = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(node);
      boolean bool = false;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
        LockSupport.park(this);
        if (Thread.interrupted())
          bool = true; 
      } 
      if (AbstractQueuedSynchronizer.this.acquireQueued(node, i) || bool)
        AbstractQueuedSynchronizer.selfInterrupt(); 
    }
    
    private int checkInterruptWhileWaiting(AbstractQueuedSynchronizer.Node param1Node) { return Thread.interrupted() ? (AbstractQueuedSynchronizer.this.transferAfterCancelledWait(param1Node) ? -1 : 1) : 0; }
    
    private void reportInterruptAfterWait(int param1Int) {
      if (param1Int == -1)
        throw new InterruptedException(); 
      if (param1Int == 1)
        AbstractQueuedSynchronizer.selfInterrupt(); 
    }
    
    public final void await() {
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedSynchronizer.Node node = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(node);
      int j = 0;
      do {
        LockSupport.park(this);
      } while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node) && (j = checkInterruptWhileWaiting(node)) == 0);
      if (AbstractQueuedSynchronizer.this.acquireQueued(node, i) && j != -1)
        j = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (j != 0)
        reportInterruptAfterWait(j); 
    }
    
    public final long awaitNanos(long param1Long) throws InterruptedException {
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedSynchronizer.Node node = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(node);
      long l = System.nanoTime() + param1Long;
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
        if (param1Long <= 0L) {
          AbstractQueuedSynchronizer.this.transferAfterCancelledWait(node);
          break;
        } 
        if (param1Long >= 1000L)
          LockSupport.parkNanos(this, param1Long); 
        if ((j = checkInterruptWhileWaiting(node)) != 0)
          break; 
        param1Long = l - System.nanoTime();
      } 
      if (AbstractQueuedSynchronizer.this.acquireQueued(node, i) && j != -1)
        j = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (j != 0)
        reportInterruptAfterWait(j); 
      return l - System.nanoTime();
    }
    
    public final boolean awaitUntil(Date param1Date) throws InterruptedException {
      long l = param1Date.getTime();
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedSynchronizer.Node node = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(node);
      boolean bool = false;
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
        if (System.currentTimeMillis() > l) {
          bool = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(node);
          break;
        } 
        LockSupport.parkUntil(this, l);
        if ((j = checkInterruptWhileWaiting(node)) != 0)
          break; 
      } 
      if (AbstractQueuedSynchronizer.this.acquireQueued(node, i) && j != -1)
        j = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (j != 0)
        reportInterruptAfterWait(j); 
      return !bool;
    }
    
    public final boolean await(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException {
      long l1 = param1TimeUnit.toNanos(param1Long);
      if (Thread.interrupted())
        throw new InterruptedException(); 
      AbstractQueuedSynchronizer.Node node = addConditionWaiter();
      int i = AbstractQueuedSynchronizer.this.fullyRelease(node);
      long l2 = System.nanoTime() + l1;
      boolean bool = false;
      int j = 0;
      while (!AbstractQueuedSynchronizer.this.isOnSyncQueue(node)) {
        if (l1 <= 0L) {
          bool = AbstractQueuedSynchronizer.this.transferAfterCancelledWait(node);
          break;
        } 
        if (l1 >= 1000L)
          LockSupport.parkNanos(this, l1); 
        if ((j = checkInterruptWhileWaiting(node)) != 0)
          break; 
        l1 = l2 - System.nanoTime();
      } 
      if (AbstractQueuedSynchronizer.this.acquireQueued(node, i) && j != -1)
        j = 1; 
      if (node.nextWaiter != null)
        unlinkCancelledWaiters(); 
      if (j != 0)
        reportInterruptAfterWait(j); 
      return !bool;
    }
    
    final boolean isOwnedBy(AbstractQueuedSynchronizer param1AbstractQueuedSynchronizer) { return (param1AbstractQueuedSynchronizer == AbstractQueuedSynchronizer.this); }
    
    protected final boolean hasWaiters() {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      for (AbstractQueuedSynchronizer.Node node = this.firstWaiter; node != null; node = node.nextWaiter) {
        if (node.waitStatus == -2)
          return true; 
      } 
      return false;
    }
    
    protected final int getWaitQueueLength() {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      byte b = 0;
      for (AbstractQueuedSynchronizer.Node node = this.firstWaiter; node != null; node = node.nextWaiter) {
        if (node.waitStatus == -2)
          b++; 
      } 
      return b;
    }
    
    protected final Collection<Thread> getWaitingThreads() {
      if (!AbstractQueuedSynchronizer.this.isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      ArrayList arrayList = new ArrayList();
      for (AbstractQueuedSynchronizer.Node node = this.firstWaiter; node != null; node = node.nextWaiter) {
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\AbstractQueuedSynchronizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */