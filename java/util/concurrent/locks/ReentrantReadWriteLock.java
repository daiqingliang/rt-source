package java.util.concurrent.locks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import sun.misc.Unsafe;

public class ReentrantReadWriteLock implements ReadWriteLock, Serializable {
  private static final long serialVersionUID = -6992448646407690164L;
  
  private final ReadLock readerLock;
  
  private final WriteLock writerLock;
  
  final Sync sync;
  
  private static final Unsafe UNSAFE;
  
  private static final long TID_OFFSET;
  
  public ReentrantReadWriteLock() { this(false); }
  
  public ReentrantReadWriteLock(boolean paramBoolean) {
    this.sync = paramBoolean ? new FairSync() : new NonfairSync();
    this.readerLock = new ReadLock(this);
    this.writerLock = new WriteLock(this);
  }
  
  public WriteLock writeLock() { return this.writerLock; }
  
  public ReadLock readLock() { return this.readerLock; }
  
  public final boolean isFair() { return this.sync instanceof FairSync; }
  
  protected Thread getOwner() { return this.sync.getOwner(); }
  
  public int getReadLockCount() { return this.sync.getReadLockCount(); }
  
  public boolean isWriteLocked() { return this.sync.isWriteLocked(); }
  
  public boolean isWriteLockedByCurrentThread() { return this.sync.isHeldExclusively(); }
  
  public int getWriteHoldCount() { return this.sync.getWriteHoldCount(); }
  
  public int getReadHoldCount() { return this.sync.getReadHoldCount(); }
  
  protected Collection<Thread> getQueuedWriterThreads() { return this.sync.getExclusiveQueuedThreads(); }
  
  protected Collection<Thread> getQueuedReaderThreads() { return this.sync.getSharedQueuedThreads(); }
  
  public final boolean hasQueuedThreads() { return this.sync.hasQueuedThreads(); }
  
  public final boolean hasQueuedThread(Thread paramThread) { return this.sync.isQueued(paramThread); }
  
  public final int getQueueLength() { return this.sync.getQueueLength(); }
  
  protected Collection<Thread> getQueuedThreads() { return this.sync.getQueuedThreads(); }
  
  public boolean hasWaiters(Condition paramCondition) {
    if (paramCondition == null)
      throw new NullPointerException(); 
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject))
      throw new IllegalArgumentException("not owner"); 
    return this.sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  public int getWaitQueueLength(Condition paramCondition) {
    if (paramCondition == null)
      throw new NullPointerException(); 
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject))
      throw new IllegalArgumentException("not owner"); 
    return this.sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  protected Collection<Thread> getWaitingThreads(Condition paramCondition) {
    if (paramCondition == null)
      throw new NullPointerException(); 
    if (!(paramCondition instanceof AbstractQueuedSynchronizer.ConditionObject))
      throw new IllegalArgumentException("not owner"); 
    return this.sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)paramCondition);
  }
  
  public String toString() {
    int i = this.sync.getCount();
    int j = Sync.exclusiveCount(i);
    int k = Sync.sharedCount(i);
    return super.toString() + "[Write locks = " + j + ", Read locks = " + k + "]";
  }
  
  static final long getThreadId(Thread paramThread) { return UNSAFE.getLongVolatile(paramThread, TID_OFFSET); }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = Thread.class;
      TID_OFFSET = UNSAFE.objectFieldOffset(clazz.getDeclaredField("tid"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class FairSync extends Sync {
    private static final long serialVersionUID = -2274990926593161451L;
    
    final boolean writerShouldBlock() { return hasQueuedPredecessors(); }
    
    final boolean readerShouldBlock() { return hasQueuedPredecessors(); }
  }
  
  static final class NonfairSync extends Sync {
    private static final long serialVersionUID = -8159625535654395037L;
    
    final boolean writerShouldBlock() { return false; }
    
    final boolean readerShouldBlock() { return apparentlyFirstQueuedIsExclusive(); }
  }
  
  public static class ReadLock implements Lock, Serializable {
    private static final long serialVersionUID = -5992448646407690164L;
    
    private final ReentrantReadWriteLock.Sync sync;
    
    protected ReadLock(ReentrantReadWriteLock param1ReentrantReadWriteLock) { this.sync = param1ReentrantReadWriteLock.sync; }
    
    public void lock() { this.sync.acquireShared(1); }
    
    public void lockInterruptibly() { this.sync.acquireSharedInterruptibly(1); }
    
    public boolean tryLock() { return this.sync.tryReadLock(); }
    
    public boolean tryLock(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException { return this.sync.tryAcquireSharedNanos(1, param1TimeUnit.toNanos(param1Long)); }
    
    public void unlock() { this.sync.releaseShared(1); }
    
    public Condition newCondition() { throw new UnsupportedOperationException(); }
    
    public String toString() {
      int i = this.sync.getReadLockCount();
      return super.toString() + "[Read locks = " + i + "]";
    }
  }
  
  static abstract class Sync extends AbstractQueuedSynchronizer {
    private static final long serialVersionUID = 6317671515068378041L;
    
    static final int SHARED_SHIFT = 16;
    
    static final int SHARED_UNIT = 65536;
    
    static final int MAX_COUNT = 65535;
    
    static final int EXCLUSIVE_MASK = 65535;
    
    private ThreadLocalHoldCounter readHolds = new ThreadLocalHoldCounter();
    
    private HoldCounter cachedHoldCounter;
    
    private Thread firstReader = null;
    
    private int firstReaderHoldCount;
    
    static int sharedCount(int param1Int) { return param1Int >>> 16; }
    
    static int exclusiveCount(int param1Int) { return param1Int & 0xFFFF; }
    
    Sync() { setState(getState()); }
    
    abstract boolean readerShouldBlock();
    
    abstract boolean writerShouldBlock();
    
    protected final boolean tryRelease(int param1Int) {
      if (!isHeldExclusively())
        throw new IllegalMonitorStateException(); 
      int i = getState() - param1Int;
      boolean bool = (exclusiveCount(i) == 0);
      if (bool)
        setExclusiveOwnerThread(null); 
      setState(i);
      return bool;
    }
    
    protected final boolean tryAcquire(int param1Int) {
      Thread thread = Thread.currentThread();
      int i = getState();
      int j = exclusiveCount(i);
      if (i != 0) {
        if (j == 0 || thread != getExclusiveOwnerThread())
          return false; 
        if (j + exclusiveCount(param1Int) > 65535)
          throw new Error("Maximum lock count exceeded"); 
        setState(i + param1Int);
        return true;
      } 
      if (writerShouldBlock() || !compareAndSetState(i, i + param1Int))
        return false; 
      setExclusiveOwnerThread(thread);
      return true;
    }
    
    protected final boolean tryReleaseShared(int param1Int) {
      int j;
      int i;
      Thread thread = Thread.currentThread();
      if (this.firstReader == thread) {
        if (this.firstReaderHoldCount == 1) {
          this.firstReader = null;
        } else {
          this.firstReaderHoldCount--;
        } 
      } else {
        HoldCounter holdCounter = this.cachedHoldCounter;
        if (holdCounter == null || holdCounter.tid != ReentrantReadWriteLock.getThreadId(thread))
          holdCounter = (HoldCounter)this.readHolds.get(); 
        j = holdCounter.count;
        if (j <= 1) {
          this.readHolds.remove();
          if (j <= 0)
            throw unmatchedUnlockException(); 
        } 
        holdCounter.count--;
      } 
      do {
        i = getState();
        j = i - 65536;
      } while (!compareAndSetState(i, j));
      return (j == 0);
    }
    
    private IllegalMonitorStateException unmatchedUnlockException() { return new IllegalMonitorStateException("attempt to unlock read lock, not locked by current thread"); }
    
    protected final int tryAcquireShared(int param1Int) {
      Thread thread = Thread.currentThread();
      int i = getState();
      if (exclusiveCount(i) != 0 && getExclusiveOwnerThread() != thread)
        return -1; 
      int j = sharedCount(i);
      if (!readerShouldBlock() && j < 65535 && compareAndSetState(i, i + 65536)) {
        if (j == 0) {
          this.firstReader = thread;
          this.firstReaderHoldCount = 1;
        } else if (this.firstReader == thread) {
          this.firstReaderHoldCount++;
        } else {
          HoldCounter holdCounter = this.cachedHoldCounter;
          if (holdCounter == null || holdCounter.tid != ReentrantReadWriteLock.getThreadId(thread)) {
            this.cachedHoldCounter = holdCounter = (HoldCounter)this.readHolds.get();
          } else if (holdCounter.count == 0) {
            this.readHolds.set(holdCounter);
          } 
          holdCounter.count++;
        } 
        return 1;
      } 
      return fullTryAcquireShared(thread);
    }
    
    final int fullTryAcquireShared(Thread param1Thread) {
      int i;
      HoldCounter holdCounter = null;
      do {
        i = getState();
        if (exclusiveCount(i) != 0) {
          if (getExclusiveOwnerThread() != param1Thread)
            return -1; 
        } else if (readerShouldBlock() && this.firstReader != param1Thread) {
          if (holdCounter == null) {
            holdCounter = this.cachedHoldCounter;
            if (holdCounter == null || holdCounter.tid != ReentrantReadWriteLock.getThreadId(param1Thread)) {
              holdCounter = (HoldCounter)this.readHolds.get();
              if (holdCounter.count == 0)
                this.readHolds.remove(); 
            } 
          } 
          if (holdCounter.count == 0)
            return -1; 
        } 
        if (sharedCount(i) == 65535)
          throw new Error("Maximum lock count exceeded"); 
      } while (!compareAndSetState(i, i + 65536));
      if (sharedCount(i) == 0) {
        this.firstReader = param1Thread;
        this.firstReaderHoldCount = 1;
      } else if (this.firstReader == param1Thread) {
        this.firstReaderHoldCount++;
      } else {
        if (holdCounter == null)
          holdCounter = this.cachedHoldCounter; 
        if (holdCounter == null || holdCounter.tid != ReentrantReadWriteLock.getThreadId(param1Thread)) {
          holdCounter = (HoldCounter)this.readHolds.get();
        } else if (holdCounter.count == 0) {
          this.readHolds.set(holdCounter);
        } 
        holdCounter.count++;
        this.cachedHoldCounter = holdCounter;
      } 
      return 1;
    }
    
    final boolean tryWriteLock() {
      Thread thread = Thread.currentThread();
      int i = getState();
      if (i != 0) {
        int j = exclusiveCount(i);
        if (j == 0 || thread != getExclusiveOwnerThread())
          return false; 
        if (j == 65535)
          throw new Error("Maximum lock count exceeded"); 
      } 
      if (!compareAndSetState(i, i + 1))
        return false; 
      setExclusiveOwnerThread(thread);
      return true;
    }
    
    final boolean tryReadLock() {
      int j;
      int i;
      Thread thread = Thread.currentThread();
      do {
        i = getState();
        if (exclusiveCount(i) != 0 && getExclusiveOwnerThread() != thread)
          return false; 
        j = sharedCount(i);
        if (j == 65535)
          throw new Error("Maximum lock count exceeded"); 
      } while (!compareAndSetState(i, i + 65536));
      if (j == 0) {
        this.firstReader = thread;
        this.firstReaderHoldCount = 1;
      } else if (this.firstReader == thread) {
        this.firstReaderHoldCount++;
      } else {
        HoldCounter holdCounter = this.cachedHoldCounter;
        if (holdCounter == null || holdCounter.tid != ReentrantReadWriteLock.getThreadId(thread)) {
          this.cachedHoldCounter = holdCounter = (HoldCounter)this.readHolds.get();
        } else if (holdCounter.count == 0) {
          this.readHolds.set(holdCounter);
        } 
        holdCounter.count++;
      } 
      return true;
    }
    
    protected final boolean isHeldExclusively() { return (getExclusiveOwnerThread() == Thread.currentThread()); }
    
    final AbstractQueuedSynchronizer.ConditionObject newCondition() { return new AbstractQueuedSynchronizer.ConditionObject(this); }
    
    final Thread getOwner() { return (exclusiveCount(getState()) == 0) ? null : getExclusiveOwnerThread(); }
    
    final int getReadLockCount() { return sharedCount(getState()); }
    
    final boolean isWriteLocked() { return (exclusiveCount(getState()) != 0); }
    
    final int getWriteHoldCount() { return isHeldExclusively() ? exclusiveCount(getState()) : 0; }
    
    final int getReadHoldCount() {
      if (getReadLockCount() == 0)
        return 0; 
      Thread thread = Thread.currentThread();
      if (this.firstReader == thread)
        return this.firstReaderHoldCount; 
      HoldCounter holdCounter = this.cachedHoldCounter;
      if (holdCounter != null && holdCounter.tid == ReentrantReadWriteLock.getThreadId(thread))
        return holdCounter.count; 
      int i = ((HoldCounter)this.readHolds.get()).count;
      if (i == 0)
        this.readHolds.remove(); 
      return i;
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws IOException, ClassNotFoundException {
      param1ObjectInputStream.defaultReadObject();
      this.readHolds = new ThreadLocalHoldCounter();
      setState(0);
    }
    
    final int getCount() { return getState(); }
    
    static final class HoldCounter {
      int count = 0;
      
      final long tid = ReentrantReadWriteLock.getThreadId(Thread.currentThread());
    }
    
    static final class ThreadLocalHoldCounter extends ThreadLocal<HoldCounter> {
      public ReentrantReadWriteLock.Sync.HoldCounter initialValue() { return new ReentrantReadWriteLock.Sync.HoldCounter(); }
    }
  }
  
  public static class WriteLock implements Lock, Serializable {
    private static final long serialVersionUID = -4992448646407690164L;
    
    private final ReentrantReadWriteLock.Sync sync;
    
    protected WriteLock(ReentrantReadWriteLock param1ReentrantReadWriteLock) { this.sync = param1ReentrantReadWriteLock.sync; }
    
    public void lock() { this.sync.acquire(1); }
    
    public void lockInterruptibly() { this.sync.acquireInterruptibly(1); }
    
    public boolean tryLock() { return this.sync.tryWriteLock(); }
    
    public boolean tryLock(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException { return this.sync.tryAcquireNanos(1, param1TimeUnit.toNanos(param1Long)); }
    
    public void unlock() { this.sync.release(1); }
    
    public Condition newCondition() { return this.sync.newCondition(); }
    
    public String toString() {
      Thread thread = this.sync.getOwner();
      return super.toString() + ((thread == null) ? "[Unlocked]" : ("[Locked by thread " + thread.getName() + "]"));
    }
    
    public boolean isHeldByCurrentThread() { return this.sync.isHeldExclusively(); }
    
    public int getHoldCount() { return this.sync.getWriteHoldCount(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\ReentrantReadWriteLock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */