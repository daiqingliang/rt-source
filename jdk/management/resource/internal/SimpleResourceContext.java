package jdk.management.resource.internal;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import jdk.management.resource.ResourceAccuracy;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceMeter;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;

public class SimpleResourceContext implements ResourceContext {
  private final ConcurrentHashMap<ResourceType, ResourceMeter> meters = new ConcurrentHashMap();
  
  private static final WeakKeyConcurrentHashMap<Thread, ResourceContext> currContext = new WeakKeyConcurrentHashMap();
  
  private static final ConcurrentHashMap<String, SimpleResourceContext> contexts = new ConcurrentHashMap();
  
  private final String name;
  
  private final int contextId;
  
  SimpleResourceContext(String paramString) { this((String)Objects.requireNonNull(paramString, "name"), ResourceNatives.createResourceContext(paramString)); }
  
  SimpleResourceContext(String paramString, int paramInt) {
    this.name = (String)Objects.requireNonNull(paramString, "name");
    this.contextId = paramInt;
    this.closed = false;
  }
  
  public String getName() { return this.name; }
  
  public static ResourceContext create(String paramString) {
    if (!contexts.containsKey(paramString)) {
      SimpleResourceContext simpleResourceContext = new SimpleResourceContext(paramString);
      ResourceContext resourceContext = (ResourceContext)contexts.putIfAbsent(paramString, simpleResourceContext);
      if (resourceContext == null)
        return simpleResourceContext; 
      ResourceNatives.destroyResourceContext(simpleResourceContext.contextId, 0);
    } 
    throw new IllegalArgumentException("ResourceContext already exists for name: " + paramString);
  }
  
  public static ResourceContext get(String paramString) {
    Objects.requireNonNull(paramString, "name");
    return (ResourceContext)contexts.get(paramString);
  }
  
  public void close() {
    synchronized (this) {
      if (!this.closed) {
        this.closed = true;
        contexts.remove(getName());
        UnassignedContext unassignedContext = UnassignedContext.getUnassignedContext();
        ResourceNatives.destroyResourceContext(this.contextId, unassignedContext.nativeThreadContext());
        boundThreads().forEach(paramThread -> paramUnassignedContext.bindThreadContext(paramThread));
        this.meters.forEach((paramResourceType, paramResourceMeter) -> {
              removeResourceMeter(paramResourceMeter);
              ApproverGroup approverGroup = ApproverGroup.getGroup(paramResourceType);
              if (approverGroup != null)
                approverGroup.purgeResourceContext(this); 
            });
      } 
    } 
  }
  
  static ConcurrentHashMap<String, SimpleResourceContext> getContexts() { return contexts; }
  
  public static Stream<ResourceContext> contexts() { return getContexts().values().stream().map(paramSimpleResourceContext -> paramSimpleResourceContext); }
  
  public static ResourceContext getThreadContext(Thread paramThread) {
    ResourceContext resourceContext = (ResourceContext)currContext.get(paramThread);
    if (resourceContext == null)
      resourceContext = UnassignedContext.getUnassignedContext(); 
    return resourceContext;
  }
  
  public ResourceContext bindThreadContext() {
    illegalStateIfClosed();
    Thread thread = Thread.currentThread();
    ResourceContext resourceContext = (ResourceContext)currContext.put(thread, this);
    if (resourceContext == null)
      resourceContext = UnassignedContext.getUnassignedContext(); 
    ThreadMetrics.updateCurrentThreadMetrics(resourceContext);
    ResourceNatives.setThreadResourceContext(this.contextId);
    return resourceContext;
  }
  
  public ResourceContext bindThreadContext(Thread paramThread) {
    illegalStateIfClosed();
    ResourceContext resourceContext = paramThread.isAlive() ? (ResourceContext)currContext.put(paramThread, this) : (ResourceContext)currContext.remove(paramThread);
    if (resourceContext == null)
      resourceContext = UnassignedContext.getUnassignedContext(); 
    try {
      ThreadMetrics.updateThreadMetrics(resourceContext, paramThread);
      ResourceNatives.setThreadResourceContext(paramThread.getId(), this.contextId);
    } catch (IllegalArgumentException illegalArgumentException) {
      currContext.remove(paramThread);
    } 
    return resourceContext;
  }
  
  public void bindNewThreadContext(Thread paramThread) { currContext.put(paramThread, this); }
  
  public static void removeThreadContext() { currContext.remove(Thread.currentThread()); }
  
  public static ResourceContext unbindThreadContext() { return UnassignedContext.getUnassignedContext().bindThreadContext(); }
  
  int nativeThreadContext() { return this.contextId; }
  
  public Stream<Thread> boundThreads() { return currContext.keysForValue(this).filter(paramThread -> paramThread.isAlive()); }
  
  public ResourceRequest getResourceRequest(ResourceType paramResourceType) {
    ResourceMeter resourceMeter = (ResourceMeter)this.meters.get(paramResourceType);
    return (resourceMeter instanceof ResourceRequest) ? (ResourceRequest)resourceMeter : null;
  }
  
  public void addResourceMeter(ResourceMeter paramResourceMeter) {
    illegalStateIfClosed();
    if (paramResourceMeter.getType().equals(ResourceType.HEAP_RETAINED) && !ResourceNatives.isHeapRetainedEnabled())
      throw new UnsupportedOperationException("ResourceType not supported by the current garbage collector: " + ResourceType.HEAP_RETAINED); 
    ResourceMeter resourceMeter = (ResourceMeter)this.meters.putIfAbsent(paramResourceMeter.getType(), paramResourceMeter);
    if (resourceMeter != null)
      throw new IllegalArgumentException("ResourceType already added to meter: " + paramResourceMeter.getType().getName()); 
    if (paramResourceMeter.getType().equals(ResourceType.THREAD_CPU) || paramResourceMeter.getType().equals(ResourceType.HEAP_ALLOCATED))
      ThreadMetrics.init(); 
    if (paramResourceMeter.getType().equals(ResourceType.HEAP_RETAINED))
      HeapMetrics.init(); 
    TotalResourceContext.validateMeter(paramResourceMeter.getType());
  }
  
  public boolean removeResourceMeter(ResourceMeter paramResourceMeter) {
    ResourceMeter resourceMeter = (ResourceMeter)this.meters.remove(paramResourceMeter.getType());
    if (resourceMeter != null) {
      TotalResourceContext totalResourceContext = TotalResourceContext.getTotalContext();
      TotalResourceContext.TotalMeter totalMeter = totalResourceContext.getMeter(resourceMeter.getType());
      totalMeter.addValue(resourceMeter.getValue());
      totalMeter.addAllocated(resourceMeter.getAllocated());
      return true;
    } 
    return false;
  }
  
  public ResourceMeter getMeter(ResourceType paramResourceType) { return (ResourceMeter)this.meters.get(paramResourceType); }
  
  public Stream<ResourceMeter> meters() { return this.meters.entrySet().stream().map(paramEntry -> (ResourceMeter)paramEntry.getValue()); }
  
  public void requestAccurateUpdate(ResourceAccuracy paramResourceAccuracy) {
    Objects.requireNonNull(paramResourceAccuracy, "accuracy");
    if (!ResourceNatives.isHeapRetainedEnabled())
      throw new UnsupportedOperationException("ResourceType not supported by the current garbage collector: " + ResourceType.HEAP_RETAINED); 
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = this.contextId;
    ResourceNatives.computeRetainedMemory(arrayOfInt, paramResourceAccuracy.ordinal());
  }
  
  private void illegalStateIfClosed() {
    if (this.closed)
      throw new IllegalStateException("ResourceContext is closed: " + getName()); 
  }
  
  public String toString() {
    StringJoiner stringJoiner = new StringJoiner("; ", this.name + "[", "]");
    this.meters.forEach((paramResourceType, paramResourceMeter) -> paramStringJoiner.add(paramResourceMeter.toString()));
    return stringJoiner.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\SimpleResourceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */