package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.UnixAsynchronousServerSocketChannelImpl")
public class UnixAsynchronousServerSocketChannelImplRMHooks {
  private static final NativeDispatcher nd = null;
  
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
  void implClose() {
    try {
      implClose();
    } finally {
      if (this.localAddress != null) {
        ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(this.localAddress);
        ResourceRequest resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
        resourceRequest.request(-1L, resourceIdImpl);
      } 
    } 
  }
  
  abstract class NativeDispatcher {
    abstract void close(FileDescriptor param1FileDescriptor) throws IOException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\UnixAsynchronousServerSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */