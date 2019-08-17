package java.util.concurrent;

public interface RunnableScheduledFuture<V> extends RunnableFuture<V>, ScheduledFuture<V> {
  boolean isPeriodic();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\RunnableScheduledFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */