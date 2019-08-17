package sun.nio.fs;

import java.util.concurrent.ExecutionException;
import sun.misc.Unsafe;

abstract class Cancellable implements Runnable {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private final long pollingAddress = unsafe.allocateMemory(4L);
  
  private final Object lock = new Object();
  
  private boolean completed;
  
  private Throwable exception;
  
  protected Cancellable() { unsafe.putIntVolatile(null, this.pollingAddress, 0); }
  
  protected long addressToPollForCancel() { return this.pollingAddress; }
  
  protected int cancelValue() { return Integer.MAX_VALUE; }
  
  final void cancel() {
    synchronized (this.lock) {
      if (!this.completed)
        unsafe.putIntVolatile(null, this.pollingAddress, cancelValue()); 
    } 
  }
  
  private Throwable exception() {
    synchronized (this.lock) {
      return this.exception;
    } 
  }
  
  public final void run() {
    try {
      implRun();
    } catch (Throwable throwable) {
      synchronized (this.lock) {
        this.exception = throwable;
      } 
    } finally {
      synchronized (this.lock) {
        this.completed = true;
        unsafe.freeMemory(this.pollingAddress);
      } 
    } 
  }
  
  abstract void implRun();
  
  static void runInterruptibly(Cancellable paramCancellable) throws ExecutionException {
    Thread thread = new Thread(paramCancellable);
    thread.start();
    boolean bool = false;
    while (thread.isAlive()) {
      try {
        thread.join();
      } catch (InterruptedException interruptedException) {
        bool = true;
        paramCancellable.cancel();
      } 
    } 
    if (bool)
      Thread.currentThread().interrupt(); 
    Throwable throwable = paramCancellable.exception();
    if (throwable != null)
      throw new ExecutionException(throwable); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\Cancellable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */