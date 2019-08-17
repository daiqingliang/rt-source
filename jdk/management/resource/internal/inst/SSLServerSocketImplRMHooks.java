package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.Socket;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.SSLServerSocketImpl")
final class SSLServerSocketImplRMHooks {
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\SSLServerSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */