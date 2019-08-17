package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmRuntimeMBean {
  EnumJvmRTBootClassPathSupport getJvmRTBootClassPathSupport() throws SnmpStatusException;
  
  String getJvmRTManagementSpecVersion() throws SnmpStatusException;
  
  String getJvmRTSpecVersion() throws SnmpStatusException;
  
  String getJvmRTSpecVendor() throws SnmpStatusException;
  
  String getJvmRTSpecName() throws SnmpStatusException;
  
  String getJvmRTVMVersion() throws SnmpStatusException;
  
  String getJvmRTVMVendor() throws SnmpStatusException;
  
  Long getJvmRTStartTimeMs() throws SnmpStatusException;
  
  Long getJvmRTUptimeMs() throws SnmpStatusException;
  
  String getJvmRTVMName() throws SnmpStatusException;
  
  String getJvmRTName() throws SnmpStatusException;
  
  Integer getJvmRTInputArgsCount() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmRuntimeMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */