package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public interface HotspotRuntimeMBean {
  long getSafepointCount();
  
  long getTotalSafepointTime();
  
  long getSafepointSyncTime();
  
  List<Counter> getInternalRuntimeCounters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotspotRuntimeMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */