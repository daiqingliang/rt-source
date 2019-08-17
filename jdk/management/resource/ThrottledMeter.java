package jdk.management.resource;

public class ThrottledMeter extends NotifyingMeter {
  private final Object mutex;
  
  private long availableBytes;
  
  private long availableTimestamp;
  
  public static ThrottledMeter create(ResourceType paramResourceType, long paramLong, ResourceApprover paramResourceApprover) { return new ThrottledMeter(paramResourceType, paramLong, null, paramResourceApprover); }
  
  public static ThrottledMeter create(ResourceType paramResourceType, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover) { return new ThrottledMeter(paramResourceType, Float.MAX_VALUE, paramResourceRequest, paramResourceApprover); }
  
  public static ThrottledMeter create(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover) { return new ThrottledMeter(paramResourceType, paramLong, paramResourceRequest, paramResourceApprover); }
  
  ThrottledMeter(ResourceType paramResourceType, long paramLong, ResourceRequest paramResourceRequest, ResourceApprover paramResourceApprover) {
    super(paramResourceType, paramResourceRequest, paramResourceApprover);
    if (paramLong <= 0L)
      throw new IllegalArgumentException("ratePerSec must be greater than zero"); 
    this.ratePerSec = paramLong;
    this.mutex = new Object();
    this.availableBytes = 0L;
    this.availableTimestamp = 0L;
  }
  
  public long validate(long paramLong1, long paramLong2, ResourceId paramResourceId) {
    long l = super.validate(paramLong1, paramLong2, paramResourceId);
    if (l <= 0L)
      return l; 
    synchronized (this.mutex) {
      while (this.availableBytes - paramLong2 < 0L) {
        long l1 = this.ratePerSec;
        long l2 = this.availableBytes;
        long l3 = System.currentTimeMillis();
        long l4 = Math.max(l3 - this.availableTimestamp, 0L);
        long l5 = l1 * l4 / 1000L;
        this.availableBytes = Math.min(this.availableBytes + l5, l1);
        this.availableTimestamp = l3;
        if (this.availableBytes - paramLong2 >= 0L || (paramLong2 > l1 && l2 > 0L))
          break; 
        long l6 = Math.min(paramLong2 - this.availableBytes, l1);
        l4 = l6 * 1000L / l1;
        try {
          this.mutex.wait(Math.max(l4, 10L));
        } catch (InterruptedException interruptedException) {
          return 0L;
        } 
      } 
      this.availableBytes -= paramLong2;
    } 
    return paramLong2;
  }
  
  public final long getCurrentRate() {
    synchronized (this.mutex) {
      long l1 = this.ratePerSec;
      long l2 = System.currentTimeMillis();
      long l3 = l2 - this.availableTimestamp;
      long l4 = l1 * l3 / 1000L;
      this.availableBytes = Math.min(this.availableBytes + l4, l1);
      this.availableTimestamp = l2;
      return l1 - this.availableBytes;
    } 
  }
  
  public final long getRatePerSec() { return this.ratePerSec; }
  
  public final long setRatePerSec(long paramLong) {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("ratePerSec must be greater than zero"); 
    long l = paramLong;
    this.ratePerSec = paramLong;
    return l;
  }
  
  public String toString() { return super.toString() + "; ratePerSec: " + Long.toString(this.ratePerSec) + "; currentRate: " + Long.toString(getCurrentRate()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\ThrottledMeter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */