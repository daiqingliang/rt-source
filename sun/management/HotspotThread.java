package sun.management;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.management.counter.Counter;

class HotspotThread implements HotspotThreadMBean {
  private VMManagement jvm;
  
  private static final String JAVA_THREADS = "java.threads.";
  
  private static final String COM_SUN_THREADS = "com.sun.threads.";
  
  private static final String SUN_THREADS = "sun.threads.";
  
  private static final String THREADS_COUNTER_NAME_PATTERN = "java.threads.|com.sun.threads.|sun.threads.";
  
  HotspotThread(VMManagement paramVMManagement) { this.jvm = paramVMManagement; }
  
  public native int getInternalThreadCount();
  
  public Map<String, Long> getInternalThreadCpuTimes() {
    int i = getInternalThreadCount();
    if (i == 0)
      return Collections.emptyMap(); 
    String[] arrayOfString = new String[i];
    long[] arrayOfLong = new long[i];
    int j = getInternalThreadTimes0(arrayOfString, arrayOfLong);
    HashMap hashMap = new HashMap(j);
    for (byte b = 0; b < j; b++)
      hashMap.put(arrayOfString[b], new Long(arrayOfLong[b])); 
    return hashMap;
  }
  
  public native int getInternalThreadTimes0(String[] paramArrayOfString, long[] paramArrayOfLong);
  
  public List<Counter> getInternalThreadingCounters() { return this.jvm.getInternalCounters("java.threads.|com.sun.threads.|sun.threads."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotspotThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */