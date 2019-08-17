package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.Exported;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@Exported(false)
@InstrumentationTarget("sun.nio.ch.DatagramDispatcher")
public class DatagramDispatcherRMHooks {
  @InstrumentationMethod
  void close(FileDescriptor paramFileDescriptor) throws IOException {
    l = 0L;
    resourceIdImpl = ResourceIdImpl.of(paramFileDescriptor);
    try {
      close(paramFileDescriptor);
      l = 1L;
    } finally {
      ResourceRequest resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor);
      resourceRequest.request(-l, resourceIdImpl);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\DatagramDispatcherRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */