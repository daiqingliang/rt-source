package java.lang.management;

import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeData;
import sun.management.MemoryUsageCompositeData;

public class MemoryUsage {
  private final long init;
  
  private final long used;
  
  private final long committed;
  
  private final long max;
  
  public MemoryUsage(long paramLong1, long paramLong2, long paramLong3, long paramLong4) {
    if (paramLong1 < -1L)
      throw new IllegalArgumentException("init parameter = " + paramLong1 + " is negative but not -1."); 
    if (paramLong4 < -1L)
      throw new IllegalArgumentException("max parameter = " + paramLong4 + " is negative but not -1."); 
    if (paramLong2 < 0L)
      throw new IllegalArgumentException("used parameter = " + paramLong2 + " is negative."); 
    if (paramLong3 < 0L)
      throw new IllegalArgumentException("committed parameter = " + paramLong3 + " is negative."); 
    if (paramLong2 > paramLong3)
      throw new IllegalArgumentException("used = " + paramLong2 + " should be <= committed = " + paramLong3); 
    if (paramLong4 >= 0L && paramLong3 > paramLong4)
      throw new IllegalArgumentException("committed = " + paramLong3 + " should be < max = " + paramLong4); 
    this.init = paramLong1;
    this.used = paramLong2;
    this.committed = paramLong3;
    this.max = paramLong4;
  }
  
  private MemoryUsage(CompositeData paramCompositeData) {
    MemoryUsageCompositeData.validateCompositeData(paramCompositeData);
    this.init = MemoryUsageCompositeData.getInit(paramCompositeData);
    this.used = MemoryUsageCompositeData.getUsed(paramCompositeData);
    this.committed = MemoryUsageCompositeData.getCommitted(paramCompositeData);
    this.max = MemoryUsageCompositeData.getMax(paramCompositeData);
  }
  
  public long getInit() { return this.init; }
  
  public long getUsed() { return this.used; }
  
  public long getCommitted() { return this.committed; }
  
  public long getMax() { return this.max; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("init = " + this.init + "(" + (this.init >> 10) + "K) ");
    stringBuffer.append("used = " + this.used + "(" + (this.used >> 10) + "K) ");
    stringBuffer.append("committed = " + this.committed + "(" + (this.committed >> 10) + "K) ");
    stringBuffer.append("max = " + this.max + "(" + (this.max >> 10) + "K)");
    return stringBuffer.toString();
  }
  
  public static MemoryUsage from(CompositeData paramCompositeData) { return (paramCompositeData == null) ? null : ((paramCompositeData instanceof MemoryUsageCompositeData) ? ((MemoryUsageCompositeData)paramCompositeData).getMemoryUsage() : new MemoryUsage(paramCompositeData)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\MemoryUsage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */