package java.util.concurrent;

public interface RunnableFuture<V> extends Runnable, Future<V> {
  void run();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\RunnableFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */