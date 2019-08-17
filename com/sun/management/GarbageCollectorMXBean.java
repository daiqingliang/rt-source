package com.sun.management;

import java.lang.management.GarbageCollectorMXBean;
import jdk.Exported;

@Exported
public interface GarbageCollectorMXBean extends GarbageCollectorMXBean {
  GcInfo getLastGcInfo();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\GarbageCollectorMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */