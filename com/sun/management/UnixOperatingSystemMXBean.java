package com.sun.management;

import jdk.Exported;

@Exported
public interface UnixOperatingSystemMXBean extends OperatingSystemMXBean {
  long getOpenFileDescriptorCount();
  
  long getMaxFileDescriptorCount();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\UnixOperatingSystemMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */