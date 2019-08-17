package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemoryMBean {
  Long getJvmMemoryNonHeapMaxSize() throws SnmpStatusException;
  
  Long getJvmMemoryNonHeapCommitted() throws SnmpStatusException;
  
  Long getJvmMemoryNonHeapUsed() throws SnmpStatusException;
  
  Long getJvmMemoryNonHeapInitSize() throws SnmpStatusException;
  
  Long getJvmMemoryHeapMaxSize() throws SnmpStatusException;
  
  Long getJvmMemoryHeapCommitted() throws SnmpStatusException;
  
  EnumJvmMemoryGCCall getJvmMemoryGCCall() throws SnmpStatusException;
  
  void setJvmMemoryGCCall(EnumJvmMemoryGCCall paramEnumJvmMemoryGCCall) throws SnmpStatusException;
  
  void checkJvmMemoryGCCall(EnumJvmMemoryGCCall paramEnumJvmMemoryGCCall) throws SnmpStatusException;
  
  Long getJvmMemoryHeapUsed() throws SnmpStatusException;
  
  EnumJvmMemoryGCVerboseLevel getJvmMemoryGCVerboseLevel() throws SnmpStatusException;
  
  void setJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel paramEnumJvmMemoryGCVerboseLevel) throws SnmpStatusException;
  
  void checkJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel paramEnumJvmMemoryGCVerboseLevel) throws SnmpStatusException;
  
  Long getJvmMemoryHeapInitSize() throws SnmpStatusException;
  
  Long getJvmMemoryPendingFinalCount() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmMemoryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */