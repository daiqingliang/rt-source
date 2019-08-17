package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.net.SocketException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.net.AbstractPlainDatagramSocketImpl")
public class AbstractPlainDatagramSocketImplRMHooks {
  protected FileDescriptor fd;
  
  @InstrumentationMethod
  protected void create() {
    long l1;
    create();
    JavaIOFileDescriptorAccess javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    try {
      l1 = javaIOFileDescriptorAccess.getHandle(this.fd);
      if (l1 == -1L)
        l1 = javaIOFileDescriptorAccess.get(this.fd); 
    } catch (UnsupportedOperationException unsupportedOperationException) {
      l1 = javaIOFileDescriptorAccess.get(this.fd);
    } 
    ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    ResourceRequest resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
    long l2 = 0L;
    try {
      l2 = resourceRequest.request(1L, resourceIdImpl);
      if (l2 < 1L)
        throw new SocketException("Resource limited: too many open file descriptors"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      resourceRequest.request(-l2, resourceIdImpl);
      SocketException socketException = new SocketException("Resource limited: too many open file descriptors");
      socketException.initCause(resourceRequestDeniedException);
      throw socketException;
    } 
    resourceRequest.request(-(l2 - 1L), resourceIdImpl);
  }
  
  @InstrumentationMethod
  protected void close() {
    if (this.fd != null) {
      long l;
      JavaIOFileDescriptorAccess javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
      try {
        l = javaIOFileDescriptorAccess.getHandle(this.fd);
        if (l == -1L)
          l = javaIOFileDescriptorAccess.get(this.fd); 
      } catch (UnsupportedOperationException unsupportedOperationException) {
        l = javaIOFileDescriptorAccess.get(this.fd);
      } 
      if (l != -1L) {
        ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(Long.valueOf(l));
        ResourceRequest resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
        resourceRequest.request(-1L, resourceIdImpl);
      } 
    } 
    close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\AbstractPlainDatagramSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */