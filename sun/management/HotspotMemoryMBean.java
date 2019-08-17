package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public interface HotspotMemoryMBean {
  List<Counter> getInternalMemoryCounters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotspotMemoryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */