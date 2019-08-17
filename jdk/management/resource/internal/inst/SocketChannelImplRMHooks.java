package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.SocketChannelImpl")
public final class SocketChannelImplRMHooks {
  @InstrumentationMethod
  public SocketAddress getLocalAddress() throws IOException { return getLocalAddress(); }
  
  @InstrumentationMethod
  public SocketChannel bind(SocketAddress paramSocketAddress) throws IOException {
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
    SocketChannel socketChannel = null;
    try {
      socketChannel = bind(paramSocketAddress);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return socketChannel;
  }
  
  @InstrumentationMethod
  public boolean connect(SocketAddress paramSocketAddress) throws IOException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (getLocalAddress() == null) {
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
    boolean bool1 = false;
    try {
      bool1 = connect(paramSocketAddress);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return bool1;
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    int k = 0;
    try {
      k = read(paramByteBuffer);
      j = Math.max(k, 0);
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
    return k;
  }
  
  @InstrumentationMethod
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      return read(paramArrayOfByteBuffer, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    for (j = paramInt1; j < paramInt1 + paramInt2; j++)
      i += paramArrayOfByteBuffer[j].remaining(); 
    try {
      l1 = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l1 < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException j) {
      ResourceRequestDeniedException resourceRequestDeniedException;
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    l2 = 0L;
    long l3 = 0L;
    try {
      l3 = read(paramArrayOfByteBuffer, paramInt1, paramInt2);
      l2 = Math.max(l3, 0L);
    } finally {
      resourceRequest.request(-(l1 - l2), resourceIdImpl);
    } 
    return l3;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      j = write(paramByteBuffer);
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
    return j;
  }
  
  @InstrumentationMethod
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      return write(paramArrayOfByteBuffer, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    for (j = paramInt1; j < paramInt1 + paramInt2; j++)
      i += paramArrayOfByteBuffer[j].remaining(); 
    try {
      l1 = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l1 < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException j) {
      ResourceRequestDeniedException resourceRequestDeniedException;
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    l2 = 0L;
    try {
      l2 = write(paramArrayOfByteBuffer, paramInt1, paramInt2);
    } finally {
      resourceRequest.request(-(l1 - l2), resourceIdImpl);
    } 
    return l2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\SocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */