package java.util.concurrent;

public class ExecutorCompletionService<V> extends Object implements CompletionService<V> {
  private final Executor executor;
  
  private final AbstractExecutorService aes;
  
  private final BlockingQueue<Future<V>> completionQueue;
  
  private RunnableFuture<V> newTaskFor(Callable<V> paramCallable) { return (this.aes == null) ? new FutureTask(paramCallable) : this.aes.newTaskFor(paramCallable); }
  
  private RunnableFuture<V> newTaskFor(Runnable paramRunnable, V paramV) { return (this.aes == null) ? new FutureTask(paramRunnable, paramV) : this.aes.newTaskFor(paramRunnable, paramV); }
  
  public ExecutorCompletionService(Executor paramExecutor) {
    if (paramExecutor == null)
      throw new NullPointerException(); 
    this.executor = paramExecutor;
    this.aes = (paramExecutor instanceof AbstractExecutorService) ? (AbstractExecutorService)paramExecutor : null;
    this.completionQueue = new LinkedBlockingQueue();
  }
  
  public ExecutorCompletionService(Executor paramExecutor, BlockingQueue<Future<V>> paramBlockingQueue) {
    if (paramExecutor == null || paramBlockingQueue == null)
      throw new NullPointerException(); 
    this.executor = paramExecutor;
    this.aes = (paramExecutor instanceof AbstractExecutorService) ? (AbstractExecutorService)paramExecutor : null;
    this.completionQueue = paramBlockingQueue;
  }
  
  public Future<V> submit(Callable<V> paramCallable) {
    if (paramCallable == null)
      throw new NullPointerException(); 
    RunnableFuture runnableFuture = newTaskFor(paramCallable);
    this.executor.execute(new QueueingFuture(runnableFuture));
    return runnableFuture;
  }
  
  public Future<V> submit(Runnable paramRunnable, V paramV) {
    if (paramRunnable == null)
      throw new NullPointerException(); 
    RunnableFuture runnableFuture = newTaskFor(paramRunnable, paramV);
    this.executor.execute(new QueueingFuture(runnableFuture));
    return runnableFuture;
  }
  
  public Future<V> take() throws InterruptedException { return (Future)this.completionQueue.take(); }
  
  public Future<V> poll() throws InterruptedException { return (Future)this.completionQueue.poll(); }
  
  public Future<V> poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException { return (Future)this.completionQueue.poll(paramLong, paramTimeUnit); }
  
  private class QueueingFuture extends FutureTask<Void> {
    private final Future<V> task;
    
    QueueingFuture(RunnableFuture<V> param1RunnableFuture) {
      super(param1RunnableFuture, null);
      this.task = param1RunnableFuture;
    }
    
    protected void done() { ExecutorCompletionService.this.completionQueue.add(this.task); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ExecutorCompletionService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */