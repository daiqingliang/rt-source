package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketOptions;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.net.Socket")
@TypeMapping(from = "jdk.management.resource.internal.inst.SocketRMHooks$SocketImpl", to = "java.net.SocketImpl")
public final class SocketRMHooks {
  private boolean created = false;
  
  SocketImpl impl;
  
  public InetAddress getLocalAddress() { return getLocalAddress(); }
  
  @InstrumentationMethod
  public void bind(SocketAddress paramSocketAddress) throws IOException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (!isBound()) {
      resourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new IOException("Resource limited: too many open sockets"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        throw new IOException("Resource limited: too many open sockets", resourceRequestDeniedException);
      } 
    } 
    bool = false;
    try {
      bind(paramSocketAddress);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
  }
  
  @InstrumentationMethod
  public boolean isBound() { return isBound(); }
  
  @InstrumentationMethod
  public void connect(SocketAddress paramSocketAddress, int paramInt) throws IOException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (!isBound()) {
      resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new IOException("Resource limited: too many open sockets"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        throw new IOException("Resource limited: too many open sockets", resourceRequestDeniedException);
      } 
    } 
    bool = false;
    try {
      connect(paramSocketAddress, paramInt);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
  }
  
  @InstrumentationMethod
  final void postAccept() {
    long l1;
    postAccept();
    FileDescriptor fileDescriptor = this.impl.getFileDescriptor();
    JavaIOFileDescriptorAccess javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    try {
      l1 = javaIOFileDescriptorAccess.getHandle(fileDescriptor);
      if (l1 == -1L)
        l1 = javaIOFileDescriptorAccess.get(fileDescriptor); 
    } catch (UnsupportedOperationException unsupportedOperationException) {
      l1 = javaIOFileDescriptorAccess.get(fileDescriptor);
    } 
    resourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fileDescriptor);
    l2 = 0L;
    bool = false;
    try {
      l2 = resourceRequest.request(1L, resourceIdImpl);
      if (l2 < 1L)
        throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors"); 
      bool = true;
    } finally {
      if (!bool) {
        try {
          close();
        } catch (IOException iOException) {}
        resourceRequest.request(-Math.max(0L, l2 - 1L), resourceIdImpl);
      } 
    } 
  }
  
  @InstrumentationMethod
  public boolean isClosed() { return isClosed(); }
  
  @InstrumentationMethod
  public void close() {
    if (isClosed())
      return; 
    bool = isBound();
    inetAddress = getLocalAddress();
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
  
  abstract class SocketImpl implements SocketOptions {
    protected FileDescriptor fd;
    
    protected FileDescriptor getFileDescriptor() { return this.fd; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\SocketRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */