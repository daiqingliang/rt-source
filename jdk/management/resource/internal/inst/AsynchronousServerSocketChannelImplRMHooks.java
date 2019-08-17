package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.AsynchronousServerSocketChannelImpl")
public class AsynchronousServerSocketChannelImplRMHooks {
  @InstrumentationMethod
  public final SocketAddress getLocalAddress() throws IOException { return getLocalAddress(); }
  
  @InstrumentationMethod
  public final AsynchronousServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (getLocalAddress() == null) {
      resourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new IOException("Resource limited: too many open socket channels"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        throw new IOException("Resource limited: too many open socket channels", resourceRequestDeniedException);
      } 
    } 
    bool = false;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel = null;
    try {
      asynchronousServerSocketChannel = bind(paramSocketAddress, paramInt);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return asynchronousServerSocketChannel;
  }
  
  @InstrumentationMethod
  public final Future<AsynchronousSocketChannel> accept() {
    Future future = accept();
    if (future.isDone()) {
      AsynchronousSocketChannel asynchronousSocketChannel;
      try {
        asynchronousSocketChannel = (AsynchronousSocketChannel)future.get();
      } catch (InterruptedException interruptedException) {
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(interruptedException);
        return completableFuture;
      } catch (ExecutionException executionException) {
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(executionException.getCause());
        return completableFuture;
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
        CompletableFuture completableFuture = new CompletableFuture();
        completableFuture.completeExceptionally(resourceRequestDeniedException);
        return completableFuture;
      } 
    } else {
      FutureWrapper futureWrapper = new FutureWrapper(future);
      future = futureWrapper;
    } 
    return future;
  }
  
  @InstrumentationMethod
  public final <A> void accept(A paramA, CompletionHandler<AsynchronousSocketChannel, ? super A> paramCompletionHandler) {
    if (paramCompletionHandler == null)
      throw new NullPointerException("'handler' is null"); 
    paramCompletionHandler = new CompletionHandlerWrapper<AsynchronousSocketChannel, ? super A>(paramCompletionHandler);
    accept(paramA, paramCompletionHandler);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\AsynchronousServerSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */