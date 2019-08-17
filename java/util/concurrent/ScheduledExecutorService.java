package java.util.concurrent;

public interface ScheduledExecutorService extends ExecutorService {
  ScheduledFuture<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);
  
  <V> ScheduledFuture<V> schedule(Callable<V> paramCallable, long paramLong, TimeUnit paramTimeUnit);
  
  ScheduledFuture<?> scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
  
  ScheduledFuture<?> scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ScheduledExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */