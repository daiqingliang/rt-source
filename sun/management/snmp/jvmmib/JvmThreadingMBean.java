package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmThreadingMBean {
  EnumJvmThreadCpuTimeMonitoring getJvmThreadCpuTimeMonitoring() throws SnmpStatusException;
  
  void setJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring paramEnumJvmThreadCpuTimeMonitoring) throws SnmpStatusException;
  
  void checkJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring paramEnumJvmThreadCpuTimeMonitoring) throws SnmpStatusException;
  
  EnumJvmThreadContentionMonitoring getJvmThreadContentionMonitoring() throws SnmpStatusException;
  
  void setJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring paramEnumJvmThreadContentionMonitoring) throws SnmpStatusException;
  
  void checkJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring paramEnumJvmThreadContentionMonitoring) throws SnmpStatusException;
  
  Long getJvmThreadTotalStartedCount() throws SnmpStatusException;
  
  Long getJvmThreadPeakCount() throws SnmpStatusException;
  
  Long getJvmThreadDaemonCount() throws SnmpStatusException;
  
  Long getJvmThreadCount() throws SnmpStatusException;
  
  Long getJvmThreadPeakCountReset() throws SnmpStatusException;
  
  void setJvmThreadPeakCountReset(Long paramLong) throws SnmpStatusException;
  
  void checkJvmThreadPeakCountReset(Long paramLong) throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmThreadingMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */