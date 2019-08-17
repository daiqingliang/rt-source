package java.util.concurrent;

import java.util.concurrent.locks.LockSupport;
import sun.misc.Unsafe;

public class FutureTask<V> extends Object implements RunnableFuture<V> {
  private static final int NEW = 0;
  
  private static final int COMPLETING = 1;
  
  private static final int NORMAL = 2;
  
  private static final int EXCEPTIONAL = 3;
  
  private static final int CANCELLED = 4;
  
  private static final int INTERRUPTING = 5;
  
  private static final int INTERRUPTED = 6;
  
  private Callable<V> callable;
  
  private Object outcome;
  
  private static final Unsafe UNSAFE;
  
  private static final long stateOffset;
  
  private static final long runnerOffset;
  
  private static final long waitersOffset;
  
  private V report(int paramInt) throws ExecutionException {
    Object object = this.outcome;
    if (paramInt == 2)
      return (V)object; 
    if (paramInt >= 4)
      throw new CancellationException(); 
    throw new ExecutionException((Throwable)object);
  }
  
  public FutureTask(Callable<V> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    this.callable = paramCallable;
    this.state = 0;
  }
  
  public FutureTask(Runnable paramRunnable, V paramV) {
    this.callable = Executors.callable(paramRunnable, paramV);
    this.state = 0;
  }
  
  public boolean isCancelled() { return (this.state >= 4); }
  
  public boolean isDone() { return (this.state != 0); }
  
  public boolean cancel(boolean paramBoolean) {
    if (this.state != 0 || !UNSAFE.compareAndSwapInt(this, stateOffset, 0, paramBoolean ? 5 : 4))
      return false; 
    try {
      if (paramBoolean)
        try {
          Thread thread = this.runner;
          if (thread != null)
            thread.interrupt(); 
        } finally {
          UNSAFE.putOrderedInt(this, stateOffset, 6);
        }  
    } finally {
      finishCompletion();
    } 
    return true;
  }
  
  public V get() throws InterruptedException, ExecutionException {
    int i = this.state;
    if (i <= 1)
      i = awaitDone(false, 0L); 
    return (V)report(i);
  }
  
  public V get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    if (paramTimeUnit == null)
      throw new NullPointerException(); 
    int i = this.state;
    if (i <= 1 && (i = awaitDone(true, paramTimeUnit.toNanos(paramLong))) <= 1)
      throw new TimeoutException(); 
    return (V)report(i);
  }
  
  protected void done() {}
  
  protected void set(V paramV) {
    if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1)) {
      this.outcome = paramV;
      UNSAFE.putOrderedInt(this, stateOffset, 2);
      finishCompletion();
    } 
  }
  
  protected void setException(Throwable paramThrowable) {
    if (UNSAFE.compareAndSwapInt(this, stateOffset, 0, 1)) {
      this.outcome = paramThrowable;
      UNSAFE.putOrderedInt(this, stateOffset, 3);
      finishCompletion();
    } 
  }
  
  public void run() {
    if (this.state != 0 || !UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))
      return; 
    try {
      Callable callable1 = this.callable;
      if (callable1 != null && this.state == 0) {
        boolean bool;
        Object object;
        try {
          object = callable1.call();
          bool = true;
        } catch (Throwable throwable) {
          object = null;
          bool = false;
          setException(throwable);
        } 
        if (bool)
          set(object); 
      } 
    } finally {
      this.runner = null;
      int i = this.state;
      if (i >= 5)
        handlePossibleCancellationInterrupt(i); 
    } 
  }
  
  protected boolean runAndReset() {
    if (this.state != 0 || !UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))
      return false; 
    boolean bool = false;
    i = this.state;
    try {
      Callable callable1 = this.callable;
      if (callable1 != null && i == 0)
        try {
          callable1.call();
          bool = true;
        } catch (Throwable throwable) {
          setException(throwable);
        }  
    } finally {
      this.runner = null;
      i = this.state;
      if (i >= 5)
        handlePossibleCancellationInterrupt(i); 
    } 
    return (bool && i == 0);
  }
  
  private void handlePossibleCancellationInterrupt(int paramInt) {
    if (paramInt == 5)
      while (this.state == 5)
        Thread.yield();  
  }
  
  private void finishCompletion() {
    WaitNode waitNode;
    while ((waitNode = this.waiters) != null) {
      if (UNSAFE.compareAndSwapObject(this, waitersOffset, waitNode, null)) {
        while (true) {
          Thread thread = waitNode.thread;
          if (thread != null) {
            waitNode.thread = null;
            LockSupport.unpark(thread);
          } 
          WaitNode waitNode1 = waitNode.next;
          if (waitNode1 == null)
            break; 
          waitNode.next = null;
          waitNode = waitNode1;
        } 
        break;
      } 
    } 
    done();
    this.callable = null;
  }
  
  private int awaitDone(boolean paramBoolean, long paramLong) throws InterruptedException {
    long l = paramBoolean ? (System.nanoTime() + paramLong) : 0L;
    WaitNode waitNode = null;
    boolean bool = false;
    while (true) {
      if (Thread.interrupted()) {
        removeWaiter(waitNode);
        throw new InterruptedException();
      } 
      int i = this.state;
      if (i > 1) {
        if (waitNode != null)
          waitNode.thread = null; 
        return i;
      } 
      if (i == 1) {
        Thread.yield();
        continue;
      } 
      if (waitNode == null) {
        waitNode = new WaitNode();
        continue;
      } 
      if (!bool) {
        bool = UNSAFE.compareAndSwapObject(this, waitersOffset, waitNode.next = this.waiters, waitNode);
        continue;
      } 
      if (paramBoolean) {
        paramLong = l - System.nanoTime();
        if (paramLong <= 0L) {
          removeWaiter(waitNode);
          return this.state;
        } 
        LockSupport.parkNanos(this, paramLong);
        continue;
      } 
      LockSupport.park(this);
    } 
  }
  
  private void removeWaiter(WaitNode paramWaitNode) {
    if (paramWaitNode != null) {
      paramWaitNode.thread = null;
      label22: while (true) {
        WaitNode waitNode1 = null;
        for (WaitNode waitNode2 = this.waiters; waitNode2 != null; waitNode2 = waitNode) {
          WaitNode waitNode = waitNode2.next;
          if (waitNode2.thread != null) {
            waitNode1 = waitNode2;
          } else if (waitNode1 != null) {
            waitNode1.next = waitNode;
            if (waitNode1.thread == null)
              continue label22; 
          } else if (!UNSAFE.compareAndSwapObject(this, waitersOffset, waitNode2, waitNode)) {
            continue label22;
          } 
        } 
        break;
      } 
    } 
  }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = FutureTask.class;
      stateOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("state"));
      runnerOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("runner"));
      waitersOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("waiters"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class WaitNode {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\FutureTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */