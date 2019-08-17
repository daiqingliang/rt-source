package java.lang.management;

import java.lang.management.PlatformManagedObject;
import java.util.List;
import java.util.Map;

public interface RuntimeMXBean extends PlatformManagedObject {
  String getName();
  
  String getVmName();
  
  String getVmVendor();
  
  String getVmVersion();
  
  String getSpecName();
  
  String getSpecVendor();
  
  String getSpecVersion();
  
  String getManagementSpecVersion();
  
  String getClassPath();
  
  String getLibraryPath();
  
  boolean isBootClassPathSupported();
  
  String getBootClassPath();
  
  List<String> getInputArguments();
  
  long getUptime();
  
  long getStartTime();
  
  Map<String, String> getSystemProperties();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\RuntimeMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */