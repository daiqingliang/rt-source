package sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmMemMgrPoolRelEntryMBean {
  String getJvmMemMgrRelPoolName() throws SnmpStatusException;
  
  String getJvmMemMgrRelManagerName() throws SnmpStatusException;
  
  Integer getJvmMemManagerIndex() throws SnmpStatusException;
  
  Integer getJvmMemPoolIndex() throws SnmpStatusException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\snmp\jvmmib\JvmMemMgrPoolRelEntryMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */