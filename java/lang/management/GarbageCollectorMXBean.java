package java.lang.management;

import java.lang.management.MemoryManagerMXBean;

public interface GarbageCollectorMXBean extends MemoryManagerMXBean {
  long getCollectionCount();
  
  long getCollectionTime();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\GarbageCollectorMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */