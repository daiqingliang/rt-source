package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmOSMBean {
  Integer getJvmOSProcessorCount() throws SnmpStatusException;
  
  String getJvmOSVersion() throws SnmpStatusException;
  
  String getJvmOSArch() throws SnmpStatusException;
  
  String getJvmOSName() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmOSMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */