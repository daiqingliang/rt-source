package jdk.management.resource.internal.inst;

import java.security.AccessControlContext;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import jdk.management.resource.internal.SimpleResourceContext;

@InstrumentationTarget("java.lang.Thread")
public final class ThreadRMHooks {
  private long tid;
  
  private static long nextThreadID() { return 0L; }
  
  @InstrumentationMethod
  private void init(ThreadGroup paramThreadGroup, Runnable paramRunnable, String paramString, long paramLong, AccessControlContext paramAccessControlContext, boolean paramBoolean) {
    long l1 = nextThreadID();
    resourceIdImpl = ResourceIdImpl.of(Long.valueOf(l1));
    resourceRequest = ApproverGroup.THREAD_CREATED_GROUP.getApprover(this);
    l2 = 1L;
    l3 = 0L;
    try {
      l3 = resourceRequest.request(l2, resourceIdImpl);
      if (l3 == 0L)
        throw new ResourceRequestDeniedException("Resource limited: thread creation denied by resource manager"); 
      init(paramThreadGroup, paramRunnable, paramString, paramLong, paramAccessControlContext, paramBoolean);
      SimpleResourceContext simpleResourceContext = (SimpleResourceContext)SimpleResourceContext.getThreadContext(Thread.currentThread());
      ThreadRMHooks threadRMHooks = this;
      simpleResourceContext.bindNewThreadContext((Thread)threadRMHooks);
    } finally {
      resourceRequest.request(-(l3 - l2), resourceIdImpl);
    } 
    this.tid = l1;
  }
  
  @InstrumentationMethod
  private void exit() {
    ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(Long.valueOf(this.tid));
    ResourceRequest resourceRequest = ApproverGroup.THREAD_CREATED_GROUP.getApprover(this);
    try {
      resourceRequest.request(-1L, resourceIdImpl);
      SimpleResourceContext.removeThreadContext();
    } finally {
      exit();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\ThreadRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */