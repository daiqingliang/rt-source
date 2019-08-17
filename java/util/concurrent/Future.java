package java.util.concurrent;

public interface Future<V> {
  boolean cancel(boolean paramBoolean);
  
  boolean isCancelled();
  
  boolean isDone();
  
  V get() throws InterruptedException, ExecutionException;
  
  V get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\Future.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */