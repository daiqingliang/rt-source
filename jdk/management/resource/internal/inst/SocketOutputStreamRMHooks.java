package jdk.management.resource.internal.inst;

import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.internal.instrumentation.TypeMapping;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("java.net.SocketOutputStream")
@TypeMapping(from = "jdk.management.resource.internal.inst.SocketOutputStreamRMHooks$AbstractPlainSocketImpl", to = "java.net.AbstractPlainSocketImpl")
public final class SocketOutputStreamRMHooks {
  private AbstractPlainSocketImpl impl = null;
  
  @InstrumentationMethod
  private void socketWrite(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0) {
      socketWrite(paramArrayOfByte, paramInt1, paramInt2);
      return;
    } 
    resourceIdImpl = ResourceIdImpl.of(Integer.valueOf(this.impl.localport));
    resourceRequest = ApproverGroup.SOCKET_WRITE_GROUP.getApprover(this);
    l = 0L;
    try {
      l = resourceRequest.request(paramInt2, resourceIdImpl);
      if (l < paramInt2)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    i = 0;
    try {
      socketWrite(paramArrayOfByte, paramInt1, paramInt2);
      i = paramInt2;
    } finally {
      resourceRequest.request(-(l - i), resourceIdImpl);
    } 
  }
  
  static class AbstractPlainSocketImpl {
    protected int localport;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\SocketOutputStreamRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */