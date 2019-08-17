package jdk.management.resource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import jdk.management.resource.internal.ResourceIdImpl;

public class SimpleMeter implements ResourceMeter, ResourceRequest {
  private final ResourceType type;
  
  private final AtomicLong value;
  
  private final AtomicLong allocated;
  
  private final ResourceRequest parent;
  
  public static SimpleMeter create(ResourceType paramResourceType) { return new SimpleMeter(paramResourceType, null); }
  
  public static SimpleMeter create(ResourceType paramResourceType, ResourceRequest paramResourceRequest) { return new SimpleMeter(paramResourceType, paramResourceRequest); }
  
  protected SimpleMeter(ResourceType paramResourceType, ResourceRequest paramResourceRequest) {
    this.type = (ResourceType)Objects.requireNonNull(paramResourceType, "type");
    this.parent = paramResourceRequest;
    this.value = new AtomicLong();
    this.allocated = new AtomicLong();
  }
  
  public final long getValue() { return this.value.get(); }
  
  public final long getAllocated() { return this.allocated.get(); }
  
  public final ResourceType getType() { return this.type; }
  
  public final ResourceRequest getParent() { return this.parent; }
  
  public final long request(long paramLong, ResourceId paramResourceId) {
    if (paramLong == 0L) {
      Object object = null;
      if (paramResourceId == null || !(paramResourceId instanceof ResourceIdImpl) || !((ResourceIdImpl)paramResourceId).isForcedUpdate())
        return 0L; 
    } 
    l = 0L;
    if (paramLong > 0L) {
      try {
        l1 = this.value.getAndAdd(paramLong);
        l = validate(l1, paramLong, paramResourceId);
      } finally {
        long l1 = paramLong - l;
        if (l1 != 0L)
          this.value.getAndAdd(-l1); 
      } 
    } else {
      long l1 = getValue();
      l = validate(l1, paramLong, paramResourceId);
      this.value.getAndAdd(l);
    } 
    if (this.parent != null) {
      l1 = l;
      l = 0L;
      try {
        l = this.parent.request(l1, paramResourceId);
      } finally {
        long l2 = l1 - l;
        if (l2 != 0L)
          this.value.getAndAdd(-l2); 
      } 
    } 
    if (l > 0L)
      this.allocated.getAndAdd(l); 
    return l;
  }
  
  protected long validate(long paramLong1, long paramLong2, ResourceId paramResourceId) throws ResourceRequestDeniedException { return paramLong2; }
  
  public String toString() {
    long l1 = this.value.get();
    long l2 = this.allocated.get();
    return this.type.toString() + ": " + Long.toString(l1) + "/" + l2;
  }
  
  public final int hashCode() { return super.hashCode(); }
  
  public final boolean equals(Object paramObject) { return super.equals(paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\SimpleMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */