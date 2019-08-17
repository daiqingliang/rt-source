package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.ServerSocketChannelImpl")
public final class ServerSocketChannelImplRMHooks {
  private static NativeDispatcher nd;
  
  @InstrumentationMethod
  public SocketAddress getLocalAddress() throws IOException { return getLocalAddress(); }
  
  @InstrumentationMethod
  public SocketChannel accept() throws IOException {
    l1 = 0L;
    l2 = 0L;
    SocketChannel socketChannel = null;
    resourceIdImpl = null;
    resourceRequest = null;
    try {
      socketChannel = accept();
      if (socketChannel != null) {
        resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(socketChannel);
        resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
        try {
          l1 = resourceRequest.request(1L, resourceIdImpl);
          if (l1 < 1L) {
            try {
              socketChannel.close();
            } catch (IOException iOException) {}
            throw new IOException("Resource limited: too many open socket channels");
          } 
        } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
          try {
            socketChannel.close();
          } catch (IOException iOException) {}
          throw new IOException("Resource limited: too many open socket channels", resourceRequestDeniedException);
        } 
        l2 = 1L;
      } 
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l1 - l2), resourceIdImpl); 
    } 
    return socketChannel;
  }
  
  public final void close() {}
  
  @InstrumentationMethod
  private int accept(FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, InetSocketAddress[] paramArrayOfInetSocketAddress) throws IOException {
    int i = accept(paramFileDescriptor1, paramFileDescriptor2, paramArrayOfInetSocketAddress);
    resourceIdImpl = ResourceIdImpl.of(paramFileDescriptor2);
    if (resourceIdImpl != null) {
      resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor2);
      l1 = 0L;
      l2 = 0L;
      try {
        try {
          l1 = resourceRequest.request(1L, resourceIdImpl);
          if (l1 < 1L)
            throw new IOException("Resource limited: too many open file descriptors"); 
        } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
          throw new IOException("Resource limited: too many open file descriptors", resourceRequestDeniedException);
        } 
        l2 = 1L;
      } finally {
        if (l2 == 0L) {
          try {
            nd.close(paramFileDescriptor2);
          } catch (IOException iOException) {}
        } else {
          resourceRequest.request(-(l1 - 1L), resourceIdImpl);
        } 
      } 
    } 
    return i;
  }
  
  @InstrumentationMethod
  public ServerSocketChannel bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (getLocalAddress() == null) {
      resourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      l = resourceRequest.request(1L, resourceIdImpl);
      if (l < 1L)
        throw new ResourceRequestDeniedException("Resource limited: too many open socket channels"); 
    } 
    bool = false;
    ServerSocketChannel serverSocketChannel = null;
    try {
      serverSocketChannel = bind(paramSocketAddress, paramInt);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return serverSocketChannel;
  }
  
  abstract class NativeDispatcher {
    abstract void close(FileDescriptor param1FileDescriptor) throws IOException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\ServerSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */