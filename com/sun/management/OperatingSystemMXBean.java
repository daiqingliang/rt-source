package com.sun.management;

import java.lang.management.OperatingSystemMXBean;
import jdk.Exported;

@Exported
public interface OperatingSystemMXBean extends OperatingSystemMXBean {
  long getCommittedVirtualMemorySize();
  
  long getTotalSwapSpaceSize();
  
  long getFreeSwapSpaceSize();
  
  long getProcessCpuTime();
  
  long getFreePhysicalMemorySize();
  
  long getTotalPhysicalMemorySize();
  
  double getSystemCpuLoad();
  
  double getProcessCpuLoad();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\OperatingSystemMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */