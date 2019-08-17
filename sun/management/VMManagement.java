package sun.management;

import java.util.List;
import sun.management.counter.Counter;

public interface VMManagement {
  boolean isCompilationTimeMonitoringSupported();
  
  boolean isThreadContentionMonitoringSupported();
  
  boolean isThreadContentionMonitoringEnabled();
  
  boolean isCurrentThreadCpuTimeSupported();
  
  boolean isOtherThreadCpuTimeSupported();
  
  boolean isThreadCpuTimeEnabled();
  
  boolean isBootClassPathSupported();
  
  boolean isObjectMonitorUsageSupported();
  
  boolean isSynchronizerUsageSupported();
  
  boolean isThreadAllocatedMemorySupported();
  
  boolean isThreadAllocatedMemoryEnabled();
  
  boolean isGcNotificationSupported();
  
  boolean isRemoteDiagnosticCommandsSupported();
  
  long getTotalClassCount();
  
  int getLoadedClassCount();
  
  long getUnloadedClassCount();
  
  boolean getVerboseClass();
  
  boolean getVerboseGC();
  
  String getManagementVersion();
  
  String getVmId();
  
  String getVmName();
  
  String getVmVendor();
  
  String getVmVersion();
  
  String getVmSpecName();
  
  String getVmSpecVendor();
  
  String getVmSpecVersion();
  
  String getClassPath();
  
  String getLibraryPath();
  
  String getBootClassPath();
  
  List<String> getVmArguments();
  
  long getStartupTime();
  
  long getUptime();
  
  int getAvailableProcessors();
  
  String getCompilerName();
  
  long getTotalCompileTime();
  
  long getTotalThreadCount();
  
  int getLiveThreadCount();
  
  int getPeakThreadCount();
  
  int getDaemonThreadCount();
  
  String getOsName();
  
  String getOsArch();
  
  String getOsVersion();
  
  long getSafepointCount();
  
  long getTotalSafepointTime();
  
  long getSafepointSyncTime();
  
  long getTotalApplicationNonStoppedTime();
  
  long getLoadedClassSize();
  
  long getUnloadedClassSize();
  
  long getClassLoadingTime();
  
  long getMethodDataSize();
  
  long getInitializedClassCount();
  
  long getClassInitializationTime();
  
  long getClassVerificationTime();
  
  List<Counter> getInternalCounters(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\VMManagement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */