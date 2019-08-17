package java.lang.management;

import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeData;
import sun.management.MemoryNotifInfoCompositeData;

public class MemoryNotificationInfo {
  private final String poolName;
  
  private final MemoryUsage usage;
  
  private final long count;
  
  public static final String MEMORY_THRESHOLD_EXCEEDED = "java.management.memory.threshold.exceeded";
  
  public static final String MEMORY_COLLECTION_THRESHOLD_EXCEEDED = "java.management.memory.collection.threshold.exceeded";
  
  public MemoryNotificationInfo(String paramString, MemoryUsage paramMemoryUsage, long paramLong) {
    if (paramString == null)
      throw new NullPointerException("Null poolName"); 
    if (paramMemoryUsage == null)
      throw new NullPointerException("Null usage"); 
    this.poolName = paramString;
    this.usage = paramMemoryUsage;
    this.count = paramLong;
  }
  
  MemoryNotificationInfo(CompositeData paramCompositeData) {
    MemoryNotifInfoCompositeData.validateCompositeData(paramCompositeData);
    this.poolName = MemoryNotifInfoCompositeData.getPoolName(paramCompositeData);
    this.usage = MemoryNotifInfoCompositeData.getUsage(paramCompositeData);
    this.count = MemoryNotifInfoCompositeData.getCount(paramCompositeData);
  }
  
  public String getPoolName() { return this.poolName; }
  
  public MemoryUsage getUsage() { return this.usage; }
  
  public long getCount() { return this.count; }
  
  public static MemoryNotificationInfo from(CompositeData paramCompositeData) { return (paramCompositeData == null) ? null : ((paramCompositeData instanceof MemoryNotifInfoCompositeData) ? ((MemoryNotifInfoCompositeData)paramCompositeData).getMemoryNotifInfo() : new MemoryNotificationInfo(paramCompositeData)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\MemoryNotificationInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */