package jdk.management.resource;

import jdk.management.resource.internal.ResourceIdImpl;

public class NotifyingMeter extends SimpleMeter {
  private final ResourceApprover approver;
  
  private long granularity;
  
  public static NotifyingMeter create(ResourceType paramResourceType, ResourceApprover paramResourceApprover) { return new NotifyingMeter(paramResourceType, null, paramResourceApprover); }
  
  public static NotifyingMeter create(ResourceType paramResourceType, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover) { return new NotifyingMeter(paramResourceType, paramResourceRequest, paramResourceApprover); }
  
  protected NotifyingMeter(ResourceType paramResourceType, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover) {
    super(paramResourceType, paramResourceRequest);
    this.approver = paramResourceApprover;
    this.granularity = 1L;
  }
  
  protected long validate(long paramLong1, long paramLong2, ResourceId paramResourceId) {
    long l = paramLong2;
    if (this.approver != null) {
      long l1 = Math.floorDiv(paramLong1, this.granularity);
      long l2 = Math.floorDiv(paramLong1 + paramLong2, this.granularity);
      if (l1 != l2 || (paramLong2 == 0L && paramResourceId != null && paramResourceId instanceof ResourceIdImpl && ((ResourceIdImpl)paramResourceId).isForcedUpdate())) {
        l = this.approver.request(this, paramLong1, paramLong2, paramResourceId);
        if (l != paramLong2 && l != 0L)
          l = paramLong2; 
      } 
    } 
    return l;
  }
  
  public final long getGranularity() { return this.granularity; }
  
  public final long setGranularity(long paramLong) { return setGranularityInternal(paramLong); }
  
  long setGranularityInternal(long paramLong) {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("granularity must be greater than zero"); 
    long l = this.granularity;
    this.granularity = paramLong;
    return l;
  }
  
  public final ResourceApprover getApprover() { return this.approver; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\NotifyingMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */