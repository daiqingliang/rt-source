package java.lang.management;

import java.lang.management.PlatformManagedObject;

public interface MemoryManagerMXBean extends PlatformManagedObject {
  String getName();
  
  boolean isValid();
  
  String[] getMemoryPoolNames();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\MemoryManagerMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */