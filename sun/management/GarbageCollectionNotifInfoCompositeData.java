package sun.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

public class GarbageCollectionNotifInfoCompositeData extends LazyCompositeData {
  private final GarbageCollectionNotificationInfo gcNotifInfo;
  
  private static final String GC_NAME = "gcName";
  
  private static final String GC_ACTION = "gcAction";
  
  private static final String GC_CAUSE = "gcCause";
  
  private static final String GC_INFO = "gcInfo";
  
  private static final String[] gcNotifInfoItemNames = { "gcName", "gcAction", "gcCause", "gcInfo" };
  
  private static HashMap<GcInfoBuilder, CompositeType> compositeTypeByBuilder = new HashMap();
  
  private static CompositeType baseGcNotifInfoCompositeType = null;
  
  private static final long serialVersionUID = -1805123446483771292L;
  
  public GarbageCollectionNotifInfoCompositeData(GarbageCollectionNotificationInfo paramGarbageCollectionNotificationInfo) { this.gcNotifInfo = paramGarbageCollectionNotificationInfo; }
  
  public GarbageCollectionNotificationInfo getGarbageCollectionNotifInfo() { return this.gcNotifInfo; }
  
  public static CompositeData toCompositeData(GarbageCollectionNotificationInfo paramGarbageCollectionNotificationInfo) {
    GarbageCollectionNotifInfoCompositeData garbageCollectionNotifInfoCompositeData = new GarbageCollectionNotifInfoCompositeData(paramGarbageCollectionNotificationInfo);
    return garbageCollectionNotifInfoCompositeData.getCompositeData();
  }
  
  private CompositeType getCompositeTypeByBuilder() {
    GcInfoBuilder gcInfoBuilder = (GcInfoBuilder)AccessController.doPrivileged(new PrivilegedAction<GcInfoBuilder>() {
          public GcInfoBuilder run() {
            try {
              Class clazz = Class.forName("com.sun.management.GcInfo");
              Field field = clazz.getDeclaredField("builder");
              field.setAccessible(true);
              return (GcInfoBuilder)field.get(GarbageCollectionNotifInfoCompositeData.this.gcNotifInfo.getGcInfo());
            } catch (ClassNotFoundException|NoSuchFieldException|IllegalAccessException classNotFoundException) {
              return null;
            } 
          }
        });
    CompositeType compositeType = null;
    synchronized (compositeTypeByBuilder) {
      compositeType = (CompositeType)compositeTypeByBuilder.get(gcInfoBuilder);
      if (compositeType == null) {
        OpenType[] arrayOfOpenType = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, gcInfoBuilder.getGcInfoCompositeType() };
        try {
          compositeType = new CompositeType("sun.management.GarbageCollectionNotifInfoCompositeType", "CompositeType for GC notification info", gcNotifInfoItemNames, gcNotifInfoItemNames, arrayOfOpenType);
          compositeTypeByBuilder.put(gcInfoBuilder, compositeType);
        } catch (OpenDataException openDataException) {
          throw Util.newException(openDataException);
        } 
      } 
    } 
    return compositeType;
  }
  
  protected CompositeData getCompositeData() {
    Object[] arrayOfObject = { this.gcNotifInfo.getGcName(), this.gcNotifInfo.getGcAction(), this.gcNotifInfo.getGcCause(), GcInfoCompositeData.toCompositeData(this.gcNotifInfo.getGcInfo()) };
    CompositeType compositeType = getCompositeTypeByBuilder();
    try {
      return new CompositeDataSupport(compositeType, gcNotifInfoItemNames, arrayOfObject);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
  
  public static String getGcName(CompositeData paramCompositeData) {
    String str = getString(paramCompositeData, "gcName");
    if (str == null)
      throw new IllegalArgumentException("Invalid composite data: Attribute gcName has null value"); 
    return str;
  }
  
  public static String getGcAction(CompositeData paramCompositeData) {
    String str = getString(paramCompositeData, "gcAction");
    if (str == null)
      throw new IllegalArgumentException("Invalid composite data: Attribute gcAction has null value"); 
    return str;
  }
  
  public static String getGcCause(CompositeData paramCompositeData) {
    String str = getString(paramCompositeData, "gcCause");
    if (str == null)
      throw new IllegalArgumentException("Invalid composite data: Attribute gcCause has null value"); 
    return str;
  }
  
  public static GcInfo getGcInfo(CompositeData paramCompositeData) {
    CompositeData compositeData = (CompositeData)paramCompositeData.get("gcInfo");
    return GcInfo.from(compositeData);
  }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(getBaseGcNotifInfoCompositeType(), paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for GarbageCollectionNotificationInfo"); 
  }
  
  private static CompositeType getBaseGcNotifInfoCompositeType() {
    if (baseGcNotifInfoCompositeType == null)
      try {
        OpenType[] arrayOfOpenType = { SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, GcInfoCompositeData.getBaseGcInfoCompositeType() };
        baseGcNotifInfoCompositeType = new CompositeType("sun.management.BaseGarbageCollectionNotifInfoCompositeType", "CompositeType for Base GarbageCollectionNotificationInfo", gcNotifInfoItemNames, gcNotifInfoItemNames, arrayOfOpenType);
      } catch (OpenDataException openDataException) {
        throw Util.newException(openDataException);
      }  
    return baseGcNotifInfoCompositeType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\GarbageCollectionNotifInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */