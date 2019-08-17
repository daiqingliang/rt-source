package java.lang.management;

import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.PlatformManagedObject;

public interface MemoryPoolMXBean extends PlatformManagedObject {
  String getName();
  
  MemoryType getType();
  
  MemoryUsage getUsage();
  
  MemoryUsage getPeakUsage();
  
  void resetPeakUsage();
  
  boolean isValid();
  
  String[] getMemoryManagerNames();
  
  long getUsageThreshold();
  
  void setUsageThreshold(long paramLong);
  
  boolean isUsageThresholdExceeded();
  
  long getUsageThresholdCount();
  
  boolean isUsageThresholdSupported();
  
  long getCollectionUsageThreshold();
  
  void setCollectionUsageThreshold(long paramLong);
  
  boolean isCollectionUsageThresholdExceeded();
  
  long getCollectionUsageThresholdCount();
  
  MemoryUsage getCollectionUsage();
  
  boolean isCollectionUsageThresholdSupported();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\MemoryPoolMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */