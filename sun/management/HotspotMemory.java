package sun.management;

import java.util.List;
import sun.management.counter.Counter;

class HotspotMemory implements HotspotMemoryMBean {
  private VMManagement jvm;
  
  private static final String JAVA_GC = "java.gc.";
  
  private static final String COM_SUN_GC = "com.sun.gc.";
  
  private static final String SUN_GC = "sun.gc.";
  
  private static final String GC_COUNTER_NAME_PATTERN = "java.gc.|com.sun.gc.|sun.gc.";
  
  HotspotMemory(VMManagement paramVMManagement) { this.jvm = paramVMManagement; }
  
  public List<Counter> getInternalMemoryCounters() { return this.jvm.getInternalCounters("java.gc.|com.sun.gc.|sun.gc."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotspotMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */