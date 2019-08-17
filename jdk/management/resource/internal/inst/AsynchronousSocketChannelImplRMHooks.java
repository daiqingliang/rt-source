package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.AsynchronousSocketChannelImpl")
public class AsynchronousSocketChannelImplRMHooks {
  @InstrumentationMethod
  public final SocketAddress getLocalAddress() throws IOException { return getLocalAddress(); }
  
  @InstrumentationMethod
  public final AsynchronousSocketChannel bind(SocketAddress paramSocketAddress) throws IOException {
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
    AsynchronousSocketChannel asynchronousSocketChannel = null;
    try {
      asynchronousSocketChannel = bind(paramSocketAddress);
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return asynchronousSocketChannel;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\AsynchronousSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */