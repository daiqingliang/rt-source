package sun.management;

import java.lang.management.MonitorInfo;
import java.util.Set;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;

public class MonitorInfoCompositeData extends LazyCompositeData {
  private final MonitorInfo lock;
  
  private static final CompositeType monitorInfoCompositeType;
  
  private static final String[] monitorInfoItemNames;
  
  private static final String CLASS_NAME = "className";
  
  private static final String IDENTITY_HASH_CODE = "identityHashCode";
  
  private static final String LOCKED_STACK_FRAME = "lockedStackFrame";
  
  private static final String LOCKED_STACK_DEPTH = "lockedStackDepth";
  
  private static final long serialVersionUID = -5825215591822908529L;
  
  private MonitorInfoCompositeData(MonitorInfo paramMonitorInfo) { this.lock = paramMonitorInfo; }
  
  public MonitorInfo getMonitorInfo() { return this.lock; }
  
  public static CompositeData toCompositeData(MonitorInfo paramMonitorInfo) {
    MonitorInfoCompositeData monitorInfoCompositeData = new MonitorInfoCompositeData(paramMonitorInfo);
    return monitorInfoCompositeData.getCompositeData();
  }
  
  protected CompositeData getCompositeData() {
    int i = monitorInfoItemNames.length;
    Object[] arrayOfObject = new Object[i];
    CompositeData compositeData = LockInfoCompositeData.toCompositeData(this.lock);
    for (b = 0; b < i; b++) {
      String str = monitorInfoItemNames[b];
      if (str.equals("lockedStackFrame")) {
        StackTraceElement stackTraceElement = this.lock.getLockedStackFrame();
        arrayOfObject[b] = (stackTraceElement != null) ? StackTraceElementCompositeData.toCompositeData(stackTraceElement) : null;
      } else if (str.equals("lockedStackDepth")) {
        arrayOfObject[b] = new Integer(this.lock.getLockedStackDepth());
      } else {
        arrayOfObject[b] = compositeData.get(str);
      } 
    } 
    try {
      return new CompositeDataSupport(monitorInfoCompositeType, monitorInfoItemNames, arrayOfObject);
    } catch (OpenDataException b) {
      OpenDataException openDataException;
      throw new AssertionError(openDataException);
    } 
  }
  
  static CompositeType getMonitorInfoCompositeType() { return monitorInfoCompositeType; }
  
  public static String getClassName(CompositeData paramCompositeData) { return getString(paramCompositeData, "className"); }
  
  public static int getIdentityHashCode(CompositeData paramCompositeData) { return getInt(paramCompositeData, "identityHashCode"); }
  
  public static StackTraceElement getLockedStackFrame(CompositeData paramCompositeData) {
    CompositeData compositeData = (CompositeData)paramCompositeData.get("lockedStackFrame");
    return (compositeData != null) ? StackTraceElementCompositeData.from(compositeData) : null;
  }
  
  public static int getLockedStackDepth(CompositeData paramCompositeData) { return getInt(paramCompositeData, "lockedStackDepth"); }
  
  public static void validateCompositeData(CompositeData paramCompositeData) {
    if (paramCompositeData == null)
      throw new NullPointerException("Null CompositeData"); 
    if (!isTypeMatched(monitorInfoCompositeType, paramCompositeData.getCompositeType()))
      throw new IllegalArgumentException("Unexpected composite type for MonitorInfo"); 
  }
  
  static  {
    try {
      monitorInfoCompositeType = (CompositeType)MappedMXBeanType.toOpenType(MonitorInfo.class);
      Set set = monitorInfoCompositeType.keySet();
      monitorInfoItemNames = (String[])set.toArray(new String[0]);
    } catch (OpenDataException openDataException) {
      throw new AssertionError(openDataException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MonitorInfoCompositeData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */