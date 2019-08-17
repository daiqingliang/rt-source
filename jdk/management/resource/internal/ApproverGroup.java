package jdk.management.resource.internal;

import java.util.concurrent.ConcurrentHashMap;
import jdk.management.resource.ResourceContext;
import jdk.management.resource.ResourceId;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.ResourceType;

public final class ApproverGroup {
  private static final ConcurrentHashMap<ResourceType, ApproverGroup> approverGroups = new ConcurrentHashMap();
  
  private final WeakKeyConcurrentHashMap<Object, ResourceContext> approvers;
  
  private final ResourceType type;
  
  private final boolean isBoundToContext;
  
  private static final ResourceRequest fallback = (paramLong, paramResourceId) -> paramLong;
  
  public static final ApproverGroup FILE_OPEN_GROUP;
  
  public static final ApproverGroup FILE_READ_GROUP;
  
  public static final ApproverGroup FILE_WRITE_GROUP;
  
  public static final ApproverGroup STDERR_WRITE_GROUP;
  
  public static final ApproverGroup STDIN_READ_GROUP;
  
  public static final ApproverGroup STDOUT_WRITE_GROUP;
  
  public static final ApproverGroup SOCKET_OPEN_GROUP;
  
  public static final ApproverGroup SOCKET_READ_GROUP;
  
  public static final ApproverGroup SOCKET_WRITE_GROUP;
  
  public static final ApproverGroup DATAGRAM_OPEN_GROUP;
  
  public static final ApproverGroup DATAGRAM_RECEIVED_GROUP;
  
  public static final ApproverGroup DATAGRAM_SENT_GROUP;
  
  public static final ApproverGroup DATAGRAM_READ_GROUP;
  
  public static final ApproverGroup DATAGRAM_WRITE_GROUP;
  
  public static final ApproverGroup THREAD_CREATED_GROUP;
  
  public static final ApproverGroup THREAD_CPU_GROUP;
  
  public static final ApproverGroup FILEDESCRIPTOR_OPEN_GROUP = (THREAD_CPU_GROUP = (THREAD_CREATED_GROUP = (DATAGRAM_WRITE_GROUP = (DATAGRAM_READ_GROUP = (DATAGRAM_SENT_GROUP = (DATAGRAM_RECEIVED_GROUP = (DATAGRAM_OPEN_GROUP = (SOCKET_WRITE_GROUP = (SOCKET_READ_GROUP = (SOCKET_OPEN_GROUP = (STDOUT_WRITE_GROUP = (STDIN_READ_GROUP = (STDERR_WRITE_GROUP = (FILE_WRITE_GROUP = (FILE_READ_GROUP = (FILE_OPEN_GROUP = create(ResourceType.FILE_OPEN, true)).create(ResourceType.FILE_READ, false)).create(ResourceType.FILE_WRITE, false)).create(ResourceType.STDERR_WRITE, false)).create(ResourceType.STDIN_READ, false)).create(ResourceType.STDOUT_WRITE, false)).create(ResourceType.SOCKET_OPEN, true)).create(ResourceType.SOCKET_READ, false)).create(ResourceType.SOCKET_WRITE, false)).create(ResourceType.DATAGRAM_OPEN, true)).create(ResourceType.DATAGRAM_RECEIVED, false)).create(ResourceType.DATAGRAM_SENT, false)).create(ResourceType.DATAGRAM_READ, false)).create(ResourceType.DATAGRAM_WRITE, false)).create(ResourceType.THREAD_CREATED, true)).create(ResourceType.THREAD_CPU, false)).create(ResourceType.FILEDESCRIPTOR_OPEN, true);
  
  public static ApproverGroup getGroup(ResourceType paramResourceType) { return (ApproverGroup)approverGroups.get(paramResourceType); }
  
  public static ApproverGroup create(ResourceType paramResourceType, boolean paramBoolean) { return (ApproverGroup)approverGroups.computeIfAbsent(paramResourceType, paramResourceType -> new ApproverGroup(paramResourceType, paramBoolean)); }
  
  private ApproverGroup(ResourceType paramResourceType, boolean paramBoolean) {
    this.type = paramResourceType;
    this.isBoundToContext = paramBoolean;
    this.approvers = new WeakKeyConcurrentHashMap();
  }
  
  public final ResourceType getId() { return this.type; }
  
  public final ResourceRequest getApprover(Object paramObject) {
    ResourceContext resourceContext;
    if (this.isBoundToContext) {
      if (paramObject != null) {
        resourceContext = (ResourceContext)this.approvers.computeIfAbsent(paramObject, paramObject -> SimpleResourceContext.getThreadContext(Thread.currentThread()));
      } else {
        throw new ResourceRequestDeniedException("null resource instance for ResourceType: " + this.type);
      } 
    } else {
      resourceContext = SimpleResourceContext.getThreadContext(Thread.currentThread());
    } 
    ResourceRequest resourceRequest = resourceContext.getResourceRequest(this.type);
    return (resourceRequest != null) ? resourceRequest : fallback;
  }
  
  public void purgeResourceContext(ResourceContext paramResourceContext) { this.approvers.purgeValue(paramResourceContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\ApproverGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */