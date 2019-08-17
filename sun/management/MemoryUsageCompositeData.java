package sun.management;

import java.lang.management.MemoryUsage;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class MemoryUsageCompositeData extends LazyCompositeData {
  private final MemoryUsage usage;
  
  private static final CompositeType memoryUsageCompositeType;
  
  private static final String INIT = "init";
  
  private static final String USED = "used";
  
  private static final String COMMITTED = "committed";
  
  private static final String MAX = "max";
  
  private static final String[] memoryUsageItemNames;
  
  private static final long serialVersionUID = -8504291541083874143L;
  
  private MemoryUsageCompositeData(MemoryUsage paramMemoryUsage) { this.usage = paramMemoryUsage; }
  
  public MemoryUsage getMemoryUsage() { return this.usage; }
  
  public static CompositeData toCompositeData(MemoryUsage paramMemoryUsage) {
    MemoryUsageCompositeData memoryUsageCompositeData = new MemoryUsageCompositeData(paramMemoryUsage);
    return memoryUsageCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    Object[] arrayOfObject = { new Long(this.usage.getInit()), new Long(this.usage.getUsed()), new Long(this.usage.getCommitted()), new Long(this.usage.getMax()) };
    try {
      return new CompositeDataSupport(memoryUsageCompositeType, memoryUsageItemNames, arrayOfObject);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
  
  static CompositeType getMemoryUsageCompositeType() { return memoryUsageCompositeType; }
  
  public static long getInit(CompositeData paramCompositeData) { return getLong(paramCompositeData, "init"); }
  
  public static long getUsed(CompositeData paramCompositeData) { return getLong(paramCompositeData, "used"); }
  
  public static long getCommitted(CompositeData paramCompositeData) { return getLong(paramCompositeData, "committed"); }
  
  public static long getMax(CompositeData paramCompositeData) { return getLong(paramCompositeData, "max"); }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(memoryUsageCompositeType, paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for MemoryUsage"); 
  }
  
  static  {
    try {
      memoryUsageCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MemoryUsage.class);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
    memoryUsageItemNames = new String[] { "init", "used", "committed", "max" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MemoryUsageCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */