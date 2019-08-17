package sun.management;

import com.sun.management.OperatingSystemMXBean;

class OperatingSystemImpl extends BaseOperatingSystemImpl implements OperatingSystemMXBean {
  private static Object psapiLock = new Object();
  
  OperatingSystemImpl(VMManagement paramVMManagement) { super(paramVMManagement); }
  
  public long getCommittedVirtualMemorySize() {
    synchronized (psapiLock) {
      return getCommittedVirtualMemorySize0();
    } 
  }
  
  private native long getCommittedVirtualMemorySize0();
  
  public native long getTotalSwapSpaceSize();
  
  public native long getFreeSwapSpaceSize();
  
  public native long getProcessCpuTime();
  
  public native long getFreePhysicalMemorySize();
  
  public native long getTotalPhysicalMemorySize();
  
  public native double getSystemCpuLoad();
  
  public native double getProcessCpuLoad();
  
  private static native void initialize();
  
  static  {
    initialize();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\OperatingSystemImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */