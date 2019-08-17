package com.sun.management;

import java.lang.management.ThreadMXBean;
import jdk.Exported;

@Exported
public interface ThreadMXBean extends ThreadMXBean {
  long[] getThreadCpuTime(long[] paramArrayOfLong);
  
  long[] getThreadUserTime(long[] paramArrayOfLong);
  
  long getThreadAllocatedBytes(long paramLong);
  
  long[] getThreadAllocatedBytes(long[] paramArrayOfLong);
  
  boolean isThreadAllocatedMemorySupported();
  
  boolean isThreadAllocatedMemoryEnabled();
  
  void setThreadAllocatedMemoryEnabled(boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\management\ThreadMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */