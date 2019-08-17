package java.lang.management;

import java.lang.management.PlatformManagedObject;
import java.lang.management.ThreadInfo;

public interface ThreadMXBean extends PlatformManagedObject {
  int getThreadCount();
  
  int getPeakThreadCount();
  
  long getTotalStartedThreadCount();
  
  int getDaemonThreadCount();
  
  long[] getAllThreadIds();
  
  ThreadInfo getThreadInfo(long paramLong);
  
  ThreadInfo[] getThreadInfo(long[] paramArrayOfLong);
  
  ThreadInfo getThreadInfo(long paramLong, int paramInt);
  
  ThreadInfo[] getThreadInfo(long[] paramArrayOfLong, int paramInt);
  
  boolean isThreadContentionMonitoringSupported();
  
  boolean isThreadContentionMonitoringEnabled();
  
  void setThreadContentionMonitoringEnabled(boolean paramBoolean);
  
  long getCurrentThreadCpuTime();
  
  long getCurrentThreadUserTime();
  
  long getThreadCpuTime(long paramLong);
  
  long getThreadUserTime(long paramLong);
  
  boolean isThreadCpuTimeSupported();
  
  boolean isCurrentThreadCpuTimeSupported();
  
  boolean isThreadCpuTimeEnabled();
  
  void setThreadCpuTimeEnabled(boolean paramBoolean);
  
  long[] findMonitorDeadlockedThreads();
  
  void resetPeakThreadCount();
  
  long[] findDeadlockedThreads();
  
  boolean isObjectMonitorUsageSupported();
  
  boolean isSynchronizerUsageSupported();
  
  ThreadInfo[] getThreadInfo(long[] paramArrayOfLong, boolean paramBoolean1, boolean paramBoolean2);
  
  ThreadInfo[] dumpAllThreads(boolean paramBoolean1, boolean paramBoolean2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\management\ThreadMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */