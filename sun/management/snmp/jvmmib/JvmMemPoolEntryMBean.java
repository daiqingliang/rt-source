package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemPoolEntryMBean {
  Long getJvmMemPoolCollectMaxSize() throws SnmpStatusException;
  
  Long getJvmMemPoolCollectCommitted() throws SnmpStatusException;
  
  Long getJvmMemPoolCollectUsed() throws SnmpStatusException;
  
  EnumJvmMemPoolCollectThreshdSupport getJvmMemPoolCollectThreshdSupport() throws SnmpStatusException;
  
  Long getJvmMemPoolCollectThreshdCount() throws SnmpStatusException;
  
  Long getJvmMemPoolCollectThreshold() throws SnmpStatusException;
  
  void setJvmMemPoolCollectThreshold(Long paramLong) throws SnmpStatusException;
  
  void checkJvmMemPoolCollectThreshold(Long paramLong) throws SnmpStatusException;
  
  Long getJvmMemPoolMaxSize() throws SnmpStatusException;
  
  Long getJvmMemPoolCommitted() throws SnmpStatusException;
  
  Long getJvmMemPoolUsed() throws SnmpStatusException;
  
  Long getJvmMemPoolInitSize() throws SnmpStatusException;
  
  EnumJvmMemPoolThreshdSupport getJvmMemPoolThreshdSupport() throws SnmpStatusException;
  
  Long getJvmMemPoolThreshdCount() throws SnmpStatusException;
  
  Long getJvmMemPoolThreshold() throws SnmpStatusException;
  
  void setJvmMemPoolThreshold(Long paramLong) throws SnmpStatusException;
  
  void checkJvmMemPoolThreshold(Long paramLong) throws SnmpStatusException;
  
  Long getJvmMemPoolPeakReset() throws SnmpStatusException;
  
  void setJvmMemPoolPeakReset(Long paramLong) throws SnmpStatusException;
  
  void checkJvmMemPoolPeakReset(Long paramLong) throws SnmpStatusException;
  
  EnumJvmMemPoolState getJvmMemPoolState() throws SnmpStatusException;
  
  EnumJvmMemPoolType getJvmMemPoolType() throws SnmpStatusException;
  
  String getJvmMemPoolName() throws SnmpStatusException;
  
  Long getJvmMemPoolPeakMaxSize() throws SnmpStatusException;
  
  Integer getJvmMemPoolIndex() throws SnmpStatusException;
  
  Long getJvmMemPoolPeakCommitted() throws SnmpStatusException;
  
  Long getJvmMemPoolPeakUsed() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmMemPoolEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */