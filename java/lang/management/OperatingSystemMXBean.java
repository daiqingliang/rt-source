package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface OperatingSystemMXBean extends PlatformManagedObject {
  String getName();
  
  String getArch();
  
  String getVersion();
  
  int getAvailableProcessors();
  
  double getSystemLoadAverage();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\OperatingSystemMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */