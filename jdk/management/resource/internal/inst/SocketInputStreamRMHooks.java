package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.SocketInputStream")
@TypeMapping(from = "jdk.management.resource.internal.inst.SocketInputStreamRMHooks$AbstractPlainSocketImpl", to = "java.net.AbstractPlainSocketImpl")
public final class SocketInputStreamRMHooks {
  private AbstractPlainSocketImpl impl = null;
  
  @InstrumentationMethod
  private int socketRead(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) throws IOException {
    int j;
    if (paramInt2 < 0)
      return socketRead(paramFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3); 
    resourceIdImpl = ResourceIdImpl.of(Integer.valueOf(this.impl.localport));
    resourceRequest = ApproverGroup.SOCKET_READ_GROUP.getApprover(this);
    l = 0L;
    try {
      l = Math.max(resourceRequest.request(paramInt2, resourceIdImpl), 0L);
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    paramInt2 = Math.min(paramInt2, (int)l);
    i = 0;
    try {
      j = socketRead(paramFileDescriptor, paramArrayOfByte, paramInt1, paramInt2, paramInt3);
      i = Math.max(j, 0);
    } finally {
      resourceRequest.request(-(l - i), resourceIdImpl);
    } 
    return j;
  }
  
  static class AbstractPlainSocketImpl {
    protected int localport;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\SocketInputStreamRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */