package jdk.management.resource.internal;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;

public class FutureWrapper<T> extends Object implements Future<T> {
  private final Future<T> future;
  
  private final ResourceId id;
  
  private final ResourceRequest ra;
  
  private final long approved;
  
  private Object clientChannel;
  
  private boolean isInvoked = false;
  
  public FutureWrapper(Future<T> paramFuture, ResourceId paramResourceId, ResourceRequest paramResourceRequest, long paramLong) {
    this.future = paramFuture;
    this.id = paramResourceId;
    this.ra = paramResourceRequest;
    this.approved = paramLong;
  }
  
  public FutureWrapper(Future<T> paramFuture) { this(paramFuture, null, null, 0L); }
  
  public FutureWrapper(Future<T> paramFuture, Object paramObject) {
    this(paramFuture, null, null, 0L);
    this.clientChannel = paramObject;
  }
  
  public boolean cancel(boolean paramBoolean) { return this.future.cancel(paramBoolean); }
  
  public boolean isCancelled() { return this.future.isCancelled(); }
  
  public boolean isDone() { return this.future.isDone(); }
  
  public T get() throws InterruptedException, ExecutionException {
    Object object = this.future.get();
    processResult(object);
    return (T)object;
  }
  
  public T get(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException, ExecutionException, TimeoutException {
    Object object = this.future.get(paramLong, paramTimeUnit);
    processResult(object);
    return (T)object;
  }
  
  private void processResult(T paramT) {
    if (this.isInvoked)
      return; 
    this.isInvoked = true;
    if (paramT instanceof Number) {
      int i = ((Number)paramT).intValue();
      if (i == -1) {
        this.ra.request(-this.approved, this.id);
      } else {
        this.ra.request(-(this.approved - i), this.id);
      } 
    } else if (paramT instanceof AsynchronousSocketChannel || this.clientChannel != null) {
      AsynchronousSocketChannel asynchronousSocketChannel = (AsynchronousSocketChannel)paramT;
      if (paramT != null) {
        asynchronousSocketChannel = (AsynchronousSocketChannel)paramT;
      } else {
        asynchronousSocketChannel = (AsynchronousSocketChannel)this.clientChannel;
      } 
      ResourceIdImpl resourceIdImpl = null;
      try {
        resourceIdImpl = ResourceIdImpl.of(asynchronousSocketChannel.getLocalAddress());
      } catch (IOException iOException) {}
      ResourceRequest resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(asynchronousSocketChannel);
      long l = 0L;
      ResourceRequestDeniedException resourceRequestDeniedException = null;
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          resourceRequestDeniedException = new ResourceRequestDeniedException("Resource limited: too many open server socket channels"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException1) {
        resourceRequestDeniedException = resourceRequestDeniedException1;
      } 
      if (resourceRequestDeniedException == null) {
        resourceRequest.request(-(l - 1L), resourceIdImpl);
      } else {
        resourceRequest.request(-l, resourceIdImpl);
        try {
          asynchronousSocketChannel.close();
        } catch (IOException iOException) {}
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\FutureWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */