package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.ProtocolFamily;
import jdk.Exported;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@Exported(false)
@InstrumentationTarget("sun.nio.ch.Net")
public class NetRMHooks {
  @InstrumentationMethod
  static FileDescriptor socket(ProtocolFamily paramProtocolFamily, boolean paramBoolean) throws IOException {
    FileDescriptor fileDescriptor = socket(paramProtocolFamily, paramBoolean);
    resourceIdImpl = ResourceIdImpl.of(fileDescriptor);
    resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fileDescriptor);
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
      resourceRequest.request(-(l1 - l2), resourceIdImpl);
    } 
    return fileDescriptor;
  }
  
  @InstrumentationMethod
  static FileDescriptor serverSocket(boolean paramBoolean) {
    FileDescriptor fileDescriptor = serverSocket(paramBoolean);
    resourceIdImpl = ResourceIdImpl.of(fileDescriptor);
    resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fileDescriptor);
    l1 = 0L;
    l2 = 0L;
    try {
      l1 = resourceRequest.request(1L, resourceIdImpl);
      if (l1 < 1L)
        throw new ResourceRequestDeniedException("Resource limited: too many open file descriptors"); 
      l2 = 1L;
    } finally {
      resourceRequest.request(-(l1 - l2), resourceIdImpl);
    } 
    return fileDescriptor;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\NetRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */