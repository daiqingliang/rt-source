package jdk.management.resource.internal;

import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceType;
import jdk.management.resource.SimpleMeter;

public class UnassignedContext extends SimpleResourceContext {
  private static final UnassignedContext unassignedContext = new UnassignedContext("Unassigned");
  
  private static final UnassignedContext systemContext = new UnassignedContext("System", 0);
  
  private UnassignedContext(String paramString) { super(paramString); }
  
  private UnassignedContext(String paramString, int paramInt) { super(paramString, paramInt); }
  
  public static UnassignedContext getSystemContext() { return systemContext; }
  
  public static UnassignedContext getUnassignedContext() { return unassignedContext; }
  
  public void close() {}
  
  public ResourceRequest getResourceRequest(ResourceType paramResourceType) {
    ResourceRequest resourceRequest = super.getResourceRequest(paramResourceType);
    if (resourceRequest == null) {
      try {
        addResourceMeter(SimpleMeter.create(paramResourceType));
      } catch (IllegalArgumentException illegalArgumentException) {}
      resourceRequest = super.getResourceRequest(paramResourceType);
    } 
    return resourceRequest;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\UnassignedContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */