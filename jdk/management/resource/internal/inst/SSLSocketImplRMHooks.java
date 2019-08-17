package jdk.management.resource.internal.inst;

import java.io.IOException;
import java.net.InetAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.SSLSocketImpl")
public final class SSLSocketImplRMHooks {
  public final boolean isBound() { return isBound(); }
  
  boolean isLayered() { return isLayered(); }
  
  public final InetAddress getLocalAddress() { return getLocalAddress(); }
  
  @InstrumentationMethod
  void waitForClose(boolean paramBoolean) throws IOException {
    InetAddress inetAddress = null;
    if (isLayered())
      inetAddress = getLocalAddress(); 
    if (isBound()) {
      ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(inetAddress);
      ResourceRequest resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      resourceRequest.request(-1L, resourceIdImpl);
    } 
    waitForClose(paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\SSLSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */