package jdk.management.resource.internal.inst;

import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.nio.channels.spi.AbstractInterruptibleChannel")
public final class AbstractInterruptibleChannelRMHooks {
  private final Object closeLock = new Object();
  
  @InstrumentationMethod
  public final void close() {
    synchronized (this.closeLock) {
      if (!this.open)
        return; 
    } 
    resourceIdImpl = null;
    socketAddress = null;
    if (DatagramChannel.class.isInstance(this)) {
      DatagramChannel datagramChannel = (DatagramChannel)this;
      socketAddress = datagramChannel.getLocalAddress();
      resourceIdImpl = ResourceIdImpl.of(socketAddress);
    } else if (SocketChannel.class.isInstance(this)) {
      SocketChannel socketChannel = (SocketChannel)this;
      socketAddress = socketChannel.getLocalAddress();
      resourceIdImpl = ResourceIdImpl.of(socketAddress);
    } else if (ServerSocketChannel.class.isInstance(this)) {
      ServerSocketChannel serverSocketChannel = (ServerSocketChannel)this;
      socketAddress = serverSocketChannel.getLocalAddress();
      resourceIdImpl = ResourceIdImpl.of(socketAddress);
    } 
    try {
      close();
    } finally {
      if (socketAddress != null)
        if (DatagramChannel.class.isInstance(this)) {
          ResourceRequest resourceRequest = ApproverGroup.DATAGRAM_OPEN_GROUP.getApprover(this);
          resourceRequest.request(-1L, resourceIdImpl);
        } else if (SocketChannel.class.isInstance(this) || ServerSocketChannel.class.isInstance(this)) {
          ResourceRequest resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
          resourceRequest.request(-1L, resourceIdImpl);
        }  
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\AbstractInterruptibleChannelRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */