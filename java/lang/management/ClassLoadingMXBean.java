package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface ClassLoadingMXBean extends PlatformManagedObject {
  long getTotalLoadedClassCount();
  
  int getLoadedClassCount();
  
  long getUnloadedClassCount();
  
  boolean isVerbose();
  
  void setVerbose(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\ClassLoadingMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */