package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmThreadInstanceEntryMBean {
  String getJvmThreadInstName() throws SnmpStatusException;
  
  Long getJvmThreadInstCpuTimeNs() throws SnmpStatusException;
  
  Long getJvmThreadInstWaitTimeMs() throws SnmpStatusException;
  
  Long getJvmThreadInstWaitCount() throws SnmpStatusException;
  
  Long getJvmThreadInstBlockTimeMs() throws SnmpStatusException;
  
  Long getJvmThreadInstBlockCount() throws SnmpStatusException;
  
  Byte[] getJvmThreadInstState() throws SnmpStatusException;
  
  String getJvmThreadInstLockOwnerPtr() throws SnmpStatusException;
  
  Long getJvmThreadInstId() throws SnmpStatusException;
  
  String getJvmThreadInstLockName() throws SnmpStatusException;
  
  Byte[] getJvmThreadInstIndex() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmThreadInstanceEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */