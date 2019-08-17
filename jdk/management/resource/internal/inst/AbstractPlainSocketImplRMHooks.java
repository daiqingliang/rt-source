package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.AbstractPlainSocketImpl")
abstract class AbstractPlainSocketImplRMHooks {
  protected FileDescriptor fd;
  
  abstract void socketClose0(boolean paramBoolean);
  
  @InstrumentationMethod
  protected void create(boolean paramBoolean) {
    create(paramBoolean);
    ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(this.fd);
    ResourceRequest resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
    long l = 0L;
    try {
      l = resourceRequest.request(1L, resourceIdImpl);
      if (l < 1L) {
        socketClose0(false);
        throw new IOException("Resource limited: too many open file descriptors");
      } 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      resourceRequest.request(-l, resourceIdImpl);
      socketClose0(false);
      throw new IOException("Resource limited: too many open file descriptors", resourceRequestDeniedException);
    } 
    resourceRequest.request(-(l - 1L), resourceIdImpl);
  }
  
  @InstrumentationMethod
  protected void close() {
    resourceIdImpl = null;
    resourceRequest = null;
    byte b = -1;
    if (this.fd != null) {
      resourceIdImpl = ResourceIdImpl.of(this.fd);
      resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
    } 
    try {
      close();
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-1L, resourceIdImpl); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\AbstractPlainSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */