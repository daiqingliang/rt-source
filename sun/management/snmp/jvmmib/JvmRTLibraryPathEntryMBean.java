package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmRTLibraryPathEntryMBean {
  String getJvmRTLibraryPathItem() throws SnmpStatusException;
  
  Integer getJvmRTLibraryPathIndex() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmRTLibraryPathEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */