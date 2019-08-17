package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.CompletionHandlerWrapper;
import jdk.management.resource.internal.FutureWrapper;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import sun.nio.ch.ThreadPool;

@InstrumentationTarget("sun.nio.ch.WindowsAsynchronousFileChannelImpl")
public final class WindowsAsynchronousFileChannelImplRMHooks {
  protected final FileDescriptor fdObj = null;
  
  protected final ReadWriteLock closeLock = new ReentrantReadWriteLock();
  
  @InstrumentationMethod
  public static AsynchronousFileChannel open(FileDescriptor paramFileDescriptor, boolean paramBoolean1, boolean paramBoolean2, ThreadPool paramThreadPool) {
    long l1;
    asynchronousFileChannel = open(paramFileDescriptor, paramBoolean1, paramBoolean2, paramThreadPool);
    JavaIOFileDescriptorAccess javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    try {
      l1 = javaIOFileDescriptorAccess.getHandle(paramFileDescriptor);
      if (l1 == -1L)
        l1 = javaIOFileDescriptorAccess.get(paramFileDescriptor); 
    } catch (UnsupportedOperationException unsupportedOperationException) {
      l1 = javaIOFileDescriptorAccess.get(paramFileDescriptor);
    } 
    resourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor);
    long l2 = 0L;
    bool = false;
    try {
      l2 = resourceRequest.request(1L, resourceIdImpl);
      if (l2 < 1L)
        throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors"); 
      bool = true;
    } finally {
      if (!bool) {
        resourceRequest.request(-1L, resourceIdImpl);
        try {
          asynchronousFileChannel.close();
        } catch (IOException iOException) {}
      } 
    } 
    bool = false;
    resourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(asynchronousFileChannel);
    try {
      l2 = resourceRequest.request(1L, resourceIdImpl);
      if (l2 < 1L) {
        try {
          asynchronousFileChannel.close();
        } catch (IOException iOException) {}
        throw new ResourceRequestDeniedException("Resource limited: too many open files");
      } 
      bool = true;
    } finally {
      if (!bool) {
        resourceRequest.request(-1L, resourceIdImpl);
        try {
          asynchronousFileChannel.close();
        } catch (IOException iOException) {}
      } 
    } 
    return asynchronousFileChannel;
  }
  
  @InstrumentationMethod
  <A> Future<Integer> implRead(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) {
    ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(this.fdObj);
    ResourceRequest resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      if (paramCompletionHandler != null) {
        paramCompletionHandler.failed(resourceRequestDeniedException, paramA);
        return null;
      } 
      CompletableFuture completableFuture = new CompletableFuture();
      completableFuture.completeExceptionally(resourceRequestDeniedException);
      return completableFuture;
    } 
    CompletionHandlerWrapper completionHandlerWrapper = null;
    if (paramCompletionHandler != null)
      completionHandlerWrapper = new CompletionHandlerWrapper(paramCompletionHandler, resourceIdImpl, resourceRequest, l); 
    Future future = implRead(paramByteBuffer, paramLong, paramA, completionHandlerWrapper);
    if (paramCompletionHandler == null)
      if (future.isDone()) {
        int j = 0;
        try {
          j = ((Integer)future.get()).intValue();
        } catch (InterruptedException|java.util.concurrent.ExecutionException interruptedException) {}
        j = Math.max(0, j);
        resourceRequest.request(-(l - j), resourceIdImpl);
      } else {
        future = new FutureWrapper(future, resourceIdImpl, resourceRequest, l);
      }  
    return future;
  }
  
  @InstrumentationMethod
  <A> Future<Integer> implWrite(ByteBuffer paramByteBuffer, long paramLong, A paramA, CompletionHandler<Integer, ? super A> paramCompletionHandler) {
    ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(this.fdObj);
    ResourceRequest resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    long l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new ResourceRequestDeniedException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      if (paramCompletionHandler != null) {
        paramCompletionHandler.failed(resourceRequestDeniedException, paramA);
        return null;
      } 
      CompletableFuture completableFuture = new CompletableFuture();
      completableFuture.completeExceptionally(resourceRequestDeniedException);
      return completableFuture;
    } 
    CompletionHandlerWrapper completionHandlerWrapper = null;
    if (paramCompletionHandler != null)
      completionHandlerWrapper = new CompletionHandlerWrapper(paramCompletionHandler, resourceIdImpl, resourceRequest, l); 
    Future future = implWrite(paramByteBuffer, paramLong, paramA, completionHandlerWrapper);
    if (paramCompletionHandler == null)
      if (future.isDone()) {
        int j = 0;
        try {
          j = ((Integer)future.get()).intValue();
        } catch (InterruptedException|java.util.concurrent.ExecutionException interruptedException) {}
        j = Math.max(0, j);
        resourceRequest.request(-(l - j), resourceIdImpl);
      } else {
        future = new FutureWrapper(future, resourceIdImpl, resourceRequest, l);
      }  
    return future;
  }
  
  @InstrumentationMethod
  public void close() {
    this.closeLock.writeLock().lock();
    try {
      if (this.closed)
        return; 
    } finally {
      this.closeLock.writeLock().unlock();
    } 
    try {
      close();
    } finally {
      JavaIOFileDescriptorAccess javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
      long l = javaIOFileDescriptorAccess.getHandle(this.fdObj);
      if (l == -1L)
        l = javaIOFileDescriptorAccess.get(this.fdObj); 
      ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(Long.valueOf(l));
      ResourceRequest resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fdObj);
      resourceRequest.request(-1L, resourceIdImpl);
      resourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      resourceRequest.request(-1L, resourceIdImpl);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\WindowsAsynchronousFileChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */