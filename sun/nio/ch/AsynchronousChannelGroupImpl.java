package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.Channel;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.action.GetIntegerAction;

abstract class AsynchronousChannelGroupImpl extends AsynchronousChannelGroup implements Executor {
  private static final int internalThreadCount = ((Integer)AccessController.doPrivileged(new GetIntegerAction("sun.nio.ch.internalThreadPoolSize", 1))).intValue();
  
  private final ThreadPool pool;
  
  private final AtomicInteger threadCount = new AtomicInteger();
  
  private ScheduledThreadPoolExecutor timeoutExecutor;
  
  private final Queue<Runnable> taskQueue;
  
  private final AtomicBoolean shutdown = new AtomicBoolean();
  
  private final Object shutdownNowLock = new Object();
  
  AsynchronousChannelGroupImpl(AsynchronousChannelProvider paramAsynchronousChannelProvider, ThreadPool paramThreadPool) {
    super(paramAsynchronousChannelProvider);
    this.pool = paramThreadPool;
    if (paramThreadPool.isFixedThreadPool()) {
      this.taskQueue = new ConcurrentLinkedQueue();
    } else {
      this.taskQueue = null;
    } 
    this.timeoutExecutor = (ScheduledThreadPoolExecutor)Executors.newScheduledThreadPool(1, ThreadPool.defaultThreadFactory());
    this.timeoutExecutor.setRemoveOnCancelPolicy(true);
  }
  
  final ExecutorService executor() { return this.pool.executor(); }
  
  final boolean isFixedThreadPool() { return this.pool.isFixedThreadPool(); }
  
  final int fixedThreadCount() { return isFixedThreadPool() ? this.pool.poolSize() : (this.pool.poolSize() + internalThreadCount); }
  
  private Runnable bindToGroup(final Runnable task) {
    final AsynchronousChannelGroupImpl thisGroup = this;
    return new Runnable() {
        public void run() throws IOException {
          Invoker.bindToGroup(thisGroup);
          task.run();
        }
      };
  }
  
  private void startInternalThread(final Runnable task) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            ThreadPool.defaultThreadFactory().newThread(task).start();
            return null;
          }
        }); }
  
  protected final void startThreads(Runnable paramRunnable) {
    if (!isFixedThreadPool())
      for (byte b = 0; b < internalThreadCount; b++) {
        startInternalThread(paramRunnable);
        this.threadCount.incrementAndGet();
      }  
    if (this.pool.poolSize() > 0) {
      paramRunnable = bindToGroup(paramRunnable);
      try {
        for (byte b = 0; b < this.pool.poolSize(); b++) {
          this.pool.executor().execute(paramRunnable);
          this.threadCount.incrementAndGet();
        } 
      } catch (RejectedExecutionException rejectedExecutionException) {}
    } 
  }
  
  final int threadCount() { return this.threadCount.get(); }
  
  final int threadExit(Runnable paramRunnable, boolean paramBoolean) {
    if (paramBoolean)
      try {
        if (Invoker.isBoundToAnyGroup()) {
          this.pool.executor().execute(bindToGroup(paramRunnable));
        } else {
          startInternalThread(paramRunnable);
        } 
        return this.threadCount.get();
      } catch (RejectedExecutionException rejectedExecutionException) {} 
    return this.threadCount.decrementAndGet();
  }
  
  abstract void executeOnHandlerTask(Runnable paramRunnable);
  
  final void executeOnPooledThread(Runnable paramRunnable) {
    if (isFixedThreadPool()) {
      executeOnHandlerTask(paramRunnable);
    } else {
      this.pool.executor().execute(bindToGroup(paramRunnable));
    } 
  }
  
  final void offerTask(Runnable paramRunnable) { this.taskQueue.offer(paramRunnable); }
  
  final Runnable pollTask() { return (this.taskQueue == null) ? null : (Runnable)this.taskQueue.poll(); }
  
  final Future<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit) {
    try {
      return this.timeoutExecutor.schedule(paramRunnable, paramLong, paramTimeUnit);
    } catch (RejectedExecutionException rejectedExecutionException) {
      if (this.terminateInitiated)
        return null; 
      throw new AssertionError(rejectedExecutionException);
    } 
  }
  
  public final boolean isShutdown() { return this.shutdown.get(); }
  
  public final boolean isTerminated() { return this.pool.executor().isTerminated(); }
  
  abstract boolean isEmpty();
  
  abstract Object attachForeignChannel(Channel paramChannel, FileDescriptor paramFileDescriptor) throws IOException;
  
  abstract void detachForeignChannel(Object paramObject);
  
  abstract void closeAllChannels() throws IOException;
  
  abstract void shutdownHandlerTasks() throws IOException;
  
  private void shutdownExecutors() throws IOException { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            AsynchronousChannelGroupImpl.this.pool.executor().shutdown();
            AsynchronousChannelGroupImpl.this.timeoutExecutor.shutdown();
            return null;
          }
        },  null, new Permission[] { new RuntimePermission("modifyThread") }); }
  
  public final void shutdown() throws IOException {
    if (this.shutdown.getAndSet(true))
      return; 
    if (!isEmpty())
      return; 
    synchronized (this.shutdownNowLock) {
      if (!this.terminateInitiated) {
        this.terminateInitiated = true;
        shutdownHandlerTasks();
        shutdownExecutors();
      } 
    } 
  }
  
  public final void shutdownNow() throws IOException {
    this.shutdown.set(true);
    synchronized (this.shutdownNowLock) {
      if (!this.terminateInitiated) {
        this.terminateInitiated = true;
        closeAllChannels();
        shutdownHandlerTasks();
        shutdownExecutors();
      } 
    } 
  }
  
  final void detachFromThreadPool() throws IOException {
    if (this.shutdown.getAndSet(true))
      throw new AssertionError("Already shutdown"); 
    if (!isEmpty())
      throw new AssertionError("Group not empty"); 
    shutdownHandlerTasks();
  }
  
  public final boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException { return this.pool.executor().awaitTermination(paramLong, paramTimeUnit); }
  
  public final void execute(Runnable paramRunnable) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      final AccessControlContext acc = AccessController.getContext();
      final Runnable delegate = paramRunnable;
      paramRunnable = new Runnable() {
          public void run() throws IOException { AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                    delegate.run();
                    return null;
                  }
                },  acc); }
        };
    } 
    executeOnPooledThread(paramRunnable);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\AsynchronousChannelGroupImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */