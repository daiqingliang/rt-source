package java.util.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractExecutorService implements ExecutorService {
  protected <T> RunnableFuture<T> newTaskFor(Runnable paramRunnable, T paramT) { return new FutureTask(paramRunnable, paramT); }
  
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> paramCallable) { return new FutureTask(paramCallable); }
  
  public Future<?> submit(Runnable paramRunnable) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    RunnableFuture runnableFuture = newTaskFor(paramRunnable, null);
    execute(runnableFuture);
    return runnableFuture;
  }
  
  public <T> Future<T> submit(Runnable paramRunnable, T paramT) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    RunnableFuture runnableFuture = newTaskFor(paramRunnable, paramT);
    execute(runnableFuture);
    return runnableFuture;
  }
  
  public <T> Future<T> submit(Callable<T> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    RunnableFuture runnableFuture = newTaskFor(paramCallable);
    execute(runnableFuture);
    return runnableFuture;
  }
  
  private <T> T doInvokeAny(Collection<? extends Callable<T>> paramCollection, boolean paramBoolean, long paramLong) throws InterruptedException, ExecutionException, TimeoutException {
    ExecutorCompletionService executorCompletionService;
    if (paramCollection == null)
      throw new NullPointerException(); 
    int i = paramCollection.size();
    if (i == 0)
      throw new IllegalArgumentException(); 
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection) throws InterruptedException, ExecutionException {
    try {
      return (T)doInvokeAny(paramCollection, false, 0L);
    } catch (TimeoutException timeoutException) {
      assert false;
      return null;
    } 
  }
  
  public <T> T invokeAny(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException { return (T)doInvokeAny(paramCollection, true, paramTimeUnit.toNanos(paramLong)); }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection) throws InterruptedException {
    if (paramCollection == null)
      throw new NullPointerException(); 
    arrayList = new ArrayList(paramCollection.size());
    bool = false;
    try {
      for (Callable callable : paramCollection) {
        RunnableFuture runnableFuture = newTaskFor(callable);
        arrayList.add(runnableFuture);
        execute(runnableFuture);
      } 
      byte b = 0;
      i = arrayList.size();
      while (b < i) {
        Future future = (Future)arrayList.get(b);
        if (!future.isDone())
          try {
            future.get();
          } catch (CancellationException cancellationException) {
          
          } catch (ExecutionException executionException) {} 
        b++;
      } 
      bool = true;
      return arrayList;
    } finally {
      if (!bool) {
        byte b = 0;
        int i = arrayList.size();
        while (b < i) {
          ((Future)arrayList.get(b)).cancel(true);
          b++;
        } 
      } 
    } 
  }
  
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> paramCollection, long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (paramCollection == null)
      throw new NullPointerException(); 
    long l = paramTimeUnit.toNanos(paramLong);
    arrayList = new ArrayList(paramCollection.size());
    bool = false;
    try {
      for (Callable callable : paramCollection)
        arrayList.add(newTaskFor(callable)); 
      long l1 = System.nanoTime() + l;
      int i = arrayList.size();
      byte b;
      for (b = 0; b < i; b++) {
        execute((Runnable)arrayList.get(b));
        l = l1 - System.nanoTime();
        if (l <= 0L)
          return arrayList; 
      } 
      for (b = 0; b < i; b++) {
        Future future = (Future)arrayList.get(b);
        if (!future.isDone()) {
          if (l <= 0L)
            return arrayList; 
          try {
            future.get(l, TimeUnit.NANOSECONDS);
          } catch (CancellationException cancellationException) {
          
          } catch (ExecutionException executionException) {
          
          } catch (TimeoutException timeoutException) {
            return arrayList;
          } 
          l = l1 - System.nanoTime();
        } 
      } 
      bool = true;
      return arrayList;
    } finally {
      if (!bool) {
        byte b = 0;
        int i = arrayList.size();
        while (b < i) {
          ((Future)arrayList.get(b)).cancel(true);
          b++;
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\AbstractExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */