package sun.management;

import java.util.List;
import java.util.Map;
import sun.management.counter.Counter;

public interface HotspotThreadMBean {
  int getInternalThreadCount();
  
  Map<String, Long> getInternalThreadCpuTimes();
  
  List<Counter> getInternalThreadingCounters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotspotThreadMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */