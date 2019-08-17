package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.ServerSocket")
final class ServerSocketRMHooks {
  private Object closeLock;
  
  @InstrumentationMethod
  public Socket accept() throws IOException {
    l1 = 0L;
    l2 = 0L;
    Socket socket = null;
    resourceIdImpl = null;
    resourceRequest = null;
    try {
      socket = accept();
      l2 = 1L;
      resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(socket);
      resourceIdImpl = ResourceIdImpl.of(socket.getLocalAddress());
      try {
        l1 = resourceRequest.request(1L, resourceIdImpl);
        if (l1 < 1L) {
          try {
            socket.close();
          } catch (IOException iOException) {}
          throw new IOException("Resource limited: too many open sockets");
        } 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        try {
          socket.close();
        } catch (IOException iOException) {}
        throw new IOException("Resource limited: too many open sockets", resourceRequestDeniedException);
      } 
      l2 = 1L;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l1 - l2), resourceIdImpl); 
    } 
    return socket;
  }
  
  @InstrumentationMethod
  public InetAddress getInetAddress() { return getInetAddress(); }
  
  @InstrumentationMethod
  public void bind(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (!isBound()) {
      resourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      l = resourceRequest.request(1L, resourceIdImpl);
      if (l < 1L)
        throw new ResourceRequestDeniedException("Resource limited: too many open sockets"); 
    } 
    bool = false;
    try {
      bind(paramSocketAddress, paramInt);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
  }
  
  @InstrumentationMethod
  public boolean isBound() { return isBound(); }
  
  @InstrumentationMethod
  public boolean isClosed() { return isClosed(); }
  
  @InstrumentationMethod
  public void close() {
    synchronized (this.closeLock) {
      if (isClosed())
        return; 
    } 
    bool = isBound();
    inetAddress = getInetAddress();
    try {
      close();
    } finally {
      if (bool) {
        ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(inetAddress);
        ResourceRequest resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
        resourceRequest.request(-1L, resourceIdImpl);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\ServerSocketRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */