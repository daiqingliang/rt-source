package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.DatagramChannelImpl")
public final class DatagramChannelImplRMHooks {
  @InstrumentationMethod
  public SocketAddress getLocalAddress() throws IOException { return getLocalAddress(); }
  
  @InstrumentationMethod
  public boolean isConnected() { return isConnected(); }
  
  @InstrumentationMethod
  public DatagramChannel bind(SocketAddress paramSocketAddress) throws IOException {
    DatagramChannel datagramChannel;
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (getLocalAddress() == null) {
      resourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      resourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new IOException("Resource limited: too many open datagram channels"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        throw new IOException("Resource limited: too many open datagram channels", resourceRequestDeniedException);
      } 
    } 
    bool = false;
    try {
      datagramChannel = bind(paramSocketAddress);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return datagramChannel;
  }
  
  @InstrumentationMethod
  public DatagramChannel connect(SocketAddress paramSocketAddress) throws IOException {
    DatagramChannel datagramChannel;
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (getLocalAddress() == null) {
      resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      resourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new IOException("Resource limited: too many open datagram channels"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        throw new IOException("Resource limited: too many open datagram channels", resourceRequestDeniedException);
      } 
    } 
    bool = false;
    try {
      datagramChannel = connect(paramSocketAddress);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return datagramChannel;
  }
  
  @InstrumentationMethod
  public SocketAddress receive(ByteBuffer paramByteBuffer) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest1 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
    l = 0L;
    try {
      l = Math.max(resourceRequest1.request(1L, resourceIdImpl), 0L);
      if (l < 1L)
        throw new IOException("Resource limited: too many received datagrams"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited: too many received datagrams", resourceRequestDeniedException);
    } 
    resourceRequest1.request(-(l - 1L), resourceIdImpl);
    int i = paramByteBuffer.remaining();
    resourceRequest2 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
    try {
      l = Math.max(resourceRequest2.request(i, resourceIdImpl), 0L);
      if (l < i) {
        resourceRequest1.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      } 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      resourceRequest1.request(-1L, resourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", resourceRequestDeniedException);
    } 
    j = 0;
    socketAddress = null;
    try {
      int k = paramByteBuffer.position();
      socketAddress = receive(paramByteBuffer);
      j = paramByteBuffer.position() - k;
    } finally {
      if (socketAddress == null)
        resourceRequest1.request(-1L, resourceIdImpl); 
      resourceRequest2.request(-(l - j), resourceIdImpl);
    } 
    return socketAddress;
  }
  
  @InstrumentationMethod
  public int send(ByteBuffer paramByteBuffer, SocketAddress paramSocketAddress) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    l = 0L;
    if (getLocalAddress() == null) {
      ResourceRequest resourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new IOException("Resource limited: too many open datagram channels"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        throw new IOException("Resource limited: too many open datagram channels", resourceRequestDeniedException);
      } 
      resourceRequest.request(-(l - 1L), resourceIdImpl);
    } 
    if (isConnected()) {
      i = send(paramByteBuffer, paramSocketAddress);
    } else {
      resourceRequest1 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
      l = 0L;
      try {
        l = Math.max(resourceRequest1.request(1L, resourceIdImpl), 0L);
        if (l < 1L)
          throw new IOException("Resource limited: too many sent datagrams"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        throw new IOException("Resource limited: too many sent datagrams", resourceRequestDeniedException);
      } 
      resourceRequest1.request(-(l - 1L), resourceIdImpl);
      int j = paramByteBuffer.remaining();
      resourceRequest2 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
      try {
        l = Math.max(resourceRequest2.request(j, resourceIdImpl), 0L);
        if (l < j) {
          resourceRequest1.request(-1L, resourceIdImpl);
          throw new IOException("Resource limited: insufficient bytes approved");
        } 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        resourceRequest1.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved", resourceRequestDeniedException);
      } 
      i = 0;
      try {
        i = send(paramByteBuffer, paramSocketAddress);
      } finally {
        if (i == 0)
          resourceRequest1.request(-1L, resourceIdImpl); 
        resourceRequest2.request(-(l - i), resourceIdImpl);
      } 
    } 
    return i;
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest1 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
    l = 0L;
    try {
      l = Math.max(resourceRequest1.request(1L, resourceIdImpl), 0L);
      if (l < 1L)
        throw new IOException("Resource limited: too many received datagrams"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited: too many received datagrams", resourceRequestDeniedException);
    } 
    resourceRequest1.request(-(l - 1L), resourceIdImpl);
    resourceRequest2 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest2.request(i, resourceIdImpl), 0L);
      if (l < i) {
        resourceRequest1.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      } 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      resourceRequest1.request(-1L, resourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", resourceRequestDeniedException);
    } 
    j = 0;
    int k = 0;
    try {
      k = read(paramByteBuffer);
      j = Math.max(k, 0);
    } finally {
      resourceRequest2.request(-(l - j), resourceIdImpl);
      if (j == 0)
        resourceRequest1.request(-1L, resourceIdImpl); 
    } 
    return k;
  }
  
  @InstrumentationMethod
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      return read(paramArrayOfByteBuffer, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest1 = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
    l1 = 0L;
    try {
      l1 = Math.max(resourceRequest1.request(1L, resourceIdImpl), 0L);
      if (l1 < 1L)
        throw new IOException("Resource limited: too many received datagrams"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited: too many received datagrams", resourceRequestDeniedException);
    } 
    resourceRequest1.request(-(l1 - 1L), resourceIdImpl);
    resourceRequest2 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    for (j = paramInt1; j < paramInt1 + paramInt2; j++)
      i += paramArrayOfByteBuffer[j].remaining(); 
    try {
      l1 = Math.max(resourceRequest2.request(i, resourceIdImpl), 0L);
      if (l1 < i) {
        resourceRequest1.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      } 
    } catch (ResourceRequestDeniedException j) {
      ResourceRequestDeniedException resourceRequestDeniedException;
      resourceRequest1.request(-1L, resourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", resourceRequestDeniedException);
    } 
    l2 = 0L;
    long l3 = 0L;
    try {
      l3 = read(paramArrayOfByteBuffer, paramInt1, paramInt2);
      l2 = Math.max(l3, 0L);
    } finally {
      resourceRequest2.request(-(l1 - l2), resourceIdImpl);
      if (l2 == 0L)
        resourceRequest1.request(-1L, resourceIdImpl); 
    } 
    return l3;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest1 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
    l = 0L;
    try {
      l = Math.max(resourceRequest1.request(1L, resourceIdImpl), 0L);
      if (l < 1L)
        throw new IOException("Resource limited: too many sent datagrams"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited: too many sent datagrams", resourceRequestDeniedException);
    } 
    resourceRequest1.request(-(l - 1L), resourceIdImpl);
    resourceRequest2 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest2.request(i, resourceIdImpl), 0L);
      if (l < i) {
        resourceRequest1.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      } 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      resourceRequest1.request(-1L, resourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      j = write(paramByteBuffer);
    } finally {
      resourceRequest2.request(-(l - j), resourceIdImpl);
      if (j == 0)
        resourceRequest1.request(-1L, resourceIdImpl); 
    } 
    return j;
  }
  
  @InstrumentationMethod
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      return write(paramArrayOfByteBuffer, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest1 = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
    l1 = 0L;
    try {
      l1 = Math.max(resourceRequest1.request(1L, resourceIdImpl), 0L);
      if (l1 < 1L)
        throw new IOException("Resource limited: too many sent datagrams"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited: too many sent datagrams", resourceRequestDeniedException);
    } 
    resourceRequest1.request(-(l1 - 1L), resourceIdImpl);
    resourceRequest2 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    for (j = paramInt1; j < paramInt1 + paramInt2; j++)
      i += paramArrayOfByteBuffer[j].remaining(); 
    try {
      l1 = Math.max(resourceRequest2.request(i, resourceIdImpl), 0L);
      if (l1 < i) {
        resourceRequest1.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved");
      } 
    } catch (ResourceRequestDeniedException j) {
      ResourceRequestDeniedException resourceRequestDeniedException;
      resourceRequest1.request(-1L, resourceIdImpl);
      throw new IOException("Resource limited: insufficient bytes approved", resourceRequestDeniedException);
    } 
    l2 = 0L;
    try {
      l2 = write(paramArrayOfByteBuffer, paramInt1, paramInt2);
    } finally {
      resourceRequest2.request(-(l1 - l2), resourceIdImpl);
      if (l2 == 0L)
        resourceRequest1.request(-1L, resourceIdImpl); 
    } 
    return l2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\DatagramChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */