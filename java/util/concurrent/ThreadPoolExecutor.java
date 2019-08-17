package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolExecutor extends AbstractExecutorService {
  private final AtomicInteger ctl = new AtomicInteger(ctlOf(-536870912, 0));
  
  private static final int COUNT_BITS = 29;
  
  private static final int CAPACITY = 536870911;
  
  private static final int RUNNING = -536870912;
  
  private static final int SHUTDOWN = 0;
  
  private static final int STOP = 536870912;
  
  private static final int TIDYING = 1073741824;
  
  private static final int TERMINATED = 1610612736;
  
  private final BlockingQueue<Runnable> workQueue;
  
  private final ReentrantLock mainLock = new ReentrantLock();
  
  private final HashSet<Worker> workers = new HashSet();
  
  private final Condition termination = this.mainLock.newCondition();
  
  private int largestPoolSize;
  
  private long completedTaskCount;
  
  private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();
  
  private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");
  
  private final AccessControlContext acc;
  
  private static final boolean ONLY_ONE = true;
  
  private static int runStateOf(int paramInt) { return paramInt & 0xE0000000; }
  
  private static int workerCountOf(int paramInt) { return paramInt & 0x1FFFFFFF; }
  
  private static int ctlOf(int paramInt1, int paramInt2) { return paramInt1 | paramInt2; }
  
  private static boolean runStateLessThan(int paramInt1, int paramInt2) { return (paramInt1 < paramInt2); }
  
  private static boolean runStateAtLeast(int paramInt1, int paramInt2) { return (paramInt1 >= paramInt2); }
  
  private static boolean isRunning(int paramInt) { return (paramInt < 0); }
  
  private boolean compareAndIncrementWorkerCount(int paramInt) { return this.ctl.compareAndSet(paramInt, paramInt + 1); }
  
  private boolean compareAndDecrementWorkerCount(int paramInt) { return this.ctl.compareAndSet(paramInt, paramInt - 1); }
  
  private void decrementWorkerCount() {
    do {
    
    } while (!compareAndDecrementWorkerCount(this.ctl.get()));
  }
  
  private void advanceRunState(int paramInt) {
    int i;
    do {
      i = this.ctl.get();
    } while (!runStateAtLeast(i, paramInt) && !this.ctl.compareAndSet(i, ctlOf(paramInt, workerCountOf(i))));
  }
  
  final void tryTerminate() {
    while (true) {
      int i = this.ctl.get();
      if (isRunning(i) || runStateAtLeast(i, 1073741824) || (runStateOf(i) == 0 && !this.workQueue.isEmpty()))
        return; 
      if (workerCountOf(i) != 0) {
        interruptIdleWorkers(true);
        return;
      } 
      reentrantLock = this.mainLock;
      reentrantLock.lock();
      try {
        if (this.ctl.compareAndSet(i, ctlOf(1073741824, 0))) {
          try {
            terminated();
          } finally {
            this.ctl.set(ctlOf(1610612736, 0));
            this.termination.signalAll();
          } 
          return;
        } 
      } finally {
        reentrantLock.unlock();
      } 
    } 
  }
  
  private void checkShutdownAccess() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(shutdownPerm);
      reentrantLock = this.mainLock;
      reentrantLock.lock();
      try {
        for (Worker worker : this.workers)
          securityManager.checkAccess(worker.thread); 
      } finally {
        reentrantLock.unlock();
      } 
    } 
  }
  
  private void interruptWorkers() {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      for (Worker worker : this.workers)
        worker.interruptIfStarted(); 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void interruptIdleWorkers(boolean paramBoolean) {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      for (Worker worker : this.workers) {
        Thread thread = worker.thread;
        if (!thread.isInterrupted() && worker.tryLock())
          try {
            thread.interrupt();
          } catch (SecurityException securityException) {
          
          } finally {
            worker.unlock();
          }  
        if (paramBoolean)
          break; 
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void interruptIdleWorkers() { interruptIdleWorkers(false); }
  
  final void reject(Runnable paramRunnable) { this.handler.rejectedExecution(paramRunnable, this); }
  
  void onShutdown() {}
  
  final boolean isRunningOrShutdown(boolean paramBoolean) {
    int i = runStateOf(this.ctl.get());
    return (i == -536870912 || (i == 0 && paramBoolean));
  }
  
  private List<Runnable> drainQueue() {
    BlockingQueue blockingQueue = this.workQueue;
    ArrayList arrayList = new ArrayList();
    blockingQueue.drainTo(arrayList);
    if (!blockingQueue.isEmpty())
      for (Runnable runnable : (Runnable[])blockingQueue.toArray(new Runnable[0])) {
        if (blockingQueue.remove(runnable))
          arrayList.add(runnable); 
      }  
    return arrayList;
  }
  
  private boolean addWorker(Runnable paramRunnable, boolean paramBoolean) {
    label51: while (true) {
      int i = this.ctl.get();
      int j = runStateOf(i);
      if (j >= 0 && (j != 0 || paramRunnable != null || this.workQueue.isEmpty()))
        return false; 
      while (true) {
        int k = workerCountOf(i);
        if (k >= 536870911 || k >= (paramBoolean ? this.corePoolSize : this.maximumPoolSize))
          return false; 
        if (compareAndIncrementWorkerCount(i))
          break; 
        i = this.ctl.get();
        if (runStateOf(i) != j)
          continue label51; 
      } 
      break;
    } 
    bool = false;
    boolean bool1 = false;
    worker = null;
    try {
      worker = new Worker(paramRunnable);
      Thread thread = worker.thread;
      if (thread != null) {
        reentrantLock = this.mainLock;
        reentrantLock.lock();
        try {
          int i = runStateOf(this.ctl.get());
          if (i < 0 || (i == 0 && paramRunnable == null)) {
            if (thread.isAlive())
              throw new IllegalThreadStateException(); 
            this.workers.add(worker);
            int j = this.workers.size();
            if (j > this.largestPoolSize)
              this.largestPoolSize = j; 
            bool1 = true;
          } 
        } finally {
          reentrantLock.unlock();
        } 
        if (bool1) {
          thread.start();
          bool = true;
        } 
      } 
    } finally {
      if (!bool)
        addWorkerFailed(worker); 
    } 
    return bool;
  }
  
  private void addWorkerFailed(Worker paramWorker) {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      if (paramWorker != null)
        this.workers.remove(paramWorker); 
      decrementWorkerCount();
      tryTerminate();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  private void processWorkerExit(Worker paramWorker, boolean paramBoolean) {
    if (paramBoolean)
      decrementWorkerCount(); 
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      this.completedTaskCount += paramWorker.completedTasks;
      this.workers.remove(paramWorker);
    } finally {
      reentrantLock.unlock();
    } 
    tryTerminate();
    int i = this.ctl.get();
    if (runStateLessThan(i, 536870912)) {
      if (!paramBoolean) {
        boolean bool = this.allowCoreThreadTimeOut ? 0 : this.corePoolSize;
        if (!bool && !this.workQueue.isEmpty())
          bool = true; 
        if (workerCountOf(i) >= bool)
          return; 
      } 
      addWorker(null, false);
    } 
  }
  
  private Runnable getTask() {
    boolean bool = false;
    while (true) {
      int i = this.ctl.get();
      int j = runStateOf(i);
      if (j >= 0 && (j >= 536870912 || this.workQueue.isEmpty())) {
        decrementWorkerCount();
        return null;
      } 
      int k = workerCountOf(i);
      boolean bool1 = (this.allowCoreThreadTimeOut || k > this.corePoolSize) ? 1 : 0;
      if ((k > this.maximumPoolSize || (bool1 && bool)) && (k > 1 || this.workQueue.isEmpty())) {
        if (compareAndDecrementWorkerCount(i))
          return null; 
        continue;
      } 
      try {
        Runnable runnable = bool1 ? (Runnable)this.workQueue.poll(this.keepAliveTime, TimeUnit.NANOSECONDS) : (Runnable)this.workQueue.take();
        if (runnable != null)
          return runnable; 
        bool = true;
      } catch (InterruptedException interruptedException) {
        bool = false;
      } 
    } 
  }
  
  final void runWorker(Worker paramWorker) {
    Thread thread = Thread.currentThread();
    runnable = paramWorker.firstTask;
    paramWorker.firstTask = null;
    paramWorker.unlock();
    bool = true;
    try {
      while (runnable != null || (runnable = getTask()) != null) {
        paramWorker.lock();
        if ((runStateAtLeast(this.ctl.get(), 536870912) || (Thread.interrupted() && runStateAtLeast(this.ctl.get(), 536870912))) && !thread.isInterrupted())
          thread.interrupt(); 
        try {
          beforeExecute(thread, runnable);
          throwable = null;
          try {
            runnable.run();
          } catch (RuntimeException runtimeException) {
            throwable = runtimeException;
            throw runtimeException;
          } catch (Error error) {
            throwable = error;
            throw error;
          } catch (Throwable throwable1) {
            throwable = throwable1;
            throw new Error(throwable1);
          } finally {
            afterExecute(runnable, throwable);
          } 
        } finally {
          runnable = null;
          paramWorker.completedTasks++;
          paramWorker.unlock();
        } 
      } 
      bool = false;
    } finally {
      processWorkerExit(paramWorker, bool);
    } 
  }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue) { this(paramInt1, paramInt2, paramLong, paramTimeUnit, paramBlockingQueue, Executors.defaultThreadFactory(), defaultHandler); }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue, ThreadFactory paramThreadFactory) { this(paramInt1, paramInt2, paramLong, paramTimeUnit, paramBlockingQueue, paramThreadFactory, defaultHandler); }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue, RejectedExecutionHandler paramRejectedExecutionHandler) { this(paramInt1, paramInt2, paramLong, paramTimeUnit, paramBlockingQueue, Executors.defaultThreadFactory(), paramRejectedExecutionHandler); }
  
  public ThreadPoolExecutor(int paramInt1, int paramInt2, long paramLong, TimeUnit paramTimeUnit, BlockingQueue<Runnable> paramBlockingQueue, ThreadFactory paramThreadFactory, RejectedExecutionHandler paramRejectedExecutionHandler) {
    if (paramInt1 < 0 || paramInt2 <= 0 || paramInt2 < paramInt1 || paramLong < 0L)
      throw new IllegalArgumentException(); 
    if (paramBlockingQueue == null || paramThreadFactory == null || paramRejectedExecutionHandler == null)
      throw new NullPointerException(); 
    this.acc = (System.getSecurityManager() == null) ? null : AccessController.getContext();
    this.corePoolSize = paramInt1;
    this.maximumPoolSize = paramInt2;
    this.workQueue = paramBlockingQueue;
    this.keepAliveTime = paramTimeUnit.toNanos(paramLong);
    this.threadFactory = paramThreadFactory;
    this.handler = paramRejectedExecutionHandler;
  }
  
  public void execute(Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    int i = this.ctl.get();
    if (workerCountOf(i) < this.corePoolSize) {
      if (addWorker(paramRunnable, true))
        return; 
      i = this.ctl.get();
    } 
    if (isRunning(i) && this.workQueue.offer(paramRunnable)) {
      int j = this.ctl.get();
      if (!isRunning(j) && remove(paramRunnable)) {
        reject(paramRunnable);
      } else if (workerCountOf(j) == 0) {
        addWorker(null, false);
      } 
    } else if (!addWorker(paramRunnable, false)) {
      reject(paramRunnable);
    } 
  }
  
  public void shutdown() {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      checkShutdownAccess();
      advanceRunState(0);
      interruptIdleWorkers();
      onShutdown();
    } finally {
      reentrantLock.unlock();
    } 
    tryTerminate();
  }
  
  public List<Runnable> shutdownNow() {
    List list;
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      checkShutdownAccess();
      advanceRunState(536870912);
      interruptWorkers();
      list = drainQueue();
    } finally {
      reentrantLock.unlock();
    } 
    tryTerminate();
    return list;
  }
  
  public boolean isShutdown() { return !isRunning(this.ctl.get()); }
  
  public boolean isTerminating() {
    int i = this.ctl.get();
    return (!isRunning(i) && runStateLessThan(i, 1610612736));
  }
  
  public boolean isTerminated() { return runStateAtLeast(this.ctl.get(), 1610612736); }
  
  public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    long l = paramTimeUnit.toNanos(paramLong);
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      while (true) {
        if (runStateAtLeast(this.ctl.get(), 1610612736))
          return true; 
        if (l <= 0L)
          return false; 
        l = this.termination.awaitNanos(l);
      } 
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  protected void finalize() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager == null || this.acc == null) {
      shutdown();
    } else {
      PrivilegedAction privilegedAction = () -> {
          shutdown();
          return null;
        };
      AccessController.doPrivileged(privilegedAction, this.acc);
    } 
  }
  
  public void setThreadFactory(ThreadFactory paramThreadFactory) {
    if (paramThreadFactory == null)
      throw new NullPointerException(); 
    this.threadFactory = paramThreadFactory;
  }
  
  public ThreadFactory getThreadFactory() { return this.threadFactory; }
  
  public void setRejectedExecutionHandler(RejectedExecutionHandler paramRejectedExecutionHandler) {
    if (paramRejectedExecutionHandler == null)
      throw new NullPointerException(); 
    this.handler = paramRejectedExecutionHandler;
  }
  
  public RejectedExecutionHandler getRejectedExecutionHandler() { return this.handler; }
  
  public void setCorePoolSize(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    int i = paramInt - this.corePoolSize;
    this.corePoolSize = paramInt;
    if (workerCountOf(this.ctl.get()) > paramInt) {
      interruptIdleWorkers();
    } else if (i > 0) {
      int j = Math.min(i, this.workQueue.size());
      do {
      
      } while (j-- > 0 && addWorker(null, true) && !this.workQueue.isEmpty());
    } 
  }
  
  public int getCorePoolSize() { return this.corePoolSize; }
  
  public boolean prestartCoreThread() { return (workerCountOf(this.ctl.get()) < this.corePoolSize && addWorker(null, true)); }
  
  void ensurePrestart() {
    int i = workerCountOf(this.ctl.get());
    if (i < this.corePoolSize) {
      addWorker(null, true);
    } else if (i == 0) {
      addWorker(null, false);
    } 
  }
  
  public int prestartAllCoreThreads() {
    byte b;
    for (b = 0; addWorker(null, true); b++);
    return b;
  }
  
  public boolean allowsCoreThreadTimeOut() { return this.allowCoreThreadTimeOut; }
  
  public void allowCoreThreadTimeOut(boolean paramBoolean) {
    if (paramBoolean && this.keepAliveTime <= 0L)
      throw new IllegalArgumentException("Core threads must have nonzero keep alive times"); 
    if (paramBoolean != this.allowCoreThreadTimeOut) {
      this.allowCoreThreadTimeOut = paramBoolean;
      if (paramBoolean)
        interruptIdleWorkers(); 
    } 
  }
  
  public void setMaximumPoolSize(int paramInt) {
    if (paramInt <= 0 || paramInt < this.corePoolSize)
      throw new IllegalArgumentException(); 
    this.maximumPoolSize = paramInt;
    if (workerCountOf(this.ctl.get()) > paramInt)
      interruptIdleWorkers(); 
  }
  
  public int getMaximumPoolSize() { return this.maximumPoolSize; }
  
  public void setKeepAliveTime(long paramLong, TimeUnit paramTimeUnit) {
    if (paramLong < 0L)
      throw new IllegalArgumentException(); 
    if (paramLong == 0L && allowsCoreThreadTimeOut())
      throw new IllegalArgumentException("Core threads must have nonzero keep alive times"); 
    long l1 = paramTimeUnit.toNanos(paramLong);
    long l2 = l1 - this.keepAliveTime;
    this.keepAliveTime = l1;
    if (l2 < 0L)
      interruptIdleWorkers(); 
  }
  
  public long getKeepAliveTime(TimeUnit paramTimeUnit) { return paramTimeUnit.convert(this.keepAliveTime, TimeUnit.NANOSECONDS); }
  
  public BlockingQueue<Runnable> getQueue() { return this.workQueue; }
  
  public boolean remove(Runnable paramRunnable) {
    boolean bool = this.workQueue.remove(paramRunnable);
    tryTerminate();
    return bool;
  }
  
  public void purge() {
    BlockingQueue blockingQueue = this.workQueue;
    try {
      Iterator iterator = blockingQueue.iterator();
      while (iterator.hasNext()) {
        Runnable runnable = (Runnable)iterator.next();
        if (runnable instanceof Future && ((Future)runnable).isCancelled())
          iterator.remove(); 
      } 
    } catch (ConcurrentModificationException concurrentModificationException) {
      for (Object object : blockingQueue.toArray()) {
        if (object instanceof Future && ((Future)object).isCancelled())
          blockingQueue.remove(object); 
      } 
    } 
    tryTerminate();
  }
  
  public int getPoolSize() {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      return runStateAtLeast(this.ctl.get(), 1073741824) ? 0 : this.workers.size();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int getActiveCount() {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      byte b = 0;
      for (Worker worker : this.workers) {
        if (worker.isLocked())
          b++; 
      } 
      return b;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public int getLargestPoolSize() {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      return this.largestPoolSize;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public long getTaskCount() {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      long l = this.completedTaskCount;
      for (Worker worker : this.workers) {
        l += worker.completedTasks;
        if (worker.isLocked())
          l++; 
      } 
      return l + this.workQueue.size();
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public long getCompletedTaskCount() {
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      long l = this.completedTaskCount;
      for (Worker worker : this.workers)
        l += worker.completedTasks; 
      return l;
    } finally {
      reentrantLock.unlock();
    } 
  }
  
  public String toString() {
    byte b;
    int i;
    long l;
    reentrantLock = this.mainLock;
    reentrantLock.lock();
    try {
      l = this.completedTaskCount;
      b = 0;
      i = this.workers.size();
      for (Worker worker : this.workers) {
        l += worker.completedTasks;
        if (worker.isLocked())
          b++; 
      } 
    } finally {
      reentrantLock.unlock();
    } 
    int j = this.ctl.get();
    String str = runStateLessThan(j, 0) ? "Running" : (runStateAtLeast(j, 1610612736) ? "Terminated" : "Shutting down");
    return super.toString() + "[" + str + ", pool size = " + i + ", active threads = " + b + ", queued tasks = " + this.workQueue.size() + ", completed tasks = " + l + "]";
  }
  
  protected void beforeExecute(Thread paramThread, Runnable paramRunnable) {}
  
  protected void afterExecute(Runnable paramRunnable, Throwable paramThrowable) {}
  
  protected void terminated() {}
  
  public static class AbortPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable param1Runnable, ThreadPoolExecutor param1ThreadPoolExecutor) { throw new RejectedExecutionException("Task " + param1Runnable.toString() + " rejected from " + param1ThreadPoolExecutor.toString()); }
  }
  
  public static class CallerRunsPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable param1Runnable, ThreadPoolExecutor param1ThreadPoolExecutor) {
      if (!param1ThreadPoolExecutor.isShutdown())
        param1Runnable.run(); 
    }
  }
  
  public static class DiscardOldestPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable param1Runnable, ThreadPoolExecutor param1ThreadPoolExecutor) {
      if (!param1ThreadPoolExecutor.isShutdown()) {
        param1ThreadPoolExecutor.getQueue().poll();
        param1ThreadPoolExecutor.execute(param1Runnable);
      } 
    }
  }
  
  public static class DiscardPolicy implements RejectedExecutionHandler {
    public void rejectedExecution(Runnable param1Runnable, ThreadPoolExecutor param1ThreadPoolExecutor) {}
  }
  
  private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
    private static final long serialVersionUID = 6138294804551838833L;
    
    final Thread thread;
    
    Runnable firstTask;
    
    Worker(Runnable param1Runnable) {
      setState(-1);
      this.firstTask = param1Runnable;
      this.thread = this$0.getThreadFactory().newThread(this);
    }
    
    public void run() { ThreadPoolExecutor.this.runWorker(this); }
    
    protected boolean isHeldExclusively() { return (getState() != 0); }
    
    protected boolean tryAcquire(int param1Int) {
      if (compareAndSetState(0, 1)) {
        setExclusiveOwnerThread(Thread.currentThread());
        return true;
      } 
      return false;
    }
    
    protected boolean tryRelease(int param1Int) {
      setExclusiveOwnerThread(null);
      setState(0);
      return true;
    }
    
    public void lock() { acquire(1); }
    
    public boolean tryLock() { return tryAcquire(1); }
    
    public void unlock() { release(1); }
    
    public boolean isLocked() { return isHeldExclusively(); }
    
    void interruptIfStarted() {
      Thread thread1;
      if (getState() >= 0 && (thread1 = this.thread) != null && !thread1.isInterrupted())
        try {
          thread1.interrupt();
        } catch (SecurityException securityException) {} 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ThreadPoolExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */