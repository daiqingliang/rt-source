package jdk.management.resource;

import java.security.AccessController;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import jdk.management.resource.internal.ResourceNatives;
import jdk.management.resource.internal.SimpleResourceContext;
import jdk.management.resource.internal.TotalResourceContext;
import jdk.management.resource.internal.UnassignedContext;
import jdk.management.resource.internal.WrapInstrumentation;

public final class ResourceContextFactory {
  private static final ResourceContextFactory instance = new ResourceContextFactory();
  
  private final ResourceContext unassigned = ResourceNatives.isEnabled() ? UnassignedContext.getUnassignedContext() : null;
  
  private static Set<ResourceType> supportedResourceTypes = null;
  
  public static boolean isEnabled() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("jdk.management.resource.getResourceContextFactory")); 
    return ResourceNatives.isEnabled();
  }
  
  public static ResourceContextFactory getInstance() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("jdk.management.resource.getResourceContextFactory")); 
    if (!ResourceNatives.isEnabled())
      throw new UnsupportedOperationException("Resource management is not enabled"); 
    instance.initInstrumentation();
    return instance;
  }
  
  private void initInstrumentation() {
    if (!this.initialized) {
      ThreadLocalRandom.current();
      new SecureRandom();
      try {
        Class clazz = Class.forName("jdk.management.resource.internal.inst.InitInstrumentation");
        Runnable runnable = (Runnable)clazz.newInstance();
        runnable.run();
      } catch (ClassNotFoundException classNotFoundException) {
      
      } catch (IllegalAccessException|InstantiationException illegalAccessException) {
        throw new InternalError("Resource management instrumentation failed", illegalAccessException);
      } 
      if (!(new WrapInstrumentation()).wrapComplete())
        throw new InternalError("Resource management instrumentation failed"); 
      initPreBoundThreads();
      this.initialized = true;
    } 
  }
  
  private void initPreBoundThreads() { AccessController.doPrivileged(() -> {
          ThreadGroup threadGroup;
          for (threadGroup = Thread.currentThread().getThreadGroup(); threadGroup.getParent() != null; threadGroup = threadGroup.getParent());
          Thread[] arrayOfThread = new Thread[threadGroup.activeCount() * 2];
          int i = threadGroup.enumerate(arrayOfThread, true);
          for (byte b = 0; b < i; b++) {
            if (arrayOfThread[b] != null) {
              UnassignedContext unassignedContext = arrayOfThread[b].getThreadGroup().equals(threadGroup) ? UnassignedContext.getSystemContext() : UnassignedContext.getUnassignedContext();
              unassignedContext.bindThreadContext(arrayOfThread[b]);
            } 
          } 
          return null;
        }); }
  
  public ResourceContext create(String paramString) { return SimpleResourceContext.create(paramString); }
  
  public ResourceContext lookup(String paramString) { return SimpleResourceContext.get(paramString); }
  
  public ResourceContext getThreadContext() { return getThreadContext(Thread.currentThread()); }
  
  public ResourceContext getThreadContext(Thread paramThread) { return SimpleResourceContext.getThreadContext(paramThread); }
  
  public ResourceRequest getResourceRequest(ResourceType paramResourceType) { return getThreadContext().getResourceRequest(paramResourceType); }
  
  public ResourceContext getUnassignedContext() { return this.unassigned; }
  
  public ResourceContext getTotalsContext() { return TotalResourceContext.getTotalContext(); }
  
  public Stream<ResourceContext> contexts() { return SimpleResourceContext.contexts(); }
  
  public Set<ResourceType> supportedResourceTypes() {
    synchronized (this) {
      if (supportedResourceTypes == null) {
        Set set = ResourceType.builtinTypes();
        if (!ResourceNatives.isHeapRetainedEnabled())
          set.remove(ResourceType.HEAP_RETAINED); 
        supportedResourceTypes = Collections.unmodifiableSet(set);
      } 
      return supportedResourceTypes;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ResourceContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */