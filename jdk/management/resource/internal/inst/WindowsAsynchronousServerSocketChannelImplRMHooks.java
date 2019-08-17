package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.net.InetSocketAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.WindowsAsynchronousServerSocketChannelImpl")
public class WindowsAsynchronousServerSocketChannelImplRMHooks {
  protected final FileDescriptor fd = null;
  
  @InstrumentationMethod
  void implClose() {
    try {
      implClose();
    } finally {
      ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(this.fd);
      if (resourceIdImpl != null) {
        ResourceRequest resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
        resourceRequest.request(-1L, resourceIdImpl);
      } 
      if (this.localAddress != null) {
        resourceIdImpl = ResourceIdImpl.of(this.localAddress);
        ResourceRequest resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
        resourceRequest.request(-1L, resourceIdImpl);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\WindowsAsynchronousServerSocketChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */