package jdk.management.resource.internal;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;

public class CompletionHandlerWrapper<V, A> extends Object implements CompletionHandler<V, A> {
  private final CompletionHandler<V, ? super A> handler;
  
  private final ResourceId id;
  
  private final ResourceRequest ra;
  
  private final long approved;
  
  private Object clientChannel;
  
  public CompletionHandlerWrapper(CompletionHandler<V, ? super A> paramCompletionHandler, ResourceId paramResourceId, ResourceRequest paramResourceRequest, long paramLong) {
    this.handler = paramCompletionHandler;
    this.id = paramResourceId;
    this.ra = paramResourceRequest;
    this.approved = paramLong;
  }
  
  public CompletionHandlerWrapper(CompletionHandler<V, ? super A> paramCompletionHandler) { this(paramCompletionHandler, null, null, 0L); }
  
  public CompletionHandlerWrapper(CompletionHandler<V, ? super A> paramCompletionHandler, Object paramObject) {
    this(paramCompletionHandler, null, null, 0L);
    this.clientChannel = paramObject;
  }
  
  public void completed(V paramV, A paramA) {
    if (paramV instanceof Number) {
      int i = ((Number)paramV).intValue();
      if (i == -1) {
        this.ra.request(-this.approved, this.id);
      } else {
        this.ra.request(-(this.approved - i), this.id);
      } 
    } else if (paramV instanceof AsynchronousSocketChannel || this.clientChannel != null) {
      AsynchronousSocketChannel asynchronousSocketChannel;
      if (paramV != null) {
        asynchronousSocketChannel = (AsynchronousSocketChannel)paramV;
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
        if (this.handler != null)
          this.handler.failed(resourceRequestDeniedException, paramA); 
        return;
      } 
    } 
    if (this.handler != null)
      this.handler.completed(paramV, paramA); 
  }
  
  public void failed(Throwable paramThrowable, A paramA) {
    if (this.ra != null && this.id != null)
      this.ra.request(-this.approved, this.id); 
    if (this.handler != null)
      this.handler.failed(paramThrowable, paramA); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\CompletionHandlerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */