package java.util.concurrent;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.util.SecurityConstants;

public class Executors {
  public static ExecutorService newFixedThreadPool(int paramInt) { return new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue()); }
  
  public static ExecutorService newWorkStealingPool(int paramInt) { return new ForkJoinPool(paramInt, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true); }
  
  public static ExecutorService newWorkStealingPool() { return new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true); }
  
  public static ExecutorService newFixedThreadPool(int paramInt, ThreadFactory paramThreadFactory) { return new ThreadPoolExecutor(paramInt, paramInt, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), paramThreadFactory); }
  
  public static ExecutorService newSingleThreadExecutor() { return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue())); }
  
  public static ExecutorService newSingleThreadExecutor(ThreadFactory paramThreadFactory) { return new FinalizableDelegatedExecutorService(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), paramThreadFactory)); }
  
  public static ExecutorService newCachedThreadPool() { return new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue()); }
  
  public static ExecutorService newCachedThreadPool(ThreadFactory paramThreadFactory) { return new ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), paramThreadFactory); }
  
  public static ScheduledExecutorService newSingleThreadScheduledExecutor() { return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1)); }
  
  public static ScheduledExecutorService newSingleThreadScheduledExecutor(ThreadFactory paramThreadFactory) { return new DelegatedScheduledExecutorService(new ScheduledThreadPoolExecutor(1, paramThreadFactory)); }
  
  public static ScheduledExecutorService newScheduledThreadPool(int paramInt) { return new ScheduledThreadPoolExecutor(paramInt); }
  
  public static ScheduledExecutorService newScheduledThreadPool(int paramInt, ThreadFactory paramThreadFactory) { return new ScheduledThreadPoolExecutor(paramInt, paramThreadFactory); }
  
  public static ExecutorService unconfigurableExecutorService(ExecutorService paramExecutorService) {
    if (paramExecutorService == null)
      throw new NullPointerException(); 
    return new DelegatedExecutorService(paramExecutorService);
  }
  
  public static ScheduledExecutorService unconfigurableScheduledExecutorService(ScheduledExecutorService paramScheduledExecutorService) {
    if (paramScheduledExecutorService == null)
      throw new NullPointerException(); 
    return new DelegatedScheduledExecutorService(paramScheduledExecutorService);
  }
  
  public static ThreadFactory defaultThreadFactory() { return new DefaultThreadFactory(); }
  
  public static ThreadFactory privilegedThreadFactory() { return new PrivilegedThreadFactory(); }
  
  public static <T> Callable<T> callable(Runnable paramRunnable, T paramT) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    return new RunnableAdapter(paramRunnable, paramT);
  }
  
  public static Callable<Object> callable(Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    return new RunnableAdapter(paramRunnable, null);
  }
  
  public static Callable<Object> callable(final PrivilegedAction<?> action) {
    if (paramPrivilegedAction == null)
      throw new NullPointerException(); 
    return new Callable<Object>() {
        public Object call() { return action.run(); }
      };
  }
  
  public static Callable<Object> callable(final PrivilegedExceptionAction<?> action) {
    if (paramPrivilegedExceptionAction == null)
      throw new NullPointerException(); 
    return new Callable<Object>() {
        public Object call() { return action.run(); }
      };
  }
  
  public static <T> Callable<T> privilegedCallable(Callable<T> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    return new PrivilegedCallable(paramCallable);
  }
  
  public static <T> Callable<T> privilegedCallableUsingCurrentClassLoader(Callable<T> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    return new PrivilegedCallableUsingCurrentClassLoader(paramCallable);
  }
  
  static class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    
    private final ThreadGroup group;
    
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    
    private final String namePrefix;
    
    DefaultThreadFactory() {
      SecurityManager securityManager = System.getSecurityManager();
      this.group = (securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
      this.namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
    }
    
    public Thread newThread(Runnable param1Runnable) {
      Thread thread = new Thread(this.group, param1Runnable, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
      if (thread.isDaemon())
        thread.setDaemon(false); 
      if (thread.getPriority() != 5)
        thread.setPriority(5); 
      return thread;
    }
  }
  
  static class DelegatedExecutorService extends AbstractExecutorService {
    private final ExecutorService e;
    
    DelegatedExecutorService(ExecutorService param1ExecutorService) { this.e = param1ExecutorService; }
    
    public void execute(Runnable param1Runnable) { this.e.execute(param1Runnable); }
    
    public void shutdown() { this.e.shutdown(); }
    
    public List<Runnable> shutdownNow() { return this.e.shutdownNow(); }
    
    public boolean isShutdown() { return this.e.isShutdown(); }
    
    public boolean isTerminated() { return this.e.isTerminated(); }
    
    public boolean awaitTermination(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException { return this.e.awaitTermination(param1Long, param1TimeUnit); }
    
    public Future<?> submit(Runnable param1Runnable) { return this.e.submit(param1Runnable); }
    
    public <T> Future<T> submit(Callable<T> param1Callable) { return this.e.submit(param1Callable); }
    
    public <T> Future<T> submit(Runnable param1Runnable, T param1T) { return this.e.submit(param1Runnable, param1T); }
    
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> param1Collection) throws InterruptedException { return this.e.invokeAll(param1Collection); }
    
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> param1Collection, long param1Long, TimeUnit param1TimeUnit) throws InterruptedException { return this.e.invokeAll(param1Collection, param1Long, param1TimeUnit); }
    
    public <T> T invokeAny(Collection<? extends Callable<T>> param1Collection) throws InterruptedException, ExecutionException { return (T)this.e.invokeAny(param1Collection); }
    
    public <T> T invokeAny(Collection<? extends Callable<T>> param1Collection, long param1Long, TimeUnit param1TimeUnit) throws InterruptedException, ExecutionException, TimeoutException { return (T)this.e.invokeAny(param1Collection, param1Long, param1TimeUnit); }
  }
  
  static class DelegatedScheduledExecutorService extends DelegatedExecutorService implements ScheduledExecutorService {
    private final ScheduledExecutorService e;
    
    DelegatedScheduledExecutorService(ScheduledExecutorService param1ScheduledExecutorService) {
      super(param1ScheduledExecutorService);
      this.e = param1ScheduledExecutorService;
    }
    
    public ScheduledFuture<?> schedule(Runnable param1Runnable, long param1Long, TimeUnit param1TimeUnit) { return this.e.schedule(param1Runnable, param1Long, param1TimeUnit); }
    
    public <V> ScheduledFuture<V> schedule(Callable<V> param1Callable, long param1Long, TimeUnit param1TimeUnit) { return this.e.schedule(param1Callable, param1Long, param1TimeUnit); }
    
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable param1Runnable, long param1Long1, long param1Long2, TimeUnit param1TimeUnit) { return this.e.scheduleAtFixedRate(param1Runnable, param1Long1, param1Long2, param1TimeUnit); }
    
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable param1Runnable, long param1Long1, long param1Long2, TimeUnit param1TimeUnit) { return this.e.scheduleWithFixedDelay(param1Runnable, param1Long1, param1Long2, param1TimeUnit); }
  }
  
  static class FinalizableDelegatedExecutorService extends DelegatedExecutorService {
    FinalizableDelegatedExecutorService(ExecutorService param1ExecutorService) { super(param1ExecutorService); }
    
    protected void finalize() { shutdown(); }
  }
  
  static final class PrivilegedCallable<T> extends Object implements Callable<T> {
    private final Callable<T> task;
    
    private final AccessControlContext acc;
    
    PrivilegedCallable(Callable<T> param1Callable) {
      this.task = param1Callable;
      this.acc = AccessController.getContext();
    }
    
    public T call() throws Exception {
      try {
        return (T)AccessController.doPrivileged(new PrivilegedExceptionAction<T>() {
              public T run() throws Exception { return (T)Executors.PrivilegedCallable.this.task.call(); }
            },  this.acc);
      } catch (PrivilegedActionException privilegedActionException) {
        throw privilegedActionException.getException();
      } 
    }
  }
  
  static final class PrivilegedCallableUsingCurrentClassLoader<T> extends Object implements Callable<T> {
    private final Callable<T> task;
    
    private final AccessControlContext acc;
    
    private final ClassLoader ccl;
    
    PrivilegedCallableUsingCurrentClassLoader(Callable<T> param1Callable) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
        securityManager.checkPermission(new RuntimePermission("setContextClassLoader"));
      } 
      this.task = param1Callable;
      this.acc = AccessController.getContext();
      this.ccl = Thread.currentThread().getContextClassLoader();
    }
    
    public T call() throws Exception {
      try {
        return (T)AccessController.doPrivileged(new PrivilegedExceptionAction<T>() {
              public T run() throws Exception {
                thread = Thread.currentThread();
                classLoader = thread.getContextClassLoader();
                if (Executors.PrivilegedCallableUsingCurrentClassLoader.this.ccl == classLoader)
                  return (T)Executors.PrivilegedCallableUsingCurrentClassLoader.this.task.call(); 
                thread.setContextClassLoader(Executors.PrivilegedCallableUsingCurrentClassLoader.this.ccl);
                try {
                  object = Executors.PrivilegedCallableUsingCurrentClassLoader.this.task.call();
                  return (T)object;
                } finally {
                  thread.setContextClassLoader(classLoader);
                } 
              }
            },  this.acc);
      } catch (PrivilegedActionException privilegedActionException) {
        throw privilegedActionException.getException();
      } 
    }
  }
  
  static class PrivilegedThreadFactory extends DefaultThreadFactory {
    private final AccessControlContext acc;
    
    private final ClassLoader ccl;
    
    PrivilegedThreadFactory() {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
        securityManager.checkPermission(new RuntimePermission("setContextClassLoader"));
      } 
      this.acc = AccessController.getContext();
      this.ccl = Thread.currentThread().getContextClassLoader();
    }
    
    public Thread newThread(final Runnable r) { return super.newThread(new Runnable() {
            public void run() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                      Thread.currentThread().setContextClassLoader(Executors.PrivilegedThreadFactory.null.this.this$0.ccl);
                      r.run();
                      return null;
                    }
                  },  Executors.PrivilegedThreadFactory.this.acc); }
          }); }
  }
  
  static final class RunnableAdapter<T> extends Object implements Callable<T> {
    final Runnable task;
    
    final T result;
    
    RunnableAdapter(Runnable param1Runnable, T param1T) {
      this.task = param1Runnable;
      this.result = param1T;
    }
    
    public T call() throws Exception {
      this.task.run();
      return (T)this.result;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\Executors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */