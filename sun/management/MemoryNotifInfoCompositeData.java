package sun.management;

import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class MemoryNotifInfoCompositeData extends LazyCompositeData {
  private final MemoryNotificationInfo memoryNotifInfo;
  
  private static final CompositeType memoryNotifInfoCompositeType;
  
  private static final String POOL_NAME = "poolName";
  
  private static final String USAGE = "usage";
  
  private static final String COUNT = "count";
  
  private static final String[] memoryNotifInfoItemNames;
  
  private static final long serialVersionUID = -1805123446483771291L;
  
  private MemoryNotifInfoCompositeData(MemoryNotificationInfo paramMemoryNotificationInfo) { this.memoryNotifInfo = paramMemoryNotificationInfo; }
  
  public MemoryNotificationInfo getMemoryNotifInfo() { return this.memoryNotifInfo; }
  
  public static CompositeData toCompositeData(MemoryNotificationInfo paramMemoryNotificationInfo) {
    MemoryNotifInfoCompositeData memoryNotifInfoCompositeData = new MemoryNotifInfoCompositeData(paramMemoryNotificationInfo);
    return memoryNotifInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    Object[] arrayOfObject = { this.memoryNotifInfo.getPoolName(), MemoryUsageCompositeData.toCompositeData(this.memoryNotifInfo.getUsage()), new Long(this.memoryNotifInfo.getCount()) };
    try {
      return new CompositeDataSupport(memoryNotifInfoCompositeType, memoryNotifInfoItemNames, arrayOfObject);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
  
  public static String getPoolName(CompositeData paramCompositeData) {
    String str = getString(paramCompositeData, "poolName");
    if (str == null)
      throw new IllegalArgumentException("Invalid composite data: Attribute poolName has null value"); 
    return str;
  }
  
  public static MemoryUsage getUsage(CompositeData paramCompositeData) {
    CompositeData compositeData = (CompositeData)paramCompositeData.get("usage");
    return MemoryUsage.from(compositeData);
  }
  
  public static long getCount(CompositeData paramCompositeData) { return getLong(paramCompositeData, "count"); }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(memoryNotifInfoCompositeType, paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for MemoryNotificationInfo"); 
  }
  
  static  {
    try {
      memoryNotifInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MemoryNotificationInfo.class);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
    memoryNotifInfoItemNames = new String[] { "poolName", "usage", "count" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MemoryNotifInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */