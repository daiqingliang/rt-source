package java.util.concurrent;

import java.util.Collection;
import java.util.List;

public interface ExecutorService extends Executor {
  void shutdown();
  
  List<Runnable> shutdownNow();
  
  boolean isShutdown();
  
  boolean isTerminated();
  
  boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  <T> Future<T> submit(Callable<T> paramCallable);
  
  <T> Future<T> submit(Runnable paramRunnable, T paramT);
  
  Future<?> submit(Runnable paramRunnable);
  
  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection) throws InterruptedException;
  
  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
  
  <T> T invokeAny(Collection<? extends Callable<T>> paramCollection) throws InterruptedException, ExecutionException;
  
  <T> T invokeAny(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */