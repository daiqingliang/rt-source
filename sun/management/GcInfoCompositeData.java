package sun.management;

import com.sun.management.GcInfo;
import java.io.InvalidObjectException;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;

public class GcInfoCompositeData extends LazyCompositeData {
  private final GcInfo info;
  
  private final GcInfoBuilder builder;
  
  private final Object[] gcExtItemValues;
  
  private static final String ID = "id";
  
  private static final String START_TIME = "startTime";
  
  private static final String END_TIME = "endTime";
  
  private static final String DURATION = "duration";
  
  private static final String MEMORY_USAGE_BEFORE_GC = "memoryUsageBeforeGc";
  
  private static final String MEMORY_USAGE_AFTER_GC = "memoryUsageAfterGc";
  
  private static final String[] baseGcInfoItemNames = { "id", "startTime", "endTime", "duration", "memoryUsageBeforeGc", "memoryUsageAfterGc" };
  
  private static MappedMXBeanType memoryUsageMapType;
  
  private static OpenType[] baseGcInfoItemTypes;
  
  private static CompositeType baseGcInfoCompositeType;
  
  private static final long serialVersionUID = -5716428894085882742L;
  
  public GcInfoCompositeData(GcInfo paramGcInfo, GcInfoBuilder paramGcInfoBuilder, Object[] paramArrayOfObject) {
    this.info = paramGcInfo;
    this.builder = paramGcInfoBuilder;
    this.gcExtItemValues = paramArrayOfObject;
  }
  
  public GcInfo getGcInfo() { return this.info; }
  
  public static CompositeData toCompositeData(final GcInfo info) {
    GcInfoBuilder gcInfoBuilder = (GcInfoBuilder)AccessController.doPrivileged(new PrivilegedAction<GcInfoBuilder>() {
          public GcInfoBuilder run() {
            try {
              Class clazz = Class.forName("com.sun.management.GcInfo");
              Field field = clazz.getDeclaredField("builder");
              field.setAccessible(true);
              return (GcInfoBuilder)field.get(info);
            } catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException classNotFoundException) {
              return null;
            } 
          }
        });
    Object[] arrayOfObject = (Object[])AccessController.doPrivileged(new PrivilegedAction<Object[]>() {
          public Object[] run() {
            try {
              Class clazz = Class.forName("com.sun.management.GcInfo");
              Field field = clazz.getDeclaredField("extAttributes");
              field.setAccessible(true);
              return (Object[])field.get(info);
            } catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException classNotFoundException) {
              return null;
            } 
          }
        });
    GcInfoCompositeData gcInfoCompositeData = new GcInfoCompositeData(paramGcInfo, gcInfoBuilder, arrayOfObject);
    return gcInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    Object[] arrayOfObject1;
    try {
      arrayOfObject1 = new Object[] { new Long(this.info.getId()), new Long(this.info.getStartTime()), new Long(this.info.getEndTime()), new Long(this.info.getDuration()), memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageBeforeGc()), memoryUsageMapType.toOpenTypeData(this.info.getMemoryUsageAfterGc()) };
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
    int i = this.builder.getGcExtItemCount();
    if (i == 0 && this.gcExtItemValues != null && this.gcExtItemValues.length != 0)
      throw new AssertionError("Unexpected Gc Extension Item Values"); 
    if (i > 0 && (this.gcExtItemValues == null || i != this.gcExtItemValues.length))
      throw new AssertionError("Unmatched Gc Extension Item Values"); 
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length + i];
    System.arraycopy(arrayOfObject1, 0, arrayOfObject2, 0, arrayOfObject1.length);
    if (i > 0)
      System.arraycopy(this.gcExtItemValues, 0, arrayOfObject2, arrayOfObject1.length, i); 
    try {
      return new CompositeDataSupport(this.builder.getGcInfoCompositeType(), this.builder.getItemNames(), arrayOfObject2);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
  
  static String[] getBaseGcInfoItemNames() { return baseGcInfoItemNames; }
  
  static OpenType[] getBaseGcInfoItemTypes() {
    if (baseGcInfoItemTypes == null) {
      OpenType openType = memoryUsageMapType.getOpenType();
      baseGcInfoItemTypes = new OpenType[] { SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, openType, openType };
    } 
    return baseGcInfoItemTypes;
  }
  
  public static long getId(CompositeData paramCompositeData) { return getLong(paramCompositeData, "id"); }
  
  public static long getStartTime(CompositeData paramCompositeData) { return getLong(paramCompositeData, "startTime"); }
  
  public static long getEndTime(CompositeData paramCompositeData) { return getLong(paramCompositeData, "endTime"); }
  
  public static Map<String, MemoryUsage> getMemoryUsageBeforeGc(CompositeData paramCompositeData) {
    try {
      TabularData tabularData = (TabularData)paramCompositeData.get("memoryUsageBeforeGc");
      return cast(memoryUsageMapType.toJavaTypeData(tabularData));
    } catch (InvalidObjectException|OpenDataException invalidObjectException) {
      throw new AssertionError(invalidObjectException);
    } 
  }
  
  public static Map<String, MemoryUsage> cast(Object paramObject) { return (Map)paramObject; }
  
  public static Map<String, MemoryUsage> getMemoryUsageAfterGc(CompositeData paramCompositeData) {
    try {
      TabularData tabularData = (TabularData)paramCompositeData.get("memoryUsageAfterGc");
      return cast(memoryUsageMapType.toJavaTypeData(tabularData));
    } catch (InvalidObjectException|OpenDataException invalidObjectException) {
      throw new AssertionError(invalidObjectException);
    } 
  }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(getBaseGcInfoCompositeType(), paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for GcInfo"); 
  }
  
  static CompositeType getBaseGcInfoCompositeType() {
    if (baseGcInfoCompositeType == null)
      try {
        baseGcInfoCompositeType = new CompositeType("sun.management.BaseGcInfoCompositeType", "CompositeType for Base GcInfo", getBaseGcInfoItemNames(), getBaseGcInfoItemNames(), getBaseGcInfoItemTypes());
      } catch (OpenDataException openDataException) {
        throw Util.newException(openDataException);
      }  
    return baseGcInfoCompositeType;
  }
  
  static  {
    try {
      Method method = GcInfo.class.getMethod("getMemoryUsageBeforeGc", new Class[0]);
      memoryUsageMapType = MappedMXBeanType.getMappedType(method.getGenericReturnType());
    } catch (NoSuchMethodException|OpenDataException noSuchMethodException) {
      throw new AssertionError(noSuchMethodException);
    } 
    baseGcInfoItemTypes = null;
    baseGcInfoCompositeType = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\GcInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */