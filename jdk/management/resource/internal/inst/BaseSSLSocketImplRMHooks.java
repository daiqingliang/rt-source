package jdk.management.resource.internal.inst;

import java.net.InetAddress;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.security.ssl.BaseSSLSocketImpl")
abstract class BaseSSLSocketImplRMHooks {
  @InstrumentationMethod
  boolean isLayered() { return isLayered(); }
  
  @InstrumentationMethod
  public final InetAddress getLocalAddress() { return getLocalAddress(); }
  
  @InstrumentationMethod
  public final boolean isBound() { return isBound(); }
  
  @InstrumentationMethod
  public void close() {
    if (isLayered() && isBound()) {
      ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(getLocalAddress());
      ResourceRequest resourceRequest = ApproverGroup.SOCKET_OPEN_GROUP.getApprover(this);
      resourceRequest.request(-1L, resourceIdImpl);
    } 
    close();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\BaseSSLSocketImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */