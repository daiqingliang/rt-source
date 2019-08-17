package java.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class Phaser {
  private static final int MAX_PARTIES = 65535;
  
  private static final int MAX_PHASE = 2147483647;
  
  private static final int PARTIES_SHIFT = 16;
  
  private static final int PHASE_SHIFT = 32;
  
  private static final int UNARRIVED_MASK = 65535;
  
  private static final long PARTIES_MASK = 4294901760L;
  
  private static final long COUNTS_MASK = 4294967295L;
  
  private static final long TERMINATION_BIT = -9223372036854775808L;
  
  private static final int ONE_ARRIVAL = 1;
  
  private static final int ONE_PARTY = 65536;
  
  private static final int ONE_DEREGISTER = 65537;
  
  private static final int EMPTY = 1;
  
  private final Phaser parent;
  
  private final Phaser root;
  
  private final AtomicReference<QNode> evenQ;
  
  private final AtomicReference<QNode> oddQ;
  
  private static final int NCPU = Runtime.getRuntime().availableProcessors();
  
  static final int SPINS_PER_ARRIVAL = (NCPU < 2) ? 1 : 256;
  
  private static final Unsafe UNSAFE;
  
  private static final long stateOffset;
  
  private static int unarrivedOf(long paramLong) {
    int i = (int)paramLong;
    return (i == 1) ? 0 : (i & 0xFFFF);
  }
  
  private static int partiesOf(long paramLong) { return (int)paramLong >>> 16; }
  
  private static int phaseOf(long paramLong) { return (int)(paramLong >>> 32); }
  
  private static int arrivedOf(long paramLong) {
    int i = (int)paramLong;
    return (i == 1) ? 0 : ((i >>> 16) - (i & 0xFFFF));
  }
  
  private AtomicReference<QNode> queueFor(int paramInt) { return ((paramInt & true) == 0) ? this.evenQ : this.oddQ; }
  
  private String badArrive(long paramLong) { return "Attempted arrival of unregistered party for " + stateToString(paramLong); }
  
  private String badRegister(long paramLong) { return "Attempt to register more than 65535 parties for " + stateToString(paramLong); }
  
  private int doArrive(int paramInt) {
    boolean bool;
    int i;
    long l;
    Phaser phaser = this.root;
    do {
      l = (phaser == this) ? this.state : reconcileState();
      i = (int)(l >>> 32);
      if (i < 0)
        return i; 
      int j = (int)l;
      bool = (j == 1) ? 0 : (j & 0xFFFF);
      if (bool)
        throw new IllegalStateException(badArrive(l)); 
    } while (!UNSAFE.compareAndSwapLong(this, stateOffset, l, l -= paramInt));
    if (bool == true) {
      long l1 = l & 0xFFFF0000L;
      int j = (int)l1 >>> 16;
      if (phaser == this) {
        if (onAdvance(i, j)) {
          l1 |= Float.MIN_VALUE;
        } else if (j == 0) {
          l1 |= 0x1L;
        } else {
          l1 |= j;
        } 
        int k = i + 1 & 0x7FFFFFFF;
        l1 |= k << 32;
        UNSAFE.compareAndSwapLong(this, stateOffset, l, l1);
        releaseWaiters(i);
      } else if (j == 0) {
        i = this.parent.doArrive(65537);
        UNSAFE.compareAndSwapLong(this, stateOffset, l, l | 0x1L);
      } else {
        i = this.parent.doArrive(1);
      } 
    } 
    return i;
  }
  
  private int doRegister(int paramInt) {
    int i;
    long l = paramInt << 16 | paramInt;
    Phaser phaser = this.parent;
    while (true) {
      long l1 = (phaser == null) ? this.state : reconcileState();
      int j = (int)l1;
      int k = j >>> 16;
      int m = j & 0xFFFF;
      if (paramInt > 65535 - k)
        throw new IllegalStateException(badRegister(l1)); 
      i = (int)(l1 >>> 32);
      if (i < 0)
        break; 
      if (j != 1) {
        if (phaser == null || reconcileState() == l1) {
          if (m == 0) {
            this.root.internalAwaitAdvance(i, null);
            continue;
          } 
          if (UNSAFE.compareAndSwapLong(this, stateOffset, l1, l1 + l))
            break; 
        } 
        continue;
      } 
      if (phaser == null) {
        long l2 = i << 32 | l;
        if (UNSAFE.compareAndSwapLong(this, stateOffset, l1, l2))
          break; 
        continue;
      } 
      synchronized (this) {
        if (this.state == l1) {
          i = phaser.doRegister(1);
          if (i < 0)
            break; 
          while (!UNSAFE.compareAndSwapLong(this, stateOffset, l1, i << 32 | l)) {
            l1 = this.state;
            i = (int)(this.root.state >>> 32);
          } 
          break;
        } 
      } 
    } 
    return i;
  }
  
  private long reconcileState() {
    Phaser phaser = this.root;
    long l = this.state;
    if (phaser != this) {
      int i;
      while ((i = (int)(phaser.state >>> 32)) != (int)(l >>> 32)) {
        int j;
        if (!UNSAFE.compareAndSwapLong(this, stateOffset, l, l = i << 32 | ((i < 0) ? (l & 0xFFFFFFFFL) : (((j = (int)l >>> 16) == 0) ? 1L : (l & 0xFFFF0000L | j)))))
          l = this.state; 
      } 
    } 
    return l;
  }
  
  public Phaser() { this(null, 0); }
  
  public Phaser(int paramInt) { this(null, paramInt); }
  
  public Phaser(Phaser paramPhaser) { this(paramPhaser, 0); }
  
  public Phaser(Phaser paramPhaser, int paramInt) {
    if (paramInt >>> 16 != 0)
      throw new IllegalArgumentException("Illegal number of parties"); 
    int i = 0;
    this.parent = paramPhaser;
    if (paramPhaser != null) {
      Phaser phaser = paramPhaser.root;
      this.root = phaser;
      this.evenQ = phaser.evenQ;
      this.oddQ = phaser.oddQ;
      if (paramInt != 0)
        i = paramPhaser.doRegister(1); 
    } else {
      this.root = this;
      this.evenQ = new AtomicReference();
      this.oddQ = new AtomicReference();
    } 
    this.state = (paramInt == 0) ? 1L : (i << 32 | paramInt << 16 | paramInt);
  }
  
  public int register() { return doRegister(1); }
  
  public int bulkRegister(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return (paramInt == 0) ? getPhase() : doRegister(paramInt);
  }
  
  public int arrive() { return doArrive(1); }
  
  public int arriveAndDeregister() { return doArrive(65537); }
  
  public int arriveAndAwaitAdvance() {
    boolean bool;
    int i;
    long l1;
    Phaser phaser = this.root;
    do {
      l1 = (phaser == this) ? this.state : reconcileState();
      i = (int)(l1 >>> 32);
      if (i < 0)
        return i; 
      int m = (int)l1;
      bool = (m == 1) ? 0 : (m & 0xFFFF);
      if (bool)
        throw new IllegalStateException(badArrive(l1)); 
    } while (!UNSAFE.compareAndSwapLong(this, stateOffset, l1, --l1));
    if (bool > true)
      return phaser.internalAwaitAdvance(i, null); 
    if (phaser != this)
      return this.parent.arriveAndAwaitAdvance(); 
    long l2 = l1 & 0xFFFF0000L;
    int j = (int)l2 >>> 16;
    if (onAdvance(i, j)) {
      l2 |= Float.MIN_VALUE;
    } else if (j == 0) {
      l2 |= 0x1L;
    } else {
      l2 |= j;
    } 
    int k = i + 1 & 0x7FFFFFFF;
    l2 |= k << 32;
    if (!UNSAFE.compareAndSwapLong(this, stateOffset, l1, l2))
      return (int)(this.state >>> 32); 
    releaseWaiters(i);
    return k;
  }
  
  public int awaitAdvance(int paramInt) {
    Phaser phaser = this.root;
    long l = (phaser == this) ? this.state : reconcileState();
    int i = (int)(l >>> 32);
    return (paramInt < 0) ? paramInt : ((i == paramInt) ? phaser.internalAwaitAdvance(paramInt, null) : i);
  }
  
  public int awaitAdvanceInterruptibly(int paramInt) {
    Phaser phaser = this.root;
    long l = (phaser == this) ? this.state : reconcileState();
    int i = (int)(l >>> 32);
    if (paramInt < 0)
      return paramInt; 
    if (i == paramInt) {
      QNode qNode = new QNode(this, paramInt, true, false, 0L);
      i = phaser.internalAwaitAdvance(paramInt, qNode);
      if (qNode.wasInterrupted)
        throw new InterruptedException(); 
    } 
    return i;
  }
  
  public int awaitAdvanceInterruptibly(int paramInt, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, TimeoutException {
    long l1 = paramTimeUnit.toNanos(paramLong);
    Phaser phaser = this.root;
    long l2 = (phaser == this) ? this.state : reconcileState();
    int i = (int)(l2 >>> 32);
    if (paramInt < 0)
      return paramInt; 
    if (i == paramInt) {
      QNode qNode = new QNode(this, paramInt, true, true, l1);
      i = phaser.internalAwaitAdvance(paramInt, qNode);
      if (qNode.wasInterrupted)
        throw new InterruptedException(); 
      if (i == paramInt)
        throw new TimeoutException(); 
    } 
    return i;
  }
  
  public void forceTermination() {
    Phaser phaser = this.root;
    long l;
    while ((l = phaser.state) >= 0L) {
      if (UNSAFE.compareAndSwapLong(phaser, stateOffset, l, l | Float.MIN_VALUE)) {
        releaseWaiters(0);
        releaseWaiters(1);
        return;
      } 
    } 
  }
  
  public final int getPhase() { return (int)(this.root.state >>> 32); }
  
  public int getRegisteredParties() { return partiesOf(this.state); }
  
  public int getArrivedParties() { return arrivedOf(reconcileState()); }
  
  public int getUnarrivedParties() { return unarrivedOf(reconcileState()); }
  
  public Phaser getParent() { return this.parent; }
  
  public Phaser getRoot() { return this.root; }
  
  public boolean isTerminated() { return (this.root.state < 0L); }
  
  protected boolean onAdvance(int paramInt1, int paramInt2) { return (paramInt2 == 0); }
  
  public String toString() { return stateToString(reconcileState()); }
  
  private String stateToString(long paramLong) { return super.toString() + "[phase = " + phaseOf(paramLong) + " parties = " + partiesOf(paramLong) + " arrived = " + arrivedOf(paramLong) + "]"; }
  
  private void releaseWaiters(int paramInt) {
    AtomicReference atomicReference = ((paramInt & true) == 0) ? this.evenQ : this.oddQ;
    QNode qNode;
    while ((qNode = (QNode)atomicReference.get()) != null && qNode.phase != (int)(this.root.state >>> 32)) {
      Thread thread;
      if (atomicReference.compareAndSet(qNode, qNode.next) && (thread = qNode.thread) != null) {
        qNode.thread = null;
        LockSupport.unpark(thread);
      } 
    } 
  }
  
  private int abortWait(int paramInt) {
    AtomicReference atomicReference = ((paramInt & true) == 0) ? this.evenQ : this.oddQ;
    while (true) {
      QNode qNode = (QNode)atomicReference.get();
      int i = (int)(this.root.state >>> 32);
      Thread thread;
      if (qNode == null || ((thread = qNode.thread) != null && qNode.phase == i))
        return i; 
      if (atomicReference.compareAndSet(qNode, qNode.next) && thread != null) {
        qNode.thread = null;
        LockSupport.unpark(thread);
      } 
    } 
  }
  
  private int internalAwaitAdvance(int paramInt, QNode paramQNode) {
    releaseWaiters(paramInt - 1);
    boolean bool = false;
    int i = 0;
    int j = SPINS_PER_ARRIVAL;
    long l;
    int k;
    while ((k = (int)((l = this.state) >>> 32)) == paramInt) {
      if (paramQNode == null) {
        int m = (int)l & 0xFFFF;
        if (m != i && (i = m) < NCPU)
          j += SPINS_PER_ARRIVAL; 
        boolean bool1 = Thread.interrupted();
        if (bool1 || --j < 0) {
          paramQNode = new QNode(this, paramInt, false, false, 0L);
          paramQNode.wasInterrupted = bool1;
        } 
        continue;
      } 
      if (paramQNode.isReleasable())
        break; 
      if (!bool) {
        AtomicReference atomicReference = ((paramInt & true) == 0) ? this.evenQ : this.oddQ;
        QNode qNode = paramQNode.next = (QNode)atomicReference.get();
        if ((qNode == null || qNode.phase == paramInt) && (int)(this.state >>> 32) == paramInt)
          bool = atomicReference.compareAndSet(qNode, paramQNode); 
        continue;
      } 
      try {
        ForkJoinPool.managedBlock(paramQNode);
      } catch (InterruptedException interruptedException) {
        paramQNode.wasInterrupted = true;
      } 
    } 
    if (paramQNode != null) {
      if (paramQNode.thread != null)
        paramQNode.thread = null; 
      if (paramQNode.wasInterrupted && !paramQNode.interruptible)
        Thread.currentThread().interrupt(); 
      if (k == paramInt && (k = (int)(this.state >>> 32)) == paramInt)
        return abortWait(paramInt); 
    } 
    releaseWaiters(paramInt);
    return k;
  }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = Phaser.class;
      stateOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("state"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class QNode implements ForkJoinPool.ManagedBlocker {
    final Phaser phaser;
    
    final int phase;
    
    final boolean interruptible;
    
    final boolean timed;
    
    boolean wasInterrupted;
    
    long nanos;
    
    final long deadline;
    
    QNode next;
    
    QNode(Phaser param1Phaser, int param1Int, boolean param1Boolean1, boolean param1Boolean2, long param1Long) {
      this.phaser = param1Phaser;
      this.phase = param1Int;
      this.interruptible = param1Boolean1;
      this.nanos = param1Long;
      this.timed = param1Boolean2;
      this.deadline = param1Boolean2 ? (System.nanoTime() + param1Long) : 0L;
      this.thread = Thread.currentThread();
    }
    
    public boolean isReleasable() {
      if (this.thread == null)
        return true; 
      if (this.phaser.getPhase() != this.phase) {
        this.thread = null;
        return true;
      } 
      if (Thread.interrupted())
        this.wasInterrupted = true; 
      if (this.wasInterrupted && this.interruptible) {
        this.thread = null;
        return true;
      } 
      if (this.timed) {
        if (this.nanos > 0L)
          this.nanos = this.deadline - System.nanoTime(); 
        if (this.nanos <= 0L) {
          this.thread = null;
          return true;
        } 
      } 
      return false;
    }
    
    public boolean block() {
      if (isReleasable())
        return true; 
      if (!this.timed) {
        LockSupport.park(this);
      } else if (this.nanos > 0L) {
        LockSupport.parkNanos(this, this.nanos);
      } 
      return isReleasable();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\Phaser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */