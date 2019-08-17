package sun.management;

import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.management.ObjectName;

class RuntimeImpl implements RuntimeMXBean {
  private final VMManagement jvm;
  
  private final long vmStartupTime;
  
  RuntimeImpl(VMManagement paramVMManagement) {
    this.jvm = paramVMManagement;
    this.vmStartupTime = this.jvm.getStartupTime();
  }
  
  public String getName() { return this.jvm.getVmId(); }
  
  public String getManagementSpecVersion() { return this.jvm.getManagementVersion(); }
  
  public String getVmName() { return this.jvm.getVmName(); }
  
  public String getVmVendor() { return this.jvm.getVmVendor(); }
  
  public String getVmVersion() { return this.jvm.getVmVersion(); }
  
  public String getSpecName() { return this.jvm.getVmSpecName(); }
  
  public String getSpecVendor() { return this.jvm.getVmSpecVendor(); }
  
  public String getSpecVersion() { return this.jvm.getVmSpecVersion(); }
  
  public String getClassPath() { return this.jvm.getClassPath(); }
  
  public String getLibraryPath() { return this.jvm.getLibraryPath(); }
  
  public String getBootClassPath() {
    if (!isBootClassPathSupported())
      throw new UnsupportedOperationException("Boot class path mechanism is not supported"); 
    Util.checkMonitorAccess();
    return this.jvm.getBootClassPath();
  }
  
  public List<String> getInputArguments() {
    Util.checkMonitorAccess();
    return this.jvm.getVmArguments();
  }
  
  public long getUptime() { return this.jvm.getUptime(); }
  
  public long getStartTime() { return this.vmStartupTime; }
  
  public boolean isBootClassPathSupported() { return this.jvm.isBootClassPathSupported(); }
  
  public Map<String, String> getSystemProperties() {
    Properties properties = System.getProperties();
    HashMap hashMap = new HashMap();
    Set set = properties.stringPropertyNames();
    for (String str1 : set) {
      String str2 = properties.getProperty(str1);
      hashMap.put(str1, str2);
    } 
    return hashMap;
  }
  
  public ObjectName getObjectName() { return Util.newObjectName("java.lang:type=Runtime"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\RuntimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */