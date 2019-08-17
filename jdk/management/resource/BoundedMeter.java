package jdk.management.resource;

public class BoundedMeter extends NotifyingMeter implements ResourceMeter, ResourceRequest {
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong) { return create(paramResourceType, paramLong, null, null); }
  
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest) { return create(paramResourceType, paramLong, paramResourceRequest, null); }
  
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover) { return new BoundedMeter(paramResourceType, paramLong, paramResourceRequest, paramResourceApprover); }
  
  public static BoundedMeter create(ResourceType paramResourceType, long paramLong, ResourceApprover paramResourceApprover) { return create(paramResourceType, paramLong, null, paramResourceApprover); }
  
  protected BoundedMeter(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover) {
    super(paramResourceType, paramResourceRequest, paramResourceApprover);
    if (paramLong < 0L)
      throw new IllegalArgumentException("bound must be zero or greater"); 
    this.bound = paramLong;
  }
  
  protected long validate(long paramLong1, long paramLong2, ResourceId paramResourceId) {
    ResourceApprover resourceApprover = getApprover();
    long l = paramLong2;
    if (resourceApprover != null) {
      long l1 = getGranularity();
      long l2 = paramLong1 + paramLong2;
      long l3 = Math.floorDiv(paramLong1, l1);
      long l4 = Math.floorDiv(l2, l1);
      if (l3 != l4 || this.bound - l2 < 0L) {
        l = resourceApprover.request(this, paramLong1, paramLong2, paramResourceId);
        if (l != paramLong2 && l != 0L)
          l = paramLong2; 
      } 
    } 
    if (this.bound - paramLong1 + l < 0L)
      l = 0L; 
    return l;
  }
  
  public final long getBound() { return this.bound; }
  
  public final long setBound(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("bound must be zero or greater"); 
    long l = this.bound;
    this.bound = paramLong;
    return l;
  }
  
  long setGranularityInternal(long paramLong) { return super.setGranularityInternal(paramLong); }
  
  public String toString() { return super.toString() + "; bound: " + Long.toString(this.bound); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\BoundedMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */