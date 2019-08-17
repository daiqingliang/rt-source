package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmRTBootClassPathEntryMBean {
  String getJvmRTBootClassPathItem() throws SnmpStatusException;
  
  Integer getJvmRTBootClassPathIndex() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmRTBootClassPathEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */