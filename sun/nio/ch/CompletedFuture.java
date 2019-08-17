package sun.nio.ch;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

final class CompletedFuture<V> extends Object implements Future<V> {
  private final V result;
  
  private final Throwable exc;
  
  private CompletedFuture(V paramV, Throwable paramThrowable) {
    this.result = paramV;
    this.exc = paramThrowable;
  }
  
  static <V> CompletedFuture<V> withResult(V paramV) { return new CompletedFuture(paramV, null); }
  
  static <V> CompletedFuture<V> withFailure(Throwable paramThrowable) {
    if (!(paramThrowable instanceof IOException) && !(paramThrowable instanceof SecurityException))
      paramThrowable = new IOException(paramThrowable); 
    return new CompletedFuture(null, paramThrowable);
  }
  
  static <V> CompletedFuture<V> withResult(V paramV, Throwable paramThrowable) { return (paramThrowable == null) ? withResult(paramV) : withFailure(paramThrowable); }
  
  public V get() throws ExecutionException {
    if (this.exc != null)
      throw new ExecutionException(this.exc); 
    return (V)this.result;
  }
  
  public V get(long paramLong, TimeUnit paramTimeUnit) throws ExecutionException {
    if (paramTimeUnit == null)
      throw new NullPointerException(); 
    if (this.exc != null)
      throw new ExecutionException(this.exc); 
    return (V)this.result;
  }
  
  public boolean isCancelled() { return false; }
  
  public boolean isDone() { return true; }
  
  public boolean cancel(boolean paramBoolean) { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\CompletedFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */