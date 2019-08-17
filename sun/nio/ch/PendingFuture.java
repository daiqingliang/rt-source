package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

final class PendingFuture<V, A> extends Object implements Future<V> {
  private static final CancellationException CANCELLED = new CancellationException();
  
  private final AsynchronousChannel channel;
  
  private final CompletionHandler<V, ? super A> handler;
  
  private final A attachment;
  
  private CountDownLatch latch;
  
  private Future<?> timeoutTask;
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA, Object paramObject) {
    this.channel = paramAsynchronousChannel;
    this.handler = paramCompletionHandler;
    this.attachment = paramA;
    this.context = paramObject;
  }
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel, CompletionHandler<V, ? super A> paramCompletionHandler, A paramA) {
    this.channel = paramAsynchronousChannel;
    this.handler = paramCompletionHandler;
    this.attachment = paramA;
  }
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel) { this(paramAsynchronousChannel, null, null); }
  
  PendingFuture(AsynchronousChannel paramAsynchronousChannel, Object paramObject) { this(paramAsynchronousChannel, null, null, paramObject); }
  
  AsynchronousChannel channel() { return this.channel; }
  
  CompletionHandler<V, ? super A> handler() { return this.handler; }
  
  A attachment() { return (A)this.attachment; }
  
  void setContext(Object paramObject) { this.context = paramObject; }
  
  Object getContext() { return this.context; }
  
  void setTimeoutTask(Future<?> paramFuture) {
    synchronized (this) {
      if (this.haveResult) {
        paramFuture.cancel(false);
      } else {
        this.timeoutTask = paramFuture;
      } 
    } 
  }
  
  private boolean prepareForWait() {
    synchronized (this) {
      if (this.haveResult)
        return false; 
      if (this.latch == null)
        this.latch = new CountDownLatch(1); 
      return true;
    } 
  }
  
  void setResult(V paramV) {
    synchronized (this) {
      if (this.haveResult)
        return; 
      this.result = paramV;
      this.haveResult = true;
      if (this.timeoutTask != null)
        this.timeoutTask.cancel(false); 
      if (this.latch != null)
        this.latch.countDown(); 
    } 
  }
  
  void setFailure(Throwable paramThrowable) {
    if (!(paramThrowable instanceof IOException) && !(paramThrowable instanceof SecurityException))
      paramThrowable = new IOException(paramThrowable); 
    synchronized (this) {
      if (this.haveResult)
        return; 
      this.exc = paramThrowable;
      this.haveResult = true;
      if (this.timeoutTask != null)
        this.timeoutTask.cancel(false); 
      if (this.latch != null)
        this.latch.countDown(); 
    } 
  }
  
  void setResult(V paramV, Throwable paramThrowable) {
    if (paramThrowable == null) {
      setResult(paramV);
    } else {
      setFailure(paramThrowable);
    } 
  }
  
  public V get() throws ExecutionException, InterruptedException {
    if (!this.haveResult) {
      boolean bool = prepareForWait();
      if (bool)
        this.latch.await(); 
    } 
    if (this.exc != null) {
      if (this.exc == CANCELLED)
        throw new CancellationException(); 
      throw new ExecutionException(this.exc);
    } 
    return (V)this.result;
  }
  
  public V get(long paramLong, TimeUnit paramTimeUnit) throws ExecutionException, InterruptedException, TimeoutException {
    if (!this.haveResult) {
      boolean bool = prepareForWait();
      if (bool && !this.latch.await(paramLong, paramTimeUnit))
        throw new TimeoutException(); 
    } 
    if (this.exc != null) {
      if (this.exc == CANCELLED)
        throw new CancellationException(); 
      throw new ExecutionException(this.exc);
    } 
    return (V)this.result;
  }
  
  Throwable exception() { return (this.exc != CANCELLED) ? this.exc : null; }
  
  V value() throws ExecutionException, InterruptedException { return (V)this.result; }
  
  public boolean isCancelled() { return (this.exc == CANCELLED); }
  
  public boolean isDone() { return this.haveResult; }
  
  public boolean cancel(boolean paramBoolean) {
    synchronized (this) {
      if (this.haveResult)
        return false; 
      if (channel() instanceof Cancellable)
        ((Cancellable)channel()).onCancel(this); 
      this.exc = CANCELLED;
      this.haveResult = true;
      if (this.timeoutTask != null)
        this.timeoutTask.cancel(false); 
    } 
    if (paramBoolean)
      try {
        channel().close();
      } catch (IOException iOException) {} 
    if (this.latch != null)
      this.latch.countDown(); 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\PendingFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */