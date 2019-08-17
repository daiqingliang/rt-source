package java.util.concurrent;

public interface CompletionService<V> {
  Future<V> submit(Callable<V> paramCallable);
  
  Future<V> submit(Runnable paramRunnable, V paramV);
  
  Future<V> take() throws InterruptedException;
  
  Future<V> poll() throws InterruptedException;
  
  Future<V> poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CompletionService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */