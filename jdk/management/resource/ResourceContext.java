package jdk.management.resource;

import java.util.stream.Stream;
import jdk.management.resource.internal.SimpleResourceContext;

public interface ResourceContext extends AutoCloseable {
  void close();
  
  String getName();
  
  default ResourceContext bindThreadContext() { throw new UnsupportedOperationException("bind not supported by " + getName()); }
  
  static ResourceContext unbindThreadContext() { return SimpleResourceContext.unbindThreadContext(); }
  
  default Stream<Thread> boundThreads() { throw new UnsupportedOperationException("boundThreads not supported by " + getName()); }
  
  ResourceRequest getResourceRequest(ResourceType paramResourceType);
  
  default void addResourceMeter(ResourceMeter paramResourceMeter) { throw new UnsupportedOperationException("addResourceMeter not supported by " + getName()); }
  
  default boolean removeResourceMeter(ResourceMeter paramResourceMeter) { throw new UnsupportedOperationException("removeResourceMeter not supported by " + getName()); }
  
  ResourceMeter getMeter(ResourceType paramResourceType);
  
  Stream<ResourceMeter> meters();
  
  default void requestAccurateUpdate(ResourceAccuracy paramResourceAccuracy) { throw new UnsupportedOperationException("requestAccurateUpdate not supported by " + getName()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ResourceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */