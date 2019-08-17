package com.sun.xml.internal.ws.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CompletedFuture<T> extends Object implements Future<T> {
  private final T v;
  
  private final Throwable re;
  
  public CompletedFuture(T paramT, Throwable paramThrowable) {
    this.v = paramT;
    this.re = paramThrowable;
  }
  
  public boolean cancel(boolean paramBoolean) { return false; }
  
  public boolean isCancelled() { return false; }
  
  public boolean isDone() { return true; }
  
  public T get() throws ExecutionException {
    if (this.re != null)
      throw new ExecutionException(this.re); 
    return (T)this.v;
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit) throws ExecutionException { return (T)get(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\CompletedFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */