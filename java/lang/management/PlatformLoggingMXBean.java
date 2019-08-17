package java.lang.management;

import java.lang.management.PlatformManagedObject;
import java.util.List;

public interface PlatformLoggingMXBean extends PlatformManagedObject {
  List<String> getLoggerNames();
  
  String getLoggerLevel(String paramString);
  
  void setLoggerLevel(String paramString1, String paramString2);
  
  String getParentLoggerName(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\PlatformLoggingMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */