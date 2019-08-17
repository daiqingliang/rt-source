package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.DatagramSocket")
public final class DatagramSocketRMHooks {
  @InstrumentationMethod
  public InetAddress getLocalAddress() { return getLocalAddress(); }
  
  @InstrumentationMethod
  public boolean isBound() { return isBound(); }
  
  @InstrumentationMethod
  public void bind(SocketAddress paramSocketAddress) throws SocketException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (!isBound()) {
      resourceIdImpl = ResourceIdImpl.of(paramSocketAddress);
      resourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new SocketException("Resource limited: too many open datagram sockets"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        SocketException socketException = new SocketException("Resource limited: too many open datagram sockets");
        socketException.initCause(resourceRequestDeniedException);
        throw socketException;
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
  private void connectInternal(InetAddress paramInetAddress, int paramInt) throws SocketException {
    resourceIdImpl = null;
    resourceRequest = null;
    l = 0L;
    if (!isBound()) {
      resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      resourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new SocketException("Resource limited: too many open datagram sockets"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        SocketException socketException = new SocketException("Resource limited: too many open datagram sockets");
        socketException.initCause(resourceRequestDeniedException);
        throw socketException;
      } 
    } 
    bool = false;
    try {
      connectInternal(paramInetAddress, paramInt);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
  }
  
  @InstrumentationMethod
  public void receive(DatagramPacket paramDatagramPacket) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    resourceRequest = ApproverGroup.DATAGRAM_RECEIVED_GROUP.getApprover(this);
    l = 0L;
    try {
      l = Math.max(resourceRequest.request(1L, resourceIdImpl), 0L);
      if (l < 1L)
        throw new IOException("Resource limited: too many received datagrams"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited: too many received datagrams", resourceRequestDeniedException);
    } 
    int i = Math.max(paramDatagramPacket.getLength(), 0);
    if (i > 0) {
      resourceRequest1 = ApproverGroup.DATAGRAM_READ_GROUP.getApprover(this);
      try {
        l = Math.max(resourceRequest1.request(i, resourceIdImpl), 0L);
        if (l < i) {
          resourceRequest.request(-1L, resourceIdImpl);
          throw new IOException("Resource limited: insufficient bytes approved");
        } 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        resourceRequest.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: insufficient bytes approved", resourceRequestDeniedException);
      } 
      j = 0;
      bool = false;
      try {
        receive(paramDatagramPacket);
        j = paramDatagramPacket.getLength();
        bool = true;
      } finally {
        resourceRequest1.request(-(l - j), resourceIdImpl);
        resourceRequest.request(-(true - bool), resourceIdImpl);
      } 
    } 
  }
  
  @InstrumentationMethod
  public void send(DatagramPacket paramDatagramPacket) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
    ResourceRequest resourceRequest = ApproverGroup.DATAGRAM_SENT_GROUP.getApprover(this);
    l = 0L;
    try {
      l = Math.max(resourceRequest.request(1L, resourceIdImpl), 0L);
      if (l < 1L)
        throw new IOException("Resource limited: too many sent datagrams"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited: too many sent datagrams", resourceRequestDeniedException);
    } 
    int i = Math.max(paramDatagramPacket.getLength(), 0);
    if (i > 0) {
      resourceRequest1 = ApproverGroup.DATAGRAM_WRITE_GROUP.getApprover(this);
      try {
        l = Math.max(resourceRequest1.request(i, resourceIdImpl), 0L);
        if (l < i) {
          resourceRequest.request(-1L, resourceIdImpl);
          throw new IOException("Resource limited: insufficient bytes approved");
        } 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        resourceRequest.request(-1L, resourceIdImpl);
        throw new IOException("Resource limited: too many sent datagrams", resourceRequestDeniedException);
      } 
      j = 0;
      try {
        send(paramDatagramPacket);
        j = paramDatagramPacket.getLength();
      } finally {
        resourceRequest1.request(-(l - j), resourceIdImpl);
      } 
    } 
  }
  
  @InstrumentationMethod
  public boolean isClosed() { return isClosed(); }
  
  @InstrumentationMethod
  public boolean isConnected() { return isConnected(); }
  
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
        ResourceRequest resourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
        resourceRequest.request(-1L, resourceIdImpl);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\DatagramSocketRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */