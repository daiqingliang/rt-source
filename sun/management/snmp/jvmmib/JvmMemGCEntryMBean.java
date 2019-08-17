package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemGCEntryMBean {
  Long getJvmMemGCTimeMs() throws SnmpStatusException;
  
  Long getJvmMemGCCount() throws SnmpStatusException;
  
  Integer getJvmMemManagerIndex() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmMemGCEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */